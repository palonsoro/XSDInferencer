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
 * Default implementation for {@link XSDFileNameGenerator}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class XSDFileNameGeneratorDefaultImpl implements XSDFileNameGenerator {

	/**
	 * @see XSDFileNameGenerator#getSchemaDocumentFileName(String, Map)
	 */
	@Override
	public String getSchemaDocumentFileName(String targetNamespace, 
			Map<String, String> namespaceURIToPrefixMappings){
		String fileName="schema-";
		String prefix = namespaceURIToPrefixMappings.get(targetNamespace);
		if(targetNamespace.equals("")){
			fileName+="no_ns";
		}
		else if(prefix.equals("")){
			fileName+="unprefixed_ns";
		}
		else{
			fileName+="ns_"+prefix;
		}
		fileName+=".xsd";
		return fileName;
	}

}
