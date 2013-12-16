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
package es.upm.dit.xsdinferencer.extraction;

import es.upm.dit.xsdinferencer.datastructures.SimpleType;

/**
 * A SimpleTypeInferencer learns simple values and infers a SimpleType based on them and on the given parameters.<br/>
 * Some important remarks:<br/>
 * <ul>
 * <li>There must be ONE SimpleTypeInferencer for each element or attribute whose simpleType is to be learned.</li>
 * <li>The simple type can be queried at any time. After that, the inferencer remains valid and new values may be learned in order to get another newer (and different) SimpleType</li>
 * </ul>
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface SimpleTypeInferencer extends Iterable<String>,Cloneable {
	
	/**
	 * Learns a value.<br/>
	 * <b>IMPORTANT REMARKS</b><br/> 
	 * <ul>
	 * <li>Values MUST NOT be trimmed or normalizad before they are learned. Learned values will be trimmed or not at 
	 * inference time when necessary (it means, depending on whether the builtin type of the values).</li>
	 * <li>Empty values are ignored if there is not any non-empty value!!! (But values that only consist of whitespace character or similar may be considered!!!).</li>
	 * </ul>
	 * @param value the value to learn
	 * @param sourceNodeNamespace the namespace of the source node
	 * @param sourceNodeName the name of the source node
	 */
	public void learnValue(String value, String sourceNodeNamespace, String sourceNodeName);
	/**
	 * Infers a SimpleType based on the currently available data (learned values, parameters...)
	 * @param name name of the new SimpleType
	 * @return the SimpleType learned
	 */
	public SimpleType getSimpleType(String name);
	
	/**
	 * It returns how many times an already value has been learned, or zero if not.
	 * @param value A non-trimmed value
	 * @return the number of occurrences of that value
	 */
	public int getValueOccurrences(String value);
	/**
	 * It returns how many non-trimmed distinct values are there.
	 * @return how many non-trimmed distinct values are there.
	 */
	public int getDistinctValuesCount();
	
//	/**
//	 * This allows the inferencer to be cloned
//	 * @return a clone
//	 */
//	public Object clone();
}
