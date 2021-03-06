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

import es.upm.dit.xsdinferencer.Results;
import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.exceptions.XSDConfigurationException;

/**
 * Main interface for the generator module, which takes an {@link Schema} object and 
 * the inference configuration and generates the results of the inferences process. 
 * Those results will be stored on a {@link Results} object, which will be returned.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface ResultsGenerator {
	/**
	 * Method that performs the results generation and returns them.
	 * @param schema the schema object whith all the inference data
	 * @param configuration the inference configuration
	 * @return a {@link Results} object with the inference results
	 * @throws XSDConfigurationException if the inference configuration is not valid
	 */
	public Results generateResults(Schema schema, XSDInferenceConfiguration configuration) throws XSDConfigurationException;
}
