package es.upm.dit.xsdinferencer.merge.mergerimpl.attribute;

import java.util.List;

import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.merge.AttributeListComparator;

/**
 * Attribute list comparator that always returns true. It is the same than ignoring attributes 
 * while merging types.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class MergeAttributeComparator implements AttributeListComparator {

	/**
	 * @see AttributeListComparator#compare(List, List)
	 */
	@Override
	public boolean compare(List<SchemaAttribute> attrList1,
			List<SchemaAttribute> attrList2) {
		return true;
	}
}
