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
import es.upm.dit.xsdinferencer.datastructures.SingularRegularExpression;

/**
 * It looks for singular or multiple regular expression and replaces any 
 * {@linkplain MultipleRegularExpression} or {@linkplain SingularRegularExpression} among its children 
 * if they have no contents or only consists of empty elements by the empty regular expression
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
class EmptyOptimizer extends RecursiveRegexOptimizer {

	/**
	 * @see RegexOptimizer#optimizeRegex(RegularExpression)
	 */
	@Override
	public boolean optimizeRegex(RegularExpression regex) {
		boolean isMultipleRegularExpression = regex instanceof MultipleRegularExpression;
		boolean isSingularRegularExpression = regex instanceof SingularRegularExpression;
		if(isMultipleRegularExpression||isSingularRegularExpression){
			EmptyRegularExpression emptyRegex = new EmptyRegularExpression();
			for(int i=0;i<regex.elementCount();i++){
				RegularExpression currentRegex = regex.getElement(i);
				boolean isCurrentMultipleRegularExpression = currentRegex instanceof MultipleRegularExpression;
				boolean isCurrentSingularRegularExpression = currentRegex instanceof SingularRegularExpression;
				if(!(isCurrentMultipleRegularExpression||isCurrentSingularRegularExpression))
					continue;
				boolean isCurrentEmpty = currentRegex instanceof EmptyRegularExpression;
				if(currentRegex.elementCount()==0 && !isCurrentEmpty){
					regex.setElement(i, emptyRegex);
					continue;
				}
				boolean hasOnlyEmptyValues=true;
				for(int j=0;j<currentRegex.elementCount();j++){
					boolean isThisChildEmpty = (currentRegex.getElement(j)) instanceof EmptyRegularExpression;
					if(!isThisChildEmpty){
						hasOnlyEmptyValues=false;
						break;
					}
				}
				if(hasOnlyEmptyValues){
					regex.setElement(i, emptyRegex);
					continue;
				}
			}
		}
		return recurse(regex);
	}
}
