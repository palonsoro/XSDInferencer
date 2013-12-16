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
package es.upm.dit.xsdinferencer.generation.generatorimpl.statisticsgeneration;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

import com.google.common.collect.Table;

import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.SchemaNode;
import es.upm.dit.xsdinferencer.generation.StatisticResultsDocGenerator;
import es.upm.dit.xsdinferencer.statistics.BasicStatisticsEntry;
import es.upm.dit.xsdinferencer.statistics.ComplexTypeStatisticsEntry;
import es.upm.dit.xsdinferencer.statistics.Statistics;
import es.upm.dit.xsdinferencer.statistics.ValueAndFrequency;

/**
 * Current implementation of {@link StatisticResultsDocGenerator}.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 * @see StatisticResultsDocGenerator
 */
class StatisticResultsDocGeneratorImpl implements StatisticResultsDocGenerator {

	/**
	 * Namespace URI of the statistics document
	 */
	public static final String STATISTICS_NAMESPACE_URI = "http://www.dit.upm.es/xsdinferencer/statistics";

	/**
	 * JDOM2 Namespace of the statistics document
	 */
	protected static final Namespace STATISTICS_NAMESPACE = Namespace.getNamespace(STATISTICS_NAMESPACE_URI);
	
	/**
	 * Element {@link Comparator} used to compare elements that come from BasicStatisticsEntry objects, 
	 * so that they can be sorted. 
	 * The comparing method is the following: <br/>
	 * <ul>
	 * <li>If both have an attribute called 'path' and it has not the same value, the return value is the string comparison of both strings</li>
	 * <li>If not, then, if both have an attribute called 'name' and it has not the same value, the return value is the string comparison of both strings</li>
	 * <li>If not, then, if both have an attribute called 'namespace' and it has not the same value, the return value is the string comparison of both strings</li>
	 * <li>If not, then, if both have an attribute called 'value' and it has not the same value, the return value is the string comparison of both strings</li>
	 * </ul>
	 */
	public static final Comparator<Element> BASIC_ENTRY_ELEMENT_COMPARATOR = new Comparator<Element>(){

		@Override
		public int compare(Element o1, Element o2) {
			String path1 = o1.getAttributeValue("path");
			String path2 = o2.getAttributeValue("path");
			String name1 = o1.getAttributeValue("name");
			String name2 = o2.getAttributeValue("name");
			String namespace1 = o1.getAttributeValue("namespace");
			String namespace2 = o2.getAttributeValue("namespace");
			String value1 = o1.getAttributeValue("value");
			String value2 = o2.getAttributeValue("value");
			if((path1!=null)&&(!path1.equals(path2))){
				return path1.compareTo(path2);
			}
			else if(!name1.equals(name2)&&!(name1==null)){
				return name1.compareTo(name2);
			}
			else if(!namespace1.equals(namespace2)&&!(namespace1==null)){
				return namespace1.compareTo(namespace2);
			}
			else if(!value1.equals(value2)&&!(value1==null)){
				return value1.compareTo(value2);
			}
			return 0;
		}
		
	};

	/**
	 * Used to round numbers. It uses English locale to choose the decimal separator.
	 */
	private DecimalFormat roundingFormat;
	
	/**
	 * Default constructor
	 */
	StatisticResultsDocGeneratorImpl(){
		roundingFormat = new DecimalFormat("0.0##",DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		roundingFormat.setRoundingMode(RoundingMode.HALF_UP);
	}

	/**
	 * Method that generates the general statistics of width and depth
	 * @param statistics statistics Statistics object
	 * @return the generalStatistics element of the statistics document, as a JDOM2 {@link Element} object.
	 */
	protected Element generateGeneralStatistics(Statistics statistics){
		Element generalStatisticsElement = new Element("generalStatistics",STATISTICS_NAMESPACE);
		
		Element depthElement = new Element("depth",STATISTICS_NAMESPACE);
		Element depthMaxElement = new Element("max",STATISTICS_NAMESPACE);
		depthMaxElement.setText(Long.toString(statistics.getMaxDepth()));
		depthElement.addContent(depthMaxElement);
		Element depthAvgElement = new Element("avg",STATISTICS_NAMESPACE);
		depthAvgElement.setText(roundingFormat.format(statistics.getAvgDepth()));
		depthElement.addContent(depthAvgElement);
		generalStatisticsElement.addContent(depthElement);

		Element widthElement = new Element("width",STATISTICS_NAMESPACE);
		Element widthMaxElement = new Element("max",STATISTICS_NAMESPACE);
		widthMaxElement.setText(Long.toString(statistics.getMaxWidth()));
		widthElement.addContent(widthMaxElement);
		Element widthAvgElement = new Element("avg",STATISTICS_NAMESPACE);
		widthAvgElement.setText(roundingFormat.format(statistics.getAvgWidth()));
		widthElement.addContent(widthAvgElement);
		
		generalStatisticsElement.addContent(widthElement);
		return generalStatisticsElement;
	}
	
	/**
	 * Overload for {@link StatisticResultsDocGeneratorImpl#generateBasicStatisticsEntryBasedElement(String, String, String, String, String, BasicStatisticsEntry, boolean, boolean)}, 
	 * where all the missing parameters are 'null'.
	 * @param elementName the name of the generated element
	 * @param path the path where the entry comes from (if it comes from a path), if it is not null, a 'path' attribute is generated with this value, else, no 'path' attribute is generated.
	 * @param entry the name of the generated element
	 * @param generateConditionedStatistics flag to indicate whether conditioned statistics info should be generated and attached to the element
	 * @param generateNonZeroRatio flag to indicate whether non zero ratio info should be generated and attached to the element
	 * @return the representation of the BasicStatisticsEntry object, as a JDOM2 element
	 * @see StatisticResultsDocGeneratorImpl#generateBasicStatisticsEntryBasedElement(String, String, String, String, String, BasicStatisticsEntry, boolean, boolean)
	 * @return
	 */
	protected Element generateBasicStatisticsEntryBasedElement(String elementName, String path, BasicStatisticsEntry entry, 
			boolean generateConditionedStatistics, boolean generateNonZeroRatio){
		return generateBasicStatisticsEntryBasedElement(elementName, path, null, null, null, entry, generateConditionedStatistics, generateNonZeroRatio);
	}
	
	/**
	 * Overload for {@link StatisticResultsDocGeneratorImpl#generateBasicStatisticsEntryBasedElement(String, String, String, String, String, BasicStatisticsEntry, boolean, boolean)}, 
	 * where all the missing parameters are 'null'.
	 * @param elementName the name of the generated element
	 * @param entry the name of the generated element
	 * @param generateConditionedStatistics flag to indicate whether conditioned statistics info should be generated and attached to the element
	 * @param generateNonZeroRatio flag to indicate whether non zero ratio info should be generated and attached to the element
	 * @return the representation of the BasicStatisticsEntry object, as a JDOM2 element
	 * @see StatisticResultsDocGeneratorImpl#generateBasicStatisticsEntryBasedElement(String, String, String, String, String, BasicStatisticsEntry, boolean, boolean)
	 */
	protected Element generateBasicStatisticsEntryBasedElement(String elementName, BasicStatisticsEntry entry, 
			boolean generateConditionedStatistics, boolean generateNonZeroRatio){
		return generateBasicStatisticsEntryBasedElement(elementName, null, null, null, null, entry, generateConditionedStatistics, generateNonZeroRatio);
	}
	
	/**
	 * This method generates an element which contains the representation of a basic statistics entry. Depending on which basic statistics entry is going to 
	 * be represented, the element could have a different name, different identifying attributes(like path, name, namespace, value...).
	 * @param elementName the name of the generated element
	 * @param path the path where the entry comes from (if it comes from a path), if it is not null, a 'path' attribute is generated with this value, else, no 'path' attribute is generated.
	 * @param name the name of the source node (if it comes from a complex type statistics entry), if it is not null, a 'name' attribute is generated with this value, else, no 'name' attribute is generated.
	 * @param namespace the namespace of the source node (if it comes from a complex type statistics entry), if it is not null, a 'namespace' attribute is generated with this value, else, no 'namespace' attribute is generated.
	 * @param value the value which the entry is related to (if it is related to a value), if it is not null, a 'value' child element with this value is generated (and xml:space attribute set to 'preserve'), else, no 'value' element is generated.
	 * @param entry the entry to represent
	 * @param generateConditionedStatistics flag to indicate whether conditioned statistics info should be generated and attached to the element
	 * @param generateNonZeroRatio flag to indicate whether non zero ratio info should be generated and attached to the element
	 * @return the representation of the BasicStatisticsEntry object, as a JDOM2 element
	 */
	protected Element generateBasicStatisticsEntryBasedElement(String elementName, String path, String name, String namespace, String value, 
			BasicStatisticsEntry entry, boolean generateConditionedStatistics, boolean generateNonZeroRatio){
		Element generatedElement=new Element(elementName,STATISTICS_NAMESPACE);
		if (path!=null) {
			Attribute pathAttr = new Attribute("path", path);
			generatedElement.setAttribute(pathAttr);
		}
		if(value!=null){
			Element valueElement = new Element("value", STATISTICS_NAMESPACE);
			valueElement.setText(value);
			Attribute spaceAttribute = new Attribute("space", "preserve", Namespace.XML_NAMESPACE);
			valueElement.setAttribute(spaceAttribute);
			generatedElement.addContent(valueElement);
			
		}
		
		if(name!=null){
			Attribute nameAttr = new Attribute("name", name);
			generatedElement.setAttribute(nameAttr);
		}
		
		if(namespace!=null){
			Attribute namespaceAttr = new Attribute("namespace", namespace);
			generatedElement.setAttribute(namespaceAttr);
		}
		
//		Element averageElement = new Element("average", STATISTICS_NAMESPACE);
//		averageElement.setText(entry.getAverage()+"");
//		atPathElement.addContent(averageElement);
		
				Attribute averageAttribute = new Attribute("average", roundingFormat.format(entry.getAverage())+"");
		generatedElement.setAttribute(averageAttribute);
		
		Set<ValueAndFrequency> mode = entry.getMode();
		
		if(!mode.isEmpty()){
			Element modeElement = new Element("modes", STATISTICS_NAMESPACE);
			for(ValueAndFrequency modeValue:mode){
				Element valueElement=new Element("modeValue",STATISTICS_NAMESPACE);
				valueElement.setText(roundingFormat.format(modeValue.getValue())+"");
				Attribute valueFreqAttr = new Attribute("frequency", modeValue.getFrequency()+"");
				valueElement.setAttribute(valueFreqAttr);
				modeElement.addContent(valueElement);
			}
			generatedElement.addContent(modeElement);
		}
		
		
//		Element maxElement=new Element("max",STATISTICS_NAMESPACE);
//		ValueAndFrequency max = entry.getMax();
//		maxElement.setText(max.getValue()+"");
//		Attribute maxFreqAttr = new Attribute("frequency", max.getFrequency()+"");
//		maxElement.setAttribute(maxFreqAttr);
//		generatedElement.addContent(maxElement);
		
		Attribute maxAttribute = new Attribute("max", roundingFormat.format(entry.getMax().getValue())+"");
		generatedElement.setAttribute(maxAttribute);
		
		Attribute maxFrequencyAttribute = new Attribute("frequencyOfMax", entry.getMax().getFrequency()+"");
		generatedElement.setAttribute(maxFrequencyAttribute);
		
//		Element minElement=new Element("min",STATISTICS_NAMESPACE);
//		ValueAndFrequency min = entry.getMin();
//		minElement.setText(min.getValue()+"");
//		Attribute minFreqAttr = new Attribute("frequency", min.getFrequency()+"");
//		minElement.setAttribute(minFreqAttr);
//		generatedElement.addContent(minElement);
		
		Attribute minAttribute = new Attribute("min", roundingFormat.format(entry.getMin().getValue())+"");
		generatedElement.setAttribute(minAttribute);
		
		Attribute minFrequencyAttribute = new Attribute("frequencyOfMin", entry.getMin().getFrequency()+"");
		generatedElement.setAttribute(minFrequencyAttribute);
		
//		Element varianceElement = new Element("variance", STATISTICS_NAMESPACE);
//		varianceElement.setText(entry.getVariance()+"");
//		generatedElement.addContent(varianceElement);
		
		Attribute varianceAttribute = new Attribute("variance", roundingFormat.format(entry.getVariance())+"");
		generatedElement.setAttribute(varianceAttribute);
		
//		Element standardDeviationAverageRatioElement = new Element("standardDeviationAverageRatio", STATISTICS_NAMESPACE);
//		standardDeviationAverageRatioElement.setText(entry.getStandardDeviationAverageRatio()+"");
//		generatedElement.addContent(standardDeviationAverageRatioElement);
		
		Attribute standardDeviationAverageRatioAttribute = new Attribute("standardDeviationAverageRatio", roundingFormat.format(entry.getStandardDeviationAverageRatio())+"");
		generatedElement.setAttribute(standardDeviationAverageRatioAttribute);
		
		if(generateConditionedStatistics){
			
			Attribute conditionedAverageAttribute = new Attribute("conditionedAverage", roundingFormat.format(entry.getConditionedAverage())+"");
			generatedElement.setAttribute(conditionedAverageAttribute);
			
			Attribute conditionedVarianceAttribute = new Attribute("conditionedVariance", roundingFormat.format(entry.getConditionedVariance())+"");
			generatedElement.setAttribute(conditionedVarianceAttribute);
			
			Attribute conditionedStandardDeviationAverageRatioAttribute = new Attribute("conditionedStandardDeviationAverageRatio", roundingFormat.format(entry.getConditionedStandardDeviationAverageRatio())+"");
			generatedElement.setAttribute(conditionedStandardDeviationAverageRatioAttribute);
			
//			Element conditionedStandardDeviationAverageRatioElement = new Element("conditionedStandardDeviationAverageRatio", STATISTICS_NAMESPACE);
//			conditionedStandardDeviationAverageRatioElement.setText(entry.getConditionedStandardDeviationAverageRatio()+"");
//			generatedElement.addContent(conditionedStandardDeviationAverageRatioElement);
//			
//			Element conditionedAverageElement = new Element("conditionedAverage", STATISTICS_NAMESPACE);
//			conditionedAverageElement.setText(entry.getConditionedAverage()+"");
//			generatedElement.addContent(conditionedAverageElement);
//			
//			Element conditionedVarianceElement = new Element("conditionedVariance", STATISTICS_NAMESPACE);
//			conditionedVarianceElement.setText(entry.getConditionedVariance()+"");
//			generatedElement.addContent(conditionedVarianceElement);
			
		}
		
		if(generateNonZeroRatio){
			Attribute nonZeroRatioAttribute = new Attribute("presenceRatio", roundingFormat.format(entry.getNonZeroRatio())+"");
			generatedElement.setAttribute(nonZeroRatioAttribute);
			
//			Element nonZeroRatio = new Element("presenceRatio", STATISTICS_NAMESPACE);
//			nonZeroRatio.setText(""+entry.getNonZeroRatio());
//			generatedElement.addContent(nonZeroRatio);
		}
				
//		Element totalElement = new Element("total", STATISTICS_NAMESPACE);
//		totalElement.setText(entry.getTotal()+"");
//		generatedElement.addContent(totalElement);
		
		Attribute totalAttribute = new Attribute("total", roundingFormat.format(entry.getTotal())+"");
		generatedElement.setAttribute(totalAttribute);
		
		return generatedElement;
	}
	
	/**
	 * This method is intended to generate the "atPath" information of elements and attributes. 
	 * It takes a {@link Map} between String and {@link BasicStatisticsEntry} which contains the information of the node at a concrete path and generates an element with the given elementName 
	 * and, for each mapping, a child element is generated via {@link StatisticResultsDocGeneratorImpl#generateBasicStatisticsEntryBasedElement(String, String, String, String, String, BasicStatisticsEntry, boolean, boolean)} 
	 * to represent the info of that element. If a Table of values info or a Map of numericValues info is passed, the corresponding entries are also generated and attached as children of the child which 
	 * describes the path.<br/>
	 * Here is an example of what is going to be generated:<br/>
	 * <pre>
	 * <![CDATA[
	 * <elementName>
	 * 	<subElementName>
	 *  Info from the BasicStatisticsEntry related to occurrences of a node at the path (as children or attributes)
	 *  <valueElementName>
	 *    <valueSubElementName>
	 *     Info from the BasicStatisticsEntry related to occurrences of a value occurrences of the node at the path (as children or attributes)
	 *    </valueSubElementName>
	 *    ...
	 *  </valueElementName>
	 *  <numericValuesStatistics>
	 *  Statistics of numeric values for the node
	 *  </numericValuesStatistics>
	 *  </subElementName>
	 *  ...
	 * </elementName>
	 * ]]>
	 * </pre><br/>
	 * Where valueElementName and numericValuesStatistics are optional elements which may not be generated if there are info about values or there are no numeric values for the path, 
	 * respectively. 
	 * @param elementName The name of the generated element.
	 * @param subElementName The name of each the element which contains the info of the node at a path.
	 * @param sourceMap The map that contains the info of the nodes at a path
	 * @param valuesElementName The name of the child element with the values info
	 * @param valuesSubElementName The name of each child of the valuesElementName child, with the info of a concrete value
	 * @param valuesTable the table that contains the info of each value at each path
	 * @param numericValuesInfo The info of numeric values
	 * @param numericValuesStatisticsElementName The element of the child which will contain the numeric statistics info
	 * @return An element with all the information described
	 */
	protected Element generateNodesAtPathElements(String elementName,String subElementName,Map<String,BasicStatisticsEntry> sourceMap,String valuesElementName, 
			String valuesSubElementName, Table<String,String,BasicStatisticsEntry> valuesTable, Map<String, BasicStatisticsEntry> numericValuesInfo, String numericValuesStatisticsElementName){
		Element element = new Element(elementName,STATISTICS_NAMESPACE);
		for(String path: sourceMap.keySet()){
			Element currentElement = generateBasicStatisticsEntryBasedElement(subElementName, path, sourceMap.get(path),true, true);
			element.addContent(currentElement);
			Map<String, BasicStatisticsEntry> values = valuesTable.row(path);
			if(!values.isEmpty()){
				Element valuesAtPathElement = new Element(valuesElementName,STATISTICS_NAMESPACE);
				for(String value: values.keySet()){
					Element currentValueElement = generateBasicStatisticsEntryBasedElement(valuesSubElementName, path, null, null, value, valuesTable.get(path,value), true, true);
					valuesAtPathElement.addContent(currentValueElement);
				}
				currentElement.addContent(valuesAtPathElement);
			}
			BasicStatisticsEntry numericValuesInfoOfPath = numericValuesInfo.get(path);
			if(numericValuesInfoOfPath!=null){
				Element numericValuesInfoOfPathElement = generateBasicStatisticsEntryBasedElement(numericValuesStatisticsElementName, numericValuesInfoOfPath,false, false);
				currentElement.addContent(numericValuesInfoOfPathElement);
			}
		}
		element.sortChildren(BASIC_ENTRY_ELEMENT_COMPARATOR);
		return element;
	}
	
	/**
	 * This method generates an element with info of the nodes under a complex type (either elements of the complex type or attributes of elements of a concrete complex type).
	 * For each node, a child element is generated with all the info of its occurrences, a child with the info of its values (if there is any info about them) which will contain one child per value 
	 * with its info and a child with the numericValuesInfo, if any. 
	 * @param elementName The name of the generated element.
	 * @param subElementName The name of each the element which contains the info of the node at a path.
	 * @param sourceMap The map that contains the info of the nodes of the complex type
	 * @param valuesElementName The name of the child element with the values info
	 * @param valuesSubElementName The name of each child of the valuesElementName child, with the info of a concrete value
	 * @param valuesTable the table that contains the info of each value of each node
	 * @param numericValuesInfo The info of numeric values
	 * @param numericValuesStatisticsElementName The element of the child which will contain the numeric statistics info
	 * @return An element with all the information described
	 */
	protected <T extends SchemaNode> Element generateNodesOfComplexTypesInfoElements(String elementName,String subElementName,Map<T,BasicStatisticsEntry> sourceMap,String valuesElementName, 
			String valuesSubElementName, Table<String,SchemaNode,BasicStatisticsEntry> valuesTable, Map<SchemaNode, BasicStatisticsEntry> numericValuesInfo, String numericValuesStatisticsElementName){
		Element element = new Element(elementName,STATISTICS_NAMESPACE);
		for(T node: sourceMap.keySet()){
			String nodeName=node.getName();
			String nodeNamespace=node.getNamespace();
			Map<String,BasicStatisticsEntry> valuesOfNode = valuesTable.column(node);
			Element currentElement = generateBasicStatisticsEntryBasedElement(subElementName, null, nodeName, nodeNamespace, null, sourceMap.get(node),true, true);
			element.addContent(currentElement);
			if(!valuesOfNode.isEmpty()){
				Element valuesOfNodeElement = new Element("values",STATISTICS_NAMESPACE);
				for(String value: valuesOfNode.keySet()){
					Element currentValueElement = generateBasicStatisticsEntryBasedElement("value", null, nodeName, nodeNamespace, value, valuesOfNode.get(value), true, true);
					valuesOfNodeElement.addContent(currentValueElement);
				}
				currentElement.addContent(valuesOfNodeElement);
			}
			BasicStatisticsEntry numericValuesInfoOfNode = numericValuesInfo.get(node);
			if(numericValuesInfoOfNode!=null){
				Element numericValuesInfoOfNodeElement = generateBasicStatisticsEntryBasedElement("numericValuesStatistics", numericValuesInfoOfNode,false, false);
				currentElement.addContent(numericValuesInfoOfNodeElement);
			}
		}
		element.sortChildren(BASIC_ENTRY_ELEMENT_COMPARATOR);
		return element;
	}
	
	/**
	 * This method generates an element with the info of subpatterns.
	 * @param elementName the element name
	 * @param subpatternsInfo the info of subpatterns
	 * @return An element with all the information described
	 */
	protected Element generateSubpatternsInfoElement(String elementName, Map<List<SchemaElement>, Integer> subpatternsInfo){
		Element element = new Element(elementName,STATISTICS_NAMESPACE);
		for(List<SchemaElement> subpattern: subpatternsInfo.keySet()){
			int occurrences = subpatternsInfo.get(subpattern);
			Element occurrencesElement = new Element("occurrences",STATISTICS_NAMESPACE);
			occurrencesElement.setText(""+occurrences);
			element.addContent(occurrencesElement);
			Element subpatternElementsElement = new Element("subpatternElements",STATISTICS_NAMESPACE);
			for(int i=0;i<subpattern.size();i++){
				SchemaElement schemaElement = subpattern.get(i);
				Element subpatternElementElement = new Element("subpatternElement", STATISTICS_NAMESPACE);
				subpatternElementElement.setAttribute("name", schemaElement.getName());
				subpatternElementElement.setAttribute("namespace", schemaElement.getNamespace());
				subpatternElementsElement.addContent(subpatternElementElement);
			}
			element.addContent(subpatternElementsElement);
		}
		return element;
	}
	
	/**
	 * This method generates an element with all the info contained on a {@link ComplexTypeStatisticsEntry} object
	 * @param elementName the name of the generated element
	 * @param complexType the complex type described by the entry
	 * @param complexTypeStatisticsEntry the entry
	 * @param elementInfoElementName the name of the element that describes the elements info
	 * @param elementInfoSubElementName the name of the element that describes the info of each element
	 * @param attributeInfoElementName the name of the element that describes the attributes info
	 * @param attributeInfoSubElementName the name of the element that describes the info of each attributes
	 * @param valuesInfoElementName the name of the element with the values info
	 * @param valuesInfoSubElementName the name of the elements which contains all the info of each concrete value (on each node)
	 * @param subpatternsInfoElementName the name of the element that describes the subpatterns info
	 * @return An element with all the information described
	 */
	protected Element generateComplexTypeEntry(String elementName,ComplexType complexType, ComplexTypeStatisticsEntry complexTypeStatisticsEntry, String elementInfoElementName, String elementInfoSubElementName, String attributeInfoElementName, String attributeInfoSubElementName, String valuesInfoElementName, String valuesInfoSubElementName, String subpatternsInfoElementName){
		Element complexTypeStatisticsEntryElement = new Element("complexType",STATISTICS_NAMESPACE);
		complexTypeStatisticsEntryElement.setAttribute("name", complexType.getName());
		Element elementsAtComplexTypeElement = generateNodesOfComplexTypesInfoElements(elementInfoElementName, elementInfoSubElementName, complexTypeStatisticsEntry.getElementInfo(), valuesInfoElementName, valuesInfoSubElementName, complexTypeStatisticsEntry.getValuesInfo(), complexTypeStatisticsEntry.getStatisticsOfNumericValuesOfNodes(), "numericValuesStatistics");
		complexTypeStatisticsEntryElement.addContent(elementsAtComplexTypeElement);
		Element attributesAtComplexTypeElement = generateNodesOfComplexTypesInfoElements(attributeInfoElementName, attributeInfoSubElementName, complexTypeStatisticsEntry.getAttributeOccurrencesInfo(), valuesInfoElementName, valuesInfoSubElementName, complexTypeStatisticsEntry.getValuesInfo(), complexTypeStatisticsEntry.getStatisticsOfNumericValuesOfNodes(), "numericValuesStatistics");
		complexTypeStatisticsEntryElement.addContent(attributesAtComplexTypeElement);
		Element subpatternsInfoElement = generateSubpatternsInfoElement(subpatternsInfoElementName, complexTypeStatisticsEntry.getSubpatternsInfo());
		complexTypeStatisticsEntryElement.addContent(subpatternsInfoElement);
		return complexTypeStatisticsEntryElement;
	}
	
	/**
	 * This method generates an element with the info about all the complex types.
	 * @param complexTypesInfo The complex types info of the statistics
	 * @return An element with all the information described
	 */
	protected Element generateComplexTypesInfo(Map<ComplexType,ComplexTypeStatisticsEntry> complexTypesInfo){
		Element complexTypesInfoElement = new Element("complexTypesInfo",STATISTICS_NAMESPACE);
		for(ComplexType complexType: complexTypesInfo.keySet()){
			Element complexTypeStatisticsEntryElement = generateComplexTypeEntry("complexType", complexType, complexTypesInfo.get(complexType), "elements", "element", "attributes", "attribute", "values", "value", "subpatternsInfo");
			complexTypesInfoElement.addContent(complexTypeStatisticsEntryElement);
		}
		return complexTypesInfoElement;
	}
	
	/**
	 * This method generates an element with the root occurrences info
	 * @param rootElementsInfo the root elements info of the statistics
	 * @return An element with all the information described
	 */
	protected Element generateRootElementsOccurrencesElement(Map<SchemaElement,Integer> rootElementsInfo){
		Element element = new Element("rootElementsInfo",STATISTICS_NAMESPACE);
		for(SchemaElement rootElement: rootElementsInfo.keySet()){
			Element currentRootInfoElement = new Element("rootElement",STATISTICS_NAMESPACE);
			Attribute currentRootName = new Attribute("name", rootElement.getName());
			currentRootInfoElement.setAttribute(currentRootName);
			Attribute currentRootNamespace = new Attribute("namespace", rootElement.getNamespace());
			currentRootInfoElement.setAttribute(currentRootNamespace);
			Attribute currentRootOccurrences = new Attribute("occurrences",rootElementsInfo.get(rootElement).toString());
			currentRootInfoElement.setAttribute(currentRootOccurrences);
			element.addContent(currentRootInfoElement);
		}
		return element;
	}
	
	/**
	 * @see StatisticResultsDocGenerator#generateStatisticResultsDoc(Statistics)
	 */
	@Override
	public Document generateStatisticResultsDoc(Statistics statistics) {
		Element statisticsElement = new Element("statistics",STATISTICS_NAMESPACE);
		
		Element generalStatisticsElement = generateGeneralStatistics(statistics);
		statisticsElement.addContent(generalStatisticsElement);

		Element rootElementsOccurrencesElement = generateRootElementsOccurrencesElement(statistics.getRootElementOccurrences());
		statisticsElement.addContent(rootElementsOccurrencesElement);
		
		Element elementsAtPathElement = generateNodesAtPathElements("elementsAtPathOccurrences", "element", statistics.getElementAtPathInfo(), "valuesAtPath", "valueAtPath", statistics.getValuesAtPathInfo(), statistics.getStatisticsOfNumericValuesAtPath(), "numericValuesStatistics");
		statisticsElement.addContent(elementsAtPathElement);

		Element attributesAtPathElement = generateNodesAtPathElements("attributesAtPathOccurrences", "attribute", statistics.getAttributeAtPathInfo(), "valuesAtPath", "valueAtPath", statistics.getValuesAtPathInfo(), statistics.getStatisticsOfNumericValuesAtPath(), "numericValuesStatistics");
		statisticsElement.addContent(attributesAtPathElement);
		
		Element complexTypesInfoElement = generateComplexTypesInfo(statistics.getComplexTypeInfo());
		statisticsElement.addContent(complexTypesInfoElement);
		
		Document resultingDocument = new Document(statisticsElement);
		return resultingDocument;
	}
}
