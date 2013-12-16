/*
Copyright 2013 Universidad Politécnica de Madrid - Center for Open Middleware (http://www.centeropenmiddleware.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package es.upm.dit.xsdinferencer.conversion.converterimpl;

import java.util.List;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.conversion.RegexConverter;
import es.upm.dit.xsdinferencer.conversion.RegexOptimizer;
import es.upm.dit.xsdinferencer.conversion.TypeConverter;
import es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex.RegexConvertersFactory;
import es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex.exceptions.NoSuchRegexCanBeInferredException;
import es.upm.dit.xsdinferencer.conversion.converterimpl.optimization.RegexOptimizersFactory;
import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.EmptyRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.MultipleRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Optional;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.SingularRegularExpression;

/**
 * Current implementation of {@link TypeConverter}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 * @see TypeConverter
 */
public class TypeConverterImpl implements TypeConverter {
	
	/**
	 * This method, for each complex type of a schema, calls one by one all the optimizers passed on a list 
	 * to optimize its regular expression, if any of the optimizers performs any change, the whole list is called 
	 * again, so the process will not stop until all the optimizers of the list do no modifications.
	 * @param schema the schema
	 * @param regexOptimizers a list with all the used optimizers
	 */
	protected void doAllOptimizations(Schema schema, List<RegexOptimizer> regexOptimizers) {
		for(ComplexType complexType: schema.getComplexTypes().values()){
			RegularExpression complexTypeWrappedRegexp = new Optional(complexType.getRegularExpression());
			boolean optimized;
			do{
				optimized=false;
				for(RegexOptimizer optimizer: regexOptimizers) {
					boolean thisOptimizerOptimized = optimizer.optimizeRegex(complexTypeWrappedRegexp);
					optimized = optimized || thisOptimizerOptimized;
				}
			}while(optimized);
			complexType.setRegularExpression(complexTypeWrappedRegexp.getElement(0));
		}
	}
	
	/**
	 * Method that removes the initial and final state on all the regular expressions 
	 * of all the complex types of a given schema.
	 * @param schema the schema
	 * @see TypeConverterImpl#removeIntialAndFinalStateRecursive(RegularExpression, SchemaElement, SchemaElement)
	 */
	protected void removeInitialAndFinalStates(Schema schema){
		for(ComplexType complexType:schema.getComplexTypes().values()){
			RegularExpression regularExpression = complexType.getRegularExpression();
			SchemaElement initialState = complexType.getAutomaton().getInitialState();
			SchemaElement finalState = complexType.getAutomaton().getFinalState();
			
			removeIntialAndFinalStateRecursive(regularExpression, initialState,
					finalState);
		}
	}

	/**
	 * Removes an initial and a final state recursively. It returns immediately 
	 * if regularExpression is an SchemaElement or an EmptyRegularExpression
	 * @param regularExpression the regular expression to recurse on
	 * @param initialState the initial state to remove
	 * @param finalState the final state to remove
	 */
	private void removeIntialAndFinalStateRecursive(
			RegularExpression regularExpression, SchemaElement initialState,
			SchemaElement finalState) {
		boolean isRegularExpressionSingular = regularExpression instanceof SingularRegularExpression;
		boolean isRegularExpressionMultiple = regularExpression instanceof MultipleRegularExpression;
		if(!(isRegularExpressionSingular||isRegularExpressionMultiple))
			return;
		RegularExpression empty = new EmptyRegularExpression();
		for(int i=0;i<regularExpression.elementCount();i++){
			RegularExpression currentSubelement = regularExpression.getElement(i);
			if(currentSubelement.equals(initialState)||
					currentSubelement.equals(finalState)){
				regularExpression.setElement(i, empty);
			}
			else{
				removeIntialAndFinalStateRecursive(currentSubelement, initialState, finalState);
			}
		}
	}
	
	/**
	 * This method converts the automaton of each complex type into a regular expression and stores it 
	 * into the source complex type object. Two converters are passed: regexConverter and auxiliarConverter. 
	 * If the first one fails doing the conversion (by throwing a {@link NoSuchRegexCanBeInferredException}), 
	 * the second one is used. It implies that the regexConverter may be one which can fail while the 
	 * auxiliarConverter  
	 * @param schema
	 * @param regexConverter
	 * @param auxiliarConverter
	 */
	protected void doAutomatonToRegexConversion(Schema schema, RegexConverter regexConverter, RegexConverter auxiliarConverter){
		for(ComplexType complexType: schema.getComplexTypes().values()){
			RegularExpression regexp;
			try {
				regexp=regexConverter.convertAutomatonToRegex(complexType.getAutomaton());
			} catch (NoSuchRegexCanBeInferredException e1) {
				try {
					regexp=auxiliarConverter.convertAutomatonToRegex(complexType.getAutomaton());
				} catch (NoSuchRegexCanBeInferredException e2) {
					throw new IllegalArgumentException("An algorithm which should not fail has failed.");
				}
			}
			complexType.setRegularExpression(regexp);
		}
	}
	
	/**
	 * @see TypeConverter#converTypes(Schema, XSDInferenceConfiguration, RegexConvertersFactory, RegexOptimizersFactory)
	 */
	@Override
	public void converTypes(Schema schema,
			XSDInferenceConfiguration configuration, 
			RegexConvertersFactory regexConvertersFactory, 
			RegexOptimizersFactory regexOptimizersFactory) {
		RegexConverter regexConverter;
		RegexConverter auxiliarConverter;
		List<RegexOptimizer> regexOptimizers = configuration.getOptimizers();
		regexConverter=regexConvertersFactory.getRegexConverterInstance(configuration.getAvoidSORE(), configuration.getTryECHARE());
		auxiliarConverter=regexConvertersFactory.getRegexConverterInstance(true, configuration.getTryECHARE());
		doAutomatonToRegexConversion(schema, regexConverter, auxiliarConverter);
		removeInitialAndFinalStates(schema);
		doAllOptimizations(schema, regexOptimizers);
	}

	/**
	 * @see TypeConverter#converTypes(Schema, XSDInferenceConfiguration)
	 */
	@Override
	public void converTypes(Schema schema,
			XSDInferenceConfiguration configuration) {
		converTypes(schema, configuration, RegexConvertersFactory.getInstance(), RegexOptimizersFactory.getInstance());
	}
}
