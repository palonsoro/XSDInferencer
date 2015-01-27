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
 * Represents an empty regular expression, to which nothing matches.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class EmptyRegularExpression implements RegularExpression {

	/**
	 * It has no elements, so it always returns null
	 * @param index ignored
	 * @return null
	 */
	@Override
	public RegularExpression getElement(int index) {
		return null;
	}

	/**
	 * It has no elements, so it always returns 0
	 * @return 0
	 */
	@Override
	public int elementCount() {
		return 0;
	}
	
	/**
	 * An EmptyRegular expression is equal to any other empty regular expression.
	 * @param obj The object to compare
	 * @return true if the object is an EmptyRegularExpression
	 */
	@Override
	public boolean equals(Object obj){
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof EmptyRegularExpression)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the same hash code for every EmptyRegularExpression, based on the class object of EmptyRegularExpression
	 * @return a hash code which is the same for every EmptyRegularExpression object.
	 */
	@Override
	public int hashCode(){
		return Objects.hash(EmptyRegularExpression.class);
	}
	
	/**
	 * @return a String representation of this regular expression. The String "epsilon" is returned because 
	 * an epsilon symbol is the normal way to denote the empty regular expression and it is not a good 
	 * idea to use such an strange character.
	 */
	@Override
	public String toString(){
		return "epsilon";
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(RegularExpression o) {
		return this.toString().compareTo(o.toString());
	}

	/**
	 * An empty regular expression contains no subelements
	 * @see RegularExpression#containsElement(RegularExpression)
	 */
	@Override
	public boolean containsElement(RegularExpression element) {
		return false;
	}

	/**
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void setElement(int index, RegularExpression element) {
		throw new UnsupportedOperationException();
	}

}
