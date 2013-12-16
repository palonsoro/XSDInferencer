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
package es.upm.dit.xsdinferencer.extraction;

import es.upm.dit.xsdinferencer.datastructures.Schema;

/**
 * Main interface of the extraction module. It is responsible of extracting an initial {@link Schema} from the 
 * input XML documents, which will contain  complex types (whose children structure will be described by means of 
 * automata, until the converter generates the corresponding regular expressions), all the simple types of the 
 * attributes and text of any complex type, the prefix-namespace mappings and the SchemaElement and SchemaAttribute 
 * data structures of the Schema object. 
 * The statistics will be gathered here and only the ones which depend on complex types will be 
 * changed before the generation.
 * Note that all the necessary parameters will be given at the constructor and not at each method invocation, 
 * like at some other modules.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface TypesExtractor {
	/**
	 * This method extracts all the information from the input documents and generates the initial Schema.
	 * @return the initial Schema
	 */
	public Schema getInitalSchema();
}
