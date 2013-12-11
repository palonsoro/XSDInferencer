
package es.upm.dit.xsdinferencer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.google.common.base.Charsets;

import es.upm.dit.xsdinferencer.conversion.TypeConverter;
import es.upm.dit.xsdinferencer.conversion.converterimpl.TypeConverterImpl;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.exceptions.XSDConfigurationException;
import es.upm.dit.xsdinferencer.extraction.TypesExtractor;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.TypesExtractorImpl;
import es.upm.dit.xsdinferencer.generation.ResultsGenerator;
import es.upm.dit.xsdinferencer.generation.generatorimpl.ResultsGeneratorImpl;
import es.upm.dit.xsdinferencer.generation.generatorimpl.statisticsgeneration.StatisticResultsDocGeneratorFactory;
import es.upm.dit.xsdinferencer.generation.generatorimpl.xsdgeneration.XSDDocumentGeneratorFactory;
import es.upm.dit.xsdinferencer.merge.TypeMerger;
import es.upm.dit.xsdinferencer.merge.mergerimpl.TypeMergerImpl;
import es.upm.dit.xsdinferencer.util.xsdfilenaming.XSDFileNameGeneratorDefaultImpl;

/**
 * Main class of the XSD inferencer. It may be called from the command line via {@link XSDInferencer#main(String[])} 
 * or used as entry point to the library
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class XSDInferencer {
	
	/**
	 * Key for the input parameter which indicates a list of input files
	 */
	protected static final String KEY_INPUT_FILES = "inputFiles";
	
	/**
	 * Key for the input parameter that indicates a folder whose XML files will be used at inference process 
	 */
	protected static final String KEY_INPUT_DIRECTORY = "inputDirectory";
	
	/**
	 * Key for the input parameter that indicates the output directory for the generated files (names are generated 
	 * dinamically and cannot be changed).
	 */
	protected static final String KEY_OUTPUT_DIRECTORY = "outputDirectory";
	
//	/**
//	 * New line separator at the current system
//	 */
//	protected static final String NEW_LINE_CHARACTER = System.getProperty("line.separator");
	
	/**
	 * Text printed if the inferencer is called with the --help
	 */
	protected static final String TEXT_HELP ="XSDInferencer - A tool to infer XSDs from XML instances\r\n" + 
			"\r\n" + 
			"Usage: java -jar XSDInferencer.jar parameters\r\n" + 
			"\r\n" + 
			"Where parameters may be:\r\n" + 
			"\r\n" + 
			"--help                                                      Displays this help message and exits\r\n" + 
			"--inptutFiles file1, [file2, ...]                           A list of input files.\r\n" + 
			"                                                            It may be combined with --inputDirectory. Either --inputFiles or \r\n" + 
			"                                                            --inputDirectory must be specified.\r\n" + 
			"--inputDirectory directory                                  All the xml files of that directory are considered input files.\r\n" + 
			"                                                            It may be combined with --inputFiles. Either --inputFiles or \r\n" + 
			"                                                            --inputDirectory must be specified.\r\n" + 
			"--outputDirectory directory                                 The directory where output files will be placed. If it is not \r\n" + 
			"                                                            specified, output documents will be print to stdout.\r\n" + 
			"\r\n" + 
			"Configuration parameters: These parameters affect the global inference configuration. Please read \r\n" + 
			"the manual in order to fully understand what does each parameter mean and all their implications.\r\n" + 
			"\r\n" + 
			"--configFile file                                           The inference configuration will be read from the \r\n" + 
			"                                                            given properties file. However, concrete configuration \r\n" + 
			"                                                            values may be overwritten via the following command \r\n" + 
			"                                                            line parameters.\r\n" + 
			"[--mainNamespace namespace]                                 The specified namespace will be forced to be the main namespace \r\n" + 
			"                                                            of the inference process. It must exist in the input documents.\r\n" + 
			"[--skipNamespace namespace] [--skipNamespace namespace2...] The specified namespace will be skipped. This parameter \r\n" + 
			"                                                            may occur as many times as desired, each given namespace \r\n" + 
			"                                                            will be skipped. If this parameter is specified at least\r\n" + 
			"                                                            once at the command line, all the skipNamespace keys at \r\n" + 
			"                                                            the properties file, if given, will be ignored.\r\n" + 
			"[--typeNameInferencer {klocal|fullpath|name}]               The type name inferencer used at extraction. \r\n" + 
			"[--locality n]                                              If a klocal type name inferencer is chosen, n will be the \r\n" + 
			"                                                            locality.\r\n" + 
			"[{--generateEnumerations|--noGenerateEnumerations}]         The former one turns on enumerations generation. The latter \r\n" + 
			"                                                            turns it off.\r\n" + 
			"[--minNumberOfDistinctValuesToEnum N]                       Minimum number of distinct values on a simple type so that \r\n" + 
			"                                                            it becomes an enumeration.\r\n" + 
			"[--maxNumberOfDistinctValuesToEnum N]                       Maximum number of distinct values on a simple type so that \r\n" + 
			"                                                            it becomes an enumeration.\r\n" + 
			"[--simpleTypeInferencer SimpleTypeInferencerImpl]           Parameter designed to make easier to extend the inferencer.\r\n" + 
			"                                                            DO NOT SPECIFY ANOTHER VALUE!!!!\r\n" + 
			"[--attributeListInferencer AttributeListInferencerImpl]     Parameter designed to make easier to extend the inferencer.\r\n" + 
			"                                                            DO NOT SPECIFY ANOTHER VALUE!!!!\r\n" + 
			"[--childrenPatternComparator {no|equals|nodebased|nodesubsumed|subsumed|reduce}]\r\n" + 
			"                                                            Children pattern comparator used by the merger module at the \r\n" + 
			"                                                            normal merge step.\r\n" + 
			"[--reduceThreshold n]                                       If --childrenPatternComparator reduce is specified, n will \r\n" + 
			"                                                            be the threshold.\r\n" + 
			"[--attributeListComparator {no|equals|merge|same|strict}]   Attribute list comparator used by the merger module at the \r\n" + 
			"                                                            normal merge step.\r\n" + 
			"[--snChildrenPatternComparator {no|equals|nodebased|nodesubsumed|subsumed|reduce}]                              \r\n" + 
			"                                                            Children pattern comparator used by the merger module at the \r\n" + 
			"                                                            common-source-nodes merge step.\r\n" + 
			"[--snReduceThreshold n]                                     If --snChildrenPatternComparator reduce is specified, n will \r\n" + 
			"                                                            be the threshold.\r\n" + 
			"[--snAttributeListComparator {no|equals|merge|same|strict}] Attribute list comparator used by the merger module at the \r\n" + 
			"                                                            common-source-nodes merge step.\r\n" + 
			"[--enumsComparator {minIntersectionBidirectional|minIntersectionUnidirectional}]\r\n" + 
			"                                                            Enumerations comparator used by the merger module at the \r\n" + 
			"                                                            normal merge step.\r\n" + 
			"[--enumsComparatorThreshold]                                Threshold for the enumerations comparator used at the normal \r\n" + 
			"                                                            merge step.\r\n" + 
			"[--snEnumsComparator {minIntersectionBidirectional|minIntersectionUnidirectional}]\r\n" + 
			"                                                            Simple type comparator used by the merger module at the \r\n" + 
			"                                                            common-source-nodes merge step.\r\n" + 
			"[--snEnumsComparatorThreshold]                              Threshold for the enumerations comparator used at the normal \r\n" + 
			"                                                            common-source-nodes step.\r\n" + 
			"[{--avoidSORE|--noAvoidSORE}]                               The former forces the type converter module not to use a SORE-based\r\n" + 
			"                                                            converter but a CHARE-based or an eCHARE-based one instead.\r\n" + 
			"[{--tryECHARE|--noTryEchare}]                               The former allows the converter module to use a eCHARE-based converter \r\n" + 
			"                                                            when the SORE-based one fails or the avoidSORE configuration parameter \r\n" + 
			"                                                            is set. The latter, forbids this, so a CHARE-based converter will be \r\n" + 
			"                                                            used instead.\r\n" + 
			"[--optimizer optimizer1 [--optimizer optimizer2 ...]]       The specified optimizer will be used by the converter module. \r\n" + 
			"                                                            This parameter may occur as many times as desired, each given optimizer \r\n" + 
			"                                                            will be used. If this parameter is specified at least once at the command \r\n" + 
			"                                                            line, all the skipNamespace keys at the properties file, if given, \r\n" + 
			"                                                            will be ignored. This parameter is intended to make the \r\n" + 
			"                                                            inferencer easily extensible. ONLY USE THIS PARAMETER TO SPECIFY THE RECOMMENDED LIST \r\n" + 
			"                                                            (IF IT IS NOT SPECIFIED ELSEWHERE).\r\n" + 
			"[{--strictValidRootDefinitionWorkaround|--noStrictValidRootDefinitionWorkaround}]\r\n" + 
			"                                                            The former sets the strictValidRootDefinitionWorkaround flag and the latter \r\n" + 
			"                                                            unsets it. If the flag is set, elements and attributes declared at auxiliary \r\n" + 
			"                                                            schemas will be surrounded by groups, so that many elements or attributes \r\n" + 
			"                                                            may be declared with the same name (at the same namespace) but different types.\r\n" + 
			"[{--elementsGlobal|--noElementsGlobal}]                     If the former parameter is provided, elements will be declared globally. If the \r\n" + 
			"                                                            latter is specified, they will be declared locally.\r\n" + 
			"[{--complexTypesGlobal|--noComplexTypesGlobal}]             If the former parameter is provided, complex types will be declared globally. \r\n" + 
			"                                                            If the latter is specified, they will be declared locally.\r\n" + 
			"[{--simpleTypesGlobal|--noSimpleTypesGlobal}]               If the former parameter is provided, simple types will be declared globally. \r\n" + 
			"                                                            If the latter is specified, they will be declared locally.\r\n" + 
			"[--typeNamesAncestorsSeparator separator]                   Separator used by type name inferencers to build type names.\r\n" + 
			"[--mergedTypesSeparator separator]                          Separator used by the merger module to build merged type names.\r\n" + 
			"";
	
	/**
	 * A {@link FilenameFilter} that filters all the files with .xml extension (case insensitive).
	 */
	private static final FilenameFilter FILE_NAME_FILTER_XML_EXTENSION = new FilenameFilter() {
		
		@Override
		public boolean accept(File dir, String name) {
			if(name.toLowerCase().endsWith(".xml")){
				return true;
			}else{
				return false;
			}
		}
	};
	
	/**
	 * Method that, given a list of input documents represented as JDOM2 {@link Document} objects and an 
	 * inference configuration, does the whole inference process by calling the appropriate submodules.
	 * @param xmlFiles A list of {@link Document} object with the input documents
	 * @param configuration the inference configuration
	 * @return a {@link Results} object with the inference results (both statistics and XSDs)
	 * @throws XSDConfigurationException if there is a problem with the configuration
	 */
	public Results inferXSD(List<Document> xmlFiles, XSDInferenceConfiguration configuration) throws XSDConfigurationException {
		long startTime = System.currentTimeMillis();
		System.out.println("Starting inference process of "+xmlFiles.size()+" files");
		TypesExtractor extractor = new TypesExtractorImpl(xmlFiles, configuration);
		System.out.println("Extracting types...");
		Schema schema = extractor.getInitalSchema();
		long extractedTime = System.currentTimeMillis();
		long extractedElapsedTime = extractedTime-startTime;
		System.out.println("Types extracted in "+extractedElapsedTime+"ms");
		System.out.println("Merging types");
		TypeMerger merger = new TypeMergerImpl();
		merger.mergeTypes(schema, configuration);
		long mergedTime = System.currentTimeMillis();
		long mergedElapsedTime = mergedTime-extractedTime;
		System.out.println("Types merged in "+mergedElapsedTime+"ms");
		System.out.println("Converting automatons to regular expressions and optimizing...");
		TypeConverter converter = new TypeConverterImpl();
		converter.converTypes(schema, configuration);
		long convertedTime = System.currentTimeMillis();
		long convertedElapsedTime = convertedTime-mergedTime;
		System.out.println("Automatons converted and optimized in "+convertedElapsedTime+"ms");
		System.out.println("Generating results...");
		ResultsGenerator generator = new ResultsGeneratorImpl();
		Results results = generator.generateResults(schema, configuration, XSDDocumentGeneratorFactory.getInstance(), StatisticResultsDocGeneratorFactory.getInstance(), new XSDFileNameGeneratorDefaultImpl());
		long generatedTime = System.currentTimeMillis();
		long generatedElapsedTime=generatedTime-convertedTime;
		System.out.println("Results generated in "+generatedElapsedTime+"ms");
		System.out.println("Done!!!!");
		
		long totalElapsedTime = generatedTime-startTime;
		System.out.println("Total time: "+totalElapsedTime+"ms");
		return results;
	
	}
	
	/**
	 * Method that, given the input args lists, returns a {@link List} of {@link File} object that represent the input XML files
	 * @param args the args array, as provided by {@link XSDInferencer#main(String[])}
	 * @return a {@link List} of {@link File} object that represent the input XML files
	 * @throws FileNotFoundException if a file is not find
	 * @throws NotDirectoryException if the path to an input directory is not a path to a directory
	 */
	private List<File> getInstanceXMLFileNames(String[] args) throws FileNotFoundException, NotDirectoryException{
		boolean somethingFound = false;
		List<File> result = new ArrayList<>();
		int startingIndex=-1;
		if(args.length<1)
			throw new IllegalArgumentException("Input files parameter not found");
		for(int i=0;i<args.length;i++){
			if(args[i].equalsIgnoreCase("--"+KEY_INPUT_FILES)){
				startingIndex=i;
				somethingFound=true;
			}
			else if(args[i].equalsIgnoreCase("--"+KEY_INPUT_DIRECTORY)){
				String directoryPath = args[i+1];
				File directory = new File(directoryPath);
				if(!directory.exists())
					throw new FileNotFoundException("XMLs input files directory not found");
				if(!directory.isDirectory())
					throw new NotDirectoryException(directoryPath);
				File[] xmlFiles = directory.listFiles(FILE_NAME_FILTER_XML_EXTENSION);
				result.addAll(Arrays.asList(xmlFiles));
				somethingFound=true;
			}
			if(i>=(args.length-1)&&!somethingFound)
				throw new IllegalArgumentException("Input files parameter not found");
		}
		for(int i=startingIndex+1;(i<args.length&&!args[i].startsWith("--"));i++){
			if(startingIndex<0)
				break;
			File xmlFile = new File(args[i]);
			if(!xmlFile.exists())
				throw new FileNotFoundException("XML input file not found: "+args[i]);
			result.add(xmlFile);
		}
		if(result.size()<1)
			throw new IllegalArgumentException("Input files not found");
		return result;
	}

	/**
	 * Method that, given an args array, does the whole inference process by calling the appropriate submodules.
	 * @param args the args array, as provided by {@link XSDInferencer#main(String[])}
	 * @return a {@link Results} object with the inference results (both statistics and XSDs)
	 * @throws XSDConfigurationException if there is a problem with the configuration
	 * @throws IOException if there is an I/O problem while reading the input XML files or writing the output files
	 * @throws JDOMException if there is any problem while parsing the input XML files 
	 */
	public Results inferXSD(String[] args) throws XSDConfigurationException, IOException, JDOMException{
		List<File> xmlFiles=getInstanceXMLFileNames(args);
		XSDInferenceConfiguration configuration = new XSDInferenceConfiguration(args);
		List<Document> xmlDocuments = new ArrayList<>(xmlFiles.size());
		SAXBuilder saxBuilder = new SAXBuilder();
		for(int i=0;i<xmlFiles.size();i++){
			File xmlFile = xmlFiles.get(i);
			System.out.print("Reading XML file "+xmlFile.getName()+"...");
			FileInputStream fis = new FileInputStream(xmlFile);
			//BufferedReader reader = new BufferedReader(new InputStreamReader(fis, Charsets.UTF_8));
			Document xmlDocument = saxBuilder.build(fis);
			xmlDocuments.add(xmlDocument);
			System.out.println("OK");
		}
		return inferXSD(xmlDocuments, configuration);
	}
	
	/**
	 * This method prints the help of the tool.
	 */
	protected static void printHelp(){
		System.out.println();
		System.out.println(TEXT_HELP);
	}
	
	/**
	 * Main method, executed when the tool is invoked as a standalone application
	 * @param args an array with all the arguments passed to the application
	 * @throws XSDConfigurationException if there is a problem regarding the configuration
	 * @throws IOException if there is an I/O problem while reading the input XML files or writing the output files
	 * @throws JDOMException if there is any problem while parsing the input XML files
	 */
	public static void main(String[] args) throws XSDConfigurationException, IOException ,JDOMException {
		if(Arrays.asList(args).contains("--help")){
			printHelp();
			System.exit(0);
		}
		try {
			XSDInferencer inferencer = new XSDInferencer();
			
			Results results = inferencer.inferXSD(args);
			
			Map<String, String> schemasAsXMLStrings = results.getSchemasAsXMLStrings();
			Map<String, String> statisticsDocumentsAsXMLStrings = results.getStatisticsAsXMLStrings();
			File outputDirectory = null;
			for(int i=0;i<args.length;i++){
				if(!args[i].equalsIgnoreCase("--"+KEY_OUTPUT_DIRECTORY))
					continue;
				if(args[i+1].startsWith("--")||i==args.length-1)
					throw new IllegalArgumentException("Output directory parameter bad specified");
				outputDirectory=new File(args[i+1]);
				if(!outputDirectory.exists())
					throw new FileNotFoundException("Output directory not found.");
				if(!outputDirectory.isDirectory())
					throw new NotDirectoryException(outputDirectory.getPath());
			}
			if(outputDirectory!=null){
				System.out.println("Writing results to "+outputDirectory.getAbsolutePath());
				for(String name: schemasAsXMLStrings.keySet()){
					File currentOutpuFile = new File(outputDirectory, name);
					FileOutputStream fOs = new FileOutputStream(currentOutpuFile);
					BufferedWriter bWriter = new BufferedWriter(new OutputStreamWriter(fOs, Charsets.UTF_8));
					bWriter.write(schemasAsXMLStrings.get(name));
					bWriter.flush();
					bWriter.close();
				}
				for(String name: statisticsDocumentsAsXMLStrings.keySet()){
					File currentOutpuFile = new File(outputDirectory, name);
					FileWriter fWriter = new FileWriter(currentOutpuFile);
					BufferedWriter bWriter = new BufferedWriter(fWriter);
					bWriter.write(statisticsDocumentsAsXMLStrings.get(name));
					bWriter.flush();
					bWriter.close();
				}
				System.out.println("Results written");
			}
			else{
				for(String name: schemasAsXMLStrings.keySet()){
					System.out.println(name+":");
					System.out.println(schemasAsXMLStrings.get(name));
					System.out.println();
				}
				
				for(String name: statisticsDocumentsAsXMLStrings.keySet()){
					System.out.println(name+":");
					System.out.println(statisticsDocumentsAsXMLStrings.get(name));
					System.out.println();
				}
			}
		} catch (XSDConfigurationException | IOException | JDOMException e) {
			System.err.println();
			System.err.println("Error at inference proccess: "+e.getMessage());
			System.exit(1);
		}
	}
}
