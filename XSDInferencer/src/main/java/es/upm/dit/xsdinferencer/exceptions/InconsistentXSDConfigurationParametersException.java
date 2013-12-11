package es.upm.dit.xsdinferencer.exceptions;

/**
 * Exception thrown when an inconsistency between configuration parameters has been found.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class InconsistentXSDConfigurationParametersException extends
		XSDConfigurationException {
	
	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The default message string for this exception
	 */
	private static final String MESSAGE_STRING = "An inconsistency between two parameters has been found.";

	public InconsistentXSDConfigurationParametersException() {
		super(MESSAGE_STRING);
	}

	public InconsistentXSDConfigurationParametersException(String message) {
		super(message);
	}

	public InconsistentXSDConfigurationParametersException(Throwable cause) {
		super(MESSAGE_STRING,cause);
	}

	public InconsistentXSDConfigurationParametersException(String message,
			Throwable cause) {
		super(message, cause);
	}

	protected InconsistentXSDConfigurationParametersException(String message,
			Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
