package es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex.exceptions;

/**
 * This exception should be thrown when a regular expression inference algorithm fails. 
 * This may happen even in normal conditions, because some algorithms are mathematically known to 
 * fail on some input automatons. 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class NoSuchRegexCanBeInferredException extends Exception {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor with a default message.
	 */
	public NoSuchRegexCanBeInferredException(){
		super("No regular expression of the desired kind can be inferred by means of this algorithm for this automaton.");
	}
	
	/**
	 * Constructor with a message
	 * @param message
	 */
	 public NoSuchRegexCanBeInferredException(String message){
		 super(message);
	 }
	
}
