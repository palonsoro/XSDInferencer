package es.upm.dit.xsdinferencer.exceptions;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;

/**
 * Base class for exceptions related to the inference configuration
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 * @see XSDInferenceConfiguration
 */
public class XSDConfigurationException extends Exception {

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
