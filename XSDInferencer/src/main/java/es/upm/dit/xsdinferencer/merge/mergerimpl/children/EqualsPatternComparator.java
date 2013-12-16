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
package es.upm.dit.xsdinferencer.merge.mergerimpl.children;

import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.merge.ChildrenPatternComparator;

/**
 * Comparator that returns true if both automatons are equal (ignoring weights)
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class EqualsPatternComparator implements ChildrenPatternComparator {

	/**
	 * @see ChildrenPatternComparator#compare(ExtendedAutomaton, ExtendedAutomaton)
	 */
	@Override
	public boolean compare(ExtendedAutomaton automaton1,
			ExtendedAutomaton automaton2) {
		
		return automaton1.equalsIgnoreWeightsAndTypes(automaton2);
	}
}
