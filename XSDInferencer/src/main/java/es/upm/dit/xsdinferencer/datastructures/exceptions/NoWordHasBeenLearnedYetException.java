package es.upm.dit.xsdinferencer.datastructures.exceptions;

import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;

/**
 * Exception thrown if anyone attempts to get occurrences info 
 * from an ExtendedAutomaton when no word has been learnt via 
 * the {@link ExtendedAutomaton#learn(java.util.List)} method.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class NoWordHasBeenLearnedYetException extends RuntimeException {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor with default message.
	 */
	public NoWordHasBeenLearnedYetException() {
		super("No word has been learnt yet by this ExtendedAutomaton");
	}

	/**
	 * Constructor with custom message.
	 * @param message a custom message
	 */
	public NoWordHasBeenLearnedYetException(String message) {
		super(message);
	}

}
