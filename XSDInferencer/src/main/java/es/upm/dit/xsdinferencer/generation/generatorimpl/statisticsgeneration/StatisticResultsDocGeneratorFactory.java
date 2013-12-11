package es.upm.dit.xsdinferencer.generation.generatorimpl.statisticsgeneration;

import es.upm.dit.xsdinferencer.generation.StatisticResultsDocGenerator;

/**
 * Factory for {@link StatisticResultsDocGenerator} implementations
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class StatisticResultsDocGeneratorFactory {

	/**
	 * Singleton instance
	 */
	protected static final StatisticResultsDocGeneratorFactory singletonInstance = new StatisticResultsDocGeneratorFactory();
	
	/**
	 * Private constructor to avoid instantiation
	 */
	private StatisticResultsDocGeneratorFactory(){
		
	}
	
	/**
	 * Returns the singleton instance
	 * @return the singleton instance
	 */
	public static StatisticResultsDocGeneratorFactory getInstance(){
		return singletonInstance;
	}
	
	/**
	 * Returns an instance of {@link StatisticResultsDocGeneratorImpl}
	 * @return an instance of {@link StatisticResultsDocGeneratorImpl}
	 */
	public StatisticResultsDocGenerator getStatisticResultsDocGeneratorInstance(){
		return new StatisticResultsDocGeneratorImpl();
	}
}
