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

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import es.upm.dit.xsdinferencer.conversion.RegexOptimizer;
import es.upm.dit.xsdinferencer.conversion.converterimpl.optimization.RegexOptimizersFactory;
import es.upm.dit.xsdinferencer.datastructures.MultipleRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Optional;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Repeated;
import es.upm.dit.xsdinferencer.datastructures.RepeatedAtLeastOnce;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.Sequence;

/**
 * Test method for {@linkplain SingularRegularExpressionOptimizer}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class SingularRegularExpressionOptimizerTest {
	
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
	}

	/**
	 * Method that checks that the contents of the resulting regular expressions are the expected ones
	 * @param regex the regex to check
	 * @param listResult the expected contents
	 */
	private void testSingularRegularExpressionTestsCommon(RegularExpression regex, List<RegularExpression> listResult){
		RegexOptimizer optimizer = RegexOptimizersFactory.getInstance().getRegexOptimizerInstance(RegexOptimizersFactory.VALUE_OPTIMIZERS_SINGULAR_REGULAR_EXPRESSION);
		optimizer.optimizeRegex(regex);
		assertEquals(listResult,((MultipleRegularExpression)regex).getImmutableListOfElements());
	}
	
	/**
	 * Method that checks that an expression like c?? is transformed into c?
	 */
	@Test
	public void testOptimizeSingularOptionalInOptional() {
		RegularExpression child = new Optional(elementC);
		RegularExpression parent = new Optional(child);
		List<RegularExpression> list = ImmutableList.of(elementA,elementB,parent,elementD,elementE);
		List<RegularExpression> listResult = ImmutableList.of(elementA,elementB,new Optional(elementC),elementD,elementE);
		testSingularRegularExpressionTestsCommon(new Sequence(list), listResult);
	}
	
	/**
	 * Method that checks that an expression like c+? is transformed into c*
	 */
	@Test
	public void testOptimizeSingularRepeatedAtLeastOnceInOptional() {
		RegularExpression child = new RepeatedAtLeastOnce(elementC);
		RegularExpression parent = new Optional(child);
		List<RegularExpression> list = ImmutableList.of(elementA,elementB,parent,elementD,elementE);
		List<RegularExpression> listResult = ImmutableList.of(elementA,elementB,new Repeated(elementC),elementD,elementE);
		testSingularRegularExpressionTestsCommon(new Sequence(list), listResult);
	}

	/**
	 * Method that checks that an expression like c*? is transformed into c?
	 */
	@Test
	public void testOptimizeSingularRepeatedInOptional() {
		RegularExpression child = new Repeated(elementC);
		RegularExpression parent = new Optional(child);
		List<RegularExpression> list = ImmutableList.of(elementA,elementB,parent,elementD,elementE);
		List<RegularExpression> listResult = ImmutableList.of(elementA,elementB,new Repeated(elementC),elementD,elementE);
		testSingularRegularExpressionTestsCommon(new Sequence(list), listResult);
	}

	/**
	 * Method that checks that an expression like c?+ is transformed into c*
	 */
	@Test
	public void testOptimizeSingularOptionalInRepeatedAtLeastOnce() {
		RegularExpression child = new Optional(elementC);
		RegularExpression parent = new RepeatedAtLeastOnce(child);
		List<RegularExpression> list = ImmutableList.of(elementA,elementB,parent,elementD,elementE);
		List<RegularExpression> listResult = ImmutableList.of(elementA,elementB,new Repeated(elementC),elementD,elementE);
		testSingularRegularExpressionTestsCommon(new Sequence(list), listResult);
	}

	/**
	 * Method that checks that an expression like c++ is transformed into c+
	 */
	@Test
	public void testOptimizeSingularRepeatedAtLeastOnceInRepeatedAtLeastOnce() {
		RegularExpression child = new RepeatedAtLeastOnce(elementC);
		RegularExpression parent = new RepeatedAtLeastOnce(child);
		List<RegularExpression> list = ImmutableList.of(elementA,elementB,parent,elementD,elementE);
		List<RegularExpression> listResult = ImmutableList.of(elementA,elementB,new RepeatedAtLeastOnce(elementC),elementD,elementE);
		testSingularRegularExpressionTestsCommon(new Sequence(list), listResult);
	}

	/**
	 * Method that checks that an expression like c*+ is transformed into c*
	 */
	@Test
	public void testOptimizeSingularRepeatedInRepeatedAtLeastOnce() {
		RegularExpression child = new Repeated(elementC);
		RegularExpression parent = new RepeatedAtLeastOnce(child);
		List<RegularExpression> list = ImmutableList.of(elementA,elementB,parent,elementD,elementE);
		List<RegularExpression> listResult = ImmutableList.of(elementA,elementB,new Repeated(elementC),elementD,elementE);
		testSingularRegularExpressionTestsCommon(new Sequence(list), listResult);
	}

	/**
	 * Method that checks that an expression like c?* is transformed into c*
	 */
	@Test
	public void testOptimizeSingularOptionalInRepeated() {
		RegularExpression child = new Optional(elementC);
		RegularExpression parent = new Repeated(child);
		List<RegularExpression> list = ImmutableList.of(elementA,elementB,parent,elementD,elementE);
		List<RegularExpression> listResult = ImmutableList.of(elementA,elementB,new Repeated(elementC),elementD,elementE);
		testSingularRegularExpressionTestsCommon(new Sequence(list), listResult);
	}

	/**
	 * Method that checks that an expression like c*+ is transformed into c*
	 */
	@Test
	public void testOptimizeSingularRepeatedAtLeastOnceInRepeated() {
		RegularExpression child = new RepeatedAtLeastOnce(elementC);
		RegularExpression parent = new Repeated(child);
		List<RegularExpression> list = ImmutableList.of(elementA,elementB,parent,elementD,elementE);
		List<RegularExpression> listResult = ImmutableList.of(elementA,elementB,new Repeated(elementC),elementD,elementE);
		testSingularRegularExpressionTestsCommon(new Sequence(list), listResult);
	}

	/**
	 * Method that checks that an expression like c** is transformed into c*
	 */
	@Test
	public void testOptimizeSingularRepeatedInRepeated() {
		RegularExpression child = new Repeated(elementC);
		RegularExpression parent = new Repeated(child);
		List<RegularExpression> list = ImmutableList.of(elementA,elementB,parent,elementD,elementE);
		List<RegularExpression> listResult = ImmutableList.of(elementA,elementB,new Repeated(elementC),elementD,elementE);
		testSingularRegularExpressionTestsCommon(new Sequence(list), listResult);
	}


}
