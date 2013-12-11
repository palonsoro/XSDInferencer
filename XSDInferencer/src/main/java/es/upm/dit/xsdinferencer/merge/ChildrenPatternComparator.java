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
