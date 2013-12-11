
package es.upm.dit.xsdinferencer.datastructures;

import java.util.Collection;

/**
 * Sequence regular expression. 
 * Each subexpression must occur once they all may follow the given order.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class Sequence extends MultipleRegularExpression {

	/**
	 * Constructor
	 * @param contents of the regular expression
	 * @throws NullPointerException if content==null
	 */
	public Sequence(RegularExpression... contents) {
		super(contents);
	}

	/**
	 * Constructor
	 * @param contents of the regular expression
	 * @throws NullPointerException if content==null
	 */
	public Sequence(Collection<? extends RegularExpression> contents) {
		super(contents);
	}
	
	/**
	 * @see Object#equals(Object) 
	 */
	@Override
	public boolean equals(Object obj){
		return super.equals(obj)&&(obj instanceof Sequence);
	}

	/**
	 * @return an String representation of this regular expression
	 * @see Object#toString()
	 */
	@Override
	public String toString(){
		//It should be
		//return toStringCommon("");
		//if we followed the theoretic convention. 
		//However, it leads to quite confusing and difficult to debug regular expressions.
		//So, in the implementation, we will represent them by:
		return toStringCommon(" ");
	}
}
