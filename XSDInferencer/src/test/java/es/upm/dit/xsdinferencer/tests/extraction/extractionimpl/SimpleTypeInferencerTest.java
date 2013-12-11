package es.upm.dit.xsdinferencer.tests.extraction.extractionimpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.extraction.SimpleTypeInferencer;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.InferencersFactory;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.SimpleTypeInferencerImpl;

/**
 * Test class for {@link SimpleTypeInferencer} and its implementation {@link SimpleTypeInferencerImpl}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class SimpleTypeInferencerTest {
	
	//Fields for testing
	private XSDInferenceConfiguration config;
	
	private SimpleTypeInferencer simpleTypeInferencer;

	@Before
	public void setUp() throws Exception {
		config=mock(XSDInferenceConfiguration.class);
		when(config.getTypeNamesAncestorsSeparator()).thenReturn("-");
		when(config.getMaxNumberOfDistinctValuesToEnum()).thenReturn(15);//Maybe quite strange values in real life but useful to test that everything behaves as specified.
		when(config.getMinNumberOfDistinctValuesToEnum()).thenReturn(4);
		when(config.getSimpleTypeInferencer()).thenReturn(XSDInferenceConfiguration.VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL);
		when(config.getGenerateEnumerations()).thenReturn(true);
		
		simpleTypeInferencer = InferencersFactory.getInstance().getSimpleTypeInferencerInstance("", config);
	}

	/**
	 * Tests the inference of a boolean simple type when all the possible values have been learned at least once.
	 * Note that a boolean will NEVER be an enumeration, regardless of any configuration.
	 */
	@Test
	public void testXSBoolean() {
		simpleTypeInferencer.learnValue("true", "", "t");
		simpleTypeInferencer.learnValue("false", "", "t");
		simpleTypeInferencer.learnValue("true", "", "t");
		simpleTypeInferencer.learnValue("0", "", "t");
		simpleTypeInferencer.learnValue("0", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals("simpleType", simpleType.getName());
		assertEquals(4,simpleType.enumerationCount());
		assertTrue(simpleType.enumerationContains("true"));
		assertTrue(simpleType.enumerationContains("false"));
		assertTrue(simpleType.enumerationContains("0"));
		assertTrue(simpleType.enumerationContains("1"));
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertEquals(4,knownValuesUnmodifiableList.size());
		assertTrue(knownValuesUnmodifiableList.contains("true"));
		assertTrue(knownValuesUnmodifiableList.contains("false"));
		assertTrue(knownValuesUnmodifiableList.contains("0"));
		assertTrue(knownValuesUnmodifiableList.contains("1"));
		assertEquals("xs:boolean",simpleType.getBuiltinType());
		assertEquals("xs:boolean",simpleType.getRepresentationName("-"));
		assertFalse(simpleType.isEnum());
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * An xs:boolean allows the values true,false,0,1 (ignoring whitespaces). However, an xs:integer should 
	 * be inferred if the known values are only 0 or 1 because it is more probable that such a simple type 
	 * is an integer and not a boolean. This test checks that an xs:integer is inferred if 0 and 1 are given.
	 */
	@Test
	public void test01AreNotBooleanWithoutTrueFalse(){
		simpleTypeInferencer.learnValue("0", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("0", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("0", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("0", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals("xs:integer",simpleType.getBuiltinType());
		assertEquals("xs:integer",simpleType.getRepresentationName("-"));
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}

	/**
	 * An xs:boolean allows the values true,false,0,1 (ignoring whitespaces). However, an xs:integer should 
	 * be inferred if the known values are only 0 or 1 because it is more probable that such a simple type 
	 * is an integer and not a boolean. This test checks that an xs:integer is inferred if 0 is given.
	 */
	@Test
	public void test0IsNotBooleanWithoutTrueFalse(){
		simpleTypeInferencer.learnValue("0", "", "t");
		simpleTypeInferencer.learnValue("0", "", "t");
		simpleTypeInferencer.learnValue("0", "", "t");
		simpleTypeInferencer.learnValue("0", "", "t");
		simpleTypeInferencer.learnValue("0", "", "t");
		simpleTypeInferencer.learnValue("0", "", "t");
		simpleTypeInferencer.learnValue("0", "", "t");
		simpleTypeInferencer.learnValue("0", "", "t");
		simpleTypeInferencer.learnValue("0", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals("xs:integer",simpleType.getBuiltinType());
		assertEquals("xs:integer",simpleType.getRepresentationName("-"));
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * Checks that whitespaces are ignored on xs:boolean
	 */
	@Test
	public void testXSBooleanWhiteSpacesIgnored(){
		simpleTypeInferencer.learnValue("true", "", "t");
		simpleTypeInferencer.learnValue("   true   ", "", "t");
		simpleTypeInferencer.learnValue("   true", "", "t");
		simpleTypeInferencer.learnValue("true    ", "", "t");
		simpleTypeInferencer.learnValue(" true    ", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals(1,simpleType.enumerationCount());
		assertTrue(simpleType.enumerationContains("true"));
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertEquals(1,knownValuesUnmodifiableList.size());
		assertTrue(knownValuesUnmodifiableList.contains("true"));
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		assertEquals("xs:boolean",simpleType.getRepresentationName("-"));
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * An xs:boolean allows the values true,false,0,1 (ignoring whitespaces). However, an xs:integer should 
	 * be inferred if the known values are only 0 or 1 because it is more probable that such a simple type 
	 * is an integer and not a boolean. This test checks that an xs:integer is inferred if 1 is given.
	 */
	@Test
	public void test1IsNotBooleanWithoutTrueFalse(){
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals("xs:integer",simpleType.getBuiltinType());
		assertEquals("xs:integer",simpleType.getRepresentationName("-"));
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * Tests the inference of an integer in a normal situation, where values  
	 * 1,-2,3 are learned (3 is learned as 3 and +3, which should only be inferred as 3)
	 */
	@Test
	public void testXSInteger(){
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		simpleTypeInferencer.learnValue("+3", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		simpleTypeInferencer.learnValue("3", "", "t");
		simpleTypeInferencer.learnValue("+3", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals("simpleType", simpleType.getName());
		assertEquals(3,simpleType.enumerationCount());
		assertTrue(simpleType.enumerationContains("1"));
		assertTrue(simpleType.enumerationContains("-2"));
		assertTrue(simpleType.enumerationContains("3"));
		assertFalse(simpleType.enumerationContains("+3"));
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertEquals(3,knownValuesUnmodifiableList.size());
		assertTrue(knownValuesUnmodifiableList.contains("1"));
		assertTrue(knownValuesUnmodifiableList.contains("-2"));
		assertTrue(knownValuesUnmodifiableList.contains("3"));
		assertFalse(knownValuesUnmodifiableList.contains("+3"));
		assertEquals("xs:integer",simpleType.getBuiltinType());
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		assertEquals("xs:integer",simpleType.getRepresentationName("-"));
		assertFalse(simpleType.isEnum());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * Tests the inference of an integer in a normal situation, where values  
	 * 1,-2,3,-4,5 are learned (3 is learned as 3 and +3, which should only be inferred as 3).
	 * More values are learned so that it is considered as an enumeration with the current configuration
	 */
	@Test
	public void testXSIntegerEnum(){
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		simpleTypeInferencer.learnValue("+3", "", "t");
		simpleTypeInferencer.learnValue("-4", "", "t");
		simpleTypeInferencer.learnValue("5", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		simpleTypeInferencer.learnValue("3", "", "t");
		simpleTypeInferencer.learnValue("+3", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals("simpleType", simpleType.getName());
		assertEquals(5,simpleType.enumerationCount());
		assertTrue(simpleType.enumerationContains("1"));
		assertTrue(simpleType.enumerationContains("-2"));
		assertTrue(simpleType.enumerationContains("3"));
		assertTrue(simpleType.enumerationContains("-4"));
		assertTrue(simpleType.enumerationContains("5"));
		assertFalse(simpleType.enumerationContains("+3"));
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertEquals(5,knownValuesUnmodifiableList.size());
		assertTrue(knownValuesUnmodifiableList.contains("1"));
		assertTrue(knownValuesUnmodifiableList.contains("-2"));
		assertTrue(knownValuesUnmodifiableList.contains("3"));
		assertTrue(knownValuesUnmodifiableList.contains("-4"));
		assertTrue(knownValuesUnmodifiableList.contains("5"));
		assertFalse(knownValuesUnmodifiableList.contains("+3"));
		assertEquals("xs:integer",simpleType.getBuiltinType());
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		assertEquals("simpleType-SimpleType",simpleType.getRepresentationName("-"));
		assertTrue(simpleType.isEnum());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * Checks that whitespaces are ignored on xs:integer
	 */
	@Test
	public void testXSIntegerWhiteSpacesIgnored(){
		simpleTypeInferencer.learnValue("5", "", "t");
		simpleTypeInferencer.learnValue("   5   ", "", "t");
		simpleTypeInferencer.learnValue("   5", "", "t");
		simpleTypeInferencer.learnValue("5    ", "", "t");
		simpleTypeInferencer.learnValue(" 5    ", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals(1,simpleType.enumerationCount());
		assertTrue(simpleType.enumerationContains("5"));
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertEquals(1,knownValuesUnmodifiableList.size());
		assertTrue(knownValuesUnmodifiableList.contains("5"));
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		assertEquals("xs:integer",simpleType.getRepresentationName("-"));
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * Tests the inference of a decimal in a normal situation.
	 * Values 1.5,2.2,3 are learned (3 is learned as 3.0,3 and +3.0).
	 */
	@Test
	public void testXSDecimal(){
		simpleTypeInferencer.learnValue("1.5", "", "t");
		simpleTypeInferencer.learnValue("-2.2", "", "t");
		simpleTypeInferencer.learnValue("+3.0", "", "t");
		simpleTypeInferencer.learnValue("-2.2", "", "t");
		simpleTypeInferencer.learnValue("1.5", "", "t");
		simpleTypeInferencer.learnValue("1.5", "", "t");
		simpleTypeInferencer.learnValue("-2.2", "", "t");
		simpleTypeInferencer.learnValue("3.0", "", "t");
		simpleTypeInferencer.learnValue("3", "", "t");
		simpleTypeInferencer.learnValue("+3", "", "t");
		simpleTypeInferencer.learnValue("-2.2", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals("simpleType", simpleType.getName());
		assertEquals(3,simpleType.enumerationCount());
		assertTrue(simpleType.enumerationContains("1.5"));
		assertTrue(simpleType.enumerationContains("-2.2"));
		assertTrue(simpleType.enumerationContains("3"));
		assertFalse(simpleType.enumerationContains("+3"));
		assertFalse(simpleType.enumerationContains("+3.0"));
		assertFalse(simpleType.enumerationContains("3.0"));
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertEquals(3,simpleType.enumerationCount());
		assertTrue(knownValuesUnmodifiableList.contains("1.5"));
		assertTrue(knownValuesUnmodifiableList.contains("-2.2"));
		assertTrue(knownValuesUnmodifiableList.contains("3"));
		assertFalse(knownValuesUnmodifiableList.contains("+3"));
		assertFalse(knownValuesUnmodifiableList.contains("+3.0"));
		assertFalse(knownValuesUnmodifiableList.contains("3.0"));
		assertEquals("xs:decimal",simpleType.getBuiltinType());
		assertEquals("xs:decimal",simpleType.getRepresentationName("-"));
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		assertFalse(simpleType.isEnum());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * Tests the inference of a decimal in a normal situation 
	 *  with many distinct values so that it is considered as an enumeration
	 */
	@Test
	public void testXSDecimalEnum(){
		simpleTypeInferencer.learnValue("1.5", "", "t");
		simpleTypeInferencer.learnValue("-2.4", "", "t");
		simpleTypeInferencer.learnValue("+3.0", "", "t");
		simpleTypeInferencer.learnValue("-2.2", "", "t");
		simpleTypeInferencer.learnValue("1.5", "", "t");
		simpleTypeInferencer.learnValue("1.6", "", "t");
		simpleTypeInferencer.learnValue("-2.5", "", "t");
		simpleTypeInferencer.learnValue("3.0", "", "t");
		simpleTypeInferencer.learnValue("3", "", "t");
		simpleTypeInferencer.learnValue("+3", "", "t");
		simpleTypeInferencer.learnValue("-2.2", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertEquals("simpleType", simpleType.getName());
		assertEquals(6,simpleType.enumerationCount());
		assertEquals(6,knownValuesUnmodifiableList.size());
		assertTrue(simpleType.enumerationContains("1.5"));
		assertTrue(simpleType.enumerationContains("1.6"));
		assertTrue(simpleType.enumerationContains("-2.2"));
		assertTrue(simpleType.enumerationContains("-2.4"));
		assertTrue(simpleType.enumerationContains("-2.5"));
		assertTrue(simpleType.enumerationContains("3"));
		assertFalse(simpleType.enumerationContains("+3"));
		assertFalse(simpleType.enumerationContains("+3.0"));
		assertFalse(simpleType.enumerationContains("3.0"));
		assertTrue(knownValuesUnmodifiableList.contains("1.5"));
		assertTrue(knownValuesUnmodifiableList.contains("1.6"));
		assertTrue(knownValuesUnmodifiableList.contains("-2.2"));
		assertTrue(knownValuesUnmodifiableList.contains("-2.4"));
		assertTrue(knownValuesUnmodifiableList.contains("-2.5"));
		assertTrue(knownValuesUnmodifiableList.contains("3"));
		assertFalse(knownValuesUnmodifiableList.contains("+3"));
		assertFalse(knownValuesUnmodifiableList.contains("+3.0"));
		assertFalse(knownValuesUnmodifiableList.contains("3.0"));
		assertEquals("xs:decimal",simpleType.getBuiltinType());
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		assertTrue(simpleType.isEnum());
		assertEquals("simpleType-SimpleType",simpleType.getRepresentationName("-"));
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * Checks that whitespaces are ignored on xs:decimal
	 */
	@Test
	public void testXSDecimalWhiteSpacesIgnored(){
		simpleTypeInferencer.learnValue("10.5", "", "t");
		simpleTypeInferencer.learnValue("   10.5   ", "", "t");
		simpleTypeInferencer.learnValue("   10.5", "", "t");
		simpleTypeInferencer.learnValue("10.5    ", "", "t");
		simpleTypeInferencer.learnValue(" 10.5    ", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals(1,simpleType.enumerationCount());
		assertTrue(simpleType.enumerationContains("10.5"));
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertEquals(1,knownValuesUnmodifiableList.size());
		assertTrue(knownValuesUnmodifiableList.contains("10.5"));
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		assertEquals("xs:decimal",simpleType.getRepresentationName("-"));
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}

	/**
	 * Tests whether the inferencer defaults to xs:string correctly (although some 
	 * boolean or numeric values are found).
	 */
	@Test
	public void testXSStringConfusing(){
		simpleTypeInferencer.learnValue("true", "", "t");
		simpleTypeInferencer.learnValue("hola", "", "t");
		simpleTypeInferencer.learnValue("1.5", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals(3,simpleType.enumerationCount());
		assertTrue(simpleType.enumerationContains("true"));
		assertTrue(simpleType.enumerationContains("hola"));
		assertTrue(simpleType.enumerationContains("1.5"));
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertEquals(3,knownValuesUnmodifiableList.size());
		assertTrue(knownValuesUnmodifiableList.contains("true"));
		assertTrue(knownValuesUnmodifiableList.contains("hola"));
		assertTrue(knownValuesUnmodifiableList.contains("1.5"));
		assertEquals("xs:string",simpleType.getBuiltinType());
		assertEquals("xs:string",simpleType.getRepresentationName("-"));
		assertFalse(simpleType.isEnum());
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * Tests whether the inferencer defaults to xs:string correctly.
	 */
	@Test
	public void testXSString(){
		simpleTypeInferencer.learnValue("buenas", "", "t");
		simpleTypeInferencer.learnValue("hola", "", "t");
		simpleTypeInferencer.learnValue("escribamos un texto", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals(3,simpleType.enumerationCount());
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertTrue(knownValuesUnmodifiableList.contains("buenas"));
		assertTrue(knownValuesUnmodifiableList.contains("hola"));
		assertTrue(knownValuesUnmodifiableList.contains("escribamos un texto"));
		assertTrue(simpleType.enumerationContains("buenas"));
		assertTrue(simpleType.enumerationContains("hola"));
		assertTrue(simpleType.enumerationContains("escribamos un texto"));
		assertEquals("xs:string",simpleType.getBuiltinType());
		assertEquals("xs:string",simpleType.getRepresentationName("-"));
		assertFalse(simpleType.isEnum());
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * Tests whether the inferencer defaults to xs:string correctly and that 
	 * the simpleType is considered as an enumeration if enough distinct values are learned.
	 */
	@Test
	public void testXSStringEnum(){
		simpleTypeInferencer.learnValue("buenas", "", "t");
		simpleTypeInferencer.learnValue("hola", "", "t");
		simpleTypeInferencer.learnValue("escribamos un texto", "", "t");
		simpleTypeInferencer.learnValue("rellenemos esto", "", "t");
		simpleTypeInferencer.learnValue("asi tendremos", "", "t");
		simpleTypeInferencer.learnValue("una enumeracion", "", "t");
		simpleTypeInferencer.learnValue("solo hay que tener varios valores distintos", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertEquals(7,simpleType.enumerationCount());
		assertEquals(7,knownValuesUnmodifiableList.size());
		assertTrue(knownValuesUnmodifiableList.contains("buenas"));
		assertTrue(knownValuesUnmodifiableList.contains("hola"));
		assertTrue(knownValuesUnmodifiableList.contains("escribamos un texto"));
		assertTrue(knownValuesUnmodifiableList.contains("rellenemos esto"));
		assertTrue(knownValuesUnmodifiableList.contains("asi tendremos"));
		assertTrue(knownValuesUnmodifiableList.contains("una enumeracion"));
		assertTrue(knownValuesUnmodifiableList.contains("solo hay que tener varios valores distintos"));
		assertTrue(simpleType.enumerationContains("buenas"));
		assertTrue(simpleType.enumerationContains("hola"));
		assertTrue(simpleType.enumerationContains("escribamos un texto"));
		assertTrue(simpleType.enumerationContains("rellenemos esto"));
		assertTrue(simpleType.enumerationContains("asi tendremos"));
		assertTrue(simpleType.enumerationContains("una enumeracion"));
		assertTrue(simpleType.enumerationContains("solo hay que tener varios valores distintos"));
		assertEquals("xs:string",simpleType.getBuiltinType());
		assertTrue(simpleType.isEnum());
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * Tests whether the inferencer defaults to xs:string correctly and that 
	 * the simpleType is not considered as an enumeration although enough distinct 
	 * values are learned when generateEnumerations is false.
	 */
	@Test
	public void testXSStringNotEnumBecauseOfConfiguration(){
		when(config.getGenerateEnumerations()).thenReturn(false);
		
		simpleTypeInferencer.learnValue("buenas", "", "t");
		simpleTypeInferencer.learnValue("hola", "", "t");
		simpleTypeInferencer.learnValue("escribamos un texto", "", "t");
		simpleTypeInferencer.learnValue("rellenemos esto", "", "t");
		simpleTypeInferencer.learnValue("asi tendremos", "", "t");
		simpleTypeInferencer.learnValue("una enumeracion", "", "t");
		simpleTypeInferencer.learnValue("solo hay que tener varios valores distintos", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertEquals(7,simpleType.enumerationCount());
		assertEquals(7,knownValuesUnmodifiableList.size());
		assertTrue(knownValuesUnmodifiableList.contains("buenas"));
		assertTrue(knownValuesUnmodifiableList.contains("hola"));
		assertTrue(knownValuesUnmodifiableList.contains("escribamos un texto"));
		assertTrue(knownValuesUnmodifiableList.contains("rellenemos esto"));
		assertTrue(knownValuesUnmodifiableList.contains("asi tendremos"));
		assertTrue(knownValuesUnmodifiableList.contains("una enumeracion"));
		assertTrue(knownValuesUnmodifiableList.contains("solo hay que tener varios valores distintos"));
		assertTrue(simpleType.enumerationContains("buenas"));
		assertTrue(simpleType.enumerationContains("hola"));
		assertTrue(simpleType.enumerationContains("escribamos un texto"));
		assertTrue(simpleType.enumerationContains("rellenemos esto"));
		assertTrue(simpleType.enumerationContains("asi tendremos"));
		assertTrue(simpleType.enumerationContains("una enumeracion"));
		assertTrue(simpleType.enumerationContains("solo hay que tener varios valores distintos"));
		assertEquals("xs:string",simpleType.getBuiltinType());
		assertFalse(simpleType.isEnum());
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * Checks that whitespaces are not ignored on xs:string
	 */
	@Test
	public void testXSStringWhiteSpacesNotIgnored(){
		simpleTypeInferencer.learnValue("text", "", "t");
		simpleTypeInferencer.learnValue("   text   ", "", "t");
		simpleTypeInferencer.learnValue("   text", "", "t");
		simpleTypeInferencer.learnValue("text    ", "", "t");
		simpleTypeInferencer.learnValue(" text    ", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals(5,simpleType.enumerationCount());
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertTrue(knownValuesUnmodifiableList.contains("text"));
		assertTrue(knownValuesUnmodifiableList.contains("   text   "));
		assertTrue(knownValuesUnmodifiableList.contains("   text"));
		assertTrue(knownValuesUnmodifiableList.contains("text    "));
		assertTrue(knownValuesUnmodifiableList.contains(" text    "));
		assertTrue(simpleType.enumerationContains("text"));
		assertTrue(simpleType.enumerationContains("   text   "));
		assertTrue(simpleType.enumerationContains("   text"));
		assertTrue(simpleType.enumerationContains("text    "));
		assertTrue(simpleType.enumerationContains(" text    "));
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		assertEquals("simpleType-SimpleType",simpleType.getRepresentationName("-"));
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * Checks that an empty value causes the builtin type to be inferred as an string
	 */
	@Test
	public void testEmptyValue(){
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		simpleTypeInferencer.learnValue("", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertTrue(knownValuesUnmodifiableList.contains("1"));
		assertTrue(knownValuesUnmodifiableList.contains("-2"));
		assertTrue(knownValuesUnmodifiableList.contains(""));
		assertEquals("simpleType", simpleType.getName());
		assertEquals(3,simpleType.enumerationCount());
		assertEquals(3,knownValuesUnmodifiableList.size());
		assertTrue(simpleType.enumerationContains("1"));
		assertTrue(simpleType.enumerationContains("-2"));
		assertTrue(simpleType.enumerationContains(""));
		assertEquals("xs:string",simpleType.getBuiltinType());
		assertEquals("xs:string",simpleType.getRepresentationName("-"));
		assertFalse(simpleType.isEnum());
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
	}
	
	/**
	 * Checks that an empty value causes the builtin type to be inferred as an string 
	 * and becomes part of an enumeration properly.
	 */
	@Test
	public void testEmptyValueEnum(){
		simpleTypeInferencer.learnValue("1", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		simpleTypeInferencer.learnValue("1.0", "", "t");
		simpleTypeInferencer.learnValue("+1.0", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		simpleTypeInferencer.learnValue("-2", "", "t");
		simpleTypeInferencer.learnValue("", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals("simpleType", simpleType.getName());
		assertEquals(5,simpleType.enumerationCount());
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertEquals(5,knownValuesUnmodifiableList.size());
		assertTrue(knownValuesUnmodifiableList.contains("1"));
		assertTrue(knownValuesUnmodifiableList.contains("1.0"));
		assertTrue(knownValuesUnmodifiableList.contains("+1.0"));
		assertTrue(knownValuesUnmodifiableList.contains("-2"));
		assertTrue(knownValuesUnmodifiableList.contains(""));
		assertTrue(simpleType.enumerationContains("1"));
		assertTrue(simpleType.enumerationContains("1.0"));
		assertTrue(simpleType.enumerationContains("+1.0"));
		assertTrue(simpleType.enumerationContains("-2"));
		assertTrue(simpleType.enumerationContains(""));
		assertEquals("xs:string",simpleType.getBuiltinType());
		assertEquals("simpleType-SimpleType",simpleType.getRepresentationName("-"));
		assertTrue(simpleType.isEnum());
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
	
	/**
	 * Checks that the simple type inferencer infers an empty simple type correctly.
	 */
	@Test
	public void testEmpty(){
		simpleTypeInferencer.learnValue("", "", "t");
		simpleTypeInferencer.learnValue("", "", "t");
		simpleTypeInferencer.learnValue("", "", "t");
		simpleTypeInferencer.learnValue("", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals("simpleType", simpleType.getName());
		assertEquals(0,simpleType.enumerationCount());
		assertEquals(ImmutableList.of(),simpleType.getKnownValuesUnmodifiableList());
		assertTrue(simpleType.isEmpty());
		assertFalse(simpleType.consistOnlyOfWhitespaceCharacters());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(0,sourceKeys.size()); //Empty because no value has been learned
	}
	
	/**
	 * Checks that a simple type infers a simple type consisting of only white space characters is correctly 
	 * correctly, it implies that the known values are the ones learned, the builtin type xs:string, 
	 * it MUST NOT be an enumeration and {@linkplain SimpleType#consistOnlyOfWhitespaceCharacters()} must return true
	 */
	@Test
	public void testOnlyWhitespaces(){
		simpleTypeInferencer.learnValue(" ", "", "t");
		simpleTypeInferencer.learnValue("\t", "", "t");
		simpleTypeInferencer.learnValue("\n", "", "t");
		simpleTypeInferencer.learnValue("\r", "", "t");
		simpleTypeInferencer.learnValue("", "", "t");
		simpleTypeInferencer.learnValue("    \n   \n      \n", "", "t");
		simpleTypeInferencer.learnValue("\r\n  \r\n   \r\n ", "", "t");
		simpleTypeInferencer.learnValue("\t \n\t\t \n\t ", "", "t");
		SimpleType simpleType = simpleTypeInferencer.getSimpleType("simpleType");
		assertEquals("simpleType", simpleType.getName());
		List<String> knownValuesUnmodifiableList = simpleType.getKnownValuesUnmodifiableList();
		assertEquals(ImmutableSet.of(" ","\t","\n","\r","","    \n   \n      \n","\r\n  \r\n   \r\n ","\t \n\t\t \n\t "),ImmutableSet.copyOf(knownValuesUnmodifiableList));
		assertEquals(8,simpleType.enumerationCount());
		assertEquals(8,knownValuesUnmodifiableList.size());
		assertTrue(simpleType.enumerationContains(" "));
		assertTrue(simpleType.enumerationContains("\t"));
		assertTrue(simpleType.enumerationContains("\n"));
		assertTrue(simpleType.enumerationContains("\r"));
		assertTrue(simpleType.enumerationContains(""));
		assertTrue(simpleType.enumerationContains("    \n   \n      \n"));
		assertTrue(simpleType.enumerationContains("\r\n  \r\n   \r\n "));
		assertTrue(simpleType.enumerationContains("\t \n\t\t \n\t "));
		assertTrue(simpleType.consistOnlyOfWhitespaceCharacters());
		assertEquals("xs:string",simpleType.getBuiltinType());
		assertEquals("xs:string",simpleType.getRepresentationName("-"));
		assertFalse(simpleType.isEnum());
		//Check that the source element is correct
		Set<String> sourceKeys=simpleType.getSourceNodeNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":t"));
	}
}
