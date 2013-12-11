package es.upm.dit.xsdinferencer.extraction;

import java.util.List;

import org.jdom2.Attribute;

import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;

/**
 * An AttributeListInferencer learns lists of attributes of elements that belong to a ComplexType and generates a 
 * {@linkplain List} of {@linkplain SchemaAttribute} objects with the correct names, namespaces, simple types and optionality (simple 
 * types are inferred via the configured inferencer).
 * IMPORTANT REMARK: The inferencer is intended to be still valid after calling {@link AttributeListInferencer#getAttributesList()}, 
 * it means, it must be possible to learn new attribute list in order to infer another (probably different) 
 * attribute list. 
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public interface AttributeListInferencer extends Cloneable{
	
	/**
	 * Learns a new list of attributes present on an element of the given complex type.
	 * @param attrList a list of JDOM2 attributes
	 * @param documentIndex the index of the document where the list comes from
	 */
	public void learnAttributeList(List<Attribute> attrList, int documentIndex);
	
	/**
	 * Gets the current list of attributes of the inferencer.
	 * @return the list of attributes.
	 */
	public List<SchemaAttribute> getAttributesList();
	
//	/**
//	 * This allows the inferencer to be cloned
//	 * @return a clone
//	 */
//	public Object clone();
}
