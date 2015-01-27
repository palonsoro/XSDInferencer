/*
Copyright 2013 Universidad Politécnica de Madrid - Center for Open Middleware (http://www.centeropenmiddleware.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package es.upm.dit.xsdinferencer.datastructures;

import java.util.Objects;

/**
 * Abstract class which must be inherited by any regular expression with a single 
 * subexpression.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public abstract class SingularRegularExpression implements RegularExpression {
	
	/**
	 * Content of the singular regular expression.
	 */
	protected RegularExpression content;
	
	/**
	 * Constructor
	 * @param content content of the regular expression.
	 * @throws NullPointerException if content==null
	 */
	protected SingularRegularExpression(RegularExpression content) {
		if(content==null)
			throw new NullPointerException("'content' must not be null");
		this.content = content;
	}

	/**
	 * Returns the single element of the regular expression
	 * @param index must be 0
	 * @return the single element of the regular expression if index==0, null otherwise.
	 * @see es.upm.dit.xsdinferencer.datastructures.RegularExpression#getElement(int)
	 */
	@Override
	public RegularExpression getElement(int index) {
		if(index==0){
			return content;
		} else{
			return null;
		}
	}

	/**
	 * @see es.upm.dit.xsdinferencer.datastructures.RegularExpression#elementCount()
	 */
	@Override
	public int elementCount() {
		return 1;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((content == null) ? 0 : content.hashCode());
//		return result;
		return Objects.hash(content);
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
		if (!(obj instanceof SingularRegularExpression)) {
			return false;
		}
		SingularRegularExpression other = (SingularRegularExpression) obj;
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!content.equals(other.content)) {
			return false;
		}
		return true;
	}
	
	/**
	 * This method implements some of the common steps of the toString() method of classes 
	 * which extend this one.<br/>
	 * Concretely, given a <i>qualifier</i>, it generates a representation which will be: 
	 * a1<i>qualifier</i> if a1 is a single element (or an empty regular expression) or 
	 * (r1)<i>qualifier</i> if r1 is a more complex subexpression (which descends from 
	 * SingularRegularExpression or MultipleRegularExpression).
	 * @param qualifier the qualifier of the concrete regular expression to be represented.
	 * @return A string as described.  
	 */
	protected String toStringCommon(String qualifier){
		StringBuilder resultBuilder = new StringBuilder();
//		boolean wrapIntoParenthesis = (content instanceof SingularRegularExpression)||
//				(content instanceof MultipleRegularExpression);
		boolean wrapIntoParenthesis = (content instanceof SingularRegularExpression);
		if(wrapIntoParenthesis)
			resultBuilder.append("(");
		resultBuilder.append(content.toString());
		if(wrapIntoParenthesis)
			resultBuilder.append(")");
		resultBuilder.append(qualifier);
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
		return content.equals(element);
	}

	/**
	 * @see RegularExpression#setElement(int, RegularExpression)
	 */
	@Override
	public void setElement(int index, RegularExpression element) {
		if(index!=0)
			throw new IndexOutOfBoundsException("A SingularRegularExpression only has content at index 0");
		content=element;
	}

}
