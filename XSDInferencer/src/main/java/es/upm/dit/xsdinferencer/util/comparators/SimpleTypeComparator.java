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

import es.upm.dit.xsdinferencer.datastructures.SimpleType;

/**
 * Name-based comparator for {@link SimpleType} objects.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class SimpleTypeComparator implements Comparator<SimpleType> {
	
	/**
	 * @see Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(SimpleType o1, SimpleType o2) {
		return o1.getName().compareTo(o2.getName());
	}

}
