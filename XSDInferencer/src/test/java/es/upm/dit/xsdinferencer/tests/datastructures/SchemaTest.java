package es.upm.dit.xsdinferencer.tests.datastructures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Table;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.statistics.Statistics;

/**
 * Test class for {@link Schema}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class SchemaTest {
	
	//Fields for testing
	private Schema schema;
	
	private NavigableMap<String,SortedSet<String>> prefixNamespaceMapping;
	
	private Table<String,String,SchemaElement> elements;
	private SchemaElement element1;
	private SchemaElement element11;
	private SchemaElement element12;
	
	private Table<String,String,SchemaAttribute> attributes;
	private SchemaAttribute attribute111;
	@SuppressWarnings("unused")
	private SchemaAttribute attribute121;
	
	private Map<String,ComplexType> complexTypes;
	private ComplexType complexType1;
	private ComplexType complexType11;
	private ComplexType complexType12;
	
	private Map<String,SimpleType> simpleTypes;	
	private SimpleType simpleType11;
	private SimpleType simpleType111;
	private SimpleType simpleType12;
	private SimpleType simpleType121;
	
	private Statistics statistics;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		
		prefixNamespaceMapping=new TreeMap<String, SortedSet<String>>();
		
		prefixNamespaceMapping.put("",new TreeSet<>(Collections.singleton("")));
		prefixNamespaceMapping.put("http://otherNamespace.com",new TreeSet<>(Collections.singleton("other")));
		
		elements = HashBasedTable.create();
		element1=mock(SchemaElement.class);
		element11=mock(SchemaElement.class);
		element12=mock(SchemaElement.class);
		elements.put("", "element1", element1);
		elements.put("", "element1-element11", element11);
		elements.put("http://otherNamespace.com", "element1-element12", element12);
		
		attributes = HashBasedTable.create();
		attribute111=mock(SchemaAttribute.class);
		attribute121=mock(SchemaAttribute.class);
		attributes.put("", "element1-element11-attribute1", attribute111);
		attributes.put("http://otherNamespace.com", "element1-element12-attribute1", attribute111);
		
		complexTypes=new HashMap<String,ComplexType>();
		complexType1=mock(ComplexType.class);
		complexType11=mock(ComplexType.class);
		complexType12=mock(ComplexType.class);
		complexTypes.put("element1",complexType1);
		complexTypes.put("element1-element11",complexType11);
		complexTypes.put("element1-element12",complexType12);
		
		simpleTypes=new HashMap<String,SimpleType>();
		simpleType11 = mock(SimpleType.class);
		simpleType111 = mock(SimpleType.class);
		simpleType12 = mock(SimpleType.class);
		simpleType121 = mock(SimpleType.class);
		simpleTypes.put("element1-element11", simpleType11);
		simpleTypes.put("element1-element12", simpleType12);
		simpleTypes.put("element1-element11-attribute1", simpleType111);
		simpleTypes.put("element1-element12-attribute1", simpleType121);
		
		statistics = mock(Statistics.class);
		
		schema = new Schema(prefixNamespaceMapping, elements, attributes, complexTypes, simpleTypes, statistics);
	}
	
	

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Tests the constructor when full values are provided
	 */
	@Test
	public void testSchema(){
		Schema otherSchema;
		
		prefixNamespaceMapping=new TreeMap<String, SortedSet<String>>();
		
		prefixNamespaceMapping.put("",new TreeSet<>(Collections.singleton("")));
		prefixNamespaceMapping.put("http://otherNamespace.com",new TreeSet<>(Collections.singleton("other")));
				
		elements = HashBasedTable.create();
		element1=mock(SchemaElement.class);
		element11=mock(SchemaElement.class);
		element12=mock(SchemaElement.class);
		elements.put("", "element1", element1);
		elements.put("", "element1-element11", element11);
		elements.put("http://otherNamespace.com", "element1-element12", element12);
		
		attributes = HashBasedTable.create();
		attribute111=mock(SchemaAttribute.class);
		attribute121=mock(SchemaAttribute.class);
		attributes.put("", "element1-element11-attribute1", attribute111);
		attributes.put("http://otherNamespace.com", "element1-element12-attribute1", attribute111);
		
		complexTypes=new HashMap<String,ComplexType>();
		complexType1=mock(ComplexType.class);
		complexType11=mock(ComplexType.class);
		complexType12=mock(ComplexType.class);
		complexTypes.put("element1",complexType1);
		complexTypes.put("element1-element11",complexType11);
		complexTypes.put("element1-element12",complexType12);
		
		simpleTypes=new HashMap<String,SimpleType>();
		simpleType11 = mock(SimpleType.class);
		simpleType111 = mock(SimpleType.class);
		simpleType12 = mock(SimpleType.class);
		simpleType121 = mock(SimpleType.class);
		simpleTypes.put("element1-element11", simpleType11);
		simpleTypes.put("element1-element12", simpleType12);
		simpleTypes.put("element1-element11-attribute1", simpleType111);
		simpleTypes.put("element1-element12-attribute1", simpleType121);
		
		statistics = mock(Statistics.class);
		
		otherSchema = new Schema(prefixNamespaceMapping, elements, attributes, complexTypes, simpleTypes, statistics);
		assertEquals(statistics,otherSchema.getStatistics());
		assertEquals(prefixNamespaceMapping, otherSchema.getNamespacesToPossiblePrefixMappingModifiable());
		assertEquals(elements, otherSchema.getElements());
		assertEquals(attributes, otherSchema.getAttributes());
		assertEquals(complexTypes, otherSchema.getComplexTypes());
		assertEquals(simpleTypes, otherSchema.getSimpleTypes());
		//assertEquals(statistics, schema.getStatistics());
	}
	
	/**
	 * Checks that the constructor throws a {@link NullPointerException} if a null value is provided.
	 */
	@Test
	public void testSchemaNull(){
		boolean ok=true;
		try{
			schema = new Schema(null, elements, attributes, complexTypes, simpleTypes, statistics);
			ok=false;
		}catch(NullPointerException e){}
		try{
			schema = new Schema(prefixNamespaceMapping, null, attributes, complexTypes, simpleTypes, statistics);
			ok=false;
		}catch(NullPointerException e){}
		try{
			schema = new Schema(prefixNamespaceMapping, elements, null, complexTypes, simpleTypes, statistics);
			ok=false;
		}catch(NullPointerException e){}
		try{
			schema = new Schema(prefixNamespaceMapping, elements, attributes, null, simpleTypes, statistics);
			ok=false;
		}catch(NullPointerException e){}
		try{
			schema = new Schema(prefixNamespaceMapping, elements, attributes, complexTypes, null, statistics);
			ok=false;
		}catch(NullPointerException e){}
		try{
			schema = new Schema(prefixNamespaceMapping, elements, attributes, complexTypes, simpleTypes, null);
			ok=false;
		}catch(NullPointerException e){}
		if(!ok)
			fail("A NullPointerException has been not properly thrown");
	}

	
	/**
	 * This method tests the constructor when empty data structures are provided.
	 */
	@Test
	public void testSchemaDefault(){
		Schema otherSchema;
		
		prefixNamespaceMapping=new TreeMap<String, SortedSet<String>>();
		prefixNamespaceMapping.put("",new TreeSet<>(Collections.singleton("")));
		//prefixNamespaceMapping.put("http://otherNamespace.com",new TreeSet<>(Collections.singleton("other")));
		
		elements = HashBasedTable.create();
				
		attributes = HashBasedTable.create();
		
		complexTypes=new HashMap<String,ComplexType>();
		
		simpleTypes=new HashMap<String,SimpleType>();
				
		otherSchema = new Schema(prefixNamespaceMapping, elements, attributes, complexTypes, simpleTypes, statistics);
		assertEquals(statistics,otherSchema.getStatistics());
		assertEquals(prefixNamespaceMapping, otherSchema.getNamespacesToPossiblePrefixMappingModifiable());
		assertEquals(elements, otherSchema.getElements());
		assertEquals(attributes, otherSchema.getAttributes());
		assertEquals(complexTypes, otherSchema.getComplexTypes());
		assertEquals(simpleTypes, otherSchema.getSimpleTypes());
	}
	
	/**
	 * Test method for {@link Schema#getStatistics()}
	 */
	@Test
	public void testGetStatistics() {
		assertEquals(statistics,schema.getStatistics());
	}

	/**
	 * Test method for {@link Schema#getNamespacesToPossiblePrefixMappingUnmodifiable()}
	 */
	@Test
	public void testGetNamespacesToPossiblePrefixMappingUnmodifiable() {
		assertEquals(prefixNamespaceMapping, schema.getNamespacesToPossiblePrefixMappingUnmodifiable());
	}

	/**
	 * Test method for {@link Schema#getElements()}
	 */
	@Test
	public void testGetElements() {
		assertEquals(elements, schema.getElements());
	}

	/**
	 * Test method for {@link Schema#getAttributes()}
	 */
	@Test
	public void testGetAttributes() {
		assertEquals(attributes, schema.getAttributes());
	}

	/**
	 * Test method for {@link Schema#getComplexTypes()}
	 */
	@Test
	public void testGetComplexTypes() {
		assertEquals(complexTypes, schema.getComplexTypes());
	}

	/**
	 * Test method for {@link Schema#getSimpleTypes()}
	 */
	@Test
	public void testGetSimpleTypes() {
		assertEquals(simpleTypes, schema.getSimpleTypes());
	}

	/**
	 * Test method for {@link Schema#guessMainNamespace(XSDInferenceConfiguration)} 
	 * if no main namespace is provided.
	 */
	@Test
	public void testGuessMainNamespace() {
		XSDInferenceConfiguration configuration = mock(XSDInferenceConfiguration.class);
		assertEquals("", schema.guessMainNamespace(configuration));
	}

	/**
	 * Test method for {@link Schema#guessMainNamespace(XSDInferenceConfiguration)} 
	 * if a main namespace is provided.
	 */
	@Test
	public void testGuessMainNamespaceWhenConfigured() {
		XSDInferenceConfiguration configuration = mock(XSDInferenceConfiguration.class);
		when(configuration.getMainNamespace()).thenReturn("http://otherNamespace.com");
		assertEquals("http://otherNamespace.com", schema.guessMainNamespace(configuration));
	}
	
//	/**
//	 * Test method for {@link Schema#containsUnqualifiedElements()}
//	 */
//	@Test
//	public void testContainsUnqualifiedElementsTrue(){
//		assertTrue(schema.containsUnqualifiedElements());
//	}
	
	/**
	 * Test method for {@link Schema#getSolvedNamespaceMappings()}.
	 */
	@Test
	public void testSolvedPrefixToNamespaceMappings(){
		ImmutableMap<String,String> expectedResult = ImmutableMap.of("http://otherNamespace.com", "other", "", "");
		assertEquals(expectedResult,schema.getSolvedNamespaceMappings());
	}
	
	/**
	 * Test method for {@link Schema#getSolvedNamespaceMappings()} when there are many unprefixed namespaces
	 */
	@Test
	public void testSolvedPrefixToNamespaceMappingsManyUnprefixeds(){
		prefixNamespaceMapping.put("http://yotromas.com", ImmutableSortedSet.of(""));
		ImmutableMap<String,String> expectedResult = ImmutableMap.of("http://otherNamespace.com", "other", "", "","http://yotromas.com","unprefixed1");
		assertEquals(expectedResult,schema.getSolvedNamespaceMappings());
	}
	
	/**
	 * Test method for {@link Schema#getSolvedNamespaceMappings()} when the unprefixed namespace is not the default one
	 */
	@Test
	public void testSolvedPrefixToNamespaceMappingsNonDefaultUnprefixeds(){
		schema.getNamespacesToPossiblePrefixMappingModifiable().remove("");
		schema.getNamespacesToPossiblePrefixMappingModifiable().put("http://yotromas.com", ImmutableSortedSet.of(""));
		ImmutableMap<String,String> expectedResult = ImmutableMap.of("http://otherNamespace.com", "other", "http://yotromas.com","");
		assertEquals(expectedResult,schema.getSolvedNamespaceMappings());
	}
	
	/**
	 * Test method for {@link Schema#getSolvedNamespaceMappings()} when there are namespaces with the same prefix
	 */
	@Test
	public void testSolvedPrefixToNamespaceMappingsChooseBetweenPrefixes(){
		prefixNamespaceMapping.put("http://yotromas.com", ImmutableSortedSet.of("other"));
		ImmutableMap<String,String> expectedResult = ImmutableMap.of("http://otherNamespace.com", "other1", "", "","http://yotromas.com","other2");
		assertEquals(expectedResult,schema.getSolvedNamespaceMappings());
	}
	

	/**
	 * Test method for {@link Schema#getSolvedNamespaceMappings()} when there are namespaces with the same prefix
	 */
	@Test
	public void testSolvedPrefixToNamespaceMappingsManyPrefixesPossible(){
		prefixNamespaceMapping.put("http://otherNamespace.com", ImmutableSortedSet.of("yetAnotherPrefix"));
		ImmutableMap<String,String> expectedResult1 = ImmutableMap.of("http://otherNamespace.com", "other", "", "");
		ImmutableMap<String,String> expectedResult2 = ImmutableMap.of("http://otherNamespace.com", "yetAnotherPrefix", "", "");
		NavigableMap<String, String> result = schema.getSolvedNamespaceMappings();
		assertTrue(result.equals(expectedResult1)||result.equals(expectedResult2));
	}
}
