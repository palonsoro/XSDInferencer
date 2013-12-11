package es.upm.dit.xsdinferencer.util.guavapredicates;

import com.google.common.base.Predicate;

import es.upm.dit.xsdinferencer.datastructures.EquivalenceClass;

/**
 * Guava predicates related to {@link EquivalenceClass}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 * @see EquivalenceClass
 * @see Predicate
 */
public class EquivalenceClassPredicates {
	
	/**
	 * Singletion instance for {@link Predicate} {@link EquivalenceClassPredicates#isSinglenton()}
	 */
	private static final Predicate<EquivalenceClass> PREDICATE_IS_SINGLETON = new Predicate<EquivalenceClass>() {
		@Override
		public boolean apply(EquivalenceClass eqClass){
			return(eqClass.size()==1);
		}
	};
	
	/**
	 * Guava {@link Predicate} that returns true if the {@link EquivalenceClass} is a singleton
	 * @return
	 */
	public static Predicate<EquivalenceClass> isSinglenton(){
		return PREDICATE_IS_SINGLETON;
	}
}
