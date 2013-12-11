package es.upm.dit.xsdinferencer.generation;

import org.jdom2.Document;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.util.xsdfilenaming.XSDFileNameGenerator;

/**
 * This generator generates an individual XSD document for a given targetNamespace (it means, it must be called 
 * once for each namespace whose inference is intended to be done). The XSD generated will vary depending on whether 
 * the target namespace is the main namespace (the one which will contain the global complex type and simple type 
 * declarations) or not.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface XSDDocumentGenerator {
	/**
	 * Method that generates the XSD for a given target namespace.
	 * @param schema the current schema
	 * @param configuration the inference configuration
	 * @param targetNamespace the target namespace whose XSD will be generated
	 * @param mainNamespace the main namespace
	 * @param fileNameGenerator The XSD file name generator used at import tags, which allow validators to find them in the filesystem. If null, no file names will be generated. 
	 * @return a JDOM2 document with the generated XSD
	 * @throws IllegalArgumentException if no XSD could be generated for the given targetNamespace, for example, because it is an skipped namespace.
	 */
	public Document generateSchemaDocument(Schema schema, XSDInferenceConfiguration configuration,
			String targetNamespace, String mainNamespace, XSDFileNameGenerator fileNameGenerator);
}
