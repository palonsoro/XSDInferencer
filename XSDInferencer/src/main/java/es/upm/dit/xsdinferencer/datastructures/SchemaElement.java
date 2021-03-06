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

import java.util.Objects;

import org.apache.xerces.util.XMLChar;

/**
 * A possible element, which may occur as root element if the schema allows it or 
 * as a child of other elements, in the ways the children structure representation 
 * of their respective complex types allow it. 
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class SchemaElement implements RegularExpression, SchemaNode{
	
	/**
	 * The name of the element
	 */
	private String name;
	
	/**
	 * The complex type which describes the element
	 */
	private ComplexType type;
	
	/**
	 * Namespace of the element
	 */
	private String namespace;
	
	/**
	 * If true, the element may be a root of a document
	 */
	private boolean isValidRoot;
	
	/**
	 * Constructor
	 * @param name name of the complex type
	 * @param namespace the namespace of the element. If it is null, an empty namespace will be set.
	 * @param complexType complex type of the element (it should not be null except for the initial and the final state)
	 * @throws NullPointerException if name is null
	 * @throws IllegalArgumentException if the name is not a valid NCName
	 */
	public SchemaElement(String name, String namespace, ComplexType complexType){
		if(name==null)
			throw new NullPointerException("'Name' must not be null");
		if(!XMLChar.isValidNCName(name)){
			String exceptionText="'name' must be a valid NCName, according to the XML 1.0 specification. \n";
			exceptionText+="Remember that no prefixes must be included here because the namespace is set via its own field";
			throw new IllegalArgumentException(exceptionText);
		}
		this.name=name;
		this.type=complexType;
		if(namespace==null){
			this.namespace="";
		} else {
			this.namespace=namespace;
		}
		setValidRoot(false);
	}
	
	/**
	 * Copy constructor
	 * @param other the SchemaElement to copy
	 */
	public SchemaElement(SchemaElement other){
		this.name=other.name;
		this.type=other.type;
		setValidRoot(other.isValidRoot);
		this.namespace=other.namespace;
	}
	
	/**
	 * Implementation of RegularExpression.
	 * We see an element as a regular expression of elements which contains a single element (the element itself).
	 * @param index must be one in this implementation
	 * @return this if index==0 and null otherwise
	 * @see RegularExpression#getElement(int)
	 */
	@Override
	public RegularExpression getElement(int index) {
		if (index==0)
			return this;
		else {
			return null;
		}
	}
	
	/**
	 * Implementation of RegularExpression.
	 * We see an element as a regular expression of elements which contains a single element (the element itself).
	 * @return 1
	 * @see RegularExpression#elementCount()
	 */
	@Override
	public int elementCount() {
		return 1;
	}

	/**
	 * Getter
	 * @return the complex type which describes the element
	 */
	public ComplexType getType() {
		return type;
	}

	/**
	 * Setter
	 * @param type the complex type to set
	 * @throws NullPointerException if type is null
	 */
	public void setType(ComplexType type) {
		if(type==null)
			throw new NullPointerException("'type' must not be null");
		this.type = type;
	}

	/**
	 * Getter
	 * @return the name of the element
	 */
	@Override
	public String getName() {
		return name;
	}

	//Autogenerated
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((name == null) ? 0 : name.hashCode());
//		result = prime * result + ((type == null) ? 0 : type.hashCode());
//		return result;
		return Objects.hash(type,name,namespace);
//		return Objects.hash(name,namespace);
	}

	/**
	 * Autogenerated
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
		if (!(obj instanceof SchemaElement)) {
			return false;
		}
		SchemaElement other = (SchemaElement) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		
		if (namespace == null) {
			if (other.namespace != null) {
				return false;
			}
		} else if (!namespace.equals(other.namespace)) {
			return false;
		}
		
		return true;
	}

	/**
	 * @see SchemaNode#equalsIgnoreType(SchemaNode)
	 */
	@Override
	public boolean equalsIgnoreType(SchemaNode otherNode) {
		if (this == otherNode) {
			return true;
		}
		if (otherNode == null) {
			return false;
		}
		if (!(otherNode instanceof SchemaElement)) {
			return false;
		}
		SchemaElement other = (SchemaElement) otherNode;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
//		if (type == null) {
//			if (other.type != null) {
//				return false;
//			}
//		} else if (!type.equals(other.type)) {
//			return false;
//		}
		
		if (namespace == null) {
			if (other.namespace != null) {
				return false;
			}
		} else if (!namespace.equals(other.namespace)) {
			return false;
		}
		
		return true;
	}

	
	/**
	 * Getter
	 * @return the namespace of the element
	 */
	@Override
	public String getNamespace() {
		return namespace;
	}

	/**
	 * Getter.
	 * @return whether the element can be used as a root of a document or not
	 */
	public boolean isValidRoot() {
		return isValidRoot;
	}

	/**
	 * Setter.
	 * @param isValidRoot whether the element can be used as a root of a document
	 */
	public void setValidRoot(boolean isValidRoot) {
		this.isValidRoot = isValidRoot;
	}
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString(){
		return namespace+":"+name;
	}

	/**
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(RegularExpression other) {
		return this.toString().compareTo(other.toString());
	}

	/**
	 * @see RegularExpression#containsElement(RegularExpression)
	 */
	@Override
	public boolean containsElement(RegularExpression element) {
		return element.equals(this);
	}
	
	/**
	 * This implementation does not support this method.
	 * @throws UnsupportedOperationException because the method is not supported
	 * @see RegularExpression#setElement(int, RegularExpression)
	 */
	@Override
	public void setElement(int index, RegularExpression element) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Setter
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace){
		this.namespace=namespace;
	}
	
}
