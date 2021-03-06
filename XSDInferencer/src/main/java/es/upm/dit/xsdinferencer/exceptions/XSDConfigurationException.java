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
package es.upm.dit.xsdinferencer.exceptions;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;

/**
 * Base class for exceptions related to the inference configuration
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 * @see XSDInferenceConfiguration
 */
public class XSDConfigurationException extends XSDInferencerException {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 1L;

	public XSDConfigurationException() {
	}

	public XSDConfigurationException(String message) {
		super(message);
	}

	public XSDConfigurationException(Throwable cause) {
		super(cause);
	}

	public XSDConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	protected XSDConfigurationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
