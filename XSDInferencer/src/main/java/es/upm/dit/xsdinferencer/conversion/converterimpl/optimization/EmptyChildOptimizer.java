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

/**
 * This optimizer removes all the empty regular expressions contained on a {@linkplain MultipleRegularExpression}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
class EmptyChildOptimizer extends RecursiveRegexOptimizer {
		
	/**
	 * @see RegexOptimizer#optimizeRegex(RegularExpression)
	 */
	@Override
	public boolean optimizeRegex(RegularExpression regex) {
		boolean isMultipleRegularExpression = regex instanceof MultipleRegularExpression;
		if(isMultipleRegularExpression){
			MultipleRegularExpression regexMultiple = (MultipleRegularExpression) regex;
			boolean modified=false;
			EmptyRegularExpression empty = new EmptyRegularExpression();
			boolean result;
			while(result = regexMultiple.remove(empty)){
				modified=modified || result; 
			}
			if(modified)
				return true;
		}
		return recurse(regex);
	}
}
