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

import static com.google.common.base.Preconditions.checkArgument;
import static es.upm.dit.xsdinferencer.XSDInferenceConfiguration.XSD_NAMESPACE_PREFIX;
import static es.upm.dit.xsdinferencer.XSDInferenceConfiguration.XSD_NAMESPACE_URI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom2.Attribute;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.All;
import es.upm.dit.xsdinferencer.datastructures.Choice;
import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.EmptyRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.MultipleRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Optional;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Repeated;
import es.upm.dit.xsdinferencer.datastructures.RepeatedAtLeastOnce;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.datastructures.SchemaAttribute;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.Sequence;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.datastructures.SingularRegularExpression;
import es.upm.dit.xsdinferencer.generation.XSDDocumentGenerator;
import es.upm.dit.xsdinferencer.util.xsdfilenaming.XSDFileNameGenerator;

/**
 * Default implementation for {@link XSDDocumentGenerator}.
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 */
class XSDDocumentGeneratorImpl implements XSDDocumentGenerator {

	/**
	 * @see XSDDocumentGenerator#generateSchemaDocument(Schema, XSDInferenceConfiguration, String, String, XSDFileNameGenerator)
	 */
	@Override
	public Document generateSchemaDocument(Schema schema, XSDInferenceConfiguration configuration,
			String targetNamespace, String mainNamespace, XSDFileNameGenerator fileNameGenerator) {
//		if(!configuration.getElementsGlobal()==false || 
//				!configuration.getComplexTypesGlobal()==true ||
//				!configuration.getSimpleTypesGlobal()==true
//				)
//			throw new UnsupportedOperationException("Not implemented yet.");
		//
		checkArgument(schema.getNamespacesToPossiblePrefixMappingModifiable().containsKey(mainNamespace), "The main namespace must be a known namespace");
		checkArgument(schema.getNamespacesToPossiblePrefixMappingModifiable().containsKey(targetNamespace), "The target namespace must be a known namespace");
//		checkArgument(!schema.getNamespacesToPossiblePrefixMappingModifiable().containsKey(XSD_NAMESPACE_URI),"The XSD namespace must not be a known namespace");
//		checkArgument(!schema.getNamespacesToPossiblePrefixMappingModifiable().containsKey(XSI_NAMESPACE_URI),"The XSI namespace must not be a known namespace");
		Map<String, String> namespaceURIToPrefixMappings = schema.getSolvedNamespaceMappings();
		if(configuration.getSkipNamespaces().contains(targetNamespace)){
			throw new IllegalArgumentException("This is an skipped namespace, so its XSD should not be generated");
		}
		if(targetNamespace.equals(XSD_NAMESPACE_URI))
			System.err.println("The XML Schema namespace is being considered as a target namespace in your documents. Independing of the inferred schemas, the only valid XSD for an XSD would be the normative one present at its first RFC");
		Namespace xsdNamespace = Namespace.getNamespace(XSD_NAMESPACE_PREFIX.replace(":", ""),  XSD_NAMESPACE_URI);
		List<Namespace> namespaceDeclarations = getNamespaceDeclarations(namespaceURIToPrefixMappings, xsdNamespace);
		Element elementSchema = new Element("schema",xsdNamespace);
		for(int i=0;i<namespaceDeclarations.size();i++){
			Namespace currentNamespace = namespaceDeclarations.get(i);
			elementSchema.addNamespaceDeclaration(currentNamespace);
			String currentNamespaceUri = currentNamespace.getURI();
			if(!targetNamespace.equals(mainNamespace)&&!currentNamespaceUri.equals(mainNamespace))
				continue;
			if(currentNamespace.equals(Namespace.XML_NAMESPACE)&&
					(!schema.getAttributes().containsRow(XSDInferenceConfiguration.XML_NAMESPACE_URI)&&
					 !schema.getElements().containsRow(XSDInferenceConfiguration.XML_NAMESPACE_URI))){
				continue;
			}
			if(currentNamespaceUri.equals(XSD_NAMESPACE_URI)&&
					!namespaceURIToPrefixMappings.containsKey(XSD_NAMESPACE_URI))
				continue;
			if(targetNamespace.equals(currentNamespaceUri)||
					(currentNamespaceUri.equals("")&&(fileNameGenerator==null)))
				continue;
			if(currentNamespaceUri.equals("")&&
					!currentNamespaceUri.equals(mainNamespace)&&
					!schema.getElements().containsRow(""))
				continue;
			Element importElement = new Element("import",xsdNamespace);
			if(!currentNamespaceUri.equals("")){
				Attribute namespaceAttr = new Attribute("namespace",currentNamespaceUri);
				importElement.setAttribute(namespaceAttr);
			}
			if(fileNameGenerator!=null && 
					!configuration.getSkipNamespaces().contains(currentNamespaceUri)){
				String fileName = fileNameGenerator.getSchemaDocumentFileName(currentNamespaceUri, namespaceURIToPrefixMappings);
				Attribute schemaLocationAttr = new Attribute("schemaLocation",fileName);
				importElement.setAttribute(schemaLocationAttr);
			}
			elementSchema.addContent(importElement);
		}
		
		if(!targetNamespace.equals("")){
			Attribute targetNamespaceAttr = new Attribute("targetNamespace", targetNamespace);
			elementSchema.setAttribute(targetNamespaceAttr);
		}
		Attribute elementFormDefault = new Attribute("elementFormDefault","qualified");
		elementSchema.setAttribute(elementFormDefault);
		Document resultingDocument = new Document(elementSchema);
		if (targetNamespace.equals(mainNamespace)) {
			//First, we declare global SimpleTypes.
			//If simpleTypesGlobal is true, any enumeration will be declared as a global simple type.
			//if not, simple types of complex types which have attributes but not children will be declared globally 
			//(due to limitations of XSD, they may not be declared locally together with the attributes info)
			if (configuration.getSimpleTypesGlobal()) {
				for (SimpleType simpleType : schema.getSimpleTypes().values()) {
					if (!simpleType.isEnum() || simpleType.isEmpty())
						continue;
					Element simpleTypeElement = generateSimpleType(simpleType,
							false, configuration, xsdNamespace);
					elementSchema.addContent(simpleTypeElement);
				}
			} else {
				for (ComplexType complexType : schema.getComplexTypes()
						.values()) {
					SimpleType simpleType = complexType.getTextSimpleType();
					if (complexType.getAttributeList().isEmpty()
							|| !(complexType.getAutomaton().nodeCount() == 0)
							|| !simpleType.isEnum() || simpleType.isEmpty())
						continue;
					Element simpleTypeElement = generateSimpleType(simpleType,
							false, configuration, xsdNamespace);
					elementSchema.addContent(simpleTypeElement);
				}
			}
			//Global complexType elements are only generated in the main schema (i.e. the one whose targetNamespace is equal to mainNamespace)
			if (configuration.getComplexTypesGlobal()) {
				for (ComplexType complexType : schema.getComplexTypes()
						.values()) {
					//It may be a good idea to uncomment the following code and test it.
					//Warning: it would require to rewrite too many unit tests 
//					boolean hasNoChildren = complexType.getRegularExpression().equals(new EmptyRegularExpression());
//					boolean hasNoAttributes = complexType.getAttributeList().size()==0;
//					boolean simpleTypeIsNotEmpty = !complexType.getTextSimpleType().isEmpty();
//					if(hasNoChildren&&hasNoAttributes&&simpleTypeIsNotEmpty)
//						continue; //Because the elements which are linked to this ComplexType at our internal model 
//               					  //will be linked to an XSD simple type elsewhere, either a builtin or a custom one.
					Element complexTypeElement = generateComplexType(
							configuration, complexType, false, targetNamespace,
							namespaceURIToPrefixMappings, mainNamespace,
							xsdNamespace);
					elementSchema.addContent(complexTypeElement);
				}
			}
		}
		//If there are many namespaces and the workaround is disabled, we must declare global attributes.
		//If the targetNamespace is not the mainNamespace, we must declare all the attributes.
		//if the target namespace is the main namespace, we do not need to declare anything, because the complex types which hold the attributes 
		//are also in the main namespace.
		if((namespaceURIToPrefixMappings.size()-configuration.getSkipNamespaces().size())>1){
			
			Map<String, SchemaAttribute> globalAttributeCandidates = schema.getAttributes().row(targetNamespace);
			if(!targetNamespace.equals(mainNamespace)&&!targetNamespace.equals("")){
				globalAttributesLoop:
				for(Map.Entry<String, SchemaAttribute> schemaAttributeEntry:globalAttributeCandidates.entrySet()){
					SchemaAttribute schemaAttribute=schemaAttributeEntry.getValue();
					//First, we check if the attribute has been already declared when the workaround is disabled. 
					//If so, we update the "use" property.
					//The type should have been already merged.
					if(!configuration.getStrictValidRootDefinitionWorkaround()){
						List<Element> alreadyGeneratedAttributeElements = elementSchema.getChildren("attribute", xsdNamespace);
						for(int i=0;i<alreadyGeneratedAttributeElements.size();i++){
							Element currentAttributeElement=alreadyGeneratedAttributeElements.get(i);
							if(currentAttributeElement.getAttributeValue("name").equals(schemaAttribute.getName())){
								continue globalAttributesLoop;
							}
						}
					}
					Element attributeOrAttributeGroupElement = generateAttribute(schemaAttribute, true, configuration, namespaceURIToPrefixMappings, targetNamespace, mainNamespace, schemaAttributeEntry.getKey(), xsdNamespace);
					elementSchema.addContent(attributeOrAttributeGroupElement);
				}
			}
		}
		
		//Now, we declare global elements.
		//An element will be declared globally if and only if: 
		//1-elementsGlobal is true in the configuration
		//2-The element is a valid root
		//3-The element is in a namespace other than the main namespace. Note that the element WILL be surrounded by the corresponding group if the workaround is enabled.
		//Another important remark: Iterating over a set copy implies iterating over DISTINCT SchemaElements, so if two keys pointed to equal SchemaElements, we would generate it only once-
		globalSchemaElementsLoop:
		for(SchemaElement schemaElement: schema.getElements().row(targetNamespace).values()){
//			if(!configuration.getElementsGlobal()&&
//					!schemaElement.isValidRoot()&&
//					(targetNamespace.equals(mainNamespace)||configuration.getStrictValidRootDefinitionWorkaround()))
			if(!configuration.getElementsGlobal()&&
					!schemaElement.isValidRoot()&&
					(targetNamespace.equals(mainNamespace)))
				continue;
			for(Element currentElement:elementSchema.getContent(Filters.element("element",xsdNamespace))){
				if(schemaElement.getName().equals(currentElement.getAttributeValue("name")))
					continue globalSchemaElementsLoop;
			}
			String possibleGroupName=schemaElement.getName()+configuration.getTypeNamesAncestorsSeparator()+schemaElement.getType().getName();
			for(Element currentElement:elementSchema.getContent(Filters.element("group",xsdNamespace))){
				if(possibleGroupName.equals(currentElement.getAttributeValue("name")))
					continue globalSchemaElementsLoop;
			}
			Element elementOrGroupElement = generateElement(schemaElement, true, configuration, targetNamespace, mainNamespace, null, namespaceURIToPrefixMappings, xsdNamespace);
			elementSchema.addContent(elementOrGroupElement);
		}
		return resultingDocument;
	}
	
	/**
	 * Method that generates the namespace declarations which will be appended to the root of the generated XSD files.
	 * @param namespaceURIToPrefixMappings solved namespace URI-to-prefix mappings
	 * @param xsdNamespace namespace of XSD
	 * @return a list of {@link Namespace} JDOM2 object that describe the solved mappings and includes the declaration of the 
	 * XSD namespace used by the generated XSD itself.
	 */
	private List<Namespace> getNamespaceDeclarations(Map<String,String> namespaceURIToPrefixMappings, Namespace xsdNamespace){
		List<Namespace> namespaceDeclarations = new ArrayList<>(namespaceURIToPrefixMappings.size()+1);
		namespaceDeclarations.add(xsdNamespace);
		for(String namespaceURI:namespaceURIToPrefixMappings.keySet()){
			String namespacePrefix=namespaceURIToPrefixMappings.get(namespaceURI).replace(":", "");
			if(namespaceURI.equals("")){
				namespaceDeclarations.add(Namespace.NO_NAMESPACE);
			}
			else if(namespacePrefix.equals("")){
				namespaceDeclarations.add(Namespace.getNamespace(namespaceURI));
			}
			else{
				namespaceDeclarations.add(Namespace.getNamespace(namespacePrefix,namespaceURI));
			}
		}
		return namespaceDeclarations;
	}
	
	/**
	 * Method that generates a complexType tag, wherever it has to be generated and in the correct way
	 * @param configuration the inference configuration
	 * @param complexType the complex type to which the tag will be generated
	 * @param anonymous whether this complex type must be anonymous (i.e. it must not have a 'name' attribute)
	 * @param targetNamespace the target namespace which is currently being generated
	 * @param namespaceURIToPrefixMappings solved mappings between namespace URIs and prefixes
	 * @param mainNamespace main namespace
	 * @param xsdNamespace namespace of the XML Schema
	 * @return a JDOM2 {@link Element} which describes the complex type
	 */
	private Element generateComplexType(XSDInferenceConfiguration configuration, ComplexType complexType, boolean anonymous, 
			String targetNamespace, Map<String, String> namespaceURIToPrefixMappings, String mainNamespace, Namespace xsdNamespace) {
		Element complexTypeElement = new Element("complexType",xsdNamespace);
		for(String commentOnComplexType:complexType.getComments()){
			complexTypeElement.addContent(new Comment(commentOnComplexType));
		}
		if(!anonymous){
			Attribute complexTypeNameAttr = new Attribute("name","");
			complexTypeNameAttr.setValue(complexType.getName());
			complexTypeElement.setAttribute(complexTypeNameAttr);
		}
		SimpleType simpleType = complexType.getTextSimpleType();
		boolean hasChildren = !complexType.getRegularExpression().equals(new EmptyRegularExpression());
		boolean hasNonWhitespaceSimpleContent = !simpleType.isEmpty() && !simpleType.consistOnlyOfWhitespaceCharacters();
		if(hasChildren){
			//Mixed complex type. As far as I know, XSD does not allow to constraint simple content on mixed types
			if(hasNonWhitespaceSimpleContent){
				Attribute mixedAttr = new Attribute("mixed","");
				mixedAttr.setValue("true");
				complexTypeElement.setAttribute(mixedAttr);	
			}
			Element childrenContent = generateRegexpRepresentation(complexType.getRegularExpression(), configuration, targetNamespace, mainNamespace, complexType, namespaceURIToPrefixMappings, xsdNamespace);
			if(childrenContent.getName().equals("element")){
				Element unwrappedChildrenContent = childrenContent;
				childrenContent = new Element("sequence",xsdNamespace);
				childrenContent.addContent(unwrappedChildrenContent);
			}
			complexTypeElement.addContent(childrenContent);
			List<Element> attributesInfo = generateAttributeList(complexType, targetNamespace, mainNamespace, configuration, namespaceURIToPrefixMappings, xsdNamespace);
			complexTypeElement.addContent(attributesInfo);
		}
		else if(complexType.getAttributeList().size()>0 && !simpleType.isEmpty()){
			Element simpleContentElement = new Element("simpleContent",xsdNamespace);
			Element extensionElement = new Element("extension",xsdNamespace);
			Attribute extensionBaseAttr = new Attribute("base","");
			String simpleTypeRepresentationName = complexType.getTextSimpleType().getRepresentationName(configuration.getTypeNamesAncestorsSeparator());
			if(!simpleTypeRepresentationName.contains(XSD_NAMESPACE_PREFIX)&&!namespaceURIToPrefixMappings.get(mainNamespace).equals("")){
				simpleTypeRepresentationName = namespaceURIToPrefixMappings.get(mainNamespace) + ":" + simpleTypeRepresentationName;
			}
			extensionBaseAttr.setValue(simpleTypeRepresentationName);
			extensionElement.setAttribute(extensionBaseAttr);
			List<Element> attributesInfo = generateAttributeList(complexType, targetNamespace, mainNamespace, configuration, namespaceURIToPrefixMappings, xsdNamespace);
			extensionElement.addContent(attributesInfo);
			simpleContentElement.addContent(extensionElement);
			complexTypeElement.addContent(simpleContentElement);
		}
		else if(complexType.getAttributeList().size()>0){
			List<Element> attributesInfo = generateAttributeList(complexType, targetNamespace, mainNamespace, configuration, namespaceURIToPrefixMappings, xsdNamespace);
			complexTypeElement.addContent(attributesInfo);
		}
		//If the complex type consists of a non empty simple type without either children or attributes, no complexType tag would be generated, so it is not handled here
		return complexTypeElement;
	
	}
	
	/**
	 * This method generates a {@link List} of JDOM2 {@link Element} objects describing the attributes allowed 
	 * under a complex type.
	 * @param complexType the complex type
	 * @param targetNamespace the target namespace of the XSD which is currently being generated
	 * @param mainNamespace the main namespace
	 * @param configuration the inference configuration
	 * @param namespaceURIToPrefixMappings the solved namespace URI to prefix mappings
	 * @param xsdNamespace the namespace of XSD
	 * @return the described list
	 */
	private List<Element> generateAttributeList(ComplexType complexType, String targetNamespace, String mainNamespace, XSDInferenceConfiguration configuration, 
			Map<String, String> namespaceURIToPrefixMappings, Namespace xsdNamespace){
		List<SchemaAttribute> schemaAttributeList = complexType.getAttributeList();
		List<Element> result = new ArrayList<>(schemaAttributeList.size());
		for(int i=0;i<schemaAttributeList.size();i++){
			SchemaAttribute schemaAttribute = schemaAttributeList.get(i);
			Element attribute = generateAttribute(schemaAttribute, false, configuration, namespaceURIToPrefixMappings, targetNamespace, mainNamespace, complexType.getName()+configuration.getTypeNamesAncestorsSeparator()+schemaAttribute.getName(), xsdNamespace);
			result.add(attribute);
		}
		return result;
	}
	
	/**
	 * Generates an element that describes an attribute (including name, type and optionality)
	 * @param schemaAttribute the attribute to describe
	 * @param isGlobal whether it will be declared globally or not
	 * @param configuration the inference configuration
	 * @param namespaceURIToPrefixMappings the solved mappings between namespace URIs and prefixes
	 * @param targetNamespace the target namespace of the XSD that is being currently generated
	 * @param mainNamespace the main namespace
	 * @param schemaAttributeKey the key used at the {@link Schema} attributes structures, which will be 
	 * also used to name attribute groups used at inter-namespace importing and exporting when the 
	 * workaround is enabled.
	 * @param xsdNamespace the XSD namespace
	 * @return a JDOM2 {@link Element} describing the attribute
	 */
	private Element generateAttribute(SchemaAttribute schemaAttribute, boolean isGlobal, XSDInferenceConfiguration configuration, Map<String, String> namespaceURIToPrefixMappings, 
			String targetNamespace, String mainNamespace, String schemaAttributeKey, Namespace xsdNamespace) {

		Element attributeElement = new Element("attribute",xsdNamespace);
		boolean workaround = configuration.getStrictValidRootDefinitionWorkaround();
		boolean belongsToSkippedNamespace = configuration.getSkipNamespaces().contains(schemaAttribute.getNamespace())||schemaAttribute.getNamespace().equals(xsdNamespace.getURI());
		if((schemaAttribute.getNamespace().equals(mainNamespace)||schemaAttribute.getNamespace().equals(""))||
				(isGlobal&&(!workaround))){
			fillNormalAttributeElement(schemaAttribute, attributeElement,
					configuration, namespaceURIToPrefixMappings,
					targetNamespace, mainNamespace, isGlobal, xsdNamespace);
			
			
			return attributeElement;
		}
		else if(!isGlobal&&(!workaround||belongsToSkippedNamespace)){
			Attribute attributeRefAttr = new Attribute("ref","");
			String namespacePrefix = namespaceURIToPrefixMappings.get(schemaAttribute.getNamespace());
			String attributeRefValue = schemaAttribute.getName();
			if(!namespacePrefix.equals(""))
				attributeRefValue=namespacePrefix+":"+attributeRefValue;
			attributeRefAttr.setValue(attributeRefValue);
			attributeElement.setAttribute(attributeRefAttr);
			return attributeElement;
		}
		else{
			Element attributeGroupElement = new Element("attributeGroup",xsdNamespace);
			if(isGlobal){
				Attribute attributeGroupNameAttr = new Attribute("name","");
				attributeGroupNameAttr.setValue(schemaAttributeKey);
				attributeGroupElement.setAttribute(attributeGroupNameAttr);
				fillNormalAttributeElement(schemaAttribute, attributeElement,
						configuration, namespaceURIToPrefixMappings,
						targetNamespace, mainNamespace, isGlobal, xsdNamespace);
				attributeGroupElement.addContent(attributeElement);
				return attributeGroupElement;
			}
			else{
				Attribute attributeGroupRefAttr = new Attribute("ref","");
				String namespacePrefix = namespaceURIToPrefixMappings.get(schemaAttribute.getNamespace());
				String refValue = "";
				if(!namespacePrefix.equals(""))
					refValue=namespacePrefix+":";
				refValue+=schemaAttributeKey;
				attributeGroupRefAttr.setValue(refValue);
				attributeGroupElement.setAttribute(attributeGroupRefAttr);
				Attribute attributeUseAttr = new Attribute("use","");
				String attributeUseValue = schemaAttribute.isOptional()?"optional":"required";
				attributeUseAttr.setValue(attributeUseValue);
				attributeElement.setAttribute(attributeUseAttr);
				return attributeGroupElement;
			}
		}
		

	}

	/**
	 * Fills an already created 'attribute' element (a future element called 'attribute' on the schema document) in a situation 
	 * such that it should have a name and a type, via 'type' attribute or a nested simpleType tag (depending on the configuration).
	 * @param schemaAttribute the source SchemaAttribute
	 * @param attributeElement the 'attribute' element
	 * @param configuration the inference configuration
	 * @param namespaceURIToPrefixMappings the resolved namespaceURIToPrefixMappings
	 * @param targetNamespace the target namespace of the current schema
	 * @param mainNamespace the default namespace of the inference
	 * @param isGlobal whether the attribute is being declared at the top of the schema (global) or not (because it is local, it means, declared into its enclosing complex type).
	 * @param xsdNamespace namespace URI for XML Schema Documents
	 */
	private void fillNormalAttributeElement(SchemaAttribute schemaAttribute,
			Element attributeElement,
			XSDInferenceConfiguration configuration,
			Map<String, String> namespaceURIToPrefixMappings, String targetNamespace,
			String mainNamespace, boolean isGlobal, Namespace xsdNamespace) {
		Attribute attributeNameAttr = new Attribute("name","");
		attributeNameAttr.setValue(schemaAttribute.getName());
		attributeElement.setAttribute(attributeNameAttr);
		if(configuration.getSimpleTypesGlobal()||!schemaAttribute.getSimpleType().isEnum()){
			Attribute attributeTypeAttr = new Attribute("type","");
			String simpleTypeRepresentationName = schemaAttribute.getSimpleType().getRepresentationName(configuration.getTypeNamesAncestorsSeparator());
			if(!simpleTypeRepresentationName.startsWith(XSD_NAMESPACE_PREFIX)&&!namespaceURIToPrefixMappings.get(mainNamespace).equals("")){
				simpleTypeRepresentationName = namespaceURIToPrefixMappings.get(mainNamespace) + ":" + simpleTypeRepresentationName;
			}
			attributeTypeAttr.setValue(simpleTypeRepresentationName);
			if(!schemaAttribute.getSimpleType().isEmpty())
				attributeElement.setAttribute(attributeTypeAttr);
		}
		
		else{
			Element simpleType = generateSimpleType(schemaAttribute.getSimpleType(), true, configuration, xsdNamespace);
			attributeElement.addContent(simpleType);
		}
		if(!(isGlobal&&!configuration.getStrictValidRootDefinitionWorkaround())){
			Attribute attributeUseAttr = new Attribute("use","");
			String attributeUseValue = schemaAttribute.isOptional()?"optional":"required";
			attributeUseAttr.setValue(attributeUseValue);
			attributeElement.setAttribute(attributeUseAttr);
		}
		//If the namespace of the attribute (which should be equals to the target namespace in this method)
		//is prefixed, this attribute should be marked as qualified.
		//Globally declared attributes (which may only happen if they belong to an auxiliary namespace and the workaround is disabled) 
		//are always qualified and it is prohibited to include the attribute 'form' (even with the value 'qualified'), so we omit it.
		if((schemaAttribute.getNamespace()!=null)&&
				(!schemaAttribute.getNamespace().equals(""))&&
				(!namespaceURIToPrefixMappings.get(schemaAttribute.getNamespace()).equals(""))
				&&!(isGlobal&&!configuration.getStrictValidRootDefinitionWorkaround())){
			Attribute attribtueFormAttr = new Attribute("form", "qualified");
			attributeElement.setAttribute(attribtueFormAttr);
			
		}
		
	}

	/**
	 * Generates an element that describes a {@link SimpleType}, whenever it is necessary.
	 * @param simpleType the simple type to describe
	 * @param anonymous whether it must be anonymous (lack of a 'name' attribute) or not
	 * @param configuration the inference configuration
	 * @param xsdNamespace the XSD namespace
	 * @return a JDOM2 {@link Element} that describes the {@link SimpleType}
	 */
	private Element generateSimpleType(SimpleType simpleType, boolean anonymous, XSDInferenceConfiguration configuration, Namespace xsdNamespace) {
		checkArgument(simpleType.isEnum());
		checkArgument(!simpleType.isEmpty());
		Element simpleTypeElement = new Element("simpleType",xsdNamespace);
		if(!anonymous){
			Attribute simpleTypeNameAttr = new Attribute("name","");
			simpleTypeNameAttr.setValue(simpleType.getRepresentationName(configuration.getTypeNamesAncestorsSeparator()));
			simpleTypeElement.setAttribute(simpleTypeNameAttr);
			//System.err.println(simpleType.getRepresentationName("-"));
		}
		Element restrictionElement = new Element("restriction",xsdNamespace);
		Attribute restrictionBaseAttr = new Attribute("base","");
		restrictionBaseAttr.setValue(simpleType.getBuiltinType());
		restrictionElement.setAttribute(restrictionBaseAttr);
		for(String value:simpleType){
			Element enumerationElement = new Element("enumeration",xsdNamespace);
			Attribute enumerationValueAttr = new Attribute("value","");
			enumerationValueAttr.setValue(value);
			enumerationElement.setAttribute(enumerationValueAttr);
			restrictionElement.addContent(enumerationElement);
		}
		simpleTypeElement.addContent(restrictionElement);
		return simpleTypeElement;
	
	}
	
	/**
	 * It a generates a XSD representation of a {@link RegularExpression}. It is called recursively to generate 
	 * subexpressions until one of them is a {@link SchemaElement}, then, {@link XSDDocumentGeneratorImpl#generateElement(SchemaElement, boolean, XSDInferenceConfiguration, String, String, ComplexType, Map, Namespace)} 
	 * is called (so it is not possible to recures infinitely if the {@link RegularExpression} is well defined. 
	 * This method is built in a way that allows to append its results directly to a <i>complexType</i> tag without errors.
	 * @param regexp the regular expression to represent
	 * @param configuration the inference configuration
	 * @param targetNamespace the target namespace of the XSD that is currently being generated
	 * @param mainNamespace the main namespace
	 * @param complexType the complex type that contains the regular expression
	 * @param namespaceURIToPrefixMappings solved mappings between namespace URIs and prefixes
	 * @param xsdNamespace XSD namespace
	 * @return a JDOM2 {@link Element} that describes the given {@link RegularExpression}
	 */
	private Element generateRegexpRepresentation(RegularExpression regexp, XSDInferenceConfiguration configuration, String targetNamespace, String mainNamespace, ComplexType complexType, Map<String, String> namespaceURIToPrefixMappings, Namespace xsdNamespace) {
		boolean isSchemaElement = regexp instanceof SchemaElement;
		boolean isMultipleRegularExpression = regexp instanceof MultipleRegularExpression;
		boolean isSingularRegularExpression = regexp instanceof SingularRegularExpression;
		boolean isEmptyRegularExpression = regexp instanceof EmptyRegularExpression;
		if(isSchemaElement){
			return generateElement((SchemaElement)regexp, false, configuration, targetNamespace, mainNamespace, complexType, namespaceURIToPrefixMappings, xsdNamespace);
		}
		else if(isMultipleRegularExpression){
			String elementName;
			if(regexp instanceof All)
				elementName="all";
			else if(regexp instanceof Choice)
				elementName="choice";
			else if(regexp instanceof Sequence)
				elementName="sequence";
			else 
				throw new IllegalArgumentException("Unknown kind of MultipleRegularExpression: "+regexp);
			Element currentElement = new Element(elementName,xsdNamespace);
			for(int i=0;i<regexp.elementCount();i++){
				Element currentElementChild = generateRegexpRepresentation(regexp.getElement(i), configuration, targetNamespace, mainNamespace, complexType, namespaceURIToPrefixMappings, xsdNamespace);
				if(currentElementChild!=null)
					currentElement.addContent(currentElementChild);
			}
			return currentElement;
		}
		else if(isSingularRegularExpression){
			RegularExpression regexpChild=regexp.getElement(0);
			if(!((regexpChild instanceof MultipleRegularExpression)||(regexpChild instanceof SchemaElement))){
				throw new IllegalArgumentException("A child of a SingularRegularExpression may only be an SchemaElement or a MultipleRegularExpression. \nPrevious optimization steps should have avoided other combinations.");
			}
			Element generatedChild = generateRegexpRepresentation(regexpChild, configuration, targetNamespace, mainNamespace, complexType, namespaceURIToPrefixMappings, xsdNamespace);
			Attribute minOccursAttr=new Attribute("minOccurs","");
			Attribute maxOccursAttr=new Attribute("maxOccurs","");
			if(regexp instanceof Optional){
				minOccursAttr.setValue("0");
				maxOccursAttr.setValue("1");
			}
			else if(regexp instanceof Repeated){
				minOccursAttr.setValue("0");
				maxOccursAttr.setValue("unbounded");
			}
			else if(regexp instanceof RepeatedAtLeastOnce){
				minOccursAttr.setValue("1");
				maxOccursAttr.setValue("unbounded");
			}
			else{
				throw new IllegalArgumentException("Unknown kind of SingularRegularExpression: "+regexp);
			}
			generatedChild.setAttribute(maxOccursAttr);
			generatedChild.setAttribute(minOccursAttr);
			return generatedChild;
		}
		else if(isEmptyRegularExpression){
			return null;
		}
		throw new IllegalArgumentException("Unknown kind of RegularExpression: "+regexp);
	
	}
	
	/**
	 * Generates an {@link Element} that describes the {@link SchemaElement} given.
	 * @param schemaElement the schema element to be represented
	 * @param isGlobal whether this element is being declared globally
	 * @param configuration the inference configuration
	 * @param targetNamespace the target namespace of the XSD that is currently being generated
	 * @param mainNamespace the main namespace
	 * @param parentComplexType the complex type under which this element may occur (if any). If it may not occur under any complex type (because it is a root element, for example), null must be passed.
	 * @param namespaceURIToPrefixMappings solved mappings between namespace URIs and prefixes
	 * @param xsdNamespace XSD namespace
	 * @return a JDOM2 {@link Element} that describes the given {@link SchemaElement}
	 */
	private Element generateElement(SchemaElement schemaElement, boolean isGlobal, XSDInferenceConfiguration configuration, 
			String targetNamespace, String mainNamespace, ComplexType parentComplexType, Map<String, String> namespaceURIToPrefixMappings, Namespace xsdNamespace) {
		
		//This variables and the isGlobal parameter will be used to calculate the conditions which will determine what element (of the future schema document) should be generated. 
		boolean elementsGlobal=configuration.getElementsGlobal();
		boolean namespacesDiffer=!mainNamespace.equals(schemaElement.getNamespace());
		boolean workaround = configuration.getStrictValidRootDefinitionWorkaround();
		boolean isValidRoot = schemaElement.isValidRoot();
		boolean belongsToSkippedNamespace = configuration.getSkipNamespaces().contains(schemaElement.getNamespace());
		//These are the conditions
		boolean avoidWorkaround = isValidRoot || belongsToSkippedNamespace;
		boolean normalElement=(elementsGlobal&&isGlobal)||(!elementsGlobal&&isGlobal&&namespacesDiffer&&(!(workaround&&!avoidWorkaround)))||
				(!elementsGlobal&&!isGlobal&&!namespacesDiffer)||(isGlobal&&isValidRoot);
		boolean referencingAnElement=(elementsGlobal&&!isGlobal)||(!elementsGlobal&&!isGlobal&&namespacesDiffer&&(!(workaround&&!avoidWorkaround)));
		boolean referencingAGroup=(!elementsGlobal&&!isGlobal&&namespacesDiffer&&(workaround&&!avoidWorkaround));
		boolean grouppedElement=(!elementsGlobal&&isGlobal&&namespacesDiffer&&(workaround&&!avoidWorkaround));
		if(referencingAnElement){
			Element elementElement = new Element("element",xsdNamespace);
			Attribute elementRefAttr = new Attribute("ref","");
			String refValue = schemaElement.getName();
			String prefix = namespaceURIToPrefixMappings.get(schemaElement.getNamespace());
			if(!prefix.equals("")) {
				refValue=prefix+":"+refValue;
			}
			elementRefAttr.setValue(refValue);
			elementElement.setAttribute(elementRefAttr);
			return elementElement;
		} else {
			String possibleGroupName = schemaElement.getName()+configuration.getTypeNamesAncestorsSeparator()+schemaElement.getType().getName();
			if(normalElement||grouppedElement){
				Element elementElement = new Element("element",xsdNamespace);
				Attribute elementNameAttr = new Attribute("name","");
				elementNameAttr.setValue(schemaElement.getName());
				elementElement.setAttribute(elementNameAttr);
				boolean isSimpleElement = (schemaElement.getType().getRegularExpression() instanceof EmptyRegularExpression);
				isSimpleElement = isSimpleElement && schemaElement.getType().getAttributeList().isEmpty();
				isSimpleElement = isSimpleElement && !schemaElement.getType().getTextSimpleType().isEmpty();
				if(isSimpleElement){
					if(configuration.getSimpleTypesGlobal()||!schemaElement.getType().getTextSimpleType().isEnum()){
						Attribute elementTypeAttr = new Attribute("type","");
						String typeStr = schemaElement.getType().getTextSimpleType().getRepresentationName(configuration.getTypeNamesAncestorsSeparator());
						if(!namespaceURIToPrefixMappings.get(mainNamespace).equals("")&&!(typeStr.startsWith(XSD_NAMESPACE_PREFIX))){
							typeStr = namespaceURIToPrefixMappings.get(mainNamespace)+":"+typeStr;
						}
						elementTypeAttr.setValue(typeStr);
						elementElement.setAttribute(elementTypeAttr);
					}
					else{
						Element elementSimpleTypeElement = generateSimpleType(schemaElement.getType().getTextSimpleType(), true, configuration, xsdNamespace);
						elementElement.addContent(elementSimpleTypeElement);
					}
				}
				else{
					if(configuration.getComplexTypesGlobal()){
						Attribute elementTypeAttr = new Attribute("type","");
						String typeStr = schemaElement.getType().getName();
						if(!namespaceURIToPrefixMappings.get(mainNamespace).equals("")){
							typeStr = namespaceURIToPrefixMappings.get(mainNamespace)+":"+typeStr;
						}
						elementTypeAttr.setValue(typeStr);
						elementElement.setAttribute(elementTypeAttr);
					}
					else{
						Element elementComplexTypeElement = generateComplexType(configuration, schemaElement.getType(), true, 
								targetNamespace, namespaceURIToPrefixMappings, mainNamespace, xsdNamespace);
								
						elementElement.addContent(elementComplexTypeElement);
					}
				}
				if(grouppedElement){
					Element elementGroup = new Element("group",xsdNamespace);
					Attribute groupNameAttr = new Attribute("name","");
					groupNameAttr.setValue(possibleGroupName);
					elementGroup.setAttribute(groupNameAttr);
					Element sequenceInGroupElement= new Element("sequence",xsdNamespace);
					sequenceInGroupElement.addContent(elementElement);
					elementGroup.addContent(sequenceInGroupElement);
					return elementGroup;
				}
				else{
					return elementElement;
				}
			}
			else if(referencingAGroup){
				Element elementGroup = new Element("group",xsdNamespace);
				Attribute groupRefAttr = new Attribute("ref","");
				String groupQName = possibleGroupName;
				String namespacePrefix=namespaceURIToPrefixMappings.get(schemaElement.getNamespace());
				if(!namespacePrefix.equals(""))
					groupQName=namespacePrefix+":"+groupQName;
				groupRefAttr.setValue(groupQName);
				elementGroup.setAttribute(groupRefAttr);
				return elementGroup;
			}
			else{
				throw new IllegalArgumentException("The parameters given have lead to an invalid situation");
			}
		}

	}

	
}
