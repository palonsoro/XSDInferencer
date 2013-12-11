
package es.upm.dit.xsdinferencer.statistics;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;

/**
 * It contains all the statistics obtained during the inference an Schema 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class Statistics {

	/**
	 * It counts how many times a valid root element occurs, it means, in how many documents a suitable root is the chosen root.
	 */
	private Map<SchemaElement,Integer> rootElementOccurrences;
	/**
	 * It stores the information concerning the complex types.
	 */
	private Map<ComplexType, ComplexTypeStatisticsEntry> complexTypeInfo;
	/**
	 * It stores info of each element present at a concrete path.
	 */
	private Map<String, BasicStatisticsEntry> elementAtPathInfo;
	/**
	 * It stores info of each attribute present at a concrete path.
	 */
	private Map<String, BasicStatisticsEntry> attributeOccurrencesAtPathInfo;
	/**
	 * It stores info of the values at each element at a concrete path.
	 */
	private Table<String,String,BasicStatisticsEntry> valuesAtPathInfo;
	/**
	 * It stores the information of the depth of the nodes. 
	 * Rows are document indexes, columns are element depths indexes (it does not matter anything of the element but its depth) 
	 * and values are depths.
	 * They are used to calculate the desired statistic parameters.
	 */
	private Table<Integer,Long,Long> depthsInfo;
	
	/**
	 * It stores the information of the width of the nodes. 
	 * Rows are document indexes, columns are element widths indexes (it does not matter anything of the element but its width) 
	 * and values are widths.
	 * They are used to calculate the desired statistic parameters.
	 */
	private Table<Integer,Long,Long> widthsInfo;
	
	/**
	 * Number of input documents
	 */
	private final int inputDocumentsCount;
	
	/**
	 * Statistics over numeric values at paths.
	 */
	private Map<String,BasicStatisticsEntry> statisticsOfNumericValuesAtPath;
	
	/**
	 * Last hash value of valuesAtPathInfo, it allows to know whether it has been modified in order to 
	 * recalculate statisticsOfNumericValuesAtPath if and only if valuesAtPathInfo has changed.
	 */
	private int valuesAtPathInfoLastHash;
	
	/**
	 * Method that sums all the numbers from a collection
	 * @param collection a collection of numbers
	 * @return the sum
	 */
	private static <E extends Number> double sum(Collection<E> collection){
		double result = 0.0;
		for(E number:collection){
			result+=number.doubleValue();
		}
		return result;
	}

	/**
	 * Method that gets the average of all the numbers from a collection
	 * @param collection a collection of numbers
	 * @return the average
	 */
	private static <E extends Number> double average(Collection<E> collection){
		double sum = sum(collection);
		return sum/collection.size();
	}
	
	/**
	 * Default constructor.
	 */
	public Statistics(int inputDocumentsCount){
		this.inputDocumentsCount=inputDocumentsCount;
		rootElementOccurrences=new HashMap<SchemaElement, Integer>();
		elementAtPathInfo=new HashMap<String, BasicStatisticsEntry>();
		complexTypeInfo=new HashMap<ComplexType, ComplexTypeStatisticsEntry>();
		attributeOccurrencesAtPathInfo=new HashMap<String, BasicStatisticsEntry>();
		valuesAtPathInfo=HashBasedTable.create();
		depthsInfo=HashBasedTable.create();
		widthsInfo=HashBasedTable.create();
		statisticsOfNumericValuesAtPath=null;
		valuesAtPathInfoLastHash=valuesAtPathInfo.hashCode();
	}
	
	/**
	 * Copy constructor.
	 * IMPORTANT: The copy is not recursive, it means, the new object will contain NEW collections with THE SAME BasicStatisticEntry and ComplexTypeEntry objects.
	 * @param statistics 
	 */
	public Statistics(Statistics statistics) {
		this.inputDocumentsCount=statistics.inputDocumentsCount;
		rootElementOccurrences=new HashMap<SchemaElement, Integer>(statistics.rootElementOccurrences);
		elementAtPathInfo=new HashMap<String, BasicStatisticsEntry>(statistics.elementAtPathInfo);
		complexTypeInfo=new HashMap<ComplexType, ComplexTypeStatisticsEntry>(statistics.complexTypeInfo);
		attributeOccurrencesAtPathInfo=new HashMap<String, BasicStatisticsEntry>(statistics.attributeOccurrencesAtPathInfo);
		valuesAtPathInfo=HashBasedTable.create(statistics.valuesAtPathInfo);
		depthsInfo=HashBasedTable.create(statistics.depthsInfo);
		widthsInfo=HashBasedTable.create(statistics.widthsInfo);
		statisticsOfNumericValuesAtPath=statistics.statisticsOfNumericValuesAtPath;
		valuesAtPathInfoLastHash=statistics.valuesAtPathInfoLastHash;
	}

	/**
	 * @return the rootElementOccurrences
	 */
	public Map<SchemaElement, Integer> getRootElementOccurrences() {
		return rootElementOccurrences;
	}

//	/**
//	 * @param rootElementOccurrences the rootElementOccurrences to set
//	 */
//	public void setRootElementOccurrences(
//			Map<SchemaElement, Integer> rootElementOccurrences) {
//		this.rootElementOccurrences = rootElementOccurrences;
//	}

	/**
	 * @return the elementAtPathInfo
	 */
	public Map<String, BasicStatisticsEntry> getElementAtPathInfo() {
		return elementAtPathInfo;
	}

//	/**
//	 * @param elementAtPathInfo the elementAtPathInfo to set
//	 */
//	public void setElementAtPathInfo(
//			Map<String, BasicStatisticsEntry> elementAtPathInfo) {
//		this.elementAtPathInfo = elementAtPathInfo;
//	}

	/**
	 * @return the complexTypeInfo
	 */
	public Map<ComplexType, ComplexTypeStatisticsEntry> getComplexTypeInfo() {
		return complexTypeInfo;
	}

//	/**
//	 * @param complexTypeInfo the complexTypeInfo to set
//	 */
//	public void setComplexTypeInfo(
//			Map<ComplexType, ComplexTypeStatisticsEntry> complexTypeInfo) {
//		this.complexTypeInfo = complexTypeInfo;
//	}

	/**
	 * @return the attributeOccurrencesAtPathInfo
	 */
	public Map<String, BasicStatisticsEntry> getAttributeAtPathInfo() {
		return attributeOccurrencesAtPathInfo;
	}

//	/**
//	 * @param attributeOccurrencesAtPathInfo the attributeOccurrencesAtPathInfo to set
//	 */
//	public void setAttributeOccurrencesAtPathInfo(
//			Map<String, BasicStatisticsEntry> attributeOccurrencesAtPathInfo) {
//		this.attributeOccurrencesAtPathInfo = attributeOccurrencesAtPathInfo;
//	}

	/**
	 * @return the valuesAtPathInfo
	 */
	public Table<String, String, BasicStatisticsEntry> getValuesAtPathInfo() {
		return valuesAtPathInfo;
	}
	
	/**
	 * 
	 * @return The maximum possible depth for an element in any document.
	 */
	public long getMaxDepth(){
		List<Long> maxDepthsByDoc = new ArrayList<>(depthsInfo.rowKeySet().size());
		for(Integer documentIndex: depthsInfo.rowKeySet()){
			maxDepthsByDoc.add(Collections.max(depthsInfo.row(documentIndex).values()));
		}
		if(maxDepthsByDoc.isEmpty())
			return 0;
		long result = Collections.max(maxDepthsByDoc);
		return result;
	}
	
	/**
	 * 
	 * @return The average depth.
	 */
	public double getAvgDepth(){
		List<Double> maxDepthsByDoc = new ArrayList<>(depthsInfo.rowKeySet().size());
		for(Integer documentIndex: depthsInfo.rowKeySet()){
			maxDepthsByDoc.add(average(depthsInfo.row(documentIndex).values()));
		}
		if(maxDepthsByDoc.isEmpty())
			return 0;
		double result = average(maxDepthsByDoc);
		return result;
	}
	
	/**
	 * 
	 * @return The maximum possible width for an element in any document.
	 */
	public long getMaxWidth(){
		List<Long> maxWidthsByDoc = new ArrayList<>(widthsInfo.rowKeySet().size());
		for(Integer documentIndex: widthsInfo.rowKeySet()){
			maxWidthsByDoc.add(Collections.max(widthsInfo.row(documentIndex).values()));
		}
		if(maxWidthsByDoc.isEmpty())
			return 0;
		long result = Collections.max(maxWidthsByDoc);
		return result;
	}
	
	/**
	 * 
	 * @return The average width.
	 */
	public double getAvgWidth(){
		List<Double> maxWidthsByDoc = new ArrayList<>(widthsInfo.rowKeySet().size());
		for(Integer documentIndex: widthsInfo.rowKeySet()){
			maxWidthsByDoc.add(average(widthsInfo.row(documentIndex).values()));
		}
		if(maxWidthsByDoc.isEmpty())
			return 0;
		double result = average(maxWidthsByDoc);
		return result;
	}
	
	/**
	 * It registers the occurrence of a root element.
	 * If the corresponding statistic entry did not exist previously, it is automatically created.
	 * @param element the root element to register
	 */
	public void registerRootElementOccurrence(SchemaElement element){
		if(rootElementOccurrences.containsKey(element)){
			rootElementOccurrences.put(element, rootElementOccurrences.get(element)+1);
		}
		else {
			rootElementOccurrences.put(element, 1);
		}
	}
	
	/**
	 * It registers the occurrence of an element at a path.
	 * If the corresponding statistic entry did not exist previously, it is automatically created.
	 * @param path the path of the element
	 * @param documentIndex the index of the document
	 */
	public void registerElementAtPathCount(String path, int documentIndex){
		if(!elementAtPathInfo.containsKey(path))
			elementAtPathInfo.put(path, new BasicStatisticsEntry(inputDocumentsCount));
		elementAtPathInfo.get(path).registerCount(documentIndex);
	}
	
	/**
	 * It registers the occurrence of an attribute at a path.
	 * If the corresponding statistic entry did not exist previously, it is automatically created.
	 * @param path the path of the attribute
	 * @param documentIndex the index of the document
	 */
	public void registerAttributeOccurrenceAtPathCount(String path, int documentIndex){
		if(!attributeOccurrencesAtPathInfo.containsKey(path))
			attributeOccurrencesAtPathInfo.put(path, new BasicStatisticsEntry(inputDocumentsCount));
		attributeOccurrencesAtPathInfo.get(path).registerCount(documentIndex);
	}

	/**
	 * It registers the occurrence of a value on an element or attribute at a path.
	 * If the corresponding statistic entry did not exist previously, it is automatically created.
	 * @param path the path of the attribute
	 * @param value the value
	 * @param documentIndex the index of the document
	 */
	public void registerValueAtPathCount(String path, String value, int documentIndex){
		if(!valuesAtPathInfo.contains(path, value))
			valuesAtPathInfo.put(path, value, new BasicStatisticsEntry(inputDocumentsCount));
		valuesAtPathInfo.get(path,value).registerCount(documentIndex);
	}
	
	/**
	 * Registers the depth of an element of the specified document
	 * @param documentIndex the index of the document 
	 * @param depth the depth to register
	 */
	public void registerDepth(int documentIndex, long depth){
		long elementNumber=1;
		Set<Long> elementIndexes = depthsInfo.row(documentIndex).keySet();
		if(elementIndexes.size()>0)
			elementNumber+=Collections.max(elementIndexes);
		depthsInfo.put(documentIndex,elementNumber,depth);
	}
	
	/**
	 * Registers the width of an element of the specified document
	 * @param documentIndex the index of the document 
	 * @param width the width to register
	 */
	public void registerWidth(int documentIndex, long width){
		long elementNumber=1;
		Set<Long> elementIndexes = widthsInfo.row(documentIndex).keySet();
		if(elementIndexes.size()>0)
			elementNumber+=Collections.max(elementIndexes);
		widthsInfo.put(documentIndex,elementNumber,width);
	}
	
	/**
	 * Looks for the ComplexTypeStatisticsEntry related to a concrete ComplexType, given its name.
	 * @param name the name of the complex type
	 * @return 
	 */
	public ComplexTypeStatisticsEntry getComplexTypeStatisticsEntryByName(String name){
		checkNotNull(name, "'name' must not be null");
		for(ComplexType complexType:complexTypeInfo.keySet()){
			if(complexType.getName().equals(name)){
				return complexTypeInfo.get(complexType);
			}
		}
		return null;
	}

	/**
	 * Returns statistics over numeric values of elements and attributes at given paths
	 * @return A map between the paths and the statistics.
	 */
	public Map<String, BasicStatisticsEntry> getStatisticsOfNumericValuesAtPath() {
		if(statisticsOfNumericValuesAtPath==null || valuesAtPathInfo.hashCode()!=valuesAtPathInfoLastHash){
			if(statisticsOfNumericValuesAtPath==null){
				statisticsOfNumericValuesAtPath=new HashMap<>();
			} else {
				statisticsOfNumericValuesAtPath.clear();
			}
		}
		pathLoop:
		for(String path: valuesAtPathInfo.rowKeySet()){
			Map<String, BasicStatisticsEntry> pathValuesInfo = valuesAtPathInfo.row(path);
			List<Double> values = new ArrayList<>();
			for(String valueStr: pathValuesInfo.keySet()){
				try {
					Double value = Double.valueOf(valueStr);
					for(int i=0;i<pathValuesInfo.get(valueStr).getTotal();i++){
						values.add(value);
					}
				} catch (NumberFormatException e) {
					continue pathLoop;
				}
			}
			BasicStatisticsEntry pathEntry = new BasicStatisticsEntry(values);
			statisticsOfNumericValuesAtPath.put(path, pathEntry);
		}
		valuesAtPathInfoLastHash=valuesAtPathInfo.hashCode();
		return statisticsOfNumericValuesAtPath;
	}
	
	/**
	 * This method rehashes all the hash-based data structures of this Statistics object and any  
	 * {@link ComplexTypeStatisticsEntry} object with has anything to do with these statistics 
	 * (by calling {@link ComplexTypeStatisticsEntry#rehashDataStructures()} on it). 
	 */
	public void rehashDataStructures(){
		rootElementOccurrences=new HashMap<>(rootElementOccurrences);
		elementAtPathInfo=new HashMap<>(elementAtPathInfo);
		attributeOccurrencesAtPathInfo=new HashMap<>(attributeOccurrencesAtPathInfo);
		valuesAtPathInfo=HashBasedTable.create(valuesAtPathInfo);
		if(statisticsOfNumericValuesAtPath!=null && !statisticsOfNumericValuesAtPath.isEmpty())
			statisticsOfNumericValuesAtPath=new HashMap<>(statisticsOfNumericValuesAtPath);
		complexTypeInfo=new HashMap<>(complexTypeInfo);
		for(ComplexType complexType: complexTypeInfo.keySet()){
			complexTypeInfo.get(complexType).rehashDataStructures();
		}
	}

	/**
	 * @return the inputDocumentsCount
	 */
	public int getInputDocumentsCount() {
		return inputDocumentsCount;
	}
	
}
