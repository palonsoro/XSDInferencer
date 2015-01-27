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
package es.upm.dit.xsdinferencer.generation;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.Schema;

/**
 * This is an interface for schema document generator submodules. Each instance of any implementation of this interface 
 * is intended to generate a single schema document (in those cases when many documents can be generated, as happens 
 * with XSD). Implementation-specific parameters (like target namespace...) can be provided via constructors or implementation-specific methods.
 *  
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 * 
 * @param <T> The type of the returned schema document.
 */
public interface SchemaDocumentGenerator<T> {
	/**
	 * Method that generates a single
	 * @param schema the current schema
	 * @param configuration the inference configuration
	 * @return a JDOM2 document with the generated XSD
	 * @throws IllegalArgumentException if no XSD could be generated for the given targetNamespace, for example, because it is an skipped namespace.
	 */
	public T generateSchemaDocument(Schema schema, XSDInferenceConfiguration configuration);
}
