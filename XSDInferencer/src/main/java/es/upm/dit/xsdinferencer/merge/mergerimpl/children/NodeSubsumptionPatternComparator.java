package es.upm.dit.xsdinferencer.merge.mergerimpl.children;

import es.upm.dit.xsdinferencer.datastructures.Automaton;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.merge.ChildrenPatternComparator;

/**
 * Children pattern comparator that returns true if an automaton contains all the nodes of the 
 * other automaton, ignoring edges.<br/>
 * There is an exception: If one of the automatons is empty (except for pseudoelements, it means, 
 * initial and final state) and the other one is not, then false is returned. This to avoid empty automatons 
 * to be merged with anything because, mathematically, the empty set is contained into any set.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class NodeSubsumptionPatternComparator implements ChildrenPatternComparator {

	/**
	 * @see ChildrenPatternComparator#compare(ExtendedAutomaton, ExtendedAutomaton)
	 */
	@Override
	public boolean compare(ExtendedAutomaton automaton1,
			ExtendedAutomaton automaton2) {
		automaton1.rehashDatastrucures();
		automaton2.rehashDatastrucures();
		ExtendedAutomaton automaton1copy=new ExtendedAutomaton(automaton1);
		for(SchemaElement node:automaton1){
			if(node.getNamespace().equals(Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE))
				automaton1copy.removeNode(node);
		}
		ExtendedAutomaton automaton2copy=new ExtendedAutomaton(automaton2);
		for(SchemaElement node:automaton2){
			if(node.getNamespace().equals(Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE))
				automaton2copy.removeNode(node);
		}
		if((automaton1copy.nodeCount()==0 && automaton2copy.nodeCount()!=0)||
				(automaton1copy.nodeCount()!=0 && automaton2copy.nodeCount()==0))
			return false;
		return automaton1copy.containsAllEquivalentNodes(automaton2copy)||
				automaton2copy.containsAllEquivalentNodes(automaton1copy);
	}
}
