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
package es.upm.dit.xsdinferencer;

import java.util.HashMap;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.Format.TextMode;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;
import org.json.JSONObject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * This class contains the results of the inference and allows to query them in many formats 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class Results {
	
	/**
	 * The XSD documents inferred, as JDOM2 {@link Document} objects (keys are file names).
	 */
	private Map<String,Document> xmlSchemas=null;
	
	/**
	 * The JSON Schema documents inferred, as {@link JSONObject} objects (keys are file names).
	 */
	private Map<String,JSONObject> jsonSchemas=null;
	
	/**
	 * How many spaces are added to each indentation level while pretty-printing JSON
	 */
	protected static final int JSON_PRETTY_PRINT_INDENT_FACTOR = 1;
	
	/**
	 * The statistics documents generated, as JDOM2 {@link Document} objects (keys are file names). 
	 * Note that the current implementation only generates one called "statistics.xml", but this 
	 * way of store allows this to change in the futures.
	 */
	private Map<String,Document> statisticsDocuments;
		
	/**
	 * Default constructor for XML inference
	 * @param xmlSchemas the inferred schema documents
	 * @param statistics the statistics
	 */
	public Results(Map<String, Document> xmlSchemas,
			Map<String, Document> statistics) {
				this(xmlSchemas, null, statistics);
			}


	/**
	 * Default constructor
	 * @param xmlSchemas the inferred XML Schema documents (null when inferring JSON)
	 * @param jsonSchemas the inferred JSON Schema documents
	 * @param statistics the statistics
	 */
	public Results(Map<String, Document> xmlSchemas,
			Map<String, JSONObject> jsonSchemas, Map<String, Document> statistics) {
		this.xmlSchemas = xmlSchemas;
		this.statisticsDocuments = statistics;
		this.jsonSchemas=jsonSchemas;
	}
	
	/**
	 * Default constructor
	 * @param jsonSchemas the inferred JSON Schema documents
	 */
	public Results(Map<String, JSONObject> jsonSchemas) {
		this(null,jsonSchemas,null);
	}
	
	
	/**
	 * This method returns the XML schemas as a {@link Map} between the name that the XSD file should have 
	 * (although no file is created here) and the JDOM2 {@link Document} that describes the XSD. 
	 * @return an {@link ImmutableMap} between suggested file names and the JDOM2 {@link Document} with the schema content.
	 */
	public Map<String,Document> getXSDsAsXMLs() {
		return ImmutableMap.copyOf(xmlSchemas);
	}
	
	/**
	 * This method returns the JSON schemas as a {@link Map} between the name that the JSON Schema file should have 
	 * (although no file is created here) and the {@link JSONObject} with the schema content. 
	 * @return an {@link ImmutableMap} between suggested file names and the JDOM2 {@link Document} objects which contain the XSDs.
	 */
	public Map<String, JSONObject> getJsonSchemasAsJsonObjects() {
		return jsonSchemas;
	}

	/**
	 * This method returns the schemas as a {@link Map} between the name that each XSD file should have 
	 * (although no file is created here) and strings with their contents. 
	 * @return an {@link ImmutableMap} between suggested file names and the strings with the contents of the schemas.
	 */
	public Map<String,String> getXSDsAsStrings() {
		if(xmlSchemas==null){
			return null;
		} else {
			return getStringMapFromXMLMap(xmlSchemas,TextMode.TRIM);
		}
	
	}
	
	/**
	 * This method returns the schemas as a {@link Map} between the name that each JSON Schema file should have 
	 * (although no file is created here) and strings with their contents. 
	 * @return an {@link ImmutableMap} between suggested file names and the strings with the contents of the schemas (null if jsonSchemas is null).
	 */
	public Map<String,String> getJsonSchemasAsStrings()	{
		if(jsonSchemas==null){
			return null;
		}
		Builder<String, String> resultBuilder = ImmutableMap.builder();
		jsonSchemas.forEach((String k, JSONObject v) -> 
			resultBuilder.put(k, v.toString(JSON_PRETTY_PRINT_INDENT_FACTOR))
		);
		return resultBuilder.build();
	}

	/**
	 * Converts a Map<String,Document> to a Map<String,String> with the same keys and String representations 
	 * of the documents as values.<br/>
	 * The XMLs are generated using the format returned by {@link Format#getPrettyFormat()} and the system line separator. 
	 * For more information about this, see {@link Format}.
	 * @param inputMap a map between Strings and Documents
	 * @return a map between the Strings and the String representation of the documents (null if inputMap is null).
	 */
	private Map<String, String> getStringMapFromXMLMap(Map<String,Document> inputMap,TextMode textMode) {
		if(inputMap==null){
			return null;
		}
		Map<String,String> results = new HashMap<>(inputMap.size());
		Format xmlFormat = Format.getPrettyFormat();
		xmlFormat.setLineSeparator(LineSeparator.SYSTEM);
		//xmlFormat.setTextMode(textMode);
		XMLOutputter outputter = new XMLOutputter(xmlFormat);
		for(String fileName: inputMap.keySet()){
			Document currentDocument = inputMap.get(fileName);
			String xsdString = outputter.outputString(currentDocument);
			results.put(fileName, xsdString);
		}
		return ImmutableMap.copyOf(results);
	}
	
	/**
	 * This method returns the schemas as a {@link Map} between the name that the statistics file should have 
	 * (although no file is created here) and the JDOM2 {@link Document} that describes the statistics. 
	 * @return an {@link ImmutableMap} between suggested file names and the JDOM2 {@link Document} objects which contain the statistics.
	 */
	public Map<String,Document> getStatisticsAsXML() {
		return statisticsDocuments;
	
	}
	
	/**
	 * This method returns the schemas as a {@link Map} between the name that the statistics file should have 
	 * (although no file is created here) and strings with their contents. 
	 * @return an {@link ImmutableMap} between suggested file names and the strings with the contents of the statistics.
	 */
	public Map<String,String> getStatisticsAsStrings() {
		if(statisticsDocuments==null){
			return null;
		} else {
			return getStringMapFromXMLMap(statisticsDocuments,TextMode.PRESERVE);
		}
	
	}
	
}
