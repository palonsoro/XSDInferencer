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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import es.upm.dit.xsdinferencer.conversion.RegexOptimizer;
import es.upm.dit.xsdinferencer.conversion.converterimpl.optimization.RegexOptimizersFactory;
import es.upm.dit.xsdinferencer.datastructures.Choice;
import es.upm.dit.xsdinferencer.datastructures.EmptyRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.MultipleRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.Sequence;

/**
 * Test class for {@link EmptyOptimizer}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class EmptyOptimizerTest {
	
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
	private RegularExpression empty;
	
	/**
	 * The list of elements which should have the resulting regular expressions
	 */
	private List<RegularExpression> listResult;
	
	
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
		
		listResult = ImmutableList.of(elementA,elementB,empty,elementC,elementD,elementE);
		
	}
	
	/**
	 * This method checks that a {@link MultipleRegularExpression} which contains a subexpression made of 
	 * empty elements or which is empty is transformed into an empty regular expression by the optimizer.
	 * @param regex the regex which should be checked
	 */
	private void testOptimizeEmptyCommon(RegularExpression regex){
		RegexOptimizer optimizer = RegexOptimizersFactory.getInstance().getRegexOptimizerInstance(RegexOptimizersFactory.VALUE_OPTIMIZERS_EMPTY);
		optimizer.optimizeRegex(regex);
		assertEquals(listResult,((MultipleRegularExpression)regex).getImmutableListOfElements());
	}

	/**
	 * Generates the testing list of elements, given a MultipleRegularExpression, 
	 * by placing it into a predefined list of subexpressions.
	 * @param multiple a {@linkplain MultipleRegularExpression} which will bw placed somewhere in the generated list.
	 * @return the generated list.
	 */
	private List<RegularExpression> generateElementList(MultipleRegularExpression multiple) {
		return ImmutableList.of(elementA,elementB,multiple,elementC,elementD,elementE);
	}

	/**
	 * This method checks that the optimizer works for an empty Choice
	 */
	@Test
	public void testOptimizeEmptyChoice() {
		MultipleRegularExpression multiple = new Choice(Collections.<RegularExpression>emptyList());
		RegularExpression regex = new Sequence(generateElementList(multiple));
		testOptimizeEmptyCommon(regex);
	}
	
	/**
	 * This method checks that the optimizer works for a Choice of empty elements
	 */
	@Test
	public void testOptimizeChoiceOfEmpty() {
		MultipleRegularExpression multiple = new Choice(ImmutableList.of(empty,empty,empty));
		RegularExpression regex = new Sequence(generateElementList(multiple));
		testOptimizeEmptyCommon(regex);
	}
	
	/**
	 * This method checks that the optimizer works for an empty Sequence
	 */
	@Test
	public void testOptimizeEmptySequence() {
		MultipleRegularExpression multiple = new Sequence(Collections.<RegularExpression>emptyList());
		RegularExpression regex = new Sequence(generateElementList(multiple));
		testOptimizeEmptyCommon(regex);
	}
	
	/**
	 * This method checks that the optimizer works for a Sequence of empty elements
	 */
	@Test
	public void testOptimizeSequenceOfEmpty() {
		MultipleRegularExpression multiple = new Sequence(ImmutableList.of(empty,empty,empty));
		RegularExpression regex = new Sequence(generateElementList(multiple));
		testOptimizeEmptyCommon(regex);
	}
}
