package es.upm.dit.xsdinferencer.datastructures;

/**
 * Optional regular expression. 
 * {@link SingularRegularExpression} whose content may occur 0 or 1 time.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class Optional extends SingularRegularExpression {

	/**
	 * Constructor
	 * @param content content of the optional regular expression
	 * @throws NullPointerException if content==null
	 * @see SingularRegularExpression#SingularRegularExpression(RegularExpression)
	 */
	public Optional(RegularExpression content) {
		super(content);
	}

	/**
	 * @see Object#equals(Object) 
	 */
	@Override
	public boolean equals(Object obj){
		return super.equals(obj)&&(obj instanceof Optional);
	}
	
	/**
	 * @return an String representation of this regular expression
	 * @see Object#toString()
	 */
	@Override
	public String toString(){
		return toStringCommon("?");
	}
}
