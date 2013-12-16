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
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;

/**
 * Test class for {@link SchemaElement}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class SchemaElementTest {
	
	//Fields for testing
	private SchemaElement element;
	private ComplexType complexType;

	@Before
	public void setUp() throws Exception {
		complexType=mock(ComplexType.class);
		element = new SchemaElement("element","http://my.customnamespace.com",complexType);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testSchemaElement(){
		ComplexType otherComplexType=mock(ComplexType.class);
		element = new SchemaElement("otherElement","http://my.customnamespace.com",otherComplexType);
		assertEquals("otherElement",element.getName());
		assertEquals(otherComplexType,element.getType());
		assertEquals("http://my.customnamespace.com", element.getNamespace());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSchemaElementIllegal(){
		ComplexType otherComplexType=mock(ComplexType.class);
		element = new SchemaElement("a:nonNCName","http://my.customnamespace.com",otherComplexType);
	}

	@Test
	public void testGetElement() {
		assertEquals(element,element.getElement(0));
	}
	
	@Test
	public void testGetElementNull() {
		assertNull(element.getElement(1));
		assertNull(element.getElement(5));
	}

	@Test
	public void testCount() {
		assertEquals(1, element.elementCount());
	}

	@Test
	public void testGetType() {
		assertEquals(complexType,element.getType());
	}

	@Test(expected = NullPointerException.class)
	public void testSetTypeNull() {
		element.setType(null);
	}
	
	@Test
	public void testSetType() {
		ComplexType otherComplexType = mock(ComplexType.class);
		element.setType(otherComplexType);
		assertEquals(otherComplexType, element.getType());
	}

	@Test
	public void testGetName() {
		assertEquals("element",element.getName());
	}
	
	@Test
	public void testGetNamespace() {
		assertEquals("http://my.customnamespace.com", element.getNamespace());
	}

}
