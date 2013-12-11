package es.upm.dit.xsdinferencer.generation;

import es.upm.dit.xsdinferencer.Results;
import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.exceptions.XSDConfigurationException;
import es.upm.dit.xsdinferencer.generation.generatorimpl.statisticsgeneration.StatisticResultsDocGeneratorFactory;
import es.upm.dit.xsdinferencer.generation.generatorimpl.xsdgeneration.XSDDocumentGeneratorFactory;
import es.upm.dit.xsdinferencer.util.xsdfilenaming.XSDFileNameGenerator;

/**
 * Main interface for the generator module, which takes an {@link Schema} object and 
 * the inference configuration and generates the results of the inferences process. 
 * Those results will be stored on a {@link Results} object, which will be returned.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public interface ResultsGenerator {
	/**
	 * Method that performs the results generation and returns them.
	 * @param schema the schema object whith all the inference data
	 * @param configuration the inference configuration
	 * @param xsdDocumentGeneratorFactory factory for {@link XSDDocumentGenerator} instances
	 * @param statisticResultsDocGeneratorFactory factory for {@link StatisticResultsDocGenerator} instances
	 * @param xsdFileNameGenerator file name generator to generate XSD file names into a predefined way, if they are going 
	 * to be stored stored on the file system (if null is provided, the namespace URI will be used, which is practical for in-memory 
	 * XSDs but unsuitable for most file systems)
	 * @return a {@link Results} object with the inference results
	 * @throws XSDConfigurationException if the inference configuration is not valid
	 */
	public Results generateResults(Schema schema, XSDInferenceConfiguration configuration, 
			XSDDocumentGeneratorFactory xsdDocumentGeneratorFactory, StatisticResultsDocGeneratorFactory statisticResultsDocGeneratorFactory, 
			XSDFileNameGenerator xsdFileNameGenerator) throws XSDConfigurationException;
}
