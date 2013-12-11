
package es.upm.dit.xsdinferencer.conversion.converterimpl.optimization;

import es.upm.dit.xsdinferencer.conversion.RegexOptimizer;
import es.upm.dit.xsdinferencer.datastructures.MultipleRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.SingularRegularExpression;

/**
 * This optimizer transforms any {@linkplain MultipleRegularExpression} which has only one element into the 
 * single element that it contains.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
class SingletonOptimizer extends RecursiveRegexOptimizer {

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
				boolean isCurrentMultipleRegularExpression = currentRegex instanceof MultipleRegularExpression;
				if(!(isCurrentMultipleRegularExpression))
					continue;
				if(currentRegex.elementCount()==1){
					regex.setElement(i, currentRegex.getElement(0));
					return true;
				}
			}
		}
		return recurse(regex);
	}
}
