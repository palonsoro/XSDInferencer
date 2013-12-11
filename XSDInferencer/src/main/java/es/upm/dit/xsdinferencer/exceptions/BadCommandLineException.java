package es.upm.dit.xsdinferencer.exceptions;

/**
 * Exception thrown if someone tries to parse a bad formed command line
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class BadCommandLineException extends XSDConfigurationException {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see Exception#Exception()
	 */
	public BadCommandLineException() {
		super("The command line has got errors");
	}

	/**
	 * @see Exception#Exception(String)
	 */
	public BadCommandLineException(String message) {
		super(message);
	}

	/**
	 * @see Exception#Exception(Throwable)
	 */
	public BadCommandLineException(Throwable cause) {
		super(cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable)
	 */
	public BadCommandLineException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @see Exception#Exception(String, Throwable, boolean, boolean)
	 */
	public BadCommandLineException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
