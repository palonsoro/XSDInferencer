/*
Copyright 2013 Universidad Polit�cnica de Madrid - Center for Open Middleware (http://www.centeropenmiddleware.com)

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
package es.upm.dit.xsdinferencer.merge.mergerimpl.attribute;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Collections2;

import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.merge.AttributeListComparator;
import es.upm.dit.xsdinferencer.util.guavapredicates.SchemaAttributePredicates;

/**
 * Equal attribute list comparator: Returns true if all the required attributes of a list 
 * are contained in the other list.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class EqualsAttributeComparator implements AttributeListComparator {

	/**
	 * @see AttributeListComparator#compare(List, List)
	 */
	@Override
	public boolean compare(List<SchemaAttribute> attrList1,
			List<SchemaAttribute> attrList2) {
		//Filter the required attributes
		Collection<SchemaAttribute> requiredAttrs1 = Collections2.filter(attrList1, SchemaAttributePredicates.isRequired());
		Collection<SchemaAttribute> requiredAttrs2 = Collections2.filter(attrList2, SchemaAttributePredicates.isRequired());
		
		boolean list1ContainsRequiredsFrom2 = attrList1.containsAll(requiredAttrs2);
		boolean list2ContainsRequiredsFrom1 = attrList2.containsAll(requiredAttrs1);
		return list1ContainsRequiredsFrom2 && list2ContainsRequiredsFrom1;
	}
}
