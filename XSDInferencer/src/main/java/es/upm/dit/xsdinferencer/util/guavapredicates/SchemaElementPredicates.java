package es.upm.dit.xsdinferencer.util.guavapredicates;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;

/**
 * Provides some useful custom predicates (see {@link Predicate}) to filter element lists.
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 * @see Predicate
 * @see Collections2#filter(java.util.Collection, Predicate)
 */
public class SchemaElementPredicates {
	
	/**
	 * Singleton field for {@link Predicate} {@link SchemaElementPredicates#isValidRoot()}
	 */
	private static final Predicate<SchemaElement> PREDICATE_IS_VALID_ROOT=new Predicate<SchemaElement>() {
		@Override
		public boolean apply(SchemaElement schemaElement){
			return schemaElement.isValidRoot();
		}
	};
	
	/**
	 * {@link Predicate} that returns true if the element is a valid root.
	 * @return the described {@link Predicate} 
	 */
	public static Predicate<SchemaElement> isValidRoot(){
		return PREDICATE_IS_VALID_ROOT;
	}
	
	//This predicate could not be built as an anonymous class because it is necessary to pass a ComplexType via a constructor. 
	/**
	 * It builds a predicate that returns true if the type of a {@link SchemaElement} is the one 
	 * provided at this method.
	 * @param complexType the complex type to which elements will be compared
	 * @return the desired predicate
	 * @see SchemaElementComplexTypePredicate
	 */
	public static Predicate<SchemaElement> complexTypeEquals(ComplexType complexType){
		return new SchemaElementComplexTypePredicate(complexType);
	}
}
