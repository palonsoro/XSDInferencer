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

import com.google.common.collect.ImmutableMap;

/**
 * This class contains the results of the inference and allows to query them in many formats 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class Results {
	
	/**
	 * The XSD documents inferred, as JDOM2 {@link Document} objects (keys are file names).
	 */
	private Map<String,Document> schemas;
	
	/**
	 * The statistics documents generated, as JDOM2 {@link Document} objects (keys are file names). 
	 * Note that the current implementation only generates one called "statistics.xml", but this 
	 * way of store allows this to change in the futures.
	 */
	private Map<String,Document> statisticsDocuments;
		
	/**
	 * Default constructor
	 * @param schemas the inferred schema documents
	 * @param statistics the statistics
	 */
	public Results(Map<String, Document> schemas,
			Map<String, Document> statistics) {
		this.schemas = schemas;
		this.statisticsDocuments = statistics;
	}

	/**
	 * This method returns the schemas as a {@link Map} between the name that the XSD file should have 
	 * (although no file is created here) and the JDOM2 {@link Document} that describes the XSD. 
	 * @return an {@link ImmutableMap} between suggested file names and the JDOM2 {@link Document} objects which contain the XSDs.
	 */
	public Map<String,Document> getSchemasAsXML() {
		return ImmutableMap.copyOf(schemas);
	
	}
	
	/**
	 * This method returns the schemas as a {@link Map} between the name that the XSD file should have 
	 * (although no file is created here) and strings with their contents. 
	 * @return an {@link ImmutableMap} between suggested file names and the strings with the contents of the XSDs.
	 */
	public Map<String,String> getSchemasAsXMLStrings() {
		return getStringMap(schemas,TextMode.TRIM);
	
	}

	/**
	 * Converts a Map<String,Document> to a Map<String,String> with the same keys and String representations 
	 * of the documents as values.<br/>
	 * The XMLs are generated using the format returned by {@link Format#getPrettyFormat()} and the system line separator. 
	 * For more information about this, see {@link Format}.
	 * @param inputMap a map between Strings and Documents
	 * @return a map between the Strings and the String representation of the documents.
	 */
	private Map<String, String> getStringMap(Map<String,Document> inputMap,TextMode textMode) {
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
	public Map<String,String> getStatisticsAsXMLStrings() {
		return getStringMap(statisticsDocuments,TextMode.PRESERVE);
	
	}
	
//	public Map<String,String> getStatisticsAsCSVStrings() {
//		throw new UnsupportedOperationException();
//	}
}
