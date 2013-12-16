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
package es.upm.dit.xsdinferencer.datastructures;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * A complex type, understood as something a bit different than in XSD.
 * We will name ComplexType a complete description of an element, which 
 * consists of a children representation (in two ways: an automaton and a 
 * regular expression derived from that automaton), an attribute list and 
 * a simple type which describes what kind of text is allowed in elements
 * under that complex type.
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class ComplexType {
	
	/**
	 * The name of the complex type
	 */
	private String name;
	
	/**
	 * Automaton which describes the structure of children
	 */
	private ExtendedAutomaton automaton;
	
	/**
	 * Simple type that describes the text of elements under this complex type
	 */
	private SimpleType textSimpleType = null;
	
	/**
	 * List of allowed attributes under this complex type.
	 */
	private List<SchemaAttribute> attributeList = null;
	
	/**
	 * Regular expression, which describes the structure of children
	 */
	private RegularExpression regularExpression;
	
	/**
	 * This set contains strings of the form: <i>namespaceURI</i>|<i>name</i> 
	 * of the elements where this complex type has been found. 
	 * This set would normally have size one before the types merge if the initial 
	 * schema is extracted with the implemented extractor and the implemented 
	 * type name inferencers.
	 * However, it makes possible to create new type name inferencers in which this 
	 * condition does not have to be satisfied.
	 */
	private Set<String> sourceElementsNamespaceAndNames;
	
	/**
	 * Set that contains all the comments made on any element that belongs to the complex type
	 */
	private Set<String> comments;
	
	/**
	 * Constructor
	 * @param name name of the complex type. It must not be null
	 * @param automaton of the new complex type, if null, a new ExtendedAutomaton is created
	 * @param textSimpleType simple type of the text, if null, a new one is created with default values 
	 *        and the same name than the complex type, for details see {@link SimpleType#SimpleType(String)}
	 * @param attributeList List of attributes, if null, an empty ArrayList is created.
	 * @throws NullPointerException if name is null
	 */
	public ComplexType(String name, ExtendedAutomaton automaton,
			SimpleType textSimpleType, List<SchemaAttribute> attributeList) {
		if(name==null)
			throw new NullPointerException("'name' must not be null");
		this.name = name;
		this.automaton = automaton;
		this.textSimpleType = textSimpleType;
		this.attributeList = attributeList;
		this.sourceElementsNamespaceAndNames=new HashSet<>();
		this.comments=new TreeSet<>();
	}

	/**
	 * Getter
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter
	 * @return the automaton which describes the structure of children
	 */
	public ExtendedAutomaton getAutomaton() {
		return automaton;
	}

	/**
	 * Getter
	 * @return the SimpleType of the text of elements of this ComplexType
	 */
	public SimpleType getTextSimpleType() {
		return textSimpleType;
	}

	/**
	 * Setter
	 * @param textSimpleType the new SimpleType of text of elements of this ComplexType
	 * @throws NullPointerException if textSimpleType is null
	 */
	public void setTextSimpleType(SimpleType textSimpleType) {
		if(textSimpleType==null)
			throw new NullPointerException("'simpleType' must not be null");
		this.textSimpleType = textSimpleType;
	}

	/**
	 * Getter
	 * @return the attribute list of the complex type
	 */
	public List<SchemaAttribute> getAttributeList() {
		return attributeList;
	}

	/**
	 * Getter
	 * @return the regular expression which describes the children structure
	 */
	public RegularExpression getRegularExpression() {
		return regularExpression;
	}

	/**
	 * Setter
	 * @param regularExpression the regular expression which describes the children structure.
	 * 							It MAY be null, that means that no regular expression has been 
	 * 							inferred yet for this complex type.
	 */
	public void setRegularExpression(RegularExpression regularExpression) {
		this.regularExpression = regularExpression;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(name);
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
		if (!(obj instanceof ComplexType)) {
			return false;
		}
		ComplexType other = (ComplexType) obj;
//		if (attributeList == null) {
//			if (other.attributeList != null) {
//				return false;
//			}
//		} else if (!attributeList.equals(other.attributeList)) {
//			return false;
//		}
//		if (automaton == null) {
//			if (other.automaton != null) {
//				return false;
//			}
//		} else if (!automaton.equals(other.automaton)) {
//			return false;
//		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
//		if (regularExpression == null) {
//			if (other.regularExpression != null) {
//				return false;
//			}
//		} else if (!regularExpression.equals(other.regularExpression)) {
//			return false;
//		}
//		if (textSimpleType == null) {
//			if (other.textSimpleType != null) {
//				return false;
//			}
//		} else if (!textSimpleType.equals(other.textSimpleType)) {
//			return false;
//		}
		return true;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param automaton the automaton to set
	 */
	public void setAutomaton(ExtendedAutomaton automaton) {
		this.automaton = automaton;
	}

	/**
	 * @param attributeList the attributeList to set
	 */
	public void setAttributeList(List<SchemaAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	/**
	 * Adds an element name and namespace to the set of source elementsAndNamespaces.
	 * This set allows to determine whether two complex types come from elements with the 
	 * same name and namespace, in order to compare them with different comparators at merging 
	 * time (if the configuration is set to do this).
	 * @param namespaceURI The namespace URI of the element
	 * @param name The name of the element
	 * @return true if it has been added
	 */
	public boolean addSourceNodeNamespaceAndName(String namespaceURI,String name) {
		String namespaceURIeffective = (namespaceURI!=null)?namespaceURI:"";
		return sourceElementsNamespaceAndNames.add(namespaceURIeffective+":"+name);
	}

	/**
	 * Adds all the element nameAndNamespaces from a complex type into the set of source 
	 * elementsAndNamespaces.
	 * This set allows to determine whether two complex types come from elements with the 
	 * same name and namespace, in order to compare them with different comparators at merging 
	 * time (if the configuration is set to do this).
	 * @param other the other complex type
	 * @return true if they have been added
	 */
	public boolean addAllTheSourceNodeNamespaceAndNames(ComplexType other) {
		return sourceElementsNamespaceAndNames.addAll(other.sourceElementsNamespaceAndNames);
	}
	
	/**
	 * Returns an unmodifiable view (via {@linkplain Collections#unmodifiableSet(Set)} of the set of source namespacesAndNames.
	 * @return an unmodifiable view (via {@linkplain Collections#unmodifiableSet(Set)} of the set of source namespacesAndNames.
	 */
	public Set<String> getSourceElementNamespacesAndNames(){
		return Collections.unmodifiableSet(sourceElementsNamespaceAndNames);
	}

	/**
	 * Returns the set of comments of the complex type
	 * @return the set of comments
	 */
	public Set<String> getComments() {
		return comments;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ComplexType [name=" + name + "]";
	}
	
}
