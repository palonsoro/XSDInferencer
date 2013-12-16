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
