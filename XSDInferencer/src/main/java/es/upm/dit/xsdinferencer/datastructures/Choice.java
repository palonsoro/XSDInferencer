package es.upm.dit.xsdinferencer.datastructures;

import java.util.Collection;

/**
 * Choice regular expression.
 * It means that only one of its subexpression may occur 
 * (in other words, you 'must choose' one).
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class Choice extends MultipleRegularExpression {

	/**
	 * Constructor
	 * @param contents contents of the regular expression
	 * @throws NullPointerException if content==null
	 */
	public Choice(RegularExpression... contents) {
		super(contents);
	}

	/**
	 * Constructor
	 * @param contents contents of the regular expression
	 * @throws NullPointerException if content==null
	 */
	public Choice(Collection<? extends RegularExpression> contents) {
		super(contents);
	}
	
	/**
	 * @see Object#equals(Object) 
	 */
	@Override
	public boolean equals(Object obj){
		return super.equals(obj)&&(obj instanceof Choice);
	}

	/**
	 * @return an String representation of this regular expression
	 * @see Object#toString()
	 */
	@Override
	public String toString(){
		//This is the one used at the papers, however, this may lead to confusion with RepeatedAtLeastOnce as we cannot use superscripts for it.
//		return toStringCommon("+"); 
		return toStringCommon("|");
	}
}
