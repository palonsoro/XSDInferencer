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

import es.upm.dit.xsdinferencer.Results;
import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.exceptions.InvalidXSDConfigurationParameterException;
import es.upm.dit.xsdinferencer.exceptions.XSDConfigurationException;
import es.upm.dit.xsdinferencer.generation.ResultsGenerator;
import es.upm.dit.xsdinferencer.generation.StatisticResultsDocGenerator;
import es.upm.dit.xsdinferencer.generation.XSDDocumentGenerator;
import es.upm.dit.xsdinferencer.generation.generatorimpl.statisticsgeneration.StatisticResultsDocGeneratorFactory;
import es.upm.dit.xsdinferencer.generation.generatorimpl.xsdgeneration.XSDDocumentGeneratorFactory;
import es.upm.dit.xsdinferencer.statistics.Statistics;
import es.upm.dit.xsdinferencer.util.xsdfilenaming.XSDFileNameGenerator;

/**
 * Main implementation of {@link ResultsGenerator}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class ResultsGeneratorImpl implements ResultsGenerator {

	/**
	 * Auxiliary method that generates the XSDs and puts them into the {@link Map} 
	 * to be included into the {@link Results} object returned by {@link ResultsGenerator#generateResults(Schema, XSDInferenceConfiguration, XSDDocumentGeneratorFactory, StatisticResultsDocGeneratorFactory, XSDFileNameGenerator)}
	 * @param schema the {@link Schema} object used at the inference
	 * @param configuration the inference configuration
	 * @param xsdDocumentGeneratorFactory factory for {@link XSDDocumentGenerator} implementations
	 * @param xsdFileNameGenerator XSD file name generator
	 * @return the {@link Map} between the names of the XSDs and the {@link Document} objects that represent them
	 * @throws XSDConfigurationException if an invalid configuration is provided
	 */
	protected Map<String,Document> generateXSDDocuments(Schema schema,XSDInferenceConfiguration configuration, 
			XSDDocumentGeneratorFactory xsdDocumentGeneratorFactory, XSDFileNameGenerator xsdFileNameGenerator) throws XSDConfigurationException {
		String mainNamespace= configuration.getMainNamespace();
		if(mainNamespace==null)
			mainNamespace=schema.guessMainNamespace(configuration);
		Map<String,Document> result = new HashMap<>(schema.getNamespacesToPossiblePrefixMappingModifiable().size());
		Map<String, String> namespaceURIsToPrefixMappings = schema.getSolvedNamespaceMappings();
		if(!namespaceURIsToPrefixMappings.keySet().contains(mainNamespace))
			throw new InvalidXSDConfigurationParameterException("The specified main namespace is not present at the input documents");
		XSDDocumentGenerator xsdGenerator = xsdDocumentGeneratorFactory.getXSDDocumentGeneratorInstance();
		for(String targetNamespace:namespaceURIsToPrefixMappings.keySet()){
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
			Document xsdDocument = xsdGenerator.generateSchemaDocument(schema, configuration, targetNamespace, mainNamespace, xsdFileNameGenerator);
			result.put(fileName, xsdDocument);
		}
		return result;
	
	}
	
	/**
	 * Auxiliary method that generates the statistics XML document and puts it into the {@link Map} 
	 * to be included into the {@link Results} object returned by {@link ResultsGenerator#generateResults(Schema, XSDInferenceConfiguration, XSDDocumentGeneratorFactory, StatisticResultsDocGeneratorFactory, XSDFileNameGenerator)}
	 * @param statistics the {@link Statistics} objects used
	 * @param statisticResultsDocGeneratorFactory the {@link StatisticResultsDocGeneratorFactory} used to build the {@link StatisticResultsDocGenerator} used to generate the document
	 * @return the {@link Map} to be included in the results object.
	 */
	protected Map<String,Document> generateStatisticsResults(Statistics statistics, StatisticResultsDocGeneratorFactory statisticResultsDocGeneratorFactory) {
		StatisticResultsDocGenerator generator = statisticResultsDocGeneratorFactory.getStatisticResultsDocGeneratorInstance();
		Document statisticsDocument = generator.generateStatisticResultsDoc(statistics);
		return Collections.singletonMap("statistics.xml", statisticsDocument);
	
	}

	@Override
	public Results generateResults(Schema schema,
			XSDInferenceConfiguration configuration, 
			XSDDocumentGeneratorFactory xsdDocumentGeneratorFactory, 
			StatisticResultsDocGeneratorFactory statisticResultsDocGeneratorFactory, XSDFileNameGenerator xsdFileNameGenerator) throws XSDConfigurationException {

		Map<String,Document> xsdDocuments = generateXSDDocuments(schema, configuration, xsdDocumentGeneratorFactory, xsdFileNameGenerator);
		
		Map<String,Document> statisticDocuments = generateStatisticsResults(schema.getStatistics(), statisticResultsDocGeneratorFactory);
		Results results = new Results(xsdDocuments, statisticDocuments);
		return results;
	}
}
