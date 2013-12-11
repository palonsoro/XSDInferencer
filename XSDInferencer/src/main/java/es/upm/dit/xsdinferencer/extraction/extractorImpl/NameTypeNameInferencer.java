package es.upm.dit.xsdinferencer.extraction.extractorImpl;

import java.util.List;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.extraction.TypeNameInferencer;

/**
 * {@link TypeNameInferencer} implementation, which builds the type name 
 * from the name of the current element.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class NameTypeNameInferencer implements TypeNameInferencer {

	/**
	 * @see TypeNameInferencer#inferTypeName(List, XSDInferenceConfiguration)
	 */
	@Override
	public String inferTypeName(List<String> path, XSDInferenceConfiguration configuration) {
		return path.get(path.size()-1);
	}
}
