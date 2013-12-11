package es.upm.dit.xsdinferencer.util.xsdfilenaming;

import java.util.Map;

/**
 * Generates the file name for an XML Schema Document, given its target namespace and the solved mappings between 
 * namespace URIs and prefixes. 
 * The way the names are genereted depends on the implementation.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface XSDFileNameGenerator {
	/**
	 * Returns the file name for the schema document of a given target namespace.
	 * @param targetNamespace the target namespace URI
	 * @param namespaceURIToPrefixMappings a Map<String,String> which maps namespace URIs to their corresponding prefixes.
	 * @return the file name (with extension but without a directory)
	 */
	public String getSchemaDocumentFileName(String targetNamespace, Map<String, String> namespaceURIToPrefixMappings);
	
}
