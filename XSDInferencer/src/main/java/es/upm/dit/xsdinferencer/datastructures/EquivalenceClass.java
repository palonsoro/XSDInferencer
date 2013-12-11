package es.upm.dit.xsdinferencer.datastructures;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * This class represent an equivalence class for the equivalence relation defined by the CRX algorithm. 
 * It contains all the elements of the equivalence class.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class EquivalenceClass implements Iterable<SchemaElement>, Comparable<EquivalenceClass> {
	
	/**
	 * Elements of the equivalence class.
	 */
	private Set<SchemaElement> elements;
	
	/**
	 * Default constructor.
	 */
	public EquivalenceClass(){
		elements=new HashSet<SchemaElement>();
	}
	
	/**
	 * Constructor with initial elements.
	 * @param initialElements array with initial elements
	 * @throws NullPointerException if initialElements == null
	 */
	public EquivalenceClass(SchemaElement[] initialElements){
		if(initialElements==null){
			throw new NullPointerException("'initialElements' must not be null");
		}
		elements=new HashSet<SchemaElement>(Arrays.asList(initialElements));
	}
	
	/**
	 * Constructor with initial elements.
	 * @param initialElements set of initial elements
	 * @throws NullPointerException if initialElements == null
	 */
	public EquivalenceClass(Set<SchemaElement> initialElements){
		if(initialElements==null){
			throw new NullPointerException("'initialElements' must not be null");
		}
		elements=initialElements;
	}
	
	/**
	 * Constructor that merges all the equivalence classes of a collection
	 * @param eqClasses a collection of equivalence classes
	 * @throws NullPointerException if eqClasses == null
	 */
	public EquivalenceClass(Collection<EquivalenceClass> eqClasses){
		if(eqClasses==null){
			throw new NullPointerException("'eqClasses' must not be null");
		}
		elements=new HashSet<SchemaElement>();
		for(EquivalenceClass eqClass:eqClasses){
			for(SchemaElement element:eqClass){
				elements.add(element);
			}
		}
	}


	/**
	 * Returns how many elements are there in the equivalence class.
	 * @return  how many elements are there in the equivalence class.
	 */
	public int size() {
		return elements.size();
	}

	/**
	 * Returns true if an element is in the equivalence class.
	 * @param element element to check
	 * @return true if the element is in the equivalence class
	 */
	public boolean contains(SchemaElement element) {
		return elements.contains(element);
	}

	/**
	 * Adds a node to the equivalence class
	 * @param element
	 * @return true if the element has been added, false otherwise.
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(SchemaElement element) {
		return elements.add(element);
	}

	/**
	 * Removes an element from the equivalence class.
	 * @param element element to remove
	 * @return true if the element has been removed
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(SchemaElement element) {
		return elements.remove(element);
	}
	
	/**
	 * Returns a hashCode value for the object
	 * @return a hashCode for the object
	 */
	@Override
	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result
//				+ ((elements == null) ? 0 : elements.hashCode());
//		return result;
		return Objects.hash(elements);
	}

	/**
	 * Returns true if the equivalence class is equal to another
	 * @param obj other object
	 * @return true if the other object is equals to this.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof EquivalenceClass)) {
			return false;
		}
		EquivalenceClass other = (EquivalenceClass) obj;
		if (elements == null) {
			if (other.elements != null) {
				return false;
			}
		} else if (!elements.equals(other.elements)) {
			return false;
		}
		return true;
	}

	/**
	 * Allows to iterate over the elements of the equivalence class.
	 * @return iterator
	 */
	@Override
	public Iterator<SchemaElement> iterator() {
		return elements.iterator();
	}	
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString(){
		return elements.toString();
	}
	
	/**
	 * @see Comparable#toString()
	 */
	@Override
	public int compareTo(EquivalenceClass other) {
		return this.toString().compareTo(other.toString());
	}
	
}
