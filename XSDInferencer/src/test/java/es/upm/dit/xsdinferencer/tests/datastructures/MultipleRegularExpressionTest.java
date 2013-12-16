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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.upm.dit.xsdinferencer.datastructures.All;
import es.upm.dit.xsdinferencer.datastructures.Choice;
import es.upm.dit.xsdinferencer.datastructures.MultipleRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.Sequence;

/**
 * Test class for {@link MultipleRegularExpression} and its implementations
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class MultipleRegularExpressionTest {

	//Testing fields
	private All regexpAll;
	private Choice regexpChoice;
	private Sequence regexpSequence;
	
	private RegularExpression regexpNested1;
	private RegularExpression regexpNested2;
	private RegularExpression regexpNested3;
	
	private SchemaElement elementNested1;
	private SchemaElement elementNested2;
	private SchemaElement elementNested3;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		regexpNested1=mock(RegularExpression.class);
		regexpNested2=mock(RegularExpression.class);
		regexpNested3=mock(RegularExpression.class);
		
		elementNested1=mock(SchemaElement.class);
		elementNested2=mock(SchemaElement.class);
		elementNested3=mock(SchemaElement.class);
		
		RegularExpression[] regexpNestedArray = {regexpNested1,regexpNested2,regexpNested3};
		SchemaElement[] elementNestedArray = {elementNested1,elementNested2,elementNested3};
		
		regexpAll = new All(elementNestedArray);
		regexpSequence = new Sequence(regexpNestedArray);
		regexpChoice = new Choice(regexpNestedArray);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link All#All(SchemaElement[])}, {@link Sequence#Sequence(RegularExpression[])} and 
	 * {@link Choice#Choice(RegularExpression[])}.
	 * It checks that all those constructors behave correctly and that all the values are properly set
	 */
	@Test
	public void testMultipleRegularExpression() {
		RegularExpression[] regexpNestedArray = {regexpNested1,regexpNested2,regexpNested3};
		SchemaElement[] elementNestedArray = {elementNested1,elementNested2,elementNested3};
		
		regexpAll = new All(elementNestedArray);
		assertEquals(3, regexpAll.elementCount());
		assertEquals(elementNested1,regexpAll.getElement(0));
		assertEquals(elementNested2,regexpAll.getElement(1));
		assertEquals(elementNested3,regexpAll.getElement(2));
		assertEquals(1,regexpAll.getMinOccurs());
		
		regexpSequence = new Sequence(regexpNestedArray);
		assertEquals(3, regexpSequence.elementCount());
		assertEquals(regexpNested1,regexpSequence.getElement(0));
		assertEquals(regexpNested2,regexpSequence.getElement(1));
		assertEquals(regexpNested3,regexpSequence.getElement(2));
		
		regexpChoice = new Choice(regexpNestedArray);
		assertEquals(3, regexpChoice.elementCount());
		assertEquals(regexpNested1,regexpChoice.getElement(0));
		assertEquals(regexpNested2,regexpChoice.getElement(1));
		assertEquals(regexpNested3,regexpChoice.getElement(2));
	}
	
	/**
	 * Test method for {@link All#All(SchemaElement[])}, {@link Sequence#Sequence(RegularExpression[])} and 
	 * {@link Choice#Choice(RegularExpression[])}.
	 * It checks that all those constructors throw a NullPointerException if a null array is provided.
	 */
	@Test
	public void testMultipleRegularExpressionNull(){
		boolean ok = false;
		RegularExpression[] nullArray=null;
		SchemaElement[] nullSchemaElementArray=null;
		try{
			new All(nullSchemaElementArray);
		} catch(NullPointerException e){
			ok=true;
		}
		if(!ok){
			fail("All() has not thrown the NullPointerException");
		}
		ok=false;
		try{
			new All(nullSchemaElementArray,1);
		} catch(NullPointerException e){
			ok=true;
		}
		if(!ok){
			fail("All() has not thrown the NullPointerException");
		}
		
		ok=false;
		try{
			new Sequence(nullArray);
		} catch(NullPointerException e){
			ok=true;
		}
		if(!ok){
			fail("Sequence() has not thrown the NullPointerException");
		}
		
		ok=false;
		try{
			new Choice(nullArray);
		} catch(NullPointerException e){
			ok=true;
		}
		if(!ok){
			fail("Choice() has not thrown the NullPointerException");
		}
	}

	/**
	 * Test method for {@link All#All(SchemaElement[], int)}.
	 * It checks that this constructor behaves well and that 
	 * the minOccurs value is well set.
	 */
	@Test
	public void testAllMinOccurs(){
		SchemaElement[] elementNestedArray = {elementNested1,elementNested2,elementNested3};
		regexpAll = new All(elementNestedArray,0);
		assertEquals(0,regexpAll.getMinOccurs());
	}
	
	/**
	 * Test method for {@link All#All(SchemaElement[], int)}.
	 * It checks that this constructor throws an IllegalArgumentException 
	 * if a value other than 0 or 1 is passed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAllMinOccursIllegal(){
		SchemaElement[] elementNestedArray = {elementNested1,elementNested2,elementNested3};
		regexpAll = new All(elementNestedArray,34);
	}
	
	/**
	 * Test method for all implementations of {@link MultipleRegularExpression#getElement(int)}.
	 */
	@Test
	public void testGetElement() {
		assertEquals(elementNested1,regexpAll.getElement(0));
		assertEquals(elementNested2,regexpAll.getElement(1));
		assertEquals(elementNested3,regexpAll.getElement(2));
		
		assertEquals(regexpNested1,regexpSequence.getElement(0));
		assertEquals(regexpNested2,regexpSequence.getElement(1));
		assertEquals(regexpNested3,regexpSequence.getElement(2));
		
		assertEquals(regexpNested1,regexpChoice.getElement(0));
		assertEquals(regexpNested2,regexpChoice.getElement(1));
		assertEquals(regexpNested3,regexpChoice.getElement(2));
	}

	/**
	 * Test method for all the implementations of {@link MultipleRegularExpression#getElement(int)}.
	 * It checks that a null value is returned if there is no element at 
	 * the specified position.
	 */
	@Test
	public void testGetElementOutOfBounds(){
		assertNull(regexpAll.getElement(15));
		assertNull(regexpSequence.getElement(15));
		assertNull(regexpChoice.getElement(15));
	}
	
	/**
	 * Test method for all the implementations of {@link MultipleRegularExpression#elementCount()}.
	 */
	@Test
	public void testCount() {
		assertEquals(3, regexpAll.elementCount());
		assertEquals(3, regexpSequence.elementCount());
		assertEquals(3, regexpChoice.elementCount());
	}

}
