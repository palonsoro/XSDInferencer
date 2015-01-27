/*
Copyright 2013 Universidad Politécnica de Madrid - Center for Open Middleware (http://www.centeropenmiddleware.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package es.upm.dit.xsdinferencer.generation.generatorimpl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;
import org.json.JSONObject;

import com.google.common.collect.ImmutableMap;

import es.upm.dit.xsdinferencer.Results;
import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.exceptions.InvalidXSDConfigurationParameterException;
import es.upm.dit.xsdinferencer.exceptions.XSDConfigurationException;
import es.upm.dit.xsdinferencer.generation.ResultsGenerator;
import es.upm.dit.xsdinferencer.generation.SchemaDocumentGenerator;
import es.upm.dit.xsdinferencer.generation.StatisticResultsDocGenerator;
import es.upm.dit.xsdinferencer.generation.generatorimpl.schemageneration.SchemaDocumentGeneratorFactory;
import es.upm.dit.xsdinferencer.generation.generatorimpl.statisticsgeneration.StatisticResultsDocGeneratorFactory;
import es.upm.dit.xsdinferencer.statistics.Statistics;
import es.upm.dit.xsdinferencer.util.xsdfilenaming.XSDFileNameGenerator;

/**
 * Main implementation of {@link ResultsGenerator}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class ResultsGeneratorImpl implements ResultsGenerator {
	
	/**
	 * Factory for {@link SchemaDocumentGenerator} objects used.
	 */
	private SchemaDocumentGeneratorFactory schemaDocumentGeneratorFactory;
	
	/**
	 * Factory for {@link StatisticResultsDocGenerator} objects used
	 */
	private StatisticResultsDocGeneratorFactory statisticResultsDocGeneratorFactory;
	
	/**
	 * {@link XSDFileNameGenerator} used to generate names for XSD files.
	 */
	private XSDFileNameGenerator xsdFileNameGenerator;

	/**
	 * Constructor.
	 * 
	 * @param schemaDocumentGeneratorFactory factory for {@link SchemaDocumentGenerator} objects used.
	 * @param statisticResultsDocGeneratorFactory factory for {@link StatisticResultsDocGenerator} objects used
	 * @param xsdFileNameGenerator {@link XSDFileNameGenerator} used to generate names for XSD files.
	 */
	public ResultsGeneratorImpl(
			SchemaDocumentGeneratorFactory xsdDocumentGeneratorFactory,
			StatisticResultsDocGeneratorFactory statisticResultsDocGeneratorFactory,
			XSDFileNameGenerator xsdFileNameGenerator) {
		super();
		this.schemaDocumentGeneratorFactory = xsdDocumentGeneratorFactory;
		this.statisticResultsDocGeneratorFactory = statisticResultsDocGeneratorFactory;
		this.xsdFileNameGenerator = xsdFileNameGenerator;
	}

	/**
	 * Auxiliary method that generates XSDs and puts them into the {@link Map} 
	 * to be included into the {@link Results} object returned by {@link ResultsGenerator#generateResults(Schema, XSDInferenceConfiguration)}.
	 * This method is to be called if the working format is XSD.
	 * @param schema the {@link Schema} object used at the inference
	 * @param configuration the inference configuration
	 * @param schemaDocumentGeneratorFactory factory for {@link SchemaDocumentGenerator} implementations
	 * @param xsdFileNameGenerator XSD file name generator
	 * @return the {@link Map} between the names of the XSDs and the {@link Document} objects that represent them
	 * @throws XSDConfigurationException if an invalid configuration is provided
	 */
	protected Map<String,Document> generateXSDDocuments(Schema schema,XSDInferenceConfiguration configuration, 
			SchemaDocumentGeneratorFactory xsdDocumentGeneratorFactory, XSDFileNameGenerator xsdFileNameGenerator) throws XSDConfigurationException {
		String mainNamespace= configuration.getMainNamespace();
		if(mainNamespace==null)
			mainNamespace=schema.guessMainNamespace(configuration);
		Map<String,Document> result = new HashMap<>(schema.getNamespacesToPossiblePrefixMappingModifiable().size());
		Map<String, String> namespaceURIsToPrefixMappings = schema.getSolvedNamespaceMappings();
		if(!namespaceURIsToPrefixMappings.keySet().contains(mainNamespace))
			throw new InvalidXSDConfigurationParameterException("The specified main namespace is not present at the input documents");
		
		for(String targetNamespace:namespaceURIsToPrefixMappings.keySet()){
			SchemaDocumentGenerator<Document> xsdGenerator = xsdDocumentGeneratorFactory.getXMLSchemaDocumentGeneratorInstance(targetNamespace, mainNamespace, xsdFileNameGenerator);
			if(configuration.getSkipNamespaces().contains(targetNamespace))
				continue;
			if(targetNamespace.equals(XSDInferenceConfiguration.XML_NAMESPACE_URI)&&
					(!schema.getAttributes().containsRow(targetNamespace)&&
					 !schema.getElements().containsRow(targetNamespace))){
				continue;
			}
			if(targetNamespace.equals("")&&
					!targetNamespace.equals(mainNamespace)&&
					!schema.getElements().containsRow(""))
				continue;
			String fileName=targetNamespace;
			if(xsdFileNameGenerator!=null)
				fileName=xsdFileNameGenerator.getSchemaDocumentFileName(targetNamespace, namespaceURIsToPrefixMappings);
			Document xsdDocument = xsdGenerator.generateSchemaDocument(schema, configuration);
			result.put(fileName, xsdDocument);
		}
		return result;
	
	}
	
	/**
	 * Auxiliary method that generates the statistics XML document and puts it into the {@link Map} 
	 * to be included into the {@link Results} object returned by {@link ResultsGenerator#generateResults(Schema, XSDInferenceConfiguration)}
	 * @param statistics the {@link Statistics} objects used
	 * @param statisticResultsDocGeneratorFactory the {@link StatisticResultsDocGeneratorFactory} used to build the {@link StatisticResultsDocGenerator} used to generate the document
	 * @return the {@link Map} to be included in the results object.
	 */
	protected Map<String,Document> generateStatisticsResults(Statistics statistics, StatisticResultsDocGeneratorFactory statisticResultsDocGeneratorFactory) {
		StatisticResultsDocGenerator generator = statisticResultsDocGeneratorFactory.getStatisticResultsDocGeneratorInstance();
		Document statisticsDocument = generator.generateStatisticResultsDoc(statistics);
		return Collections.singletonMap("statistics.xml", statisticsDocument);
	
	}
	
	/**
	 * This method generates JSON Schema documents from the provided {@link Schema} object. 
	 * It is the entry point to the JSON Schema generator submodule.
	 * @param schema the schema object
	 * @param configuration the inference configuration
	 * @return a map between strings representing file names and {@link JSONObject} with generated JSON schemas
	 */
	protected Map<String,JSONObject> generateJSONSchemaDocuments(Schema schema, XSDInferenceConfiguration configuration, SchemaDocumentGeneratorFactory factory){
		ImmutableMap.Builder<String, JSONObject> resultBuilder = new ImmutableMap.Builder<>();
		SchemaDocumentGenerator<JSONObject> jsonSchemaGeneratorMain = factory.getJSONSchemaDocumentGeneratorInstance();
		JSONObject mainJsonSchema = jsonSchemaGeneratorMain.generateSchemaDocument(schema, configuration);
		resultBuilder.put("schema.json",mainJsonSchema);
		return resultBuilder.build(); 
	}

	@Override
	public Results generateResults(Schema schema,
			XSDInferenceConfiguration configuration) throws XSDConfigurationException {
		
		Results results;
		
		if(configuration.getWorkingFormat().equals("xml")){
			Map<String,Document> statisticDocuments = generateStatisticsResults(schema.getStatistics(), statisticResultsDocGeneratorFactory);
			Map<String,Document> xsdDocuments = generateXSDDocuments(schema, configuration, schemaDocumentGeneratorFactory, xsdFileNameGenerator);
			results = new Results(xsdDocuments, statisticDocuments);
		
		}
		else if(configuration.getWorkingFormat().equals("json")){
			Map<String,JSONObject> jsonSchemaDocuments = generateJSONSchemaDocuments(schema, configuration,schemaDocumentGeneratorFactory);
			results = new Results(null,jsonSchemaDocuments ,ImmutableMap.of());
		}
		else{
			throw new XSDConfigurationException("Working format '"+configuration.getWorkingFormat()+"' not supported by this generator module implementation.");
		}
		
		
		return results;
	}
}
