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
package es.upm.dit.xsdinferencer.util.xsdfilenaming;

import java.util.Map;

/**
 * Generates the file name for an XML Schema Document, given its target namespace and the solved mappings between 
 * namespace URIs and prefixes. 
 * The way the names are genereted depends on the implementation.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface XSDFileNameGenerator {
	/**
	 * Returns the file name for the schema document of a given target namespace.
	 * @param targetNamespace the target namespace URI
	 * @param namespaceURIToPrefixMappings a Map<String,String> which maps namespace URIs to their corresponding prefixes.
	 * @return the file name (with extension but without a directory)
	 */
	public String getSchemaDocumentFileName(String targetNamespace, Map<String, String> namespaceURIToPrefixMappings);
	
}
