package es.upm.dit.xsdinferencer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.xerces.util.XML11Char;

import com.google.common.base.Predicates;
import com.google.common.collect.Sets;

import es.upm.dit.xsdinferencer.conversion.RegexOptimizer;
import es.upm.dit.xsdinferencer.conversion.converterimpl.optimization.RegexOptimizersFactory;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.exceptions.BadCommandLineException;
import es.upm.dit.xsdinferencer.exceptions.InconsistentXSDConfigurationParametersException;
import es.upm.dit.xsdinferencer.exceptions.InvalidXSDConfigurationParameterException;
import es.upm.dit.xsdinferencer.exceptions.XSDConfigurationException;
import es.upm.dit.xsdinferencer.extraction.TypeNameInferencer;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.FullPathTypeNameInferencer;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.KLocalTypeNameInferencer;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.NameTypeNameInferencer;
import es.upm.dit.xsdinferencer.merge.AttributeListComparator;
import es.upm.dit.xsdinferencer.merge.ChildrenPatternComparator;
import es.upm.dit.xsdinferencer.merge.EnumComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.attribute.EqualsAttributeComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.attribute.MergeAttributeComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.attribute.SameAttributeComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.attribute.StrictAttributeComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.children.EqualsPatternComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.children.NodeBasedPatternComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.children.NodeSubsumptionPatternComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.children.ReducePatternComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.children.SubsumptionPatternComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.enumeration.MinIntersectionBidirectionalEnumComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.enumeration.MinIntersectionUnidirectionalEnumComparator;

/**
 * Objects of this class represent inference configurations.
 * Constructors, constants and auxiliar methods are provided in order to 
 * help at configurations building
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class XSDInferenceConfiguration {
	
	/**
	 * Main namespace of the inference.
	 * If it is not set, it will be guessed at runtime (look at {@link Schema#guessMainNamespace(XSDInferenceConfiguration)} for details).
	 * A skipped namespace may never be the main namespace.
	 */
	private String mainNamespace = null;
	/**
	 * A list of namespaces whose schema must not be inferenced.
	 * Note that, if a namespace is skipped, a correct schema of that namespace should be 
	 * available if the generated XSDs are going to be used to validate XML files, otherwise, 
	 * the validation would fail. 
	 * It is important to remark, that XML Schema instance namespace (http://www.w3.org/2001/XMLSchema-instance) 
	 * WILL always be skipped, regardless of it is present at this list or not (in fact, it will be completely ignored 
	 * by the inferencer).
	 */
	private List<String> skipNamespaces = new ArrayList<String>();
	/**
	 * The type name inferencer to use
	 */
	private TypeNameInferencer typeNameInferencer;
	/**
	 * The simple type inferencer to use (the implementation to instantiate)
	 */
	private String simpleTypeInferencer = VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL;
	/**
	 * The attribute list inferencer to use
	 */
	private String attributeListInferencer = VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL;
	/**
	 * Flag to indicate whether enumerations must be generated or not
	 */
	private boolean generateEnumerations = false;
	/**
	 * Minimum number of distinct values of a single type to generate an enumeration
	 */
	private int minNumberOfDistinctValuesToEnum = 0;
	/**
	 * Maximum number of distinct values of a single type to generate an enumeration
	 */
	private int maxNumberOfDistinctValuesToEnum = 10;
	/**
	 * Children comparator that compares the children structures (automatons) of two complex types 
	 * and decides if they are similar enough, according to its own criterion.
	 * If it was null, there would not be any merge of complex types.
	 */
	private ChildrenPatternComparator childrenPatternComparator = null;
	/**
	 * Attribute comparator that compares the attributes list of two complex types 
	 * and decides if they are similar enough, according to its own criterion.
	 * If it was null, there would not be any merge of complex types.
	 */
	private AttributeListComparator attributeListComparator = null;
	/**
	 * Children comparator that compares the children structures (automatons) of two complex types 
	 * and decides if they are similar enough, according to its own criterion.
	 * This one is the one used in the first phase of type merge, when complex types such that 
	 * some of their source elements have the same name are mixed after comparing them with different comparators.
	 * If childrenPatternComparator or attributeComparator were null, there would be no merge and this 
	 * setting would not have any effect.
	 * If it was null but snAttributeComparator is not, childrenPatternComparator would be used instead.
	 * If both snChildrenPatternComparator and snAttributeComparator were null, there would not be any 
	 * the first phase of merging complex types of elements with the same name would not take place.
	 */
	private ChildrenPatternComparator snChildrenPatternComparator = null;
	/**
	 * Attribute comparator that compares the attributes list of two complex types 
	 * and decides if they are similar enough, according to its own criterion.
	 *This one is the one used in the first phase of type merge, when complex types such that 
	 * some of their source elements have the same name are mixed after comparing them with different comparators.
	 * If childrenPatternComparator or attributeComparator were null, there would be no merge and this 
	 * setting would not have any effect.
	 * If it was null but snChildrenComparator is not, attributeComparator would be used instead.
	 * If both snChildrenPatternComparator and snAttributeComparator were null, there would not be any 
	 * the first phase of merging complex types of elements with the same name would not take place.
	 */
	private AttributeListComparator snAttributeListComparator = null;
	
	/**
	 * The comparator used to decide if two enums are 
	 * similar enough to be merged. 
	 * If it was null, there would be no enum merging.
	 */
	private EnumComparator enumsComparator = null;
	
	/**
	 * The comparator used to decide if two enums which appear in two elements with the same name are 
	 * similar enough to be merged. 
	 * This one is the one used in the first phase of type merge, when complex types such that 
	 * some of their source elements have the same name are mixed after comparing them with different comparators.
	 * If it was null, there would be no enum merging.
	 */
	private EnumComparator snEnumsComparator = null;
	/**
	 * If this flag is true, the converter won't not try to infer a SORE but will directly try to 
	 * infer an eCHARE or a CHARE, according to the value of tryECHARE.
	 */
	private boolean avoidSORE = false;
	/**
	 * When a SORE conversion fails (or avoidSORE is true), if this flag is true, the converter 
	 * would try to infer an eCHARE, else, the converter would try to infer a CHARE.
	 */
	private boolean tryECHARE = true;
	/**
	 * A list of the optimizers that should be run on the optimization step of the converter.
	 */
	private List<RegexOptimizer> optimizers = new ArrayList<RegexOptimizer>();
	/**
	 * If this flags is true, elements and attributes in namespaces which are not the main namespace 
	 * will be surrounded by groups and not declared as global elements in order to avoid them to become 
	 * valid root elements and to allow them to have distinct complex types when they have same names.
	 * If this flag was false when there are multiple namespaces, the only allowed TypeNameInferencer 
	 * would be NameTypeInferencer because it is not possible to declare two global elements with the 
	 * same name or a global element with two types.
	 */
	private boolean strictValidRootDefinitionWorkaround = true;
	/**
	 * If this flag is true, elements are generated globally in the XSD and referenced with ref when necessary.
	 * If this flag was true, the only allowed TypeNameInferencer would be NameTypeInferencer because it is 
	 * not possible to declare two global elements with the same name or a global element with two types. 
	 */
	private boolean elementsGlobal = false;
	/**
	 * If this flag is true, complexType tags will be declared globally and referenced when necessary. 
	 * If this flag is false, each complexType will be included into each element tag which represents 
	 * an element of that complex type.
	 */
	private boolean complexTypesGlobal = true;
	/**
	 * If this flag is true, simpleType tags referring to enumerations will be declared globally and 
	 * referenced when necessary. 
	 * If this flag is false, each element or attribute whose simpleType is an inferred enumeration 
	 * will include into its corresponding tag a simpleType tag describing the enumeration.
	 * No simpleType tag will be created to refer to a built-in type. 
	 */
	private boolean simpleTypesGlobal = true;
	/**
	 * Separator used to build type names based on ancestors and in some other situations. 
	 * For example, a KLocalTypeNameInferencer with k=1 would build a type name like "ancestor-element" 
	 * if typeNamesSeparator is "-".
	 * It must be a valid NCName substring (made of non-first characters allowed in NCNames).  
	 */
	private String typeNamesAncestorsSeparator = "-";
	/**
	 * Separator used to build type names of merged types. 
	 * For example, if the types "ancestor1-element1" and "ancestor2-element2" are mixed, 
	 * the name of the new type will be "ancestor1-element1_and_ancestor2-element2" if the separator 
	 * is "_and_".
	 * It must be a valid NCName substring (made of non-first characters allowed in NCNames).
	 */
	private String mergedTypesSeparator = "_and_";
	
	/**
	 * XSI (XML Schema Instance) namespace URI.
	 */
	public static final String XSI_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema-instance";
	
	/**
	 * XSD Namespace URI
	 */
	public static final String XSD_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";
	
	/**
	 * XML Namespace (browse the URI for more info)
	 */
	public static final String XML_NAMESPACE_URI = "http://www.w3.org/XML/1998/namespace";
	
	/**
	 * Namespace prefix for the XSD XML tags, which must be used as prefix of builtin simple types.
	 * It MUST end with :
	 */
	public static final String XSD_NAMESPACE_PREFIX = "xs:";
	
	//Some default values for completely optional parameters
	public static final String DEFAULT_REDUCE_THRESHOLD = "0.7";
	public static final String DEFAULT_ENUM_COMPARATORS_THRESHOLD = "0.9";

	//Constants for the name of the keys in the properties file
	//KEY_MULTIPLE means that multiple properties starting with the given value are valid 
	public static final String KEY_MAIN_NAMESPACE = "mainNamespace";
	public static final String KEY_MULTIPLE_SKIP_NAMESPACES = "skipNamespace"; 
	public static final String KEY_TYPE_NAME_INFERENCER = "typeNameInferencer";
	public static final String KEY_TYPE_NAME_INFERENCER_LOCALITY = "locality";
	public static final String KEY_GENERATE_ENUMERATIONS = "generateEnumerations";
	public static final String KEY_MIN_NUMBER_OF_DISTINCT_VALUES_TO_ENUM = "minNumberOfDistinctValuesToEnum";
	public static final String KEY_MAX_NUMBER_OF_DISTINCT_VALUES_TO_ENUM = "maxNumberOfDistinctValuesToEnum";
	public static final String KEY_SIMPLE_TYPE_INFERENCER = "simpleTypeInferencer";
	public static final String KEY_ATTRIBUTE_LIST_INFERENCER = "attributeListInferencer";
	public static final String KEY_CHILDREN_PATTERN_COMPARATOR = "childrenPatternComparator";
	public static final String KEY_CHILDREN_PATTERN_COMPARATOR_REDUCE_THRESHOLD = "reduceThreshold";
	public static final String KEY_ATTRIBUTE_LIST_COMPARATOR = "attributeListComparator";
	public static final String KEY_SAME_NAME_CHILDREN_PATTERN_COMPARATOR = "snChildrenPatternComparator";
	public static final String KEY_SAME_NAME_CHILDREN_PATTERN_COMPARATOR_REDUCE_THRESHOLD = "snReduceThreshold";
	public static final String KEY_SAME_NAME_ATTRIBUTE_LIST_COMPARATOR = "snAttributeListComparator";
	public static final String KEY_ENUMS_COMPARATOR = "enumsComparator";
	public static final String KEY_ENUMS_COMPARATOR_THRESHOLD = "enumsComparatorThreshold";
	public static final String KEY_SAME_NAME_ENUMS_COMPARATOR = "snEnumsComparator";
	public static final String KEY_SAME_NAME_ENUMS_COMPARATOR_THRESHOLD = "snEnumsComparatorThreshold";
	public static final String KEY_AVOID_SORE = "avoidSORE";
	public static final String KEY_TRY_ECHARE = "tryECHARE";
	public static final String KEY_MULTIPLE_OPTIMIZERS = "optimizer";
	public static final String KEY_STRICT_VALID_ROOT_DEFINITION_WORKAROUND = "strictValidRootDefinitionWorkaround";
	public static final String KEY_ELEMENTS_GLOBAL = "elementsGlobal";
	public static final String KEY_COMPLEX_TYPES_GLOBAL = "complexTypesGlobal";
	public static final String KEY_SIMPLE_TYPES_GLOBAL = "simpleTypesGlobal";
	public static final String KEY_TYPE_NAMES_ANCESTORS_SEPARATOR = "typeNamesAncestorsSeparator";
	public static final String KEY_MERGED_TYPES_SEPARATOR = "mergedTypesSeparator";
	//Special key to indicate via the command line the path at which the properties file that must be loaded as a configuration.
	public static final String KEY_CONFIG_FILE = "configFile";
	
	//Constants for the allowed values on some parameters
	public static final String VALUE_TYPE_INFERENCER_NAME = "name";
	public static final String VALUE_TYPE_INFERENCER_KLOCAL = "klocal";
	public static final String VALUE_TYPE_INFERENCER_FULLPATH = "fullpath";
	public static final String VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL = "SimpleTypeInferencerImpl";
	public static final String VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL = "AttributeListInferencerImpl";
	public static final String VALUE_CHILDREN_PATTERN_COMPARATOR_NO = "no";
	public static final String VALUE_CHILDREN_PATTERN_COMPARATOR_EQUALS = "equals";
	public static final String VALUE_CHILDREN_PATTERN_COMPARATOR_NODEBASED = "nodebased";
	public static final String VALUE_CHILDREN_PATTERN_COMPARATOR_NODESUBSUMED = "nodesubsumed";
	public static final String VALUE_CHILDREN_PATTERN_COMPARATOR_REDUCE = "reduce";
	public static final String VALUE_CHILDREN_PATTERN_COMPARATOR_SUBSUMED = "subsumed";
	public static final String VALUE_ATTRIBUTE_LIST_COMPARATOR_NO = "no";
	public static final String VALUE_ATTRIBUTE_LIST_COMPARATOR_EQUALS = "equals";
	public static final String VALUE_ATTRIBUTE_LIST_COMPARATOR_MERGE = "merge";
	public static final String VALUE_ATTRIBUTE_LIST_COMPARATOR_SAME = "same";
	public static final String VALUE_ATTRIBUTE_LIST_COMPARATOR_STRICT = "strict";
	public static final String VALUE_ENUMS_COMPARATOR_NO = "no";
	public static final String VALUE_ENUMS_COMPARATOR_MIN_INTERSECTION_BIDIRECTIONAL="minIntersectionBidirectional";
	public static final String VALUE_ENUMS_COMPARATOR_MIN_INTERSECTION_UNIDIRECTIONAL="minIntersectionUnidirectional";
	
	/**
	 * Default constructor. It initializes the the default values
	 */
	public XSDInferenceConfiguration() {
		typeNameInferencer=new KLocalTypeNameInferencer(2);
		simpleTypeInferencer= VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL;
		attributeListInferencer = VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL;
		addSetDefaultOptimizers(true);
	}
	
	/**
	 * Adds the default optimizers to the optimizers list. The list may be cleared before adding or not.
	 * @param clearPrevious Whether the list of optimizers should be cleared before adding the new ones or not
	 */
	protected void addSetDefaultOptimizers(boolean clearPrevious){
		if(clearPrevious)
			optimizers.clear();
		RegexOptimizersFactory regexOptimizersFactory = RegexOptimizersFactory.getInstance();
		optimizers.addAll(regexOptimizersFactory.getDefaultOptimizersList());
	}
	
	/**
	 * Constructor. It builds a configuration from a base file.
	 * @param file
	 * @throws IOException
	 * @throws XSDConfigurationException
	 */
	public XSDInferenceConfiguration(File file) throws IOException, XSDConfigurationException{
		loadFromFile(file);
	}
	
	/**
	 * Constructor from command line. It parses a command line to set the parameters. 
	 * If a configuration file was found, it would be parsed and its values would be loaded 
	 * PRIOR TO parse the command line, so any value present at the command line would override 
	 * the one present at the file, if both were present.
	 * @param args a list with the contents of the args parameter of the main method
	 * @throws XSDConfigurationException if there is an error at parsing the command line or any invalid or inconsistent parameter is found
	 * @throws IOException if there are problems when reading a configuration file (if one was specified via the command line)
	 */
	public XSDInferenceConfiguration(List<String> args) throws XSDConfigurationException, IOException{
		loadFromCmdLine(args);
	}
	
	/**
	 * Constructor from command line. It parses a command line to set the parameters. 
	 * If a configuration file was found, it would be parsed and its values would be loaded 
	 * PRIOR TO parse the command line, so any value present at the command line would override 
	 * the one present at the file, if both were present.
	 * @param args an array with the contents of the args parameter of the main method
	 * @throws XSDConfigurationException if there is an error at parsing the command line or any invalid or inconsistent parameter is found
	 * @throws IOException if there are problems when reading a configuration file (if one was specified via the command line)
	 */
	public XSDInferenceConfiguration(String[] args) throws XSDConfigurationException, IOException{
		loadFromCmdLine(Arrays.asList(args));
	}

	/**
	 * Loads a configuration from a properties file. Any value which was not present at the file would remain 
	 * at its previous value (which might be the default value) 
	 * @param file File object representing the properties file
	 * @throws IOException If there is an I/O error related to the properties file.
	 * @throws XSDConfigurationException if a value of a parameter is not valid or if a value of any parameter is inconsistent 
	 * 															with the value of another parameter
	 */
	protected void loadFromFile(File file) throws IOException, XSDConfigurationException{
		FileInputStream fis = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(fis);
		String readMainNamespace = properties.getProperty(KEY_MAIN_NAMESPACE);
		if(readMainNamespace!=null){
			mainNamespace=readMainNamespace;
		}
		
		Set<String> foundSkipNamespacesKeys = Sets.filter(properties.stringPropertyNames(), 
				Predicates.containsPattern("^\\Q"+KEY_MULTIPLE_SKIP_NAMESPACES+"\\E.*$"));
		if(!foundSkipNamespacesKeys.isEmpty()) {
			skipNamespaces.clear();
		}
		for(String skipNamespace: foundSkipNamespacesKeys){
			skipNamespaces.add(properties.getProperty(skipNamespace));
		}
		
		String readTypeInferencer=properties.getProperty(KEY_TYPE_NAME_INFERENCER);
		String localityStr = properties.getProperty(KEY_TYPE_NAME_INFERENCER_LOCALITY);
		
		if(readTypeInferencer!=null){
			setTypeInferencer(readTypeInferencer, localityStr);
		}
		
		String readGenerateEnumerations = properties.getProperty(KEY_GENERATE_ENUMERATIONS);
		if(readGenerateEnumerations!=null){
			setGenerateEnumerations(readGenerateEnumerations);
		}
		
		String readMinNumberOfDistinctValuesToEnum = properties.getProperty(KEY_MIN_NUMBER_OF_DISTINCT_VALUES_TO_ENUM);
		if(readMinNumberOfDistinctValuesToEnum!=null){
			minNumberOfDistinctValuesToEnum=Integer.parseInt(readMinNumberOfDistinctValuesToEnum);
		}
		
		String readMaxNumberOfDistinctValuesToEnum = properties.getProperty(KEY_MAX_NUMBER_OF_DISTINCT_VALUES_TO_ENUM);
		if(readMaxNumberOfDistinctValuesToEnum!=null){
			maxNumberOfDistinctValuesToEnum=Integer.parseInt(readMaxNumberOfDistinctValuesToEnum);
		}
				
		String readSimpleTypeInferencer = properties.getProperty(KEY_SIMPLE_TYPE_INFERENCER);
		if(readSimpleTypeInferencer!=null){
			setSimpleTypeInferencer(readSimpleTypeInferencer);
		}
		
		String readAttributeListInferencer = properties.getProperty(KEY_ATTRIBUTE_LIST_INFERENCER);
		if(readAttributeListInferencer!=null){
			setAttributeListInferencer(readAttributeListInferencer);
		}
				
		String readChildrenPatternComparator = properties.getProperty(KEY_CHILDREN_PATTERN_COMPARATOR);
		String readAttributeListComparator = properties.getProperty(KEY_ATTRIBUTE_LIST_COMPARATOR);
		String readReduceThreshold = properties.getProperty(KEY_CHILDREN_PATTERN_COMPARATOR_REDUCE_THRESHOLD, DEFAULT_REDUCE_THRESHOLD);
		
		if((readChildrenPatternComparator==null && readAttributeListComparator!=null)||
				(readChildrenPatternComparator!=null && readAttributeListComparator==null)){
			throw new InconsistentXSDConfigurationParametersException("If a 'childrenPatternComparator' is specified, an 'attributeListComparator' must be specified as well and vice versa");
		}
		else if(readChildrenPatternComparator!=null && readAttributeListComparator!=null){
			if((readChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_NO)&&!readAttributeListComparator.equals(VALUE_ATTRIBUTE_LIST_COMPARATOR_NO))||
					(!readChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_NO)&&readAttributeListComparator.equals(VALUE_ATTRIBUTE_LIST_COMPARATOR_NO))){
				throw new InconsistentXSDConfigurationParametersException("If a 'childrenPatternComparator' is specified to no, 'attributeListComparator' must be specified as well to no and vice versa");
			}
			setChildrenPatternComparator(readChildrenPatternComparator,readReduceThreshold);
			setAttributeListComparator(readAttributeListComparator);
		} 
		
		String snReadChildrenPatternComparator = properties.getProperty(KEY_SAME_NAME_CHILDREN_PATTERN_COMPARATOR);
		String snReadAttributeListComparator = properties.getProperty(KEY_SAME_NAME_ATTRIBUTE_LIST_COMPARATOR);
		String snReadReduceThreshold = properties.getProperty(KEY_SAME_NAME_CHILDREN_PATTERN_COMPARATOR_REDUCE_THRESHOLD, DEFAULT_REDUCE_THRESHOLD);		
		if(snReadChildrenPatternComparator!=null || snReadAttributeListComparator!=null){
			
			if((readAttributeListComparator==null || attributeListComparator==null) && !(snReadChildrenPatternComparator!=null && snReadAttributeListComparator!=null)){
				throw new InconsistentXSDConfigurationParametersException("If same name comparators are defined when normal comparators are not defined, both same name comparators must be defined");
			}
			setSnChildrenPatternComparator(snReadChildrenPatternComparator,snReadReduceThreshold);
			setSnAttributeListComparator(snReadAttributeListComparator);
		}
		
		String readEnumsComparator = properties.getProperty(KEY_ENUMS_COMPARATOR);
		String readEnumComparatorThreshold=properties.getProperty(KEY_ENUMS_COMPARATOR_THRESHOLD,DEFAULT_ENUM_COMPARATORS_THRESHOLD);
		if(readEnumsComparator!=null){
			setEnumsComparator(readEnumsComparator, readEnumComparatorThreshold);
		}
		
		String readSnEnumsComparator = properties.getProperty(KEY_SAME_NAME_ENUMS_COMPARATOR);
		String readSnEnumComparatorThreshold=properties.getProperty(KEY_SAME_NAME_ENUMS_COMPARATOR_THRESHOLD,DEFAULT_ENUM_COMPARATORS_THRESHOLD);
		if(readSnEnumsComparator!=null){
			setSnEnumsComparator(readSnEnumsComparator,	readSnEnumComparatorThreshold);
		}
		
		String readAvoidSore = properties.getProperty(KEY_AVOID_SORE);
		if(readAvoidSore!=null){
			setAvoidSORE(readAvoidSore);
		}
		
		String readTryECHARE = properties.getProperty(KEY_TRY_ECHARE);
		if(readTryECHARE!=null){
			setTryECHARE(readTryECHARE);
		}
		
		Set<String> foundOptimizersKeys = Sets.filter(properties.stringPropertyNames(), 
				Predicates.containsPattern("^\\Q"+KEY_MULTIPLE_OPTIMIZERS+"\\E.*$"));
		
		
		if(!foundOptimizersKeys.isEmpty()){
			optimizers.clear();
			Set<String> foundOptimizersValues = new HashSet<String>(foundOptimizersKeys.size());
			for(String foundOptimizerKey: foundOptimizersKeys){
				foundOptimizersValues.add(properties.getProperty(foundOptimizerKey));
			}
			addOptimizersFromStringsSet(foundOptimizersValues);
		}
		
		String readStrictValidRootDefinitionWorkaround = properties.getProperty(KEY_STRICT_VALID_ROOT_DEFINITION_WORKAROUND);
		if(readStrictValidRootDefinitionWorkaround!=null){
			setStrictValidRootDefinitionWorkaround(readStrictValidRootDefinitionWorkaround);
		}
		
		String readElementsGlobal = properties.getProperty(KEY_ELEMENTS_GLOBAL);
		if(readElementsGlobal!=null){
			setElementsGlobal(readElementsGlobal);
		}
		
		String readComplexTypesGlobal = properties.getProperty(KEY_COMPLEX_TYPES_GLOBAL);
		if(readComplexTypesGlobal!=null){
			setComplexTypesGlobal(readComplexTypesGlobal);
		}
		
		String readSimpleTypesGlobal = properties.getProperty(KEY_SIMPLE_TYPES_GLOBAL);
		if(readSimpleTypesGlobal!=null){
			setSimpleTypesGlobal(readSimpleTypesGlobal);
		}
		
		String readTypeNamesAncestorSeparator = properties.getProperty(KEY_TYPE_NAMES_ANCESTORS_SEPARATOR);
		if(readTypeNamesAncestorSeparator!=null){
			setTypeNamesAncestorsSeparator(readTypeNamesAncestorSeparator);
		}
		
		String readMergedTypesSeparator = properties.getProperty(KEY_MERGED_TYPES_SEPARATOR);
		if(readMergedTypesSeparator!=null){
			setMergedTypesSeparator(readMergedTypesSeparator);
		}

	}
	
	/**
	 * It looks for a parameter value at the parameter list.
	 * For example: if the command line looks like <br/>
	 * {@value ... --enumsComparator no ...}<br/>
	 * {@value --enumsComparator} is at index i and {@value no} is at index i+1
	 * If you look for {@value enumsComparator} value, it looks for the index i of {@value --enumsComparator} 
	 * and returns {@value no} because it is at index i+1. 
	 * @param key the searched key (without the starting --)
	 * @param parameterList a List containing all the values of the args parameter of the main method
	 * @param defaultValue the value returned if the key is not found
	 * @return the value or defaultValue, if the key is not found
	 * @throws XSDConfigurationException If the parameter is not followed by its value
	 */
	private String getParamValue(String key,List<String> parameterList,String defaultValue) throws XSDConfigurationException{
		
		for(int i=0;i<parameterList.size();i++){
			if(!parameterList.get(i).equalsIgnoreCase("--"+key))
				continue; //This is not the searched key.
			String valueCandidate="";
			if((i+1)>=parameterList.size()||(valueCandidate=parameterList.get(i+1)).startsWith("--")){
				throw new BadCommandLineException("A value for parameter '--"+key+"' must be next to it. However, '"+valueCandidate+"' has been found.");
			} else {
				return valueCandidate;
			}
			
		}
		return defaultValue;
	}
	
	/**
	 * It looks for a parameter value at the parameter list.
	 * For example: if the command line looks like <br/>
	 * {@value ... --enumsComparator no ...}<br/>
	 * {@value --enumsComparator} is at index i and {@value no} is at index i+1
	 * If you look for {@value enumsComparator} value, it looks for the index i of {@value --enumsComparator} 
	 * and returns {@value no} because it is at index i+1. 
	 * @param key the searched key (without the starting --)
	 * @param parameterList a List containing all the values of the args parameter of the main method
	 * @return the value or null, if the key is not found
	 * @throws XSDConfigurationException If the parameter is not followed by its value but by another parameter.
	 */
	private String getParamValue(String key,List<String> parameterList) throws XSDConfigurationException{
		return getParamValue(key, parameterList, null);
	}
	
	/**
	 * Checks if a boolean parameter is checked (--<i>key</i> is present in the command line) or unchecked 
	 * (--no<i>key</i>).
	 * @param key the parameter to search
	 * @param parameterList a List containing all the values of the args parameter of the main method
	 * @return an String that is "true" if the parameter is checked, "false" if it is unchecked and null if it is not present. 
	 * @throws XSDConfigurationException if a parameter is checked and unchecked in the same command line.
	 */
	private String getParamIsCheckedOrUnchecked(String key,List<String> parameterList) throws XSDConfigurationException{
		String positiveParameter="--"+key;
		String negativeParameter="--no"+key;
		boolean containsPositive=false;
		boolean containsNegative=false;
		for(int i=0;i<parameterList.size();i++){
			String value=parameterList.get(i);
			if(value.equalsIgnoreCase(positiveParameter))
				containsPositive=true;
			if(value.equalsIgnoreCase(negativeParameter))
				containsNegative=true;
		}
		if(containsNegative&&containsPositive){
			throw new BadCommandLineException("Parameter '"+key+"' has been checked and unchecked on the same command line.");
		} 
		else if(containsPositive){
			return "true";
		}
		else if(containsNegative){
			return "false";
		}
		else{
			return null;
		}
	}
	
	/**
	 * Returns the set of values of a multiple parameter. 
	 * For example, skipNamespaces is a multiple parameter as it allows to define a set of namespaces to skip and may have any size. 
	 * In the command line, it would be represented as follows:<br/>
	 * {@value ... --skipNamespace1 namespace1 ... --skipNamespace2 namespace2 ...}
	 * (Note that the values do not have to be together and that any key starting with the 
	 * parameter key is allowed).
	 * This method returns a set that contains all those multiple values.
	 * @param key the key of the multiple values
	 * @param parameterList a List containing all the values of the args parameter of the main method
	 * @return A set of the values of the multiple parameter. If there are no values, it means, the parameter is not present at all, 
	 * an empty set is returned.
	 * @throws XSDConfigurationException if a correct key of this parameter has not a corresponding value (because the 
	 *                                   following element of the command line is another parameter).
	 */
	private Set<String> getParamValues(String key,List<String> parameterList) throws XSDConfigurationException{
		Set<String> values = new HashSet<String>(parameterList.size()/3);
		for(int i=0;i<parameterList.size();i++){
			if(!parameterList.get(i).toLowerCase().startsWith("--"+key.toLowerCase()))
				continue; //This is not a searched key.
			String valueCandidate="";
			if((i+1)>=parameterList.size()||(valueCandidate=parameterList.get(i+1)).startsWith("--")){
				throw new BadCommandLineException("A value for parameter '--"+key+"' must be next to it. However, '"+valueCandidate+"' has been found.");
			} else {
				values.add(valueCandidate);
			}
			
		}
		return values;
	}
	
	/**
	 * Reads a whole configuration from the command line. Each non-found parameter remains at 
	 * its previous value (which may be the default value).
	 * If a configuration file is specified, it is read PRIOR TO parse any other parameter of the 
	 * command line, so the parameters present in the file will always be overridden by the values present 
	 * in the command line (those parameters present in the file but not in the command line will 
	 * not be overriden).
	 * @param parameterList a List containing all the values of the args parameter of the main method
	 * @throws XSDConfigurationException if there is any error at command line parsing, or either invalid or inconsistent values are found
	 * @throws IOException if there are problems when reading the properties file
	 */
	protected void loadFromCmdLine(List<String> parameterList) throws XSDConfigurationException,IOException{
		String readConfigurationFile = getParamValue(KEY_CONFIG_FILE, parameterList);
		if(readConfigurationFile!=null){
			File propertiesFile = new File(readConfigurationFile);
			if(!propertiesFile.exists()){
				throw new FileNotFoundException("Properties file not found");
			}
			loadFromFile(propertiesFile);
		}
			
		String readMainNamespace = getParamValue(KEY_MAIN_NAMESPACE, parameterList);
		if(readMainNamespace!=null){
			mainNamespace=readMainNamespace;
		}
		
		Set<String> foundSkipNamespaces = getParamValues(KEY_MULTIPLE_SKIP_NAMESPACES, parameterList);
		if(!foundSkipNamespaces.isEmpty()) {
			skipNamespaces.clear();
			skipNamespaces.addAll(foundSkipNamespaces);
		}
				
		String readTypeInferencer=getParamValue(KEY_TYPE_NAME_INFERENCER,parameterList);
		String localityStr = getParamValue(KEY_TYPE_NAME_INFERENCER_LOCALITY,parameterList);
		
		if(readTypeInferencer!=null){
			setTypeInferencer(readTypeInferencer, localityStr);
		}
		
		String readGenerateEnumerations = getParamIsCheckedOrUnchecked(KEY_GENERATE_ENUMERATIONS,parameterList);
		if(readGenerateEnumerations!=null){
			setGenerateEnumerations(readGenerateEnumerations);
		}
		
		String readMinNumberOfDistinctValuesToEnum = getParamValue(KEY_MIN_NUMBER_OF_DISTINCT_VALUES_TO_ENUM,parameterList);
		if(readMinNumberOfDistinctValuesToEnum!=null){
			minNumberOfDistinctValuesToEnum=Integer.parseInt(readMinNumberOfDistinctValuesToEnum);
		}
		
		String readMaxNumberOfDistinctValuesToEnum = getParamValue(KEY_MAX_NUMBER_OF_DISTINCT_VALUES_TO_ENUM,parameterList);
		if(readMaxNumberOfDistinctValuesToEnum!=null){
			maxNumberOfDistinctValuesToEnum=Integer.parseInt(readMaxNumberOfDistinctValuesToEnum);
		}
				
		String readSimpleTypeInferencer = getParamValue(KEY_SIMPLE_TYPE_INFERENCER,parameterList);
		if(readSimpleTypeInferencer!=null){
			setSimpleTypeInferencer(readSimpleTypeInferencer);
		}
		
		String readAttributeListInferencer = getParamValue(KEY_ATTRIBUTE_LIST_INFERENCER,parameterList);
		if(readAttributeListInferencer!=null){
			setAttributeListInferencer(readAttributeListInferencer);
		}
				
		String readChildrenPatternComparator = getParamValue(KEY_CHILDREN_PATTERN_COMPARATOR,parameterList);
		String readAttributeListComparator = getParamValue(KEY_ATTRIBUTE_LIST_COMPARATOR,parameterList);
		String readReduceThreshold = getParamValue(KEY_CHILDREN_PATTERN_COMPARATOR_REDUCE_THRESHOLD, parameterList,DEFAULT_REDUCE_THRESHOLD);
		
		if(((readChildrenPatternComparator==null && childrenPatternComparator==null) && readAttributeListComparator!=null)||
				(readChildrenPatternComparator!=null && (readAttributeListComparator==null && attributeListComparator==null))){
			throw new InconsistentXSDConfigurationParametersException("If a 'childrenPatternComparator' is specified, an 'attributeListComparator' must be specified as well via command line or properties file and vice versa");
		}
		else if(readChildrenPatternComparator!=null || readAttributeListComparator!=null){
			if((readChildrenPatternComparator!=null && readAttributeListComparator!=null))
				if((readChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_NO)&&!readAttributeListComparator.equals(VALUE_ATTRIBUTE_LIST_COMPARATOR_NO))||
					(!readChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_NO)&&readAttributeListComparator.equals(VALUE_ATTRIBUTE_LIST_COMPARATOR_NO))){
				throw new InconsistentXSDConfigurationParametersException("If a 'childrenPatternComparator' is specified to no, 'attributeListComparator' must be specified as well to no and vice versa");
			}
			if(readChildrenPatternComparator!=null)
				setChildrenPatternComparator(readChildrenPatternComparator,readReduceThreshold);
			if(readAttributeListComparator!=null)
				setAttributeListComparator(readAttributeListComparator);
		} 
		
		String snReadChildrenPatternComparator = getParamValue(KEY_SAME_NAME_CHILDREN_PATTERN_COMPARATOR,parameterList);
		String snReadAttributeListComparator = getParamValue(KEY_SAME_NAME_ATTRIBUTE_LIST_COMPARATOR,parameterList);
		String snReadReduceThreshold = getParamValue(KEY_SAME_NAME_CHILDREN_PATTERN_COMPARATOR_REDUCE_THRESHOLD, parameterList, DEFAULT_REDUCE_THRESHOLD);		
		if(snReadChildrenPatternComparator!=null || snReadAttributeListComparator!=null){
			
			if((childrenPatternComparator==null || attributeListComparator==null) && 
					!((snReadChildrenPatternComparator!=null && snChildrenPatternComparator==null) && (snReadAttributeListComparator!=null && snAttributeListComparator==null))){
				throw new InconsistentXSDConfigurationParametersException("If same name comparators are defined when normal comparators are not defined, both same name comparators must be defined via properties file or command line");
			}
			
			if(snReadChildrenPatternComparator!=null)
				setSnChildrenPatternComparator(snReadChildrenPatternComparator,snReadReduceThreshold);
			if(snReadAttributeListComparator!=null)
			setSnAttributeListComparator(snReadAttributeListComparator);
		}
		
		String readEnumsComparator = getParamValue(KEY_ENUMS_COMPARATOR,parameterList);
		String readEnumComparatorThreshold=getParamValue(KEY_ENUMS_COMPARATOR_THRESHOLD,parameterList,DEFAULT_ENUM_COMPARATORS_THRESHOLD);
		if(readEnumsComparator!=null){
			setEnumsComparator(readEnumsComparator, readEnumComparatorThreshold);
		}
		
		String readSnEnumsComparator = getParamValue(KEY_SAME_NAME_ENUMS_COMPARATOR,parameterList);
		String readSnEnumComparatorThreshold=getParamValue(KEY_SAME_NAME_ENUMS_COMPARATOR_THRESHOLD,parameterList,DEFAULT_ENUM_COMPARATORS_THRESHOLD);
		if(readSnEnumsComparator!=null){
			setSnEnumsComparator(readSnEnumsComparator,	readSnEnumComparatorThreshold);
		}
		
		String readAvoidSore = getParamIsCheckedOrUnchecked(KEY_AVOID_SORE,parameterList);
		if(readAvoidSore!=null){
			setAvoidSORE(readAvoidSore);
		}
		
		String readTryECHARE = getParamIsCheckedOrUnchecked(KEY_TRY_ECHARE,parameterList);
		if(readTryECHARE!=null){
			setTryECHARE(readTryECHARE);
		}
		
		Set<String> foundOptimizersValues = getParamValues(KEY_MULTIPLE_OPTIMIZERS, parameterList);
		if(!foundOptimizersValues.isEmpty()){
			optimizers.clear();
			addOptimizersFromStringsSet(foundOptimizersValues);
		}
		
		String readStrictValidRootDefinitionWorkaround = getParamIsCheckedOrUnchecked(KEY_STRICT_VALID_ROOT_DEFINITION_WORKAROUND, parameterList);
		if(readStrictValidRootDefinitionWorkaround!=null){
			setStrictValidRootDefinitionWorkaround(readStrictValidRootDefinitionWorkaround);
		}
		
		String readElementsGlobal = getParamIsCheckedOrUnchecked(KEY_ELEMENTS_GLOBAL,parameterList);
		if(readElementsGlobal!=null){
			setElementsGlobal(readElementsGlobal);
		}
		
		String readComplexTypesGlobal = getParamIsCheckedOrUnchecked(KEY_COMPLEX_TYPES_GLOBAL,parameterList);
		if(readComplexTypesGlobal!=null){
			setComplexTypesGlobal(readComplexTypesGlobal);
		}
		
		String readSimpleTypesGlobal = getParamIsCheckedOrUnchecked(KEY_SIMPLE_TYPES_GLOBAL,parameterList);
		if(readSimpleTypesGlobal!=null){
			setSimpleTypesGlobal(readSimpleTypesGlobal);
		}
		
		String readTypeNamesAncestorSeparator = getParamValue(KEY_TYPE_NAMES_ANCESTORS_SEPARATOR,parameterList);
		if(readTypeNamesAncestorSeparator!=null){
			setTypeNamesAncestorsSeparator(readTypeNamesAncestorSeparator);
		}
		
		String readMergedTypesSeparator = getParamValue(KEY_MERGED_TYPES_SEPARATOR,parameterList);
		if(readMergedTypesSeparator!=null){
			setMergedTypesSeparator(readMergedTypesSeparator);
		}
	}

	/**
	 * Iterates over a set of Strings and, according to the found values, adds 
	 * the correct optimizers. If an invalid value is found, an exception is thrown.
	 * @param foundOptimizersValues
	 * @throws InvalidXSDConfigurationParameterException
	 * @throws NullPointerException if foundOptimizerValues is null
	 */
	public void addOptimizersFromStringsSet(Set<String> foundOptimizersValues)
			throws InvalidXSDConfigurationParameterException {
		if(foundOptimizersValues==null)
			throw new NullPointerException("'foundOptimizersValues' must not be null");
		Set<RegexOptimizer> tempOptimizers=new HashSet<RegexOptimizer>(foundOptimizersValues.size());
		for(String foundOptimizer: foundOptimizersValues){
			try{
				tempOptimizers.add(RegexOptimizersFactory.getInstance().getRegexOptimizerInstance(foundOptimizer));
			} catch (IllegalArgumentException e){
				throw new InvalidXSDConfigurationParameterException("Unknown optimizer: "+foundOptimizer);
			}
//			if(foundOptimizer.equalsIgnoreCase(RegexOptimizersFactory.VALUE_OPTIMIZERS_CHOICE)){
//				tempOptimizers.add(new ChoiceOptimizer());
//			}
//			else if(foundOptimizer.equalsIgnoreCase(RegexOptimizersFactory.VALUE_OPTIMIZERS_EMPTYCHILD)){
//				tempOptimizers.add(new EmptyChildOptimizer());
//			}
//			else if(foundOptimizer.equalsIgnoreCase(RegexOptimizersFactory.VALUE_OPTIMIZERS_EMPTY)){
//				tempOptimizers.add(new EmptyOptimizer());
//			}
//			else if(foundOptimizer.equalsIgnoreCase(RegexOptimizersFactory.VALUE_OPTIMIZERS_SINGULAR_REGULAR_EXPRESSION)){
//				tempOptimizers.add(new SingularRegularExpressionOptimizer());
//			}
//			else if(foundOptimizer.equalsIgnoreCase(RegexOptimizersFactory.VALUE_OPTIMIZERS_SEQUENCE)){
//				tempOptimizers.add(new SequenceOptimizer());
//			}
//			else if(foundOptimizer.equalsIgnoreCase(RegexOptimizersFactory.VALUE_OPTIMIZERS_SINGLETON)){
//				tempOptimizers.add(new SingletonOptimizer());
//			}
//			else{
//				throw new InvalidXSDConfigurationParameterException("Unknown optimizer: "+foundOptimizer);
//			}
		}
		optimizers.addAll(tempOptimizers);
	}

	/**
	 * Sets the tryECHARE parameter from a read String
	 * @param readTryECHARE
	 * @throws InvalidXSDConfigurationParameterException if the read String is not valid
	 * @throw NullPointerException if a null value is provided
	 */
	public void setTryECHARE(String readTryECHARE)
			throws InvalidXSDConfigurationParameterException {
		if(readTryECHARE==null)
			throw new NullPointerException();
		if(!(readTryECHARE.equalsIgnoreCase("true")||readTryECHARE.equalsIgnoreCase("false")))
			throw new InvalidXSDConfigurationParameterException("'tryECHARE' must be a valid boolean: true or false.\n"+readTryECHARE+" is not valid");
		tryECHARE=Boolean.parseBoolean(readTryECHARE);
	}

	/**
	 * Sets avoidSORE from a read String
	 * @param readAvoidSore
	 * @throws InvalidXSDConfigurationParameterException if the read String is not valid
	 * @throw NullPointerException if a null value is provided
	 */
	public void setAvoidSORE(String readAvoidSore)
			throws InvalidXSDConfigurationParameterException {
		if(readAvoidSore==null)
			throw new NullPointerException();
		if(!(readAvoidSore.equalsIgnoreCase("true")||readAvoidSore.equalsIgnoreCase("false")))
			throw new InvalidXSDConfigurationParameterException("'readAvoidSore' must be a valid boolean: true or false"+readAvoidSore+" is not valid");
		avoidSORE=Boolean.parseBoolean(readAvoidSore);
	}

	/**
	 * Sets snEnumsComparator from read Strings. 
	 * If a null value is provided, it is set to the same value than enumsComparator.
	 * @param readSnEnumsComparator
	 * @param readSnEnumsComparatorThreshold threshold value for those comparators that need it. Only mandatory for those comparators.
	 * @throws XSDConfigurationException If an unknown value is provided or a null value of readSnEnumComparatorThreshold is given when the comparator needs it.
	 */
	public void setSnEnumsComparator(String readSnEnumsComparator,
			String readSnEnumsComparatorThreshold) throws XSDConfigurationException {
		if(readSnEnumsComparator==null){
			snEnumsComparator=enumsComparator;
		}
		if(readSnEnumsComparator.equalsIgnoreCase(VALUE_ENUMS_COMPARATOR_MIN_INTERSECTION_BIDIRECTIONAL)){
			snEnumsComparator = new MinIntersectionBidirectionalEnumComparator(Float.parseFloat(readSnEnumsComparatorThreshold));
		}
		else if(readSnEnumsComparator.equalsIgnoreCase(VALUE_ENUMS_COMPARATOR_MIN_INTERSECTION_UNIDIRECTIONAL)){
			snEnumsComparator = new MinIntersectionUnidirectionalEnumComparator(Float.parseFloat(readSnEnumsComparatorThreshold));
		} 
		else if(readSnEnumsComparator.equalsIgnoreCase(VALUE_ENUMS_COMPARATOR_NO)){
			snEnumsComparator=null;
		}
		else {
			throw new InvalidXSDConfigurationParameterException("Unknown enumsComparator: "+readSnEnumsComparator);
		}
	}

	/**
	 * Sets enumsComparator from read Strings
	 * @param readEnumsComparator
	 * @param readEnumsComparatorThreshold threshold value for those comparators that need it. Only mandatory for those comparators.
	 * @throws XSDConfigurationException If an unknown value is provided or a null value of readEnumComparatorThreshold is given when the comparator needs it.
	 * @throws NullPointerException if a null readEnumsComparator is provided
	 */
	public void setEnumsComparator(String readEnumsComparator,
			String readEnumsComparatorThreshold) throws XSDConfigurationException {
		if(readEnumsComparator==null){
			throw new NullPointerException();
		}
		if(readEnumsComparator.equalsIgnoreCase(VALUE_ENUMS_COMPARATOR_MIN_INTERSECTION_BIDIRECTIONAL)){
			enumsComparator = new MinIntersectionBidirectionalEnumComparator(Float.parseFloat(readEnumsComparatorThreshold));
		}
		else if(readEnumsComparator.equalsIgnoreCase(VALUE_ENUMS_COMPARATOR_MIN_INTERSECTION_UNIDIRECTIONAL)){
			enumsComparator = new MinIntersectionUnidirectionalEnumComparator(Float.parseFloat(readEnumsComparatorThreshold));
		}
		else if(readEnumsComparator.equalsIgnoreCase(VALUE_ENUMS_COMPARATOR_NO)){
			enumsComparator = null;
		}
		else {
			throw new InvalidXSDConfigurationParameterException("Unknown enumsComparator: "+readEnumsComparator);
		}
	}

	/**
	 * Sets snAttributeComparator from a read String.
	 * If a null value is provided, snAttributeComparator is set to the same value that attributeComparator.
	 * @param snReadAttributeListComparator
	 * @throws InvalidXSDConfigurationParameterException if an invalid value is provided
	 */
	public void setSnAttributeListComparator(
			String snReadAttributeListComparator)
			throws InvalidXSDConfigurationParameterException {
		
		if(snReadAttributeListComparator==null){
			snAttributeListComparator=attributeListComparator;
		}
		else if(snReadAttributeListComparator.equalsIgnoreCase(VALUE_ATTRIBUTE_LIST_COMPARATOR_EQUALS)){
			snAttributeListComparator=new EqualsAttributeComparator();
		}
		else if(snReadAttributeListComparator.equalsIgnoreCase(VALUE_ATTRIBUTE_LIST_COMPARATOR_MERGE)){
			snAttributeListComparator=new MergeAttributeComparator();
		}
		else if(snReadAttributeListComparator.equalsIgnoreCase(VALUE_ATTRIBUTE_LIST_COMPARATOR_SAME)){
			snAttributeListComparator=new SameAttributeComparator();
		}
		else if(snReadAttributeListComparator.equalsIgnoreCase(VALUE_ATTRIBUTE_LIST_COMPARATOR_STRICT)){
			snAttributeListComparator=new StrictAttributeComparator();
		}
		else if(snReadAttributeListComparator.equalsIgnoreCase(VALUE_ATTRIBUTE_LIST_COMPARATOR_NO)){
			snAttributeListComparator=null;
		} 
		else {
			throw new InvalidXSDConfigurationParameterException("Unknown same name attribute list comparator");
		}
	}

	/**
	 * Sets snChildrenPatternComparator from a read String.
	 * If a null value is provided, it is set to the same value than childrenPatternComparator.
	 * @param snReadChildrenPatternComparator
	 * @param snReadReduceThreshold
	 * @throws XSDConfigurationException if an invalid value is provided or a reduce pattern comparator is chosen without providing a threshold.
	 */
	public void setSnChildrenPatternComparator(
			String snReadChildrenPatternComparator, String snReadReduceThreshold)
			throws XSDConfigurationException {
		if(snReadChildrenPatternComparator==null){
			snChildrenPatternComparator=childrenPatternComparator;
		}
		else if(snReadChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_EQUALS)){
			snChildrenPatternComparator=new EqualsPatternComparator();
		} 
		else if(snReadChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_NODEBASED)){
			snChildrenPatternComparator=new NodeBasedPatternComparator();
		}
		else if(snReadChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_NODESUBSUMED)){
			snChildrenPatternComparator=new NodeSubsumptionPatternComparator();
		}
		else if(snReadChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_REDUCE)){
			if(snReadReduceThreshold==null)
				throw new InconsistentXSDConfigurationParametersException("Reduce children pattern comparator has been chosen without providing a threshold");
			snChildrenPatternComparator=new ReducePatternComparator(Float.parseFloat(snReadReduceThreshold));
		}
		else if(snReadChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_SUBSUMED)){
			snChildrenPatternComparator=new SubsumptionPatternComparator();
		}
		else if(snReadChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_NO)){
			snAttributeListComparator=null;
		}
		else {
			throw new InvalidXSDConfigurationParameterException("Unknown same name children pattern comparator");
		}
	}

	/**
	 * Sets attributeListComparator from a read String
	 * @param readAttributeListComparator
	 */
	public void setAttributeListComparator(String readAttributeListComparator) {
	    if(readAttributeListComparator.equalsIgnoreCase(VALUE_ATTRIBUTE_LIST_COMPARATOR_NO)){
	    	attributeListComparator=null;
	    }
		else if(readAttributeListComparator.equalsIgnoreCase(VALUE_ATTRIBUTE_LIST_COMPARATOR_EQUALS)){
			attributeListComparator=new EqualsAttributeComparator();
		}
		else if(readAttributeListComparator.equalsIgnoreCase(VALUE_ATTRIBUTE_LIST_COMPARATOR_MERGE)){
			attributeListComparator=new MergeAttributeComparator();
		}
		else if(readAttributeListComparator.equalsIgnoreCase(VALUE_ATTRIBUTE_LIST_COMPARATOR_SAME)){
			attributeListComparator=new SameAttributeComparator();
		}
		else if(readAttributeListComparator.equalsIgnoreCase(VALUE_ATTRIBUTE_LIST_COMPARATOR_STRICT)){
			attributeListComparator=new StrictAttributeComparator();
		}
	}

	/**
	 * Sets childrenPatternComparator from a read String.
	 * @param readChildrenPatternComparator
	 * @param readReduceThreshold
	 * @throws InvalidXSDConfigurationParameterException if an invalid value is provided or a threshold value is not given when a reduce comparator is chosen.
	 */
	public void setChildrenPatternComparator(
			String readChildrenPatternComparator, String readReduceThreshold)
			throws  XSDConfigurationException{
		if(readChildrenPatternComparator==null)
			throw new NullPointerException();
	    if(readChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_NO)){
	    	childrenPatternComparator=null;
	    }
		else if(readChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_EQUALS)){
			childrenPatternComparator=new EqualsPatternComparator();
		} 
		else if(readChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_NODEBASED)){
			childrenPatternComparator=new NodeBasedPatternComparator();
		}
		else if(readChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_NODESUBSUMED)){
			childrenPatternComparator=new NodeSubsumptionPatternComparator();
		}
		else if(readChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_REDUCE)){
			childrenPatternComparator=new ReducePatternComparator(Float.parseFloat(readReduceThreshold));
		}
		else if(readChildrenPatternComparator.equalsIgnoreCase(VALUE_CHILDREN_PATTERN_COMPARATOR_SUBSUMED)){
			childrenPatternComparator=new SubsumptionPatternComparator();
		}
		else {
			throw new InvalidXSDConfigurationParameterException("Unknown children pattern comparator");
		}
	}

	/**
	 * Sets generateEnumerations from a read String
	 * @param readGenerateEnumerations
	 * @throws InvalidXSDConfigurationParameterException if an invalid value is provided 
	 */
	public void setGenerateEnumerations(String readGenerateEnumerations)
			throws InvalidXSDConfigurationParameterException {
		if(!(readGenerateEnumerations.equalsIgnoreCase("true")||readGenerateEnumerations.equalsIgnoreCase("false")))
			throw new InvalidXSDConfigurationParameterException("'generateEnumerations' must be a valid boolean: true or false");
		generateEnumerations=Boolean.parseBoolean(readGenerateEnumerations);
	}

	/**
	 * Sets typeInferencer from a read String
	 * @param readTypeInferencer
	 * @param localityStr
	 * @throws XSDConfigurationException if an unknown value is provided or localityStr is null when klocal is chosen.
	 * @throws NullPointerException if a null value is passed
	 */
	public void setTypeInferencer(String readTypeInferencer, String localityStr)
			throws XSDConfigurationException {
		if(readTypeInferencer==null){
			throw new NullPointerException();
		}
		if(readTypeInferencer.equalsIgnoreCase(VALUE_TYPE_INFERENCER_NAME)){
			typeNameInferencer=new NameTypeNameInferencer();
		}
		else if((readTypeInferencer.equalsIgnoreCase(VALUE_TYPE_INFERENCER_FULLPATH)||
				readTypeInferencer.equalsIgnoreCase(VALUE_TYPE_INFERENCER_KLOCAL))&&
				(elementsGlobal||!strictValidRootDefinitionWorkaround)){
			throw new InconsistentXSDConfigurationParametersException("Only a 'name' type name inferencer may be used if elementsGlobal is true or the workaround is disabled. Change it to false prior to set another type name inferencer");
		}
		else if (readTypeInferencer.equalsIgnoreCase(VALUE_TYPE_INFERENCER_FULLPATH)){
			typeNameInferencer=new FullPathTypeNameInferencer();
		} else if (readTypeInferencer.equalsIgnoreCase(VALUE_TYPE_INFERENCER_KLOCAL)){
			if(localityStr==null)
				throw new InconsistentXSDConfigurationParametersException("A 'klocal' type name inferencer has been specified but the 'locality' parameter is missing");
			typeNameInferencer = new KLocalTypeNameInferencer(Integer.parseInt(localityStr));
		} else {
			throw new InvalidXSDConfigurationParameterException("The value for 'typeNameInferencer' must be: 'klocal', 'name' or 'fullpath' (case-insensitive)");
		}
	}
	
	/**
	 * Sets strictValidRootDefinitionWorkaround
	 * @param readStrictValidRootDefinitionWorkaroundStr read value as string
	 * @throws InvalidXSDConfigurationParameterException if an invalid value is given
	 * @throws InconsistentXSDConfigurationParametersException if the workaround is false and the current type name inferencer is not name based.
	 * @throws NullPointerException if a null value is passed
	 */
	public void setStrictValidRootDefinitionWorkaround(String readStrictValidRootDefinitionWorkaroundStr)
			throws InvalidXSDConfigurationParameterException, InconsistentXSDConfigurationParametersException {
		if(readStrictValidRootDefinitionWorkaroundStr==null)
			throw new NullPointerException();
		if(!(readStrictValidRootDefinitionWorkaroundStr.equalsIgnoreCase("true")||readStrictValidRootDefinitionWorkaroundStr.equalsIgnoreCase("false")))
			throw new InvalidXSDConfigurationParameterException("'strictValidRootDefinition' must be a valid boolean: true or false");
		boolean readStrictValidRootDefinitionWorkaround = Boolean.parseBoolean(readStrictValidRootDefinitionWorkaroundStr);
		if(!readStrictValidRootDefinitionWorkaround&&!(typeNameInferencer instanceof NameTypeNameInferencer)){
			throw new InconsistentXSDConfigurationParametersException("The workaround cannot be false if the type name inferencer is not name based");
		}
		strictValidRootDefinitionWorkaround=readStrictValidRootDefinitionWorkaround;
	}
	
	/**
	 * Sets elementsGlobal from a read String
	 * @param readElementsGlobalStr
	 * @throws InvalidXSDConfigurationParameterException if an invalid value is given
	 * @throws XSDConfigurationException If the given parameter cannot be set due to a configuration error
	 * @throws NullPointerException if a null value is passed
	 */
	public void setElementsGlobal(String readElementsGlobalStr)
			throws InvalidXSDConfigurationParameterException, XSDConfigurationException {
		if(readElementsGlobalStr==null)
			throw new NullPointerException();
		if(!(readElementsGlobalStr.equalsIgnoreCase("true")||readElementsGlobalStr.equalsIgnoreCase("false")))
			throw new InvalidXSDConfigurationParameterException("'elementsGlobal' must be a valid boolean: true or false");
		boolean readElementsGlobal = Boolean.parseBoolean(readElementsGlobalStr);
		if(readElementsGlobal&&!(typeNameInferencer instanceof NameTypeNameInferencer)){
			throw new InconsistentXSDConfigurationParametersException("elementsGlobal cannot be set to true if the type name inferencer is not a 'name' type name inferencer.");
		}
		elementsGlobal=readElementsGlobal;
	}
	
	/**
	 * Sets complexTypesGlobal from a read String
	 * @param readComplexTypesGlobal
	 * @throws InvalidXSDConfigurationParameterException
	 * @throws NullPointerException if a null value is passed
	 */
	public void setComplexTypesGlobal(String readComplexTypesGlobal)
			throws InvalidXSDConfigurationParameterException {
		if(readComplexTypesGlobal==null)
			throw new NullPointerException();
		if(!(readComplexTypesGlobal.equalsIgnoreCase("true")||readComplexTypesGlobal.equalsIgnoreCase("false")))
			throw new InvalidXSDConfigurationParameterException("'elementsGlobal' must be a valid boolean: true or false");
		complexTypesGlobal=Boolean.parseBoolean(readComplexTypesGlobal);
	}
	
	/**
	 * Sets simpleTypesGlobal from a read String
	 * @param readSimpleTypesGlobal
	 * @throws InvalidXSDConfigurationParameterException
	 * @throws NullPointerException if a null value is passed
	 */
	public void setSimpleTypesGlobal(String readSimpleTypesGlobal)
			throws InvalidXSDConfigurationParameterException {
		if(readSimpleTypesGlobal==null)
			throw new NullPointerException();
		
		if(!(readSimpleTypesGlobal.equalsIgnoreCase("true")||readSimpleTypesGlobal.equalsIgnoreCase("false")))
			throw new InvalidXSDConfigurationParameterException("'elementsGlobal' must be a valid boolean: true or false");
		simpleTypesGlobal=Boolean.parseBoolean(readSimpleTypesGlobal);
	}

	public XSDInferenceConfiguration(File file, String [] cmdLineArgs) {
	
	}

	/**
	 * @return the mainNamespace
	 */
	public String getMainNamespace() {
		return mainNamespace;
	}

	/**
	 * @return the skipNamespaces
	 */
	public List<String> getSkipNamespaces() {
		return skipNamespaces;
	}

	/**
	 * @return the typeNameInferencer
	 */
	public TypeNameInferencer getTypeNameInferencer() {
		return typeNameInferencer;
	}

	/**
	 * @return the simpleTypeInferencer
	 */
	public String getSimpleTypeInferencer() {
		return simpleTypeInferencer;
	}

	/**
	 * @return the attributeListInferencer
	 */
	public String getAttributeListInferencer() {
		return attributeListInferencer;
	}

	/**
	 * @return the generateEnumerations
	 */
	public boolean getGenerateEnumerations() {
		return generateEnumerations;
	}

	/**
	 * @return the minNumberOfDistinctValuesToEnum
	 */
	public int getMinNumberOfDistinctValuesToEnum() {
		return minNumberOfDistinctValuesToEnum;
	}

	/**
	 * @return the maxNumberOfDistinctValuesToEnum
	 */
	public int getMaxNumberOfDistinctValuesToEnum() {
		return maxNumberOfDistinctValuesToEnum;
	}

	/**
	 * @return the childrenPatternComparator
	 */
	public ChildrenPatternComparator getChildrenPatternComparator() {
		return childrenPatternComparator;
	}

	/**
	 * @return the attributeComparator
	 */
	public AttributeListComparator getAttributeListComparator() {
		return attributeListComparator;
	}

	/**
	 * @return the snChildrenPatternComparator
	 */
	public ChildrenPatternComparator getSnChildrenPatternComparator() {
		return snChildrenPatternComparator;
	}

	/**
	 * @return the snAttributeComparator
	 */
	public AttributeListComparator getSnAttributeListComparator() {
		return snAttributeListComparator;
	}

	/**
	 * @return the snEnumsComparator
	 */
	public EnumComparator getSnEnumsComparator() {
		return snEnumsComparator;
	}

	/**
	 * @return the tryECHARE
	 */
	public boolean getTryECHARE() {
		return tryECHARE;
	}

	/**
	 * @return the avoidSORE
	 */
	public boolean getAvoidSORE() {
		return avoidSORE;
	}

	/**
	 * @return the optimizers
	 */
	public List<RegexOptimizer> getOptimizers() {
		return optimizers;
	}

	/**
	 * @return the strictValidRootDefinitionWorkaround
	 */
	public boolean getStrictValidRootDefinitionWorkaround() {
		return strictValidRootDefinitionWorkaround;
	}

	/**
	 * @return the elementsGlobal
	 */
	public boolean getElementsGlobal() {
		return elementsGlobal;
	}

	/**
	 * @return the complexTypesGlobal
	 */
	public boolean getComplexTypesGlobal() {
		return complexTypesGlobal;
	}

	/**
	 * @return the simpleTypesGlobal
	 */
	public boolean getSimpleTypesGlobal() {
		return simpleTypesGlobal;
	}

	/**
	 * @return the typeNamesAncestorsSeparator
	 */
	public String getTypeNamesAncestorsSeparator() {
		return typeNamesAncestorsSeparator;
	}

	/**
	 * @return the mergedTypesSeparator
	 */
	public String getMergedTypesSeparator() {
		return mergedTypesSeparator;
	}

	/**
	 * @param mainNamespace the mainNamespace to set
	 */
	public void setMaintNamespace(String mainNamespace) {
		this.mainNamespace = mainNamespace;
	}

	/**
	 * @param skipNamespaces the skipNamespaces to set
	 */
	public void setSkipNamespaces(List<String> skipNamespaces) {
		this.skipNamespaces = skipNamespaces;
	}

	/**
	 * @param typeNameInferencer the typeNameInferencer to set
	 */
	public void setTypeNameInferencer(TypeNameInferencer typeNameInferencer) {
		this.typeNameInferencer = typeNameInferencer;
	}

	/**
	 * @param simpleTypeInferencer the simpleTypeInferencer to set
	 * @throws XSDConfigurationException If the provided value would lead to an error.
	 */
	public void setSimpleTypeInferencer(String simpleTypeInferencer) throws XSDConfigurationException {
		if(!simpleTypeInferencer.equalsIgnoreCase(VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL))
			throw new InvalidXSDConfigurationParameterException("Unknown simple type inferencer");
		this.simpleTypeInferencer = simpleTypeInferencer;
	}

	/**
	 * @param attributeListInferencer the attributeListInferencer to set
	 * @throws XSDConfigurationException If the provided value would lead to an error.
	 */
	public void setAttributeListInferencer(
			String attributeListInferencer) throws XSDConfigurationException {
		if(!attributeListInferencer.equalsIgnoreCase(VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL))
			throw new InvalidXSDConfigurationParameterException("Unknown attribute list inferencer");
		this.attributeListInferencer = VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL;
	}

	/**
	 * @param generateEnumerations the generateEnumerations to set
	 */
	public void setGenerateEnumerations(boolean generateEnumerations) {
		this.generateEnumerations = generateEnumerations;
	}

	/**
	 * @param minNumberOfDistinctValuesToEnum the minNumberOfDistinctValuesToEnum to set
	 */
	public void setMinNumberOfDistinctValuesToEnum(
			int minNumberOfDistinctValuesToEnum) {
		this.minNumberOfDistinctValuesToEnum = minNumberOfDistinctValuesToEnum;
	}

	/**
	 * @param maxNumberOfDistinctValuesToEnum the maxNumberOfDistinctValuesToEnum to set
	 */
	public void setMaxNumberOfDistinctValuesToEnum(
			int maxNumberOfDistinctValuesToEnum) {
		this.maxNumberOfDistinctValuesToEnum = maxNumberOfDistinctValuesToEnum;
	}

	/**
	 * @param childrenPatternComparator the childrenPatternComparator to set
	 */
	public void setChildrenPatternComparator(
			ChildrenPatternComparator childrenPatternComparator) {
		this.childrenPatternComparator = childrenPatternComparator;
	}

	/**
	 * @param attributeComparator the attributeComparator to set
	 */
	public void setAttributeListComparator(AttributeListComparator attributeComparator) {
		this.attributeListComparator = attributeComparator;
	}

	/**
	 * @param snChildrenPatternComparator the snChildrenPatternComparator to set
	 */
	public void setSnChildrenPatternComparator(
			ChildrenPatternComparator snChildrenPatternComparator) {
		this.snChildrenPatternComparator = snChildrenPatternComparator;
	}

	/**
	 * @param snAttributeComparator the snAttributeComparator to set
	 */
	public void setSnAttributeListComparator(
			AttributeListComparator snAttributeComparator) {
		this.snAttributeListComparator = snAttributeComparator;
	}

	/**
	 * @param snEnumsComparator the snEnumsComparator to set
	 */
	public void setSnEnumsComparator(EnumComparator snEnumsComparator) {
		this.snEnumsComparator = snEnumsComparator;
	}

	/**
	 * @param tryECHARE the tryECHARE to set
	 */
	public void setTryECHARE(boolean tryECHARE) {
		this.tryECHARE = tryECHARE;
	}

	/**
	 * @param avoidSORE the avoidSORE to set
	 */
	public void setAvoidSORE(boolean avoidSORE) {
		this.avoidSORE = avoidSORE;
	}

	/**
	 * @param optimizers the optimizers to set
	 */
	public void setOptimizers(List<RegexOptimizer> optimizers) {
		this.optimizers = optimizers;
	}

	/**
	 * @param strictValidRootDefinitionWorkaround the strictValidRootDefinitionWorkaround to set
	 */
	public void setStrictValidRootDefinitionWorkaround(
			boolean strictValidRootDefinitionWorkaround) {
		this.strictValidRootDefinitionWorkaround = strictValidRootDefinitionWorkaround;
	}

	/**
	 * @param elementsGlobal the elementsGlobal to set
	 */
	public void setElementsGlobal(boolean elementsGlobal) {
		this.elementsGlobal = elementsGlobal;
	}

	/**
	 * @param complexTypesGlobal the complexTypesGlobal to set
	 */
	public void setComplexTypesGlobal(boolean complexTypesGlobal) {
		this.complexTypesGlobal = complexTypesGlobal;
	}

	/**
	 * @param simpleTypesGlobal the simpleTypesGlobal to set
	 */
	public void setSimpleTypesGlobal(boolean simpleTypesGlobal) {
		this.simpleTypesGlobal = simpleTypesGlobal;
	}

	/**
	 * @param typeNamesAncestorsSeparator the typeNamesAncestorsSeparator to set
	 * @throws XSDConfigurationException if the value provided is not a valid NCName
	 */
	public void setTypeNamesAncestorsSeparator(String typeNamesAncestorsSeparator) throws XSDConfigurationException {
		if(!XML11Char.isXML11ValidNCName("a"+typeNamesAncestorsSeparator))//Workaround so that all the characters are treated as non-first characters
			throw new InvalidXSDConfigurationParameterException(typeNamesAncestorsSeparator+" is not a valid NCName");
		this.typeNamesAncestorsSeparator = typeNamesAncestorsSeparator;
	}

	/**
	 * @param mergedTypesSeparator the mergedTypesSeparator to set
	 * @throws XSDConfigurationException if the value provided is not a valid NCName
	 */
	public void setMergedTypesSeparator(String mergedTypesSeparator) throws XSDConfigurationException {
		if(!XML11Char.isXML11ValidNCName("a"+mergedTypesSeparator))//Workaround so that all the characters are treated as non-first characters
			throw new InvalidXSDConfigurationParameterException(mergedTypesSeparator+" is not a valid NCName");
		this.mergedTypesSeparator = mergedTypesSeparator;
	}

	/**
	 * @return the enumsComparator
	 */
	public EnumComparator getEnumsComparator() {
		return enumsComparator;
	}

	/**
	 * @param enumsComparator the enumsComparator to set
	 */
	public void setEnumsComparator(EnumComparator enumsComparator) {
		this.enumsComparator = enumsComparator;
	}
}
