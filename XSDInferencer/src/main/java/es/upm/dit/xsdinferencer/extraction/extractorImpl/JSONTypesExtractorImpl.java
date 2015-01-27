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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.util.IteratorIterable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.exceptions.JSONInferenceException;
import es.upm.dit.xsdinferencer.extraction.AttributeListInferencer;
import es.upm.dit.xsdinferencer.extraction.SimpleTypeInferencer;
import es.upm.dit.xsdinferencer.extraction.TypesExtractor;

/**
 * {@link TypesExtractor} implementation intended to work with JSON input files. 
 * It transforms them into XML files by means of {@link XML} methods provided by the 
 * Java JSON library and applies to those XML files the same processing 
 * than its parent {@link TypesExtractorImpl}. 
 * 
 * @author Pablo Alonso Rodriguez (Center for Open Middleware) 
 *
 */
public class JSONTypesExtractorImpl extends TypesExtractorImpl {
	
	/**
	 * Comparator for {@link Element} objects which first looks at the namespace URI 
	 * (prefixes are ignored) and then at the name.
	 */
	public static final Comparator<Element> NAMESPACE_AND_NAME_COMPARATOR = 
			Comparator.comparing(Element::getNamespaceURI)
			          .thenComparing(Element::getName);
	
	/**
	 * Given either a {@link JSONArray} or a {@link JSONObject}, a key ending and a prefix to add, 
	 * it looks for keys at any object inside the provided JSON ending with the key ending and adds 
	 * the given prefix to that ending.
	 * This method calls itself recursively to traverse inner parts of {@link JSONObject}s.
	 * @param jsonRoot The root {@link JSONObject} or {@link JSONArray}. If an object of any other type is provided, 
	 * 					the method just does nothing.
	 * @param oldKeyEnding the ending to look for.
	 * @param prefixToAdd the prefix to add to that ending.
	 */
	private void addPrefixToJSONKeysEndingsRecursive(Object jsonRoot,String oldKeyEnding,String prefixToAdd){
		if(jsonRoot instanceof JSONObject){
			JSONObject jsonObject = (JSONObject) jsonRoot;
			SortedSet<String> keys = ImmutableSortedSet.<String>naturalOrder()
					.addAll(jsonObject.keySet())
					.build();
			for (String key : keys) {
				Object value = jsonObject.get(key);
				addPrefixToJSONKeysEndingsRecursive(value, oldKeyEnding, prefixToAdd);
				if(key.endsWith(oldKeyEnding)){
					String newKey = key.replaceAll(Pattern.quote(oldKeyEnding)+"$", prefixToAdd + oldKeyEnding);
					jsonObject.remove(key);
					jsonObject.put(newKey, value);
				}
			}
		} else if (jsonRoot instanceof JSONArray){
			JSONArray jsonArray = (JSONArray) jsonRoot;
			for(int i=0;i<jsonArray.length();i++){
				Object value = jsonArray.get(i);
				addPrefixToJSONKeysEndingsRecursive(value, oldKeyEnding, prefixToAdd);
			}
		} else{
			return;
		}
	}
	
	/**
	 * This method adds additional quotes to any String value inside a JSON. 
	 * This helps to distinguish whether values come from strings or other 
	 * primitive types when the JSON is converted to XML. 
	 * @param jsonRoot
	 */
	private void quoteStringsAtJSON(Object jsonRoot){
		if(jsonRoot instanceof JSONObject){
			JSONObject jsonObject = (JSONObject) jsonRoot;
			ImmutableSet<String> jsonObjectKeys = ImmutableSet.copyOf(jsonObject.keys());
			for (String key: jsonObjectKeys){
				Object value = jsonObject.get(key);
				if(value instanceof String){
					String valueStr = (String) value;
					jsonObject.put(key, "\""+valueStr+"\"");
				} else if (value instanceof JSONObject || value instanceof JSONArray){
					quoteStringsAtJSON(value);
				}
			}
		} else if (jsonRoot instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray) jsonRoot;
			for (int i=0;i<jsonArray.length();i++){
				Object value = jsonArray.get(i);
				if(value instanceof String){
					String valueStr = (String) value;
					jsonArray.put(i, "\""+valueStr+"\"");
				} else if (value instanceof JSONObject || value instanceof JSONArray){
					quoteStringsAtJSON(value);
				}
			}
		} else {
			return;
		}
	}

	
	
	/**
	 * This method looks recursively for arrays and encapsulates each of them into another array. 
	 * So, anything like this:<br/>
	 * <code>
	 * {"myKey":["stuff1","stuff2"]}
	 * </code><br/>
	 * would become this:<br/>
	 * <code>
	 * {"myKey":[["stuff1","stuff2"]]}
	 * </code><br/>
	 * We do this strange preprocessing because structures like the first example are  
	 * converted to XML producing this result:<br/>
	 * <code>
	 * &lt;myKey&gt;stuff1&lt;/myKey&gt;&lt;myKey&gt;stuff2&lt;/myKey&gt;
	 * </code><br/>
	 * Which makes impossible to distinguish single-element arrays from an element 
	 * outside any array, because, both this:
	 * <code>
	 * {"singleElement":["stuff"]}
	 * </code><br/>
	 * and this:<br/>
	 * <code>
	 * {"singleElement":"stuff"}
	 * </code><br/>
	 * Would be converted to:<br/>
	 * <code>
	 * &lt;singleElement&gt;stuff&lt;/singleElement&gt;
	 * </code><br/>
	 * By doing this, we allow distingushing a single-element array from an non-array element, because 
	 * the first one would be converted to:<br/>
	 * <code>
	 * &lt;singleElement&gt;&lt;array&gt;stuff&lt;/array&gt;&lt;/singleElement&gt;
	 * 
	 * @param jsonRoot The {@link JSONObject} or {@link JSONArray} which is the root of our JSON document.
	 */
	private void encapsulateArraysAtJSONRecursive(Object jsonRoot){
		if(jsonRoot instanceof JSONObject){
			JSONObject jsonObject = (JSONObject) jsonRoot;
			Set<String> keys = ImmutableSet.copyOf(jsonObject.keySet());
			for(String key:keys){
				Object value = jsonObject.get(key);
				encapsulateArraysAtJSONRecursive(value);
				if(value instanceof JSONArray){
					JSONArray encapsulatingJsonArray = new JSONArray();
					encapsulatingJsonArray.put(0,value);
					jsonObject.put(key, encapsulatingJsonArray);
				}
			}
		} else if(jsonRoot instanceof JSONArray){
			JSONArray jsonArray = (JSONArray) jsonRoot;
			for(int i=0; i<jsonArray.length();i++){
				Object value = jsonArray.get(i);
				encapsulateArraysAtJSONRecursive(value);
				if(value instanceof JSONArray){
					JSONObject encapsulatingJsonObject = new JSONObject();
					encapsulatingJsonObject.put(XSDInferenceConfiguration.ARRAY_ELEMENT_NAME,value);
					jsonArray.put(i,encapsulatingJsonObject);
				}
			}
		} else {
			return;
		}
	}

	/**
	 * This method sets a given namespace to the given element and any of its descendants whose 
	 * name is a provided one and have no namespace.
	 * @param name the name of the searched elements
	 * @param rootElement the root element to begin the search at
	 * @param namespace the namespace to set
	 */
	private void setNamespaceToElementsByName(String name, Element rootElement, Namespace namespace){
		IteratorIterable<Element> descendants = rootElement.getDescendants(Filters.element(name,Namespace.NO_NAMESPACE));
		for(Element descendant:descendants){
			descendant.setNamespace(namespace);
		}
		if(rootElement.getName().equals(name) && rootElement.getNamespace().equals(Namespace.NO_NAMESPACE)){
			rootElement.setNamespace(namespace);
		}
	}
	
	/**
	 * This method undoes what addPrefixToJSONKeysEndingsRecursive method did at the original JSON: It looks 
	 * for elements whose name ends at a desired key ending preceeded by a prefix to remove and removes it.
	 * @param rootElement the root element.
	 * @param desiredKeyEnding the desired ending.
	 * @param prefixToRemove the prefix to remove.
	 */
	private void removePrefixToElementNameEndings(Element rootElement,String desiredKeyEnding,String prefixToRemove){
		String keyToSearch=prefixToRemove+desiredKeyEnding;
		for(Element element:rootElement.getDescendants(Filters.element())){
			if(!(element.getName().endsWith(keyToSearch) && element.getNamespace().equals(Namespace.NO_NAMESPACE))){
				continue;
			}
			String name = element.getName();
			String newName = name .replaceAll(Pattern.quote(keyToSearch)+"$", desiredKeyEnding);
			element.setName(newName);
		}
	}
	
	/**
	 * This mehod sorts all the {@link Element} descendants of an element 
	 * by calling {@link Element#sortChildren(Comparator)} recursively.
	 * @param rootElement the element to start at.
	 * @param comparator the comparator used.
	 */
	private void sortElementsRecursive(Element rootElement, Comparator<Element> comparator){
		rootElement.sortChildren(comparator);
		for(Element element: rootElement.getChildren()){
			sortElementsRecursive(element,comparator);
		}
	}
	

	/**
	 * This mehod sorts all the {@link Element} descendants of an element 
	 * by calling {@link Element#sortChildren(Comparator)} recursively.
	 * We use a comparator which first compares namespace URIs and then names.
	 * 
	 * @param rootElement the element to start at.
	 */
	private void sortElementsRecursive(Element rootElement){
		sortElementsRecursive(rootElement, NAMESPACE_AND_NAME_COMPARATOR);
	}

	/**
	 * Constructor. It internally transforms input documents to XML and builds the data structures of the parent.
	 * Note that documents whose root is an array and whose root is an object must be provided in 
	 * separated lists, because {@link JSONObject} and {@link JSONArray} objects of the Java JSON library 
	 * do not share any base class or interface.
	 * 
	 * @param jsonDocumentWithRootObjects JSON input documents whose root is a JSON object.
	 * @param jsonDocumentWithRootArrays  JSON input documents whose root is a JSON array.
	 * @param configuration the inference configuration
	 * @param inferencersFactory {@link InferencersFactory} used to build {@link AttributeListInferencer} and {@link SimpleTypeInferencer} objects used.
	 * 
	 * @throws JSONInferenceException if there are problems at JSON parsing.
	 */
	public JSONTypesExtractorImpl(List<JSONObject> jsonDocumentWithRootObjects,
			List<JSONArray> jsonDocumentWithRootArrays,
			XSDInferenceConfiguration configuration,
			InferencersFactory inferencersFactory) throws JSONInferenceException{
		try {
			List<Document> xmlTranslatedDocuments = new ArrayList<Document>(
					jsonDocumentWithRootArrays.size()
							+ jsonDocumentWithRootObjects.size());
			for (Object jsonRoot : jsonDocumentWithRootObjects) {
				Namespace namespaceRoot = XSDInferenceConfiguration.NAMESPACE_ROOT_OBJECT;
				Document xmlDocument = convertJSONToXML(jsonRoot, namespaceRoot);
				xmlTranslatedDocuments.add(xmlDocument);
			}
			for (Object jsonRoot : jsonDocumentWithRootArrays) {
				Namespace namespaceRoot = XSDInferenceConfiguration.NAMESPACE_ROOT_ARRAY;
				Document xmlDocument = convertJSONToXML(jsonRoot, namespaceRoot);
				xmlTranslatedDocuments.add(xmlDocument);
			}
			//CODE SNIPPET to print internal XMLs to stderr (it may be uncomfortable to watch this from the debugger). Uncomment to use.
//			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
//			for(int i=0;i<xmlTranslatedDocuments.size();i++){
//				Document document = xmlTranslatedDocuments.get(i);
//				System.err.println("Document "+i+":");
//				outputter.output(document, System.err);
//				System.err.println();
//			}
			//END
			initializeData(xmlTranslatedDocuments, configuration,
					inferencersFactory); 
		} catch (JSONException | JDOMException | IOException e) {
			throw new JSONInferenceException("Error during extraction from JSON documents",e);
		}
	}

	/**
	 * Method that performs the conversion between JSON and XML
	 * @param jsonRoot the root of the JSON document. It must be either a {@link JSONObject} or a {@link JSONArray}
	 * @param namespaceRoot the namespace of the resulting root element
	 * @return a JDOM2 {@link Document} representing the obtained XML.
	 * @throws JDOMException
	 * @throws IOException
	 */
	protected Document convertJSONToXML(Object jsonRoot, Namespace namespaceRoot)
			throws JDOMException, IOException {
		if(!(jsonRoot instanceof JSONArray || jsonRoot instanceof JSONObject)){
			throw new IllegalArgumentException("'jsonRoot' must be either a JSONObject or a JSONArray");
		}
		quoteStringsAtJSON(jsonRoot);
		addPrefixToJSONKeysEndingsRecursive(jsonRoot, XSDInferenceConfiguration.ARRAY_ELEMENT_NAME, XSDInferenceConfiguration.ARRAY_ELEMENT_ESCAPING_PREFIX);
		encapsulateArraysAtJSONRecursive(jsonRoot);
		String xmlString = "<"+XSDInferenceConfiguration.XML_ROOT_NAME+">"+XML.toString(jsonRoot)+"</"+XSDInferenceConfiguration.XML_ROOT_NAME+">";
		SAXBuilder saxBuilder = new SAXBuilder();
		Document xmlDocument = saxBuilder.build(new StringReader(xmlString));
		setNamespaceToElementsByName(XSDInferenceConfiguration.ARRAY_ELEMENT_NAME, xmlDocument.getRootElement(), XSDInferenceConfiguration.NAMESPACE_ARRAY_ELEMENT);
		removePrefixToElementNameEndings(xmlDocument.getRootElement(), XSDInferenceConfiguration.ARRAY_ELEMENT_NAME, XSDInferenceConfiguration.ARRAY_ELEMENT_ESCAPING_PREFIX);
		xmlDocument.getRootElement().setNamespace(namespaceRoot);
		sortElementsRecursive(xmlDocument.getRootElement());
		return xmlDocument;
	}

	/**
	 * Constructor. It internally transforms input documents to XML and builds the data structures of the parent.
	 * Note that documents whose root is an array and whose root is an object must be provided in 
	 * separated lists, because {@link JSONObject} and {@link JSONArray} objects of the Java JSON library 
	 * do not share any base class or interface.
	 * This constructor uses the default {@link InferencersFactory} returned by {@link InferencersFactory#getInstance()}
	 * 
	 * @param jsonDocumentWithRootObjects JSON input documents whose root is a JSON object.
	 * @param jsonDocumentWithRootArrays  JSON input documents whose root is a JSON array.
	 * @param configuration the inference configuration
	 * 
	 * @throws JSONInferenceException if there are problems at JSON parsing.
	 */
	public JSONTypesExtractorImpl(List<JSONObject> jsonDocumentWithRootObjects,
			List<JSONArray> jsonDocumentWithRootArrays,
			XSDInferenceConfiguration configuration) throws JSONInferenceException {
		this(jsonDocumentWithRootObjects, jsonDocumentWithRootArrays,
				configuration, InferencersFactory.getInstance());
	}

}
