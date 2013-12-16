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
package es.upm.dit.xsdinferencer.merge.mergerimpl.attribute;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Collections2;

import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.merge.AttributeListComparator;
import es.upm.dit.xsdinferencer.util.guavapredicates.SchemaAttributePredicates;

/**
 * Attribute list comparator that returns true if two attribute list have the same required 
 * and optional attributes.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class StrictAttributeComparator implements AttributeListComparator {

	/**
	 * @see AttributeListComparator#compare(List, List)
	 */
	@Override
	public boolean compare(List<SchemaAttribute> attrList1,
			List<SchemaAttribute> attrList2) {
		
		//Required and optional attributes MUST be compared separately because an attribute may be 
		//equal to another although one is optional and the other one is not (as only name and namespace 
		//are considered for attribute equality).
		Set<SchemaAttribute> required1 = new HashSet<SchemaAttribute>(Collections2.filter(attrList1, SchemaAttributePredicates.isRequired()));
		Set<SchemaAttribute> required2 = new HashSet<SchemaAttribute>(Collections2.filter(attrList2, SchemaAttributePredicates.isRequired()));
		Set<SchemaAttribute> optional1 = new HashSet<SchemaAttribute>(Collections2.filter(attrList1, SchemaAttributePredicates.isOptional()));
		Set<SchemaAttribute> optional2 = new HashSet<SchemaAttribute>(Collections2.filter(attrList2, SchemaAttributePredicates.isOptional()));

		return required1.equals(required2) && optional1.equals(optional2);
	}
}
