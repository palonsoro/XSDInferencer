package es.upm.dit.xsdinferencer.datastructures.exceptions;

/**
 * Exception when an operation fails because it only can be performed 
 * on an acyclic graph
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class NonAcyclicGraphException extends RuntimeException {
	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor with a default message
	 */
	public NonAcyclicGraphException(){
		super("This operation may only be performed on an acyclic graph");
	}
	
	/**
	 * Constructor with custom message
	 * @param message the custom message
	 */
	public NonAcyclicGraphException(String message){
		super(message);
	}
}
