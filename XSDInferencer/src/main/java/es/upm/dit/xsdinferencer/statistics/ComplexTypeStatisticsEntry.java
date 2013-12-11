
package es.upm.dit.xsdinferencer.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import es.upm.dit.xsdinferencer.datastructures.Automaton;
import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.SchemaNode;

/**
 * This object contains all the statistics related to a particular {@link ComplexType}.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class ComplexTypeStatisticsEntry {
	
	/**
	 * Info of elements of the complex type
	 */
	private Map<SchemaElement,BasicStatisticsEntry> elementInfo;
	
	/**
	 * Info of subpatterns in this complex type
	 */
	private Map<List<SchemaElement>,Integer> subpatternsInfo;
	
	/**
	 * Info of attributes of this complex type
	 */
	private Map<SchemaAttribute, BasicStatisticsEntry> attributeOccurrencesInfo;
	/**
	 * Info of the values present at elements of this complex type.
	 * <ul>
	 * <li>Rows are the different values</li>
	 * <li>Columns are either elements of this complex type or attributes of elements under this complex type.</li>
	 * <li>Values are the BasicStatisticsEntry object associated to the value specified by the row occurring 
	 * at the element or attribute specified by the column.</li>
	 * </ul>
	 */
	private Table<String,SchemaNode,BasicStatisticsEntry> valuesInfo;
	/**
	 * The count of input documents
	 */
	private int inputDocumentsCount;
	
	/**
	 * Statistics over numeric values at elements or attributes of the complex type.
	 */
	private Map<SchemaNode,BasicStatisticsEntry> statisticsOfNumericValuesAtPath;	
	
	/**
	 * It contains the last hash {@link ComplexTypeStatisticsEntry#valuesInfo} in order 
	 * to detect whether it has changed. If it had changed, info about numeric values would 
	 * have to be recreated.
	 */
	private int lastAllValuesTablesHash;
	//private int totalOccurrences;
	
	/**
	 * Default constructor
	 * @param inputDocumentsCount how many input documents are there
	 */
	public ComplexTypeStatisticsEntry(int inputDocumentsCount){
		this.inputDocumentsCount=inputDocumentsCount;
		this.elementInfo=new HashMap<SchemaElement, BasicStatisticsEntry>();
		this.subpatternsInfo=new HashMap<List<SchemaElement>, Integer>();
		this.attributeOccurrencesInfo=new HashMap<SchemaAttribute, BasicStatisticsEntry>();
		this.valuesInfo=HashBasedTable.create();
		this.lastAllValuesTablesHash=valuesInfo.hashCode();
	}
	
	
	/**
	 * Copy constructor.
	 * Data structure objects are duplicated (not the original ones) but they point to the 
	 * original contents (BasicStatisticEntry objects etc.) 
	 * @param complexTypeStatisticsEntryScenario2CB the entry to copy
	 */
	public ComplexTypeStatisticsEntry(
			ComplexTypeStatisticsEntry complexTypeStatisticsEntryScenario2CB) {
		this.inputDocumentsCount=complexTypeStatisticsEntryScenario2CB.inputDocumentsCount;
		this.elementInfo=new HashMap<SchemaElement, BasicStatisticsEntry>(complexTypeStatisticsEntryScenario2CB.elementInfo);
		this.subpatternsInfo=new HashMap<List<SchemaElement>, Integer>(complexTypeStatisticsEntryScenario2CB.subpatternsInfo);
		this.attributeOccurrencesInfo=new HashMap<SchemaAttribute, BasicStatisticsEntry>(complexTypeStatisticsEntryScenario2CB.attributeOccurrencesInfo);
		this.valuesInfo=HashBasedTable.create(complexTypeStatisticsEntryScenario2CB.valuesInfo);
		this.lastAllValuesTablesHash=complexTypeStatisticsEntryScenario2CB.lastAllValuesTablesHash;
	}

	/**
	 * @return the elementInfo
	 */
	public Map<SchemaElement, BasicStatisticsEntry> getElementInfo() {
		return elementInfo;
	}

	/**
	 * @return the subpatternsInfo
	 */
	public Map<List<SchemaElement>, Integer> getSubpatternsInfo() {
		return subpatternsInfo;
	}

	/**
	 * @return the attributeOccurrencesInfo
	 */
	public Map<SchemaAttribute, BasicStatisticsEntry> getAttributeOccurrencesInfo() {
		return attributeOccurrencesInfo;
	}

	/**
	 * @return the valuesOfChildrenInfo
	 */
	public Table<String, SchemaNode, BasicStatisticsEntry> getValuesInfo() {
		return valuesInfo;
	}
	
	/**
	 * It takes a list of elements and registers all the subpatterns present.
	 * @param list a list of elements (the children of another element in the same order).
	 */
	public void registerSubpatternsFromList(List<SchemaElement>list){
		for(int i=0;i<list.size();i++){
			for(int j=i;j<list.size();j++){
				List<SchemaElement> subpattern = ImmutableList.copyOf(list.subList(i, j+1));//toIndex del metodo subList NO esta incluido
				Integer previousCount=subpatternsInfo.get(subpattern);
				if(previousCount==null){
					subpatternsInfo.put(subpattern, 1);
				}
				else{
					subpatternsInfo.put(subpattern, previousCount+1);
				}
			}
		}
	}
	
	/**
	 * It registers an occurrence of a child element on an element of this 
	 * complex type at a document.
	 * If the corresponding statistic entry did not exist previously, it is automatically created.
	 * @param element the element
	 * @param documentIndex the index of the document
	 */
	public void registerElementCount(SchemaElement element, int documentIndex){
		if(!elementInfo.containsKey(element))
			elementInfo.put(element, new BasicStatisticsEntry(inputDocumentsCount));
		elementInfo.get(element).registerCount(documentIndex);
		updateInputDocumentsCount(documentIndex+1);
	}
	
	/**
	 * It registers an occurrence of an attribute of an element of this complex type at a 
	 * document.
	 * If the corresponding statistic entry did not exist previously, it is automatically created.
	 * @param attribute the attribute
	 * @param documentIndex the index of the document
	 */
	public void registerAttributeOccurrenceInfoCount(SchemaAttribute attribute, int documentIndex){
		if(!attributeOccurrencesInfo.containsKey(attribute))
			attributeOccurrencesInfo.put(attribute, new BasicStatisticsEntry(inputDocumentsCount));
		attributeOccurrencesInfo.get(attribute).registerCount(documentIndex);
		updateInputDocumentsCount(documentIndex+1);
	}
	
	/**
	 * It registers the occurrence of a value on an element or attribute of this complex type.
	 * If the corresponding statistic entry did not exist previously, it is automatically created.
	 * @param value the value
	 * @param sourceElement the element or attribute on which the value occurred
	 * @param documentIndex the index of the document
	 */
	public void registerValueOfNodeCount(String value, SchemaNode sourceElement, int documentIndex) {
		if(!valuesInfo.contains(value, sourceElement)){
			valuesInfo.put(value, sourceElement, new BasicStatisticsEntry(inputDocumentsCount));
		}
		valuesInfo.get(value, sourceElement).registerCount(documentIndex);
		updateInputDocumentsCount(documentIndex+1);
	}

	/**
	 * @return the inputDocumentsCount
	 */
	public int getInputDocumentsCount() {
		return inputDocumentsCount;
	}
	
	/**
	 * It updates the current document count while registering new values at a BasicStatisticsEntry of a 
	 * field of this ComplexTypeStatisticsEntry.
	 * @param countCandidate a possible new count (for example, resulting from a documentIndex+1). The field is only updated if the input count is greater than the current one. 
	 */
	private void updateInputDocumentsCount(int countCandidate){
		if(countCandidate>this.inputDocumentsCount)
			this.inputDocumentsCount=countCandidate;
	}
	
	/**
	 * Method that generates merged {@link ComplexTypeStatisticsEntry} from 
	 * two original ones.
	 * @param entry1 An entry.
	 * @param entry2 Another entry.
	 * @param mergedSchemaElements The schema elements whose information will be present at the merged complex type.
	 * @param mergedSchemaAttributes The schema attributes whose information will be present at the merged complex type.
	 * @param mergedNodesWithValues The schema nodes whose information about values will be present at the merged complex type.
	 * @return The merged entry.
	 */
	public static ComplexTypeStatisticsEntry mergeEntries(ComplexTypeStatisticsEntry entry1, ComplexTypeStatisticsEntry entry2, 
			Set<SchemaElement> mergedSchemaElements, Set<SchemaAttribute> mergedSchemaAttributes, Set<SchemaNode> mergedNodesWithValues){

		int sizeNew=Math.max(entry1.getInputDocumentsCount(), entry2.getInputDocumentsCount());
		ComplexTypeStatisticsEntry entryMerged = new ComplexTypeStatisticsEntry(sizeNew);
		fillMergedMap(entry1.elementInfo, entry2.elementInfo, entryMerged.elementInfo, mergedSchemaElements);
		fillMergedMap(entry1.attributeOccurrencesInfo,entry2.attributeOccurrencesInfo,entryMerged.attributeOccurrencesInfo, mergedSchemaAttributes);
		fillMergedTable(entry1.valuesInfo, entry2.valuesInfo, entryMerged.valuesInfo, mergedNodesWithValues);
		fillMergedPatternsMap(entry1.subpatternsInfo, entry2.subpatternsInfo, entryMerged.subpatternsInfo, mergedSchemaElements);
		return entryMerged;
	}

	/**
	 * It fills an already created Map between anything and BasicStatisticsEntry which is intended to 
	 * be the merged map of two previously existing ones. The entries are merged via {@linkplain BasicStatisticsEntry#mergeBasicStatisticsEntries(BasicStatisticsEntry, BasicStatisticsEntry)} 
	 * if both existed previously or, if one of them is null, the other one is returned.
	 * @param entry1Info the first original map
	 * @param entry2Info the second original map
	 * @param entryMergedInfo the merged map to fill
	 * @param nodesMerged the set of merged nodes whose info will also be merged (the original ones may be either in the first, the second or both entries).
	 */
	private static <E extends SchemaNode> void fillMergedMap(Map<E, BasicStatisticsEntry> entry1Info,
			Map<E, BasicStatisticsEntry> entry2Info,
			Map<E, BasicStatisticsEntry> entryMergedInfo, Set<E> nodesMerged) {
		for(E currentNode:nodesMerged){
			if(currentNode.getNamespace().equals(Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE))
				continue;
			SchemaNode node1Equivalent=null;
			for(SchemaNode node1: entry1Info.keySet()){
				if(node1.getNamespace().equals(currentNode.getNamespace())&&
						node1.getName().equals(currentNode.getName())){
					node1Equivalent=node1;
					break;
				}
			}
			SchemaNode node2Equivalent=null;
			for(SchemaNode node2: entry2Info.keySet()){
				if(node2.getNamespace().equals(currentNode.getNamespace())&&
						node2.getName().equals(currentNode.getName())){
					node2Equivalent=node2;
					break;
				}
			}
			BasicStatisticsEntry basicStatisticsEntry1=entry1Info.get(node1Equivalent);
			BasicStatisticsEntry basicStatisticsEntry2=entry2Info.get(node2Equivalent);
			BasicStatisticsEntry basicStatisticsEntryMerged;
			if(basicStatisticsEntry1==null&&basicStatisticsEntry2!=null){
				basicStatisticsEntryMerged=basicStatisticsEntry2;
			}
			else if(basicStatisticsEntry1!=null&&basicStatisticsEntry2==null){
				basicStatisticsEntryMerged=basicStatisticsEntry1;
			}else if(basicStatisticsEntry1!=null&&basicStatisticsEntry2!=null){
				basicStatisticsEntryMerged=BasicStatisticsEntry.mergeBasicStatisticsEntries(basicStatisticsEntry1, basicStatisticsEntry2);
			}else {
				throw new NullPointerException("elementInfo of a ComplexTypeEntry should not contain nulls");
			}
			entryMergedInfo.put(currentNode, basicStatisticsEntryMerged);
		}
	}
	
	/**
	 * This method takes an original subset of nodes (in all of which a value has occurred) and returns 
	 * a set made of nodes with the same names and namespaces from another given set
	 * @param originalSubset the original subset
	 * @param mergedSet the merged set
	 * @return a subset of mergedSet which consists of values equivalent to the ones of originalSubset
	 */
	private static <F extends SchemaNode> Set<F> getEquivalentSetOfNodes(Set<F> originalSubset, Set<F> mergedSet){
		Set<F> resultingSet = new HashSet<F>(originalSubset.size());
		originalNodeLoop:
			for(F originalNode:originalSubset){
				for(F newNode:mergedSet){
					if(originalNode.equalsIgnoreType(newNode)){
						resultingSet.add(newNode);
						continue originalNodeLoop;
					}
				}
			}
		return resultingSet;
	}
	
	/**
	 * It fills an already created Table with any kind of keys and BasicStatisticsEntry 
	 * which is intended to be the merged table of two previously existing ones.
	 * @param entry1Info the first original table
	 * @param entry2Info the second original table
	 * @param entryMergedInfo the merged table to fill
	 * @param mergedNodes the nodes whose information will be present at the merged table. Necessary because some of those 
	 *                    objects might be not the same that the ones which represented the nodes before the merge.
	 */
	private static <E,F extends SchemaNode> void fillMergedTable(Table<E,F,BasicStatisticsEntry> entry1Info,
			Table<E,F,BasicStatisticsEntry> entry2Info,
			Table<E,F,BasicStatisticsEntry> entryMergedInfo, Set<F> mergedNodes) {
		Set<E> unionOfRowKeySets = Sets.union(entry1Info.rowKeySet(), entry2Info.rowKeySet());
		
		Set<E> commonRows = ImmutableSet.copyOf(unionOfRowKeySets);
		for(E currentRow:commonRows){
			Set<F> unionOfColumnKeySets = Sets.union(entry1Info.row(currentRow).keySet(), entry2Info.row(currentRow).keySet());
			Set<F> mergedNodesOfCurrentRow = getEquivalentSetOfNodes(unionOfColumnKeySets, mergedNodes);
			fillMergedMap(entry1Info.row(currentRow), entry2Info.row(currentRow), entryMergedInfo.row(currentRow), mergedNodesOfCurrentRow);
		}
	}
	
	/**
	 * It fills an already created Map between anything and Integer which is intended to 
	 * be the merged map of two previously existing ones. The Integers are merged by summing them.
	 * @param entry1Info the first original map
	 * @param entry2Info the second original map
	 * @param entryMergedInfo the merged map to fill
	 * @param mergedSchemaElements {@link SchemaElement} objects that represent the children of the merged complex type,
	 *                             whose subpatterns information is being merged.
	 */
	private static void fillMergedPatternsMap(Map<List<SchemaElement>,Integer> entry1Info, 
			Map<List<SchemaElement>,Integer> entry2Info, Map<List<SchemaElement>,Integer> entryMergedInfo, Set<SchemaElement> mergedSchemaElements){
		Set<List<SchemaElement>> commonOriginalLists = ImmutableSet.copyOf(Sets.union(entry1Info.keySet(), entry2Info.keySet()));
		for(List<SchemaElement> currentList:commonOriginalLists){
			List<SchemaElement> currentListOfMerged = new ArrayList<>(currentList.size());
			for(int i=0; i<currentList.size();i++){
				SchemaElement currentOriginalElement = currentList.get(i);
				SchemaElement newElement = null;
				for(SchemaElement newElementCandidate: mergedSchemaElements){
					if(currentOriginalElement.equalsIgnoreType(newElementCandidate)){
						newElement=newElementCandidate;
						break;
					}
				}
				if(newElement==null){
					throw new IllegalArgumentException("Original element at entry which does not match one of the merged");
				}
				currentListOfMerged.add(newElement);
			}
			Integer num1W=entry1Info.get(currentList);
			Integer num2W=entry2Info.get(currentList);
			int num1 = num1W!=null?num1W.intValue():0;
			int num2 = num2W!=null?num2W.intValue():0;
			int newNum = num1+num2;
			//If one and only one of the nums is zero, there may be an equivalent old value which has already been 
			//gathered from a list of elements with same name and namespace but different types
			if((num1W==null) != (num2W==null)){ //The condition is like a XOR
				Integer numOldW=entryMergedInfo.get(currentListOfMerged);
				int numOld = numOldW!=null?numOldW.intValue():0;
				newNum+=numOld;
			}
			entryMergedInfo.put(currentListOfMerged, newNum);
		}
	}
	
	/**
	 * Returns statistics over numeric values of elements and attributes at given paths
	 * @return A map between the paths and the statistics.
	 */
	public Map<SchemaNode, BasicStatisticsEntry> getStatisticsOfNumericValuesOfNodes() {
		int currentHash = valuesInfo.hashCode();
		if(statisticsOfNumericValuesAtPath==null || currentHash!=lastAllValuesTablesHash){
			if(statisticsOfNumericValuesAtPath==null){
				statisticsOfNumericValuesAtPath=new HashMap<>();
			} else {
				statisticsOfNumericValuesAtPath.clear();
			}
		}
		nodeLoop:
		for(SchemaNode node: valuesInfo.columnKeySet()){
			Map<String, BasicStatisticsEntry> pathValuesInfo = valuesInfo.column(node);
			List<Double> values = new ArrayList<>();
			for(String valueStr: pathValuesInfo.keySet()){
				try {
					Double value = Double.valueOf(valueStr);
					for(int i=0;i<pathValuesInfo.get(valueStr).getTotal();i++){
						values.add(value);
					}
				} catch (NumberFormatException e) {
					continue nodeLoop;
				}
			}
			BasicStatisticsEntry pathEntry = new BasicStatisticsEntry(values);
			statisticsOfNumericValuesAtPath.put(node, pathEntry);
		}
		return statisticsOfNumericValuesAtPath;
	}
	
	/**
	 * This method re-creates the data structures, so that all the hashes are re-calculated. 
	 * This allows to avoid some misbehaviors on hash-based collections when objects are changed 
	 * in a way such that the hash changes after storing them into the collection. 
	 */
	public void rehashDataStructures(){
		elementInfo=new HashMap<>(elementInfo);
		attributeOccurrencesInfo=new HashMap<>(attributeOccurrencesInfo);
		valuesInfo=HashBasedTable.create(valuesInfo);
		subpatternsInfo=new HashMap<>(subpatternsInfo);
		if(statisticsOfNumericValuesAtPath!=null){
			statisticsOfNumericValuesAtPath=new HashMap<>(statisticsOfNumericValuesAtPath);
		} 
		
	}


	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime
//				* result
//				+ ((attributeOccurrencesInfo == null) ? 0
//						: attributeOccurrencesInfo.hashCode());
//		result = prime * result
//				+ ((elementInfo == null) ? 0 : elementInfo.hashCode());
//		result = prime * result + inputDocumentsCount;
//		result = prime
//				* result
//				+ ((statisticsOfNumericValuesAtPath == null) ? 0
//						: statisticsOfNumericValuesAtPath.hashCode());
//		result = prime * result
//				+ ((subpatternsInfo == null) ? 0 : subpatternsInfo.hashCode());
//		result = prime * result
//				+ ((valuesInfo == null) ? 0 : valuesInfo.hashCode());
//		return result;
		return Objects.hash(this.elementInfo,this.attributeOccurrencesInfo,this.inputDocumentsCount,this.subpatternsInfo,this.valuesInfo);
	}


	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ComplexTypeStatisticsEntry)) {
			return false;
		}
		ComplexTypeStatisticsEntry other = (ComplexTypeStatisticsEntry) obj;
		if (attributeOccurrencesInfo == null) {
			if (other.attributeOccurrencesInfo != null) {
				return false;
			}
		} else if (!attributeOccurrencesInfo
				.equals(other.attributeOccurrencesInfo)) {
			return false;
		}
		if (elementInfo == null) {
			if (other.elementInfo != null) {
				return false;
			}
		} else if (!elementInfo.equals(other.elementInfo)) {
			return false;
		}
		if (inputDocumentsCount != other.inputDocumentsCount) {
			return false;
		}
		if (subpatternsInfo == null) {
			if (other.subpatternsInfo != null) {
				return false;
			}
		} else if (!subpatternsInfo.equals(other.subpatternsInfo)) {
			return false;
		}
		if (valuesInfo == null) {
			if (other.valuesInfo != null) {
				return false;
			}
		} else if (!valuesInfo.equals(other.valuesInfo)) {
			return false;
		}
		return true;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ComplexTypeStatisticsEntry [inputDocumentsCount="
				+ inputDocumentsCount + ", elementInfo=" + elementInfo
				+ ", attributeOccurrencesInfo=" + attributeOccurrencesInfo
				+ ", valuesInfo=" + valuesInfo
				+ ", statisticsOfNumericValuesAtPath="
				+ statisticsOfNumericValuesAtPath + ", subpatternsInfo="
				+ subpatternsInfo + "]";
	}
}
