package es.upm.dit.xsdinferencer.tests.datastructures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.upm.dit.xsdinferencer.datastructures.EmptyRegularExpression;

/**
 * Test class for {@link EmptyRegularExpression}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class EmptyRegularExpressionTest {
	
	//Fields for testing
	private EmptyRegularExpression regexpEmpty;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		regexpEmpty = new EmptyRegularExpression();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetElement() {
		assertNull(regexpEmpty.getElement(0));
		assertNull(regexpEmpty.getElement(1));
		assertNull(regexpEmpty.getElement(10));
	}

	@Test
	public void testCount() {
		assertEquals(0,regexpEmpty.elementCount());
	}

}
