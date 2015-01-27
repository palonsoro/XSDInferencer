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
package es.upm.dit.xsdinferencer.generation.generatorimpl.schemageneration;

import org.jdom2.Document;
import org.json.JSONObject;

import es.upm.dit.xsdinferencer.generation.SchemaDocumentGenerator;
import es.upm.dit.xsdinferencer.util.xsdfilenaming.XSDFileNameGenerator;

/**
 * Factory for {@link SchemaDocumentGenerator} implementations
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class SchemaDocumentGeneratorFactory {

	/**
	 * Singleton instance
	 */
	protected static final SchemaDocumentGeneratorFactory singletonInstance = new SchemaDocumentGeneratorFactory();
	
	/**
	 * Private constructor to avoid instantiation
	 */
	private SchemaDocumentGeneratorFactory(){
		
	}
	
	/**
	 * Method that returns the singleton instance of the factory
	 * @return the singleton instance
	 */
	public static SchemaDocumentGeneratorFactory getInstance(){
		return singletonInstance;
	}
	
	/**
	 * Returns a {@link XMLSchemaDocumentGenerator} instance
	 * 
	 * @param targetNamespace The target namespace to generate the document from.
	 * @param mainNamespace The main namespace, either the one stored at the configuration or a guessed one.
	 * @param fileNameGenerator A {@link XSDFileNameGenerator} to generate XSD file names.
	 * 
	 * @return a {@link XMLSchemaDocumentGenerator} instance
	 * 
	 * @see XMLSchemaDocumentGenerator#XMLSchemaDocumentGenerator(String, String, XSDFileNameGenerator)
	 */
	public SchemaDocumentGenerator<Document> getXMLSchemaDocumentGeneratorInstance(String targetNamespace, String mainNamespace, XSDFileNameGenerator fileNameGenerator){
		return new XMLSchemaDocumentGenerator(targetNamespace, mainNamespace, fileNameGenerator);
	}
	
	/**
	 * Returns a {@link JSONSchemaDocumentGenerator} instance.
	 * 
	 * @return a {@link JSONSchemaDocumentGenerator} instance.
	 */
	public SchemaDocumentGenerator<JSONObject> getJSONSchemaDocumentGeneratorInstance(){
		return new JSONSchemaDocumentGenerator();
	}
	
}
