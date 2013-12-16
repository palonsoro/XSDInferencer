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

import java.util.List;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;

/**
 * A TypeNameInferencer determines which name should have the complex type of an element on a given 
 * path, based on criteria of the concrete implementation and on the configuration.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface TypeNameInferencer{
	/**
	 * It infers the type name of an element on a given path
	 * @param path a List which contains all the path elements in the correct order. Note that they must not contain invalid characters according to the NCName XML type specification.
	 * @param configuration the inference configuration
	 * @return the complex type name
	 */
	public String inferTypeName(List<String> path, XSDInferenceConfiguration configuration);
}
