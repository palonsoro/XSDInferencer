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
package es.upm.dit.xsdinferencer.datastructures;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import es.upm.dit.xsdinferencer.datastructures.exceptions.NoWordHasBeenLearnedYetException;

/**
 * This kind of automaton, which may only have elements ({@link SchemaElement} objects) as nodes, 
 * holds extra information about occurrences of elements on each learned word and provides extra 
 * methods which only have sense for {@link SchemaElement} nodes.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class ExtendedAutomaton extends Automaton<SchemaElement> {
	
	/**
	 * For each source word (row index) contains how many times an SchemaElement 
	 * of it (column index) occurs at that word (value).
	 */
	private Table<Integer,SchemaElement, Integer> sourceWordSymbolOccurrences;
	
	/**
	 * @return the sourceWordSymbolOccurrences
	 */
	public Table<Integer, SchemaElement, Integer> getSourceWordSymbolOccurrences() {
		return sourceWordSymbolOccurrences;
	}

	/**
	 * Default constructor
	 */
	public ExtendedAutomaton() {
		super();
		sourceWordSymbolOccurrences=HashBasedTable.create();
	}

	/**
	 * Copy constructor.
	 * @param otherExtendedAutomaton ExtendedAutomaton to copy
	 */
	public ExtendedAutomaton(
			ExtendedAutomaton otherExtendedAutomaton) {
		super(otherExtendedAutomaton);
		sourceWordSymbolOccurrences=HashBasedTable.create(otherExtendedAutomaton.sourceWordSymbolOccurrences);
	}

	/**
	 * Returns the minimum and maximum occurrences of a factor in each learned word. 
	 * Occurrences of a factor means the sum of the occurrences of the symbols of 
	 * the factor.
	 * @param factor
	 * @return a map with two keys, "max" and "min", whose values contain the maximum and minimum 
	 * occurrences.
	 * @throws NullPointerException if factor is null.
	 * @throws NoWordHasBeenLearnedYetException if no word has been learned yet via {@link ExtendedAutomaton#learn(List)}. 
	 */
	public Map<String,Integer> getFactorMinMaxOccurrences(EquivalenceClass factor) {
		if(factor==null)
			throw new NullPointerException("'factor' must not be null");
		if(sourceWordSymbolOccurrences.isEmpty())
			throw new NoWordHasBeenLearnedYetException();
		Map<String,Integer> result = new HashMap<String,Integer>(2);
		result.put("max", 0);
		result.put("min", Integer.MAX_VALUE);
		for(Map<SchemaElement,Integer> occurrencesAtWord: sourceWordSymbolOccurrences.rowMap().values()){
			int occurrenceSum=0; //Sum of occurrences of each individual symbol in the current word
			for(SchemaElement element: factor){
				if(occurrencesAtWord.containsKey(element)){
					occurrenceSum+=occurrencesAtWord.get(element);
				}
			}
			result.put("max", Math.max(result.get("max"), occurrenceSum));
			result.put("min", Math.min(result.get("min"), occurrenceSum));
		}
		return result;
	}
	
	/**
	 * Returns the minimum and maximum occurrences of any individual symbol of a equivalence class 
	 * in each learned word.
	 * @param factor
	 * @return a map with two keys, "max" and "min", whose values contain the maximum and minimum 
	 * occurrences.
	 * @throws NullPointerException if factor is null
	 * @throws NoWordHasBeenLearnedYetException if no word has been learned yet via {@link ExtendedAutomaton#learn(List)}.
	 */
	public Map<String,Integer> getFactorSymbolMinMaxOccurrences(EquivalenceClass factor) {
		if(factor==null)
			throw new NullPointerException("'factor' must not be null");
		if(sourceWordSymbolOccurrences.isEmpty())
			throw new NoWordHasBeenLearnedYetException();
		Map<String,Integer> result = new HashMap<String,Integer>(2);
		result.put("max", 0);
		result.put("min", Integer.MAX_VALUE);
		for(Map<SchemaElement,Integer> occurrencesAtWord: sourceWordSymbolOccurrences.rowMap().values()){
			for(SchemaElement element: factor){
				if(!occurrencesAtWord.containsKey(element)){
					result.put("max", Math.max(result.get("max"), 0));
					result.put("min", Math.min(result.get("min"), 0));
				} else {
					result.put("max", Math.max(result.get("max"), occurrencesAtWord.get(element)));
					result.put("min", Math.min(result.get("min"), occurrencesAtWord.get(element)));
				}
				
			}
		}
		return result;
	}

	/**
	 * It learns a word in the way that Automaton does, but also updates the symbol occurrences information 
	 * stored by this kind of automaton.
	 * @see es.upm.dit.xsdinferencer.datastructures.Automaton#learn(java.util.List)
	 */
	@Override
	public void learn(List<SchemaElement> word) {
		super.learn(word);
		int newWordIndex = sourceWordSymbolOccurrences.rowKeySet().size()+1;
		for(SchemaElement element: word){
			if(!sourceWordSymbolOccurrences.contains(newWordIndex, element)){
				sourceWordSymbolOccurrences.put(newWordIndex, element, 1);
			} else {
				sourceWordSymbolOccurrences.put(newWordIndex, 
						element, 
						sourceWordSymbolOccurrences.get(newWordIndex, element) + 1);
				
			}
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
//		if (!(obj instanceof ExtendedAutomaton)) {
//			return false;
//		}
//		ExtendedAutomaton other = (ExtendedAutomaton) obj;
//		if (sourceWordSymbolOccurrences == null) {
//			if (other.sourceWordSymbolOccurrences != null) {
//				return false;
//			}
//		} else if (!sourceWordSymbolOccurrences
//				.equals(other.sourceWordSymbolOccurrences)) {
//			return false;
//		}
		return true;
	}
	
	/**
	 * @see Automaton#rehashDatastrucures()
	 */
	@Override
	public void rehashDatastrucures(){
		super.rehashDatastrucures();
		sourceWordSymbolOccurrences=HashBasedTable.create(sourceWordSymbolOccurrences);
	}
	
	/**
	 * It looks for an SchemaElement of the automaton such that it is equalsIgnoreType to a given one.
	 * If there is not such a node, it returns null.
	 * @param otherNode the node whose equivalent is searched
	 * @return an equivalent node or null if there is no equivalent node
	 */
	public SchemaElement getEquivalentNode(SchemaElement otherNode){
		for(SchemaElement node: this.nodes){
			if(node.equalsIgnoreType(otherNode))
				return node;
		}
		return null;
	}

	/**
	 * Returns true if the automaton contains all the nodes of the collection. 
	 * The main difference between this and its parent {@link Automaton#containsAllNodes(Collection)} 
	 * is that {@linkplain SchemaElement} are compared via {@link SchemaElement#equalsIgnoreType(SchemaNode)} 
	 * instead of using traditional containsAll methods
	 * @param nodesCollection a collection of nodes
	 * @return true if this automaton contains all those nodes, false otherwise
	 * @throws NullPointerException if nodesCollection is null
	 */
	public boolean containsAllEquivalentNodes(Collection<? extends SchemaElement> nodesCollection){
		checkNotNull(nodesCollection,"'nodes' must not be null");
		Set<SchemaElement> nodesImmutable = this.getNodesImmutable();
		nodesCollectionLoop:
		for(SchemaElement node: nodesCollection){
			if(node.getNamespace().equals(DEFAULT_PSEUDOELEMENTS_NAMESPACE))
				continue;
			for(SchemaElement nodeOriginal: nodesImmutable){
				if(nodeOriginal.getNamespace().equals(DEFAULT_PSEUDOELEMENTS_NAMESPACE))
					continue;
				if(nodeOriginal.equalsIgnoreType(node))
					continue nodesCollectionLoop;//We have found a node of this automaton which equalsIgnoreType that node of the collection, so we may continue looking for other nodes of the collection
			}
			return false; //We have not found any node of this automaton which is equalsIgnoreType to the current node, so it is not contained and we must return false
		}
		return true;
	}
	
	/**
	 * Returns true if the automaton contains all the nodes of another automaton. 
	 * The main difference between this and its parent {@link Automaton#containsAllNodes(Collection)} 
	 * is that {@linkplain SchemaElement} are compared via {@link SchemaElement#equalsIgnoreType(SchemaNode)} 
	 * instead of using traditional containsAll methods
	 * @param automaton other automaton
	 * @return true if this automaton contains all those nodes, false otherwise
	 * @throws NullPointerException if nodesCollection is null
	 */
	public boolean containsAllEquivalentNodes(ExtendedAutomaton automaton){
		return containsAllEquivalentNodes(automaton.nodes);
	}
	
	/**
	 * Returns true if the automaton contains all the nodes and edges of the other automaton
	 * @param other another automaton
	 * @return true if this automaton contains all those nodes, false otherwise
	 * @throws NullPointerException if other is null
	 */
	public boolean containsAllEquivalentNodesAndEdges(ExtendedAutomaton other){
		if(!containsAllEquivalentNodes(other))
			return false;
		for(SchemaElement src:other.edges.rowKeySet()){
			for(SchemaElement dst:other.edges.rowMap().get(src).keySet()){
				if(!edges.contains(this.getEquivalentNode(src), this.getEquivalentNode(dst)))
						return false;
			}
		}
		return true;
	}
	
	/**
	 * This method checks whether there is an edge between the equivalent nodes of two given ones
	 * @param node1 the source node
	 * @param node2 the destination node
	 * @return true if there are such an edge, false otherwise.
	 */
	public boolean containsEdgeEquivalent(SchemaElement node1, SchemaElement node2){
		return edges.contains(getEquivalentNode(node1), getEquivalentNode(node2));
	}
	
	/**
	 * Method that checks whether this automaton is equal to other automaton except for 
	 * the weights of the edges, ignoring types of nodes.
	 * @param other other automaton
	 * @return true if the other automaton has the same nodes and edges (although the edges have different weights)
	 */
	public boolean equalsIgnoreWeightsAndTypes(ExtendedAutomaton other) {
		
		if (edges == null) {
			if (other.edges != null) {
				return false;
			}
		} else {
			for(SchemaElement node1:other){
				for(SchemaElement node2:other.getOutgoingEdges(node1).keySet()){
					if(!this.containsEdgeEquivalent(node1, node2))
						return false;
				}
			}
			for(SchemaElement node1:this){
				for(SchemaElement node2:this.getOutgoingEdges(node1).keySet()){
					if(!other.containsEdgeEquivalent(node1, node2))
						return false;
				}
			}
		}
		if (finalState == null) {
			if (other.finalState != null) {
				return false;
			}
		} else if (!finalState.equals(other.finalState)) {
			return false;
		}
		if (initialState == null) {
			if (other.initialState != null) {
				return false;
			}
		} else if (!initialState.equals(other.initialState)) {
			return false;
		}
		if (nodes == null) {
			if (other.nodes != null) {
				return false;
			}
		} else if (!(this.containsAllEquivalentNodes(other) && other.containsAllEquivalentNodes(this))) {
			return false;
		}
		return true;
	}
	

}
