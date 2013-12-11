
package es.upm.dit.xsdinferencer.datastructures;

/**
 * Repeated at least once.
 * {@link SingularRegularExpression} whose content may occur 1 or more times.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class RepeatedAtLeastOnce extends SingularRegularExpression {

	/**
	 * Constructor
	 * @param content of the repeated at least once
	 * @throws NullPointerException if content==null
	 * @see SingularRegularExpression#SingularRegularExpression(RegularExpression)
	 */
	public RepeatedAtLeastOnce(RegularExpression content) {
		super(content);
	}

	/**
	 * @see Object#equals(Object) 
	 */
	@Override
	public boolean equals(Object obj){
		return super.equals(obj)&&(obj instanceof RepeatedAtLeastOnce);
	}
	
	/**
	 * @return an String representation of this regular expression
	 * @see Object#toString()
	 */
	@Override
	public String toString(){
		return toStringCommon("+");
	}
}
