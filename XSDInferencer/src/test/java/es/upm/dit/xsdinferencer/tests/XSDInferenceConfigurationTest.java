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
package es.upm.dit.xsdinferencer.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.conversion.RegexOptimizer;
import es.upm.dit.xsdinferencer.exceptions.XSDConfigurationException;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.KLocalTypeNameInferencer;
import es.upm.dit.xsdinferencer.merge.mergerimpl.attribute.SameAttributeComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.attribute.StrictAttributeComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.children.NodeBasedPatternComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.children.NodeSubsumptionPatternComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.children.ReducePatternComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.enumeration.MinIntersectionBidirectionalEnumComparator;

/**
 * Test class for {@link XSDInferenceConfiguration}
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class XSDInferenceConfigurationTest {

	private File propertiesFile;
	private XSDInferenceConfiguration configuration;
	private List<String> commandLine;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		File binRoot= new File(getClass().getResource("/").getFile());
		//String pathToProperties=binRoot.getParentFile().getParent()+"/src/main/resources/inferenceConfiguration.properties";
		String pathToProperties=binRoot.getParent()+"/classes/inferenceConfiguration.properties";
		propertiesFile=new File(pathToProperties);
		String[] commandLine ={"--configFile",propertiesFile.getAbsolutePath(),"--skipNamespace","http://www.w3.org/1999/xhtml","--noGenerateEnumerations","--childrenPatternComparator","nodebased","--snChildrenPatternComparator","reduce","--snReduceThreshold","0.8","--avoidSORE","--noTryEchare"};
		this.commandLine=Arrays.asList(commandLine);
	}

	@After 
	public void tearDown() throws Exception {
	}

	/**
	 * Checks that an optimizer of a class is present in a list of optimizers.
	 * 
	 * @param optimizers list of optimizers
	 * @return
	 */
	private boolean checkDefaultOptimizers(List<RegexOptimizer> optimizers){
		List<String> classes = new ArrayList<>(6);
		//If there should be n optimizers of a class, that class would have to be added n times.
		//However, there should normally be only one optimizer of each class
		classes.add("es.upm.dit.xsdinferencer.conversion.converterimpl.optimization.ChoiceOptimizer");
		classes.add("es.upm.dit.xsdinferencer.conversion.converterimpl.optimization.EmptyChildOptimizer");
		classes.add("es.upm.dit.xsdinferencer.conversion.converterimpl.optimization.EmptyOptimizer");
		classes.add("es.upm.dit.xsdinferencer.conversion.converterimpl.optimization.SingularRegularExpressionOptimizer");
		classes.add("es.upm.dit.xsdinferencer.conversion.converterimpl.optimization.SequenceOptimizer");
		classes.add("es.upm.dit.xsdinferencer.conversion.converterimpl.optimization.SingletonOptimizer");
		List<RegexOptimizer> listCopy = new ArrayList<RegexOptimizer>(optimizers);
		while(!(listCopy.isEmpty()||classes.isEmpty())){
			if(!classes.contains(listCopy.get(0).getClass().getName())){
				return false;
			}
			else{
				classes.remove(listCopy.get(0).getClass().getName());
				listCopy.remove(0);
			}
		}
		return (listCopy.isEmpty()&&classes.isEmpty());
	}
	
	/**
	 * Test method for the default constructor. Checks the default values.
	 */
	@Test
	public void testXSDInferenceConfiguration() {
		//System.out.println(propertiesFile.getAbsolutePath());
		configuration = new XSDInferenceConfiguration();
		assertNull(configuration.getMainNamespace());
		assertTrue(configuration.getSkipNamespaces().isEmpty());
		assertTrue(configuration.getTypeNameInferencer() instanceof KLocalTypeNameInferencer && ((KLocalTypeNameInferencer)configuration.getTypeNameInferencer()).getLocality()==2);
		assertEquals(XSDInferenceConfiguration.VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL, configuration.getSimpleTypeInferencer());
		assertEquals(XSDInferenceConfiguration.VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL, configuration.getAttributeListInferencer());
		assertFalse(configuration.getGenerateEnumerations());
		assertEquals(0,configuration.getMinNumberOfDistinctValuesToEnum());
		assertEquals(10,configuration.getMaxNumberOfDistinctValuesToEnum());
		assertNull(configuration.getChildrenPatternComparator());
		assertNull(configuration.getSnChildrenPatternComparator());
		assertNull(configuration.getAttributeListComparator());
		assertNull(configuration.getSnAttributeListComparator());
		assertNull(configuration.getEnumsComparator());
		assertNull(configuration.getSnEnumsComparator());
		assertFalse(configuration.getAvoidSORE());
		assertTrue(configuration.getTryECHARE());
		assertTrue(checkDefaultOptimizers(configuration.getOptimizers()));
		assertTrue(configuration.getStrictValidRootDefinitionWorkaround());
		assertFalse(configuration.getElementsGlobal());
		assertTrue(configuration.getComplexTypesGlobal());
		assertTrue(configuration.getSimpleTypesGlobal());
		assertEquals("-", configuration.getTypeNamesAncestorsSeparator());
		assertEquals("_and_", configuration.getMergedTypesSeparator());
	}

	@Test
	public void testXSDInferenceConfigurationFile() throws IOException, XSDConfigurationException {
		configuration=new XSDInferenceConfiguration(propertiesFile);
		assertNull(configuration.getMainNamespace());
		assertTrue(configuration.getSkipNamespaces().contains("http://www.w3.org/2001/XMLSchema"));
		assertEquals(1,configuration.getSkipNamespaces().size());
		assertTrue(configuration.getTypeNameInferencer() instanceof KLocalTypeNameInferencer && ((KLocalTypeNameInferencer)configuration.getTypeNameInferencer()).getLocality()==2);
		assertEquals(XSDInferenceConfiguration.VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL, configuration.getSimpleTypeInferencer());
		assertEquals(XSDInferenceConfiguration.VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL, configuration.getAttributeListInferencer());
		assertTrue(configuration.getGenerateEnumerations());
		assertEquals(0,configuration.getMinNumberOfDistinctValuesToEnum());
		assertEquals(8,configuration.getMaxNumberOfDistinctValuesToEnum());
		assertTrue((configuration.getChildrenPatternComparator() instanceof ReducePatternComparator));
		assertEquals(0.1,((ReducePatternComparator)configuration.getChildrenPatternComparator()).getThreshold(),0.001);
		assertTrue(configuration.getAttributeListComparator() instanceof StrictAttributeComparator);
		assertTrue(configuration.getSnChildrenPatternComparator() instanceof NodeSubsumptionPatternComparator);
		assertTrue(configuration.getSnAttributeListComparator() instanceof SameAttributeComparator);
		assertTrue((configuration.getEnumsComparator() instanceof MinIntersectionBidirectionalEnumComparator));
		assertEquals(((MinIntersectionBidirectionalEnumComparator)configuration.getEnumsComparator()).getThreshold(),0.9,0.001);
		assertTrue((configuration.getSnEnumsComparator() instanceof MinIntersectionBidirectionalEnumComparator));
		assertEquals(((MinIntersectionBidirectionalEnumComparator)configuration.getSnEnumsComparator()).getThreshold(),0.8,0.001);
		assertFalse(configuration.getAvoidSORE());
		assertTrue(configuration.getTryECHARE());
		assertTrue(checkDefaultOptimizers(configuration.getOptimizers()));
		assertTrue(configuration.getStrictValidRootDefinitionWorkaround());
		assertFalse(configuration.getElementsGlobal());
		assertTrue(configuration.getComplexTypesGlobal());
		assertTrue(configuration.getSimpleTypesGlobal());
		assertEquals("-", configuration.getTypeNamesAncestorsSeparator());
		assertEquals("_and_", configuration.getMergedTypesSeparator());
		
	}
	
	@Test
	public void testXSDInferenceConfigurationCommandLine() throws IOException, XSDConfigurationException {
		configuration=new XSDInferenceConfiguration(commandLine);
		assertNull(configuration.getMainNamespace());
		assertTrue(configuration.getSkipNamespaces().contains("http://www.w3.org/1999/xhtml"));
		assertEquals(1,configuration.getSkipNamespaces().size());
		assertTrue(configuration.getTypeNameInferencer() instanceof KLocalTypeNameInferencer && ((KLocalTypeNameInferencer)configuration.getTypeNameInferencer()).getLocality()==2);
		assertEquals(XSDInferenceConfiguration.VALUE_SIMPLE_TYPE_INFERENCER_DEFAULTIMPL, configuration.getSimpleTypeInferencer());
		assertEquals(XSDInferenceConfiguration.VALUE_ATTRIBUTE_LIST_INFERENCER_DEFAULTIMPL, configuration.getAttributeListInferencer());
		assertFalse(configuration.getGenerateEnumerations());
		assertEquals(0,configuration.getMinNumberOfDistinctValuesToEnum());
		assertEquals(8,configuration.getMaxNumberOfDistinctValuesToEnum());
		assertTrue((configuration.getSnChildrenPatternComparator() instanceof ReducePatternComparator));
		assertEquals(0.8,((ReducePatternComparator)configuration.getSnChildrenPatternComparator()).getThreshold(),0.001);
		assertTrue(configuration.getAttributeListComparator() instanceof StrictAttributeComparator);
		assertTrue(configuration.getChildrenPatternComparator() instanceof NodeBasedPatternComparator);
		assertTrue(configuration.getSnAttributeListComparator() instanceof SameAttributeComparator);
		assertTrue((configuration.getEnumsComparator() instanceof MinIntersectionBidirectionalEnumComparator));
		assertEquals(((MinIntersectionBidirectionalEnumComparator)configuration.getEnumsComparator()).getThreshold(),0.9,0.001);
		assertTrue((configuration.getSnEnumsComparator() instanceof MinIntersectionBidirectionalEnumComparator));
		assertEquals(((MinIntersectionBidirectionalEnumComparator)configuration.getSnEnumsComparator()).getThreshold(),0.8,0.001);
		assertTrue(configuration.getAvoidSORE());
		assertFalse(configuration.getTryECHARE());
		assertTrue(checkDefaultOptimizers(configuration.getOptimizers()));
		assertTrue(configuration.getStrictValidRootDefinitionWorkaround());
		assertFalse(configuration.getElementsGlobal());
		assertTrue(configuration.getComplexTypesGlobal());
		assertTrue(configuration.getSimpleTypesGlobal());
		assertEquals("-", configuration.getTypeNamesAncestorsSeparator());
		assertEquals("_and_", configuration.getMergedTypesSeparator());
	}

}
