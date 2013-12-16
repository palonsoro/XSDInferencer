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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Table;

import es.upm.dit.xsdinferencer.datastructures.exceptions.NonAcyclicGraphException;

/**
 * This class describes an Automaton wherever an Automaton could be used. 
 * It always maintains edge weight information.
 * The type parameter allows to specify the type of the nodes of the automaton.
 * Some practical methods are also provided
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 * @param <E> The type of the nodes
 */
public class Automaton<E extends Comparable<? super E>> implements Cloneable,Iterable<E> {
	
	/**
	 * Contains all the nodes of the automaton excluding initial and final states.
	 */
	protected Set<E> nodes;
		
	/**
	 * Contains all the edges of the automaton and their weights.
	 * On each edge, the first key is the edge source, the second one is the edge destination 
	 * and the value is the weight. 
	 */
	protected Table<E,E,Long> edges;
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(nodes,edges,initialState,finalState);
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Automaton)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		Automaton<E> other = (Automaton<E>) obj;
		if (edges == null) {
			if (other.edges != null) {
				return false;
			}
		} else if (!edges.equals(other.edges)) {
			return false;
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
		} else if (!nodes.equals(other.nodes)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Method that checks whether this automaton is equal to other automaton except for 
	 * the weights of the edges.
	 * @param other other automaton
	 * @return true if the other automaton has the same nodes and edges (although the edges have different weights)
	 */
	public boolean equalsIgnoreWeights(Automaton<E> other) {
		if (edges == null) {
			if (other.edges != null) {
				return false;
			}
		} else {
			for(E node1:other){
				for(E node2:other.getOutgoingEdges(node1).keySet()){
					if(!edges.contains(node1, node2))
						return false;
				}
			}
			for(E node1:this){
				for(E node2:this.getOutgoingEdges(node1).keySet()){
					if(!other.edges.contains(node1, node2))
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
		} else if (!nodes.equals(other.nodes)) {
			return false;
		}
		return true;
	}

	/**
	 * Initial state, if it was necessary
	 */
	protected E initialState = null;
	
	/**
	 * Final state, if it was necessary
	 */
	protected E finalState = null;

	/**
	 * Namespace to which all the pseudo-elements must belong, so that they can be identified.
	 */
	public static final String DEFAULT_PSEUDOELEMENTS_NAMESPACE = "http://dit.upm.es/xsdinferencer/pseudoelements";
	
	/**
	 * @return the initialState
	 */
	public E getInitialState() {
		return initialState;
	}

	/**
	 * @param initialState the initialState to set
	 */
	public void setInitialState(E initialState) {
		this.initialState = initialState;
		if(initialState!=null)
			addNode(initialState);
	}
	
	/**
	 * Default constructor
	 */
	public Automaton(){
		nodes = new LinkedHashSet<E>();
		edges = HashBasedTable.create();
	}
	
	/**
	 * Copy constructor
	 */
	public Automaton(Automaton<? extends E> otherAutomaton){
		nodes = new LinkedHashSet<E>(otherAutomaton.nodes);
		edges = HashBasedTable.create(otherAutomaton.edges);
		this.initialState=otherAutomaton.initialState;
		this.finalState=otherAutomaton.finalState;
	}
	
	/**
	 * @return the finalState, if the automaton has got it
	 */
	public E getFinalState() {
		return finalState;
	}

	/**
	 * @param finalState the finalState to set, if the automaton has got it
	 */
	public void setFinalState(E finalState) {
		this.finalState = finalState;
		if(finalState!=null)
			addNode(finalState);
	}

	/**
	 * Returns how many nodes are present in the automaton
	 * @return the number of nodes
	 */
	public int nodeCount(){
		return nodes.size();
	}
	
	/**
	 * Returns how many edges are present in the automaton
	 * @return
	 */
	public int edgeCount(){
		return edges.size();
	}
	
	/**
	 * Clones the automaton.
	 */
	@Override
	public Object clone(){
		return new Automaton<E>(this);
	}

	/**
	 * Adds a node into the automaton
	 * @param node the node to add
	 * @throws NullPointerException if node is null
	 */
	public void addNode(E node) {
		if(node==null){
			throw new NullPointerException("'node' must not be null.");
		}
		nodes.add(node);
	}
	
	/**
	 * Returns whether the automaton contains a node or not.
	 * @param node node to look for
	 * @return if the automaton contains the node or not.
	 * @throws NullPointerException if node is null
	 */
	public boolean containsNode(E node){
		checkNotNull(node,"'node' must not be null");
		return nodes.contains(node);
	}
	
	/**
	 * Returns true if the automaton contains all the nodes of the collection
	 * @param nodesCollection a collection of nodes
	 * @return true if this automaton contains all those nodes, false otherwise
	 * @throws NullPointerException if nodesCollection is null
	 */
	public boolean containsAllNodes(Collection<? extends E> nodesCollection){
		checkNotNull(nodesCollection,"'nodes' must not be null");
		return this.nodes.containsAll(nodesCollection);
	}
	
	/**
	 * Returns true if the automaton contains all the nodes of the other automaton
	 * @param other another automaton
	 * @return true if this automaton contains all those nodes, false otherwise
	 * @throws NullPointerException if other is null
	 */
	public boolean containsAllNodes(Automaton<? extends E> other){
		checkNotNull(nodes,"'other' must not be null");
		return containsAllNodes(other.nodes);
	}
	
	/**
	 * Returns true if the automaton contains all the nodes and edges of the other automaton
	 * @param other another automaton
	 * @return true if this automaton contains all those nodes, false otherwise
	 * @throws NullPointerException if other is null
	 */
	public boolean containsAllNodesAndEdges(Automaton<? extends E> other){
		if(!containsAllNodes(other))
			return false;
		for(E src:other.edges.rowKeySet()){
			for(E dst:other.edges.rowMap().get(src).keySet()){
				if(!edges.contains(src, dst))
						return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns an unmodifiable Set (as provided by {@linkplain Collections#unmodifiableSet(Set)}) 
	 * that contains all the {@linkplain Table.Cell} objects that represent an edge.
	 * @return An unmodifiable set of edges.
	 */
	public Set<Table.Cell<E, E, Long>> getEdgeCellSet(){
		return Collections.unmodifiableSet(edges.cellSet());
	}

	/**
	 * Adds an edge between a source node and a destination node with the desired weight.<br/>
	 * If the edge previously existed, its weight is set to the given value.<br/>
	 * If one of the nodes had not been added previously to the automaton, it would be automatically added. 
	 * @param source source node
	 * @param destination destination node
	 * @param weight weight of the edge
	 * @throws NullPointerException if any parameter is null
	 */
	public void addEdge(E source, E destination, Long weight) {
		if(source==null || destination==null || weight==null){
			throw new NullPointerException("'source' and 'destination' cannot be null");
		}
		
		if(weight<=0){
			throw new IllegalArgumentException("'weight' must be >= 1");
		}
		
		if(!nodes.contains(source)){
			nodes.add(source);
		}
		
		if(!nodes.contains(destination)){
			nodes.add(destination);
		}
		
		edges.put(source, destination, weight);
	}
	
	/**
	 * Adds an edge between a source node and a destination node.<br/>
	 * If the edge previously existed, its weight is incremented by one. If not, it is created with weight 1.<br/>
	 * If one of the nodes had not been added previously to the automaton, it would be automatically added. 
	 * @param source source node
	 * @param destination destination node
	 * @throws NullPointerException if any parameter is null
	 */
	public void addEdge(E source, E destination) {
		if(source==null || destination==null){
			throw new NullPointerException("'source' and 'destination' cannot be null");
		}
		
		Long weight = edges.get(source, destination);
		
		if (weight==null || weight.longValue()==0){
			addEdge(source,destination,Long.valueOf(1));
		} else {
			addEdge(source,destination,++weight);
		}
	}
	
	/**
	 * Returns the edge weight, or 0 if there is no edge between those nodes.
	 * @param source source node
	 * @param destination destination node
	 * @return weight of the edge, 0 if it does no exist, -1 if one of the nodes does not belong to the automaton
	 * @throws NullPointerException if source==null or destination==null
	 * nodes of this automaton.
	 */
	public long getEdgeWeight(E source, E destination){
		rehashDatastrucures();
		if(source==null || destination == null)
			throw new NullPointerException("'source' and 'destination' must not be null");
		if(!this.containsNode(source) || !this.containsNode(destination))
			return -1;
		Long weight = edges.get(source, destination);
		if(weight==null){
			return 0;
		}else{
			return weight;
		}
	}

	/**
	 * Removes an edge between two nodes
	 * @param source
	 * @param destination
	 * @throws NullPointerException if source==null or destination==null
	 */
	public void removeEdge(E source, E destination) {
		if(source==null || destination == null)
			throw new NullPointerException("'source' and 'destination' must not be null");
		if(!this.containsNode(source) || !this.containsNode(destination))
			throw new IllegalArgumentException("'source' and 'destination' must be nodes of this automaton");
//		if(this.getEdgeWeight(source, destination)==0)
//			throw new IllegalArgumentException("there is no edge between those nodes");
		edges.remove(source, destination);
	}
	
	/**
	 * Removes a node from the automaton.<br/>
	 * Its incoming and outgoing edges are also removed.
	 * @param node node to remove
	 * @throws NullPointerException if node is null
	 * @throws IllegalArgumentException if the node does not belong to the automaton
	 */
	public void removeNode(E node) {
		if(node==null){
			throw new NullPointerException("'node' cannot be null");
		}
		if(!this.containsNode(node))
			throw new IllegalArgumentException("the node does not belong to this automaton");
		Map<E,Long> incoming = new HashMap<E,Long>(getIncomingEdges(node));
		
		for(E source: incoming.keySet()){
			removeEdge(source, node);
		}
		Map<E,Long> outgoing = new HashMap<E,Long>(getOutgoingEdges(node));
		for(E destination: outgoing.keySet()){
			removeEdge(node, destination);
		}
		if(node==initialState){
			initialState=null;
		}
		if(node==finalState){
			finalState=null;
		}
		nodes.remove(node);
	}
	
	/**
	 * Returns all incoming edges to a node
	 * @param node node
	 * @return a map whose keys are the sources of incoming edges and whose values are the weights.
	 * @throws NullPointerException if node is null
	 * @throws IllegalArgumentException if the node does not belong to the automaton
	 */
	public Map<E,Long> getIncomingEdges(E node) {
		rehashDatastrucures();
		if(node==null){
			throw new NullPointerException("'node' cannot be null");
		}
		if(!this.containsNode(node))
			throw new IllegalArgumentException("the node does not belong to this automaton");
		return edges.column(node);
	
	}

	/**
	 * Returns all outgoing edges from a node
	 * @param node node
	 * @return a map whose keys are the destinations of outgoing edges and whose values are the weights.
	 */
	public Map<E,Long> getOutgoingEdges(E node) {
		rehashDatastrucures();
		if(node==null){
			throw new NullPointerException("'node' cannot be null");
		}
		if(!nodes.contains(node))
			throw new IllegalArgumentException("the node does not belong to this automaton");
		return edges.row(node);
	
	}
	
//	/**
//	 * Replaces a node with another one, it means:<br/>
//	 * <ul>
//	 * <li>The new node is added</li>
//	 * <li>Each incoming and outgoing edge of the old node is remapped</li>
//	 * <li>The old node is removed</li>
//	 * </ul>
//	 * @param oldNode the old node to be replaced, it must exist previously
//	 * @param newNode the node which will replace the old one.
//	 * @throws NullPointerException if oldNode==null or newNode==null
//	 * @throws IllegalArgumentException if the oldNode does not belong to the automaton 
//	 * or the newNode belongs to the automaton.
//	 */
//	public void substituteNode(E oldNode, E newNode) {
//		if(oldNode==null || newNode==null)
//			throw new NullPointerException("'oldNode' and 'newNode' must not be null");
//		if(!this.containsNode(oldNode))
//			throw new IllegalArgumentException("'oldNode' must belong to the automaton");
//		nodes.add(newNode);
//		Map<E,Long> incomingOld = getIncomingEdges(oldNode);
//		Map<E,Long> outgoingOld = getOutgoingEdges(oldNode);
//		for(E source: incomingOld.keySet()){
//			long weight=edges.get(source, oldNode);
//			addEdge(source,oldNode,weight);
//			removeEdge(source, oldNode);
//		}
//		for(E destination: outgoingOld.keySet()){
//			long weight=edges.get(oldNode,destination);
//			addEdge(oldNode, destination, weight);
//			removeEdge(oldNode, destination);
//		}
//		nodes.remove(oldNode);
//	}

	/**
	 * Replaces a node with another one, it means:<br/>
	 * <ul>
	 * <li>The new node is added</li>
	 * <li>Each incoming and outgoing edge of each old node is remapped</li>
	 * <li>The old nodes are removed</li>
	 * </ul>
	 * @param oldNodes a collection of old nodes to be replaced, they must exist previously
	 * @param newNode the node which will replace the old ones.
	 * @throws NullPointerException if oldNode==null or newNode==null
	 * @throws IllegalArgumentException if the oldNode does not belong to the automaton 
	 * or the newNode belongs to the automaton.
	 */
	public void substituteNodes(Collection<E> oldNodes, E newNode) {
		if(oldNodes==null || newNode==null)
			throw new NullPointerException("'oldNodes' and 'newNode' must not be null");
		rehashDatastrucures();
//		for(E oldNode: oldNodes){
//			if(!this.containsNode(oldNode))
//				throw new IllegalArgumentException("all the nodes of 'oldNodes' must belong to the automaton");
//		}
		boolean ignoreAll=true;
		for(E oldNode: oldNodes){
			if(this.containsNode(oldNode))
				ignoreAll=false;
		}
		if(ignoreAll){
			return;
		}
		nodes.add(newNode);
		for(E oldNode: ImmutableSet.copyOf(oldNodes)){
			if(!nodes.contains(oldNode))
				continue;
			Map<E,Long> incomingOld = new HashMap<E,Long>(getIncomingEdges(oldNode));
			Map<E,Long> outgoingOld = new HashMap<E,Long>(getOutgoingEdges(oldNode));
			for(E source: incomingOld.keySet()){
				if(source.equals(oldNode))
					continue;
				long weight=getEdgeWeight(source, oldNode);
				long previousWeight=getEdgeWeight(source, newNode);
				if(previousWeight<0)
					previousWeight=0;
				long newWeight = weight+previousWeight;
				if(newWeight<1)
					newWeight=1;
				removeEdge(source, oldNode);
				addEdge(source,newNode,newWeight);
				
			}
			for(E destination: outgoingOld.keySet()){
				
				long weight=getEdgeWeight(oldNode,destination);
				long previousWeight=getEdgeWeight(newNode,destination);
				if(previousWeight<0)
					previousWeight=0;
				long newWeight = weight+previousWeight;
				if(newWeight<1)
					newWeight=1;
				removeEdge(oldNode, destination);
				if(destination.equals(oldNode))
					addEdge(newNode, newNode, newWeight);
				else
					addEdge(newNode, destination, newWeight);
				
			}
			if(!oldNode.equals(newNode))
				nodes.remove(oldNode);
		}
	}
	
	/**
	 * Overload of {@link Automaton#substituteNodes(Collection, Object)} useful 
	 * when only one node must be replaced
	 * @see Automaton#substituteNodes(Collection, Object)
	 */
	public void substituteNodes(E oldNode, E newNode) {
		if(oldNode==null)
			throw new NullPointerException("'oldNode' and 'newNode' must not be null");
		substituteNodes(Collections.singleton(oldNode), newNode);
	}
		
	/**
	 * It returns a Set with every node which is reachable from a node, by 
	 * following one or more edges.
	 * @param node
	 * @return Set of reachable nodes
	 */
	public Set<E> getReachableNodes(E node) {
		Set<E> reachableNodes = new HashSet<E>();
		addReachableNodesRecursive(node, reachableNodes);
		return reachableNodes;
	
	}
	
	/**
	 * Recursive helper method to get all the reachable nodes, starting from a node.<br/>
	 * It takes an startNode, gets its outgoing edges and, for each node which is not already 
	 * contained in the result set, that node is added to reachableNodes and all of its 
	 * outgoing nodes are examined by calling this method with the node as startNode.
	 * @param startNode the node from which the 
	 * @param reachableNodes
	 */
	private void addReachableNodesRecursive(E startNode, Set<E> reachableNodes){
		Map<E,Long> outgoing = getOutgoingEdges(startNode);
		for(E node : outgoing.keySet()){
			if(reachableNodes.contains(node)){
				continue; //VERY IMPORTANT, IN ORDER TO NOT RECURSE INFINITELY!!!!!!
			}
			reachableNodes.add(node);
			addReachableNodesRecursive(node, reachableNodes);
		}
	}
	
	/**
	 * Gets the leaves of the automaton, it means, the nodes with no outgoing edges.
	 * @return leaves
	 */
	public List<E> getLeaves() {
//		if(!this.isAcyclic())
//			throw new NonAcyclicGraphException();
		List<E> leaves = new ArrayList<E>();
		for(E node: nodes){
			if(getOutgoingEdges(node).keySet().size()==0){
				leaves.add(node);
			}
		}
		return leaves;
	}
	
	/**
	 * Computes the topologically sorted node list of the automaton.
	 * It will not work on acyclic graphs.
	 * @return a topologically sorted node list
	 * @throws NonAcyclicGraphException if the graph is not acyclic.
	 */
	public List<E> getTopologicallySortedNodeList() {
		if(!this.isAcyclic())
			throw new NonAcyclicGraphException();
		LinkedList<E> result = new LinkedList<E>(); //We need some of Deque methods implemented by LinkedList but not included in List
		Automaton<E> automatonCopy = new Automaton<E>(this);
		for(List<E> leaves = automatonCopy.getLeaves(); leaves.size()>0; leaves = automatonCopy.getLeaves()){
			E leave = leaves.get(leaves.size()-1);
			result.addFirst(leave);
			automatonCopy.removeNode(leave);
		}
		return result;
	}
	
	/**
	 * Learns a word into the automaton, by adding the correct edges and nodes.
	 * If the automaton has got them, the word must start by the initial state and end with the 
	 * final state
	 * @param word word to learn
	 */
	public void learn(List<E> word) {
		if(word==null){
			throw new NullPointerException("'word' must not be null");
		}
			
		if(word.size()==0){
			throw new IllegalArgumentException("'word' must contain at least one element");
		} 
		else if(initialState!=null && (initialState!=word.get(0) || Collections.frequency(word, initialState)>1)){
			throw new IllegalArgumentException("the automaton has an initial state, so the word must start at that initial state");
		}
		else if(finalState!=null && (finalState!=word.get(word.size()-1) || Collections.frequency(word, finalState)>1)){
			throw new IllegalArgumentException("the automaton has a final state, so the word must end at that final state");
		}
		else if(word.size()==1){
			addNode(word.get(0));
			return;
		}else {
			for(int i=0;i<word.size()-1;i++){
				addEdge(word.get(i), word.get(i+1));
			}
		}
	}

	/**
	 * @return an iterator over the nodes.
	 */
	@Override
	public Iterator<E> iterator() {
		List<E> nodesList = new LinkedList<E>(nodes);
		Collections.sort(nodesList);
		return Iterators.unmodifiableIterator(nodesList.iterator());
	}
	
	/**
	 * Checks whether this automaton is an acyclic graph or not
	 * @return true if the automaton is acyclic, false otherwise
	 */
	public boolean isAcyclic(){
		for(E node: this){
			if(this.getReachableNodes(node).contains(node))
				return false;
		}
		return true;
	}

	/**
	 * Adds all the nodes from a collection.
	 * @param newNodes a collection of nodes
	 * @return true if the nodes have been added
	 */
	public boolean addAllNodes(Collection<? extends E> newNodes) {
		return nodes.addAll(newNodes);
	}
	
	/**
	 * This method rebuilds the structures that contain information about nodes and edges, to prevent 
	 * that changes to fields which affect the hash code of nodes lead the structures to errors.
	 */
	public void rehashDatastrucures(){
		nodes = new HashSet<>(nodes);
		edges = HashBasedTable.create(edges);
	}

	/**
	 * @return an {@link ImmutableSet} with all the nodes of the automaton
	 */
	public final Set<E> getNodesImmutable() {
		return ImmutableSet.copyOf(nodes);
	}
}
