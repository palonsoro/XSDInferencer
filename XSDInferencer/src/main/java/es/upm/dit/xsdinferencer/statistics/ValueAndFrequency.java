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

import java.util.Objects;

/**
 * A pair that holds a value and its frequency
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class ValueAndFrequency {
	
	/**
	 * The value
	 */
	private double value;
	/**
	 * The frequency
	 */
	private int frequency;
	
	/**
	 * Constructor.
	 * @param value
	 * @param frequency
	 */
	public ValueAndFrequency(double value, int frequency) {
		this.value = value;
		this.frequency = frequency;
	}
	
	/**
	 * Getter.
	 * @return the value
	 */
	public double getValue() {
		return value;
	}
	
	/**
	 * Getter.
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(value,frequency);
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
		if (!(obj instanceof ValueAndFrequency)) {
			return false;
		}
		ValueAndFrequency other = (ValueAndFrequency) obj;
		if (frequency != other.frequency) {
			return false;
		}
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return an String representation of the object
	 * @see Object#toString()
	 */
	@Override
	public String toString(){
		return "Value: "+value+ " Frequency: "+frequency;
	}
	
}
