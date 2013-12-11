package es.upm.dit.xsdinferencer.tests.merge.mergerimpl;

import static com.google.common.base.Preconditions.checkArgument;
import static es.upm.dit.xsdinferencer.datastructures.Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.SchemaNode;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.merge.AttributeListComparator;
import es.upm.dit.xsdinferencer.merge.ChildrenPatternComparator;
import es.upm.dit.xsdinferencer.merge.EnumComparator;
import es.upm.dit.xsdinferencer.merge.TypeMerger;
import es.upm.dit.xsdinferencer.merge.mergerimpl.TypeMergerImpl;
import es.upm.dit.xsdinferencer.statistics.BasicStatisticsEntry;
import es.upm.dit.xsdinferencer.statistics.ComplexTypeStatisticsEntry;
import es.upm.dit.xsdinferencer.statistics.Statistics;
import es.upm.dit.xsdinferencer.statistics.ValueAndFrequency;

/**
 * Test of {@link TypeMerger} and its implementation {@link TypeMergerImpl}.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class TypeMergerImplTest {
	//Fields for testing
	
	/**
	 * Initial state of automatons
	 */
	private SchemaElement initialState;
	/**
	 * Final state of automatons
	 */
	private SchemaElement finalState;
	
	//First scenario fields
	
	/**
	 * Complex type of the root of the documents of the first scenario. 
	 * It only allows either a child called element1 whose type is complexTypeElement1 
	 * or a child called element2 whose type is complexTypeElement2. 
	 * 
	 */
	private ComplexType complexTypeScenario1Root;
	
	/**
	 * First complex type to merge on the first scenario
	 */
	private ComplexType complexTypeScenario1RootElement1;
	
	/**
	 * SimpleType of attr1 on element1 on scenario 1
	 */
	private SimpleType simpleTypeScenario1Element1Attr1;

	/**
	 * SimpleType of attr2 on element1 on scenario 1
	 */
	private SimpleType simpleTypeScenario1Element1Attr2;

	/**
	 * SimpleType of attr3 on element1 on scenario 1
	 */
	private SimpleType simpleTypeScenario1Element1Attr3;
	
	/**
	 * Complex type of node A in the automaton of complexTypeScenario1RootElement1 on the first scenario
	 */
	private ComplexType complexTypeScenario1RootElement1NodeA;
	
	/**
	 * Complex type of node B in the automaton of complexTypeScenario1RootElement1 on the first scenario
	 */
	private ComplexType complexTypeScenario1RootElement1NodeB;
	
	/**
	 * Complex type of node C in the automaton of complexTypeScenario1RootElement1 on the first scenario
	 */
	private ComplexType complexTypeScenario1RootElement1NodeC;
	
	/**
	 * Complex type of node E in the automaton of complexTypeScenario1RootElement1NodeC on the first scenario
	 */
	private ComplexType complexTypeScenario1RootElement1NodeCNodeE;
	
	/**
	 * Second complex type to merge on the first scenario
	 */
	private ComplexType complexTypeScenario1RootElement2;
	
	/**
	 * SimpleType of attr1 on element2 on scenario 1
	 */
	private SimpleType simpleTypeScenario1Element2Attr1;

	/**
	 * SimpleType of attr2 on element2 on scenario 1
	 */
	private SimpleType simpleTypeScenario1Element2Attr2;

	/**
	 * SimpleType of attr4 on element 2 on scenario 1
	 */
	private SimpleType simpleTypeScenario1Element2Attr4;
	
	/**
	 * Complex type of node A in the automaton of complexTypeScenario1RootElement2 on the first scenario
	 */
	private ComplexType complexTypeScenario1RootElement2NodeA;
	
	/**
	 * Complex type of node B in the automaton of complexTypeScenario1RootElement2 on the first scenario
	 */
	private ComplexType complexTypeScenario1RootElement2NodeB;
	
	/**
	 * Complex type of node C in the automaton of complexTypeScenario1RootElement2 on the first scenario
	 */
	private ComplexType complexTypeScenario1RootElement2NodeC;
	
	/**
	 * Complex type of node F in the automaton of complexTypeScenario1RootElement2NodeC on the first scenario
	 */
	private ComplexType complexTypeScenario1RootElement2NodeCNodeF;
	
	/**
	 * Complex type of node D in the automaton of complexTypeScenario1RootElement2 on the first scenario
	 */
	private ComplexType complexTypeScenario1RootElement2NodeD;
	
	/**
	 * ComplexTypeStatisticsEntry for complexTypeRootScenario1 on the first scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeScenario1RootStatisticsEntry;
	
	/**
	 * ComplexTypeStatisticsEntry for complexTypeScenario1RootElement1 on the first scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeScenario1RootElement1StatisticsEntry;
	
	/**
	 * ComplexTypeStatisticsEntry for complexTypeScenario1RootElement1NodeA on the first scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeScenario1RootElement1NodeAStatisticsEntry;
	
	/**
	 * ComplexTypeStatisticsEntry for complexTypeScenario1RootElement1NodeB on the first scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeScenario1RootElement1NodeBStatisticsEntry;
	
	/**
	 * ComplexTypeStatisticsEntry for complexTypeScenario1RootElement1NodeC on the first scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeScenario1RootElement1NodeCStatisticsEntry;
	
	/**
	 * ComplexTypeStatisticsEntry for complexTypeScenario1RootElement1NodeCNodeE on the first scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeScenario1RootElement1NodeCNodeEStatisticsEntry;
	
	/**
	 * ComplexTypeStatisticsEntry for complexTypeScenario1RootElement2 on the first scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeScenario1RootElement2StatisticsEntry;
	/**
	 * ComplexTypeStatisticsEntry for complexTypeScenario1RootElement2NodeA on the first scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeScenario1RootElement2NodeAStatisticsEntry;
	
	/**
	 * ComplexTypeStatisticsEntry for complexTypeScenario1RootElement2NodeB on the first scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeScenario1RootElement2NodeBStatisticsEntry;
	
	/**
	 * ComplexTypeStatisticsEntry for complexTypeScenario1RootElement2NodeC on the first scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeScenario1RootElement2NodeCStatisticsEntry;
	
	/**
	 * ComplexTypeStatisticsEntry for complexTypeScenario1RootElement2NodeCNodeF on the first scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeScenario1RootElement2NodeCNodeFStatisticsEntry;
	
	/**
	 * ComplexTypeStatisticsEntry for complexTypeScenario1RootElement2NodeD on the first scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeScenario1RootElement2NodeDStatisticsEntry;
	
	/**
	 * Simple type of complexTypeScenario1RootElement1NodeA on the first scenario
	 */
	private SimpleType simpleTypeScenario1RootElement1NodeA;
	
	/**
	 * Simple type of complexTypeScenario1RootElement1NodeB on the first scenario
	 */
	private SimpleType simpleTypeScenario1RootElement1NodeB;
	
	/**
	 * Simple type of complexTypeScenario1RootElement2NodeA on the first scenario
	 */
	private SimpleType simpleTypeScenario1RootElement2NodeB;
	
	/**
	 * Simple type of complexTypeScenario1RootElement2NodeB on the first scenario
	 */
	private SimpleType simpleTypeScenario1RootElement2NodeA;
	
	/**
	 * Schema element of root on the first scenario
	 */
	private SchemaElement elementScenario1Root;
	
	/**
	 * Schema element element1 on the first scenario
	 */
	private SchemaElement elementScenario1RootElement1;
	
	/**
	 * Schema element A (child of element1) on the first scenario
	 */
	private SchemaElement elementScenario1RootElement1A;
	
	/**
	 * Schema element B (child of element1) on the first scenario
	 */
	private SchemaElement elementScenario1RootElement1B;
	
	/**
	 * Schema element E (child of B) on the first scenario
	 */
	private SchemaElement elementScenario1RootElement1CE;
	
	/**
	 * Schema element C (child of element1) on the first scenario
	 */
	private SchemaElement elementScenario1RootElement1C;
	
	/**
	 * Schema element element2 on the first scenario
	 */
	private SchemaElement elementScenario1RootElement2;
	/**
	 * Schema element A (child of element2) on the first scenario
	 */
	private SchemaElement elementScenario1RootElement2A;
	
	/**
	 * Schema element B (child of element2) on the first scenario
	 */
	private SchemaElement elementScenario1RootElement2B;
	
	/**
	 * Schema element F (child of B) on the first scenario
	 */
	private SchemaElement elementScenario1RootElement2CF;
	
	/**
	 * Schema element C (child of element2) on the first scenario
	 */
	private SchemaElement elementScenario1RootElement2C;
	
	/**
	 * Schema element D (child of element2) on the first scenario
	 */
	private SchemaElement elementScenario1RootElement2D;
	
	/**
	 * Schema attribute attr1 of of element1 on the first scenario
	 */
	private SchemaAttribute attributeScenario1RootElement1Attr1;
	
	/**
	 * Schema attribute attr2 of of element1 on the first scenario
	 */
	private SchemaAttribute attributeScenario1RootElement1Attr2;
	
	/**
	 * Schema attribute attr3 of of element1 on the first scenario
	 */
	private SchemaAttribute attributeScenario1RootElement1Attr3;
	
	/**
	 * Schema attribute attr1 of of element2 on the first scenario
	 */
	private SchemaAttribute attributeScenario1RootElement2Attr1;
	
	/**
	 * Schema attribute attr2 of of element2 on the first scenario
	 */
	private SchemaAttribute attributeScenario1RootElement2Attr2;
	
	/**
	 * Schema attribute attr4 of of element2 on the first scenario
	 */
	private SchemaAttribute attributeScenario1RootElement2Attr4;
	
	/**
	 * The testing schema of the first scenario, some of whose types are to be merged
	 */
	private Schema schema1;
	
	/**
	 * Inference configuration of the first scenario
	 */
	private XSDInferenceConfiguration configuration1;
	
	//Fields of the scenario 2
	
	/**
	 * Complex type _root on the second scenario
	 */
	private ComplexType complexTypeScenario2Root;
	
	/**
	 * Complex type _root-_A on the second scenario
	 */
	private ComplexType complexTypeScenario2RootA;
	
	/**
	 * Complex type _A-_D on the second scenario
	 */
	private ComplexType complexTypeScenario2AD;
	
	/**
	 * Complex type _root-_B on the second scenario
	 */
	private ComplexType complexTypeScenario2RootB;
	
	/**
	 * Complex type _B-_D on the second scenario
	 */
	private ComplexType complexTypeScenario2BD;
	
	/**
	 * Complex type _root-_B on the second scenario
	 */
	private ComplexType complexTypeScenario2RootC;
	
	/**
	 * Complex type _C-_B on the second scenario
	 */
	private ComplexType complexTypeScenario2CB;
	
	/**
	 * Complex type _B-_E on the second scenario
	 */
	private ComplexType complexTypeScenario2BE;
	
	/**
	 * Complex type statistics entry of complex type _root on the second scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario2Root;
	
	/**
	 * Complex type statistics entry of complex type _root-_A on the second scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario2RootA;
	
	/**
	 * Complex type statistics entry of complex type _A-_D on the second scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario2AD;
	
	/**
	 * Complex type statistics entry of complex type _root-_B on the second scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario2RootB;
	
	/**
	 * Complex type statistics entry of complex type _B-_D on the second scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario2BD;
	
	/**
	 * Complex type statistics entry of complex type _root-_B on the second scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario2RootC;
	
	/**
	 * Complex type statistics entry of complex type _C-_B on the second scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario2CB;
	
	/**
	 * Complex type statistics entry of complex type _B-_E on the second scenario
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario2BE;
	
	/**
	 * Simple type of attribute attr of complex type _A-_D on the second scenario
	 */
	private SimpleType simpleTypeScenario2ADAttr;
	
	/**
	 * Simple type of attribute attr2 of complex type _A-_D on the second scenario
	 */
	private SimpleType simpleTypeScenario2ADAttr2;
	
	/**
	 * Simple type of attribute attr of complex type _B-_D on the second scenario
	 */
	private SimpleType simpleTypeScenario2BDAttr;
	
	/**
	 * Simple type of attribute attr2 of complex type _B-_D on the second scenario
	 */
	private SimpleType simpleTypeScenario2BDAttr2;
	
	/**
	 * Element root on the second scenario
	 */
	private SchemaElement elementScenario2Root;
	
	/**
	 * Element a of complex type _root on the second scenario
	 */
	private SchemaElement elementScenario2RootA;
	
	/**
	 * Element d of complex type _root-_a on the second scenario
	 */
	private SchemaElement elementScenario2RootAD;
	
	/**
	 * Element b of complex type _root on the second scenario
	 */
	private SchemaElement elementScenario2RootB;
	
	/**
	 * Element d of complex type _root-_b on the second scenario
	 */
	private SchemaElement elementScenario2RootBD;
	
	/**
	 * Element c of complex type _root on the second scenario
	 */
	private SchemaElement elementScenario2RootC;
	
	/**
	 * Element b of complex type _root-_c on the second scenario
	 */
	private SchemaElement elementScenario2RootCB;
	
	/**
	 * Element d of complex type _root-_b on the second scenario
	 */
	private SchemaElement elementScenario2CBD;
	
	/**
	 * Element d of complex type _c-_b on the second scenario
	 */
	private SchemaElement elementScenario2CBE;
	
	/**
	 * Attribute attr of complex type _A-_D on the second scenario
	 */
	private SchemaAttribute attributeScenario2ADAttr;
	
	/**
	 * Attribute attr of complex type _A-_D on the second scenario
	 */
	private SchemaAttribute attributeScenario2ADAttr2;
	
	/**
	 * Attribute attr of complex type _B-_D on the second scenario
	 */
	private SchemaAttribute attributeScenario2BDAttr;
	
	/**
	 * Attribute attr2 of complex type _B-_D on the second scenario
	 */
	private SchemaAttribute attributeScenario2BDAttr2;
	
	/**
	 * The testing schema of the second scenario, some of whose types are to be merged
	 */
	private Schema schema2;
	
	/**
	 * Inference configuration of the second scenario
	 */
	private XSDInferenceConfiguration configuration2;
	
	//Some other fields
	
	/**
	 * Mock comparator that always returns false.
	 */
	private ChildrenPatternComparator alwaysFalseChildrenPatternComparator;
	
	/**
	 * Mock comparator that always returns false.
	 */
	private AttributeListComparator alwaysFalseAttributeListComparator;
	
	/**
	 * Mock comparator that always returns true.
	 */
	private AttributeListComparator alwaysTrueAttributeListComparator;
	
	/**
	 * Mock comparator that always returns false.
	 */
	private EnumComparator alwaysFalseEnumComparator;
	
	
	/**
	 * This method registers one count of an SchemaNode on 'documentCount' consecutive documents, starting 
	 * at the 'startingDocumentIndex'th document index into a {@link ComplexTypeStatisticsEntry}. 
	 * @param entry {@link ComplexTypeStatisticsEntry} where the counts will be registered 
	 * @param node the node whose count will be registered
	 * @param startingDocumentIndex
	 * @param documentCount
	 */
	private void registerMultipleNodeCount(ComplexTypeStatisticsEntry entry, SchemaNode node, int startingDocumentIndex, int documentCount){
		checkArgument(documentCount>0);
		for(int i=startingDocumentIndex;i<(startingDocumentIndex+documentCount);i++){
			boolean isSchemaElement = node instanceof SchemaElement;
			boolean isSchemaAttribute = node instanceof SchemaAttribute;
			if(isSchemaElement){
				entry.registerElementCount((SchemaElement) node, i);
			}
			else if(isSchemaAttribute){
				entry.registerAttributeOccurrenceInfoCount((SchemaAttribute) node, i);
			}
			else{
				throw new IllegalArgumentException("Unknown kind of SchemaNode: "+node.getClass().getName());
			}
		}
	}

	/**
	 * Given a list of values and an index, it registers an occurrence of each value at position <i>i</i> at 
	 * the document <i>startingIndex</i>+<i>i</i>
	 * @param entry the complex type statistics entry where values will be registered
	 * @param values the list of values
	 * @param startingDocumentIndex The starting document index
	 * @param sourceNode The source node
	 */
	private void registerMultipleValuesAtComplexTypeStatisticsEntry(ComplexTypeStatisticsEntry entry, List<String> values, int startingDocumentIndex, SchemaNode sourceNode){
		for(int i=0; i<values.size(); i++){
			entry.registerValueOfNodeCount(values.get(i), sourceNode, startingDocumentIndex+i);
		}
	}
	
	/**
	 * It builds the complex type statistics entries for the complex types. We do not build other statistics 
	 * because they have nothing to do with this module (and are tested elsewhere).
	 * Statistic values are registered while creating the related Elements/Attributes/SimpleTypes (for values) 
	 */
	private void buildComplexTypeStatisticsEntriesScenario1(){
		complexTypeScenario1RootStatisticsEntry= new ComplexTypeStatisticsEntry(16);
		complexTypeScenario1RootElement1StatisticsEntry= new ComplexTypeStatisticsEntry(16);
		complexTypeScenario1RootElement1NodeAStatisticsEntry= new ComplexTypeStatisticsEntry(16);
		complexTypeScenario1RootElement1NodeBStatisticsEntry= new ComplexTypeStatisticsEntry(16);
		complexTypeScenario1RootElement1NodeCStatisticsEntry= new ComplexTypeStatisticsEntry(16);
		complexTypeScenario1RootElement1NodeCNodeEStatisticsEntry= new ComplexTypeStatisticsEntry(16);
		complexTypeScenario1RootElement2StatisticsEntry= new ComplexTypeStatisticsEntry(16);
		complexTypeScenario1RootElement2NodeAStatisticsEntry= new ComplexTypeStatisticsEntry(16);
		complexTypeScenario1RootElement2NodeBStatisticsEntry= new ComplexTypeStatisticsEntry(16);
		complexTypeScenario1RootElement2NodeCStatisticsEntry= new ComplexTypeStatisticsEntry(16);
		complexTypeScenario1RootElement2NodeCNodeFStatisticsEntry= new ComplexTypeStatisticsEntry(16);
		complexTypeScenario1RootElement2NodeDStatisticsEntry= new ComplexTypeStatisticsEntry(16);
	}
	
	/**
	 * It builds the non-trivial SimpleType objects used in the first scenario
	 */
	private void buildSimpleTypesScenario1() {
		
		List<String> simpleTypeElement1NodeAValues = Lists.newArrayList("cuarenta","50","60","70");
		simpleTypeScenario1RootElement1NodeA=new SimpleType("_root-_element1-_A","xs:string",simpleTypeElement1NodeAValues,true);
		
		List<String> simpleTypeElement1NodeBValues = Lists.newArrayList("hola","saludos","50");
		simpleTypeScenario1RootElement1NodeB=new SimpleType("_root-_element1-_B","xs:string",simpleTypeElement1NodeBValues,true);
		
		List<String> simpleTypeElement2NodeBValues = Lists.newArrayList("40","50","60","70");
		simpleTypeScenario1RootElement2NodeB=new SimpleType("_root-_element2-_B","xs:integer",simpleTypeElement2NodeBValues,true);
		
		List<String> simpleTypeElement2NodeAValues = Lists.newArrayList("buenos dias","saludos");
		simpleTypeScenario1RootElement2NodeA=new SimpleType("_root-_element2-_A","xs:string",simpleTypeElement2NodeAValues,true);
		
		List<String> simpleTypeElement1Attr1Values = Lists.newArrayList("alfa","beta");
		simpleTypeScenario1Element1Attr1 =  new SimpleType("_root-_element1-attr1-SimpleTypeOfAttribute","xs:string",simpleTypeElement1Attr1Values,false);
		List<String> simpleTypeElement1Attr2Values = Lists.newArrayList("1","2");
		simpleTypeScenario1Element1Attr2 =  new SimpleType("_root-_element1-attr2-SimpleTypeOfAttribute","xs:integer",simpleTypeElement1Attr2Values,false);
		List<String> simpleTypeElement1Attr3Values = Lists.newArrayList("true","false","0","1");
		simpleTypeScenario1Element1Attr3 =  new SimpleType("_root-_element1-attr3-SimpleTypeOfAttribute","xs:boolean",simpleTypeElement1Attr3Values,false);
		List<String> simpleTypeElement2Attr1Values = Lists.newArrayList("lambda","omega");
		simpleTypeScenario1Element2Attr1 =  new SimpleType("_root-_element2-attr1-SimpleTypeOfAttribute","xs:string",simpleTypeElement2Attr1Values,false);
		List<String> simpleTypeElement2Attr2Values = Lists.newArrayList("3","4");
		simpleTypeScenario1Element2Attr2 =  new SimpleType("_root-_element2-attr2-SimpleTypeOfAttribute","xs:integer",simpleTypeElement2Attr2Values,false);
		List<String> simpleTypeElement2Attr4Values = Lists.newArrayList("gamma","epsilon","omicron","zeta");
		simpleTypeScenario1Element2Attr4 =  new SimpleType("_root-_element2-attr4-SimpleTypeOfAttribute","xs:string",simpleTypeElement2Attr4Values,false);
	
	}

	/**
	 * It builds the SchemaAttribute objects used in the first scenario
	 */
	private void buildSchemaAttributesScenario1() {
//		attributeScenario1RootElement1Attr1=new SchemaAttribute("attr1", "", false, new SimpleType("_root-_element1-@_attr1"));
//		registerMultipleNodeCount(complexTypeScenario1RootElement1StatisticsEntry, attributeScenario1RootElement1Attr1, 0, 8);
//		attributeScenario1RootElement1Attr2=new SchemaAttribute("attr2", "", false, new SimpleType("_root-_element1-@_attr2"));
//		registerMultipleNodeCount(complexTypeScenario1RootElement1StatisticsEntry, attributeScenario1RootElement1Attr2, 0, 8);
//		attributeScenario1RootElement1Attr3=new SchemaAttribute("attr3", "", false, new SimpleType("_root-_element1-@_attr3"));
//		registerMultipleNodeCount(complexTypeScenario1RootElement1StatisticsEntry, attributeScenario1RootElement1Attr3, 0, 8);
//		
//		attributeScenario1RootElement2Attr1=new SchemaAttribute("attr1", "", false, new SimpleType("_root-_element2-@_attr1"));
//		registerMultipleNodeCount(complexTypeScenario1RootElement2StatisticsEntry, attributeScenario1RootElement2Attr1, 8, 8);
//		attributeScenario1RootElement2Attr2=new SchemaAttribute("attr2", "", true, new SimpleType("_root-_element2-@_attr2"));
//		registerMultipleNodeCount(complexTypeScenario1RootElement2StatisticsEntry, attributeScenario1RootElement2Attr2, 8, 4);
//		attributeScenario1RootElement2Attr4=new SchemaAttribute("attr4", "", true, new SimpleType("_root-_element2-@_attr4"));
//		registerMultipleNodeCount(complexTypeScenario1RootElement2StatisticsEntry, attributeScenario1RootElement2Attr4, 12, 2);
		
		attributeScenario1RootElement1Attr1=new SchemaAttribute("attr1", "", false, simpleTypeScenario1Element1Attr1);
		registerMultipleNodeCount(complexTypeScenario1RootElement1StatisticsEntry, attributeScenario1RootElement1Attr1, 0, 8);
		attributeScenario1RootElement1Attr2=new SchemaAttribute("attr2", "", false, simpleTypeScenario1Element1Attr2);
		registerMultipleNodeCount(complexTypeScenario1RootElement1StatisticsEntry, attributeScenario1RootElement1Attr2, 0, 8);
		attributeScenario1RootElement1Attr3=new SchemaAttribute("attr3", "", false, simpleTypeScenario1Element1Attr3);
		registerMultipleNodeCount(complexTypeScenario1RootElement1StatisticsEntry, attributeScenario1RootElement1Attr3, 0, 8);
		
		attributeScenario1RootElement2Attr1=new SchemaAttribute("attr1", "", false, simpleTypeScenario1Element2Attr1);
		registerMultipleNodeCount(complexTypeScenario1RootElement2StatisticsEntry, attributeScenario1RootElement2Attr1, 8, 8);
		attributeScenario1RootElement2Attr2=new SchemaAttribute("attr2", "", true, simpleTypeScenario1Element2Attr2);
		registerMultipleNodeCount(complexTypeScenario1RootElement2StatisticsEntry, attributeScenario1RootElement2Attr2, 8, 4);
		attributeScenario1RootElement2Attr4=new SchemaAttribute("attr4", "", true, simpleTypeScenario1Element2Attr4);
		registerMultipleNodeCount(complexTypeScenario1RootElement2StatisticsEntry, attributeScenario1RootElement2Attr4, 12, 4);
	}

	/**
	 * It builds the complex types of the scenario 1
	 */
	private void buildComplexTypesScenario1() {
		complexTypeScenario1Root = new ComplexType("_root",new ExtendedAutomaton(),new SimpleType("root"),new ArrayList<SchemaAttribute>());
		
		complexTypeScenario1RootElement1 = new ComplexType("_root-_element1",new ExtendedAutomaton(),new SimpleType("_root-_element1"),Lists.newArrayList(attributeScenario1RootElement1Attr1,attributeScenario1RootElement1Attr2,attributeScenario1RootElement1Attr3));
		complexTypeScenario1RootElement1.getComments().add("Comentario 1");
		complexTypeScenario1RootElement1NodeA = new ComplexType("_root-_element1-_A",new ExtendedAutomaton(),simpleTypeScenario1RootElement1NodeA,new ArrayList<SchemaAttribute>());
		complexTypeScenario1RootElement1NodeB = new ComplexType("_root-_element1-_B",new ExtendedAutomaton(),simpleTypeScenario1RootElement1NodeB,new ArrayList<SchemaAttribute>());
		complexTypeScenario1RootElement1NodeC = new ComplexType("_root-_element1-_C",new ExtendedAutomaton(),new SimpleType("_root-_element1-_C"),new ArrayList<SchemaAttribute>());
		complexTypeScenario1RootElement1NodeCNodeE = new ComplexType("_root-_element1-_C-_E",new ExtendedAutomaton(),new SimpleType("_root-_element1-_C"),new ArrayList<SchemaAttribute>());
		
		complexTypeScenario1RootElement2 = new ComplexType("_root-_element2",new ExtendedAutomaton(),new SimpleType("_root-_element2"),Lists.newArrayList(attributeScenario1RootElement2Attr1,attributeScenario1RootElement2Attr2,attributeScenario1RootElement2Attr4));
		complexTypeScenario1RootElement2.getComments().add("Comentario 2");
		complexTypeScenario1RootElement2NodeA = new ComplexType("_root-_element2-_A",new ExtendedAutomaton(),simpleTypeScenario1RootElement2NodeA,new ArrayList<SchemaAttribute>());
		complexTypeScenario1RootElement2NodeB = new ComplexType("_root-_element2-_B",new ExtendedAutomaton(),simpleTypeScenario1RootElement2NodeB,new ArrayList<SchemaAttribute>());
		complexTypeScenario1RootElement2NodeC = new ComplexType("_root-_element2-_C",new ExtendedAutomaton(),new SimpleType("_root-_element2-_C"),new ArrayList<SchemaAttribute>());
		complexTypeScenario1RootElement2NodeCNodeF = new ComplexType("_root-_element2-_C-_F",new ExtendedAutomaton(),new SimpleType("_root-_element2-_F"),new ArrayList<SchemaAttribute>());
		complexTypeScenario1RootElement2NodeD = new ComplexType("_root-_element2-_D",new ExtendedAutomaton(),new SimpleType("_root-_element2-_D"),new ArrayList<SchemaAttribute>());
	}

	/**
	 * It builds the SchemaElement objects used in first scenario
	 */
	private void buildSchemaElementsScenario1() {
		elementScenario1Root=new SchemaElement("root","",complexTypeScenario1Root);
		elementScenario1Root.setValidRoot(true);
		
		elementScenario1RootElement1=new SchemaElement("element1","",complexTypeScenario1RootElement1);
		complexTypeScenario1RootElement1.addSourceNodeNamespaceAndName(elementScenario1RootElement1.getNamespace(), elementScenario1RootElement1.getName());
		elementScenario1RootElement1A=new SchemaElement("A","",complexTypeScenario1RootElement1NodeA);
		complexTypeScenario1RootElement1NodeA.addSourceNodeNamespaceAndName(elementScenario1RootElement1A.getNamespace(), elementScenario1RootElement1A.getName());
		simpleTypeScenario1RootElement1NodeA.addSourceNodeNamespaceAndName(elementScenario1RootElement1A.getNamespace(), elementScenario1RootElement1A.getName());
		elementScenario1RootElement1B=new SchemaElement("B","",complexTypeScenario1RootElement1NodeB);
		complexTypeScenario1RootElement1NodeB.addSourceNodeNamespaceAndName(elementScenario1RootElement1B.getNamespace(), elementScenario1RootElement1B.getName());
		simpleTypeScenario1RootElement1NodeB.addSourceNodeNamespaceAndName(elementScenario1RootElement1B.getNamespace(), elementScenario1RootElement1B.getName());
		elementScenario1RootElement1C=new SchemaElement("C","",complexTypeScenario1RootElement1NodeC);
		complexTypeScenario1RootElement1NodeC.addSourceNodeNamespaceAndName(elementScenario1RootElement1C.getNamespace(), elementScenario1RootElement1C.getName());
		elementScenario1RootElement1CE=new SchemaElement("E","",complexTypeScenario1RootElement1NodeCNodeE);
		complexTypeScenario1RootElement1NodeCNodeE.addSourceNodeNamespaceAndName(elementScenario1RootElement1CE.getNamespace(), elementScenario1RootElement1CE.getName());
		
		elementScenario1RootElement2=new SchemaElement("element2","",complexTypeScenario1RootElement2);
		complexTypeScenario1RootElement2.addSourceNodeNamespaceAndName(elementScenario1RootElement2.getNamespace(), elementScenario1RootElement2.getName());
		elementScenario1RootElement2A=new SchemaElement("A","",complexTypeScenario1RootElement2NodeA);
		complexTypeScenario1RootElement2NodeA.addSourceNodeNamespaceAndName(elementScenario1RootElement2A.getNamespace(), elementScenario1RootElement2A.getName());
		simpleTypeScenario1RootElement2NodeA.addSourceNodeNamespaceAndName(elementScenario1RootElement2A.getNamespace(), elementScenario1RootElement2A.getName());
		elementScenario1RootElement2B=new SchemaElement("B","",complexTypeScenario1RootElement2NodeB);
		complexTypeScenario1RootElement2NodeB.addSourceNodeNamespaceAndName(elementScenario1RootElement2B.getNamespace(), elementScenario1RootElement2B.getName());
		simpleTypeScenario1RootElement2NodeB.addSourceNodeNamespaceAndName(elementScenario1RootElement2B.getNamespace(), elementScenario1RootElement2B.getName());
		elementScenario1RootElement2C=new SchemaElement("C","",complexTypeScenario1RootElement2NodeC);
		complexTypeScenario1RootElement2NodeC.addSourceNodeNamespaceAndName(elementScenario1RootElement2C.getNamespace(), elementScenario1RootElement2C.getName());
		elementScenario1RootElement2CF=new SchemaElement("F","",complexTypeScenario1RootElement2NodeCNodeF);
		complexTypeScenario1RootElement2NodeCNodeF.addSourceNodeNamespaceAndName(elementScenario1RootElement2CF.getNamespace(), elementScenario1RootElement2CF.getName());
		elementScenario1RootElement2D=new SchemaElement("D","",complexTypeScenario1RootElement2NodeD);
		complexTypeScenario1RootElement2NodeD.addSourceNodeNamespaceAndName(elementScenario1RootElement2D.getNamespace(), elementScenario1RootElement2D.getName());
	}

	/**
	 * This helper method registers all the value occurrences on the statistics of the scenario 1
	 */
	private void registerValuesOccurrencesScenario1(){
		List <String> simpleTypeElement1NodeAValueOccurrences = simpleTypeScenario1RootElement1NodeA.getKnownValuesUnmodifiableList();
		List <String> simpleTypeElement1NodeBValueOccurrences = new ArrayList<>(simpleTypeScenario1RootElement1NodeB.getKnownValuesUnmodifiableList());
		simpleTypeElement1NodeBValueOccurrences.add("50");
		List <String> simpleTypeElement2NodeAValueOccurrences = simpleTypeScenario1RootElement2NodeA.getKnownValuesUnmodifiableList();
		List <String> simpleTypeElement2NodeBValueOccurrences = simpleTypeScenario1RootElement2NodeB.getKnownValuesUnmodifiableList();
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement1NodeAStatisticsEntry, simpleTypeElement1NodeAValueOccurrences, 0, elementScenario1RootElement1A);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement1NodeBStatisticsEntry, simpleTypeElement1NodeBValueOccurrences, 4, elementScenario1RootElement1B);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement2NodeAStatisticsEntry, simpleTypeElement2NodeAValueOccurrences, 8, elementScenario1RootElement2A);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement2NodeAStatisticsEntry, simpleTypeElement2NodeAValueOccurrences, 10, elementScenario1RootElement2A);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement2NodeAStatisticsEntry, simpleTypeElement2NodeAValueOccurrences, 12, elementScenario1RootElement2A);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement2NodeAStatisticsEntry, simpleTypeElement2NodeAValueOccurrences, 14, elementScenario1RootElement2A);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement2NodeBStatisticsEntry, simpleTypeElement2NodeBValueOccurrences, 8, elementScenario1RootElement2B);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement2NodeBStatisticsEntry, simpleTypeElement2NodeBValueOccurrences, 12, elementScenario1RootElement2B);
		
		List <String> simpleTypeScenario1Element1Attr1ValueOccurrences = simpleTypeScenario1Element1Attr1.getKnownValuesUnmodifiableList();
		List <String> simpleTypeScenario1Element1Attr2ValueOccurrences = simpleTypeScenario1Element1Attr2.getKnownValuesUnmodifiableList();
		List <String> simpleTypeScenario1Element1Attr3ValueOccurrences = simpleTypeScenario1Element1Attr3.getKnownValuesUnmodifiableList();
		List <String> simpleTypeScenario1Element2Attr1ValueOccurrences = simpleTypeScenario1Element2Attr1.getKnownValuesUnmodifiableList();
		List <String> simpleTypeScenario1Element2Attr2ValueOccurrences = simpleTypeScenario1Element2Attr2.getKnownValuesUnmodifiableList();
		List <String> simpleTypeScenario1Element2Attr4ValueOccurrences = simpleTypeScenario1Element2Attr4.getKnownValuesUnmodifiableList();
		
		//attr1 at element1
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement1StatisticsEntry, simpleTypeScenario1Element1Attr1ValueOccurrences, 0, attributeScenario1RootElement1Attr1);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement1StatisticsEntry, simpleTypeScenario1Element1Attr1ValueOccurrences, 2, attributeScenario1RootElement1Attr1);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement1StatisticsEntry, simpleTypeScenario1Element1Attr1ValueOccurrences, 4, attributeScenario1RootElement1Attr1);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement1StatisticsEntry, simpleTypeScenario1Element1Attr1ValueOccurrences, 6, attributeScenario1RootElement1Attr1);
		
		//attr2 at element1
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement1StatisticsEntry, simpleTypeScenario1Element1Attr2ValueOccurrences, 0, attributeScenario1RootElement1Attr2);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement1StatisticsEntry, simpleTypeScenario1Element1Attr2ValueOccurrences, 2, attributeScenario1RootElement1Attr2);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement1StatisticsEntry, simpleTypeScenario1Element1Attr2ValueOccurrences, 4, attributeScenario1RootElement1Attr2);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement1StatisticsEntry, simpleTypeScenario1Element1Attr2ValueOccurrences, 6, attributeScenario1RootElement1Attr2);
		
		//attr3 at element1
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement1StatisticsEntry, simpleTypeScenario1Element1Attr3ValueOccurrences, 0, attributeScenario1RootElement1Attr3);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement1StatisticsEntry, simpleTypeScenario1Element1Attr3ValueOccurrences, 4, attributeScenario1RootElement1Attr3);
		
		//attr1 at element2
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement2StatisticsEntry, simpleTypeScenario1Element2Attr1ValueOccurrences, 8, attributeScenario1RootElement2Attr1);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement2StatisticsEntry, simpleTypeScenario1Element2Attr1ValueOccurrences, 10, attributeScenario1RootElement2Attr1);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement2StatisticsEntry, simpleTypeScenario1Element2Attr1ValueOccurrences, 12, attributeScenario1RootElement2Attr1);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement2StatisticsEntry, simpleTypeScenario1Element2Attr1ValueOccurrences, 14, attributeScenario1RootElement2Attr1);
		
		//attr2 at element2
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement2StatisticsEntry, simpleTypeScenario1Element2Attr2ValueOccurrences, 8, attributeScenario1RootElement2Attr2);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement2StatisticsEntry, simpleTypeScenario1Element2Attr2ValueOccurrences, 10, attributeScenario1RootElement2Attr2);
		
		//attr4 at element2
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement2StatisticsEntry, simpleTypeScenario1Element2Attr4ValueOccurrences, 8, attributeScenario1RootElement2Attr4);
		registerMultipleValuesAtComplexTypeStatisticsEntry(complexTypeScenario1RootElement2StatisticsEntry, simpleTypeScenario1Element2Attr4ValueOccurrences, 12, attributeScenario1RootElement2Attr4);
				
	}

	/**
	 * It builds the automatons used in the first scenario
	 */
	private void buildAutomatonsScenario1() {
		
		ExtendedAutomaton complexTypeRootAutomaton=complexTypeScenario1Root.getAutomaton();
		complexTypeRootAutomaton.setInitialState(initialState);
		complexTypeRootAutomaton.setFinalState(finalState);
		complexTypeRootAutomaton.addEdge(initialState, elementScenario1RootElement1,8L);
		complexTypeRootAutomaton.addEdge(elementScenario1RootElement1, finalState,8L);
		complexTypeRootAutomaton.addEdge(initialState, elementScenario1RootElement2,8L);
		complexTypeRootAutomaton.addEdge(elementScenario1RootElement2, finalState,8L);
		registerMultipleNodeCount(complexTypeScenario1RootStatisticsEntry, elementScenario1RootElement1, 0, 2);
		registerMultipleNodeCount(complexTypeScenario1RootStatisticsEntry, elementScenario1RootElement2, 2, 2);
		complexTypeScenario1RootStatisticsEntry.registerSubpatternsFromList(Collections.singletonList(elementScenario1RootElement1));
		complexTypeScenario1RootStatisticsEntry.registerSubpatternsFromList(Collections.singletonList(elementScenario1RootElement1));
		complexTypeScenario1RootStatisticsEntry.registerSubpatternsFromList(Collections.singletonList(elementScenario1RootElement2));
		complexTypeScenario1RootStatisticsEntry.registerSubpatternsFromList(Collections.singletonList(elementScenario1RootElement2));
		
		
		ExtendedAutomaton complexTypeElement1Automaton=complexTypeScenario1RootElement1.getAutomaton();
		complexTypeElement1Automaton.setInitialState(initialState);
		complexTypeElement1Automaton.setFinalState(finalState);
		complexTypeElement1Automaton.addEdge(initialState, elementScenario1RootElement1A, 4L);
		complexTypeElement1Automaton.addEdge(initialState, elementScenario1RootElement1B, 4L);
		complexTypeElement1Automaton.addEdge(elementScenario1RootElement1A, elementScenario1RootElement1C, 4L);
		complexTypeElement1Automaton.addEdge(elementScenario1RootElement1B, elementScenario1RootElement1C, 4L);
		complexTypeElement1Automaton.addEdge(elementScenario1RootElement1C, finalState, 8L);
		registerMultipleNodeCount(complexTypeScenario1RootElement1StatisticsEntry, elementScenario1RootElement1A, 0, 4);
		registerMultipleNodeCount(complexTypeScenario1RootElement1StatisticsEntry, elementScenario1RootElement1B, 4, 4);
		registerMultipleNodeCount(complexTypeScenario1RootElement1StatisticsEntry, elementScenario1RootElement1C, 0, 8);
		for(int i=0;i<4;i++)
			complexTypeScenario1RootElement1StatisticsEntry.registerSubpatternsFromList(ImmutableList.of(elementScenario1RootElement1A, elementScenario1RootElement1C));
		for(int i=4;i<8;i++)
			complexTypeScenario1RootElement1StatisticsEntry.registerSubpatternsFromList(ImmutableList.of(elementScenario1RootElement1B, elementScenario1RootElement1C));
		
		ExtendedAutomaton complexTypeElement1NodeCAutomaton=complexTypeScenario1RootElement1NodeC.getAutomaton();
		complexTypeElement1NodeCAutomaton.setInitialState(initialState);
		complexTypeElement1NodeCAutomaton.setFinalState(finalState);
		complexTypeElement1NodeCAutomaton.addEdge(initialState, elementScenario1RootElement1CE,8L);
		complexTypeElement1NodeCAutomaton.addEdge(elementScenario1RootElement1CE, finalState,8L);
		registerMultipleNodeCount(complexTypeScenario1RootElement1NodeCStatisticsEntry, elementScenario1RootElement1CE, 0, 8);
		for(int i=0;i<8;i++)
			complexTypeScenario1RootElement1NodeCStatisticsEntry.registerSubpatternsFromList(Collections.singletonList(elementScenario1RootElement1CE));
		
		ExtendedAutomaton complexTypeElement2Automaton=complexTypeScenario1RootElement2.getAutomaton();
		complexTypeElement2Automaton.setInitialState(initialState);
		complexTypeElement2Automaton.setFinalState(finalState);
		complexTypeElement2Automaton.addEdge(initialState, elementScenario1RootElement2A, 8L);
		complexTypeElement2Automaton.addEdge(elementScenario1RootElement2A, elementScenario1RootElement2B, 8L);
		complexTypeElement2Automaton.addEdge(elementScenario1RootElement2B, elementScenario1RootElement2C, 8L);
		complexTypeElement2Automaton.addEdge(elementScenario1RootElement2C, elementScenario1RootElement2C, 5L);
		complexTypeElement2Automaton.addEdge(elementScenario1RootElement2C, elementScenario1RootElement2D, 8L);
		complexTypeElement2Automaton.addEdge(elementScenario1RootElement2D, finalState, 8L);
		registerMultipleNodeCount(complexTypeScenario1RootElement2StatisticsEntry, elementScenario1RootElement2A, 8, 8);
		registerMultipleNodeCount(complexTypeScenario1RootElement2StatisticsEntry, elementScenario1RootElement2B, 8, 8);
		registerMultipleNodeCount(complexTypeScenario1RootElement2StatisticsEntry, elementScenario1RootElement2C, 8, 8);
		registerMultipleNodeCount(complexTypeScenario1RootElement2StatisticsEntry, elementScenario1RootElement2C, 8, 5); //Repetitions of C which should generate its self-loop on the automaton
		registerMultipleNodeCount(complexTypeScenario1RootElement2StatisticsEntry, elementScenario1RootElement2D, 8, 8);
		for(int i=8;i<13;i++)
			complexTypeScenario1RootElement2StatisticsEntry.registerSubpatternsFromList(ImmutableList.of(elementScenario1RootElement2A,elementScenario1RootElement2B,elementScenario1RootElement2C,elementScenario1RootElement2C,elementScenario1RootElement2D));
		for(int i=13;i<16;i++)
			complexTypeScenario1RootElement2StatisticsEntry.registerSubpatternsFromList(ImmutableList.of(elementScenario1RootElement2A,elementScenario1RootElement2B,elementScenario1RootElement2C,elementScenario1RootElement2D));
		
		ExtendedAutomaton complexTypeElement2NodeCAutomaton=complexTypeScenario1RootElement2NodeC.getAutomaton();
		complexTypeElement2NodeCAutomaton.setInitialState(initialState);
		complexTypeElement2NodeCAutomaton.setFinalState(finalState);
		complexTypeElement2NodeCAutomaton.addEdge(initialState, elementScenario1RootElement2CF,8L);
		complexTypeElement2NodeCAutomaton.addEdge(elementScenario1RootElement2CF, finalState,8L);
		registerMultipleNodeCount(complexTypeScenario1RootElement2NodeCStatisticsEntry, elementScenario1RootElement2CF, 8, 8);
		registerMultipleNodeCount(complexTypeScenario1RootElement2NodeCStatisticsEntry, elementScenario1RootElement2CF, 8, 5); //Repetitions because of repetitions of C
		for(int i=8;i<16;i++)
			complexTypeScenario1RootElement2NodeCStatisticsEntry.registerSubpatternsFromList(Collections.singletonList(elementScenario1RootElement2CF));
		for(int i=8;i<13;i++)
			complexTypeScenario1RootElement2NodeCStatisticsEntry.registerSubpatternsFromList(Collections.singletonList(elementScenario1RootElement2CF));
	}

	/**
	 * It builds the structures of the Schema object of the first scenario
	 */
	private void buildSchema1() {
		NavigableMap<String,SortedSet<String>> prefixNamespaceMapping = new TreeMap<String, SortedSet<String>>();
		prefixNamespaceMapping.put("", new TreeSet<>(Collections.singleton("")));
		
		Map<String, ComplexType> complexTypes = new HashMap<>(11);
		complexTypes.put("_root", complexTypeScenario1Root);
		complexTypes.put("_root-_element1", complexTypeScenario1RootElement1);
		complexTypes.put("_root-_element1-_A", complexTypeScenario1RootElement1NodeA);
		complexTypes.put("_root-_element1-_B", complexTypeScenario1RootElement1NodeB);
		complexTypes.put("_root-_element1-_C", complexTypeScenario1RootElement1NodeC);
		complexTypes.put("_root-_element1-_C-_E", complexTypeScenario1RootElement1NodeCNodeE);
		complexTypes.put("_root-_element2", complexTypeScenario1RootElement2);
		complexTypes.put("_root-_element2-_A", complexTypeScenario1RootElement2NodeA);
		complexTypes.put("_root-_element2-_B", complexTypeScenario1RootElement2NodeB);
		complexTypes.put("_root-_element2-_C", complexTypeScenario1RootElement2NodeC);
		complexTypes.put("_root-_element2-_D", complexTypeScenario1RootElement2NodeD);
		complexTypes.put("_root-_element2-_C-_F", complexTypeScenario1RootElement2NodeCNodeF);
		
		
		Table<String, String, SchemaElement> elements = HashBasedTable.create(1, 11);
		elements.put("", "root", elementScenario1Root);
		elements.put("", "_root-element1", elementScenario1RootElement1);
		elements.put("", "_root-_element1-A", elementScenario1RootElement1A);
		elements.put("", "_root-_element1-B", elementScenario1RootElement1B);
		elements.put("", "_root-_element1-C", elementScenario1RootElement1C);
		elements.put("", "_root-_element1-_C-E", elementScenario1RootElement1CE);
		elements.put("", "_root-element2", elementScenario1RootElement2);
		elements.put("", "_root-_element2-A", elementScenario1RootElement2A);
		elements.put("", "_root-_element2-B", elementScenario1RootElement2B);
		elements.put("", "_root-_element2-C", elementScenario1RootElement2C);
		elements.put("", "_root-_element2-D", elementScenario1RootElement2D);
		elements.put("", "_root-_element2-_C-F", elementScenario1RootElement2CF);
	
		
		Table<String, String, SchemaAttribute> attributes = HashBasedTable.create(1,6);
		attributes.put("", "_root-_element1-attr1", attributeScenario1RootElement1Attr1);
		attributes.put("", "_root-_element1-attr2", attributeScenario1RootElement1Attr2);
		attributes.put("", "_root-_element1-attr3", attributeScenario1RootElement1Attr3);
		attributes.put("", "_root-_element2-attr1", attributeScenario1RootElement2Attr1);
		attributes.put("", "_root-_element2-attr2", attributeScenario1RootElement2Attr2);
		attributes.put("", "_root-_element2-attr4", attributeScenario1RootElement2Attr4);
		
		Map<String, SimpleType> simpleTypes = new HashMap<>(17);
		simpleTypes.put("_root", complexTypeScenario1Root.getTextSimpleType());
		simpleTypes.put("_root-_element1", complexTypeScenario1RootElement1.getTextSimpleType());
		simpleTypes.put("_root-_element1-_A", simpleTypeScenario1RootElement1NodeA);
		simpleTypes.put("_root-_element1-_B", simpleTypeScenario1RootElement1NodeB);
		simpleTypes.put("_root-_element1-_C", complexTypeScenario1RootElement1NodeC.getTextSimpleType());
		simpleTypes.put("_root-_element1-_C-_E", complexTypeScenario1RootElement1NodeCNodeE.getTextSimpleType());
		simpleTypes.put("_root-_element2", complexTypeScenario1RootElement2.getTextSimpleType());
		simpleTypes.put("_root-_element2-_A", simpleTypeScenario1RootElement2NodeB);
		simpleTypes.put("_root-_element2-_B", simpleTypeScenario1RootElement2NodeA);
		simpleTypes.put("_root-_element2-_C", complexTypeScenario1RootElement2NodeC.getTextSimpleType());
		simpleTypes.put("_root-_element2-_C-_F", complexTypeScenario1RootElement2NodeCNodeF.getTextSimpleType());
		simpleTypes.put("_root-_element2-_C", complexTypeScenario1RootElement2NodeC.getTextSimpleType());
		
		Statistics statistics = new Statistics(4);
		Map<ComplexType, ComplexTypeStatisticsEntry> complexTypeInfo = statistics.getComplexTypeInfo();
		complexTypeInfo.put(complexTypeScenario1Root,complexTypeScenario1RootStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1RootElement1,complexTypeScenario1RootElement1StatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1RootElement1NodeA,complexTypeScenario1RootElement1NodeAStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1RootElement1NodeB,complexTypeScenario1RootElement1NodeBStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1RootElement1NodeC,complexTypeScenario1RootElement1NodeCStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1RootElement1NodeCNodeE,complexTypeScenario1RootElement1NodeCNodeEStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1RootElement2,complexTypeScenario1RootElement2StatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1RootElement2NodeA,complexTypeScenario1RootElement2NodeAStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1RootElement2NodeB,complexTypeScenario1RootElement2NodeBStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1RootElement2NodeC,complexTypeScenario1RootElement2NodeCStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1RootElement2NodeCNodeF,complexTypeScenario1RootElement2NodeCNodeFStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1RootElement2NodeD,complexTypeScenario1RootElement2NodeDStatisticsEntry);
		
		schema1 = new Schema(prefixNamespaceMapping, elements, attributes, complexTypes, simpleTypes, statistics);
		
	}
	
	/**
	 * This method builds everything related with the first scenario
	 */
	private void buildScenario1() {
		buildComplexTypeStatisticsEntriesScenario1();
		buildSimpleTypesScenario1();
		buildSchemaAttributesScenario1();
		buildComplexTypesScenario1();
		buildSchemaElementsScenario1();
		registerValuesOccurrencesScenario1();
		buildAutomatonsScenario1();
		buildSchema1();
		
		configuration1 = mock(XSDInferenceConfiguration.class);
		when(configuration1.getTypeNamesAncestorsSeparator()).thenReturn("-");
		when(configuration1.getMergedTypesSeparator()).thenReturn("_and_");
		
		when(configuration1.getGenerateEnumerations()).thenReturn(true);
		
		when(configuration1.getSimpleTypeInferencer()).thenReturn(XSDInferenceConfiguration.VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL);
		when(configuration1.getAttributeListInferencer()).thenReturn(XSDInferenceConfiguration.VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL);
		when(configuration1.getMaxNumberOfDistinctValuesToEnum()).thenReturn(20);
	}
	
	//Second scenario building methods
	
	/**
	 * It builds the complex type statistics entries of the second scenario
	 */
	private void buildComplexTypeStatisticsEntriesScenario2(){
		complexTypeStatisticsEntryScenario2Root = new ComplexTypeStatisticsEntry(1);
		complexTypeStatisticsEntryScenario2RootA = new ComplexTypeStatisticsEntry(1);
		complexTypeStatisticsEntryScenario2AD = new ComplexTypeStatisticsEntry(1);
		complexTypeStatisticsEntryScenario2RootB = new ComplexTypeStatisticsEntry(1);
		complexTypeStatisticsEntryScenario2BD = new ComplexTypeStatisticsEntry(1);
		complexTypeStatisticsEntryScenario2RootC = new ComplexTypeStatisticsEntry(1);
		complexTypeStatisticsEntryScenario2CB = new ComplexTypeStatisticsEntry(1);
		complexTypeStatisticsEntryScenario2BE = new ComplexTypeStatisticsEntry(1);
	}
	
	/**
	 * It builds the non-trivial SimpleType objects used in the second scenario
	 */
	private void buildSimpleTypesScenario2(){
		List<String> attrValues = Lists.newArrayList("value1");
		
		List<String> attr2Values = Lists.newArrayList("value2");
		
		simpleTypeScenario2ADAttr = new SimpleType("_a-_d-@_attr","xs:string",attrValues,true);
		simpleTypeScenario2ADAttr2 = new SimpleType("_a-_d-@_attr2","xs:string",attr2Values,true);
		simpleTypeScenario2BDAttr = new SimpleType("_b-_d-@_attr","xs:string",attrValues,true);
		simpleTypeScenario2BDAttr2 = new SimpleType("_b-_d-@_attr2","xs:string",attr2Values,true);
	}
	
	/**
	 * It builds the SchemaAttribute objects of the second scenario
	 */
	private void buildSchemaAttributesScenario2(){
		attributeScenario2ADAttr = new SchemaAttribute("attr", "", false, simpleTypeScenario2ADAttr);
		complexTypeStatisticsEntryScenario2AD.registerAttributeOccurrenceInfoCount(attributeScenario2ADAttr, 0);
		attributeScenario2ADAttr2 = new SchemaAttribute("attr2", "", false, simpleTypeScenario2ADAttr2);
		complexTypeStatisticsEntryScenario2AD.registerAttributeOccurrenceInfoCount(attributeScenario2ADAttr2, 0);
		attributeScenario2BDAttr = new SchemaAttribute("attr", "", true, simpleTypeScenario2BDAttr);
		complexTypeStatisticsEntryScenario2BD.registerAttributeOccurrenceInfoCount(attributeScenario2BDAttr, 0);
		attributeScenario2BDAttr2 = new SchemaAttribute("attr2", "", false, simpleTypeScenario2BDAttr2);
		complexTypeStatisticsEntryScenario2BD.registerAttributeOccurrenceInfoCount(attributeScenario2BDAttr2, 0);
	}
	
	/**
	 * It builds the complex types of the scenario 2
	 */
	private void buildComplexTypesScenario2(){
		complexTypeScenario2Root = new ComplexType("_root", new ExtendedAutomaton(), new SimpleType("_root","xs:string",Collections.singleton("\r\n\t\r\n\t\r\n\t\r\n"),false), new ArrayList<SchemaAttribute>());
		
		complexTypeScenario2RootA = new ComplexType("_root-_a", new ExtendedAutomaton(), new SimpleType("_root-_a"), new ArrayList<SchemaAttribute>());
		complexTypeScenario2AD = new ComplexType("_a-_d", new ExtendedAutomaton(), new SimpleType("_a-_d"), Lists.newArrayList(attributeScenario2ADAttr,attributeScenario2ADAttr2));
		
		complexTypeScenario2RootB = new ComplexType("_root-_b", new ExtendedAutomaton(), new SimpleType("_root-_b"), new ArrayList<SchemaAttribute>());
		complexTypeScenario2BD = new ComplexType("_b-_d", new ExtendedAutomaton(), new SimpleType("_b-_d"), Lists.newArrayList(attributeScenario2BDAttr,attributeScenario2BDAttr2));
		complexTypeScenario2BE = new ComplexType("_b-_e", new ExtendedAutomaton(), new SimpleType("_b-_e"), new ArrayList<SchemaAttribute>());
		
		complexTypeScenario2RootC = new ComplexType("_root-_c", new ExtendedAutomaton(), new SimpleType("_root-_c"), new ArrayList<SchemaAttribute>());
		complexTypeScenario2CB = new ComplexType("_c-_b", new ExtendedAutomaton(), new SimpleType("_c-_b"), new ArrayList<SchemaAttribute>());
		
	}
	
	/**
	 * It builds SchemaElements objects on the scenario 2
	 */
	private void buildSchemaElementsScenario2() {
		elementScenario2Root = new SchemaElement("root", "", complexTypeScenario2Root);
		
		elementScenario2RootA = new SchemaElement("a", "", complexTypeScenario2RootA);
		elementScenario2RootAD = new SchemaElement("d", "", complexTypeScenario2AD);
		
		elementScenario2RootB = new SchemaElement("b", "", complexTypeScenario2RootB);
		elementScenario2RootBD = new SchemaElement("d", "", complexTypeScenario2BD);
		
		elementScenario2RootC = new SchemaElement("c", "", complexTypeScenario2RootC);
		elementScenario2RootCB = new SchemaElement("b", "", complexTypeScenario2CB);
		elementScenario2CBD = new SchemaElement("d", "", complexTypeScenario2BD);
		elementScenario2CBE = new SchemaElement("e", "", complexTypeScenario2BE);
		
	}
	
	/**
	 * This helper method registers all the value occurrences on the statistics of the scenario 2
	 */
	private void registerValuesOccurrencesScenario2(){
		complexTypeStatisticsEntryScenario2AD.registerValueOfNodeCount("value1", attributeScenario2ADAttr, 0);
		complexTypeStatisticsEntryScenario2AD.registerValueOfNodeCount("value2", attributeScenario2ADAttr2, 0);
		complexTypeStatisticsEntryScenario2BD.registerValueOfNodeCount("value1", attributeScenario2BDAttr, 0);
		complexTypeStatisticsEntryScenario2BD.registerValueOfNodeCount("value2", attributeScenario2BDAttr2, 0);
		complexTypeStatisticsEntryScenario2BD.registerValueOfNodeCount("value2", attributeScenario2BDAttr2, 0);
	}
	
	/**
	 * This method builds the automatons used at scenario 2
	 */
	private void buildAutomatonsScenario2(){
		ExtendedAutomaton automatonRoot = complexTypeScenario2Root.getAutomaton();
		automatonRoot.setInitialState(initialState);
		automatonRoot.setFinalState(finalState);
		automatonRoot.addEdge(initialState, elementScenario2RootA, 1L);
		automatonRoot.addEdge(elementScenario2RootA, elementScenario2RootB, 1L);
		automatonRoot.addEdge(elementScenario2RootB, elementScenario2RootC, 1L);
		automatonRoot.addEdge(elementScenario2RootC, finalState, 1L);
		complexTypeStatisticsEntryScenario2Root.registerElementCount(elementScenario2RootA, 0);
		complexTypeStatisticsEntryScenario2Root.registerElementCount(elementScenario2RootB, 0);
		complexTypeStatisticsEntryScenario2Root.registerElementCount(elementScenario2RootC, 0);
		complexTypeStatisticsEntryScenario2Root.registerSubpatternsFromList(ImmutableList.of(elementScenario2RootA, elementScenario2RootB, elementScenario2RootC));
		
		ExtendedAutomaton automatonRootA = complexTypeScenario2RootA.getAutomaton();
		automatonRootA.setInitialState(initialState);
		automatonRootA.setFinalState(finalState);
		automatonRootA.addEdge(initialState, elementScenario2RootAD, 1L);
		automatonRootA.addEdge(elementScenario2RootAD, finalState, 1L);
		complexTypeStatisticsEntryScenario2RootA.registerElementCount(elementScenario2RootAD, 0);
		complexTypeStatisticsEntryScenario2RootA.registerSubpatternsFromList(ImmutableList.of(elementScenario2RootAD));
		
		ExtendedAutomaton automatonAD = complexTypeScenario2AD.getAutomaton();
		automatonAD.setInitialState(initialState);
		automatonAD.setFinalState(finalState);
		automatonAD.addEdge(initialState, finalState);
		
		ExtendedAutomaton automatonRootB = complexTypeScenario2RootB.getAutomaton();
		automatonRootB.setInitialState(initialState);
		automatonRootB.setFinalState(finalState);
		automatonRootB.addEdge(initialState, elementScenario2RootBD, 1L);
		automatonRootB.addEdge(elementScenario2RootBD, finalState, 1L);
		complexTypeStatisticsEntryScenario2RootB.registerElementCount(elementScenario2RootBD, 0);
		complexTypeStatisticsEntryScenario2RootB.registerSubpatternsFromList(ImmutableList.of(elementScenario2RootBD));
		
		ExtendedAutomaton automatonBD = complexTypeScenario2BD.getAutomaton();
		automatonBD.setInitialState(initialState);
		automatonBD.setFinalState(finalState);
		automatonBD.addEdge(initialState, finalState);
		
		ExtendedAutomaton automatonBE = complexTypeScenario2BE.getAutomaton();
		automatonBE.setInitialState(initialState);
		automatonBE.setFinalState(finalState);
		automatonBE.addEdge(initialState, finalState);
		
		ExtendedAutomaton automatonRootC = complexTypeScenario2RootC.getAutomaton();
		automatonRootC.setInitialState(initialState);
		automatonRootC.setFinalState(finalState);
		automatonRootC.addEdge(initialState, elementScenario2RootCB, 1L);
		automatonRootC.addEdge(elementScenario2RootCB, finalState, 1L);
		
		ExtendedAutomaton automatonCB = complexTypeScenario2CB.getAutomaton();
		automatonCB.setInitialState(initialState);
		automatonCB.setFinalState(finalState);
		automatonCB.addEdge(initialState, elementScenario2CBD, 1L);
		automatonCB.addEdge(elementScenario2CBD, elementScenario2CBE, 1L);
		automatonCB.addEdge(elementScenario2CBE, finalState, 1L);
		complexTypeStatisticsEntryScenario2CB.registerElementCount(elementScenario2CBD, 0);
		complexTypeStatisticsEntryScenario2CB.registerSubpatternsFromList(ImmutableList.of(elementScenario2CBD));
	}
	
	/**
	 * It builds the structures of the Schema object of the scenario 2
	 */
	private void buildSchema2() {
		NavigableMap<String,SortedSet<String>> prefixNamespaceMapping = new TreeMap<String, SortedSet<String>>();
		prefixNamespaceMapping.put("", new TreeSet<>(Collections.singleton("")));
		
		Table<String,String,SchemaElement> elements = HashBasedTable.create(1,9);
		elements.put("", "root", elementScenario2Root);
		elements.put("", "_root-a", elementScenario2RootA);
		elements.put("", "_root-_a-d", elementScenario2RootAD);
		elements.put("", "_root-b", elementScenario2RootB);
		elements.put("", "_root-_b-d", elementScenario2RootBD);
		elements.put("", "_root-c", elementScenario2RootC);
		elements.put("", "_root-_c-b", elementScenario2RootCB);
		elements.put("", "_c-_b-d", elementScenario2CBD);
		elements.put("", "_c-_b-e", elementScenario2CBE);
		
		Table<String, String, SchemaAttribute> attributes = HashBasedTable.create(1,4);
		attributes.put("", "_a-_d-attr", attributeScenario2ADAttr);
		attributes.put("", "_a-_d-attr2", attributeScenario2ADAttr2);
		attributes.put("", "_b-_d-attr", attributeScenario2BDAttr);
		attributes.put("", "_b-_d-attr2", attributeScenario2BDAttr2);
		
		Map<String, ComplexType> complexTypes = new HashMap<>(8);
		complexTypes.put(complexTypeScenario2Root.getName(), complexTypeScenario2Root);
		complexTypes.put(complexTypeScenario2RootA.getName(), complexTypeScenario2RootA);
		complexTypes.put(complexTypeScenario2RootB.getName(), complexTypeScenario2RootB);
		complexTypes.put(complexTypeScenario2RootC.getName(), complexTypeScenario2RootC);
		complexTypes.put(complexTypeScenario2AD.getName(), complexTypeScenario2AD);
		complexTypes.put(complexTypeScenario2BD.getName(), complexTypeScenario2BD);
		complexTypes.put(complexTypeScenario2BE.getName(), complexTypeScenario2BE);
		complexTypes.put(complexTypeScenario2CB.getName(), complexTypeScenario2CB);
		
		Map<String, SimpleType> simpleTypes = new HashMap<>(11);
		simpleTypes.put(complexTypeScenario2Root.getTextSimpleType().getName(), complexTypeScenario2Root.getTextSimpleType());
		simpleTypes.put(complexTypeScenario2RootA.getTextSimpleType().getName(), complexTypeScenario2RootA.getTextSimpleType());
		simpleTypes.put(complexTypeScenario2RootB.getTextSimpleType().getName(), complexTypeScenario2RootB.getTextSimpleType());
		simpleTypes.put(complexTypeScenario2RootC.getTextSimpleType().getName(), complexTypeScenario2RootC.getTextSimpleType());
		simpleTypes.put(complexTypeScenario2AD.getTextSimpleType().getName(), complexTypeScenario2AD.getTextSimpleType());
		simpleTypes.put(complexTypeScenario2BD.getTextSimpleType().getName(), complexTypeScenario2BD.getTextSimpleType());
		simpleTypes.put(complexTypeScenario2BE.getTextSimpleType().getName(), complexTypeScenario2BE.getTextSimpleType());
		simpleTypes.put(complexTypeScenario2CB.getTextSimpleType().getName(), complexTypeScenario2CB.getTextSimpleType());
		simpleTypes.put(simpleTypeScenario2ADAttr.getName(), simpleTypeScenario2ADAttr);
		simpleTypes.put(simpleTypeScenario2BDAttr.getName(), simpleTypeScenario2BDAttr);
		simpleTypes.put(simpleTypeScenario2BDAttr2.getName(), simpleTypeScenario2BDAttr2);
		
		Statistics statistics = new Statistics(1);
		Map<ComplexType, ComplexTypeStatisticsEntry> complexTypeInfo = statistics.getComplexTypeInfo();
		complexTypeInfo.put(complexTypeScenario2Root, complexTypeStatisticsEntryScenario2Root);
		complexTypeInfo.put(complexTypeScenario2RootA, complexTypeStatisticsEntryScenario2RootA);
		complexTypeInfo.put(complexTypeScenario2RootB, complexTypeStatisticsEntryScenario2RootB);
		complexTypeInfo.put(complexTypeScenario2RootC, complexTypeStatisticsEntryScenario2RootC);
		complexTypeInfo.put(complexTypeScenario2AD, complexTypeStatisticsEntryScenario2AD);
		complexTypeInfo.put(complexTypeScenario2BD, complexTypeStatisticsEntryScenario2BD);
		complexTypeInfo.put(complexTypeScenario2BE, complexTypeStatisticsEntryScenario2BE);
		complexTypeInfo.put(complexTypeScenario2CB, complexTypeStatisticsEntryScenario2CB);
		
		schema2 = new Schema(prefixNamespaceMapping, elements, attributes , complexTypes, simpleTypes, statistics);
	}
	
	/**
	 * This method builds the testing scenario 2
	 */
	private void buildScenario2(){
		buildComplexTypeStatisticsEntriesScenario2();
		buildSimpleTypesScenario2();
		buildSchemaAttributesScenario2();
		buildComplexTypesScenario2();
		buildSchemaElementsScenario2();
		registerValuesOccurrencesScenario2();
		buildAutomatonsScenario2();
		buildSchema2();
		
		configuration2 = mock(XSDInferenceConfiguration.class);
		when(configuration2.getTypeNamesAncestorsSeparator()).thenReturn("-");
		when(configuration2.getMergedTypesSeparator()).thenReturn("_and_");
						
		when(configuration2.getGenerateEnumerations()).thenReturn(true);
		
		when(configuration2.getSimpleTypeInferencer()).thenReturn(XSDInferenceConfiguration.VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL);
		when(configuration2.getAttributeListInferencer()).thenReturn(XSDInferenceConfiguration.VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL);
		when(configuration2.getMaxNumberOfDistinctValuesToEnum()).thenReturn(20);
	}

	/**
	 * This method sets up the testing environment 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		initialState=new SchemaElement("initial",DEFAULT_PSEUDOELEMENTS_NAMESPACE,null);
		finalState=new SchemaElement("final",DEFAULT_PSEUDOELEMENTS_NAMESPACE,null);
		
		alwaysFalseChildrenPatternComparator = mock(ChildrenPatternComparator.class);
		when(alwaysFalseChildrenPatternComparator.compare(any(ExtendedAutomaton.class), any(ExtendedAutomaton.class))).thenReturn(false);
		
		alwaysFalseAttributeListComparator = mock(AttributeListComparator.class);
		when(alwaysFalseAttributeListComparator.compare(anyListOf(SchemaAttribute.class), anyListOf(SchemaAttribute.class))).thenReturn(false);
		
		alwaysTrueAttributeListComparator = mock(AttributeListComparator.class);
		when(alwaysTrueAttributeListComparator.compare(anyListOf(SchemaAttribute.class), anyListOf(SchemaAttribute.class))).thenReturn(true);
		
		alwaysFalseEnumComparator = mock(EnumComparator.class);
		when(alwaysFalseEnumComparator.compare(any(SimpleType.class), any(SimpleType.class))).thenReturn(false);
		
		buildScenario1();
		buildScenario2();
	}

	//Scenario 1 test methods
	
	/**
	 * This method checks that the simple type of the "A" element of element1 and the simple type of the "B" element of element2 are merged when the corresponding comparators 
	 * return true. 
	 * No complex types should be merged in this test method.
	 */
	@Test
	public void testScenario1OnlyNormalSimpleTypeMerge() {
		when(configuration1.getChildrenPatternComparator()).thenReturn(alwaysFalseChildrenPatternComparator);
		when(configuration1.getAttributeListComparator()).thenReturn(alwaysFalseAttributeListComparator);
		
		EnumComparator onlySimpleTypesElement1AAndElement2BAreMerged = mock(EnumComparator.class);
		when(onlySimpleTypesElement1AAndElement2BAreMerged.compare(any(SimpleType.class), any(SimpleType.class))).thenReturn(false);
		when(onlySimpleTypesElement1AAndElement2BAreMerged.compare(simpleTypeScenario1RootElement1NodeA, simpleTypeScenario1RootElement2NodeB)).thenReturn(true);
		when(configuration1.getEnumsComparator()).thenReturn(onlySimpleTypesElement1AAndElement2BAreMerged);
		
		TypeMerger typeMerger = new TypeMergerImpl();
		typeMerger.mergeTypes(schema1, configuration1);
		
		assertEquals(simpleTypeScenario1RootElement1NodeB,elementScenario1RootElement1B.getType().getTextSimpleType());
		assertEquals(simpleTypeScenario1RootElement2NodeA,elementScenario1RootElement2A.getType().getTextSimpleType()); //We verify that these simple types are not merged
		
		assertFalse(simpleTypeScenario1RootElement1NodeA.equals(elementScenario1RootElement1A.getType().getTextSimpleType()));
		assertFalse(simpleTypeScenario1RootElement2NodeB.equals(elementScenario1RootElement2B.getType().getTextSimpleType()));
		
		assertEquals(elementScenario1RootElement1A.getType().getTextSimpleType(),elementScenario1RootElement2B.getType().getTextSimpleType());
		
		String simpleTypeMergedExpectedName = simpleTypeScenario1RootElement1NodeA.getName()+configuration1.getMergedTypesSeparator()+simpleTypeScenario1RootElement2NodeB.getName();
		assertTrue(schema1.getSimpleTypes().containsKey(simpleTypeMergedExpectedName));
		
		SimpleType simpleTypeMerged = elementScenario1RootElement1A.getType().getTextSimpleType();
		assertEquals(simpleTypeMerged,schema1.getSimpleTypes().get(simpleTypeMergedExpectedName));
		
		assertEquals(simpleTypeMergedExpectedName,
				simpleTypeMerged.getName());
		assertEquals("xs:string",simpleTypeMerged.getBuiltinType());
		assertEquals(5,simpleTypeMerged.enumerationCount());
		assertTrue(simpleTypeMerged.isEnum());
		assertTrue(simpleTypeMerged.enumerationContainsAll(Arrays.asList("40", "50", "60", "70", "cuarenta")));
		
		
	}
	
	/**
	 * This method checks that a merge via snEnumComparator is made and no merge via enumComparator 
	 * when they are properly configured. 
	 */
	@Test
	public void testScenario1OnlySameNameSimpleTypeMerge(){
		when(configuration1.getChildrenPatternComparator()).thenReturn(alwaysFalseChildrenPatternComparator);
		when(configuration1.getAttributeListComparator()).thenReturn(alwaysFalseAttributeListComparator);
		when(configuration1.getEnumsComparator()).thenReturn(alwaysFalseEnumComparator);
		
		EnumComparator onlySimpleTypesOfBAreMerged = mock(EnumComparator.class);
		when(onlySimpleTypesOfBAreMerged.compare(any(SimpleType.class), any(SimpleType.class))).thenReturn(false);
		when(onlySimpleTypesOfBAreMerged.compare(simpleTypeScenario1RootElement1NodeB, simpleTypeScenario1RootElement2NodeB)).thenReturn(true);
		when(configuration1.getSnEnumsComparator()).thenReturn(onlySimpleTypesOfBAreMerged);
		
		TypeMerger typeMerger = new TypeMergerImpl();
		typeMerger.mergeTypes(schema1, configuration1);
		
		assertEquals(simpleTypeScenario1RootElement1NodeA,elementScenario1RootElement1A.getType().getTextSimpleType());
		assertEquals(simpleTypeScenario1RootElement2NodeA,elementScenario1RootElement2A.getType().getTextSimpleType()); //We verify that these simple types are not merged
		
		assertFalse(simpleTypeScenario1RootElement1NodeB.equals(elementScenario1RootElement1B.getType().getTextSimpleType()));
		assertFalse(simpleTypeScenario1RootElement2NodeB.equals(elementScenario1RootElement2B.getType().getTextSimpleType()));
		
		assertEquals(elementScenario1RootElement1B.getType().getTextSimpleType(),elementScenario1RootElement2B.getType().getTextSimpleType());

		String simpleTypeMergedExpectedName = simpleTypeScenario1RootElement1NodeB.getName()+configuration1.getMergedTypesSeparator()+simpleTypeScenario1RootElement2NodeB.getName();
		assertTrue(schema1.getSimpleTypes().containsKey(simpleTypeMergedExpectedName));
		
		SimpleType simpleTypeMerged = elementScenario1RootElement1B.getType().getTextSimpleType();
		assertEquals(simpleTypeMerged,schema1.getSimpleTypes().get(simpleTypeMergedExpectedName));
		
		assertEquals(simpleTypeMergedExpectedName,
				simpleTypeMerged.getName());
		assertEquals("xs:string",simpleTypeMerged.getBuiltinType());
		assertEquals(6,simpleTypeMerged.enumerationCount());
		assertTrue(simpleTypeMerged.isEnum());
		assertTrue(simpleTypeMerged.enumerationContainsAll(Arrays.asList("saludos","hola","40","50","60","70")));
		
	}

	/**
	 * At this test, two complex types are merged via the normal comparators. 
	 * These complex types are so designed that their merge should face the most 
	 * common problems at attribute list and automaton merge (including forced submerges)
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testScenario1OnlyNormalComplexTypeMerge(){
		ChildrenPatternComparator onlyElement1AndElement2ChildrenPatternsAreSimilar=mock(ChildrenPatternComparator.class);
		when(onlyElement1AndElement2ChildrenPatternsAreSimilar.compare(any(ExtendedAutomaton.class), any(ExtendedAutomaton.class))).thenReturn(false);
		when(onlyElement1AndElement2ChildrenPatternsAreSimilar.compare(complexTypeScenario1RootElement1.getAutomaton(), complexTypeScenario1RootElement2.getAutomaton())).thenReturn(true);
		
		AttributeListComparator onlyElement1AndElement2AttributeListsAreSimilar=mock(AttributeListComparator.class);
		when(onlyElement1AndElement2AttributeListsAreSimilar.compare(anyListOf(SchemaAttribute.class), anyListOf(SchemaAttribute.class))).thenReturn(false);
		when(onlyElement1AndElement2AttributeListsAreSimilar.compare(complexTypeScenario1RootElement1.getAttributeList(), complexTypeScenario1RootElement2.getAttributeList())).thenReturn(true);
		
		when(configuration1.getChildrenPatternComparator()).thenReturn(onlyElement1AndElement2ChildrenPatternsAreSimilar);
		when(configuration1.getAttributeListComparator()).thenReturn(onlyElement1AndElement2AttributeListsAreSimilar);
		when(configuration1.getEnumsComparator()).thenReturn(alwaysFalseEnumComparator);
		
		TypeMerger typeMerger = new TypeMergerImpl();
		typeMerger.mergeTypes(schema1, configuration1);
		
		//First, we check that element1 and element2 point to the same merged complex type, that it exists at the schema structures and that the old complex types do not exist anymore
		//We also check that the merged complex type name is correct.s
		Table<String, String, SchemaElement> schemaElements = schema1.getElements();
		Map<String, ComplexType> schemaComplexTypes = schema1.getComplexTypes();
		assertEquals(schemaElements.get("", "_root-element1").getType(),schemaElements.get("", "_root-element2").getType());
		String complexTypeMergedExpectedName=complexTypeScenario1RootElement1.getName()+configuration1.getMergedTypesSeparator()+complexTypeScenario1RootElement2.getName();
		assertTrue(schemaComplexTypes.containsKey(complexTypeMergedExpectedName));
		assertFalse(schemaComplexTypes.containsKey("_root-_element1"));
		assertFalse(schemaComplexTypes.containsKey("_root-_element2"));
		ComplexType complexTypeMerged = schemaComplexTypes.get(complexTypeMergedExpectedName);
		assertEquals(complexTypeMerged,elementScenario1RootElement1.getType());
		assertEquals(complexTypeMerged,elementScenario1RootElement2.getType());
		assertEquals(complexTypeMergedExpectedName,complexTypeMerged.getName());
		
		assertEquals(ImmutableSet.of("Comentario 1","Comentario 2"),complexTypeMerged.getComments());
		
		//Then, we check that the automatons of the complex type has been properly merged
		SchemaElement elementMergedA=schemaElements.get("", "_root-_element1_and__root-_element2-A");
		SchemaElement elementMergedB=schemaElements.get("", "_root-_element1_and__root-_element2-B");
		SchemaElement elementMergedC=schemaElements.get("", "_root-_element1_and__root-_element2-C");
		SchemaElement elementMergedD=schemaElements.get("", "_root-_element1_and__root-_element2-D");
		
		SchemaElement elementMergedCE=schemaElements.get("", "_root-_element1-_C_and__root-_element2-_C-E");
		SchemaElement elementMergedCF=schemaElements.get("", "_root-_element1-_C_and__root-_element2-_C-F");
		
		ExtendedAutomaton mergedAutomaton = complexTypeMerged.getAutomaton();
		assertTrue(mergedAutomaton.containsAllNodes(Lists.newArrayList(elementMergedA,elementMergedB,elementMergedC,elementMergedD)));
		assertEquals(12, mergedAutomaton.getEdgeWeight(initialState, elementMergedA));
		assertEquals(4, mergedAutomaton.getEdgeWeight(initialState, elementMergedB));
		assertEquals(8, mergedAutomaton.getEdgeWeight(elementMergedA, elementMergedB));
		assertEquals(4, mergedAutomaton.getEdgeWeight(elementMergedA, elementMergedC));
		assertEquals(12, mergedAutomaton.getEdgeWeight(elementMergedB, elementMergedC));
		assertEquals(5, mergedAutomaton.getEdgeWeight(elementMergedC, elementMergedC));
		assertEquals(8, mergedAutomaton.getEdgeWeight(elementMergedC, elementMergedD));
		assertEquals(8, mergedAutomaton.getEdgeWeight(elementMergedC, finalState));
		assertEquals(8, mergedAutomaton.getEdgeWeight(elementMergedD, finalState));
		
		//Now, we check that complex types of elements which are descendants of element1 or element2 are also unavoidably and recursively merged 
		//(for example, any complex type from an A node is merged with any complex type of an A node but not with any complex type of a B node) 
		//Although the other merges are forced, there are no other elements of those complex types, so they should not exist anymore
		String complexTypeAMergedExpectedName = "_root-_element1-_A_and__root-_element2-_A";
		assertTrue(schemaComplexTypes.containsKey(complexTypeAMergedExpectedName));
		assertFalse(schemaComplexTypes.containsKey("_root-_element1-_A"));
		assertFalse(schemaComplexTypes.containsKey("_root-_element2-_A"));
		ComplexType complexTypeMergedA = schemaComplexTypes.get(complexTypeAMergedExpectedName);
		assertEquals(complexTypeAMergedExpectedName,complexTypeMergedA.getName());
		
		String complexTypeBMergedExpectedName = "_root-_element1-_B_and__root-_element2-_B";
		assertTrue(schemaComplexTypes.containsKey(complexTypeBMergedExpectedName));
		assertFalse(schemaComplexTypes.containsKey("_root-_element1-_B"));
		assertFalse(schemaComplexTypes.containsKey("_root-_element2-_B"));
		ComplexType complexTypeMergedB = schemaComplexTypes.get(complexTypeBMergedExpectedName);
		assertEquals(complexTypeBMergedExpectedName,complexTypeMergedB.getName());
		
		String complexTypeMergedCExpectedName = TypeMergerImpl.getMergedTypeName(configuration1, complexTypeScenario1RootElement1NodeC.getName(), complexTypeScenario1RootElement2NodeC.getName());
		assertTrue(schemaComplexTypes.containsKey(complexTypeMergedCExpectedName));
		assertFalse(schemaComplexTypes.containsKey("_root-_element1-_C"));
		assertFalse(schemaComplexTypes.containsKey("_root-_element2-_C"));
		ComplexType complexTypeMergedC = schemaComplexTypes.get(complexTypeMergedCExpectedName);
		assertEquals(complexTypeMergedCExpectedName,complexTypeMergedC.getName());
		ExtendedAutomaton automatonMergedC = complexTypeMergedC.getAutomaton();
		assertTrue(automatonMergedC.containsAllNodes(ImmutableSet.of(elementMergedCE,elementMergedCF)));
		assertEquals(8, automatonMergedC.getEdgeWeight(initialState, elementMergedCE));
		assertEquals(8, automatonMergedC.getEdgeWeight(elementMergedCE, finalState));
		assertEquals(8, automatonMergedC.getEdgeWeight(initialState, elementMergedCF));
		assertEquals(8, automatonMergedC.getEdgeWeight(elementMergedCF, finalState));
		String complexTypeDMergedExpectedName = "_root-_element2-_D";
		assertTrue(schemaComplexTypes.containsKey(complexTypeDMergedExpectedName));
		ComplexType complexTypeMergedD = schemaComplexTypes.get(complexTypeDMergedExpectedName);
		assertEquals(complexTypeDMergedExpectedName,complexTypeMergedD.getName());
		
		String complexTypeEMergedExpectedName = "_root-_element1-_C-_E";
		assertTrue(schemaComplexTypes.containsKey(complexTypeEMergedExpectedName));
		ComplexType complexTypeMergedE = schemaComplexTypes.get(complexTypeEMergedExpectedName);
		assertEquals(complexTypeEMergedExpectedName,complexTypeMergedE.getName());
		
		String complexTypeFMergedExpectedName = "_root-_element2-_C-_F";
		assertTrue(schemaComplexTypes.containsKey(complexTypeFMergedExpectedName));
		ComplexType complexTypeMergedF = schemaComplexTypes.get(complexTypeFMergedExpectedName);
		assertEquals(complexTypeFMergedExpectedName,complexTypeMergedF.getName());
		
		//Now, we check that the non trivial simple types of nodes A and B are properly merged
		SimpleType simpleTypeAMerged = schema1.getSimpleTypes().get(complexTypeAMergedExpectedName);
		assertEquals(simpleTypeAMerged,complexTypeMergedA.getTextSimpleType());
		assertEquals(complexTypeAMergedExpectedName,
				simpleTypeAMerged.getName());
		assertEquals("xs:string",simpleTypeAMerged.getBuiltinType());
		assertEquals(6,simpleTypeAMerged.enumerationCount());
		assertTrue(simpleTypeAMerged.isEnum());
		assertTrue(simpleTypeAMerged.enumerationContainsAll(Arrays.asList("50", "60", "70", "cuarenta", "buenos dias", "saludos")));
		
		
		SimpleType simpleTypeBMerged = schema1.getSimpleTypes().get(complexTypeBMergedExpectedName);
		assertEquals(simpleTypeBMerged,complexTypeMergedB.getTextSimpleType());
		assertEquals(complexTypeBMergedExpectedName,
				simpleTypeBMerged.getName());
		assertEquals("xs:string",simpleTypeBMerged.getBuiltinType());
		assertEquals(6,simpleTypeBMerged.enumerationCount());
		assertTrue(simpleTypeBMerged.isEnum());
		assertTrue(simpleTypeBMerged.enumerationContainsAll(Arrays.asList("hola", "saludos", "40", "50", "60", "70")));

		//Now, we check the attributes and the attribute lists
		Table<String, String, SchemaAttribute> schemaAttributes = schema1.getAttributes();
		assertEquals(4,schemaAttributes.size());
		assertTrue(schemaAttributes.contains("", "_root-_element1_and__root-_element2-attr1"));
		assertTrue(schemaAttributes.contains("", "_root-_element1_and__root-_element2-attr2"));
		assertTrue(schemaAttributes.contains("", "_root-_element1_and__root-_element2-attr3"));
		assertTrue(schemaAttributes.contains("", "_root-_element1_and__root-_element2-attr4"));
		SchemaAttribute attr1Merged = schemaAttributes.get("", "_root-_element1_and__root-_element2-attr1");
		SchemaAttribute attr2Merged = schemaAttributes.get("", "_root-_element1_and__root-_element2-attr2");
		SchemaAttribute attr3Merged = schemaAttributes.get("", "_root-_element1_and__root-_element2-attr3");
		SchemaAttribute attr4Merged = schemaAttributes.get("", "_root-_element1_and__root-_element2-attr4");
		List<SchemaAttribute> attributeListMerged = schemaComplexTypes.get("_root-_element1_and__root-_element2").getAttributeList();
		assertEquals(4,attributeListMerged.size());
		assertTrue(attributeListMerged.containsAll(Arrays.asList(attr1Merged,attr2Merged,attr3Merged,attr4Merged)));
		assertEquals("attr1",attr1Merged.getName());
		assertEquals("", attr1Merged.getNamespace());
		assertFalse(attr1Merged.isOptional());
		assertEquals("xs:string",attr1Merged.getSimpleType().getBuiltinType());
		assertEquals("attr2",attr2Merged.getName());
		assertEquals("", attr2Merged.getNamespace());
		assertTrue(attr2Merged.isOptional());
		assertEquals("xs:integer",attr2Merged.getSimpleType().getBuiltinType());
		assertEquals("attr3",attr3Merged.getName());
		assertEquals("", attr3Merged.getNamespace());
		assertTrue(attr3Merged.isOptional());
		assertEquals("xs:boolean",attr3Merged.getSimpleType().getBuiltinType());
		assertEquals("attr4",attr4Merged.getName());
		assertEquals("", attr4Merged.getNamespace());
		assertTrue(attr4Merged.isOptional());
		assertEquals("xs:string",attr4Merged.getSimpleType().getBuiltinType());
		
		//Check statistics
		Statistics statistics = schema1.getStatistics();
		Map<ComplexType, ComplexTypeStatisticsEntry> complexTypeInfo = statistics.getComplexTypeInfo();
		assertNull(complexTypeInfo.get(complexTypeScenario1RootElement1));
		assertNull(complexTypeInfo.get(complexTypeScenario1RootElement2));
		
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMerged = complexTypeInfo.get(complexTypeMerged);
		assertNotNull(complexTypeStatisticsEntryMerged);
		assertEquals(16,complexTypeStatisticsEntryMerged.getInputDocumentsCount());
		
		//Attribute occurrences info
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeStatisticsEntryMerged.getAttributeOccurrencesInfo();
		assertEquals(4, attributeOccurrencesInfo.size());
		BasicStatisticsEntry attr1OccurrencesBasicStatisticsEntry = attributeOccurrencesInfo.get(attr1Merged);
		ArrayList<Double> attr1OccurrencesBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		BasicStatisticsEntry attr1OccurrencesBasicStatisticsEntryExpected = new BasicStatisticsEntry(attr1OccurrencesBasicStatisticsEntryExpectedCounts);
		assertEquals(attr1OccurrencesBasicStatisticsEntryExpected,attr1OccurrencesBasicStatisticsEntry);
		BasicStatisticsEntry attr2OccurrencesBasicStatisticsEntry = attributeOccurrencesInfo.get(attr2Merged);
		ArrayList<Double> attr2OccurrencesBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry attr2OccurrencesBasicStatisticsEntryExpected = new BasicStatisticsEntry(attr2OccurrencesBasicStatisticsEntryExpectedCounts);
		assertEquals(attr2OccurrencesBasicStatisticsEntryExpected,attr2OccurrencesBasicStatisticsEntry);
		BasicStatisticsEntry attr3OccurrencesBasicStatisticsEntry = attributeOccurrencesInfo.get(attr3Merged);
		ArrayList<Double> attr3OccurrencesBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry attr3OccurrencesBasicStatisticsEntryExpected = new BasicStatisticsEntry(attr3OccurrencesBasicStatisticsEntryExpectedCounts);
		assertEquals(attr3OccurrencesBasicStatisticsEntryExpected,attr3OccurrencesBasicStatisticsEntry);
		BasicStatisticsEntry attr4OccurrencesBasicStatisticsEntry = attributeOccurrencesInfo.get(attr4Merged);
		ArrayList<Double> attr4OccurrencesBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,1.0,1.0,1.0);
		BasicStatisticsEntry attr4OccurrencesBasicStatisticsEntryExpected = new BasicStatisticsEntry(attr4OccurrencesBasicStatisticsEntryExpectedCounts);
		assertEquals(attr4OccurrencesBasicStatisticsEntryExpected,attr4OccurrencesBasicStatisticsEntry);
		
		//Element occurrences info
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeStatisticsEntryMerged.getElementInfo();
		assertEquals(4, elementInfo.size());
		BasicStatisticsEntry elementMergedABasicStatisticsEntry = elementInfo.get(elementMergedA);
		ArrayList<Double> elementMergedABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,1.0,1.0,1.0,0.0,0.0,0.0,0.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		BasicStatisticsEntry elementMergedABasicStatisticsEntryExpected = new BasicStatisticsEntry(elementMergedABasicStatisticsEntryExpectedCounts);
		assertEquals(elementMergedABasicStatisticsEntryExpected,elementMergedABasicStatisticsEntry);
		BasicStatisticsEntry elementMergedBBasicStatisticsEntry = elementInfo.get(elementMergedB);
		ArrayList<Double> elementMergedBBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		BasicStatisticsEntry elementMergedBBasicStatisticsEntryExpected = new BasicStatisticsEntry(elementMergedBBasicStatisticsEntryExpectedCounts);
		assertEquals(elementMergedBBasicStatisticsEntryExpected,elementMergedBBasicStatisticsEntry);
		BasicStatisticsEntry elementMergedCBasicStatisticsEntry = elementInfo.get(elementMergedC);
		ArrayList<Double> elementMergedCBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,2.0,2.0,2.0,2.0,2.0,1.0,1.0,1.0);
		BasicStatisticsEntry elementMergedCBasicStatisticsEntryExpected = new BasicStatisticsEntry(elementMergedCBasicStatisticsEntryExpectedCounts);
		assertEquals(elementMergedCBasicStatisticsEntryExpected,elementMergedCBasicStatisticsEntry);
		BasicStatisticsEntry elementMergedDBasicStatisticsEntry = elementInfo.get(elementMergedD);
		ArrayList<Double> elementMergedDBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		BasicStatisticsEntry elementMergedDBasicStatisticsEntryExpected = new BasicStatisticsEntry(elementMergedDBasicStatisticsEntryExpectedCounts);
		assertEquals(elementMergedDBasicStatisticsEntryExpected,elementMergedDBasicStatisticsEntry);
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeStatisticsEntryMerged.getStatisticsOfNumericValuesOfNodes();
		assertEquals(1,statisticsOfNumericValuesOfNodes.size());
		BasicStatisticsEntry attr2NumericValuesInfo = statisticsOfNumericValuesOfNodes.get(attr2Merged);
		assertEquals(2.167,attr2NumericValuesInfo.getAverage(),0.001);
		assertEquals(1.139,attr2NumericValuesInfo.getVariance(),0.001);
		assertEquals(2.167,attr2NumericValuesInfo.getConditionedAverage(),0.001);
		assertEquals(1.139,attr2NumericValuesInfo.getConditionedVariance(),0.001);
		assertTrue(attr2NumericValuesInfo.getMode().isEmpty());
		assertEquals(new ValueAndFrequency(1, 4),attr2NumericValuesInfo.getMin());
		assertEquals(new ValueAndFrequency(4, 2),attr2NumericValuesInfo.getMax());
		assertEquals(26,attr2NumericValuesInfo.getTotal());
		assertEquals(1.0, attr2NumericValuesInfo.getNonZeroRatio(),0.001);
		assertEquals(0.493,attr2NumericValuesInfo.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0.493,attr2NumericValuesInfo.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<List<SchemaElement>, Integer> subpatternsInfo = complexTypeStatisticsEntryMerged.getSubpatternsInfo();
		assertEquals(17, subpatternsInfo.size());
		List<SchemaElement> subpatternABCCD = ImmutableList.of(elementMergedA , elementMergedB, elementMergedC, elementMergedC, elementMergedD);
		assertEquals(5,subpatternsInfo.get(subpatternABCCD).intValue());
		List<SchemaElement> subpatternABCC = ImmutableList.of(elementMergedA , elementMergedB, elementMergedC, elementMergedC);
		assertEquals(5,subpatternsInfo.get(subpatternABCC).intValue());
		List<SchemaElement> subpatternABCD = ImmutableList.of(elementMergedA , elementMergedB, elementMergedC, elementMergedD);
		assertEquals(3,subpatternsInfo.get(subpatternABCD).intValue());
		List<SchemaElement> subpatternABC = ImmutableList.of(elementMergedA , elementMergedB, elementMergedC);
		assertEquals(8,subpatternsInfo.get(subpatternABC).intValue());
		List<SchemaElement> subpatternAB = ImmutableList.of(elementMergedA , elementMergedB);
		assertEquals(8,subpatternsInfo.get(subpatternAB).intValue());
		List<SchemaElement> subpatternAC = ImmutableList.of(elementMergedA , elementMergedC);
		assertEquals(4,subpatternsInfo.get(subpatternAC).intValue());
		List<SchemaElement> subpatternA = ImmutableList.of(elementMergedA);
		assertEquals(12,subpatternsInfo.get(subpatternA).intValue());
		List<SchemaElement> subpatternBCCD = ImmutableList.of(elementMergedB, elementMergedC, elementMergedC, elementMergedD);
		assertEquals(5,subpatternsInfo.get(subpatternBCCD).intValue());
		List<SchemaElement> subpatternBCC = ImmutableList.of(elementMergedB, elementMergedC, elementMergedC);
		assertEquals(5,subpatternsInfo.get(subpatternBCC).intValue());
		List<SchemaElement> subpatternBCD = ImmutableList.of(elementMergedB, elementMergedC, elementMergedD);
		assertEquals(3,subpatternsInfo.get(subpatternBCD).intValue());
		List<SchemaElement> subpatternBC = ImmutableList.of(elementMergedB, elementMergedC);
		assertEquals(12,subpatternsInfo.get(subpatternBC).intValue());
		List<SchemaElement> subpatternB = ImmutableList.of(elementMergedB);
		assertEquals(12,subpatternsInfo.get(subpatternB).intValue());
		List<SchemaElement> subpatternCCD = ImmutableList.of(elementMergedC, elementMergedC, elementMergedD);
		assertEquals(5,subpatternsInfo.get(subpatternCCD).intValue());
		List<SchemaElement> subpatternCC = ImmutableList.of(elementMergedC, elementMergedC);
		assertEquals(5,subpatternsInfo.get(subpatternCC).intValue());
		List<SchemaElement> subpatternCD = ImmutableList.of(elementMergedC, elementMergedD);
		assertEquals(8,subpatternsInfo.get(subpatternCD).intValue());
		List<SchemaElement> subpatternC = ImmutableList.of(elementMergedC);
		assertEquals(21,subpatternsInfo.get(subpatternC).intValue());
		List<SchemaElement> subpatternD = ImmutableList.of(elementMergedD);
		assertEquals(8,subpatternsInfo.get(subpatternD).intValue());
		
		//Now we check the info about attribute values
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeStatisticsEntryMerged.getValuesInfo();
		assertEquals(16,valuesInfo.size());
		
		Map<String, BasicStatisticsEntry> valuesInfoAttr1 = valuesInfo.column(attr1Merged);
		assertEquals(4,valuesInfoAttr1.size());
		BasicStatisticsEntry valueAlfaAttr1BasicStatisticsEntry = valuesInfoAttr1.get("alfa");
		ArrayList<Double> valueAlfaAttr1ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueAlfaAttr1ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueAlfaAttr1ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueAlfaAttr1ABasicStatisticsEntryExpected,valueAlfaAttr1BasicStatisticsEntry);
		BasicStatisticsEntry valueBetaAttr1BasicStatisticsEntry = valuesInfoAttr1.get("beta");
		ArrayList<Double> valueBetaAttr1ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueBetaAttr1ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueBetaAttr1ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueBetaAttr1ABasicStatisticsEntryExpected,valueBetaAttr1BasicStatisticsEntry);
		BasicStatisticsEntry valueLambdaAttr1BasicStatisticsEntry = valuesInfoAttr1.get("lambda");
		ArrayList<Double> valueLambdaAttr1ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0);
		BasicStatisticsEntry valueLambdaAttr1ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueLambdaAttr1ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueLambdaAttr1ABasicStatisticsEntryExpected,valueLambdaAttr1BasicStatisticsEntry);
		BasicStatisticsEntry valueOmegaAttr1BasicStatisticsEntry = valuesInfoAttr1.get("omega");
		ArrayList<Double> valueOmegaAttr1ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0);
		BasicStatisticsEntry valueOmegaAttr1ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueOmegaAttr1ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueOmegaAttr1ABasicStatisticsEntryExpected,valueOmegaAttr1BasicStatisticsEntry);
		
		Map<String, BasicStatisticsEntry> valuesInfoAttr2 = valuesInfo.column(attr2Merged);
		assertEquals(4,valuesInfoAttr2.size());
		BasicStatisticsEntry value1Attr2BasicStatisticsEntry = valuesInfoAttr2.get("1");
		ArrayList<Double> value1Attr2ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value1Attr2ABasicStatisticsEntryExpected = new BasicStatisticsEntry(value1Attr2ABasicStatisticsEntryExpectedCounts);
		assertEquals(value1Attr2ABasicStatisticsEntryExpected,value1Attr2BasicStatisticsEntry);
		BasicStatisticsEntry value2Attr2BasicStatisticsEntry = valuesInfoAttr2.get("2");
		ArrayList<Double> value2Attr2ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value2Attr2ABasicStatisticsEntryExpected = new BasicStatisticsEntry(value2Attr2ABasicStatisticsEntryExpectedCounts);
		assertEquals(value2Attr2ABasicStatisticsEntryExpected,value2Attr2BasicStatisticsEntry);
		BasicStatisticsEntry value3Attr2BasicStatisticsEntry = valuesInfoAttr2.get("3");
		ArrayList<Double> value3Attr2ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value3Attr2ABasicStatisticsEntryExpected = new BasicStatisticsEntry(value3Attr2ABasicStatisticsEntryExpectedCounts);
		assertEquals(value3Attr2ABasicStatisticsEntryExpected,value3Attr2BasicStatisticsEntry);
		BasicStatisticsEntry value4Attr2BasicStatisticsEntry = valuesInfoAttr2.get("4");
		ArrayList<Double> value4Attr2ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,1.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value4Attr2ABasicStatisticsEntryExpected = new BasicStatisticsEntry(value4Attr2ABasicStatisticsEntryExpectedCounts);
		assertEquals(value4Attr2ABasicStatisticsEntryExpected,value4Attr2BasicStatisticsEntry);
		
		Map<String, BasicStatisticsEntry> valuesInfoAttr3 = valuesInfo.column(attr3Merged);
		assertEquals(4,valuesInfoAttr3.size());
		BasicStatisticsEntry valueTrueAttr3BasicStatisticsEntry = valuesInfoAttr3.get("true");
		ArrayList<Double> valueTrueAttr3ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueTrueAttr3ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueTrueAttr3ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueTrueAttr3ABasicStatisticsEntryExpected,valueTrueAttr3BasicStatisticsEntry);
		BasicStatisticsEntry valueFalseAttr3BasicStatisticsEntry = valuesInfoAttr3.get("false");
		ArrayList<Double> valueFalseAttr3ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueFalseAttr3ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueFalseAttr3ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueFalseAttr3ABasicStatisticsEntryExpected,valueFalseAttr3BasicStatisticsEntry);
		BasicStatisticsEntry value0Attr3BasicStatisticsEntry = valuesInfoAttr3.get("0");
		ArrayList<Double> value0Attr3ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value0Attr3ABasicStatisticsEntryExpected = new BasicStatisticsEntry(value0Attr3ABasicStatisticsEntryExpectedCounts);
		assertEquals(value0Attr3ABasicStatisticsEntryExpected,value0Attr3BasicStatisticsEntry);
		BasicStatisticsEntry value1Attr3BasicStatisticsEntry = valuesInfoAttr3.get("1");
		ArrayList<Double> value1Attr3ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value1Attr3ABasicStatisticsEntryExpected = new BasicStatisticsEntry(value1Attr3ABasicStatisticsEntryExpectedCounts);
		assertEquals(value1Attr3ABasicStatisticsEntryExpected,value1Attr3BasicStatisticsEntry);
		
		Map<String, BasicStatisticsEntry> valuesInfoAttr4 = valuesInfo.column(attr4Merged);
		assertEquals(4,valuesInfoAttr4.size());
		BasicStatisticsEntry valueGammaAttr4BasicStatisticsEntry = valuesInfoAttr4.get("gamma");
		ArrayList<Double> valueGammaAttr4ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueGammaAttr4ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueGammaAttr4ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueGammaAttr4ABasicStatisticsEntryExpected,valueGammaAttr4BasicStatisticsEntry);
		BasicStatisticsEntry valueEpsilonAttr4BasicStatisticsEntry = valuesInfoAttr4.get("epsilon");
		ArrayList<Double> valueEpsilonAttr4ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0);
		BasicStatisticsEntry valueEpsilonAttr4ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueEpsilonAttr4ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueEpsilonAttr4ABasicStatisticsEntryExpected,valueEpsilonAttr4BasicStatisticsEntry);
		BasicStatisticsEntry valueOmicronAttr4BasicStatisticsEntry = valuesInfoAttr4.get("omicron");
		ArrayList<Double> valueOmicronAttr4ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0);
		BasicStatisticsEntry valueOmicronAttr4ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueOmicronAttr4ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueOmicronAttr4ABasicStatisticsEntryExpected,valueOmicronAttr4BasicStatisticsEntry);
		BasicStatisticsEntry valueZetaAttr4BasicStatisticsEntry = valuesInfoAttr4.get("zeta");
		ArrayList<Double> valueZetaAttr4ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0);
		BasicStatisticsEntry valueZetaAttr4ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueZetaAttr4ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueZetaAttr4ABasicStatisticsEntryExpected,valueZetaAttr4BasicStatisticsEntry);
		
		//Now, we check entries of other complex types
		
		//Complex type A
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedA = complexTypeInfo.get(complexTypeMergedA);
		assertNotNull(complexTypeStatisticsEntryMergedA);
		assertEquals(16,complexTypeStatisticsEntryMergedA.getInputDocumentsCount());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfoA = complexTypeStatisticsEntryMergedA.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfoA.isEmpty());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfoA = complexTypeStatisticsEntryMergedA.getElementInfo();
		assertTrue(elementInfoA.isEmpty());
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodesA = complexTypeStatisticsEntryMergedA.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodesA.isEmpty());
		
		Map<List<SchemaElement>, Integer> subpatternsInfoA = complexTypeStatisticsEntryMergedA.getSubpatternsInfo();
		assertTrue(subpatternsInfoA.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoA = complexTypeStatisticsEntryMergedA.getValuesInfo();
		assertEquals(6, valuesInfoA.size());
		assertEquals(6, valuesInfoA.rowKeySet().size());
		assertEquals(1, valuesInfoA.columnKeySet().size());
		BasicStatisticsEntry valueCuarentaNodeABasicStatisticsEntry = valuesInfoA.get("cuarenta", elementMergedA);
		ArrayList<Double> valueCuarentaNodeABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueCuarentaNodeABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueCuarentaNodeABasicStatisticsEntryExpectedCounts);
		assertEquals(valueCuarentaNodeABasicStatisticsEntryExpected,valueCuarentaNodeABasicStatisticsEntry);
		BasicStatisticsEntry value50NodeABasicStatisticsEntry = valuesInfoA.get("50", elementMergedA);
		ArrayList<Double> value50NodeABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value50NodeABasicStatisticsEntryExpected = new BasicStatisticsEntry(value50NodeABasicStatisticsEntryExpectedCounts);
		assertEquals(value50NodeABasicStatisticsEntryExpected,value50NodeABasicStatisticsEntry);
		BasicStatisticsEntry value60NodeABasicStatisticsEntry = valuesInfoA.get("60", elementMergedA);
		ArrayList<Double> value60NodeABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value60NodeABasicStatisticsEntryExpected = new BasicStatisticsEntry(value60NodeABasicStatisticsEntryExpectedCounts);
		assertEquals(value60NodeABasicStatisticsEntryExpected,value60NodeABasicStatisticsEntry);
		BasicStatisticsEntry value70NodeABasicStatisticsEntry = valuesInfoA.get("70", elementMergedA);
		ArrayList<Double> value70NodeABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value70NodeABasicStatisticsEntryExpected = new BasicStatisticsEntry(value70NodeABasicStatisticsEntryExpectedCounts);
		assertEquals(value70NodeABasicStatisticsEntryExpected,value70NodeABasicStatisticsEntry);
		BasicStatisticsEntry valueBuenosDiasNodeABasicStatisticsEntry = valuesInfoA.get("buenos dias", elementMergedA);
		ArrayList<Double> valueBuenosDiasNodeABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0);
		BasicStatisticsEntry valueBuenosDiasNodeABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueBuenosDiasNodeABasicStatisticsEntryExpectedCounts);
		assertEquals(valueBuenosDiasNodeABasicStatisticsEntryExpected,valueBuenosDiasNodeABasicStatisticsEntry);
		BasicStatisticsEntry valueSaludosNodeABasicStatisticsEntry = valuesInfoA.get("saludos", elementMergedA);
		ArrayList<Double> valueSaludosNodeABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0);
		BasicStatisticsEntry valueSaludosNodeABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueSaludosNodeABasicStatisticsEntryExpectedCounts);
		assertEquals(valueSaludosNodeABasicStatisticsEntryExpected,valueSaludosNodeABasicStatisticsEntry);
		
		//Complex type B
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedB = complexTypeInfo.get(complexTypeMergedB);
		assertNotNull(complexTypeStatisticsEntryMergedB);
		assertEquals(16,complexTypeStatisticsEntryMergedB.getInputDocumentsCount());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfoB = complexTypeStatisticsEntryMergedB.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfoB.isEmpty());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfoB = complexTypeStatisticsEntryMergedB.getElementInfo();
		assertTrue(elementInfoB.isEmpty());
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodesB = complexTypeStatisticsEntryMergedB.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodesB.isEmpty());
		
		Map<List<SchemaElement>, Integer> subpatternsInfoB = complexTypeStatisticsEntryMergedB.getSubpatternsInfo();
		assertTrue(subpatternsInfoB.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoB = complexTypeStatisticsEntryMergedB.getValuesInfo();
		assertEquals(6, valuesInfoB.size());
		assertEquals(6, valuesInfoB.rowKeySet().size());
		assertEquals(1, valuesInfoB.columnKeySet().size());
		BasicStatisticsEntry value40NodeBBasicStatisticsEntry = valuesInfoB.get("40", elementMergedB);
		ArrayList<Double> value40NodeBBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0);
		BasicStatisticsEntry value40NodeBBasicStatisticsEntryExpected = new BasicStatisticsEntry(value40NodeBBasicStatisticsEntryExpectedCounts);
		assertEquals(value40NodeBBasicStatisticsEntryExpected,value40NodeBBasicStatisticsEntry);
		BasicStatisticsEntry value50NodeBBasicStatisticsEntry = valuesInfoB.get("50", elementMergedB);
		ArrayList<Double> value50NodeBBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,1.0,1.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0);
		BasicStatisticsEntry value50NodeBBasicStatisticsEntryExpected = new BasicStatisticsEntry(value50NodeBBasicStatisticsEntryExpectedCounts);
		assertEquals(value50NodeBBasicStatisticsEntryExpected,value50NodeBBasicStatisticsEntry);
		BasicStatisticsEntry value60NodeBBasicStatisticsEntry = valuesInfoB.get("60", elementMergedB);
		ArrayList<Double> value60NodeBBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0);
		BasicStatisticsEntry value60NodeBBasicStatisticsEntryExpected = new BasicStatisticsEntry(value60NodeBBasicStatisticsEntryExpectedCounts);
		assertEquals(value60NodeBBasicStatisticsEntryExpected,value60NodeBBasicStatisticsEntry);
		BasicStatisticsEntry value70NodeBBasicStatisticsEntry = valuesInfoB.get("70", elementMergedB);
		ArrayList<Double> value70NodeBBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0);
		BasicStatisticsEntry value70NodeBBasicStatisticsEntryExpected = new BasicStatisticsEntry(value70NodeBBasicStatisticsEntryExpectedCounts);
		assertEquals(value70NodeBBasicStatisticsEntryExpected,value70NodeBBasicStatisticsEntry);
		BasicStatisticsEntry valueHolaNodeBBasicStatisticsEntry = valuesInfoB.get("hola", elementMergedB);
		ArrayList<Double> valueHolaNodeBBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueHolaNodeBBasicStatisticsEntryExpected = new BasicStatisticsEntry(valueHolaNodeBBasicStatisticsEntryExpectedCounts);
		assertEquals(valueHolaNodeBBasicStatisticsEntryExpected,valueHolaNodeBBasicStatisticsEntry);
		BasicStatisticsEntry valueSaludosNodeBBasicStatisticsEntry = valuesInfoB.get("saludos", elementMergedB);
		ArrayList<Double> valueSaludosNodeBBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueSaludosNodeBBasicStatisticsEntryExpected = new BasicStatisticsEntry(valueSaludosNodeBBasicStatisticsEntryExpectedCounts);
		assertEquals(valueSaludosNodeBBasicStatisticsEntryExpected,valueSaludosNodeBBasicStatisticsEntry);
		
		//Complex type C
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedC = complexTypeInfo.get(complexTypeMergedC);
		assertNotNull(complexTypeStatisticsEntryMergedC);
		assertEquals(16,complexTypeStatisticsEntryMergedC.getInputDocumentsCount());

		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfoC = complexTypeStatisticsEntryMergedC.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfoC.isEmpty());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfoC = complexTypeStatisticsEntryMergedC.getElementInfo();
		assertEquals(2, elementInfoC.size());
		BasicStatisticsEntry elementMergedCEBasicStatisticsEntry = elementInfoC.get(elementMergedCE);
		ArrayList<Double> elementMergedCEBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry elementMergedCEBasicStatisticsEntryExpected = new BasicStatisticsEntry(elementMergedCEBasicStatisticsEntryExpectedCounts);
		assertEquals(elementMergedCEBasicStatisticsEntryExpected,elementMergedCEBasicStatisticsEntry);
		BasicStatisticsEntry elementMergedCFBasicStatisticsEntry = elementInfoC.get(elementMergedCF);
		ArrayList<Double> elementMergedCFBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,2.0,2.0,2.0,2.0,2.0,1.0,1.0,1.0);
		BasicStatisticsEntry elementMergedCFBasicStatisticsEntryExpected = new BasicStatisticsEntry(elementMergedCFBasicStatisticsEntryExpectedCounts);
		assertEquals(elementMergedCFBasicStatisticsEntryExpected,elementMergedCFBasicStatisticsEntry);
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodesC = complexTypeStatisticsEntryMergedC.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodesC.isEmpty());
		
		Map<List<SchemaElement>, Integer> subpatternsInfoC = complexTypeStatisticsEntryMergedC.getSubpatternsInfo();
		List<SchemaElement> subpatternE = ImmutableList.of(elementMergedCE);
		assertEquals(8, subpatternsInfoC.get(subpatternE).intValue());
		List<SchemaElement> subpatternF = ImmutableList.of(elementMergedCF);
		assertEquals(13, subpatternsInfoC.get(subpatternF).intValue());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoC = complexTypeStatisticsEntryMergedC.getValuesInfo();
		assertTrue(valuesInfoC.isEmpty());
		
		//Complex type D
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedD = complexTypeInfo.get(complexTypeMergedD);
		assertNotNull(complexTypeStatisticsEntryMergedD);
		assertEquals(16,complexTypeStatisticsEntryMergedD.getInputDocumentsCount());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfoD = complexTypeStatisticsEntryMergedD.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfoD.isEmpty());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfoD = complexTypeStatisticsEntryMergedD.getElementInfo();
		assertTrue(elementInfoD.isEmpty());
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodesD = complexTypeStatisticsEntryMergedD.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodesD.isEmpty());
		
		Map<List<SchemaElement>, Integer> subpatternsInfoD = complexTypeStatisticsEntryMergedD.getSubpatternsInfo();
		assertTrue(subpatternsInfoD.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoD = complexTypeStatisticsEntryMergedD.getValuesInfo();
		assertTrue(valuesInfoD.isEmpty());
		
		//Complex type E
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedE = complexTypeInfo.get(complexTypeMergedE);
		assertNotNull(complexTypeStatisticsEntryMergedE);
		assertEquals(16,complexTypeStatisticsEntryMergedE.getInputDocumentsCount());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfoE = complexTypeStatisticsEntryMergedE.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfoE.isEmpty());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfoE = complexTypeStatisticsEntryMergedE.getElementInfo();
		assertTrue(elementInfoE.isEmpty());
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodesE = complexTypeStatisticsEntryMergedE.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodesE.isEmpty());
		
		Map<List<SchemaElement>, Integer> subpatternsInfoE = complexTypeStatisticsEntryMergedE.getSubpatternsInfo();
		assertTrue(subpatternsInfoE.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoE = complexTypeStatisticsEntryMergedE.getValuesInfo();
		assertTrue(valuesInfoE.isEmpty());
		
		//Complex type D
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedF = complexTypeInfo.get(complexTypeMergedF);
		assertNotNull(complexTypeStatisticsEntryMergedF);
		assertEquals(16,complexTypeStatisticsEntryMergedF.getInputDocumentsCount());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfoF = complexTypeStatisticsEntryMergedF.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfoF.isEmpty());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfoF = complexTypeStatisticsEntryMergedF.getElementInfo();
		assertTrue(elementInfoF.isEmpty());
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodesF = complexTypeStatisticsEntryMergedF.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodesF.isEmpty());
		
		Map<List<SchemaElement>, Integer> subpatternsInfoF = complexTypeStatisticsEntryMergedF.getSubpatternsInfo();
		assertTrue(subpatternsInfoF.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoF = complexTypeStatisticsEntryMergedF.getValuesInfo();
		assertTrue(valuesInfoF.isEmpty());
		
		
	}
	
	/**
	 * Checks that a merge made only via same name comparators is well made
	 */
	@Test
	public void testScenario1OnlySameNameComplexTypeMerge(){
		ChildrenPatternComparator onlyElement1CAndElement2CChildrenPatternsAreSimilar=mock(ChildrenPatternComparator.class);
		when(onlyElement1CAndElement2CChildrenPatternsAreSimilar.compare(any(ExtendedAutomaton.class), any(ExtendedAutomaton.class))).thenReturn(false);
		when(onlyElement1CAndElement2CChildrenPatternsAreSimilar.compare(complexTypeScenario1RootElement1NodeC.getAutomaton(), complexTypeScenario1RootElement2NodeC.getAutomaton())).thenReturn(true);
		
		AttributeListComparator onlyElement1CAndElement2CAttributeListsAreSimilar=mock(AttributeListComparator.class);
		when(onlyElement1CAndElement2CAttributeListsAreSimilar.compare(anyListOf(SchemaAttribute.class), anyListOf(SchemaAttribute.class))).thenReturn(false);
		when(onlyElement1CAndElement2CAttributeListsAreSimilar.compare(complexTypeScenario1RootElement1NodeC.getAttributeList(), complexTypeScenario1RootElement2NodeC.getAttributeList())).thenReturn(true);
		
		when(configuration1.getChildrenPatternComparator()).thenReturn(alwaysFalseChildrenPatternComparator);
		when(configuration1.getAttributeListComparator()).thenReturn(alwaysFalseAttributeListComparator);
		when(configuration1.getEnumsComparator()).thenReturn(alwaysFalseEnumComparator);
		
		when(configuration1.getSnAttributeListComparator()).thenReturn(onlyElement1CAndElement2CAttributeListsAreSimilar);
		when(configuration1.getSnChildrenPatternComparator()).thenReturn(onlyElement1CAndElement2CChildrenPatternsAreSimilar);
		
		TypeMerger typeMerger = new TypeMergerImpl();
		typeMerger.mergeTypes(schema1, configuration1);
		
		Table<String, String, SchemaElement> schemaElements = schema1.getElements();
		assertFalse(schemaElements.get("","_root-element1").getType().equals(schemaElements.get("","_root-element2").getType()));
		
		SchemaElement elementMergedCE=schemaElements.get("", "_root-_element1-_C_and__root-_element2-_C-E");
		SchemaElement elementMergedCF=schemaElements.get("", "_root-_element1-_C_and__root-_element2-_C-F");
		
		ComplexType complexTypeMergedC = schema1.getComplexTypes().get("_root-_element1-_C_and__root-_element2-_C");
		assertEquals(complexTypeMergedC,schemaElements.get("", "_root-_element1-C").getType());
		assertEquals(complexTypeMergedC,schemaElements.get("", "_root-_element2-C").getType());
		ExtendedAutomaton automatonMergedC = complexTypeMergedC.getAutomaton();
		assertTrue(automatonMergedC.containsAllNodes(ImmutableSet.of(elementMergedCE,elementMergedCF)));
		assertEquals(8, automatonMergedC.getEdgeWeight(initialState, elementMergedCE));
		assertEquals(8, automatonMergedC.getEdgeWeight(elementMergedCE, finalState));
		assertEquals(8, automatonMergedC.getEdgeWeight(initialState, elementMergedCF));
		assertEquals(8, automatonMergedC.getEdgeWeight(elementMergedCF, finalState));
	}
	
	/**
	 * In this method we checks that a situation when all kinds of merge should be done are done correctly.
	 * Note that the results are the same than in {@link TypeMergerImplTests()#testScenario1OnlyNormalComplexTypeMerge()}, 
	 * the difference is that the forced merges of that method are not forced here
	 */
	@Test
	public void testScenario1MergesOfAllKinds(){
		ChildrenPatternComparator onlyElement1AndElement2ChildrenPatternsAreSimilar=mock(ChildrenPatternComparator.class);
		when(onlyElement1AndElement2ChildrenPatternsAreSimilar.compare(any(ExtendedAutomaton.class), any(ExtendedAutomaton.class))).thenReturn(false);
		when(onlyElement1AndElement2ChildrenPatternsAreSimilar.compare(complexTypeScenario1RootElement1.getAutomaton(), complexTypeScenario1RootElement2.getAutomaton())).thenReturn(true);
		
		AttributeListComparator onlyElement1AndElement2AttributeListsAreSimilar=mock(AttributeListComparator.class);
		when(onlyElement1AndElement2AttributeListsAreSimilar.compare(anyListOf(SchemaAttribute.class), anyListOf(SchemaAttribute.class))).thenReturn(false);
		when(onlyElement1AndElement2AttributeListsAreSimilar.compare(complexTypeScenario1RootElement1.getAttributeList(), complexTypeScenario1RootElement2.getAttributeList())).thenReturn(true);
		
		ChildrenPatternComparator onlyElement1CAndElement2CChildrenPatternsAreSimilar=mock(ChildrenPatternComparator.class);
		when(onlyElement1CAndElement2CChildrenPatternsAreSimilar.compare(any(ExtendedAutomaton.class), any(ExtendedAutomaton.class))).thenReturn(false);
		when(onlyElement1CAndElement2CChildrenPatternsAreSimilar.compare(complexTypeScenario1RootElement1NodeC.getAutomaton(), complexTypeScenario1RootElement2NodeC.getAutomaton())).thenReturn(true);
		
		AttributeListComparator onlyElement1CAndElement2CAttributeListsAreSimilar=mock(AttributeListComparator.class);
		when(onlyElement1CAndElement2CAttributeListsAreSimilar.compare(anyListOf(SchemaAttribute.class), anyListOf(SchemaAttribute.class))).thenReturn(false);
		when(onlyElement1CAndElement2CAttributeListsAreSimilar.compare(complexTypeScenario1RootElement1NodeC.getAttributeList(), complexTypeScenario1RootElement2NodeC.getAttributeList())).thenReturn(true);
		
		EnumComparator onlySimpleTypes1AAnd2BAreMerged = mock(EnumComparator.class);
		when(onlySimpleTypes1AAnd2BAreMerged.compare(any(SimpleType.class), any(SimpleType.class))).thenReturn(false);
		when(onlySimpleTypes1AAnd2BAreMerged.compare(simpleTypeScenario1RootElement1NodeA, simpleTypeScenario1RootElement2NodeB)).thenReturn(true);
		when(configuration1.getEnumsComparator()).thenReturn(onlySimpleTypes1AAnd2BAreMerged);
//		EnumComparator onlySimpleTypesOfBAreMerged = mock(EnumComparator.class);
//		when(onlySimpleTypesOfBAreMerged.compare(any(SimpleType.class), any(SimpleType.class))).thenReturn(false);
//		when(onlySimpleTypesOfBAreMerged.compare(simpleTypeScenario1RootElement1NodeB, simpleTypeScenario1RootElement2NodeB)).thenReturn(true);
//		when(configuration1.getSnEnumsComparator()).thenReturn(onlySimpleTypesOfBAreMerged);
		
		when(configuration1.getChildrenPatternComparator()).thenReturn(onlyElement1AndElement2ChildrenPatternsAreSimilar);
		when(configuration1.getSnChildrenPatternComparator()).thenReturn(onlyElement1CAndElement2CChildrenPatternsAreSimilar);
		when(configuration1.getAttributeListComparator()).thenReturn(onlyElement1AndElement2AttributeListsAreSimilar);
		when(configuration1.getSnAttributeListComparator()).thenReturn(onlyElement1CAndElement2CAttributeListsAreSimilar);
		
		when(configuration1.getEnumsComparator()).thenReturn(onlySimpleTypes1AAnd2BAreMerged);
		when(configuration1.getSnEnumsComparator()).thenReturn(alwaysFalseEnumComparator);
		
		TypeMerger typeMerger = new TypeMergerImpl();
		typeMerger.mergeTypes(schema1, configuration1);
		
		//First, we check that element1 and element2 point to the same merged complex type, that it exists at the schema structures and that the old complex types do not exist anymore
		//We also check that the merged complex type name is correct.s
		Table<String, String, SchemaElement> schemaElements = schema1.getElements();
		Map<String, ComplexType> schemaComplexTypes = schema1.getComplexTypes();
		assertEquals(schemaElements.get("", "_root-element1").getType(),schemaElements.get("", "_root-element2").getType());
		String complexTypeMergedExpectedName=complexTypeScenario1RootElement1.getName()+configuration1.getMergedTypesSeparator()+complexTypeScenario1RootElement2.getName();
		assertTrue(schemaComplexTypes.containsKey(complexTypeMergedExpectedName));
		assertFalse(schemaComplexTypes.containsKey("_root-_element1"));
		assertFalse(schemaComplexTypes.containsKey("_root-_element2"));
		ComplexType complexTypeMerged = schemaComplexTypes.get(complexTypeMergedExpectedName);
		assertEquals(complexTypeMerged,elementScenario1RootElement1.getType());
		assertEquals(complexTypeMerged,elementScenario1RootElement2.getType());
		assertEquals(complexTypeMergedExpectedName,complexTypeMerged.getName());
		
		//Then, we check that the automatons of the complex type has been properly merged
		SchemaElement elementMergedA=schemaElements.get("", "_root-_element1_and__root-_element2-A");
		SchemaElement elementMergedB=schemaElements.get("", "_root-_element1_and__root-_element2-B");
		SchemaElement elementMergedC=schemaElements.get("", "_root-_element1_and__root-_element2-C");
		SchemaElement elementMergedD=schemaElements.get("", "_root-_element1_and__root-_element2-D");
		
		SchemaElement elementMergedCE=schemaElements.get("", "_root-_element1-_C_and__root-_element2-_C-E");
		SchemaElement elementMergedCF=schemaElements.get("", "_root-_element1-_C_and__root-_element2-_C-F");
		
		ExtendedAutomaton mergedAutomaton = complexTypeMerged.getAutomaton();
		assertTrue(mergedAutomaton.containsAllNodes(Lists.newArrayList(elementMergedA,elementMergedB,elementMergedC,elementMergedD)));
		assertEquals(12, mergedAutomaton.getEdgeWeight(initialState, elementMergedA));
		assertEquals(4, mergedAutomaton.getEdgeWeight(initialState, elementMergedB));
		assertEquals(8, mergedAutomaton.getEdgeWeight(elementMergedA, elementMergedB));
		assertEquals(4, mergedAutomaton.getEdgeWeight(elementMergedA, elementMergedC));
		assertEquals(12, mergedAutomaton.getEdgeWeight(elementMergedB, elementMergedC));
		assertEquals(5, mergedAutomaton.getEdgeWeight(elementMergedC, elementMergedC));
		assertEquals(8, mergedAutomaton.getEdgeWeight(elementMergedC, elementMergedD));
		assertEquals(8, mergedAutomaton.getEdgeWeight(elementMergedC, finalState));
		assertEquals(8, mergedAutomaton.getEdgeWeight(elementMergedD, finalState));
		
		//Now, we check that complex types of elements which are descendants of element1 or element2 are also unavoidably and recursively merged 
		//(for example, any complex type from an A node is merged with any complex type of an A node but not with any complex type of a B node) 
		//Although the other merges are forced, there are no other elements of those complex types, so they should not exist anymore
		String complexTypeAMergedExpectedName = "_root-_element1-_A_and__root-_element2-_A";
		assertTrue(schemaComplexTypes.containsKey(complexTypeAMergedExpectedName));
		assertFalse(schemaComplexTypes.containsKey("_root-_element1-_A"));
		assertFalse(schemaComplexTypes.containsKey("_root-_element2-_A"));
		ComplexType complexTypeMergedA = schemaComplexTypes.get(complexTypeAMergedExpectedName);
		assertEquals(complexTypeAMergedExpectedName,complexTypeMergedA.getName());
		
		String complexTypeBMergedExpectedName = "_root-_element1-_B_and__root-_element2-_B";
		assertTrue(schemaComplexTypes.containsKey(complexTypeBMergedExpectedName));
		assertFalse(schemaComplexTypes.containsKey("_root-_element1-_B"));
		assertFalse(schemaComplexTypes.containsKey("_root-_element2-_B"));
		ComplexType complexTypeMergedB = schemaComplexTypes.get(complexTypeBMergedExpectedName);
		assertEquals(complexTypeBMergedExpectedName,complexTypeMergedB.getName());
		
		assertTrue(schemaComplexTypes.containsKey("_root-_element1-_C_and__root-_element2-_C"));
		assertFalse(schemaComplexTypes.containsKey("_root-_element1-_C"));
		assertFalse(schemaComplexTypes.containsKey("_root-_element2-_C"));
		ComplexType complexTypeMergedC = schemaComplexTypes.get("_root-_element1-_C_and__root-_element2-_C");
		assertEquals("_root-_element1-_C_and__root-_element2-_C",complexTypeMergedC.getName());
		ExtendedAutomaton automatonMergedC = complexTypeMergedC.getAutomaton();
		assertTrue(automatonMergedC.containsAllNodes(ImmutableSet.of(elementMergedCE,elementMergedCF)));
		assertEquals(8, automatonMergedC.getEdgeWeight(initialState, elementMergedCE));
		assertEquals(8, automatonMergedC.getEdgeWeight(elementMergedCE, finalState));
		assertEquals(8, automatonMergedC.getEdgeWeight(initialState, elementMergedCF));
		assertEquals(8, automatonMergedC.getEdgeWeight(elementMergedCF, finalState));
		String complexTypeDMergedExpectedName = "_root-_element2-_D";
		assertTrue(schemaComplexTypes.containsKey(complexTypeDMergedExpectedName));
		ComplexType complexTypeMergedD = schemaComplexTypes.get(complexTypeDMergedExpectedName);
		assertEquals(complexTypeDMergedExpectedName,complexTypeMergedD.getName());
		
		String complexTypeEMergedExpectedName = "_root-_element1-_C-_E";
		assertTrue(schemaComplexTypes.containsKey(complexTypeEMergedExpectedName));
		ComplexType complexTypeMergedE = schemaComplexTypes.get(complexTypeEMergedExpectedName);
		assertEquals(complexTypeEMergedExpectedName,complexTypeMergedE.getName());
		
		String complexTypeFMergedExpectedName = "_root-_element2-_C-_F";
		assertTrue(schemaComplexTypes.containsKey(complexTypeFMergedExpectedName));
		ComplexType complexTypeMergedF = schemaComplexTypes.get(complexTypeFMergedExpectedName);
		assertEquals(complexTypeFMergedExpectedName,complexTypeMergedF.getName());
				
		
		//Now, we check that the simple types of nodes A and B are properly merged
//		String simpleType1A2BName = simpleTypeScenario1RootElement1NodeA.getName() + configuration1.getMergedTypesSeparator() + simpleTypeScenario1RootElement2NodeB.getName();
		String simpleTypeAExpectedName = simpleTypeScenario1RootElement1NodeA.getName() + configuration1.getMergedTypesSeparator() + simpleTypeScenario1RootElement2NodeA.getName() + configuration1.getMergedTypesSeparator() + simpleTypeScenario1RootElement2NodeB.getName();
		String simpleTypeBExpectedName = simpleTypeScenario1RootElement1NodeA.getName() + configuration1.getMergedTypesSeparator() + simpleTypeScenario1RootElement1NodeB.getName() + configuration1.getMergedTypesSeparator() + simpleTypeScenario1RootElement2NodeB.getName();
		
		SimpleType simpleTypeAMerged = schema1.getSimpleTypes().get(simpleTypeAExpectedName);
		assertEquals(simpleTypeAMerged,schemaComplexTypes.get(complexTypeAMergedExpectedName).getTextSimpleType());
		assertEquals(simpleTypeAExpectedName,
				simpleTypeAMerged.getName());
		assertEquals("xs:string",simpleTypeAMerged.getBuiltinType());
		assertEquals(7,simpleTypeAMerged.enumerationCount());
		assertTrue(simpleTypeAMerged.isEnum());
		assertTrue(simpleTypeAMerged.enumerationContainsAll(Arrays.asList("40", "50", "60", "70", "cuarenta", "buenos dias", "saludos")));
		
		SimpleType simpleTypeBMerged = schema1.getSimpleTypes().get(simpleTypeBExpectedName);
		assertEquals(simpleTypeBMerged,schemaComplexTypes.get(complexTypeBMergedExpectedName).getTextSimpleType());
		assertEquals(simpleTypeBExpectedName,
				simpleTypeBMerged.getName());
		assertEquals("xs:string",simpleTypeBMerged.getBuiltinType());
		assertEquals(7,simpleTypeBMerged.enumerationCount());
		assertTrue(simpleTypeBMerged.isEnum());
		assertTrue(simpleTypeBMerged.enumerationContainsAll(Arrays.asList("hola", "saludos", "40", "50", "60", "70")));

		//Now, we check that the simple types of attr1 and attr2 are properly merged
		String simpleTypeAttr1ExpectedName = simpleTypeScenario1Element1Attr1.getName() + configuration1.getMergedTypesSeparator() + simpleTypeScenario1Element2Attr1.getName();
		String simpleTypeAttr2ExpectedName = simpleTypeScenario1Element1Attr2.getName() + configuration1.getMergedTypesSeparator() + simpleTypeScenario1Element2Attr2.getName();
		
		SimpleType simpleTypeAttr1Merged = schema1.getSimpleTypes().get(simpleTypeAttr1ExpectedName);
		assertEquals(simpleTypeAttr1Merged,complexTypeMerged.getAttributeList().get(0).getSimpleType());
		assertEquals(simpleTypeAttr1ExpectedName,
				simpleTypeAttr1Merged.getName());
		assertEquals("xs:string",simpleTypeAttr1Merged.getBuiltinType());
		assertEquals(4,simpleTypeAttr1Merged.enumerationCount());
		assertTrue(simpleTypeAttr1Merged.isEnum());
		assertTrue(simpleTypeAttr1Merged.enumerationContainsAll(Arrays.asList("alfa","beta","lambda","omega")));
		
		SimpleType simpleTypeAttr2Merged = schema1.getSimpleTypes().get(simpleTypeAttr2ExpectedName);
		assertEquals(simpleTypeAttr2Merged,complexTypeMerged.getAttributeList().get(1).getSimpleType());
		assertEquals(simpleTypeAttr2ExpectedName,
				simpleTypeAttr2Merged.getName());
		assertEquals("xs:integer",simpleTypeAttr2Merged.getBuiltinType());
		assertEquals(4,simpleTypeAttr2Merged.enumerationCount());
		assertTrue(simpleTypeAttr2Merged.isEnum());
		assertTrue(simpleTypeAttr2Merged.enumerationContainsAll(Arrays.asList("1", "2", "3", "4")));
		
		//Now, we check the attributes and the attribute lists
		Table<String, String, SchemaAttribute> schemaAttributes = schema1.getAttributes();
		assertEquals(4,schemaAttributes.size());
		assertTrue(schemaAttributes.contains("", "_root-_element1_and__root-_element2-attr1"));
		assertTrue(schemaAttributes.contains("", "_root-_element1_and__root-_element2-attr2"));
		assertTrue(schemaAttributes.contains("", "_root-_element1_and__root-_element2-attr3"));
		assertTrue(schemaAttributes.contains("", "_root-_element1_and__root-_element2-attr4"));
		SchemaAttribute attr1Merged = schemaAttributes.get("", "_root-_element1_and__root-_element2-attr1");
		SchemaAttribute attr2Merged = schemaAttributes.get("", "_root-_element1_and__root-_element2-attr2");
		SchemaAttribute attr3Merged = schemaAttributes.get("", "_root-_element1_and__root-_element2-attr3");
		SchemaAttribute attr4Merged = schemaAttributes.get("", "_root-_element1_and__root-_element2-attr4");
		List<SchemaAttribute> attributeListMerged = schemaComplexTypes.get("_root-_element1_and__root-_element2").getAttributeList();
		assertEquals(4,attributeListMerged.size());
		assertTrue(attributeListMerged.containsAll(Arrays.asList(attr1Merged,attr2Merged,attr3Merged,attr4Merged)));
		assertEquals("attr1",attr1Merged.getName());
		assertEquals("", attr1Merged.getNamespace());
		assertFalse(attr1Merged.isOptional());
		assertEquals("xs:string",attr1Merged.getSimpleType().getBuiltinType());
		assertEquals("attr2",attr2Merged.getName());
		assertEquals("", attr2Merged.getNamespace());
		assertTrue(attr2Merged.isOptional());
		assertEquals("xs:integer",attr2Merged.getSimpleType().getBuiltinType());
		assertEquals("attr3",attr3Merged.getName());
		assertEquals("", attr3Merged.getNamespace());
		assertTrue(attr3Merged.isOptional());
		assertEquals("xs:boolean",attr3Merged.getSimpleType().getBuiltinType());
		assertEquals("attr4",attr4Merged.getName());
		assertEquals("", attr4Merged.getNamespace());
		assertTrue(attr4Merged.isOptional());
		assertEquals("xs:string",attr4Merged.getSimpleType().getBuiltinType());	
		
		//Check statistics
		Statistics statistics = schema1.getStatistics();
		Map<ComplexType, ComplexTypeStatisticsEntry> complexTypeInfo = statistics.getComplexTypeInfo();
		assertNull(complexTypeInfo.get(complexTypeScenario1RootElement1));
		assertNull(complexTypeInfo.get(complexTypeScenario1RootElement2));
		
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMerged = complexTypeInfo.get(complexTypeMerged);
		assertNotNull(complexTypeStatisticsEntryMerged);
		assertEquals(16,complexTypeStatisticsEntryMerged.getInputDocumentsCount());
		
		//Attribute occurrences info
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeStatisticsEntryMerged.getAttributeOccurrencesInfo();
		assertEquals(4, attributeOccurrencesInfo.size());
		BasicStatisticsEntry attr1OccurrencesBasicStatisticsEntry = attributeOccurrencesInfo.get(attr1Merged);
		ArrayList<Double> attr1OccurrencesBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		BasicStatisticsEntry attr1OccurrencesBasicStatisticsEntryExpected = new BasicStatisticsEntry(attr1OccurrencesBasicStatisticsEntryExpectedCounts);
		assertEquals(attr1OccurrencesBasicStatisticsEntryExpected,attr1OccurrencesBasicStatisticsEntry);
		BasicStatisticsEntry attr2OccurrencesBasicStatisticsEntry = attributeOccurrencesInfo.get(attr2Merged);
		ArrayList<Double> attr2OccurrencesBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry attr2OccurrencesBasicStatisticsEntryExpected = new BasicStatisticsEntry(attr2OccurrencesBasicStatisticsEntryExpectedCounts);
		assertEquals(attr2OccurrencesBasicStatisticsEntryExpected,attr2OccurrencesBasicStatisticsEntry);
		BasicStatisticsEntry attr3OccurrencesBasicStatisticsEntry = attributeOccurrencesInfo.get(attr3Merged);
		ArrayList<Double> attr3OccurrencesBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry attr3OccurrencesBasicStatisticsEntryExpected = new BasicStatisticsEntry(attr3OccurrencesBasicStatisticsEntryExpectedCounts);
		assertEquals(attr3OccurrencesBasicStatisticsEntryExpected,attr3OccurrencesBasicStatisticsEntry);
		BasicStatisticsEntry attr4OccurrencesBasicStatisticsEntry = attributeOccurrencesInfo.get(attr4Merged);
		ArrayList<Double> attr4OccurrencesBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,1.0,1.0,1.0);
		BasicStatisticsEntry attr4OccurrencesBasicStatisticsEntryExpected = new BasicStatisticsEntry(attr4OccurrencesBasicStatisticsEntryExpectedCounts);
		assertEquals(attr4OccurrencesBasicStatisticsEntryExpected,attr4OccurrencesBasicStatisticsEntry);
		
		
		//Element occurrences info
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeStatisticsEntryMerged.getElementInfo();
		assertEquals(4, elementInfo.size());
		BasicStatisticsEntry elementMergedABasicStatisticsEntry = elementInfo.get(elementMergedA);
		ArrayList<Double> elementMergedABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,1.0,1.0,1.0,0.0,0.0,0.0,0.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		BasicStatisticsEntry elementMergedABasicStatisticsEntryExpected = new BasicStatisticsEntry(elementMergedABasicStatisticsEntryExpectedCounts);
		assertEquals(elementMergedABasicStatisticsEntryExpected,elementMergedABasicStatisticsEntry);
		BasicStatisticsEntry elementMergedBBasicStatisticsEntry = elementInfo.get(elementMergedB);
		ArrayList<Double> elementMergedBBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		BasicStatisticsEntry elementMergedBBasicStatisticsEntryExpected = new BasicStatisticsEntry(elementMergedBBasicStatisticsEntryExpectedCounts);
		assertEquals(elementMergedBBasicStatisticsEntryExpected,elementMergedBBasicStatisticsEntry);
		BasicStatisticsEntry elementMergedCBasicStatisticsEntry = elementInfo.get(elementMergedC);
		ArrayList<Double> elementMergedCBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,2.0,2.0,2.0,2.0,2.0,1.0,1.0,1.0);
		BasicStatisticsEntry elementMergedCBasicStatisticsEntryExpected = new BasicStatisticsEntry(elementMergedCBasicStatisticsEntryExpectedCounts);
		assertEquals(elementMergedCBasicStatisticsEntryExpected,elementMergedCBasicStatisticsEntry);
		BasicStatisticsEntry elementMergedDBasicStatisticsEntry = elementInfo.get(elementMergedD);
		ArrayList<Double> elementMergedDBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		BasicStatisticsEntry elementMergedDBasicStatisticsEntryExpected = new BasicStatisticsEntry(elementMergedDBasicStatisticsEntryExpectedCounts);
		assertEquals(elementMergedDBasicStatisticsEntryExpected,elementMergedDBasicStatisticsEntry);
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeStatisticsEntryMerged.getStatisticsOfNumericValuesOfNodes();
		assertEquals(1,statisticsOfNumericValuesOfNodes.size());
		BasicStatisticsEntry attr2NumericValuesInfo = statisticsOfNumericValuesOfNodes.get(attr2Merged);
		assertEquals(2.167,attr2NumericValuesInfo.getAverage(),0.001);
		assertEquals(1.139,attr2NumericValuesInfo.getVariance(),0.001);
		assertEquals(2.167,attr2NumericValuesInfo.getConditionedAverage(),0.001);
		assertEquals(1.139,attr2NumericValuesInfo.getConditionedVariance(),0.001);
		assertTrue(attr2NumericValuesInfo.getMode().isEmpty());
		assertEquals(new ValueAndFrequency(1, 4),attr2NumericValuesInfo.getMin());
		assertEquals(new ValueAndFrequency(4, 2),attr2NumericValuesInfo.getMax());
		assertEquals(26,attr2NumericValuesInfo.getTotal());
		assertEquals(1.0, attr2NumericValuesInfo.getNonZeroRatio(),0.001);
		assertEquals(0.493,attr2NumericValuesInfo.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0.493,attr2NumericValuesInfo.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<List<SchemaElement>, Integer> subpatternsInfo = complexTypeStatisticsEntryMerged.getSubpatternsInfo();
		assertEquals(17, subpatternsInfo.size());
		List<SchemaElement> subpatternABCCD = ImmutableList.of(elementMergedA , elementMergedB, elementMergedC, elementMergedC, elementMergedD);
		assertEquals(5,subpatternsInfo.get(subpatternABCCD).intValue());
		List<SchemaElement> subpatternABCC = ImmutableList.of(elementMergedA , elementMergedB, elementMergedC, elementMergedC);
		assertEquals(5,subpatternsInfo.get(subpatternABCC).intValue());
		List<SchemaElement> subpatternABCD = ImmutableList.of(elementMergedA , elementMergedB, elementMergedC, elementMergedD);
		assertEquals(3,subpatternsInfo.get(subpatternABCD).intValue());
		List<SchemaElement> subpatternABC = ImmutableList.of(elementMergedA , elementMergedB, elementMergedC);
		assertEquals(8,subpatternsInfo.get(subpatternABC).intValue());
		List<SchemaElement> subpatternAB = ImmutableList.of(elementMergedA , elementMergedB);
		assertEquals(8,subpatternsInfo.get(subpatternAB).intValue());
		List<SchemaElement> subpatternAC = ImmutableList.of(elementMergedA , elementMergedC);
		assertEquals(4,subpatternsInfo.get(subpatternAC).intValue());
		List<SchemaElement> subpatternA = ImmutableList.of(elementMergedA);
		assertEquals(12,subpatternsInfo.get(subpatternA).intValue());
		List<SchemaElement> subpatternBCCD = ImmutableList.of(elementMergedB, elementMergedC, elementMergedC, elementMergedD);
		assertEquals(5,subpatternsInfo.get(subpatternBCCD).intValue());
		List<SchemaElement> subpatternBCC = ImmutableList.of(elementMergedB, elementMergedC, elementMergedC);
		assertEquals(5,subpatternsInfo.get(subpatternBCC).intValue());
		List<SchemaElement> subpatternBCD = ImmutableList.of(elementMergedB, elementMergedC, elementMergedD);
		assertEquals(3,subpatternsInfo.get(subpatternBCD).intValue());
		List<SchemaElement> subpatternBC = ImmutableList.of(elementMergedB, elementMergedC);
		assertEquals(12,subpatternsInfo.get(subpatternBC).intValue());
		List<SchemaElement> subpatternB = ImmutableList.of(elementMergedB);
		assertEquals(12,subpatternsInfo.get(subpatternB).intValue());
		List<SchemaElement> subpatternCCD = ImmutableList.of(elementMergedC, elementMergedC, elementMergedD);
		assertEquals(5,subpatternsInfo.get(subpatternCCD).intValue());
		List<SchemaElement> subpatternCC = ImmutableList.of(elementMergedC, elementMergedC);
		assertEquals(5,subpatternsInfo.get(subpatternCC).intValue());
		List<SchemaElement> subpatternCD = ImmutableList.of(elementMergedC, elementMergedD);
		assertEquals(8,subpatternsInfo.get(subpatternCD).intValue());
		List<SchemaElement> subpatternC = ImmutableList.of(elementMergedC);
		assertEquals(21,subpatternsInfo.get(subpatternC).intValue());
		List<SchemaElement> subpatternD = ImmutableList.of(elementMergedD);
		assertEquals(8,subpatternsInfo.get(subpatternD).intValue());
		
		//Now we check the info about attribute values
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeStatisticsEntryMerged.getValuesInfo();
		assertEquals(16,valuesInfo.size());
		
		Map<String, BasicStatisticsEntry> valuesInfoAttr1 = valuesInfo.column(attr1Merged);
		assertEquals(4,valuesInfoAttr1.size());
		BasicStatisticsEntry valueAlfaAttr1BasicStatisticsEntry = valuesInfoAttr1.get("alfa");
		ArrayList<Double> valueAlfaAttr1ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueAlfaAttr1ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueAlfaAttr1ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueAlfaAttr1ABasicStatisticsEntryExpected,valueAlfaAttr1BasicStatisticsEntry);
		BasicStatisticsEntry valueBetaAttr1BasicStatisticsEntry = valuesInfoAttr1.get("beta");
		ArrayList<Double> valueBetaAttr1ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueBetaAttr1ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueBetaAttr1ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueBetaAttr1ABasicStatisticsEntryExpected,valueBetaAttr1BasicStatisticsEntry);
		BasicStatisticsEntry valueLambdaAttr1BasicStatisticsEntry = valuesInfoAttr1.get("lambda");
		ArrayList<Double> valueLambdaAttr1ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0);
		BasicStatisticsEntry valueLambdaAttr1ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueLambdaAttr1ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueLambdaAttr1ABasicStatisticsEntryExpected,valueLambdaAttr1BasicStatisticsEntry);
		BasicStatisticsEntry valueOmegaAttr1BasicStatisticsEntry = valuesInfoAttr1.get("omega");
		ArrayList<Double> valueOmegaAttr1ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0);
		BasicStatisticsEntry valueOmegaAttr1ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueOmegaAttr1ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueOmegaAttr1ABasicStatisticsEntryExpected,valueOmegaAttr1BasicStatisticsEntry);
		
		Map<String, BasicStatisticsEntry> valuesInfoAttr2 = valuesInfo.column(attr2Merged);
		assertEquals(4,valuesInfoAttr2.size());
		BasicStatisticsEntry value1Attr2BasicStatisticsEntry = valuesInfoAttr2.get("1");
		ArrayList<Double> value1Attr2ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value1Attr2ABasicStatisticsEntryExpected = new BasicStatisticsEntry(value1Attr2ABasicStatisticsEntryExpectedCounts);
		assertEquals(value1Attr2ABasicStatisticsEntryExpected,value1Attr2BasicStatisticsEntry);
		BasicStatisticsEntry value2Attr2BasicStatisticsEntry = valuesInfoAttr2.get("2");
		ArrayList<Double> value2Attr2ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value2Attr2ABasicStatisticsEntryExpected = new BasicStatisticsEntry(value2Attr2ABasicStatisticsEntryExpectedCounts);
		assertEquals(value2Attr2ABasicStatisticsEntryExpected,value2Attr2BasicStatisticsEntry);
		BasicStatisticsEntry value3Attr2BasicStatisticsEntry = valuesInfoAttr2.get("3");
		ArrayList<Double> value3Attr2ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value3Attr2ABasicStatisticsEntryExpected = new BasicStatisticsEntry(value3Attr2ABasicStatisticsEntryExpectedCounts);
		assertEquals(value3Attr2ABasicStatisticsEntryExpected,value3Attr2BasicStatisticsEntry);
		BasicStatisticsEntry value4Attr2BasicStatisticsEntry = valuesInfoAttr2.get("4");
		ArrayList<Double> value4Attr2ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,1.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value4Attr2ABasicStatisticsEntryExpected = new BasicStatisticsEntry(value4Attr2ABasicStatisticsEntryExpectedCounts);
		assertEquals(value4Attr2ABasicStatisticsEntryExpected,value4Attr2BasicStatisticsEntry);
		
		Map<String, BasicStatisticsEntry> valuesInfoAttr3 = valuesInfo.column(attr3Merged);
		assertEquals(4,valuesInfoAttr3.size());
		BasicStatisticsEntry valueTrueAttr3BasicStatisticsEntry = valuesInfoAttr3.get("true");
		ArrayList<Double> valueTrueAttr3ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueTrueAttr3ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueTrueAttr3ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueTrueAttr3ABasicStatisticsEntryExpected,valueTrueAttr3BasicStatisticsEntry);
		BasicStatisticsEntry valueFalseAttr3BasicStatisticsEntry = valuesInfoAttr3.get("false");
		ArrayList<Double> valueFalseAttr3ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueFalseAttr3ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueFalseAttr3ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueFalseAttr3ABasicStatisticsEntryExpected,valueFalseAttr3BasicStatisticsEntry);
		BasicStatisticsEntry value0Attr3BasicStatisticsEntry = valuesInfoAttr3.get("0");
		ArrayList<Double> value0Attr3ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value0Attr3ABasicStatisticsEntryExpected = new BasicStatisticsEntry(value0Attr3ABasicStatisticsEntryExpectedCounts);
		assertEquals(value0Attr3ABasicStatisticsEntryExpected,value0Attr3BasicStatisticsEntry);
		BasicStatisticsEntry value1Attr3BasicStatisticsEntry = valuesInfoAttr3.get("1");
		ArrayList<Double> value1Attr3ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value1Attr3ABasicStatisticsEntryExpected = new BasicStatisticsEntry(value1Attr3ABasicStatisticsEntryExpectedCounts);
		assertEquals(value1Attr3ABasicStatisticsEntryExpected,value1Attr3BasicStatisticsEntry);
		
		Map<String, BasicStatisticsEntry> valuesInfoAttr4 = valuesInfo.column(attr4Merged);
		assertEquals(4,valuesInfoAttr4.size());
		BasicStatisticsEntry valueGammaAttr4BasicStatisticsEntry = valuesInfoAttr4.get("gamma");
		ArrayList<Double> valueGammaAttr4ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueGammaAttr4ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueGammaAttr4ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueGammaAttr4ABasicStatisticsEntryExpected,valueGammaAttr4BasicStatisticsEntry);
		BasicStatisticsEntry valueEpsilonAttr4BasicStatisticsEntry = valuesInfoAttr4.get("epsilon");
		ArrayList<Double> valueEpsilonAttr4ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0);
		BasicStatisticsEntry valueEpsilonAttr4ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueEpsilonAttr4ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueEpsilonAttr4ABasicStatisticsEntryExpected,valueEpsilonAttr4BasicStatisticsEntry);
		BasicStatisticsEntry valueOmicronAttr4BasicStatisticsEntry = valuesInfoAttr4.get("omicron");
		ArrayList<Double> valueOmicronAttr4ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0);
		BasicStatisticsEntry valueOmicronAttr4ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueOmicronAttr4ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueOmicronAttr4ABasicStatisticsEntryExpected,valueOmicronAttr4BasicStatisticsEntry);
		BasicStatisticsEntry valueZetaAttr4BasicStatisticsEntry = valuesInfoAttr4.get("zeta");
		ArrayList<Double> valueZetaAttr4ABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0);
		BasicStatisticsEntry valueZetaAttr4ABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueZetaAttr4ABasicStatisticsEntryExpectedCounts);
		assertEquals(valueZetaAttr4ABasicStatisticsEntryExpected,valueZetaAttr4BasicStatisticsEntry);
		
		//Now, we check entries of other complex types
		
		//Complex type A
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedA = complexTypeInfo.get(complexTypeMergedA);
		assertNotNull(complexTypeStatisticsEntryMergedA);
		assertEquals(16,complexTypeStatisticsEntryMergedA.getInputDocumentsCount());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfoA = complexTypeStatisticsEntryMergedA.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfoA.isEmpty());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfoA = complexTypeStatisticsEntryMergedA.getElementInfo();
		assertTrue(elementInfoA.isEmpty());
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodesA = complexTypeStatisticsEntryMergedA.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodesA.isEmpty());
		
		Map<List<SchemaElement>, Integer> subpatternsInfoA = complexTypeStatisticsEntryMergedA.getSubpatternsInfo();
		assertTrue(subpatternsInfoA.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoA = complexTypeStatisticsEntryMergedA.getValuesInfo();
		assertEquals(6, valuesInfoA.size());
		assertEquals(6, valuesInfoA.rowKeySet().size());
		assertEquals(1, valuesInfoA.columnKeySet().size());
		BasicStatisticsEntry valueCuarentaNodeABasicStatisticsEntry = valuesInfoA.get("cuarenta", elementMergedA);
		ArrayList<Double> valueCuarentaNodeABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueCuarentaNodeABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueCuarentaNodeABasicStatisticsEntryExpectedCounts);
		assertEquals(valueCuarentaNodeABasicStatisticsEntryExpected,valueCuarentaNodeABasicStatisticsEntry);
		BasicStatisticsEntry value50NodeABasicStatisticsEntry = valuesInfoA.get("50", elementMergedA);
		ArrayList<Double> value50NodeABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value50NodeABasicStatisticsEntryExpected = new BasicStatisticsEntry(value50NodeABasicStatisticsEntryExpectedCounts);
		assertEquals(value50NodeABasicStatisticsEntryExpected,value50NodeABasicStatisticsEntry);
		BasicStatisticsEntry value60NodeABasicStatisticsEntry = valuesInfoA.get("60", elementMergedA);
		ArrayList<Double> value60NodeABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value60NodeABasicStatisticsEntryExpected = new BasicStatisticsEntry(value60NodeABasicStatisticsEntryExpectedCounts);
		assertEquals(value60NodeABasicStatisticsEntryExpected,value60NodeABasicStatisticsEntry);
		BasicStatisticsEntry value70NodeABasicStatisticsEntry = valuesInfoA.get("70", elementMergedA);
		ArrayList<Double> value70NodeABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry value70NodeABasicStatisticsEntryExpected = new BasicStatisticsEntry(value70NodeABasicStatisticsEntryExpectedCounts);
		assertEquals(value70NodeABasicStatisticsEntryExpected,value70NodeABasicStatisticsEntry);
		BasicStatisticsEntry valueBuenosDiasNodeABasicStatisticsEntry = valuesInfoA.get("buenos dias", elementMergedA);
		ArrayList<Double> valueBuenosDiasNodeABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0);
		BasicStatisticsEntry valueBuenosDiasNodeABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueBuenosDiasNodeABasicStatisticsEntryExpectedCounts);
		assertEquals(valueBuenosDiasNodeABasicStatisticsEntryExpected,valueBuenosDiasNodeABasicStatisticsEntry);
		BasicStatisticsEntry valueSaludosNodeABasicStatisticsEntry = valuesInfoA.get("saludos", elementMergedA);
		ArrayList<Double> valueSaludosNodeABasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0);
		BasicStatisticsEntry valueSaludosNodeABasicStatisticsEntryExpected = new BasicStatisticsEntry(valueSaludosNodeABasicStatisticsEntryExpectedCounts);
		assertEquals(valueSaludosNodeABasicStatisticsEntryExpected,valueSaludosNodeABasicStatisticsEntry);
		
		//Complex type B
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedB = complexTypeInfo.get(complexTypeMergedB);
		assertNotNull(complexTypeStatisticsEntryMergedB);
		assertEquals(16,complexTypeStatisticsEntryMergedB.getInputDocumentsCount());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfoB = complexTypeStatisticsEntryMergedB.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfoB.isEmpty());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfoB = complexTypeStatisticsEntryMergedB.getElementInfo();
		assertTrue(elementInfoB.isEmpty());
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodesB = complexTypeStatisticsEntryMergedB.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodesB.isEmpty());
		
		Map<List<SchemaElement>, Integer> subpatternsInfoB = complexTypeStatisticsEntryMergedB.getSubpatternsInfo();
		assertTrue(subpatternsInfoB.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoB = complexTypeStatisticsEntryMergedB.getValuesInfo();
		assertEquals(6, valuesInfoB.size());
		assertEquals(6, valuesInfoB.rowKeySet().size());
		assertEquals(1, valuesInfoB.columnKeySet().size());
		BasicStatisticsEntry value40NodeBBasicStatisticsEntry = valuesInfoB.get("40", elementMergedB);
		ArrayList<Double> value40NodeBBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0);
		BasicStatisticsEntry value40NodeBBasicStatisticsEntryExpected = new BasicStatisticsEntry(value40NodeBBasicStatisticsEntryExpectedCounts);
		assertEquals(value40NodeBBasicStatisticsEntryExpected,value40NodeBBasicStatisticsEntry);
		BasicStatisticsEntry value50NodeBBasicStatisticsEntry = valuesInfoB.get("50", elementMergedB);
		ArrayList<Double> value50NodeBBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,1.0,1.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0);
		BasicStatisticsEntry value50NodeBBasicStatisticsEntryExpected = new BasicStatisticsEntry(value50NodeBBasicStatisticsEntryExpectedCounts);
		assertEquals(value50NodeBBasicStatisticsEntryExpected,value50NodeBBasicStatisticsEntry);
		BasicStatisticsEntry value60NodeBBasicStatisticsEntry = valuesInfoB.get("60", elementMergedB);
		ArrayList<Double> value60NodeBBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0);
		BasicStatisticsEntry value60NodeBBasicStatisticsEntryExpected = new BasicStatisticsEntry(value60NodeBBasicStatisticsEntryExpectedCounts);
		assertEquals(value60NodeBBasicStatisticsEntryExpected,value60NodeBBasicStatisticsEntry);
		BasicStatisticsEntry value70NodeBBasicStatisticsEntry = valuesInfoB.get("70", elementMergedB);
		ArrayList<Double> value70NodeBBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0);
		BasicStatisticsEntry value70NodeBBasicStatisticsEntryExpected = new BasicStatisticsEntry(value70NodeBBasicStatisticsEntryExpectedCounts);
		assertEquals(value70NodeBBasicStatisticsEntryExpected,value70NodeBBasicStatisticsEntry);
		BasicStatisticsEntry valueHolaNodeBBasicStatisticsEntry = valuesInfoB.get("hola", elementMergedB);
		ArrayList<Double> valueHolaNodeBBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueHolaNodeBBasicStatisticsEntryExpected = new BasicStatisticsEntry(valueHolaNodeBBasicStatisticsEntryExpectedCounts);
		assertEquals(valueHolaNodeBBasicStatisticsEntryExpected,valueHolaNodeBBasicStatisticsEntry);
		BasicStatisticsEntry valueSaludosNodeBBasicStatisticsEntry = valuesInfoB.get("saludos", elementMergedB);
		ArrayList<Double> valueSaludosNodeBBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry valueSaludosNodeBBasicStatisticsEntryExpected = new BasicStatisticsEntry(valueSaludosNodeBBasicStatisticsEntryExpectedCounts);
		assertEquals(valueSaludosNodeBBasicStatisticsEntryExpected,valueSaludosNodeBBasicStatisticsEntry);
		
		//Complex type C
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedC = complexTypeInfo.get(complexTypeMergedC);
		assertNotNull(complexTypeStatisticsEntryMergedC);
		assertEquals(16,complexTypeStatisticsEntryMergedC.getInputDocumentsCount());

		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfoC = complexTypeStatisticsEntryMergedC.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfoC.isEmpty());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfoC = complexTypeStatisticsEntryMergedC.getElementInfo();
		assertEquals(2, elementInfoC.size());
		BasicStatisticsEntry elementMergedCEBasicStatisticsEntry = elementInfoC.get(elementMergedCE);
		ArrayList<Double> elementMergedCEBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		BasicStatisticsEntry elementMergedCEBasicStatisticsEntryExpected = new BasicStatisticsEntry(elementMergedCEBasicStatisticsEntryExpectedCounts);
		assertEquals(elementMergedCEBasicStatisticsEntryExpected,elementMergedCEBasicStatisticsEntry);
		BasicStatisticsEntry elementMergedCFBasicStatisticsEntry = elementInfoC.get(elementMergedCF);
		ArrayList<Double> elementMergedCFBasicStatisticsEntryExpectedCounts = Lists.<Double>newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,2.0,2.0,2.0,2.0,2.0,1.0,1.0,1.0);
		BasicStatisticsEntry elementMergedCFBasicStatisticsEntryExpected = new BasicStatisticsEntry(elementMergedCFBasicStatisticsEntryExpectedCounts);
		assertEquals(elementMergedCFBasicStatisticsEntryExpected,elementMergedCFBasicStatisticsEntry);
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodesC = complexTypeStatisticsEntryMergedC.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodesC.isEmpty());
		
		Map<List<SchemaElement>, Integer> subpatternsInfoC = complexTypeStatisticsEntryMergedC.getSubpatternsInfo();
		List<SchemaElement> subpatternE = ImmutableList.of(elementMergedCE);
		assertEquals(8, subpatternsInfoC.get(subpatternE).intValue());
		List<SchemaElement> subpatternF = ImmutableList.of(elementMergedCF);
		assertEquals(13, subpatternsInfoC.get(subpatternF).intValue());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoC = complexTypeStatisticsEntryMergedC.getValuesInfo();
		assertTrue(valuesInfoC.isEmpty());
		
		//Complex type D
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedD = complexTypeInfo.get(complexTypeMergedD);
		assertNotNull(complexTypeStatisticsEntryMergedD);
		assertEquals(16,complexTypeStatisticsEntryMergedD.getInputDocumentsCount());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfoD = complexTypeStatisticsEntryMergedD.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfoD.isEmpty());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfoD = complexTypeStatisticsEntryMergedD.getElementInfo();
		assertTrue(elementInfoD.isEmpty());
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodesD = complexTypeStatisticsEntryMergedD.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodesD.isEmpty());
		
		Map<List<SchemaElement>, Integer> subpatternsInfoD = complexTypeStatisticsEntryMergedD.getSubpatternsInfo();
		assertTrue(subpatternsInfoD.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoD = complexTypeStatisticsEntryMergedD.getValuesInfo();
		assertTrue(valuesInfoD.isEmpty());
		
		//Complex type E
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedE = complexTypeInfo.get(complexTypeMergedE);
		assertNotNull(complexTypeStatisticsEntryMergedE);
		assertEquals(16,complexTypeStatisticsEntryMergedE.getInputDocumentsCount());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfoE = complexTypeStatisticsEntryMergedE.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfoE.isEmpty());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfoE = complexTypeStatisticsEntryMergedE.getElementInfo();
		assertTrue(elementInfoE.isEmpty());
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodesE = complexTypeStatisticsEntryMergedE.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodesE.isEmpty());
		
		Map<List<SchemaElement>, Integer> subpatternsInfoE = complexTypeStatisticsEntryMergedE.getSubpatternsInfo();
		assertTrue(subpatternsInfoE.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoE = complexTypeStatisticsEntryMergedE.getValuesInfo();
		assertTrue(valuesInfoE.isEmpty());
		
		//Complex type D
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedF = complexTypeInfo.get(complexTypeMergedF);
		assertNotNull(complexTypeStatisticsEntryMergedF);
		assertEquals(16,complexTypeStatisticsEntryMergedF.getInputDocumentsCount());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfoF = complexTypeStatisticsEntryMergedF.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfoF.isEmpty());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfoF = complexTypeStatisticsEntryMergedF.getElementInfo();
		assertTrue(elementInfoF.isEmpty());
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodesF = complexTypeStatisticsEntryMergedF.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodesF.isEmpty());
		
		Map<List<SchemaElement>, Integer> subpatternsInfoF = complexTypeStatisticsEntryMergedF.getSubpatternsInfo();
		assertTrue(subpatternsInfoF.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoF = complexTypeStatisticsEntryMergedF.getValuesInfo();
		assertTrue(valuesInfoF.isEmpty());	}
	
	/**
	 * Test method that checks what happens on the second scenario when two similar types are forced to be 
	 * merged (before they are merged directly via comparators)
	 */
	@Test
	public void test2ForcedMergeWhenTypesAreSimilar(){
		ChildrenPatternComparator onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator = mock(ChildrenPatternComparator.class);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(any(ExtendedAutomaton.class), any(ExtendedAutomaton.class))).thenReturn(false);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2AD.getAutomaton(), complexTypeScenario2BD.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2AD.getAutomaton(), complexTypeScenario2BE.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2BD.getAutomaton(), complexTypeScenario2AD.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2BD.getAutomaton(), complexTypeScenario2BE.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2BE.getAutomaton(), complexTypeScenario2AD.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2BE.getAutomaton(), complexTypeScenario2BD.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2AD.getAutomaton(), complexTypeScenario2AD.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2BD.getAutomaton(), complexTypeScenario2BD.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2BE.getAutomaton(), complexTypeScenario2BE.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2RootA.getAutomaton(), complexTypeScenario2RootA.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2RootA.getAutomaton(), complexTypeScenario2RootB.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2RootB.getAutomaton(), complexTypeScenario2RootA.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2RootB.getAutomaton(), complexTypeScenario2RootB.getAutomaton())).thenReturn(true);
		
		
		AttributeListComparator onlyDElementsAreSimilarToDAndEmptyToEmpty = mock(AttributeListComparator.class);
		when(onlyDElementsAreSimilarToDAndEmptyToEmpty.compare(anyListOf(SchemaAttribute.class),anyListOf(SchemaAttribute.class))).thenReturn(false);
		when(onlyDElementsAreSimilarToDAndEmptyToEmpty.compare(Collections.<SchemaAttribute>emptyList(),Collections.<SchemaAttribute>emptyList())).thenReturn(true);
		when(onlyDElementsAreSimilarToDAndEmptyToEmpty.compare(complexTypeScenario2AD.getAttributeList(),complexTypeScenario2AD.getAttributeList())).thenReturn(true);
		when(onlyDElementsAreSimilarToDAndEmptyToEmpty.compare(complexTypeScenario2AD.getAttributeList(),complexTypeScenario2BD.getAttributeList())).thenReturn(true);
		when(onlyDElementsAreSimilarToDAndEmptyToEmpty.compare(complexTypeScenario2BD.getAttributeList(),complexTypeScenario2AD.getAttributeList())).thenReturn(true);
		when(onlyDElementsAreSimilarToDAndEmptyToEmpty.compare(complexTypeScenario2BD.getAttributeList(),complexTypeScenario2BD.getAttributeList())).thenReturn(true);
		
		
		when(configuration2.getAttributeListComparator()).thenReturn(onlyDElementsAreSimilarToDAndEmptyToEmpty);
		when(configuration2.getChildrenPatternComparator()).thenReturn(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator);
		when(configuration2.getEnumsComparator()).thenReturn(alwaysFalseEnumComparator);
		
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario2CBCopy = new ComplexTypeStatisticsEntry(complexTypeStatisticsEntryScenario2CB);
		
		TypeMerger typeMerger = new TypeMergerImpl();
		typeMerger.mergeTypes(schema2, configuration2);
		
		//First, we check that the _root-a and _root-b elements point to the merged complex type and _c-b does not
		Map<String, ComplexType> schemaComplexTypes = schema2.getComplexTypes();
		assertEquals(6,schemaComplexTypes.size());
		
		//Now, we check complex type of the root
		assertTrue(complexTypeScenario2Root.getTextSimpleType().consistOnlyOfWhitespaceCharacters());
		assertTrue(complexTypeScenario2Root.getAttributeList().isEmpty());
		SchemaElement elementAMerged = schema2.getElements().get("", complexTypeScenario2Root.getName()+"-a");
		SchemaElement elementBMerged = schema2.getElements().get("", complexTypeScenario2Root.getName()+"-b");
		ExtendedAutomaton complexTypeScenario2RootAutomaton = complexTypeScenario2Root.getAutomaton();
		assertTrue(complexTypeScenario2RootAutomaton.containsAllNodes(ImmutableSet.of(elementAMerged,elementBMerged,elementScenario2RootC)));
		assertEquals(1L,complexTypeScenario2RootAutomaton.getEdgeWeight(initialState, elementAMerged));
		assertEquals(1L,complexTypeScenario2RootAutomaton.getEdgeWeight(elementAMerged, elementBMerged));
		assertEquals(1L,complexTypeScenario2RootAutomaton.getEdgeWeight(elementBMerged, elementScenario2RootC));
		assertEquals(1L,complexTypeScenario2RootAutomaton.getEdgeWeight(elementScenario2RootC, finalState));
		
		//Now we check the newly created complex type for A and B elements
		String complexTypeMergedRootABExpectedName = complexTypeScenario2RootA.getName() + configuration2.getMergedTypesSeparator() + complexTypeScenario2RootB.getName();
		ComplexType complexTypeMergedRootAB = schemaComplexTypes.get(complexTypeMergedRootABExpectedName);
		assertFalse(schemaComplexTypes.containsKey(complexTypeScenario2RootA.getName()));
		assertFalse(schemaComplexTypes.containsKey(complexTypeScenario2RootB.getName()));
		assertTrue(schemaComplexTypes.containsKey(complexTypeMergedRootABExpectedName));
		assertEquals(complexTypeMergedRootABExpectedName, complexTypeMergedRootAB.getName());
		ExtendedAutomaton complexTypeMergedRootABAutomaton = complexTypeMergedRootAB.getAutomaton();
		assertEquals(3,complexTypeMergedRootABAutomaton.nodeCount());
		SchemaElement elementDMerged = schema2.getElements().get("", complexTypeMergedRootAB.getName()+"-d");
		assertTrue(complexTypeMergedRootABAutomaton.containsNode(elementDMerged));
		assertEquals(2L,complexTypeMergedRootABAutomaton.getEdgeWeight(initialState, elementDMerged));
		assertEquals(2L,complexTypeMergedRootABAutomaton.getEdgeWeight(elementDMerged, finalState));
		assertEquals(2,complexTypeMergedRootABAutomaton.getEdgeCellSet().size());
		assertTrue(complexTypeMergedRootAB.getAttributeList().isEmpty());
		assertTrue(complexTypeMergedRootAB.getTextSimpleType().isEmpty());
						
		String complexTypeMergedDExpectedName = complexTypeScenario2AD.getName() + configuration2.getMergedTypesSeparator() + complexTypeScenario2BD.getName();
		ComplexType complexTypeMergedD = schemaComplexTypes.get(complexTypeMergedDExpectedName);
		assertFalse(schemaComplexTypes.containsKey(complexTypeScenario2BD.getName()));
		assertFalse(schemaComplexTypes.containsKey(complexTypeScenario2AD.getName()));
		assertTrue(schemaComplexTypes.containsKey(complexTypeMergedDExpectedName));
		assertEquals(complexTypeMergedDExpectedName, complexTypeMergedD.getName());
		ExtendedAutomaton complexTypeMergedDAutomaton = complexTypeMergedD.getAutomaton();
		assertEquals(2,complexTypeMergedDAutomaton.nodeCount());
		assertTrue(complexTypeMergedDAutomaton.containsAllNodes(ImmutableSet.of(initialState,finalState)));
		List<SchemaAttribute> complexTypeMergedDAttributeList = complexTypeMergedD.getAttributeList();
		SchemaAttribute attributeMergedABDAttr = schema2.getAttributes().get("", complexTypeMergedD.getName()+"-attr");
		assertTrue(attributeMergedABDAttr.isOptional());
		SchemaAttribute attributeMergedABDAttr2 = schema2.getAttributes().get("", complexTypeMergedD.getName()+"-attr2");
		assertFalse(attributeMergedABDAttr2.isOptional());
		assertEquals(ImmutableList.of(attributeMergedABDAttr, attributeMergedABDAttr2),complexTypeMergedDAttributeList);
		
		//Now we check the the complex type of B child of C remains the same one except for the complex type of its node d, which should be the mixed one
		assertEquals(complexTypeScenario2CB,schemaComplexTypes.get(complexTypeScenario2CB.getName())); //It is really the same 
		assertEquals(complexTypeMergedD,complexTypeScenario2CB.getAutomaton().getTopologicallySortedNodeList().get(1).getType());
		//Now we check the the complex type statistics of _c-_b remains the same, as the type of _c-b has not changed
		assertEquals(complexTypeStatisticsEntryScenario2CBCopy,complexTypeStatisticsEntryScenario2CB);
		
		//Now we check the statistics of merged complex types
		Statistics statistics = schema2.getStatistics();
		Map<ComplexType, ComplexTypeStatisticsEntry> complexTypeInfo = statistics.getComplexTypeInfo();
		assertEquals(6,complexTypeInfo.size());
		
		//Statistics of complexTypeMergedRootAB
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedRootAB = complexTypeInfo.get(complexTypeMergedRootAB);
		assertEquals(1,complexTypeStatisticsEntryMergedRootAB.getInputDocumentsCount());
		Map<SchemaAttribute, BasicStatisticsEntry> complexTypeStatisticsEntryMergedRootABAttributeOccurrencesInfo = complexTypeStatisticsEntryMergedRootAB.getAttributeOccurrencesInfo();
		assertTrue(complexTypeStatisticsEntryMergedRootABAttributeOccurrencesInfo.isEmpty());
		Map<SchemaElement, BasicStatisticsEntry> complexTypeStatisticsEntryMergedRootABElementInfo = complexTypeStatisticsEntryMergedRootAB.getElementInfo();
		assertEquals(1,complexTypeStatisticsEntryMergedRootABElementInfo.size());
		BasicStatisticsEntry complexTypeStatisticsEntryMergedRootABElementDEntry = complexTypeStatisticsEntryMergedRootABElementInfo.get(elementDMerged);
		BasicStatisticsEntry complexTypeStatisticsEntryMergedRootABElementDExpectedEntry = new BasicStatisticsEntry(ImmutableList.of(2.0));
		assertEquals(complexTypeStatisticsEntryMergedRootABElementDExpectedEntry, complexTypeStatisticsEntryMergedRootABElementDEntry);
		assertTrue(complexTypeStatisticsEntryMergedRootAB.getStatisticsOfNumericValuesOfNodes().isEmpty());
		Map<List<SchemaElement>, Integer> complexTypeStatisticsEntryMergedRootABSubpatternsInfo = complexTypeStatisticsEntryMergedRootAB.getSubpatternsInfo();
		assertEquals(1,complexTypeStatisticsEntryMergedRootABSubpatternsInfo.size());
		assertEquals(2,complexTypeStatisticsEntryMergedRootABSubpatternsInfo.get(ImmutableList.of(elementDMerged)).intValue());
		Table<String, SchemaNode, BasicStatisticsEntry> complexTypeStatisticsEntryMergedRootABValuesInfo = complexTypeStatisticsEntryMergedRootAB.getValuesInfo();
		assertTrue(complexTypeStatisticsEntryMergedRootABValuesInfo.isEmpty());
		
		//Statistics of complexTypeMergedD
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedD = complexTypeInfo.get(complexTypeMergedD);
		assertEquals(1,complexTypeStatisticsEntryMergedD.getInputDocumentsCount());
		Map<SchemaAttribute, BasicStatisticsEntry> complexTypeStatisticsEntryMergedDAttributeOccurrencesInfo = complexTypeStatisticsEntryMergedD.getAttributeOccurrencesInfo();
		assertEquals(2,complexTypeStatisticsEntryMergedDAttributeOccurrencesInfo.size());
		BasicStatisticsEntry complexTypeStatisticsEntryMergedDAttr1ExpectedEntry = new BasicStatisticsEntry(ImmutableList.of(2.0));
		assertEquals(complexTypeStatisticsEntryMergedDAttr1ExpectedEntry, complexTypeStatisticsEntryMergedDAttributeOccurrencesInfo.get(attributeMergedABDAttr));
		BasicStatisticsEntry complexTypeStatisticsEntryMergedDAttr2ExpectedEntry = new BasicStatisticsEntry(ImmutableList.of(2.0));
		assertEquals(complexTypeStatisticsEntryMergedDAttr2ExpectedEntry, complexTypeStatisticsEntryMergedDAttributeOccurrencesInfo.get(attributeMergedABDAttr2));
		Map<SchemaElement, BasicStatisticsEntry> complexTypeStatisticsEntryMergedDElementInfo = complexTypeStatisticsEntryMergedD.getElementInfo();
		assertTrue(complexTypeStatisticsEntryMergedDElementInfo.isEmpty());
		Map<List<SchemaElement>, Integer> complexTypeStatisticsEntryMergedDSubpatternsInfo = complexTypeStatisticsEntryMergedD.getSubpatternsInfo();
		assertTrue(complexTypeStatisticsEntryMergedDSubpatternsInfo.isEmpty());
		Table<String, SchemaNode, BasicStatisticsEntry> complexTypeStatisticsEntryMergedDValuesInfo = complexTypeStatisticsEntryMergedD.getValuesInfo();
		assertEquals(2,complexTypeStatisticsEntryMergedDValuesInfo.size());
		BasicStatisticsEntry complexTypeStatisticsEntryMergedDValue1AttrEntry = complexTypeStatisticsEntryMergedDValuesInfo.get("value1", attributeMergedABDAttr);
		BasicStatisticsEntry complexTypeStatisticsEntryMergedDValue1AttrExpectedEntry = new BasicStatisticsEntry(ImmutableList.of(2.0));
		assertEquals(complexTypeStatisticsEntryMergedDValue1AttrExpectedEntry,complexTypeStatisticsEntryMergedDValue1AttrEntry);
		BasicStatisticsEntry complexTypeStatisticsEntryMergedDValue2Attr2Entry = complexTypeStatisticsEntryMergedDValuesInfo.get("value2", attributeMergedABDAttr2);
		BasicStatisticsEntry complexTypeStatisticsEntryMergedDValue2Attr2ExpectedEntry = new BasicStatisticsEntry(ImmutableList.of(3.0));
		assertEquals(complexTypeStatisticsEntryMergedDValue2Attr2ExpectedEntry,complexTypeStatisticsEntryMergedDValue2Attr2Entry);
		
		//Now, we check that the other complex type entries of the statistics remain unchanged
		
		assertSame(complexTypeStatisticsEntryScenario2Root,complexTypeInfo.get(complexTypeScenario2Root));
		assertSame(complexTypeStatisticsEntryScenario2RootC,complexTypeInfo.get(complexTypeScenario2RootC));
		assertSame(complexTypeStatisticsEntryScenario2CB,complexTypeInfo.get(complexTypeScenario2CB));
		assertSame(complexTypeStatisticsEntryScenario2BE,complexTypeInfo.get(complexTypeScenario2BE));
	}
	
	/**
	 * Test method that checks what happens on the second scenario when two non similar types are forced to be 
	 * merged (it means, they would have not been merged via comparators).
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void test2ForcedMergeWhenSomeTypesAreNotSimilar(){
		ChildrenPatternComparator onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator = mock(ChildrenPatternComparator.class);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(any(ExtendedAutomaton.class), any(ExtendedAutomaton.class))).thenReturn(false);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2AD.getAutomaton(), complexTypeScenario2AD.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2AD.getAutomaton(), complexTypeScenario2BD.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2AD.getAutomaton(), complexTypeScenario2BE.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2BD.getAutomaton(), complexTypeScenario2AD.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2BD.getAutomaton(), complexTypeScenario2BD.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2BD.getAutomaton(), complexTypeScenario2BE.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2BE.getAutomaton(), complexTypeScenario2AD.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2BE.getAutomaton(), complexTypeScenario2BD.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2BE.getAutomaton(), complexTypeScenario2BE.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2RootA.getAutomaton(), complexTypeScenario2RootA.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2RootA.getAutomaton(), complexTypeScenario2RootB.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2RootB.getAutomaton(), complexTypeScenario2RootA.getAutomaton())).thenReturn(true);
		when(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator.compare(complexTypeScenario2RootB.getAutomaton(), complexTypeScenario2RootB.getAutomaton())).thenReturn(true);
		
		
		AttributeListComparator onlyEmptyListsAreSimilarToEmptyLists = mock(AttributeListComparator.class);
		when(onlyEmptyListsAreSimilarToEmptyLists.compare(anyListOf(SchemaAttribute.class),anyListOf(SchemaAttribute.class))).thenReturn(false);
		when(onlyEmptyListsAreSimilarToEmptyLists.compare(Collections.<SchemaAttribute>emptyList(),Collections.<SchemaAttribute>emptyList())).thenReturn(true);
		
		when(configuration2.getAttributeListComparator()).thenReturn(onlyEmptyListsAreSimilarToEmptyLists);
		when(configuration2.getChildrenPatternComparator()).thenReturn(onlyRootAAndRootBAndDescendantsAreSimilarChildrenComparator);
		when(configuration2.getEnumsComparator()).thenReturn(alwaysFalseEnumComparator);
		
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario2CBCopy = new ComplexTypeStatisticsEntry(complexTypeStatisticsEntryScenario2CB);
		
		TypeMerger typeMerger = new TypeMergerImpl();
		typeMerger.mergeTypes(schema2, configuration2);
		
		//First, we check that the _root-a and _root-b elements point to the merged complex type and _c-b does not
		Map<String, ComplexType> schemaComplexTypes = schema2.getComplexTypes();
		assertEquals(7,schemaComplexTypes.size());
		
		//Now, we check complex type of the root
		assertTrue(complexTypeScenario2Root.getTextSimpleType().consistOnlyOfWhitespaceCharacters());
		assertTrue(complexTypeScenario2Root.getAttributeList().isEmpty());
		SchemaElement elementAMerged = schema2.getElements().get("", complexTypeScenario2Root.getName()+"-a");
		SchemaElement elementBMerged = schema2.getElements().get("", complexTypeScenario2Root.getName()+"-b");
		ExtendedAutomaton complexTypeScenario2RootAutomaton = complexTypeScenario2Root.getAutomaton();
		assertTrue(complexTypeScenario2RootAutomaton.containsAllNodes(ImmutableSet.of(elementAMerged,elementBMerged,elementScenario2RootC)));
		assertEquals(1L,complexTypeScenario2RootAutomaton.getEdgeWeight(initialState, elementAMerged));
		assertEquals(1L,complexTypeScenario2RootAutomaton.getEdgeWeight(elementAMerged, elementBMerged));
		assertEquals(1L,complexTypeScenario2RootAutomaton.getEdgeWeight(elementBMerged, elementScenario2RootC));
		assertEquals(1L,complexTypeScenario2RootAutomaton.getEdgeWeight(elementScenario2RootC, finalState));
		
		//Now we check the newly created complex type for _root-a and _root-b elements
		String complexTypeMergedRootABExpectedName = complexTypeScenario2RootA.getName() + configuration2.getMergedTypesSeparator() + complexTypeScenario2RootB.getName();
		ComplexType complexTypeMergedRootAB = schemaComplexTypes.get(complexTypeMergedRootABExpectedName);
		assertFalse(schemaComplexTypes.containsKey(complexTypeScenario2RootA.getName()));
		assertFalse(schemaComplexTypes.containsKey(complexTypeScenario2RootB.getName()));
		assertTrue(schemaComplexTypes.containsKey(complexTypeMergedRootABExpectedName));
		assertEquals(complexTypeMergedRootABExpectedName, complexTypeMergedRootAB.getName());
		ExtendedAutomaton complexTypeMergedRootABAutomaton = complexTypeMergedRootAB.getAutomaton();
		assertEquals(3,complexTypeMergedRootABAutomaton.nodeCount());
		SchemaElement elementDMerged = schema2.getElements().get("", complexTypeMergedRootAB.getName()+"-d");
		assertTrue(complexTypeMergedRootABAutomaton.containsNode(elementDMerged));
		assertEquals(2L,complexTypeMergedRootABAutomaton.getEdgeWeight(initialState, elementDMerged));
		assertEquals(2L,complexTypeMergedRootABAutomaton.getEdgeWeight(elementDMerged, finalState));
		assertEquals(2,complexTypeMergedRootABAutomaton.getEdgeCellSet().size());
		assertTrue(complexTypeMergedRootAB.getAttributeList().isEmpty());
		assertTrue(complexTypeMergedRootAB.getTextSimpleType().isEmpty());
						
		String complexTypeMergedDExpectedName = TypeMergerImpl.getMergedTypeName(configuration2, complexTypeScenario2AD.getName(), complexTypeScenario2BD.getName());
		ComplexType complexTypeMergedD = schemaComplexTypes.get(complexTypeMergedDExpectedName);
		assertFalse(schemaComplexTypes.containsKey(complexTypeScenario2AD.getName()));
		assertTrue(schemaComplexTypes.containsKey(complexTypeMergedDExpectedName));
		assertEquals(complexTypeMergedDExpectedName, complexTypeMergedD.getName());
		ExtendedAutomaton complexTypeMergedDAutomaton = complexTypeMergedD.getAutomaton();
		assertEquals(2,complexTypeMergedDAutomaton.nodeCount());
		assertTrue(complexTypeMergedDAutomaton.containsAllNodes(ImmutableSet.of(initialState,finalState)));
		List<SchemaAttribute> complexTypeMergedDAttributeList = complexTypeMergedD.getAttributeList();
		SchemaAttribute attributeMergedABDAttr = schema2.getAttributes().get("", complexTypeMergedD.getName()+"-attr");
		assertTrue(attributeMergedABDAttr.isOptional()); //Merges are done with all the info of the original complex types, not only the info regarding the elements which forced the merge
		SchemaAttribute attributeMergedABDAttr2 = schema2.getAttributes().get("", complexTypeMergedD.getName()+"-attr2");
		assertFalse(attributeMergedABDAttr2.isOptional());
		assertEquals(ImmutableList.of(attributeMergedABDAttr, attributeMergedABDAttr2),complexTypeMergedDAttributeList);
		
		//Now, we check that the complex type _b-_d still exists, as it should be the complex type of the element _c-_b-d
		assertTrue(schemaComplexTypes.containsKey(complexTypeScenario2BD.getName())); 
		assertEquals(complexTypeScenario2BD,schemaComplexTypes.get(complexTypeScenario2BD.getName()));
		//Now we check the the complex type of B child of C has not changed at all
		assertEquals(complexTypeScenario2CB,schemaComplexTypes.get(complexTypeScenario2CB.getName())); //It is really the same 
		assertEquals(complexTypeScenario2BD,complexTypeScenario2CB.getAutomaton().getTopologicallySortedNodeList().get(1).getType());
		//Now we check the the complex type statistics of _c-_b remains the same, as the type of _c-b has not changed
		assertEquals(complexTypeStatisticsEntryScenario2CBCopy,complexTypeStatisticsEntryScenario2CB);
		
		//Now we check the statistics of merged complex types
		Statistics statistics = schema2.getStatistics();
		Map<ComplexType, ComplexTypeStatisticsEntry> complexTypeInfo = statistics.getComplexTypeInfo();
		assertEquals(7,complexTypeInfo.size());
		
		//Statistics of complexTypeMergedRootAB
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedRootAB = complexTypeInfo.get(complexTypeMergedRootAB);
		assertEquals(1,complexTypeStatisticsEntryMergedRootAB.getInputDocumentsCount());
		Map<SchemaAttribute, BasicStatisticsEntry> complexTypeStatisticsEntryMergedRootABAttributeOccurrencesInfo = complexTypeStatisticsEntryMergedRootAB.getAttributeOccurrencesInfo();
		assertTrue(complexTypeStatisticsEntryMergedRootABAttributeOccurrencesInfo.isEmpty());
		Map<SchemaElement, BasicStatisticsEntry> complexTypeStatisticsEntryMergedRootABElementInfo = complexTypeStatisticsEntryMergedRootAB.getElementInfo();
		assertEquals(1,complexTypeStatisticsEntryMergedRootABElementInfo.size());
		BasicStatisticsEntry complexTypeStatisticsEntryMergedRootABElementDEntry = complexTypeStatisticsEntryMergedRootABElementInfo.get(elementDMerged);
		BasicStatisticsEntry complexTypeStatisticsEntryMergedRootABElementDExpectedEntry = new BasicStatisticsEntry(ImmutableList.of(2.0));
		assertEquals(complexTypeStatisticsEntryMergedRootABElementDExpectedEntry, complexTypeStatisticsEntryMergedRootABElementDEntry);
		assertTrue(complexTypeStatisticsEntryMergedRootAB.getStatisticsOfNumericValuesOfNodes().isEmpty());
		Map<List<SchemaElement>, Integer> complexTypeStatisticsEntryMergedRootABSubpatternsInfo = complexTypeStatisticsEntryMergedRootAB.getSubpatternsInfo();
		assertEquals(1,complexTypeStatisticsEntryMergedRootABSubpatternsInfo.size());
		assertEquals(2,complexTypeStatisticsEntryMergedRootABSubpatternsInfo.get(ImmutableList.of(elementDMerged)).intValue());
		Table<String, SchemaNode, BasicStatisticsEntry> complexTypeStatisticsEntryMergedRootABValuesInfo = complexTypeStatisticsEntryMergedRootAB.getValuesInfo();
		assertTrue(complexTypeStatisticsEntryMergedRootABValuesInfo.isEmpty());
		
		//Statistics of complexTypeMergedD
		ComplexTypeStatisticsEntry complexTypeStatisticsEntryMergedD = complexTypeInfo.get(complexTypeMergedD);
		assertEquals(1,complexTypeStatisticsEntryMergedD.getInputDocumentsCount());
		Map<SchemaAttribute, BasicStatisticsEntry> complexTypeStatisticsEntryMergedDAttributeOccurrencesInfo = complexTypeStatisticsEntryMergedD.getAttributeOccurrencesInfo();
		assertEquals(2,complexTypeStatisticsEntryMergedDAttributeOccurrencesInfo.size());
		BasicStatisticsEntry complexTypeStatisticsEntryMergedDAttr1ExpectedEntry = new BasicStatisticsEntry(ImmutableList.of(2.0));
		assertEquals(complexTypeStatisticsEntryMergedDAttr1ExpectedEntry, complexTypeStatisticsEntryMergedDAttributeOccurrencesInfo.get(attributeMergedABDAttr));
		BasicStatisticsEntry complexTypeStatisticsEntryMergedDAttr2ExpectedEntry = new BasicStatisticsEntry(ImmutableList.of(2.0));
		assertEquals(complexTypeStatisticsEntryMergedDAttr2ExpectedEntry, complexTypeStatisticsEntryMergedDAttributeOccurrencesInfo.get(attributeMergedABDAttr2));
		Map<SchemaElement, BasicStatisticsEntry> complexTypeStatisticsEntryMergedDElementInfo = complexTypeStatisticsEntryMergedD.getElementInfo();
		assertTrue(complexTypeStatisticsEntryMergedDElementInfo.isEmpty());
		Map<List<SchemaElement>, Integer> complexTypeStatisticsEntryMergedDSubpatternsInfo = complexTypeStatisticsEntryMergedD.getSubpatternsInfo();
		assertTrue(complexTypeStatisticsEntryMergedDSubpatternsInfo.isEmpty());
		Table<String, SchemaNode, BasicStatisticsEntry> complexTypeStatisticsEntryMergedDValuesInfo = complexTypeStatisticsEntryMergedD.getValuesInfo();
		assertEquals(2,complexTypeStatisticsEntryMergedDValuesInfo.size());
		BasicStatisticsEntry complexTypeStatisticsEntryMergedDValue1AttrEntry = complexTypeStatisticsEntryMergedDValuesInfo.get("value1", attributeMergedABDAttr);
		BasicStatisticsEntry complexTypeStatisticsEntryMergedDValue1AttrExpectedEntry = new BasicStatisticsEntry(ImmutableList.of(2.0));
		assertEquals(complexTypeStatisticsEntryMergedDValue1AttrExpectedEntry,complexTypeStatisticsEntryMergedDValue1AttrEntry);
		BasicStatisticsEntry complexTypeStatisticsEntryMergedDValue2Attr2Entry = complexTypeStatisticsEntryMergedDValuesInfo.get("value2", attributeMergedABDAttr2);
		BasicStatisticsEntry complexTypeStatisticsEntryMergedDValue2Attr2ExpectedEntry = new BasicStatisticsEntry(ImmutableList.of(3.0));
		assertEquals(complexTypeStatisticsEntryMergedDValue2Attr2ExpectedEntry,complexTypeStatisticsEntryMergedDValue2Attr2Entry);
		
		//Now, we check that the other complex type entries of the statistics remain unchanged
		
		assertSame(complexTypeStatisticsEntryScenario2Root,complexTypeInfo.get(complexTypeScenario2Root));
		assertSame(complexTypeStatisticsEntryScenario2RootC,complexTypeInfo.get(complexTypeScenario2RootC));
		assertSame(complexTypeStatisticsEntryScenario2CB,complexTypeInfo.get(complexTypeScenario2CB));
		assertSame(complexTypeStatisticsEntryScenario2BD,complexTypeInfo.get(complexTypeScenario2BD));
		assertSame(complexTypeStatisticsEntryScenario2BE,complexTypeInfo.get(complexTypeScenario2BE));
	}
	
}
