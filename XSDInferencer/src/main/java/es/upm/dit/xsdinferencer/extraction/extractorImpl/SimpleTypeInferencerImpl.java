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
package es.upm.dit.xsdinferencer.extraction.extractorImpl;

import static com.google.common.base.Preconditions.checkNotNull;
import static es.upm.dit.xsdinferencer.XSDInferenceConfiguration.XSD_NAMESPACE_PREFIX;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.extraction.SimpleTypeInferencer;

/**
 * Default implementation for {@link SimpleTypeInferencer}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class SimpleTypeInferencerImpl implements SimpleTypeInferencer {

	//Regular expressions for builtin type inference
	protected static final String BUILTIN_REGEX_XSBOOLEAN = "^true|false|0|1$";
	protected static final String BUILTIN_REGEX_XSINTEGER = "^(\\+|\\-)?[0-9]+$";
	protected static final String BUILTIN_REGEX_XSDECIMAL = "^(\\+|\\-)?[0-9]+(\\.[0-9]+)?$";
	
	/**
	 * It maps the learned Values to its occurrences numbers
	 */
	private Map<String,Integer> valueOccurrences;
	/**
	 * The current inference configuration
	 */
	private XSDInferenceConfiguration config;
	
	/**
	 * It stores a set of strings of the form <i>namespaceUri</i>:<i>name</i> of each element and attribute from where this inferencer has learned a value.
	 */
	private Set<String> sourceNamespacesAndNames;
	
	/**
	 * Flag that indicates how many times the empty value has been learned.
	 * This is maintained separately because those values will only be added if and only if at least another value occurs.
	 */
	private int emptyValuesLearned;
	
	/**
	 * Constructor
	 * @param config Current inference configuration
	 */
	public SimpleTypeInferencerImpl(XSDInferenceConfiguration config) {
		checkNotNull(config,"'config' must not be null");
		this.config=config;
		this.valueOccurrences=new HashMap<String,Integer>();
		this.emptyValuesLearned=0;
		this.sourceNamespacesAndNames=new HashSet<>();
	}
	
	/**
	 * @see Object#clone()
	 */
	@Override
	public Object clone(){
		SimpleTypeInferencerImpl copy = new SimpleTypeInferencerImpl(config);
		copy.emptyValuesLearned=this.emptyValuesLearned;
		copy.valueOccurrences=new HashMap<>(this.valueOccurrences);
		copy.sourceNamespacesAndNames=new HashSet<>(this.sourceNamespacesAndNames);
		return copy;
	}
	
	/**
	 * @see SimpleTypeInferencer#learnValue(String, String, String)
	 */
	@Override
	public void learnValue(String value, String sourceNodeNamespaceURI, String sourceNodeName) {
		checkNotNull(value,"'value' must not be null");
		if(value.equals("")){
			emptyValuesLearned++;
			return; 
		}
		if(!valueOccurrences.containsKey(value)){
			valueOccurrences.put(value, 1);
		} else {
			int previousOccurrences=valueOccurrences.get(value);
			valueOccurrences.put(value, previousOccurrences+1);
		}
		if(sourceNodeName!=null)
			sourceNamespacesAndNames.add(sourceNodeNamespaceURI+":"+sourceNodeName);
	}
	
	/**
	 * @see SimpleTypeInferencer#getSimpleType(String)
	 */
	@Override
	public SimpleType getSimpleType(String name) {
		//First, we should consider whether there are only empty values or not
//		String actualName = name.replace("@", "");
//		if(name.contains("@"))
//			actualName+="-SimpleTypeOfAttribute";
		String actualName = name;
		if(valueOccurrences.isEmpty()){
			return new SimpleType(actualName,"",valueOccurrences.keySet(),false);
		} else {
			if(emptyValuesLearned>0)
				valueOccurrences.put("", emptyValuesLearned); //If there are other values than the empty value, the empty value must be added and the simple type must be generated in a normal way
		}
		String builtinType = inferBuiltInType();
		Set<String> valueSet = valueOccurrences.keySet();
		if(!builtinTypePreservesWhitespaces(builtinType))
			valueSet=replaceAndTrimWhitespaces(valueSet, builtinTypeTrimsAndCollapsesWhitespaces(builtinType));
		if(builtinTypeRequiresNumericEquivalenceFix(builtinType))
			valueSet=fixEquivalentNumbers(valueSet);
		boolean isEnum=config.getGenerateEnumerations();
		isEnum=isEnum&&config.getMinNumberOfDistinctValuesToEnum()<=valueSet.size();
        isEnum=isEnum&&valueSet.size()<=config.getMaxNumberOfDistinctValuesToEnum();
        isEnum=isEnum&&(!builtinType.equalsIgnoreCase(XSD_NAMESPACE_PREFIX+"boolean")); //Booleans are never inferred as enumerations
		SimpleType simpleType = new SimpleType(actualName,builtinType,valueSet,isEnum);
		simpleType.addAllTheSourceNodeNamespaceAndNames(sourceNamespacesAndNames);
		if(simpleType.consistOnlyOfWhitespaceCharacters())
			simpleType.setEnum(false);
		return simpleType;
		
	}
	
	/**
	 * Method to check whether the whiteSpace property of an inferable builtin type is Preserve or not. This is important in order to consider actual values or the ones resulting of replacing whitespaces.
	 * @param builtinType the builtin type to check
	 * @return if the given type extends string or not
	 */
	private boolean builtinTypePreservesWhitespaces(String builtinType){
		checkNotNull(builtinType);
		//This method MUST be modified when the ability to infer more builtin types is added  
		//This dummy implementation does only take care of whether the type extends xs:string (whiteSpace=preserve) or not (whiteSpace=collapse for all the other currently inferable simple types)
		if(builtinType.equals(XSD_NAMESPACE_PREFIX+"string")){
			return true;
		} else{
			return false;
		}
	}
	
	/**
	 * This method returns true if the given builtin type trims whitespaces and false otherwise.
	 * @param builtinType the builtin type to check
	 * @return whether the builtin type should collapse and trim white spaces or not
	 */
	private boolean builtinTypeTrimsAndCollapsesWhitespaces(String builtinType){
		checkNotNull(builtinType);
		//This method MUST be modified when the ability to infer more builtin types is added
		//Now, it is bound to builtinTypePreservesWhitespaces() because we do not infere builtin types whose whiteSpace property is replace.
		//This dummy implementation does only take care of whether the type extends xs:string (whiteSpace=preserve) or not (whiteSpace=collapse for all the other currently inferable simple types)
		return builtinTypePreservesWhitespaces(builtinType);
	}
	
	/**
	 * This method returns true if the {@link SimpleTypeInferencerImpl#fixEquivalentNumbers(Set)} method must be called, in order to fix the situation of having equivalent 
	 * numbers with different string representations.
	 * @param builtinType the builtinType
	 * @return true if the fix is necessary
	 */
	private boolean builtinTypeRequiresNumericEquivalenceFix(String builtinType){
		checkNotNull(builtinType);
		//It may be necessary to modify this method if new numeric types other than xs:decimal and xs:integer
        //are inferred
		if(builtinType.equals(XSD_NAMESPACE_PREFIX+"integer")){
			return true;
		}
		else if(builtinType.equals(XSD_NAMESPACE_PREFIX+"decimal")){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Given a Set of Strings, returns another Set with all those values trimmed (note that the size of the new Set may be less or equal to the size of the original one).
	 * @param originalSet a Set of Strings
	 * @param trimAndCollapse whether white spaces should be trimmed and collapse or not (the other characters are replaced and nothing more is done)
	 * @return a Set which contains a trimmed version of each value of the original Set
	 */
	private Set<String> replaceAndTrimWhitespaces(Set<String> originalSet, boolean trimAndCollapse){
		checkNotNull(originalSet);
		Set<String> result = new HashSet<String>(originalSet.size());
		for(String value: originalSet){
			String newValue = value.replaceAll("[\t\n\r]", " ");
			if(trimAndCollapse){
				newValue=newValue.replaceAll("[ ]{2,}", " ").trim();
			}
			result.add(newValue.trim());
		}
		return result;
	}
	
	/**
	 * In xs:integer and xs:decimal, values such as +5 and 5 are equivalent.
	 * In xs:decimal, values like 5.0 and 5 are also equivalent. 
	 * This method takes a set and transform values like those to their 
	 * shortest equivalents (without + or 0 decimals).
	 * @param originalSet the original set
	 * @return the fixed set
	 */
	private Set<String> fixEquivalentNumbers(Set<String> originalSet){
		checkNotNull(originalSet);
		Set<String> result = new HashSet<String>(originalSet);
		for(String value: originalSet){
			String newValue=value;
			if(value.startsWith("+"))
				newValue=value.replaceAll("\\+", "");
			if(value.matches("^.+\\.0+$"))
				newValue=newValue.replaceAll("\\.0+", "");
			if(!value.equals(newValue)){
				result.remove(value);
				result.add(newValue);
			}
		}
		return result;
	}
	
	/**
	 * It infers a builtin type by performing some checks in a concrete order. If one check returns 
	 * true, its associated type is returned.
	 * If no check returns true, an xs:string is returned
	 * @return An string that represents the built-in type of this simple type (including the XSD namespace prefix).
	 * @see XSDInferenceConfiguration#XSD_NAMESPACE_PREFIX
	 */
	private String inferBuiltInType(){
		if(checkXSBoolean()){
			return XSD_NAMESPACE_PREFIX+"boolean";
		}
		else if(checkRegexp(BUILTIN_REGEX_XSINTEGER, true)){
			return XSD_NAMESPACE_PREFIX+"integer";
		}
		else if(checkRegexp(BUILTIN_REGEX_XSDECIMAL, true)){
			return XSD_NAMESPACE_PREFIX+"decimal";
		}
		else{
			return XSD_NAMESPACE_PREFIX+"string";
		}
	}
	
	/**
	 * Method to check whether the builtin type would be a xs:boolean or not.
	 * This method should be used instead of {@linkplain SimpleTypeInferencerImpl#checkRegexp(String, boolean)} 
	 * because it also checks that there is any "true" or "false" values so that simple types with only 
	 * "0" or "1" values do not become booleans.
	 * @return true if the type is a boolean, false if not
	 */
	private boolean checkXSBoolean(){
		boolean noTrueFalseStringFound=true; //If there are only 0 or 1 values, it may be an xs:integer.
		for(String value:valueOccurrences.keySet()){
			if(valueOccurrences.get(value).equals(0))
				continue;
			if(!(value.trim().matches(BUILTIN_REGEX_XSBOOLEAN)))
				return false;
			if((value.trim().equals("true")||value.trim().equals("false")))
					noTrueFalseStringFound=false;
		}
		if(!noTrueFalseStringFound)
			return true;
		else 
			return false;
	}
	
	/**
	 * Checks that all the known values match a given regular expression in order to determine its simple 
	 * type.
	 * @param regex the regular expression to which every known value must match
	 * @param trimValues if each value should be trimmed before it is checked against the regular expression
	 * @return true if all the known values match the given regular expression, false if not
	 */
	private boolean checkRegexp(String regex, boolean trimValues){
		checkNotNull(regex);
		for(String value: valueOccurrences.keySet()){
			if(valueOccurrences.get(value).equals(0))
				continue;
			String effectiveValue = value;
			if(trimValues)
				effectiveValue=value.replaceAll("[\t\n\r]", " ").replaceAll("[ ]{2,}", " ").trim();
			if(!effectiveValue.matches(regex))
				return false;
		}
		return true;
	}
	
		
	/**
	 * Returns an iterator over the distinct values
	 */
	@Override
	public Iterator<String> iterator() {
		return valueOccurrences.keySet().iterator();
	}
	
	
	@Override
	public int getValueOccurrences(String value) {
		return valueOccurrences.get(value);
	}
	
	
	@Override
	public int getDistinctValuesCount() {
		return valueOccurrences.size();
	}
}
