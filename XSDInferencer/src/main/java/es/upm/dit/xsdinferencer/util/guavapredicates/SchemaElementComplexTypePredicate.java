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
package es.upm.dit.xsdinferencer.util.guavapredicates;

import com.google.common.base.Predicate;

import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;

/**
 * Guava predicate that returns true if the complex type of the {@link SchemaElement} object 
 * is equal to one given at construction time. 
 * This class must be instantiated via {@link SchemaElementPredicates#complexTypeEquals(ComplexType)}
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 * @see Predicate
 * @see SchemaElementPredicates#complexTypeEquals(ComplexType)
 * @see SchemaElementPredicates
 */
class SchemaElementComplexTypePredicate implements Predicate<SchemaElement>{
	
	/**
	 * The {@link ComplexType} to compare with.
	 */
	private ComplexType complexTypeToCompare;

	/**
	 * Constructor
	 * @param complexTypeToCompare the complex type to compare the one of the SchemaElement objects
	 */
	SchemaElementComplexTypePredicate(ComplexType complexTypeToCompare) {
		this.complexTypeToCompare = complexTypeToCompare;
	}

	/**
	 * @see Predicate#apply(Object)
	 */
	@Override
	public boolean apply(SchemaElement element) {
		return element.getType().equals(complexTypeToCompare);
	}

}
