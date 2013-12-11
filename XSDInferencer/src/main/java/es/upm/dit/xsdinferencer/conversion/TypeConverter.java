package es.upm.dit.xsdinferencer.conversion;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex.RegexConvertersFactory;
import es.upm.dit.xsdinferencer.conversion.converterimpl.optimization.RegexOptimizersFactory;
import es.upm.dit.xsdinferencer.datastructures.Schema;

/**
 * Implementations of this converter convert all the automatons of all the complex types present 
 * on a {@link Schema} into regular expressions and optimizes them, so that they become suitable for 
 * being represented on XSD documents.
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 * 
 */
public interface TypeConverter {
	/**
	 * This method does the same than {@link TypeConverter#converTypes(Schema, XSDInferenceConfiguration, RegexConvertersFactory, RegexOptimizersFactory)} 
	 * by using default factories
	 * @param schema the schema
	 * @param configuration the inference configuration
	 */
	public void converTypes(Schema schema, XSDInferenceConfiguration configuration);
	
	/**
	 * This method reads all the complex types of the schema (as returned by {@link Schema#getComplexTypes()}), 
	 * takes each automaton, converts it to a regular expression, optimizes it and stores the result on the complex 
	 * type (the automaton info is preserved).
	 * @param schema the schema
	 * @param configuration the inference configuration
	 * @param regexConvertersFactory factory used to build the regex converters
	 * @param regexOptimizersFactory factory used to build the regex optimizers
	 */
	public void converTypes(Schema schema, XSDInferenceConfiguration configuration, RegexConvertersFactory regexConvertersFactory, RegexOptimizersFactory regexOptimizersFactory);
}
