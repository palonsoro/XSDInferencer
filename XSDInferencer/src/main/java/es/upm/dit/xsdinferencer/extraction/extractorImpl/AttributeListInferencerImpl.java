package es.upm.dit.xsdinferencer.extraction.extractorImpl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static es.upm.dit.xsdinferencer.XSDInferenceConfiguration.XSI_NAMESPACE_URI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.extraction.AttributeListInferencer;
import es.upm.dit.xsdinferencer.extraction.SimpleTypeInferencer;
import es.upm.dit.xsdinferencer.statistics.ComplexTypeStatisticsEntry;
import es.upm.dit.xsdinferencer.statistics.Statistics;

/**
 * Default implementation for {@link AttributeListInferencer}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class AttributeListInferencerImpl implements AttributeListInferencer {
	
	/**
	 * Maps each known attribute with its SimpleTypeInferencer
	 */
	private Map<SchemaAttribute,SimpleTypeInferencer> knownAttributes;
	
	/**
	 * Name of the complex type that would contain the attribute list.
	 * Necessary in order to build some simple type names.
	 */
	private String parentComplexTypeName;
	
	/**
	 * Current inference configuration. The simple type inferencer is determined by this.
	 */
	private XSDInferenceConfiguration config;
	
	/**
	 * Flag that indicates whether the {@linkplain AttributeListInferencerImpl#learnAttributeList(List, int)} has been called at least once or not.
	 */
	private boolean firstTime;
	
	/**
	 * Statistics entry of the enclosing complex type
	 */
	private ComplexTypeStatisticsEntry complexTypeStatisticsEntry;
	
	/**
	 * Statistics
	 */
	private Statistics statistics;
	
	/**
	 * Solved mappings
	 */
	private Map<String, String> solvedNamespaceToPrefixMapping;
	
	/**
	 * Constructor.
	 * @param parentComplexTypeName the name of the complex type that encloses this attribute list
	 * @param statistics the statistics. IMPORTANT: The parent complex type MUST have an entry on the statistics BEFORE the inferencer is created.
	 * @param solvedNamespaceToPrefixMapping solved namespace URIs to prefixes mapping. Necessary to build some names correctly.
	 * @param configuration the current inference configuration
	 * @throws IllegalArgumentException if the statistics have not an entry for the given complexType
	 */
	public AttributeListInferencerImpl(String parentComplexTypeName, XSDInferenceConfiguration config, Statistics statistics, Map<String, String> solvedNamespaceToPrefixMapping) {
		checkNotNull(parentComplexTypeName);
		checkNotNull(config);
		checkNotNull(statistics);
		checkNotNull(solvedNamespaceToPrefixMapping,"'solvedNamespaceToPrefixMapping' must not be null");
		this.parentComplexTypeName = parentComplexTypeName;
		this.firstTime=true;
		this.config=config;
		this.statistics=statistics;
		this.solvedNamespaceToPrefixMapping=solvedNamespaceToPrefixMapping;
		this.complexTypeStatisticsEntry=statistics.getComplexTypeStatisticsEntryByName(parentComplexTypeName);
		checkArgument(this.complexTypeStatisticsEntry!=null, "The complex type statistics entry of the given parent complex type '"+parentComplexTypeName+"' has been not found.");
		this.knownAttributes=new HashMap<SchemaAttribute, SimpleTypeInferencer>();
	}
	
	/**
	 * @see Object#clone()
	 */
	@Override
	public Object clone(){
		AttributeListInferencerImpl copy = new AttributeListInferencerImpl(this.parentComplexTypeName, this.config, statistics, this.solvedNamespaceToPrefixMapping);
		copy.firstTime=this.firstTime;
		copy.knownAttributes=new HashMap<>(this.knownAttributes);
		return copy;
	}

	/**
	 * Helper method that, given a name and a namespace, looks for the SchemaAttribute object that represents 
	 * that known attribute, if it was already known. If it is not known, null is returned.
	 * @param namespace the namespace of the attribute to search
	 * @param name the name of the attribute to search
	 * @return the SchemaAttribute object of the attribute searched or null, if it is not known
	 */
	private SchemaAttribute searchSchemaAttribute(String namespace, String name){
		for(SchemaAttribute attr: knownAttributes.keySet()){
			if(attr.getNamespace().equals(namespace)&&attr.getName().equals(name)){
				return attr;
			}
		}
		return null;
	}

	/**
	 * @see AttributeListInferencer#learnAttributeList(List, int)
	 */
	@Override
	public void learnAttributeList(List<Attribute> attrList, int documentIndex) {
		checkNotNull(attrList,"'attrList' must not be null");
		if(!firstTime){
			//First, we mark as optional any known attribute which does not reoccur
			for(SchemaAttribute schemaAttribute: knownAttributes.keySet()){
				boolean found = false;
				for(Attribute attribute:attrList){
					if(attribute.getName().equals(schemaAttribute.getName())&&attribute.getNamespace().getURI().equals(schemaAttribute.getNamespace()))
						found=true;
				}
				if(!found)
					schemaAttribute.setOptional(true);
			}
		}
		//Now, we learn the information given by this list of atttributes
		for(Attribute attribute:attrList){
			if(attribute.getNamespaceURI().equals(XSI_NAMESPACE_URI))
				continue;//Attributes in the XSI namespace are not extracted.
			SchemaAttribute schemaAttribute = searchSchemaAttribute(attribute.getNamespaceURI(), attribute.getName());
			//New attribute
			if(schemaAttribute==null){
				schemaAttribute= new SchemaAttribute(attribute.getName(), attribute.getNamespaceURI(), true, new SimpleType(""));
				if(firstTime)
					schemaAttribute.setOptional(false);
				SimpleTypeInferencer simpleTypeInferencer = InferencersFactory.getInstance().getSimpleTypeInferencerInstance(schemaAttribute.getNamespace()+config.getTypeNamesAncestorsSeparator()+schemaAttribute.getName(), config);
				simpleTypeInferencer.learnValue(attribute.getValue(), attribute.getNamespaceURI(), "@"+attribute.getName());
				knownAttributes.put(schemaAttribute,simpleTypeInferencer);
			}
			//Already known attribute
			else{
				knownAttributes.get(schemaAttribute).learnValue(attribute.getValue(),attribute.getNamespaceURI(), "@"+attribute.getName());
			}
			complexTypeStatisticsEntry.registerAttributeOccurrenceInfoCount(schemaAttribute, documentIndex);
			complexTypeStatisticsEntry.registerValueOfNodeCount(attribute.getValue(), schemaAttribute, documentIndex);
			String realPathFiltered=TypesExtractorImpl.filterAndJoinRealPath(TypesExtractorImpl.getRealPathOfAttributeUnfiltered(attribute, config, solvedNamespaceToPrefixMapping));
			statistics.registerAttributeOccurrenceAtPathCount(realPathFiltered, documentIndex);
			statistics.registerValueAtPathCount(realPathFiltered, attribute.getValue(), documentIndex);
		}
		firstTime=false;
	}
	
	/**
	 * For each known attribute, it infers its simple type via its simple type inferencer.
	 */
	private void inferAllSimpleTypes(){
		for(SchemaAttribute schemaAttribute: knownAttributes.keySet()){
			SimpleTypeInferencer simpleTypeInferencer = knownAttributes.get(schemaAttribute);
//			SimpleType simpleType = simpleTypeInferencer.getSimpleType(parentComplexTypeName+config.getTypeNamesAncestorsSeparator()+
//					"@"+solvedNamespaceToPrefixMapping.get(schemaAttribute.getNamespace())+"_"+schemaAttribute.getName());
			SimpleType simpleType = simpleTypeInferencer.getSimpleType(parentComplexTypeName+config.getTypeNamesAncestorsSeparator()+
					   solvedNamespaceToPrefixMapping.get(schemaAttribute.getNamespace())+"_"+schemaAttribute.getName()+"-SimpleTypeOfAttribute");
			schemaAttribute.setSimpleType(simpleType);
		}
	}

	/**
	 * @see AttributeListInferencer#getAttributesList()
	 */
	@Override
	public List<SchemaAttribute> getAttributesList() {
		inferAllSimpleTypes(); //First, we infer the simple type of each attribute
		return new ArrayList<SchemaAttribute>(knownAttributes.keySet());
	}
}
