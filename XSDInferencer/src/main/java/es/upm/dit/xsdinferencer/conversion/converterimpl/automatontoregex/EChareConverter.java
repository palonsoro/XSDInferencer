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
package es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableSet;

import es.upm.dit.xsdinferencer.conversion.RegexConverter;
import es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex.exceptions.NoSuchRegexCanBeInferredException;
import es.upm.dit.xsdinferencer.datastructures.All;
import es.upm.dit.xsdinferencer.datastructures.EquivalenceClass;
import es.upm.dit.xsdinferencer.datastructures.ExtendedAutomaton;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;

/**
 * This converter uses the eCRX algorithm to infer an eCHARE from a given automaton.
 * Note that this converter will never throw an {@linkplain NoSuchRegexCanBeInferredException} although the interface requires to declare 
 * it, as eCRX never fails (in the worst situation, it infers an eCHARE which is quite more general than the input automaton).
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
class EChareConverter extends ChareConverter implements RegexConverter {

	/**
	 * This method implements the only differences between eCRX and CRX, by performing the additional operations 
	 * described at eCRX.
	 * @param automaton the elements automaton
	 * @param topologicallySortedNodeList the topologically sorted node list of equivalence classes.
	 * @return an eCHARE
	 * @see ChareConverter#buildRegularExpression(ExtendedAutomaton, List)
	 */
	@Override
	protected RegularExpression buildRegularExpression(
			ExtendedAutomaton automaton,
			List<EquivalenceClass> topologicallySortedNodeList) {
		if(topologicallySortedNodeList.size()==1){
			EquivalenceClass eqClass = topologicallySortedNodeList.get(0);
			Map<String, Integer> factorSymbolMinMaxOccurrences = automaton.getFactorSymbolMinMaxOccurrences(eqClass);
			int symbolMax = factorSymbolMinMaxOccurrences.get("max");
			int symbolMin = factorSymbolMinMaxOccurrences.get("min");
			Map<String, Integer> factorMinMaxOccurrences = automaton.getFactorMinMaxOccurrences(eqClass);
			int max = factorMinMaxOccurrences.get("max");
//			int min = factorMinMaxOccurrences.get("min");
			if((eqClass.size() > 1) &&
					(symbolMax==1) &&
					(max>1)){
				ImmutableSet<SchemaElement> nodesOfAll = ImmutableSet.copyOf(eqClass);
				All resultingAll = new All(nodesOfAll);
				if(symbolMin==0)
					resultingAll.setMinOccurs(0);
				return resultingAll;
			}
		}
		return super.buildRegularExpression(automaton, topologicallySortedNodeList);
	}

	
}
