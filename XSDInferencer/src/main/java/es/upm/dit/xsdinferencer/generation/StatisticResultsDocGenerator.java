
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
