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
package es.upm.dit.xsdinferencer.merge.mergerimpl.children;

import es.upm.dit.xsdinferencer.datastructures.Automaton;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.merge.ChildrenPatternComparator;

/**
 * Children comparator that returns true if one automaton subsumes all the nodes and 
 * edges of the other automaton
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class SubsumptionPatternComparator implements ChildrenPatternComparator {

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
		return automaton1.containsAllEquivalentNodesAndEdges(automaton2)||
				automaton2.containsAllEquivalentNodesAndEdges(automaton1);
	}
}
