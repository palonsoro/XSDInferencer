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
package es.upm.dit.xsdinferencer.tests.extraction.extractionimpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.extraction.AttributeListInferencer;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.InferencersFactory;
import es.upm.dit.xsdinferencer.statistics.ComplexTypeStatisticsEntry;
import es.upm.dit.xsdinferencer.statistics.Statistics;

/**
 * Test class for {@link AttributeListInferencer} and its implementation
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class AttributeListInferencerTest {
	
	//Fields for testing
	/**
	 * The used inferencer
	 */
	private AttributeListInferencer attributeListInferencer;
	/**
	 * The inference configuration
	 */
	private XSDInferenceConfiguration config;
	/**
	 * The statistics
	 */
	private Statistics statistics;
	/**
	 * The complex type statistics entry of the parent complex type
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntry;
	/**
	 * The solved namespace URI to prefix mapping
	 */
	private Map<String,String> solvedNamespaceToPrefixMapping;
	
	private String [] testingXMLs ={
			"<test><testElem attr1=\"hola\" attr2=\"1\" attr3=\"4.3\" attr4=\"true\"/></test>",
			"<test><testElem attr1=\"hola\" attr3=\"-5.33\" attr4=\"false\" attr5=\"true\"/></test>",
			"<test><testElem attr1=\"50\" attr2=\"+5\" attr3=\"+4\" attr4=\"0\"/></test>"
	};
	
	
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		
		statistics=mock(Statistics.class);
		
		complexTypeStatisticsEntry=mock(ComplexTypeStatisticsEntry.class);
		Map<ComplexType,ComplexTypeStatisticsEntry> mockMap=mock(Map.class);
		when(mockMap.get(anyObject())).thenReturn(complexTypeStatisticsEntry);
		when(statistics.getComplexTypeInfo()).thenReturn(mockMap);
		when(statistics.getComplexTypeStatisticsEntryByName(anyString())).thenReturn(complexTypeStatisticsEntry);
		
		config=mock(XSDInferenceConfiguration.class);
		when(config.getTypeNamesAncestorsSeparator()).thenReturn("-");
		when(config.getMaxNumberOfDistinctValuesToEnum()).thenReturn(8);
		when(config.getMinNumberOfDistinctValuesToEnum()).thenReturn(0);
		when(config.getSimpleTypeInferencer()).thenReturn(XSDInferenceConfiguration.VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL);
		when(config.getAttributeListInferencer()).thenReturn(XSDInferenceConfiguration.VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL);
		when(config.getGenerateEnumerations()).thenReturn(false);
		when(config.getWorkingFormat()).thenReturn("xml");
		
//		File binRoot= new File(getClass().getResource("/").getFile());
//		String pathToProperties=binRoot.getParent()+"/src/main/resources/inferenceConfiguration.properties";
//		File propertiesFile=new File(pathToProperties);
//		config=new XSDInferenceConfiguration(propertiesFile);
		
		solvedNamespaceToPrefixMapping=ImmutableMap.of("", "");
		
		attributeListInferencer = InferencersFactory.getInstance().getAttributeListInferencerInstance ("_test-_testElem", config, solvedNamespaceToPrefixMapping, statistics);
		for(int i=0;i<testingXMLs.length;i++){
			Document testingDocument = new SAXBuilder().build(new StringReader(testingXMLs[i]));
			List<Attribute> attrList = testingDocument.getRootElement().getChildren().get(0).getAttributes();
			attributeListInferencer.learnAttributeList(attrList, i);
		}
		
	}
	
	/**
	 * Given a namespace and a name, this method looks for an SchemaAttribute whose name and namespace are the ones given.
	 * @param list an SchemaAttribute list
	 * @param namespace the name 
	 * @param name the namespace
	 * @return an SchemaAttribute whose name and namespace are the ones given
	 */
	private SchemaAttribute searchAttribute(List<SchemaAttribute> list,String namespace,String name){
		for(SchemaAttribute schemaAttr: list){
			if(schemaAttr.getNamespace().equals(namespace)&&schemaAttr.getName().equals(name))
				return schemaAttr;
		}
		return null;
	}
	
	/**
	 * This method test that the attr1 SchemaAttribute has been properly created
	 */
	@Test
	public void testAttr1() {
		SchemaAttribute attr = searchAttribute(attributeListInferencer.getAttributesList(),"","attr1");
		assertEquals("attr1",attr.getName());
		assertEquals("",attr.getNamespace());
		assertFalse(attr.isOptional());
		assertEquals("xs:string",attr.getSimpleType().getBuiltinType());
		assertEquals("_test-_testElem-_attr1-SimpleTypeOfAttribute",attr.getSimpleType().getName());//It has a suffix so that attributes of different namespaces do not conflict
		assertFalse(attr.getSimpleType().isEnum());
		
		verify(statistics).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr1", 0);
		verify(statistics).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr1", 1);
		verify(statistics).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr1", 2);
		verify(statistics).registerValueAtPathCount("/test/testElem/@attr1", "hola", 0);
		verify(statistics).registerValueAtPathCount("/test/testElem/@attr1", "hola", 1);
		verify(statistics).registerValueAtPathCount("/test/testElem/@attr1", "50", 2);
		
		verify(complexTypeStatisticsEntry).registerAttributeOccurrenceInfoCount(attr, 0);
		verify(complexTypeStatisticsEntry).registerAttributeOccurrenceInfoCount(attr, 1);
		verify(complexTypeStatisticsEntry).registerAttributeOccurrenceInfoCount(attr, 2);
		verify(complexTypeStatisticsEntry).registerValueOfNodeCount("hola", attr, 0);
		verify(complexTypeStatisticsEntry).registerValueOfNodeCount("hola", attr, 1);
		verify(complexTypeStatisticsEntry).registerValueOfNodeCount("50", attr, 2);
		
	}
	
	/**
	 * This method test that the attr2 SchemaAttribute has been properly created
	 */
	@Test
	public void testAttr2() {
		SchemaAttribute attr = searchAttribute(attributeListInferencer.getAttributesList(),"","attr2");
		assertEquals("attr2",attr.getName());
		assertEquals("",attr.getNamespace());
		assertTrue(attr.isOptional());
		assertEquals("xs:integer",attr.getSimpleType().getBuiltinType());
		assertEquals("_test-_testElem-_attr2-SimpleTypeOfAttribute",attr.getSimpleType().getName());//It has a suffix so that attributes of different namespaces do not conflict
		assertFalse(attr.getSimpleType().isEnum());
		
		verify(statistics).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr2", 0);
		verify(statistics,never()).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr2", 1);
		verify(statistics).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr2", 2);
		verify(statistics).registerValueAtPathCount("/test/testElem/@attr2", "1", 0);
		verify(statistics,never()).registerValueAtPathCount(eq("/test/testElem/@attr2"), anyString(), eq(1));
		verify(statistics).registerValueAtPathCount("/test/testElem/@attr2", "+5", 2);
		
		verify(complexTypeStatisticsEntry).registerAttributeOccurrenceInfoCount(attr, 0);
		verify(complexTypeStatisticsEntry,never()).registerAttributeOccurrenceInfoCount(attr, 1);
		verify(complexTypeStatisticsEntry).registerAttributeOccurrenceInfoCount(attr, 2);
		verify(complexTypeStatisticsEntry).registerValueOfNodeCount("1", attr, 0);
		verify(complexTypeStatisticsEntry,never()).registerValueOfNodeCount(anyString(), eq(attr), eq(1));
		verify(complexTypeStatisticsEntry).registerValueOfNodeCount("+5", attr, 2);
	}
	
	/**
	 * This method test that the attr3 SchemaAttribute has been properly created
	 */
	@Test
	public void testAttr3() {
		SchemaAttribute attr = searchAttribute(attributeListInferencer.getAttributesList(),"","attr3");
		assertEquals("attr3",attr.getName());
		assertEquals("",attr.getNamespace());
		assertFalse(attr.isOptional());
		assertEquals("xs:decimal",attr.getSimpleType().getBuiltinType());
		assertEquals("_test-_testElem-_attr3-SimpleTypeOfAttribute",attr.getSimpleType().getName());//It has a suffix so that attributes of different namespaces do not conflict
		assertFalse(attr.getSimpleType().isEnum());
		
		verify(statistics).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr3", 0);
		verify(statistics).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr3", 1);
		verify(statistics).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr3", 2);
		verify(statistics).registerValueAtPathCount("/test/testElem/@attr3", "4.3", 0);
		verify(statistics).registerValueAtPathCount("/test/testElem/@attr3", "-5.33", 1);
		verify(statistics).registerValueAtPathCount("/test/testElem/@attr3", "+4", 2);
		
		verify(complexTypeStatisticsEntry).registerAttributeOccurrenceInfoCount(attr, 0);
		verify(complexTypeStatisticsEntry).registerAttributeOccurrenceInfoCount(attr, 1);
		verify(complexTypeStatisticsEntry).registerAttributeOccurrenceInfoCount(attr, 2);
		verify(complexTypeStatisticsEntry).registerValueOfNodeCount("4.3", attr, 0);
		verify(complexTypeStatisticsEntry).registerValueOfNodeCount("-5.33", attr, 1);
		verify(complexTypeStatisticsEntry).registerValueOfNodeCount("+4", attr, 2);
	}
	
	/**
	 * This method test that the attr4 SchemaAttribute has been properly created
	 */
	@Test
	public void testAttr4() {
		SchemaAttribute attr = searchAttribute(attributeListInferencer.getAttributesList(),"","attr4");
		assertEquals("attr4",attr.getName());
		assertEquals("",attr.getNamespace());
		assertFalse(attr.isOptional());
		assertEquals("xs:boolean",attr.getSimpleType().getBuiltinType());
		assertEquals("_test-_testElem-_attr4-SimpleTypeOfAttribute",attr.getSimpleType().getName());
		assertFalse(attr.getSimpleType().isEnum());
		
		verify(statistics).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr4", 0);
		verify(statistics).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr4", 1);
		verify(statistics).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr4", 2);
		verify(statistics).registerValueAtPathCount("/test/testElem/@attr4", "true", 0);
		verify(statistics).registerValueAtPathCount("/test/testElem/@attr4", "false", 1);
		verify(statistics).registerValueAtPathCount("/test/testElem/@attr4", "0", 2);
		
		verify(complexTypeStatisticsEntry).registerAttributeOccurrenceInfoCount(attr, 0);
		verify(complexTypeStatisticsEntry).registerAttributeOccurrenceInfoCount(attr, 1);
		verify(complexTypeStatisticsEntry).registerAttributeOccurrenceInfoCount(attr, 2);
		verify(complexTypeStatisticsEntry).registerValueOfNodeCount("true", attr, 0);
		verify(complexTypeStatisticsEntry).registerValueOfNodeCount("false", attr, 1);
		verify(complexTypeStatisticsEntry).registerValueOfNodeCount("0", attr, 2);
	}
	
	/**
	 * This method test that the attr5 SchemaAttribute has been properly created
	 */
	@Test
	public void testAttr5() {
		SchemaAttribute attr = searchAttribute(attributeListInferencer.getAttributesList(),"","attr5");
		assertEquals("attr5",attr.getName());
		assertEquals("",attr.getNamespace());
		assertTrue(attr.isOptional());
		assertEquals("xs:boolean",attr.getSimpleType().getBuiltinType());
		assertEquals("_test-_testElem-_attr5-SimpleTypeOfAttribute",attr.getSimpleType().getName());//It has a suffix so that attributes of different namespaces do not conflict
		assertFalse(attr.getSimpleType().isEnum());
		
		verify(statistics,never()).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr5", 0);
		verify(statistics).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr5", 1);
		verify(statistics,never()).registerAttributeOccurrenceAtPathCount("/test/testElem/@attr5", 2);
		verify(statistics,never()).registerValueAtPathCount(eq("/test/testElem/@attr5"), anyString(), eq(0));
		verify(statistics).registerValueAtPathCount("/test/testElem/@attr5", "true", 1);
		verify(statistics,never()).registerValueAtPathCount(eq("/test/testElem/@attr5"), anyString(), eq(2));
		
		verify(complexTypeStatisticsEntry,never()).registerAttributeOccurrenceInfoCount(attr, 0);
		verify(complexTypeStatisticsEntry).registerAttributeOccurrenceInfoCount(attr, 1);
		verify(complexTypeStatisticsEntry,never()).registerAttributeOccurrenceInfoCount(attr, 2);
		verify(complexTypeStatisticsEntry,never()).registerValueOfNodeCount(anyString(), eq(attr), eq(0));
		verify(complexTypeStatisticsEntry).registerValueOfNodeCount("true", attr, 1);
		verify(complexTypeStatisticsEntry,never()).registerValueOfNodeCount(anyString(), eq(attr), eq(2));
	}
	
	/**
	 * This test checks that, after learning some empty attribute lists, the inferencer returns an empty list
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testNoAttributes(){
		Statistics statisticsEmpty=mock(Statistics.class);
		
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryEmpty=mock(ComplexTypeStatisticsEntry.class);		
		Map<ComplexType,ComplexTypeStatisticsEntry> mockMap=mock(Map.class);
		when(mockMap.get(anyObject())).thenReturn(complexTypeStatisticsEntryEmpty);
		when(statisticsEmpty.getComplexTypeInfo()).thenReturn(mockMap);
		when(statisticsEmpty.getComplexTypeStatisticsEntryByName(anyString())).thenReturn(complexTypeStatisticsEntryEmpty);
		
		
		attributeListInferencer = InferencersFactory.getInstance().getAttributeListInferencerInstance ("_test-_testElem", config, solvedNamespaceToPrefixMapping, statisticsEmpty);
		List<Attribute> emptyAttributeList = Collections.emptyList();
		attributeListInferencer.learnAttributeList(emptyAttributeList, 0);
		attributeListInferencer.learnAttributeList(emptyAttributeList, 0);
		attributeListInferencer.learnAttributeList(emptyAttributeList, 1);
		attributeListInferencer.learnAttributeList(emptyAttributeList, 2);
		attributeListInferencer.learnAttributeList(emptyAttributeList, 2);
		attributeListInferencer.learnAttributeList(emptyAttributeList, 3);
		attributeListInferencer.learnAttributeList(emptyAttributeList, 4);
		List<SchemaAttribute> emptySchemaAttribtueList = attributeListInferencer.getAttributesList();
		assertTrue(emptySchemaAttribtueList.isEmpty());
		
		verifyZeroInteractions(complexTypeStatisticsEntryEmpty);
		verify(statisticsEmpty,never()).registerAttributeOccurrenceAtPathCount(anyString(), anyInt());
		verify(statisticsEmpty,never()).registerValueAtPathCount(anyString(), anyString(), anyInt());
		
	}
	
	/**
	 * This test checks that, after learning no attribute lists, the inferencer returns an empty list
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testNoAttributeLearned(){
		Statistics statisticsEmpty=mock(Statistics.class);
		
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryEmpty=mock(ComplexTypeStatisticsEntry.class);		
		Map<ComplexType,ComplexTypeStatisticsEntry> mockMap=mock(Map.class);
		when(mockMap.get(anyObject())).thenReturn(complexTypeStatisticsEntryEmpty);
		when(statisticsEmpty.getComplexTypeInfo()).thenReturn(mockMap);
		when(statisticsEmpty.getComplexTypeStatisticsEntryByName(anyString())).thenReturn(complexTypeStatisticsEntryEmpty);
		
		
		attributeListInferencer = InferencersFactory.getInstance().getAttributeListInferencerInstance ("_test-_testElem", config, solvedNamespaceToPrefixMapping, statisticsEmpty);
		List<SchemaAttribute> emptySchemaAttribtueList = attributeListInferencer.getAttributesList();
		assertTrue(emptySchemaAttribtueList.isEmpty());
		
		verifyZeroInteractions(complexTypeStatisticsEntryEmpty);
		verify(statisticsEmpty,never()).registerAttributeOccurrenceAtPathCount(anyString(), anyInt());
		verify(statisticsEmpty,never()).registerValueAtPathCount(anyString(), anyString(), anyInt());
	}

}
