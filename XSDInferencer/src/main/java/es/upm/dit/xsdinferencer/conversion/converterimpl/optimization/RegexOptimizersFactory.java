package es.upm.dit.xsdinferencer.conversion.converterimpl.optimization;

import java.util.ArrayList;
import java.util.List;

import es.upm.dit.xsdinferencer.conversion.RegexOptimizer;

/**
 * Factory for the different implementations of {@link RegexOptimizer}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class RegexOptimizersFactory {
	
	/**
	 * Value to choose a {@link ChoiceOptimizer}
	 */
	public static final String VALUE_OPTIMIZERS_CHOICE = "choiceOptimizer";
	
	/**
	 * Value to choose an {@link EmptyChildOptimizer}
	 */
	public static final String VALUE_OPTIMIZERS_EMPTYCHILD = "emptyChildOptimizer";
	
	/**
	 * Value to choose an {@link EmptyOptimizer}
	 */
	public static final String VALUE_OPTIMIZERS_EMPTY = "emptyOptimizer";
	
	/**
	 * Value to choose a {@link SingularRegularExpressionOptimizer}
	 */
	public static final String VALUE_OPTIMIZERS_SINGULAR_REGULAR_EXPRESSION = "singularRegularExpressionOptimizer";
	
	/**
	 * Value to choose a {@link SequenceOptimizer}
	 */
	public static final String VALUE_OPTIMIZERS_SEQUENCE = "sequenceOptimizer";
	
	/**
	 * Value to choose a {@link SingletonOptimizer}
	 */
	public static final String VALUE_OPTIMIZERS_SINGLETON = "singletonOptimizer";
	
	/**
	 * Singleton instance
	 */
	private static RegexOptimizersFactory singletonInstance = null;
	
	/**
	 * Method that returns (and creates when necessary) the singleton instance
	 * @return the singleton instance
	 */
	public static RegexOptimizersFactory getInstance(){
		if(singletonInstance==null)
			singletonInstance=new RegexOptimizersFactory();
		return singletonInstance;
	}
	
	/**
	 * Method that returns the default optimizers list, which contains one of each implemented optimizer
	 * @return a list with the default optimizers
	 */
	public List<RegexOptimizer> getDefaultOptimizersList(){
		List<RegexOptimizer> optimizers = new ArrayList<>(6);
		optimizers.add(new ChoiceOptimizer());
		optimizers.add(new EmptyChildOptimizer());
		optimizers.add(new EmptyOptimizer());
		optimizers.add(new SingularRegularExpressionOptimizer());
		optimizers.add(new SequenceOptimizer());
		optimizers.add(new SingletonOptimizer());
		return optimizers;
	}
	
	/**
	 * This method returns a concrete implementation of {@link RegexOptimizer} depending on the input value. 
	 * Valid values are specified as constants at this class.
	 * @param value the value used to choose the implementation to return
	 * @return an instance of the desired optimizer
	 * @throws IllegalArgumentException if the value passed as input is unknown
	 */
	public RegexOptimizer getRegexOptimizerInstance(String value){
		if(value.equals(VALUE_OPTIMIZERS_CHOICE)) {
			return new ChoiceOptimizer();
		} 
		else if(value.equals(VALUE_OPTIMIZERS_EMPTYCHILD)) { 
			return new EmptyChildOptimizer();
		} 
		else if(value.equals(VALUE_OPTIMIZERS_EMPTY)) { 
			return new EmptyOptimizer();
		} 
		else 
		if(value.equals(VALUE_OPTIMIZERS_SINGULAR_REGULAR_EXPRESSION)) { 
			return new SingularRegularExpressionOptimizer();
		}
		else if(value.equals(VALUE_OPTIMIZERS_SEQUENCE)) { 
			return new SequenceOptimizer();
		} 
		else if(value.equals(VALUE_OPTIMIZERS_SINGLETON)) { 
			return new SingletonOptimizer();
		} 
		else {
			throw new IllegalArgumentException();
		}
	}
}
