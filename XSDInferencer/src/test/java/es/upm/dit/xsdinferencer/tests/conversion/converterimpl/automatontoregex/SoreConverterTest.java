package es.upm.dit.xsdinferencer.tests.conversion.converterimpl.automatontoregex;

import static es.upm.dit.xsdinferencer.datastructures.Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import es.upm.dit.xsdinferencer.conversion.RegexConverter;
import es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex.RegexConvertersFactory;
import es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex.exceptions.NoSuchRegexCanBeInferredException;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.Optional;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.Sequence;

/**
 * Test class for {@link SoreConverter}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class SoreConverterTest {

	//Fields for testing
	
	/**
	 * Element A of the automaton to convert 	
	 */
	private SchemaElement elementA;
	
	/**
	 * Element B of the automaton to convert 	
	 */
	private SchemaElement elementB;
	
	/**
	 * Element C of the automaton to convert 	
	 */
	private SchemaElement elementC;
	
	/**
	 * Element D of the automaton to convert 	
	 */
	private SchemaElement elementD;
	
	/**
	 * Element E of the automaton to convert 	
	 */
	private SchemaElement elementE;
	
	/**
	 * Initial state of automatons
	 */
	private SchemaElement initialState;
	
	/**
	 * Final state of automatons
	 */
	private SchemaElement finalState;
	
	/**
	 * Automaton for testing
	 */
	private ExtendedAutomaton automaton;
	
	@Before
	public void setUp() throws Exception {
		
		automaton = new ExtendedAutomaton();
		
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
		
		initialState=mock(SchemaElement.class);
		when(initialState.getName()).thenReturn("initial");
		when(initialState.getNamespace()).thenReturn(DEFAULT_PSEUDOELEMENTS_NAMESPACE);
		when(initialState.getElement(0)).thenReturn(initialState);
		when(initialState.elementCount()).thenReturn(1);
		when(initialState.toString()).thenReturn(DEFAULT_PSEUDOELEMENTS_NAMESPACE+":initial");
		
		finalState=mock(SchemaElement.class);
		when(finalState.getName()).thenReturn("final");
		when(finalState.getNamespace()).thenReturn(DEFAULT_PSEUDOELEMENTS_NAMESPACE);
		when(finalState.getElement(0)).thenReturn(finalState);
		when(finalState.elementCount()).thenReturn(1);
		when(finalState.toString()).thenReturn(DEFAULT_PSEUDOELEMENTS_NAMESPACE+":final");
		
		automaton.setInitialState(initialState);
		automaton.setFinalState(finalState);
	}

	/**
	 * This test builds the example automaton from the figure 2 of the following paper:  
	 * <pre>
	 * Geert Jan Bex, Frank Neven, Thomas Schwentick, and Karl Tuyls. 2006. 
	 * Inference of concise DTDs from XML data. 
	 * In Proceedings of the 32nd international conference on Very large data bases (VLDB '06), Umeshwar Dayal, Khu-Yong Whang, David Lomet, Gustavo Alonso, Guy Lohman, Martin Kersten, Sang K. Cha, and Young-Kuk Kim (Eds.). 
	 * VLDB Endowment 115-126.
	 * </pre>
	 * Then, it tries to infer a SORE from it. The resulting regular expression should be quite 
	 * similar to the one of the paper, but with some differences, because our algorithm includes the initial and final state as a part of the expression and removes them 
	 * in a later step (the optimization step), which is not tested here. 
	 * @throws NoSuchRegexCanBeInferredException If the conversion fails (which should not be possible for this automaton).
	 */
	@Test
	public void testComplexAutomatonToSoreConversion() throws NoSuchRegexCanBeInferredException {
		//bacacdacde
		automaton.learn(Lists.newArrayList(initialState,elementB,elementA,elementC,elementA,elementC,elementD,elementA,elementC,elementD,elementE,finalState));
		//cbacdbacde
		automaton.learn(Lists.newArrayList(initialState,elementC,elementB,elementA,elementC,elementD,elementB,elementA,elementC,elementD,elementE,finalState));
		//abccaadcde
		automaton.learn(Lists.newArrayList(initialState,elementA,elementB,elementC,elementC,elementA,elementA,elementD,elementC,elementD,elementE,finalState));
		RegexConvertersFactory regexConvertersFactory = RegexConvertersFactory.getInstance();
		RegexConverter regexConverter = regexConvertersFactory.getRegexConverterInstance(RegexConvertersFactory.SORE_CONVERTER);
		RegularExpression resultingRegexp = regexConverter.convertAutomatonToRegex(automaton);
//		System.out.println(resultingRegexp.toString());
		//There are many valid regular expressions, depending on some more or less random iteration orders.
		//IMPORTANT NOTE: This test MAY FAIL although the converter works normally because of this.
		assertTrue(resultingRegexp.toString().equals("(http://dit.upm.es/xsdinferencer/pseudoelements:initial (((((:b? (:c|:a)))+ :d))+ (:e http://dit.upm.es/xsdinferencer/pseudoelements:final)))") ||
				resultingRegexp.toString().equals("(http://dit.upm.es/xsdinferencer/pseudoelements:initial (((((:b? (:a|:c)))+ :d))+ (:e http://dit.upm.es/xsdinferencer/pseudoelements:final)))") ||
				resultingRegexp.toString().equals("(http://dit.upm.es/xsdinferencer/pseudoelements:initial ((((:b? (:c|:a)))+ :d))+ (:e http://dit.upm.es/xsdinferencer/pseudoelements:final))")||
				resultingRegexp.toString().equals("(http://dit.upm.es/xsdinferencer/pseudoelements:initial ((((:b? (:a|:c)))+ :d))+ (:e http://dit.upm.es/xsdinferencer/pseudoelements:final))")||
				resultingRegexp.toString().equals("(http://dit.upm.es/xsdinferencer/pseudoelements:initial (((:b? (:c|:a))+ :d)+ (:e http://dit.upm.es/xsdinferencer/pseudoelements:final)))")||
				resultingRegexp.toString().equals("(http://dit.upm.es/xsdinferencer/pseudoelements:initial (((:b? (:a|:c))+ :d)+ (:e http://dit.upm.es/xsdinferencer/pseudoelements:final)))"));
	}
	
	/**
	 * This method checks that a {@link NoSuchRegexCanBeInferredException} is thrown when the input is an automaton 
	 * which is makes fail the conversion algorithm.
	 * @throws NoSuchRegexCanBeInferredException The expected exception, thrown when a conversion fails.
	 */
	@SuppressWarnings("unused")
	@Test(expected = NoSuchRegexCanBeInferredException.class)
	public void testExpectedFail() throws NoSuchRegexCanBeInferredException{
		//a
		automaton.learn(Lists.newArrayList(initialState,elementA,finalState));
		//abca
		automaton.learn(Lists.newArrayList(initialState,elementA,elementB,elementC,elementA,finalState));
		RegexConvertersFactory regexConvertersFactory = RegexConvertersFactory.getInstance();
		RegexConverter regexConverter = regexConvertersFactory.getRegexConverterInstance(RegexConvertersFactory.SORE_CONVERTER);
		RegularExpression resultingRegexp = regexConverter.convertAutomatonToRegex(automaton);
//		System.err.println(resultingRegexp.toString());
	}
	
	/**
	 * This method checks that the conversion of an automaton consisting of a single real element (and the initial and final states) returns the expected result: 
	 * a sequence consisting of the initial state, the element and the final state.
	 * @throws NoSuchRegexCanBeInferredException specified by {@link RegexConverter#convertAutomatonToRegex(ExtendedAutomaton)} but never used at the tested converters.
	 */
	@Test
	public void testOnlyOneElementToSoreConversion() throws NoSuchRegexCanBeInferredException{
		automaton.learn(Lists.newArrayList(initialState,elementA,finalState));
				
		RegexConvertersFactory regexConvertersFactory = RegexConvertersFactory.getInstance();
		RegexConverter soreConverter = regexConvertersFactory.getRegexConverterInstance(RegexConvertersFactory.SORE_CONVERTER);
		RegularExpression sore = soreConverter.convertAutomatonToRegex(automaton);
		
		
//		RegularExpression soreElement1 = sore.getElement(0);
//		boolean factor1IsCorrectlyWrapped = soreElement1 instanceof SchemaElement;
//		assertTrue(factor1IsCorrectlyWrapped); 
//		assertEquals(initialState,soreElement1);
//		
//		RegularExpression soreElement2 = sore.getElement(1);
//		boolean soreElement2IsCorrectlyWrapped = soreElement2 instanceof SchemaElement;
//		assertTrue(soreElement2IsCorrectlyWrapped); 
//		assertEquals(elementA,soreElement2);
//		
//		RegularExpression soreElement3 = sore.getElement(2);
//		boolean soreElement3IsCorrectlyWrapped = soreElement3 instanceof SchemaElement;
//		assertTrue(soreElement3IsCorrectlyWrapped); 
//		assertEquals(finalState,soreElement3);
		
		RegularExpression soreElement1 = sore.getElement(0);
		boolean sore1IsSequence = soreElement1 instanceof Sequence;
		boolean sore1IsElement = soreElement1 instanceof SchemaElement;
		if(sore1IsSequence){
			assertEquals(2,soreElement1.elementCount());
			assertEquals(initialState,soreElement1.getElement(0));
			assertEquals(elementA,soreElement1.getElement(1));
		} else if(sore1IsElement){
			assertEquals(initialState,soreElement1);
		} else {
			fail("The first element of the SORE is of an unexpected type: "+soreElement1.getClass().getName());
		}
		
		RegularExpression soreElement2 = sore.getElement(1);
		RegularExpression soreElement3 = sore.getElement(2);
		boolean sore2IsSequence = soreElement2 instanceof Sequence;
		boolean sore2IsElement = soreElement2 instanceof SchemaElement;
		if(sore2IsSequence){
			assertEquals(2,soreElement2.elementCount());
			assertEquals(elementA,soreElement2.getElement(0));
			assertEquals(finalState,soreElement2.getElement(1));
		} else if(sore2IsElement){
			
			if(sore1IsElement){
				assertEquals(elementA,soreElement2);
				assertEquals(finalState,soreElement3);
				assertEquals(3,sore.elementCount());
			} else {
				assertEquals(finalState,soreElement2);
				assertEquals(2,sore.elementCount());
			}
		} else {
			fail("The first element of the SORE is of an unexpected type: "+soreElement2.getClass().getName());
		}
		
	}
	
	/**
	 * This method checks that the conversion of an automaton consisting of a single real element (and the initial and final states) and an 
	 * edge between the initial and the final state returns the expected result: the single element wrapped into an optional.
	 * @throws NoSuchRegexCanBeInferredException specified by {@link RegexConverter#convertAutomatonToRegex(ExtendedAutomaton)} but never used at the tested converters.
	 */
	@Test
	public void testOnlyOneOptionalElementToEchareConversion() throws NoSuchRegexCanBeInferredException{
		automaton.learn(Lists.newArrayList(initialState,elementA,finalState));
		automaton.learn(Lists.newArrayList(initialState,finalState));
			
		RegexConvertersFactory regexConvertersFactory = RegexConvertersFactory.getInstance();
		RegexConverter soreConverter = regexConvertersFactory.getRegexConverterInstance(RegexConvertersFactory.SORE_CONVERTER);
		RegularExpression sore = soreConverter.convertAutomatonToRegex(automaton);
				
		RegularExpression soreElement1 = sore.getElement(0);
		boolean sore1IsSequence = soreElement1 instanceof Sequence;
		boolean sore1IsElement = soreElement1 instanceof SchemaElement;
		if(sore1IsSequence){
			assertEquals(2,soreElement1.elementCount());
			assertEquals(initialState,soreElement1.getElement(0));
			RegularExpression soreElement12 = soreElement1.getElement(1);
			boolean soreElement12IsOptional = soreElement12 instanceof Optional;
			assertTrue(soreElement12IsOptional);
			assertEquals(1,soreElement12.elementCount());
			assertEquals(elementA,soreElement12.getElement(0));
		} else if(sore1IsElement){
			assertEquals(initialState,soreElement1);
		} else {
			fail("The first element of the SORE is of an unexpected type: "+soreElement1.getClass().getName());
		}
		
		RegularExpression soreElement2 = sore.getElement(1);
		RegularExpression soreElement3 = sore.getElement(2);
		boolean sore2IsSequence = soreElement2 instanceof Sequence;
		boolean sore2IsElement = soreElement2 instanceof SchemaElement;
		if(sore2IsSequence){
			assertEquals(2,soreElement2.elementCount());
			RegularExpression soreElement21 = soreElement2.getElement(0);
			boolean soreElement21IsOptional = soreElement21 instanceof Optional;
			assertTrue(soreElement21IsOptional);
			assertEquals(1,soreElement21.elementCount());
			assertEquals(elementA,soreElement21.getElement(0));
			assertEquals(finalState,soreElement2.getElement(1));
		} else if(sore2IsElement){
			if(sore1IsElement){
				RegularExpression soreElement21 = soreElement2.getElement(0);
				boolean soreElement21IsOptional = soreElement21 instanceof Optional;
				assertTrue(soreElement21IsOptional);
				assertEquals(1,soreElement21.elementCount());
				assertEquals(elementA,soreElement21.getElement(0));
				assertEquals(finalState,soreElement3);
				assertEquals(3,sore.elementCount());
			} else {
				assertEquals(finalState,soreElement2);
				assertEquals(2,sore.elementCount());
			}
		} else {
			fail("The first element of the SORE is of an unexpected type: "+soreElement2.getClass().getName());
		}
				
//		RegularExpression soreElement2 = sore.getElement(1);
//		boolean soreElement2IsCorrectlyWrapped = soreElement2 instanceof Optional;
//		assertTrue(soreElement2IsCorrectlyWrapped); 
//		assertEquals(1,soreElement2.count());
//		assertTrue(soreElement2.containsElement(elementA));
//		RegularExpression soreElement2Unwrapped = soreElement2.getElement(0);
//		assertEquals(soreElement2Unwrapped,elementA);
		
	}


}
