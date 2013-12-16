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

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;

/**
 * Test class for {@link ComplexType}
 * @author Pablo Alonso Rodriguez
 */
public class ComplexTypeTest {
	
	//Fields for testig
	private ComplexType complexType;
	private ExtendedAutomaton automaton;
	private SimpleType simpleType;
	private SchemaAttribute attr1;
	private SchemaAttribute attr2;
	private SchemaAttribute attr3;
	private List<SchemaAttribute> attrList;
	private RegularExpression regexp;
	

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
		automaton=mock(ExtendedAutomaton.class);
		simpleType=mock(SimpleType.class);
		attrList = new ArrayList<SchemaAttribute>();
		attr1=mock(SchemaAttribute.class);
		attr2=mock(SchemaAttribute.class);
		attr3=mock(SchemaAttribute.class);
		attrList.add(attr1);
		attrList.add(attr2);
		attrList.add(attr3);
		complexType = new ComplexType("complexType", automaton, simpleType,attrList);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.ComplexType#ComplexType(java.lang.String, es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton, es.upm.dit.xsdinferencer.datastructures.SimpleType, java.util.List)}.
	 * It checks that the constructor behaves well and all the values are correctly set.
	 */
	@Test
	public void testComplexType() {
		automaton=mock(ExtendedAutomaton.class);
		simpleType=mock(SimpleType.class);
		attrList = new ArrayList<SchemaAttribute>();
		attr1=mock(SchemaAttribute.class);
		attr2=mock(SchemaAttribute.class);
		attr3=mock(SchemaAttribute.class);
		attrList.add(attr1);
		attrList.add(attr2);
		attrList.add(attr3);
		complexType = new ComplexType("otherComplexType", automaton, simpleType,attrList);
		assertEquals("otherComplexType", complexType.getName());
		assertEquals(automaton,complexType.getAutomaton());
		assertEquals(simpleType, complexType.getTextSimpleType());
		assertEquals(attrList, complexType.getAttributeList());
		complexType.setRegularExpression(regexp);
	}
	
	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.ComplexType#ComplexType(java.lang.String, es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton, es.upm.dit.xsdinferencer.datastructures.SimpleType, java.util.List)}.
	 * It checks that the constructor throws a NullPointerException if a null name is specified.
	 */
	@Test(expected = NullPointerException.class)
	public void testComplexTypeNull() {
		new ComplexType(null, automaton, simpleType, attrList);
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.ComplexType#getName()}.
	 */
	@Test
	public void testGetName() {
		assertEquals("complexType", complexType.getName());
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.ComplexType#getAutomaton()}.
	 */
	@Test
	public void testGetAutomaton() {
		assertEquals(automaton,complexType.getAutomaton());
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.ComplexType#getTextSimpleType()}.
	 */
	@Test
	public void testGetTextSimpleType() {
		assertEquals(simpleType, complexType.getTextSimpleType());
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.ComplexType#setTextSimpleType(es.upm.dit.xsdinferencer.datastructures.SimpleType)}.
	 */
	@Test
	public void testSetTextSimpleType() {
		SimpleType otherSimpleType=mock(SimpleType.class);
		complexType.setTextSimpleType(otherSimpleType);
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.ComplexType#setTextSimpleType(es.upm.dit.xsdinferencer.datastructures.SimpleType)}.
	 * It checks that a NullPointerException is thrown 
	 * when a null is passed.
	 */
	@Test(expected = NullPointerException.class)
	public void testSetTextSimpleTypeNull(){
		complexType.setTextSimpleType(null);
	}
	
	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.ComplexType#getAttributeList()}.
	 */
	@Test
	public void testGetAttributeList() {
		assertEquals(attrList, complexType.getAttributeList());
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.ComplexType#getRegularExpression()}.
	 */
	@Test
	public void testGetRegularExpression() {
		assertEquals(regexp, complexType.getRegularExpression());
	}
	
	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.ComplexType#getRegularExpression()}.
	 */
	@Test
	public void testGetRegularExpressionDefaultNull() {
		complexType=new ComplexType("complex", automaton, simpleType, attrList); //We create the complex type again to see its default null regular expression
		assertNull(complexType.getRegularExpression());
	}

	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.ComplexType#setRegularExpression(es.upm.dit.xsdinferencer.datastructures.RegularExpression)}.
	 */
	@Test
	public void testSetRegularExpression() {
		RegularExpression otherRegexp = mock(RegularExpression.class);
		complexType.setRegularExpression(otherRegexp);
		assertEquals(otherRegexp,complexType.getRegularExpression());
	}
	
	/**
	 * Test method for {@link es.upm.dit.xsdinferencer.datastructures.ComplexType#setRegularExpression(es.upm.dit.xsdinferencer.datastructures.RegularExpression)}.
	 * It checks that there is no problem when setting a null RegularExpression.
	 */
	@Test
	public void testSetRegularExpressionToNull() {
		complexType.setRegularExpression(null);
		assertNull(complexType.getRegularExpression());
	}

}
