package es.upm.dit.xsdinferencer.extraction.extractorImpl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static es.upm.dit.xsdinferencer.XSDInferenceConfiguration.XSI_NAMESPACE_URI;
import static es.upm.dit.xsdinferencer.datastructures.Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;

import org.jdom2.Attribute;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.extraction.AttributeListInferencer;
import es.upm.dit.xsdinferencer.extraction.SimpleTypeInferencer;
import es.upm.dit.xsdinferencer.extraction.TypeNameInferencer;
import es.upm.dit.xsdinferencer.extraction.TypesExtractor;
import es.upm.dit.xsdinferencer.statistics.ComplexTypeStatisticsEntry;
import es.upm.dit.xsdinferencer.statistics.Statistics;
/**
 * Main implementation for {@link TypesExtractor}.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class TypesExtractorImpl implements TypesExtractor {
	
	/**
	 * Input XML Documents
	 */
	private List<Document> xmlDocuments;
	
	/**
	 * Current inference configuration
	 */
	private XSDInferenceConfiguration configuration;
	
	/**
	 * Unsolved mapping between any found namespace URI and all the prefixes 
	 * which have ever been bounded to it
	 */
	private NavigableMap<String,SortedSet<String>> prefixNamespaceMapping;
	
	/**
	 * Solved mapping between all the known namespace URIs and prefixes, which will 
	 * be used at the generated XSDs.
	 */
	private NavigableMap<String,String> solvedNamespaceToPrefixMapping;
	
	/**
	 * Mapping between complex type names and ComplexType objects.
	 */
	private Map<String,ComplexType> complexTypes;
	
	/**
	 * Mapping between complex type names and the SimpleTypeInferencer of the text simple types
	 */
	private Map<String,SimpleTypeInferencer> simpleTypeInferencersOfComplexTypes; //Only simpleTypes of texts
	
	/**
	 * Mapping between complex type names and the AttributeListInferencer objects which will infer their attribute lists 
	 */
	private Map<String,AttributeListInferencer> attributeListInferencers;
	
	/**
	 * Mapping between complex type names and their automatons
	 */
	private Map<String,ExtendedAutomaton> automatons;

	/**
	 * Elements table of the extracted Schema, it will be properly filled during extraction.
	 */
	private Table<String,String,SchemaElement> elements;

	/**
	 * Attributes table of the extracted Schema, it will be properly filled during extraction.
	 */
	private Table<String,String,SchemaAttribute> attributes;
	
	/**
	 * Map between simple type names and SimpleType objects which will be used at the extracted Schema
	 */
	private Map<String,SimpleType> simpleTypes;
	
	/**
	 * Statistics object which will be used at the generated Schema
	 */
	private Statistics statistics;
	
	/**
	 * InferencersFactory to be used
	 */
	private InferencersFactory inferencersFactory = InferencersFactory.getInstance();
	
	/**
	 * Returns a path of the element made of the name of the elements and their prefixes (or namespace URIs). 
	 * Prefixes (or URIs) are separated from element names by :, so THEY MUST BE REPLACED BY _ if they are 
	 * going to be used to build type names.
	 * Note that the : WILL always appear, although there is not any namespace prefix.
	 * If a solved namespace-prefix mapping is given, the prefix mapped to the namespace of the element will be used instead of the prefix of the element. 
	 * @param element the element
	 * @param config current inference configuration
	 * @param useURI if true, the URI is used to build the path, if false, the prefix is used
	 * @param solvedNamespaceToPrefixMapping the solved mappings between the namespace URIs and prefixes
	 * @return a list that represents the path. Each element of the list is a path element.
	 * @throws NullPointerException if any argument is null
	 */
	public static List<String> getRealPathOfElementUnfiltered(Element element, XSDInferenceConfiguration config, boolean useURI, Map<String, String> solvedNamespaceToPrefixMapping){
		checkNotNull(element,"'element' must not be null");
		checkNotNull(config,"'config' must not be null");
		LinkedList<String> path = new LinkedList<String>();
		Element currentElement=element;
		do{
			String e;
			if(useURI){
				e = currentElement.getNamespaceURI()+":"+currentElement.getName();
			} else {
				String effectivePrefix=solvedNamespaceToPrefixMapping!=null?solvedNamespaceToPrefixMapping.get(currentElement.getNamespaceURI()):currentElement.getNamespacePrefix();
				e = effectivePrefix+":"+currentElement.getName();
			}
			path.addFirst(e);
		}while((currentElement=currentElement.getParentElement())!=null);
		return path;
	}
	
	/**
	 * Returns a path of the attribute made of the name of the elements and their prefixes. 
	 * Prefixes are separated from element names by :, so THEY MUST BE REPLACED BY _ if they are going to be used to build type names.
	 * Note that the : WILL always appear, although there is not any namespace prefix.
	 * @param attribute the attribute
	 * @param config current inference configuration
	 * @param solvedNamespaceToPrefixMapping the solved mappings between the namespace URIs and prefix
	 * @return a list that represents the path
	 * @throws NullPointerException if any argument is null
	 */
	public static List<String> getRealPathOfAttributeUnfiltered(Attribute attribute, XSDInferenceConfiguration config, Map<String, String> solvedNamespaceToPrefixMapping){
		checkNotNull(attribute,"'attribute' must not be null");
		checkNotNull(config,"'config' must not be null");
		List<String> path = getRealPathOfElementUnfiltered(attribute.getParent(), config, false, solvedNamespaceToPrefixMapping);
		path.add("@"+attribute.getNamespacePrefix()+":"+attribute.getName());
		return path;
	}
	
	/**
	 * Removes the : of any token of a path built via {@link TypesExtractorImpl#getRealPathOfElementUnfiltered(Element, XSDInferenceConfiguration, boolean, Map)} 
	 * or {@link TypesExtractorImpl#getRealPathOfAttributeUnfiltered(Attribute, XSDInferenceConfiguration, Map)} which does not have a namespace prefix
	 * @param unfilteredPath the unfiltered path
	 * @return the same path, but filtered
	 */
	public static String filterAndJoinRealPath(List<String> unfilteredPath){
		checkNotNull(unfilteredPath,"'unfilteredPath' must not be null");
		checkArgument(!unfilteredPath.isEmpty(), "The path must not be empty");
		StringBuilder resultBuilder=new StringBuilder();
		for(int i=0;i<unfilteredPath.size();i++){
			String value = unfilteredPath.get(i);
			if(value.startsWith(":")||value.startsWith("@:")){
				value=value.replaceAll(Matcher.quoteReplacement(":"), "");
			}
			resultBuilder.append("/");
			resultBuilder.append(value);
		}
		return resultBuilder.toString();
	}

	/**
	 * Transforms a path created via {@link TypesExtractorImpl#getRealPathOfElementUnfiltered(Content, XSDInferenceConfiguration, boolean, Map)} 
	 * to a path whose tokens can fit into a NCName field (for more information on what an NCName is, look at the XML RFC).
	 * This implementation does it by replacing all the ':' characters by '_'.
	 * @param realPath the path
	 * @return a transformed path that fits into an NCName
	 */
	public static List<String> getSuitablePath(List<String> realPath){
		checkNotNull(realPath,"'realPath' must not be null");
		List<String> result = new ArrayList<String>(realPath);
		for(int i=0;i<result.size();i++){
			result.set(i,result.get(i).replaceAll(":","_"));
		}
		return result;
	}
	
	/**
	 * Default constructor.
	 * @param xmlDocuments A list of all the input XML Documents, as JDOM2 {@link Document} objects.
	 * @param configuration the inference configuration
	 */
	public TypesExtractorImpl(List<Document> xmlDocuments,
			XSDInferenceConfiguration configuration) {
		this(xmlDocuments,configuration,InferencersFactory.getInstance()); 		
	}
	
	/**
	 * Default constructor with custom inferencers factory.
	 * @param xmlDocuments A list of all the input XML Documents, as JDOM2 {@link Document} objects.
	 * @param configuration the inference configuration
	 */
	public TypesExtractorImpl(List<Document> xmlDocuments,
			XSDInferenceConfiguration configuration, InferencersFactory inferencersFactory) {
		checkNotNull(xmlDocuments,"'xmlDocuments' must not be null");
		checkNotNull(configuration,"'configuration' must not be null");
		this.xmlDocuments = xmlDocuments;
		this.configuration = configuration;
		this.simpleTypeInferencersOfComplexTypes=new HashMap<String, SimpleTypeInferencer>();
		this.attributeListInferencers=new HashMap<String, AttributeListInferencer>();
		this.automatons=new HashMap<String, ExtendedAutomaton>();
		this.statistics = new Statistics(xmlDocuments.size());
		this.elements=HashBasedTable.create();
		this.complexTypes=new HashMap<>();
		this.simpleTypes=new HashMap<String, SimpleType>();
		this.elements=HashBasedTable.create();
		this.attributes=HashBasedTable.create();
		this.prefixNamespaceMapping=new TreeMap<String, SortedSet<String>>();
		this.inferencersFactory=inferencersFactory;
	}
	
//	
//	public TypesExtractorImpl(List<Document> xmlDocuments,
//			XSDInferenceConfiguration configuration, SimpleTypeInferencer referenceSimpleTypeInferencer,
//			AttributeListInferencer referenceAttributeListInferencer){
//		this(xmlDocuments, configuration);
//		this.referenceAttributeListInferencer=referenceAttributeListInferencer;
//		this.referenceSimpleTypeInferencer=referenceSimpleTypeInferencer;
//		
//	}
	
	/**
	 * Method that clears all the data structures prior to the extraction of an initial schema. 
	 */
	private void clearAll(){
		this.simpleTypeInferencersOfComplexTypes.clear();
		this.attributeListInferencers.clear();
		this.automatons.clear();
		this.statistics = new Statistics(xmlDocuments.size());
		
		this.simpleTypes.clear();
		this.complexTypes.clear();
		this.attributes.clear();
		this.elements.clear();
		this.prefixNamespaceMapping.clear();
		this.automatons.clear();
		this.solvedNamespaceToPrefixMapping=null;
	}
	
	/**
	 * This method traverses all the elements of each input document in order to find all the namespace URI to prefix mappings present at the documents.
	 * With that information, the map between namespace URIs and their known prefixes is filled. It is used later to solve which prefix should be bound 
	 * to each namespace URI.
	 */
	private void fillKnownNamespaceToPrefixMappings(){
		Filter<Element> elementFilter = Filters.element();
		for(int i=0;i<xmlDocuments.size();i++){
			for(Element element:xmlDocuments.get(i).getDescendants(elementFilter)){
				for(Namespace namespace:element.getNamespacesInScope()){
					//We do not add XSI to the known namespaces 
					if(namespace.getURI().equalsIgnoreCase(XSI_NAMESPACE_URI))
						continue;
					String uri=namespace.getURI();
					String prefix=namespace.getPrefix();
					SortedSet<String> currentPrefixes = prefixNamespaceMapping.get(uri);
					if(currentPrefixes==null){
						currentPrefixes=new TreeSet<String>();
						prefixNamespaceMapping.put(uri, currentPrefixes);
					}
					currentPrefixes.add(prefix);
				}
				//If the element belongs to the empty namespace (empty string) with no prefix, we must add 
				//this to the prefix-namespace mapping explicitly
				if(element.getNamespacePrefix().equals("") && element.getNamespaceURI().equals("")){
					String uri="";
					String prefix="";
					SortedSet<String> currentPrefixes = prefixNamespaceMapping.get(uri);
					if(currentPrefixes==null){
						currentPrefixes=new TreeSet<>();
						prefixNamespaceMapping.put(uri, currentPrefixes);
					}
					currentPrefixes.add(prefix);
				}
			}
		}
	}
	
	/**
	 * @see TypesExtractor#getInitalSchema()
	 */
	@Override
	public Schema getInitalSchema() {
		clearAll();
		fillKnownNamespaceToPrefixMappings();
		solvedNamespaceToPrefixMapping=Schema.solveMappings(prefixNamespaceMapping);
		for(int i=0;i<xmlDocuments.size();i++){
			traverseElement(i,xmlDocuments.get(i).getRootElement(), "");
		}
		fillSchemaStructures();
		return new Schema(new TreeMap<>(prefixNamespaceMapping),elements,attributes,complexTypes,simpleTypes,statistics);
	}
	
	/**
	 * Recursive method that traverses an element to extract all the possible information from it.
	 * It is recursive because it calls itself for each child of the element (obviously, infinite recursion 
	 * is not possible as there are not, or there should not be, parent-child loops).  
	 * The index of the current document is necessary in order to add well some information to 
	 * the statistics.
	 * @param documentIndex index of current document
	 * @param element the element to traverse (as a JDOM2 {@link Element})
	 * @param enclosingComplexType the complex type which will contain the current element
	 */
	private void traverseElement(int documentIndex, Element element, String enclosingComplexType){
		//Elements in the XSI namespace should be ignored
		if(element.getNamespaceURI().equalsIgnoreCase(XSI_NAMESPACE_URI))
			return;
		List<String> realPathUnfiltered=getRealPathOfElementUnfiltered(element, configuration, false, solvedNamespaceToPrefixMapping);
		String realPathFiltered=filterAndJoinRealPath(realPathUnfiltered);//Path for the statistics
		List<String> typePathUnfiltered=getRealPathOfElementUnfiltered(element, configuration, false, solvedNamespaceToPrefixMapping);
		List<String> suitablePath=getSuitablePath(typePathUnfiltered);//Path for type name inferencing
		//First, we will register the information of width and depth
		//The root is in a level whose width is 1, if we did not do the following, that width would be never registered
		if(element.isRootElement()){
			statistics.registerWidth(documentIndex, 1);
		}
		statistics.registerDepth(documentIndex, realPathUnfiltered.size());
		int width = element.getChildren().size();
		if(width>0){
			statistics.registerWidth(documentIndex, width);
		}
		TypeNameInferencer typeNameInferencer = configuration.getTypeNameInferencer();
		String complexTypeName = typeNameInferencer.inferTypeName(suitablePath, configuration);//Complex type of this element
//		//Little workaround that ensures that the same complex type is used 
//		//when the elements on its path are the same (same name and namespace) but some of them 
//		//use different namespace prefixes
//		List<String> realPathUnfilteredKey=getRealPathOfElementUnfiltered(element, configuration, false, solvedNamespaceToPrefixMapping);
//		List<String> suitablePathKey=getSuitablePath(realPathUnfilteredKey);//Path for type name inferencing
//		String complexTypeNameKey = typeNameInferencer.inferTypeName(suitablePathKey, configuration);//Complex type of this element
		String complexTypeNameKey = complexTypeName;
		//The complex type object of this element.
		ComplexType complexType = complexTypes.get(complexTypeNameKey);
		if(complexType==null){
			complexType = new ComplexType(complexTypeName, null, null, null);
			complexTypes.put(complexTypeNameKey, complexType); //New complex type
		}
		complexType.addSourceNodeNamespaceAndName(element.getNamespaceURI(), element.getName());
		//Comment processing
		for(Comment comment: element.getDescendants(Filters.comment())){
			if(comment.getParentElement().equals(element))
				complexType.getComments().add(comment.getText());
		}
		
		//Key to find the corresponding SchemaElement
		//This key is: if the SchemaElement has an enclosing complex type (i.e., it is not a valid root), its name will be:
		//enclosingComplexType+typeNamesSeparator+elementName
		//If the element is a suitable root, the key is the name of the element.
		String schemaElementKey=(!enclosingComplexType.equals(""))?enclosingComplexType+configuration.getTypeNamesAncestorsSeparator()+element.getName():element.getName();
		SchemaElement schemaElement = elements.get(element.getNamespaceURI(), schemaElementKey);
		if(schemaElement==null){
			schemaElement=new SchemaElement(element.getName(), element.getNamespaceURI(), complexType);//Complex type already not known.
			elements.put(element.getNamespaceURI(), schemaElementKey, schemaElement);
		}
		boolean wasAlreadyValidRoot=schemaElement.isValidRoot();
		schemaElement.setValidRoot(wasAlreadyValidRoot||element.isRootElement());
		
		ComplexTypeStatisticsEntry complexTypeStatisticsEntry = statistics.getComplexTypeInfo().get(complexType);
		if(complexTypeStatisticsEntry==null){
			complexTypeStatisticsEntry=new ComplexTypeStatisticsEntry(xmlDocuments.size());
			statistics.getComplexTypeInfo().put(complexType, complexTypeStatisticsEntry);
		}
		
		AttributeListInferencer attributeListInferencer = attributeListInferencers.get(complexTypeName);
		
		if(attributeListInferencer==null){
			attributeListInferencer=inferencersFactory.getAttributeListInferencerInstance(complexTypeName, configuration, solvedNamespaceToPrefixMapping, statistics);
			attributeListInferencers.put(complexTypeName, attributeListInferencer);
		}
		attributeListInferencer.learnAttributeList(element.getAttributes(), documentIndex);
		
		SimpleTypeInferencer simpleTypeInferencer = simpleTypeInferencersOfComplexTypes.get(complexTypeName);
		if(simpleTypeInferencer==null){
			simpleTypeInferencer=inferencersFactory.getSimpleTypeInferencerInstance(complexTypeName, configuration);
			simpleTypeInferencersOfComplexTypes.put(complexTypeName, simpleTypeInferencer);
		}
		simpleTypeInferencer.learnValue(element.getText(), element.getNamespaceURI(), element.getName());
		
		
//		SchemaElement previousChildSchemaElement=null; //We need to store the previous child in order to add the edge between it and the current child.
		List<SchemaElement> schemaElementChildren = new ArrayList<>(element.getChildren().size());
		for(int i=0;i<element.getChildren().size();i++){
			Element child = element.getChildren().get(i);
			traverseElement(documentIndex, child, complexTypeName);
			String childSchemaElementKey=complexTypeName+configuration.getTypeNamesAncestorsSeparator()+child.getName();
			SchemaElement childSchemaElement=elements.get(child.getNamespaceURI(), childSchemaElementKey);//The SchemaElement object does exist because the method traverseElement is called before this.
//			if(i==0){
//				automaton.addEdge(automaton.getInitialState(), childSchemaElement);
//			}
//			else {
//				automaton.addEdge(previousChildSchemaElement, childSchemaElement);
//				if(i==(element.getChildren().size()-1)){
//					automaton.addEdge(childSchemaElement, automaton.getFinalState());
//				}
//			}
			complexTypeStatisticsEntry.registerElementCount(childSchemaElement, documentIndex);
			schemaElementChildren.add(childSchemaElement);
//			previousChildSchemaElement=childSchemaElement;
		}
		
		ExtendedAutomaton automaton = automatons.get(complexTypeName);
		if(automaton==null){
			automaton = new ExtendedAutomaton();
			SchemaElement initialState = new SchemaElement("initial", DEFAULT_PSEUDOELEMENTS_NAMESPACE, null);
			automaton.setInitialState(initialState);
			SchemaElement finalState = new SchemaElement("final", DEFAULT_PSEUDOELEMENTS_NAMESPACE, null);
			automaton.setFinalState(finalState);
			automatons.put(complexTypeName, automaton);
		}
		
		List<SchemaElement> schemaElementChildrenWithInitialAndFinal = new ArrayList<>(schemaElementChildren);
		schemaElementChildrenWithInitialAndFinal.add(0, automaton.getInitialState());
		schemaElementChildrenWithInitialAndFinal.add(automaton.getFinalState());
		automaton.learn(schemaElementChildrenWithInitialAndFinal);
		
		complexTypeStatisticsEntry.registerSubpatternsFromList(schemaElementChildren);
		complexTypeStatisticsEntry.registerValueOfNodeCount(element.getText(), schemaElement, documentIndex);
		
		statistics.registerElementAtPathCount(realPathFiltered, documentIndex);
		statistics.registerValueAtPathCount(realPathFiltered, element.getText(), documentIndex);
		if(enclosingComplexType.equals("")){
			statistics.registerRootElementOccurrence(schemaElement);
		}
	}
	
	/**
	 * This method fills all the structures of the future schema which are not already filled with all the 
	 * information gathered. 
	 */
	private void fillSchemaStructures(){
		for(String complexTypeName: complexTypes.keySet()){
			ComplexType complexType=complexTypes.get(complexTypeName);
			SimpleTypeInferencer simpleTypeInferencer=simpleTypeInferencersOfComplexTypes.get(complexTypeName);
			AttributeListInferencer attributeListInferencer = attributeListInferencers.get(complexTypeName);
			ExtendedAutomaton automaton = automatons.get(complexTypeName);
			List<SchemaAttribute> attributesList = attributeListInferencer.getAttributesList();
			complexType.setAttributeList(attributesList);
			for(SchemaAttribute schemaAttribute:attributesList){
				attributes.put(schemaAttribute.getNamespace(), 
						complexTypeName+configuration.getTypeNamesAncestorsSeparator()+schemaAttribute.getName(), 
						schemaAttribute);
				SimpleType attrSimpleType=schemaAttribute.getSimpleType();
				simpleTypes.put(attrSimpleType.getName(), attrSimpleType);
			}
			SimpleType simpleType = simpleTypeInferencer.getSimpleType(complexTypeName);
			simpleTypes.put(complexTypeName, simpleType);
			complexType.setTextSimpleType(simpleType);
			complexType.setAutomaton(automaton);
		}
	}

	/**
	 * @return the solvedNamespaceToPrefixMapping
	 */
	public NavigableMap<String, String> getSolvedNamespaceToPrefixMapping() {
		return solvedNamespaceToPrefixMapping;
	}
	

}
