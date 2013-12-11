package es.upm.dit.xsdinferencer.datastructures;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

import org.apache.xerces.util.XML11Char;

/**
 * A possible attribute, which may occur on those elements under a complex type 
 * which holds it in its attribute list.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class SchemaAttribute implements SchemaNode{
	
	/**
	 * The name of the attribute
	 */
	private String name;
	
	/**
	 * Whether it must be checked as optional or required.
	 */
	private boolean optional = false;
	
	/**
	 * The simpleType of the attribute.
	 */
	private SimpleType simpleType;
	
	/**
	 * The namespace of the attribute
	 */
	private String namespace;
	
	/**
	 * Constructor.
	 * @param name Name of the attribute
	 * @param namespace the namespace of the attribute. If it is null, an empty namespace will be set. This will be equivalent to say that the attribute is unqualified.
	 * @param optional whether the attribute is optional or not
	 * @param simpleType simple type of the text of the attribute
	 * @throws NullPointerException if a null simpleType or name is provided
	 */
	public SchemaAttribute(String name, String namespace, boolean optional, SimpleType simpleType) {
		if(simpleType==null||name ==null) 
			throw new NullPointerException("'simpleType' and 'name' must not be null)");
		if(!XML11Char.isXML11ValidNCName(name)){
			String exceptionText="'name' must be a valid NCName, according to the XML 1.1 specification. \n";
			exceptionText+="Remember that no prefixes must be included here because the namespace is set via its own field";
			throw new IllegalArgumentException(exceptionText);
		}
		this.name = name;
		this.optional = optional;
		this.simpleType = simpleType;
		if(namespace==null){
			this.namespace="";
		} else {
			this.namespace=namespace;
		}
	}
	
	/**
	 * Copy constructor
	 * @param other another SchemaAttribute
	 */
	public SchemaAttribute(SchemaAttribute other){
		this(other.name,other.namespace,other.optional,other.simpleType);
	}

	/**
	 * Getter
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @return if it is optional
	 */
	public boolean isOptional() {
		return optional;
	}

	/**
	 * Allows to mark the attribute as optional (true) or required (false)
	 * @param optional If it is now optional or not
	 */
	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	/**
	 * @return the simpleType of the attribute
	 */
	public SimpleType getSimpleType() {
		return simpleType;
	}

	/**
	 * Sets the simple type of the attribute
	 * @param simpleType the simpleType to set
	 * @throws NullPointerException if a null value is provided
	 */
	public void setSimpleType(SimpleType simpleType) {
		if(simpleType==null) 
			throw new NullPointerException("'simpleType' must not be null)");
		this.simpleType = simpleType;
	}
	
	
	/**
	 * @return the namespace of the attribute
	 */
	@Override
	public String getNamespace() {
		return namespace;
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
		if (!(obj instanceof SchemaAttribute)) {
			return false;
		}
		SchemaAttribute other = (SchemaAttribute) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
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
		return equals(otherNode);
	};

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hash(name,namespace);
	}
	
	/**
	 * @see Object#toString()
	 */
	@Override
	public String toString(){
		return getNamespace()+":"+getName();
	}

	/**
	 * Returns the first index of a List of SchemaAttributes such that the SchemaAttribute at that position 
	 * has the same name and namespace than a given one
	 * @param list the list
	 * @param schemaAttribute the SchemaAttribute to compare
	 * @return the first index, or -1 if not found
	 */
	public static int indexOf(List<SchemaAttribute> list, SchemaAttribute schemaAttribute){
		checkNotNull(list);
		checkNotNull(schemaAttribute);
		for(int i=0;i<list.size();i++){
			SchemaAttribute currentSchemaAttribute=list.get(i);
			if(schemaAttribute.getName().equals(currentSchemaAttribute.getName())&&
					schemaAttribute.getNamespace().equals(currentSchemaAttribute.getNamespace()))
				return i;
		}
		return -1;
		
	}
	
	/**
	 * Setter
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace){
		this.namespace=namespace;
	}
}
