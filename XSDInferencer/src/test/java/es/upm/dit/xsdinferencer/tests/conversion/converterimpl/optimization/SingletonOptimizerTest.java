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
import es.upm.dit.xsdinferencer.datastructures.All;
import es.upm.dit.xsdinferencer.datastructures.Choice;
import es.upm.dit.xsdinferencer.datastructures.MultipleRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.Sequence;

/**
 * Test class for {@link SingletonOptimizer}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class SingletonOptimizerTest {
	
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
		
		listResult = ImmutableList.<RegularExpression>of(elementA,elementB,elementC,elementD,elementE);
	}

	/**
	 * Generates the testing list of elements, given a MultipleRegularExpression, it is placed 
	 * into a predefined list of subexpressions.
	 * @param multiple a {@linkplain MultipleRegularExpression} which will be placed somewhere in the generated list.
	 * @return the generated list.
	 */
	private List<RegularExpression> generateElementList(MultipleRegularExpression multiple) {
		return ImmutableList.of(elementA,elementB,multiple,elementD,elementE);
	}
	
	/**
	 * This method checks that a given regular expression is optimized correctly by the optimizer, it means, 
	 * by transforming any MultipleRegularExpression subexpression which consists of a single element into 
	 * the element itself.
	 * @param regex a regex to test
	 */
	private void testOptimizeSingletonCommon(RegularExpression regex){
		RegexOptimizer optimizer = RegexOptimizersFactory.getInstance().getRegexOptimizerInstance(RegexOptimizersFactory.VALUE_OPTIMIZERS_SINGLETON);
		optimizer.optimizeRegex(regex);
		assertEquals(listResult,((MultipleRegularExpression)regex).getImmutableListOfElements());
	}
	
	/**
	 * This method checks that a singleton All subexpression is transformed into the element properly
	 */
	@Test
	public void testSingletonAll() {
		MultipleRegularExpression multiple = new All(Collections.singleton(elementC));
		List<RegularExpression> regexContents = generateElementList(multiple);
		RegularExpression regex = new Sequence(regexContents);
		testOptimizeSingletonCommon(regex);
	}
	
	/**
	 * This method checks that a singleton Sequence subexpression is transformed into the element properly
	 */
	@Test
	public void testSingletonSequence() {
		MultipleRegularExpression multiple = new Sequence(Collections.singleton(elementC));
		List<RegularExpression> regexContents = generateElementList(multiple);
		RegularExpression regex = new Sequence(regexContents);
		testOptimizeSingletonCommon(regex);
	}
	
	/**
	 * This method checks that a singleton Choice subexpression is transformed into the element properly
	 */
	@Test
	public void testSingletonChoice() {
		MultipleRegularExpression multiple = new Choice(Collections.singleton(elementC));
		List<RegularExpression> regexContents = generateElementList(multiple);
		RegularExpression regex = new Sequence(regexContents);
		testOptimizeSingletonCommon(regex);
	}
}
