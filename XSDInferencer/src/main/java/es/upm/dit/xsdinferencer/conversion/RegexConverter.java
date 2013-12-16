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
package es.upm.dit.xsdinferencer.conversion;

import es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex.exceptions.NoSuchRegexCanBeInferredException;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;

/**
 * Implementations of this interface convert a particular automaton into a regular expression, by means of an 
 * algorithm that depends on the concrete implementation.
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 * 
 */
public interface RegexConverter{
	
	/**
	 * Method that, given an automaton, returns an equivalent regular expression. If no equivalent regular 
	 * expression can be inferred, a less restrictive one may be returned (without any particular advice) or the 
	 * converter may also fail completely by throwing an exception.
	 * @param automaton Automaton to convert
	 * @return The inferred regular expression, which should be equivalent to the input automaton or less restrictive 
	 * @throws NoSuchRegexCanBeInferredException if the implementation is not able to convert the input automaton. 
	 */
	public RegularExpression convertAutomatonToRegex(ExtendedAutomaton automaton) throws NoSuchRegexCanBeInferredException;
}