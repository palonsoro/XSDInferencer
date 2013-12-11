
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
