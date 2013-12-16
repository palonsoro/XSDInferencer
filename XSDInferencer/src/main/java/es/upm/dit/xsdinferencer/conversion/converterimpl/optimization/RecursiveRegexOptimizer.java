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
package es.upm.dit.xsdinferencer.conversion.converterimpl.optimization;

import es.upm.dit.xsdinferencer.conversion.RegexOptimizer;
import es.upm.dit.xsdinferencer.datastructures.EmptyRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.MultipleRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.SingularRegularExpression;

/**
 * Abstract class for recursive {@link RegexOptimizer} implementations. 
 * Recursive means that the optimization will be performed to all the 
 * subexpressions recursively until an {@link SchemaElement} or an 
 * {@link EmptyRegularExpression} is found.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
abstract class RecursiveRegexOptimizer implements RegexOptimizer {
	
	/**
	 * @see RegexOptimizer#optimizeRegex(RegularExpression)
	 */
	@Override
	public abstract boolean optimizeRegex(RegularExpression regex);
	
	/**
	 * Tries to optimize all the children of a regular expression.
	 * @param regex
	 * @return
	 */
	protected boolean recurse(RegularExpression regex) {
		boolean isSingularRegex = regex instanceof SingularRegularExpression;
		boolean isMultipleRegex = regex instanceof MultipleRegularExpression;
		if(!isSingularRegex&&!isMultipleRegex)
			return false;
		boolean modified=false;
		for(int i=0;i<regex.elementCount();i++){
			modified = modified || optimizeRegex(regex.getElement(i));
		}
		return modified;
		
	}
}
