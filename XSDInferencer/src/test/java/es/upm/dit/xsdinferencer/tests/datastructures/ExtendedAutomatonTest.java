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
package es.upm.dit.xsdinferencer.tests.datastructures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableSet;

import es.upm.dit.xsdinferencer.datastructures.Automaton;
import es.upm.dit.xsdinferencer.datastructures.EquivalenceClass;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.exceptions.NoWordHasBeenLearnedYetException;

/**
 * Tests for {@link ExtendedAutomaton}. 
 * Only specific methods of this subclass will be tested. 
 * Non-overridden methods are tested in {@link AutomatonTest}.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class ExtendedAutomatonTest {
	
	//Fields for testing

	//Test nodes
	
	//Element automaton
	private ExtendedAutomaton elementAutomaton;
	//Nodes for elementAutomaton
	private SchemaElement element0 = mock(SchemaElement.class);
	private SchemaElement elementA = mock(SchemaElement.class);
	private SchemaElement elementB = mock(SchemaElement.class);
	private SchemaElement elementC = mock(SchemaElement.class);
	private SchemaElement elementD = mock(SchemaElement.class); //Not in the default automaton
	private SchemaElement elementE = mock(SchemaElement.class);
	private SchemaElement elementF = mock(SchemaElement.class);
	private SchemaElement elementG = mock(SchemaElement.class);
	private SchemaElement element1 = mock(SchemaElement.class);
	
	private SchemaElement elementAEquivalent = mock(SchemaElement.class);
	private SchemaElement elementCEquivalent = mock(SchemaElement.class);
	private SchemaElement elementHEquivalent = mock(SchemaElement.class);
	//Words to build the automaton
	private SchemaElement[][] elementWordArrays = 
		{{element0,elementA,elementC,elementC,elementG,element1},
			{element0,elementB,elementC,elementE,elementF,elementG,element1},
			{element0,elementA,elementC,elementF,element1},
			{element0,elementB,elementC,elementC,elementE,elementG,element1},
			{element0,elementA,elementC,element1},
			{element0,elementB,elementC,elementC,elementF,elementE,element1}};
	//Equivalence classes used to run the tests on the automaton
	private EquivalenceClass eqClassAB; //Note that this equivalence class would not be formed directly but after merging singleton nodes.
	private EquivalenceClass eqClassC;
	private EquivalenceClass eqClassEF;
	private EquivalenceClass eqClassG;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Sets up all the equivalence class mocks
	 * @throws Exception
	 */
	private void setUpMocks(){
		eqClassAB=mock(EquivalenceClass.class);
		//We do this in this complicated wait so that the iterator is created each time 
		//the iterator() method is called and not only once
		when(eqClassAB.iterator()).thenAnswer(new Answer<Iterator<SchemaElement>>() {
			@SuppressWarnings("unchecked")
			@Override
			public Iterator<SchemaElement> answer(InvocationOnMock invocation)
					throws Throwable {
				Iterator<SchemaElement> eqClassABIterator =mock(Iterator.class);
				when(eqClassABIterator.next()).thenReturn(elementA, elementB).thenThrow(NoSuchElementException.class);
				when(eqClassABIterator.hasNext()).thenReturn(true,true,false);
				return eqClassABIterator;
			}
		});
		when(eqClassAB.contains(any(SchemaElement.class))).thenReturn(false);
		when(eqClassAB.contains(elementA)).thenReturn(true);
		when(eqClassAB.contains(elementB)).thenReturn(true);
		when(eqClassAB.size()).thenReturn(2);
		
		eqClassC=mock(EquivalenceClass.class);
		when(eqClassC.iterator()).thenAnswer(new Answer<Iterator<SchemaElement>>() {
			@SuppressWarnings("unchecked")
			@Override
			public Iterator<SchemaElement> answer(InvocationOnMock invocation)
					throws Throwable {
				Iterator<SchemaElement> eqClassCIterator =mock(Iterator.class);
				when(eqClassCIterator.next()).thenReturn(elementC).thenThrow(NoSuchElementException.class);
				when(eqClassCIterator.hasNext()).thenReturn(true,false);
				return eqClassCIterator;
			}
		});
		when(eqClassC.contains(any(SchemaElement.class))).thenReturn(false);
		when(eqClassC.contains(elementC)).thenReturn(true);
		when(eqClassC.size()).thenReturn(1);
		
		eqClassEF=mock(EquivalenceClass.class);
//		Iterator<SchemaElement> eqClassEFIterator = mock(Iterator.class);
		when(eqClassEF.iterator()).thenAnswer(new Answer<Iterator<SchemaElement>>() {
			@SuppressWarnings("unchecked")
			@Override
			public Iterator<SchemaElement> answer(InvocationOnMock invocation)
					throws Throwable {
				Iterator<SchemaElement> eqClassEFIterator = mock(Iterator.class);
				when(eqClassEFIterator.next()).thenReturn(elementE, elementF).thenThrow(NoSuchElementException.class);
				when(eqClassEFIterator.hasNext()).thenReturn(true,true,false);
				return eqClassEFIterator;
			}
		});
		when(eqClassEF.contains(any(SchemaElement.class))).thenReturn(false);
		when(eqClassEF.contains(elementE)).thenReturn(true);
		when(eqClassEF.contains(elementF)).thenReturn(true);
		when(eqClassEF.size()).thenReturn(2);
		
		eqClassG=mock(EquivalenceClass.class);
		when(eqClassG.iterator()).thenAnswer(new Answer<Iterator<SchemaElement>>() {
			@SuppressWarnings("unchecked")
			@Override
			public Iterator<SchemaElement> answer(InvocationOnMock invocation)
					throws Throwable {
				Iterator<SchemaElement> eqClassGIterator =mock(Iterator.class);
				when(eqClassGIterator.next()).thenReturn(elementG).thenThrow(NoSuchElementException.class);
				when(eqClassGIterator.hasNext()).thenReturn(true,false);
				return eqClassGIterator;
			}
		});
		when(eqClassG.contains(any(SchemaElement.class))).thenReturn(false);
		when(eqClassG.contains(elementG)).thenReturn(true);
		when(eqClassG.size()).thenReturn(1);
		
		when(element0.getNamespace()).thenReturn(Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE);
		when(element1.getNamespace()).thenReturn(Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE);
		when(elementA.getNamespace()).thenReturn("");
		when(elementB.getNamespace()).thenReturn("");
		when(elementC.getNamespace()).thenReturn("");
		when(elementD.getNamespace()).thenReturn("");
		when(elementE.getNamespace()).thenReturn("");
		when(elementF.getNamespace()).thenReturn("");
		when(elementG.getNamespace()).thenReturn("");
		when(elementAEquivalent.getNamespace()).thenReturn("");
		when(elementCEquivalent.getNamespace()).thenReturn("");
		when(elementHEquivalent.getNamespace()).thenReturn("");
		
		when(element0.getName()).thenReturn("initial");
		when(element1.getName()).thenReturn("final");
		when(elementA.getName()).thenReturn("a");
		when(elementB.getName()).thenReturn("b");
		when(elementC.getName()).thenReturn("c");
		when(elementD.getName()).thenReturn("d");
		when(elementE.getName()).thenReturn("e");
		when(elementF.getName()).thenReturn("f");
		when(elementG.getName()).thenReturn("g");
		when(elementAEquivalent.getName()).thenReturn("a");
		when(elementCEquivalent.getName()).thenReturn("c");
		when(elementHEquivalent.getName()).thenReturn("h");
		
		when(elementA.equalsIgnoreType(elementAEquivalent)).thenReturn(true);
		when(elementAEquivalent.equalsIgnoreType(elementA)).thenReturn(true);
		when(elementC.equalsIgnoreType(elementCEquivalent)).thenReturn(true);
		when(elementCEquivalent.equalsIgnoreType(elementC)).thenReturn(true);
	}
	
	/**
	 * Sets up all the testing fields
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		elementAutomaton = new ExtendedAutomaton();
		elementAutomaton.setInitialState(element0);
		elementAutomaton.setFinalState(element1);
		for(int i=0; i<elementWordArrays.length;i++){
			elementAutomaton.learn(Arrays.asList(elementWordArrays[i]));
		}
		setUpMocks();
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Checks that the default constructor works fine
	 */
	@Test
	public void testExtendedAutomaton() {
		elementAutomaton = new ExtendedAutomaton();
	}
	
	/**
	 * Test method for {@link ExtendedAutomaton#getFactorMinMaxOccurrences(EquivalenceClass)}.
	 * It checks that this method returns a correct 
	 * map with keys "max" and "min" and no other keys.
	 */
	@Test
	public void testGetFactorMinMaxOccurrencesCorrectKeys() {
		Map<String,Integer> resultAB = elementAutomaton.getFactorMinMaxOccurrences(eqClassAB);
		Map<String,Integer> resultC = elementAutomaton.getFactorMinMaxOccurrences(eqClassC);
		Map<String,Integer> resultEF = elementAutomaton.getFactorMinMaxOccurrences(eqClassEF);
		Map<String,Integer> resultG = elementAutomaton.getFactorMinMaxOccurrences(eqClassG);
		
		assertEquals(2,resultAB.keySet().size());
		assertTrue(resultAB.keySet().contains("min"));
		assertTrue(resultAB.keySet().contains("max"));
		
		assertEquals(2,resultC.keySet().size());
		assertTrue(resultC.keySet().contains("min"));
		assertTrue(resultC.keySet().contains("max"));
		
		assertEquals(2,resultEF.keySet().size());
		assertTrue(resultEF.keySet().contains("min"));
		assertTrue(resultEF.keySet().contains("max"));
		
		assertEquals(2,resultG.keySet().size());
		assertTrue(resultG.keySet().contains("min"));
		assertTrue(resultG.keySet().contains("max"));
		
	}
	
	/**
	 * Test method for {@link ExtendedAutomaton#getFactorMinMaxOccurrences(EquivalenceClass)}.
	 * It checks that a NullPointerException is thrown when a null value is provided.
	 */
	@Test(expected = NullPointerException.class)
	public void testGetFactorMinMaxOccurrencesNull() {
		elementAutomaton.getFactorMinMaxOccurrences(null);
	}
	
	/**
	 * Test method for {@link ExtendedAutomaton#getFactorMinMaxOccurrences(EquivalenceClass)}.
	 * It checks that a {@link NoWordHasBeenLearnedYetException} is thrown when a null value is provided.
	 */
	@Test(expected = NoWordHasBeenLearnedYetException.class)
	public void testGetFactorMinMaxOccurrencesEmpty() {
		new ExtendedAutomaton().getFactorMinMaxOccurrences(eqClassAB);
	}
	
	/**
	 * Test method for {@link ExtendedAutomaton#getFactorMinMaxOccurrences(EquivalenceClass)}
	 * It checks that this method returns correct values 
	 * from the example automaton.
	 */
	@Test
	public void testGetFactorMinMaxOccurrences() {
		Map<String,Integer> resultAB = elementAutomaton.getFactorMinMaxOccurrences(eqClassAB);
		Map<String,Integer> resultC = elementAutomaton.getFactorMinMaxOccurrences(eqClassC);
		Map<String,Integer> resultEF = elementAutomaton.getFactorMinMaxOccurrences(eqClassEF);
		Map<String,Integer> resultG = elementAutomaton.getFactorMinMaxOccurrences(eqClassG);
		
		assertEquals(1, resultAB.get("max").intValue());
		assertEquals(1, resultAB.get("min").intValue());
		assertEquals(2, resultC.get("max").intValue());
		assertEquals(1, resultC.get("min").intValue());
		assertEquals(2, resultEF.get("max").intValue());
		assertEquals(0, resultEF.get("min").intValue());
		assertEquals(1, resultG.get("max").intValue());
		assertEquals(0, resultG.get("min").intValue());
	}

	/**
	 * Test method for {@link ExtendedAutomaton#getFactorSymbolMinMaxOccurrences(EquivalenceClass)}.
	 * It checks that this method returns correct values 
	 * from the example automaton. 
	 */
	@Test
	public void testGetFactorSymbolMinMaxOccurrences() {
		Map<String,Integer> resultAB = elementAutomaton.getFactorSymbolMinMaxOccurrences(eqClassAB);
		Map<String,Integer> resultC = elementAutomaton.getFactorSymbolMinMaxOccurrences(eqClassC);
		Map<String,Integer> resultEF = elementAutomaton.getFactorSymbolMinMaxOccurrences(eqClassEF);
		Map<String,Integer> resultG = elementAutomaton.getFactorSymbolMinMaxOccurrences(eqClassG);
		
		assertEquals(1, resultAB.get("max").intValue());
		assertEquals(0, resultAB.get("min").intValue());
		assertEquals(2, resultC.get("max").intValue());
		assertEquals(1, resultC.get("min").intValue());
		assertEquals(1, resultEF.get("max").intValue());
		assertEquals(0, resultEF.get("min").intValue());
		assertEquals(1, resultG.get("max").intValue());
		assertEquals(0, resultG.get("min").intValue());
	}

	
	
	/**
	 * Test method for {@link ExtendedAutomaton#getFactorSymbolMinMaxOccurrences(EquivalenceClass)}.
	 * It checks that this method returns a correct 
	 * map with keys "max" and "min" and no other keys.
	 */
	@Test
	public void testGetFactorSymbolMinMaxOccurrencesCorrectKeys() {
		Map<String,Integer> resultAB = elementAutomaton.getFactorSymbolMinMaxOccurrences(eqClassAB);
		Map<String,Integer> resultC = elementAutomaton.getFactorSymbolMinMaxOccurrences(eqClassC);
		Map<String,Integer> resultEF = elementAutomaton.getFactorSymbolMinMaxOccurrences(eqClassEF);
		Map<String,Integer> resultG = elementAutomaton.getFactorSymbolMinMaxOccurrences(eqClassG);
		
		assertEquals(2,resultAB.keySet().size());
		assertTrue(resultAB.keySet().contains("min"));
		assertTrue(resultAB.keySet().contains("max"));
		
		assertEquals(2,resultC.keySet().size());
		assertTrue(resultC.keySet().contains("min"));
		assertTrue(resultC.keySet().contains("max"));
		
		assertEquals(2,resultEF.keySet().size());
		assertTrue(resultEF.keySet().contains("min"));
		assertTrue(resultEF.keySet().contains("max"));
		
		assertEquals(2,resultG.keySet().size());
		assertTrue(resultG.keySet().contains("min"));
		assertTrue(resultG.keySet().contains("max"));
		
	}
	
	/**
	 * Test method for {@link ExtendedAutomaton#getFactorSymbolMinMaxOccurrences(EquivalenceClass)}.
	 * It checks that a NullPointerException is thrown when a null value is provided.
	 */
	@Test(expected = NullPointerException.class)
	public void testGetFactorSymbolMinMaxOccurrencesNull() {
		elementAutomaton.getFactorSymbolMinMaxOccurrences(null);
	}
	
	/**
	 * Test method for {@link ExtendedAutomaton#getFactorSymbolMinMaxOccurrences(EquivalenceClass)}.
	 * It checks that a {@link NoWordHasBeenLearnedYetException} is thrown when a null value is provided.
	 */
	@Test(expected = NoWordHasBeenLearnedYetException.class)
	public void testGetFactorSymbolMinMaxOccurrencesEmpty() {
		new ExtendedAutomaton().getFactorSymbolMinMaxOccurrences(eqClassAB);
	}
	
	/**
	 * Test method for {@link ExtendedAutomaton#learn(java.util.List)}.
	 * It checks that the method adds new nodes when it learns a word with new nodes.
	 */
	@Test
	public void testLearnListOfSchemaElementNewNode() {
		SchemaElement[] elementWordArray={element0,elementA,elementC,elementD,element1};
		elementAutomaton.learn(Arrays.asList(elementWordArray));
		assertTrue(elementAutomaton.containsNode(elementD));
	}
	
	/**
	 * Test method for {@link ExtendedAutomaton#learn(java.util.List)}.
	 * It checks that a NullPointerException is thrown if a null value is provided.
	 */
	@Test(expected = NullPointerException.class)
	public void testLearnListOfSchemaElementNull() {
		elementAutomaton.learn(null);
	}
	
	/**
	 * Test method for {@link ExtendedAutomaton#learn(java.util.List)}.
	 * It checks that a word is learned correctly on a recently created automaton.
	 */
	@Test
	public void testLearnListOfSchemaElementOnEmptyAutomaton() {
		SchemaElement[] elementWordArray={element0,elementA,elementC,elementD,element1};
		ExtendedAutomaton newExtendedAutomaton = new ExtendedAutomaton();
		newExtendedAutomaton.learn(Arrays.asList(elementWordArray));
		assertTrue(newExtendedAutomaton.containsNode(element0));
		assertTrue(newExtendedAutomaton.containsNode(elementA));
		assertTrue(newExtendedAutomaton.containsNode(elementC));
		assertTrue(newExtendedAutomaton.containsNode(elementD));
		assertTrue(newExtendedAutomaton.containsNode(element1));
		assertEquals(1,newExtendedAutomaton.getEdgeWeight(element0, elementA));
		assertEquals(1,newExtendedAutomaton.getEdgeWeight(elementA, elementC));
		assertEquals(1,newExtendedAutomaton.getEdgeWeight(elementC, elementD));
		assertEquals(1,newExtendedAutomaton.getEdgeWeight(elementD, element1));
	}
	
	/**
	 * Test method for the copy constructor {@link ExtendedAutomaton#ExtendedAutomaton(ExtendedAutomaton)}.
	 * It checks that the copy is equals (but not same) to the original.
	 */
	@Test
	public void testExtendedAutomatonExtendedAutomaton() {
		ExtendedAutomaton copy = new ExtendedAutomaton(elementAutomaton);
		assertEquals(elementAutomaton,copy);
		assertNotSame(elementAutomaton,copy);
	}
	
	/**
	 * Test for the containsAllEquivalentNodes() methods
	 */
	@Test
	public void testContainsEquivalentNodes(){
		assertTrue(elementAutomaton.containsAllEquivalentNodes(ImmutableSet.of(elementAEquivalent,elementCEquivalent)));
		assertTrue(elementAutomaton.containsAllEquivalentNodes(ImmutableSet.of(elementAEquivalent)));
		assertTrue(elementAutomaton.containsAllEquivalentNodes(ImmutableSet.of(elementCEquivalent)));
		
		assertFalse(elementAutomaton.containsAllEquivalentNodes(ImmutableSet.of(elementHEquivalent)));
		assertFalse(elementAutomaton.containsAllEquivalentNodes(ImmutableSet.of(elementAEquivalent,elementHEquivalent)));
		assertFalse(elementAutomaton.containsAllEquivalentNodes(ImmutableSet.of(elementCEquivalent,elementHEquivalent)));
		assertFalse(elementAutomaton.containsAllEquivalentNodes(ImmutableSet.of(elementAEquivalent,elementCEquivalent,elementHEquivalent)));
		
	}
	
	/**
	 * Test for the containsAllEquivalentEdges() method
	 */
	@Test
	public void testContainsEquivalentEdges(){
		assertTrue(elementAutomaton.containsEdgeEquivalent(elementAEquivalent, elementCEquivalent));
		
		assertFalse(elementAutomaton.containsEdgeEquivalent(elementCEquivalent, elementAEquivalent));
		assertFalse(elementAutomaton.containsEdgeEquivalent(elementAEquivalent, elementHEquivalent));
		assertFalse(elementAutomaton.containsEdgeEquivalent(elementHEquivalent, elementCEquivalent));
	}
	
}
