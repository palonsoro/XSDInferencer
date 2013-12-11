
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
