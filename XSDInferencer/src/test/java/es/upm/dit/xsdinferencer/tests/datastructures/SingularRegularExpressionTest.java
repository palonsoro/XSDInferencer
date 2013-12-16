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

import es.upm.dit.xsdinferencer.datastructures.Optional;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Repeated;
import es.upm.dit.xsdinferencer.datastructures.RepeatedAtLeastOnce;
import es.upm.dit.xsdinferencer.datastructures.SingularRegularExpression;

/**
 * Test for {@link SingularRegularExpression} and its implementations 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class SingularRegularExpressionTest {
	
	//Fields for testing
	
	private RegularExpression regexpNested;
	
	private Optional regexpOptional;
	private Repeated regexpRepeated;
	private RepeatedAtLeastOnce regexpRepeatedAtLeastOnce;
	

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
		regexpNested = mock(RegularExpression.class);
		regexpOptional = new Optional(regexpNested);
		regexpRepeated = new Repeated(regexpNested);
		regexpRepeatedAtLeastOnce = new RepeatedAtLeastOnce(regexpNested);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.SingularRegularExpression#SingularRegularExpression(es.upm.dit.xsdinferencer.datastructures.RegularExpression)}.
	 * It checks that the constructor behaves well and that all the values are properly set.
	 */
	@Test
	public void testSingularRegularExpression() {
		regexpNested = mock(RegularExpression.class);
		regexpOptional = new Optional(regexpNested);
		regexpRepeated = new Repeated(regexpNested);
		regexpRepeatedAtLeastOnce = new RepeatedAtLeastOnce(regexpNested);
		assertEquals(regexpNested, regexpOptional.getElement(0));
		assertEquals(regexpNested, regexpRepeated.getElement(0));
		assertEquals(regexpNested, regexpRepeatedAtLeastOnce.getElement(0));
	}
	
	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.SingularRegularExpression#SingularRegularExpression(es.upm.dit.xsdinferencer.datastructures.RegularExpression)}.
	 * It checks that the constructor throws a NullPointerException if a null 
	 * value is passed.
	 */
	@Test
	public void testSingularRegularExpressionNull(){
		boolean ok = false;
		try{
			new Optional(null);
		} catch(NullPointerException e){
			ok=true;
		}
		if(!ok){
			fail("Optional() has not thrown the NullPointerException");
		}
		
		
		ok=false;
		try{
			new Repeated(null);
		} catch(NullPointerException e){
			ok=true;
		}
		if(!ok){
			fail("Repeated() has not thrown the NullPointerException");
		}
		
		ok=false;
		try{
			new RepeatedAtLeastOnce(null);
		} catch(NullPointerException e){
			ok=true;
		}
		if(!ok){
			fail("RepeatedAtLeastOnce() has not thrown the NullPointerException");
		}
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.SingularRegularExpression#getElement(int)}.
	 * It checks that the only subexpression is well returned
	 */
	@Test
	public void testGetElement() {
		assertEquals(regexpNested, regexpOptional.getElement(0));
		assertEquals(regexpNested, regexpRepeated.getElement(0));
		assertEquals(regexpNested, regexpRepeatedAtLeastOnce.getElement(0));
	}
	
	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.SingularRegularExpression#getElement(int)}.
	 * It checks that the only subexpression is well returned
	 */
	@Test
	public void testGetElementNull(){
		assertNull(regexpOptional.getElement(1));
		assertNull(regexpRepeated.getElement(1));
		assertNull(regexpRepeatedAtLeastOnce.getElement(1));
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.SingularRegularExpression#elementCount()}.
	 * It checks that it always returns 1.
	 */
	@Test
	public void testCount() {
		assertEquals(1, regexpOptional.elementCount());
		assertEquals(1, regexpRepeated.elementCount());
		assertEquals(1, regexpRepeatedAtLeastOnce.elementCount());
	}

}
