package es.upm.dit.xsdinferencer.extraction.extractorImpl;

import java.util.List;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.extraction.TypeNameInferencer;

/**
 * {@link TypeNameInferencer} implementation, which builds type names from 
 * the name of the elements and up to k ancestor names (name means qualified name).
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class KLocalTypeNameInferencer implements TypeNameInferencer {
	
	/**
	 * The locality
	 */
	private int locality;
	
	/**
	 * Constructor
	 * @param locality the locality
	 */
	public KLocalTypeNameInferencer(int locality) {
	 this.locality=locality;
	}

	/**
	 * @see TypeNameInferencer#inferTypeName(List, XSDInferenceConfiguration)
	 */
	@Override
	public String inferTypeName(List<String> path, XSDInferenceConfiguration configuration) {
		String result="";
		int start = path.size()-1-locality;
		if(start<0)
			start=0;
		for(int i=start;i<path.size();i++){
			result+=path.get(i);
			if(i<path.size()-1)
				result+=configuration.getTypeNamesAncestorsSeparator();
		}
		return result;
	}

	/**
	 * @return the locality
	 */
	public int getLocality() {
		return locality;
	}

}
