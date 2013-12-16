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

import org.junit.Before;
import org.junit.Test;

import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;

/**
 * Test class for {@link SchemaAttribute}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class SchemaAttributeTest {
	
	//Fields for testing
	private SchemaAttribute schemaAttribute;
	private SimpleType simpleType;
	
	/**
	 * It prepares a simple SchemaAttribute object for testing
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		simpleType = mock(SimpleType.class);
		schemaAttribute=new SchemaAttribute("attr", "http://mycustomnamespace.com", false, simpleType);
	}

	/**
	 * Test for {@link SchemaAttribute#SchemaAttribute(String, boolean, SimpleType)}.
	 * It checks that all the specified values are set properly.
	 */
	@Test
	public void testSchemaAttribute() {
		SimpleType otherSimpleType=mock(SimpleType.class);
		schemaAttribute=new SchemaAttribute("other", "http://mycustomnamespace.com", true, otherSimpleType);
		assertTrue(schemaAttribute.isOptional());
		assertEquals("other",schemaAttribute.getName());
		assertEquals(otherSimpleType, schemaAttribute.getSimpleType());
		assertEquals("http://mycustomnamespace.com", schemaAttribute.getNamespace());
	}
	

	/**
	 * Test for {@link SchemaAttribute#SchemaAttribute(String, boolean, SimpleType)}.
	 * It checks that an IllegalArgumentException is thrown when the name is not a valid NCName
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSchemaAttributeIllegal(){
		SimpleType otherSimpleType=mock(SimpleType.class);
		schemaAttribute=new SchemaAttribute("other:notNCNAME", "http://mycustomnamespace.com", true, otherSimpleType);
	}

	/**
	 * Test method for {@link SchemaAttribute#getName()}
	 */
	@Test
	public void testGetName() {
		assertEquals("attr",schemaAttribute.getName());
	}

	/**
	 * Test method for {@link SchemaAttribute#isOptional()}
	 */
	@Test
	public void testIsOptional() {
		assertFalse(schemaAttribute.isOptional());
	}

	/**
	 * Test method for {@link SchemaAttribute#setOptional()}
	 */
	@Test
	public void testSetOptional() {
		schemaAttribute.setOptional(true);
		assertTrue(schemaAttribute.isOptional());
	}

	/**
	 * Test method for {@link SchemaAttribute#getSimpleType()}
	 */
	@Test
	public void testGetSimpleType() {
		assertEquals(simpleType,schemaAttribute.getSimpleType());
	}

	/**
	 * Test method for {@link SchemaAttribute#setSimpleType()}
	 */
	@Test
	public void testSetSimpleType() {
		SimpleType other = mock(SimpleType.class);
		schemaAttribute.setSimpleType(other);
		assertEquals(other,schemaAttribute.getSimpleType());
	}
	
	/**
	 * Test method for {@link SchemaAttribute#setSimpleType()}
	 * It checks that a NullPointerException is thrown if a null 
	 * value is passed
	 */
	@Test(expected = NullPointerException.class)
	public void testSetSimpleTypeNull() {
		schemaAttribute.setSimpleType(null);
	}

	/**
	 * Test method for {@link SchemaAttribute#getNamespace()}
	 * It checks that the expected value is returned correctly
	 */
	@Test
	public void testGetNamespace(){
		assertEquals("http://mycustomnamespace.com",schemaAttribute.getNamespace());
	}
	
	/**
	 * Test method for {@link SchemaAttribute#getNamespace()}
	 * It checks that the expected value is returned correctly when the 
	 * namespace is an empty namespace.
	 */
	@Test
	public void testGetNamespaceEmpty(){
		schemaAttribute=new SchemaAttribute("otherAttribute", null, true, simpleType);
		assertEquals("",schemaAttribute.getNamespace());
	}
}
