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
package es.upm.dit.xsdinferencer.extraction.extractorImpl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.extraction.AttributeListInferencer;
import es.upm.dit.xsdinferencer.extraction.SimpleTypeInferencer;
import es.upm.dit.xsdinferencer.statistics.Statistics;

/**
 * Factory whose methods return the correct implementations of {@linkplain SimpleTypeInferencer} and 
 * {@linkplain AttributeListInferencer} according to the values of the configuration provided. 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class InferencersFactory {
	
	/**
	 * Singleton instance
	 */
	protected static final InferencersFactory instance;
	
	static{
		instance = new InferencersFactory();
	}
	
	/**
	 * Private constructor to avoid instantiation
	 */
	private InferencersFactory(){}
	
	/**
	 * @return The singleton instance
	 */
	public static InferencersFactory getInstance(){
		return instance;
	}

	/**
	 * Method that returns the correct implementation of SimpleTypeInferencer according to the configuration.
	 * @param source source attribute or complex type name
	 * @param config the current inference configuration
	 * @return A new SimpleTypeInferencer, whose implementation is the one indicated by the configuration
	 * @throws NullPointerException if the argument is null
	 */
	public SimpleTypeInferencer getSimpleTypeInferencerInstance(String source, XSDInferenceConfiguration config){
		checkNotNull(config,"null arguments are not allowed");
		//source is not really necessary at this implementation, but only at mocked versions of it.
		//however, it might be useful in future revisions of this implementation or to other implementations.
		if(config.getSimpleTypeInferencer().equalsIgnoreCase(XSDInferenceConfiguration.VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL)){
			return new SimpleTypeInferencerImpl(config);
		}
		else{
			throw new IllegalArgumentException("Unknown simple type inferencer");
		}
	}

	/**
	 * Returns a new instance of AttributeListInferencer, whose implementation is the one indicated in the configuration
	 * @param parentComplexTypeName name of the complex type which will contain the attribute list (necessary to build the inferencer)
	 * @param config current inference configuration
	 * @param solvedNamespaceToPrefixMapping solved mappings between namespaces URIs and prefixes
	 * @param statistics the statistics of the schema, in order to register statistics related to the attributes inferred
	 * @return A new AttributeListInferencer as described.
	 * @throws NullPointerException if any argument is null
	 */
	public AttributeListInferencer getAttributeListInferencerInstance(String parentComplexTypeName, XSDInferenceConfiguration config, Map<String, String> solvedNamespaceToPrefixMapping, Statistics statistics){
		checkNotNull(parentComplexTypeName,"null arguments are not allowed");
		checkNotNull(config,"null arguments are not allowed");
		checkNotNull(statistics,"null arguments are not allowed");
		if(config.getAttributeListInferencer().equalsIgnoreCase(XSDInferenceConfiguration.VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL)){
			return new AttributeListInferencerImpl(parentComplexTypeName, config, statistics, solvedNamespaceToPrefixMapping);
		}
		else{
			throw new IllegalArgumentException("Unknown attribute list inferencer");
		}
	}

}
