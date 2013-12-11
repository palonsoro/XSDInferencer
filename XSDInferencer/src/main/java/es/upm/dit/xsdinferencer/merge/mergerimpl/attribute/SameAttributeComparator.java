package es.upm.dit.xsdinferencer.merge.mergerimpl.attribute;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Collections2;

import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.merge.AttributeListComparator;
import es.upm.dit.xsdinferencer.util.guavapredicates.SchemaAttributePredicates;

/**
 * Attribute list comparator that returns true if two attribute list have the same required attributes.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class SameAttributeComparator implements AttributeListComparator {

	/**
	 * @see AttributeListComparator#compare(List, List)
	 */
	@Override
	public boolean compare(List<SchemaAttribute> attrList1,
			List<SchemaAttribute> attrList2) {
		Set<SchemaAttribute> required1 = new HashSet<SchemaAttribute>(Collections2.filter(attrList1, SchemaAttributePredicates.isRequired()));
		Set<SchemaAttribute> required2 = new HashSet<SchemaAttribute>(Collections2.filter(attrList2, SchemaAttributePredicates.isRequired()));
		return required1.equals(required2);
	}
}
