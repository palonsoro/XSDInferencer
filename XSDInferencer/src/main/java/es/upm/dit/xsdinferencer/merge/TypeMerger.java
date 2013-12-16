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
package es.upm.dit.xsdinferencer.merge;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.Schema;
/**
 * Main interface for the merge module, which merges all the types that are 
 * similar enough, according to specific criteria which depend on the 
 * implementation and user's preferences.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface TypeMerger {
	/**
	 * Method that takes an input {@link Schema} and the inference configuration 
	 * and merges all the types of that Schema, according to the user's preferences 
	 * specified at the configuration. Results are returned as modifications on the 
	 * data structures of the passed Schema, not only the information about complex 
	 * types but also other related data (like types of elements or per-complex-type 
	 * statistics).
	 * @param schema the {@link Schema} object used at the current inference process
	 * @param configuration the inference configuration
	 */
	public void mergeTypes(Schema schema, XSDInferenceConfiguration configuration);
}
