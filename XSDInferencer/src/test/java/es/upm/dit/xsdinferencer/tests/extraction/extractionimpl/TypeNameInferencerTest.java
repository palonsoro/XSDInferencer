package es.upm.dit.xsdinferencer.tests.extraction.extractionimpl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.extraction.TypeNameInferencer;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.FullPathTypeNameInferencer;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.KLocalTypeNameInferencer;
import es.upm.dit.xsdinferencer.extraction.extractorImpl.NameTypeNameInferencer;

/**
 * Test class for [{@link TypeNameInferencer} and all of its implementations
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
public class TypeNameInferencerTest {
	
	//Fields for testing
	private List<String> normalPath;
	private List<String> notDeepPath;
	private List<String> rootPath;
	private XSDInferenceConfiguration config;

	@Before
	public void setUp() throws Exception {
		normalPath = new ArrayList<>();
		normalPath.add("_elementA");
		normalPath.add("ns_elementB");
		normalPath.add("_elementC");
		normalPath.add("_elementD");
		normalPath.add("_elementE");
		notDeepPath = new ArrayList<>();
		notDeepPath.add("_elementA");
		notDeepPath.add("ns_elementB");
		rootPath = new ArrayList<>();
		rootPath.add("_elementA");
		
		config=mock(XSDInferenceConfiguration.class);
		when(config.getTypeNamesAncestorsSeparator()).thenReturn("-");
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * This method tests {@link NameTypeNameInferencer} with the root path, the normal path and the deep path
	 */
	@Test
	public void testNameTypeInferencer() {
		TypeNameInferencer inferencer = new NameTypeNameInferencer();
		String normalPathTypeName=inferencer.inferTypeName(normalPath, config);
		assertEquals("_elementE", normalPathTypeName);
		String notDeepPathTypeName=inferencer.inferTypeName(notDeepPath, config);
		assertEquals("ns_elementB", notDeepPathTypeName);
		String rootPathTypeName=inferencer.inferTypeName(rootPath, config);
		assertEquals("_elementA", rootPathTypeName);
	}

	/**
	 * This method tests {@link FullPathTypeNameInferencer} with the root path, the normal path and the deep path
	 */
	@Test
	public void testFullPathTypeInferencer() {
		TypeNameInferencer inferencer = new FullPathTypeNameInferencer();
		String normalPathTypeName=inferencer.inferTypeName(normalPath, config);
		assertEquals("_elementA-ns_elementB-_elementC-_elementD-_elementE", normalPathTypeName);
		String notDeepPathTypeName=inferencer.inferTypeName(notDeepPath, config);
		assertEquals("_elementA-ns_elementB", notDeepPathTypeName);
		String rootPathTypeName=inferencer.inferTypeName(rootPath, config);
		assertEquals("_elementA", rootPathTypeName);
	}
	
	/**
	 * This method tests {@link KLocalTypeNameInferencer} with the root path, the normal path and the deep path
	 */
	@Test
	public void testKLocalTypeInferencer() {
		TypeNameInferencer inferencer = new KLocalTypeNameInferencer(2);
		String normalPathTypeName=inferencer.inferTypeName(normalPath, config);
		assertEquals("_elementC-_elementD-_elementE", normalPathTypeName);
		String notDeepPathTypeName=inferencer.inferTypeName(notDeepPath, config);
		assertEquals("_elementA-ns_elementB", notDeepPathTypeName);
		String rootPathTypeName=inferencer.inferTypeName(rootPath, config);
		assertEquals("_elementA", rootPathTypeName);
	}
}
