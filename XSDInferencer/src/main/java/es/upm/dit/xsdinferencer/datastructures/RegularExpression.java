package es.upm.dit.xsdinferencer.datastructures;

/**
 * Regular expression of elements which describes the structure of children that an element of a concrete complex type can have.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface RegularExpression extends Comparable<RegularExpression> {
	
	/**
	 * Returns the element of the regular expression at index or null if there is no element at that index.
	 * @param index index of the element.
	 * @return the element of the regular expression at index or null if there is no element at that index.
	 */
	public RegularExpression getElement(int index);
	
	/**
	 * @return How many elements has the regular expression.
	 */
	public int elementCount();
	
	/**
	 * Sets an element at the specified position (Optional operation).
	 * @param index the index to set
	 * @param element the new element
	 * @throws IndexOutOfBoundsException if it is not possible to set the element at the given index
	 * @throws UnsupportedOperationException if the implementation does not support it
	 */
	public void setElement(int index, RegularExpression element);
	
	/**
	 * Tests whether the regular expression contains a given subexpression (it does not search recursively)
	 * @param element the subexpression to search
	 * @return whether the regex contains the subexpression as direct subelement (it means, it is not searched recursively)
	 */
	public boolean containsElement(RegularExpression element);
	
	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj);
	
	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode();
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString();
}
