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
package es.upm.dit.xsdinferencer.merge.mergerimpl.enumeration;

import java.util.Set;

import com.google.common.collect.Sets;

import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.merge.EnumComparator;

/**
 * This comparator computes the set of values which are common to both simple types, 
 * then calculates the ratios resulting from dividing the size of the common values set 
 * (it means, the amount of common values) to the size of the set of values of each simple 
 * type (it means, the amount of values of that simple type). 
 * If at least one of those ratios is less or equal to a given threshold, the comparators return 
 * true, else, it returns false.
 * This comparator always returns false if any of the simpleTypes is not an enumeration.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class MinIntersectionBidirectionalEnumComparator implements EnumComparator {
	
	/**
	 * Threshold under which, comparator returns false
	 */
	private float threshold;

	public MinIntersectionBidirectionalEnumComparator(float threshold){
		this.threshold=threshold;
	}
	
	/**
	 * @see EnumComparator#compare(SimpleType, SimpleType)
	 */
	@Override
	public boolean compare(SimpleType simpleType1, SimpleType simpleType2) {
		if(!simpleType1.isEnum()||!simpleType2.isEnum())
			return false;
		if(simpleType1.isEmpty() && simpleType2.isEmpty())
			return true;
		Set<String> valuesOfSimpleType1 = Sets.newHashSet(simpleType1);
		Set<String> valuesOfSimpleType2 = Sets.newHashSet(simpleType2);
		Set<String> valuesOfIntersection = Sets.intersection(valuesOfSimpleType1, valuesOfSimpleType2);
		float ratio1=((float) valuesOfIntersection.size() )/((float) valuesOfSimpleType1.size());
		float ratio2=((float) valuesOfIntersection.size() )/((float) valuesOfSimpleType2.size());
		return ratio1>=threshold && ratio2 >= threshold;
	}

	/**
	 * @return the threshold
	 */
	public float getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}
}
