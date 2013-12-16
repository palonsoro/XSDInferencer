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
package es.upm.dit.xsdinferencer.merge;

import es.upm.dit.xsdinferencer.datastructures.SimpleType;

/**
 * Comparator that checks whether two {@link SimpleType} are similar enough. 
 * It only should be used if both complex types are enumerations.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface EnumComparator {
	/**
	 * Method that performs the comparison
	 * @param simpleType1 one simple type
	 * @param simpleType2 another simple type
	 * @return true if both simple types are similar, false otherwise.
	 */
	public boolean compare(SimpleType simpleType1, SimpleType simpleType2);
}
