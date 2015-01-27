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
package es.upm.dit.xsdinferencer.tests.generation.generatorimpl.xsdgeneration;

import static es.upm.dit.xsdinferencer.datastructures.Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

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
import es.upm.dit.xsdinferencer.datastructures.Sequence;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.generation.SchemaDocumentGenerator;
import es.upm.dit.xsdinferencer.generation.generatorimpl.schemageneration.SchemaDocumentGeneratorFactory;
import es.upm.dit.xsdinferencer.merge.AttributeListComparator;
import es.upm.dit.xsdinferencer.merge.ChildrenPatternComparator;
import es.upm.dit.xsdinferencer.merge.EnumComparator;
import es.upm.dit.xsdinferencer.statistics.Statistics;
import es.upm.dit.xsdinferencer.util.xsdfilenaming.XSDFileNameGenerator;
import es.upm.dit.xsdinferencer.util.xsdfilenaming.XSDFileNameGeneratorDefaultImpl;
//The scenario is a variation of the one on TypeMergerImplTests. The only difference is that the original 
//choice between the main element1 and element2 is a Sequence, so both must appear instead of one and only one.

/**
 * Test class for {@link XMLSchemaDocumentGenerator}
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class XMLSchemaDocumentGeneratorTest {
	// Fields for testing

	/**
	 * Namespace URI for the namespace 'testing'
	 */
	private static final String NAMESPACE_URI_TESTING = "http://probando.net";

	/**
	 * Namespace URI for the namespace 'test'
	 */
	private static final String NAMESPACE_URI_TEST = "http://prueba.net";

	// Pseudoelements

	/**
	 * Initial state of automatons
	 */
	private SchemaElement initialState;
	/**
	 * Final state of automatons
	 */
	private SchemaElement finalState;

	// Scenario 1

	/**
	 * Complex type of the root of the documents on scenario 1. It only allows
	 * either a child called element1 whose type is complexTypeElement1 or a
	 * child called element2 whose type is complexTypeElement2.
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
	 * Complex type of node C in the automaton of complexTypeScenario1Element1
	 * on scenario 1
	 */
	private ComplexType complexTypeScenario1Element1NodeC;

	/**
	 * Complex type of node E in the automaton of
	 * complexTypeScenario1Element1NodeC
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
	 * Complex type of node F in the automaton of
	 * complexTypeScenario1Element2NodeC
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
	 * The testing schema, which we will use to generate XSDs on scenario 1
	 */
	private Schema schema1;

	/**
	 * Inference configuration of scenario 1
	 */
	private XSDInferenceConfiguration configuration1;

	// Scenario 2

	/**
	 * Complex type of the element test:root of the scenario 2
	 */
	private ComplexType complexTypeScenario2TestRoot;

	/**
	 * Complex type of the element testing:raiz of the scenario 2
	 */
	private ComplexType complexTypeScenario2TestingRaiz;

	/**
	 * Element test:root of the scenario 2
	 */
	private SchemaElement elementScenario2TestRoot;

	/**
	 * Element testing:raiz of the scenario 2
	 */
	private SchemaElement elementScenario2TestingRaiz;

	/**
	 * The testing schema, which we will use to generate XSDs on scenario 2
	 */
	private Schema schema2;

	/**
	 * Inference configuration of scenario 2
	 */
	private XSDInferenceConfiguration configuration2;

	// Other fields

	/**
	 * Mock file generator
	 */
	private XSDFileNameGenerator xsdFileNameGenerator;

	/**
	 * Mock comparator that always returns false.
	 */
	private ChildrenPatternComparator alwaysFalseChildrenPatternComparator;

	/**
	 * Mock comparator that always returns false.
	 */
	private AttributeListComparator alwaysFalseAttributeListComparator;

	/**
	 * Mock comparator that always returns false.
	 */
	private EnumComparator alwaysFalseEnumComparator;

	/**
	 * The empty regular expression
	 */
	private final RegularExpression emptyRegexp = new EmptyRegularExpression();

	/**
	 * Element comparator to sort generated documents in order to verify them
	 */
	private Comparator<Element> elementComparator = new Comparator<Element>() {

		@Override
		public int compare(Element element1, Element element2) {
			if (!(element1.getName().equals(element2.getName()))) {
				return 0;
			} else if (!element1.getName().equals("element")
					&& element1.getAttribute("name") != null
					&& element2.getAttribute("name") != null) {
				return element1.getAttributeValue("name").compareTo(
						element2.getAttributeValue("name"));
			} else if (element1.getAttribute("value") != null
					&& element2.getAttribute("value") != null) {
				return element1.getAttributeValue("value").compareTo(
						element2.getAttributeValue("value"));
			}
			return 0;
		}

	};

	/**
	 * It builds the non-trivial SimpleType objects used at scenario 1
	 */
	private void buildSimpleTypesScenario1() {
		List<String> simpleTypeElement1NodeAValues = Lists.newArrayList(
				"cuarenta", "50", "60", "70");
		simpleTypeScenario1Element1NodeA = new SimpleType("_root-_element1-_a",
				"xs:string", simpleTypeElement1NodeAValues, true);
		List<String> simpleTypeElement1NodeBValues = Lists.newArrayList("hola",
				"saludos", "hasta luego");
		simpleTypeScenario1Element1NodeB = new SimpleType("_root-_element1-_b",
				"xs:string", simpleTypeElement1NodeBValues, true);
		List<String> simpleTypeElement2NodeAValues = Lists.newArrayList("40",
				"50", "60", "70");
		simpleTypeScenario1Element2NodeA = new SimpleType("_root-_element2-_a",
				"xs:integer", simpleTypeElement2NodeAValues, true);
		List<String> simpleTypeElement2NodeBValues = Lists.newArrayList(
				"buenos dias", "saludos", "adios");
		simpleTypeScenario1Element2NodeB = new SimpleType("_root-_element2-_b",
				"xs:string", simpleTypeElement2NodeBValues, true);

		List<String> simpleTypeElement1Attr1Values = Lists.newArrayList("alfa",
				"beta");
		simpleTypeScenario1Element1Attr1 = new SimpleType(
				"_root-_element1-attr1", "xs:string",
				simpleTypeElement1Attr1Values, false);
		List<String> simpleTypeElement1Attr2Values = Lists.newArrayList("1",
				"2");
		simpleTypeScenario1Element1Attr2 = new SimpleType(
				"_root-_element1-attr2", "xs:integer",
				simpleTypeElement1Attr2Values, false);
		List<String> simpleTypeElement1Attr3Values = Lists.newArrayList("true",
				"false", "0", "1");
		simpleTypeScenario1Element1Attr3 = new SimpleType(
				"_root-_element1-attr3", "xs:boolean",
				simpleTypeElement1Attr3Values, false);
		List<String> simpleTypeElement2Attr1Values = Lists.newArrayList(
				"lambda", "omega");
		simpleTypeScenario1Element2Attr1 = new SimpleType(
				"_root-_element2-attr1", "xs:string",
				simpleTypeElement2Attr1Values, false);
		List<String> simpleTypeElement2Attr2Values = Lists.newArrayList("3",
				"4");
		simpleTypeScenario1Element2Attr2 = new SimpleType(
				"_root-_element2-attr2", "xs:integer",
				simpleTypeElement2Attr2Values, false);
		List<String> simpleTypeElement2Attr4Values = Lists.newArrayList(
				"gamma", "epsilon", "omicron", "zeta");
		simpleTypeScenario1Element2Attr4 = new SimpleType(
				"_root-_element2-attr4", "xs:string",
				simpleTypeElement2Attr4Values, false);
	}

	/**
	 * It builds the SchemaAttribute objects used at scenario 1
	 */
	private void buildSchemaAttributesScenario1() {
		attributeScenario1Element1Attr1 = new SchemaAttribute("attr1", "",
				false, simpleTypeScenario1Element1Attr1);
		attributeScenario1Element1Attr2 = new SchemaAttribute("attr2", "",
				false, simpleTypeScenario1Element1Attr2);
		attributeScenario1Element1Attr3 = new SchemaAttribute("attr3", "",
				false, simpleTypeScenario1Element1Attr3);

		attributeScenario1Element2Attr1 = new SchemaAttribute("attr1", "",
				false, simpleTypeScenario1Element2Attr1);
		attributeScenario1Element2Attr2 = new SchemaAttribute("attr2", "",
				true, simpleTypeScenario1Element2Attr2);
		attributeScenario1Element2Attr4 = new SchemaAttribute("attr4", "",
				true, simpleTypeScenario1Element2Attr4);
	}

	/**
	 * It builds the ComplexType objects of the schema at scenario 1
	 */
	private void buildComplexTypesScenario1() {
		complexTypeScenario1Root = new ComplexType("_root",
				new ExtendedAutomaton(), new SimpleType("root"),
				new ArrayList<SchemaAttribute>());

		complexTypeScenario1Element1 = new ComplexType("_root-_element1",
				new ExtendedAutomaton(), new SimpleType("_root-_element1"),
				Lists.newArrayList(attributeScenario1Element1Attr1,
						attributeScenario1Element1Attr2,
						attributeScenario1Element1Attr3));
		complexTypeScenario1Element1.getComments().add("Comentario 1");
		complexTypeScenario1Element1.getComments().add("Otro comentario 1");
		complexTypeScenario1Element1NodeA = new ComplexType(
				"_root-_element1-_a", new ExtendedAutomaton(),
				simpleTypeScenario1Element1NodeA,
				new ArrayList<SchemaAttribute>());
		complexTypeScenario1Element1NodeA.getComments().add("Comentario A");
		complexTypeScenario1Element1NodeB = new ComplexType(
				"_root-_element1-_b", new ExtendedAutomaton(),
				simpleTypeScenario1Element1NodeB,
				new ArrayList<SchemaAttribute>());
		complexTypeScenario1Element1NodeC = new ComplexType(
				"_root-_element1-_c", new ExtendedAutomaton(), new SimpleType(
						"_root-_element1-_c"), new ArrayList<SchemaAttribute>());
		complexTypeScenario1Element1NodeCNodeE = new ComplexType(
				"_root-_element1-_c-_e", new ExtendedAutomaton(),
				new SimpleType("_root-_element1-_c"),
				new ArrayList<SchemaAttribute>());

		complexTypeScenario1Element2 = new ComplexType("_root-_element2",
				new ExtendedAutomaton(), new SimpleType("_root-_element2"),
				Lists.newArrayList(attributeScenario1Element2Attr1,
						attributeScenario1Element2Attr2,
						attributeScenario1Element2Attr4));
		complexTypeScenario1Element2NodeA = new ComplexType(
				"_root-_element2-_a", new ExtendedAutomaton(),
				simpleTypeScenario1Element2NodeA,
				new ArrayList<SchemaAttribute>());
		complexTypeScenario1Element2NodeB = new ComplexType(
				"_root-_element2-_b", new ExtendedAutomaton(),
				simpleTypeScenario1Element2NodeB,
				new ArrayList<SchemaAttribute>());
		complexTypeScenario1Element2NodeC = new ComplexType(
				"_root-_element2-_c", new ExtendedAutomaton(), new SimpleType(
						"_root-_element2-_c"), new ArrayList<SchemaAttribute>());
		complexTypeScenario1Element2NodeCNodeF = new ComplexType(
				"_root-_element2-_c-_f", new ExtendedAutomaton(),
				new SimpleType("_root-_element2-_f"),
				new ArrayList<SchemaAttribute>());
		complexTypeScenario1Element2NodeD = new ComplexType(
				"_root-_element2-_d", new ExtendedAutomaton(), new SimpleType(
						"_root-_element2-_d"), new ArrayList<SchemaAttribute>());
	}

	/**
	 * It builds the SchemaElement objects used at scenario 1
	 */
	private void buildSchemaElementsScenario1() {

		elementScenario1Root = new SchemaElement("root", "",
				complexTypeScenario1Root);
		elementScenario1Root.setValidRoot(true);

		elementScenario1Element1 = new SchemaElement("element1", "",
				complexTypeScenario1Element1);
		elementScenario1Element1A = new SchemaElement("a", "",
				complexTypeScenario1Element1NodeA);
		elementScenario1Element1B = new SchemaElement("b", "",
				complexTypeScenario1Element1NodeB);
		elementScenario1Element1C = new SchemaElement("c", "",
				complexTypeScenario1Element1NodeC);
		elementScenario1Element1CE = new SchemaElement("e", "",
				complexTypeScenario1Element1NodeCNodeE);

		elementScenario1Element2 = new SchemaElement("element2", "",
				complexTypeScenario1Element2);
		elementScenario1Element2A = new SchemaElement("a", "",
				complexTypeScenario1Element2NodeA);
		elementScenario1Element2B = new SchemaElement("b", "",
				complexTypeScenario1Element2NodeB);
		elementScenario1Element2C = new SchemaElement("c", "",
				complexTypeScenario1Element2NodeC);
		elementScenario1Element2CF = new SchemaElement("f", "",
				complexTypeScenario1Element2NodeCNodeF);
		elementScenario1Element2D = new SchemaElement("d", "",
				complexTypeScenario1Element2NodeD);
	}

	/**
	 * It builds the automatons used at scenario 1
	 */
	private void buildAutomatonsScenario1() {
		ExtendedAutomaton complexTypeRootAutomaton = complexTypeScenario1Root
				.getAutomaton();
		complexTypeRootAutomaton.setInitialState(initialState);
		complexTypeRootAutomaton.setFinalState(finalState);
		complexTypeRootAutomaton.addEdge(initialState,
				elementScenario1Element1, 8L);
		complexTypeRootAutomaton.addEdge(elementScenario1Element1,
				elementScenario1Element2, 8L);
		complexTypeRootAutomaton.addEdge(elementScenario1Element2, finalState,
				8L);

		ExtendedAutomaton complexTypeElement1Automaton = complexTypeScenario1Element1
				.getAutomaton();
		complexTypeElement1Automaton.setInitialState(initialState);
		complexTypeElement1Automaton.setFinalState(finalState);
		complexTypeElement1Automaton.addEdge(initialState,
				elementScenario1Element1A, 4L);
		complexTypeElement1Automaton.addEdge(initialState,
				elementScenario1Element1B, 4L);
		complexTypeElement1Automaton.addEdge(elementScenario1Element1A,
				elementScenario1Element1C, 4L);
		complexTypeElement1Automaton.addEdge(elementScenario1Element1B,
				elementScenario1Element1C, 4L);
		complexTypeElement1Automaton.addEdge(elementScenario1Element1C,
				finalState, 8L);

		ExtendedAutomaton complexTypeElement1NodeCAutomaton = complexTypeScenario1Element1NodeC
				.getAutomaton();
		complexTypeElement1NodeCAutomaton.setInitialState(initialState);
		complexTypeElement1NodeCAutomaton.setFinalState(finalState);
		complexTypeElement1NodeCAutomaton.addEdge(initialState,
				elementScenario1Element1CE, 8L);
		complexTypeElement1NodeCAutomaton.addEdge(elementScenario1Element1CE,
				finalState, 8L);

		ExtendedAutomaton complexTypeElement2Automaton = complexTypeScenario1Element2
				.getAutomaton();
		complexTypeElement2Automaton.setInitialState(initialState);
		complexTypeElement2Automaton.setFinalState(finalState);
		complexTypeElement2Automaton.addEdge(initialState,
				elementScenario1Element2A, 8L);
		complexTypeElement2Automaton.addEdge(elementScenario1Element2A,
				elementScenario1Element2B, 8L);
		complexTypeElement2Automaton.addEdge(elementScenario1Element2B,
				elementScenario1Element2C, 8L);
		complexTypeElement2Automaton.addEdge(elementScenario1Element2C,
				elementScenario1Element2C, 5L);
		complexTypeElement2Automaton.addEdge(elementScenario1Element2C,
				elementScenario1Element2D, 8L);
		complexTypeElement2Automaton.addEdge(elementScenario1Element2D,
				finalState, 8L);

		ExtendedAutomaton complexTypeElement2NodeCAutomaton = complexTypeScenario1Element2NodeC
				.getAutomaton();
		complexTypeElement2NodeCAutomaton.setInitialState(initialState);
		complexTypeElement2NodeCAutomaton.setFinalState(finalState);
		complexTypeElement2NodeCAutomaton.addEdge(initialState,
				elementScenario1Element2CF, 13L);
		complexTypeElement2NodeCAutomaton.addEdge(elementScenario1Element2CF,
				finalState, 13L);
	}

	/**
	 * It builds the RegularExpression objects associated with the complex types
	 */
	private void buildRegularExpressionsScenario1() {
		RegularExpression complexTypeRootRegexp = new Sequence(
				ImmutableList.of(elementScenario1Element1,
						elementScenario1Element2));
		complexTypeScenario1Root.setRegularExpression(complexTypeRootRegexp);

		RegularExpression complexTypeElement1SubregexpChoice = new Choice(
				ImmutableList.of(elementScenario1Element1A,
						elementScenario1Element1B));
		RegularExpression complexTypeElement1Regexp = new Sequence(
				ImmutableList.of(complexTypeElement1SubregexpChoice,
						elementScenario1Element1C));
		complexTypeScenario1Element1
				.setRegularExpression(complexTypeElement1Regexp);
		complexTypeScenario1Element1NodeA.setRegularExpression(emptyRegexp);
		complexTypeScenario1Element1NodeB.setRegularExpression(emptyRegexp);
		complexTypeScenario1Element1NodeC
				.setRegularExpression(elementScenario1Element1CE);
		complexTypeScenario1Element1NodeCNodeE
				.setRegularExpression(emptyRegexp);

		RegularExpression complexTypeElement2SubregexpRepeatedAtLeastOnce = new RepeatedAtLeastOnce(
				elementScenario1Element2C);
		RegularExpression complexTypeElement2Regexp = new Sequence(
				ImmutableList.of(elementScenario1Element2A,
						elementScenario1Element2B,
						complexTypeElement2SubregexpRepeatedAtLeastOnce,
						elementScenario1Element2D));
		complexTypeScenario1Element2
				.setRegularExpression(complexTypeElement2Regexp);
		complexTypeScenario1Element2NodeA.setRegularExpression(emptyRegexp);
		complexTypeScenario1Element2NodeB.setRegularExpression(emptyRegexp);
		complexTypeScenario1Element2NodeC
				.setRegularExpression(elementScenario1Element2CF);
		complexTypeScenario1Element2NodeCNodeF
				.setRegularExpression(emptyRegexp);
		complexTypeScenario1Element2NodeD.setRegularExpression(emptyRegexp);
	}

	/**
	 * It builds the structures of the Schema object of the scenario 1
	 */
	private void buildSchemaScenario1() {
		NavigableMap<String, SortedSet<String>> prefixNamespaceMapping = new TreeMap<String, SortedSet<String>>();
		prefixNamespaceMapping
				.put("", new TreeSet<>(Collections.singleton("")));
		// prefixNamespaceMapping.put(NAMESPACE_URI_TEST, new
		// TreeSet<>(Collections.singleton("test")));
		// prefixNamespaceMapping.put(NAMESPACE_URI_TESTING, new
		// TreeSet<>(Collections.singleton("testing")));

		Map<String, ComplexType> complexTypes = new HashMap<>(11);
		complexTypes.put("_root", complexTypeScenario1Root);
		complexTypes.put("_root-_element1", complexTypeScenario1Element1);
		complexTypes.put("_root-_element1-_a",
				complexTypeScenario1Element1NodeA);
		complexTypes.put("_root-_element1-_b",
				complexTypeScenario1Element1NodeB);
		complexTypes.put("_root-_element1-_c",
				complexTypeScenario1Element1NodeC);
		complexTypes.put("_root-_element1-_c-_e",
				complexTypeScenario1Element1NodeCNodeE);
		complexTypes.put("_root-_element2", complexTypeScenario1Element2);
		complexTypes.put("_root-_element2-_a",
				complexTypeScenario1Element2NodeA);
		complexTypes.put("_root-_element2-_b",
				complexTypeScenario1Element2NodeB);
		complexTypes.put("_root-_element2-_c",
				complexTypeScenario1Element2NodeC);
		complexTypes.put("_root-_element2-_d",
				complexTypeScenario1Element2NodeD);
		complexTypes.put("_root-_element2-_c-_f",
				complexTypeScenario1Element2NodeCNodeF);

		Table<String, String, SchemaElement> elements = HashBasedTable.create(
				1, 11);
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

		Table<String, String, SchemaAttribute> attributes = HashBasedTable
				.create(1, 6);
		attributes.put("", "_root-_element1-attr1",
				attributeScenario1Element1Attr1);
		attributes.put("", "_root-_element1-attr2",
				attributeScenario1Element1Attr2);
		attributes.put("", "_root-_element1-attr3",
				attributeScenario1Element1Attr3);
		attributes.put("", "_root-_element2-attr1",
				attributeScenario1Element2Attr1);
		attributes.put("", "_root-_element2-attr2",
				attributeScenario1Element2Attr2);
		attributes.put("", "_root-_element2-attr4",
				attributeScenario1Element2Attr4);

		Map<String, SimpleType> simpleTypes = new HashMap<>(17);
		simpleTypes.put("_root", complexTypeScenario1Root.getTextSimpleType());
		simpleTypes.put("_root-_element1",
				complexTypeScenario1Element1.getTextSimpleType());
		simpleTypes.put("_root-_element1-_a", simpleTypeScenario1Element1NodeA);
		simpleTypes.put("_root-_element1-_b", simpleTypeScenario1Element1NodeB);
		simpleTypes.put("_root-_element1-_c",
				complexTypeScenario1Element1NodeC.getTextSimpleType());
		simpleTypes.put("_root-_element1-_c-_e",
				complexTypeScenario1Element1NodeCNodeE.getTextSimpleType());
		simpleTypes.put("_root-_element2",
				complexTypeScenario1Element2.getTextSimpleType());
		simpleTypes.put("_root-_element2-_a", simpleTypeScenario1Element2NodeA);
		simpleTypes.put("_root-_element2-_b", simpleTypeScenario1Element2NodeB);
		simpleTypes.put("_root-_element2-_c",
				complexTypeScenario1Element2NodeC.getTextSimpleType());
		simpleTypes.put("_root-_element2-_c-_f",
				complexTypeScenario1Element2NodeCNodeF.getTextSimpleType());
		simpleTypes.put("_root-_element2-_c",
				complexTypeScenario1Element2NodeC.getTextSimpleType());

		schema1 = new Schema(prefixNamespaceMapping, elements, attributes,
				complexTypes, simpleTypes, new Statistics(6));

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

		buildSchemaScenario1();

		configuration1 = mock(XSDInferenceConfiguration.class);
		when(configuration1.getTypeNamesAncestorsSeparator()).thenReturn("-");
		when(configuration1.getMergedTypesSeparator()).thenReturn("_and_");

		when(configuration1.getGenerateEnumerations()).thenReturn(true);

		when(configuration1.getSimpleTypeInferencer())
				.thenReturn(
						XSDInferenceConfiguration.VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL);
		when(configuration1.getAttributeListInferencer())
				.thenReturn(
						XSDInferenceConfiguration.VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL);
		when(configuration1.getMaxNumberOfDistinctValuesToEnum())
				.thenReturn(20);
		when(configuration1.getMinNumberOfDistinctValuesToEnum()).thenReturn(3);
	}

	/**
	 * It builds the ComplexType objects of the schema at scenario 2
	 */
	private void buildComplexTypesScenario2() {
		complexTypeScenario2TestRoot = new ComplexType("test_root",
				new ExtendedAutomaton(), new SimpleType("test_root"),
				new ArrayList<SchemaAttribute>());
		complexTypeScenario2TestRoot.setRegularExpression(emptyRegexp);
		complexTypeScenario2TestingRaiz = new ComplexType("testing_raiz",
				new ExtendedAutomaton(), new SimpleType("testing_raiz"),
				new ArrayList<SchemaAttribute>());
		complexTypeScenario2TestingRaiz.setRegularExpression(emptyRegexp);
	}

	/**
	 * It builds the SchemaElement objects used at scenario 2
	 */
	private void buildSchemaElementsScenario2() {
		elementScenario2TestRoot = new SchemaElement("root",
				NAMESPACE_URI_TEST, complexTypeScenario2TestRoot);
		elementScenario2TestRoot.setValidRoot(true);
		elementScenario2TestingRaiz = new SchemaElement("raiz",
				NAMESPACE_URI_TESTING, complexTypeScenario2TestingRaiz);
		elementScenario2TestingRaiz.setValidRoot(true);
	}

	/**
	 * It builds the structures of the Schema object of the scenario 2
	 */
	private void buildSchemaScenario2() {
		NavigableMap<String, SortedSet<String>> prefixNamespaceMapping = new TreeMap<String, SortedSet<String>>();
		prefixNamespaceMapping.put(NAMESPACE_URI_TEST, new TreeSet<>(
				Collections.singleton("test")));
		prefixNamespaceMapping.put(NAMESPACE_URI_TESTING, new TreeSet<>(
				Collections.singleton("testing")));

		Map<String, ComplexType> complexTypes = new HashMap<>(2);
		complexTypes.put("test_root", complexTypeScenario2TestRoot);
		complexTypes.put("testing_raiz", complexTypeScenario2TestingRaiz);

		Table<String, String, SchemaElement> elements = HashBasedTable.create(
				2, 1);
		elements.put(NAMESPACE_URI_TEST, "test_root", elementScenario2TestRoot);
		elements.put(NAMESPACE_URI_TESTING, "testing_raiz",
				elementScenario2TestingRaiz);

		Table<String, String, SchemaAttribute> attributes = HashBasedTable
				.create(2, 1);

		Map<String, SimpleType> simpleTypes = new HashMap<>(17);
		simpleTypes.put("test_root",
				complexTypeScenario2TestRoot.getTextSimpleType());
		simpleTypes.put("testing_raiz",
				complexTypeScenario2TestingRaiz.getTextSimpleType());

		schema2 = new Schema(prefixNamespaceMapping, elements, attributes,
				complexTypes, simpleTypes, new Statistics(6));

	}

	/**
	 * It builds the scenario 2
	 */
	private void buildScenario2() {
		buildComplexTypesScenario2();
		buildSchemaElementsScenario2();
		buildSchemaScenario2();

		configuration2 = mock(XSDInferenceConfiguration.class);
		when(configuration2.getTypeNamesAncestorsSeparator()).thenReturn("-");
		when(configuration2.getMergedTypesSeparator()).thenReturn("_and_");
		when(configuration2.getMainNamespace()).thenReturn(NAMESPACE_URI_TEST);

		when(configuration2.getGenerateEnumerations()).thenReturn(true);

		when(configuration2.getSimpleTypeInferencer())
				.thenReturn(
						XSDInferenceConfiguration.VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL);
		when(configuration2.getAttributeListInferencer())
				.thenReturn(
						XSDInferenceConfiguration.VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL);
		when(configuration2.getMaxNumberOfDistinctValuesToEnum())
				.thenReturn(20);
		when(configuration2.getMinNumberOfDistinctValuesToEnum()).thenReturn(3);
	}

	/**
	 * Sorts all the children recursively
	 * 
	 * @param element
	 *            starting element
	 */
	private void sortChildrenRecursive(Element element) {
		if (element.getChildren().size() > 0) {
			element.sortChildren(elementComparator);
			for (int i = 0; i < element.getChildren().size(); i++) {
				sortChildrenRecursive(element.getChildren().get(i));
			}
		}
	}

	/**
	 * This method sets up the testing environment
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		initialState = new SchemaElement("initial",
				DEFAULT_PSEUDOELEMENTS_NAMESPACE, null);
		finalState = new SchemaElement("final",
				DEFAULT_PSEUDOELEMENTS_NAMESPACE, null);

		alwaysFalseChildrenPatternComparator = mock(ChildrenPatternComparator.class);
		when(
				alwaysFalseChildrenPatternComparator.compare(
						any(ExtendedAutomaton.class),
						any(ExtendedAutomaton.class))).thenReturn(false);

		alwaysFalseAttributeListComparator = mock(AttributeListComparator.class);
		when(
				alwaysFalseAttributeListComparator.compare(
						anyListOf(SchemaAttribute.class),
						anyListOf(SchemaAttribute.class))).thenReturn(false);

		alwaysFalseEnumComparator = mock(EnumComparator.class);
		when(
				alwaysFalseEnumComparator.compare(any(SimpleType.class),
						any(SimpleType.class))).thenReturn(false);

		xsdFileNameGenerator = mock(XSDFileNameGenerator.class);
		when(
				xsdFileNameGenerator.getSchemaDocumentFileName(eq(""),
						Matchers.<Map<String, String>> any())).thenReturn(
				"schema-no_ns.xsd");
		when(
				xsdFileNameGenerator.getSchemaDocumentFileName(
						eq(NAMESPACE_URI_TEST),
						Matchers.<Map<String, String>> any())).thenReturn(
				"schema-ns_test.xsd");
		when(
				xsdFileNameGenerator.getSchemaDocumentFileName(
						eq(NAMESPACE_URI_TESTING),
						Matchers.<Map<String, String>> any())).thenReturn(
				"schema-ns_testing.xsd");

		buildScenario1();
		buildScenario2();
	}

	// Tests at scenario 1

	/**
	 * Test that checks the XSD generated for the main namespace from a complex
	 * scenario when only the main namespace is known (although there are other
	 * declared namespaces)
	 */
	@Test
	public void testScenario1WholeXSDSingleNamespaceNormal() {
		when(configuration1.getElementsGlobal()).thenReturn(false);
		when(configuration1.getSimpleTypesGlobal()).thenReturn(true);
		when(configuration1.getComplexTypesGlobal()).thenReturn(true);
		when(configuration1.getStrictValidRootDefinitionWorkaround())
				.thenReturn(true);
		SchemaDocumentGeneratorFactory factory = SchemaDocumentGeneratorFactory
				.getInstance();
		SchemaDocumentGenerator<Document> xsdGenerator = factory.getXMLSchemaDocumentGeneratorInstance("","",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocument = xsdGenerator.generateSchemaDocument(schema1,
				configuration1);
		// Schema document is partially sorted so that problems related to
		// unordered data structures are avoided
		// this absence of order does not make any trouble with XSD but turns
		// the verification quite complicated
		//sortChildrenRecursive(xsdDocument.getRootElement());
		// System.out.println(xsdDocument.toString());
		Format format = Format.getRawFormat();
		format.setLineSeparator(LineSeparator.SYSTEM);
		XMLOutputter outputter = new XMLOutputter(format);
		String xsdOutputString = outputter.outputString(xsdDocument);
		// System.out.println(xsdOutputString);
		String lineSeparator = System.getProperty("line.separator");
		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\"><xs:simpleType name=\"_root-_element1-_a-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /><xs:enumeration value=\"cuarenta\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element1-_b-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"hasta luego\" /><xs:enumeration value=\"hola\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element2-_a-SimpleType\"><xs:restriction base=\"xs:integer\"><xs:enumeration value=\"40\" /><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element2-_b-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"adios\" /><xs:enumeration value=\"buenos dias\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType><xs:complexType name=\"_root\"><xs:sequence><xs:element name=\"element1\" type=\"_root-_element1\" /><xs:element name=\"element2\" type=\"_root-_element2\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element1\"><!--Comentario 1--><!--Otro comentario 1--><xs:sequence><xs:choice><xs:element name=\"a\" type=\"_root-_element1-_a-SimpleType\" /><xs:element name=\"b\" type=\"_root-_element1-_b-SimpleType\" /></xs:choice><xs:element name=\"c\" type=\"_root-_element1-_c\" /></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attribute name=\"attr2\" type=\"xs:integer\" use=\"required\" /><xs:attribute name=\"attr3\" type=\"xs:boolean\" use=\"required\" /></xs:complexType><xs:complexType name=\"_root-_element1-_a\"><!--Comentario A--></xs:complexType><xs:complexType name=\"_root-_element1-_c\"><xs:sequence><xs:element name=\"e\" type=\"_root-_element1-_c-_e\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element1-_c-_e\" /><xs:complexType name=\"_root-_element2\"><xs:sequence><xs:element name=\"a\" type=\"_root-_element2-_a-SimpleType\" /><xs:element name=\"b\" type=\"_root-_element2-_b-SimpleType\" /><xs:element name=\"c\" type=\"_root-_element2-_c\" maxOccurs=\"unbounded\" minOccurs=\"1\" /><xs:element name=\"d\" type=\"_root-_element2-_d\" /></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attribute name=\"attr2\" type=\"xs:integer\" use=\"optional\" /><xs:attribute name=\"attr4\" type=\"xs:string\" use=\"optional\" /></xs:complexType><xs:complexType name=\"_root-_element2-_c\"><xs:sequence><xs:element name=\"f\" type=\"_root-_element2-_c-_f\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element2-_c-_f\" /><xs:complexType name=\"_root-_element2-_d\" /><xs:element name=\"root\" type=\"_root\" /></xs:schema>"
				+ lineSeparator;
		// for(int i=0;i<expectedOutput.length();i++){
		// if(expectedOutput.charAt(i)!=xsdOutputString.charAt(i))
		// System.err.println("Difference at index "+i+". Expected char is '"+expectedOutput.charAt(i)+"' but found is '"+xsdOutputString.charAt(i)+"'");
		// }
//		System.out.println(expectedOutput);
//		System.out.println(xsdOutputString);
		assertEquals(expectedOutput, xsdOutputString);
	}

	/**
	 * Test that checks the XSD documents generated for the main namespace from
	 * a complex scenario when there are elements and attributes of many
	 * namespaces and the workaround is enabled.
	 */
	@Test
	public void testScenario1WholeXSDManyNamespacesWorkaround() {
		// First, we modify the scenario so that some elements and attributes
		// have different namespaces
		Map<String, SortedSet<String>> prefixNamespaceMapping = schema1
				.getNamespacesToPossiblePrefixMappingModifiable();
		prefixNamespaceMapping.put(NAMESPACE_URI_TEST, new TreeSet<>(
				Collections.singleton("test")));
		prefixNamespaceMapping.put(NAMESPACE_URI_TESTING, new TreeSet<>(
				Collections.singleton("testing")));
		Table<String, String, SchemaElement> elements = schema1.getElements();
		elements.put(NAMESPACE_URI_TEST, "_root-_element1-c",
				elementScenario1Element1C);
		elements.put(NAMESPACE_URI_TEST, "_root-_element1-_c-e",
				elementScenario1Element1CE);
		elements.put(NAMESPACE_URI_TEST, "_root-_element2-c",
				elementScenario1Element2C);
		elements.put(NAMESPACE_URI_TESTING, "_root-_element2-d",
				elementScenario1Element2D);
		elements.remove("", "_root-_element1-c");
		elements.remove("", "_root-_element1-_c-e");
		elements.remove("", "_root-_element2-c");
		elements.remove("", "_root-_element2-d");
		elementScenario1Element1C.setNamespace(NAMESPACE_URI_TEST);
		elementScenario1Element1CE.setNamespace(NAMESPACE_URI_TEST);
		elementScenario1Element2C.setNamespace(NAMESPACE_URI_TEST);
		elementScenario1Element2D.setNamespace(NAMESPACE_URI_TESTING);
		Table<String, String, SchemaAttribute> attributes = schema1
				.getAttributes();
		attributes.put(NAMESPACE_URI_TEST, "_root-_element1-attr2",
				attributeScenario1Element1Attr2);
		attributes.put(NAMESPACE_URI_TEST, "_root-_element2-attr2",
				attributeScenario1Element2Attr2);
		attributes.put(NAMESPACE_URI_TESTING, "_root-_element2-attr4",
				attributeScenario1Element2Attr4);
		attributes.remove("", "_root-_element1-attr2");
		attributes.remove("", "_root-_element2-attr2");
		attributes.remove("", "_root-_element2-attr4");
		attributeScenario1Element1Attr2.setNamespace(NAMESPACE_URI_TEST);
		attributeScenario1Element2Attr2.setNamespace(NAMESPACE_URI_TEST);
		attributeScenario1Element2Attr4.setNamespace(NAMESPACE_URI_TESTING);

		when(configuration1.getStrictValidRootDefinitionWorkaround())
				.thenReturn(true);
		when(configuration1.getElementsGlobal()).thenReturn(false);
		when(configuration1.getSimpleTypesGlobal()).thenReturn(true);
		when(configuration1.getComplexTypesGlobal()).thenReturn(true);
		SchemaDocumentGeneratorFactory xsdDocumentGeneratorFactoryInstance = SchemaDocumentGeneratorFactory
				.getInstance();
		SchemaDocumentGenerator<Document> xsdGeneratorDefaultNS = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance("","",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocumentDefaultNS = xsdGeneratorDefaultNS.generateSchemaDocument(
				schema1, configuration1);
		// Schema document is partially sorted so that problems related to
		// unordered data structures are avoided
		// this absence of order does not make any trouble with XSD but turns
		// the verification quite complicated
		//sortChildrenRecursive(xsdDocumentDefaultNS.getRootElement());
		// System.out.println(xsdDocument.toString());
		Format format = Format.getRawFormat();
		format.setLineSeparator(LineSeparator.SYSTEM);
		XMLOutputter outputter = new XMLOutputter(format);
		String xsdOutputStringDefaultNS = outputter
				.outputString(xsdDocumentDefaultNS);
		// System.out.println(xsdOutputString);
		String lineSeparator = System.getProperty("line.separator");
		String expectedOutputDefaultNS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:test=\"http://prueba.net\" xmlns:testing=\"http://probando.net\" elementFormDefault=\"qualified\"><xs:import namespace=\"http://probando.net\" schemaLocation=\"schema-ns_testing.xsd\" /><xs:import namespace=\"http://prueba.net\" schemaLocation=\"schema-ns_test.xsd\" /><xs:simpleType name=\"_root-_element1-_a-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /><xs:enumeration value=\"cuarenta\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element1-_b-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"hasta luego\" /><xs:enumeration value=\"hola\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element2-_a-SimpleType\"><xs:restriction base=\"xs:integer\"><xs:enumeration value=\"40\" /><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element2-_b-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"adios\" /><xs:enumeration value=\"buenos dias\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType><xs:complexType name=\"_root\"><xs:sequence><xs:element name=\"element1\" type=\"_root-_element1\" /><xs:element name=\"element2\" type=\"_root-_element2\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element1\"><!--Comentario 1--><!--Otro comentario 1--><xs:sequence><xs:choice><xs:element name=\"a\" type=\"_root-_element1-_a-SimpleType\" /><xs:element name=\"b\" type=\"_root-_element1-_b-SimpleType\" /></xs:choice><xs:group ref=\"test:c-_root-_element1-_c\" /></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attributeGroup ref=\"test:_root-_element1-attr2\" /><xs:attribute name=\"attr3\" type=\"xs:boolean\" use=\"required\" /></xs:complexType><xs:complexType name=\"_root-_element1-_a\"><!--Comentario A--></xs:complexType><xs:complexType name=\"_root-_element1-_c\"><xs:group ref=\"test:e-_root-_element1-_c-_e\" /></xs:complexType><xs:complexType name=\"_root-_element1-_c-_e\" /><xs:complexType name=\"_root-_element2\"><xs:sequence><xs:element name=\"a\" type=\"_root-_element2-_a-SimpleType\" /><xs:element name=\"b\" type=\"_root-_element2-_b-SimpleType\" /><xs:group ref=\"test:c-_root-_element2-_c\" maxOccurs=\"unbounded\" minOccurs=\"1\" /><xs:group ref=\"testing:d-_root-_element2-_d\" /></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attributeGroup ref=\"test:_root-_element2-attr2\" /><xs:attributeGroup ref=\"testing:_root-_element2-attr4\" /></xs:complexType><xs:complexType name=\"_root-_element2-_c\"><xs:sequence><xs:element name=\"f\" type=\"_root-_element2-_c-_f\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element2-_c-_f\" /><xs:complexType name=\"_root-_element2-_d\" /><xs:element name=\"root\" type=\"_root\" /></xs:schema>"
				+ lineSeparator;
		// for(int i=0;i<expectedOutput.length();i++){
		// if(expectedOutput.charAt(i)!=xsdOutputString.charAt(i))
		// System.err.println("Difference at index "+i+". Expected char is '"+expectedOutput.charAt(i)+"' but found is '"+xsdOutputString.charAt(i)+"'");
		// }
//		System.out.println(expectedOutputDefaultNS);
//		System.out.println(xsdOutputStringDefaultNS);
		assertEquals(expectedOutputDefaultNS, xsdOutputStringDefaultNS);

		SchemaDocumentGenerator<Document> xsdGeneratorTestNS = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance(NAMESPACE_URI_TEST,"",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocumentTestNS = xsdGeneratorTestNS.generateSchemaDocument(
				schema1, configuration1);
		String xsdOutputStringTestNS = outputter
				.outputString(xsdDocumentTestNS);

		String expectedOutputTestNS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:test=\"http://prueba.net\" xmlns:testing=\"http://probando.net\" targetNamespace=\"http://prueba.net\" elementFormDefault=\"qualified\"><xs:import schemaLocation=\"schema-no_ns.xsd\" /><xs:attributeGroup name=\"_root-_element1-attr2\"><xs:attribute name=\"attr2\" type=\"xs:integer\" use=\"required\" form=\"qualified\" /></xs:attributeGroup><xs:attributeGroup name=\"_root-_element2-attr2\"><xs:attribute name=\"attr2\" type=\"xs:integer\" use=\"optional\" form=\"qualified\" /></xs:attributeGroup><xs:group name=\"c-_root-_element1-_c\"><xs:sequence><xs:element name=\"c\" type=\"_root-_element1-_c\" /></xs:sequence></xs:group><xs:group name=\"c-_root-_element2-_c\"><xs:sequence><xs:element name=\"c\" type=\"_root-_element2-_c\" /></xs:sequence></xs:group><xs:group name=\"e-_root-_element1-_c-_e\"><xs:sequence><xs:element name=\"e\" type=\"_root-_element1-_c-_e\" /></xs:sequence></xs:group></xs:schema>"
				+ lineSeparator;
//		System.out.println(expectedOutputTestNS);
//		System.out.println(xsdOutputStringTestNS);
		assertEquals(expectedOutputTestNS, xsdOutputStringTestNS);

		SchemaDocumentGenerator<Document> xsdGeneratorTestingNS = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance(NAMESPACE_URI_TESTING,"",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocumentTestingNS = xsdGeneratorTestingNS.generateSchemaDocument(
				schema1, configuration1);
		String xsdOutputStringTestingNS = outputter
				.outputString(xsdDocumentTestingNS);

		String expectedOutputTestingNS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:test=\"http://prueba.net\" xmlns:testing=\"http://probando.net\" targetNamespace=\"http://probando.net\" elementFormDefault=\"qualified\"><xs:import schemaLocation=\"schema-no_ns.xsd\" /><xs:attributeGroup name=\"_root-_element2-attr4\"><xs:attribute name=\"attr4\" type=\"xs:string\" use=\"optional\" form=\"qualified\" /></xs:attributeGroup><xs:group name=\"d-_root-_element2-_d\"><xs:sequence><xs:element name=\"d\" type=\"_root-_element2-_d\" /></xs:sequence></xs:group></xs:schema>"
				+ lineSeparator;
//		System.out.println(expectedOutputTestingNS);
//		System.out.println(xsdOutputStringTestingNS);
		assertEquals(expectedOutputTestingNS, xsdOutputStringTestingNS);
	}

	/**
	 * Test that checks the XSD documents generated for the main namespace from
	 * a complex scenario when there are elements and attributes of many
	 * namespaces and the workaround is enabled.
	 */
	@Test
	public void testScenario1SomeSkippedNamespacesManyNamespacesWorkaround() {
		// First, we modify the scenario so that some elements and attributes
		// have different namespaces
		Map<String, SortedSet<String>> prefixNamespaceMapping = schema1
				.getNamespacesToPossiblePrefixMappingModifiable();
		prefixNamespaceMapping.put(NAMESPACE_URI_TEST, new TreeSet<>(
				Collections.singleton("test")));
		prefixNamespaceMapping.put(NAMESPACE_URI_TESTING, new TreeSet<>(
				Collections.singleton("testing")));
		Table<String, String, SchemaElement> elements = schema1.getElements();
		elements.put(NAMESPACE_URI_TEST, "_root-_element1-c",
				elementScenario1Element1C);
		elements.put(NAMESPACE_URI_TEST, "_root-_element1-_c-e",
				elementScenario1Element1CE);
		elements.put(NAMESPACE_URI_TEST, "_root-_element2-c",
				elementScenario1Element2C);
		elements.put(NAMESPACE_URI_TESTING, "_root-_element2-d",
				elementScenario1Element2D);
		elements.remove("", "_root-_element1-c");
		elements.remove("", "_root-_element1-_c-e");
		elements.remove("", "_root-_element2-c");
		elements.remove("", "_root-_element2-d");
		elementScenario1Element1C.setNamespace(NAMESPACE_URI_TEST);
		elementScenario1Element1CE.setNamespace(NAMESPACE_URI_TEST);
		elementScenario1Element2C.setNamespace(NAMESPACE_URI_TEST);
		elementScenario1Element2D.setNamespace(NAMESPACE_URI_TESTING);
		Table<String, String, SchemaAttribute> attributes = schema1
				.getAttributes();
		attributes.put(NAMESPACE_URI_TEST, "_root-_element1-attr2",
				attributeScenario1Element1Attr2);
		attributes.put(NAMESPACE_URI_TEST, "_root-_element2-attr2",
				attributeScenario1Element2Attr2);
		attributes.put(NAMESPACE_URI_TESTING, "_root-_element2-attr4",
				attributeScenario1Element2Attr4);
		attributes.remove("", "_root-_element1-attr2");
		attributes.remove("", "_root-_element2-attr2");
		attributes.remove("", "_root-_element2-attr4");
		attributeScenario1Element1Attr2.setNamespace(NAMESPACE_URI_TEST);
		attributeScenario1Element2Attr2.setNamespace(NAMESPACE_URI_TEST);
		attributeScenario1Element2Attr4.setNamespace(NAMESPACE_URI_TESTING);

		when(configuration1.getSkipNamespaces()).thenReturn(
				ImmutableList.of(NAMESPACE_URI_TESTING));
		when(configuration1.getStrictValidRootDefinitionWorkaround())
				.thenReturn(true);
		when(configuration1.getElementsGlobal()).thenReturn(false);
		when(configuration1.getSimpleTypesGlobal()).thenReturn(true);
		when(configuration1.getComplexTypesGlobal()).thenReturn(true);
		SchemaDocumentGeneratorFactory xsdDocumentGeneratorFactoryInstance = SchemaDocumentGeneratorFactory
				.getInstance();
		SchemaDocumentGenerator<Document> xsdGeneratorDefaultNS = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance("","",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocumentDefaultNS = xsdGeneratorDefaultNS.generateSchemaDocument(
				schema1, configuration1);
		// Schema document is partially sorted so that problems related to
		// unordered data structures are avoided
		// this absence of order does not make any trouble with XSD but turns
		// the verification quite complicated
		//sortChildrenRecursive(xsdDocumentDefaultNS.getRootElement());
		// System.out.println(xsdDocument.toString());
		Format format = Format.getRawFormat();
		format.setLineSeparator(LineSeparator.SYSTEM);
		XMLOutputter outputter = new XMLOutputter(format);
		String xsdOutputStringDefaultNS = outputter
				.outputString(xsdDocumentDefaultNS);
		// System.out.println(xsdOutputString);
		String lineSeparator = System.getProperty("line.separator");
		String expectedOutputDefaultNS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:test=\"http://prueba.net\" xmlns:testing=\"http://probando.net\" elementFormDefault=\"qualified\"><xs:import namespace=\"http://probando.net\" /><xs:import namespace=\"http://prueba.net\" schemaLocation=\"schema-ns_test.xsd\" /><xs:simpleType name=\"_root-_element1-_a-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /><xs:enumeration value=\"cuarenta\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element1-_b-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"hasta luego\" /><xs:enumeration value=\"hola\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element2-_a-SimpleType\"><xs:restriction base=\"xs:integer\"><xs:enumeration value=\"40\" /><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element2-_b-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"adios\" /><xs:enumeration value=\"buenos dias\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType><xs:complexType name=\"_root\"><xs:sequence><xs:element name=\"element1\" type=\"_root-_element1\" /><xs:element name=\"element2\" type=\"_root-_element2\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element1\"><!--Comentario 1--><!--Otro comentario 1--><xs:sequence><xs:choice><xs:element name=\"a\" type=\"_root-_element1-_a-SimpleType\" /><xs:element name=\"b\" type=\"_root-_element1-_b-SimpleType\" /></xs:choice><xs:group ref=\"test:c-_root-_element1-_c\" /></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attributeGroup ref=\"test:_root-_element1-attr2\" /><xs:attribute name=\"attr3\" type=\"xs:boolean\" use=\"required\" /></xs:complexType><xs:complexType name=\"_root-_element1-_a\"><!--Comentario A--></xs:complexType><xs:complexType name=\"_root-_element1-_c\"><xs:group ref=\"test:e-_root-_element1-_c-_e\" /></xs:complexType><xs:complexType name=\"_root-_element1-_c-_e\" /><xs:complexType name=\"_root-_element2\"><xs:sequence><xs:element name=\"a\" type=\"_root-_element2-_a-SimpleType\" /><xs:element name=\"b\" type=\"_root-_element2-_b-SimpleType\" /><xs:group ref=\"test:c-_root-_element2-_c\" maxOccurs=\"unbounded\" minOccurs=\"1\" /><xs:element ref=\"testing:d\" /></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attributeGroup ref=\"test:_root-_element2-attr2\" /><xs:attribute ref=\"testing:attr4\" /></xs:complexType><xs:complexType name=\"_root-_element2-_c\"><xs:sequence><xs:element name=\"f\" type=\"_root-_element2-_c-_f\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element2-_c-_f\" /><xs:complexType name=\"_root-_element2-_d\" /><xs:element name=\"root\" type=\"_root\" /></xs:schema>"
				+ lineSeparator;
		// for(int i=0;i<expectedOutput.length();i++){
		// if(expectedOutput.charAt(i)!=xsdOutputString.charAt(i))
		// System.err.println("Difference at index "+i+". Expected char is '"+expectedOutput.charAt(i)+"' but found is '"+xsdOutputString.charAt(i)+"'");
		// }
//		System.out.println(expectedOutputDefaultNS);
//		System.out.println(xsdOutputStringDefaultNS);
		assertEquals(expectedOutputDefaultNS, xsdOutputStringDefaultNS);

		SchemaDocumentGenerator<Document> xsdGeneratorTestNS = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance(NAMESPACE_URI_TEST,"",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocumentTestNS = xsdGeneratorTestNS.generateSchemaDocument(
				schema1, configuration1);
		String xsdOutputStringTestNS = outputter
				.outputString(xsdDocumentTestNS);

		String expectedOutputTestNS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:test=\"http://prueba.net\" xmlns:testing=\"http://probando.net\" targetNamespace=\"http://prueba.net\" elementFormDefault=\"qualified\"><xs:import schemaLocation=\"schema-no_ns.xsd\" /><xs:attributeGroup name=\"_root-_element1-attr2\"><xs:attribute name=\"attr2\" type=\"xs:integer\" use=\"required\" form=\"qualified\" /></xs:attributeGroup><xs:attributeGroup name=\"_root-_element2-attr2\"><xs:attribute name=\"attr2\" type=\"xs:integer\" use=\"optional\" form=\"qualified\" /></xs:attributeGroup><xs:group name=\"c-_root-_element1-_c\"><xs:sequence><xs:element name=\"c\" type=\"_root-_element1-_c\" /></xs:sequence></xs:group><xs:group name=\"c-_root-_element2-_c\"><xs:sequence><xs:element name=\"c\" type=\"_root-_element2-_c\" /></xs:sequence></xs:group><xs:group name=\"e-_root-_element1-_c-_e\"><xs:sequence><xs:element name=\"e\" type=\"_root-_element1-_c-_e\" /></xs:sequence></xs:group></xs:schema>"
				+ lineSeparator;
//		System.out.println(expectedOutputTestNS);
//		System.out.println(xsdOutputStringTestNS);		
		assertEquals(expectedOutputTestNS, xsdOutputStringTestNS);

		// Document xsdDocumentTestingNS =
		// xsdGenerator.generateSchemaDocument(schema1, configuration1,
		// NAMESPACE_URI_TESTING, "", xsdFileNameGenerator);
		// String xsdOutputStringTestingNS =
		// outputter.outputString(xsdDocumentTestingNS);
		//
		// String
		// expectedOutputTestingNS="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		// lineSeparator +
		// "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:test=\"http://prueba.net\" xmlns:testing=\"http://probando.net\" targetNamespace=\"http://probando.net\" elementFormDefault=\"qualified\"><xs:import schemaLocation=\"schema-no_ns.xsd\" /><xs:attributeGroup name=\"_root-_element2-attr4\"><xs:attribute name=\"attr4\" type=\"xs:string\" use=\"optional\" form=\"qualified\" /></xs:attributeGroup><xs:group name=\"d-_root-_element2-_d\"><xs:sequence><xs:element name=\"d\" type=\"_root-_element2-_d\" /></xs:sequence></xs:group></xs:schema>"+lineSeparator;
		// assertEquals(expectedOutputTestingNS, xsdOutputStringTestingNS);
	}

	/**
	 * Test that checks the XSD documents generated for the main namespace from
	 * a complex scenario when there are elements and attributes of many
	 * namespaces and the workaround is not enabled.
	 */
	@Test
	public void testScenario1WholeXSDManyNamespacesNoWorkaround() {
		// First, we modify the scenario so that some elements and attributes
		// have different namespaces
		Map<String, SortedSet<String>> prefixNamespaceMapping = schema1
				.getNamespacesToPossiblePrefixMappingModifiable();
		prefixNamespaceMapping.put(NAMESPACE_URI_TEST, new TreeSet<>(
				Collections.singleton("test")));
		prefixNamespaceMapping.put(NAMESPACE_URI_TESTING, new TreeSet<>(
				Collections.singleton("testing")));
		Table<String, String, SchemaElement> elements = schema1.getElements();
		elements.put(NAMESPACE_URI_TEST, "_root-_element1-c",
				elementScenario1Element1C);
		elements.put(NAMESPACE_URI_TEST, "_root-_element1-_c-e",
				elementScenario1Element1CE);
		elements.put(NAMESPACE_URI_TEST, "_root-_element2-c",
				elementScenario1Element2C);
		elements.put(NAMESPACE_URI_TESTING, "_root-_element2-d",
				elementScenario1Element2D);
		elements.remove("", "_root-_element1-c");
		elements.remove("", "_root-_element1-_c-e");
		elements.remove("", "_root-_element2-c");
		elements.remove("", "_root-_element2-d");
		elementScenario1Element1C.setNamespace(NAMESPACE_URI_TEST);
		elementScenario1Element1CE.setNamespace(NAMESPACE_URI_TEST);
		elementScenario1Element2C.setNamespace(NAMESPACE_URI_TEST);
		elementScenario1Element2D.setNamespace(NAMESPACE_URI_TESTING);
		Table<String, String, SchemaAttribute> attributes = schema1
				.getAttributes();
		attributes.put(NAMESPACE_URI_TEST, "_root-_element1-attr2",
				attributeScenario1Element1Attr2);
		attributes.put(NAMESPACE_URI_TEST, "_root-_element2-attr2",
				attributeScenario1Element2Attr2);
		attributes.put(NAMESPACE_URI_TESTING, "_root-_element2-attr4",
				attributeScenario1Element2Attr4);
		attributes.remove("", "_root-_element1-attr2");
		attributes.remove("", "_root-_element2-attr2");
		attributes.remove("", "_root-_element2-attr4");
		attributeScenario1Element1Attr2.setNamespace(NAMESPACE_URI_TEST);
		attributeScenario1Element2Attr2.setNamespace(NAMESPACE_URI_TEST);
		attributeScenario1Element2Attr4.setNamespace(NAMESPACE_URI_TESTING);

		when(configuration1.getElementsGlobal()).thenReturn(false);
		when(configuration1.getSimpleTypesGlobal()).thenReturn(true);
		when(configuration1.getComplexTypesGlobal()).thenReturn(true);
		when(configuration1.getStrictValidRootDefinitionWorkaround())
				.thenReturn(false);
		SchemaDocumentGeneratorFactory xsdDocumentGeneratorFactoryInstance = SchemaDocumentGeneratorFactory
				.getInstance();
		SchemaDocumentGenerator<Document> xsdGeneratorDefaultNS = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance("","",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocumentDefaultNS = xsdGeneratorDefaultNS.generateSchemaDocument(
				schema1, configuration1);
		// Schema document is partially sorted so that problems related to
		// unordered data structures are avoided
		// this absence of order does not make any trouble with XSD but turns
		// the verification quite complicated
		//sortChildrenRecursive(xsdDocumentDefaultNS.getRootElement());
		// System.out.println(xsdDocument.toString());
		Format format = Format.getRawFormat();
		format.setLineSeparator(LineSeparator.SYSTEM);
		XMLOutputter outputter = new XMLOutputter(format);
		String xsdOutputStringDefaultNS = outputter
				.outputString(xsdDocumentDefaultNS);
		// System.out.println(xsdOutputString);
		String lineSeparator = System.getProperty("line.separator");
		String expectedOutputDefaultNS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:test=\"http://prueba.net\" xmlns:testing=\"http://probando.net\" elementFormDefault=\"qualified\"><xs:import namespace=\"http://probando.net\" schemaLocation=\"schema-ns_testing.xsd\" /><xs:import namespace=\"http://prueba.net\" schemaLocation=\"schema-ns_test.xsd\" /><xs:simpleType name=\"_root-_element1-_a-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /><xs:enumeration value=\"cuarenta\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element1-_b-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"hasta luego\" /><xs:enumeration value=\"hola\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element2-_a-SimpleType\"><xs:restriction base=\"xs:integer\"><xs:enumeration value=\"40\" /><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element2-_b-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"adios\" /><xs:enumeration value=\"buenos dias\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType><xs:complexType name=\"_root\"><xs:sequence><xs:element name=\"element1\" type=\"_root-_element1\" /><xs:element name=\"element2\" type=\"_root-_element2\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element1\"><!--Comentario 1--><!--Otro comentario 1--><xs:sequence><xs:choice><xs:element name=\"a\" type=\"_root-_element1-_a-SimpleType\" /><xs:element name=\"b\" type=\"_root-_element1-_b-SimpleType\" /></xs:choice><xs:element ref=\"test:c\" /></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attribute ref=\"test:attr2\" /><xs:attribute name=\"attr3\" type=\"xs:boolean\" use=\"required\" /></xs:complexType><xs:complexType name=\"_root-_element1-_a\"><!--Comentario A--></xs:complexType><xs:complexType name=\"_root-_element1-_c\"><xs:sequence><xs:element ref=\"test:e\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element1-_c-_e\" /><xs:complexType name=\"_root-_element2\"><xs:sequence><xs:element name=\"a\" type=\"_root-_element2-_a-SimpleType\" /><xs:element name=\"b\" type=\"_root-_element2-_b-SimpleType\" /><xs:element ref=\"test:c\" maxOccurs=\"unbounded\" minOccurs=\"1\" /><xs:element ref=\"testing:d\" /></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attribute ref=\"test:attr2\" /><xs:attribute ref=\"testing:attr4\" /></xs:complexType><xs:complexType name=\"_root-_element2-_c\"><xs:sequence><xs:element name=\"f\" type=\"_root-_element2-_c-_f\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element2-_c-_f\" /><xs:complexType name=\"_root-_element2-_d\" /><xs:element name=\"root\" type=\"_root\" /></xs:schema>"
				+ lineSeparator;
		// for(int i=0;i<expectedOutput.length();i++){
		// if(expectedOutput.charAt(i)!=xsdOutputString.charAt(i))
		// System.err.println("Difference at index "+i+". Expected char is '"+expectedOutput.charAt(i)+"' but found is '"+xsdOutputString.charAt(i)+"'");
		// }
//		System.out.println(expectedOutputDefaultNS);
//		System.out.println(xsdOutputStringDefaultNS);
		assertEquals(expectedOutputDefaultNS, xsdOutputStringDefaultNS);
		 
		SchemaDocumentGenerator<Document> xsdGeneratorTestNS = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance(NAMESPACE_URI_TEST,"",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocumentTestNS = xsdGeneratorTestNS.generateSchemaDocument(
				schema1, configuration1);
		String xsdOutputStringTestNS = outputter
				.outputString(xsdDocumentTestNS);

		String expectedOutputTestNS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:test=\"http://prueba.net\" xmlns:testing=\"http://probando.net\" targetNamespace=\"http://prueba.net\" elementFormDefault=\"qualified\"><xs:import schemaLocation=\"schema-no_ns.xsd\" /><xs:attribute name=\"attr2\" type=\"xs:integer\" /><xs:element name=\"c\" type=\"_root-_element1-_c\" /><xs:element name=\"e\" type=\"_root-_element1-_c-_e\" /></xs:schema>"
				+ lineSeparator;
//		System.out.println(expectedOutputTestNS);
//		System.out.println(xsdOutputStringTestNS);
		assertEquals(expectedOutputTestNS, xsdOutputStringTestNS);

		SchemaDocumentGenerator<Document> xsdGeneratorTestingNS = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance(NAMESPACE_URI_TESTING,"",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocumentTestingNS = xsdGeneratorTestingNS.generateSchemaDocument(
				schema1, configuration1);
		String xsdOutputStringTestingNS = outputter
				.outputString(xsdDocumentTestingNS);

		String expectedOutputTestingNS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:test=\"http://prueba.net\" xmlns:testing=\"http://probando.net\" targetNamespace=\"http://probando.net\" elementFormDefault=\"qualified\"><xs:import schemaLocation=\"schema-no_ns.xsd\" /><xs:attribute name=\"attr4\" type=\"xs:string\" /><xs:element name=\"d\" type=\"_root-_element2-_d\" /></xs:schema>"
				+ lineSeparator;
//		System.out.println(expectedOutputTestingNS);
//		System.out.println(xsdOutputStringTestingNS);
		assertEquals(expectedOutputTestingNS, xsdOutputStringTestingNS);
	}

	/**
	 * Test that checks the XSD generated for the main namespace from a complex
	 * scenario when only the main namespace is known (although there are other
	 * declared namespaces) when complex types are set to be locally declared.
	 */
	@Test
	public void testScenario1WholeXSDSingleNamespaceComplexTypesLocal() {
		when(configuration1.getElementsGlobal()).thenReturn(false);
		when(configuration1.getSimpleTypesGlobal()).thenReturn(true);
		when(configuration1.getComplexTypesGlobal()).thenReturn(false);
		when(configuration1.getStrictValidRootDefinitionWorkaround())
				.thenReturn(true);
		SchemaDocumentGeneratorFactory xsdDocumentGeneratorFactoryInstance = SchemaDocumentGeneratorFactory
				.getInstance();
		SchemaDocumentGenerator<Document> xsdGenerator = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance("","",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocument = xsdGenerator.generateSchemaDocument(schema1,
				configuration1);
		// Schema document is partially sorted so that problems related to
		// unordered data structures are avoided
		// this absence of order does not make any trouble with XSD but turns
		// the verification quite complicated
		//sortChildrenRecursive(xsdDocument.getRootElement());
		// System.out.println(xsdDocument.toString());
		Format format = Format.getRawFormat();
		format.setLineSeparator(LineSeparator.SYSTEM);
		XMLOutputter outputter = new XMLOutputter(format);
		String xsdOutputString = outputter.outputString(xsdDocument);
		// System.out.println(xsdOutputString);
		String lineSeparator = System.getProperty("line.separator");
		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\"><xs:simpleType name=\"_root-_element1-_a-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /><xs:enumeration value=\"cuarenta\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element1-_b-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"hasta luego\" /><xs:enumeration value=\"hola\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element2-_a-SimpleType\"><xs:restriction base=\"xs:integer\"><xs:enumeration value=\"40\" /><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element2-_b-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"adios\" /><xs:enumeration value=\"buenos dias\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType><xs:element name=\"root\"><xs:complexType><xs:sequence><xs:element name=\"element1\"><xs:complexType><!--Comentario 1--><!--Otro comentario 1--><xs:sequence><xs:choice><xs:element name=\"a\" type=\"_root-_element1-_a-SimpleType\" /><xs:element name=\"b\" type=\"_root-_element1-_b-SimpleType\" /></xs:choice><xs:element name=\"c\"><xs:complexType><xs:sequence><xs:element name=\"e\"><xs:complexType /></xs:element></xs:sequence></xs:complexType></xs:element></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attribute name=\"attr2\" type=\"xs:integer\" use=\"required\" /><xs:attribute name=\"attr3\" type=\"xs:boolean\" use=\"required\" /></xs:complexType></xs:element><xs:element name=\"element2\"><xs:complexType><xs:sequence><xs:element name=\"a\" type=\"_root-_element2-_a-SimpleType\" /><xs:element name=\"b\" type=\"_root-_element2-_b-SimpleType\" /><xs:element name=\"c\" maxOccurs=\"unbounded\" minOccurs=\"1\"><xs:complexType><xs:sequence><xs:element name=\"f\"><xs:complexType /></xs:element></xs:sequence></xs:complexType></xs:element><xs:element name=\"d\"><xs:complexType /></xs:element></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attribute name=\"attr2\" type=\"xs:integer\" use=\"optional\" /><xs:attribute name=\"attr4\" type=\"xs:string\" use=\"optional\" /></xs:complexType></xs:element></xs:sequence></xs:complexType></xs:element></xs:schema>"
				+ lineSeparator;
		// for(int i=0;i<expectedOutput.length();i++){
		// if(expectedOutput.charAt(i)!=xsdOutputString.charAt(i))
		// System.err.println("Difference at index "+i+". Expected char is '"+expectedOutput.charAt(i)+"' but found is '"+xsdOutputString.charAt(i)+"'");
		// }
//		System.out.println(expectedOutput);
//		System.out.println(xsdOutputString);
		assertEquals(expectedOutput, xsdOutputString);
	}

	/**
	 * Test that checks the XSD generated for the main namespace from a complex
	 * scenario when only the main namespace is known (although there are other
	 * declared namespaces) when elements are declared globally.
	 */
	@Test
	public void testScenario1WholeXSDSingleNamespaceElementsGlobal() {
		when(configuration1.getElementsGlobal()).thenReturn(true);
		when(configuration1.getSimpleTypesGlobal()).thenReturn(true);
		when(configuration1.getComplexTypesGlobal()).thenReturn(true);
		when(configuration1.getStrictValidRootDefinitionWorkaround())
				.thenReturn(true);
		SchemaDocumentGeneratorFactory xsdDocumentGeneratorFactoryInstance = SchemaDocumentGeneratorFactory
				.getInstance();
		SchemaDocumentGenerator<Document> xsdGenerator = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance("","",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocument = xsdGenerator.generateSchemaDocument(schema1,
				configuration1);
		// Schema document is partially sorted so that problems related to
		// unordered data structures are avoided
		// this absence of order does not make any trouble with XSD but turns
		// the verification quite complicated
		//sortChildrenRecursive(xsdDocument.getRootElement());
		// System.out.println(xsdDocument.toString());
		Format format = Format.getRawFormat();
		format.setLineSeparator(LineSeparator.SYSTEM);
		XMLOutputter outputter = new XMLOutputter(format);
		String xsdOutputString = outputter.outputString(xsdDocument);
		// System.out.println(xsdOutputString);
		String lineSeparator = System.getProperty("line.separator");
		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\"><xs:simpleType name=\"_root-_element1-_a-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /><xs:enumeration value=\"cuarenta\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element1-_b-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"hasta luego\" /><xs:enumeration value=\"hola\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element2-_a-SimpleType\"><xs:restriction base=\"xs:integer\"><xs:enumeration value=\"40\" /><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /></xs:restriction></xs:simpleType><xs:simpleType name=\"_root-_element2-_b-SimpleType\"><xs:restriction base=\"xs:string\"><xs:enumeration value=\"adios\" /><xs:enumeration value=\"buenos dias\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType><xs:complexType name=\"_root\"><xs:sequence><xs:element ref=\"element1\" /><xs:element ref=\"element2\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element1\"><!--Comentario 1--><!--Otro comentario 1--><xs:sequence><xs:choice><xs:element ref=\"a\" /><xs:element ref=\"b\" /></xs:choice><xs:element ref=\"c\" /></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attribute name=\"attr2\" type=\"xs:integer\" use=\"required\" /><xs:attribute name=\"attr3\" type=\"xs:boolean\" use=\"required\" /></xs:complexType><xs:complexType name=\"_root-_element1-_a\"><!--Comentario A--></xs:complexType><xs:complexType name=\"_root-_element1-_c\"><xs:sequence><xs:element ref=\"e\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element1-_c-_e\" /><xs:complexType name=\"_root-_element2\"><xs:sequence><xs:element ref=\"a\" /><xs:element ref=\"b\" /><xs:element ref=\"c\" maxOccurs=\"unbounded\" minOccurs=\"1\" /><xs:element ref=\"d\" /></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attribute name=\"attr2\" type=\"xs:integer\" use=\"optional\" /><xs:attribute name=\"attr4\" type=\"xs:string\" use=\"optional\" /></xs:complexType><xs:complexType name=\"_root-_element2-_c\"><xs:sequence><xs:element ref=\"f\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element2-_c-_f\" /><xs:complexType name=\"_root-_element2-_d\" /><xs:element name=\"a\" type=\"_root-_element1-_a-SimpleType\" /><xs:element name=\"b\" type=\"_root-_element1-_b-SimpleType\" /><xs:element name=\"c\" type=\"_root-_element1-_c\" /><xs:element name=\"d\" type=\"_root-_element2-_d\" /><xs:element name=\"e\" type=\"_root-_element1-_c-_e\" /><xs:element name=\"element1\" type=\"_root-_element1\" /><xs:element name=\"element2\" type=\"_root-_element2\" /><xs:element name=\"f\" type=\"_root-_element2-_c-_f\" /><xs:element name=\"root\" type=\"_root\" /></xs:schema>"
				+ lineSeparator;
		// for(int i=0;i<expectedOutput.length();i++){
		// if(expectedOutput.charAt(i)!=xsdOutputString.charAt(i))
		// System.err.println("Difference at index "+i+". Expected char is '"+expectedOutput.charAt(i)+"' but found is '"+xsdOutputString.charAt(i)+"'");
		// }
//		System.out.println(expectedOutput);
//		System.out.println(xsdOutputString);
		assertEquals(expectedOutput, xsdOutputString);
	}

	/**
	 * Test that checks the XSD generated for the main namespace from a complex
	 * scenario when only the main namespace is known (although there are other
	 * declared namespaces) when simple types are set to be locally declared.
	 */
	@Test
	public void testScenario1WholeXSDSingleNamespaceSimpleTypesLocal() {
		when(configuration1.getElementsGlobal()).thenReturn(false);
		when(configuration1.getSimpleTypesGlobal()).thenReturn(false);
		when(configuration1.getComplexTypesGlobal()).thenReturn(true);
		when(configuration1.getStrictValidRootDefinitionWorkaround())
				.thenReturn(true);
		SchemaDocumentGeneratorFactory xsdDocumentGeneratorFactoryInstance = SchemaDocumentGeneratorFactory
				.getInstance();
		SchemaDocumentGenerator<Document> xsdGenerator = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance("","",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocument = xsdGenerator.generateSchemaDocument(schema1,
				configuration1);
		// Schema document is partially sorted so that problems related to
		// unordered data structures are avoided
		// this absence of order does not make any trouble with XSD but turns
		// the verification quite complicated
		//sortChildrenRecursive(xsdDocument.getRootElement());
		// System.out.println(xsdDocument.toString());
		Format format = Format.getRawFormat();
		format.setLineSeparator(LineSeparator.SYSTEM);
		XMLOutputter outputter = new XMLOutputter(format);
		String xsdOutputString = outputter.outputString(xsdDocument);
		// System.out.println(xsdOutputString);
		String lineSeparator = System.getProperty("line.separator");
		String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\"><xs:complexType name=\"_root\"><xs:sequence><xs:element name=\"element1\" type=\"_root-_element1\" /><xs:element name=\"element2\" type=\"_root-_element2\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element1\"><!--Comentario 1--><!--Otro comentario 1--><xs:sequence><xs:choice><xs:element name=\"a\"><xs:simpleType><xs:restriction base=\"xs:string\"><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /><xs:enumeration value=\"cuarenta\" /></xs:restriction></xs:simpleType></xs:element><xs:element name=\"b\"><xs:simpleType><xs:restriction base=\"xs:string\"><xs:enumeration value=\"hasta luego\" /><xs:enumeration value=\"hola\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType></xs:element></xs:choice><xs:element name=\"c\" type=\"_root-_element1-_c\" /></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attribute name=\"attr2\" type=\"xs:integer\" use=\"required\" /><xs:attribute name=\"attr3\" type=\"xs:boolean\" use=\"required\" /></xs:complexType><xs:complexType name=\"_root-_element1-_a\"><!--Comentario A--></xs:complexType><xs:complexType name=\"_root-_element1-_c\"><xs:sequence><xs:element name=\"e\" type=\"_root-_element1-_c-_e\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element1-_c-_e\" /><xs:complexType name=\"_root-_element2\"><xs:sequence><xs:element name=\"a\"><xs:simpleType><xs:restriction base=\"xs:integer\"><xs:enumeration value=\"40\" /><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /></xs:restriction></xs:simpleType></xs:element><xs:element name=\"b\"><xs:simpleType><xs:restriction base=\"xs:string\"><xs:enumeration value=\"adios\" /><xs:enumeration value=\"buenos dias\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType></xs:element><xs:element name=\"c\" type=\"_root-_element2-_c\" maxOccurs=\"unbounded\" minOccurs=\"1\" /><xs:element name=\"d\" type=\"_root-_element2-_d\" /></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attribute name=\"attr2\" type=\"xs:integer\" use=\"optional\" /><xs:attribute name=\"attr4\" type=\"xs:string\" use=\"optional\" /></xs:complexType><xs:complexType name=\"_root-_element2-_c\"><xs:sequence><xs:element name=\"f\" type=\"_root-_element2-_c-_f\" /></xs:sequence></xs:complexType><xs:complexType name=\"_root-_element2-_c-_f\" /><xs:complexType name=\"_root-_element2-_d\" /><xs:element name=\"root\" type=\"_root\" /></xs:schema>"
				+ lineSeparator;
		// for(int i=0;i<expectedOutput.length();i++){
		// if(expectedOutput.charAt(i)!=xsdOutputString.charAt(i))
		// System.err.println("Difference at index "+i+". Expected char is '"+expectedOutput.charAt(i)+"' but found is '"+xsdOutputString.charAt(i)+"'");
		// }
//		System.out.println(expectedOutput);
//		System.out.println(xsdOutputString);
		assertEquals(expectedOutput, xsdOutputString);
	}

	/**
	 * Test that checks the XSD documents generated for the main namespace from
	 * a complex scenario when there are elements and attributes of many
	 * namespaces and the workaround is enabled.
	 */
	@Test
	public void testScenario1WholeXSDManyNamespacesWorkaroundCTSTLocal() {
		// First, we modify the scenario so that some elements and attributes
		// have different namespaces
		Map<String, SortedSet<String>> prefixNamespaceMapping = schema1
				.getNamespacesToPossiblePrefixMappingModifiable();
		prefixNamespaceMapping.put(NAMESPACE_URI_TEST, new TreeSet<>(
				Collections.singleton("test")));
		prefixNamespaceMapping.put(NAMESPACE_URI_TESTING, new TreeSet<>(
				Collections.singleton("testing")));
		Table<String, String, SchemaElement> elements = schema1.getElements();
		elements.put(NAMESPACE_URI_TEST, "_root-_element1-b",
				elementScenario1Element1B);
		elements.put(NAMESPACE_URI_TEST, "_root-_element1-c",
				elementScenario1Element1C);
		elements.put(NAMESPACE_URI_TEST, "_root-_element1-_c-e",
				elementScenario1Element1CE);
		elements.put(NAMESPACE_URI_TEST, "_root-_element2-c",
				elementScenario1Element2C);
		elements.put(NAMESPACE_URI_TESTING, "_root-_element2-d",
				elementScenario1Element2D);
		elements.remove("", "_root-_element1-b");
		elements.remove("", "_root-_element1-c");
		elements.remove("", "_root-_element1-_c-e");
		elements.remove("", "_root-_element2-c");
		elements.remove("", "_root-_element2-d");
		elementScenario1Element1B.setNamespace(NAMESPACE_URI_TEST);
		elementScenario1Element1C.setNamespace(NAMESPACE_URI_TEST);
		elementScenario1Element1CE.setNamespace(NAMESPACE_URI_TEST);
		elementScenario1Element2C.setNamespace(NAMESPACE_URI_TEST);
		elementScenario1Element2D.setNamespace(NAMESPACE_URI_TESTING);
		Table<String, String, SchemaAttribute> attributes = schema1
				.getAttributes();
		attributes.put(NAMESPACE_URI_TEST, "_root-_element1-attr2",
				attributeScenario1Element1Attr2);
		attributes.put(NAMESPACE_URI_TEST, "_root-_element2-attr2",
				attributeScenario1Element2Attr2);
		attributes.put(NAMESPACE_URI_TESTING, "_root-_element2-attr4",
				attributeScenario1Element2Attr4);
		attributes.remove("", "_root-_element1-attr2");
		attributes.remove("", "_root-_element2-attr2");
		attributes.remove("", "_root-_element2-attr4");
		attributeScenario1Element1Attr2.setNamespace(NAMESPACE_URI_TEST);
		attributeScenario1Element2Attr2.setNamespace(NAMESPACE_URI_TEST);
		attributeScenario1Element2Attr4.setNamespace(NAMESPACE_URI_TESTING);

		when(configuration1.getStrictValidRootDefinitionWorkaround())
				.thenReturn(true);
		when(configuration1.getElementsGlobal()).thenReturn(false);
		when(configuration1.getSimpleTypesGlobal()).thenReturn(false);
		when(configuration1.getComplexTypesGlobal()).thenReturn(false);
		SchemaDocumentGeneratorFactory xsdDocumentGeneratorFactoryInstance = SchemaDocumentGeneratorFactory
				.getInstance();
		SchemaDocumentGenerator<Document> xsdGeneratorDefaultNS = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance("","",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocumentDefaultNS = xsdGeneratorDefaultNS.generateSchemaDocument(
				schema1, configuration1);
		// Schema document is partially sorted so that problems related to
		// unordered data structures are avoided
		// this absence of order does not make any trouble with XSD but turns
		// the verification quite complicated
		//sortChildrenRecursive(xsdDocumentDefaultNS.getRootElement());
		// System.out.println(xsdDocument.toString());
		Format format = Format.getRawFormat();
		format.setLineSeparator(LineSeparator.SYSTEM);
		XMLOutputter outputter = new XMLOutputter(format);
		String xsdOutputStringDefaultNS = outputter
				.outputString(xsdDocumentDefaultNS);
		// System.out.println(xsdOutputString);
		String lineSeparator = System.getProperty("line.separator");
		String expectedOutputDefaultNS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:test=\"http://prueba.net\" xmlns:testing=\"http://probando.net\" elementFormDefault=\"qualified\"><xs:import namespace=\"http://probando.net\" schemaLocation=\"schema-ns_testing.xsd\" /><xs:import namespace=\"http://prueba.net\" schemaLocation=\"schema-ns_test.xsd\" /><xs:element name=\"root\"><xs:complexType><xs:sequence><xs:element name=\"element1\"><xs:complexType><!--Comentario 1--><!--Otro comentario 1--><xs:sequence><xs:choice><xs:element name=\"a\"><xs:simpleType><xs:restriction base=\"xs:string\"><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /><xs:enumeration value=\"cuarenta\" /></xs:restriction></xs:simpleType></xs:element><xs:group ref=\"test:b-_root-_element1-_b\" /></xs:choice><xs:group ref=\"test:c-_root-_element1-_c\" /></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attributeGroup ref=\"test:_root-_element1-attr2\" /><xs:attribute name=\"attr3\" type=\"xs:boolean\" use=\"required\" /></xs:complexType></xs:element><xs:element name=\"element2\"><xs:complexType><xs:sequence><xs:element name=\"a\"><xs:simpleType><xs:restriction base=\"xs:integer\"><xs:enumeration value=\"40\" /><xs:enumeration value=\"50\" /><xs:enumeration value=\"60\" /><xs:enumeration value=\"70\" /></xs:restriction></xs:simpleType></xs:element><xs:element name=\"b\"><xs:simpleType><xs:restriction base=\"xs:string\"><xs:enumeration value=\"adios\" /><xs:enumeration value=\"buenos dias\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType></xs:element><xs:group ref=\"test:c-_root-_element2-_c\" maxOccurs=\"unbounded\" minOccurs=\"1\" /><xs:group ref=\"testing:d-_root-_element2-_d\" /></xs:sequence><xs:attribute name=\"attr1\" type=\"xs:string\" use=\"required\" /><xs:attributeGroup ref=\"test:_root-_element2-attr2\" /><xs:attributeGroup ref=\"testing:_root-_element2-attr4\" /></xs:complexType></xs:element></xs:sequence></xs:complexType></xs:element></xs:schema>"
				+ lineSeparator;
		// for(int i=0;i<expectedOutput.length();i++){
		// if(expectedOutput.charAt(i)!=xsdOutputString.charAt(i))
		// System.err.println("Difference at index "+i+". Expected char is '"+expectedOutput.charAt(i)+"' but found is '"+xsdOutputString.charAt(i)+"'");
		// }
//		System.out.println(expectedOutputDefaultNS);
//		System.out.println(xsdOutputStringDefaultNS);
		assertEquals(expectedOutputDefaultNS, xsdOutputStringDefaultNS);

		SchemaDocumentGenerator<Document> xsdGeneratorTestNS = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance(NAMESPACE_URI_TEST,"",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocumentTestNS = xsdGeneratorTestNS.generateSchemaDocument(
				schema1, configuration1);
		String xsdOutputStringTestNS = outputter
				.outputString(xsdDocumentTestNS);

		String expectedOutputTestNS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:test=\"http://prueba.net\" xmlns:testing=\"http://probando.net\" targetNamespace=\"http://prueba.net\" elementFormDefault=\"qualified\"><xs:import schemaLocation=\"schema-no_ns.xsd\" /><xs:attributeGroup name=\"_root-_element1-attr2\"><xs:attribute name=\"attr2\" type=\"xs:integer\" use=\"required\" form=\"qualified\" /></xs:attributeGroup><xs:attributeGroup name=\"_root-_element2-attr2\"><xs:attribute name=\"attr2\" type=\"xs:integer\" use=\"optional\" form=\"qualified\" /></xs:attributeGroup><xs:group name=\"b-_root-_element1-_b\"><xs:sequence><xs:element name=\"b\"><xs:simpleType><xs:restriction base=\"xs:string\"><xs:enumeration value=\"hasta luego\" /><xs:enumeration value=\"hola\" /><xs:enumeration value=\"saludos\" /></xs:restriction></xs:simpleType></xs:element></xs:sequence></xs:group><xs:group name=\"c-_root-_element1-_c\"><xs:sequence><xs:element name=\"c\"><xs:complexType><xs:group ref=\"test:e-_root-_element1-_c-_e\" /></xs:complexType></xs:element></xs:sequence></xs:group><xs:group name=\"c-_root-_element2-_c\"><xs:sequence><xs:element name=\"c\"><xs:complexType><xs:sequence><xs:element name=\"f\"><xs:complexType /></xs:element></xs:sequence></xs:complexType></xs:element></xs:sequence></xs:group><xs:group name=\"e-_root-_element1-_c-_e\"><xs:sequence><xs:element name=\"e\"><xs:complexType /></xs:element></xs:sequence></xs:group></xs:schema>"
				+ lineSeparator;
		
//		System.out.println(expectedOutputTestNS);
//		System.out.println(xsdOutputStringTestNS);
		assertEquals(expectedOutputTestNS, xsdOutputStringTestNS);

		SchemaDocumentGenerator<Document> xsdGeneratorTestingNS = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance(NAMESPACE_URI_TESTING,"",new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocumentTestingNS = xsdGeneratorTestingNS.generateSchemaDocument(
				schema1, configuration1);
		String xsdOutputStringTestingNS = outputter
				.outputString(xsdDocumentTestingNS);

		String expectedOutputTestingNS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:test=\"http://prueba.net\" xmlns:testing=\"http://probando.net\" targetNamespace=\"http://probando.net\" elementFormDefault=\"qualified\"><xs:import schemaLocation=\"schema-no_ns.xsd\" /><xs:attributeGroup name=\"_root-_element2-attr4\"><xs:attribute name=\"attr4\" type=\"xs:string\" use=\"optional\" form=\"qualified\" /></xs:attributeGroup><xs:group name=\"d-_root-_element2-_d\"><xs:sequence><xs:element name=\"d\"><xs:complexType /></xs:element></xs:sequence></xs:group></xs:schema>"
				+ lineSeparator;
		assertEquals(expectedOutputTestingNS, xsdOutputStringTestingNS);
	}

	// Test at scenario 2

	/**
	 * Test that checks that the XSDs for scenario 2 are generated correctly.
	 * This scenario consists of two empty root elements of different
	 * namespaces.
	 */
	@Test
	public void testScenario2Normal() {
		when(configuration2.getElementsGlobal()).thenReturn(false);
		when(configuration2.getSimpleTypesGlobal()).thenReturn(true);
		when(configuration2.getComplexTypesGlobal()).thenReturn(true);
		when(configuration2.getStrictValidRootDefinitionWorkaround())
				.thenReturn(true); // VERY IMPORTANT, because elements MUST be
									// declared globally and not be wrapped into
									// groups

		SchemaDocumentGeneratorFactory xsdDocumentGeneratorFactoryInstance = SchemaDocumentGeneratorFactory
				.getInstance();
		SchemaDocumentGenerator<Document> xsdGeneratorTest = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance(NAMESPACE_URI_TEST,NAMESPACE_URI_TEST,new XSDFileNameGeneratorDefaultImpl());

		Format format = Format.getRawFormat();
		format.setLineSeparator(LineSeparator.SYSTEM);
		XMLOutputter outputter = new XMLOutputter(format);
		String lineSeparator = System.getProperty("line.separator");

		Document xsdDocumentTestNS = xsdGeneratorTest.generateSchemaDocument(
				schema2, configuration2);
		String xsdOutputStringTestNS = outputter
				.outputString(xsdDocumentTestNS);

		String expectedOutputTestNS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:test=\"http://prueba.net\" xmlns:testing=\"http://probando.net\" targetNamespace=\"http://prueba.net\" elementFormDefault=\"qualified\"><xs:import namespace=\"http://probando.net\" schemaLocation=\"schema-ns_testing.xsd\" /><xs:complexType name=\"test_root\" /><xs:complexType name=\"testing_raiz\" /><xs:element name=\"root\" type=\"test:test_root\" /></xs:schema>"
				+ lineSeparator;
//		System.out.println(expectedOutputTestNS);
//		System.out.println(xsdOutputStringTestNS);

		
		assertEquals(expectedOutputTestNS, xsdOutputStringTestNS);

		SchemaDocumentGenerator<Document> xsdGeneratorTesting = xsdDocumentGeneratorFactoryInstance
				.getXMLSchemaDocumentGeneratorInstance(NAMESPACE_URI_TESTING,NAMESPACE_URI_TEST,new XSDFileNameGeneratorDefaultImpl());
		Document xsdDocumentTestingNS = xsdGeneratorTesting.generateSchemaDocument(
				schema2, configuration2);
		String xsdOutputStringTestingNS = outputter
				.outputString(xsdDocumentTestingNS);

		String expectedOutputTestingNS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ lineSeparator
				+ "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:test=\"http://prueba.net\" xmlns:testing=\"http://probando.net\" targetNamespace=\"http://probando.net\" elementFormDefault=\"qualified\"><xs:import namespace=\"http://prueba.net\" schemaLocation=\"schema-ns_test.xsd\" /><xs:element name=\"raiz\" type=\"test:testing_raiz\" /></xs:schema>"
				+ lineSeparator;
//		System.out.println(expectedOutputTestingNS);
//		System.out.println(xsdOutputStringTestingNS);
		assertEquals(expectedOutputTestingNS, xsdOutputStringTestingNS);

	}
}
