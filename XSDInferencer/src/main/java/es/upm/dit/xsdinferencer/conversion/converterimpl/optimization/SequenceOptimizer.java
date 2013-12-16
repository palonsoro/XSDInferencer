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
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Sequence;

/**
 * This optimizer tries to flattern a Sequence regex. If a Sequence contains another Sequence, the contained Sequence is 
 * replaced by its children in the container Sequence 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
class SequenceOptimizer extends RecursiveRegexOptimizer {

	/**
	 * @see RegexOptimizer#optimizeRegex(RegularExpression)
	 */
	@Override
	public boolean optimizeRegex(RegularExpression regex) {
		boolean isSequence = regex instanceof Sequence;
		if(isSequence){
			Sequence regexSequence = (Sequence) regex;
			for(int i=0;i<regexSequence.elementCount();i++){
				RegularExpression child = regexSequence.getElement(i);
				boolean isChildSequence = child instanceof Sequence;
				if(!isChildSequence)
					continue;
				Sequence childSequence = (Sequence) child;
				List<RegularExpression> childrenOfChild = childSequence.getImmutableListOfElements();
				regexSequence.remove(i);
				regexSequence.addAllElements(i, childrenOfChild);
				return true;
			}
		}
		return recurse(regex);
	}
}
