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
package es.upm.dit.xsdinferencer.tests.conversion.converterimpl.optimization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import es.upm.dit.xsdinferencer.conversion.RegexOptimizer;
import es.upm.dit.xsdinferencer.conversion.converterimpl.optimization.RegexOptimizersFactory;
import es.upm.dit.xsdinferencer.datastructures.All;
import es.upm.dit.xsdinferencer.datastructures.Choice;
import es.upm.dit.xsdinferencer.datastructures.EmptyRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.MultipleRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.Sequence;

/**
 * Test method for {@link EmptyChildOptimizer}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class EmptyChildOptimizertTest {

	//Fields for testing
	
	/**
	 * Element A of the regular expression to optimize
	 */
	private SchemaElement elementA;
	
	/**
	 * Element B of the regular expression to optimize
	 */
	private SchemaElement elementB;
	
	/**
	 * Element C of the regular expression to optimize
	 */
	private SchemaElement elementC;
	
	/**
	 * Element D of the regular expression to optimize
	 */
	private SchemaElement elementD;
	
	/**
	 * Element E of the regular expression to optimize
	 */
	private SchemaElement elementE;
	
	/**
	 * Empty regular expression
	 */
	private EmptyRegularExpression empty;
	
	/**
	 * List of elements with Empty children made to build test regular expressions
	 */
	private List<RegularExpression> elements;
	
	/**
	 * A list with the same elements than the previous one, but without empty elements, to compare.
	 */
	private List<SchemaElement> onlyElements;
	
	@Before
	public void setUp() throws Exception {
		elementA=mock(SchemaElement.class);
		when(elementA.getName()).thenReturn("a");
		when(elementA.getNamespace()).thenReturn("");
		when(elementA.getElement(0)).thenReturn(elementA);
		when(elementA.elementCount()).thenReturn(1);
		when(elementA.toString()).thenReturn(":a");
		
		elementB=mock(SchemaElement.class);
		when(elementB.getName()).thenReturn("b");
		when(elementB.getNamespace()).thenReturn("");
		when(elementB.getElement(0)).thenReturn(elementB);
		when(elementB.elementCount()).thenReturn(1);
		when(elementB.toString()).thenReturn(":b");
		
		elementC=mock(SchemaElement.class);
		when(elementC.getName()).thenReturn("c");
		when(elementC.getNamespace()).thenReturn("");
		when(elementC.getElement(0)).thenReturn(elementC);
		when(elementC.elementCount()).thenReturn(1);
		when(elementC.toString()).thenReturn(":c");
		
		elementD=mock(SchemaElement.class);
		when(elementD.getName()).thenReturn("d");
		when(elementD.getNamespace()).thenReturn("");
		when(elementD.getElement(0)).thenReturn(elementD);
		when(elementD.elementCount()).thenReturn(1);
		when(elementD.toString()).thenReturn(":d");
		
		elementE=mock(SchemaElement.class);
		when(elementE.getName()).thenReturn("e");
		when(elementE.getNamespace()).thenReturn("");
		when(elementE.getElement(0)).thenReturn(elementE);
		when(elementE.elementCount()).thenReturn(1);
		when(elementE.toString()).thenReturn(":e");
		
		empty = new EmptyRegularExpression();
		
		elements = ImmutableList.of(elementA,elementB,empty,elementC,empty,elementD,elementE,empty);
		onlyElements = ImmutableList.of(elementA,elementB,elementC,elementD,elementE);
	}

	/**
	 * Common method to check that an optimized {@link MultipleRegularExpression} (of any kind) 
	 * has no empty elements and is the expected one
	 * @param regex a regex to optimize
	 */
	private void testOptimizeCommon(RegularExpression regex){
		RegexOptimizer optimizer = RegexOptimizersFactory.getInstance().getRegexOptimizerInstance(RegexOptimizersFactory.VALUE_OPTIMIZERS_EMPTYCHILD);
		optimizer.optimizeRegex(regex);
		assertEquals(onlyElements,((MultipleRegularExpression)regex).getImmutableListOfElements());
		assertFalse(regex.containsElement(empty));
	}
	
	/**
	 * Tests the optimizer against a Choice
	 */
	@Test
	public void testOptimizeChoice() {
		RegularExpression choice = new Choice(elements);
		testOptimizeCommon(choice);
	}
	
	/**
	 * Tests the optimizer against a Sequence
	 */
	@Test
	public void testOptimizeSequence() {
		RegularExpression sequence = new Sequence(elements);
		testOptimizeCommon(sequence);
	}

	/**
	 * Tests that the optimizer does not change an All regular expression, although it is a MultipleRegularExpression, 
	 * as an All may not contain anything but elements.
	 */
	@Test
	public void testOptimizeAll() {
		RegularExpression all = new All(onlyElements);
		testOptimizeCommon(all);
	}

}
