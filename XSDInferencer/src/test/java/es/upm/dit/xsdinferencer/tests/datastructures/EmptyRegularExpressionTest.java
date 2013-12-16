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
