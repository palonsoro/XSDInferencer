
package es.upm.dit.xsdinferencer.conversion.converterimpl.optimization;

import es.upm.dit.xsdinferencer.conversion.RegexOptimizer;
import es.upm.dit.xsdinferencer.datastructures.MultipleRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Optional;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Repeated;
import es.upm.dit.xsdinferencer.datastructures.RepeatedAtLeastOnce;
import es.upm.dit.xsdinferencer.datastructures.SingularRegularExpression;

//Based on the "repetition" optimizer from Kore Nordmann, but improved.
/**
 * This optimizer tries to simplify a {@linkplain SingularRegularExpression} which contains another {@linkplain SingularRegularExpression} by transforming it 
 * into another {@linkplain SingularRegularExpression} whose content is the same as the contained {@linkplain SingularRegularExpression} and such that the whole 
 * expression is equivalent to the original.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
class SingularRegularExpressionOptimizer extends RecursiveRegexOptimizer {

	/**
	 * @see RegexOptimizer#optimizeRegex(RegularExpression)
	 */
	@Override
	public boolean optimizeRegex(RegularExpression regex) {
		boolean isMultipleRegularExpression = regex instanceof MultipleRegularExpression;
		boolean isSingularRegularExpression = regex instanceof SingularRegularExpression;
		if(isMultipleRegularExpression||isSingularRegularExpression){
			for(int i=0;i<regex.elementCount();i++){
				RegularExpression currentRegex = regex.getElement(i);
				boolean isCurrentSingularRegularExpression = currentRegex instanceof SingularRegularExpression;
				if(!isCurrentSingularRegularExpression)
					continue;
				
				RegularExpression currentRegexChild = currentRegex.getElement(0);
				boolean isCurrentChildSingularRegularExpression = currentRegexChild instanceof SingularRegularExpression;
				if(!isCurrentChildSingularRegularExpression)
					continue;
				
				RegularExpression currentRegexChildContent = currentRegexChild.getElement(0);
				RegularExpression replacement=null;
				
				boolean isCurrentOptional = currentRegex instanceof Optional;
//				boolean isCurrentRepeated = currentRegex instanceof Repeated;
				boolean isCurrentRepeatedAtLeastOnce = currentRegex instanceof RepeatedAtLeastOnce;
				
				boolean isCurrentChildOptional = currentRegexChild instanceof Optional;
//				boolean isCurrentChildRepeated = currentRegexChild instanceof Repeated;
				boolean isCurrentChildRepeatedAtLeastOnce = currentRegexChild instanceof RepeatedAtLeastOnce;
				
				if(isCurrentOptional && isCurrentChildOptional){
					replacement = new Optional(currentRegexChildContent);
				}
				else if (isCurrentRepeatedAtLeastOnce && isCurrentChildRepeatedAtLeastOnce){
					replacement = new RepeatedAtLeastOnce(currentRegexChildContent);
				}
				else{ //This includes all the other possible parent-child combinations
					replacement = new Repeated(currentRegexChildContent);
				}
				//This check is intended to simplify modifications although it is not necessary with the current code
				if(replacement!=null){
					regex.setElement(i, replacement);
					return true;
				}
			}
		}
		return recurse(regex);
	}
}
