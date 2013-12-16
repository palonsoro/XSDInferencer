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
package es.upm.dit.xsdinferencer.conversion;

import es.upm.dit.xsdinferencer.datastructures.RegularExpression;

/**
 * Interface for optimizers, which take a regular expression as an input and transform it in order 
 * to optimize it in a particular way, which depends on each implementation.
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface RegexOptimizer {
	/**
	 * This method performs the optimization on the input regular expression. All the manipulations are made 
	 * directly to the input object, it implies that all the modifications are seen by anyone who has a reference 
	 * to the input object
	 * @param regex the input regular expression
	 * @return true if there has been any modification
	 */
	public boolean optimizeRegex(RegularExpression regex);
}
