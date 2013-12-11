
package es.upm.dit.xsdinferencer.conversion;

import es.upm.dit.xsdinferencer.datastructures.RegularExpression;

/**
 * Interface for optimizers, which take a regular expression as an input and transform it in order 
 * to optimize it in a particular way, which depends on each implementation.
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface RegexOptimizer {
	/**
	 * This method performs the optimization on the input regular expression. All the manipulations are made 
	 * directly to the input object, it implies that all the modifications are seen by anyone who has a reference 
	 * to the input object
	 * @param regex the input regular expression
	 * @return true if there has been any modification
	 */
	public boolean optimizeRegex(RegularExpression regex);
}
