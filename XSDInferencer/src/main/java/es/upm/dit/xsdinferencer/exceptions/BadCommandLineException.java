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
