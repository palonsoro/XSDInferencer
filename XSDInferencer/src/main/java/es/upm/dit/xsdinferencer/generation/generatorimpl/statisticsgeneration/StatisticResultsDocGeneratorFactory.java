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
