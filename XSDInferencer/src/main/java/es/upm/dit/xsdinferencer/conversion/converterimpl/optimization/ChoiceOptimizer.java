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

import java.util.List;

import es.upm.dit.xsdinferencer.conversion.RegexOptimizer;
import es.upm.dit.xsdinferencer.datastructures.Choice;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;

/**
 * This optimizer tries to flattern a Choice regex. If a Choice contains another Choice, the contained Choice is 
 * replaced by its children in the container Choice 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
class ChoiceOptimizer extends RecursiveRegexOptimizer {

	/**
	 * @see RegexOptimizer#optimizeRegex(RegularExpression)
	 */
	@Override
	public boolean optimizeRegex(RegularExpression regex) {
		boolean isChoice = regex instanceof Choice;
		if(isChoice){
			Choice regexChoice = (Choice) regex;
			for(int i=0;i<regexChoice.elementCount();i++){
				RegularExpression child = regexChoice.getElement(i);
				boolean isChildChoice = child instanceof Choice;
				if(!isChildChoice)
					continue;
				Choice childChoice = (Choice) child;
				List<RegularExpression> childrenOfChild = childChoice.getImmutableListOfElements();
				regexChoice.remove(i);
				regexChoice.addAllElements(i, childrenOfChild);
				return true;
			}
		}
		return recurse(regex);
	}
}
