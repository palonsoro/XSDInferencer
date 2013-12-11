package es.upm.dit.xsdinferencer.extraction.extractorImpl;

import java.util.List;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.extraction.TypeNameInferencer;

/**
 * {@link TypeNameInferencer} implementation, that builds the type names from 
 * the full path of the inferred element.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class FullPathTypeNameInferencer implements TypeNameInferencer {

	/**
	 * @see TypeNameInferencer#inferTypeName(List, XSDInferenceConfiguration)
	 */
	@Override
	public String inferTypeName(List<String> path, XSDInferenceConfiguration configuration) {
		String result="";
		for(int i=0;i<path.size();i++){
			result+=path.get(i);
			if(i<path.size()-1)
				result+=configuration.getTypeNamesAncestorsSeparator();
		}
		return result;
	}
}
