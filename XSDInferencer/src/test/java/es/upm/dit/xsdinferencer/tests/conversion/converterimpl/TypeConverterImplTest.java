package es.upm.dit.xsdinferencer.tests.conversion.converterimpl;

import static es.upm.dit.xsdinferencer.datastructures.Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.conversion.RegexConverter;
import es.upm.dit.xsdinferencer.conversion.RegexOptimizer;
import es.upm.dit.xsdinferencer.conversion.TypeConverter;
import es.upm.dit.xsdinferencer.conversion.converterimpl.TypeConverterImpl;
import es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex.RegexConvertersFactory;
import es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex.exceptions.NoSuchRegexCanBeInferredException;
import es.upm.dit.xsdinferencer.conversion.converterimpl.optimization.RegexOptimizersFactory;
import es.upm.dit.xsdinferencer.datastructures.All;
import es.upm.dit.xsdinferencer.datastructures.Choice;
import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.EmptyRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.Optional;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Repeated;
import es.upm.dit.xsdinferencer.datastructures.RepeatedAtLeastOnce;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.Sequence;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.statistics.Statistics;

/**
 * Test class for the whole type converter module
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class TypeConverterImplTest {

	//Fields for testing
	
	/**
	 * Schema used at the test scenario
	 */
	private Schema schema;
	
	/**
	 * Root element of the type complexTypeRoot
	 */
	private SchemaElement elementRoot;
	
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
	 * Element F of the regular expression to optimize
	 */
	private SchemaElement elementF;
	
	/**
	 * Element G of the regular expression to optimize
	 */
	private SchemaElement elementG;
	
	/**
	 * Element H of the regular expression to optimize
	 */
	private SchemaElement elementH;
	
	/**
	 * Element I of the regular expression to optimize
	 */
	private SchemaElement elementI;
	
	/**
	 * Complex type of the root element, whose automaton will be converted for testing.
	 */
	private ComplexType complexTypeRoot;
	
	/**
	 * Complex type of element A.
	 */
	private ComplexType complexTypeElementA;
	
	/**
	 * Complex type of element B.
	 */
	private ComplexType complexTypeElementB;
	
	/**
	 * Complex type of element C.
	 */
	private ComplexType complexTypeElementC;
	
	/**
	 * Complex type of element D.
	 */
	private ComplexType complexTypeElementD;
	
	/**
	 * Complex type of element E.
	 */
	private ComplexType complexTypeElementE;
	
	/**
	 * Complex type of element F.
	 */
	private ComplexType complexTypeElementF;
	
	/**
	 * Complex type of element G.
	 */
	private ComplexType complexTypeElementG;
	
	/**
	 * Complex type of element H.
	 */
	private ComplexType complexTypeElementH;
	
	/**
	 * Complex type of element I.
	 */
	private ComplexType complexTypeElementI;
	
	/**
	 * Initial state of automatons
	 */
	private SchemaElement initialState;
	
	/**
	 * Final state of automatons
	 */
	private SchemaElement finalState;
	
	/**
	 * Automaton of complexTypeRoot
	 */
	private ExtendedAutomaton automatonRoot;
	
	/**
	 * Inference configuration of the test scenario
	 */
	private XSDInferenceConfiguration configuration;
	
	/**
	 * RegexConvertersFactory used
	 */
	private RegexConvertersFactory regexConvertersFactory;
	
	/**
	 * RegexOptimizersFactory used
	 */
	private RegexOptimizersFactory regexOptimizersFactory;
	
	/**
	 * Mock that always returns false
	 */
	private RegexOptimizer mockFalseRegexOptimizer;
	
	/**
	 * Mock that returns true once and always false after that
	 */
	private RegexOptimizer mockTrueOnceRegexOptimizer;

	/**
	 * Mock that returns true twice and always false after that
	 */
	private RegexOptimizer mockTrueTwiceRegexOptimizer;
	
	/**
	 * Method that builds the complex types of the scenario. The automatonRoot should be built on each test method.
	 */
	private void buildComplexTypes(){
		automatonRoot = new ExtendedAutomaton();
		complexTypeRoot = new ComplexType("_root",automatonRoot,new SimpleType("root"),new ArrayList<SchemaAttribute>());
		
		complexTypeElementA = new ComplexType("_root-_A",new ExtendedAutomaton(),new SimpleType("_root-_A"),new ArrayList<SchemaAttribute>());
		complexTypeElementB = new ComplexType("_root-_B",new ExtendedAutomaton(),new SimpleType("_root-_B"),new ArrayList<SchemaAttribute>());
		complexTypeElementC = new ComplexType("_root-_C",new ExtendedAutomaton(),new SimpleType("_root-_C"),new ArrayList<SchemaAttribute>());
		complexTypeElementD = new ComplexType("_root-_D",new ExtendedAutomaton(),new SimpleType("_root-_D"),new ArrayList<SchemaAttribute>());
		complexTypeElementE = new ComplexType("_root-_E",new ExtendedAutomaton(),new SimpleType("_root-_E"),new ArrayList<SchemaAttribute>());
		complexTypeElementF = new ComplexType("_root-_F",new ExtendedAutomaton(),new SimpleType("_root-_F"),new ArrayList<SchemaAttribute>());
		complexTypeElementG = new ComplexType("_root-_G",new ExtendedAutomaton(),new SimpleType("_root-_G"),new ArrayList<SchemaAttribute>());
		complexTypeElementH = new ComplexType("_root-_H",new ExtendedAutomaton(),new SimpleType("_root-_H"),new ArrayList<SchemaAttribute>());
		complexTypeElementI = new ComplexType("_root-_I",new ExtendedAutomaton(),new SimpleType("_root-_I"),new ArrayList<SchemaAttribute>());
	}
	
	/**
	 * Method that builds the SchemaElement objects of the testing scenario
	 */
	private void buildSchemaElements(){
		initialState=new SchemaElement("initial",DEFAULT_PSEUDOELEMENTS_NAMESPACE,null);
		finalState=new SchemaElement("final",DEFAULT_PSEUDOELEMENTS_NAMESPACE,null);
		automatonRoot.setInitialState(initialState);
		automatonRoot.setFinalState(finalState);
		
		elementRoot=new SchemaElement("root","",complexTypeRoot);
		elementRoot.setValidRoot(true);
		
		elementA=new SchemaElement("A","",complexTypeElementA);
		elementB=new SchemaElement("B","",complexTypeElementB);
		elementC=new SchemaElement("C","",complexTypeElementC);
		elementD=new SchemaElement("D","",complexTypeElementD);
		elementE=new SchemaElement("E","",complexTypeElementE);
		elementF=new SchemaElement("F","",complexTypeElementF);
		elementG=new SchemaElement("G","",complexTypeElementG);
		elementH=new SchemaElement("H","",complexTypeElementH);
		elementI=new SchemaElement("I","",complexTypeElementI);
		
	}
	
	/**
	 * Method that builds the Schema object of the scenario.
	 */
	private void buildSchema(){
		NavigableMap<String,SortedSet<String>> prefixNamespaceMapping = new TreeMap<String, SortedSet<String>>();
		prefixNamespaceMapping.put("", Sets.newTreeSet(Collections.singleton("")));
		
		Map<String, ComplexType> complexTypes = new HashMap<>(10);
		complexTypes.put("_root", complexTypeRoot);
		complexTypes.put("_root-_A", complexTypeElementA);
		complexTypes.put("_root-_B", complexTypeElementB);
		complexTypes.put("_root-_C", complexTypeElementC);
		complexTypes.put("_root-_D", complexTypeElementD);
		complexTypes.put("_root-_E", complexTypeElementE);
		complexTypes.put("_root-_F", complexTypeElementF);
		complexTypes.put("_root-_G", complexTypeElementG);
		complexTypes.put("_root-_H", complexTypeElementH);
		complexTypes.put("_root-_I", complexTypeElementI);
		
		Table<String, String, SchemaElement> elements = HashBasedTable.create(1, 10);
		elements.put("","_root", elementRoot);
		elements.put("","_root-A", elementA);
		elements.put("","_root-B", elementB);
		elements.put("","_root-C", elementC);
		elements.put("","_root-D", elementD);
		elements.put("","_root-E", elementE);
		elements.put("","_root-F", elementF);
		elements.put("","_root-G", elementG);
		elements.put("","_root-H", elementH);
		elements.put("","_root-I", elementI);
		
		Table<String, String, SchemaAttribute> attributes = ImmutableTable.of();
		
		Map<String, SimpleType> simpleTypes = new HashMap<>(10);
		simpleTypes.put("_root", complexTypeRoot.getTextSimpleType());
		simpleTypes.put("_root-_A", complexTypeElementA.getTextSimpleType());
		simpleTypes.put("_root-_B", complexTypeElementB.getTextSimpleType());
		simpleTypes.put("_root-_C", complexTypeElementC.getTextSimpleType());
		simpleTypes.put("_root-_D", complexTypeElementD.getTextSimpleType());
		simpleTypes.put("_root-_E", complexTypeElementE.getTextSimpleType());
		simpleTypes.put("_root-_F", complexTypeElementF.getTextSimpleType());
		simpleTypes.put("_root-_G", complexTypeElementG.getTextSimpleType());
		simpleTypes.put("_root-_H", complexTypeElementH.getTextSimpleType());
		simpleTypes.put("_root-_I", complexTypeElementI.getTextSimpleType());
		
		schema = new Schema(prefixNamespaceMapping, elements, attributes, complexTypes, simpleTypes, new Statistics(6));
	}
	
	@Before
	public void setUp() throws Exception {
		buildComplexTypes();
		buildSchemaElements();
		buildSchema();
		
		regexConvertersFactory = mock(RegexConvertersFactory.class);
		regexOptimizersFactory = mock(RegexOptimizersFactory.class);
		
		configuration = mock(XSDInferenceConfiguration.class);
//		when(configuration.getOptimizers()).thenReturn(ImmutableList.<RegexOptimizer>of(
//				regexOptimizersFactory.getRegexOptimizerInstance(RegexOptimizersFactory.VALUE_OPTIMIZERS_CHOICE),
//				regexOptimizersFactory.getRegexOptimizerInstance(RegexOptimizersFactory.VALUE_OPTIMIZERS_EMPTY),
//				regexOptimizersFactory.getRegexOptimizerInstance(RegexOptimizersFactory.VALUE_OPTIMIZERS_EMPTYCHILD),
//				regexOptimizersFactory.getRegexOptimizerInstance(RegexOptimizersFactory.VALUE_OPTIMIZERS_SEQUENCE),
//				regexOptimizersFactory.getRegexOptimizerInstance(RegexOptimizersFactory.VALUE_OPTIMIZERS_SINGLETON),
//				regexOptimizersFactory.getRegexOptimizerInstance(RegexOptimizersFactory.VALUE_OPTIMIZERS_SINGULAR_REGULAR_EXPRESSION)));
		
	}	
//	private RegularExpression buildNonOptimizedComplexSore(){
//		Choice choiceCA = new Choice(elementC,elementA);
//		Optional optionalB = new Optional(elementB);
//		Sequence sequenceOptionalBChoiceCA = new Sequence(optionalB, choiceCA);
//		RepeatedAtLeastOnce repeatedAtLeastOnceSequenceOptionalBChoiceCA = new RepeatedAtLeastOnce(sequenceOptionalBChoiceCA);
//		Sequence sequenceRepeatedAtLeastOnceD = new Sequence(repeatedAtLeastOnceSequenceOptionalBChoiceCA,elementD);
//		RepeatedAtLeastOnce repeatedAtLeastOnceSequence = new RepeatedAtLeastOnce(sequenceRepeatedAtLeastOnceD);
//		Sequence eFinal = new Sequence(elementE,finalState);
//		Sequence sequenceRepeatedAtLeastOnceEFinal = new Sequence(repeatedAtLeastOnceSequence,eFinal);
//		RegularExpression result = new Sequence(initialState, sequenceRepeatedAtLeastOnceEFinal);
//		return result;
//	}
	
	/**
	 * This method builds the complex SORE used at {@link TypeConverterImplTest#testComplexAutomatonToSoreConversion()}
	 * @return the built SORE
	 */
	private RegularExpression buildOptimizedComplexSore(){
		Choice choiceCA = new Choice(elementC,elementA);
		Optional optionalB = new Optional(elementB);
		Sequence sequenceOptionalBChoiceCA = new Sequence(optionalB, choiceCA);
		RepeatedAtLeastOnce repeatedAtLeastOnceSequenceOptionalBChoiceCA = new RepeatedAtLeastOnce(sequenceOptionalBChoiceCA);
		Sequence sequenceRepeatedAtLeastOnceD = new Sequence(repeatedAtLeastOnceSequenceOptionalBChoiceCA,elementD);
		RepeatedAtLeastOnce repeatedAtLeastOnceSequence = new RepeatedAtLeastOnce(sequenceRepeatedAtLeastOnceD);
		RegularExpression result = new Sequence(repeatedAtLeastOnceSequence,elementE);
		return result;
	}
	
	/**
	 * This method builds the CHARE that results when the converter falls back to the CHARE converter when the 
	 * SORE converter fails at {@link TypeConverterImplTest#testMustFallbackToChare()}
	 * @return a fallback CHARE
	 */
	private RegularExpression buildOptimizedFallbackChare() {
		return new RepeatedAtLeastOnce(new Choice(elementA,elementB,elementC));
	}

	/**
	 * This method builds an already optimized quite complex CHARE to be used by the method 
	 * {@link TypeConverterImplTest#testComplexToChareConversion()}
	 * @return a complex CHARE
	 */
	private RegularExpression buildOptimizedComplexChare(){
		RegularExpression factor1 = new RepeatedAtLeastOnce(new Choice(elementA,elementB,elementC));
		RegularExpression factor2 = new Choice(elementD,elementF);
		RegularExpression factor3 = new Optional(elementE);
		RegularExpression factor4 = new Repeated(elementG);
		RegularExpression factor5 = new Optional(elementH);
		RegularExpression factor6 = new Optional(elementI);
		RegularExpression result = new Sequence(factor1,factor2,factor3,factor4,factor5,factor6);
		return result;
	}
	
	/**
	 * This method builds an eCHARE with an All regular expression, to be used at the method 
	 * {@link TypeConverterImplTest#testToEchareWithAllConversion()}
	 * @return an eCHARE with All
	 */
	private RegularExpression buildEchareWithAll(){
		return new All(elementA,elementB,elementC);
	}
	
	/**
	 * This test builds the example automaton from the figure 2 of the following paper:  
	 * <pre>
	 * Geert Jan Bex, Frank Neven, Thomas Schwentick, and Karl Tuyls. 2006. 
	 * Inference of concise DTDs from XML data. 
	 * In Proceedings of the 32nd international conference on Very large data bases (VLDB '06), Umeshwar Dayal, Khu-Yong Whang, David Lomet, Gustavo Alonso, Guy Lohman, Martin Kersten, Sang K. Cha, and Young-Kuk Kim (Eds.). 
	 * VLDB Endowment 115-126.
	 * </pre>
	 * Then, it tries to infer SOREs from it. The resulting regular expression should be quite 
	 * similar to the ones of the paper (there are many possible results depending on which rewrite steps 
	 * are applied first, as those steps are applied in a quite random order). 
	 * @throws NoSuchRegexCanBeInferredException If the conversion fails (which should not be possible for this automaton).
	 */
	@Test
	public void testComplexAutomatonToSoreConversion() throws NoSuchRegexCanBeInferredException {
		//bacacdacde
		automatonRoot.learn(Lists.newArrayList(initialState,elementB,elementA,elementC,elementA,elementC,elementD,elementA,elementC,elementD,elementE,finalState));
		//cbacdbacde
		automatonRoot.learn(Lists.newArrayList(initialState,elementC,elementB,elementA,elementC,elementD,elementB,elementA,elementC,elementD,elementE,finalState));
		//abccaadcde
		automatonRoot.learn(Lists.newArrayList(initialState,elementA,elementB,elementC,elementC,elementA,elementA,elementD,elementC,elementD,elementE,finalState));

		when(configuration.getAvoidSORE()).thenReturn(false);
		when(configuration.getTryECHARE()).thenReturn(true);
		
		//Let's define mocks
		
		RegexConverter mockedTypeConverter = mock(RegexConverter.class);
		RegularExpression expectedRegex = buildOptimizedComplexSore();
		RegularExpression emptyRegularExpression = new EmptyRegularExpression();
		Optional wrappedEmpty = new Optional(emptyRegularExpression);
		Optional wrappedExpectedRegex = new Optional(expectedRegex);
		
		mockFalseRegexOptimizer=mock(RegexOptimizer.class);
		when(mockFalseRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(false);
		
		mockTrueOnceRegexOptimizer=mock(RegexOptimizer.class);
		when(mockTrueOnceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,false);
		
		mockTrueTwiceRegexOptimizer=mock(RegexOptimizer.class);
		when(mockTrueTwiceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,true,false);
		
		List<RegexOptimizer> optimizers = Lists.newArrayList(mockFalseRegexOptimizer,mockTrueOnceRegexOptimizer,mockTrueTwiceRegexOptimizer,mockFalseRegexOptimizer);
		when(configuration.getOptimizers()).thenReturn(optimizers);
				
		when(mockedTypeConverter.convertAutomatonToRegex(any(ExtendedAutomaton.class))).thenReturn(emptyRegularExpression);
		when(mockedTypeConverter.convertAutomatonToRegex(automatonRoot)).thenReturn(expectedRegex);
		
		regexConvertersFactory = mock(RegexConvertersFactory.class);
		when(regexConvertersFactory.getRegexConverterInstance(eq(false), anyBoolean())).thenReturn(mockedTypeConverter);

		TypeConverter converter = new TypeConverterImpl();
		converter.converTypes(schema, configuration, regexConvertersFactory, regexOptimizersFactory);
		RegularExpression resultingRegexp = schema.getComplexTypes().get("_root").getRegularExpression();
		assertNotNull(resultingRegexp);
		assertSame(expectedRegex,resultingRegexp);
		
		verify(mockTrueOnceRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockTrueTwiceRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockFalseRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockTrueOnceRegexOptimizer, times(3)).optimizeRegex(wrappedExpectedRegex);
		verify(mockTrueTwiceRegexOptimizer, times(3)).optimizeRegex(wrappedExpectedRegex);
		verify(mockFalseRegexOptimizer, times(6)).optimizeRegex(wrappedExpectedRegex);
		
		
//		//System.out.println("SORE: "+resultingRegexp.toString());
//		assertTrue(resultingRegexp.toString().equals("(((:B? (:A|:C))+ :D)+ :E)")||
//				resultingRegexp.toString().equals("(((:B? (:C|:A))+ :D)+ :E)")||
//				resultingRegexp.toString().equals("(((:B? (:A|:C)+)+ :D)+ :E)")||
//				resultingRegexp.toString().equals("(((:B? (:C|:A)+)+ :D)+ :E)"));
//	//			//There are many valid regular expressions, depending on some more or less random iteration orders.
//	//			assertTrue(resultingRegexp.toString().equals("(http://dit.upm.es/xsdinferencer/pseudoelements:initial (((((:b? (:c|:a)))+ :d))+ (:e http://dit.upm.es/xsdinferencer/pseudoelements:final)))") ||
//	//					resultingRegexp.toString().equals("(http://dit.upm.es/xsdinferencer/pseudoelements:initial (((((:b? (:a|:c)))+ :d))+ (:e http://dit.upm.es/xsdinferencer/pseudoelements:final)))") ||
//	//					resultingRegexp.toString().equals("(http://dit.upm.es/xsdinferencer/pseudoelements:initial ((((:b? (:c|:a)))+ :d))+ (:e http://dit.upm.es/xsdinferencer/pseudoelements:final))")||
//	//					resultingRegexp.toString().equals("(http://dit.upm.es/xsdinferencer/pseudoelements:initial ((((:b? (:a|:c)))+ :d))+ (:e http://dit.upm.es/xsdinferencer/pseudoelements:final))"));
	}
	
	
	/**
	 * This method checks that the TypeConverter fallbacks properly to a CHARE or eCHARE converter if the SORE conversion fails 
	 * @throws NoSuchRegexCanBeInferredException specified by {@link TypeConverter#converTypes(Schema, XSDInferenceConfiguration)} but not used
	 */
	@Test
	public void testMustFallbackToChare() throws NoSuchRegexCanBeInferredException{
		//a
		automatonRoot.learn(Lists.newArrayList(initialState,elementA,finalState));
		//abca
		automatonRoot.learn(Lists.newArrayList(initialState,elementA,elementB,elementC,elementA,finalState));
		when(configuration.getAvoidSORE()).thenReturn(false);
		when(configuration.getTryECHARE()).thenReturn(false);
		
		//Let's define mocks
		RegexConverter mockedFailingTypeConverter = mock(RegexConverter.class);
		RegexConverter mockedTypeConverter = mock(RegexConverter.class);
		
		RegularExpression expectedRegex = buildOptimizedFallbackChare();
		RegularExpression emptyRegularExpression = new EmptyRegularExpression();
		Optional wrappedEmpty = new Optional(emptyRegularExpression);
		Optional wrappedExpectedRegex = new Optional(expectedRegex);
		
		mockFalseRegexOptimizer=mock(RegexOptimizer.class);
		when(mockFalseRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(false);
		
		mockTrueOnceRegexOptimizer=mock(RegexOptimizer.class);
		when(mockTrueOnceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,false);
		
		mockTrueTwiceRegexOptimizer=mock(RegexOptimizer.class);
		when(mockTrueTwiceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,true,false);
		
		List<RegexOptimizer> optimizers = Lists.newArrayList(mockFalseRegexOptimizer,mockTrueOnceRegexOptimizer,mockTrueTwiceRegexOptimizer,mockFalseRegexOptimizer);
		when(configuration.getOptimizers()).thenReturn(optimizers);
				
		when(mockedTypeConverter.convertAutomatonToRegex(any(ExtendedAutomaton.class))).thenReturn(emptyRegularExpression);
		when(mockedTypeConverter.convertAutomatonToRegex(automatonRoot)).thenReturn(expectedRegex);
		
		when(mockedFailingTypeConverter.convertAutomatonToRegex(any(ExtendedAutomaton.class))).thenReturn(emptyRegularExpression);
		when(mockedFailingTypeConverter.convertAutomatonToRegex(automatonRoot)).thenThrow(new NoSuchRegexCanBeInferredException());
		
		regexConvertersFactory = mock(RegexConvertersFactory.class);
		when(regexConvertersFactory.getRegexConverterInstance(eq(false), anyBoolean())).thenReturn(mockedFailingTypeConverter);
		when(regexConvertersFactory.getRegexConverterInstance(eq(true), eq(false))).thenReturn(mockedTypeConverter);
		
		TypeConverter converter = new TypeConverterImpl();
		converter.converTypes(schema, configuration, regexConvertersFactory, regexOptimizersFactory);
		RegularExpression resultingRegexp = schema.getComplexTypes().get("_root").getRegularExpression();
		boolean resultingRegexpIsRepeatedAtLeastOnce = resultingRegexp instanceof RepeatedAtLeastOnce;
		assertTrue(resultingRegexpIsRepeatedAtLeastOnce);
		assertEquals(1,resultingRegexp.elementCount());
		RegularExpression resultingRegexp1 = resultingRegexp.getElement(0);
		boolean resultingRegexp1IsChoice = resultingRegexp1 instanceof Choice;
		assertTrue(resultingRegexp1IsChoice);
		assertEquals(3, resultingRegexp1.elementCount());
		assertTrue(resultingRegexp1.containsElement(elementA));
		assertTrue(resultingRegexp1.containsElement(elementB));
		assertTrue(resultingRegexp1.containsElement(elementC));
		
		verify(mockedFailingTypeConverter).convertAutomatonToRegex(automatonRoot);
		verify(mockedTypeConverter).convertAutomatonToRegex(automatonRoot);
		
		verify(mockTrueOnceRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockTrueTwiceRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockFalseRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockTrueOnceRegexOptimizer, times(3)).optimizeRegex(wrappedExpectedRegex);
		verify(mockTrueTwiceRegexOptimizer, times(3)).optimizeRegex(wrappedExpectedRegex);
		verify(mockFalseRegexOptimizer, times(6)).optimizeRegex(wrappedExpectedRegex);
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
	public void  testComplexToChareConversion() throws NoSuchRegexCanBeInferredException {
		//abccde
		automatonRoot.learn(Lists.newArrayList(initialState,elementA,elementB,elementC,elementC,elementD,elementE,finalState));
		//cccad
		automatonRoot.learn(Lists.newArrayList(initialState,elementC,elementC,elementC,elementA,elementD,finalState));
		//bfegg
		automatonRoot.learn(Lists.newArrayList(initialState,elementB,elementF,elementE,elementG,elementG,finalState));
		//bfehi
		automatonRoot.learn(Lists.newArrayList(initialState,elementB,elementF,elementE,elementH,elementI,finalState));
		
		
//		RegexConverter resultingRegexpConverter = new ChareConverter();
//		RegularExpression resultingRegexp = resultingRegexpConverter.convertAutomatonToRegex(automatonRoot);
		
		when(configuration.getAvoidSORE()).thenReturn(true);
		when(configuration.getTryECHARE()).thenReturn(false);
		
		//Let's define mocks
		
		RegexConverter mockedTypeConverter = mock(RegexConverter.class);
		RegularExpression expectedRegex = buildOptimizedComplexChare();
		RegularExpression emptyRegularExpression = new EmptyRegularExpression();
		Optional wrappedEmpty = new Optional(emptyRegularExpression);
		Optional wrappedExpectedRegex = new Optional(expectedRegex);
		
		mockFalseRegexOptimizer=mock(RegexOptimizer.class);
		when(mockFalseRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(false);
		
		mockTrueOnceRegexOptimizer=mock(RegexOptimizer.class);
		when(mockTrueOnceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,false);
		
		mockTrueTwiceRegexOptimizer=mock(RegexOptimizer.class);
		when(mockTrueTwiceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,true,false);
		
		List<RegexOptimizer> optimizers = Lists.newArrayList(mockFalseRegexOptimizer,mockTrueOnceRegexOptimizer,mockTrueTwiceRegexOptimizer,mockFalseRegexOptimizer);
		when(configuration.getOptimizers()).thenReturn(optimizers);
				
		when(mockedTypeConverter.convertAutomatonToRegex(any(ExtendedAutomaton.class))).thenReturn(emptyRegularExpression);
		when(mockedTypeConverter.convertAutomatonToRegex(automatonRoot)).thenReturn(expectedRegex);
		
		regexConvertersFactory = mock(RegexConvertersFactory.class);
		when(regexConvertersFactory.getRegexConverterInstance(eq(true), anyBoolean())).thenReturn(mockedTypeConverter);
		
		TypeConverter converter = new TypeConverterImpl();
		converter.converTypes(schema, configuration, regexConvertersFactory, regexOptimizersFactory);
		RegularExpression resultingChare = schema.getComplexTypes().get("_root").getRegularExpression();
		
		//System.out.println("CHARE: " + resultingChare.toString());
		
		assertTrue(resultingChare instanceof Sequence);
		assertEquals(6,resultingChare.elementCount());
		
		RegularExpression factor1 = resultingChare.getElement(0);
		boolean factor1isCorrectlyWrapped = factor1 instanceof RepeatedAtLeastOnce;
		assertTrue(factor1isCorrectlyWrapped); //Mysteriously, the assert fails if the expression is not extracted in a variable.
		RegularExpression factor1Unwrapped = factor1.getElement(0);
		assertEquals(3,factor1Unwrapped.elementCount());
		assertTrue(factor1Unwrapped.containsElement(elementA));
		assertTrue(factor1Unwrapped.containsElement(elementB));
		assertTrue(factor1Unwrapped.containsElement(elementC));
		
		RegularExpression factor2 = resultingChare.getElement(1);
		boolean factor2isNotWrapped = factor2 instanceof Choice;
		assertTrue(factor2isNotWrapped);
		assertEquals(2,factor2.elementCount());
		assertTrue(factor2.containsElement(elementD));
		assertTrue(factor2.containsElement(elementF));
		
		RegularExpression factor3 = resultingChare.getElement(2);
		boolean factor3isCorrectlyWrapped = factor3 instanceof Optional;
		assertTrue(factor3isCorrectlyWrapped); //Mysteriously, the assert fails if the expression is not extracted in a variable.
		RegularExpression factor3Unwrapped = factor3.getElement(0);
		assertEquals(1,factor3Unwrapped.elementCount());
		assertTrue(factor3Unwrapped.equals(elementE));
		
		RegularExpression factor4 = resultingChare.getElement(3);
		boolean factor4isCorrectlyWrapped = factor4 instanceof Repeated;
		assertTrue(factor4isCorrectlyWrapped); //Mysteriously, the assert fails if the expression is not extracted in a variable.
		RegularExpression factor4Unwrapped = factor4.getElement(0);
		assertEquals(1,factor4Unwrapped.elementCount());
		assertTrue(factor4Unwrapped.equals(elementG));
		
		RegularExpression factor5 = resultingChare.getElement(4);
		boolean factor5isCorrectlyWrapped = factor5 instanceof Optional;
		assertTrue(factor5isCorrectlyWrapped); //Mysteriously, the assert fails if the expression is not extracted in a variable.
		RegularExpression factor5Unwrapped = factor5.getElement(0);
		assertEquals(1,factor5Unwrapped.elementCount());
		assertTrue(factor5Unwrapped.equals(elementH));
		
		RegularExpression factor6 = resultingChare.getElement(5);
		boolean factor6isCorrectlyWrapped = factor6 instanceof Optional;
		assertTrue(factor6isCorrectlyWrapped); //Mysteriously, the assert fails if the expression is not extracted in a variable.
		RegularExpression factor6Unwrapped = factor6.getElement(0);
		assertEquals(1,factor6Unwrapped.elementCount());
		assertTrue(factor6Unwrapped.equals(elementI));
		
		verify(mockTrueOnceRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockTrueTwiceRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockFalseRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockTrueOnceRegexOptimizer, times(3)).optimizeRegex(wrappedExpectedRegex);
		verify(mockTrueTwiceRegexOptimizer, times(3)).optimizeRegex(wrappedExpectedRegex);
		verify(mockFalseRegexOptimizer, times(6)).optimizeRegex(wrappedExpectedRegex);
		
//		RegexConverter eChareConverter = new EChareConverter();
//		RegularExpression resultingEChare = eChareConverter.convertAutomatonToRegex(automatonRoot);
		when(configuration.getTryECHARE()).thenReturn(true);
		converter.converTypes(schema, configuration, regexConvertersFactory, regexOptimizersFactory);
		RegularExpression resultingEChare = schema.getComplexTypes().get("_root").getRegularExpression();
		assertEquals(resultingChare,resultingEChare);
	}
	
	/**
	 * This method checks that an All is inferred in an example where it should be.
	 * @throws NoSuchRegexCanBeInferredException specified by {@link TypeConverter#converTypes(Schema, XSDInferenceConfiguration)} but not used
	 */
	@Test
	public void testToEchareWithAllConversion() throws NoSuchRegexCanBeInferredException{
		//abc
		automatonRoot.learn(Lists.newArrayList(initialState,elementA,elementB,elementC,finalState));
		//bac
		automatonRoot.learn(Lists.newArrayList(initialState,elementB,elementA,elementC,finalState));
		//cab
		automatonRoot.learn(Lists.newArrayList(initialState,elementC,elementA,elementB,finalState));
		
//		RegexConverter eChareConverter = new EChareConverter();
//		RegularExpression resultingEChare = eChareConverter.convertAutomatonToRegex(automatonRoot);
		when(configuration.getAvoidSORE()).thenReturn(true);
		when(configuration.getTryECHARE()).thenReturn(true);
		
		//Let's define mocks
		
		RegexConverter mockedTypeConverter = mock(RegexConverter.class);
		RegularExpression expectedRegex = buildEchareWithAll();
		RegularExpression emptyRegularExpression = new EmptyRegularExpression();
		Optional wrappedEmpty = new Optional(emptyRegularExpression);
		Optional wrappedExpectedRegex = new Optional(expectedRegex);
		
		mockFalseRegexOptimizer=mock(RegexOptimizer.class);
		when(mockFalseRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(false);
		
		mockTrueOnceRegexOptimizer=mock(RegexOptimizer.class);
		when(mockTrueOnceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,false);
		
		mockTrueTwiceRegexOptimizer=mock(RegexOptimizer.class);
		when(mockTrueTwiceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,true,false);
		
		List<RegexOptimizer> optimizers = Lists.newArrayList(mockFalseRegexOptimizer,mockTrueOnceRegexOptimizer,mockTrueTwiceRegexOptimizer,mockFalseRegexOptimizer);
		when(configuration.getOptimizers()).thenReturn(optimizers);
				
		when(mockedTypeConverter.convertAutomatonToRegex(any(ExtendedAutomaton.class))).thenReturn(emptyRegularExpression);
		when(mockedTypeConverter.convertAutomatonToRegex(automatonRoot)).thenReturn(expectedRegex);
		
		regexConvertersFactory = mock(RegexConvertersFactory.class);
		when(regexConvertersFactory.getRegexConverterInstance(eq(true), anyBoolean())).thenReturn(mockedTypeConverter);
		
		TypeConverter converter = new TypeConverterImpl();
		converter.converTypes(schema, configuration, regexConvertersFactory, regexOptimizersFactory);
		RegularExpression resultingEChare = schema.getComplexTypes().get("_root").getRegularExpression();
		
//		System.out.println("ECHARE: "+resultingEChare.toString());
		
		boolean allGenerated = resultingEChare instanceof All;
		assertTrue(allGenerated);
		assertEquals(3, resultingEChare.elementCount());
		assertTrue(resultingEChare.containsElement(elementA));
		assertTrue(resultingEChare.containsElement(elementB));
		assertTrue(resultingEChare.containsElement(elementC));
		
		verify(mockTrueOnceRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockTrueTwiceRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockFalseRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockTrueOnceRegexOptimizer, times(3)).optimizeRegex(wrappedExpectedRegex);
		verify(mockTrueTwiceRegexOptimizer, times(3)).optimizeRegex(wrappedExpectedRegex);
		verify(mockFalseRegexOptimizer, times(6)).optimizeRegex(wrappedExpectedRegex);
		
	}
	
	/**
	 * This method checks that an empty regular expression is inferred correctly when avoidSORE is false.
	 * @throws NoSuchRegexCanBeInferredException specified by {@link TypeConverter#converTypes(Schema, XSDInferenceConfiguration)} but not used
	 */
	@Test
	public void testEmptyToSoreConversion() throws NoSuchRegexCanBeInferredException{
		automatonRoot.learn(Lists.newArrayList(initialState,finalState));
		automatonRoot.learn(Lists.newArrayList(initialState,finalState));
		
		when(configuration.getAvoidSORE()).thenReturn(false);
		when(configuration.getTryECHARE()).thenReturn(true);
		
		RegexConverter mockedTypeConverter = mock(RegexConverter.class);
//		RegularExpression expectedRegex = buildEchareWithAll();
		RegularExpression emptyRegularExpression = new EmptyRegularExpression();
		Optional wrappedEmpty = new Optional(emptyRegularExpression);
//		Optional wrappedExpectedRegex = new Optional(expectedRegex);
		
		mockFalseRegexOptimizer=mock(RegexOptimizer.class);
		when(mockFalseRegexOptimizer.optimizeRegex(wrappedEmpty)).thenReturn(false);
		
//		mockTrueOnceRegexOptimizer=mock(RegexOptimizer.class);
//		when(mockTrueOnceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,false);
//		
//		mockTrueTwiceRegexOptimizer=mock(RegexOptimizer.class);
//		when(mockTrueTwiceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,true,false);
		
		List<RegexOptimizer> optimizers = Lists.newArrayList(mockFalseRegexOptimizer,mockFalseRegexOptimizer,mockFalseRegexOptimizer,mockFalseRegexOptimizer);
		when(configuration.getOptimizers()).thenReturn(optimizers);
				
		when(mockedTypeConverter.convertAutomatonToRegex(any(ExtendedAutomaton.class))).thenReturn(emptyRegularExpression);
		
		regexConvertersFactory = mock(RegexConvertersFactory.class);
		when(regexConvertersFactory.getRegexConverterInstance(eq(false), anyBoolean())).thenReturn(mockedTypeConverter);
		
		TypeConverter converter = new TypeConverterImpl();
		converter.converTypes(schema, configuration, regexConvertersFactory, regexOptimizersFactory);
		RegularExpression resultingRegexp = schema.getComplexTypes().get("_root").getRegularExpression();
		
//		System.out.println("ECHARE: "+resultingRegexp.toString());
		
		boolean emptyGenerated = resultingRegexp instanceof EmptyRegularExpression;
		assertTrue(emptyGenerated);
		assertEquals(new EmptyRegularExpression(),resultingRegexp);
		
		verify(mockFalseRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
	}
	
	/**
	 * This method checks that an empty regular expression is inferred correctly when avoidSORE is true and tryECHARE is true.
	 * @throws NoSuchRegexCanBeInferredException specified by {@link TypeConverter#converTypes(Schema, XSDInferenceConfiguration)} but not used
	 */
	@Test
	public void testEmptyToEChareConversion() throws NoSuchRegexCanBeInferredException{
		automatonRoot.learn(Lists.newArrayList(initialState,finalState));
		automatonRoot.learn(Lists.newArrayList(initialState,finalState));
		
		when(configuration.getAvoidSORE()).thenReturn(true);
		when(configuration.getTryECHARE()).thenReturn(true);
		
		RegexConverter mockedTypeConverter = mock(RegexConverter.class);
//		RegularExpression expectedRegex = buildEchareWithAll();
		RegularExpression emptyRegularExpression = new EmptyRegularExpression();
		Optional wrappedEmpty = new Optional(emptyRegularExpression);
//		Optional wrappedExpectedRegex = new Optional(expectedRegex);
		
		mockFalseRegexOptimizer=mock(RegexOptimizer.class);
		when(mockFalseRegexOptimizer.optimizeRegex(wrappedEmpty)).thenReturn(false);
		
//		mockTrueOnceRegexOptimizer=mock(RegexOptimizer.class);
//		when(mockTrueOnceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,false);
//		
//		mockTrueTwiceRegexOptimizer=mock(RegexOptimizer.class);
//		when(mockTrueTwiceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,true,false);
		
		List<RegexOptimizer> optimizers = Lists.newArrayList(mockFalseRegexOptimizer,mockFalseRegexOptimizer,mockFalseRegexOptimizer,mockFalseRegexOptimizer);
		when(configuration.getOptimizers()).thenReturn(optimizers);
				
		when(mockedTypeConverter.convertAutomatonToRegex(any(ExtendedAutomaton.class))).thenReturn(emptyRegularExpression);
		
		regexConvertersFactory = mock(RegexConvertersFactory.class);
		when(regexConvertersFactory.getRegexConverterInstance(eq(true), eq(true))).thenReturn(mockedTypeConverter);
		
		TypeConverter converter = new TypeConverterImpl();
		converter.converTypes(schema, configuration, regexConvertersFactory, regexOptimizersFactory);
		RegularExpression resultingRegexp = schema.getComplexTypes().get("_root").getRegularExpression();
		
//		System.out.println("ECHARE: "+resultingRegexp.toString());
		
		boolean emptyGenerated = resultingRegexp instanceof EmptyRegularExpression;
		assertTrue(emptyGenerated);
		assertEquals(new EmptyRegularExpression(),resultingRegexp);
		
		verify(mockFalseRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
	}
	
	/**
	 * This method checks that an empty regular expression is inferred correctly when avoidSORE is true and tryECHARE is false.
	 * @throws NoSuchRegexCanBeInferredException specified by {@link TypeConverter#converTypes(Schema, XSDInferenceConfiguration)} but not used
	 */
	@Test
	public void testEmptyToChareConversion() throws NoSuchRegexCanBeInferredException{
		automatonRoot.learn(Lists.newArrayList(initialState,finalState));
		automatonRoot.learn(Lists.newArrayList(initialState,finalState));
		
		when(configuration.getAvoidSORE()).thenReturn(true);
		when(configuration.getTryECHARE()).thenReturn(false);
		
		RegexConverter mockedTypeConverter = mock(RegexConverter.class);
//		RegularExpression expectedRegex = buildEchareWithAll();
		RegularExpression emptyRegularExpression = new EmptyRegularExpression();
		Optional wrappedEmpty = new Optional(emptyRegularExpression);
//		Optional wrappedExpectedRegex = new Optional(expectedRegex);
		
		mockFalseRegexOptimizer=mock(RegexOptimizer.class);
		when(mockFalseRegexOptimizer.optimizeRegex(wrappedEmpty)).thenReturn(false);
		
//		mockTrueOnceRegexOptimizer=mock(RegexOptimizer.class);
//		when(mockTrueOnceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,false);
//		
//		mockTrueTwiceRegexOptimizer=mock(RegexOptimizer.class);
//		when(mockTrueTwiceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,true,false);
		
		List<RegexOptimizer> optimizers = Lists.newArrayList(mockFalseRegexOptimizer,mockFalseRegexOptimizer,mockFalseRegexOptimizer,mockFalseRegexOptimizer);
		when(configuration.getOptimizers()).thenReturn(optimizers);
				
		when(mockedTypeConverter.convertAutomatonToRegex(any(ExtendedAutomaton.class))).thenReturn(emptyRegularExpression);
		
		regexConvertersFactory = mock(RegexConvertersFactory.class);
		when(regexConvertersFactory.getRegexConverterInstance(eq(true), eq(false))).thenReturn(mockedTypeConverter);
		
		TypeConverter converter = new TypeConverterImpl();
		converter.converTypes(schema, configuration, regexConvertersFactory, regexOptimizersFactory);
		RegularExpression resultingRegexp = schema.getComplexTypes().get("_root").getRegularExpression();
		
//		System.out.println("ECHARE: "+resultingRegexp.toString());
		
		boolean emptyGenerated = resultingRegexp instanceof EmptyRegularExpression;
		assertTrue(emptyGenerated);
		assertEquals(new EmptyRegularExpression(),resultingRegexp);
		
		verify(mockFalseRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
	}
	
	/**
	 * This method checks that an empty regular expression is inferred correctly when avoidSORE is false.
	 * @throws NoSuchRegexCanBeInferredException specified by {@link TypeConverter#converTypes(Schema, XSDInferenceConfiguration)} but not used
	 */
	@Test
	public void testSingleElementToSoreConversion() throws NoSuchRegexCanBeInferredException{
		automatonRoot.learn(Lists.newArrayList(initialState,elementA,finalState));
		
		when(configuration.getAvoidSORE()).thenReturn(false);
		when(configuration.getTryECHARE()).thenReturn(true);
		
		RegexConverter mockedTypeConverter = mock(RegexConverter.class);
		RegularExpression expectedRegex = elementA;
		RegularExpression emptyRegularExpression = new EmptyRegularExpression();
		Optional wrappedEmpty = new Optional(emptyRegularExpression);
		Optional wrappedExpectedRegex = new Optional(expectedRegex);
		
		mockFalseRegexOptimizer=mock(RegexOptimizer.class);
		when(mockFalseRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(false);
		
		mockTrueOnceRegexOptimizer=mock(RegexOptimizer.class);
		when(mockTrueOnceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,false);
		
		mockTrueTwiceRegexOptimizer=mock(RegexOptimizer.class);
		when(mockTrueTwiceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,true,false);
		
		List<RegexOptimizer> optimizers = Lists.newArrayList(mockFalseRegexOptimizer,mockTrueOnceRegexOptimizer,mockTrueTwiceRegexOptimizer,mockFalseRegexOptimizer);
		when(configuration.getOptimizers()).thenReturn(optimizers);
				
		when(mockedTypeConverter.convertAutomatonToRegex(any(ExtendedAutomaton.class))).thenReturn(emptyRegularExpression);
		when(mockedTypeConverter.convertAutomatonToRegex(automatonRoot)).thenReturn(expectedRegex);
		
		regexConvertersFactory = mock(RegexConvertersFactory.class);
		when(regexConvertersFactory.getRegexConverterInstance(eq(false), anyBoolean())).thenReturn(mockedTypeConverter);
		
		TypeConverter converter = new TypeConverterImpl();
		converter.converTypes(schema, configuration, regexConvertersFactory, regexOptimizersFactory);
		RegularExpression resultingRegexp = schema.getComplexTypes().get("_root").getRegularExpression();
		
//		System.out.println("ECHARE: "+resultingRegexp.toString());
		
		boolean elementGenerated = resultingRegexp instanceof SchemaElement;
		assertTrue(elementGenerated);
		assertEquals(elementA,resultingRegexp);
		
		verify(mockTrueOnceRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockTrueTwiceRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockFalseRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockTrueOnceRegexOptimizer, times(3)).optimizeRegex(wrappedExpectedRegex);
		verify(mockTrueTwiceRegexOptimizer, times(3)).optimizeRegex(wrappedExpectedRegex);
		verify(mockFalseRegexOptimizer, times(6)).optimizeRegex(wrappedExpectedRegex);
	}
	
	/**
	 * This method checks that an empty regular expression is inferred correctly when avoidSORE is true and tryECHARE is true.
	 * @throws NoSuchRegexCanBeInferredException specified by {@link TypeConverter#converTypes(Schema, XSDInferenceConfiguration)} but not used
	 */
	@Test
	public void testSingleElementToEChareConversion() throws NoSuchRegexCanBeInferredException{
		automatonRoot.learn(Lists.newArrayList(initialState,elementA,finalState));
		
		when(configuration.getAvoidSORE()).thenReturn(true);
		when(configuration.getTryECHARE()).thenReturn(true);
		
		RegexConverter mockedTypeConverter = mock(RegexConverter.class);
		RegularExpression expectedRegex = elementA;
		RegularExpression emptyRegularExpression = new EmptyRegularExpression();
		Optional wrappedEmpty = new Optional(emptyRegularExpression);
		Optional wrappedExpectedRegex = new Optional(expectedRegex);
		
		mockFalseRegexOptimizer=mock(RegexOptimizer.class);
		when(mockFalseRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(false);
		
		mockTrueOnceRegexOptimizer=mock(RegexOptimizer.class);
		when(mockTrueOnceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,false);
		
		mockTrueTwiceRegexOptimizer=mock(RegexOptimizer.class);
		when(mockTrueTwiceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,true,false);
		
		List<RegexOptimizer> optimizers = Lists.newArrayList(mockFalseRegexOptimizer,mockTrueOnceRegexOptimizer,mockTrueTwiceRegexOptimizer,mockFalseRegexOptimizer);
		when(configuration.getOptimizers()).thenReturn(optimizers);
				
		when(mockedTypeConverter.convertAutomatonToRegex(any(ExtendedAutomaton.class))).thenReturn(emptyRegularExpression);
		when(mockedTypeConverter.convertAutomatonToRegex(automatonRoot)).thenReturn(expectedRegex);
		
		regexConvertersFactory = mock(RegexConvertersFactory.class);
		when(regexConvertersFactory.getRegexConverterInstance(eq(true), eq(true))).thenReturn(mockedTypeConverter);
		
		TypeConverter converter = new TypeConverterImpl();
		converter.converTypes(schema, configuration, regexConvertersFactory, regexOptimizersFactory);
		RegularExpression resultingRegexp = schema.getComplexTypes().get("_root").getRegularExpression();
		
//		System.out.println("ECHARE: "+resultingRegexp.toString());
		
		boolean elementGenerated = resultingRegexp instanceof SchemaElement;
		assertTrue(elementGenerated);
		assertEquals(elementA,resultingRegexp);
		
		verify(mockTrueOnceRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockTrueTwiceRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockFalseRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockTrueOnceRegexOptimizer, times(3)).optimizeRegex(wrappedExpectedRegex);
		verify(mockTrueTwiceRegexOptimizer, times(3)).optimizeRegex(wrappedExpectedRegex);
		verify(mockFalseRegexOptimizer, times(6)).optimizeRegex(wrappedExpectedRegex);
	}
	
	/**
	 * This method checks that an empty regular expression is inferred correctly when avoidSORE is true and tryECHARE is true.
	 * @throws NoSuchRegexCanBeInferredException specified by {@link TypeConverter#converTypes(Schema, XSDInferenceConfiguration)} but not used
	 */
	@Test
	public void testSingleElementToChareConversion() throws NoSuchRegexCanBeInferredException{
		automatonRoot.learn(Lists.newArrayList(initialState,elementA,finalState));
		
		when(configuration.getAvoidSORE()).thenReturn(true);
		when(configuration.getTryECHARE()).thenReturn(false);
		
		RegexConverter mockedTypeConverter = mock(RegexConverter.class);
		RegularExpression expectedRegex = elementA;
		RegularExpression emptyRegularExpression = new EmptyRegularExpression();
		Optional wrappedEmpty = new Optional(emptyRegularExpression);
		Optional wrappedExpectedRegex = new Optional(expectedRegex);
		
		mockFalseRegexOptimizer=mock(RegexOptimizer.class);
		when(mockFalseRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(false);
		
		mockTrueOnceRegexOptimizer=mock(RegexOptimizer.class);
		when(mockTrueOnceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,false);
		
		mockTrueTwiceRegexOptimizer=mock(RegexOptimizer.class);
		when(mockTrueTwiceRegexOptimizer.optimizeRegex(wrappedExpectedRegex)).thenReturn(true,true,false);
		
		List<RegexOptimizer> optimizers = Lists.newArrayList(mockFalseRegexOptimizer,mockTrueOnceRegexOptimizer,mockTrueTwiceRegexOptimizer,mockFalseRegexOptimizer);
		when(configuration.getOptimizers()).thenReturn(optimizers);
				
		when(mockedTypeConverter.convertAutomatonToRegex(any(ExtendedAutomaton.class))).thenReturn(emptyRegularExpression);
		when(mockedTypeConverter.convertAutomatonToRegex(automatonRoot)).thenReturn(expectedRegex);
		
		regexConvertersFactory = mock(RegexConvertersFactory.class);
		when(regexConvertersFactory.getRegexConverterInstance(eq(true), eq(false))).thenReturn(mockedTypeConverter);
		
		TypeConverter converter = new TypeConverterImpl();
		converter.converTypes(schema, configuration, regexConvertersFactory, regexOptimizersFactory);
		RegularExpression resultingRegexp = schema.getComplexTypes().get("_root").getRegularExpression();
		
//		System.out.println("ECHARE: "+resultingRegexp.toString());
		
		boolean elementGenerated = resultingRegexp instanceof SchemaElement;
		assertTrue(elementGenerated);
		assertEquals(elementA,resultingRegexp);
		
		verify(mockTrueOnceRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockTrueTwiceRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockFalseRegexOptimizer, atLeastOnce()).optimizeRegex(wrappedEmpty);
		verify(mockTrueOnceRegexOptimizer, times(3)).optimizeRegex(wrappedExpectedRegex);
		verify(mockTrueTwiceRegexOptimizer, times(3)).optimizeRegex(wrappedExpectedRegex);
		verify(mockFalseRegexOptimizer, times(6)).optimizeRegex(wrappedExpectedRegex);
	}
}
