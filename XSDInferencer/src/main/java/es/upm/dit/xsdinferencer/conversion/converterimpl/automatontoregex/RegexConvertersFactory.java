package es.upm.dit.xsdinferencer.conversion.converterimpl.automatontoregex;

import es.upm.dit.xsdinferencer.conversion.RegexConverter;

/**
 * Factory for current implementations of {@link RegexConverter}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class RegexConvertersFactory {
	
	/**
	 * Type string for a {@link SoreConverter}
	 */
	public static final String SORE_CONVERTER = "sore";
	
	/**
	 * Type string for a {@link ChareConverter}
	 */
	public static final String CHARE_CONVERTER = "chare";
	
	/**
	 * Type string for an {@link EChareConverter}
	 */
	public static final String ECHARE_CONVERTER = "echare";
	
	/**
	 * Singleton instance of the class
	 */
	private static RegexConvertersFactory singletonInstance = null;
	
	/**
	 * Private constructor to avoid direct instantiation
	 */
	private RegexConvertersFactory(){}
	
	/**
	 * Static method that returns the single instance of the factory, creating it when necessary.
	 * @return the single instance of the class
	 */
	public static RegexConvertersFactory getInstance(){
		if(singletonInstance==null)
			singletonInstance = new RegexConvertersFactory();
		return singletonInstance;
	}
	
	/**
	 * Method that returns an instance of the desired type, depending on the input type string.
	 * @param type the type. The suitable ones for this class are specified as public constants. Other classes that extend this one may accept different ones. 
	 * @return the desired converter
	 * @throws IllegalArgumentException if the type string is unknown
	 */
	public RegexConverter getRegexConverterInstance(String type){
		if(type.equals(SORE_CONVERTER)){
			return new SoreConverter();
		}
		else if(type.equals(CHARE_CONVERTER)) {
			return new ChareConverter();
		}
		else if(type.equals(ECHARE_CONVERTER)){
			return new EChareConverter();
		}
		else {
			throw new IllegalArgumentException("Unknown type: "+type);
		}
	}
	
	/**
	 * Method that returns an instance of the desired type, depending on the input boolean flags extracted from the configuration.
	 * @param avoidSORE If false, a {@link SoreConverter} is returned, if true, the result depends on tryECHARE
	 * @param tryECHARE If it is true and avoidSORE true, an {@link EChareConverter} is returned. If it is false and avoidSORE is true, a {@link ChareConverter} is returned. If avoidSORE is false, this is ignored.
	 * @return the desired converter
	 * @throws IllegalArgumentException if the type string is unknown
	 */
	public RegexConverter getRegexConverterInstance(boolean avoidSORE, boolean tryECHARE){
		if(!avoidSORE){
			return new SoreConverter();
		}
		else if (!tryECHARE) {
			return new ChareConverter();
		}
		else {
			return new EChareConverter();
		}
	}
}
