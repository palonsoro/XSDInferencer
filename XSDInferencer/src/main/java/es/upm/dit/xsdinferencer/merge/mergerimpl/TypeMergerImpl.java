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
package es.upm.dit.xsdinferencer.merge.mergerimpl;

import static es.upm.dit.xsdinferencer.datastructures.Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.Automaton;
import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.SchemaNode;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.extraction.SimpleTypeInferencer;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.InferencersFactory;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.NameTypeNameInferencer;
import es.upm.dit.xsdinferencer.merge.AttributeListComparator;
import es.upm.dit.xsdinferencer.merge.ChildrenPatternComparator;
import es.upm.dit.xsdinferencer.merge.EnumComparator;
import es.upm.dit.xsdinferencer.merge.TypeMerger;
import es.upm.dit.xsdinferencer.statistics.ComplexTypeStatisticsEntry;
import es.upm.dit.xsdinferencer.statistics.Statistics;
import es.upm.dit.xsdinferencer.util.guavapredicates.SchemaElementPredicates;

/**
 * Default implementation of {@link TypeMerger}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class TypeMergerImpl implements TypeMerger {
	
	/**
	 * Map between complex type names of those complex types which have been generated at merging time and the resulting complex type names.
	 * This is very important in order to avoid infinite recursion and some other problems.
	 */
	private Map<String,List<String>> mergedComplexTypeNames;
	
	/**
	 * Map between simple type names of those complex types which have been generated at merging time and the resulting simple type names.
	 * This is very important in order to avoid infinite recursion and some other problems.
	 */
	private Map<String,List<String>> mergedSimpleTypeNames;
	
	/**
	 * If two complex types are merged, they are backed up here so that complex type statistics entries may be merged afterwards
	 */
	private Map<String,ComplexType> mergedComplexTypesBackup;
	
	/**
	 * Default constructor
	 */
	public TypeMergerImpl(){
		//originalMergedMappings=new HashMap<>();
		mergedComplexTypeNames=new HashMap<>();
		mergedSimpleTypeNames=new HashMap<>();
		mergedComplexTypesBackup=new HashMap<>();
	}

	/**
	 * Returns true if the intersection of two sets is not empty
	 * @param set1 Set 1
	 * @param set2 Set 2
	 * @return true if the intersection is not empty, false otherwise
	 */
	private boolean intersectionNotEmpty(Set<?> set1, Set<?> set2){
//		Set<?> intersection=Sets.intersection(set1, set2);
//		return !intersection.isEmpty();
		for(Object obj :set1){
			if(set2.contains(obj))
				return true;
		}
		for(Object obj :set2){
			if(set1.contains(obj))
				return true;
		}
		return false;
	}
	
	/**
	 * @see TypeMerger#mergeTypes(Schema, XSDInferenceConfiguration)
	 */
	@Override
	public void mergeTypes(Schema schema,
			XSDInferenceConfiguration configuration) {
		//originalMergedMappings.clear();
		AttributeListComparator attributeListComparator = configuration.getAttributeListComparator();
		ChildrenPatternComparator childrenPatternComparator = configuration.getChildrenPatternComparator();
		EnumComparator enumComparator = configuration.getEnumsComparator();
		AttributeListComparator snAttributeListComparator = configuration.getSnAttributeListComparator();
		ChildrenPatternComparator snChildrenPatternComparator = configuration.getSnChildrenPatternComparator();
		EnumComparator snEnumComparator = configuration.getSnEnumsComparator();
		
		clearAll();
		
		boolean needsSameNameForcedMerge=(!configuration.getStrictValidRootDefinitionWorkaround() && !(configuration.getTypeNameInferencer() instanceof NameTypeNameInferencer))&&
				((schema.getNamespacesToPossiblePrefixMappingUnmodifiable().keySet().size()-configuration.getSkipNamespaces().size())>1);
//		boolean needsSameNameForcedMerge=false;
		boolean forcedSimpleTypeMergedAlreadyWarned=false;
		boolean forcedComplexTypeMergedAlreadyWarned=false;
		boolean changed=true;
		boolean anythingHasBeenMerged=false;
		
		mainChangedLoop:
		while(changed){
			changed=false;
			if(snEnumComparator!=null || needsSameNameForcedMerge){
				//Same name merge
				for(String simpleType1Key: schema.getSimpleTypes().keySet()){
					SimpleType simpleType1 = schema.getSimpleTypes().get(simpleType1Key);
					for(String simpleType2Key: schema.getSimpleTypes().keySet()){
						if(simpleType1Key.equals(simpleType2Key))
							continue;
						SimpleType simpleType2 = schema.getSimpleTypes().get(simpleType2Key);
						if(!intersectionNotEmpty(simpleType1.getSourceNodeNamespacesAndNames(), 
								simpleType2.getSourceNodeNamespacesAndNames()))
							continue;
						boolean simpleTypesAreSimilar = snEnumComparator!=null?snEnumComparator.compare(simpleType1, simpleType2):false;
						if(simpleTypesAreSimilar||needsSameNameForcedMerge){
							if(!simpleTypesAreSimilar&&!forcedSimpleTypeMergedAlreadyWarned){
								//System.err.println("It has been necessary to merge simple types because they appear on elements or attributes with the same name at namespace.");
								forcedSimpleTypeMergedAlreadyWarned=true;
							}
							SimpleType simpleTypeMerged = getMergedSimpleType(
									schema, configuration, simpleType1, simpleType2);
							replaceSimpleTypes(schema, simpleType1,
									simpleTypeMerged);
							replaceSimpleTypes(schema, simpleType2,
									simpleTypeMerged);
							changed=true;
							anythingHasBeenMerged=true;
							continue mainChangedLoop;
						}
					}
				}
				
			}
			
			if((snAttributeListComparator!=null && snChildrenPatternComparator!=null)||needsSameNameForcedMerge){
				//boolean removeOriginals=true;
				for(String complexType1Key:schema.getComplexTypes().keySet()){
					ComplexType complexType1 = schema.getComplexTypes().get(complexType1Key);
					for(String complexType2Key:schema.getComplexTypes().keySet()){
						if(complexType1Key.equals(complexType2Key))
							continue;
						ComplexType complexType2 = schema.getComplexTypes().get(complexType2Key);
						if(!intersectionNotEmpty(complexType1.getSourceElementNamespacesAndNames(), 
								complexType2.getSourceElementNamespacesAndNames()))
							continue;
						
						List<SchemaAttribute> attributeList1 = complexType1.getAttributeList();
						List<SchemaAttribute> attributeList2 = complexType2.getAttributeList();
						boolean attributesCondition = snAttributeListComparator!=null?snAttributeListComparator.compare(attributeList1,attributeList2):false;
						boolean automatonCondition = snChildrenPatternComparator!=null?snChildrenPatternComparator.compare(complexType1.getAutomaton(), complexType2.getAutomaton()):false;
						//We merge if: 1-Simple types are similar (if there is a comparator) or 
						//2-Simple types are not enumeration and have the same builtin type or 
						//3-Both consist of whitespace characters and/or are empty
						SimpleType simpleTypeOf1 = complexType1.getTextSimpleType();
						SimpleType simpleTypeOf2 = complexType2.getTextSimpleType();
						boolean simpleTypeCondition = snEnumComparator!=null?snEnumComparator.compare(simpleTypeOf1, simpleTypeOf2):false;//1
						simpleTypeCondition = simpleTypeCondition|| ((!simpleTypeOf1.isEnum() && !simpleTypeOf2.isEnum()) && simpleTypeOf1.getBuiltinType().equals(simpleTypeOf2.getBuiltinType()));//2
						simpleTypeCondition = simpleTypeCondition|| ((simpleTypeOf1.isEmpty()||simpleTypeOf1.isEmpty()) && (simpleTypeOf2.isEmpty()||simpleTypeOf2.isEmpty()));//3
						if((attributesCondition&&automatonCondition&&simpleTypeCondition)||needsSameNameForcedMerge){
							if(!(attributesCondition&&automatonCondition)&&!forcedComplexTypeMergedAlreadyWarned){
								//System.err.println("It has been necessary to merge complex types because they appear on elements with the same name at a namespace.");
								forcedComplexTypeMergedAlreadyWarned=true;
							}
							mergeComplexTypes(schema, configuration,
									snAttributeListComparator,
									snChildrenPatternComparator,
									snEnumComparator, true,
									complexType1, complexType2);
							for(ComplexType complexType: schema.getComplexTypes().values()){
								complexType.getAutomaton().rehashDatastrucures();
							}
							changed=true;
							anythingHasBeenMerged=true;
							continue mainChangedLoop;
						}
					}
				}
			}
			
			if(enumComparator!=null){

				//Same name merge
				for(String simpleType1Key: schema.getSimpleTypes().keySet()){
					SimpleType simpleType1 = schema.getSimpleTypes().get(simpleType1Key);
					for(String simpleType2Key: schema.getSimpleTypes().keySet()){
						if(simpleType1Key.equals(simpleType2Key))
							continue;
						SimpleType simpleType2 = schema.getSimpleTypes().get(simpleType2Key);
						if(enumComparator.compare(simpleType1, simpleType2)){
							SimpleType simpleTypeMerged = getMergedSimpleType(
									schema, configuration, simpleType1, simpleType2);
							replaceSimpleTypes(schema, simpleType1,
									simpleTypeMerged);
							replaceSimpleTypes(schema, simpleType2,
									simpleTypeMerged);
							
							changed=true;
							anythingHasBeenMerged=true;
							continue mainChangedLoop;
						}
					}
				}
				
			}
			
			if(attributeListComparator!=null && childrenPatternComparator!=null){
				//boolean removeOriginals=true;
				for(String complexType1Key:schema.getComplexTypes().keySet()){
					ComplexType complexType1 = schema.getComplexTypes().get(complexType1Key);
					for(String complexType2Key:schema.getComplexTypes().keySet()){
						if(complexType1Key.equals(complexType2Key))
							continue;
						ComplexType complexType2 = schema.getComplexTypes().get(complexType2Key);
						List<SchemaAttribute> attributeList1 = complexType1.getAttributeList();
						List<SchemaAttribute> attributeList2 = complexType2.getAttributeList();
						boolean attributesCondition = attributeListComparator.compare(attributeList1,attributeList2);
						boolean automatonCondition = childrenPatternComparator.compare(complexType1.getAutomaton(), complexType2.getAutomaton());
						//We merge if: 1-Simple types are similar (if there is a comparator) or 
						//2-Simple types are not enumeration and have the same builtin type or 
						//3-Both consist of whitespace characters and/or are empty
						SimpleType simpleTypeOf1 = complexType1.getTextSimpleType();
						SimpleType simpleTypeOf2 = complexType2.getTextSimpleType();
						boolean simpleTypeCondition = enumComparator!=null?enumComparator.compare(simpleTypeOf1, simpleTypeOf2):false;//1
						simpleTypeCondition = simpleTypeCondition|| ((!simpleTypeOf1.isEnum() && !simpleTypeOf2.isEnum()) && simpleTypeOf1.getBuiltinType().equals(simpleTypeOf2.getBuiltinType()));
						simpleTypeCondition = simpleTypeCondition|| ((simpleTypeOf1.isEmpty()||simpleTypeOf1.isEmpty()) && (simpleTypeOf2.isEmpty()||simpleTypeOf2.isEmpty()));
						if(attributesCondition&&automatonCondition&&simpleTypeCondition){
							mergeComplexTypes(schema, configuration,
									attributeListComparator,
									childrenPatternComparator,
									enumComparator, true,
									complexType1, complexType2);
							for(ComplexType complexType: schema.getComplexTypes().values()){
								complexType.getAutomaton().rehashDatastrucures();
							}
							changed=true;
							anythingHasBeenMerged=true;
							continue mainChangedLoop;
						}
					}
				}
			}
		
		}
		
		if(!anythingHasBeenMerged)
			return;
		
		Statistics statistics = schema.getStatistics();
		statistics.rehashDataStructures();
		Map<String,ComplexType> previousAndCurrentComplexTypes = new HashMap<>(mergedComplexTypesBackup);
		previousAndCurrentComplexTypes.putAll(schema.getComplexTypes());
		Set<String> complexTypeNamesWhoseStatisticsAreNotMergedYet = new HashSet<>(mergedComplexTypeNames.keySet());
//		mainStatisticsMerginLoop:
		while(!complexTypeNamesWhoseStatisticsAreNotMergedYet.isEmpty()){
			for(String complexTypeMergedName: mergedComplexTypeNames.keySet()){
				ComplexType complexTypeMerged = previousAndCurrentComplexTypes.get(complexTypeMergedName);
				ComplexTypeStatisticsEntry entryMerged = null;
				for (int i = 0; i < mergedComplexTypeNames.get(
							complexTypeMergedName).size()-1; i++) {
					String complexType1Name = mergedComplexTypeNames.get(complexTypeMergedName).get(i);
					String complexType2Name = mergedComplexTypeNames.get(complexTypeMergedName).get(i+1);
					ComplexType complexType1 = previousAndCurrentComplexTypes.get(complexType1Name);
					ComplexType complexType2 = previousAndCurrentComplexTypes.get(complexType2Name);
					List<SchemaAttribute> attributeListMerged = complexTypeMerged
							.getAttributeList();
//					ExtendedAutomaton automatonMerged = complexTypeMerged
//							.getAutomaton();
					ComplexTypeStatisticsEntry entry1 = i==0?statistics
							.getComplexTypeInfo().get(complexType1):entryMerged;
					ComplexTypeStatisticsEntry entry2 = statistics
							.getComplexTypeInfo().get(complexType2);
					if (entry1 == null || entry2 == null) {
						continue; //We have tried to merge statistics which should have been created by merging other ones but have not been created yet
					}
					Predicate<SchemaElement> elementsOfComplexTypeMerged = SchemaElementPredicates
							.complexTypeEquals(complexTypeMerged);
					Set<SchemaNode> nodesWithValues = new HashSet<SchemaNode>(
							Collections2.filter(schema.getElements().values(),
									elementsOfComplexTypeMerged));
					nodesWithValues.addAll(attributeListMerged);
					Set<SchemaElement> mergedSchemaElements = new HashSet<>(
							complexTypeMerged.getAutomaton()
									.getNodesImmutable());
					for (SchemaElement element : complexTypeMerged
							.getAutomaton().getNodesImmutable()) {
						if (element.getNamespace().equals(
								Automaton.DEFAULT_PSEUDOELEMENTS_NAMESPACE))
							mergedSchemaElements.remove(element);
					}
					entryMerged = ComplexTypeStatisticsEntry
							.mergeEntries(entry1, entry2, mergedSchemaElements,
									ImmutableSet.copyOf(attributeListMerged),
									nodesWithValues);
					
				}
				statistics.getComplexTypeInfo().put(complexTypeMerged,entryMerged);
				complexTypeNamesWhoseStatisticsAreNotMergedYet.remove(complexTypeMergedName);
			}
		}
		removeUnusedSimpleTypes(schema);
		removeUnusedComplexTypes(schema);
		removedUnusedSchemaElements(schema);
		cleanUnusedComplexTypeStatisticsEntries(schema);
	}

	/**
	 * Clears some private variables. This is required before starting a type merge and called 
	 * by {@link TypeMergerImpl#mergeTypes(Schema, XSDInferenceConfiguration)}
	 */
	protected void clearAll() {
		mergedComplexTypeNames.clear();
		mergedSimpleTypeNames.clear();
		mergedComplexTypesBackup.clear();
	}

	/**
	 * @param schema
	 */
	private void removeUnusedComplexTypes(Schema schema) {
		complexTypeRemovalLoop:
		for(String complexTypeKey: ImmutableSet.copyOf(schema.getComplexTypes().keySet())){
			ComplexType complexType=schema.getComplexTypes().get(complexTypeKey);
			for(SchemaElement element: schema.getElements().values()){
				if(element.getType().equals(complexType))
					continue complexTypeRemovalLoop;
			}
			schema.getComplexTypes().remove(complexTypeKey);
		}
	}
	
	private void removedUnusedSchemaElements(Schema schema){
		boolean changed = true;
		Table<String, String, SchemaElement> schemaElements = schema.getElements();
		mainLoop:
		while(changed){
			changed=false;
			for(String namespaceURI:schemaElements.rowKeySet()){
				elementInnerLoop:
				for(String key:schemaElements.row(namespaceURI).keySet()){
					SchemaElement currentSchemaElement = schemaElements.get(namespaceURI, key);
					if(currentSchemaElement.isValidRoot())
						continue elementInnerLoop;
					for(ComplexType complexType:schema.getComplexTypes().values()){
						if(complexType.getAutomaton().containsNode(currentSchemaElement))
							continue elementInnerLoop;
					}
					//If no complex type contains it and it is not a valid root, we delete it
					schemaElements.remove(namespaceURI, key);
					changed=true;
					continue mainLoop;
				}
			}
		}
	}

	/**
	 * Removes the unused simple types from a given schema
	 * @param schema the schema
	 */
	private void removeUnusedSimpleTypes(Schema schema) {
		simpleTypeRemovalLoop:
		for(String simpleTypeKey: ImmutableSet.copyOf(schema.getSimpleTypes().keySet())){
			SimpleType simpleType=schema.getSimpleTypes().get(simpleTypeKey);
			for(SchemaAttribute attr: schema.getAttributes().values()){
				if(attr.getSimpleType().equals(simpleType))
					continue simpleTypeRemovalLoop;
			}
			for(ComplexType complexType: schema.getComplexTypes().values()){
				if(complexType.getTextSimpleType().equals(simpleType))
					continue simpleTypeRemovalLoop;
			}
			schema.getSimpleTypes().remove(simpleTypeKey);
		}
	}

	/**
	 * It merges two complex types and updates all the corresponding schema data structures.
	 * @param schema the schema
	 * @param configuration the inference configuration
	 * @param attributeListComparator the current attribute list comparator
	 * @param childrenPatternComparator the current children pattern comparator
	 * @param enumComparator the current enumeration comparator
	 * @param removeOriginals if the original complex types must be always removed or not. 
	 * 			If it is false, an original complex type will only be removed if it is similar (comparators return true) 
	 * 			to the other one or to the merged complex type.
	 * @param complexType1 the first complex type to merge
	 * @param complexType2 the second complex type to merge
	 */
	private void mergeComplexTypes(Schema schema,
			XSDInferenceConfiguration configuration,
			AttributeListComparator attributeListComparator,
			ChildrenPatternComparator childrenPatternComparator,
			EnumComparator enumComparator, boolean removeOriginals,
			ComplexType complexType1, ComplexType complexType2) {
		String complexTypeMergedName = getMergedTypeName(configuration,
				complexType1.getName(), complexType2.getName(),mergedComplexTypeNames);
		//Here, we avoid a possible infinite recurion.
		//Look at the javadoc of mergedComplexTypes for details
		if(mergedComplexTypeNames.containsKey(complexTypeMergedName)){
			return;
		}
		else{
			registerTypeMerge(complexType1.getName(), complexType2.getName(), complexTypeMergedName, mergedComplexTypeNames);
		}

		ComplexType complexTypeMerged = new ComplexType(complexTypeMergedName,null,null,null);
		
		mergedComplexTypesBackup.put(complexType1.getName(), complexType1);
		mergedComplexTypesBackup.put(complexType2.getName(), complexType2);
		
		Set<String> complexType1Comments = complexType1.getComments();
		Set<String> complexType2Comments = complexType2.getComments();
		Set<String> complexTypeMergedComments = complexTypeMerged.getComments();
		
		complexTypeMergedComments.addAll(complexType1Comments);
		complexTypeMergedComments.addAll(complexType2Comments);
		
		SimpleType simpleType1=complexType1.getTextSimpleType();
		SimpleType simpleType2=complexType2.getTextSimpleType();
		
		List<SchemaAttribute> attributeList1 = complexType1.getAttributeList();
		List<SchemaAttribute> attributeList2 = complexType2.getAttributeList();
		
		//SimpleType merge
		
		if (!simpleType1.equals(simpleType2)) {
			SimpleType simpleTypeMerged = getMergedSimpleType(schema,
					configuration, simpleType1, simpleType2);
			complexTypeMerged.setTextSimpleType(simpleTypeMerged);
			postStepAfterForcedSimpleTypeMerged(schema, enumComparator,
					simpleType1, simpleType2, simpleTypeMerged);
		} else {
			complexTypeMerged.setTextSimpleType(simpleType1);
		}
		
		
		//Attribute list merge
		
		List<SchemaAttribute> attributeListMerged = getMergedAttributeList(
				schema, complexType1, complexType2, configuration, enumComparator);
		complexTypeMerged.setAttributeList(attributeListMerged);
		
		//Now, we have to update the schema data structures
		//it includes the removal of old complex type IF THE COMPLEX TYPES ARE MERGED VIA COMPARATORS
		updateSchemaAttributes(schema, configuration,
				removeOriginals, complexType1.getName(),
				complexType2.getName(), complexTypeMergedName,
				attributeList1, attributeList2, attributeListMerged, enumComparator);
		
		//Now, we have to merge the automatons
		ExtendedAutomaton automaton1 = complexType1.getAutomaton();
		ExtendedAutomaton automaton2 = complexType2.getAutomaton();
		ExtendedAutomaton automatonMerged = new ExtendedAutomaton(automaton1);
		complexTypeMerged.setAutomaton(automatonMerged);
		
		for(Table.Cell<SchemaElement,SchemaElement,Long> edge2: automaton2.getEdgeCellSet()){
//			SchemaElement mergedRow = automatonMerged.getEquivalentNode(edge2.getRowKey());
//			SchemaElement mergedColumn = automatonMerged.getEquivalentNode(edge2.getColumnKey());
			SchemaElement mergedRow = edge2.getRowKey();
			SchemaElement mergedColumn = edge2.getColumnKey();
			if(mergedRow==null)
				mergedRow=edge2.getRowKey();
			if(mergedColumn==null)
				mergedColumn=edge2.getColumnKey();
			long previousWeight=automatonMerged.getEdgeWeight(mergedRow, mergedColumn);
			if(previousWeight<0)
				previousWeight=0;
			automatonMerged.addEdge(mergedRow, mergedColumn,previousWeight+edge2.getValue());
		}
		//Now, we must merge the source words info of the second automaton (the info of the first one is already there)
		for(Table.Cell<Integer, SchemaElement, Integer> entry: automaton2.getSourceWordSymbolOccurrences().cellSet()){
			int offset = automaton1.getSourceWordSymbolOccurrences().rowKeySet().size();
			automatonMerged.getSourceWordSymbolOccurrences().put(offset+entry.getRowKey(), entry.getColumnKey(), entry.getValue());
		}
		
		boolean forceRemoveOriginals = (attributeListComparator==null)||(childrenPatternComparator==null);
		
		//Now, we must merge all the nodes with the same name and namespace but different complex types
//		mergeNodesWithSameNameAndNamespaceButDifferentType(schema,
//				configuration, attributeListComparator,
//				childrenPatternComparator, enumComparator, automaton1,
//				automaton2, automatonMerged, forceRemoveOriginals);
		//System.out.println("Fixing automaton of: "+complexTypeMergedName);
		automatonFixLoop:
		while(true){
			for(SchemaElement node1:automatonMerged){
				ComplexType complexTypeNode1 = node1.getType();
				for(SchemaElement node2:automatonMerged){
					boolean sameNameNamespaceButDifferentType = node1.equalsIgnoreType(node2)&&!node1.equals(node2);
					if(!sameNameNamespaceButDifferentType){
						continue;
					}
					ComplexType complexTypeNode2 = node2.getType();
					ComplexType childComplexTypeMerged;
					SchemaElement nodeMerged;
					if(mergedComplexTypeNames.containsKey(complexTypeNode1.getName())&&
							mergedComplexTypeNames.get(complexTypeNode1.getName()).contains(complexTypeNode2.getName())){
						childComplexTypeMerged=complexTypeNode1;
						nodeMerged=node1;
						automatonMerged.substituteNodes(ImmutableSet.of(node2), nodeMerged);
					}
					else if(mergedComplexTypeNames.containsKey(complexTypeNode2.getName())&&
							mergedComplexTypeNames.get(complexTypeNode2.getName()).contains(complexTypeNode1.getName())){
						childComplexTypeMerged=complexTypeNode2;
						nodeMerged=node2;
						automatonMerged.substituteNodes(ImmutableSet.of(node1), nodeMerged);
					} else{
						//We do this in this complicated way to avoid that updateComplexTypeSchemaDatastructures changes the type of node1 and node2 to the childComplexTypeMerged 
						//when mergeComplexTypes is called, so node1, node2 and nodeMerged become indistinguishable before the info or edges is properly merged 
						nodeMerged=new SchemaElement(node1.getName(), node1.getNamespace(), null);
						nodeMerged.setValidRoot(node1.isValidRoot()||node2.isValidRoot());
						automatonMerged.substituteNodes(ImmutableSet.of(node1, node2), nodeMerged);
						mergeComplexTypes(schema, configuration, attributeListComparator, 
								childrenPatternComparator, enumComparator, forceRemoveOriginals, 
								complexTypeNode1, complexTypeNode2);
						String childComplexTypeMergedName = getMergedTypeName(configuration,
								complexTypeNode1.getName(), complexTypeNode2.getName(),mergedComplexTypeNames);
						childComplexTypeMerged = schema.getComplexTypes().get(childComplexTypeMergedName);
						nodeMerged.setType(childComplexTypeMerged);
					}
					//
					continue automatonFixLoop;
				}
			}
			break;
		}
		//System.out.println("Ending automaton fix of: "+complexTypeMergedName);
		updateComplexTypeSchemaDatastructures(schema, complexType1, complexType2, complexTypeMerged, removeOriginals, attributeListComparator, childrenPatternComparator, configuration);
		
	}

//	/**
//	 * This method generates the name of a complex type which has been created by merging two other ones. 
//	 * The name is of the form: <br/>
//	 * <i>aComplexType{@link XSDInferenceConfiguration#getMergedTypesSeparator()}anotherComplexType</i><br/>
//	 * where the <i>aComplexType</i> and <i>anotherComplexType</i> are such that <i>aComplexType</i>.getName().compareTo(<i>anotherComplexType</i>.getName()) <= 0
//	 * @param configuration
//	 * @param complexTypeNode1
//	 * @param complexTypeNode2
//	 * @return
//	 */
//	public static String getComplexTypeMergedName(
//			XSDInferenceConfiguration configuration,
//			ComplexType complexTypeNode1, ComplexType complexTypeNode2) {
//		String result="";
//		if(complexTypeNode1.getName().compareTo(complexTypeNode2.getName())>0){
//			result=complexTypeNode2.getName() + configuration.getMergedTypesSeparator() + complexTypeNode1.getName();
//		}
//		else{
//			result=complexTypeNode1.getName() + configuration.getMergedTypesSeparator() + complexTypeNode2.getName();
//		}
//		return result;
//	}
	
	/**
	 * This method removes all the {@linkplain ComplexTypeStatisticsEntry} present at the {@linkplain Statistics} 
	 * object of a given {@linkplain Schema}, if the {@linkplain ComplexType} they are referring to does not exist. 
	 * This method is intended to be a cleaning method called after the types merge, assuming that the {@linkplain ComplexTypeStatisticsEntry} 
	 * of merged types have been already generated elsewhere. 
	 * @param schema The schema.
	 */
	private void cleanUnusedComplexTypeStatisticsEntries(Schema schema){
		Statistics statistics = schema.getStatistics();
		Set<ComplexType> currentComplexTypes = ImmutableSet.copyOf(schema.getComplexTypes().values());
		Set<ComplexType> complexTypesAtStatistics = ImmutableSet.copyOf(statistics.getComplexTypeInfo().keySet());
		Set<ComplexType> complexTypesToRemove = Sets.difference(complexTypesAtStatistics, currentComplexTypes);
		for(ComplexType complexTypeToRemove: complexTypesToRemove){
			statistics.getComplexTypeInfo().remove(complexTypeToRemove);
		}
	}

//	/**
//	 * DO NOT USE THIS METHOD, it has many errors.
//	 * It may be commented to be kept as a reference and may be deleted in the future. 
//	 * It merges the nodes present on the automaton of a merged complex types when they have the same name and namespace but different complex types.
//	 * @param schema the schema
//	 * @param configuration the inference configuration
//	 * @param attributeListComparator the current attribute list comparator
//	 * @param childrenPatternComparator the current children pattern comparator
//	 * @param enumComparator the current enumerations comparator
//	 * @param automaton1 the automaton of the first original complex type
//	 * @param automaton2 the automaton of the second original complex type
//	 * @param automatonMerged the automaton of the merged complex type
//	 * @param forceRemoveOriginals whether to force the removal of originals (it means, a complete merge) or not
//	 */
//	private void mergeNodesWithSameNameAndNamespaceButDifferentTypeOld(
//			Schema schema, XSDInferenceConfiguration configuration,
//			AttributeListComparator attributeListComparator,
//			ChildrenPatternComparator childrenPatternComparator,
//			EnumComparator enumComparator, ExtendedAutomaton automaton1,
//			ExtendedAutomaton automaton2, ExtendedAutomaton automatonMerged, boolean forceRemoveOriginals) {
//		for(SchemaElement node1: automaton1){
//			for(SchemaElement node2: automaton2){
//				if(node1.getNamespace().equals(DEFAULT_PSEUDOELEMENTS_NAMESPACE)||
//						node2.getNamespace().equals(DEFAULT_PSEUDOELEMENTS_NAMESPACE))
//					continue;
//				if(node1.getName().equals(node2.getName())&&
//						node1.getNamespace().equals(node2.getNamespace())&&
//						!node1.getType().equals(node2.getType())){
//					mergeComplexTypes(schema, configuration, attributeListComparator, childrenPatternComparator, enumComparator, forceRemoveOriginals, node1.getType(), node2.getType());
//					String childComplexTypeMergedName = node1.getType().getName()+
//							configuration.getMergedTypesSeparator()+
//							node2.getType().getName();
//					//If one or both nodes are of the merged complex type (because they are similar via comparators), 
//					//the merged complex type already exists and its name should be generated according to that.
//					List<String> sourcesOfType1 = mergedComplexTypeNames.get(node1.getType().getName());
//					List<String> sourcesOfType2 = mergedComplexTypeNames.get(node2.getType().getName());
//					if(node1.getType().equals(node2.getType())||(
//							sourcesOfType1!=null&&
//							sourcesOfType1.contains(node2.getType().getName()))){
//						childComplexTypeMergedName=node1.getType().getName();
//					} else if(sourcesOfType2!=null&&sourcesOfType2.contains(node1.getType().getName())){
//						childComplexTypeMergedName=node2.getType().getName();
//					}
//					
//					ComplexType nodeComplexTypeMerged=schema.getComplexTypes().get(childComplexTypeMergedName);
//					SchemaElement nodeNew = new SchemaElement(node1.getName(),node1.getNamespace(),nodeComplexTypeMerged);
//					nodeNew.setValidRoot(node1.isValidRoot()||node2.isValidRoot());
//					automatonMerged.substituteNodes(ImmutableSet.of(node1, node2), nodeNew);
//					Set<Table.Cell<Integer, SchemaElement, Integer>> entriesCopy = ImmutableSet.copyOf(automatonMerged.getSourceWordSymbolOccurrences().cellSet());				
//					for(Table.Cell<Integer, SchemaElement, Integer> entry: entriesCopy){
//						if((entry.getColumnKey().getName().equals(node1.getName())&&entry.getColumnKey().getNamespace().equals(node1.getNamespace()))||
//						   (entry.getColumnKey().getName().equals(node2.getName())&&entry.getColumnKey().getNamespace().equals(node2.getNamespace()))){
//							automatonMerged.getSourceWordSymbolOccurrences().remove(entry.getRowKey(), entry.getColumnKey());
//							automatonMerged.getSourceWordSymbolOccurrences().put(entry.getRowKey(), nodeNew, entry.getValue());
//						}
//					}
//				}
//				for(ComplexType complexType: schema.getComplexTypes().values()){
//					complexType.getAutomaton().rehashDatastrucures();
//				}
//				
//			}
//		}
//	}
	
	/**
	 * This method updates all the schema data structures when two complex types are merged.
	 * @param schema the schema
	 * @param complexType1 the first original complex type
	 * @param complexType2 the second original complex type
	 * @param complexTypeMerged the merged complex 
	 * @param removeOriginals if the original complex types are to be removed
	 * @param attributeListComparator the current attribute list comparator
	 * @param childrenPatternComparator the current children pattern comparator
	 * @param configuration inference configuration
	 */
	private void updateComplexTypeSchemaDatastructures(Schema schema, ComplexType complexType1, ComplexType complexType2, 
			ComplexType complexTypeMerged, boolean removeOriginals, AttributeListComparator attributeListComparator, 
			ChildrenPatternComparator childrenPatternComparator, XSDInferenceConfiguration configuration){
		
		
		//First, we add the complex types to the data structures
		schema.getComplexTypes().put(complexTypeMerged.getName(), complexTypeMerged);
		
		
		//These variables indicate whether the structures of any of the original complex types must be updated or not
		boolean update1=removeOriginals;
		boolean update2=removeOriginals;
		
		if(attributeListComparator!=null&&childrenPatternComparator!=null){
			boolean attributesCondition = attributeListComparator.compare(complexType1.getAttributeList(),complexType2.getAttributeList());
			boolean automatonCondition = childrenPatternComparator.compare(complexType1.getAutomaton(), complexType2.getAutomaton());
			
			update1=(update1)||attributesCondition&&automatonCondition;
			update2=(update2)||attributesCondition&&automatonCondition;
			update1=(update1)||(attributeListComparator.compare(complexType1.getAttributeList(),complexTypeMerged.getAttributeList())&&
					childrenPatternComparator.compare(complexType1.getAutomaton(), complexTypeMerged.getAutomaton()));
			update2=(update2)||(attributeListComparator.compare(complexType2.getAttributeList(),complexTypeMerged.getAttributeList())&&
					childrenPatternComparator.compare(complexType2.getAutomaton(), complexTypeMerged.getAutomaton()));
			if(!(attributesCondition&&automatonCondition)&&
				((attributeListComparator.compare(complexType1.getAttributeList(),complexTypeMerged.getAttributeList())&&
					childrenPatternComparator.compare(complexType1.getAutomaton(), complexTypeMerged.getAutomaton()))||
					(attributeListComparator.compare(complexType2.getAttributeList(),complexTypeMerged.getAttributeList())&&
							childrenPatternComparator.compare(complexType2.getAutomaton(), complexTypeMerged.getAutomaton())))){
		}
		}
		
		if(update1){
			replaceAndRemoveComplexType(schema,
					complexType1, complexTypeMerged);
		}
		
		if(update2){
			replaceAndRemoveComplexType(schema,
					complexType2, complexTypeMerged);
		}
		
		//Now, we update the schema element data structures with the info related to the new merged automaton
		for(SchemaElement currentElement:complexTypeMerged.getAutomaton()){
			if(currentElement.getNamespace().equals(DEFAULT_PSEUDOELEMENTS_NAMESPACE))
				continue;
			String schemaElementKey1 = complexType1.getName() + configuration.getTypeNamesAncestorsSeparator() + currentElement.getName();
			String schemaElementKey2 = complexType2.getName() + configuration.getTypeNamesAncestorsSeparator() + currentElement.getName();
			String schemaElementKeyMerged = complexTypeMerged.getName() + configuration.getTypeNamesAncestorsSeparator() + currentElement.getName();
			if(update1 && schema.getElements().contains(currentElement.getNamespace(),schemaElementKey1)){
				schema.getElements().remove(currentElement.getNamespace(), schemaElementKey1);
			}
				
			if(update2 && schema.getElements().contains(currentElement.getNamespace(),schemaElementKey2)){
				schema.getElements().remove(currentElement.getNamespace(), schemaElementKey2);
			}
			schema.getElements().put(currentElement.getNamespace(), schemaElementKeyMerged, currentElement);
		}
		removeUnusedSimpleTypes(schema);//To remove simple types which are unused because they are the originals from a merge caused by a merge of complex types
	}

	/**
	 * It replaces the given complexTypeOriginal on the schema elements data structures by the complexTypeMerged given.
	 * Then, it removes the complexTypeOriginal from the schema complex type data structures
	 * @param schema the schema
	 * @param complexTypeOriginal the original complex type
	 * @param complexTypeMerged the merged complex type
	 */
	private void replaceAndRemoveComplexType(Schema schema,
			ComplexType complexTypeOriginal, ComplexType complexTypeMerged) {
		for(String namespace: schema.getElements().rowKeySet()){
			for(String elementKey:schema.getElements().row(namespace).keySet()){
				SchemaElement currentElement = schema.getElements().get(namespace, elementKey);
				if(currentElement.getType().equals(complexTypeOriginal))
					currentElement.setType(complexTypeMerged);
			}
		}
		for(String complexTypeKey: ImmutableSet.copyOf(schema.getComplexTypes().keySet())){
			if(schema.getComplexTypes().get(complexTypeKey).equals(complexTypeOriginal)){
				schema.getComplexTypes().remove(complexTypeKey);
			}
		}
	}

	/**
	 * This method is to be called after a forced merge (i.e. NOT via comparators) between 
	 * two simple types if an enum comparator is defined. 
	 * If both source simple types are similar (according to the comparator), they are remapped 
	 * to the merged one as usual.
	 * Else, if any of the simple types is similar enough to the merged type, that type is remapped 
	 * to the merged one.
	 * Else, if none of the previous comparisons returns true, the merged type is added to the schema 
	 * structures and nothing more changes.
	 * @param schema the schema
	 * @param enumComparator the enumeration comparator
	 * @param simpleType1 the first source simple type
	 * @param simpleType2 the second source simple type
	 * @param simpleTypeMerged the merged simple type
	 */
	private void postStepAfterForcedSimpleTypeMerged(Schema schema,
			EnumComparator enumComparator, SimpleType simpleType1,
			SimpleType simpleType2, SimpleType simpleTypeMerged) {
		if(simpleType1==null && simpleType2==null){
			throw new NullPointerException();
		}
		if(enumComparator==null || (simpleType1==null && simpleType2!=null) || (simpleType1!=null && simpleType2==null)){
			schema.getSimpleTypes().put(simpleTypeMerged.getName(),simpleTypeMerged);
			return;
		}
		if(enumComparator.compare(simpleType1, simpleType2)){
//									String simpleTypeMergedKey =simpleTypeMerged.getName();
			replaceSimpleTypes(schema, simpleType1,
					simpleTypeMerged);
			replaceSimpleTypes(schema, simpleType2,
					simpleTypeMerged);
		}
		else{
			if(enumComparator.compare(simpleType1, simpleTypeMerged)){
				replaceSimpleTypes(schema, simpleType1,
						simpleTypeMerged);
			}
			else if(enumComparator.compare(simpleType2, simpleTypeMerged)){
				replaceSimpleTypes(schema, simpleType2,
						simpleTypeMerged);
			}
			else {
				//This step is done on every substitution, so if there is no substitution
				//it must be manually done
				schema.getSimpleTypes().put(simpleTypeMerged.getName(),simpleTypeMerged);
			}
		}
	}

	/**
	 * Updates the SchemaAttribute data structures on a given schema after a type merge.
	 * @param schema the schema
	 * @param configuration the inference configuration
	 * @param removeOriginals whether the original complex types are going to be removed or not.
	 * @param complexType1Name name of the first original complex type
	 * @param complexType2Name name of the second original complex type
	 * @param complexTypeMergedName the name of the merged complex type
	 * @param attributeList1 attribute list of complex type one
	 * @param attributeList2 attribute list of complex type two
	 * @param attributeListMerged attribute list of merged complex
	 * @param enumComparator the enumeration comparator, in order to merge completely the attributes lists if types are enough similar but the merge has not been started by a comparison
	 */
	private void updateSchemaAttributes(Schema schema,
			XSDInferenceConfiguration configuration, boolean removeOriginals,
			String complexType1Name, String complexType2Name,
			String complexTypeMergedName,
			List<SchemaAttribute> attributeList1, List<SchemaAttribute> attributeList2, List<SchemaAttribute> attributeListMerged, EnumComparator enumComparator) {
		

		for(SchemaAttribute schemaAttrMerged:attributeListMerged){
			String schemaAttrKeyMerged = complexTypeMergedName + configuration.getTypeNamesAncestorsSeparator() + schemaAttrMerged.getName();
			//Buscar solo por nombre y namespace
			int indexAtList1 = SchemaAttribute.indexOf(attributeList1,schemaAttrMerged);
			SchemaAttribute schemaAttr1 = indexAtList1>=0?attributeList1.get(indexAtList1):null;
			int indexAtList2 = SchemaAttribute.indexOf(attributeList2,schemaAttrMerged);
			SchemaAttribute schemaAttr2 = indexAtList2>=0?attributeList2.get(indexAtList2):null;
			String schemaAttrKey1 = null;
			if(indexAtList1>=0)
				schemaAttrKey1 = complexType1Name + configuration.getTypeNamesAncestorsSeparator() + schemaAttr1.getName();
			String schemaAttrKey2=null;
			if(indexAtList2>=0)
				schemaAttrKey2 = complexType2Name + configuration.getTypeNamesAncestorsSeparator() + schemaAttr2.getName();
			if(removeOriginals){
				if(indexAtList1>=0)
					schema.getAttributes().remove(schemaAttr1.getNamespace(), schemaAttrKey1);
				if(indexAtList2>=0)
					schema.getAttributes().remove(schemaAttr2.getNamespace(), schemaAttrKey2);
			}
			
			schema.getAttributes().put(schemaAttrMerged.getNamespace(), schemaAttrKeyMerged, schemaAttrMerged);
			SimpleType schemaAttr1SimpleType = schemaAttr1!=null?schemaAttr1.getSimpleType():null;
			SimpleType schemaAttr2SimpleType = schemaAttr2!=null?schemaAttr2.getSimpleType():null;
			if(removeOriginals){
				if (indexAtList1>=0) {
					SimpleType simpleType = schemaAttr1SimpleType;
					//schema.getSimpleTypes().remove(simpleType.getName());
					replaceSimpleTypes(schema, simpleType, schemaAttrMerged.getSimpleType());
				}
				if (indexAtList2>=0) {
					SimpleType simpleType = schemaAttr2SimpleType;
					//schema.getSimpleTypes().remove(simpleType.getName());
					replaceSimpleTypes(schema, simpleType, schemaAttrMerged.getSimpleType());
				}
				SimpleType simpleTypeMerged=schemaAttrMerged.getSimpleType();
				schema.getSimpleTypes().put(simpleTypeMerged.getName(), simpleTypeMerged);
			}
			else{
				postStepAfterForcedSimpleTypeMerged(schema, enumComparator, schemaAttr1SimpleType, schemaAttr2SimpleType, schemaAttrMerged.getSimpleType());
			}
		}
		
	}
	
	/**
	 * Returns an attribute list made by mixing the attribute list of two complex types
	 * @param schema the schema
	 * @param complexType1 first complex type
	 * @param complexType2 second complex type
	 * @param configuration the inference configuration
	 * @param enumComparator the enumeration comparator
	 * @return the merged attribute list
	 */
	private List<SchemaAttribute> getMergedAttributeList(Schema schema,
			ComplexType complexType1, ComplexType complexType2, XSDInferenceConfiguration configuration, EnumComparator enumComparator) {
		//First, we merge the attribute lists
		List<SchemaAttribute> attributeListMerged = new ArrayList<SchemaAttribute>(complexType1.getAttributeList().size()+complexType2.getAttributeList().size());
		List<SchemaAttribute> attributeList1=complexType1.getAttributeList();
		List<SchemaAttribute> attributeList2=complexType2.getAttributeList();
		
		//Add all the attributes of the first complex type 
	//							attributeListMerged.addAll(attributeList1);
		for(int i=0;i<attributeList1.size();i++){
			SchemaAttribute schemaAttr = new SchemaAttribute(attributeList1.get(i));
			attributeListMerged.add(schemaAttr);
			//If an attribute of the first complex type is not present at the second one, 
			//we must mark it as optional
			if(!attributeList2.contains(schemaAttr)){
				schemaAttr.setOptional(true);
			}else{
				SchemaAttribute schemaAttr2 = attributeList2.get(attributeList2.indexOf(schemaAttr));
				boolean optionalNew=schemaAttr.isOptional()||schemaAttr2.isOptional();
				SimpleType simpleTypeNew=getMergedSimpleType(schema, configuration, schemaAttr.getSimpleType(), schemaAttr2.getSimpleType());
				schemaAttr.setOptional(optionalNew);
				schemaAttr.setSimpleType(simpleTypeNew);
				
//				schema.getSimpleTypes().put(simpleTypeNew.getName(), simpleTypeNew);
			}
		}
		
		//Now, we add all the attributes of the second complex type which are not present 
		//at the first one
		for(int k=0;k<attributeList2.size();k++){
			SchemaAttribute schemaAttr = new SchemaAttribute(attributeList2.get(k));
			if(!attributeListMerged.contains(schemaAttr)){
				schemaAttr.setOptional(true);
				attributeListMerged.add(schemaAttr);
			}
		}
		return attributeListMerged;
	}
	
	/**
	 * It merges two simple types
	 * @param schema the current {@link Schema}
	 * @param configuration the inference configuration
	 * @param simpleType1 one simple type
	 * @param simpleType2 other simple type
	 * @return the merged simple type
	 */
	private SimpleType getMergedSimpleType(Schema schema,
			XSDInferenceConfiguration configuration, SimpleType simpleType1, SimpleType simpleType2) {
		//System.out.println("Merging simple types: " + simpleType1.getName() + " and "+ simpleType2.getName());
		String simpleType1Name = simpleType1.getName();
		String simpleType2Name = simpleType2.getName();
		String simpleTypeMergedName = getMergedTypeName(configuration,
				simpleType1Name, simpleType2Name, mergedSimpleTypeNames);
		//We should avoid re-merge
//		if(mergedSimpleTypeNames.containsKey(simpleType1.getName())&&(
//				mergedSimpleTypeNames.get(simpleType1.getName()).contains(simpleType2.getName())||
//				mergedSimpleTypeNames.get(simpleType1.getName()).containsAll(mergedSimpleTypeNames.get(simpleType2.getName()))))
//			return simpleType1;
//		if(mergedSimpleTypeNames.containsKey(simpleType2.getName())&&(
//				mergedSimpleTypeNames.get(simpleType2.getName()).contains(simpleType1.getName())||
//				mergedSimpleTypeNames.get(simpleType2.getName()).containsAll(mergedSimpleTypeNames.get(simpleType1.getName()))))
//			return simpleType2;
		SimpleTypeInferencer simpleTypeInferencer= InferencersFactory.getInstance().getSimpleTypeInferencerInstance(simpleTypeMergedName, configuration);
		for(String value:simpleType1)
			simpleTypeInferencer.learnValue(value, null, null);
		for(String value:simpleType2)
			simpleTypeInferencer.learnValue(value, null, null);
		SimpleType simpleTypeMerged=simpleTypeInferencer.getSimpleType(simpleTypeMergedName);
		simpleTypeMerged.addAllTheSourceNodeNamespaceAndNames(simpleType1);
		simpleTypeMerged.addAllTheSourceNodeNamespaceAndNames(simpleType2);
		registerTypeMerge(simpleType1Name, simpleType2Name,
				simpleTypeMergedName, mergedSimpleTypeNames);
		return simpleTypeMerged;
	}

	/**
	 * This method registers correctly that a typeMerged comes from a type1 and a type2
	 * @param type1Name
	 * @param type2Name
	 * @param typeMergedName
	 */
	private void registerTypeMerge(String type1Name, String type2Name,
			String typeMergedName, Map<String,List<String>> mergedTypeNames) {
		List<String> sourceSimpleTypeNames = new ArrayList<>();
		if(mergedTypeNames.containsKey(type1Name)){
			sourceSimpleTypeNames.addAll(mergedTypeNames.get(type1Name));
		} else {
			sourceSimpleTypeNames.add(type1Name);
		}
		if(mergedTypeNames.containsKey(type2Name)){
			sourceSimpleTypeNames.addAll(mergedTypeNames.get(type2Name));
		} else {
			sourceSimpleTypeNames.add(type2Name);
		}
		sourceSimpleTypeNames=new ArrayList<>(ImmutableSet.copyOf(sourceSimpleTypeNames));
		Collections.sort(sourceSimpleTypeNames);
		mergedTypeNames.put(typeMergedName, sourceSimpleTypeNames);
	}

	/**
	 * Method that builds the type of two merged types (which may be simple or complex).
	 * This methods avoids problems with merged types which come from already merged types 
	 * which share source types (for example, if "type1_and_type2", which comes from "type1" 
	 * and "type2", is merged with "type2_and_type3", which comes from "type2" and "type3", 
	 * the result is "type1_and_type2_and_type3"
	 * @param configuration the inference configuration
	 * @param type1Name the name of the first type
	 * @param type2Name the name of the second type
	 * @param mergedTypeNames a map between the already merged type names and lists with their source 
	 * 					      type names
	 * @return the merged type name
	 */
	public static String getMergedTypeName(XSDInferenceConfiguration configuration,
			String type1Name, String type2Name, Map<String, List<String>> mergedTypeNames) {
		List<String> typeNameTokens = new ArrayList<>();
		if(mergedTypeNames.containsKey(type1Name)){
			typeNameTokens.addAll(mergedTypeNames.get(type1Name));
		} else {
			typeNameTokens.add(type1Name);
		}
		if(mergedTypeNames.containsKey(type2Name)){
			typeNameTokens.addAll(mergedTypeNames.get(type2Name));
		} else {
			typeNameTokens.add(type2Name);
		}
		typeNameTokens=new ArrayList<>(ImmutableSet.copyOf(typeNameTokens));
		Collections.sort(typeNameTokens);
		String typeMergedName=Joiner.on(configuration.getMergedTypesSeparator()).join(typeNameTokens);
		return typeMergedName;
	}
	
	/**
	 * Method that builds the type of two merged types (which may be simple or complex).
	 * This method is suitable only for source types which do not come from 
	 * other merges. Please use {@link #getMergedTypeName(XSDInferenceConfiguration, String, String, Map)} instead.
	 * @param configuration the inference configuration
	 * @param type1Name the name of the first type
	 * @param type2Name the name of the second type
	 * @return the merged type name
	 * @deprecated This method is only suitable for source types which 
	 * do not come from another merge. Please use {@link #getMergedTypeName(XSDInferenceConfiguration, String, String, Map)} instead.
	 */
	@Deprecated
	public static String getMergedTypeName(XSDInferenceConfiguration configuration,
			String type1Name, String type2Name){
		List<String> typeNameTokens = new ArrayList<>();
		typeNameTokens.add(type1Name);
		typeNameTokens.add(type2Name);
		typeNameTokens=new ArrayList<>(ImmutableSet.copyOf(typeNameTokens));
		Collections.sort(typeNameTokens);
		String typeMergedName=Joiner.on(configuration.getMergedTypesSeparator()).join(typeNameTokens);
		return typeMergedName;
	}
	
	/**
	 * Replaces completely a provided simple type with another one in a given schema.
	 * It includes all the simple types of elements and attributes and the schema data structures.
	 * @param schema the schema
	 * @param simpleTypeOriginal the original simple type
	 * @param simpleTypeNew the new simple type
	 */
	private void replaceSimpleTypes(Schema schema, SimpleType simpleTypeOriginal,
			SimpleType simpleTypeNew) {
		for(String complexTypeName: schema.getComplexTypes().keySet()){
			ComplexType complexType=schema.getComplexTypes().get(complexTypeName);
			if(complexType.getTextSimpleType().equals(simpleTypeOriginal))
				complexType.setTextSimpleType(simpleTypeNew);
			for(SchemaAttribute attr:complexType.getAttributeList()){
				if(attr.getSimpleType().equals(simpleTypeOriginal))
					attr.setSimpleType(simpleTypeNew);
			}
			if(complexType.getTextSimpleType().equals(simpleTypeOriginal))
				complexType.setTextSimpleType(simpleTypeNew);
		}
		schema.getSimpleTypes().put(simpleTypeNew.getName(), simpleTypeNew);
		schema.getSimpleTypes().remove(simpleTypeOriginal.getName());
	}
}
