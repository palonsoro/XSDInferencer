package es.upm.dit.xsdinferencer.tests.datastructures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import es.upm.dit.xsdinferencer.datastructures.SimpleType;

/**
 * Test class for {@link SimpleType}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class SimpleTypeTest {
	
	//Field for testing
	private SimpleType simpleType;

	
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * It prepares a SimpleType for testing. There are four known values, 
	 * type xs:string and isEnum false.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		simpleType=new SimpleType("padre-hijo-@simpletype");
		simpleType.addToEnumeration("uno");
		simpleType.addToEnumeration("dos");
		simpleType.addToEnumeration("tres");
		simpleType.addToEnumeration("cuatro");
	}

	/**
	 * Test for constructor {@link SimpleType#SimpleType(String)}.
	 * It checks that all the default options are well set.
	 */
	@Test
	public void testSimpleTypeString() {
		simpleType=new SimpleType("otroNombre");
		assertEquals("otroNombre", simpleType.getName());
		assertFalse(simpleType.isEnum());
		assertEquals(0, simpleType.enumerationCount());
	}
	
	/**
	 * Test for constructor {@link SimpleType#SimpleType(String)}.
	 * It checks that a NullPointerException is thrown if name == null
	 */
	@Test(expected = NullPointerException.class)
	public void testSimpleTypeStringNull() {
		simpleType=new SimpleType(null);
	}

	/**
	 * Test for constructor {@link SimpleType#SimpleType(String, String, Collection, boolean)}.
	 * It checks that a NullPointerException is thrown if name == null
	 */
	@Test(expected = NullPointerException.class)
	public void testSimpleTypeStringStringCollectionOfQextendsStringBooleanNull() {
		simpleType=new SimpleType(null,"xs:string",new ArrayList<String>(),false);
	}

	/**
	 * Test method for {@link SimpleType#SimpleType(String, String, Collection, boolean)}.
	 * It checks that the values of the new object are set properly.
	 */
	@Test
	public void testSimpleTypeStringStringCollectionOfQextendsStringBoolean() {
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		simpleType=new SimpleType("tipoPersonalizado","xs:normalizedString",list,true);
		assertTrue(simpleType.isEnum());
		assertEquals("xs:normalizedString",simpleType.getBuiltinType());
		assertEquals(2,simpleType.enumerationCount());
		assertTrue(simpleType.enumerationContains("a"));
		assertTrue(simpleType.enumerationContains("b"));
		assertEquals("a",simpleType.getEnumerationElement(0));
		assertEquals("b",simpleType.getEnumerationElement(1));
	}

	/**
	 * Test method for {@link SimpleType#getName()}.
	 */
	@Test
	public void testGetName() {
		assertEquals("padre-hijo-@simpletype", simpleType.getName());
	}

	/**
	 * Test method for {@link SimpleType#setName(String)}
	 */
	@Test
	public void testSetName() {
		simpleType.setName("probando");
		assertEquals("probando", simpleType.getName());
	}

	/**
	 * Test method for {@link SimpleType#getBuiltinType()}
	 */
	@Test
	public void testGetBuiltinType() {
		assertEquals("", simpleType.getBuiltinType());
	}

	/**
	 * Test method for {@link SimpleType#setBuiltinType(String)}
	 */
	@Test
	public void testSetBuiltinType() {
		simpleType.setBuiltinType("xs:integer");
		assertEquals("xs:integer",simpleType.getBuiltinType());
	}

	/**
	 * Test method for {@link SimpleType#isEnum()}
	 */
	@Test
	public void testIsEnum() {
		assertFalse(simpleType.isEnum());
	}

	/**
	 * Test method for {@link SimpleType#setEnum(boolean)}
	 */
	@Test
	public void testSetEnum() {
		simpleType.setEnum(true);
		assertTrue(simpleType.isEnum());
	}

	/**
	 * Test method for {@link SimpleType#enumerationCount()}
	 */
	@Test
	public void testEnumerationCount() {
		assertEquals(4, simpleType.enumerationCount());
	}

	/**
	 * Test method for {@link SimpleType#enumerationContains(String)}
	 */
	@Test
	public void testEnumerationContains() {
		assertTrue(simpleType.enumerationContains("uno"));
		assertTrue(simpleType.enumerationContains("dos"));
		assertTrue(simpleType.enumerationContains("tres"));
		assertTrue(simpleType.enumerationContains("cuatro"));
		assertFalse(simpleType.enumerationContains("cinco"));
	}

	/**
	 * Test method for {@link SimpleType#addToEnumeration(String)}
	 */
	@Test
	public void testAddToEnumeration() {
		simpleType.addToEnumeration("cinco");
		assertTrue(simpleType.enumerationContains("cinco"));
		assertEquals(5, simpleType.enumerationCount());
		assertEquals("cinco",simpleType.getEnumerationElement(4));
	}

	/**
	 * Test method for {@link SimpleType#enumerationContainsAll(Collection)}. 
	 * Checks that the method behaves correctly when a subset of the known values 
	 * is passed and when one of the elements is not a known value.
	 */
	@Test
	public void testEnumerationContainsAllPartially() {
		List<String> list = new ArrayList<String>(2);
		list.add("uno");
		list.add("cuatro");
		assertTrue(simpleType.enumerationContainsAll(list));
		list.add("ups");
		assertFalse(simpleType.enumerationContainsAll(list));
	}
	
	/**
	 * Test method for {@link SimpleType#enumerationContainsAll(Collection)}. 
	 * Checks that the method behaves correctly when a collection of all the 
	 * known values is passed and when the collection consists of all the known 
	 * values plus another one.
	 */
	@Test
	public void testEnumerationContainsAll() {
		List<String> list = new ArrayList<String>(4);
		list.add("uno");
		list.add("cuatro");
		list.add("tres");
		list.add("dos");
		assertTrue(simpleType.enumerationContainsAll(list));
		list.add("ups");
		assertFalse(simpleType.enumerationContainsAll(list));
	}

	/**
	 * Test method for {@link SimpleType#addAllToEnumeration(Collection)}
	 */
	@Test
	public void testAddAllToEnumeration() {
		List<String> list = new ArrayList<String>(2);
		list.add("cinco");
		list.add("seis");
		simpleType.addAllToEnumeration(list);
	}

	/**
	 * Test method for {@link SimpleType#getEnumerationElement(int)}
	 */
	@Test
	public void testGetEnumerationElement() {
		assertEquals("uno",simpleType.getEnumerationElement(0));
		assertEquals("dos",simpleType.getEnumerationElement(1));
		assertEquals("tres",simpleType.getEnumerationElement(2));
		assertEquals("cuatro",simpleType.getEnumerationElement(3));
	}

}
