package es.upm.dit.xsdinferencer.datastructures;

/**
 * Common interface for {@linkplain SchemaElement} and {@linkplain SchemaAttribute} (i.e. anything 
 * that has a name, a namespace and a type, it does not matter if it is a simple or a complex type).
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public interface SchemaNode {
	/**
	 * 
	 * @return The name of the node
	 */
	public String getName();
	/**
	 * 
	 * @return The namespace URI of the node
	 */
	public String getNamespace();
	/**
	 * Similar to equals but ignores the type
	 */
	public boolean equalsIgnoreType(SchemaNode otherNode);
}
