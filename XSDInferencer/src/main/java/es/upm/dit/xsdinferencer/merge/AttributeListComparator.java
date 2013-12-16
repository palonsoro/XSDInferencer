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

import java.util.List;

import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;

/**
 * Comparator that checks whether the attribute lists from two complex types are similar enough. 
 * (Note that order is not relevant).
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface AttributeListComparator {
	/**
	 * Method that performs the comparison
	 * @param attrList1 an attribute list
	 * @param attrList2 another attribute list
	 * @return
	 */
	public boolean compare(List<SchemaAttribute> attrList1, List<SchemaAttribute> attrList2);
}
