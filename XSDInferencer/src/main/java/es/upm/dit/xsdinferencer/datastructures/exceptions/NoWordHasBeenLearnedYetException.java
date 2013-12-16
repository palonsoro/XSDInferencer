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
