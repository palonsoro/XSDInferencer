package es.upm.dit.xsdinferencer.util.guavapredicates;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;


/**
 * Provides some useful custom predicates (see {@link Predicate}) to filter attribute lists.
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 * @see Predicate
 * @see Collections2#filter(java.util.Collection, Predicate)
 */
public class SchemaAttributePredicates {
	
	/**
	 * Returns true if the attribute is required
	 */
	private static final Predicate<SchemaAttribute> PREDICATE_REQUIRED = new Predicate<SchemaAttribute> () {
		@Override
		public boolean apply(SchemaAttribute input){
			return (input.isOptional()==false);
		}
	};
	
	/**
	 * Returns true if the attribute is optional
	 */
	private static final Predicate<SchemaAttribute> PREDICATE_OPTIONAL = new Predicate<SchemaAttribute> () {
		@Override
		public boolean apply(SchemaAttribute input){
			return (input.isOptional()==true);
		}
	};

	/**
	 * Returns a {@linkplain Predicate} that returns true for those {@linkplain SchemaAttribute} 
	 * whose method {@linkplain SchemaAttribute#isOptional()} returns false.
	 * @return the predicate
	 */
	public static Predicate<SchemaAttribute> isRequired(){
		return PREDICATE_REQUIRED;
	}
	
	/**
	 * Returns a {@linkplain Predicate} that returns true for those {@linkplain SchemaAttribute} 
	 * whose method {@linkplain SchemaAttribute#isOptional()} returns true.
	 * @return the predicate
	 */
	public static Predicate<SchemaAttribute> isOptional(){
		return PREDICATE_OPTIONAL;
	}
}
			