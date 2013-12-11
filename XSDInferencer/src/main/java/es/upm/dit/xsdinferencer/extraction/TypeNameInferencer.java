package es.upm.dit.xsdinferencer.extraction;

import java.util.List;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;

/**
 * A TypeNameInferencer determines which name should have the complex type of an element on a given 
 * path, based on criteria of the concrete implementation and on the configuration.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface TypeNameInferencer{
	/**
	 * It infers the type name of an element on a given path
	 * @param path a List which contains all the path elements in the correct order. Note that they must not contain invalid characters according to the NCName XML type specification.
	 * @param configuration the inference configuration
	 * @return the complex type name
	 */
	public String inferTypeName(List<String> path, XSDInferenceConfiguration configuration);
}
