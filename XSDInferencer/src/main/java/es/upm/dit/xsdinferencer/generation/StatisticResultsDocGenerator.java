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
package es.upm.dit.xsdinferencer.generation;

import org.jdom2.Document;

import es.upm.dit.xsdinferencer.statistics.Statistics;

/**
 * Implementations of this interface generate an XML Document with all the statistics info 
 * which has been gathered.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public interface StatisticResultsDocGenerator {
	/**
	 * This method takes an statistics object as input and generates an XML document which 
	 * represents the statistics that it contains.
	 * @param statistics The statistics object whose content is to be represented
	 * @return a JDOM2 {@link Document} with an XML representation of the statistics
	 */
	public Document generateStatisticResultsDoc(Statistics statistics);
}
