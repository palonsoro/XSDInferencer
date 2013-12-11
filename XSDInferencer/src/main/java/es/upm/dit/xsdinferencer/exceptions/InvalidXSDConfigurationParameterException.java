package es.upm.dit.xsdinferencer.exceptions;

/**
 * Exception thrown when an invalid value of parameter is read, either 
 * from the command line or a configuration file.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class InvalidXSDConfigurationParameterException extends XSDConfigurationException {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The default message string for this exception
	 */
	private static final String MESSAGE_STRING = "An invalid parameter value has been found";

	public InvalidXSDConfigurationParameterException() {
		super(MESSAGE_STRING);
	}

	public InvalidXSDConfigurationParameterException(String message) {
		super(message);
		
	}

	public InvalidXSDConfigurationParameterException(Throwable cause) {
		super(MESSAGE_STRING,cause);
		
	}

	public InvalidXSDConfigurationParameterException(String message,
			Throwable cause) {
		super(message, cause);
	}

	public InvalidXSDConfigurationParameterException(String message,
			Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
