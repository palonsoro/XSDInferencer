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
package es.upm.dit.xsdinferencer.tests.merge.mergerimpl.attribute;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.merge.AttributeListComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.attribute.EqualsAttributeComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.attribute.MergeAttributeComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.attribute.SameAttributeComparator;
import es.upm.dit.xsdinferencer.merge.mergerimpl.attribute.StrictAttributeComparator;

/**
 * Tests for all the implementations of {@link AttributeListComparator}: {@link EqualsAttributeComparator}, 
 * {@link MergeAttributeComparator}, {@link SameAttributeComparator}, {@link StrictAttributeComparator}.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
public class AttributeListComparatorTest {
	
	//Fields for testing
	/**
	 * A required attribute called 'attr1' with a dummy mocked SimpleType
	 */
	private SchemaAttribute schemaAttribute1Required;
	/**
	 * A required attribute called 'attr2' with a dummy mocked SimpleType
	 */
	private SchemaAttribute schemaAttribute2Required;
	/**
	 * A required attribute called 'attr3' with a dummy mocked SimpleType
	 */
	private SchemaAttribute schemaAttribute3Required;
	
	/**
	 * An optional attribute called 'attr2' with a dummy mocked SimpleType
	 */
	private SchemaAttribute schemaAttribute2Optional;
	/**
	 * An optional attribute called 'attr3' with a dummy mocked SimpleType
	 */
	private SchemaAttribute schemaAttribute3Optional;
	/**
	 * An optional attribute called 'attr4' with a dummy mocked SimpleType
	 */
	private SchemaAttribute schemaAttribute4Optional;
	/**
	 * An optional attribute called 'attr5' with a dummy mocked SimpleType
	 */
	private SchemaAttribute schemaAttribute5Optional;
	
	/**
	 * An attribute list which reamains the same along the tests, it consists of:
	 * schemaAttribute1Required,schemaAttribute2Required, schemaAttribute3Optional, schemaAttribute4Optional
	 */
	private List<SchemaAttribute> attrList1;
	
	/**
	 * An attribute list which changes in each test method.
	 */
	private List<SchemaAttribute> attrList2;
	
	/**
	 * An empty attribute list
	 */
	private List<SchemaAttribute> attrListEmpty;
	
	@Before
	public void setUp() throws Exception {
		
		//SchemaAttribute objects cannot be mocked because we need that the methods 
		//hashCode() and equals() of SchemaAttribute behave as specified and mockito
		//does not allow to mock or redefine them.
		//Anyway, data structures are supposed to work well and are deeply tested elsewhere.
		//Some of them may be mocked because it would be easier than creating them and the difference 
		//is  notrelevant.
		
		SimpleType simpleType = mock(SimpleType.class);
		
		schemaAttribute1Required=new SchemaAttribute("attr1", "", false, simpleType);
		schemaAttribute2Required=new SchemaAttribute("attr2", "", false, simpleType);
		schemaAttribute3Required=new SchemaAttribute("attr3", "", false, simpleType);
		
		schemaAttribute2Optional=new SchemaAttribute("attr2", "", true, simpleType);
		schemaAttribute3Optional=new SchemaAttribute("attr3", "", true, simpleType);
		schemaAttribute4Optional=new SchemaAttribute("attr4", "", true, simpleType);
		schemaAttribute5Optional=new SchemaAttribute("attr5", "", true, simpleType);
		
		attrList1 = new ArrayList<SchemaAttribute>(4);
		attrList1.add(schemaAttribute1Required);
		attrList1.add(schemaAttribute2Required);
		attrList1.add(schemaAttribute3Optional);
		attrList1.add(schemaAttribute4Optional);
		
		attrList2 = new ArrayList<SchemaAttribute>(4);
		
		attrListEmpty = Collections.emptyList();
	}

	/**
	 * Checks that all the comparators return true when the lists are completely equal
	 */
	@Test
	public void testAllTrue() {
		attrList2.add(schemaAttribute1Required);
		attrList2.add(schemaAttribute2Required);
		attrList2.add(schemaAttribute3Optional);
		attrList2.add(schemaAttribute4Optional);
		
		assertTrue(new StrictAttributeComparator().compare(attrList1, attrList2));
		assertTrue(new SameAttributeComparator().compare(attrList1, attrList2));
		assertTrue(new EqualsAttributeComparator().compare(attrList1, attrList2));
		assertTrue(new MergeAttributeComparator().compare(attrList1, attrList2));
	}
	
	/**
	 * Checks that all the comparators return true when the lists are the same (it means, 
	 * they are the same according to the == operator).
	 */
	@Test
	public void testAllTrueSameList() {
//		attrList2.add(schemaAttribute1Required);
//		attrList2.add(schemaAttribute2Required);
//		attrList2.add(schemaAttribute3Optional);
//		attrList2.add(schemaAttribute4Optional);
		
		assertTrue(new StrictAttributeComparator().compare(attrList1, attrList1));
		assertTrue(new SameAttributeComparator().compare(attrList1, attrList1));
		assertTrue(new EqualsAttributeComparator().compare(attrList1, attrList1));
		assertTrue(new MergeAttributeComparator().compare(attrList1, attrList1));
	}
	
	/**
	 * Checks that Strict returns false and the other ones return true 
	 * if the optional attributes are different but all the required 
	 * attributes are the same.
	 */
	@Test
	public void testOnlyStrictFalse() {
		attrList2.add(schemaAttribute1Required);
		attrList2.add(schemaAttribute2Required);
		attrList2.add(schemaAttribute4Optional);
		attrList2.add(schemaAttribute5Optional);
		
		assertFalse(new StrictAttributeComparator().compare(attrList1, attrList2));
		assertTrue(new SameAttributeComparator().compare(attrList1, attrList2));
		assertTrue(new EqualsAttributeComparator().compare(attrList1, attrList2));
		assertTrue(new MergeAttributeComparator().compare(attrList1, attrList2));
	}
	
	/**
	 * Checks that Strict and Same return false and the others return true 
	 * if the required attributes of each list are contained in the other 
	 * one (although they may not be required at that list). 
	 */
	@Test
	public void testStrictAndSameFalse() {
		attrList2.add(schemaAttribute1Required);
		attrList2.add(schemaAttribute2Optional);
		attrList2.add(schemaAttribute3Required);
		attrList2.add(schemaAttribute5Optional);
		
		assertFalse(new StrictAttributeComparator().compare(attrList1, attrList2));
		assertFalse(new SameAttributeComparator().compare(attrList1, attrList2));
		assertTrue(new EqualsAttributeComparator().compare(attrList1, attrList2));
		assertTrue(new MergeAttributeComparator().compare(attrList1, attrList2));
	}
	
	/**
	 * Checks that only Merge returns true on a completely different list.
	 */
	@Test
	public void testOnlyMergeTrue() {
		attrList2.add(schemaAttribute5Optional);
		
		assertFalse(new StrictAttributeComparator().compare(attrList1, attrList2));
		assertFalse(new SameAttributeComparator().compare(attrList1, attrList2));
		assertFalse(new EqualsAttributeComparator().compare(attrList1, attrList2));
		assertTrue(new MergeAttributeComparator().compare(attrList1, attrList2));
	}
	
	/**
	 * This method checks that all the comparators return true if both lists are empty (they should behave so 
	 * if we follow their definition strictly).
	 */
	@Test
	public void testBothEmpty(){
		assertTrue(new StrictAttributeComparator().compare(attrListEmpty, attrListEmpty));
		assertTrue(new SameAttributeComparator().compare(attrListEmpty, attrListEmpty));
		assertTrue(new EqualsAttributeComparator().compare(attrListEmpty, attrListEmpty));
		assertTrue(new MergeAttributeComparator().compare(attrListEmpty, attrListEmpty));
	}

	/**
	 * This method checks that only the merge comparator returns true when an empty and a 
	 * non-empty list are compared 
	 */
	@Test
	public void testOneEmpty(){
		assertFalse(new StrictAttributeComparator().compare(attrList1, attrListEmpty));
		assertFalse(new SameAttributeComparator().compare(attrList1, attrListEmpty));
		assertFalse(new EqualsAttributeComparator().compare(attrList1, attrListEmpty));
		assertTrue(new MergeAttributeComparator().compare(attrList1, attrListEmpty));
	}
}
