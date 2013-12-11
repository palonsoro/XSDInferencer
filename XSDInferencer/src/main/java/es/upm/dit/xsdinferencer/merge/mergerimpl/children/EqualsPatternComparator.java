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
