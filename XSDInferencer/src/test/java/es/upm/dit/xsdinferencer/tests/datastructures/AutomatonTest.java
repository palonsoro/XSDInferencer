package es.upm.dit.xsdinferencer.tests.datastructures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.upm.dit.xsdinferencer.datastructures.Automaton;
import es.upm.dit.xsdinferencer.datastructures.EquivalenceClass;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.exceptions.NonAcyclicGraphException;

/**
 * Test class for {@link Automaton} 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class AutomatonTest {
	
	//Fields for testing
	
	//Test nodes
	
	//Element automaton
	private Automaton<SchemaElement> elementAutomaton;
	//Nodes for elementAutomaton
	private SchemaElement element0 = mock(SchemaElement.class);
	private SchemaElement elementA = mock(SchemaElement.class);
	private SchemaElement elementB = mock(SchemaElement.class);
	private SchemaElement elementC = mock(SchemaElement.class);
	private SchemaElement elementD = mock(SchemaElement.class);
	private SchemaElement elementE = mock(SchemaElement.class);
	private SchemaElement elementF = mock(SchemaElement.class);
	private SchemaElement element1 = mock(SchemaElement.class);

	
	//Regular expression automaton 
	private Automaton<RegularExpression> regexpAutomaton;
	//Nodes for regexpAutomaton
	private RegularExpression regexp0 = mock(RegularExpression.class);
	private RegularExpression regexpA = mock(RegularExpression.class);
	private RegularExpression regexpB = mock(RegularExpression.class);
	private RegularExpression regexpC = mock(RegularExpression.class);
	private RegularExpression regexpD = mock(RegularExpression.class);
	private RegularExpression regexpE = mock(RegularExpression.class);
	private RegularExpression regexpF = mock(RegularExpression.class);
	private RegularExpression regexp1 = mock(RegularExpression.class);
	
	//EquivalenceClass automaton without initial and final state
	private Automaton<EquivalenceClass> eqClassAutomaton;
	//Nodes for eqClassAutomaton
	private EquivalenceClass eqClassA = mock(EquivalenceClass.class);
	private EquivalenceClass eqClassB = mock(EquivalenceClass.class);
	private EquivalenceClass eqClassC = mock(EquivalenceClass.class);
	private EquivalenceClass eqClassD = mock(EquivalenceClass.class);
	private EquivalenceClass eqClassE = mock(EquivalenceClass.class);
	private EquivalenceClass eqClassF = mock(EquivalenceClass.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Called before each test.
	 * Here, we create three testing automatons, they have identical nodes 
	 * and edges 
	 * except for some differences:
	 * <ul>
	 * <li>One is an SchemaElement ExtendedAutomaton with initial and final state 
	 * (here we will only test methods concerning Automaton).</li>
	 * <li>Another one is a RegularExpression Automaton with initial and final state</li>
	 * <li>Another one is an EquivalenceClass Automaton without initial and final state</li>
	 * </ul>
	 * We chose these examples because in our solution we will use these three kinds of automatons
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		elementAutomaton = new ExtendedAutomaton();
		elementAutomaton.addNode(element0);
		elementAutomaton.addNode(elementA);
		elementAutomaton.addNode(elementB);
		elementAutomaton.addNode(elementC);
		elementAutomaton.addNode(elementD);
		elementAutomaton.addNode(elementE);
		elementAutomaton.addNode(elementF);
		elementAutomaton.addNode(element1);
		elementAutomaton.addEdge(element0,elementA,(long) 2); //It is as if we have learned two words
		elementAutomaton.addEdge(elementA,elementB);
		elementAutomaton.addEdge(elementA,elementC);
		elementAutomaton.addEdge(elementB,elementD);
		elementAutomaton.addEdge(elementC,elementD);
		elementAutomaton.addEdge(elementD,elementE);
		elementAutomaton.addEdge(elementD,elementF);
		elementAutomaton.addEdge(elementE,element1);
		elementAutomaton.addEdge(elementF,element1);
		elementAutomaton.setInitialState(element0);
		elementAutomaton.setFinalState(element1);
		
		regexpAutomaton = new Automaton<RegularExpression>();
		regexpAutomaton.addNode(regexp0);
		regexpAutomaton.addNode(regexpA);
		regexpAutomaton.addNode(regexpB);
		regexpAutomaton.addNode(regexpC);
		regexpAutomaton.addNode(regexpD);
		regexpAutomaton.addNode(regexpE);
		regexpAutomaton.addNode(regexpF);
		regexpAutomaton.addNode(regexp1);
		regexpAutomaton.addEdge(regexp0,regexpA,(long) 2); //It is as if we have learned two words
		regexpAutomaton.addEdge(regexpA,regexpB);
		regexpAutomaton.addEdge(regexpA,regexpC);
		regexpAutomaton.addEdge(regexpB,regexpD);
		regexpAutomaton.addEdge(regexpC,regexpD);
		regexpAutomaton.addEdge(regexpD,regexpE);
		regexpAutomaton.addEdge(regexpD,regexpF);
		regexpAutomaton.addEdge(regexpE,regexp1);
		regexpAutomaton.addEdge(regexpF,regexp1);
		regexpAutomaton.setInitialState(regexp0);
		regexpAutomaton.setFinalState(regexp1);
		
		eqClassAutomaton = new Automaton<EquivalenceClass>();
		eqClassAutomaton.addNode(eqClassA);
		eqClassAutomaton.addNode(eqClassB);
		eqClassAutomaton.addNode(eqClassC);
		eqClassAutomaton.addNode(eqClassD);
		eqClassAutomaton.addNode(eqClassE);
		eqClassAutomaton.addNode(eqClassF);
		eqClassAutomaton.addEdge(eqClassA,eqClassB);
		eqClassAutomaton.addEdge(eqClassA,eqClassC);
		eqClassAutomaton.addEdge(eqClassB,eqClassD);
		eqClassAutomaton.addEdge(eqClassC,eqClassD);
		eqClassAutomaton.addEdge(eqClassD,eqClassE);
		eqClassAutomaton.addEdge(eqClassD,eqClassF);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for the default constructor {@link Automaton#Automaton()}.
	 * Checks that automatons created by the default constructor are empty 
	 * automatons with no errors, as expected.
	 */
	@SuppressWarnings("unused")
	@Test
	public void testAutomaton() {

		elementAutomaton = new ExtendedAutomaton();
		assertEquals(0,elementAutomaton.nodeCount());
		assertEquals(0,elementAutomaton.edgeCount());
		for(SchemaElement e: elementAutomaton){
			fail("There should be no elements in the automaton");
		}
		
		regexpAutomaton = new Automaton<RegularExpression>();
		assertEquals(0,regexpAutomaton.nodeCount());
		assertEquals(0,regexpAutomaton.edgeCount());
		for(RegularExpression e: regexpAutomaton){
			fail("There should be no elements in the automaton");
		}
		
		eqClassAutomaton = new Automaton<EquivalenceClass>();
		assertEquals(0,eqClassAutomaton.nodeCount());
		assertEquals(0,eqClassAutomaton.edgeCount());
		for(EquivalenceClass e: eqClassAutomaton){
			fail("There should be no elements in the automaton");
		}
		
	}

	/**
	 * Test method for the copy constructor {@link Automaton#Automaton(Automaton)}.
	 * Note: The ExtendedAutomaton of SchemaElement will no be tested here because this 
	 * constructor is overridden in ExtendedAutomaton, so it will be tested in ExtendedAutomatonTests.
	 */
	@Test
	public void testAutomatonAutomatonOfQextendsE() {
		
		Automaton<RegularExpression> regexpAutomatonCopy = new Automaton<RegularExpression>(regexpAutomaton);
		assertNotSame(regexpAutomaton,regexpAutomatonCopy);
		assertTrue(regexpAutomaton.equals(regexpAutomatonCopy));
		
		Automaton<EquivalenceClass> eqClassAutomatonCopy = new Automaton<EquivalenceClass>(eqClassAutomaton);
		assertNotSame(eqClassAutomaton,eqClassAutomatonCopy);
		assertTrue(eqClassAutomaton.equals(eqClassAutomatonCopy));
		
	}

	/**
	 * Test method for {@link Automaton#getInitialState()}.
	 * Checks that this getter returns the expected values for each automaton.
	 */
	@Test
	public void testGetInitialState() {
		assertEquals(element0,elementAutomaton.getInitialState());
		assertEquals(regexp0,regexpAutomaton.getInitialState());
		assertNull(eqClassAutomaton.getInitialState());
	}

	/**
	 * Test method for {@link Automaton#setInitialState(Object)}.
	 * Checks that nothing wrong happens when the initial state is changed 
	 * via the setter
	 */
	@Test
	public void testSetInitialState() {
		elementAutomaton.setInitialState(elementB);
		assertEquals(elementB,elementAutomaton.getInitialState());
		regexpAutomaton.setInitialState(regexpB);
		assertEquals(regexpB,regexpAutomaton.getInitialState());
	}
	
	/**
	 * Test method for {@link Automaton#setInitialState(Object)}.
	 * Checks that nothing wrong happens when the initial state is changed 
	 * to null via the setter
	 */
	@Test
	public void testSetInitialStateToNull() {
		elementAutomaton.setInitialState(null);
		assertNull(elementAutomaton.getInitialState());
		regexpAutomaton.setInitialState(null);
		assertNull(regexpAutomaton.getInitialState());
	}
	
	/**
	 * Test method for {@link Automaton#setInitialState(Object)}.
	 * Checks that nothing wrong happens when the initial state is changed 
	 * from null via the setter
	 */
	@Test
	public void testSetInitialStateFromNull() {
		eqClassAutomaton.setInitialState(eqClassC);
		assertEquals(eqClassC,eqClassAutomaton.getInitialState());
	}

	/**
	 * Test method for {@link Automaton#getFinalState()}.
	 * Checks that this getter returns the expected values for each automaton.
	 */
	@Test
	public void testGetFinalState() {
		assertEquals(element1,elementAutomaton.getFinalState());
		assertEquals(regexp1,regexpAutomaton.getFinalState());
		assertNull(eqClassAutomaton.getFinalState());
	}

	/**
	 * Test method for {@link Automaton#setFinalState(Object)}.
	 * Checks that nothing wrong happens when the final state is changed 
	 * via the setter
	 */
	@Test
	public void testSetFinalState() {
		elementAutomaton.setFinalState(elementB);
		assertEquals(elementB,elementAutomaton.getFinalState());
		regexpAutomaton.setFinalState(regexpB);
		assertEquals(regexpB,regexpAutomaton.getFinalState());
	}
	
	/**
	 * Test method for {@link Automaton#setFinalState(Object)}.
	 * Checks that nothing wrong happens when the final state is changed 
	 * to null via the setter
	 */
	@Test
	public void testSetFinalStateToNull() {
		elementAutomaton.setFinalState(null);
		assertNull(elementAutomaton.getFinalState());
		regexpAutomaton.setFinalState(null);
		assertNull(regexpAutomaton.getFinalState());
	}
	
	/**
	 * Test method for {@link Automaton#setFinalState(Object)}.
	 * Checks that nothing wrong happens when the final state is changed 
	 * from null via the setter
	 */
	@Test
	public void testSetFinalStateFromNull() {
		eqClassAutomaton.setFinalState(eqClassC);
		assertEquals(eqClassC,eqClassAutomaton.getFinalState());
	}
	
	/**
	 * Test method for {@link Automaton#nodeCount()}, checking that the node counts 
	 * are correct at the example automatons.
	 * Note that the situation with no nodes has already been tested at {@link AutomatonTest#testAutomaton()}
	 */
	@Test
	public void testNodeCount() {
		assertEquals(8,elementAutomaton.nodeCount());
		assertEquals(8,regexpAutomaton.nodeCount());
		assertEquals(6,eqClassAutomaton.nodeCount());
	}
	
	/**
	 * Test method for {@link Automaton#edgeCount()}, checking that the node counts 
	 * are correct at the example automatons.
	 * Note that the situation with no edges has already been tested at {@link AutomatonTest#testAutomaton()}
	 */
	@Test
	public void testEdgeCount() {
		assertEquals(9,elementAutomaton.edgeCount());
		assertEquals(9,regexpAutomaton.edgeCount());
		assertEquals(6,eqClassAutomaton.edgeCount());
	}

	/**
	 * Test method for {@link Automaton#addNode(Object)}.
	 * Adds new nodes to the automatons and checks that there are no problems.
	 */
	@Test
	public void testAddNode() {
		SchemaElement newElement = mock(SchemaElement.class);
		elementAutomaton.addNode(newElement);
		assertTrue(elementAutomaton.containsNode(newElement));
		
		RegularExpression newRegexp = mock(RegularExpression.class);
		regexpAutomaton.addNode(newRegexp);
		assertTrue(regexpAutomaton.containsNode(newRegexp));
		
		EquivalenceClass newEqClass = mock(EquivalenceClass.class);
		eqClassAutomaton.addNode(newEqClass);
		assertTrue(eqClassAutomaton.containsNode(newEqClass));
	}
	
	/**
	 * Test method for {@link Automaton#addNode(Object)}.
	 * Checks the result of adding a null node (an exception should be raised).
	 */
	@Test(expected = java.lang.NullPointerException.class)
	public void testAddNullNode(){
		regexpAutomaton.addNode(null);
	}

	/**
	 * Test method for {@link Automaton#containsNode(Object)}.
	 * It checks that all the nodes of the example automatons are contained 
	 * in the automaton, according to the method and that other nodes are not contained.
	 */
	@Test
	public void testContainsNode() {
		assertTrue(elementAutomaton.containsNode(element0));
		assertTrue(elementAutomaton.containsNode(elementA));
		assertTrue(elementAutomaton.containsNode(elementB));
		assertTrue(elementAutomaton.containsNode(elementC));
		assertTrue(elementAutomaton.containsNode(elementD));
		assertTrue(elementAutomaton.containsNode(elementE));
		assertTrue(elementAutomaton.containsNode(elementF));
		assertTrue(elementAutomaton.containsNode(element1));
		
		assertTrue(regexpAutomaton.containsNode(regexp0));
		assertTrue(regexpAutomaton.containsNode(regexpA));
		assertTrue(regexpAutomaton.containsNode(regexpB));
		assertTrue(regexpAutomaton.containsNode(regexpC));
		assertTrue(regexpAutomaton.containsNode(regexpD));
		assertTrue(regexpAutomaton.containsNode(regexpE));
		assertTrue(regexpAutomaton.containsNode(regexpF));
		assertTrue(regexpAutomaton.containsNode(regexp1));
		
		assertTrue(eqClassAutomaton.containsNode(eqClassA));
		assertTrue(eqClassAutomaton.containsNode(eqClassB));
		assertTrue(eqClassAutomaton.containsNode(eqClassC));
		assertTrue(eqClassAutomaton.containsNode(eqClassD));
		assertTrue(eqClassAutomaton.containsNode(eqClassE));
		assertTrue(eqClassAutomaton.containsNode(eqClassF));
		
		SchemaElement otherElement=mock(SchemaElement.class);
		assertFalse(elementAutomaton.containsNode(otherElement));
		
		RegularExpression otherRegexp=mock(RegularExpression.class);
		assertFalse(regexpAutomaton.containsNode(otherRegexp));
		
		EquivalenceClass otherEqClass=mock(EquivalenceClass.class);
		assertFalse(eqClassAutomaton.containsNode(otherEqClass));
	}

	/**
	 * Test method for {@link Automaton#containsNode(Object)}.
	 * It checks that a null value makes a NullPointerException to be thrown
	 */
	@Test(expected = NullPointerException.class)
	public void testContainsNodeNull() {
		elementAutomaton.containsNode(null);
	}
	
	/**
	 * Test method for {@link Automaton#addEdge(Object, Object)}.
	 * It adds a new edge between two existing nodes. Its weight should be one.
	 */
	@Test
	public void testAddEdgeEENewBetweenExisting() {
		elementAutomaton.addEdge(elementA,elementD);
		assertEquals(1,elementAutomaton.getEdgeWeight(elementA, elementD));
		
		regexpAutomaton.addEdge(regexpA,regexpD);
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpA, regexpD));
		
		eqClassAutomaton.addEdge(eqClassA,eqClassD);
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassD));
	}
	
	/**
	 * Test method for {@link Automaton#addEdge(Object, Object)}.
	 * It adds again an existing edge between two existing nodes. 
	 * So its weight should be incremented by one.
	 * This add is repeated and tested once more.
	 */
	@Test
	public void testAddEdgeEEAgainBetweenExisting() {
		
		elementAutomaton.addEdge(elementA,elementB);
		assertEquals(2,elementAutomaton.getEdgeWeight(elementA, elementB));
		
		regexpAutomaton.addEdge(regexpA,regexpB);
		assertEquals(2,regexpAutomaton.getEdgeWeight(regexpA, regexpB));
		
		eqClassAutomaton.addEdge(eqClassA,eqClassB);
		assertEquals(2,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassB));
		
		elementAutomaton.addEdge(elementA,elementB);
		assertEquals(3,elementAutomaton.getEdgeWeight(elementA, elementB));
		
		regexpAutomaton.addEdge(regexpA,regexpB);
		assertEquals(3,regexpAutomaton.getEdgeWeight(regexpA, regexpB));
		
		eqClassAutomaton.addEdge(eqClassA,eqClassB);
		assertEquals(3,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassB));
	}
	
	/**
	 * Test method for {@link Automaton#addEdge(Object, Object)}.
	 * It adds a new edge between an existing node and a new node, so it should be added. 
	 * Its weight should be one.
	 */
	@Test
	public void testAddEdgeEENewBetweenExistingSourceAndNonExistingDestination() {
		SchemaElement otherElement = mock(SchemaElement.class);
		elementAutomaton.addEdge(elementA,otherElement);
		assertTrue(elementAutomaton.containsNode(otherElement));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementA, otherElement));
		
		RegularExpression otherRegexp = mock(RegularExpression.class);
		regexpAutomaton.addEdge(regexpA,otherRegexp);
		assertTrue(regexpAutomaton.containsNode(otherRegexp));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpA, otherRegexp));
		
		EquivalenceClass otherEqClass = mock(EquivalenceClass.class);
		eqClassAutomaton.addEdge(eqClassA,otherEqClass);
		assertTrue(eqClassAutomaton.containsNode(otherEqClass));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassA, otherEqClass));
		
	}
	
	/**
	 * Test method for {@link Automaton#addEdge(Object, Object)}.
	 * It adds a new edge between a new node and an existing node, so it should be added. 
	 * Its weight should be one.
	 */
	@Test
	public void testAddEdgeEENewBetweenNonExistingSourceAndExistingDestination() {
		SchemaElement otherElement = mock(SchemaElement.class);
		elementAutomaton.addEdge(otherElement,elementA);
		assertTrue(elementAutomaton.containsNode(otherElement));
		assertEquals(1,elementAutomaton.getEdgeWeight(otherElement,elementA));
		
		RegularExpression otherRegexp = mock(RegularExpression.class);
		regexpAutomaton.addEdge(otherRegexp,regexpA);
		assertTrue(regexpAutomaton.containsNode(otherRegexp));
		assertEquals(1,regexpAutomaton.getEdgeWeight(otherRegexp,regexpA));
		
		EquivalenceClass otherEqClass = mock(EquivalenceClass.class);
		eqClassAutomaton.addEdge(otherEqClass,eqClassA);
		assertTrue(eqClassAutomaton.containsNode(otherEqClass));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(otherEqClass,eqClassA));
		
	}
	
	/**
	 * Test method for {@link Automaton#addEdge(Object, Object)}.
	 * It adds a new edge between non existing nodes, so they should be added. 
	 * Its weight should be one.
	 */
	@Test
	public void testAddEdgeEENewBetweenNonExistingNodes() {
		SchemaElement otherElement = mock(SchemaElement.class);
		SchemaElement otherElement2 = mock(SchemaElement.class);
		elementAutomaton.addEdge(otherElement,otherElement2);
		assertTrue(elementAutomaton.containsNode(otherElement));
		assertTrue(elementAutomaton.containsNode(otherElement2));
		assertEquals(1,elementAutomaton.getEdgeWeight(otherElement,otherElement2));
		
		RegularExpression otherRegexp = mock(RegularExpression.class);
		RegularExpression otherRegexp2 = mock(RegularExpression.class);
		regexpAutomaton.addEdge(otherRegexp,otherRegexp2);
		assertTrue(regexpAutomaton.containsNode(otherRegexp));
		assertTrue(regexpAutomaton.containsNode(otherRegexp2));
		assertEquals(1,regexpAutomaton.getEdgeWeight(otherRegexp,otherRegexp2));
		
		EquivalenceClass otherEqClass = mock(EquivalenceClass.class);
		EquivalenceClass otherEqClass2 = mock(EquivalenceClass.class);
		eqClassAutomaton.addEdge(otherEqClass,otherEqClass2);
		assertTrue(eqClassAutomaton.containsNode(otherEqClass));
		assertTrue(eqClassAutomaton.containsNode(otherEqClass2));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(otherEqClass,otherEqClass2));
	}
	
	/**
	 * Test method for {@link Automaton#addEdge(Object, Object, Long).
	 * It checks that everything is ok when adding a new edge with a concrete weight.
	 */
	@Test
	public void testAddEdgeEELongNew() {
		elementAutomaton.addEdge(elementA, elementD, (long)45);
		assertEquals(45,elementAutomaton.getEdgeWeight(elementA, elementD));
		
		regexpAutomaton.addEdge(regexpA, regexpD, (long)45);
		assertEquals(45,regexpAutomaton.getEdgeWeight(regexpA, regexpD));
		
		eqClassAutomaton.addEdge(eqClassA, eqClassD, (long)45);
		assertEquals(45,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassD));
	}

	/**
	 * Test method for {@link Automaton#addEdge(Object, Object, Long).
	 * It checks that everything is ok when using the method to 
	 * change the weight of an existing edge.
	 */
	@Test
	public void testAddEdgeEELongExisting() {
		elementAutomaton.addEdge(elementA, elementB, (long)45);
		assertEquals(45,elementAutomaton.getEdgeWeight(elementA, elementB));
		
		regexpAutomaton.addEdge(regexpA, regexpB, (long)45);
		assertEquals(45,regexpAutomaton.getEdgeWeight(regexpA, regexpB));
		
		eqClassAutomaton.addEdge(eqClassA, eqClassB, (long)45);
		assertEquals(45,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassB));
	}
	
	/**
	 * Test method for {@link Automaton#addEdge(Object, Object, Long).
	 * It checks that NullPointerException is thrown for null arguments
	 */
	@Test
	public void testAddEdgeEELongNull(){
		boolean error=true;
		try{
			elementAutomaton.addEdge(null,elementB,(long)33);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("null allowed on first argument");
		}
		
		error=true;
		try{
			elementAutomaton.addEdge(elementB,null,(long)33);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("null allowed on first argument");
		}
		
		error=true;
		try{
			elementAutomaton.addEdge(elementA,elementB,null);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("null allowed on first argument");
		}
		
	}
	
	/**
	 * Test method for {@link Automaton#addEdge(Object, Object, Long).
	 * It checks that IllegalArgumentException is thrown for negative weights
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddEdgeEELongIllegalWeight(){
		elementAutomaton.addEdge(elementB, elementC, (long)-5);
	}
	
	/**
	 * Test method for {@link Automaton#addEdge(Object, Object, Long).
	 * It checks that IllegalArgumentException is thrown for zero weights
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddEdgeEELongIllegalZeroWeight(){
		elementAutomaton.addEdge(elementB, elementC, (long)0);
	}


	/**
	 * Test method for {@link Automaton#getEdgeWeight(Object, Object)}.
	 * It checks whether the initial edge weights are correct.
	 * Note that other more complicated situations have been already tested on many 
	 * testAddEdge*** test methods.
	 */
	@Test
	public void testGetEdgeWeightInitial() {
		assertEquals(2,elementAutomaton.getEdgeWeight(element0, elementA));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementA, elementB));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementA, elementC));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementB, elementD));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementC, elementD));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementD, elementE));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementD, elementF));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementE, element1));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementF, element1));
		
		assertEquals(2,regexpAutomaton.getEdgeWeight(regexp0, regexpA));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpA, regexpB));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpA, regexpC));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpB, regexpD));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpC, regexpD));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpD, regexpE));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpD, regexpF));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpE, regexp1));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpF, regexp1));
		
		
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassB));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassC));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassB, eqClassD));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassC, eqClassD));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassD, eqClassE));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassD, eqClassF));
		
	}
	
	/**
	 * Test method for {@link Automaton#getEdgeWeight(Object, Object)}.
	 * It checks whether a non existing edge returns zero, as it should do.
	 */
	@Test
	public void testGetEdgeWeightNonExisting() {
		assertEquals(0, elementAutomaton.getEdgeWeight(elementA, elementD));
		
		assertEquals(0, regexpAutomaton.getEdgeWeight(regexpA, regexpD));
		
		assertEquals(0, eqClassAutomaton.getEdgeWeight(eqClassA, eqClassD));
	}
	
	/**
	 * Test method for {@link Automaton#getEdgeWeight(Object, Object)}.
	 * It checks whether an IllegalArgumentException is thrown when the source or 
	 * the destination node does not belong to the automaton.
	 */
	@Test
	public void testGetEdgeWeightNonContainedNodes() {
		SchemaElement otherElement = mock(SchemaElement.class);
		RegularExpression otherRegexp = mock(RegularExpression.class);
		EquivalenceClass otherEqClass = mock(EquivalenceClass.class);
		
		assertEquals(-1,elementAutomaton.getEdgeWeight(otherElement, elementA));
		assertEquals(-1,elementAutomaton.getEdgeWeight(elementA, otherElement));
		assertEquals(-1,elementAutomaton.getEdgeWeight(otherElement, otherElement));
		
		assertEquals(-1,regexpAutomaton.getEdgeWeight(otherRegexp, regexpA));
		assertEquals(-1,regexpAutomaton.getEdgeWeight(regexpA, otherRegexp));
		assertEquals(-1,regexpAutomaton.getEdgeWeight(otherRegexp, otherRegexp));
		
		assertEquals(-1,eqClassAutomaton.getEdgeWeight(otherEqClass, eqClassA));
		assertEquals(-1,eqClassAutomaton.getEdgeWeight(eqClassA, otherEqClass));
		assertEquals(-1,eqClassAutomaton.getEdgeWeight(otherEqClass, otherEqClass));
		
//		boolean error=true;		
//		try{
//			elementAutomaton.getEdgeWeight(otherElement,elementA);
//		} catch(IllegalArgumentException e){
//			error=false;
//		}
//		if(error){
//			fail("exception not thrown when the source does not belong to the automaton");
//		}
//		
//		error=true;
//		try{
//			regexpAutomaton.getEdgeWeight(otherRegexp,regexpA);
//		} catch(IllegalArgumentException e){
//			error=false;
//		}
//		if(error){
//			fail("exception not thrown when the source does not belong to the automaton");
//		}
//		
//		error=true;
//		try{
//			eqClassAutomaton.getEdgeWeight(otherEqClass,eqClassA);
//		} catch(IllegalArgumentException e){
//			error=false;
//		}
//		if(error){
//			fail("exception not thrown when the source does not belong to the automaton");
//		}
//		
//		error=true;
//		try{
//			elementAutomaton.getEdgeWeight(elementA,otherElement);
//		} catch(IllegalArgumentException e){
//			error=false;
//		}
//		if(error){
//			fail("exception not thrown when the destination does not belong to the automaton");
//		}
//		
//		error=true;
//		try{
//			regexpAutomaton.getEdgeWeight(regexpA,otherRegexp);
//		} catch(IllegalArgumentException e){
//			error=false;
//		}
//		if(error){
//			fail("exception not thrown when the destination does not belong to the automaton");
//		}
//		
//		error=true;
//		try{
//			eqClassAutomaton.getEdgeWeight(eqClassA,otherEqClass);
//		} catch(IllegalArgumentException e){
//			error=false;
//		}
//		if(error){
//			fail("exception not thrown when the destination does not belong to the automaton");
//		}
//		
//		error=true;
//		try{
//			elementAutomaton.getEdgeWeight(otherElement,otherElement);
//		} catch(IllegalArgumentException e){
//			error=false;
//		}
//		if(error){
//			fail("exception not thrown when both the source and the destination do not belong to the automaton");
//		}
//		
//		error=true;
//		try{
//			regexpAutomaton.getEdgeWeight(otherRegexp,otherRegexp);
//		} catch(IllegalArgumentException e){
//			error=false;
//		}
//		if(error){
//			fail("exception not thrown when both the source and the destination do not belong to the automaton");
//		}
//		
//		error=true;
//		try{
//			eqClassAutomaton.getEdgeWeight(otherEqClass,otherEqClass);
//		} catch(IllegalArgumentException e){
//			error=false;
//		}
//		if(error){
//			fail("exception not thrown when both the source and the destination do not belong to the automaton");
//		}
	}

	/**
	 * Test method for {@link Automaton#getEdgeWeight(Object, Object)}.
	 * It checks whether an NullPointerException is thrown when the source or 
	 * the destination node are null.
	 */
	@Test
	public void testGetEdgeWeightNull() {
		
		boolean error=true;
		try{
			elementAutomaton.getEdgeWeight(null,elementA);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the source is null");
		}
		
		error=true;
		try{
			regexpAutomaton.getEdgeWeight(null,regexpA);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the source is null");
		}
		
		error=true;
		try{
			eqClassAutomaton.getEdgeWeight(null,eqClassA);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the source is null");
		}
		
		error=true;
		try{
			elementAutomaton.getEdgeWeight(elementA,null);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the destination is null");
		}
		
		error=true;
		try{
			regexpAutomaton.getEdgeWeight(regexpA,null);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the destination is null");
		}
		
		error=true;
		try{
			eqClassAutomaton.getEdgeWeight(eqClassA,null);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the destination is null");
		}
		
		error=true;
		try{
			elementAutomaton.getEdgeWeight(null,null);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when both the source and the destination are null");
		}
		
		error=true;
		try{
			regexpAutomaton.getEdgeWeight(null,null);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when both the source and the destination are null");
		}
		
		error=true;
		try{
			eqClassAutomaton.getEdgeWeight(null,null);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when both the source and the destination are null");
		}
	}	
	
	/**
	 * Test method for {@link Automaton#removeEdge(Object, Object)}.
	 * It checks that the edge is actually removed but nodes remain present in the automaton.
	 * It also checks that an edge whose weight is more than one is also removed succesfully in 
	 * the same way.
	 */
	@Test
	public void testRemoveEdge() {
		elementAutomaton.removeEdge(element0, elementA);
		assertEquals(0, elementAutomaton.getEdgeWeight(element0, elementA));
		assertTrue(elementAutomaton.containsNode(element0));
		assertTrue(elementAutomaton.containsNode(elementA));
		//We add an edge with weight more than one and repeat the test
		elementAutomaton.addEdge(element0, elementC, (long) 10);
		elementAutomaton.removeEdge(element0, elementC);
		assertEquals(0, elementAutomaton.getEdgeWeight(element0, elementC));
		assertTrue(elementAutomaton.containsNode(element0));
		assertTrue(elementAutomaton.containsNode(elementC));
		
		regexpAutomaton.removeEdge(regexp0, regexpA);
		assertEquals(0, regexpAutomaton.getEdgeWeight(regexp0, regexpA));
		assertTrue(regexpAutomaton.containsNode(regexp0));
		assertTrue(regexpAutomaton.containsNode(regexpA));
		//We add an edge with weight more than one and repeat the test
		regexpAutomaton.addEdge(regexp0, regexpC, (long) 10);
		regexpAutomaton.removeEdge(regexp0, regexpC);
		assertEquals(0, regexpAutomaton.getEdgeWeight(regexp0, regexpC));
		assertTrue(regexpAutomaton.containsNode(regexp0));
		assertTrue(regexpAutomaton.containsNode(regexpC));
		
		eqClassAutomaton.removeEdge(eqClassB, eqClassD);
		assertEquals(0, eqClassAutomaton.getEdgeWeight(eqClassB, eqClassD));
		assertTrue(eqClassAutomaton.containsNode(eqClassB));
		assertTrue(eqClassAutomaton.containsNode(eqClassD));
		//We add an edge with weight more than one and repeat the test
		eqClassAutomaton.addEdge(eqClassB, eqClassC, (long) 10);
		eqClassAutomaton.removeEdge(eqClassB, eqClassC);
		assertEquals(0, eqClassAutomaton.getEdgeWeight(eqClassB, eqClassC));
		assertTrue(eqClassAutomaton.containsNode(eqClassB));
		assertTrue(eqClassAutomaton.containsNode(eqClassC));
	}
	
	/**
	 * Test method for {@link Automaton#removeEdge(Object, Object)}.
	 * It checks whether an IllegalArgumentException is thrown when the source or 
	 * the destination node does not belong to the automaton.
	 */
	@Test
	public void testRemoveEdgeIllegal() {
		SchemaElement otherElement = mock(SchemaElement.class);
		RegularExpression otherRegexp = mock(RegularExpression.class);
		EquivalenceClass otherEqClass = mock(EquivalenceClass.class);
		
		boolean error=true;
		try{
			elementAutomaton.removeEdge(otherElement,elementA);
		} catch(IllegalArgumentException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the source does not belong to the automaton");
		}
		
		error=true;
		try{
			regexpAutomaton.removeEdge(otherRegexp,regexpA);
		} catch(IllegalArgumentException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the source does not belong to the automaton");
		}
		
		error=true;
		try{
			eqClassAutomaton.removeEdge(otherEqClass,eqClassA);
		} catch(IllegalArgumentException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the source does not belong to the automaton");
		}
		
		error=true;
		try{
			elementAutomaton.removeEdge(elementA,otherElement);
		} catch(IllegalArgumentException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the destination does not belong to the automaton");
		}
		
		error=true;
		try{
			regexpAutomaton.removeEdge(regexpA,otherRegexp);
		} catch(IllegalArgumentException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the destination does not belong to the automaton");
		}
		
		error=true;
		try{
			eqClassAutomaton.removeEdge(eqClassA,otherEqClass);
		} catch(IllegalArgumentException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the destination does not belong to the automaton");
		}
		
		error=true;
		try{
			elementAutomaton.removeEdge(otherElement,otherElement);
		} catch(IllegalArgumentException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when both the source and the destination do not belong to the automaton");
		}
		
		error=true;
		try{
			regexpAutomaton.removeEdge(otherRegexp,otherRegexp);
		} catch(IllegalArgumentException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when both the source and the destination do not belong to the automaton");
		}
		
		error=true;
		try{
			eqClassAutomaton.removeEdge(otherEqClass,otherEqClass);
		} catch(IllegalArgumentException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when both the source and the destination do not belong to the automaton");
		}
	}

	/**
	 * Test method for {@link Automaton#removeEdge(Object, Object)}.
	 * It checks whether an NullPointerException is thrown when the source or 
	 * the destination node are null.
	 */
	@Test
	public void testRemoveEdgeNull() {
		
		boolean error=true;
		try{
			elementAutomaton.removeEdge(null,elementA);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the source is null");
		}
		
		error=true;
		try{
			regexpAutomaton.removeEdge(null,regexpA);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the source is null");
		}
		
		error=true;
		try{
			eqClassAutomaton.removeEdge(null,eqClassA);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the source is null");
		}
		
		error=true;
		try{
			elementAutomaton.removeEdge(elementA,null);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the destination is null");
		}
		
		error=true;
		try{
			regexpAutomaton.removeEdge(regexpA,null);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the destination is null");
		}
		
		error=true;
		try{
			eqClassAutomaton.removeEdge(eqClassA,null);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when the destination is null");
		}
		
		error=true;
		try{
			elementAutomaton.removeEdge(null,null);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when both the source and the destination are null");
		}
		
		error=true;
		try{
			regexpAutomaton.removeEdge(null,null);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when both the source and the destination are null");
		}
		
		error=true;
		try{
			eqClassAutomaton.removeEdge(null,null);
		} catch(NullPointerException e){
			error=false;
		}
		if(error){
			fail("exception not thrown when both the source and the destination are null");
		}
	}	

	/**
	 * Test method for {@link Automaton#removeNode(Object)}.
	 * It checks that a node may be deleted successfully and the automaton does not contain it 
	 * after that.
	 */
	@Test
	public void testRemoveNode() {
		elementAutomaton.removeNode(elementB);
		assertFalse(elementAutomaton.containsNode(elementB));
		
		regexpAutomaton.removeNode(regexpB);
		assertFalse(regexpAutomaton.containsNode(regexpB));
		
		eqClassAutomaton.removeNode(eqClassB);
		assertFalse(eqClassAutomaton.containsNode(eqClassB));
	}
	
	/**
	 * Test method for {@link Automaton#removeNode(Object)}.
	 * It checks that, when a node is removed, its edges are also successfully removed.
	 */
	@Test
	public void testRemoveNodeAndItsEdges() {
		elementAutomaton.removeNode(elementB);
		Map<SchemaElement,Long> elementAOutgoing=elementAutomaton.getOutgoingEdges(elementA);
		assertFalse(elementAOutgoing.containsKey(elementB));
		Map<SchemaElement,Long> elementDIncoming=elementAutomaton.getIncomingEdges(elementD);
		assertFalse(elementDIncoming.containsKey(elementD));
		
		regexpAutomaton.removeNode(regexpB);
		Map<RegularExpression,Long> regexpAOutgoing=regexpAutomaton.getOutgoingEdges(regexpA);
		assertFalse(regexpAOutgoing.containsKey(regexpB));
		Map<RegularExpression,Long> regexpDIncoming=regexpAutomaton.getIncomingEdges(regexpD);
		assertFalse(regexpDIncoming.containsKey(regexpD));
		
		eqClassAutomaton.removeNode(eqClassB);
		Map<EquivalenceClass,Long> eqClassAOutgoing=eqClassAutomaton.getOutgoingEdges(eqClassA);
		assertFalse(eqClassAOutgoing.containsKey(eqClassB));
		Map<EquivalenceClass,Long> eqClassDIncoming=eqClassAutomaton.getIncomingEdges(eqClassD);
		assertFalse(eqClassDIncoming.containsKey(eqClassD));
	}
	
	/**
	 * Test method for {@link Automaton#removeNode(Object)}.
	 * It checks that a null parameter causes a NullPointerException
	 */
	@Test(expected = NullPointerException.class)
	public void testRemoveNodeNull() {
		elementAutomaton.removeNode(null);
	}
	
	/**
	 * Test method for {@link Automaton#removeNode(Object)}.
	 * It checks that an IllegalArgumentException is thrown when attempting to 
	 * remove a node which does not belong to the automaton.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testRemoveNodeIllegal() {
		SchemaElement otherElement = mock(SchemaElement.class);
		elementAutomaton.removeNode(otherElement);
	}
	

	/**
	 * Test method for {@link Automaton#getIncomingEdges(Object)}.
	 * It checks that the incoming edges of a normal node are well 
	 * returned.
	 */
	@Test
	public void testGetIncomingEdges() {
		Map<SchemaElement,Long> elementExpectedEdges = new HashMap<SchemaElement,Long>();
		elementExpectedEdges.put(elementB, (long) 1);
		elementExpectedEdges.put(elementC, (long)1);
		Map<SchemaElement,Long> elementActualEdges = elementAutomaton.getIncomingEdges(elementD);
		assertEquals(elementExpectedEdges,elementActualEdges);
		
		Map<RegularExpression,Long> regexpExpectedEdges = new HashMap<RegularExpression,Long>();
		regexpExpectedEdges.put(regexpB, (long) 1);
		regexpExpectedEdges.put(regexpC, (long) 1);
		Map<RegularExpression,Long> regexpActualEdges = regexpAutomaton.getIncomingEdges(regexpD);
		assertEquals(regexpExpectedEdges,regexpActualEdges);
		
		Map<EquivalenceClass,Long> eqClassExpectedEdges = new HashMap<EquivalenceClass,Long>();
		eqClassExpectedEdges.put(eqClassB, (long) 1);
		eqClassExpectedEdges.put(eqClassC, (long) 1);
		Map<EquivalenceClass,Long> eqClassActualEdges = eqClassAutomaton.getIncomingEdges(eqClassD);
		assertEquals(eqClassExpectedEdges,eqClassActualEdges);
	}

	/**
	 * Test method for {@link Automaton#getIncomingEdges(Object)}.
	 * It checks that the incoming edges of a node with no incoming 
	 * edges are well returned.
	 */
	@Test
	public void testGetIncomingEdgesEmpty() {
		Map<SchemaElement,Long> elementExpectedEdges = new HashMap<SchemaElement,Long>();
		Map<SchemaElement,Long> elementActualEdges = elementAutomaton.getIncomingEdges(element0);
		assertEquals(elementExpectedEdges,elementActualEdges);
		
		Map<RegularExpression,Long> regexpExpectedEdges = new HashMap<RegularExpression,Long>();
		Map<RegularExpression,Long> regexpActualEdges = regexpAutomaton.getIncomingEdges(regexp0);
		assertEquals(regexpExpectedEdges,regexpActualEdges);
		
		Map<EquivalenceClass,Long> eqClassExpectedEdges = new HashMap<EquivalenceClass,Long>();
		Map<EquivalenceClass,Long> eqClassActualEdges = eqClassAutomaton.getIncomingEdges(eqClassA);
		assertEquals(eqClassExpectedEdges,eqClassActualEdges);
	}
	
	/**
	 * Test method for {@link Automaton#getIncomingEdges(Object)}.
	 * It checks that a NullPointerException is thrown when a null parameter is passed.
	 */
	@Test(expected = NullPointerException.class)
	public void testGetIncomingEdgesNull() {
		elementAutomaton.getIncomingEdges(null);
	}
	
	/**
	 * Test method for {@link Automaton#getIncomingEdges(Object)}.
	 * It checks that an IllegalArgumentException is thrown when attempting to 
	 * get the incoming edges of a node which does not belong to the automaton.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetIncomingEdgesIllegal() {
		SchemaElement otherElement = mock(SchemaElement.class);
		elementAutomaton.getIncomingEdges(otherElement);
	}
	
	/**
	 * Test method for {@link Automaton#getOutgoingEdges(Object)}.
	 * It checks that the outgoing edges of a normal node are well 
	 * returned.
	 */
	@Test
	public void testGetOutgoingEdges() {
		Map<SchemaElement,Long> elementExpectedEdges = new HashMap<SchemaElement,Long>();
		elementExpectedEdges.put(elementE, (long) 1);
		elementExpectedEdges.put(elementF, (long) 1);
		Map<SchemaElement,Long> elementActualEdges = elementAutomaton.getOutgoingEdges(elementD);
		assertEquals(elementExpectedEdges,elementActualEdges);
		
		Map<RegularExpression,Long> regexpExpectedEdges = new HashMap<RegularExpression,Long>();
		regexpExpectedEdges.put(regexpE, (long) 1);
		regexpExpectedEdges.put(regexpF, (long) 1);
		Map<RegularExpression,Long> regexpActualEdges = regexpAutomaton.getOutgoingEdges(regexpD);
		assertEquals(regexpExpectedEdges,regexpActualEdges);
		
		Map<EquivalenceClass,Long> eqClassExpectedEdges = new HashMap<EquivalenceClass,Long>();
		eqClassExpectedEdges.put(eqClassE, (long) 1);
		eqClassExpectedEdges.put(eqClassF, (long) 1);
		Map<EquivalenceClass,Long> eqClassActualEdges = eqClassAutomaton.getOutgoingEdges(eqClassD);
		assertEquals(eqClassExpectedEdges,eqClassActualEdges);
	}
	
	/**
	 * Test method for {@link Automaton#getOutgoingEdges(Object)}.
	 * It checks that the outgoing edges of a node with no outgoing 
	 * edges are well returned.
	 */
	@Test
	public void testGetOutgoingEdgesEmpty() {
		Map<SchemaElement,Long> elementExpectedEdges = new HashMap<SchemaElement,Long>();
		Map<SchemaElement,Long> elementActualEdges = elementAutomaton.getOutgoingEdges(element1);
		assertEquals(elementExpectedEdges,elementActualEdges);
		
		Map<RegularExpression,Long> regexpExpectedEdges = new HashMap<RegularExpression,Long>();
		Map<RegularExpression,Long> regexpActualEdges = regexpAutomaton.getOutgoingEdges(regexp1);
		assertEquals(regexpExpectedEdges,regexpActualEdges);
		
		Map<EquivalenceClass,Long> eqClassExpectedEdges = new HashMap<EquivalenceClass,Long>();
		Map<EquivalenceClass,Long> eqClassActualEdges = eqClassAutomaton.getOutgoingEdges(eqClassE);
		assertEquals(eqClassExpectedEdges,eqClassActualEdges);
	}
	
	/**
	 * Test method for {@link Automaton#getOutgoingEdges(Object)}.
	 * It checks that a NullPointerException is thrown when a null parameter is passed.
	 */
	@Test(expected = NullPointerException.class)
	public void testGetOutgoingEdgesNull() {
		elementAutomaton.getOutgoingEdges(null);
	}
	
	/**
	 * Test method for {@link Automaton#getOutgoingEdges(Object)}.
	 * It checks that an IllegalArgumentException is thrown when attempting to 
	 * get the outgoing edges of a node which does not belong to the automaton.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetOutgoingEdgesIllegal() {
		SchemaElement otherElement = mock(SchemaElement.class);
		elementAutomaton.getOutgoingEdges(otherElement);
	}

	/**
	 * Test method for {@link Automaton#substituteNodes(Object, Object)}.
	 * It checks that the node is correctly substituted and that all the edges are 
	 * remapped correctly.
	 */
	@Test
	public void testSubstituteNodesSingle() {
		SchemaElement otherElement = mock(SchemaElement.class);
		elementAutomaton.substituteNodes(elementD, otherElement);
		assertTrue(elementAutomaton.containsNode(otherElement));
		assertFalse(elementAutomaton.containsNode(elementD));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementB, otherElement));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementC, otherElement));
		assertEquals(1,elementAutomaton.getEdgeWeight(otherElement, elementE));
		assertEquals(1,elementAutomaton.getEdgeWeight(otherElement, elementF));
		
		RegularExpression otherRegexp = mock(RegularExpression.class);
		regexpAutomaton.substituteNodes(regexpD, otherRegexp);
		assertTrue(regexpAutomaton.containsNode(otherRegexp));
		assertFalse(regexpAutomaton.containsNode(regexpD));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpB, otherRegexp));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpC, otherRegexp));
		assertEquals(1,regexpAutomaton.getEdgeWeight(otherRegexp, regexpE));
		assertEquals(1,regexpAutomaton.getEdgeWeight(otherRegexp, regexpF));
		
		EquivalenceClass otherEqClass = mock(EquivalenceClass.class);
		eqClassAutomaton.substituteNodes(eqClassD, otherEqClass);
		assertTrue(eqClassAutomaton.containsNode(otherEqClass));
		assertFalse(eqClassAutomaton.containsNode(eqClassD));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassB, otherEqClass));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassC, otherEqClass));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(otherEqClass, eqClassE));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(otherEqClass, eqClassF));
	}
	
	/**
	 * Test method for {@link Automaton#substituteNodes(Object, Object)}.
	 * It checks that the nodes are correctly substituted and that all the edges are 
	 * remapped correctly.
	 */
	@Test
	public void testSubstituteNodes() {
		SchemaElement otherElement = mock(SchemaElement.class);
		Set<SchemaElement> oldNodesElements = new HashSet<SchemaElement>();
		oldNodesElements.add(elementB);
		oldNodesElements.add(elementC);
		elementAutomaton.substituteNodes(oldNodesElements, otherElement);
		assertTrue(elementAutomaton.containsNode(otherElement));
		assertFalse(elementAutomaton.containsNode(elementB));
		assertFalse(elementAutomaton.containsNode(elementC));
		assertEquals(2,elementAutomaton.getEdgeWeight(elementA, otherElement));
		assertEquals(2,elementAutomaton.getEdgeWeight(otherElement, elementD));
		
		RegularExpression otherRegexp = mock(RegularExpression.class);
		Set<RegularExpression> oldNodesRegexp = new HashSet<RegularExpression>();
		oldNodesRegexp.add(regexpB);
		oldNodesRegexp.add(regexpC);
		regexpAutomaton.substituteNodes(oldNodesRegexp, otherRegexp);
		assertTrue(regexpAutomaton.containsNode(otherRegexp));
		assertFalse(regexpAutomaton.containsNode(regexpB));
		assertFalse(regexpAutomaton.containsNode(regexpC));
		assertEquals(2,regexpAutomaton.getEdgeWeight(regexpA, otherRegexp));
		assertEquals(2,regexpAutomaton.getEdgeWeight(otherRegexp, regexpD));
		
		EquivalenceClass otherEqClass = mock(EquivalenceClass.class);
		Set<EquivalenceClass> oldNodesEqClass = new HashSet<EquivalenceClass>();
		oldNodesEqClass.add(eqClassB);
		oldNodesEqClass.add(eqClassC);
		eqClassAutomaton.substituteNodes(oldNodesEqClass, otherEqClass);
		assertTrue(eqClassAutomaton.containsNode(otherEqClass));
		assertFalse(eqClassAutomaton.containsNode(eqClassB));
		assertFalse(eqClassAutomaton.containsNode(eqClassC));
		assertEquals(2,eqClassAutomaton.getEdgeWeight(eqClassA, otherEqClass));
		assertEquals(2,eqClassAutomaton.getEdgeWeight(otherEqClass, eqClassD));
	}

	/**
	 * Test method for {@link Automaton#substituteNodes(Object, Object)}.
	 * It checks that a node without edges is substituted correctly.
	 */
	@Test
	public void testSubstituteNodesSingleWithoutEdges() {
		SchemaElement otherElement = mock(SchemaElement.class);
		SchemaElement otherElement2 = mock(SchemaElement.class);
		elementAutomaton.addNode(otherElement);
		elementAutomaton.substituteNodes(otherElement, otherElement2);
		assertTrue(elementAutomaton.containsNode(otherElement2));
		assertFalse(elementAutomaton.containsNode(otherElement));
			
		RegularExpression otherRegexp = mock(RegularExpression.class);
		RegularExpression otherRegexp2 = mock(RegularExpression.class);
		regexpAutomaton.addNode(otherRegexp);
		regexpAutomaton.substituteNodes(otherRegexp, otherRegexp2);
		assertTrue(regexpAutomaton.containsNode(otherRegexp2));
		assertFalse(regexpAutomaton.containsNode(otherRegexp));
		
		EquivalenceClass otherEqClass = mock(EquivalenceClass.class);
		EquivalenceClass otherEqClass2 = mock(EquivalenceClass.class);
		eqClassAutomaton.addNode(otherEqClass);
		eqClassAutomaton.substituteNodes(otherEqClass, otherEqClass2);
		assertTrue(eqClassAutomaton.containsNode(otherEqClass2));
		assertFalse(eqClassAutomaton.containsNode(otherEqClass));
	}

	/**
	 * Test method for {@link Automaton#getReachableNodes(Object)}.
	 * It checks that all the reachable nodes from one node are present on the 
	 * returned set and the other ones are not.
	 */
	@Test
	public void testGetReachableNodes() {
		Set<SchemaElement> elementReachableNodesD = elementAutomaton.getReachableNodes(elementD);
		assertTrue(elementReachableNodesD.contains(elementE));
		assertTrue(elementReachableNodesD.contains(elementF));
		assertTrue(elementReachableNodesD.contains(element1));
		assertFalse(elementReachableNodesD.contains(element0));
		assertFalse(elementReachableNodesD.contains(elementA));
		assertFalse(elementReachableNodesD.contains(elementB));
		assertFalse(elementReachableNodesD.contains(elementC));
		assertFalse(elementReachableNodesD.contains(elementD));
		
		Set<RegularExpression> regexpReachableNodesD = regexpAutomaton.getReachableNodes(regexpD);
		assertTrue(regexpReachableNodesD.contains(regexpE));
		assertTrue(regexpReachableNodesD.contains(regexpF));
		assertTrue(regexpReachableNodesD.contains(regexp1));
		assertFalse(regexpReachableNodesD.contains(regexp0));
		assertFalse(regexpReachableNodesD.contains(regexpA));
		assertFalse(regexpReachableNodesD.contains(regexpB));
		assertFalse(regexpReachableNodesD.contains(regexpC));
		assertFalse(regexpReachableNodesD.contains(regexpD));
		
		Set<EquivalenceClass> eqClassReachableNodesD = eqClassAutomaton.getReachableNodes(eqClassD);
		assertTrue(eqClassReachableNodesD.contains(eqClassE));
		assertTrue(eqClassReachableNodesD.contains(eqClassF));
		assertFalse(eqClassReachableNodesD.contains(eqClassA));
		assertFalse(eqClassReachableNodesD.contains(eqClassB));
		assertFalse(eqClassReachableNodesD.contains(eqClassC));
		assertFalse(eqClassReachableNodesD.contains(eqClassD));
		
		
	}
	
	/**
	 * Test method for {@link Automaton#getReachableNodes(Object)}.
	 * It checks that all the reachable nodes from one node are present on the 
	 * returned set and the other ones are not in a graph with cycles.
	 */
	@Test
	public void testGetReachableNodesCyclic() {
		elementAutomaton.addEdge(elementE,elementD);
		Set<SchemaElement> elementReachableNodesD = elementAutomaton.getReachableNodes(elementD);
		assertTrue(elementReachableNodesD.contains(elementE));
		assertTrue(elementReachableNodesD.contains(elementF));
		assertTrue(elementReachableNodesD.contains(element1));
		assertFalse(elementReachableNodesD.contains(element0));
		assertFalse(elementReachableNodesD.contains(elementA));
		assertFalse(elementReachableNodesD.contains(elementB));
		assertFalse(elementReachableNodesD.contains(elementC));
		assertTrue(elementReachableNodesD.contains(elementD));
		
		regexpAutomaton.addEdge(regexpE,regexpD);
		Set<RegularExpression> regexpReachableNodesD = regexpAutomaton.getReachableNodes(regexpD);
		assertTrue(regexpReachableNodesD.contains(regexpE));
		assertTrue(regexpReachableNodesD.contains(regexpF));
		assertTrue(regexpReachableNodesD.contains(regexp1));
		assertFalse(regexpReachableNodesD.contains(regexp0));
		assertFalse(regexpReachableNodesD.contains(regexpA));
		assertFalse(regexpReachableNodesD.contains(regexpB));
		assertFalse(regexpReachableNodesD.contains(regexpC));
		assertTrue(regexpReachableNodesD.contains(regexpD));
		
		eqClassAutomaton.addEdge(eqClassE,eqClassD);
		Set<EquivalenceClass> eqClassReachableNodesD = eqClassAutomaton.getReachableNodes(eqClassD);
		assertTrue(eqClassReachableNodesD.contains(eqClassE));
		assertTrue(eqClassReachableNodesD.contains(eqClassF));
		assertFalse(eqClassReachableNodesD.contains(eqClassA));
		assertFalse(eqClassReachableNodesD.contains(eqClassB));
		assertFalse(eqClassReachableNodesD.contains(eqClassC));
		assertTrue(eqClassReachableNodesD.contains(eqClassD));
		
		
	}

	/**
	 * Test method for {@link Automaton#getReachableNodes(Object)}.
	 * It checks that an empty set is returned when there are no reachable nodes.
	 */
	@Test
	public void testGetReachableNodesEmpty() {
		Set<SchemaElement> elementReachableNodes1 = elementAutomaton.getReachableNodes(element1);
		assertTrue(elementReachableNodes1.isEmpty());	
		Set<RegularExpression> regexpReachableNodes1 = regexpAutomaton.getReachableNodes(regexp1);
		assertTrue(regexpReachableNodes1.isEmpty());		
		Set<EquivalenceClass> eqClassReachableNodesE = eqClassAutomaton.getReachableNodes(eqClassE);
		assertTrue(eqClassReachableNodesE.isEmpty());
	}
	
	/**
	 * Test method for {@link Automaton#getReachableNodes(Object)}.
	 * It checks that an IllegalArgumentException is thrown if the node does not belong to the automaton.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testGetReachableNodesIllegal() {
		SchemaElement otherElement = mock(SchemaElement.class);
		elementAutomaton.getReachableNodes(otherElement);
	}
	
	/**
	 * Test method for {@link Automaton#getReachableNodes(Object)}.
	 * It checks that a NullPointerException is thrown when a null value is passed.
	 */
	@Test(expected = NullPointerException.class)
	public void testGetReachableNodesNull() {
		elementAutomaton.getReachableNodes(null);
	}
	
	/**
	 * Test method for {@link Automaton#getLeaves()}.
	 * It checks that the leaves are correct. 
	 */
	@Test
	public void testGetLeaves() {
		List<SchemaElement> elementLeaves = elementAutomaton.getLeaves();
		assertTrue(elementLeaves.contains(element1));
		assertEquals(1,elementLeaves.size());
		
		List<RegularExpression> regexpLeaves = regexpAutomaton.getLeaves();
		assertTrue(regexpLeaves.contains(regexp1));
		assertEquals(1,regexpLeaves.size());
		
		List<EquivalenceClass> eqClassLeaves = eqClassAutomaton.getLeaves();
		assertTrue(eqClassLeaves.contains(eqClassE));
		assertTrue(eqClassLeaves.contains(eqClassF));
		assertEquals(2,eqClassLeaves.size());
	}
	
	/**
	 * Test method for {@link Automaton#getLeaves()}.
	 * It checks that it behaves well with an empty automaton.
	 */
	@Test
	public void testGetLeavesEmptyAutomaton() {
		List<SchemaElement> elementLeaves = new ExtendedAutomaton().getLeaves();
		assertEquals(0,elementLeaves.size());
		
		List<RegularExpression> regexpLeaves = new Automaton<RegularExpression>().getLeaves();
		assertEquals(0,regexpLeaves.size());
		
		List<EquivalenceClass> eqClassLeaves = new Automaton<EquivalenceClass>().getLeaves();
		assertEquals(0,eqClassLeaves.size());
	}
	
	/**
	 * Test method for {@link Automaton#getTopologicallySortedNodeList()}.
	 * It checks that it behaves well with an empty automaton.
	 */
	@Test
	public void testGetTopologicallySortedNodeListEmptyAutomaton() {
		List<SchemaElement> elementLeaves = new ExtendedAutomaton().getTopologicallySortedNodeList();
		assertEquals(0,elementLeaves.size());
		
		List<RegularExpression> regexpLeaves = new Automaton<RegularExpression>().getTopologicallySortedNodeList();
		assertEquals(0,regexpLeaves.size());
		
		List<EquivalenceClass> eqClassLeaves = new Automaton<EquivalenceClass>().getTopologicallySortedNodeList();
		assertEquals(0,eqClassLeaves.size());
	}

	/**
	 * Test method for {@link Automaton#getTopologicallySortedNodeList()}.
	 * It checks that the returned list is equal to one of the possible 
	 * ones (there is <b>more than one</b> possible result).
	 */
	@Test
	public void testGetTopologicallySortedNodeList() {
		List<SchemaElement> elementPossibleList1=new ArrayList<SchemaElement>(elementAutomaton.nodeCount());
		elementPossibleList1.add(element0);
		elementPossibleList1.add(elementA);
		elementPossibleList1.add(elementB);
		elementPossibleList1.add(elementC);
		elementPossibleList1.add(elementD);
		elementPossibleList1.add(elementE);
		elementPossibleList1.add(elementF);
		elementPossibleList1.add(element1);
		List<SchemaElement> elementPossibleList2=new ArrayList<SchemaElement>(elementPossibleList1);
		Collections.swap(elementPossibleList2, 2, 3);
		List<SchemaElement> elementPossibleList3=new ArrayList<SchemaElement>(elementPossibleList1);
		Collections.swap(elementPossibleList3, 5, 6);
		List<SchemaElement> elementPossibleList4=new ArrayList<SchemaElement>(elementPossibleList1);
		Collections.swap(elementPossibleList4, 2, 3);
		Collections.swap(elementPossibleList4, 5, 6);
		List<SchemaElement> elementActualList = elementAutomaton.getTopologicallySortedNodeList();
		boolean condicionElement = false;
		condicionElement |= elementActualList.equals(elementPossibleList1);
		condicionElement |= elementActualList.equals(elementPossibleList2);
		condicionElement |= elementActualList.equals(elementPossibleList3);
		condicionElement |= elementActualList.equals(elementPossibleList4);
		assertTrue(condicionElement);
		
		List<RegularExpression> regexpPossibleList1=new ArrayList<RegularExpression>(regexpAutomaton.nodeCount());
		regexpPossibleList1.add(regexp0);
		regexpPossibleList1.add(regexpA);
		regexpPossibleList1.add(regexpB);
		regexpPossibleList1.add(regexpC);
		regexpPossibleList1.add(regexpD);
		regexpPossibleList1.add(regexpE);
		regexpPossibleList1.add(regexpF);
		regexpPossibleList1.add(regexp1);
		List<RegularExpression> regexpPossibleList2=new ArrayList<RegularExpression>(regexpPossibleList1);
		Collections.swap(regexpPossibleList2, 2, 3);
		List<RegularExpression> regexpPossibleList3=new ArrayList<RegularExpression>(regexpPossibleList1);
		Collections.swap(regexpPossibleList3, 5, 6);
		List<RegularExpression> regexpPossibleList4=new ArrayList<RegularExpression>(regexpPossibleList1);
		Collections.swap(regexpPossibleList4, 2, 3);
		Collections.swap(regexpPossibleList4, 5, 6);
		List<RegularExpression> regexpActualList = regexpAutomaton.getTopologicallySortedNodeList();
		boolean condicionRegexp = false;
		condicionRegexp |= regexpActualList.equals(regexpPossibleList1);
		condicionRegexp |= regexpActualList.equals(regexpPossibleList2);
		condicionRegexp |= regexpActualList.equals(regexpPossibleList3);
		condicionRegexp |= regexpActualList.equals(regexpPossibleList4);
		assertTrue(condicionRegexp);
		
		List<EquivalenceClass> eqClassPossibleList1=new ArrayList<EquivalenceClass>(eqClassAutomaton.nodeCount());
		eqClassPossibleList1.add(eqClassA);
		eqClassPossibleList1.add(eqClassB);
		eqClassPossibleList1.add(eqClassC);
		eqClassPossibleList1.add(eqClassD);
		eqClassPossibleList1.add(eqClassE);
		eqClassPossibleList1.add(eqClassF);
		List<EquivalenceClass> eqClassPossibleList2=new ArrayList<EquivalenceClass>(eqClassPossibleList1);
		Collections.swap(eqClassPossibleList2, 1, 2);
		List<EquivalenceClass> eqClassPossibleList3=new ArrayList<EquivalenceClass>(eqClassPossibleList1);
		Collections.swap(eqClassPossibleList3, 4, 5);
		List<EquivalenceClass> eqClassPossibleList4=new ArrayList<EquivalenceClass>(eqClassPossibleList1);
		Collections.swap(eqClassPossibleList4, 1, 2);
		Collections.swap(eqClassPossibleList4, 4, 5);
		List<EquivalenceClass> eqClassActualList = eqClassAutomaton.getTopologicallySortedNodeList();
		boolean condicionEqClass = false;
		condicionEqClass |= eqClassActualList.equals(eqClassPossibleList1);
		condicionEqClass |= eqClassActualList.equals(eqClassPossibleList2);
		condicionEqClass |= eqClassActualList.equals(eqClassPossibleList3);
		condicionEqClass |= eqClassActualList.equals(eqClassPossibleList4);
		assertTrue(condicionEqClass);
	}

	/**
	 * Test method for {@link Automaton#getTopologicallySortedNodeList()}.
	 * It checks that a {@link NonAcyclicGraphException} is thrown if the method 
	 * is performed on a graph with cycles.
	 */
	@Test(expected = NonAcyclicGraphException.class)
	public void testGetTopologicallySortedNodeListAcyclic() {
		elementAutomaton.addEdge(elementE, elementB); // We add a cycle
		elementAutomaton.getTopologicallySortedNodeList();
	}
	
	/**
	 * Test method for {@link Automaton#learn(List)}.
	 * We learn a word and check that the changes made to the automaton are correct.
	 */
	@Test
	public void testLearn() {
		SchemaElement[] elementWordArray = {element0,elementA,elementD,elementE,element1};
		elementAutomaton.learn(Arrays.asList(elementWordArray));
		assertEquals(3,elementAutomaton.getEdgeWeight(element0, elementA));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementA, elementB));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementA, elementC));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementA, elementD));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementB, elementD));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementC, elementD));
		assertEquals(2,elementAutomaton.getEdgeWeight(elementD, elementE));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementD, elementF));
		assertEquals(2,elementAutomaton.getEdgeWeight(elementE, element1));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementF, element1));
		
		RegularExpression[] regexpWordArray = {regexp0,regexpA,regexpD,regexpE,regexp1};
		regexpAutomaton.learn(Arrays.asList(regexpWordArray));
		assertEquals(3,regexpAutomaton.getEdgeWeight(regexp0, regexpA));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpA, regexpB));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpA, regexpC));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpA, regexpD));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpB, regexpD));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpC, regexpD));
		assertEquals(2,regexpAutomaton.getEdgeWeight(regexpD, regexpE));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpD, regexpF));
		assertEquals(2,regexpAutomaton.getEdgeWeight(regexpE, regexp1));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpF, regexp1));
		
		EquivalenceClass[] eqClassWordArray = {eqClassA,eqClassD,eqClassE};
		eqClassAutomaton.learn(Arrays.asList(eqClassWordArray));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassB));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassC));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassD));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassB, eqClassD));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassC, eqClassD));
		assertEquals(2,eqClassAutomaton.getEdgeWeight(eqClassD, eqClassE));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassD, eqClassF));
	}
	
	/**
	 * Test method for {@link Automaton#learn(List)}.
	 * We learn a word with an element repetition and check that the changes 
	 * made to the automaton are correct.
	 */
	@Test
	public void testLearnSelfLoop() {
		SchemaElement[] elementWordArray = {element0,elementA,elementA,elementD,elementE,element1};
		elementAutomaton.learn(Arrays.asList(elementWordArray));
		assertEquals(3,elementAutomaton.getEdgeWeight(element0, elementA));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementA, elementA));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementA, elementB));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementA, elementC));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementA, elementD));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementB, elementD));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementC, elementD));
		assertEquals(2,elementAutomaton.getEdgeWeight(elementD, elementE));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementD, elementF));
		assertEquals(2,elementAutomaton.getEdgeWeight(elementE, element1));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementF, element1));
		
		RegularExpression[] regexpWordArray = {regexp0,regexpA,regexpA,regexpD,regexpE,regexp1};
		regexpAutomaton.learn(Arrays.asList(regexpWordArray));
		assertEquals(3,regexpAutomaton.getEdgeWeight(regexp0, regexpA));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpA, regexpA));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpA, regexpB));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpA, regexpC));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpA, regexpD));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpB, regexpD));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpC, regexpD));
		assertEquals(2,regexpAutomaton.getEdgeWeight(regexpD, regexpE));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpD, regexpF));
		assertEquals(2,regexpAutomaton.getEdgeWeight(regexpE, regexp1));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpF, regexp1));
		
		EquivalenceClass[] eqClassWordArray = {eqClassA,eqClassA,eqClassD,eqClassE};
		eqClassAutomaton.learn(Arrays.asList(eqClassWordArray));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassA));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassB));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassC));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassD));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassB, eqClassD));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassC, eqClassD));
		assertEquals(2,eqClassAutomaton.getEdgeWeight(eqClassD, eqClassE));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassD, eqClassF));
	}
	
	/**
	 * Test method for {@link Automaton#learn(List)}.
	 * We learn a word with new symbols and check that the changes made to the automaton are correct.
	 */
	@Test
	public void testLearnNewSymbols() {
		SchemaElement otherElement = mock(SchemaElement.class);
		SchemaElement[] elementWordArray = {element0, otherElement,elementA,elementD,elementE,element1};
		elementAutomaton.learn(Arrays.asList(elementWordArray));
		assertEquals(2,elementAutomaton.getEdgeWeight(element0, elementA));
		assertEquals(1,elementAutomaton.getEdgeWeight(element0, otherElement));
		assertEquals(1,elementAutomaton.getEdgeWeight(otherElement, elementA));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementA, elementB));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementA, elementC));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementA, elementD));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementB, elementD));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementC, elementD));
		assertEquals(2,elementAutomaton.getEdgeWeight(elementD, elementE));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementD, elementF));
		assertEquals(2,elementAutomaton.getEdgeWeight(elementE, element1));
		assertEquals(1,elementAutomaton.getEdgeWeight(elementF, element1));
		
		RegularExpression otherRegexp = mock(RegularExpression.class);
		RegularExpression[] regexpWordArray = {regexp0,otherRegexp,regexpA,regexpD,regexpE,regexp1};
		regexpAutomaton.learn(Arrays.asList(regexpWordArray));
		assertEquals(2,regexpAutomaton.getEdgeWeight(regexp0, regexpA));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexp0, otherRegexp));
		assertEquals(1,regexpAutomaton.getEdgeWeight(otherRegexp, regexpA));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpA, regexpB));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpA, regexpC));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpA, regexpD));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpB, regexpD));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpC, regexpD));
		assertEquals(2,regexpAutomaton.getEdgeWeight(regexpD, regexpE));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpD, regexpF));
		assertEquals(2,regexpAutomaton.getEdgeWeight(regexpE, regexp1));
		assertEquals(1,regexpAutomaton.getEdgeWeight(regexpF, regexp1));
		
		EquivalenceClass otherEqClass = mock(EquivalenceClass.class);
		EquivalenceClass[] eqClassWordArray = {otherEqClass,eqClassA,eqClassD,eqClassE};
		eqClassAutomaton.learn(Arrays.asList(eqClassWordArray));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(otherEqClass, eqClassA));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassB));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassC));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassA, eqClassD));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassB, eqClassD));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassC, eqClassD));
		assertEquals(2,eqClassAutomaton.getEdgeWeight(eqClassD, eqClassE));
		assertEquals(1,eqClassAutomaton.getEdgeWeight(eqClassD, eqClassF));
	}

	/**
	 * Test method for {@link Automaton#learn(List)}.
	 * We learn a word with only one symbol.
	 */
	@Test
	public void testLearnSingleElement(){
		EquivalenceClass otherEqClass = mock(EquivalenceClass.class);
		eqClassAutomaton.learn(Collections.singletonList(otherEqClass));
		assertTrue(eqClassAutomaton.containsNode(otherEqClass));
		assertTrue(eqClassAutomaton.getIncomingEdges(otherEqClass).isEmpty());
		assertTrue(eqClassAutomaton.getOutgoingEdges(otherEqClass).isEmpty());
	}
	
	/**
	 * Test method for {@link Automaton#learn(List)}.
	 * We check that an IllegalArgumentException is thrown when we try 
	 * to learn an empty word
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLearnIllegal(){
		eqClassAutomaton.learn(new ArrayList<EquivalenceClass>());
	}
	
	/**
	 * Test method for {@link Automaton#learn(List)}.
	 * We check that an IllegalArgumentException is thrown when we try 
	 * to learn an empty word
	 */
	@Test(expected = NullPointerException.class)
	public void testLearnNull(){
		eqClassAutomaton.learn(null);
	}
	
	/**
	 * Test method for {@link Automaton#learn(List)}.
	 * It checks that an IllegalArgumentException is thrown if we try to learn a word 
	 * without the initial state at the beginning.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLearnWithoutInitial(){
		SchemaElement[] elementWordArray = {elementA, elementB, element1};
		elementAutomaton.learn(Arrays.asList(elementWordArray));		
	}
	
	/**
	 * Test method for {@link Automaton#learn(List)}.
	 * It checks that an IllegalArgumentException is thrown if we try to learn a word 
	 * with the initial state out of order
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLearnInitialBadOrder(){
		SchemaElement[] elementWordArray = {elementA, element0, element1};
		elementAutomaton.learn(Arrays.asList(elementWordArray));		
	}
	
	/**
	 * Test method for {@link Automaton#learn(List)}.
	 * It checks that an IllegalArgumentException is thrown if we try to learn a word 
	 * with the initial state repeated, although one repetition is at the beginning.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLearnInitialRepeated(){
		SchemaElement[] elementWordArray = {element0, element0, element1};
		elementAutomaton.learn(Arrays.asList(elementWordArray));		
	}
	
	
	/**
	 * Test method for {@link Automaton#learn(List)}.
	 * It checks that an IllegalArgumentException is thrown if we try to learn a word 
	 * without the final state at the ending.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLearnWithoutFinal(){
		SchemaElement[] elementWordArray = {element0, elementB, elementC};
		elementAutomaton.learn(Arrays.asList(elementWordArray));		
	}
	
	/**
	 * Test method for {@link Automaton#learn(List)}.
	 * It checks that an IllegalArgumentException is thrown if we try to learn a word 
	 * with the final state out of order
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLearnFinalBadOrder(){
		SchemaElement[] elementWordArray = {element0, element1, elementA};
		elementAutomaton.learn(Arrays.asList(elementWordArray));		
	}
	
	/**
	 * Test method for {@link Automaton#learn(List)}.
	 * It checks that an IllegalArgumentException is thrown if we try to learn a word 
	 * with the final state repeated, although one repetition is at the ending.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testLearnFinalRepeated(){
		SchemaElement[] elementWordArray = {element0, element1, element1};
		elementAutomaton.learn(Arrays.asList(elementWordArray));		
	}
	
	/**
	 * Test method for {@link Automaton#learn(List)}.
	 * We learn a word into a new Automaton
	 */
	@Test
	public void testLearnAtTheBeginning(){
		Automaton<SchemaElement> newElementAutomaton = new ExtendedAutomaton();
		newElementAutomaton.setInitialState(element0); //We must set them explicitly
		newElementAutomaton.setFinalState(element1);   //because there could be no such states
		SchemaElement[] elementWordArray = {element0, elementA, elementB, element1};
		newElementAutomaton.learn(Arrays.asList(elementWordArray));
		assertTrue(newElementAutomaton.containsNode(element0));
		assertTrue(newElementAutomaton.containsNode(elementA));
		assertTrue(newElementAutomaton.containsNode(elementB));
		assertTrue(newElementAutomaton.containsNode(element1));
		assertEquals(1,newElementAutomaton.getEdgeWeight(element0, elementA));
		assertEquals(1,newElementAutomaton.getEdgeWeight(elementA, elementB));
		assertEquals(1,newElementAutomaton.getEdgeWeight(elementB, element1));
		
		Automaton<RegularExpression> newRegexpAutomaton = new Automaton<RegularExpression>();
		newRegexpAutomaton.setInitialState(regexp0); //We must set them explicitly
		newRegexpAutomaton.setFinalState(regexp1);   //because there could be no such states
		RegularExpression[] regexpWordArray = {regexp0, regexpA, regexpB, regexp1};
		newRegexpAutomaton.learn(Arrays.asList(regexpWordArray));
		assertTrue(newRegexpAutomaton.containsNode(regexp0));
		assertTrue(newRegexpAutomaton.containsNode(regexpA));
		assertTrue(newRegexpAutomaton.containsNode(regexpB));
		assertTrue(newRegexpAutomaton.containsNode(regexp1));
		assertEquals(1,newRegexpAutomaton.getEdgeWeight(regexp0, regexpA));
		assertEquals(1,newRegexpAutomaton.getEdgeWeight(regexpA, regexpB));
		assertEquals(1,newRegexpAutomaton.getEdgeWeight(regexpB, regexp1));
		
		Automaton<EquivalenceClass> newEqClassAutomaton = new Automaton<EquivalenceClass>();
		EquivalenceClass[] eqClassWordArray = {eqClassA, eqClassB};
		newEqClassAutomaton.learn(Arrays.asList(eqClassWordArray));
		assertTrue(newEqClassAutomaton.containsNode(eqClassA));
		assertTrue(newEqClassAutomaton.containsNode(eqClassB));
		assertEquals(1,newEqClassAutomaton.getEdgeWeight(eqClassA, eqClassB));

	}
	
}
