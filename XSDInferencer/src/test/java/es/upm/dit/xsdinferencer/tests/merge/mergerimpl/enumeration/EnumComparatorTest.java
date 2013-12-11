package es.upm.dit.xsdinferencer.tests.merge.mergerimpl.enumeration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.merge.EnumComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.enumeration.MinIntersectionBidirectionalEnumComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.enumeration.MinIntersectionUnidirectionalEnumComparator;

/**
 * Test for {@link EnumComparator} and its current implementations: 
 * {@linkplain MinIntersectionBidirectionalEnumComparator} and 
 * {@linkplain MinIntersectionUnidirectionalEnumComparator}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class EnumComparatorTest {

	//Fields for testing
	
	/**
	 * Simple type with xs:string built-in type and isEnum true, whose learned values are: 
	 * "a","b","c","d","e","f"
	 */
	private SimpleType simpleType1;
	
	/**
	 * Copy of {@linkplain simpleType1}
	 */
	private SimpleType simpleType1Copy;
	
	/**
	 * Simple type with xs:string built-in type and isEnum true, whose learned values are: 
	 * "a","b","c","g","h","i"
	 */
	private SimpleType simpleType2;
	
	/**
	 * Simple type with xs:string built-in type and isEnum true, whose learned values are: 
	 * "a","g"
	 */
	private SimpleType simpleType3;
	
	/**
	 * Simple type with xs:string built-in type and isEnum true, whose learned values are: 
	 * "g","h","i","j","k","l"
	 */
	private SimpleType simpleType4;
	
	/**
	 * Simple type with xs:string built-in type and isEnum true, whose learned values are: 
	 * "a","g","h","i","j","k"
	 */
	private SimpleType simpleType5;
	
	/**
	 * Simple type which has been generated after learning no values.
	 */
	private SimpleType simpleTypeEmpty;
	
	/**
	 * Another simple type which has been generated after learning no values.
	 */
	private SimpleType simpleTypeEmpty2;
	
	/**
	 * Simple type with xs:string built-in type and isEnum false, whose learned values are: 
	 * "a","b","c","d","e","f"
	 */
	private SimpleType simpleTypeNotEnumeration1;
	
	/**
	 * Simple type with xs:string built-in type and isEnum false, whose learned values are: 
	 * "a","b","c","g","h","i"
	 */
	private SimpleType simpleTypeNotEnumeration2;
	
	/**
	 * {@linkplain MinIntersectionUnidirectionalEnumComparator} for testing. Threshold at 0.5
	 */
	private MinIntersectionUnidirectionalEnumComparator unidirectionalComparator0Dot5;
	
	/**
	 * {@linkplain MinIntersectionBidirectionalEnumComparator}. Threshold at 0.5
	 */
	private MinIntersectionBidirectionalEnumComparator bidirectionalComparator0Dot5;
	
	@Before
	public void setUp() throws Exception {
		Set<String> valuesOfSimpleType1=ImmutableSet.of("a","b","c","d","e","f");
		Set<String> valuesOfSimpleType2=ImmutableSet.of("a","b","c","g","h","i");
		Set<String> valuesOfSimpleType3=ImmutableSet.of("a","g");
		Set<String> valuesOfSimpleType4=ImmutableSet.of("g","h","i","j","k","l");
		Set<String> valuesOfSimpleType5=ImmutableSet.of("a","g","h","i","j","k");
		Set<String> noValues = ImmutableSet.<String>of();
		simpleType1=new SimpleType("simpleType1","xs:string",valuesOfSimpleType1,true);
		simpleType1Copy=new SimpleType("simpleType1Copy","xs:string",valuesOfSimpleType1,true);
		simpleType2=new SimpleType("simpleType2","xs:string",valuesOfSimpleType2,true);
		simpleType3=new SimpleType("simpleType3","xs:string",valuesOfSimpleType3,true);
		simpleType4=new SimpleType("simpleType4","xs:string",valuesOfSimpleType4,true);
		simpleType5=new SimpleType("simpleType5","xs:string",valuesOfSimpleType5,true);
		simpleTypeNotEnumeration1=new SimpleType("simpleType1n","xs:string",valuesOfSimpleType1,false);
		simpleTypeNotEnumeration2=new SimpleType("simpleType1n","xs:string",valuesOfSimpleType2,false);
		simpleTypeEmpty=new SimpleType("simpleTypeEmpty","",noValues,true);
		simpleTypeEmpty2=new SimpleType("simpleTypeEmpty","",noValues,true);
		
		unidirectionalComparator0Dot5=new MinIntersectionUnidirectionalEnumComparator(0.5f);
		bidirectionalComparator0Dot5=new MinIntersectionBidirectionalEnumComparator(0.5f);
	}

	/**
	 * Checks that all the comparators return true when both simple types have the same values	
	 */
	@Test
	public void testEqual() {
		assertTrue(unidirectionalComparator0Dot5.compare(simpleType1, simpleType1Copy));
		assertTrue(unidirectionalComparator0Dot5.compare(simpleType1Copy, simpleType1));
		assertTrue(bidirectionalComparator0Dot5.compare(simpleType1, simpleType1Copy));
		assertTrue(bidirectionalComparator0Dot5.compare(simpleType1Copy, simpleType1));
	}
	
	/**
	 * Checks that all the comparators return true when both simple types have the same values	
	 */
	@Test
	public void testSame() {
		assertTrue(unidirectionalComparator0Dot5.compare(simpleType1, simpleType1));
		assertTrue(bidirectionalComparator0Dot5.compare(simpleType1, simpleType1));
	}
	
	
	/**
	 * Checks that both comparators return true if they have enough common values
	 */
	@Test
	public void testAllTrue() {
		assertTrue(unidirectionalComparator0Dot5.compare(simpleType1, simpleType2));
		assertTrue(unidirectionalComparator0Dot5.compare(simpleType2, simpleType1));
		assertTrue(bidirectionalComparator0Dot5.compare(simpleType1, simpleType2));
		assertTrue(bidirectionalComparator0Dot5.compare(simpleType2, simpleType1));
	}
	
	
	/**
	 * Checks that only the unidirectional comparator returns true when the ratio 
	 * of common values is greater than the threshold only at one simple type.
	 */
	@Test
	public void testOnlyUnidirectional() {
		assertTrue(unidirectionalComparator0Dot5.compare(simpleType1, simpleType3));
		assertTrue(unidirectionalComparator0Dot5.compare(simpleType3, simpleType1));
		assertFalse(bidirectionalComparator0Dot5.compare(simpleType1, simpleType3));
		assertFalse(bidirectionalComparator0Dot5.compare(simpleType3, simpleType1));
	}
	
	/**
	 * Checks that the comparison of two simple types with no common values returns false 
	 */
	@Test
	public void testAllFalseNoCommon() {
		assertFalse(unidirectionalComparator0Dot5.compare(simpleType1, simpleType4));
		assertFalse(unidirectionalComparator0Dot5.compare(simpleType4, simpleType1));
		assertFalse(bidirectionalComparator0Dot5.compare(simpleType1, simpleType4));
		assertFalse(bidirectionalComparator0Dot5.compare(simpleType4, simpleType1));
	}
	
	/**
	 * Checks that the comparison of two simple types which do not have enough 
	 * common values returns false 
	 */
	@Test
	public void testAllFalseAlthoughSomeCommon() {
		assertFalse(unidirectionalComparator0Dot5.compare(simpleType1, simpleType5));
		assertFalse(unidirectionalComparator0Dot5.compare(simpleType5, simpleType1));
		assertFalse(bidirectionalComparator0Dot5.compare(simpleType1, simpleType5));
		assertFalse(bidirectionalComparator0Dot5.compare(simpleType5, simpleType1));
	}
	
	/**
	 * Checks that the comparators return false for simple types which are not enumerations, 
	 * although they would return true if they were.
	 */
	@Test
	public void testNotEnumerationFalse() {
		assertFalse(unidirectionalComparator0Dot5.compare(simpleTypeNotEnumeration1,simpleTypeNotEnumeration2));
		assertFalse(unidirectionalComparator0Dot5.compare(simpleTypeNotEnumeration2, simpleTypeNotEnumeration1));
		assertFalse(bidirectionalComparator0Dot5.compare(simpleTypeNotEnumeration1,simpleTypeNotEnumeration2));
		assertFalse(bidirectionalComparator0Dot5.compare(simpleTypeNotEnumeration2, simpleTypeNotEnumeration1));
	}
	
	/**
	 * Checks that an empty and a non-empty simple type return false for all comparators
	 */
	@Test
	public void testEmptyAndNonEmpty(){
		assertFalse(unidirectionalComparator0Dot5.compare(simpleType1, simpleTypeEmpty));
		assertFalse(unidirectionalComparator0Dot5.compare(simpleTypeEmpty, simpleType1));
		assertFalse(bidirectionalComparator0Dot5.compare(simpleType1, simpleTypeEmpty));
		assertFalse(bidirectionalComparator0Dot5.compare(simpleTypeEmpty, simpleType1));
	}
	
	/**
	 * Checks that two empty simple types are always similar.
	 */
	@Test
	public void testEmpty(){
		assertTrue(unidirectionalComparator0Dot5.compare(simpleTypeEmpty, simpleTypeEmpty2));
		assertTrue(unidirectionalComparator0Dot5.compare(simpleTypeEmpty2, simpleTypeEmpty));
		assertTrue(bidirectionalComparator0Dot5.compare(simpleTypeEmpty, simpleTypeEmpty2));
		assertTrue(bidirectionalComparator0Dot5.compare(simpleTypeEmpty2, simpleTypeEmpty));
	}
}
