package es.upm.dit.xsdinferencer.util.xsdfilenaming;

import java.util.Map;

/**
 * Default implementation for {@link XSDFileNameGenerator}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class XSDFileNameGeneratorDefaultImpl implements XSDFileNameGenerator {

	/**
	 * @see XSDFileNameGenerator#getSchemaDocumentFileName(String, Map)
	 */
	@Override
	public String getSchemaDocumentFileName(String targetNamespace, 
			Map<String, String> namespaceURIToPrefixMappings){
		String fileName="schema-";
		String prefix = namespaceURIToPrefixMappings.get(targetNamespace);
		if(targetNamespace.equals("")){
			fileName+="no_ns";
		}
		else if(prefix.equals("")){
			fileName+="unprefixed_ns";
		}
		else{
			fileName+="ns_"+prefix;
		}
		fileName+=".xsd";
		return fileName;
	}

}
