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

import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;

/**
 * Comparator that compares two automaton in order to determine 
 * whether the children structures of two complex types are 
 * similar enough.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface ChildrenPatternComparator {
	/**
	 * Method that performs the comparison.
	 * @param automaton1 the automaton of a complex type
	 * @param automaton2 the automaton of another complex type.
	 * @return true if the automatons are similar enough, false otherwise
	 */
	public boolean compare(ExtendedAutomaton automaton1, ExtendedAutomaton automaton2);
}
