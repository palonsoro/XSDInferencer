
package es.upm.dit.xsdinferencer.datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;

/**
 * Represents a simple type, it means, the type of a text of an element or 
 * an attribute (xs:string, xs:integer, an enumeration...).
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class SimpleType implements Iterable<String> {
	
	/**
	 * Name of simpleType.
	 * It should be of the form:
	 * <ul>
	 * <li>For attributes: <i>nameOfComplexTypeOfElement_attributeName-SimpleTypeOfAttribute</i>.</li>
	 * <li>For elements: <i>nameOfComplexType</i>.
	 * </ul>
	 */
	private String name;
	
	/**
	 * XSD built-in type inferenced for this SimpleType.
	 * It must be qualified.
	 */
	private String builtinType;
	
	/**
	 * All the known values occurred, which will be the only allowed values on this SimpleType 
	 * when it is considered as an enumeration.
	 */
	private List<String> knownValues;
	
	/**
	 * Whether this SimpleType must be considered as an enumeration or not.
	 */
	private boolean isEnum;
	
	/**
	 * This set contains strings of the form: <i>namespaceURI</i>|<i>name</i> 
	 * of the elements and attributes where this simple type has been found. 
	 * This set would normally have size one before the types merge if the initial 
	 * schema is extracted with the implemented extractor and the implemented 
	 * type name inferencers.
	 * However, it makes possible to create new type name inferencers in which this 
	 * condition does not have to be satisfied.
	 */
	private Set<String> sourceNodesNamespaceAndNames;
	
	/**
	 * Constructor
	 * @param name Name of simpleType
	 * @param builtinType An String which represents the XSD built-in simpleType of values.
	 * 					  It may also be null, which defaults to "xs:string" builtin type.
	 * @param knownValues A collection of known values of the simpleType (it will be copied).
	 * 					  It may also be null, then an empty list is created.
	 * @param isEnum Indicates whether the knownValues should be considered as an enumeration or not.
	 * @throws NullPointerException if name is null
	 */
	public SimpleType(String name, String builtinType,
			Collection<? extends String> knownValues, boolean isEnum) {
		if(name==null)
			throw new NullPointerException("'name' must not be null");
		this.name = name;
		if(builtinType==null){
			this.builtinType="xs:string";
		} else {
			this.builtinType = builtinType;
		}
		if(knownValues == null) {
			this.knownValues = new ArrayList<String>();
		}else{
			this.knownValues = new ArrayList<String>(knownValues);
		}
		this.isEnum = isEnum;
		this.sourceNodesNamespaceAndNames=new HashSet<>();
	}

	/**
	 * Default constructor with built-in type xs:string, empty knownValues and isEnum=false. 
	 * @param name Name of SimpleType
	 * @throws NullPointerException if name is null
	 */
	public SimpleType(String name){
		this(name,"",null,false);
	}
	
	//Getters and setters
	
	/**
	 * @return the name of the SimpleType
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name of the SimpleType to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the builtinType
	 */
	public String getBuiltinType() {
		return builtinType;
	}

	/**
	 * @param builtinType the builtinType to set
	 */
	public void setBuiltinType(String builtinType) {
		this.builtinType = builtinType;
	}

	/**
	 * @return whether it should be considered as an enum
	 */
	public boolean isEnum() {
		return isEnum;
	}

	/**
	 * @param isEnum of the SimpleType
	 */
	public void setEnum(boolean isEnum) {
		this.isEnum = isEnum;
	}

	/**
	 * This method returns true if the known values consist of white space characters as defined by the RFC, 
	 * it means: #x9 (tab), #xA (line feed), #xD (carriage return) and #x20 (space).
	 * @return true if all the known values consist of those characters, false otherwise (including when there are no known values)
	 */
	public boolean consistOnlyOfWhitespaceCharacters(){
		for(int i=0;i<knownValues.size();i++){
			if(!knownValues.get(i).matches("[\t\n\r ]*"))
				return false;
		}
		return !isEmpty(); //true if this is not empty, false otherwise, as specified.
	}
	
	//knownValues delegate methods
	//Some of these methods may be useless if the SimpleType is not an enumeration.
	
	/**
	 * 
	 * @return true if there is no known value
	 */
	public boolean isEmpty(){
		return knownValues.isEmpty();
	}
	
	/**
	 * @return how many elements the enumeration has
	 */
	public int enumerationCount() {
		return knownValues.size();
	}

	/**
	 * Tests whether the enumeration contains a value
	 * @param value the value to test
	 * @return true if the enumeration contains the value, false otherwise
	 */
	public boolean enumerationContains(String value) {
		return knownValues.contains(value);
	}

	/**
	 * Adds an element to the enumeration
	 * @param element the element to add.
	 * @return true if it has been added.
	 */
	public boolean addToEnumeration(String element) {
		return knownValues.add(element);
	}

	/**
	 * Tests whether the enumeration contains all the values of a collection
	 * @param collection the collection
	 * @return true if all the values of the collection are present in the enumeration, false otherwise
	 */
	public boolean enumerationContainsAll(Collection<?> collection) {
		return knownValues.containsAll(collection);
	}

	/**
	 * Adds all the elements of a collection into another collection
	 * @param collection contains the elements to add
	 * @return true if they could be added
	 */
	public boolean addAllToEnumeration(Collection<? extends String> collection) {
		return knownValues.addAll(collection);
	}

	/**
	 * Gets an element of the enumeration 
	 * @param index the index of the element
	 * @return the element
	 * @see java.util.List#get(int)
	 */
	public String getEnumerationElement(int index) {
		return knownValues.get(index);
	}
	
	/**
	 * Returns an iterator to iterate over the known values.
	 * @return iterator of knownValues
	 */
	@Override
	public Iterator<String> iterator(){
		return knownValues.iterator();
	}

	//Other methods
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result
//				+ ((builtinType == null) ? 0 : builtinType.hashCode());
//		result = prime * result + (isEnum ? 1231 : 1237);
//		result = prime * result
//				+ ((knownValues == null) ? 0 : knownValues.hashCode());
//		result = prime * result + ((name == null) ? 0 : name.hashCode());
//		return result;
		return Objects.hash(name,builtinType,isEnum);
	}

	//Autogenerated
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
		if (!(obj instanceof SimpleType)) {
			return false;
		}
		SimpleType other = (SimpleType) obj;
		if (builtinType == null) {
			if (other.builtinType != null) {
				return false;
			}
		} else if (!builtinType.equals(other.builtinType)) {
			return false;
		}
		if (isEnum != other.isEnum) {
			return false;
		}
//		if (knownValues == null) {
//			if (other.knownValues != null) {
//				return false;
//			}
//		} else if (!knownValues.equals(other.knownValues)) {
//			return false;
//		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Adds an element name and namespace to the set of source elementsAndNamespaces.
	 * This set allows to determine whether two simple types come from elements with the 
	 * same name and namespace, in order to compare them with different comparators at merging 
	 * time (if the configuration is set to do this).
	 * If name==null, this method does nothing and returns false (the source 
	 * @param namespaceURI The namespace URI of the element
	 * @param name The name of the element. It must be preceded by @ if it is an attribute
	 * @return true if it has been added
	 */
	public boolean addSourceNodeNamespaceAndName(String namespaceURI,String name) {
		if(name==null)
			return false;
		String namespaceURIeffective = (namespaceURI!=null)?namespaceURI:"";
		return sourceNodesNamespaceAndNames.add(namespaceURIeffective+":"+name);
	}

	/**
	 * Adds all the element nameAndNamespaces from a simple type into the set of source 
	 * elementsAndNamespaces.
	 * This set allows to determine whether two simple types come from elements with the 
	 * same name and namespace, in order to compare them with different comparators at merging 
	 * time (if the configuration is set to do this).
	 * @param other the other simple type
	 * @return true if they have been added
	 */
	public boolean addAllTheSourceNodeNamespaceAndNames(Collection<String> c) {
		return sourceNodesNamespaceAndNames.addAll(c);
	}
	
	/**
	 * Adds all the element nameAndNamespaces from a collection into the set of source 
	 * elementsAndNamespaces.
	 * This set allows to determine whether two simple types come from elements with the 
	 * same name and namespace, in order to compare them with different comparators at merging 
	 * time (if the configuration is set to do this).
	 * @param other the other simple type
	 * @return true if they have been added
	 */
	public boolean addAllTheSourceNodeNamespaceAndNames(SimpleType other) {
		return sourceNodesNamespaceAndNames.addAll(other.sourceNodesNamespaceAndNames);
	}
	
	/**
	 * Returns an unmodifiable view (via {@linkplain Collections#unmodifiableSet(Set)} of the set of source namespacesAndNames.
	 * @return an unmodifiable view (via {@linkplain Collections#unmodifiableSet(Set)} of the set of source namespacesAndNames.
	 */
	public Set<String> getSourceNodeNamespacesAndNames(){
		return Collections.unmodifiableSet(sourceNodesNamespaceAndNames);
	}
	
	/**
	 * Returns the name which will be used to represent this simple type in the XSD document.
	 * If this simple type is an enumeration, then it returns the simple type name plus SimpleType, else, it returns 
	 * the builtin simple type.
	 * @param separator a separator between the simple type internal name and the 'SimpleType' string used to 
	 * avoid collisions with complex type names. Normally, it should be {@linkplain XSDInferenceConfiguration#getTypeNamesAncestorsSeparator()}.
	 * @return the result of getName() if this SimpleType isEnum() returns true, getBuiltinType() otherwise.
	 */
	public String getRepresentationName(String separator){
		if(isEnum)
			return getName()+separator+"SimpleType";
		else
			return getBuiltinType();
	}
	
	/**
	 * It returns an unmodifiable list of the known values
	 * @return an unmodifiable list of the known values, backed by the internal list of this SimpleType
	 */
	public List<String> getKnownValuesUnmodifiableList(){
		return Collections.unmodifiableList(knownValues);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SimpleType [name=" + name + "]";
	}
}
