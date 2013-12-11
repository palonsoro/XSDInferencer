package es.upm.dit.xsdinferencer.tests.conversion.converterimpl.automatontoregex;

import static es.upm.dit.xsdinferencer.datastructures.Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import es.upm.dit.xsdinferencer.conversion.RegexConverter;
import es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex.RegexConvertersFactory;
import es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex.exceptions.NoSuchRegexCanBeInferredException;
import es.upm.dit.xsdinferencer.datastructures.All;
import es.upm.dit.xsdinferencer.datastructures.Choice;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.Optional;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Repeated;
import es.upm.dit.xsdinferencer.datastructures.RepeatedAtLeastOnce;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.Sequence;

/**
 * Test class for {@link ChareConverter} and its subclass {@link EChareConverter}.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class ChareAndEChareCoverterTest {
	
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
	 * Element F of the automaton to convert 	
	 */
	private SchemaElement elementF;
	/**
	 * Element G of the automaton to convert 	
	 */
	private SchemaElement elementG;
	/**
	 * Element H of the automaton to convert 	
	 */
	private SchemaElement elementH;
	/**
	 * Element I of the automaton to convert 	
	 */
	private SchemaElement elementI;
	
	/**
	 * Initial state of automatons
	 */
	private SchemaElement initialState;
	
	/**
	 * Final state of automatons
	 */
	private SchemaElement finalState;
	
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
		when(elementA.containsElement(elementA)).thenReturn(true);
		
		elementB=mock(SchemaElement.class);
		when(elementB.getName()).thenReturn("b");
		when(elementB.getNamespace()).thenReturn("");
		when(elementB.getElement(0)).thenReturn(elementB);
		when(elementB.elementCount()).thenReturn(1);
		when(elementB.toString()).thenReturn(":b");
		when(elementB.containsElement(elementB)).thenReturn(true);
		
		elementC=mock(SchemaElement.class);
		when(elementC.getName()).thenReturn("c");
		when(elementC.getNamespace()).thenReturn("");
		when(elementC.getElement(0)).thenReturn(elementC);
		when(elementC.elementCount()).thenReturn(1);
		when(elementC.toString()).thenReturn(":c");
		when(elementC.containsElement(elementC)).thenReturn(true);
		
		elementD=mock(SchemaElement.class);
		when(elementD.getName()).thenReturn("d");
		when(elementD.getNamespace()).thenReturn("");
		when(elementD.getElement(0)).thenReturn(elementD);
		when(elementD.elementCount()).thenReturn(1);
		when(elementD.toString()).thenReturn(":d");
		when(elementD.containsElement(elementD)).thenReturn(true);
		
		elementE=mock(SchemaElement.class);
		when(elementE.getName()).thenReturn("e");
		when(elementE.getNamespace()).thenReturn("");
		when(elementE.getElement(0)).thenReturn(elementE);
		when(elementE.elementCount()).thenReturn(1);
		when(elementE.toString()).thenReturn(":e");
		when(elementE.containsElement(elementE)).thenReturn(true);
		
		elementF=mock(SchemaElement.class);
		when(elementF.getName()).thenReturn("f");
		when(elementF.getNamespace()).thenReturn("");
		when(elementF.getElement(0)).thenReturn(elementF);
		when(elementF.elementCount()).thenReturn(1);
		when(elementF.toString()).thenReturn(":f");
		when(elementF.containsElement(elementF)).thenReturn(true);
		
		elementG=mock(SchemaElement.class);
		when(elementG.getName()).thenReturn("g");
		when(elementG.getNamespace()).thenReturn("");
		when(elementG.getElement(0)).thenReturn(elementG);
		when(elementG.elementCount()).thenReturn(1);
		when(elementG.toString()).thenReturn(":g");
		when(elementG.containsElement(elementG)).thenReturn(true);
		
		elementH=mock(SchemaElement.class);
		when(elementH.getName()).thenReturn("h");
		when(elementH.getNamespace()).thenReturn("");
		when(elementH.getElement(0)).thenReturn(elementH);
		when(elementH.elementCount()).thenReturn(1);
		when(elementH.toString()).thenReturn(":h");
		when(elementH.containsElement(elementH)).thenReturn(true);
		
		elementI=mock(SchemaElement.class);
		when(elementI.getName()).thenReturn("i");
		when(elementI.getNamespace()).thenReturn("");
		when(elementI.getElement(0)).thenReturn(elementI);
		when(elementI.elementCount()).thenReturn(1);
		when(elementI.toString()).thenReturn(":i");
		when(elementI.containsElement(elementI)).thenReturn(true);
		
		initialState=mock(SchemaElement.class);
		when(initialState.getName()).thenReturn("initial");
		when(initialState.getNamespace()).thenReturn(DEFAULT_PSEUDOELEMENTS_NAMESPACE);
		when(initialState.getElement(0)).thenReturn(initialState);
		when(initialState.elementCount()).thenReturn(1);
		when(initialState.toString()).thenReturn(DEFAULT_PSEUDOELEMENTS_NAMESPACE+":initial");
		when(initialState.containsElement(initialState)).thenReturn(true);
		
		finalState=mock(SchemaElement.class);
		when(finalState.getName()).thenReturn("final");
		when(finalState.getNamespace()).thenReturn(DEFAULT_PSEUDOELEMENTS_NAMESPACE);
		when(finalState.getElement(0)).thenReturn(finalState);
		when(finalState.elementCount()).thenReturn(1);
		when(finalState.toString()).thenReturn(DEFAULT_PSEUDOELEMENTS_NAMESPACE+":final");
		when(initialState.containsElement(initialState)).thenReturn(true);
		
		automaton.setInitialState(initialState);
		automaton.setFinalState(finalState);
		
	}

	/**
	 * This test builds the example automaton from words of the Example 2. of the following paper:  
	 * <pre>
	 * Geert Jan Bex, Frank Neven, Thomas Schwentick, and Karl Tuyls. 2006. 
	 * Inference of concise DTDs from XML data. 
	 * In Proceedings of the 32nd international conference on Very large data bases (VLDB '06), Umeshwar Dayal, Khu-Yong Whang, David Lomet, Gustavo Alonso, Guy Lohman, Martin Kersten, Sang K. Cha, and Young-Kuk Kim (Eds.). 
	 * VLDB Endowment 115-126.
	 * </pre>
	 * Then, it tries to infer a CHARE and an eCHARE from it (the CHARE should be equals to the eCHARE because there are more than one equivalence classes). 
	 * The resulting regular expression should be quite similar to the one of the paper, but with some differences, because our algorithm includes the initial and final state as a part of the expression and removes them 
	 * in a later step (the optimization step), which is not tested here. 
	 * @throws NoSuchRegexCanBeInferredException If the conversion fails (which should not be possible for the tested algorithm).
	 */
	@Test
	public void  testComplexAutomatonToChareConversion() throws NoSuchRegexCanBeInferredException {
		//abccde
		automaton.learn(Lists.newArrayList(initialState,elementA,elementB,elementC,elementC,elementD,elementE,finalState));
		//cccad
		automaton.learn(Lists.newArrayList(initialState,elementC,elementC,elementC,elementA,elementD,finalState));
		//bfegg
		automaton.learn(Lists.newArrayList(initialState,elementB,elementF,elementE,elementG,elementG,finalState));
		//bfehi
		automaton.learn(Lists.newArrayList(initialState,elementB,elementF,elementE,elementH,elementI,finalState));
		
		RegexConvertersFactory regexConvertersFactory = RegexConvertersFactory.getInstance();
		RegexConverter chareConverter = regexConvertersFactory.getRegexConverterInstance(RegexConvertersFactory.CHARE_CONVERTER);
		RegularExpression chare = chareConverter.convertAutomatonToRegex(automaton);
//		System.out.println("CHARE: " + chare.toString()); //DEJAR ESTE
		
		assertTrue(chare instanceof Sequence);
		assertEquals(6,chare.elementCount());
		
		checkChareFromNormal(chare);
		
		RegexConverter eChareConverter = regexConvertersFactory.getRegexConverterInstance(RegexConvertersFactory.ECHARE_CONVERTER);
		RegularExpression eChare = eChareConverter.convertAutomatonToRegex(automaton);
		checkChareFromNormal(eChare);
	}

	/**
	 * Auxiliar method to determine that a CHARE (which may be obtained via the CHARE or the eCHARE converter), 
	 * is the one described at the test method {@link ChareAndEChareCoverterTest#testComplexAutomatonToChareConversion()}
	 * @param chare the chare/echare to check
	 */
	private void checkChareFromNormal(RegularExpression chare) {
		RegularExpression factor1 = chare.getElement(0);
		boolean factor1isCorrectlyWrapped = factor1 instanceof RepeatedAtLeastOnce;
		assertTrue(factor1isCorrectlyWrapped); //Mysteriously, the assert fails if the expression is not extracted in a variable.
		RegularExpression factor1Unwrapped = factor1.getElement(0);
		assertEquals(3,factor1Unwrapped.elementCount());
		assertTrue(factor1Unwrapped.containsElement(elementA));
		assertTrue(factor1Unwrapped.containsElement(elementB));
		assertTrue(factor1Unwrapped.containsElement(elementC));
		
		RegularExpression factor2 = chare.getElement(1);
		boolean factor2isNotWrapped = factor2 instanceof Choice;
		assertTrue(factor2isNotWrapped);
		assertEquals(2,factor2.elementCount());
		assertTrue(factor2.containsElement(elementD));
		assertTrue(factor2.containsElement(elementF));
		
		RegularExpression factorG=new Repeated(elementG);
		assertTrue("Bad CHARE: "+chare.toString(),
				chare.getElement(3).equals(factorG)||
				chare.getElement(5).equals(factorG)||
				chare.getElement(4).equals(factorG));
		RegularExpression factorH=new Optional(elementH);
		RegularExpression factorI=new Optional(elementI);
		assertTrue("Bad CHARE: "+chare.toString(),
				(chare.getElement(3).equals(factorH)&&chare.getElement(4).equals(factorI))||
				(chare.getElement(4).equals(factorH)&&chare.getElement(5).equals(factorI))||
				(chare.getElement(3).equals(factorH)&&chare.getElement(5).equals(factorI)));
	}
	
	/**
	 * This method checks that an example of automaton where an All regular expression should be inferred 
	 * is actually inferred by {@link EChareConverter}.
	 * @throws NoSuchRegexCanBeInferredException specified by {@link RegexConverter#convertAutomatonToRegex(ExtendedAutomaton)} but never used at the tested converters.
	 */
	@Test
	public void testAutomatonToEchareWithAllConversion() throws NoSuchRegexCanBeInferredException{
		//abc
		automaton.learn(Lists.newArrayList(initialState,elementA,elementB,elementC,finalState));
		//bac
		automaton.learn(Lists.newArrayList(initialState,elementB,elementA,elementC,finalState));
		//cab
		automaton.learn(Lists.newArrayList(initialState,elementC,elementA,elementB,finalState));
		
		RegexConvertersFactory regexConvertersFactory = RegexConvertersFactory.getInstance();
		RegexConverter eChareConverter = regexConvertersFactory.getRegexConverterInstance(RegexConvertersFactory.ECHARE_CONVERTER);
		RegularExpression eChare = eChareConverter.convertAutomatonToRegex(automaton);
		
//		System.out.println("ECHARE: "+eChare.toString());
		
		boolean allGenerated = eChare instanceof All;
		assertTrue(allGenerated);
		assertEquals(3, eChare.elementCount());
		assertTrue(eChare.containsElement(elementA));
		assertTrue(eChare.containsElement(elementB));
		assertTrue(eChare.containsElement(elementC));
	}
	
	/**
	 * This method checks that an empty automaton produces the expected result, which is an empty regular expression (at these converters)
	 * @throws NoSuchRegexCanBeInferredException
	 */
	@Test
	public void testEmptyAutomatonToEchareConversion() throws NoSuchRegexCanBeInferredException{
		automaton.learn(Lists.newArrayList(initialState,finalState));
		automaton.learn(Lists.newArrayList(initialState,finalState));
		
		RegexConvertersFactory regexConvertersFactory = RegexConvertersFactory.getInstance();
		RegexConverter eChareConverter = regexConvertersFactory.getRegexConverterInstance(RegexConvertersFactory.ECHARE_CONVERTER);
		RegularExpression eChare = eChareConverter.convertAutomatonToRegex(automaton);
		assertEquals(0,eChare.elementCount()); //The CHARE and eCHARE converter returns the empty regular expression because pseudoelements are not taken into account at equivalence class computation
	}
	
	/**
	 * This method checks that the conversion of an automaton consisting of a single real element (and the initial and final states) returns the expected result: the single element.
	 * @throws NoSuchRegexCanBeInferredException specified by {@link RegexConverter#convertAutomatonToRegex(ExtendedAutomaton)} but never used at the tested converters.
	 */
	@Test
	public void testOnlyOneElementToEchareConversion() throws NoSuchRegexCanBeInferredException{
		automaton.learn(Lists.newArrayList(initialState,elementA,finalState));
				
		RegexConvertersFactory regexConvertersFactory = RegexConvertersFactory.getInstance();
		RegexConverter eChareConverter = regexConvertersFactory.getRegexConverterInstance(RegexConvertersFactory.ECHARE_CONVERTER);
		RegularExpression eChare = eChareConverter.convertAutomatonToRegex(automaton);
		assertEquals(1,eChare.elementCount()); //The CHARE and eCHARE converter returns the empty regular expression because pseudoelements are not taken into account at equivalence class computation
		
		RegularExpression factor1 = eChare.getElement(0);
		boolean factor1isCorrectlyWrapped = factor1 instanceof SchemaElement;
		assertTrue(factor1isCorrectlyWrapped); 
		assertEquals(elementA,factor1);
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
		RegexConverter eChareConverter = regexConvertersFactory.getRegexConverterInstance(RegexConvertersFactory.ECHARE_CONVERTER);
		RegularExpression eChare = eChareConverter.convertAutomatonToRegex(automaton);
		assertEquals(1,eChare.elementCount()); //The CHARE and eCHARE converter returns the empty regular expression because pseudoelements are not taken into account at equivalence class computation
		
		RegularExpression factor1 = eChare.getElement(0);
		boolean factor1isCorrectlyWrapped = factor1 instanceof Optional;
		assertTrue(factor1isCorrectlyWrapped); 
		assertEquals(1,factor1.elementCount());
		assertTrue(factor1.containsElement(elementA));
		RegularExpression factor1Unwrapped = factor1.getElement(0);
		assertEquals(factor1Unwrapped,elementA);
	}

}
