package es.upm.dit.xsdinferencer.datastructures;

/**
 * Repeated.
 * {@link SingularRegularExpression} whose content may occur 0 or more times.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class Repeated extends SingularRegularExpression {

	/**
	 * Constructor
	 * @param content content of the repeated 
	 * @throws NullPointerException if content==null
	 * @see SingularRegularExpression#SingularRegularExpression(RegularExpression)
	 */
	public Repeated(RegularExpression content) {
		super(content);
	}
	
	/**
	 * @see Object#equals(Object) 
	 */
	@Override
	public boolean equals(Object obj){
		return super.equals(obj)&&(obj instanceof Repeated);
	}
	
	/**
	 * @return an String representation of this regular expression
	 * @see Object#toString()
	 */
	@Override
	public String toString(){
		return toStringCommon("*");
	}

}
