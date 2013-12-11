package es.upm.dit.xsdinferencer.tests.extraction.extractionimpl;

import static es.upm.dit.xsdinferencer.datastructures.Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedSet;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Table;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.SchemaNode;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.extraction.TypeNameInferencer;
import es.upm.dit.xsdinferencer.extraction.TypesExtractor;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.InferencersFactory;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.KLocalTypeNameInferencer;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.NameTypeNameInferencer;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.TypesExtractorImpl;
import es.upm.dit.xsdinferencer.statistics.BasicStatisticsEntry;
import es.upm.dit.xsdinferencer.statistics.ComplexTypeStatisticsEntry;
import es.upm.dit.xsdinferencer.statistics.Statistics;
import es.upm.dit.xsdinferencer.statistics.ValueAndFrequency;

/**
 * Test for {@link TypesExtractor} and its implementation {@link TypesExtractorImpl} which DOES NOT use mocks for submodules, 
 * so that the whole module is tested.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class TypesExtractorModuleTest {
	
	/**
	 * The inferencers factory
	 */
	protected static InferencersFactory inferencersFactory;
	
	/**
	 * Value sample object with value 1 and frequency 2
	 */
	private static final ValueAndFrequency VALUE_1_FREQUENCY_2 = new ValueAndFrequency(1,2);
	/**
	 * Value sample object with value 0 and frequency 1
	 */
	private static final ValueAndFrequency VALUE_0_FREQUENCY_1 = new ValueAndFrequency(0,1);
	/**
	 * Value sample object with value 1 and frequency 1
	 */
	private static final ValueAndFrequency VALUE_1_FREQUENCY_1 = new ValueAndFrequency(1,1);
	/**
	 * Value sample object with value 2 and frequency 1
	 */
	private static final ValueAndFrequency VALUE_2_FREQUENCY_1 = new ValueAndFrequency(2,1);
	/**
	 * Value sample object with value 5 and frequency 1
	 */
	private static final ValueAndFrequency VALUE_5_FREQUENCY_1 = new ValueAndFrequency(5,1);
	/**
	 * Value sample object with value 6 and frequency 1
	 */
	private static final ValueAndFrequency VALUE_6_FREQUENCY_1 = new ValueAndFrequency(6,1);
	/**
	 * Value sample object with value 7 and frequency 1
	 */
	private static final ValueAndFrequency VALUE_7_FREQUENCY_1 = new ValueAndFrequency(7,1);
	
	/**
	 * {@link TypeNameInferencer} of the type {@link KLocalTypeNameInferencer} with k=1
	 */
	private TypeNameInferencer oneLocalTypeNameInferencer;
	
	/**
	 * {@link TypeNameInferencer} of the type {@link NameTypeNameInferencer}
	 */
	private TypeNameInferencer nameTypeNameInferencer;
	
	/**
	 * Solved namespace URI to prefix mapping for testing scenarios of documents with no namespaces.
	 */
	private final Map<String,String> solvedNamespaceToPrefixMappingNoNS = ImmutableMap.of("", "", XSDInferenceConfiguration.XML_NAMESPACE_URI, "xml");
	/**
	 * {@link TypesExtractor} of the first scenario
	 */
	private TypesExtractor typesExtractor1;
	/**
	 * Solved namespace URI to prefix mapping for the first testing scenario. It will also be used to test static methods.
	 */
	private final Map<String,String> solvedNamespaceToPrefixMapping1 = ImmutableMap.of("", "", "http://prueba.net", "test", "http://probando.net", "unprefixed1", "http://nousado.net", "nu", XSDInferenceConfiguration.XML_NAMESPACE_URI, "xml");
	
	/**
	 * Inference configuration for the first testing scenario.
	 */
	private XSDInferenceConfiguration config1;
	
	/**
	 * Testing XML files for the first testing scenario.
	 */
	private String[] testingXMLs1={
			
			"<root xmlns:nu=\"http://nousado.net\">\n" +
			"	<element1 attr1=\"5\" attr2=\"hola\">value1</element1>\n" +
			"	<element2 xmlns=\"http://probando.net\">true</element2>\n" +
			"   <!-- Aqui va un comentario -->\n" +
			"	<element3>\n" +
			"   <!-- Aqui va otro comentario -->\n" +
			"	</element3>\n" +
			"	<test:element4 xmlns:test=\"http://prueba.net\"/>\n" +
			"</root>\n",

			"<root xmlns:test=\"http://prueba.net\" xmlns:yetanotherpref=\"http://prueba.net\">\n" +
			"	<element1 attr1=\"6\" attr3=\"buenas\" test:attr4=\"con namespace\">probando probando 123</element1>\n" +
			"	<element2 xmlns=\"http://probando.net\">false</element2>\n" +
			"	<element3 attr5=\"7\"/>\n" +
			"	<element3/>\n" +
			"	<element3 xmlns=\"http://prueba.net\"/>\n" +
			"</root>\n",
	};
	
	/**
	 * JDOM2 Document objects for the XML files of first testing scenario.
	 */
	private List<Document> testingXMLDocuments1;
	
	/**
	 * {@link TypesExtractor} of the second scenario
	 */
	private TypesExtractor typesExtractor2;
		
	/**
	 * Inference configuration for the second and third testing scenarios.
	 */
	private XSDInferenceConfiguration config2y3;
	
	/**
	 * Testing XML files for the second testing scenario.
	 */
	private String[] testingXMLs2={
			"<root>\n" +
		"\t<a/>\n" +
		"\t<b>cosilla</b>\n" +
		"\t<c>Esta cosa<e/></c>\n" +
		"\t<d><e/></d>\n" +
		"</root>",

			"<raiz attr=\"value\">\n" +
	"\t<a>0</a>\n" +
	"\t<b><e/></b>\n" +
	"\t<c/>\n" +
	"\t<d/>\n" +
	"</raiz>",
	};
	
	/**
	 * JDOM2 Document objects for the XML files of second testing scenario.
	 */
	private List<Document> testingXMLDocuments2;
	
	/**
	 * {@link TypesExtractor} of the third scenario
	 */
	private TypesExtractor typesExtractor3;
	
	/**
	 * Testing XML files for the third testing scenario.
	 */
	private String[] testingXMLs3={
			"<nodoRaiz/>",
			"<nodoRaiz attr=\"value\"/>",
	};
	
	/**
	 * JDOM2 Document objects for the XML files of first testing scenario.
	 */
	private List<Document> testingXMLDocuments3;
	
	/**
	 * Initial state used at automatons
	 */
	
	private final SchemaElement initialState = new SchemaElement("initial", DEFAULT_PSEUDOELEMENTS_NAMESPACE, null);
	/**
	 * Final state used at automatons
	 */
	
	private final SchemaElement finalState = new SchemaElement("final", DEFAULT_PSEUDOELEMENTS_NAMESPACE, null);

	/**
	 * This method sets up the {@link InferencersFactory} used for this class
	 */
	@BeforeClass
	public static void setUpBeforeClass(){
		inferencersFactory = InferencersFactory.getInstance();
	}
	
	/**
	 * Method executed before any test method. Both scenarios are built although only one is used at a test method.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		oneLocalTypeNameInferencer=new KLocalTypeNameInferencer(1);
		nameTypeNameInferencer=new NameTypeNameInferencer();
		
		SAXBuilder saxBuilder = new SAXBuilder();
		
		//Build first scenario
		
		config1=mock(XSDInferenceConfiguration.class);
		when(config1.getTypeNamesAncestorsSeparator()).thenReturn("-");
		when(config1.getMaxNumberOfDistinctValuesToEnum()).thenReturn(8);
		when(config1.getMinNumberOfDistinctValuesToEnum()).thenReturn(0);
		when(config1.getSimpleTypeInferencer()).thenReturn(XSDInferenceConfiguration.VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL);
		when(config1.getAttributeListInferencer()).thenReturn(XSDInferenceConfiguration.VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL);
		when(config1.getGenerateEnumerations()).thenReturn(false);
		when(config1.getTypeNameInferencer()).thenReturn(oneLocalTypeNameInferencer);
		
		testingXMLDocuments1=new ArrayList<Document>(testingXMLs1.length);
		
		for(int i=0;i<testingXMLs1.length;i++){
			Document document = saxBuilder.build(new StringReader(testingXMLs1[i]));
			testingXMLDocuments1.add(document);
		}
		
		typesExtractor1=new TypesExtractorImpl(testingXMLDocuments1, config1);
		
		//Build second scenario
		
		config2y3=mock(XSDInferenceConfiguration.class);
		when(config2y3.getTypeNamesAncestorsSeparator()).thenReturn("-");
		when(config2y3.getMaxNumberOfDistinctValuesToEnum()).thenReturn(8);
		when(config2y3.getMinNumberOfDistinctValuesToEnum()).thenReturn(0);
		when(config2y3.getSimpleTypeInferencer()).thenReturn(XSDInferenceConfiguration.VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL);
		when(config2y3.getAttributeListInferencer()).thenReturn(XSDInferenceConfiguration.VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL);
		when(config2y3.getGenerateEnumerations()).thenReturn(false);
		when(config2y3.getTypeNameInferencer()).thenReturn(nameTypeNameInferencer);
		
		testingXMLDocuments2=new ArrayList<Document>(testingXMLs2.length);
		for(int i=0;i<testingXMLs2.length;i++){
			Document document = saxBuilder.build(new StringReader(testingXMLs2[i]));
			testingXMLDocuments2.add(document);
		}
		
		typesExtractor2=new TypesExtractorImpl(testingXMLDocuments2, config2y3);
		
		//Build third scenario
		
		testingXMLDocuments3=new ArrayList<Document>(testingXMLs3.length);
		for(int i=0;i<testingXMLs3.length;i++){
			Document document = saxBuilder.build(new StringReader(testingXMLs3[i]));
			testingXMLDocuments3.add(document);
		}
		
		typesExtractor3=new TypesExtractorImpl(testingXMLDocuments3, config2y3);
		
	}
	
	//First scenario methods
	
	/**
	 * 
	 * Test method for static method {@link TypesExtractorImpl#getRealPathOfElementUnfiltered(Element, XSDInferenceConfiguration, boolean, Map)}.
	 * It uses the first testing scenario.
	 */
	@Test
	public void testOnScenario1GetRealPathOfElementUnfiltered() {
		Element element1=testingXMLDocuments1.get(0).getRootElement().getChildren().get(0);
		List<String> element1UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(element1, config1, false, solvedNamespaceToPrefixMapping1);
		assertEquals(2,element1UnfilteredPath.size());
		assertEquals(":root", element1UnfilteredPath.get(0));
		assertEquals(":element1", element1UnfilteredPath.get(1));
		
		Element element2=testingXMLDocuments1.get(0).getRootElement().getChildren().get(1);
		List<String> element2UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(element2, config1, false, solvedNamespaceToPrefixMapping1);
		assertEquals(2,element2UnfilteredPath.size());
		assertEquals(":root", element2UnfilteredPath.get(0));
		assertEquals("unprefixed1:element2", element2UnfilteredPath.get(1));
		
		Element element3=testingXMLDocuments1.get(0).getRootElement().getChildren().get(2);
		List<String> element3UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(element3, config1, false, solvedNamespaceToPrefixMapping1);
		assertEquals(2,element3UnfilteredPath.size());
		assertEquals(":root", element3UnfilteredPath.get(0));
		assertEquals(":element3", element3UnfilteredPath.get(1));
		
		Element testElement4=testingXMLDocuments1.get(0).getRootElement().getChildren().get(3);
		List<String> testElement4UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(testElement4, config1, false, solvedNamespaceToPrefixMapping1);
		assertEquals(2,testElement4UnfilteredPath.size());
		assertEquals(":root", testElement4UnfilteredPath.get(0));
		assertEquals("test:element4", testElement4UnfilteredPath.get(1));
		
		Element testElement3=testingXMLDocuments1.get(1).getRootElement().getChildren().get(4);
		List<String> testElement3UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(testElement3, config1, false, solvedNamespaceToPrefixMapping1);
		assertEquals(2,testElement3UnfilteredPath.size());
		assertEquals(":root", testElement3UnfilteredPath.get(0));
		assertEquals("test:element3", testElement3UnfilteredPath.get(1));
		
	}
	
	/**
	 * Test method for static method {@link TypesExtractorImpl#getRealPathOfAttributeUnfiltered(Element, XSDInferenceConfiguration, Map)}.
	 * It uses the first testing scenario.
	 */
	@Test
	public void testOnScenario1GetRealPathOfAttributeUnfiltered() {
		Attribute attr1=testingXMLDocuments1.get(0).getRootElement().getChildren().get(0).getAttributes().get(0);
		List<String> attr1UnfilteredPath = TypesExtractorImpl.getRealPathOfAttributeUnfiltered(attr1, config1, solvedNamespaceToPrefixMapping1);
		assertEquals(3,attr1UnfilteredPath.size());
		assertEquals(":root", attr1UnfilteredPath.get(0));
		assertEquals(":element1", attr1UnfilteredPath.get(1));
		assertEquals("@:attr1", attr1UnfilteredPath.get(2));
		
		Attribute attr2=testingXMLDocuments1.get(0).getRootElement().getChildren().get(0).getAttributes().get(1);
		List<String> attr2UnfilteredPath = TypesExtractorImpl.getRealPathOfAttributeUnfiltered(attr2, config1, solvedNamespaceToPrefixMapping1);
		assertEquals(3,attr2UnfilteredPath.size());
		assertEquals(":root", attr2UnfilteredPath.get(0));
		assertEquals(":element1", attr2UnfilteredPath.get(1));
		assertEquals("@:attr2", attr2UnfilteredPath.get(2));
		
		Attribute attr3=testingXMLDocuments1.get(1).getRootElement().getChildren().get(0).getAttributes().get(1);
		List<String> attr3UnfilteredPath = TypesExtractorImpl.getRealPathOfAttributeUnfiltered(attr3, config1, solvedNamespaceToPrefixMapping1);
		assertEquals(3,attr3UnfilteredPath.size());
		assertEquals(":root", attr3UnfilteredPath.get(0));
		assertEquals(":element1", attr3UnfilteredPath.get(1));
		assertEquals("@:attr3", attr3UnfilteredPath.get(2));
		
		Attribute attr4=testingXMLDocuments1.get(1).getRootElement().getChildren().get(0).getAttributes().get(2);
		List<String> attr4UnfilteredPath = TypesExtractorImpl.getRealPathOfAttributeUnfiltered(attr4, config1, solvedNamespaceToPrefixMapping1);
		assertEquals(3,attr4UnfilteredPath.size());
		assertEquals(":root", attr4UnfilteredPath.get(0));
		assertEquals(":element1", attr4UnfilteredPath.get(1));
		assertEquals("@test:attr4", attr4UnfilteredPath.get(2));
		
		Attribute attr5=testingXMLDocuments1.get(1).getRootElement().getChildren().get(2).getAttributes().get(0);
		List<String> attr5UnfilteredPath = TypesExtractorImpl.getRealPathOfAttributeUnfiltered(attr5, config1, solvedNamespaceToPrefixMapping1);
		assertEquals(3,attr5UnfilteredPath.size());
		assertEquals(":root", attr5UnfilteredPath.get(0));
		assertEquals(":element3", attr5UnfilteredPath.get(1));
		assertEquals("@:attr5", attr5UnfilteredPath.get(2));
		
		
	}
	
	/**
	 * Test method for static method {@link TypesExtractorImpl#getSuitablePath(List)}.
	 * It uses the first testing scenario.
	 */
	@Test
	public void testOnScenario1GetSuitablePath(){

		Element element1=testingXMLDocuments1.get(0).getRootElement().getChildren().get(0);
		List<String> element1UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(element1, config1, false, solvedNamespaceToPrefixMapping1);
		List<String> element1SuitablePath = TypesExtractorImpl.getSuitablePath(element1UnfilteredPath);
		assertEquals(2,element1SuitablePath.size());
		assertEquals("_root", element1SuitablePath.get(0));
		assertEquals("_element1", element1SuitablePath.get(1));

		Element element2=testingXMLDocuments1.get(0).getRootElement().getChildren().get(1);
		List<String> element2UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(element2, config1, false, solvedNamespaceToPrefixMapping1);
		List<String> element2SuitablePath = TypesExtractorImpl.getSuitablePath(element2UnfilteredPath);
		assertEquals(2,element2SuitablePath.size());
		assertEquals("_root", element2SuitablePath.get(0));
		assertEquals("unprefixed1_element2", element2SuitablePath.get(1));

		Element element3=testingXMLDocuments1.get(0).getRootElement().getChildren().get(2);
		List<String> element3UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(element3, config1, false, solvedNamespaceToPrefixMapping1);
		List<String> element3SuitablePath = TypesExtractorImpl.getSuitablePath(element3UnfilteredPath);
		assertEquals(2,element3SuitablePath.size());
		assertEquals("_root", element3SuitablePath.get(0));
		assertEquals("_element3", element3SuitablePath.get(1));

		Element testElement4=testingXMLDocuments1.get(0).getRootElement().getChildren().get(3);
		List<String> testElement4UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(testElement4, config1, false, solvedNamespaceToPrefixMapping1);
		List<String> testElement4SuitablePath = TypesExtractorImpl.getSuitablePath(testElement4UnfilteredPath);
		assertEquals(2,testElement4SuitablePath.size());
		assertEquals("_root", testElement4SuitablePath.get(0));
		assertEquals("test_element4", testElement4SuitablePath.get(1));

		Element testElement3=testingXMLDocuments1.get(1).getRootElement().getChildren().get(4);
		List<String> testElement3UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(testElement3, config1, false, solvedNamespaceToPrefixMapping1);
		List<String> testElement3SuitablePath = TypesExtractorImpl.getSuitablePath(testElement3UnfilteredPath);
		assertEquals(2,testElement3SuitablePath.size());
		assertEquals("_root", testElement3SuitablePath.get(0));
		assertEquals("test_element3", testElement3SuitablePath.get(1));
		
	}
	
	/**
	 * Test method for static method {@link TypesExtractorImpl#filterAndJoinRealPath(List)} when the path ends on an element.
	 * It uses the first testing scenario.
	 */
	@Test
	public void testOnScenario1FilterAndJoinRealPathOfElement(){
		Element element1=testingXMLDocuments1.get(0).getRootElement().getChildren().get(0);
		List<String> element1UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(element1, config1, false, solvedNamespaceToPrefixMapping1);
		String element1Path = TypesExtractorImpl.filterAndJoinRealPath(element1UnfilteredPath);
		assertEquals("/root/element1",element1Path);
		
		Element element2=testingXMLDocuments1.get(0).getRootElement().getChildren().get(1);
		List<String> element2UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(element2, config1, false, solvedNamespaceToPrefixMapping1);
		String element2Path = TypesExtractorImpl.filterAndJoinRealPath(element2UnfilteredPath);
		assertEquals("/root/unprefixed1:element2",element2Path);
		
		Element element3=testingXMLDocuments1.get(0).getRootElement().getChildren().get(2);
		List<String> element3UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(element3, config1, false, solvedNamespaceToPrefixMapping1);
		String element3Path = TypesExtractorImpl.filterAndJoinRealPath(element3UnfilteredPath);
		assertEquals("/root/element3",element3Path);
		
		Element testElement4=testingXMLDocuments1.get(0).getRootElement().getChildren().get(3);
		List<String> testElement4UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(testElement4, config1, false, solvedNamespaceToPrefixMapping1);
		String testElement4Path = TypesExtractorImpl.filterAndJoinRealPath(testElement4UnfilteredPath);
		assertEquals("/root/test:element4",testElement4Path);
		
		Element testElement3=testingXMLDocuments1.get(1).getRootElement().getChildren().get(4);
		List<String> testElement3UnfilteredPath = TypesExtractorImpl.getRealPathOfElementUnfiltered(testElement3, config1, false, solvedNamespaceToPrefixMapping1);
		String testElement3Path = TypesExtractorImpl.filterAndJoinRealPath(testElement3UnfilteredPath);
		assertEquals("/root/test:element3",testElement3Path);
	}
	
	/**
	 * Test method for static method {@link TypesExtractorImpl#filterAndJoinRealPath(List)} when the path ends on an attribute.
	 * It uses the first testing scenario.
	 */
	@Test
	public void testOnScenario1FilterAndJoinRealPathOfAttribute(){
		Attribute attr1=testingXMLDocuments1.get(0).getRootElement().getChildren().get(0).getAttributes().get(0);
		List<String> attr1UnfilteredPath = TypesExtractorImpl.getRealPathOfAttributeUnfiltered(attr1, config1, solvedNamespaceToPrefixMapping1);
		String attr1Path = TypesExtractorImpl.filterAndJoinRealPath(attr1UnfilteredPath);
		assertEquals("/root/element1/@attr1",attr1Path);
		
		Attribute attr2=testingXMLDocuments1.get(0).getRootElement().getChildren().get(0).getAttributes().get(1);
		List<String> attr2UnfilteredPath = TypesExtractorImpl.getRealPathOfAttributeUnfiltered(attr2, config1, solvedNamespaceToPrefixMapping1);
		String attr2Path = TypesExtractorImpl.filterAndJoinRealPath(attr2UnfilteredPath);
		assertEquals("/root/element1/@attr2",attr2Path);
		
		Attribute attr3=testingXMLDocuments1.get(1).getRootElement().getChildren().get(0).getAttributes().get(1);
		List<String> attr3UnfilteredPath = TypesExtractorImpl.getRealPathOfAttributeUnfiltered(attr3, config1, solvedNamespaceToPrefixMapping1);
		String attr3Path = TypesExtractorImpl.filterAndJoinRealPath(attr3UnfilteredPath);
		assertEquals("/root/element1/@attr3",attr3Path);
		
		Attribute attr4=testingXMLDocuments1.get(1).getRootElement().getChildren().get(0).getAttributes().get(2);
		List<String> attr4UnfilteredPath = TypesExtractorImpl.getRealPathOfAttributeUnfiltered(attr4, config1, solvedNamespaceToPrefixMapping1);
		String attr4Path = TypesExtractorImpl.filterAndJoinRealPath(attr4UnfilteredPath);
		assertEquals("/root/element1/@test:attr4",attr4Path);
		
		Attribute attr5=testingXMLDocuments1.get(1).getRootElement().getChildren().get(2).getAttributes().get(0);
		List<String> attr5UnfilteredPath = TypesExtractorImpl.getRealPathOfAttributeUnfiltered(attr5, config1, solvedNamespaceToPrefixMapping1);
		String attr5Path = TypesExtractorImpl.filterAndJoinRealPath(attr5UnfilteredPath);
		assertEquals("/root/element3/@attr5",attr5Path);
		
	}
	
	/**
	 * Test method that checks that all the SchemElement have been created properly.
	 */
	@Test
	public void testOnScenario1SchemaElements(){
		Schema schema = typesExtractor1.getInitalSchema();
		Table<String,String,SchemaElement> elements =  schema.getElements();
		assertEquals(6,elements.size());
		
		SchemaElement root=elements.get("", "root");
		assertNotNull(root);
		assertEquals("root",root.getName());
		assertEquals("",root.getNamespace());
		assertEquals("_root",root.getType().getName());
		assertTrue(root.isValidRoot());
		
		SchemaElement element1=elements.get("", "_root-element1");
		assertNotNull(element1);
		assertEquals("element1",element1.getName());
		assertEquals("",element1.getNamespace());
		assertEquals("_root-_element1",element1.getType().getName());
		assertFalse(element1.isValidRoot());
		
		SchemaElement element2=elements.get("http://probando.net", "_root-element2");
		assertNotNull(element2);
		assertEquals("element2",element2.getName());
		assertEquals("http://probando.net",element2.getNamespace());
		assertEquals("_root-unprefixed1_element2",element2.getType().getName());
		assertFalse(element2.isValidRoot());
		
		SchemaElement element3=elements.get("", "_root-element3");
		assertNotNull(element3);
		assertEquals("element3",element3.getName());
		assertEquals("",element3.getNamespace());
		assertEquals("_root-_element3",element3.getType().getName());
		assertFalse(element3.isValidRoot());
		
		SchemaElement testElement3=elements.get("http://prueba.net", "_root-element3");
		assertNotNull(testElement3);
		assertEquals("element3",testElement3.getName());
		assertEquals("http://prueba.net",testElement3.getNamespace());
		assertEquals("_root-test_element3",testElement3.getType().getName());
		assertFalse(testElement3.isValidRoot());
		
		SchemaElement testElement4=elements.get("http://prueba.net", "_root-element4");
		assertNotNull(testElement4);
		assertEquals("element4",testElement4.getName());
		assertEquals("http://prueba.net",testElement4.getNamespace());
		assertEquals("_root-test_element4",testElement4.getType().getName());
		assertFalse(testElement4.isValidRoot());
	}
	
	/**
	 * Test that checks that all the attributes have been inferred properly
	 */
	@Test
	public void testOnScenario1SchemaAttributes(){
		Schema schema = typesExtractor1.getInitalSchema();
		Table <String,String,SchemaAttribute> attributes = schema.getAttributes();
		assertEquals(5, attributes.size());
		
		SchemaAttribute attr1=attributes.get("","_root-_element1-attr1");
		assertNotNull(attr1);
		assertEquals("attr1",attr1.getName());
		assertEquals("",attr1.getNamespace());
		assertFalse(attr1.isOptional());
		assertEquals("xs:integer",attr1.getSimpleType().getBuiltinType());
		assertFalse(attr1.getSimpleType().isEnum());
		assertEquals(2,attr1.getSimpleType().enumerationCount());
		assertTrue(attr1.getSimpleType().enumerationContains("5"));
		assertTrue(attr1.getSimpleType().enumerationContains("6"));
		
		SchemaAttribute attr2=attributes.get("","_root-_element1-attr2");
		assertNotNull(attr2);
		assertEquals("attr2",attr2.getName());
		assertEquals("",attr2.getNamespace());
		assertTrue(attr2.isOptional());
		assertEquals("xs:string",attr2.getSimpleType().getBuiltinType());
		assertFalse(attr2.getSimpleType().isEnum());
		assertEquals(1,attr2.getSimpleType().enumerationCount());
		assertTrue(attr2.getSimpleType().enumerationContains("hola"));
		
		SchemaAttribute attr3=attributes.get("","_root-_element1-attr3");
		assertNotNull(attr3);
		assertEquals("attr3",attr3.getName());
		assertEquals("",attr3.getNamespace());
		assertTrue(attr3.isOptional());
		assertEquals("xs:string",attr3.getSimpleType().getBuiltinType());
		assertFalse(attr3.getSimpleType().isEnum());
		assertEquals(1,attr3.getSimpleType().enumerationCount());
		assertTrue(attr3.getSimpleType().enumerationContains("buenas"));
		
		SchemaAttribute attr4=attributes.get("http://prueba.net","_root-_element1-attr4");
		assertNotNull(attr4);
		assertEquals("attr4",attr4.getName());
		assertEquals("http://prueba.net",attr4.getNamespace());
		assertTrue(attr4.isOptional());
		assertEquals("xs:string",attr4.getSimpleType().getBuiltinType());
		assertFalse(attr4.getSimpleType().isEnum());
		assertEquals(1,attr4.getSimpleType().enumerationCount());
		assertTrue(attr4.getSimpleType().enumerationContains("con namespace"));
		
		SchemaAttribute attr5=attributes.get("","_root-_element3-attr5");
		assertNotNull(attr5);
		assertEquals("attr5",attr5.getName());
		assertEquals("",attr5.getNamespace());
		assertTrue(attr5.isOptional());
		assertEquals("xs:integer",attr5.getSimpleType().getBuiltinType());
		assertFalse(attr5.getSimpleType().isEnum());
		assertEquals(1,attr5.getSimpleType().enumerationCount());
		assertTrue(attr5.getSimpleType().enumerationContains("7"));
	}
	
	/**
	 * Test that checks that the namespace-prefix mappings are correctly generated
	 */
	@Test
	public void testOnScenario1NamespacePrefixMappings(){
		Schema schema = typesExtractor1.getInitalSchema();
		Map<String, SortedSet<String>> prefixNamespaceMapping = schema.getNamespacesToPossiblePrefixMappingModifiable();
		assertEquals(5,prefixNamespaceMapping.size());
		
		Set<String> prefixesOfEmptyNamespace = prefixNamespaceMapping.get("");
		assertNotNull(prefixesOfEmptyNamespace);
		assertEquals(1,prefixesOfEmptyNamespace.size());
		assertTrue(prefixesOfEmptyNamespace.contains(""));
		
		Set<String> prefixesOfTestNamespace = prefixNamespaceMapping.get("http://prueba.net");
		assertNotNull(prefixesOfTestNamespace);
		assertEquals(3,prefixesOfTestNamespace.size());
		assertTrue(prefixesOfTestNamespace.contains("test"));
		assertTrue(prefixesOfTestNamespace.contains("yetanotherpref"));
		assertTrue(prefixesOfTestNamespace.contains(""));
		
		Set<String> prefixesOfTest2Namespace = prefixNamespaceMapping.get("http://probando.net");
		assertNotNull(prefixesOfTest2Namespace);
		assertEquals(1,prefixesOfTest2Namespace.size());
		assertTrue(prefixesOfTest2Namespace.contains(""));
	}
	
	/**
	 * This method checks that the prefix-namespace mappings have been made properly on the first scenario.
	 */
	@Test
	public void testOnScenario1SolvedMappings(){
		Schema schema = typesExtractor1.getInitalSchema();
		NavigableMap<String, String> solvedMappingsBetweenNamespaceURIsAndPrefixes = schema.getSolvedNamespaceMappings();
		assertEquals(solvedNamespaceToPrefixMapping1,solvedMappingsBetweenNamespaceURIsAndPrefixes);
	}
	
	/**
	 * Test that checks that the correct amount of ComplexTypes is extracted on the first scenario.
	 */
	@Test
	public void testOnScenario1ComplexTypesCount(){
		Schema schema = typesExtractor1.getInitalSchema();
		assertEquals(6,schema.getComplexTypes().size());
	}

	/**
	 * This test checks that the complex type _root is correctly generated on the first scenario.
	 */
	@Test
	public void testOnScenario1ComplexTypeRoot() {
		Schema schema = typesExtractor1.getInitalSchema();
		
		ComplexType complexTypeRoot=schema.getComplexTypes().get("_root");
		assertEquals("_root",complexTypeRoot.getName());
		
		//Source element
		
		Set<String> sourceKeys = complexTypeRoot.getSourceElementNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":root"));
		
		//Automaton
		
		ExtendedAutomaton complexTypeRootAutomaton=complexTypeRoot.getAutomaton();
		
		assertEquals(7,complexTypeRootAutomaton.nodeCount());
		assertEquals(8,complexTypeRootAutomaton.edgeCount());
		
		SchemaElement node1=schema.getElements().get("", "_root-element1");
		SchemaElement node2=schema.getElements().get("http://probando.net", "_root-element2");
		SchemaElement node3=schema.getElements().get("", "_root-element3");
		SchemaElement node3b=schema.getElements().get("http://prueba.net", "_root-element3");
		SchemaElement node4=schema.getElements().get("http://prueba.net", "_root-element4");
		
		assertTrue(complexTypeRootAutomaton.containsNode(initialState));
		assertEquals(initialState,complexTypeRootAutomaton.getInitialState());
		assertEquals(0,complexTypeRootAutomaton.getIncomingEdges(initialState).size());
		Map<SchemaElement, Long> initialStateOutgoingEdges = complexTypeRootAutomaton.getOutgoingEdges(initialState);
		assertEquals(1,initialStateOutgoingEdges.size());
		assertTrue(initialStateOutgoingEdges.containsKey(node1));
		assertEquals(2,initialStateOutgoingEdges.get(node1).longValue());
		
		assertTrue(complexTypeRootAutomaton.containsNode(node1));
		Map<SchemaElement, Long> node1IncomingEdges=complexTypeRootAutomaton.getIncomingEdges(node1);
		assertEquals(1,node1IncomingEdges.size());
		assertTrue(node1IncomingEdges.containsKey(initialState));
		assertEquals(2,node1IncomingEdges.get(initialState).longValue());
		Map<SchemaElement, Long> node1OutgoingEdges=complexTypeRootAutomaton.getOutgoingEdges(node1);
		assertEquals(1,node1OutgoingEdges.size());
		assertTrue(node1OutgoingEdges.containsKey(node2));
		assertEquals(2,node1OutgoingEdges.get(node2).longValue());
		
		assertTrue(complexTypeRootAutomaton.containsNode(node2));
		Map<SchemaElement, Long> node2IncomingEdges=complexTypeRootAutomaton.getIncomingEdges(node2);
		assertEquals(1,node2IncomingEdges.size());
		assertTrue(node2IncomingEdges.containsKey(node1));
		assertEquals(new Long(2),node2IncomingEdges.get(node1));
		Map<SchemaElement, Long> node2OutgoingEdges=complexTypeRootAutomaton.getOutgoingEdges(node2);
		assertEquals(1,node2OutgoingEdges.size());
		assertTrue(node2OutgoingEdges.containsKey(node3));
		assertEquals(2,node2OutgoingEdges.get(node3).longValue());
		
		assertTrue(complexTypeRootAutomaton.containsNode(node3));
		Map<SchemaElement, Long> node3IncomingEdges=complexTypeRootAutomaton.getIncomingEdges(node3);
		assertEquals(2,node3IncomingEdges.size());
		assertTrue(node3IncomingEdges.containsKey(node2));
		assertEquals(2,node3IncomingEdges.get(node2).longValue());
		assertTrue(node3IncomingEdges.containsKey(node3));
		assertEquals(1,node3IncomingEdges.get(node3).longValue());
		Map<SchemaElement, Long> node3OutgoingEdges=complexTypeRootAutomaton.getOutgoingEdges(node3);
		assertEquals(3,node3OutgoingEdges.size());
		assertTrue(node3OutgoingEdges.containsKey(node3));
		assertEquals(1,node3OutgoingEdges.get(node3).longValue());
		assertTrue(node3OutgoingEdges.containsKey(node4));
		assertEquals(1,node3OutgoingEdges.get(node4).longValue());
		assertTrue(node3OutgoingEdges.containsKey(node3b));
		assertEquals(1,node3OutgoingEdges.get(node3b).longValue());
		
		assertTrue(complexTypeRootAutomaton.containsNode(node4));
		Map<SchemaElement, Long> node4IncomingEdges=complexTypeRootAutomaton.getIncomingEdges(node4);
		assertEquals(1,node4IncomingEdges.size());
		assertTrue(node4IncomingEdges.containsKey(node3));
		assertEquals(1,node4IncomingEdges.get(node3).longValue());
		Map<SchemaElement, Long> node4OutgoingEdges=complexTypeRootAutomaton.getOutgoingEdges(node4);
		assertEquals(1,node4OutgoingEdges.size());
		assertTrue(node4OutgoingEdges.containsKey(finalState));
		assertEquals(1,node4OutgoingEdges.get(finalState).longValue());
		
		assertTrue(complexTypeRootAutomaton.containsNode(node3b));
		Map<SchemaElement, Long> node3bIncomingEdges=complexTypeRootAutomaton.getIncomingEdges(node3b);
		assertEquals(1,node3bIncomingEdges.size());
		assertTrue(node3bIncomingEdges.containsKey(node3));
		assertEquals(1,node3bIncomingEdges.get(node3).longValue());
		Map<SchemaElement, Long> node3bOutgoingEdges=complexTypeRootAutomaton.getOutgoingEdges(node3b);
		assertEquals(1,node3bOutgoingEdges.size());
		assertTrue(node3bOutgoingEdges.containsKey(finalState));
		assertEquals(1,node3bOutgoingEdges.get(finalState).longValue());
		
		assertTrue(complexTypeRootAutomaton.containsNode(finalState));
		assertEquals(finalState,complexTypeRootAutomaton.getFinalState());
		Map<SchemaElement, Long> finalStateIncomingEdges = complexTypeRootAutomaton.getIncomingEdges(finalState);
		assertEquals(2,finalStateIncomingEdges.size());
		assertTrue(finalStateIncomingEdges.containsKey(node3b));
		assertEquals(1,finalStateIncomingEdges.get(node3b).longValue());
		assertTrue(finalStateIncomingEdges.containsKey(node4));
		assertEquals(1,finalStateIncomingEdges.get(node4).longValue());
		assertEquals(0,complexTypeRootAutomaton.getOutgoingEdges(finalState).size());
		
		//Attribute list
		
		assertTrue(complexTypeRoot.getAttributeList().isEmpty());
		
		//Text SimpleType
		
		assertTrue(complexTypeRoot.getTextSimpleType().consistOnlyOfWhitespaceCharacters());
		
		//Comments
		
		assertEquals(1,complexTypeRoot.getComments().size());
		assertTrue(complexTypeRoot.getComments().contains(" Aqui va un comentario "));
	}

	/**
	 * This test checks that the complex type _root-_element1 is correctly generated on the first scenario.
	 */
	@Test
	public void testOnScenario1ComplexTypeRootElement1() {
		Schema schema = typesExtractor1.getInitalSchema();
		
		ComplexType complexTypeRootElement1 = schema.getComplexTypes().get("_root-_element1");
		
		//Source element
		
		Set<String> sourceKeys = complexTypeRootElement1.getSourceElementNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":element1"));
		
		//Automaton
		
		ExtendedAutomaton complexTypeRootElement1Automaton = complexTypeRootElement1.getAutomaton();
		assertEquals(2,complexTypeRootElement1Automaton.nodeCount());//The initial and the final state
		assertTrue(complexTypeRootElement1Automaton.containsNode(initialState));
		assertTrue(complexTypeRootElement1Automaton.containsNode(finalState));
		
		
		//Attribute list 
		//Note that the correctness of the attributes is tested in testSchemaAttributes() and that checks will not be repeated here.
		
		List<SchemaAttribute> complexTypeRootElement1AttrList = complexTypeRootElement1.getAttributeList();
		assertEquals(4,complexTypeRootElement1AttrList.size());
		assertTrue(complexTypeRootElement1AttrList.contains(schema.getAttributes().get("","_root-_element1-attr1")));
		assertTrue(complexTypeRootElement1AttrList.contains(schema.getAttributes().get("","_root-_element1-attr2")));
		assertTrue(complexTypeRootElement1AttrList.contains(schema.getAttributes().get("","_root-_element1-attr3")));
		assertTrue(complexTypeRootElement1AttrList.contains(schema.getAttributes().get("http://prueba.net","_root-_element1-attr4")));
		
		//Text simple type
		
		SimpleType complexTypeRootElement1TextSimpleType = complexTypeRootElement1.getTextSimpleType();
		assertEquals("xs:string",complexTypeRootElement1TextSimpleType.getBuiltinType());
		assertFalse(complexTypeRootElement1TextSimpleType.isEnum());
		assertEquals(2,complexTypeRootElement1TextSimpleType.enumerationCount());
		assertTrue(complexTypeRootElement1TextSimpleType.enumerationContainsAll(ImmutableSet.of("value1", "probando probando 123")));
	}

	/**
	 * This test checks that the complex type _root-unprefixed_element2 is correctly generated on the first scenario.
	 */
	@Test
	public void testOnScenario1ComplexTypeRootElement2() {
		Schema schema = typesExtractor1.getInitalSchema();
		
		ComplexType complexTypeRootElement2 = schema.getComplexTypes().get("_root-unprefixed1_element2");
		
		//Source element
		
		Set<String> sourceKeys = complexTypeRootElement2.getSourceElementNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains("http://probando.net:element2"));
		
		//Automaton
		
		ExtendedAutomaton complexTypeRootElement2Automaton = complexTypeRootElement2.getAutomaton();
		assertEquals(2,complexTypeRootElement2Automaton.nodeCount());//The initial and the final state
		assertTrue(complexTypeRootElement2Automaton.containsNode(initialState));
		assertTrue(complexTypeRootElement2Automaton.containsNode(finalState));
		
		//Attribute list
		
		assertTrue(complexTypeRootElement2.getAttributeList().isEmpty());
		
		
		//Text simple type
		
		SimpleType complexTypeRootElement2TextSimpleType = complexTypeRootElement2.getTextSimpleType();
		assertEquals("xs:boolean",complexTypeRootElement2TextSimpleType.getBuiltinType());
		assertFalse(complexTypeRootElement2TextSimpleType.isEnum());
		assertEquals(2,complexTypeRootElement2TextSimpleType.enumerationCount());
		assertTrue(complexTypeRootElement2TextSimpleType.enumerationContainsAll(ImmutableSet.of("true", "false")));
	}

	/**
	 * This test checks that the complex type _root-_element3 is correctly generated on the first scenario.
	 */
	@Test
	public void testOnScenario1ComplexTypeRootElement3() {
		Schema schema = typesExtractor1.getInitalSchema();
		
		ComplexType complexTypeRootElement3 = schema.getComplexTypes().get("_root-_element3");
		
		//Source element
		
		Set<String> sourceKeys = complexTypeRootElement3.getSourceElementNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":element3"));	
		
		//Automaton
		
		ExtendedAutomaton complexTypeRootElement3Automaton = complexTypeRootElement3.getAutomaton();
		assertEquals(2,complexTypeRootElement3Automaton.nodeCount());//The initial and the final state
		assertTrue(complexTypeRootElement3Automaton.containsNode(initialState));
		assertTrue(complexTypeRootElement3Automaton.containsNode(finalState));
		
		//Attribute list
		
		List<SchemaAttribute> complexTypeRootElement3AttrList = complexTypeRootElement3.getAttributeList();
		assertEquals(1,complexTypeRootElement3AttrList.size());
		assertTrue(complexTypeRootElement3AttrList.contains(schema.getAttributes().get("","_root-_element3-attr5")));
		
		
		//Text simple type
		
		assertTrue(complexTypeRootElement3.getTextSimpleType().consistOnlyOfWhitespaceCharacters());
		
		//Comments
		
		assertEquals(1,complexTypeRootElement3.getComments().size());
		assertTrue(complexTypeRootElement3.getComments().contains(" Aqui va otro comentario "));
	}
	
	/**
	 * This test checks that the complex type _root-test_element4 is correctly generated on the first scenario.
	 */
	@Test
	public void testOnScenario1ComplexTypeRootTestElement3() {
		Schema schema = typesExtractor1.getInitalSchema();
		ComplexType complexTypeRootTestElement3 = schema.getComplexTypes().get("_root-test_element3");
		
		//Source element
		
		Set<String> sourceKeys = complexTypeRootTestElement3.getSourceElementNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains("http://prueba.net:element3"));
		
		//Automaton
		
		ExtendedAutomaton complexTypeRootTestElement3Automaton = complexTypeRootTestElement3.getAutomaton();
		assertEquals(2,complexTypeRootTestElement3Automaton.nodeCount());//The initial and the final state
		assertTrue(complexTypeRootTestElement3Automaton.containsNode(initialState));
		assertTrue(complexTypeRootTestElement3Automaton.containsNode(finalState));
		
		//Attribute list
		
		assertTrue(complexTypeRootTestElement3.getAttributeList().isEmpty());
		
		
		//Text simple type
		
		assertTrue(complexTypeRootTestElement3.getTextSimpleType().isEmpty());
	}

	/**
	 * This test checks that the complex type _root-test_element4 is correctly generated on the first scenario.
	 */
	@Test
	public void testOnScenario1ComplexTypeRootTestElement4() {
		Schema schema = typesExtractor1.getInitalSchema();
		ComplexType complexTypeRootTestElement4 = schema.getComplexTypes().get("_root-test_element4");
		
		//Source element
		
		Set<String> sourceKeys = complexTypeRootTestElement4.getSourceElementNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains("http://prueba.net:element4"));
		
		//Automaton
		
		ExtendedAutomaton complexTypeRootTestElement4Automaton = complexTypeRootTestElement4.getAutomaton();
		assertEquals(2,complexTypeRootTestElement4Automaton.nodeCount());//The initial and the final state
		assertTrue(complexTypeRootTestElement4Automaton.containsNode(initialState));
		assertTrue(complexTypeRootTestElement4Automaton.containsNode(finalState));
		
		//Attribute list
		
		assertTrue(complexTypeRootTestElement4.getAttributeList().isEmpty());
		
		
		//Text simple type
		
		assertTrue(complexTypeRootTestElement4.getTextSimpleType().isEmpty());
	}
	
	/**
	 * This method checks that the general statistics of the first scenario are correctly generated.
	 */
	@Test
	public void testOnScenario1GeneralStatistics(){
		Schema schema = typesExtractor1.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		//Input documents count
		assertEquals(2, statistics.getInputDocumentsCount());
		
		//Depth
		assertEquals(2,statistics.getMaxDepth());
		assertEquals(1.8167, statistics.getAvgDepth(), 0.0001);
		
		//Width
		assertEquals(5,statistics.getMaxWidth());
		assertEquals(2.75,statistics.getAvgWidth(),0.01);
	}
	
	/**
	 * This method checks that the root elements occurrences info is correctly generated on the first scenario.
	 */
	@Test
	public void testOnScenario1RootElementsOccurrences(){
		Schema schema = typesExtractor1.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Map<SchemaElement, Integer> rootElementOccurrences = statistics.getRootElementOccurrences();
		assertEquals(1,rootElementOccurrences.keySet().size());
		assertTrue(rootElementOccurrences.containsKey(schema.getElements().get("", "root")));
	}
	
	/**
	 * This method checks that the elements at path information of the first scenario is well generated.
	 */
	@Test
	public void testOnScenario1ElementsAtPathStatistics(){
		Schema schema = typesExtractor1.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		Map<String, BasicStatisticsEntry> elementAtPathInfo = statistics.getElementAtPathInfo();
		assertEquals(6,elementAtPathInfo.size());
				
		//root
		BasicStatisticsEntry rootEntry = elementAtPathInfo.get("/root");
		assertEquals(1,rootEntry.getAverage(),0.001);
		assertEquals(0,rootEntry.getVariance(),0.001);
		assertEquals(1,rootEntry.getConditionedAverage(),0.001);
		assertEquals(0,rootEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),rootEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_2,rootEntry.getMax());
		assertEquals(VALUE_1_FREQUENCY_2,rootEntry.getMin());
		assertEquals(2,rootEntry.getTotal());
		assertEquals(1,rootEntry.getNonZeroRatio(),0.001);
		assertEquals(0,rootEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,rootEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//element1
		BasicStatisticsEntry element1Entry = elementAtPathInfo.get("/root/element1");
		assertEquals(1,element1Entry.getAverage(),0.001);
		assertEquals(0,element1Entry.getVariance(),0.001);
		assertEquals(1,element1Entry.getConditionedAverage(),0.001);
		assertEquals(0,element1Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element1Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_2,element1Entry.getMax());
		assertEquals(VALUE_1_FREQUENCY_2,element1Entry.getMin());
		assertEquals(2,element1Entry.getTotal());
		assertEquals(1,element1Entry.getNonZeroRatio(),0.001);
		assertEquals(0,element1Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,element1Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//unprefixed1:element2
		BasicStatisticsEntry element2Entry = elementAtPathInfo.get("/root/unprefixed1:element2");
		assertEquals(1,element2Entry.getAverage(),0.001);
		assertEquals(0,element2Entry.getVariance(),0.001);
		assertEquals(1,element2Entry.getConditionedAverage(),0.001);
		assertEquals(0,element2Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element2Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_2,element2Entry.getMax());
		assertEquals(VALUE_1_FREQUENCY_2,element2Entry.getMin());
		assertEquals(2,element2Entry.getTotal());
		assertEquals(1,element2Entry.getNonZeroRatio(),0.001);
		assertEquals(0,element2Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,element2Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//element3
		BasicStatisticsEntry element3Entry = elementAtPathInfo.get("/root/element3");
		assertEquals(1.5,element3Entry.getAverage(),0.001);
		assertEquals(0.25,element3Entry.getVariance(),0.001);
		assertEquals(1.5,element3Entry.getConditionedAverage(),0.001);
		assertEquals(0.25,element3Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element3Entry.getMode());
		assertEquals(VALUE_2_FREQUENCY_1,element3Entry.getMax());
		assertEquals(VALUE_1_FREQUENCY_1,element3Entry.getMin());
		assertEquals(3,element3Entry.getTotal());
		assertEquals(1,element3Entry.getNonZeroRatio(),0.001);
		assertEquals(0.333,element3Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0.333,element3Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//test:element3
		BasicStatisticsEntry testElement3Entry = elementAtPathInfo.get("/root/test:element3");
		assertEquals(0.5,testElement3Entry.getAverage(),0.001);
		assertEquals(0.25,testElement3Entry.getVariance(),0.001);
		assertEquals(1,testElement3Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,testElement3Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),testElement3Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,testElement3Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,testElement3Entry.getMin());
		assertEquals(1,testElement3Entry.getTotal());
		assertEquals(0.5,testElement3Entry.getNonZeroRatio(),0.001);
		assertEquals(1,testElement3Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,testElement3Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//test:element4
		BasicStatisticsEntry testElement4Entry = elementAtPathInfo.get("/root/test:element4");
		assertEquals(0.5,testElement4Entry.getAverage(),0.001);
		assertEquals(0.25,testElement4Entry.getVariance(),0.001);
		assertEquals(1,testElement4Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,testElement4Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),testElement4Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,testElement4Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,testElement4Entry.getMin());
		assertEquals(1,testElement4Entry.getTotal());
		assertEquals(0.5,testElement4Entry.getNonZeroRatio(),0.001);
		assertEquals(1,testElement4Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,testElement4Entry.getConditionedStandardDeviationAverageRatio(),0.001);
	}
	
	/**
	 * This method checks that the attributes at path information of the first scenario is well generated.
	 */
	@Test
	public void testOnScenario1AttributesAtPathInfo(){
		Schema schema = typesExtractor1.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		Map<String, BasicStatisticsEntry> attributeAtPathInfo = statistics.getAttributeAtPathInfo();
		
		//attr1
		BasicStatisticsEntry attr1Entry = attributeAtPathInfo.get("/root/element1/@attr1");
		assertEquals(1,attr1Entry.getAverage(),0.001);
		assertEquals(0,attr1Entry.getVariance(),0.001);
		assertEquals(1,attr1Entry.getConditionedAverage(),0.001);
		assertEquals(0,attr1Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr1Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_2,attr1Entry.getMax());
		assertEquals(VALUE_1_FREQUENCY_2,attr1Entry.getMin());
		assertEquals(2,attr1Entry.getTotal());
		assertEquals(1,attr1Entry.getNonZeroRatio(),0.001);
		assertEquals(0,attr1Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr1Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//attr2
		BasicStatisticsEntry attr2Entry = attributeAtPathInfo.get("/root/element1/@attr2");
		assertEquals(0.5,attr2Entry.getAverage(),0.001);
		assertEquals(0.25,attr2Entry.getVariance(),0.001);
		assertEquals(1,attr2Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,attr2Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr2Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr2Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr2Entry.getMin());
		assertEquals(1,attr2Entry.getTotal());
		assertEquals(0.5,attr2Entry.getNonZeroRatio(),0.001);
		assertEquals(1,attr2Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr2Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//attr3
		BasicStatisticsEntry attr3Entry = attributeAtPathInfo.get("/root/element1/@attr3");
		assertEquals(0.5,attr3Entry.getAverage(),0.001);
		assertEquals(0.25,attr3Entry.getVariance(),0.001);
		assertEquals(1,attr3Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,attr3Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr3Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr3Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr3Entry.getMin());
		assertEquals(1,attr3Entry.getTotal());
		assertEquals(0.5,attr3Entry.getNonZeroRatio(),0.001);
		assertEquals(1,attr3Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr3Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//test:attr4
		BasicStatisticsEntry testAttr4Entry = attributeAtPathInfo.get("/root/element1/@test:attr4");
		assertEquals(0.5,testAttr4Entry.getAverage(),0.001);
		assertEquals(0.25,testAttr4Entry.getVariance(),0.001);
		assertEquals(1,testAttr4Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,testAttr4Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),testAttr4Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,testAttr4Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,testAttr4Entry.getMin());
		assertEquals(1,testAttr4Entry.getTotal());
		assertEquals(0.5,testAttr4Entry.getNonZeroRatio(),0.001);
		assertEquals(1,testAttr4Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,testAttr4Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//attr5
		BasicStatisticsEntry attr5Entry = attributeAtPathInfo.get("/root/element3/@attr5");
		assertEquals(0.5,attr5Entry.getAverage(),0.001);
		assertEquals(0.25,attr5Entry.getVariance(),0.001);
		assertEquals(1,attr5Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,attr5Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr5Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr5Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr5Entry.getMin());
		assertEquals(1,attr5Entry.getTotal());
		assertEquals(0.5,attr5Entry.getNonZeroRatio(),0.001);
		assertEquals(1,attr5Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr5Entry.getConditionedStandardDeviationAverageRatio(),0.001);
	}
	
	/**
	 * This method checks that the values at path information of the first scenario is well generated.
	 */
	@Test
	public void testOnScenario1ValuesAtPathInfo(){
		Schema schema = typesExtractor1.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Table<String, String, BasicStatisticsEntry> valuesAtPathInfo = statistics.getValuesAtPathInfo();
		
		//element1
		Map<String,BasicStatisticsEntry> element1Values = valuesAtPathInfo.row("/root/element1");
		assertFalse(element1Values.isEmpty());
		assertEquals(2,element1Values.keySet().size());
		BasicStatisticsEntry element1ValueValue1Entry = element1Values.get("value1");
		assertNotNull(element1ValueValue1Entry);
		assertEquals(0.5,element1ValueValue1Entry.getAverage(),0.001);
		assertEquals(0.25,element1ValueValue1Entry.getVariance(),0.001);
		assertEquals(1,element1ValueValue1Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,element1ValueValue1Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element1ValueValue1Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,element1ValueValue1Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,element1ValueValue1Entry.getMin());
		assertEquals(1,element1ValueValue1Entry.getTotal());
		assertEquals(0.5,element1ValueValue1Entry.getNonZeroRatio(),0.001);
		assertEquals(1,element1ValueValue1Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,element1ValueValue1Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		BasicStatisticsEntry element1ValueProbandoProbando123Entry = element1Values.get("probando probando 123");
		assertNotNull(element1ValueProbandoProbando123Entry);
		assertEquals(0.5,element1ValueProbandoProbando123Entry.getAverage(),0.001);
		assertEquals(0.25,element1ValueProbandoProbando123Entry.getVariance(),0.001);
		assertEquals(1,element1ValueProbandoProbando123Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,element1ValueProbandoProbando123Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element1ValueProbandoProbando123Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,element1ValueProbandoProbando123Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,element1ValueProbandoProbando123Entry.getMin());
		assertEquals(1,element1ValueProbandoProbando123Entry.getTotal());
		assertEquals(0.5,element1ValueProbandoProbando123Entry.getNonZeroRatio(),0.001);
		assertEquals(1,element1ValueProbandoProbando123Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,element1ValueProbandoProbando123Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//element2
		Map<String,BasicStatisticsEntry> element2Values = valuesAtPathInfo.row("/root/unprefixed1:element2");
		assertFalse(element2Values.isEmpty());
		assertEquals(2,element2Values.keySet().size());
		BasicStatisticsEntry element2ValueTrue = element2Values.get("true");
		assertNotNull(element2ValueTrue);
		assertEquals(0.5,element2ValueTrue.getAverage(),0.001);
		assertEquals(0.25,element2ValueTrue.getVariance(),0.001);
		assertEquals(1,element2ValueTrue.getConditionedAverage(),0.001);
		assertEquals(0.0,element2ValueTrue.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element2ValueTrue.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,element2ValueTrue.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,element2ValueTrue.getMin());
		assertEquals(1,element2ValueTrue.getTotal());
		assertEquals(0.5,element2ValueTrue.getNonZeroRatio(),0.001);
		assertEquals(1,element2ValueTrue.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,element2ValueTrue.getConditionedStandardDeviationAverageRatio(),0.001);
		BasicStatisticsEntry element2ValueFalseEntry = element2Values.get("false");
		assertNotNull(element2ValueFalseEntry);
		assertEquals(0.5,element2ValueFalseEntry.getAverage(),0.001);
		assertEquals(0.25,element2ValueFalseEntry.getVariance(),0.001);
		assertEquals(1,element2ValueFalseEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,element2ValueFalseEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element2ValueFalseEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,element2ValueFalseEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,element2ValueFalseEntry.getMin());
		assertEquals(1,element2ValueFalseEntry.getTotal());
		assertEquals(0.5,element2ValueFalseEntry.getNonZeroRatio(),0.001);
		assertEquals(1,element2ValueFalseEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,element2ValueFalseEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//element3
		Map<String,BasicStatisticsEntry> element3Values = valuesAtPathInfo.row("/root/element3");
		assertFalse(element3Values.isEmpty());
		BasicStatisticsEntry element3ValueEmptyEntry = element3Values.get("");
		assertNotNull(element3ValueEmptyEntry);
		assertEquals(1.0,element3ValueEmptyEntry.getAverage(),0.001);
		assertEquals(1.0,element3ValueEmptyEntry.getVariance(),0.001);
		assertEquals(2.0,element3ValueEmptyEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,element3ValueEmptyEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element3ValueEmptyEntry.getMode());
		assertEquals(VALUE_2_FREQUENCY_1,element3ValueEmptyEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,element3ValueEmptyEntry.getMin());
		assertEquals(2,element3ValueEmptyEntry.getTotal());
		assertEquals(0.5,element3ValueEmptyEntry.getNonZeroRatio(),0.001);
		assertEquals(1.0,element3ValueEmptyEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0.0,element3ValueEmptyEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//test:element3
		Map<String,BasicStatisticsEntry> testElement3Values = valuesAtPathInfo.row("/root/test:element3");
		assertFalse(testElement3Values.isEmpty());
		BasicStatisticsEntry testElement3ValueEmptyEntry = testElement3Values.get("");
		assertNotNull(testElement3ValueEmptyEntry);
		assertEquals(0.5,testElement3ValueEmptyEntry.getAverage(),0.001);
		assertEquals(0.25,testElement3ValueEmptyEntry.getVariance(),0.001);
		assertEquals(1,testElement3ValueEmptyEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,testElement3ValueEmptyEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),testElement3ValueEmptyEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,testElement3ValueEmptyEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,testElement3ValueEmptyEntry.getMin());
		assertEquals(1,testElement3ValueEmptyEntry.getTotal());
		assertEquals(0.5,testElement3ValueEmptyEntry.getNonZeroRatio(),0.001);
		assertEquals(1,testElement3ValueEmptyEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,testElement3ValueEmptyEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//test:element4
		Map<String,BasicStatisticsEntry> testElement4Values = valuesAtPathInfo.row("/root/test:element4");
		assertFalse(testElement4Values.isEmpty());
		BasicStatisticsEntry testElement4ValueEmptyEntry = testElement4Values.get("");
		assertNotNull(testElement4ValueEmptyEntry);
		assertEquals(0.5,testElement4ValueEmptyEntry.getAverage(),0.001);
		assertEquals(0.25,testElement4ValueEmptyEntry.getVariance(),0.001);
		assertEquals(1,testElement4ValueEmptyEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,testElement4ValueEmptyEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),testElement4ValueEmptyEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,testElement4ValueEmptyEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,testElement4ValueEmptyEntry.getMin());
		assertEquals(1,testElement4ValueEmptyEntry.getTotal());
		assertEquals(0.5,testElement4ValueEmptyEntry.getNonZeroRatio(),0.001);
		assertEquals(1,testElement4ValueEmptyEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,testElement4ValueEmptyEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//attr1
		Map<String,BasicStatisticsEntry> attr1Values = valuesAtPathInfo.row("/root/element1/@attr1");
		assertFalse(attr1Values.isEmpty());
		BasicStatisticsEntry attr1Value5Entry = attr1Values.get("5");
		assertNotNull(attr1Value5Entry);
		assertEquals(0.5,attr1Value5Entry.getAverage(),0.001);
		assertEquals(0.25,attr1Value5Entry.getVariance(),0.001);
		assertEquals(1,attr1Value5Entry.getConditionedAverage(),0.001);
		assertEquals(0,attr1Value5Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr1Value5Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr1Value5Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr1Value5Entry.getMin());
		assertEquals(1,attr1Value5Entry.getTotal());
		assertEquals(0.5,attr1Value5Entry.getNonZeroRatio(),0.001);
		assertEquals(1,attr1Value5Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr1Value5Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		BasicStatisticsEntry attr1Value6Entry = attr1Values.get("6");
		assertNotNull(attr1Value6Entry);
		assertEquals(0.5,attr1Value6Entry.getAverage(),0.001);
		assertEquals(0.25,attr1Value6Entry.getVariance(),0.001);
		assertEquals(1,attr1Value6Entry.getConditionedAverage(),0.001);
		assertEquals(0,attr1Value6Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr1Value6Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr1Value6Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr1Value6Entry.getMin());
		assertEquals(1,attr1Value6Entry.getTotal());
		assertEquals(0.5,attr1Value6Entry.getNonZeroRatio(),0.001);
		assertEquals(1,attr1Value6Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr1Value6Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//attr2
		Map<String,BasicStatisticsEntry> attr2Values = valuesAtPathInfo.row("/root/element1/@attr2");
		assertFalse(attr2Values.isEmpty());
		BasicStatisticsEntry attr2ValueHolaEntry = attr2Values.get("hola");
		assertNotNull(attr2ValueHolaEntry);
		assertEquals(0.5,attr2ValueHolaEntry.getAverage(),0.001);
		assertEquals(0.25,attr2ValueHolaEntry.getVariance(),0.001);
		assertEquals(1,attr2ValueHolaEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,attr2ValueHolaEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr2ValueHolaEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr2ValueHolaEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr2ValueHolaEntry.getMin());
		assertEquals(1,attr2ValueHolaEntry.getTotal());
		assertEquals(0.5,attr2ValueHolaEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attr2ValueHolaEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr2ValueHolaEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//attr3
		Map<String,BasicStatisticsEntry> attr3Values = valuesAtPathInfo.row("/root/element1/@attr3");
		assertFalse(attr3Values.isEmpty());
		BasicStatisticsEntry attr3ValueBuenasEntry = attr3Values.get("buenas");
		assertNotNull(attr3ValueBuenasEntry);
		assertEquals(0.5,attr3ValueBuenasEntry.getAverage(),0.001);
		assertEquals(0.25,attr3ValueBuenasEntry.getVariance(),0.001);
		assertEquals(1,attr3ValueBuenasEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,attr3ValueBuenasEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr3ValueBuenasEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr3ValueBuenasEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr3ValueBuenasEntry.getMin());
		assertEquals(1,attr3ValueBuenasEntry.getTotal());
		assertEquals(0.5,attr3ValueBuenasEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attr3ValueBuenasEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr3ValueBuenasEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//test:attr4
		Map<String,BasicStatisticsEntry> testAttr4Values = valuesAtPathInfo.row("/root/element1/@test:attr4");
		assertFalse(testAttr4Values.isEmpty());
		BasicStatisticsEntry testAttr4ValueConNamespaceEntry = testAttr4Values.get("con namespace");
		assertNotNull(testAttr4ValueConNamespaceEntry);
		assertEquals(0.5,testAttr4ValueConNamespaceEntry.getAverage(),0.001);
		assertEquals(0.25,testAttr4ValueConNamespaceEntry.getVariance(),0.001);
		assertEquals(1,testAttr4ValueConNamespaceEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,testAttr4ValueConNamespaceEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),testAttr4ValueConNamespaceEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,testAttr4ValueConNamespaceEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,testAttr4ValueConNamespaceEntry.getMin());
		assertEquals(1,testAttr4ValueConNamespaceEntry.getTotal());
		assertEquals(0.5,testAttr4ValueConNamespaceEntry.getNonZeroRatio(),0.001);
		assertEquals(1,testAttr4ValueConNamespaceEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,testAttr4ValueConNamespaceEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//attr5
		Map<String,BasicStatisticsEntry> attr5Values = valuesAtPathInfo.row("/root/element3/@attr5");
		assertFalse(attr5Values.isEmpty());
		BasicStatisticsEntry attr5Value7Entry = attr5Values.get("7");
		assertNotNull(attr5Value7Entry);
		assertEquals(0.5,attr5Value7Entry.getAverage(),0.001);
		assertEquals(0.25,attr5Value7Entry.getVariance(),0.001);
		assertEquals(1,attr5Value7Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,attr5Value7Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr5Value7Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr5Value7Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr5Value7Entry.getMin());
		assertEquals(1,attr5Value7Entry.getTotal());
		assertEquals(0.5,attr5Value7Entry.getNonZeroRatio(),0.001);
		assertEquals(1,attr5Value7Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr5Value7Entry.getConditionedStandardDeviationAverageRatio(),0.001);
	}
	
	/**
	 * This method checks that the statistics of numeric values at path of the first scenario are well generated.
	 */
	@Test
	public void testOnScenario1StatisticsOfNumericValuesAtPath(){
		Schema schema = typesExtractor1.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		Map<String, BasicStatisticsEntry> statisticsOfNumericValuesAtPath = statistics.getStatisticsOfNumericValuesAtPath();
		
		//attr1
		BasicStatisticsEntry attr1ValuesStatisticsEntry = statisticsOfNumericValuesAtPath.get("/root/element1/@attr1");
		assertNotNull(attr1ValuesStatisticsEntry);
		assertEquals(5.5,attr1ValuesStatisticsEntry.getAverage(),0.001);
		assertEquals(0.25,attr1ValuesStatisticsEntry.getVariance(),0.001);
		assertEquals(5.5,attr1ValuesStatisticsEntry.getConditionedAverage(),0.001);
		assertEquals(0.25,attr1ValuesStatisticsEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr1ValuesStatisticsEntry.getMode());
		assertEquals(VALUE_6_FREQUENCY_1,attr1ValuesStatisticsEntry.getMax());
		assertEquals(VALUE_5_FREQUENCY_1,attr1ValuesStatisticsEntry.getMin());
		assertEquals(11,attr1ValuesStatisticsEntry.getTotal());
		assertEquals(1,attr1ValuesStatisticsEntry.getNonZeroRatio(),0.001);
		assertEquals(0.091,attr1ValuesStatisticsEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0.091,attr1ValuesStatisticsEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//attr5
		BasicStatisticsEntry attr5ValuesStatisticsEntry = statisticsOfNumericValuesAtPath.get("/root/element3/@attr5");
		assertNotNull(attr5ValuesStatisticsEntry);
		assertEquals(7,attr5ValuesStatisticsEntry.getAverage(),0.001);
		assertEquals(0,attr5ValuesStatisticsEntry.getVariance(),0.001);
		assertEquals(7,attr5ValuesStatisticsEntry.getConditionedAverage(),0.001);
		assertEquals(0,attr5ValuesStatisticsEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr5ValuesStatisticsEntry.getMode());
		assertEquals(VALUE_7_FREQUENCY_1,attr5ValuesStatisticsEntry.getMax());
		assertEquals(VALUE_7_FREQUENCY_1,attr5ValuesStatisticsEntry.getMin());
		assertEquals(7,attr5ValuesStatisticsEntry.getTotal());
		assertEquals(1,attr5ValuesStatisticsEntry.getNonZeroRatio(),0.001);
		assertEquals(0,attr5ValuesStatisticsEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr5ValuesStatisticsEntry.getConditionedStandardDeviationAverageRatio(),0.001);
	}
	
	/**
	 * Helper method that checks whether all the strings from a Collection are empty strings or only consist of whitespace characters (it means, <i>space</i>, \\n, \\t or \\r). 
	 * @param values the collection of values
	 * @return true if all the strings of the collection are as described above or the whole collection is empty, false otherwise.
	 */
	private boolean valuesEmptyOrOnlyConsistOfWhitespaceCharacters(Collection<String> values){
		for(String value: values){
			if(!value.matches("[\n\r\t ]*"))
				return false;
		}
		return true;
	}
	
	/**
	 * This method checks that the statistic information of the complex type _root from the first scenario is well generated.
	 */
	@Test
	public void testOnScenario1ComplexTypeRootStatisticsEntry(){
		Schema schema = typesExtractor1.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Table<String,String,SchemaElement> elements =  schema.getElements();
		SchemaElement element1=elements.get("", "_root-element1");
		SchemaElement element2=elements.get("http://probando.net", "_root-element2");
		SchemaElement element3=elements.get("", "_root-element3");
		SchemaElement testElement3=elements.get("http://prueba.net", "_root-element3");
		SchemaElement testElement4=elements.get("http://prueba.net", "_root-element4");
		
		ComplexTypeStatisticsEntry complexTypeRootStatisticsEntry = statistics.getComplexTypeStatisticsEntryByName("_root");
		assertEquals(2, complexTypeRootStatisticsEntry.getInputDocumentsCount());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeRootStatisticsEntry.getElementInfo();
		
		//element1
		BasicStatisticsEntry element1Entry = elementInfo.get(element1);
		assertEquals(1,element1Entry.getAverage(),0.001);
		assertEquals(0,element1Entry.getVariance(),0.001);
		assertEquals(1,element1Entry.getConditionedAverage(),0.001);
		assertEquals(0,element1Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element1Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_2,element1Entry.getMax());
		assertEquals(VALUE_1_FREQUENCY_2,element1Entry.getMin());
		assertEquals(2,element1Entry.getTotal());
		assertEquals(1,element1Entry.getNonZeroRatio(),0.001);
		assertEquals(0,element1Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,element1Entry.getConditionedStandardDeviationAverageRatio(),0.001);

		//unprefixed1:element2
		BasicStatisticsEntry element2Entry = elementInfo.get(element2);
		assertEquals(1,element2Entry.getAverage(),0.001);
		assertEquals(0,element2Entry.getVariance(),0.001);
		assertEquals(1,element2Entry.getConditionedAverage(),0.001);
		assertEquals(0,element2Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element2Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_2,element2Entry.getMax());
		assertEquals(VALUE_1_FREQUENCY_2,element2Entry.getMin());
		assertEquals(2,element2Entry.getTotal());
		assertEquals(1,element2Entry.getNonZeroRatio(),0.001);
		assertEquals(0,element2Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,element2Entry.getConditionedStandardDeviationAverageRatio(),0.001);

		//element3
		BasicStatisticsEntry element3Entry = elementInfo.get(element3);
		assertEquals(1.5,element3Entry.getAverage(),0.001);
		assertEquals(0.25,element3Entry.getVariance(),0.001);
		assertEquals(1.5,element3Entry.getConditionedAverage(),0.001);
		assertEquals(0.25,element3Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element3Entry.getMode());
		assertEquals(VALUE_2_FREQUENCY_1,element3Entry.getMax());
		assertEquals(VALUE_1_FREQUENCY_1,element3Entry.getMin());
		assertEquals(3,element3Entry.getTotal());
		assertEquals(1,element3Entry.getNonZeroRatio(),0.001);
		assertEquals(0.333,element3Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0.333,element3Entry.getConditionedStandardDeviationAverageRatio(),0.001);

		//test:element3
		BasicStatisticsEntry testElement3Entry = elementInfo.get(testElement3);
		assertEquals(0.5,testElement3Entry.getAverage(),0.001);
		assertEquals(0.25,testElement3Entry.getVariance(),0.001);
		assertEquals(1,testElement3Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,testElement3Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),testElement3Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,testElement3Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,testElement3Entry.getMin());
		assertEquals(1,testElement3Entry.getTotal());
		assertEquals(0.5,testElement3Entry.getNonZeroRatio(),0.001);
		assertEquals(1,testElement3Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,testElement3Entry.getConditionedStandardDeviationAverageRatio(),0.001);

		//test:Element4
		BasicStatisticsEntry testElement4Entry = elementInfo.get(testElement4);
		assertEquals(0.5,testElement4Entry.getAverage(),0.001);
		assertEquals(0.25,testElement4Entry.getVariance(),0.001);
		assertEquals(1,testElement4Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,testElement4Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),testElement4Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,testElement4Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,testElement4Entry.getMin());
		assertEquals(1,testElement4Entry.getTotal());
		assertEquals(0.5,testElement4Entry.getNonZeroRatio(),0.001);
		assertEquals(1,testElement4Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,testElement4Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeRootStatisticsEntry.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfo.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeRootStatisticsEntry.getValuesInfo();
		assertTrue(valuesInfo.isEmpty()||valuesEmptyOrOnlyConsistOfWhitespaceCharacters(valuesInfo.rowKeySet()));
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeRootStatisticsEntry.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodes.isEmpty());
	}
	
	/**
	 * This method checks that the statistic information of the complex type _root-_element1 from the first scenario is well generated.
	 */
	@Test
	public void testOnScenario1ComplexTypeRootElement1StatisticsEntry(){
		Schema schema = typesExtractor1.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Table<String, String, SchemaElement> elements = schema.getElements();
		SchemaElement element1=elements.get("", "_root-element1");
		
		Table<String,String,SchemaAttribute> attributes=schema.getAttributes();	
		SchemaAttribute attr1=attributes.get("","_root-_element1-attr1");
		SchemaAttribute attr2=attributes.get("","_root-_element1-attr2");
		SchemaAttribute attr3=attributes.get("","_root-_element1-attr3");
		SchemaAttribute testAttr4=attributes.get("http://prueba.net","_root-_element1-attr4");
		
		ComplexTypeStatisticsEntry complexTypeRootElement1StatisticsEntry = statistics.getComplexTypeStatisticsEntryByName("_root-_element1");
		assertNotNull(complexTypeRootElement1StatisticsEntry);
		assertEquals(2, complexTypeRootElement1StatisticsEntry.getInputDocumentsCount());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeRootElement1StatisticsEntry.getElementInfo();
		assertTrue(elementInfo.isEmpty());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeRootElement1StatisticsEntry.getAttributeOccurrencesInfo();
		assertEquals(4,attributeOccurrencesInfo.size());
		
		//attr1
		BasicStatisticsEntry attr1Entry = attributeOccurrencesInfo.get(attr1);
		assertEquals(1,attr1Entry.getAverage(),0.001);
		assertEquals(0,attr1Entry.getVariance(),0.001);
		assertEquals(1,attr1Entry.getConditionedAverage(),0.001);
		assertEquals(0,attr1Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr1Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_2,attr1Entry.getMax());
		assertEquals(VALUE_1_FREQUENCY_2,attr1Entry.getMin());
		assertEquals(2,attr1Entry.getTotal());
		assertEquals(1,attr1Entry.getNonZeroRatio(),0.001);
		assertEquals(0,attr1Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr1Entry.getConditionedStandardDeviationAverageRatio(),0.001);

		//attr2
		BasicStatisticsEntry attr2Entry = attributeOccurrencesInfo.get(attr2);
		assertEquals(0.5,attr2Entry.getAverage(),0.001);
		assertEquals(0.25,attr2Entry.getVariance(),0.001);
		assertEquals(1,attr2Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,attr2Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr2Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr2Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr2Entry.getMin());
		assertEquals(1,attr2Entry.getTotal());
		assertEquals(0.5,attr2Entry.getNonZeroRatio(),0.001);
		assertEquals(1,attr2Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr2Entry.getConditionedStandardDeviationAverageRatio(),0.001);

		//attr3
		BasicStatisticsEntry attr3Entry = attributeOccurrencesInfo.get(attr3);
		assertEquals(0.5,attr3Entry.getAverage(),0.001);
		assertEquals(0.25,attr3Entry.getVariance(),0.001);
		assertEquals(1,attr3Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,attr3Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr3Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr3Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr3Entry.getMin());
		assertEquals(1,attr3Entry.getTotal());
		assertEquals(0.5,attr3Entry.getNonZeroRatio(),0.001);
		assertEquals(1,attr3Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr3Entry.getConditionedStandardDeviationAverageRatio(),0.001);

		//test:attr4
		BasicStatisticsEntry testAttr4Entry = attributeOccurrencesInfo.get(testAttr4);
		assertEquals(0.5,testAttr4Entry.getAverage(),0.001);
		assertEquals(0.25,testAttr4Entry.getVariance(),0.001);
		assertEquals(1,testAttr4Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,testAttr4Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),testAttr4Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,testAttr4Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,testAttr4Entry.getMin());
		assertEquals(1,testAttr4Entry.getTotal());
		assertEquals(0.5,testAttr4Entry.getNonZeroRatio(),0.001);
		assertEquals(1,testAttr4Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,testAttr4Entry.getConditionedStandardDeviationAverageRatio(),0.001);	
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeRootElement1StatisticsEntry.getValuesInfo();
		
		//element1 values
		Map<String, BasicStatisticsEntry> element1Values = valuesInfo.column(element1);
		BasicStatisticsEntry element1ValueValue1Entry = element1Values.get("value1");
		assertNotNull(element1ValueValue1Entry);
		assertEquals(0.5,element1ValueValue1Entry.getAverage(),0.001);
		assertEquals(0.25,element1ValueValue1Entry.getVariance(),0.001);
		assertEquals(1,element1ValueValue1Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,element1ValueValue1Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element1ValueValue1Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,element1ValueValue1Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,element1ValueValue1Entry.getMin());
		assertEquals(1,element1ValueValue1Entry.getTotal());
		assertEquals(0.5,element1ValueValue1Entry.getNonZeroRatio(),0.001);
		assertEquals(1,element1ValueValue1Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,element1ValueValue1Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		BasicStatisticsEntry element1ValueProbandoProbando123Entry = element1Values.get("probando probando 123");
		assertNotNull(element1ValueProbandoProbando123Entry);
		assertEquals(0.5,element1ValueProbandoProbando123Entry.getAverage(),0.001);
		assertEquals(0.25,element1ValueProbandoProbando123Entry.getVariance(),0.001);
		assertEquals(1,element1ValueProbandoProbando123Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,element1ValueProbandoProbando123Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element1ValueProbandoProbando123Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,element1ValueProbandoProbando123Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,element1ValueProbandoProbando123Entry.getMin());
		assertEquals(1,element1ValueProbandoProbando123Entry.getTotal());
		assertEquals(0.5,element1ValueProbandoProbando123Entry.getNonZeroRatio(),0.001);
		assertEquals(1,element1ValueProbandoProbando123Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,element1ValueProbandoProbando123Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		//attr1 values
		Map<String,BasicStatisticsEntry> attr1Values = valuesInfo.column(attr1);
		assertFalse(attr1Values.isEmpty());
		BasicStatisticsEntry attr1Value5Entry = attr1Values.get("5");
		assertNotNull(attr1Value5Entry);
		assertEquals(0.5,attr1Value5Entry.getAverage(),0.001);
		assertEquals(0.25,attr1Value5Entry.getVariance(),0.001);
		assertEquals(1,attr1Value5Entry.getConditionedAverage(),0.001);
		assertEquals(0,attr1Value5Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr1Value5Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr1Value5Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr1Value5Entry.getMin());
		assertEquals(1,attr1Value5Entry.getTotal());
		assertEquals(0.5,attr1Value5Entry.getNonZeroRatio(),0.001);
		assertEquals(1,attr1Value5Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr1Value5Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		BasicStatisticsEntry attr1Value6Entry = attr1Values.get("6");
		assertNotNull(attr1Value6Entry);
		assertEquals(0.5,attr1Value6Entry.getAverage(),0.001);
		assertEquals(0.25,attr1Value6Entry.getVariance(),0.001);
		assertEquals(1,attr1Value6Entry.getConditionedAverage(),0.001);
		assertEquals(0,attr1Value6Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr1Value6Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr1Value6Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr1Value6Entry.getMin());
		assertEquals(1,attr1Value6Entry.getTotal());
		assertEquals(0.5,attr1Value6Entry.getNonZeroRatio(),0.001);
		assertEquals(1,attr1Value6Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr1Value6Entry.getConditionedStandardDeviationAverageRatio(),0.001);

		//attr2 values
		Map<String,BasicStatisticsEntry> attr2Values = valuesInfo.column(attr2);
		assertFalse(attr2Values.isEmpty());
		BasicStatisticsEntry attr2ValueHolaEntry = attr2Values.get("hola");
		assertNotNull(attr2ValueHolaEntry);
		assertEquals(0.5,attr2ValueHolaEntry.getAverage(),0.001);
		assertEquals(0.25,attr2ValueHolaEntry.getVariance(),0.001);
		assertEquals(1,attr2ValueHolaEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,attr2ValueHolaEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr2ValueHolaEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr2ValueHolaEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr2ValueHolaEntry.getMin());
		assertEquals(1,attr2ValueHolaEntry.getTotal());
		assertEquals(0.5,attr2ValueHolaEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attr2ValueHolaEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr2ValueHolaEntry.getConditionedStandardDeviationAverageRatio(),0.001);

		//attr3 values
		Map<String,BasicStatisticsEntry> attr3Values = valuesInfo.column(attr3);
		assertFalse(attr3Values.isEmpty());
		BasicStatisticsEntry attr3ValueBuenasEntry = attr3Values.get("buenas");
		assertNotNull(attr3ValueBuenasEntry);
		assertEquals(0.5,attr3ValueBuenasEntry.getAverage(),0.001);
		assertEquals(0.25,attr3ValueBuenasEntry.getVariance(),0.001);
		assertEquals(1,attr3ValueBuenasEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,attr3ValueBuenasEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr3ValueBuenasEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr3ValueBuenasEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr3ValueBuenasEntry.getMin());
		assertEquals(1,attr3ValueBuenasEntry.getTotal());
		assertEquals(0.5,attr3ValueBuenasEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attr3ValueBuenasEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr3ValueBuenasEntry.getConditionedStandardDeviationAverageRatio(),0.001);

		//test:attr4 values
		Map<String,BasicStatisticsEntry> testAttr4Values = valuesInfo.column(testAttr4);
		assertFalse(testAttr4Values.isEmpty());
		BasicStatisticsEntry testAttr4ValueConNamespaceEntry = testAttr4Values.get("con namespace");
		assertNotNull(testAttr4ValueConNamespaceEntry);
		assertEquals(0.5,testAttr4ValueConNamespaceEntry.getAverage(),0.001);
		assertEquals(0.25,testAttr4ValueConNamespaceEntry.getVariance(),0.001);
		assertEquals(1,testAttr4ValueConNamespaceEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,testAttr4ValueConNamespaceEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),testAttr4ValueConNamespaceEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,testAttr4ValueConNamespaceEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,testAttr4ValueConNamespaceEntry.getMin());
		assertEquals(1,testAttr4ValueConNamespaceEntry.getTotal());
		assertEquals(0.5,testAttr4ValueConNamespaceEntry.getNonZeroRatio(),0.001);
		assertEquals(1,testAttr4ValueConNamespaceEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,testAttr4ValueConNamespaceEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeRootElement1StatisticsEntry.getStatisticsOfNumericValuesOfNodes();
		assertEquals(1,statisticsOfNumericValuesOfNodes.size());
		
		//attr1 statistics over numeric values
		BasicStatisticsEntry attr1ValuesStatisticsEntry = statisticsOfNumericValuesOfNodes.get(attr1);
		assertNotNull(attr1ValuesStatisticsEntry);
		assertEquals(5.5,attr1ValuesStatisticsEntry.getAverage(),0.001);
		assertEquals(0.25,attr1ValuesStatisticsEntry.getVariance(),0.001);
		assertEquals(5.5,attr1ValuesStatisticsEntry.getConditionedAverage(),0.001);
		assertEquals(0.25,attr1ValuesStatisticsEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr1ValuesStatisticsEntry.getMode());
		assertEquals(VALUE_6_FREQUENCY_1,attr1ValuesStatisticsEntry.getMax());
		assertEquals(VALUE_5_FREQUENCY_1,attr1ValuesStatisticsEntry.getMin());
		assertEquals(11,attr1ValuesStatisticsEntry.getTotal());
		assertEquals(1,attr1ValuesStatisticsEntry.getNonZeroRatio(),0.001);
		assertEquals(0.091,attr1ValuesStatisticsEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0.091,attr1ValuesStatisticsEntry.getConditionedStandardDeviationAverageRatio(),0.001);
	}
	
	/**
	 * This method checks that the statistic information of the complex type _root-unprefixed1_element2 from the first scenario is well generated.
	 */
	@Test
	public void testOnScenario1ComplexTypeRootElement2StatisticsEntry(){
		Schema schema = typesExtractor1.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Table<String, String, SchemaElement> elements = schema.getElements();
		SchemaElement element2=elements.get("http://probando.net", "_root-element2");
		
		ComplexTypeStatisticsEntry complexTypeRootElement2StatisticsEntry = statistics.getComplexTypeStatisticsEntryByName("_root-unprefixed1_element2");
		assertNotNull(complexTypeRootElement2StatisticsEntry);
		assertEquals(2, complexTypeRootElement2StatisticsEntry.getInputDocumentsCount());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeRootElement2StatisticsEntry.getElementInfo();
		assertTrue(elementInfo.isEmpty());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeRootElement2StatisticsEntry.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfo.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeRootElement2StatisticsEntry.getValuesInfo();
		assertEquals(2,valuesInfo.size());
		Map<String,BasicStatisticsEntry> element2Values = valuesInfo.column(element2);
		assertFalse(element2Values.isEmpty());
		assertEquals(2,element2Values.keySet().size());
		BasicStatisticsEntry element2ValueTrue = element2Values.get("true");
		assertNotNull(element2ValueTrue);
		assertEquals(0.5,element2ValueTrue.getAverage(),0.001);
		assertEquals(0.25,element2ValueTrue.getVariance(),0.001);
		assertEquals(1,element2ValueTrue.getConditionedAverage(),0.001);
		assertEquals(0.0,element2ValueTrue.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element2ValueTrue.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,element2ValueTrue.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,element2ValueTrue.getMin());
		assertEquals(1,element2ValueTrue.getTotal());
		assertEquals(0.5,element2ValueTrue.getNonZeroRatio(),0.001);
		assertEquals(1,element2ValueTrue.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,element2ValueTrue.getConditionedStandardDeviationAverageRatio(),0.001);
		BasicStatisticsEntry element2ValueFalseEntry = element2Values.get("false");
		assertNotNull(element2ValueFalseEntry);
		assertEquals(0.5,element2ValueFalseEntry.getAverage(),0.001);
		assertEquals(0.25,element2ValueFalseEntry.getVariance(),0.001);
		assertEquals(1,element2ValueFalseEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,element2ValueFalseEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),element2ValueFalseEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,element2ValueFalseEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,element2ValueFalseEntry.getMin());
		assertEquals(1,element2ValueFalseEntry.getTotal());
		assertEquals(0.5,element2ValueFalseEntry.getNonZeroRatio(),0.001);
		assertEquals(1,element2ValueFalseEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,element2ValueFalseEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeRootElement2StatisticsEntry.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodes.isEmpty());
	}
	
	/**
	 * This method checks that the statistic information of the complex type _root-_element3 from the first scenario is well generated.
	 */
	@Test
	public void testOnScenario1ComplexTypeRootElement3StatisticsEntry(){
		Schema schema = typesExtractor1.getInitalSchema();
		Statistics statistics = schema.getStatistics();
				
		Table<String,String,SchemaAttribute> attributes=schema.getAttributes();	
		SchemaAttribute attr5=attributes.get("","_root-_element3-attr5");
		
		ComplexTypeStatisticsEntry complexTypeRootElement3StatisticsEntry = statistics.getComplexTypeStatisticsEntryByName("_root-_element3");
		assertNotNull(complexTypeRootElement3StatisticsEntry);
		assertEquals(2, complexTypeRootElement3StatisticsEntry.getInputDocumentsCount());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeRootElement3StatisticsEntry.getElementInfo();
		assertTrue(elementInfo.isEmpty());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeRootElement3StatisticsEntry.getAttributeOccurrencesInfo();
		assertEquals(1,attributeOccurrencesInfo.size());
		
		//attr5
		BasicStatisticsEntry attr5Entry = attributeOccurrencesInfo.get(attr5);
		assertEquals(0.5,attr5Entry.getAverage(),0.001);
		assertEquals(0.25,attr5Entry.getVariance(),0.001);
		assertEquals(1,attr5Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,attr5Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr5Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr5Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr5Entry.getMin());
		assertEquals(1,attr5Entry.getTotal());
		assertEquals(0.5,attr5Entry.getNonZeroRatio(),0.001);
		assertEquals(1,attr5Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr5Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeRootElement3StatisticsEntry.getValuesInfo();
		assertEquals(3,valuesInfo.size()); //Because of the empty and only whitespaces values of element3
		
		//attr5 values
		Map<String,BasicStatisticsEntry> attr5Values = valuesInfo.column(attr5);
		assertFalse(attr5Values.isEmpty());
		BasicStatisticsEntry attr5Value7Entry = attr5Values.get("7");
		assertNotNull(attr5Value7Entry);
		assertEquals(0.5,attr5Value7Entry.getAverage(),0.001);
		assertEquals(0.25,attr5Value7Entry.getVariance(),0.001);
		assertEquals(1,attr5Value7Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,attr5Value7Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr5Value7Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attr5Value7Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attr5Value7Entry.getMin());
		assertEquals(1,attr5Value7Entry.getTotal());
		assertEquals(0.5,attr5Value7Entry.getNonZeroRatio(),0.001);
		assertEquals(1,attr5Value7Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr5Value7Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeRootElement3StatisticsEntry.getStatisticsOfNumericValuesOfNodes();
		assertEquals(1,statisticsOfNumericValuesOfNodes.size());
		
		//attr5 statistics over numeric values
		BasicStatisticsEntry attr5ValuesStatisticsEntry = statisticsOfNumericValuesOfNodes.get(attr5);
		assertNotNull(attr5ValuesStatisticsEntry);
		assertEquals(7,attr5ValuesStatisticsEntry.getAverage(),0.001);
		assertEquals(0,attr5ValuesStatisticsEntry.getVariance(),0.001);
		assertEquals(7,attr5ValuesStatisticsEntry.getConditionedAverage(),0.001);
		assertEquals(0,attr5ValuesStatisticsEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attr5ValuesStatisticsEntry.getMode());
		assertEquals(VALUE_7_FREQUENCY_1,attr5ValuesStatisticsEntry.getMax());
		assertEquals(VALUE_7_FREQUENCY_1,attr5ValuesStatisticsEntry.getMin());
		assertEquals(7,attr5ValuesStatisticsEntry.getTotal());
		assertEquals(1,attr5ValuesStatisticsEntry.getNonZeroRatio(),0.001);
		assertEquals(0,attr5ValuesStatisticsEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attr5ValuesStatisticsEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
	}
	
	/**
	 * This method checks that the statistic information of the complex type _root-test_element3 from the first scenario is well generated.
	 */
	@Test
	public void testOnScenario1ComplexTypeRootTestElement3StatisticsEntry(){
		Schema schema = typesExtractor1.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		ComplexTypeStatisticsEntry complexTypeRootTestElement3StatisticsEntry = statistics.getComplexTypeStatisticsEntryByName("_root-test_element3");
		assertNotNull(complexTypeRootTestElement3StatisticsEntry);
		assertEquals(2, complexTypeRootTestElement3StatisticsEntry.getInputDocumentsCount());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeRootTestElement3StatisticsEntry.getElementInfo();
		assertTrue(elementInfo.isEmpty());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeRootTestElement3StatisticsEntry.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfo.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeRootTestElement3StatisticsEntry.getValuesInfo();
		assertTrue(valuesInfo.isEmpty()||valuesEmptyOrOnlyConsistOfWhitespaceCharacters(valuesInfo.rowKeySet()));
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeRootTestElement3StatisticsEntry.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodes.isEmpty());
	}
	
	/**
	 * This method checks that the statistic information of the complex type _root-test_element4 from the first scenario is well generated.
	 */
	@Test
	public void testOnScenario1ComplexTypeRootTestElement4StatisticsEntry(){
		Schema schema = typesExtractor1.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		ComplexTypeStatisticsEntry complexTypeRootTestElement4StatisticsEntry = statistics.getComplexTypeStatisticsEntryByName("_root-test_element4");
		assertNotNull(complexTypeRootTestElement4StatisticsEntry);
		assertEquals(2, complexTypeRootTestElement4StatisticsEntry.getInputDocumentsCount());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeRootTestElement4StatisticsEntry.getElementInfo();
		assertTrue(elementInfo.isEmpty());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeRootTestElement4StatisticsEntry.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfo.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeRootTestElement4StatisticsEntry.getValuesInfo();
		assertTrue(valuesInfo.isEmpty()||valuesEmptyOrOnlyConsistOfWhitespaceCharacters(valuesInfo.rowKeySet()));
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeRootTestElement4StatisticsEntry.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodes.isEmpty());
	}

	//Second scenario methods
	
	/**
	 * This method checks that all the schema elements have been properly extracted on the second scenario
	 */
	@Test
	public void testOnScenario2SchemaElements(){
		Schema schema = typesExtractor2.getInitalSchema();
		Table<String,String,SchemaElement> elements =  schema.getElements();
		
		assertEquals(13,elements.size());
		assertEquals(7,ImmutableSet.copyOf(elements.values()).size());
		
		SchemaElement elementRoot=elements.get("", "root");
		assertNotNull(elementRoot);
		assertEquals("root",elementRoot.getName());
		assertEquals("",elementRoot.getNamespace());
		assertEquals("_root",elementRoot.getType().getName());
		assertTrue(elementRoot.isValidRoot());
		
		SchemaElement elementRootA=elements.get("", "_root-a");
		assertNotNull(elementRootA);
		assertEquals("a",elementRootA.getName());
		assertEquals("",elementRootA.getNamespace());
		assertEquals("_a",elementRootA.getType().getName());
		assertFalse(elementRootA.isValidRoot());
		
		SchemaElement elementRootB=elements.get("", "_root-b");
		assertNotNull(elementRootB);
		assertEquals("b",elementRootB.getName());
		assertEquals("",elementRootB.getNamespace());
		assertEquals("_b",elementRootB.getType().getName());
		assertFalse(elementRootB.isValidRoot());
		
		SchemaElement elementRootC=elements.get("", "_root-c");
		assertNotNull(elementRootC);
		assertEquals("c",elementRootC.getName());
		assertEquals("",elementRootC.getNamespace());
		assertEquals("_c",elementRootC.getType().getName());
		assertFalse(elementRootC.isValidRoot());
		
		SchemaElement elementRootD=elements.get("", "_root-d");
		assertNotNull(elementRootD);
		assertEquals("d",elementRootD.getName());
		assertEquals("",elementRootD.getNamespace());
		assertEquals("_d",elementRootD.getType().getName());
		assertFalse(elementRootD.isValidRoot());
		
		SchemaElement elementRaiz=elements.get("", "raiz");
		assertNotNull(elementRaiz);
		assertEquals("raiz",elementRaiz.getName());
		assertEquals("",elementRaiz.getNamespace());
		assertEquals("_raiz",elementRaiz.getType().getName());
		assertTrue(elementRaiz.isValidRoot());
		
		SchemaElement elementRaizA=elements.get("", "_raiz-a");
		assertNotNull(elementRaizA);
		assertEquals("a",elementRaizA.getName());
		assertEquals("",elementRaizA.getNamespace());
		assertEquals("_a",elementRaizA.getType().getName());
		assertFalse(elementRaizA.isValidRoot());
		
		SchemaElement elementRaizB=elements.get("", "_raiz-b");
		assertNotNull(elementRaizB);
		assertEquals("b",elementRaizB.getName());
		assertEquals("",elementRaizB.getNamespace());
		assertEquals("_b",elementRaizB.getType().getName());
		assertFalse(elementRaizB.isValidRoot());
		
		SchemaElement elementRaizC=elements.get("", "_raiz-c");
		assertNotNull(elementRaizC);
		assertEquals("c",elementRaizC.getName());
		assertEquals("",elementRaizC.getNamespace());
		assertEquals("_c",elementRaizC.getType().getName());
		assertFalse(elementRaizC.isValidRoot());
		
		SchemaElement elementRaizD=elements.get("", "_raiz-d");
		assertNotNull(elementRaizD);
		assertEquals("d",elementRaizD.getName());
		assertEquals("",elementRaizD.getNamespace());
		assertEquals("_d",elementRaizD.getType().getName());
		assertFalse(elementRaizD.isValidRoot());
		
		SchemaElement elementBE=elements.get("", "_b-e");
		assertNotNull(elementBE);
		assertEquals("e",elementBE.getName());
		assertEquals("",elementBE.getNamespace());
		assertEquals("_e",elementBE.getType().getName());
		assertFalse(elementBE.isValidRoot());
		
		SchemaElement elementCE=elements.get("", "_c-e");
		assertNotNull(elementCE);
		assertEquals("e",elementCE.getName());
		assertEquals("",elementCE.getNamespace());
		assertEquals("_e",elementCE.getType().getName());
		assertFalse(elementCE.isValidRoot());
		
		SchemaElement elementDE=elements.get("", "_d-e");
		assertNotNull(elementDE);
		assertEquals("e",elementDE.getName());
		assertEquals("",elementDE.getNamespace());
		assertEquals("_e",elementDE.getType().getName());
		assertFalse(elementDE.isValidRoot());
		
		assertEquals(elementRaizA,elementRootA);
		assertEquals(elementRaizB,elementRootB);
		assertEquals(elementRaizC,elementRootC);
		assertEquals(elementRaizD,elementRootD);
		
		assertEquals(elementBE, elementCE);
		assertEquals(elementBE, elementDE);
		assertEquals(elementCE, elementDE);
		
	}
	
	/**
	 * This method checks that attributes have been extracted properly on scenario 2
	 */
	@Test
	public void testOnScenario2SchemaAttributes(){
		Schema schema = typesExtractor2.getInitalSchema();
		Table <String,String,SchemaAttribute> attributes = schema.getAttributes();
		assertEquals(1, attributes.size());
		
		SchemaAttribute attrAttr=attributes.get("","_raiz-attr");
		assertNotNull(attrAttr);
		assertEquals("attr",attrAttr.getName());
		assertEquals("",attrAttr.getNamespace());
		assertFalse(attrAttr.isOptional());
		assertEquals("xs:string",attrAttr.getSimpleType().getBuiltinType());
		assertFalse(attrAttr.getSimpleType().isEnum());
		assertEquals(1,attrAttr.getSimpleType().enumerationCount());
		assertTrue(attrAttr.getSimpleType().enumerationContains("value"));
		
	}
	
	/**
	 * Test that checks that the namespace-prefix mappings are correctly generated on the second scenario
	 */
	@Test
	public void testOnScenario2NamespacePrefixMappings(){
		Schema schema = typesExtractor2.getInitalSchema();
		Map<String, SortedSet<String>> prefixNamespaceMapping = schema.getNamespacesToPossiblePrefixMappingModifiable();
		assertEquals(2,prefixNamespaceMapping.size());
		
		Set<String> prefixesOfEmptyNamespace = prefixNamespaceMapping.get("");
		assertNotNull(prefixesOfEmptyNamespace);
		assertEquals(1,prefixesOfEmptyNamespace.size());
		assertTrue(prefixesOfEmptyNamespace.contains(""));
	}
	
	/**
	 * This method checks that the prefix-namespace mappings have been made properly on the second scenario.
	 */
	@Test
	public void testOnScenario2SolvedMappings(){
		Schema schema = typesExtractor2.getInitalSchema();
		NavigableMap<String, String> solvedMappingsBetweenNamespaceURIsAndPrefixes = schema.getSolvedNamespaceMappings();
		assertEquals(solvedNamespaceToPrefixMappingNoNS,solvedMappingsBetweenNamespaceURIsAndPrefixes);
	}
	
	/**
	 * This method checks that there are the expected number of complex types on the scenario 2, which should be equal 
	 * to the actual number of elements.
	 */
	@Test
	public void testOnScenario2ComplexTypesCount(){
		Schema schema = typesExtractor2.getInitalSchema();
		int complexTypesCount = schema.getComplexTypes().size();
		assertEquals(7, complexTypesCount);
		assertEquals("The name of elements and complex types does not match",ImmutableSet.copyOf(schema.getElements().values()).size(),complexTypesCount);
	}
	
	/**
	 * This test checks that the complex type _root is correctly generated on the second scenario.
	 */
	@Test
	public void testOnScenario2ComplexTypeRoot() {
		Schema schema = typesExtractor2.getInitalSchema();
		
		ComplexType complexTypeRoot=schema.getComplexTypes().get("_root");
		assertEquals("_root",complexTypeRoot.getName());
		
		//Source element
		
		Set<String> sourceKeys = complexTypeRoot.getSourceElementNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":root"));
		
		//Automaton
		
		ExtendedAutomaton complexTypeRootAutomaton=complexTypeRoot.getAutomaton();
		
		assertEquals(6,complexTypeRootAutomaton.nodeCount());
		assertEquals(5,complexTypeRootAutomaton.edgeCount());
		
		SchemaElement nodeA=schema.getElements().get("", "_root-a");
		SchemaElement nodeB=schema.getElements().get("", "_root-b");
		SchemaElement nodeC=schema.getElements().get("", "_root-c");
		SchemaElement nodeD=schema.getElements().get("", "_root-d");
		
		assertTrue(complexTypeRootAutomaton.containsNode(initialState));
		assertEquals(initialState,complexTypeRootAutomaton.getInitialState());
		assertEquals(0,complexTypeRootAutomaton.getIncomingEdges(initialState).size());
		Map<SchemaElement, Long> initialStateOutgoingEdges = complexTypeRootAutomaton.getOutgoingEdges(initialState);
		assertEquals(1,initialStateOutgoingEdges.size());
		assertTrue(initialStateOutgoingEdges.containsKey(nodeA));
		assertEquals(1,initialStateOutgoingEdges.get(nodeA).longValue());
		
		assertTrue(complexTypeRootAutomaton.containsNode(nodeA));
		Map<SchemaElement, Long> nodeAIncomingEdges=complexTypeRootAutomaton.getIncomingEdges(nodeA);
		assertEquals(1,nodeAIncomingEdges.size());
		assertTrue(nodeAIncomingEdges.containsKey(initialState));
		assertEquals(1,nodeAIncomingEdges.get(initialState).longValue());
		Map<SchemaElement, Long> nodeAOutgoingEdges=complexTypeRootAutomaton.getOutgoingEdges(nodeA);
		assertEquals(1,nodeAOutgoingEdges.size());
		assertTrue(nodeAOutgoingEdges.containsKey(nodeB));
		assertEquals(1,nodeAOutgoingEdges.get(nodeB).longValue());
		
		assertTrue(complexTypeRootAutomaton.containsNode(nodeB));
		Map<SchemaElement, Long> nodeBIncomingEdges=complexTypeRootAutomaton.getIncomingEdges(nodeB);
		assertEquals(1,nodeBIncomingEdges.size());
		assertTrue(nodeBIncomingEdges.containsKey(nodeA));
		assertEquals(1,nodeBIncomingEdges.get(nodeA).longValue());
		Map<SchemaElement, Long> nodeBOutgoingEdges=complexTypeRootAutomaton.getOutgoingEdges(nodeB);
		assertEquals(1,nodeBOutgoingEdges.size());
		assertTrue(nodeBOutgoingEdges.containsKey(nodeC));
		assertEquals(1,nodeBOutgoingEdges.get(nodeC).longValue());
		
		assertTrue(complexTypeRootAutomaton.containsNode(nodeC));
		Map<SchemaElement, Long> nodeCIncomingEdges=complexTypeRootAutomaton.getIncomingEdges(nodeC);
		assertEquals(1,nodeCIncomingEdges.size());
		assertTrue(nodeCIncomingEdges.containsKey(nodeB));
		assertEquals(1,nodeCIncomingEdges.get(nodeB).longValue());
		Map<SchemaElement, Long> nodeCOutgoingEdges=complexTypeRootAutomaton.getOutgoingEdges(nodeC);
		assertEquals(1,nodeCOutgoingEdges.size());
		assertTrue(nodeCOutgoingEdges.containsKey(nodeD));
		assertEquals(1,nodeCOutgoingEdges.get(nodeD).longValue());
						
		assertTrue(complexTypeRootAutomaton.containsNode(nodeD));
		Map<SchemaElement, Long> nodeDIncomingEdges=complexTypeRootAutomaton.getIncomingEdges(nodeD);
		assertEquals(1,nodeDIncomingEdges.size());
		assertTrue(nodeDIncomingEdges.containsKey(nodeC));
		assertEquals(1,nodeDIncomingEdges.get(nodeC).longValue());
		Map<SchemaElement, Long> nodeDOutgoingEdges=complexTypeRootAutomaton.getOutgoingEdges(nodeD);
		assertEquals(1,nodeDOutgoingEdges.size());
		assertTrue(nodeDOutgoingEdges.containsKey(finalState));
		assertEquals(1,nodeDOutgoingEdges.get(finalState).longValue());
		
		assertTrue(complexTypeRootAutomaton.containsNode(finalState));
		assertEquals(finalState,complexTypeRootAutomaton.getFinalState());
		Map<SchemaElement, Long> finalStateIncomingEdges = complexTypeRootAutomaton.getIncomingEdges(finalState);
		assertEquals(1,finalStateIncomingEdges.size());
		assertTrue(finalStateIncomingEdges.containsKey(nodeD));
		assertEquals(1,finalStateIncomingEdges.get(nodeD).longValue());
		assertEquals(0,complexTypeRootAutomaton.getOutgoingEdges(finalState).size());
		
		//Attribute list
		
		assertTrue(complexTypeRoot.getAttributeList().isEmpty());
		
		//Text SimpleType
		
		assertTrue(complexTypeRoot.getTextSimpleType().consistOnlyOfWhitespaceCharacters());
	}
	
	/**
	 * This test checks that the complex type _root is correctly generated on the second scenario.
	 */
	@Test
	public void testOnScenario2ComplexTypeRaiz() {
		Schema schema = typesExtractor2.getInitalSchema();
		
		ComplexType complexTypeRaiz=schema.getComplexTypes().get("_raiz");
		assertEquals("_raiz",complexTypeRaiz.getName());
		
		//Source element
		
		Set<String> sourceKeys = complexTypeRaiz.getSourceElementNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":raiz"));
		
		//Automaton
		
		ExtendedAutomaton complexTypeRaizAutomaton=complexTypeRaiz.getAutomaton();
		
		assertEquals(6,complexTypeRaizAutomaton.nodeCount());
		assertEquals(5,complexTypeRaizAutomaton.edgeCount());
		
		SchemaElement nodeA=schema.getElements().get("", "_raiz-a");
		SchemaElement nodeB=schema.getElements().get("", "_raiz-b");
		SchemaElement nodeC=schema.getElements().get("", "_raiz-c");
		SchemaElement nodeD=schema.getElements().get("", "_raiz-d");
		
		assertTrue(complexTypeRaizAutomaton.containsNode(initialState));
		assertEquals(initialState,complexTypeRaizAutomaton.getInitialState());
		assertEquals(0,complexTypeRaizAutomaton.getIncomingEdges(initialState).size());
		Map<SchemaElement, Long> initialStateOutgoingEdges = complexTypeRaizAutomaton.getOutgoingEdges(initialState);
		assertEquals(1,initialStateOutgoingEdges.size());
		assertTrue(initialStateOutgoingEdges.containsKey(nodeA));
		assertEquals(1,initialStateOutgoingEdges.get(nodeA).longValue());
		
		assertTrue(complexTypeRaizAutomaton.containsNode(nodeA));
		Map<SchemaElement, Long> nodeAIncomingEdges=complexTypeRaizAutomaton.getIncomingEdges(nodeA);
		assertEquals(1,nodeAIncomingEdges.size());
		assertTrue(nodeAIncomingEdges.containsKey(initialState));
		assertEquals(1,nodeAIncomingEdges.get(initialState).longValue());
		Map<SchemaElement, Long> nodeAOutgoingEdges=complexTypeRaizAutomaton.getOutgoingEdges(nodeA);
		assertEquals(1,nodeAOutgoingEdges.size());
		assertTrue(nodeAOutgoingEdges.containsKey(nodeB));
		assertEquals(1,nodeAOutgoingEdges.get(nodeB).longValue());
		
		assertTrue(complexTypeRaizAutomaton.containsNode(nodeB));
		Map<SchemaElement, Long> nodeBIncomingEdges=complexTypeRaizAutomaton.getIncomingEdges(nodeB);
		assertEquals(1,nodeBIncomingEdges.size());
		assertTrue(nodeBIncomingEdges.containsKey(nodeA));
		assertEquals(1,nodeBIncomingEdges.get(nodeA).longValue());
		Map<SchemaElement, Long> nodeBOutgoingEdges=complexTypeRaizAutomaton.getOutgoingEdges(nodeB);
		assertEquals(1,nodeBOutgoingEdges.size());
		assertTrue(nodeBOutgoingEdges.containsKey(nodeC));
		assertEquals(1,nodeBOutgoingEdges.get(nodeC).longValue());
		
		assertTrue(complexTypeRaizAutomaton.containsNode(nodeC));
		Map<SchemaElement, Long> nodeCIncomingEdges=complexTypeRaizAutomaton.getIncomingEdges(nodeC);
		assertEquals(1,nodeCIncomingEdges.size());
		assertTrue(nodeCIncomingEdges.containsKey(nodeB));
		assertEquals(1,nodeCIncomingEdges.get(nodeB).longValue());
		Map<SchemaElement, Long> nodeCOutgoingEdges=complexTypeRaizAutomaton.getOutgoingEdges(nodeC);
		assertEquals(1,nodeCOutgoingEdges.size());
		assertTrue(nodeCOutgoingEdges.containsKey(nodeD));
		assertEquals(1,nodeCOutgoingEdges.get(nodeD).longValue());
						
		assertTrue(complexTypeRaizAutomaton.containsNode(nodeD));
		Map<SchemaElement, Long> nodeDIncomingEdges=complexTypeRaizAutomaton.getIncomingEdges(nodeD);
		assertEquals(1,nodeDIncomingEdges.size());
		assertTrue(nodeDIncomingEdges.containsKey(nodeC));
		assertEquals(1,nodeDIncomingEdges.get(nodeC).longValue());
		Map<SchemaElement, Long> nodeDOutgoingEdges=complexTypeRaizAutomaton.getOutgoingEdges(nodeD);
		assertEquals(1,nodeDOutgoingEdges.size());
		assertTrue(nodeDOutgoingEdges.containsKey(finalState));
		assertEquals(1,nodeDOutgoingEdges.get(finalState).longValue());
		
		assertTrue(complexTypeRaizAutomaton.containsNode(finalState));
		assertEquals(finalState,complexTypeRaizAutomaton.getFinalState());
		Map<SchemaElement, Long> finalStateIncomingEdges = complexTypeRaizAutomaton.getIncomingEdges(finalState);
		assertEquals(1,finalStateIncomingEdges.size());
		assertTrue(finalStateIncomingEdges.containsKey(nodeD));
		assertEquals(1,finalStateIncomingEdges.get(nodeD).longValue());
		assertEquals(0,complexTypeRaizAutomaton.getOutgoingEdges(finalState).size());
		
		//Attribute list
		
		List<SchemaAttribute> complexTypeRaizAttrList = complexTypeRaiz.getAttributeList();
		assertEquals(1,complexTypeRaizAttrList.size());
		assertTrue(complexTypeRaizAttrList.contains(schema.getAttributes().get("","_raiz-attr")));
		
		//Text SimpleType
		
		assertTrue(complexTypeRaiz.getTextSimpleType().consistOnlyOfWhitespaceCharacters());
	}

	/**
	 * This method checks that complex type of 'raiz' and 'root' have equal automatons 
	 */
	@Test
	public void testOnScenario2ComplexTypeRootAndComplexTypeRaizHaveEqualAutomatons(){
		Schema schema = typesExtractor2.getInitalSchema();
		
		ComplexType complexTypeRoot=schema.getComplexTypes().get("_root");
		assertEquals("_root",complexTypeRoot.getName());
		ComplexType complexTypeRaiz=schema.getComplexTypes().get("_raiz");
		assertEquals("_raiz",complexTypeRaiz.getName());
		
		assertEquals(complexTypeRaiz.getAutomaton(),complexTypeRoot.getAutomaton());
		
	}
	
	/**
	 * This metod checks that the complex type _a has been properly extracted on the second scenario
	 */
	@Test
	public void testOnScenario2ComplexTypeA(){
		Schema schema = typesExtractor2.getInitalSchema();
		
		ComplexType complexTypeA=schema.getComplexTypes().get("_a");
		assertEquals("_a",complexTypeA.getName());
		
		//Source element
		
		Set<String> sourceKeys = complexTypeA.getSourceElementNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":a"));
		
		//Automaton
		
		ExtendedAutomaton complexTypeAAutomaton=complexTypeA.getAutomaton();
		assertEquals(2,complexTypeAAutomaton.nodeCount());
		assertTrue(complexTypeAAutomaton.containsAllNodes(ImmutableSet.of(initialState, finalState)));
		
		//Attribute list
		
		List<SchemaAttribute> complexTypeAAttrList = complexTypeA.getAttributeList();
		assertTrue(complexTypeAAttrList.isEmpty());
		
		//Text simple type
		
		SimpleType complexTypeATextSimpleType = complexTypeA.getTextSimpleType();
		assertEquals("xs:string",complexTypeATextSimpleType.getBuiltinType());
		assertFalse(complexTypeATextSimpleType.isEnum());
		assertEquals(2,complexTypeATextSimpleType.enumerationCount());
		assertTrue(complexTypeATextSimpleType.enumerationContainsAll(ImmutableSet.of("0", "")));
	}
	
	/**
	 * This metod checks that the complex type _b has been properly extracted on the second scenario
	 */
	@Test
	public void testOnScenario2ComplexTypeB(){
		Schema schema = typesExtractor2.getInitalSchema();
		
		ComplexType complexTypeB=schema.getComplexTypes().get("_b");
		assertEquals("_b",complexTypeB.getName());
		
		SchemaElement elementE = schema.getElements().get("", "_b-e");
		
		//Source element
		
		Set<String> sourceKeys = complexTypeB.getSourceElementNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":b"));
		
		//Automaton
		
		ExtendedAutomaton complexTypeBAutomaton=complexTypeB.getAutomaton();
		assertEquals(3,complexTypeBAutomaton.nodeCount());
		assertEquals(initialState, complexTypeBAutomaton.getInitialState());
		assertEquals(finalState, complexTypeBAutomaton.getFinalState());
		
		assertTrue(complexTypeBAutomaton.containsNode(initialState));
		Map<SchemaElement, Long> initialStateIncomingEdges = complexTypeBAutomaton.getIncomingEdges(initialState);
		assertTrue(initialStateIncomingEdges.isEmpty());
		Map<SchemaElement, Long> initialStateOutgoingEdges = complexTypeBAutomaton.getOutgoingEdges(initialState);
		assertEquals(2,initialStateOutgoingEdges.size());
		assertTrue(initialStateOutgoingEdges.containsKey(finalState));
		assertEquals(1,initialStateOutgoingEdges.get(finalState).longValue());
		assertTrue(initialStateOutgoingEdges.containsKey(elementE));
		assertEquals(1,initialStateOutgoingEdges.get(elementE).longValue());
		
		assertTrue(complexTypeBAutomaton.containsNode(elementE));
		Map<SchemaElement, Long> elementEIncomingEdges = complexTypeBAutomaton.getIncomingEdges(elementE);
		assertEquals(1,elementEIncomingEdges.size());
		assertTrue(elementEIncomingEdges.containsKey(initialState));
		assertEquals(1,elementEIncomingEdges.get(initialState).longValue());
		Map<SchemaElement, Long> elementEOutgoingEdges = complexTypeBAutomaton.getOutgoingEdges(elementE);
		assertEquals(1,elementEOutgoingEdges.size());
		assertTrue(elementEOutgoingEdges.containsKey(finalState));
		assertEquals(1,elementEOutgoingEdges.get(finalState).longValue());
		
		assertTrue(complexTypeBAutomaton.containsNode(finalState));
		Map<SchemaElement, Long> finalStateIncomingEdges = complexTypeBAutomaton.getIncomingEdges(finalState);
		assertEquals(2,finalStateIncomingEdges.size());
		assertTrue(finalStateIncomingEdges.containsKey(initialState));
		assertEquals(1,finalStateIncomingEdges.get(initialState).longValue());
		assertTrue(finalStateIncomingEdges.containsKey(elementE));
		assertEquals(1,finalStateIncomingEdges.get(elementE).longValue());
		Map<SchemaElement, Long> finalStateOutgoingEdges = complexTypeBAutomaton.getOutgoingEdges(finalState);
		assertTrue(finalStateOutgoingEdges.isEmpty());
		
		//Attribute list
		
		List<SchemaAttribute> complexTypeBAttrList = complexTypeB.getAttributeList();
		assertTrue(complexTypeBAttrList.isEmpty());
		
		//Text simple type
		
		SimpleType complexTypeBTextSimpleType = complexTypeB.getTextSimpleType();
		assertEquals("xs:string",complexTypeBTextSimpleType.getBuiltinType());
		assertFalse(complexTypeBTextSimpleType.isEnum());
		assertEquals(2,complexTypeBTextSimpleType.enumerationCount());
		assertTrue(complexTypeBTextSimpleType.enumerationContainsAll(ImmutableSet.of("cosilla", "")));
	}
	
	/**
	 * This metod checks that the complex type _c has been properly extracted on the second scenario
	 */
	@Test
	public void testOnScenario2ComplexTypeC(){
		Schema schema = typesExtractor2.getInitalSchema();
		
		ComplexType complexTypeC=schema.getComplexTypes().get("_c");
		assertEquals("_c",complexTypeC.getName());
		
		SchemaElement elementE = schema.getElements().get("", "_c-e");
		
		//Source element
		
		Set<String> sourceKeys = complexTypeC.getSourceElementNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":c"));
		
		//Automaton
		
		ExtendedAutomaton complexTypeCAutomaton=complexTypeC.getAutomaton();
		assertEquals(3,complexTypeCAutomaton.nodeCount());
		assertEquals(initialState, complexTypeCAutomaton.getInitialState());
		assertEquals(finalState, complexTypeCAutomaton.getFinalState());
		
		assertTrue(complexTypeCAutomaton.containsNode(initialState));
		Map<SchemaElement, Long> initialStateIncomingEdges = complexTypeCAutomaton.getIncomingEdges(initialState);
		assertTrue(initialStateIncomingEdges.isEmpty());
		Map<SchemaElement, Long> initialStateOutgoingEdges = complexTypeCAutomaton.getOutgoingEdges(initialState);
		assertEquals(2,initialStateOutgoingEdges.size());
		assertTrue(initialStateOutgoingEdges.containsKey(finalState));
		assertEquals(1,initialStateOutgoingEdges.get(finalState).longValue());
		assertTrue(initialStateOutgoingEdges.containsKey(elementE));
		assertEquals(1,initialStateOutgoingEdges.get(elementE).longValue());
		
		assertTrue(complexTypeCAutomaton.containsNode(elementE));
		Map<SchemaElement, Long> elementEIncomingEdges = complexTypeCAutomaton.getIncomingEdges(elementE);
		assertEquals(1,elementEIncomingEdges.size());
		assertTrue(elementEIncomingEdges.containsKey(initialState));
		assertEquals(1,elementEIncomingEdges.get(initialState).longValue());
		Map<SchemaElement, Long> elementEOutgoingEdges = complexTypeCAutomaton.getOutgoingEdges(elementE);
		assertEquals(1,elementEOutgoingEdges.size());
		assertTrue(elementEOutgoingEdges.containsKey(finalState));
		assertEquals(1,elementEOutgoingEdges.get(finalState).longValue());
		
		assertTrue(complexTypeCAutomaton.containsNode(finalState));
		Map<SchemaElement, Long> finalStateIncomingEdges = complexTypeCAutomaton.getIncomingEdges(finalState);
		assertEquals(2,finalStateIncomingEdges.size());
		assertTrue(finalStateIncomingEdges.containsKey(initialState));
		assertEquals(1,finalStateIncomingEdges.get(initialState).longValue());
		assertTrue(finalStateIncomingEdges.containsKey(elementE));
		assertEquals(1,finalStateIncomingEdges.get(elementE).longValue());
		Map<SchemaElement, Long> finalStateOutgoingEdges = complexTypeCAutomaton.getOutgoingEdges(finalState);
		assertTrue(finalStateOutgoingEdges.isEmpty());
		
		//Attribute list
		
		List<SchemaAttribute> complexTypeCAttrList = complexTypeC.getAttributeList();
		assertTrue(complexTypeCAttrList.isEmpty());
		
		//Text simple type
		
		SimpleType complexTypeCTextSimpleType = complexTypeC.getTextSimpleType();
		assertEquals("xs:string",complexTypeCTextSimpleType.getBuiltinType());
		assertFalse(complexTypeCTextSimpleType.isEnum());
		assertEquals(2,complexTypeCTextSimpleType.enumerationCount());
		assertTrue(complexTypeCTextSimpleType.enumerationContainsAll(ImmutableSet.of("Esta cosa", "")));
	}
	
	/**
	 * This metod checks that the complex type _d has been properly extracted on the second scenario
	 */
	@Test
	public void testOnScenario2ComplexTypeD(){
		Schema schema = typesExtractor2.getInitalSchema();
		
		ComplexType complexTypeD=schema.getComplexTypes().get("_d");
		assertEquals("_d",complexTypeD.getName());
		
		SchemaElement elementE = schema.getElements().get("", "_d-e");
		
		//Source element
		
		Set<String> sourceKeys = complexTypeD.getSourceElementNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":d"));
		
		//Automaton
		
		ExtendedAutomaton complexTypeDAutomaton=complexTypeD.getAutomaton();
		assertEquals(3,complexTypeDAutomaton.nodeCount());
		assertEquals(initialState, complexTypeDAutomaton.getInitialState());
		assertEquals(finalState, complexTypeDAutomaton.getFinalState());
		
		assertTrue(complexTypeDAutomaton.containsNode(initialState));
		Map<SchemaElement, Long> initialStateIncomingEdges = complexTypeDAutomaton.getIncomingEdges(initialState);
		assertTrue(initialStateIncomingEdges.isEmpty());
		Map<SchemaElement, Long> initialStateOutgoingEdges = complexTypeDAutomaton.getOutgoingEdges(initialState);
		assertEquals(2,initialStateOutgoingEdges.size());
		assertTrue(initialStateOutgoingEdges.containsKey(finalState));
		assertEquals(1,initialStateOutgoingEdges.get(finalState).longValue());
		assertTrue(initialStateOutgoingEdges.containsKey(elementE));
		assertEquals(1,initialStateOutgoingEdges.get(elementE).longValue());
		
		assertTrue(complexTypeDAutomaton.containsNode(elementE));
		Map<SchemaElement, Long> elementEIncomingEdges = complexTypeDAutomaton.getIncomingEdges(elementE);
		assertEquals(1,elementEIncomingEdges.size());
		assertTrue(elementEIncomingEdges.containsKey(initialState));
		assertEquals(1,elementEIncomingEdges.get(initialState).longValue());
		Map<SchemaElement, Long> elementEOutgoingEdges = complexTypeDAutomaton.getOutgoingEdges(elementE);
		assertEquals(1,elementEOutgoingEdges.size());
		assertTrue(elementEOutgoingEdges.containsKey(finalState));
		assertEquals(1,elementEOutgoingEdges.get(finalState).longValue());
		
		assertTrue(complexTypeDAutomaton.containsNode(finalState));
		Map<SchemaElement, Long> finalStateIncomingEdges = complexTypeDAutomaton.getIncomingEdges(finalState);
		assertEquals(2,finalStateIncomingEdges.size());
		assertTrue(finalStateIncomingEdges.containsKey(initialState));
		assertEquals(1,finalStateIncomingEdges.get(initialState).longValue());
		assertTrue(finalStateIncomingEdges.containsKey(elementE));
		assertEquals(1,finalStateIncomingEdges.get(elementE).longValue());
		Map<SchemaElement, Long> finalStateOutgoingEdges = complexTypeDAutomaton.getOutgoingEdges(finalState);
		assertTrue(finalStateOutgoingEdges.isEmpty());
		
		//Attribute list
		
		List<SchemaAttribute> complexTypeDAttrList = complexTypeD.getAttributeList();
		assertTrue(complexTypeDAttrList.isEmpty());
		
		//Text simple type
		
		SimpleType complexTypeDTextSimpleType = complexTypeD.getTextSimpleType();
		assertTrue(complexTypeDTextSimpleType.isEmpty());
	}
	
	/**
	 * This metod checks that the complex type _e has been properly extracted on the second scenario
	 */
	@Test
	public void testOnScenario2ComplexTypeE(){
		Schema schema = typesExtractor2.getInitalSchema();
		
		ComplexType complexTypeE=schema.getComplexTypes().get("_e");
		assertEquals("_e",complexTypeE.getName());
		
//		SchemaElement elementE = schema.getElements().get("", "_d-e");
		
		//Source element
		
		Set<String> sourceKeys = complexTypeE.getSourceElementNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":e"));
		
		//Automaton
		
		ExtendedAutomaton complexTypeEAutomaton=complexTypeE.getAutomaton();
		assertEquals(2,complexTypeEAutomaton.nodeCount());
		assertEquals(initialState, complexTypeEAutomaton.getInitialState());
		assertEquals(finalState, complexTypeEAutomaton.getFinalState());
		
		assertTrue(complexTypeEAutomaton.containsNode(initialState));
		Map<SchemaElement, Long> initialStateIncomingEdges = complexTypeEAutomaton.getIncomingEdges(initialState);
		assertTrue(initialStateIncomingEdges.isEmpty());
		Map<SchemaElement, Long> initialStateOutgoingEdges = complexTypeEAutomaton.getOutgoingEdges(initialState);
		assertEquals(1,initialStateOutgoingEdges.size());
		assertTrue(initialStateOutgoingEdges.containsKey(finalState));
		assertEquals(3,initialStateOutgoingEdges.get(finalState).longValue());
				
		assertTrue(complexTypeEAutomaton.containsNode(finalState));
		Map<SchemaElement, Long> finalStateIncomingEdges = complexTypeEAutomaton.getIncomingEdges(finalState);
		assertEquals(1,finalStateIncomingEdges.size());
		assertTrue(finalStateIncomingEdges.containsKey(initialState));
		assertEquals(3,finalStateIncomingEdges.get(initialState).longValue());
		Map<SchemaElement, Long> finalStateOutgoingEdges = complexTypeEAutomaton.getOutgoingEdges(finalState);
		assertTrue(finalStateOutgoingEdges.isEmpty());
		
		//Attribute list
		
		List<SchemaAttribute> complexTypeEAttrList = complexTypeE.getAttributeList();
		assertTrue(complexTypeEAttrList.isEmpty());
		
		//Text simple type
		
		SimpleType complexTypeETextSimpleType = complexTypeE.getTextSimpleType();
		assertTrue(complexTypeETextSimpleType.isEmpty());
	}
	
	/**
	 * This method checks that the general statistics of the second scenario are correctly generated.
	 */
	@Test
	public void testOnScenario2GeneralStatistics(){
		Schema schema = typesExtractor2.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		//Input documents count
		assertEquals(2, statistics.getInputDocumentsCount());
		
		//Depth
		assertEquals(3,statistics.getMaxDepth());
		assertEquals(2.071,statistics.getAvgDepth(), 0.001);
		
		//Width
		assertEquals(4,statistics.getMaxWidth());
		assertEquals(1.875,statistics.getAvgWidth(),0.001);
	}
	
	/**
	 * This method checks that the root elements occurrences info is correctly generated on the second scenario.
	 */
	@Test
	public void testOnScenario2RootElementsOccurrences(){
		Schema schema = typesExtractor2.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Map<SchemaElement, Integer> rootElementOccurrences = statistics.getRootElementOccurrences();
		assertEquals(2,rootElementOccurrences.keySet().size());
		SchemaElement elementRoot = schema.getElements().get("", "root");
		assertTrue(rootElementOccurrences.containsKey(elementRoot));
		assertEquals(1,rootElementOccurrences.get(elementRoot).intValue());
		SchemaElement elementRaiz = schema.getElements().get("", "raiz");
		assertTrue(rootElementOccurrences.containsKey(elementRaiz));
		assertEquals(1,rootElementOccurrences.get(elementRaiz).intValue());
		
	}
	
	/**
	 * This method checks that the elements at path information of the second scenario is well generated.
	 */
	@Test
	public void testOnScenario2ElementsAtPathStatistics(){
		Schema schema = typesExtractor2.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		Map<String, BasicStatisticsEntry> elementAtPathInfo = statistics.getElementAtPathInfo();
		assertEquals(13,elementAtPathInfo.size());
				
		// /root
		BasicStatisticsEntry rootEntry = elementAtPathInfo.get("/root");
		assertEquals(0.5,rootEntry.getAverage(),0.001);
		assertEquals(0.25,rootEntry.getVariance(),0.001);
		assertEquals(1,rootEntry.getConditionedAverage(),0.001);
		assertEquals(0,rootEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),rootEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,rootEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,rootEntry.getMin());
		assertEquals(1,rootEntry.getTotal());
		assertEquals(0.5,rootEntry.getNonZeroRatio(),0.001);
		assertEquals(1,rootEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,rootEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /root/a
		BasicStatisticsEntry elementRootAEntry = elementAtPathInfo.get("/root/a");
		assertEquals(0.5,elementRootAEntry.getAverage(),0.001);
		assertEquals(0.25,elementRootAEntry.getVariance(),0.001);
		assertEquals(1,elementRootAEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRootAEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRootAEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRootAEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRootAEntry.getMin());
		assertEquals(1,elementRootAEntry.getTotal());
		assertEquals(0.5,elementRootAEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRootAEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRootAEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /root/b
		BasicStatisticsEntry elementRootBEntry = elementAtPathInfo.get("/root/b");
		assertEquals(0.5,elementRootBEntry.getAverage(),0.001);
		assertEquals(0.25,elementRootBEntry.getVariance(),0.001);
		assertEquals(1,elementRootBEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRootBEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRootBEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRootBEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRootBEntry.getMin());
		assertEquals(1,elementRootBEntry.getTotal());
		assertEquals(0.5,elementRootBEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRootBEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRootBEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /root/c
		BasicStatisticsEntry elementRootCEntry = elementAtPathInfo.get("/root/c");
		assertEquals(0.5,elementRootCEntry.getAverage(),0.001);
		assertEquals(0.25,elementRootCEntry.getVariance(),0.001);
		assertEquals(1,elementRootCEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRootCEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRootCEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRootCEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRootCEntry.getMin());
		assertEquals(1,elementRootCEntry.getTotal());
		assertEquals(0.5,elementRootCEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRootCEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRootCEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /root/d
		BasicStatisticsEntry elementRootDEntry = elementAtPathInfo.get("/root/d");
		assertEquals(0.5,elementRootDEntry.getAverage(),0.001);
		assertEquals(0.25,elementRootDEntry.getVariance(),0.001);
		assertEquals(1,elementRootDEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRootDEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRootDEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRootDEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRootDEntry.getMin());
		assertEquals(1,elementRootDEntry.getTotal());
		assertEquals(0.5,elementRootDEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRootDEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRootDEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /root/c/e
		BasicStatisticsEntry elementRootCEEntry = elementAtPathInfo.get("/root/c/e");
		assertEquals(0.5,elementRootCEEntry.getAverage(),0.001);
		assertEquals(0.25,elementRootCEEntry.getVariance(),0.001);
		assertEquals(1,elementRootCEEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRootCEEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRootCEEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRootCEEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRootCEEntry.getMin());
		assertEquals(1,elementRootCEEntry.getTotal());
		assertEquals(0.5,elementRootCEEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRootCEEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRootCEEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /root/d/e
		BasicStatisticsEntry elementRootDEEntry = elementAtPathInfo.get("/root/d/e");
		assertEquals(0.5,elementRootDEEntry.getAverage(),0.001);
		assertEquals(0.25,elementRootDEEntry.getVariance(),0.001);
		assertEquals(1,elementRootDEEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRootDEEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRootDEEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRootDEEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRootDEEntry.getMin());
		assertEquals(1,elementRootDEEntry.getTotal());
		assertEquals(0.5,elementRootDEEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRootDEEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRootDEEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /raiz
		BasicStatisticsEntry raizEntry = elementAtPathInfo.get("/raiz");
		assertEquals(0.5,raizEntry.getAverage(),0.001);
		assertEquals(0.25,raizEntry.getVariance(),0.001);
		assertEquals(1,raizEntry.getConditionedAverage(),0.001);
		assertEquals(0,raizEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),raizEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,raizEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,raizEntry.getMin());
		assertEquals(1,raizEntry.getTotal());
		assertEquals(0.5,raizEntry.getNonZeroRatio(),0.001);
		assertEquals(1,raizEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,raizEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /raiz/a
		BasicStatisticsEntry elementRaizAEntry = elementAtPathInfo.get("/raiz/a");
		assertEquals(0.5,elementRaizAEntry.getAverage(),0.001);
		assertEquals(0.25,elementRaizAEntry.getVariance(),0.001);
		assertEquals(1,elementRaizAEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRaizAEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRaizAEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRaizAEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRaizAEntry.getMin());
		assertEquals(1,elementRaizAEntry.getTotal());
		assertEquals(0.5,elementRaizAEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRaizAEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRaizAEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /raiz/b
		BasicStatisticsEntry elementRaizBEntry = elementAtPathInfo.get("/raiz/b");
		assertEquals(0.5,elementRaizBEntry.getAverage(),0.001);
		assertEquals(0.25,elementRaizBEntry.getVariance(),0.001);
		assertEquals(1,elementRaizBEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRaizBEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRaizBEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRaizBEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRaizBEntry.getMin());
		assertEquals(1,elementRaizBEntry.getTotal());
		assertEquals(0.5,elementRaizBEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRaizBEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRaizBEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /raiz/c
		BasicStatisticsEntry elementRaizCEntry = elementAtPathInfo.get("/raiz/c");
		assertEquals(0.5,elementRaizCEntry.getAverage(),0.001);
		assertEquals(0.25,elementRaizCEntry.getVariance(),0.001);
		assertEquals(1,elementRaizCEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRaizCEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRaizCEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRaizCEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRaizCEntry.getMin());
		assertEquals(1,elementRaizCEntry.getTotal());
		assertEquals(0.5,elementRaizCEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRaizCEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRaizCEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /raiz/d
		BasicStatisticsEntry elementRaizDEntry = elementAtPathInfo.get("/raiz/d");
		assertEquals(0.5,elementRaizDEntry.getAverage(),0.001);
		assertEquals(0.25,elementRaizDEntry.getVariance(),0.001);
		assertEquals(1,elementRaizDEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRaizDEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRaizDEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRaizDEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRaizDEntry.getMin());
		assertEquals(1,elementRaizDEntry.getTotal());
		assertEquals(0.5,elementRaizDEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRaizDEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRaizDEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /raiz/b/e
		BasicStatisticsEntry elementRaizBEEntry = elementAtPathInfo.get("/raiz/b/e");
		assertEquals(0.5,elementRaizBEEntry.getAverage(),0.001);
		assertEquals(0.25,elementRaizBEEntry.getVariance(),0.001);
		assertEquals(1,elementRaizBEEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRaizBEEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRaizBEEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRaizBEEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRaizBEEntry.getMin());
		assertEquals(1,elementRaizBEEntry.getTotal());
		assertEquals(0.5,elementRaizBEEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRaizBEEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRaizBEEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
	}
	
	/**
	 * This method checks that the attributes at path information of the second scenario is well generated.
	 */
	@Test
	public void testOnScenario2AttributesAtPathInfo(){
		Schema schema = typesExtractor2.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		Map<String, BasicStatisticsEntry> attributeAtPathInfo = statistics.getAttributeAtPathInfo();
		assertEquals(1, attributeAtPathInfo.size());
		
		// /raiz/@attr
		BasicStatisticsEntry attrEntry = attributeAtPathInfo.get("/raiz/@attr");
		assertEquals(0.5,attrEntry.getAverage(),0.001);
		assertEquals(0.25,attrEntry.getVariance(),0.001);
		assertEquals(1,attrEntry.getConditionedAverage(),0.001);
		assertEquals(0,attrEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attrEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attrEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attrEntry.getMin());
		assertEquals(1,attrEntry.getTotal());
		assertEquals(0.5,attrEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attrEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attrEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
	}
	
	/**
	 * This method checks that the values at path information of the second scenario is well generated.
	 */
	@Test
	public void testOnScenario2ValuesAtPathInfo(){
		Schema schema = typesExtractor2.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Table<String, String, BasicStatisticsEntry> valuesAtPathInfo = statistics.getValuesAtPathInfo();
		
		
		// /root/b = 'cosilla'
		Map<String,BasicStatisticsEntry> elementRootBValues = valuesAtPathInfo.row("/root/b");
		assertFalse(elementRootBValues.isEmpty());
		assertEquals(1,elementRootBValues.keySet().size());
		BasicStatisticsEntry elementRootBValueCosillaEntry = elementRootBValues.get("cosilla");
		assertNotNull(elementRootBValueCosillaEntry);
		assertEquals(0.5,elementRootBValueCosillaEntry.getAverage(),0.001);
		assertEquals(0.25,elementRootBValueCosillaEntry.getVariance(),0.001);
		assertEquals(1,elementRootBValueCosillaEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,elementRootBValueCosillaEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRootBValueCosillaEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRootBValueCosillaEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRootBValueCosillaEntry.getMin());
		assertEquals(1,elementRootBValueCosillaEntry.getTotal());
		assertEquals(0.5,elementRootBValueCosillaEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRootBValueCosillaEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRootBValueCosillaEntry.getConditionedStandardDeviationAverageRatio(),0.001);
				
		// /raiz/c = 'Esta cosa'
		Map<String,BasicStatisticsEntry> elementRootCValues = valuesAtPathInfo.row("/root/c");
		assertFalse(elementRootCValues.isEmpty());
		assertEquals(1,elementRootCValues.keySet().size());
		BasicStatisticsEntry elementRootCValueEstaCosa = elementRootCValues.get("Esta cosa");
		assertNotNull(elementRootCValueEstaCosa);
		assertEquals(0.5,elementRootCValueEstaCosa.getAverage(),0.001);
		assertEquals(0.25,elementRootCValueEstaCosa.getVariance(),0.001);
		assertEquals(1,elementRootCValueEstaCosa.getConditionedAverage(),0.001);
		assertEquals(0.0,elementRootCValueEstaCosa.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRootCValueEstaCosa.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRootCValueEstaCosa.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRootCValueEstaCosa.getMin());
		assertEquals(1,elementRootCValueEstaCosa.getTotal());
		assertEquals(0.5,elementRootCValueEstaCosa.getNonZeroRatio(),0.001);
		assertEquals(1,elementRootCValueEstaCosa.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRootCValueEstaCosa.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /root/c no debe tener valores
		Map<String,BasicStatisticsEntry> elementRaizCValues = valuesAtPathInfo.row("/raiz/c");
		assertTrue(valuesEmptyOrOnlyConsistOfWhitespaceCharacters(elementRaizCValues.keySet()));
		
		
		// /raiz/a = '0'
		Map<String,BasicStatisticsEntry> elementRaizAValues = valuesAtPathInfo.row("/raiz/a");
		assertFalse(elementRaizAValues.isEmpty());
		BasicStatisticsEntry elementRaizAValue0Entry = elementRaizAValues.get("0");
		assertNotNull(elementRaizAValue0Entry);
		assertEquals(0.5,elementRaizAValue0Entry.getAverage(),0.001);
		assertEquals(0.25,elementRaizAValue0Entry.getVariance(),0.001);
		assertEquals(1,elementRaizAValue0Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,elementRaizAValue0Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRaizAValue0Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRaizAValue0Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRaizAValue0Entry.getMin());
		assertEquals(1,elementRaizAValue0Entry.getTotal());
		assertEquals(0.5,elementRaizAValue0Entry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRaizAValue0Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRaizAValue0Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /raiz/@attr
		Map<String,BasicStatisticsEntry> attrValues = valuesAtPathInfo.row("/raiz/@attr");
		assertFalse(attrValues.isEmpty());
		BasicStatisticsEntry attrValueValueEntry = attrValues.get("value");
		assertNotNull(attrValueValueEntry);
		assertEquals(0.5,attrValueValueEntry.getAverage(),0.001);
		assertEquals(0.25,attrValueValueEntry.getVariance(),0.001);
		assertEquals(1,attrValueValueEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,attrValueValueEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attrValueValueEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attrValueValueEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attrValueValueEntry.getMin());
		assertEquals(1,attrValueValueEntry.getTotal());
		assertEquals(0.5,attrValueValueEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attrValueValueEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attrValueValueEntry.getConditionedStandardDeviationAverageRatio(),0.001);
	}
	
	/**
	 * This method checks that the statistics of numeric values at path of the second scenario are well generated.
	 */
	@Test
	public void testOnScenario2StatisticsOfNumericValuesAtPath(){
		Schema schema = typesExtractor2.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		Map<String, BasicStatisticsEntry> statisticsOfNumericValuesAtPath = statistics.getStatisticsOfNumericValuesAtPath();
		
		// /raiz/a
		BasicStatisticsEntry elementRaizAEntry = statisticsOfNumericValuesAtPath.get("/raiz/a");
		assertNotNull(elementRaizAEntry);
		assertEquals(0,elementRaizAEntry.getAverage(),0.001);
		assertEquals(0,elementRaizAEntry.getVariance(),0.001);
		assertEquals(Double.NaN,elementRaizAEntry.getConditionedAverage(),0.001);
		assertEquals(Double.NaN,elementRaizAEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRaizAEntry.getMode());
		assertEquals(VALUE_0_FREQUENCY_1,elementRaizAEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRaizAEntry.getMin());
		assertEquals(0,elementRaizAEntry.getTotal());
		assertEquals(0,elementRaizAEntry.getNonZeroRatio(),0.001);
		assertEquals(Double.NaN,elementRaizAEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(Double.NaN,elementRaizAEntry.getConditionedStandardDeviationAverageRatio(),0.001);
	}
	
	/**
	 * This method checks that the statistic information of the complex type _root from the second scenario is well generated.
	 */
	@Test
	public void testOnScenario2ComplexTypeRootStatisticsEntry(){
		Schema schema = typesExtractor2.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Table<String,String,SchemaElement> elements =  schema.getElements();
		SchemaElement elementRootA=elements.get("", "_root-a");
		SchemaElement elementRootB=elements.get("", "_root-b");
		SchemaElement elementRootC=elements.get("", "_root-c");
		SchemaElement elementRootD=elements.get("", "_root-d");
		
		ComplexTypeStatisticsEntry complexTypeRootStatisticsEntry = statistics.getComplexTypeStatisticsEntryByName("_root");
		assertEquals(2, complexTypeRootStatisticsEntry.getInputDocumentsCount());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeRootStatisticsEntry.getElementInfo();
		assertEquals(4, elementInfo.size());
		
		// /root/a
		BasicStatisticsEntry elementRootAEntry = elementInfo.get(elementRootA);
		assertEquals(0.5,elementRootAEntry.getAverage(),0.001);
		assertEquals(0.25,elementRootAEntry.getVariance(),0.001);
		assertEquals(1,elementRootAEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRootAEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRootAEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRootAEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRootAEntry.getMin());
		assertEquals(1,elementRootAEntry.getTotal());
		assertEquals(0.5,elementRootAEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRootAEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRootAEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /root/b
		BasicStatisticsEntry elementRootBEntry = elementInfo.get(elementRootB);
		assertEquals(0.5,elementRootBEntry.getAverage(),0.001);
		assertEquals(0.25,elementRootBEntry.getVariance(),0.001);
		assertEquals(1,elementRootBEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRootBEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRootBEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRootBEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRootBEntry.getMin());
		assertEquals(1,elementRootBEntry.getTotal());
		assertEquals(0.5,elementRootBEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRootBEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRootBEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /root/c
		BasicStatisticsEntry elementRootCEntry = elementInfo.get(elementRootC);
		assertEquals(0.5,elementRootCEntry.getAverage(),0.001);
		assertEquals(0.25,elementRootCEntry.getVariance(),0.001);
		assertEquals(1,elementRootCEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRootCEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRootCEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRootCEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRootCEntry.getMin());
		assertEquals(1,elementRootCEntry.getTotal());
		assertEquals(0.5,elementRootCEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRootCEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRootCEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /root/d
		BasicStatisticsEntry elementRootDEntry = elementInfo.get(elementRootD);
		assertEquals(0.5,elementRootDEntry.getAverage(),0.001);
		assertEquals(0.25,elementRootDEntry.getVariance(),0.001);
		assertEquals(1,elementRootDEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRootDEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRootDEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRootDEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRootDEntry.getMin());
		assertEquals(1,elementRootDEntry.getTotal());
		assertEquals(0.5,elementRootDEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRootDEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRootDEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeRootStatisticsEntry.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfo.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeRootStatisticsEntry.getValuesInfo();
		assertTrue(valuesInfo.isEmpty()||valuesEmptyOrOnlyConsistOfWhitespaceCharacters(valuesInfo.rowKeySet()));
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeRootStatisticsEntry.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodes.isEmpty());
	}
	
	/**
	 * This method checks that the statistic information of the complex type _raiz from the second scenario is well generated.
	 */
	@Test
	public void testOnScenario2ComplexTypeRaizStatisticsEntry(){
		Schema schema = typesExtractor2.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Table<String,String,SchemaElement> elements =  schema.getElements();
		SchemaElement elementRaizA=elements.get("", "_raiz-a");
		SchemaElement elementRaizB=elements.get("", "_raiz-b");
		SchemaElement elementRaizC=elements.get("", "_raiz-c");
		SchemaElement elementRaizD=elements.get("", "_raiz-d");
		
		Table <String,String,SchemaAttribute> attributes = schema.getAttributes();
		SchemaAttribute attributeAttr=attributes.get("","_raiz-attr");
		
		ComplexTypeStatisticsEntry complexTypeRaizStatisticsEntry = statistics.getComplexTypeStatisticsEntryByName("_raiz");
		assertEquals(2, complexTypeRaizStatisticsEntry.getInputDocumentsCount());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeRaizStatisticsEntry.getElementInfo();
		
		// /raiz/a
		BasicStatisticsEntry elementRaizAEntry = elementInfo.get(elementRaizA);
		assertEquals(0.5,elementRaizAEntry.getAverage(),0.001);
		assertEquals(0.25,elementRaizAEntry.getVariance(),0.001);
		assertEquals(1,elementRaizAEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRaizAEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRaizAEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRaizAEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRaizAEntry.getMin());
		assertEquals(1,elementRaizAEntry.getTotal());
		assertEquals(0.5,elementRaizAEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRaizAEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRaizAEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /raiz/b
		BasicStatisticsEntry elementRaizBEntry = elementInfo.get(elementRaizB);
		assertEquals(0.5,elementRaizBEntry.getAverage(),0.001);
		assertEquals(0.25,elementRaizBEntry.getVariance(),0.001);
		assertEquals(1,elementRaizBEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRaizBEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRaizBEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRaizBEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRaizBEntry.getMin());
		assertEquals(1,elementRaizBEntry.getTotal());
		assertEquals(0.5,elementRaizBEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRaizBEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRaizBEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /raiz/c
		BasicStatisticsEntry elementRaizCEntry = elementInfo.get(elementRaizC);
		assertEquals(0.5,elementRaizCEntry.getAverage(),0.001);
		assertEquals(0.25,elementRaizCEntry.getVariance(),0.001);
		assertEquals(1,elementRaizCEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRaizCEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRaizCEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRaizCEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRaizCEntry.getMin());
		assertEquals(1,elementRaizCEntry.getTotal());
		assertEquals(0.5,elementRaizCEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRaizCEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRaizCEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// /raiz/d
		BasicStatisticsEntry elementRaizDEntry = elementInfo.get(elementRaizD);
		assertEquals(0.5,elementRaizDEntry.getAverage(),0.001);
		assertEquals(0.25,elementRaizDEntry.getVariance(),0.001);
		assertEquals(1,elementRaizDEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementRaizDEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementRaizDEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementRaizDEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementRaizDEntry.getMin());
		assertEquals(1,elementRaizDEntry.getTotal());
		assertEquals(0.5,elementRaizDEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementRaizDEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementRaizDEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeRaizStatisticsEntry.getAttributeOccurrencesInfo();
		// _raiz-@attr
		BasicStatisticsEntry attrEntry = attributeOccurrencesInfo.get(attributeAttr);
		assertEquals(0.5,attrEntry.getAverage(),0.001);
		assertEquals(0.25,attrEntry.getVariance(),0.001);
		assertEquals(1,attrEntry.getConditionedAverage(),0.001);
		assertEquals(0,attrEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attrEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attrEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attrEntry.getMin());
		assertEquals(1,attrEntry.getTotal());
		assertEquals(0.5,attrEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attrEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attrEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeRaizStatisticsEntry.getValuesInfo();
		// _raiz-@attr
		Map<String,BasicStatisticsEntry> attributeAttrValues = valuesInfo.column(attributeAttr);
		assertFalse(attributeAttrValues.isEmpty());
		BasicStatisticsEntry attrValueValueEntry = attributeAttrValues.get("value");
		assertNotNull(attrValueValueEntry);
		assertEquals(0.5,attrValueValueEntry.getAverage(),0.001);
		assertEquals(0.25,attrValueValueEntry.getVariance(),0.001);
		assertEquals(1,attrValueValueEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,attrValueValueEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attrValueValueEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attrValueValueEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attrValueValueEntry.getMin());
		assertEquals(1,attrValueValueEntry.getTotal());
		assertEquals(0.5,attrValueValueEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attrValueValueEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attrValueValueEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeRaizStatisticsEntry.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodes.isEmpty());
	}
	
	/**
	 * This method checks that the statistics of complex type _a have been properly extracted on the second scenario
	 */
	@Test
	public void testOnScenario2ComplexTypeAStatisticsEntry(){
		Schema schema = typesExtractor2.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Table<String,String,SchemaElement> elements =  schema.getElements();
		SchemaElement elementRaizA=elements.get("", "_raiz-a");

		ComplexTypeStatisticsEntry complexTypeAStatisticsEntry = statistics.getComplexTypeStatisticsEntryByName("_a");
		assertEquals(2, complexTypeAStatisticsEntry.getInputDocumentsCount());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeAStatisticsEntry.getElementInfo();
		assertTrue(elementInfo.isEmpty());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeAStatisticsEntry.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfo.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeAStatisticsEntry.getValuesInfo();
		assertEquals(2,valuesInfo.size());
		// _a = "0"
		Map<String,BasicStatisticsEntry> elementAValues = valuesInfo.column(elementRaizA);
		assertFalse(elementAValues.isEmpty());
		BasicStatisticsEntry attrValue0Entry = elementAValues.get("0");
		assertNotNull(attrValue0Entry);
		assertEquals(0.5,attrValue0Entry.getAverage(),0.001);
		assertEquals(0.25,attrValue0Entry.getVariance(),0.001);
		assertEquals(1,attrValue0Entry.getConditionedAverage(),0.001);
		assertEquals(0.0,attrValue0Entry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attrValue0Entry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attrValue0Entry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attrValue0Entry.getMin());
		assertEquals(1,attrValue0Entry.getTotal());
		assertEquals(0.5,attrValue0Entry.getNonZeroRatio(),0.001);
		assertEquals(1,attrValue0Entry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attrValue0Entry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// _a = ""
		BasicStatisticsEntry attrValueEmptyEntry = elementAValues.get("");
		assertNotNull(attrValueEmptyEntry);
		assertEquals(0.5,attrValueEmptyEntry.getAverage(),0.001);
		assertEquals(0.25,attrValueEmptyEntry.getVariance(),0.001);
		assertEquals(1,attrValueEmptyEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,attrValueEmptyEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attrValueEmptyEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attrValueEmptyEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attrValueEmptyEntry.getMin());
		assertEquals(1,attrValueEmptyEntry.getTotal());
		assertEquals(0.5,attrValueEmptyEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attrValueEmptyEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attrValueEmptyEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeAStatisticsEntry.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodes.isEmpty());
	}
	
	/**
	 * This method checks that the statistics of complex type _b have been properly extracted on the second scenario
	 */
	@Test
	public void testOnScenario2ComplexTypeBStatisticsEntry(){
		Schema schema = typesExtractor2.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Table<String,String,SchemaElement> elements =  schema.getElements();
		SchemaElement elementRaizB=elements.get("", "_raiz-b");
		SchemaElement elementBE=elements.get("", "_b-e");

		ComplexTypeStatisticsEntry complexTypeBStatisticsEntry = statistics.getComplexTypeStatisticsEntryByName("_b");
		assertEquals(2, complexTypeBStatisticsEntry.getInputDocumentsCount());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeBStatisticsEntry.getElementInfo();
		assertEquals(1, elementInfo.size());
		// _b-e
		BasicStatisticsEntry elementBEntry = elementInfo.get(elementBE);
		assertEquals(0.5,elementBEntry.getAverage(),0.001);
		assertEquals(0.25,elementBEntry.getVariance(),0.001);
		assertEquals(1,elementBEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementBEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementBEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementBEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementBEntry.getMin());
		assertEquals(1,elementBEntry.getTotal());
		assertEquals(0.5,elementBEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementBEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementBEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeBStatisticsEntry.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfo.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeBStatisticsEntry.getValuesInfo();
		assertEquals(2,valuesInfo.size());
		// _b = "cosilla"
		Map<String,BasicStatisticsEntry> elementBValues = valuesInfo.column(elementRaizB);
		assertFalse(elementBValues.isEmpty());
		BasicStatisticsEntry attrValueCosillaEntry = elementBValues.get("cosilla");
		assertNotNull(attrValueCosillaEntry);
		assertEquals(0.5,attrValueCosillaEntry.getAverage(),0.001);
		assertEquals(0.25,attrValueCosillaEntry.getVariance(),0.001);
		assertEquals(1,attrValueCosillaEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,attrValueCosillaEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attrValueCosillaEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attrValueCosillaEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attrValueCosillaEntry.getMin());
		assertEquals(1,attrValueCosillaEntry.getTotal());
		assertEquals(0.5,attrValueCosillaEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attrValueCosillaEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attrValueCosillaEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// _b = ""
		BasicStatisticsEntry attrValueEmptyEntry = elementBValues.get("");
		assertNotNull(attrValueEmptyEntry);
		assertEquals(0.5,attrValueEmptyEntry.getAverage(),0.001);
		assertEquals(0.25,attrValueEmptyEntry.getVariance(),0.001);
		assertEquals(1,attrValueEmptyEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,attrValueEmptyEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attrValueEmptyEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attrValueEmptyEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attrValueEmptyEntry.getMin());
		assertEquals(1,attrValueEmptyEntry.getTotal());
		assertEquals(0.5,attrValueEmptyEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attrValueEmptyEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attrValueEmptyEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeBStatisticsEntry.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodes.isEmpty());
	}
	
	/**
	 * This method checks that the statistics of complex type _c have been properly extracted on the second scenario
	 */
	@Test
	public void testOnScenario2ComplexTypeCStatisticsEntry(){
		Schema schema = typesExtractor2.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Table<String,String,SchemaElement> elements =  schema.getElements();
		SchemaElement elementRaizC=elements.get("", "_raiz-c");
		SchemaElement elementCE=elements.get("", "_c-e");
		
		ComplexTypeStatisticsEntry complexTypeCStatisticsEntry = statistics.getComplexTypeStatisticsEntryByName("_c");
		assertEquals(2, complexTypeCStatisticsEntry.getInputDocumentsCount());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeCStatisticsEntry.getElementInfo();
		assertEquals(1, elementInfo.size());
		// _c-e
		BasicStatisticsEntry elementCEntry = elementInfo.get(elementCE);
		assertEquals(0.5,elementCEntry.getAverage(),0.001);
		assertEquals(0.25,elementCEntry.getVariance(),0.001);
		assertEquals(1,elementCEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementCEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementCEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementCEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementCEntry.getMin());
		assertEquals(1,elementCEntry.getTotal());
		assertEquals(0.5,elementCEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementCEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementCEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeCStatisticsEntry.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfo.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeCStatisticsEntry.getValuesInfo();
		assertEquals(2,valuesInfo.size());
		// _c = "Esta cosa"
		Map<String,BasicStatisticsEntry> elementBValues = valuesInfo.column(elementRaizC);
		assertFalse(elementBValues.isEmpty());
		BasicStatisticsEntry attrValueEstaCosaEntry = elementBValues.get("Esta cosa");
		assertNotNull(attrValueEstaCosaEntry);
		assertEquals(0.5,attrValueEstaCosaEntry.getAverage(),0.001);
		assertEquals(0.25,attrValueEstaCosaEntry.getVariance(),0.001);
		assertEquals(1,attrValueEstaCosaEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,attrValueEstaCosaEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attrValueEstaCosaEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attrValueEstaCosaEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attrValueEstaCosaEntry.getMin());
		assertEquals(1,attrValueEstaCosaEntry.getTotal());
		assertEquals(0.5,attrValueEstaCosaEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attrValueEstaCosaEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attrValueEstaCosaEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		// _c = ""
		BasicStatisticsEntry attrValueEmptyEntry = elementBValues.get("");
		assertNotNull(attrValueEmptyEntry);
		assertEquals(0.5,attrValueEmptyEntry.getAverage(),0.001);
		assertEquals(0.25,attrValueEmptyEntry.getVariance(),0.001);
		assertEquals(1,attrValueEmptyEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,attrValueEmptyEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attrValueEmptyEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attrValueEmptyEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attrValueEmptyEntry.getMin());
		assertEquals(1,attrValueEmptyEntry.getTotal());
		assertEquals(0.5,attrValueEmptyEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attrValueEmptyEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attrValueEmptyEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeCStatisticsEntry.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodes.isEmpty());
	}
	
	/**
	 * This method checks that the statistics of complex type _d have been properly extracted on the second scenario
	 */
	@Test
	public void testOnScenario2ComplexTypeDStatisticsEntry(){
		Schema schema = typesExtractor2.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Table<String,String,SchemaElement> elements =  schema.getElements();
		SchemaElement elementDE=elements.get("", "_d-e");
		
		ComplexTypeStatisticsEntry complexTypeDStatisticsEntry = statistics.getComplexTypeStatisticsEntryByName("_d");
		assertEquals(2, complexTypeDStatisticsEntry.getInputDocumentsCount());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeDStatisticsEntry.getElementInfo();
		assertEquals(1, elementInfo.size());
		// _d-e
		BasicStatisticsEntry elementCEntry = elementInfo.get(elementDE);
		assertEquals(0.5,elementCEntry.getAverage(),0.001);
		assertEquals(0.25,elementCEntry.getVariance(),0.001);
		assertEquals(1,elementCEntry.getConditionedAverage(),0.001);
		assertEquals(0,elementCEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),elementCEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,elementCEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,elementCEntry.getMin());
		assertEquals(1,elementCEntry.getTotal());
		assertEquals(0.5,elementCEntry.getNonZeroRatio(),0.001);
		assertEquals(1,elementCEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,elementCEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeDStatisticsEntry.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfo.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeDStatisticsEntry.getValuesInfo();
		assertTrue(valuesInfo.isEmpty()||valuesEmptyOrOnlyConsistOfWhitespaceCharacters(valuesInfo.rowKeySet()));
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeDStatisticsEntry.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodes.isEmpty());
		
	}
	
	/**
	 * This method checks that the statistics of complex type _e have been properly extracted on the second scenario
	 */
	@Test
	public void testOnScenario2ComplexTypeEStatisticsEntry(){
		Schema schema = typesExtractor2.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		ComplexTypeStatisticsEntry complexTypeEStatisticsEntry = statistics.getComplexTypeStatisticsEntryByName("_e");
		assertEquals(2, complexTypeEStatisticsEntry.getInputDocumentsCount());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeEStatisticsEntry.getElementInfo();
		assertTrue(elementInfo.isEmpty());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeEStatisticsEntry.getAttributeOccurrencesInfo();
		assertTrue(attributeOccurrencesInfo.isEmpty());
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeEStatisticsEntry.getValuesInfo();
		assertTrue(valuesInfo.isEmpty()||valuesEmptyOrOnlyConsistOfWhitespaceCharacters(valuesInfo.rowKeySet()));
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeEStatisticsEntry.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodes.isEmpty());
		
	}
	
	//Third scenario
	
	/**
	 * Test method that checks that all the SchemElement have been created properly on scenario 3.
	 */
	@Test
	public void testOnScenario3SchemaElements(){
		Schema schema = typesExtractor3.getInitalSchema();
		Table<String,String,SchemaElement> elements =  schema.getElements();
		assertEquals(1,elements.size());
		
		SchemaElement nodoRaiz=elements.get("", "nodoRaiz");
		assertNotNull(nodoRaiz);
		assertEquals("nodoRaiz",nodoRaiz.getName());
		assertEquals("",nodoRaiz.getNamespace());
		assertEquals("_nodoRaiz",nodoRaiz.getType().getName());
		assertTrue(nodoRaiz.isValidRoot());
	}
	
	/**
	 * This method checks that attributes have been extracted properly on scenario 3
	 */
	@Test
	public void testOnScenario3SchemaAttributes(){
		Schema schema = typesExtractor3.getInitalSchema();
		Table <String,String,SchemaAttribute> attributes = schema.getAttributes();
		assertEquals(1, attributes.size());
		
		SchemaAttribute attrAttr=attributes.get("","_nodoRaiz-attr");
		assertNotNull(attrAttr);
		assertEquals("attr",attrAttr.getName());
		assertEquals("",attrAttr.getNamespace());
		assertTrue(attrAttr.isOptional());
		assertEquals("xs:string",attrAttr.getSimpleType().getBuiltinType());
		assertFalse(attrAttr.getSimpleType().isEnum());
		assertEquals(1,attrAttr.getSimpleType().enumerationCount());
		assertTrue(attrAttr.getSimpleType().enumerationContains("value"));
		
	}
	
	/**
	 * Test that checks that the namespace-prefix mappings are correctly generated on the third scenario
	 */
	@Test
	public void testOnScenario3NamespacePrefixMappings(){
		Schema schema = typesExtractor3.getInitalSchema();
		Map<String, SortedSet<String>> prefixNamespaceMapping = schema.getNamespacesToPossiblePrefixMappingModifiable();
		assertEquals(2,prefixNamespaceMapping.size());
		
		Set<String> prefixesOfEmptyNamespace = prefixNamespaceMapping.get("");
		assertNotNull(prefixesOfEmptyNamespace);
		assertEquals(1,prefixesOfEmptyNamespace.size());
		assertTrue(prefixesOfEmptyNamespace.contains(""));
	}
	
	/**
	 * This method checks that the prefix-namespace mappings have been made properly on the third scenario.
	 */
	@Test
	public void testOnScenario3SolvedMappings(){
		Schema schema = typesExtractor3.getInitalSchema();
		NavigableMap<String, String> solvedMappingsBetweenNamespaceURIsAndPrefixes = schema.getSolvedNamespaceMappings();
		assertEquals(solvedNamespaceToPrefixMappingNoNS,solvedMappingsBetweenNamespaceURIsAndPrefixes);
	}
	
	/**
	 * This method checks that there are the expected number of complex types on the scenario 2, which should be equal 
	 * to the actual number of elements.
	 */
	@Test
	public void testOnScenario3ComplexTypesCount(){
		Schema schema = typesExtractor3.getInitalSchema();
		int complexTypesCount = schema.getComplexTypes().size();
		assertEquals(1, complexTypesCount);
		assertEquals("The name of elements and complex types does not match",ImmutableSet.copyOf(schema.getElements().values()).size(),complexTypesCount);
	}
	

	/**
	 * This test checks that the complex type _nodoRaiz is correctly generated on the third scenario.
	 */
	@Test
	public void testOnScenario3ComplexTypeNodoRaiz() {
		Schema schema = typesExtractor3.getInitalSchema();
		
		ComplexType complexTypeNodoRaiz=schema.getComplexTypes().get("_nodoRaiz");
		assertEquals("_nodoRaiz",complexTypeNodoRaiz.getName());
		
		//Source element
		
		Set<String> sourceKeys = complexTypeNodoRaiz.getSourceElementNamespacesAndNames();
		assertEquals(1,sourceKeys.size());
		assertTrue(sourceKeys.contains(":nodoRaiz"));
		
		//Automaton
		
		ExtendedAutomaton complexTypeNodoRaizAutomaton=complexTypeNodoRaiz.getAutomaton();
		assertTrue(complexTypeNodoRaizAutomaton.containsAllNodes(ImmutableSet.of(initialState,finalState)));
		assertEquals(2, complexTypeNodoRaizAutomaton.nodeCount());
		assertEquals(2,complexTypeNodoRaizAutomaton.getEdgeWeight(initialState, finalState));		
		
		//Attribute list
		
		List<SchemaAttribute> complexTypeNodoRaizAttrList = complexTypeNodoRaiz.getAttributeList();
		assertEquals(1,complexTypeNodoRaizAttrList.size());
		assertTrue(complexTypeNodoRaizAttrList.contains(schema.getAttributes().get("","_nodoRaiz-attr")));
		
		//Text SimpleType
		
		SimpleType textSimpleType = complexTypeNodoRaiz.getTextSimpleType();
		assertTrue(textSimpleType.isEmpty());
	}

	/**
	 * This method checks that the general statistics of the third scenario are correctly generated.
	 */
	@Test
	public void testOnScenario3GeneralStatistics(){
		Schema schema = typesExtractor3.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		//Input documents count
		assertEquals(2, statistics.getInputDocumentsCount());
		
		//Depth
		assertEquals(1,statistics.getMaxDepth());
		assertEquals(1,statistics.getAvgDepth(), 0.001);
		
		//Width
		assertEquals(1,statistics.getMaxWidth());
		assertEquals(1,statistics.getAvgWidth(),0.001);
	}
	
	/**
	 * This method checks that the root elements occurrences info is correctly generated on the third scenario.
	 */
	@Test
	public void testOnScenario3RootElementsOccurrences(){
		Schema schema = typesExtractor3.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Map<SchemaElement, Integer> nodoRaizElementOccurrences = statistics.getRootElementOccurrences();
		assertEquals(1,nodoRaizElementOccurrences.keySet().size());
		SchemaElement elementRoot = schema.getElements().get("", "nodoRaiz");
		assertTrue(nodoRaizElementOccurrences.containsKey(elementRoot));
		assertEquals(2,nodoRaizElementOccurrences.get(elementRoot).intValue());
	}
	
	/**
	 * This method checks that the elements at path information of the second scenario is well generated.
	 */
	@Test
	public void testOnScenario3ElementsAtPathStatistics(){
		Schema schema = typesExtractor3.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		Map<String, BasicStatisticsEntry> elementAtPathInfo = statistics.getElementAtPathInfo();
		assertEquals(1,elementAtPathInfo.size());
		
		// /nodoRaiz
		BasicStatisticsEntry nodoRaizEntry = elementAtPathInfo.get("/nodoRaiz");
		assertEquals(1,nodoRaizEntry.getAverage(),0.001);
		assertEquals(0,nodoRaizEntry.getVariance(),0.001);
		assertEquals(1,nodoRaizEntry.getConditionedAverage(),0.001);
		assertEquals(0,nodoRaizEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),nodoRaizEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_2,nodoRaizEntry.getMax());
		assertEquals(VALUE_1_FREQUENCY_2,nodoRaizEntry.getMin());
		assertEquals(2,nodoRaizEntry.getTotal());
		assertEquals(1,nodoRaizEntry.getNonZeroRatio(),0.001);
		assertEquals(0,nodoRaizEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,nodoRaizEntry.getConditionedStandardDeviationAverageRatio(),0.001);
	}
	
	/**
	 * This method checks that the attributes at path information of the third scenario is well generated.
	 */
	@Test
	public void testOnScenario3AttributesAtPathInfo(){
		Schema schema = typesExtractor3.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		Map<String, BasicStatisticsEntry> attributeAtPathInfo = statistics.getAttributeAtPathInfo();
		assertEquals(1, attributeAtPathInfo.size());
		
		// /nodoRaiz/@attr
		BasicStatisticsEntry attrEntry = attributeAtPathInfo.get("/nodoRaiz/@attr");
		assertEquals(0.5,attrEntry.getAverage(),0.001);
		assertEquals(0.25,attrEntry.getVariance(),0.001);
		assertEquals(1,attrEntry.getConditionedAverage(),0.001);
		assertEquals(0,attrEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attrEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attrEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attrEntry.getMin());
		assertEquals(1,attrEntry.getTotal());
		assertEquals(0.5,attrEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attrEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attrEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
	}
	
	/**
	 * This method checks that the values at path information of the third scenario is well generated.
	 */
	@Test
	public void testOnScenario3ValuesAtPathInfo(){
		Schema schema = typesExtractor3.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		
		Table<String, String, BasicStatisticsEntry> valuesAtPathInfo = statistics.getValuesAtPathInfo();
				
		// /nodoRaiz/@attr
		Map<String,BasicStatisticsEntry> attrValues = valuesAtPathInfo.row("/nodoRaiz/@attr");
		assertFalse(attrValues.isEmpty());
		BasicStatisticsEntry attrValueValueEntry = attrValues.get("value");
		assertNotNull(attrValueValueEntry);
		assertEquals(0.5,attrValueValueEntry.getAverage(),0.001);
		assertEquals(0.25,attrValueValueEntry.getVariance(),0.001);
		assertEquals(1,attrValueValueEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,attrValueValueEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attrValueValueEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attrValueValueEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attrValueValueEntry.getMin());
		assertEquals(1,attrValueValueEntry.getTotal());
		assertEquals(0.5,attrValueValueEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attrValueValueEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attrValueValueEntry.getConditionedStandardDeviationAverageRatio(),0.001);
	}
	
	/**
	 * This method checks that the statistics of numeric values at path of the third scenario are well generated.
	 */
	@Test
	public void testOnScenario3StatisticsOfNumericValuesAtPath(){
		Schema schema = typesExtractor3.getInitalSchema();
		Statistics statistics = schema.getStatistics();
		Map<String, BasicStatisticsEntry> statisticsOfNumericValuesAtPath = statistics.getStatisticsOfNumericValuesAtPath();
		assertTrue(statisticsOfNumericValuesAtPath.isEmpty());
	}
	
	/**
	 * This method checks that the statistic information of the complex type _nodoRaiz from the third scenario is well generated.
	 */
	@Test
	public void testOnScenario3ComplexTypeNodoRaizStatisticsEntry(){
		Schema schema = typesExtractor3.getInitalSchema();
		Statistics statistics = schema.getStatistics();
	
		Table <String,String,SchemaAttribute> attributes = schema.getAttributes();
		SchemaAttribute attributeAttr=attributes.get("","_nodoRaiz-attr");
		
		ComplexTypeStatisticsEntry complexTypeNodoRaizStatisticsEntry = statistics.getComplexTypeStatisticsEntryByName("_nodoRaiz");
		assertEquals(2, complexTypeNodoRaizStatisticsEntry.getInputDocumentsCount());
		
		Map<SchemaElement, BasicStatisticsEntry> elementInfo = complexTypeNodoRaizStatisticsEntry.getElementInfo();
		assertTrue(elementInfo.isEmpty());
		
		Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo = complexTypeNodoRaizStatisticsEntry.getAttributeOccurrencesInfo();
		// _nodoRaiz-@attr
		BasicStatisticsEntry attrEntry = attributeOccurrencesInfo.get(attributeAttr);
		assertEquals(0.5,attrEntry.getAverage(),0.001);
		assertEquals(0.25,attrEntry.getVariance(),0.001);
		assertEquals(1,attrEntry.getConditionedAverage(),0.001);
		assertEquals(0,attrEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attrEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attrEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attrEntry.getMin());
		assertEquals(1,attrEntry.getTotal());
		assertEquals(0.5,attrEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attrEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attrEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Table<String, SchemaNode, BasicStatisticsEntry> valuesInfo = complexTypeNodoRaizStatisticsEntry.getValuesInfo();
		// _nodoRaiz-@attr
		Map<String,BasicStatisticsEntry> attributeAttrValues = valuesInfo.column(attributeAttr);
		assertFalse(attributeAttrValues.isEmpty());
		BasicStatisticsEntry attrValueValueEntry = attributeAttrValues.get("value");
		assertNotNull(attrValueValueEntry);
		assertEquals(0.5,attrValueValueEntry.getAverage(),0.001);
		assertEquals(0.25,attrValueValueEntry.getVariance(),0.001);
		assertEquals(1,attrValueValueEntry.getConditionedAverage(),0.001);
		assertEquals(0.0,attrValueValueEntry.getConditionedVariance(),0.001);
		assertEquals(ImmutableSet.of(),attrValueValueEntry.getMode());
		assertEquals(VALUE_1_FREQUENCY_1,attrValueValueEntry.getMax());
		assertEquals(VALUE_0_FREQUENCY_1,attrValueValueEntry.getMin());
		assertEquals(1,attrValueValueEntry.getTotal());
		assertEquals(0.5,attrValueValueEntry.getNonZeroRatio(),0.001);
		assertEquals(1,attrValueValueEntry.getStandardDeviationAverageRatio(),0.001);
		assertEquals(0,attrValueValueEntry.getConditionedStandardDeviationAverageRatio(),0.001);
		
		Map<SchemaNode, BasicStatisticsEntry> statisticsOfNumericValuesOfNodes = complexTypeNodoRaizStatisticsEntry.getStatisticsOfNumericValuesOfNodes();
		assertTrue(statisticsOfNumericValuesOfNodes.isEmpty());
	}
	
}

