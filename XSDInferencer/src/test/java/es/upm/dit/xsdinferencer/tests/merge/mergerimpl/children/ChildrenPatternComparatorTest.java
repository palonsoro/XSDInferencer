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
package es.upm.dit.xsdinferencer.tests.merge.mergerimpl.children;

import static es.upm.dit.xsdinferencer.datastructures.Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.merge.ChildrenPatternComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.children.EqualsPatternComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.children.NodeBasedPatternComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.children.NodeSubsumptionPatternComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.children.ReducePatternComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.children.SubsumptionPatternComparator;

/**
 * Test for {@link ChildrenPatternComparator} and all its implementations: {@link EqualsPatternComparator}, {@link NodeBasedPatternComparator}, 
 * {@link NodeSubsumptionPatternComparator}, {@link ReducePatternComparator} and {@link SubsumptionPatternComparator}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class ChildrenPatternComparatorTest {
	
	//Fields for testing
	/**
	 * An element called A
	 */
	private SchemaElement elementA;
	
	/**
	 * An element called B
	 */
	private SchemaElement elementB;
	
	/**
	 * An element called C
	 */
	private SchemaElement elementC;
	
	/**
	 * The initial state of automatons
	 */
	private SchemaElement initialState;
	
	/**
	 * The final state of automatons
	 */
	private SchemaElement finalState;
	
	/**
	 * First testing automaton
	 */
	private ExtendedAutomaton automaton1;
	
	/**
	 * Second testing automaton
	 */
	private ExtendedAutomaton automaton2;
	
	/**
	 * Third testing automaton
	 */
	private ExtendedAutomaton automaton3;
	
	/**
	 * Fourth testing automaton
	 */
	private ExtendedAutomaton automaton4;
	
	/**
	 * Fifth testing automaton
	 */
	private ExtendedAutomaton automaton5;
	
	/**
	 * Sixth testing automaton
	 */
	private ExtendedAutomaton automaton6;
	
	/**
	 * Seventh testing automaton
	 */
	private ExtendedAutomaton automaton7;
	
	/**
	 * Eighth testing automaton
	 */
	private ExtendedAutomaton automaton8;

	@Before
	public void setUp() throws Exception {
		
		elementA=new SchemaElement("A", "", null);
		elementB=new SchemaElement("B", "", null);
		elementC=new SchemaElement("C", "", null);
		initialState=new SchemaElement("initial", DEFAULT_PSEUDOELEMENTS_NAMESPACE, null);
		finalState=new SchemaElement("final", DEFAULT_PSEUDOELEMENTS_NAMESPACE, null);
		
		
		automaton1=new ExtendedAutomaton();
		automaton1.setInitialState(initialState);
		automaton1.setFinalState(finalState);
		automaton1.addEdge(initialState, elementA, 2L);
		automaton1.addEdge(elementA, elementB, 1L);
		automaton1.addEdge(elementA, elementC, 1L);
		automaton1.addEdge(elementB, elementB, 1L);
		automaton1.addEdge(elementB, elementC, 1L);
		automaton1.addEdge(elementC, finalState, 2L);
		
		automaton2=new ExtendedAutomaton(automaton1);
				
		automaton3=new ExtendedAutomaton(automaton1);
		automaton3.removeNode(elementB);
		automaton3.addEdge(initialState, elementA, 1L);
		automaton3.addEdge(elementC, finalState, 1L);
		
		automaton4=new ExtendedAutomaton();
		automaton4.setInitialState(initialState);
		automaton4.setFinalState(finalState);
		automaton4.addEdge(initialState, elementC, 2L);
		automaton4.addEdge(elementC, elementB, 1L);
		automaton4.addEdge(elementC, elementA, 1L);
		automaton4.addEdge(elementB, elementA, 1L);
		automaton4.addEdge(elementA, finalState, 2L);
		
		automaton5=new ExtendedAutomaton(automaton4);
		automaton5.removeNode(elementB);
		automaton5.addEdge(initialState, elementC, 1L);
		automaton5.addEdge(elementA, finalState, 1L);
		
		automaton6=new ExtendedAutomaton(automaton1);
		automaton6.removeEdge(elementB, elementB);
		
		automaton7=new ExtendedAutomaton(automaton6);
		automaton7.removeNode(elementC);
		automaton7.addEdge(initialState, elementA, 1L);
		automaton7.addEdge(elementB, finalState, 1L);
		
		automaton8=new ExtendedAutomaton(automaton1);
		automaton8.addEdge(initialState, finalState);
	}

	/**
	 * Checks that all the comparators (including Reduce with threshold 0) return true 
	 * for equal automatons
	 */
	@Test
	public void testAllTrue() {
		assertTrue(new EqualsPatternComparator().compare(automaton1, automaton2));
		assertTrue(new NodeBasedPatternComparator().compare(automaton1, automaton2));
		assertTrue(new NodeSubsumptionPatternComparator().compare(automaton1, automaton2));
		assertTrue(new ReducePatternComparator(0f).compare(automaton1, automaton2));
		assertTrue(new SubsumptionPatternComparator().compare(automaton1, automaton2));
	}
	
	/**
	 * Checks that all the comparators (including Reduce with threshold 0) return true 
	 * for equal automatons
	 */
	@Test
	public void testAllTrueSame() {
		assertTrue(new EqualsPatternComparator().compare(automaton1, automaton1));
		assertTrue(new NodeBasedPatternComparator().compare(automaton1, automaton1));
		assertTrue(new NodeSubsumptionPatternComparator().compare(automaton1, automaton1));
		assertTrue(new ReducePatternComparator(0f).compare(automaton1, automaton1));
		assertTrue(new SubsumptionPatternComparator().compare(automaton1, automaton1));
	}
	
	/**
	 * Checks that all the comparators except Equals and NodeBased return true if 
	 * on automaton subsumes all the nodes and edges of the other automaton (but are not equal).
	 * It also tests the symmetric situation (by swapping the automatons to compare).
	 */
	@Test
	public void testSubsumedTrue() {
		assertFalse(new EqualsPatternComparator().compare(automaton1, automaton3));
		assertFalse(new NodeBasedPatternComparator().compare(automaton1, automaton3));
		assertTrue(new NodeSubsumptionPatternComparator().compare(automaton1, automaton3));
		assertTrue(new SubsumptionPatternComparator().compare(automaton1, automaton3));
		
		assertFalse(new EqualsPatternComparator().compare(automaton3, automaton1));
		assertFalse(new NodeBasedPatternComparator().compare(automaton3, automaton1));
		assertTrue(new NodeSubsumptionPatternComparator().compare(automaton3, automaton1));
		assertTrue(new SubsumptionPatternComparator().compare(automaton3, automaton1));
	}

	/**
	 * Checks that all the comparators except equals and subsumed return true if both 
	 * automatons have the same nodes but completely different edges.
	 */
	@Test
	public void testNodeBasedTrue() {
		assertFalse(new EqualsPatternComparator().compare(automaton1, automaton4));
		assertTrue(new NodeBasedPatternComparator().compare(automaton1, automaton4));
		assertTrue(new NodeSubsumptionPatternComparator().compare(automaton1, automaton4));
		assertFalse(new SubsumptionPatternComparator().compare(automaton1, automaton4));
		
		assertFalse(new EqualsPatternComparator().compare(automaton4, automaton1));
		assertTrue(new NodeBasedPatternComparator().compare(automaton4, automaton1));
		assertTrue(new NodeSubsumptionPatternComparator().compare(automaton4, automaton1));
		assertFalse(new SubsumptionPatternComparator().compare(automaton4, automaton1));
	}
	
	/**
	 * Checks that only NodeSubsumed returns true if the nodes of an automaton are contained in 
	 * the other automaton but both automatons do not have common edges.
	 */
	@Test
	public void testNodeSubsumedTrue() {
		assertFalse(new EqualsPatternComparator().compare(automaton1, automaton5));
		assertFalse(new NodeBasedPatternComparator().compare(automaton1, automaton5));
		assertTrue(new NodeSubsumptionPatternComparator().compare(automaton1, automaton5));
		assertFalse(new SubsumptionPatternComparator().compare(automaton1, automaton5));
		
		assertFalse(new EqualsPatternComparator().compare(automaton5, automaton1));
		assertFalse(new NodeBasedPatternComparator().compare(automaton5, automaton1));
		assertTrue(new NodeSubsumptionPatternComparator().compare(automaton5, automaton1));
		assertFalse(new SubsumptionPatternComparator().compare(automaton5, automaton1));
	}
	
	/**
	 *  Checks that all the comparators return false if both automatons do not have common edges 
	 *  and the nodes of none of them are contained in the other one.
	 */
	@Test
	public void testAllFalse() {
		assertFalse(new EqualsPatternComparator().compare(automaton5, automaton7));
		assertFalse(new NodeBasedPatternComparator().compare(automaton5, automaton7));
		assertFalse(new NodeSubsumptionPatternComparator().compare(automaton5, automaton7));
		assertFalse(new SubsumptionPatternComparator().compare(automaton5, automaton7));
		
		assertFalse(new EqualsPatternComparator().compare(automaton7, automaton5));
		assertFalse(new NodeBasedPatternComparator().compare(automaton7, automaton5));
		assertFalse(new NodeSubsumptionPatternComparator().compare(automaton7, automaton5));
		assertFalse(new SubsumptionPatternComparator().compare(automaton7, automaton5));
	}
	
	/**
	 * Checks that the Reduce comparator returns true if the threshold is 0.2 and the distance 
	 * 1/9 
	 */
	@Test
	public void testReduceTrue(){
		assertTrue(new ReducePatternComparator(0.2f).compare(automaton1, automaton6));
		assertTrue(new ReducePatternComparator(0.2f).compare(automaton6, automaton1));
	}
	
	/**
	 * Checks that the Reduce comparator returns false if the threshold is 0.1 and the distance 
	 * 1/9
	 */
	@Test
	public void testReduceFalse(){
		assertFalse(new ReducePatternComparator(0.1f).compare(automaton1, automaton6));
		assertFalse(new ReducePatternComparator(0.1f).compare(automaton6, automaton1));
	}
	
	/**
	 * Checks whether two empty automatons are always similar
	 */
	@Test
	public void testEmptyAutomatonsAreSimilar(){
		ExtendedAutomaton automaton1Empty=new ExtendedAutomaton();
		ExtendedAutomaton automaton2Empty=new ExtendedAutomaton();
		
		automaton1Empty.setInitialState(initialState);
		automaton1Empty.setFinalState(finalState);
		automaton1Empty.addEdge(initialState, finalState);
		
		automaton2Empty.setInitialState(initialState);
		automaton2Empty.setFinalState(finalState);
		automaton2Empty.addEdge(initialState, finalState);
		
		assertTrue(new EqualsPatternComparator().compare(automaton1Empty, automaton2Empty));
		assertTrue(new NodeBasedPatternComparator().compare(automaton1Empty, automaton2Empty));
		assertTrue(new NodeSubsumptionPatternComparator().compare(automaton1Empty, automaton2Empty));
		assertTrue(new ReducePatternComparator(0f).compare(automaton1Empty, automaton2Empty));
		assertTrue(new SubsumptionPatternComparator().compare(automaton1Empty, automaton2Empty));
	}
	
	/**
	 * In this test, we will compare an empty automaton, which really means an automaton that contains an 
	 * initial state, a final state and an edge between them, with a non-empty automaton that has 
	 * no edge between the initial and the final state.
	 * The expected results are:
	 * <ul>
	 * <li>Equals: False</li>
	 * <li>Node based: False</li>
	 * <li>Node subsumed: True (the nodes of the empty automaton, the initial and final state, are contained on the non-empty one).</li>
	 * <li>Subsumed: False</li>
	 * <li>Reduce: True with a threshold>2. False otherwise</li>
	 * </ul>
	 * Note that NodeSubsumption and Subsumption should have returned true if the empty automaton if the 
	 * empty automaton was really empty (without initial and final state), because the empty set (of nodes 
	 * or edges in this situation) is a subset of any other set. 
	 */
	@Test
	public void testEmptyAutomatonAndAutomaton1(){
		
		ExtendedAutomaton automatonEmpty=new ExtendedAutomaton();
		
		automatonEmpty.setInitialState(initialState);
		automatonEmpty.setFinalState(finalState);
		automatonEmpty.addEdge(initialState, finalState);
		
		assertFalse(new EqualsPatternComparator().compare(automaton1, automatonEmpty));
		assertFalse(new NodeBasedPatternComparator().compare(automaton1, automatonEmpty));
		assertFalse(new NodeSubsumptionPatternComparator().compare(automaton1, automatonEmpty));
		assertFalse(new SubsumptionPatternComparator().compare(automaton1, automatonEmpty));
		
		assertFalse(new ReducePatternComparator(1.995f).compare(automaton1, automatonEmpty));
		assertFalse(new ReducePatternComparator(1.995f).compare(automatonEmpty, automaton1));
		
		assertFalse(new EqualsPatternComparator().compare(automatonEmpty, automaton1));
		assertFalse(new NodeBasedPatternComparator().compare(automatonEmpty, automaton1));
		assertFalse(new NodeSubsumptionPatternComparator().compare(automatonEmpty, automaton1));
		assertFalse(new SubsumptionPatternComparator().compare(automatonEmpty, automaton1));
		
		assertTrue(new ReducePatternComparator(2.005f).compare(automatonEmpty, automaton1));
		assertTrue(new ReducePatternComparator(2.005f).compare(automaton1, automatonEmpty));
		
	}
	
	/**
	 * In this test, we will compare an empty automaton, which really means an automaton that contains an 
	 * initial state, a final state and an edge between them, with a non-empty automaton that does have 
	 * an edge between the initial and the final state.
	 * The expected results are:
	 * <ul>
	 * <li>Equals: False</li>
	 * <li>Node based: False</li>
	 * <li>Node subsumed: True (the nodes of the empty automaton, the initial and final state, are contained on the non-empty one).</li>
	 * <li>Subsumed: True</li>
	 * <li>Reduce: True with a threshold>0.8888888. False otherwise</li>
	 * </ul>
	 * Note that NodeSubsumption and Subsumption should have returned true if the empty automaton if the 
	 * empty automaton was really empty (without initial and final state), because the empty set (of nodes 
	 * or edges in this situation) is a subset of any other set. 
	 */
	@Test
	public void testEmptyAutomatonAndAutomaton8(){
		ExtendedAutomaton automatonEmpty=new ExtendedAutomaton();
		
		automatonEmpty.setInitialState(initialState);
		automatonEmpty.setFinalState(finalState);
		automatonEmpty.addEdge(initialState, finalState);
		
		assertFalse(new EqualsPatternComparator().compare(automaton8, automatonEmpty));
		assertFalse(new NodeBasedPatternComparator().compare(automaton8, automatonEmpty));
		assertFalse(new NodeSubsumptionPatternComparator().compare(automaton8, automatonEmpty));
		assertFalse(new SubsumptionPatternComparator().compare(automaton8, automatonEmpty));
		
		assertFalse(new ReducePatternComparator(0.85f).compare(automaton8, automatonEmpty));
		assertFalse(new ReducePatternComparator(0.85f).compare(automatonEmpty, automaton8));
				
		assertFalse(new EqualsPatternComparator().compare(automatonEmpty, automaton8));
		assertFalse(new NodeBasedPatternComparator().compare(automatonEmpty, automaton8));
		assertFalse(new NodeSubsumptionPatternComparator().compare(automatonEmpty, automaton8));
		assertFalse(new SubsumptionPatternComparator().compare(automatonEmpty, automaton8));
		
		assertTrue(new ReducePatternComparator(0.89f).compare(automatonEmpty, automaton8));
		assertTrue(new ReducePatternComparator(0.89f).compare(automaton8, automatonEmpty));
	}
	
}
