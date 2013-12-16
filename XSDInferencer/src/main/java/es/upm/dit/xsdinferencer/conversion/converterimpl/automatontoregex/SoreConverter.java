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
package es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import es.upm.dit.xsdinferencer.conversion.RegexConverter;
import es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex.exceptions.NoSuchRegexCanBeInferredException;
import es.upm.dit.xsdinferencer.datastructures.Automaton;
import es.upm.dit.xsdinferencer.datastructures.Choice;
import es.upm.dit.xsdinferencer.datastructures.EmptyRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.Optional;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.RepeatedAtLeastOnce;
import es.upm.dit.xsdinferencer.datastructures.Sequence;

/**
 * This converter tries to infer a SORE from a given automaton by using the <i>Rewrite</i> algorithm.
 * This converter may fail for certain input automatons.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
class SoreConverter implements RegexConverter{
	
	/**
	 * This method tries to apply as many times as possible the <i>Disjunction</i> rule of the algorithm.
	 * @param regexpAutomaton the regular expression automaton
	 * @return true if any modification has been made.
	 */
	private boolean soreRuleDisjunction(Automaton<RegularExpression> regexpAutomaton){
		boolean automatonChanged=false;
		
		//This strange "always restart" way to iterate is used because the iterators 
		//fast-fail where there has been any modification during the iterations
		//as reported by the corresponding documentation
		boolean changedAtIteration=true;
		mainIterationLoop:
		while(changedAtIteration){
			changedAtIteration=false;
			for(RegularExpression node1:regexpAutomaton){
				Set<RegularExpression> choiceCandidates = new HashSet<RegularExpression>();
				for(RegularExpression node2:regexpAutomaton){
					if(regexpAutomaton.getIncomingEdges(node1).keySet().equals(regexpAutomaton.getIncomingEdges(node2).keySet())&&
							regexpAutomaton.getOutgoingEdges(node1).keySet().equals(regexpAutomaton.getOutgoingEdges(node2).keySet()))
						choiceCandidates.add(node2);
				}
				if(choiceCandidates.size()>1){
					changedAtIteration=true;
					automatonChanged=true;
					Choice newNode = new Choice(choiceCandidates.toArray(new RegularExpression[choiceCandidates.size()]));
					regexpAutomaton.substituteNodes(choiceCandidates, newNode);
					continue mainIterationLoop;
				}
			}
		}
		
		return automatonChanged;
	}

	/**
	 * This method tries to apply as many times as possible the <i>Concatenation</i> rule of the algorithm.
	 * @param regexpAutomaton the regular expression automaton
	 * @return true if any modification has been made.
	 */
	private boolean soreRuleConcatenation(Automaton<RegularExpression> regexpAutomaton){
		boolean automatonChanged=false;
		boolean changedAtIteration=true;
		
		mainIterationLoop:
		while(changedAtIteration){
			changedAtIteration=false;
			nodeIterationLoop:
			for(RegularExpression startingNode:regexpAutomaton){
				if(regexpAutomaton.getOutgoingEdges(startingNode).keySet().size()!=1)
					continue;
				List<RegularExpression> sequenceCandidate = new ArrayList<>();
				sequenceCandidate.add(startingNode);
				Set<RegularExpression> outgoingEdgeDestinations = regexpAutomaton.getOutgoingEdges(startingNode).keySet();
//				RegularExpression nextNode = outgoingEdgeDestinations.iterator().next();
				RegularExpression nextNode;
				while(outgoingEdgeDestinations.size()==1 &&
						regexpAutomaton.getIncomingEdges(nextNode=outgoingEdgeDestinations.iterator().next()).size()==1){
					if(sequenceCandidate.contains(nextNode))
						continue nodeIterationLoop; //Here, we avoid indirect self-loops
					sequenceCandidate.add(nextNode);
					outgoingEdgeDestinations = regexpAutomaton.getOutgoingEdges(nextNode).keySet();
					
//					nextNode = outgoingEdgeDestinations.iterator().next();
				}
				if(sequenceCandidate.size()<=1)
					continue nodeIterationLoop; 
				//This is in order to fix the self-loop weight (it may not be necessary to take care about weights)
				long selfLoopWeight = regexpAutomaton.getEdgeWeight(sequenceCandidate.get(sequenceCandidate.size()-1), sequenceCandidate.get(0));
				Sequence newNode = new Sequence(sequenceCandidate);
				regexpAutomaton.substituteNodes(sequenceCandidate, newNode);
				regexpAutomaton.removeEdge(newNode, newNode);
				if(selfLoopWeight>0){
					regexpAutomaton.addEdge(newNode, newNode, selfLoopWeight);
				}
				automatonChanged=true;
				changedAtIteration=true;
				continue mainIterationLoop;
			}
		}
		return automatonChanged;
	}
	
	/**
	 * This method tries to apply as many times as possible the <i>Self-Loop</i> rule of the algorithm.
	 * @param regexpAutomaton the regular expression automaton
	 * @return true if any modification has been made.
	 */
	private boolean soreRuleSelfLoop(Automaton<RegularExpression> regexpAutomaton){
		boolean automatonChanged=false;
		for(RegularExpression node: ImmutableSet.copyOf(regexpAutomaton)){
			if(regexpAutomaton.getEdgeWeight(node, node)>0){
				regexpAutomaton.removeEdge(node, node);
				RepeatedAtLeastOnce newNode = new RepeatedAtLeastOnce(node);
				regexpAutomaton.substituteNodes(node, newNode);
				automatonChanged=true;
			}
		}
		return automatonChanged;
	}
	
	/**
	 * This method tries to apply as many times as possible the <i>Optional</i> rule of the algorithm.
	 * @param regexpAutomaton the regular expression automaton
	 * @return true if any modification has been made.
	 */
	private boolean soreRuleOptional(Automaton<RegularExpression> regexpAutomaton){
		boolean automatonChanged=false;
		nodeIterationLoop:
		for(RegularExpression node: ImmutableSet.copyOf(regexpAutomaton)){
			//First, we check that the successors set of 'node' is contained in 
			//the successor set of any node that belongs to the predecessors set of 'node'
			Set<RegularExpression> predecessorsOfNode = regexpAutomaton.getIncomingEdges(node).keySet();
			Set<RegularExpression> successorsOfNode = regexpAutomaton.getOutgoingEdges(node).keySet();
			if(predecessorsOfNode.isEmpty()||successorsOfNode.isEmpty())
				continue nodeIterationLoop; //If the node has not any predeccessor or successor, this rule does not apply.
			for(RegularExpression predecessor: predecessorsOfNode){
				Set<RegularExpression> successorsOfPredecessor = regexpAutomaton.getOutgoingEdges(predecessor).keySet();
				if(!successorsOfPredecessor.containsAll(successorsOfNode))
					continue nodeIterationLoop;
				
			}
			Optional newNode = new Optional(node);
			for(RegularExpression predecessor:predecessorsOfNode){
				if(predecessor.equals(node))
					continue;
				for(RegularExpression successor:successorsOfNode){
					if(successor.equals(node))
						continue;
					regexpAutomaton.removeEdge(predecessor, successor);
				}
			}
			regexpAutomaton.substituteNodes(node, newNode);
			automatonChanged=true;
		}
		return automatonChanged;
	}
	
	/**
	 * @see RegexConverter#convertAutomatonToRegex(ExtendedAutomaton)
	 */
	@Override
	public RegularExpression convertAutomatonToRegex(ExtendedAutomaton automaton) throws NoSuchRegexCanBeInferredException {
		Automaton<RegularExpression> regexpAutomaton = new Automaton<RegularExpression> (automaton);
		if(regexpAutomaton.nodeCount()==0)
			return new EmptyRegularExpression();
		if(regexpAutomaton.nodeCount()==1)
			return regexpAutomaton.iterator().next();
		boolean changed = true;
		while(changed){
			changed=false;
			changed=changed||soreRuleOptional(regexpAutomaton);
			changed=changed||soreRuleDisjunction(regexpAutomaton);
			changed=changed||soreRuleConcatenation(regexpAutomaton);
			changed=changed||soreRuleSelfLoop(regexpAutomaton);
		}
		if(regexpAutomaton.nodeCount()==1)
			return regexpAutomaton.iterator().next();
		else
			throw new NoSuchRegexCanBeInferredException("No more rewrite rules can be applied, so this algorithm can not infer a SORE from this automaton.");
	}
	
	
}
