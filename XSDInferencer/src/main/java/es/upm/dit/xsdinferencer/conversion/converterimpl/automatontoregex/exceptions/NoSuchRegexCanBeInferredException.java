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
