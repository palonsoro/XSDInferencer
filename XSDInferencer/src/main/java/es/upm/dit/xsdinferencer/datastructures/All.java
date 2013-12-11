package es.upm.dit.xsdinferencer.datastructures;

import java.util.Collection;
import java.util.Objects;

/**
 * All regular expression.
 * It allows its elements to occur in any order.
 * Because of limitations of XSD (related to the Unique Particle Attribution), 
 * this regular expression may only contain elements, each of them must occur once 
 * or zero times (it means, no element is allowed to be repeated in any way).
 * Because of limitations of the eCRX algorithm used to infer these kinds of regular 
 * expressions, if one of the subelements is optional, all of them are optional.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class All extends MultipleRegularExpression {

	/**
	 *  Minimum occurrence times for each element. It must be 0 or 1.
	 */
	private int minOccurs;
	
	/**
	 * Constructor (every single element must occur once)
	 * @param contents elements of the all expression
	 */
	public All(SchemaElement... contents) {
		super(contents);
		minOccurs=1;
	}
	
	/**
	 * Constructor.
	 * @param contents elements of the all expression
	 * @param minOccurs the minimum number of times that each element may occur
	 * @throws NullPointerException if contents==null
	 * @throws IllegalArgumentException if minOccurs is not either 0 or 1
	 */
	public All(SchemaElement[] contents, int minOccurs){
		super(contents);
		if(minOccurs!=0 && minOccurs!=1){
			throw new IllegalArgumentException("'minOccurs' must be 0 or 1");
		}
		this.minOccurs=minOccurs;
	}

	/**
	 * Constructor (every single element must occur once)
	 * @param contents elements of the all expression
	 * @see MultipleRegularExpression#MultipleRegularExpression(RegularExpression[])
	 */
	public All(Collection<? extends SchemaElement> contents) {
		super(contents);
		minOccurs=1;
	}
	
	/**
	 * Constructor.
	 * @param contents elements of the all expression
	 * @param minOccurs the minimum number of times that each element may occur
	 * @throws NullPointerException if contents==null
	 * @throws IllegalArgumentException if minOccurs is not either 0 or 1
	 */
	public All(Collection<? extends SchemaElement> contents, int minOccurs){
		super(contents);
		if(minOccurs!=0 && minOccurs!=1){
			throw new IllegalArgumentException("'minOccurs' must be 0 or 1");
		}
		this.minOccurs=minOccurs;
	}

	
	/**
	 * Getter
	 * @return Minimum occurrence times for each element
	 */
	public int getMinOccurs() {
		return minOccurs;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(contents,minOccurs);
	}

	/**
	 * @see Object#equals(Object) 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof All)) {
			return false;
		}
		All other = (All) obj;
		if (minOccurs != other.minOccurs) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return an String representation of this regular expression
	 * @see Object#toString()
	 */
	@Override
	public String toString(){
		String result = toStringCommon("&");
		if(minOccurs==0)
			result+="?";
		return result;
	}

	/**
	 * Sets the minimum number of occurrences of an element, which must be 0 or 1
	 * @param minOccurs the value to set
	 */
	public void setMinOccurs(int minOccurs) {
		if(minOccurs!=0 && minOccurs!=1){
			throw new IllegalArgumentException("'minOccurs' must be 0 or 1");
		}
		this.minOccurs=minOccurs;
	}

}
