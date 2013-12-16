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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.upm.dit.xsdinferencer.datastructures.EquivalenceClass;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;

/**
 * Tests the EquivalenceClass class
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class EquivalenceClassTest {
	
	//Fields for testing
	private SchemaElement testElement1;
	private SchemaElement testElement2;
	private SchemaElement testElement3;
	private SchemaElement[] testArray = new SchemaElement[3];
	private Set<SchemaElement> testSet = new HashSet<SchemaElement>();
	private EquivalenceClass eqClassTest;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		testElement1 = mock(SchemaElement.class);
		when(testElement1.getName()).thenReturn("test1");
		testElement2 = mock(SchemaElement.class);
		when(testElement2.getName()).thenReturn("test2");
		testElement3 = mock(SchemaElement.class);
		when(testElement3.getName()).thenReturn("test3");
		testArray[0]=testElement1;
		testArray[1]=testElement2;
		testArray[2]=testElement3;
		testSet.clear();
		testSet.add(testElement1);
		testSet.add(testElement2);
		testSet.add(testElement3);
		eqClassTest = new EquivalenceClass ();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.EquivalenceClass#EquivalenceClass(es.upm.dit.xsdinferencer.datastructures.SchemaElement[])}.
	 */
	@Test
	public void testEquivalenceClassSchemaElementArray() {
		EquivalenceClass eqClass = new EquivalenceClass(testArray);
		assertTrue(eqClass.contains(testElement1));
		assertTrue(eqClass.contains(testElement2));
		assertTrue(eqClass.contains(testElement3));
	}

	
	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.EquivalenceClass#EquivalenceClass(es.upm.dit.xsdinferencer.datastructures.SchemaElement[])}.
	 */
	@SuppressWarnings("unused")
	@Test(expected = java.lang.NullPointerException.class)
	public void testEquivalenceClassSchemaElementArrayNull() {
		SchemaElement[] nullArray = null;
		EquivalenceClass eqClass = new EquivalenceClass(nullArray);
	}
	
	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.EquivalenceClass#EquivalenceClass(es.upm.dit.xsdinferencer.datastructures.SchemaElement[])}.
	 */
	@SuppressWarnings("unused")
	@Test(expected = java.lang.NullPointerException.class)
	public void testEquivalenceClassSchemaElementSetOfSchemaNull() {
		Set<SchemaElement> nullArray = null;
		EquivalenceClass eqClass = new EquivalenceClass(nullArray);
	}
	
	
	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.EquivalenceClass#EquivalenceClass(java.util.Set)}.
	 */
	@Test
	public void testEquivalenceClassSetOfSchemaElement() {
		EquivalenceClass eqClass = new EquivalenceClass(testSet);
		assertTrue(eqClass.contains(testElement1));
		assertTrue(eqClass.contains(testElement2));
		assertTrue(eqClass.contains(testElement3));
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.EquivalenceClass#size()}.
	 * Checks whether the size of a recently created EquivalenceClass is zero.
	 */
	@Test
	public void testSizeZero() {
		assertEquals(0, eqClassTest.size());
	}
	
	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.EquivalenceClass#size()}.
	 * Checks the size of an equivalence class with one element.
	 */
	@Test
	public void testSizeOne() {
		eqClassTest.add(testElement1);
		assertEquals(1, eqClassTest.size());
	}
	
	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.EquivalenceClass#size()}.
	 * Checks the size of an equivalence class with many elements.
	 */
	@Test
	public void testSizeMany() {
		eqClassTest.add(testElement1);
		eqClassTest.add(testElement2);
		eqClassTest.add(testElement3);
		assertEquals(3, eqClassTest.size());
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.EquivalenceClass#contains(es.upm.dit.xsdinferencer.datastructures.SchemaElement)}.
	 * Checks whether the contains method returns true for the actual elements contained in a equivalence class 
	 * and does not contain other elements.
	 */
	@Test
	public void testContains() {
		eqClassTest.add(testElement1);
		eqClassTest.add(testElement2);
		assertTrue(eqClassTest.contains(testElement1));
		assertTrue(eqClassTest.contains(testElement2));
		assertFalse(eqClassTest.contains(testElement3));
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.EquivalenceClass#add(es.upm.dit.xsdinferencer.datastructures.SchemaElement)}.
	 * It checks whether elements are added correctly and that there are no repetitions.
	 */
	@Test
	public void testAdd() {
		eqClassTest.add(testElement1);
		assertTrue(eqClassTest.contains(testElement1));
		assertEquals(1, eqClassTest.size());
		eqClassTest.add(testElement1);
		assertEquals("There should be only one element because repetitions are not alloweds",1, eqClassTest.size());
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.EquivalenceClass#remove(es.upm.dit.xsdinferencer.datastructures.SchemaElement)}.
	 * Checks whether an element may be successfully removed and whether a non existing element may not 
	 * be removed.
	 */
	@Test
	public void testRemove() {
		eqClassTest.add(testElement1);
		eqClassTest.add(testElement2);
		assertTrue(eqClassTest.remove(testElement1));
		assertFalse(eqClassTest.contains(testElement1));
		assertEquals(1, eqClassTest.size());
		assertFalse(eqClassTest.remove(testElement1));
		assertFalse(eqClassTest.remove(testElement3));
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.EquivalenceClass#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {
		eqClassTest.add(testElement1);
		eqClassTest.add(testElement2);
		eqClassTest.add(testElement3);
		
		EquivalenceClass eqClassTest2=new EquivalenceClass();
		eqClassTest2.add(testElement1);
		eqClassTest2.add(testElement2);
		eqClassTest2.add(testElement3);
		
		assertEquals(eqClassTest,eqClassTest);
		
		assertEquals(eqClassTest2,eqClassTest);
		
		EquivalenceClass eqClassTest3 = new EquivalenceClass();
		
		assertFalse(eqClassTest2.equals(eqClassTest3));
	}

}
