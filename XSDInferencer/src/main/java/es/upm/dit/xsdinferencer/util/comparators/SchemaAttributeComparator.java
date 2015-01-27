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
package es.upm.dit.xsdinferencer.util.comparators;

import java.util.Comparator;

import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;

/**
 * Name-based comparator for {@link SchemaAttribute} objects.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class SchemaAttributeComparator extends SchemaNodeComparator implements Comparator<SchemaAttribute>{

	/**
	 * @see Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(SchemaAttribute o1, SchemaAttribute o2) {
		int superCompare = super.compareNodes(o1, o2);
		if (superCompare!=0){
			return superCompare;
		}
		else{
			return o1.getSimpleType().getName().compareTo(o2.getSimpleType().getName());
		}
		
	}

}
