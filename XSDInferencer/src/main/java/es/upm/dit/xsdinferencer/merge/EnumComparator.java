
package es.upm.dit.xsdinferencer.merge;

import es.upm.dit.xsdinferencer.datastructures.SimpleType;

/**
 * Comparator that checks whether two {@link SimpleType} are similar enough. 
 * It only should be used if both complex types are enumerations.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface EnumComparator {
	/**
	 * Method that performs the comparison
	 * @param simpleType1 one simple type
	 * @param simpleType2 another simple type
	 * @return true if both simple types are similar, false otherwise.
	 */
	public boolean compare(SimpleType simpleType1, SimpleType simpleType2);
}
