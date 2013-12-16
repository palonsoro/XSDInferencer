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
package es.upm.dit.xsdinferencer.generation.generatorimpl.xsdgeneration;

import es.upm.dit.xsdinferencer.generation.XSDDocumentGenerator;

/**
 * Factory for {@link XSDDocumentGenerator} implementations
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class XSDDocumentGeneratorFactory {

	/**
	 * Singleton instance
	 */
	protected static final XSDDocumentGeneratorFactory singletonInstance = new XSDDocumentGeneratorFactory();
	
	/**
	 * Private constructor to avoid instantiation
	 */
	private XSDDocumentGeneratorFactory(){
		
	}
	
	/**
	 * Method that returns the singleton instance of the factory
	 * @return the singleton instance
	 */
	public static XSDDocumentGeneratorFactory getInstance(){
		return singletonInstance;
	}
	
	/**
	 * Returns an {@link XSDDocumentGeneratorImpl} instance
	 * @return an {@link XSDDocumentGeneratorImpl} instance
	 */
	public XSDDocumentGenerator getXSDDocumentGeneratorInstance(){
		return new XSDDocumentGeneratorImpl();
	}
	
}
