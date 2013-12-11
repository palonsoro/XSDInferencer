
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
