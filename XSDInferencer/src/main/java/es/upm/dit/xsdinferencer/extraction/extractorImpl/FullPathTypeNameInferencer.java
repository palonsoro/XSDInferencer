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
