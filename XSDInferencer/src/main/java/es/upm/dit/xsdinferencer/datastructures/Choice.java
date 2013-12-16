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
