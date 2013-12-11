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
