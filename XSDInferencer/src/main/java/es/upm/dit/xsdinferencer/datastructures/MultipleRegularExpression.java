package es.upm.dit.xsdinferencer.datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;


/**
 * Abstract class which must be inherited by any regular expression with more than one  
 * subexpression.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public abstract class MultipleRegularExpression implements RegularExpression {
	
	/**
	 * Subexpressions of the regular expression
	 */
	protected List<RegularExpression> contents;

	/**
	 * Constructor
	 * @param contents contents of the regular expression
	 * @throws NullPointerException if contents==null
	 */
	protected MultipleRegularExpression(RegularExpression[] contents) {
		if(contents==null)
			throw new NullPointerException("'contents' must not be null");
		this.contents = Lists.newArrayList(contents);//Arrays.asList MUST NOT be used here, because such a List is backed by the original array and does not allow removals. 
	}
	
	/**
	 * Constructor
	 * @param contents contents of the regular expression
	 * @throws NullPointerException if contents==null
	 */
	protected MultipleRegularExpression(Collection<? extends RegularExpression> contents){
		if(contents==null)
			throw new NullPointerException("'contents' must not be null");
		this.contents=new ArrayList<>(contents);
	}

	/**
	 * It returns the subexpression at the specified index or null, if it does not exist
	 * @param index the index to look for
	 * @return the subexpression at index index.
	 * @see es.upm.dit.xsdinferencer.datastructures.RegularExpression#getElement(int)
	 */
	@Override
	public RegularExpression getElement(int index) {
		if(index<0 || index >= contents.size()){
			return null;
		} else {
			return contents.get(index);
		}
	}

	/**
	 * Returns how many elements are there on the regular expression
	 * @see es.upm.dit.xsdinferencer.datastructures.RegularExpression#elementCount()
	 */
	@Override
	public int elementCount() {
		return contents.size();
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + Arrays.hashCode(contents);
//		return result;
		return Objects.hash((Object)contents);
	}

	/**
	 * @see Object#equals(Object) 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MultipleRegularExpression)) {
			return false;
		}
		MultipleRegularExpression other = (MultipleRegularExpression) obj;
		if (!contents.equals(other.contents)) {
			return false;
		}
		return true;
	}
	
	/**
	 * This method implements some of the common steps of the toString() method of classes 
	 * which extend this one.<br/>
	 * Concretely, given a <i>separator</i>, it generates a string like <code>(a1<i>separator</i>a2<i>separator</i>...<i>separator</i>an)</code>.
	 * @param contentsSeparator a separator between the subexpressions of the regular expression
	 * @return A string like (a1<i>separator</i>a2<i>separator</i>...<i>separator</i>an).
	 */
	protected String toStringCommon(String contentsSeparator){
		StringBuilder resultBuilder = new StringBuilder();
		resultBuilder.append("(");
		for(int i=0;i<contents.size();i++){
			resultBuilder.append(contents.get(i).toString());
			if(i<contents.size()-1)
				resultBuilder.append(contentsSeparator);
		}
		resultBuilder.append(")");
		String result=resultBuilder.toString();
		return result;
	}
	
	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(RegularExpression other) {
		return this.toString().compareTo(other.toString());
	}

	/**
	 * @see RegularExpression#containsElement(RegularExpression)
	 */
	@Override
	public boolean containsElement(RegularExpression element) {
		return contents.contains(element);
	}

	/**
	 * Removes a given subexpression.
	 * @param subexpression the regular expression to remove
	 * @return true if a regular expression has been removed
	 */
	public boolean remove(RegularExpression subexpression) {
		return contents.remove(subexpression);
	}

	/**
	 * Removes the subexpression at the given index
	 * @param index the index of the regular expression to remove
	 * @return the regular expression removed
	 */
	public RegularExpression remove(int index) {
		return contents.remove(index);
	}

	/**
	 * Adds a subexpression
	 * @param subexpression the subexpression
	 * @return true if the subexpression was added
	 */
	public boolean addElement(RegularExpression subexpression) {
		return contents.add(subexpression);
	}

	/**
	 * Adds all the subexpressions of a collection
	 * @param collection a collection of subexpressions
	 * @return true if the subexpressions were added
	 */
	public boolean addAllElements(Collection<? extends RegularExpression> collection) {
		return contents.addAll(collection);
	}

	/**
	 * Adds all the subexpressions from a collection at the specified index
	 * @param index the index to add them
	 * @param collection a collection of subexpressions
	 * @return true if the subexpressions were added
	 */
	public boolean addAllElements(int index, Collection<? extends RegularExpression> collection) {
		return contents.addAll(index, collection);
	}

	/**
	 * Adds a subexpression at the specified index
	 * @param index the index to add it
	 * @param element the subexpression
	 */
	public void addElement(int index, RegularExpression element) {
		contents.add(index, element);
	}
	
	/**
	 * Returns an ImmutableList of the contents of the array
	 * @return an ImmutableList of the contents of the array
	 */
	public List<RegularExpression> getImmutableListOfElements(){
		return ImmutableList.copyOf(contents);
	}

	/**
	 * @see RegularExpression#setElement(int, RegularExpression)
	 */
	@Override
	public void setElement(int index, RegularExpression element) {
		contents.set(index, element);
	}
	
}
