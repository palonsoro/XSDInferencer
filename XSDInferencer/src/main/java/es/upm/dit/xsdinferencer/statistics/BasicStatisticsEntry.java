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
package es.upm.dit.xsdinferencer.statistics;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * This class stores a collection of numeric values whose statistics are to be calculated and calculates them. 
 * Such a collection could be the occurrences number per document of an individual element, attribute or value 
 * (under a concrete element or attribute), at a concrete path or under a complex type.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class BasicStatisticsEntry {

	/**
	 * The numeric values. The index in the list must correspond with the document number.
	 */
	private List<Double> values;

	/**
	 * Default constructor.
	 * @param valuesListSize the size of the initial values list, which will be filled with zeros.
	 */
	public BasicStatisticsEntry(int valuesListSize){
		checkArgument(valuesListSize>=1,"'valuesListSize' must be greater or equal to 1");
		values = new ArrayList<>(valuesListSize);
		for(int i=0;i<valuesListSize;i++)
			values.add(0.0);
	}
	
	/**
	 * Constructs an entry from a given List<Integer> (the list is copied)
	 * @param list the list
	 */
	public BasicStatisticsEntry(List<Double> sourceList){
		checkNotNull(sourceList);
		values = new ArrayList<>(sourceList);
	}
	
	/**
	 * Copy constructor 
	 * @param other the statistics entry to copy (the values list is also copied)
	 */
	public BasicStatisticsEntry(BasicStatisticsEntry other){
		values = new ArrayList<>(other.values);
	}
	
	/**
	 * @return The variance of the values
	 */
	public double getVariance() {
		if(values.isEmpty())
			return 0.0;
		double average=getAverage();
		double sum=0.0;
		for(double value: values)
			sum+=Math.pow(value-average,2);
		
		return sum/(values.size());
	
	}
	
	/**
	 * @return The average of the values
	 */
	public double getAverage() {
		if(values.isEmpty())
			return 0.0;
		double sum=0.0;
		for(double value: values)
			sum+=value;
		
		return sum/(values.size());
	
	}
	
	/**
	 * @return The average of the non-zero values. If there are no zero values, it returns {@link Double.NaN}
	 */
	public double getConditionedAverage() {
		if(values.isEmpty())
			return 0.0;
		double sum=0.0;
		double nonZeroCount=0.0;
		for(double value: values){
			sum+=value;
			if(value!=0.0)
				nonZeroCount++;
		}
		return sum/nonZeroCount;
	}
	
	/**
	 * @return The variance of the non-zero values
	 */
	public double getConditionedVariance() {
		if(values.isEmpty())
			return 0.0;
		double conditionedAverage=getConditionedAverage();
		double sum=0.0;
		double nonZeroCount=0.0;
		for(double value: values){
			if(value!=0.0){
				nonZeroCount++;
				sum+=Math.pow(value-conditionedAverage,2);
			}
		}
		return sum/nonZeroCount;
	
	}
	
	/**
	 * @return The mode of the value related to the entry
	 */
	public Set<ValueAndFrequency> getMode() {
		if(values.isEmpty())
			return Collections.singleton(new ValueAndFrequency(0.0,0));
//		return Collections.max(values);
		Set<ValueAndFrequency> result = new HashSet<>();
		int currentMaxFreq = Integer.MIN_VALUE;
		for(int i=0;i<values.size();i++){
			Double value = values.get(i);
			int frequency = Collections.frequency(values, value);
			if(frequency>currentMaxFreq){
				result.clear();
			}
			if(frequency>=currentMaxFreq){
				result.add(new ValueAndFrequency(value,frequency));
				currentMaxFreq=frequency;
			}
		}
		//If all the elements have the same frequency, there is no mode
		boolean clear = true;
		for(ValueAndFrequency resultValue: result){
			if(!values.contains(resultValue.getValue())){
				clear=false;
				break;
			}
		}
		if(clear)
			result.clear();
		return result;
	}
	
	/**
	 * @return the maximum number of values
	 */
	public ValueAndFrequency getMax(){
		if(values.isEmpty())
			return new ValueAndFrequency(0.0,0);
		double max = Collections.max(values);
		int frequency = Collections.frequency(values, max);
		
		return new ValueAndFrequency(max,frequency);
	}
	
	/**
	 * @return the minimum number of values
	 */
	public ValueAndFrequency getMin(){
		if(values.isEmpty())
			return new ValueAndFrequency(0.0,0);
		double min = Collections.min(values);
		int frequency = Collections.frequency(values, min);
		
		return new ValueAndFrequency(min,frequency);
	}
	
	/**
	 * @return the sum of all the values of the entry
	 */
	public long getTotal(){
		long sum=0;
		for(double value: values)
			sum+=value;
		
		return sum;
	}
	
	/**
	 * It returns the ratio between the number of non-zero values and the count of all the values.
	 * @return the described ratio, or 0 if there are no values
	 */
	public double getNonZeroRatio(){
		if(values.isEmpty())
			return 0.0;
		double nonZeroCount=0.0;
		for(double value: values){
			if(value!=0.0)
				nonZeroCount++;
		}
		return nonZeroCount/(values.size());
	}
	
	/**
	 * It returns the ratio between the standard deviation and the average
	 * @return the described ratio (note that it will be {@link Double.NaN} if the average is zero)
	 */
	public double getStandardDeviationAverageRatio(){
		return Math.sqrt(getVariance())/getAverage();
	}
	
	/**
	 * It returns the ratio between the standard deviation and the average, when both are conditioned 
	 * to non-zero values
	 * @return the described ratio (note that it will be {@link Double.NaN} if the average is zero or there are no non-zero values) 
	 */
	public double getConditionedStandardDeviationAverageRatio(){
		return Math.sqrt(getConditionedVariance())/getConditionedAverage();
	}

	/**
	 * This method increments by one the value at the given index. It is useful to register counts 
	 * when the object is used to store occurrences info.
	 * @param index the index whose value will be incremented.
	 */
	public void registerCount(int index){
		double previousCount;
		if(index<values.size()){
			previousCount=values.get(index);
		}
		else{
			previousCount=0;
			for(int i=values.size();i<=index;i++)
				values.add(0.0);
		}
		values.set(index,previousCount+1);
	}

	/**
	 * This method sets the value at the given position to the given value. If index >= the count of values, 
	 * the values list is filled with zeros as needed.
	 * @param index the index to be set
	 * @param value the value to set
	 */
	public void setValue(int index, double value){
		if(index>=values.size()){
			for(int i=values.size();i<=index;i++)
				values.add(0.0);
		}
		values.set(index,value);
	}

	/**
	 * Hash code generated from the source list
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(values.hashCode());
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BasicStatisticsEntry)) {
			return false;
		}
		BasicStatisticsEntry other = (BasicStatisticsEntry) obj;
		if (values == null) {
			if (other.values != null) {
				return false;
			}
		} else if (!values.equals(other.values)) {
			return false;
		}
		return true;
	}

	/**
	 * String representation which includes all the values and statistic information
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BasicStatisticsEntry [values=" + values  
//				+ ", getAverage()="
//				+ getAverage() + ", getConditionedAverage()="
//				+ getConditionedAverage()
//				+ ", getConditionedStandardDeviationAverageRatio()="
//				+ getConditionedStandardDeviationAverageRatio()
//				+ ", getConditionedVariance()=" + getConditionedVariance()
//				+ ", getMax()=" + getMax() + ", getMin()=" + getMin()
//				+ ", getMode()=" + getMode() + ", getNonZeroRatio()="
//				+ getNonZeroRatio() + ", getStandardDeviationAverageRatio()="
//				+ getStandardDeviationAverageRatio() + ", getTotal()="
//				+ getTotal() + ", getVariance()=" + getVariance() 
				+ "]"
				;
	}

	/**
	 * Returns a BasicStatisticsEntry which consists of merging two given BasicStatisticsEntry. The merged one: 
	 * <ul>
	 * <li>Will have the size of the longest one</li>
	 * <li>Its values will be the sum of the previously existing elements (if there is no value at a position, it will be considered a zero)</li>
	 * </ul>
	 * @param entry1 the first original entry
	 * @param entry2 the second original entry
	 * @return the merged entry
	 */
	public static BasicStatisticsEntry mergeBasicStatisticsEntries(BasicStatisticsEntry entry1, BasicStatisticsEntry entry2){
		checkNotNull(entry1);
		checkNotNull(entry2);
		int sizeNew=Math.max(entry1.values.size(), entry2.values.size());
		BasicStatisticsEntry entryNew = new BasicStatisticsEntry(sizeNew);
		for(int i=0;i<sizeNew;i++){
			double value1=0;
			double value2=0;
			if(i<entry1.values.size()){
				value1=entry1.values.get(i);
			}
			if(i<entry2.values.size()){
				value2=entry2.values.get(i);
			}
			entryNew.values.set(i, value1+value2);
		}
		return entryNew;
	}
	
}
