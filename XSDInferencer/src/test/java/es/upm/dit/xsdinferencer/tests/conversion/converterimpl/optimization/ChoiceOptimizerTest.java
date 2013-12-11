package es.upm.dit.xsdinferencer.tests.conversion.converterimpl.optimization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import es.upm.dit.xsdinferencer.conversion.RegexOptimizer;
import es.upm.dit.xsdinferencer.conversion.converterimpl.optimization.RegexOptimizersFactory;
import es.upm.dit.xsdinferencer.datastructures.Choice;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.Sequence;

/**
 * Test class for {@link ChoiceOptimizer}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class ChoiceOptimizerTest {
	
	//Fields for testing
	private RegularExpression mainRegularExpression;
	
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
		
		Choice containedChoice=new Choice(Lists.newArrayList(elementC,elementD));
		Choice containerChoice=new Choice(Lists.newArrayList(elementB,containedChoice));
		mainRegularExpression=new Sequence(Lists.newArrayList(elementA,containerChoice,elementE));
		
	}

	/**
	 * This method checks that the optimizer optimizes a subexpression made of nested 
	 * choices by converting it into a single choice, without changing anything more.
	 */
	@Test
	public void testOptimizeChoice() {
		RegexOptimizer optimizer = RegexOptimizersFactory.getInstance().getRegexOptimizerInstance(RegexOptimizersFactory.VALUE_OPTIMIZERS_CHOICE);
		boolean modified = optimizer.optimizeRegex(mainRegularExpression);
		assertTrue(modified);
		RegularExpression newChoice = mainRegularExpression.getElement(1);
		boolean containerIsStillChoice = newChoice instanceof Choice;
		assertTrue(containerIsStillChoice);
		assertTrue(newChoice.containsElement(elementB));
		assertTrue(newChoice.containsElement(elementC));
		assertTrue(newChoice.containsElement(elementD));
		assertEquals(3,newChoice.elementCount());
	}

}
