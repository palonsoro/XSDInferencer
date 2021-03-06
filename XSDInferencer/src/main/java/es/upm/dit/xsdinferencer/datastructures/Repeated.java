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
