
package es.upm.dit.xsdinferencer.merge;

import java.util.List;

import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;

/**
 * Comparator that checks whether the attribute lists from two complex types are similar enough. 
 * (Note that order is not relevant).
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface AttributeListComparator {
	/**
	 * Method that performs the comparison
	 * @param attrList1 an attribute list
	 * @param attrList2 another attribute list
	 * @return
	 */
	public boolean compare(List<SchemaAttribute> attrList1, List<SchemaAttribute> attrList2);
}
