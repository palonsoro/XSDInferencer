package es.upm.dit.xsdinferencer.tests.generation.generatorimpl.statisticsgeneration;

import static es.upm.dit.xsdinferencer.datastructures.Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.Choice;
import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.EmptyRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.RepeatedAtLeastOnce;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.SchemaNode;
import es.upm.dit.xsdinferencer.datastructures.Sequence;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.generation.StatisticResultsDocGenerator;
import es.upm.dit.xsdinferencer.generation.generatorimpl.statisticsgeneration.StatisticResultsDocGeneratorFactory;
import es.upm.dit.xsdinferencer.statistics.BasicStatisticsEntry;
import es.upm.dit.xsdinferencer.statistics.ComplexTypeStatisticsEntry;
import es.upm.dit.xsdinferencer.statistics.Statistics;

/**
 * Test class for {@link StatisticResultsDocGeneratorImpl}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class StatisticResultsDocGeneratorImplTest {
	
	//Fields for testing

	/**
	 * Namespace URI for the namespace 'testing'
	 */
	private static final String NAMESPACE_URI_TESTING = "http://probando.net";
	
	/**
	 * Namespace URI for the namespace 'test'
	 */
	private static final String NAMESPACE_URI_TEST = "http://prueba.net";
	
	//Pseudoelements
	
	/**
	 * Initial state of automatons
	 */
	private SchemaElement initialState;
	/**
	 * Final state of automatons
	 */
	private SchemaElement finalState;

	//Scenario 1
	
	/**
	 * Complex type of the root of the documents on scenario 1. 
	 * It only allows either a child called element1 whose type is complexTypeElement1 
	 * or a child called element2 whose type is complexTypeElement2. 
	 * 
	 */
	private ComplexType complexTypeScenario1Root;
	/**
	 * Complex type of element1 on scenario 1
	 */
	private ComplexType complexTypeScenario1Element1;

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
	 * Complex type of node A in the automaton of complexTypeScenario1Element1
	 */
	private ComplexType complexTypeScenario1Element1NodeA;

	/**
	 * Simple type of complexTypeScenario1Element1NodeA
	 */
	private SimpleType simpleTypeScenario1Element1NodeA;

	/**
	 * Complex type of node B in the automaton of complexTypeScenario1Element1
	 */
	private ComplexType complexTypeScenario1Element1NodeB;

	/**
	 * Simple type of complexTypeElement1NodeB on scenario 1
	 */
	private SimpleType simpleTypeScenario1Element1NodeB;

	/**
	 * Complex type of node C in the automaton of complexTypeScenario1Element1 on scenario 1
	 */
	private ComplexType complexTypeScenario1Element1NodeC;

	/**
	 * Complex type of node E in the automaton of complexTypeScenario1Element1NodeC
	 */
	private ComplexType complexTypeScenario1Element1NodeCNodeE;

	/**
	 * Complex type of element1 on scenario 1
	 */
	private ComplexType complexTypeScenario1Element2;

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
	 * Complex type of node A in the automaton of complexTypeScenario1Element2
	 */
	private ComplexType complexTypeScenario1Element2NodeA;

	/**
	 * Simple type of complexTypeScenario1Element2NodeA
	 */
	private SimpleType simpleTypeScenario1Element2NodeA;

	/**
	 * Complex type of node B in the automaton of complexScenario1TypeElement2
	 */
	private ComplexType complexTypeScenario1Element2NodeB;

	/**
	 * Simple type of complexTypeScenario1Element2NodeB
	 */
	private SimpleType simpleTypeScenario1Element2NodeB;

	/**
	 * Complex type of node C in the automaton of complexTypeScenario1Element2
	 */
	private ComplexType complexTypeScenario1Element2NodeC;

	/**
	 * Complex type of node F in the automaton of complexTypeScenario1Element2NodeC
	 */
	private ComplexType complexTypeScenario1Element2NodeCNodeF;

	/**
	 * Complex type of node D in the automaton of complexTypeScenario1Element2
	 */
	private ComplexType complexTypeScenario1Element2NodeD;

	/**
	 * Schema element root on scenario 1
	 */
	private SchemaElement elementScenario1Root;

	/**
	 * Schema element element1 on scenario 1
	 */
	private SchemaElement elementScenario1Element1;

	/**
	 * Schema element A (child of element1) on scenario 1
	 */
	private SchemaElement elementScenario1Element1A;

	/**
	 * Schema element B (child of element1) on scenario 1
	 */
	private SchemaElement elementScenario1Element1B;

	/**
	 * Schema element E (child of B) on scenario 1
	 */
	private SchemaElement elementScenario1Element1CE;

	/**
	 * Schema element C (child of element1) on scenario 1
	 */
	private SchemaElement elementScenario1Element1C;

	/**
	 * Schema element element2 on scenario 1
	 */
	private SchemaElement elementScenario1Element2;
	/**
	 * Schema element A (child of element2) on scenario 1
	 */
	private SchemaElement elementScenario1Element2A;

	/**
	 * Schema element B (child of element2) on scenario 1
	 */
	private SchemaElement elementScenario1Element2B;

	/**
	 * Schema element F (child of B) on scenario 1
	 */
	private SchemaElement elementScenario1Element2CF;

	/**
	 * Schema element C (child of element2) on scenario 1
	 */
	private SchemaElement elementScenario1Element2C;

	/**
	 * Schema element D (child of element2) on scenario 1
	 */
	private SchemaElement elementScenario1Element2D;

	/**
	 * Schema attribute attr1 of of element1 on scenario 1
	 */
	private SchemaAttribute attributeScenario1Element1Attr1;

	/**
	 * Schema attribute attr2 of of element1 on scenario 1
	 */
	private SchemaAttribute attributeScenario1Element1Attr2;

	/**
	 * Schema attribute attr3 of of element1 on scenario 1
	 */
	private SchemaAttribute attributeScenario1Element1Attr3;

	/**
	 * Schema attribute attr1 of of element2 on scenario 1
	 */
	private SchemaAttribute attributeScenario1Element2Attr1;

	/**
	 * Schema attribute attr2 of of element2 on scenario 1
	 */
	private SchemaAttribute attributeScenario1Element2Attr2;

	/**
	 * Schema attribute attr4 of of element2 on scenario 1
	 */
	private SchemaAttribute attributeScenario1Element2Attr4;
	
	/**
	 * Entry for complexTypeRoot
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario1Root;
	/**
	 * Entry for complexTypeElement1
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario1Element1;

	/**
	 * Entry for complexTypeElement1NodeA
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario1Element1NodeA;

	/**
	 * Entry for complexTypeElement1NodeB
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario1Element1NodeB;

	/**
	 * Entry for complexTypeElement1NodeC
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario1Element1NodeC;

	/**
	 * Entry for complexTypeElement1NodeCNodeE
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario1Element1NodeCNodeE;

	/**
	 * Entry for complexTypeElement2
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario1Element2;

	/**
	 * Entry for complexTypeElement2NodeA
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario1Element2NodeA;

	/**
	 * Entry for complexTypeElement2NodeB
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario1Element2NodeB;

	/**
	 * Entry for complexTypeElement2NodeC
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario1Element2NodeC;

	/**
	 * Entry for complexTypeElement2NodeCNodeF
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario1Element2NodeCNodeF;

	/**
	 * Entry for complexTypeElement2NodeD
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario1Element2NodeD;

	/**
	 * Entry for elementRoot
	 */
	private BasicStatisticsEntry elementScenario1RootStatisticsEntry;
	
	/**
	 * Entry for elementScenario1Element1
	 */
	private BasicStatisticsEntry elementScenario1Element1StatisticsEntry;

	/**
	 * Entry for elementScenario1Element1NodeA
	 */
	private BasicStatisticsEntry elementScenario1Element1NodeAStatisticsEntry;

	/**
	 * Entry for elementScenario1Element1NodeB
	 */
	private BasicStatisticsEntry elementScenario1Element1NodeBStatisticsEntry;

	/**
	 * Entry for elementScenario1Element1NodeC
	 */
	private BasicStatisticsEntry elementScenario1Element1NodeCStatisticsEntry;

	/**
	 * Entry for elementScenario1Element1NodeCNodeE
	 */
	private BasicStatisticsEntry elementScenario1Element1NodeCNodeEStatisticsEntry;

	/**
	 * Entry for elementScenario1Element2
	 */
	private BasicStatisticsEntry elementScenario1Element2StatisticsEntry;

	/**
	 * Entry for elementScenario1Element2NodeA
	 */
	private BasicStatisticsEntry elementScenario1Element2NodeAStatisticsEntry;

	/**
	 * Entry for elementScenario1Element2NodeB
	 */
	private BasicStatisticsEntry elementScenario1Element2NodeBStatisticsEntry;

	/**
	 * Entry for elementScenario1Element2NodeC
	 */
	private BasicStatisticsEntry elementScenario1Element2NodeCStatisticsEntry;

	/**
	 * Entry for elementScenario1Element2NodeCNodeF
	 */
	private BasicStatisticsEntry elementScenario1Element2NodeCNodeFStatisticsEntry;

	/**
	 * Entry for elementScenario1Element2NodeD
	 */
	private BasicStatisticsEntry elementScenario1Element2NodeDStatisticsEntry;
	
	/**
	 * Entry for attributeScenario1Element1Attr1
	 */
	private BasicStatisticsEntry attributeScenario1Element1Attr1StatisticsEntry;
	
	/**
	 * Entry for attributeScenario1Element1Attr2
	 */
	private BasicStatisticsEntry attributeScenario1Element1Attr2StatisticsEntry;
	
	/**
	 * Entry for attributeScenario1Element1Attr3
	 */
	private BasicStatisticsEntry attributeScenario1Element1Attr3StatisticsEntry;
	
	/**
	 * Entry for attributeScenario1Element2Attr1
	 */
	private BasicStatisticsEntry attributeScenario1Element2Attr1StatisticsEntry;
	
	/**
	 * Entry for attributeScenario1Element2Attr2
	 */
	private BasicStatisticsEntry attributeScenario1Element2Attr2StatisticsEntry;
	
	/**
	 * Entry for attributeScenario1Element2Attr4
	 */
	private BasicStatisticsEntry attributeScenario1Element2Attr4StatisticsEntry;
	
	/**
	 * Entry for elementScenario1Element1NodeA value cuarenta
	 */
	private BasicStatisticsEntry elementScenario1Element1NodeAValueCuarentaStatisticsEntry;

	/**
	 * Entry for elementScenario1Element1NodeA value 50
	 */
	private BasicStatisticsEntry elementScenario1Element1NodeAValue50StatisticsEntry;

	/**
	 * Entry for elementScenario1Element1NodeA value 60
	 */
	private BasicStatisticsEntry elementScenario1Element1NodeAValue60StatisticsEntry;

	/**
	 * Entry for elementScenario1Element1NodeA value 70
	 */
	private BasicStatisticsEntry elementScenario1Element1NodeAValue70StatisticsEntry;

	/**
	 * Entry for elementScenario1Element1NodeB value hola
	 */
	private BasicStatisticsEntry elementScenario1Element1NodeBValueHolaStatisticsEntry;

	/**
	 * Entry for elementScenario1Element1NodeB value saludos
	 */
	private BasicStatisticsEntry elementScenario1Element1NodeBValueSaludosStatisticsEntry;

	/**
	 * Entry for elementScenario1Element1NodeB value adios
	 */
	private BasicStatisticsEntry elementScenario1Element1NodeBValueAdiosStatisticsEntry;

	/**
	 * Entry for elementScenario1Element2NodeA value 40
	 */
	private BasicStatisticsEntry elementScenario1Element2NodeAValue40StatisticsEntry;

	/**
	 * Entry for elementScenario1Element2NodeA value 50
	 */
	private BasicStatisticsEntry elementScenario1Element2NodeAValue50StatisticsEntry;

	/**
	 * Entry for elementScenario1Element2NodeA value 60
	 */
	private BasicStatisticsEntry elementScenario1Element2NodeAValue60StatisticsEntry;

	/**
	 * Entry for elementScenario1Element2NodeA value 70
	 */
	private BasicStatisticsEntry elementScenario1Element2NodeAValue70StatisticsEntry;

	/**
	 * Entry for elementScenario1Element2NodeB value buenos dias
	 */
	private BasicStatisticsEntry elementScenario1Element2NodeBValueBuenosDiasStatisticsEntry;

	/**
	 * Entry for elementScenario1Element2NodeB value saludos
	 */
	private BasicStatisticsEntry elementScenario1Element2NodeBValueSaludosStatisticsEntry;

	/**
	 * Entry for elementScenario1Element2NodeB value adios
	 */
	private BasicStatisticsEntry elementScenario1Element2NodeBValueAdiosStatisticsEntry;

	/**
	 * Entry for attributeScenario1Element1Attr1 value alfa
	 */
	private BasicStatisticsEntry attributeScenario1Element1Attr1ValueAlfaStatisticsEntry;

	/**
	 * Entry for attributeScenario1Element1Attr1 value beta
	 */
	private BasicStatisticsEntry attributeScenario1Element1Attr1ValueBetaStatisticsEntry;

	/**
	 * Entry for attributeScenario1Element1Attr2 value 1
	 */
	private BasicStatisticsEntry attributeScenario1Element1Attr2Value1StatisticsEntry;

	/**
	 * Entry for attributeScenario1Element1Attr2 value 2
	 */
	private BasicStatisticsEntry attributeScenario1Element1Attr2Value2StatisticsEntry;

	/**
	 * Entry for attributeScenario1Element1Attr3 value true
	 */
	private BasicStatisticsEntry attributeScenario1Element1Attr3ValueTrueStatisticsEntry;

	/**
	 * Entry for attributeScenario1Element1Attr3 value false
	 */
	private BasicStatisticsEntry attributeScenario1Element1Attr3ValueFalseStatisticsEntry;

	/**
	 * Entry for attributeScenario1Element1Attr3 value 0
	 */
	private BasicStatisticsEntry attributeScenario1Element1Attr3Value0StatisticsEntry;

	/**
	 * Entry for attributeScenario1Element1Attr3 value 1
	 */
	private BasicStatisticsEntry attributeScenario1Element1Attr3Value1StatisticsEntry;

	/**
	 * Entry for attributeScenario1Element2Attr1 value lambda
	 */
	private BasicStatisticsEntry attributeScenario1Element2Attr1ValueLambdaStatisticsEntry;

	/**
	 * Entry for attributeScenario1Element2Attr1 value omega
	 */
	private BasicStatisticsEntry attributeScenario1Element2Attr1ValueOmegaStatisticsEntry;

	/**
	 * Entry for attributeScenario1Element2Attr2 value 3
	 */
	private BasicStatisticsEntry attributeScenario1Element2Attr2Value3StatisticsEntry;

	/**
	 * Entry for attributeScenario1Element2Attr2 value 4
	 */
	private BasicStatisticsEntry attributeScenario1Element2Attr2Value4StatisticsEntry;

	/**
	 * Entry for attributeScenario1Element2Attr4 value gamma
	 */
	private BasicStatisticsEntry attributeScenario1Element2Attr4ValueGammaStatisticsEntry;

	/**
	 * Entry for attributeScenario1Element2Attr4 value epsilon
	 */
	private BasicStatisticsEntry attributeScenario1Element2Attr4ValueEpsilonStatisticsEntry;

	/**
	 * Entry for attributeScenario1Element2Attr4 value omicron
	 */
	private BasicStatisticsEntry attributeScenario1Element2Attr4ValueOmicronStatisticsEntry;

	/**
	 * Entry for attributeScenario1Element2Attr4 value zeta
	 */
	private BasicStatisticsEntry attributeScenario1Element2Attr4ValueZetaStatisticsEntry;
	
	/**
	 * Statistics of scenario 1
	 */
	private Statistics statistics1;

	/**
	 * The testing schema, which we will use to generate XSDs on scenario 1
	 */
	@SuppressWarnings("unused")
	private Schema schema1;
	
	/**
	 * Inference configuration of scenario 1
	 */
	private XSDInferenceConfiguration configuration1;
	
	//Other fields
	
	/**
	 * The empty regular expression
	 */
	private final RegularExpression emptyRegexp = new EmptyRegularExpression();
	
	/**
	 * It builds the non-trivial SimpleType objects used at scenario 1
	 */
	private void buildSimpleTypesScenario1() {
		List<String> simpleTypeElement1NodeAValues = Lists.newArrayList("cuarenta","50","60","70");
		simpleTypeScenario1Element1NodeA=new SimpleType("_root-_element1-_a","xs:string",simpleTypeElement1NodeAValues,true);
		List<String> simpleTypeElement1NodeBValues = Lists.newArrayList("hola","saludos","hasta luego");
		simpleTypeScenario1Element1NodeB=new SimpleType("_root-_element1-_b","xs:string",simpleTypeElement1NodeBValues,true);
		List<String> simpleTypeElement2NodeAValues = Lists.newArrayList("40","50","60","70");
		simpleTypeScenario1Element2NodeA=new SimpleType("_root-_element2-_a","xs:integer",simpleTypeElement2NodeAValues,true);
		List<String> simpleTypeElement2NodeBValues = Lists.newArrayList("buenos dias","saludos","adios");
		simpleTypeScenario1Element2NodeB=new SimpleType("_root-_element2-_b","xs:string",simpleTypeElement2NodeBValues,true);
	
		List<String> simpleTypeElement1Attr1Values = Lists.newArrayList("alfa","beta");
		simpleTypeScenario1Element1Attr1 =  new SimpleType("_root-_element1-attr1","xs:string",simpleTypeElement1Attr1Values,false);
		List<String> simpleTypeElement1Attr2Values = Lists.newArrayList("1","2");
		simpleTypeScenario1Element1Attr2 =  new SimpleType("_root-_element1-attr2","xs:integer",simpleTypeElement1Attr2Values,false);
		List<String> simpleTypeElement1Attr3Values = Lists.newArrayList("true","false","0","1");
		simpleTypeScenario1Element1Attr3 =  new SimpleType("_root-_element1-attr3","xs:boolean",simpleTypeElement1Attr3Values,false);
		List<String> simpleTypeElement2Attr1Values = Lists.newArrayList("lambda","omega");
		simpleTypeScenario1Element2Attr1 =  new SimpleType("_root-_element2-attr1","xs:string",simpleTypeElement2Attr1Values,false);
		List<String> simpleTypeElement2Attr2Values = Lists.newArrayList("3","4");
		simpleTypeScenario1Element2Attr2 =  new SimpleType("_root-_element2-attr2","xs:integer",simpleTypeElement2Attr2Values,false);
		List<String> simpleTypeElement2Attr4Values = Lists.newArrayList("gamma","epsilon","omicron","zeta");
		simpleTypeScenario1Element2Attr4 =  new SimpleType("_root-_element2-attr4","xs:string",simpleTypeElement2Attr4Values,false);
	}

	/**
	 * It builds the SchemaAttribute objects used at scenario 1
	 */
	private void buildSchemaAttributesScenario1() {
		attributeScenario1Element1Attr1=new SchemaAttribute("attr1", "", false, simpleTypeScenario1Element1Attr1);
		attributeScenario1Element1Attr2=new SchemaAttribute("attr2", "", false, simpleTypeScenario1Element1Attr2);
		attributeScenario1Element1Attr3=new SchemaAttribute("attr3", "", false, simpleTypeScenario1Element1Attr3);
	
		attributeScenario1Element2Attr1=new SchemaAttribute("attr1", "", false, simpleTypeScenario1Element2Attr1);
		attributeScenario1Element2Attr2=new SchemaAttribute("attr2", "", true, simpleTypeScenario1Element2Attr2);
		attributeScenario1Element2Attr4=new SchemaAttribute("attr4", "", true, simpleTypeScenario1Element2Attr4);
	}

	/**
	 * It builds the ComplexType objects of the schema at scenario 1
	 */
	private void buildComplexTypesScenario1() {
		complexTypeScenario1Root = new ComplexType("_root",new ExtendedAutomaton(),new SimpleType("root"),new ArrayList<SchemaAttribute>());
	
		complexTypeScenario1Element1 = new ComplexType("_root-_element1",new ExtendedAutomaton(),new SimpleType("_root-_element1"),Lists.newArrayList(attributeScenario1Element1Attr1,attributeScenario1Element1Attr2,attributeScenario1Element1Attr3));
		complexTypeScenario1Element1NodeA = new ComplexType("_root-_element1-_a",new ExtendedAutomaton(),simpleTypeScenario1Element1NodeA,new ArrayList<SchemaAttribute>());
		complexTypeScenario1Element1NodeB = new ComplexType("_root-_element1-_b",new ExtendedAutomaton(),simpleTypeScenario1Element1NodeB,new ArrayList<SchemaAttribute>());
		complexTypeScenario1Element1NodeC = new ComplexType("_root-_element1-_c",new ExtendedAutomaton(),new SimpleType("_root-_element1-_c"),new ArrayList<SchemaAttribute>());
		complexTypeScenario1Element1NodeCNodeE = new ComplexType("_root-_element1-_c-_e",new ExtendedAutomaton(),new SimpleType("_root-_element1-_c"),new ArrayList<SchemaAttribute>());
	
		complexTypeScenario1Element2 = new ComplexType("_root-_element2",new ExtendedAutomaton(),new SimpleType("_root-_element2"),Lists.newArrayList(attributeScenario1Element2Attr1,attributeScenario1Element2Attr2,attributeScenario1Element2Attr4));
		complexTypeScenario1Element2NodeA = new ComplexType("_root-_element2-_a",new ExtendedAutomaton(),simpleTypeScenario1Element2NodeA,new ArrayList<SchemaAttribute>());
		complexTypeScenario1Element2NodeB = new ComplexType("_root-_element2-_b",new ExtendedAutomaton(),simpleTypeScenario1Element2NodeB,new ArrayList<SchemaAttribute>());
		complexTypeScenario1Element2NodeC = new ComplexType("_root-_element2-_c",new ExtendedAutomaton(),new SimpleType("_root-_element2-_c"),new ArrayList<SchemaAttribute>());
		complexTypeScenario1Element2NodeCNodeF = new ComplexType("_root-_element2-_c-_f",new ExtendedAutomaton(),new SimpleType("_root-_element2-_f"),new ArrayList<SchemaAttribute>());
		complexTypeScenario1Element2NodeD = new ComplexType("_root-_element2-_d",new ExtendedAutomaton(),new SimpleType("_root-_element2-_d"),new ArrayList<SchemaAttribute>());
	}

	/**
	 * It builds the SchemaElement objects used at scenario 1
	 */
	private void buildSchemaElementsScenario1() {
	
		elementScenario1Root=new SchemaElement("root","",complexTypeScenario1Root);
		elementScenario1Root.setValidRoot(true);
	
		elementScenario1Element1=new SchemaElement("element1","",complexTypeScenario1Element1);
		elementScenario1Element1A=new SchemaElement("a","",complexTypeScenario1Element1NodeA);
		elementScenario1Element1B=new SchemaElement("b","",complexTypeScenario1Element1NodeB);
		elementScenario1Element1C=new SchemaElement("c","",complexTypeScenario1Element1NodeC);
		elementScenario1Element1CE=new SchemaElement("e","",complexTypeScenario1Element1NodeCNodeE);
	
		elementScenario1Element2=new SchemaElement("element2","",complexTypeScenario1Element2);
		elementScenario1Element2A=new SchemaElement("a","",complexTypeScenario1Element2NodeA);
		elementScenario1Element2B=new SchemaElement("b","",complexTypeScenario1Element2NodeB);
		elementScenario1Element2C=new SchemaElement("c","",complexTypeScenario1Element2NodeC);
		elementScenario1Element2CF=new SchemaElement("f","",complexTypeScenario1Element2NodeCNodeF);
		elementScenario1Element2D=new SchemaElement("d","",complexTypeScenario1Element2NodeD);
	}

	/**
	 * It builds the automatons used at scenario 1
	 */
	private void buildAutomatonsScenario1() {
		ExtendedAutomaton complexTypeRootAutomaton=complexTypeScenario1Root.getAutomaton();
		complexTypeRootAutomaton.setInitialState(initialState);
		complexTypeRootAutomaton.setFinalState(finalState);
		complexTypeRootAutomaton.addEdge(initialState, elementScenario1Element1, 8L);
		complexTypeRootAutomaton.addEdge(elementScenario1Element1, elementScenario1Element2, 8L);
		complexTypeRootAutomaton.addEdge(elementScenario1Element2, finalState, 8L);
	
		ExtendedAutomaton complexTypeElement1Automaton=complexTypeScenario1Element1.getAutomaton();
		complexTypeElement1Automaton.setInitialState(initialState);
		complexTypeElement1Automaton.setFinalState(finalState);
		complexTypeElement1Automaton.addEdge(initialState, elementScenario1Element1A, 4L);
		complexTypeElement1Automaton.addEdge(initialState, elementScenario1Element1B, 4L);
		complexTypeElement1Automaton.addEdge(elementScenario1Element1A, elementScenario1Element1C, 4L);
		complexTypeElement1Automaton.addEdge(elementScenario1Element1B, elementScenario1Element1C, 4L);
		complexTypeElement1Automaton.addEdge(elementScenario1Element1C, finalState, 8L);
	
		ExtendedAutomaton complexTypeElement1NodeCAutomaton=complexTypeScenario1Element1NodeC.getAutomaton();
		complexTypeElement1NodeCAutomaton.setInitialState(initialState);
		complexTypeElement1NodeCAutomaton.setFinalState(finalState);
		complexTypeElement1NodeCAutomaton.addEdge(initialState, elementScenario1Element1CE,8L);
		complexTypeElement1NodeCAutomaton.addEdge(elementScenario1Element1CE, finalState,8L);
	
		ExtendedAutomaton complexTypeElement2Automaton=complexTypeScenario1Element2.getAutomaton();
		complexTypeElement2Automaton.setInitialState(initialState);
		complexTypeElement2Automaton.setFinalState(finalState);
		complexTypeElement2Automaton.addEdge(initialState, elementScenario1Element2A, 8L);
		complexTypeElement2Automaton.addEdge(elementScenario1Element2A, elementScenario1Element2B, 8L);
		complexTypeElement2Automaton.addEdge(elementScenario1Element2B, elementScenario1Element2C, 8L);
		complexTypeElement2Automaton.addEdge(elementScenario1Element2C, elementScenario1Element2C, 5L);
		complexTypeElement2Automaton.addEdge(elementScenario1Element2C, elementScenario1Element2D, 8L);
		complexTypeElement2Automaton.addEdge(elementScenario1Element2D, finalState, 8L);
	
		ExtendedAutomaton complexTypeElement2NodeCAutomaton=complexTypeScenario1Element2NodeC.getAutomaton();
		complexTypeElement2NodeCAutomaton.setInitialState(initialState);
		complexTypeElement2NodeCAutomaton.setFinalState(finalState);
		complexTypeElement2NodeCAutomaton.addEdge(initialState, elementScenario1Element2CF,13L);
		complexTypeElement2NodeCAutomaton.addEdge(elementScenario1Element2CF, finalState,13L);
	}

	/**
	 * It builds the RegularExpression objects associated with the complex types
	 */
	private void buildRegularExpressionsScenario1(){
		RegularExpression complexTypeRootRegexp = new Sequence(ImmutableList.of(elementScenario1Element1,elementScenario1Element2));
		complexTypeScenario1Root.setRegularExpression(complexTypeRootRegexp);
	
		RegularExpression complexTypeElement1SubregexpChoice = new Choice(ImmutableList.of(elementScenario1Element1A,elementScenario1Element1B));
		RegularExpression complexTypeElement1Regexp = new Sequence(ImmutableList.of(complexTypeElement1SubregexpChoice, elementScenario1Element1C));
		complexTypeScenario1Element1.setRegularExpression(complexTypeElement1Regexp);
		complexTypeScenario1Element1NodeA.setRegularExpression(emptyRegexp);
		complexTypeScenario1Element1NodeB.setRegularExpression(emptyRegexp);
		complexTypeScenario1Element1NodeC.setRegularExpression(elementScenario1Element1CE);
		complexTypeScenario1Element1NodeCNodeE.setRegularExpression(emptyRegexp);
	
		RegularExpression complexTypeElement2SubregexpRepeatedAtLeastOnce = new RepeatedAtLeastOnce(elementScenario1Element2C);
		RegularExpression complexTypeElement2Regexp = new Sequence(ImmutableList.of(elementScenario1Element2A, elementScenario1Element2B, complexTypeElement2SubregexpRepeatedAtLeastOnce, elementScenario1Element2D));
		complexTypeScenario1Element2.setRegularExpression(complexTypeElement2Regexp);
		complexTypeScenario1Element2NodeA.setRegularExpression(emptyRegexp);
		complexTypeScenario1Element2NodeB.setRegularExpression(emptyRegexp);
		complexTypeScenario1Element2NodeC.setRegularExpression(elementScenario1Element2CF);
		complexTypeScenario1Element2NodeCNodeF.setRegularExpression(emptyRegexp);
		complexTypeScenario1Element2NodeD.setRegularExpression(emptyRegexp);
	}

	/**
	 * It builds the structures of the Schema object of the scenario 1
	 */
	private void buildSchemaScenario1() {
		NavigableMap<String,SortedSet<String>> prefixNamespaceMapping = new TreeMap<String, SortedSet<String>>();
		prefixNamespaceMapping.put("", new TreeSet<>(Collections.singleton("")));
		prefixNamespaceMapping.put(NAMESPACE_URI_TEST, new TreeSet<>(Collections.singleton("test")));
		prefixNamespaceMapping.put(NAMESPACE_URI_TESTING, new TreeSet<>(Collections.singleton("testing")));
	
		Map<String, ComplexType> complexTypes = new HashMap<>(11);
		complexTypes.put("_root", complexTypeScenario1Root);
		complexTypes.put("_root-_element1", complexTypeScenario1Element1);
		complexTypes.put("_root-_element1-_a", complexTypeScenario1Element1NodeA);
		complexTypes.put("_root-_element1-_b", complexTypeScenario1Element1NodeB);
		complexTypes.put("_root-_element1-_c", complexTypeScenario1Element1NodeC);
		complexTypes.put("_root-_element1-_c-_e", complexTypeScenario1Element1NodeCNodeE);
		complexTypes.put("_root-_element2", complexTypeScenario1Element2);
		complexTypes.put("_root-_element2-_a", complexTypeScenario1Element2NodeA);
		complexTypes.put("_root-_element2-_b", complexTypeScenario1Element2NodeB);
		complexTypes.put("_root-_element2-_c", complexTypeScenario1Element2NodeC);
		complexTypes.put("_root-_element2-_d", complexTypeScenario1Element2NodeD);
		complexTypes.put("_root-_element2-_c-_f", complexTypeScenario1Element2NodeCNodeF);
	
	
		Table<String, String, SchemaElement> elements = HashBasedTable.create(1, 11);
		elements.put("", "root", elementScenario1Root);
		elements.put("", "_root-element1", elementScenario1Element1);
		elements.put("", "_root-_element1-a", elementScenario1Element1A);
		elements.put("", "_root-_element1-b", elementScenario1Element1B);
		elements.put("", "_root-_element1-c", elementScenario1Element1C);
		elements.put("", "_root-_element1-_c-e", elementScenario1Element1CE);
		elements.put("", "_root-element2", elementScenario1Element2);
		elements.put("", "_root-_element2-a", elementScenario1Element2A);
		elements.put("", "_root-_element2-b", elementScenario1Element2B);
		elements.put("", "_root-_element2-c", elementScenario1Element2C);
		elements.put("", "_root-_element2-d", elementScenario1Element2D);
		elements.put("", "_root-_element2-_c-f", elementScenario1Element2CF);
	
	
		Table<String, String, SchemaAttribute> attributes = HashBasedTable.create(1,6);
		attributes.put("", "_root-_element1-attr1", attributeScenario1Element1Attr1);
		attributes.put("", "_root-_element1-attr2", attributeScenario1Element1Attr2);
		attributes.put("", "_root-_element1-attr3", attributeScenario1Element1Attr3);
		attributes.put("", "_root-_element2-attr1", attributeScenario1Element2Attr1);
		attributes.put("", "_root-_element2-attr2", attributeScenario1Element2Attr2);
		attributes.put("", "_root-_element2-attr4", attributeScenario1Element2Attr4);
	
		Map<String, SimpleType> simpleTypes = new HashMap<>(17);
		simpleTypes.put("_root", complexTypeScenario1Root.getTextSimpleType());
		simpleTypes.put("_root-_element1", complexTypeScenario1Element1.getTextSimpleType());
		simpleTypes.put("_root-_element1-_a", simpleTypeScenario1Element1NodeA);
		simpleTypes.put("_root-_element1-_b", simpleTypeScenario1Element1NodeB);
		simpleTypes.put("_root-_element1-_c", complexTypeScenario1Element1NodeC.getTextSimpleType());
		simpleTypes.put("_root-_element1-_c-_e", complexTypeScenario1Element1NodeCNodeE.getTextSimpleType());
		simpleTypes.put("_root-_element2", complexTypeScenario1Element2.getTextSimpleType());
		simpleTypes.put("_root-_element2-_a", simpleTypeScenario1Element2NodeA);
		simpleTypes.put("_root-_element2-_b", simpleTypeScenario1Element2NodeB);
		simpleTypes.put("_root-_element2-_c", complexTypeScenario1Element2NodeC.getTextSimpleType());
		simpleTypes.put("_root-_element2-_c-_f", complexTypeScenario1Element2NodeCNodeF.getTextSimpleType());
		simpleTypes.put("_root-_element2-_c", complexTypeScenario1Element2NodeC.getTextSimpleType());
	
		schema1 = new Schema(prefixNamespaceMapping, elements, attributes, complexTypes, simpleTypes, statistics1);
	
	}
	
	/**
	 * This method builds all the {@link BasicStatisticsEntry} objects used at the scenario 1
	 */
	private void buildBasicStatisticsEntriesScenario1(){
		List<Double> elementScenario1RootStatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		elementScenario1RootStatisticsEntry = new BasicStatisticsEntry(elementScenario1RootStatisticsEntryValues);

		List<Double> elementScenario1Element1StatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		elementScenario1Element1StatisticsEntry = new BasicStatisticsEntry(elementScenario1Element1StatisticsEntryValues);

		List<Double> elementScenario1Element1NodeAStatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,0.0,0.0,0.0,0.0);
		elementScenario1Element1NodeAStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element1NodeAStatisticsEntryValues);

		List<Double> elementScenario1Element1NodeBStatisticsEntryValues = Lists.newArrayList(0.0,0.0,0.0,0.0,1.0,1.0,1.0,1.0);
		elementScenario1Element1NodeBStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element1NodeBStatisticsEntryValues);

		List<Double> elementScenario1Element1NodeCStatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		elementScenario1Element1NodeCStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element1NodeCStatisticsEntryValues);

		List<Double> elementScenario1Element1NodeCNodeEStatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		elementScenario1Element1NodeCNodeEStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element1NodeCNodeEStatisticsEntryValues);

		List<Double> elementScenario1Element2StatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		elementScenario1Element2StatisticsEntry = new BasicStatisticsEntry(elementScenario1Element2StatisticsEntryValues);

		List<Double> elementScenario1Element2NodeAStatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		elementScenario1Element2NodeAStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element2NodeAStatisticsEntryValues);

		List<Double> elementScenario1Element2NodeBStatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		elementScenario1Element2NodeBStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element2NodeBStatisticsEntryValues);

		List<Double> elementScenario1Element2NodeCStatisticsEntryValues = Lists.newArrayList(2.0,2.0,2.0,2.0,2.0,1.0,1.0,1.0);
		elementScenario1Element2NodeCStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element2NodeCStatisticsEntryValues);

		List<Double> elementScenario1Element2NodeCNodeFStatisticsEntryValues = Lists.newArrayList(2.0,2.0,2.0,2.0,2.0,1.0,1.0,1.0);
		elementScenario1Element2NodeCNodeFStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element2NodeCNodeFStatisticsEntryValues);

		List<Double> elementScenario1Element2NodeDStatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		elementScenario1Element2NodeDStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element2NodeDStatisticsEntryValues);

		List<Double> attributeScenario1Element1Attr1StatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		attributeScenario1Element1Attr1StatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element1Attr1StatisticsEntryValues);

		List<Double> attributeScenario1Element1Attr2StatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		attributeScenario1Element1Attr2StatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element1Attr2StatisticsEntryValues);

		List<Double> attributeScenario1Element1Attr3StatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		attributeScenario1Element1Attr3StatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element1Attr3StatisticsEntryValues);

		List<Double> attributeScenario1Element2Attr1StatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0);
		attributeScenario1Element2Attr1StatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element2Attr1StatisticsEntryValues);

		List<Double> attributeScenario1Element2Attr2StatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,0.0,0.0,0.0,0.0);
		attributeScenario1Element2Attr2StatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element2Attr2StatisticsEntryValues);

		List<Double> attributeScenario1Element2Attr4StatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,1.0,0.0,0.0,0.0,0.0);
		attributeScenario1Element2Attr4StatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element2Attr4StatisticsEntryValues);

		List<Double> elementScenario1Element1NodeAValueCuarentaStatisticsEntryValues = Lists.newArrayList(1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0);
		elementScenario1Element1NodeAValueCuarentaStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element1NodeAValueCuarentaStatisticsEntryValues);

		List<Double> elementScenario1Element1NodeAValue50StatisticsEntryValues = Lists.newArrayList(0.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0);
		elementScenario1Element1NodeAValue50StatisticsEntry = new BasicStatisticsEntry(elementScenario1Element1NodeAValue50StatisticsEntryValues);

		List<Double> elementScenario1Element1NodeAValue60StatisticsEntryValues = Lists.newArrayList(0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0);
		elementScenario1Element1NodeAValue60StatisticsEntry = new BasicStatisticsEntry(elementScenario1Element1NodeAValue60StatisticsEntryValues);

		List<Double> elementScenario1Element1NodeAValue70StatisticsEntryValues = Lists.newArrayList(0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0);
		elementScenario1Element1NodeAValue70StatisticsEntry = new BasicStatisticsEntry(elementScenario1Element1NodeAValue70StatisticsEntryValues);

		List<Double> elementScenario1Element1NodeBValueHolaStatisticsEntryValues = Lists.newArrayList(0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0);
		elementScenario1Element1NodeBValueHolaStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element1NodeBValueHolaStatisticsEntryValues);

		List<Double> elementScenario1Element1NodeBValueSaludosStatisticsEntryValues = Lists.newArrayList(0.0,0.0,0.0,0.0,0.0,1.0,1.0,0.0);
		elementScenario1Element1NodeBValueSaludosStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element1NodeBValueSaludosStatisticsEntryValues);

		List<Double> elementScenario1Element1NodeBValueAdiosStatisticsEntryValues = Lists.newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,0.0,1.0);
		elementScenario1Element1NodeBValueAdiosStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element1NodeBValueAdiosStatisticsEntryValues);

		List<Double> elementScenario1Element2NodeAValue40StatisticsEntryValues = Lists.newArrayList(1.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0);
		elementScenario1Element2NodeAValue40StatisticsEntry = new BasicStatisticsEntry(elementScenario1Element2NodeAValue40StatisticsEntryValues);

		List<Double> elementScenario1Element2NodeAValue50StatisticsEntryValues = Lists.newArrayList(0.0,0.0,1.0,1.0,0.0,0.0,0.0,0.0);
		elementScenario1Element2NodeAValue50StatisticsEntry = new BasicStatisticsEntry(elementScenario1Element2NodeAValue50StatisticsEntryValues);

		List<Double> elementScenario1Element2NodeAValue60StatisticsEntryValues = Lists.newArrayList(0.0,0.0,0.0,0.0,1.0,1.0,0.0,0.0);
		elementScenario1Element2NodeAValue60StatisticsEntry = new BasicStatisticsEntry(elementScenario1Element2NodeAValue60StatisticsEntryValues);

		List<Double> elementScenario1Element2NodeAValue70StatisticsEntryValues = Lists.newArrayList(0.0,0.0,0.0,0.0,0.0,0.0,1.0,1.0);
		elementScenario1Element2NodeAValue70StatisticsEntry = new BasicStatisticsEntry(elementScenario1Element2NodeAValue70StatisticsEntryValues);

		List<Double> elementScenario1Element2NodeBValueBuenosDiasStatisticsEntryValues = Lists.newArrayList(1.0,1.0,1.0,0.0,0.0,0.0,0.0,0.0);
		elementScenario1Element2NodeBValueBuenosDiasStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element2NodeBValueBuenosDiasStatisticsEntryValues);

		List<Double> elementScenario1Element2NodeBValueSaludosStatisticsEntryValues = Lists.newArrayList(0.0,0.0,0.0,1.0,1.0,0.0,0.0,0.0);
		elementScenario1Element2NodeBValueSaludosStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element2NodeBValueSaludosStatisticsEntryValues);

		List<Double> elementScenario1Element2NodeBValueAdiosStatisticsEntryValues = Lists.newArrayList(0.0,0.0,0.0,0.0,0.0,1.0,1.0,1.0);
		elementScenario1Element2NodeBValueAdiosStatisticsEntry = new BasicStatisticsEntry(elementScenario1Element2NodeBValueAdiosStatisticsEntryValues);

		List<Double> attributeScenario1Element1Attr1ValueAlfaStatisticsEntryValues = Lists.newArrayList(1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0);
		attributeScenario1Element1Attr1ValueAlfaStatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element1Attr1ValueAlfaStatisticsEntryValues);

		List<Double> attributeScenario1Element1Attr1ValueBetaStatisticsEntryValues = Lists.newArrayList(0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0);
		attributeScenario1Element1Attr1ValueBetaStatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element1Attr1ValueBetaStatisticsEntryValues);

		List<Double> attributeScenario1Element1Attr2Value1StatisticsEntryValues = Lists.newArrayList(1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0);
		attributeScenario1Element1Attr2Value1StatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element1Attr2Value1StatisticsEntryValues);

		List<Double> attributeScenario1Element1Attr2Value2StatisticsEntryValues = Lists.newArrayList(0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0);
		attributeScenario1Element1Attr2Value2StatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element1Attr2Value2StatisticsEntryValues);

		List<Double> attributeScenario1Element1Attr3ValueTrueStatisticsEntryValues = Lists.newArrayList(1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0);
		attributeScenario1Element1Attr3ValueTrueStatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element1Attr3ValueTrueStatisticsEntryValues);

		List<Double> attributeScenario1Element1Attr3ValueFalseStatisticsEntryValues = Lists.newArrayList(0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0);
		attributeScenario1Element1Attr3ValueFalseStatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element1Attr3ValueFalseStatisticsEntryValues);
		
		List<Double> attributeScenario1Element1Attr3Value0StatisticsEntryValues = Lists.newArrayList(0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0);
		attributeScenario1Element1Attr3Value0StatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element1Attr3Value0StatisticsEntryValues);

		List<Double> attributeScenario1Element1Attr3Value1StatisticsEntryValues = Lists.newArrayList(0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0);
		attributeScenario1Element1Attr3Value1StatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element1Attr3Value1StatisticsEntryValues);
		
		List<Double> attributeScenario1Element2Attr1ValueLambdaStatisticsEntryValues = Lists.newArrayList(1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0);
		attributeScenario1Element2Attr1ValueLambdaStatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element2Attr1ValueLambdaStatisticsEntryValues);

		List<Double> attributeScenario1Element2Attr1ValueOmegaStatisticsEntryValues = Lists.newArrayList(0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0);
		attributeScenario1Element2Attr1ValueOmegaStatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element2Attr1ValueOmegaStatisticsEntryValues);

		List<Double> attributeScenario1Element2Attr2Value3StatisticsEntryValues = Lists.newArrayList(1.0,0.0,1.0,0.0,1.0,0.0,1.0,0.0);
		attributeScenario1Element2Attr2Value3StatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element2Attr2Value3StatisticsEntryValues);

		List<Double> attributeScenario1Element2Attr2Value4StatisticsEntryValues = Lists.newArrayList(0.0,1.0,0.0,1.0,0.0,1.0,0.0,1.0);
		attributeScenario1Element2Attr2Value4StatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element2Attr2Value4StatisticsEntryValues);

		List<Double> attributeScenario1Element2Attr4ValueGammaStatisticsEntryValues = Lists.newArrayList(1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0);
		attributeScenario1Element2Attr4ValueGammaStatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element2Attr4ValueGammaStatisticsEntryValues);

		List<Double> attributeScenario1Element2Attr4ValueEpsilonStatisticsEntryValues = Lists.newArrayList(0.0,1.0,0.0,0.0,0.0,1.0,0.0,0.0);
		attributeScenario1Element2Attr4ValueEpsilonStatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element2Attr4ValueEpsilonStatisticsEntryValues);

		List<Double> attributeScenario1Element2Attr4ValueOmicronStatisticsEntryValues = Lists.newArrayList(0.0,0.0,1.0,0.0,0.0,0.0,1.0,0.0);
		attributeScenario1Element2Attr4ValueOmicronStatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element2Attr4ValueOmicronStatisticsEntryValues);

		List<Double> attributeScenario1Element2Attr4ValueZetaStatisticsEntryValues = Lists.newArrayList(0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0);
		attributeScenario1Element2Attr4ValueZetaStatisticsEntry = new BasicStatisticsEntry(attributeScenario1Element2Attr4ValueZetaStatisticsEntryValues);

	}
	
	/**
	 * This method builds the attributeAtPath info of the scenario1
	 */
	private void buildAttributeAtPathInfoScenario1(){
		Map<String, BasicStatisticsEntry> attributeAtPathInfo = statistics1.getAttributeAtPathInfo();
		attributeAtPathInfo.put("/root/element1/@attr1",attributeScenario1Element1Attr1StatisticsEntry);
		attributeAtPathInfo.put("/root/element1/@attr2",attributeScenario1Element1Attr2StatisticsEntry);
		attributeAtPathInfo.put("/root/element1/@attr3",attributeScenario1Element1Attr3StatisticsEntry);
		attributeAtPathInfo.put("/root/element2/@attr1",attributeScenario1Element2Attr1StatisticsEntry);
		attributeAtPathInfo.put("/root/element2/@attr2",attributeScenario1Element2Attr2StatisticsEntry);
		attributeAtPathInfo.put("/root/element2/@attr4",attributeScenario1Element2Attr4StatisticsEntry);	
	}
	
	/**
	 * This method builds the elementAtPath info of the scenario1
	 */
	private void buildElementAtPathInfo(){
		Map<String, BasicStatisticsEntry> elementAtPathInfo = statistics1.getElementAtPathInfo();
		elementAtPathInfo.put("/root",elementScenario1RootStatisticsEntry);
		elementAtPathInfo.put("/root/element1",elementScenario1Element1StatisticsEntry);
		elementAtPathInfo.put("/root/element1/a",elementScenario1Element1NodeAStatisticsEntry);
		elementAtPathInfo.put("/root/element1/b",elementScenario1Element1NodeBStatisticsEntry);
		elementAtPathInfo.put("/root/element1/c",elementScenario1Element1NodeCStatisticsEntry);
		elementAtPathInfo.put("/root/element1/c/e",elementScenario1Element1NodeCNodeEStatisticsEntry);
		elementAtPathInfo.put("/root/element2",elementScenario1Element2StatisticsEntry);
		elementAtPathInfo.put("/root/element2/a",elementScenario1Element2NodeAStatisticsEntry);
		elementAtPathInfo.put("/root/element2/b",elementScenario1Element2NodeBStatisticsEntry);
		elementAtPathInfo.put("/root/element2/c",elementScenario1Element2NodeCStatisticsEntry);
		elementAtPathInfo.put("/root/element2/c/f",elementScenario1Element2NodeCNodeFStatisticsEntry);
		elementAtPathInfo.put("/root/element2/d",elementScenario1Element2NodeDStatisticsEntry);
	}
	
	private void buildValuesAtPathInfo(){
		Table<String, String, BasicStatisticsEntry> valuesAtPathInfo = statistics1.getValuesAtPathInfo();
		valuesAtPathInfo.put("/root/element1/a","cuarenta",elementScenario1Element1NodeAValueCuarentaStatisticsEntry);
		valuesAtPathInfo.put("/root/element1/a","50",elementScenario1Element1NodeAValue50StatisticsEntry);
		valuesAtPathInfo.put("/root/element1/a","60",elementScenario1Element1NodeAValue60StatisticsEntry);
		valuesAtPathInfo.put("/root/element1/a","70",elementScenario1Element1NodeAValue70StatisticsEntry);
		valuesAtPathInfo.put("/root/element1/b","hola",elementScenario1Element1NodeBValueHolaStatisticsEntry);
		valuesAtPathInfo.put("/root/element1/b","saludos",elementScenario1Element1NodeBValueSaludosStatisticsEntry);
		valuesAtPathInfo.put("/root/element1/b","adios",elementScenario1Element1NodeBValueAdiosStatisticsEntry);
		valuesAtPathInfo.put("/root/element2/a","40",elementScenario1Element2NodeAValue40StatisticsEntry);
		valuesAtPathInfo.put("/root/element2/a","50",elementScenario1Element2NodeAValue50StatisticsEntry);
		valuesAtPathInfo.put("/root/element2/a","60",elementScenario1Element2NodeAValue60StatisticsEntry);
		valuesAtPathInfo.put("/root/element2/a","70",elementScenario1Element2NodeAValue70StatisticsEntry);
		valuesAtPathInfo.put("/root/element2/b","buenos dias",elementScenario1Element2NodeBValueBuenosDiasStatisticsEntry);
		valuesAtPathInfo.put("/root/element2/b","saludos",elementScenario1Element2NodeBValueSaludosStatisticsEntry);
		valuesAtPathInfo.put("/root/element2/b","adios",elementScenario1Element2NodeBValueAdiosStatisticsEntry);
		valuesAtPathInfo.put("/root/element1/@attr1","alfa",attributeScenario1Element1Attr1ValueAlfaStatisticsEntry);
		valuesAtPathInfo.put("/root/element1/@attr1","beta",attributeScenario1Element1Attr1ValueBetaStatisticsEntry);
		valuesAtPathInfo.put("/root/element1/@attr2","1",attributeScenario1Element1Attr2Value1StatisticsEntry);
		valuesAtPathInfo.put("/root/element1/@attr2","2",attributeScenario1Element1Attr2Value2StatisticsEntry);
		valuesAtPathInfo.put("/root/element1/@attr3","true",attributeScenario1Element1Attr3ValueTrueStatisticsEntry);
		valuesAtPathInfo.put("/root/element1/@attr3","false",attributeScenario1Element1Attr3ValueFalseStatisticsEntry);
		valuesAtPathInfo.put("/root/element1/@attr3","0",attributeScenario1Element1Attr3Value0StatisticsEntry);
		valuesAtPathInfo.put("/root/element1/@attr3","1",attributeScenario1Element1Attr3Value1StatisticsEntry);
		valuesAtPathInfo.put("/root/element2/@attr1","lambda",attributeScenario1Element2Attr1ValueLambdaStatisticsEntry);
		valuesAtPathInfo.put("/root/element2/@attr1","omega",attributeScenario1Element2Attr1ValueOmegaStatisticsEntry);
		valuesAtPathInfo.put("/root/element2/@attr2","3",attributeScenario1Element2Attr2Value3StatisticsEntry);
		valuesAtPathInfo.put("/root/element2/@attr2","4",attributeScenario1Element2Attr2Value4StatisticsEntry);
		valuesAtPathInfo.put("/root/element2/@attr4","gamma",attributeScenario1Element2Attr4ValueGammaStatisticsEntry);
		valuesAtPathInfo.put("/root/element2/@attr4","epsilon",attributeScenario1Element2Attr4ValueEpsilonStatisticsEntry);
		valuesAtPathInfo.put("/root/element2/@attr4","omicron",attributeScenario1Element2Attr4ValueOmicronStatisticsEntry);
		valuesAtPathInfo.put("/root/element2/@attr4","zeta",attributeScenario1Element2Attr4ValueZetaStatisticsEntry);
	}
	
	/**
	 * This method builds the complex types info of the statistics of the scenario 1
	 */
	private void buildComplexTypesStatisticInfoScenario1(){
		Map<ComplexType, ComplexTypeStatisticsEntry> complexTypeInfo = statistics1.getComplexTypeInfo();
		
		complexTypeStatisticsEntryScenario1Root = new ComplexTypeStatisticsEntry(8);
		complexTypeStatisticsEntryScenario1Root.getElementInfo().put(elementScenario1Root, elementScenario1RootStatisticsEntry);
		complexTypeStatisticsEntryScenario1Root.getSubpatternsInfo().put(Lists.newArrayList(elementScenario1Element1,elementScenario1Element2), 8);
		complexTypeInfo.put(complexTypeScenario1Root,complexTypeStatisticsEntryScenario1Root);

		complexTypeStatisticsEntryScenario1Element1 = new ComplexTypeStatisticsEntry(8);
		complexTypeStatisticsEntryScenario1Element1.getElementInfo().put(elementScenario1Element1, elementScenario1Element1StatisticsEntry);
		complexTypeStatisticsEntryScenario1Element1.getSubpatternsInfo().put(Lists.newArrayList(elementScenario1Element1A,elementScenario1Element1C), 4);
		complexTypeStatisticsEntryScenario1Element1.getSubpatternsInfo().put(Lists.newArrayList(elementScenario1Element1B,elementScenario1Element1C), 4);
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfoElement1 = complexTypeStatisticsEntryScenario1Element1.getAttributeOccurrencesInfo();
		attributeOccurrencesInfoElement1 .put(attributeScenario1Element1Attr1,attributeScenario1Element1Attr1StatisticsEntry);
		attributeOccurrencesInfoElement1.put(attributeScenario1Element1Attr2,attributeScenario1Element1Attr2StatisticsEntry);
		attributeOccurrencesInfoElement1.put(attributeScenario1Element1Attr3,attributeScenario1Element1Attr3StatisticsEntry);
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoElement1 = complexTypeStatisticsEntryScenario1Element1.getValuesInfo();
		valuesInfoElement1.put("alfa",attributeScenario1Element1Attr1,attributeScenario1Element1Attr1ValueAlfaStatisticsEntry);
		valuesInfoElement1.put("beta",attributeScenario1Element1Attr1,attributeScenario1Element1Attr1ValueBetaStatisticsEntry);
		valuesInfoElement1.put("1",attributeScenario1Element1Attr2,attributeScenario1Element1Attr2Value1StatisticsEntry);
		valuesInfoElement1.put("2",attributeScenario1Element1Attr2,attributeScenario1Element1Attr2Value2StatisticsEntry);
		valuesInfoElement1.put("true",attributeScenario1Element1Attr3,attributeScenario1Element1Attr3ValueTrueStatisticsEntry);
		valuesInfoElement1.put("false",attributeScenario1Element1Attr3,attributeScenario1Element1Attr3ValueFalseStatisticsEntry);
		valuesInfoElement1.put("0",attributeScenario1Element1Attr3,attributeScenario1Element1Attr3Value0StatisticsEntry);
		valuesInfoElement1.put("1",attributeScenario1Element1Attr3,attributeScenario1Element1Attr3Value1StatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1Element1,complexTypeStatisticsEntryScenario1Element1);
		
		complexTypeStatisticsEntryScenario1Element1NodeA = new ComplexTypeStatisticsEntry(8);
		complexTypeStatisticsEntryScenario1Element1NodeA.getElementInfo().put(elementScenario1Element1A, elementScenario1Element1NodeAStatisticsEntry);
		
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoElement1NodeA = complexTypeStatisticsEntryScenario1Element1NodeA.getValuesInfo();
		valuesInfoElement1NodeA.put("cuarenta",elementScenario1Element1A,elementScenario1Element1NodeAValueCuarentaStatisticsEntry);
		valuesInfoElement1NodeA.put("50",elementScenario1Element1A,elementScenario1Element1NodeAValue50StatisticsEntry);
		valuesInfoElement1NodeA.put("60",elementScenario1Element1A,elementScenario1Element1NodeAValue60StatisticsEntry);
		valuesInfoElement1NodeA.put("70",elementScenario1Element1A,elementScenario1Element1NodeAValue70StatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1Element1NodeA,complexTypeStatisticsEntryScenario1Element1NodeA);

		complexTypeStatisticsEntryScenario1Element1NodeB = new ComplexTypeStatisticsEntry(8);
		complexTypeStatisticsEntryScenario1Element1NodeB.getElementInfo().put(elementScenario1Element1B, elementScenario1Element1NodeBStatisticsEntry);
		
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoElement1NodeB = complexTypeStatisticsEntryScenario1Element1NodeB.getValuesInfo();
		valuesInfoElement1NodeB.put("hola",elementScenario1Element1A,elementScenario1Element1NodeBValueHolaStatisticsEntry);
		valuesInfoElement1NodeB.put("saludos",elementScenario1Element1A,elementScenario1Element1NodeBValueSaludosStatisticsEntry);
		valuesInfoElement1NodeB.put("adios",elementScenario1Element1A,elementScenario1Element1NodeBValueAdiosStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1Element1NodeB,complexTypeStatisticsEntryScenario1Element1NodeB);
		
		complexTypeStatisticsEntryScenario1Element1NodeC = new ComplexTypeStatisticsEntry(8);
		complexTypeStatisticsEntryScenario1Element1NodeC.getSubpatternsInfo().put(Lists.newArrayList(elementScenario1Element1CE), 8);
		complexTypeStatisticsEntryScenario1Element1NodeC.getElementInfo().put(elementScenario1Element1C, elementScenario1Element1NodeCStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1Element1NodeC,complexTypeStatisticsEntryScenario1Element1NodeC);

		complexTypeStatisticsEntryScenario1Element1NodeCNodeE = new ComplexTypeStatisticsEntry(8);
		complexTypeStatisticsEntryScenario1Element1NodeCNodeE.getElementInfo().put(elementScenario1Element1CE, elementScenario1Element1NodeCNodeEStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1Element1NodeCNodeE,complexTypeStatisticsEntryScenario1Element1NodeCNodeE);

		complexTypeStatisticsEntryScenario1Element2 = new ComplexTypeStatisticsEntry(8);
		complexTypeStatisticsEntryScenario1Element2.getSubpatternsInfo().put(Lists.newArrayList(elementScenario1Element2A,elementScenario1Element2B,elementScenario1Element2C,elementScenario1Element2C,elementScenario1Element2D), 5);
		complexTypeStatisticsEntryScenario1Element2.getSubpatternsInfo().put(Lists.newArrayList(elementScenario1Element2A,elementScenario1Element2B,elementScenario1Element2C,elementScenario1Element2D), 3);
		complexTypeStatisticsEntryScenario1Element2.getElementInfo().put(elementScenario1Element2, elementScenario1Element2StatisticsEntry);
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfoElement2 = complexTypeStatisticsEntryScenario1Element2.getAttributeOccurrencesInfo();
		attributeOccurrencesInfoElement2.put(attributeScenario1Element2Attr1,attributeScenario1Element2Attr1StatisticsEntry);
		attributeOccurrencesInfoElement2.put(attributeScenario1Element2Attr2,attributeScenario1Element2Attr2StatisticsEntry);
		attributeOccurrencesInfoElement2.put(attributeScenario1Element2Attr4,attributeScenario1Element2Attr4StatisticsEntry);
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoElement2 = complexTypeStatisticsEntryScenario1Element2.getValuesInfo();
		valuesInfoElement2.put("lambda",attributeScenario1Element2Attr1,attributeScenario1Element2Attr1ValueLambdaStatisticsEntry);
		valuesInfoElement2.put("omega",attributeScenario1Element2Attr1,attributeScenario1Element2Attr1ValueOmegaStatisticsEntry);
		valuesInfoElement2.put("3",attributeScenario1Element2Attr2,attributeScenario1Element2Attr2Value3StatisticsEntry);
		valuesInfoElement2.put("4",attributeScenario1Element2Attr2,attributeScenario1Element2Attr2Value4StatisticsEntry);
		valuesInfoElement2.put("gamma",attributeScenario1Element2Attr4,attributeScenario1Element2Attr4ValueGammaStatisticsEntry);
		valuesInfoElement2.put("epsilon",attributeScenario1Element2Attr4,attributeScenario1Element2Attr4ValueEpsilonStatisticsEntry);
		valuesInfoElement2.put("omicron",attributeScenario1Element2Attr4,attributeScenario1Element2Attr4ValueOmicronStatisticsEntry);
		valuesInfoElement2.put("zeta",attributeScenario1Element2Attr4,attributeScenario1Element2Attr4ValueZetaStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1Element2,complexTypeStatisticsEntryScenario1Element2);

		complexTypeStatisticsEntryScenario1Element2NodeA = new ComplexTypeStatisticsEntry(8);
		complexTypeStatisticsEntryScenario1Element2NodeA.getElementInfo().put(elementScenario1Element2A, elementScenario1Element2NodeAStatisticsEntry);
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoElement2NodeA = complexTypeStatisticsEntryScenario1Element2NodeA.getValuesInfo();
		valuesInfoElement2NodeA.put("40",elementScenario1Element2A,elementScenario1Element2NodeAValue40StatisticsEntry);
		valuesInfoElement2NodeA.put("50",elementScenario1Element2A,elementScenario1Element2NodeAValue50StatisticsEntry);
		valuesInfoElement2NodeA.put("60",elementScenario1Element2A,elementScenario1Element2NodeAValue60StatisticsEntry);
		valuesInfoElement2NodeA.put("70",elementScenario1Element2A,elementScenario1Element2NodeAValue70StatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1Element2NodeA,complexTypeStatisticsEntryScenario1Element2NodeA);

		complexTypeStatisticsEntryScenario1Element2NodeB = new ComplexTypeStatisticsEntry(8);
		complexTypeStatisticsEntryScenario1Element2NodeB.getElementInfo().put(elementScenario1Element2B, elementScenario1Element2NodeBStatisticsEntry);
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfoElement2NodeB = complexTypeStatisticsEntryScenario1Element2NodeB.getValuesInfo();
		valuesInfoElement2NodeB.put("buenos dias",elementScenario1Element2A,elementScenario1Element2NodeBValueBuenosDiasStatisticsEntry);
		valuesInfoElement2NodeB.put("saludos",elementScenario1Element2A,elementScenario1Element2NodeBValueSaludosStatisticsEntry);
		valuesInfoElement2NodeB.put("adios",elementScenario1Element2A,elementScenario1Element2NodeBValueAdiosStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1Element2NodeB,complexTypeStatisticsEntryScenario1Element2NodeB);

		complexTypeStatisticsEntryScenario1Element2NodeC = new ComplexTypeStatisticsEntry(8);
		complexTypeStatisticsEntryScenario1Element2NodeC.getElementInfo().put(elementScenario1Element2C, elementScenario1Element2NodeCStatisticsEntry);
		complexTypeStatisticsEntryScenario1Element2NodeC.getSubpatternsInfo().put(Lists.newArrayList(elementScenario1Element2CF), 13);
		complexTypeInfo.put(complexTypeScenario1Element2NodeC,complexTypeStatisticsEntryScenario1Element2NodeC);

		complexTypeStatisticsEntryScenario1Element2NodeCNodeF = new ComplexTypeStatisticsEntry(8);
		complexTypeStatisticsEntryScenario1Element2NodeCNodeF.getElementInfo().put(elementScenario1Element2CF, elementScenario1Element2NodeCNodeFStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1Element2NodeCNodeF,complexTypeStatisticsEntryScenario1Element2NodeCNodeF);

		complexTypeStatisticsEntryScenario1Element2NodeD = new ComplexTypeStatisticsEntry(8);
		complexTypeStatisticsEntryScenario1Element2NodeD.getElementInfo().put(elementScenario1Element2D, elementScenario1Element2NodeDStatisticsEntry);
		complexTypeInfo.put(complexTypeScenario1Element2NodeD,complexTypeStatisticsEntryScenario1Element2NodeD);
		
		
	}
	
	/**
	 * This method registers all the info about root element occurrences, widths and depths of the scenario 1
	 */
	private void registerRootOccurrencesWidthsDepthsScenario1() {
		for(int documentIndex=0;documentIndex<8;documentIndex++){
			statistics1.registerRootElementOccurrence(elementScenario1Root);
	
			// /root
			statistics1.registerWidth(documentIndex, 1);
			statistics1.registerDepth(documentIndex, 1);
			// /root/element1
			statistics1.registerWidth(documentIndex, 2);
			statistics1.registerDepth(documentIndex, 2);
			// /root/element1/a
			statistics1.registerDepth(documentIndex, 3);
			// /root/element1/b
			statistics1.registerDepth(documentIndex, 3);
			// /root/element1/c
			statistics1.registerWidth(documentIndex, 1);
			statistics1.registerDepth(documentIndex, 3);
			// /root/element1/c/e
			statistics1.registerDepth(documentIndex, 4);
			// /root/element2
			if(documentIndex<5){
				statistics1.registerWidth(documentIndex, 5);
			}
			else {
				statistics1.registerWidth(documentIndex, 4);
			}
			statistics1.registerDepth(documentIndex, 2);
			// /root/element2/a
			statistics1.registerDepth(documentIndex, 3);
			// /root/element2/b
			statistics1.registerDepth(documentIndex, 3);
			// /root/element2/c
			statistics1.registerWidth(documentIndex, 1);
			statistics1.registerDepth(documentIndex, 3);
			if(documentIndex<5){
				statistics1.registerWidth(documentIndex, 1);
				statistics1.registerDepth(documentIndex, 3);
			}
			
			// /root/element2/c/f
			statistics1.registerDepth(documentIndex, 4);
			if(documentIndex<5){
				statistics1.registerDepth(documentIndex, 4);
			}
			// /root/element2/d
			statistics1.registerDepth(documentIndex, 3);
		}
	}

	/**
	 * This method builds the statistics of the testing scenario 1
	 */
	private void buildStatisticsScenario1(){
		statistics1 = new Statistics(8);
		
		buildBasicStatisticsEntriesScenario1();
		buildAttributeAtPathInfoScenario1();
		buildElementAtPathInfo();
		buildValuesAtPathInfo();
		buildComplexTypesStatisticInfoScenario1();
		registerRootOccurrencesWidthsDepthsScenario1();
		
	}

	/**
	 * It builds the testing scenario 1
	 */
	private void buildScenario1() {
		buildSimpleTypesScenario1();
	
		buildSchemaAttributesScenario1();
	
		buildComplexTypesScenario1();
	
		buildSchemaElementsScenario1();
	
		buildAutomatonsScenario1();
	
		buildRegularExpressionsScenario1();
		
		buildStatisticsScenario1();
	
		buildSchemaScenario1();
		
		configuration1 = mock(XSDInferenceConfiguration.class);
		when(configuration1.getTypeNamesAncestorsSeparator()).thenReturn("-");
		when(configuration1.getMergedTypesSeparator()).thenReturn("_and_");
		
		when(configuration1.getGenerateEnumerations()).thenReturn(true);
		
		when(configuration1.getSimpleTypeInferencer()).thenReturn(XSDInferenceConfiguration.VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL);
		when(configuration1.getAttributeListInferencer()).thenReturn(XSDInferenceConfiguration.VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL);
		when(configuration1.getMaxNumberOfDistinctValuesToEnum()).thenReturn(20);
		when(configuration1.getMinNumberOfDistinctValuesToEnum()).thenReturn(3);
	}
	
	@Before
	public void setUp() throws Exception {
		initialState=new SchemaElement("initial",DEFAULT_PSEUDOELEMENTS_NAMESPACE,null);
		finalState=new SchemaElement("final",DEFAULT_PSEUDOELEMENTS_NAMESPACE,null);
		
		buildScenario1();
	}

	/**
	 * This method runs the statistics generator with a quite complex Statistics objects as input and checks that the output is correct 
	 */
	@Test
	public void testNormal() {
		StatisticResultsDocGeneratorFactory statisticResultsDocGeneratorFactory = StatisticResultsDocGeneratorFactory.getInstance();
		StatisticResultsDocGenerator statisticResultsDocGenerator = statisticResultsDocGeneratorFactory.getStatisticResultsDocGeneratorInstance();
		Document statisticsDocument = statisticResultsDocGenerator.generateStatisticResultsDoc(statistics1);
		Format format = Format.getRawFormat();
		format.setLineSeparator(LineSeparator.SYSTEM);
		XMLOutputter outputter = new XMLOutputter(format);
		String statisticsOutputString = outputter.outputString(statisticsDocument);
		String lineSeparator = System.getProperty("line.separator");
		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+ lineSeparator +
				"<statistics xmlns=\"http://www.dit.upm.es/xsdinferencer/statistics\"><generalStatistics><depth><max>4</max><avg>2.893</avg></depth><width><max>5</max><avg>1.821</avg></width></generalStatistics><rootElementsInfo><rootElement name=\"root\" namespace=\"\" occurrences=\"8\" /></rootElementsInfo><elementsAtPathOccurrences><element path=\"/root\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\" /><element path=\"/root/element1\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\" /><element path=\"/root/element1/a\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><valuesAtPath><valueAtPath path=\"/root/element1/a\" average=\"0.125\" max=\"1.0\" frequencyOfMax=\"1\" min=\"0.0\" frequencyOfMin=\"7\" variance=\"0.109\" standardDeviationAverageRatio=\"2.646\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.125\" total=\"1.0\"><value xml:space=\"preserve\">60</value></valueAtPath><valueAtPath path=\"/root/element1/a\" average=\"0.125\" max=\"1.0\" frequencyOfMax=\"1\" min=\"0.0\" frequencyOfMin=\"7\" variance=\"0.109\" standardDeviationAverageRatio=\"2.646\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.125\" total=\"1.0\"><value xml:space=\"preserve\">70</value></valueAtPath><valueAtPath path=\"/root/element1/a\" average=\"0.125\" max=\"1.0\" frequencyOfMax=\"1\" min=\"0.0\" frequencyOfMin=\"7\" variance=\"0.109\" standardDeviationAverageRatio=\"2.646\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.125\" total=\"1.0\"><value xml:space=\"preserve\">cuarenta</value></valueAtPath><valueAtPath path=\"/root/element1/a\" average=\"0.125\" max=\"1.0\" frequencyOfMax=\"1\" min=\"0.0\" frequencyOfMin=\"7\" variance=\"0.109\" standardDeviationAverageRatio=\"2.646\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.125\" total=\"1.0\"><value xml:space=\"preserve\">50</value></valueAtPath></valuesAtPath></element><element path=\"/root/element1/b\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><valuesAtPath><valueAtPath path=\"/root/element1/b\" average=\"0.125\" max=\"1.0\" frequencyOfMax=\"1\" min=\"0.0\" frequencyOfMin=\"7\" variance=\"0.109\" standardDeviationAverageRatio=\"2.646\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.125\" total=\"1.0\"><value xml:space=\"preserve\">hola</value></valueAtPath><valueAtPath path=\"/root/element1/b\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">saludos</value></valueAtPath><valueAtPath path=\"/root/element1/b\" average=\"0.125\" max=\"1.0\" frequencyOfMax=\"1\" min=\"0.0\" frequencyOfMin=\"7\" variance=\"0.109\" standardDeviationAverageRatio=\"2.646\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.125\" total=\"1.0\"><value xml:space=\"preserve\">adios</value></valueAtPath></valuesAtPath></element><element path=\"/root/element1/c\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\" /><element path=\"/root/element1/c/e\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\" /><element path=\"/root/element2\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\" /><element path=\"/root/element2/a\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\"><valuesAtPath><valueAtPath path=\"/root/element2/a\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">60</value></valueAtPath><valueAtPath path=\"/root/element2/a\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">70</value></valueAtPath><valueAtPath path=\"/root/element2/a\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">40</value></valueAtPath><valueAtPath path=\"/root/element2/a\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">50</value></valueAtPath></valuesAtPath><numericValuesStatistics average=\"55.0\" max=\"70.0\" frequencyOfMax=\"2\" min=\"40.0\" frequencyOfMin=\"2\" variance=\"125.0\" standardDeviationAverageRatio=\"0.203\" total=\"440.0\" /></element><element path=\"/root/element2/b\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\"><valuesAtPath><valueAtPath path=\"/root/element2/b\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">saludos</value></valueAtPath><valueAtPath path=\"/root/element2/b\" average=\"0.375\" max=\"1.0\" frequencyOfMax=\"3\" min=\"0.0\" frequencyOfMin=\"5\" variance=\"0.234\" standardDeviationAverageRatio=\"1.291\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.375\" total=\"3.0\"><value xml:space=\"preserve\">adios</value></valueAtPath><valueAtPath path=\"/root/element2/b\" average=\"0.375\" max=\"1.0\" frequencyOfMax=\"3\" min=\"0.0\" frequencyOfMin=\"5\" variance=\"0.234\" standardDeviationAverageRatio=\"1.291\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.375\" total=\"3.0\"><value xml:space=\"preserve\">buenos dias</value></valueAtPath></valuesAtPath></element><element path=\"/root/element2/c\" average=\"1.625\" max=\"2.0\" frequencyOfMax=\"5\" min=\"1.0\" frequencyOfMin=\"3\" variance=\"0.234\" standardDeviationAverageRatio=\"0.298\" conditionedAverage=\"1.625\" conditionedVariance=\"0.234\" conditionedStandardDeviationAverageRatio=\"0.298\" presenceRatio=\"1.0\" total=\"13.0\" /><element path=\"/root/element2/c/f\" average=\"1.625\" max=\"2.0\" frequencyOfMax=\"5\" min=\"1.0\" frequencyOfMin=\"3\" variance=\"0.234\" standardDeviationAverageRatio=\"0.298\" conditionedAverage=\"1.625\" conditionedVariance=\"0.234\" conditionedStandardDeviationAverageRatio=\"0.298\" presenceRatio=\"1.0\" total=\"13.0\" /><element path=\"/root/element2/d\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\" /></elementsAtPathOccurrences><attributesAtPathOccurrences><attribute path=\"/root/element1/@attr1\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\"><valuesAtPath><valueAtPath path=\"/root/element1/@attr1\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">beta</value></valueAtPath><valueAtPath path=\"/root/element1/@attr1\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">alfa</value></valueAtPath></valuesAtPath></attribute><attribute path=\"/root/element1/@attr2\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\"><valuesAtPath><valueAtPath path=\"/root/element1/@attr2\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">1</value></valueAtPath><valueAtPath path=\"/root/element1/@attr2\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">2</value></valueAtPath></valuesAtPath><numericValuesStatistics average=\"1.5\" max=\"2.0\" frequencyOfMax=\"4\" min=\"1.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"0.333\" total=\"12.0\" /></attribute><attribute path=\"/root/element1/@attr3\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\"><valuesAtPath><valueAtPath path=\"/root/element1/@attr3\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">true</value></valueAtPath><valueAtPath path=\"/root/element1/@attr3\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">false</value></valueAtPath><valueAtPath path=\"/root/element1/@attr3\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">1</value></valueAtPath><valueAtPath path=\"/root/element1/@attr3\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">0</value></valueAtPath></valuesAtPath></attribute><attribute path=\"/root/element2/@attr1\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\"><valuesAtPath><valueAtPath path=\"/root/element2/@attr1\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">omega</value></valueAtPath><valueAtPath path=\"/root/element2/@attr1\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">lambda</value></valueAtPath></valuesAtPath></attribute><attribute path=\"/root/element2/@attr2\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><valuesAtPath><valueAtPath path=\"/root/element2/@attr2\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">3</value></valueAtPath><valueAtPath path=\"/root/element2/@attr2\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">4</value></valueAtPath></valuesAtPath><numericValuesStatistics average=\"3.5\" max=\"4.0\" frequencyOfMax=\"4\" min=\"3.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"0.143\" total=\"28.0\" /></attribute><attribute path=\"/root/element2/@attr4\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><valuesAtPath><valueAtPath path=\"/root/element2/@attr4\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">epsilon</value></valueAtPath><valueAtPath path=\"/root/element2/@attr4\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">zeta</value></valueAtPath><valueAtPath path=\"/root/element2/@attr4\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">gamma</value></valueAtPath><valueAtPath path=\"/root/element2/@attr4\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">omicron</value></valueAtPath></valuesAtPath></attribute></attributesAtPathOccurrences><complexTypesInfo><complexType name=\"_root-_element2-_d\"><elements><element name=\"d\" namespace=\"\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\" /></elements><attributes /><subpatternsInfo /></complexType><complexType name=\"_root\"><elements><element name=\"root\" namespace=\"\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\" /></elements><attributes /><subpatternsInfo><occurrences>8</occurrences><subpatternElements><subpatternElement name=\"element1\" namespace=\"\" /><subpatternElement name=\"element2\" namespace=\"\" /></subpatternElements></subpatternsInfo></complexType><complexType name=\"_root-_element1-_a\"><elements><element name=\"a\" namespace=\"\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><values><value name=\"a\" namespace=\"\" average=\"0.125\" max=\"1.0\" frequencyOfMax=\"1\" min=\"0.0\" frequencyOfMin=\"7\" variance=\"0.109\" standardDeviationAverageRatio=\"2.646\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.125\" total=\"1.0\"><value xml:space=\"preserve\">70</value></value><value name=\"a\" namespace=\"\" average=\"0.125\" max=\"1.0\" frequencyOfMax=\"1\" min=\"0.0\" frequencyOfMin=\"7\" variance=\"0.109\" standardDeviationAverageRatio=\"2.646\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.125\" total=\"1.0\"><value xml:space=\"preserve\">cuarenta</value></value><value name=\"a\" namespace=\"\" average=\"0.125\" max=\"1.0\" frequencyOfMax=\"1\" min=\"0.0\" frequencyOfMin=\"7\" variance=\"0.109\" standardDeviationAverageRatio=\"2.646\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.125\" total=\"1.0\"><value xml:space=\"preserve\">60</value></value><value name=\"a\" namespace=\"\" average=\"0.125\" max=\"1.0\" frequencyOfMax=\"1\" min=\"0.0\" frequencyOfMin=\"7\" variance=\"0.109\" standardDeviationAverageRatio=\"2.646\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.125\" total=\"1.0\"><value xml:space=\"preserve\">50</value></value></values></element></elements><attributes /><subpatternsInfo /></complexType><complexType name=\"_root-_element1-_b\"><elements><element name=\"b\" namespace=\"\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\" /></elements><attributes /><subpatternsInfo /></complexType><complexType name=\"_root-_element2\"><elements><element name=\"element2\" namespace=\"\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\" /></elements><attributes><attribute name=\"attr1\" namespace=\"\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\"><values><value name=\"attr1\" namespace=\"\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">lambda</value></value><value name=\"attr1\" namespace=\"\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">omega</value></value></values></attribute><attribute name=\"attr2\" namespace=\"\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><values><value name=\"attr2\" namespace=\"\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">3</value></value><value name=\"attr2\" namespace=\"\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">4</value></value></values><numericValuesStatistics average=\"3.5\" max=\"4.0\" frequencyOfMax=\"4\" min=\"3.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"0.143\" total=\"28.0\" /></attribute><attribute name=\"attr4\" namespace=\"\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><values><value name=\"attr4\" namespace=\"\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">gamma</value></value><value name=\"attr4\" namespace=\"\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">epsilon</value></value><value name=\"attr4\" namespace=\"\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">zeta</value></value><value name=\"attr4\" namespace=\"\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">omicron</value></value></values></attribute></attributes><subpatternsInfo><occurrences>5</occurrences><subpatternElements><subpatternElement name=\"a\" namespace=\"\" /><subpatternElement name=\"b\" namespace=\"\" /><subpatternElement name=\"c\" namespace=\"\" /><subpatternElement name=\"c\" namespace=\"\" /><subpatternElement name=\"d\" namespace=\"\" /></subpatternElements><occurrences>3</occurrences><subpatternElements><subpatternElement name=\"a\" namespace=\"\" /><subpatternElement name=\"b\" namespace=\"\" /><subpatternElement name=\"c\" namespace=\"\" /><subpatternElement name=\"d\" namespace=\"\" /></subpatternElements></subpatternsInfo></complexType><complexType name=\"_root-_element1\"><elements><element name=\"element1\" namespace=\"\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\" /></elements><attributes><attribute name=\"attr1\" namespace=\"\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\"><values><value name=\"attr1\" namespace=\"\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">alfa</value></value><value name=\"attr1\" namespace=\"\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">beta</value></value></values></attribute><attribute name=\"attr2\" namespace=\"\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\"><values><value name=\"attr2\" namespace=\"\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">2</value></value><value name=\"attr2\" namespace=\"\" average=\"0.5\" max=\"1.0\" frequencyOfMax=\"4\" min=\"0.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"1.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.5\" total=\"4.0\"><value xml:space=\"preserve\">1</value></value></values><numericValuesStatistics average=\"1.5\" max=\"2.0\" frequencyOfMax=\"4\" min=\"1.0\" frequencyOfMin=\"4\" variance=\"0.25\" standardDeviationAverageRatio=\"0.333\" total=\"12.0\" /></attribute><attribute name=\"attr3\" namespace=\"\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\"><values><value name=\"attr3\" namespace=\"\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">1</value></value><value name=\"attr3\" namespace=\"\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">0</value></value><value name=\"attr3\" namespace=\"\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">false</value></value><value name=\"attr3\" namespace=\"\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">true</value></value></values></attribute></attributes><subpatternsInfo><occurrences>4</occurrences><subpatternElements><subpatternElement name=\"a\" namespace=\"\" /><subpatternElement name=\"c\" namespace=\"\" /></subpatternElements><occurrences>4</occurrences><subpatternElements><subpatternElement name=\"b\" namespace=\"\" /><subpatternElement name=\"c\" namespace=\"\" /></subpatternElements></subpatternsInfo></complexType><complexType name=\"_root-_element1-_c\"><elements><element name=\"c\" namespace=\"\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\" /></elements><attributes /><subpatternsInfo><occurrences>8</occurrences><subpatternElements><subpatternElement name=\"e\" namespace=\"\" /></subpatternElements></subpatternsInfo></complexType><complexType name=\"_root-_element1-_c-_e\"><elements><element name=\"e\" namespace=\"\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\" /></elements><attributes /><subpatternsInfo /></complexType><complexType name=\"_root-_element2-_a\"><elements><element name=\"a\" namespace=\"\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\"><values><value name=\"a\" namespace=\"\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">40</value></value><value name=\"a\" namespace=\"\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">70</value></value><value name=\"a\" namespace=\"\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">60</value></value><value name=\"a\" namespace=\"\" average=\"0.25\" max=\"1.0\" frequencyOfMax=\"2\" min=\"0.0\" frequencyOfMin=\"6\" variance=\"0.188\" standardDeviationAverageRatio=\"1.732\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"0.25\" total=\"2.0\"><value xml:space=\"preserve\">50</value></value></values><numericValuesStatistics average=\"55.0\" max=\"70.0\" frequencyOfMax=\"2\" min=\"40.0\" frequencyOfMin=\"2\" variance=\"125.0\" standardDeviationAverageRatio=\"0.203\" total=\"440.0\" /></element></elements><attributes /><subpatternsInfo /></complexType><complexType name=\"_root-_element2-_b\"><elements><element name=\"b\" namespace=\"\" average=\"1.0\" max=\"1.0\" frequencyOfMax=\"8\" min=\"1.0\" frequencyOfMin=\"8\" variance=\"0.0\" standardDeviationAverageRatio=\"0.0\" conditionedAverage=\"1.0\" conditionedVariance=\"0.0\" conditionedStandardDeviationAverageRatio=\"0.0\" presenceRatio=\"1.0\" total=\"8.0\" /></elements><attributes /><subpatternsInfo /></complexType><complexType name=\"_root-_element2-_c-_f\"><elements><element name=\"f\" namespace=\"\" average=\"1.625\" max=\"2.0\" frequencyOfMax=\"5\" min=\"1.0\" frequencyOfMin=\"3\" variance=\"0.234\" standardDeviationAverageRatio=\"0.298\" conditionedAverage=\"1.625\" conditionedVariance=\"0.234\" conditionedStandardDeviationAverageRatio=\"0.298\" presenceRatio=\"1.0\" total=\"13.0\" /></elements><attributes /><subpatternsInfo /></complexType><complexType name=\"_root-_element2-_c\"><elements><element name=\"c\" namespace=\"\" average=\"1.625\" max=\"2.0\" frequencyOfMax=\"5\" min=\"1.0\" frequencyOfMin=\"3\" variance=\"0.234\" standardDeviationAverageRatio=\"0.298\" conditionedAverage=\"1.625\" conditionedVariance=\"0.234\" conditionedStandardDeviationAverageRatio=\"0.298\" presenceRatio=\"1.0\" total=\"13.0\" /></elements><attributes /><subpatternsInfo><occurrences>13</occurrences><subpatternElements><subpatternElement name=\"f\" namespace=\"\" /></subpatternElements></subpatternsInfo></complexType></complexTypesInfo></statistics>"+lineSeparator;
//		for(int i=0;i<expectedOutput.length();i++){
//			if(expectedOutput.charAt(i)!=xsdOutputString.charAt(i))
//				System.err.println("Difference at index "+i+". Expected char is '"+expectedOutput.charAt(i)+"' but found is '"+xsdOutputString.charAt(i)+"'");
//		}
//		System.out.println(statisticsOutputString);
		assertEquals(expectedOutput, statisticsOutputString);
	}
	
	/**
	 * This method checks that an empty {@link Statistics} object makes the generator return the correct result.
	 */
	@Test
	public void testEmpty(){
		StatisticResultsDocGeneratorFactory statisticResultsDocGeneratorFactory = StatisticResultsDocGeneratorFactory.getInstance();
		StatisticResultsDocGenerator statisticResultsDocGenerator = statisticResultsDocGeneratorFactory.getStatisticResultsDocGeneratorInstance();
		Statistics statistics = new Statistics(1);
		Document statisticsDocument = statisticResultsDocGenerator.generateStatisticResultsDoc(statistics );
		Format format = Format.getRawFormat();
		format.setLineSeparator(LineSeparator.SYSTEM);
		XMLOutputter outputter = new XMLOutputter(format);
		String statisticsOutputString = outputter.outputString(statisticsDocument);
		String lineSeparator = System.getProperty("line.separator");
		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+ lineSeparator +
				"<statistics xmlns=\"http://www.dit.upm.es/xsdinferencer/statistics\"><generalStatistics><depth><max>0</max><avg>0.0</avg></depth><width><max>0</max><avg>0.0</avg></width></generalStatistics><rootElementsInfo /><elementsAtPathOccurrences /><attributesAtPathOccurrences /><complexTypesInfo /></statistics>"+lineSeparator;
//		for(int i=0;i<expectedOutput.length();i++){
//			if(expectedOutput.charAt(i)!=xsdOutputString.charAt(i))
//				System.err.println("Difference at index "+i+". Expected char is '"+expectedOutput.charAt(i)+"' but found is '"+xsdOutputString.charAt(i)+"'");
//		}
//		System.out.println(statisticsOutputString);
		assertEquals(expectedOutput, statisticsOutputString);
	}

}
