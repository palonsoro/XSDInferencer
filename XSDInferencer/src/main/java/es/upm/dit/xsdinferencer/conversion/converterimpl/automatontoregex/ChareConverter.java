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
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;

import es.upm.dit.xsdinferencer.conversion.RegexConverter;
import es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex.exceptions.NoSuchRegexCanBeInferredException;
import es.upm.dit.xsdinferencer.datastructures.Automaton;
import es.upm.dit.xsdinferencer.datastructures.Choice;
import es.upm.dit.xsdinferencer.datastructures.EmptyRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.EquivalenceClass;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.Optional;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Repeated;
import es.upm.dit.xsdinferencer.datastructures.RepeatedAtLeastOnce;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.Sequence;
import es.upm.dit.xsdinferencer.util.guavapredicates.EquivalenceClassPredicates;

/**
 * This converter uses the CRX algorithm to infer a CHARE from a given automaton.
 * Note that this converter will never throw an {@linkplain NoSuchRegexCanBeInferredException} although the interface requires to declare 
 * it, as CRX never fails (in the worst situation, it infers a CHARE which is quite more general than the input automaton).
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
class ChareConverter implements RegexConverter {
	
	/**
	 * Guava {@linkplain Predicate} that returns true if an equivalence class is a singleton
	 */
	protected Predicate<EquivalenceClass> isSingleton = EquivalenceClassPredicates.isSinglenton();
	
	/**
	 * Given a node automaton, it computes the equivalence class automaton (the equivalence classes considered 
	 * are the ones of the equivalence relation defined by the algorithm).
	 * @param automaton an element automaton
	 * @return a equivalence class automaton
	 */
	protected Automaton<EquivalenceClass> computeEqClassAutomaton(ExtendedAutomaton automaton){
		Automaton<EquivalenceClass> eqClassAutomaton = new Automaton<>();
		Set<SchemaElement> skipNodes = new HashSet<>(automaton.nodeCount());
		skipNodes.add(automaton.getInitialState());
		skipNodes.add(automaton.getFinalState());
		List<EquivalenceClass> eqClasses = new ArrayList<>(automaton.nodeCount());
		eqClassesCreationLoop:
		for(SchemaElement element:automaton){
			if(skipNodes.contains(element))
				continue eqClassesCreationLoop;
			EquivalenceClass eqClass = new EquivalenceClass();
			eqClass.add(element);
			skipNodes.add(element);
			for(SchemaElement equivalentElement:automaton){
				if(equivalentElement.equals(element)||skipNodes.contains(equivalentElement))
					continue;
				if(automaton.getReachableNodes(element).contains(equivalentElement) &&
					automaton.getReachableNodes(equivalentElement).contains(element)){
					eqClass.add(equivalentElement);
					skipNodes.add(equivalentElement);
				}
			}
			eqClasses.add(eqClass);
			
		}
		eqClassAutomaton.addAllNodes(eqClasses);
		for(EquivalenceClass eqClass1:eqClassAutomaton){
			for(EquivalenceClass eqClass2:eqClassAutomaton){
				if(eqClass1.equals(eqClass2))
					continue;
				if(areThereEdgesBetweenNodes(automaton, eqClass1, eqClass2))
					eqClassAutomaton.addEdge(eqClass1, eqClass2);
					
			}
		}
		return eqClassAutomaton;
	}

	/**
	 * It check whether there are edges between elements of two given equivalence classes 
	 * on a given automaton
	 * @param automaton an automaton of nodes
	 * @param eqClass1 the first equivalence class
	 * @param eqClass2 the second equivalence class
	 */
	private boolean areThereEdgesBetweenNodes(ExtendedAutomaton automaton,
			EquivalenceClass eqClass1, EquivalenceClass eqClass2) {
		for(SchemaElement element1:eqClass1){
			for(SchemaElement element2:eqClass2){
				if(automaton.getEdgeWeight(element1, element2)>0)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * It merges the singleton nodes which have the same incoming and outgoing edges. It is a step of the CRX algorithm.
	 * @param eqClassAutomaton
	 */
	protected void mergeSingletonNodes(Automaton<EquivalenceClass> eqClassAutomaton){
		boolean changed = true;
		mainIterationLoop:
		while(changed){
			changed=false;
			Set<EquivalenceClass> singletons = ImmutableSet.copyOf(Iterators.filter(eqClassAutomaton.iterator(),isSingleton));
			
			for(EquivalenceClass singleton1: singletons){
				if(singleton1.size()!=1)
					continue; //It is not a singleton
				Set<EquivalenceClass> mergedSingletons = new HashSet<>(eqClassAutomaton.nodeCount());
				mergedSingletons.add(singleton1);
				for(EquivalenceClass singleton2:singletons){
					if(singleton1.equals(singleton2))
						continue;
					if(eqClassAutomaton.getIncomingEdges(singleton1).equals(eqClassAutomaton.getIncomingEdges(singleton2))&&
							eqClassAutomaton.getOutgoingEdges(singleton1).equals(eqClassAutomaton.getOutgoingEdges(singleton2))){
						mergedSingletons.add(singleton2);						
					}
				}
				if(mergedSingletons.size()>1){
					EquivalenceClass mergedEqClass = new EquivalenceClass(mergedSingletons);
					eqClassAutomaton.substituteNodes(mergedSingletons, mergedEqClass);
					changed=true;
					continue mainIterationLoop;
				}
			}
		}
	}
	
	/**
	 * Given an equivalence class and an automaton, it creates a factor, which is correctly wrapped depending on the source words info of the given automaton.
	 * @param eqClass the equivalence class.
	 * @param automaton the automaton.
	 * @return a wrapped factor.
	 */
	protected RegularExpression generateWrappedFactorRegularExpression(EquivalenceClass eqClass, ExtendedAutomaton automaton){
		RegularExpression unwrappedFactor;
		if(eqClass.size()==1){
			unwrappedFactor=eqClass.iterator().next();
		}
		else {
			ImmutableSet<SchemaElement> elementsOfFactor = ImmutableSet.copyOf(eqClass);
			unwrappedFactor=new Choice(elementsOfFactor.toArray(new RegularExpression[elementsOfFactor.size()]));
		}
		Map<String, Integer> factorMinMaxOccurrences = automaton.getFactorMinMaxOccurrences(eqClass);
		int max = factorMinMaxOccurrences.get("max");
		int min = factorMinMaxOccurrences.get("min");
		RegularExpression factor = unwrappedFactor;
		if(min==1 && max ==1){
			factor=unwrappedFactor;
		}
		else if(min==0 && max==1){
			factor=new Optional(unwrappedFactor);
		}
		else if(min==0 && max>1) {
			factor=new Repeated(unwrappedFactor);
		}
		else if(min>0 && max>1) {
			factor=new RepeatedAtLeastOnce(unwrappedFactor);
		}
		else{
			throw new IllegalArgumentException("For the equivalence class: "+eqClass.toString()+" getFactorMinMaxOccurrences() method has returned min="+min+" max="+max+" which does not make sense.\n"+
		"The only way that this may have happened is that an automaton with invalid sources word info had been provided.");
		}
		return factor;
	}

	/**
	 * Given an automaton of nodes and a topologically sorted node list of equivalence classes, it builds a regular expression in the way that CRX does it at its final steps.
	 * @param automaton an automaton of nodes
	 * @param topologicallySortedNodeList a topologically sorted node list of equivalence classes
	 * @return a CHARE
	 */
	protected RegularExpression buildRegularExpression(ExtendedAutomaton automaton, List<EquivalenceClass> topologicallySortedNodeList){
		List<RegularExpression> sequenceCandidate = new ArrayList<RegularExpression>(topologicallySortedNodeList.size());
		for(int i=0;i<topologicallySortedNodeList.size();i++){
			EquivalenceClass eqClass=topologicallySortedNodeList.get(i);
			RegularExpression factor = generateWrappedFactorRegularExpression(eqClass, automaton);
			sequenceCandidate.add(factor);			
		}
		return new Sequence(sequenceCandidate.toArray(new RegularExpression[sequenceCandidate.size()]));
	}
	
	/**
	 * @see RegexConverter#convertAutomatonToRegex(ExtendedAutomaton)
	 */
	@Override
	public RegularExpression convertAutomatonToRegex(ExtendedAutomaton automaton) throws NoSuchRegexCanBeInferredException {
		if(automaton.nodeCount()==0||automaton.nodeCount()==1)
			return new EmptyRegularExpression();
		Automaton<EquivalenceClass> eqClassAutomaton = computeEqClassAutomaton(automaton);
		mergeSingletonNodes(eqClassAutomaton);
		List<EquivalenceClass> topologicallySortedNodeList = eqClassAutomaton.getTopologicallySortedNodeList();
		return buildRegularExpression(automaton, topologicallySortedNodeList);
	}
}
