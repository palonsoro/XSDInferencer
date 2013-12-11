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
