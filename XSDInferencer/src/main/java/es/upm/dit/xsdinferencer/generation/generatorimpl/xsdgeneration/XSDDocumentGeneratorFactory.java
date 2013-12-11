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
