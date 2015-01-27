package es.upm.dit.xsdinferencer.generation.generatorimpl.schemageneration;

import static es.upm.dit.xsdinferencer.XSDInferenceConfiguration.NAMESPACE_ARRAY_ELEMENT;
import static es.upm.dit.xsdinferencer.XSDInferenceConfiguration.NAMESPACE_ROOT_ARRAY;
import static es.upm.dit.xsdinferencer.XSDInferenceConfiguration.NAMESPACE_ROOT_OBJECT;
import static es.upm.dit.xsdinferencer.XSDInferenceConfiguration.XSD_NAMESPACE_PREFIX;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import es.upm.dit.xsdinferencer.XSDInferenceConfiguration;
import es.upm.dit.xsdinferencer.datastructures.All;
import es.upm.dit.xsdinferencer.datastructures.Choice;
import es.upm.dit.xsdinferencer.datastructures.ComplexType;
import es.upm.dit.xsdinferencer.datastructures.EmptyRegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Optional;
import es.upm.dit.xsdinferencer.datastructures.RegularExpression;
import es.upm.dit.xsdinferencer.datastructures.Schema;
import es.upm.dit.xsdinferencer.datastructures.SchemaElement;
import es.upm.dit.xsdinferencer.datastructures.Sequence;
import es.upm.dit.xsdinferencer.datastructures.SimpleType;
import es.upm.dit.xsdinferencer.datastructures.SingularRegularExpression;
import es.upm.dit.xsdinferencer.generation.SchemaDocumentGenerator;

/**
 * {@link SchemaDocumentGenerator} implementation to generate JSON Schemas from
 * the inference data.
 *
 * @author Pablo Alonso Rodriguez (Center for Open Middleware)
 *
 */
class JSONSchemaDocumentGenerator implements
		SchemaDocumentGenerator<JSONObject> {

	/**
	 * URI to tell the parser which version of JSON Schema should be used. It is
	 * suggested to use draft-4 (or higher), as we use many features introduced
	 * by draft-4.
	 */
	public static final String JSON_SCHEMA_VERSION_URI = "http://json-schema.org/draft-04/schema#";

	/**
	 * This method generates a subschema to represent a simple type.
	 * This subschema is a JSON object with a 'type' parameter indicating the 
	 * primitive type and, when necessary, an 'enum' parameter with the enumerated 
	 * values.
	 * 
	 * @param simpleType the simple type to represent.
	 * @return a {@link JSONObject} containing the representation.
	 */
	private JSONObject getSimpleTypeRepresentation(SimpleType simpleType) {
		
		if (simpleType.isEmpty()) {
			return new JSONObject();
		}
		boolean areThereBooleans = simpleType.containsTrueFalseValues();
		boolean areThereIntegers = simpleType.containsIntegerValues();
		boolean areThereDoublesNonIntegers = simpleType
				.containsDecimalNonIntegerValues();
		boolean areThereStrings = simpleType.getBuiltinType().equals(
				XSD_NAMESPACE_PREFIX + "string");
		boolean areThereNulls = simpleType.getKnownValuesUnmodifiableList().contains("null");
		
		JSONObject booleanSubschema = areThereBooleans ? new JSONObject(
				ImmutableMap.of("type", "boolean")) : null;
		JSONObject integerSubschema = (areThereIntegers && !areThereDoublesNonIntegers) ? new JSONObject(
				ImmutableMap.of("type", "integer")) : null;
		JSONObject numberSubschema = areThereDoublesNonIntegers ? new JSONObject(
				ImmutableMap.of("type", "number")) : null;
		JSONObject stringSubschema = areThereStrings ? new JSONObject(
				ImmutableMap.of("type", "string")) : null;
		JSONObject nullSubschema = areThereNulls ? new JSONObject(
				ImmutableMap.of("type", "null")) : null;

	    // If type is enumerated, we separate all the enums for each different primitice type
		if (simpleType.isEnum()) {
			JSONArray booleanEnums = new JSONArray();
			JSONArray integerEnums = new JSONArray();
			JSONArray numberEnums = new JSONArray();
			JSONArray stringEnums = new JSONArray();

			for (int i = 0; i < simpleType.enumerationCount(); i++) {
				String enumValue = simpleType.getEnumerationElement(i);

				Integer intValue = Ints.tryParse(enumValue);
				Double doubleValue = Doubles.tryParse(enumValue);

				if (enumValue.startsWith("\"") && enumValue.endsWith("\"")) {
					stringEnums.put(enumValue.substring(1,
							enumValue.length() - 1));
				} else if (enumValue.equals("true")
						|| enumValue.equals("false")) {
					booleanEnums.put(Boolean.parseBoolean(enumValue));
				} else if (intValue != null && !areThereDoublesNonIntegers) {
					integerEnums.put(intValue.intValue());
				} else if (doubleValue != null
						&& (areThereIntegers || areThereDoublesNonIntegers)) {
					numberEnums.put(doubleValue.doubleValue());
				}
			}

			if (areThereBooleans) {
				booleanSubschema.put("enum", booleanEnums);
			}
			if (areThereIntegers && !areThereDoublesNonIntegers) {
				integerSubschema.put("enum", integerEnums);
			}
			if (areThereDoublesNonIntegers) {
				numberSubschema.put("enum", numberEnums);
			}
			if (areThereStrings) {
				stringSubschema.put("enum", stringEnums);
			}

		}
		//Now, we join the subschemas
		JSONArray anyOfArray = new JSONArray();
		if(areThereBooleans){
			anyOfArray.put(booleanSubschema);
		}
		if(areThereIntegers && !areThereDoublesNonIntegers){
			anyOfArray.put(integerSubschema);
		}
		if(areThereDoublesNonIntegers){
			anyOfArray.put(numberSubschema);
		}
		if(areThereStrings){
			anyOfArray.put(stringSubschema);
		}
		if(areThereNulls){
			anyOfArray.put(nullSubschema);
		}
		
		JSONObject result;
		
		if(anyOfArray.length()==1){
			result=anyOfArray.getJSONObject(0);
		} else {
			result=new JSONObject();
			result.put("anyOf",anyOfArray);
		}
		
		return result;
	}

	/**
	 * This method generates a JSON object with a subschema to be included 
	 * locally on many places to refer to complex types or enumerated simple types 
	 * defined under the "definitions" from other internal places. 
	 * If the given complex type has no regular expression information (it means, 
	 * it does not restrict to either "array" or "object" JSON primitive types), 
	 * and it is not enumerated, the complete simple type representation is returned 
	 * (because those simple types are always represented in-line and not declared into "definitions").
	 * 
	 * @param complexType the complex type to represent
	 * @return a {@link JSONObject} with the type restriction subschema.
	 */
	private JSONObject getLocalTypeRestrictionSubschema(ComplexType complexType) {
		JSONObject result = new JSONObject();
		if (!(complexType.getRegularExpression() instanceof EmptyRegularExpression)) {
			result.put("$ref", "#/definitions/" + complexType.getName());
		} else if (!complexType.getTextSimpleType().isEmpty()) {
			SimpleType simpleType = complexType.getTextSimpleType();

			if (simpleType.isEnum()) {
				result.put("$ref", "#/definitions/" + simpleType.getName());
			} else {
				result=getSimpleTypeRepresentation(simpleType); //Non-enumerated simple types are always represented in-line.
			}
		} else {
			result.put("type", "null");
		}

		return result;
	}

	/**
	 * This method analyzes a regular expression and updates a subschema 
	 * containing a JSON object type description with the properties information 
	 * extracted from that regular expression.
	 * If addAsRequired, properties discovered under this method invocation are 
	 * added to the 'required' field.
	 * To handle nested regular expression, this method must be called over them. 
	 * Depending on the type of the nested regular expression, recursive invocations 
	 * may be done with 'addAsRequired' having a false value.
	 * 
	 * @param objectSubschema the object subschema to update
	 * @param addAsRequired if properties discovered at this invocation must be added to the required field. 
	 * 						When this method is called outside itself (it means, it is not a recursive call), 
	 *                      this parameter must be true.
	 * @param regex the regular expression to analyze.
	 */
	private void updatePropertiesWithRegexRecursive(JSONObject objectSubschema,
			boolean addAsRequired, RegularExpression regex) {
		JSONObject properties = objectSubschema.getJSONObject("properties");
		JSONArray required = objectSubschema.getJSONArray("required");
		boolean propagateMultipleGivenRequired = (regex instanceof Sequence)
				|| ((regex instanceof All && ((All) regex).getMinOccurs() > 0));
		boolean propagateMultipleNonRequired = (regex instanceof Choice)
				|| ((regex instanceof All && ((All) regex).getMinOccurs() == 0));
		if (regex instanceof SchemaElement) {
			SchemaElement schemaElement = (SchemaElement) regex;
			if (schemaElement.getNamespace().equals(
					NAMESPACE_ARRAY_ELEMENT.getURI())) {
				return; // We ignore array elements here.
			}
			JSONObject propertyContent = getLocalTypeRestrictionSubschema(
					schemaElement.getType());
			properties.put(schemaElement.getName(), propertyContent);
			if (addAsRequired) {
				required.put(schemaElement.getName());
			}
		} else if (regex instanceof Optional) {
			updatePropertiesWithRegexRecursive(objectSubschema, false,
					regex.getElement(0));
		} else if (propagateMultipleGivenRequired) {
			for (int i = 0; i < regex.elementCount(); i++) {
				updatePropertiesWithRegexRecursive(objectSubschema,
						addAsRequired, regex.getElement(i));
			}
		} else if (propagateMultipleNonRequired) {
			if (regex.elementCount() == 2) {
				int arrayElementFoundAtIndex = -1;
				// Check particular situation: A single element plus an array
				// element.
				for (int i = 0; i < regex.elementCount(); i++) {
					RegularExpression nestedRegularExpression = regex
							.getElement(i);
					if ((nestedRegularExpression instanceof SchemaElement)
							&& ((SchemaElement) nestedRegularExpression)
									.getNamespace().equals(
											NAMESPACE_ARRAY_ELEMENT.getURI())) {
						arrayElementFoundAtIndex = i;
					}
				}
				if (arrayElementFoundAtIndex >= 0) {
					int indexOfOtherElement = (arrayElementFoundAtIndex == 0) ? 1
							: 0;
					updatePropertiesWithRegexRecursive(objectSubschema,
							addAsRequired,
							regex.getElement(indexOfOtherElement));
					return;
				}
			}
			for (int i = 0; i < regex.elementCount(); i++) {
				updatePropertiesWithRegexRecursive(objectSubschema, false,
						regex.getElement(i));
			}

		}
	}

	/**
	 * This method analyzes a regular expression and updates an subschema describing a JSON array 
	 * type with the information gathered from that regular expression.
	 * @param arraySubschema the array type description subschema to complete
	 * @param regex the regular expression to analyze
	 */
	private void updateArraySubschemaWithInfoFromComplexType(
			JSONObject arraySubschema, RegularExpression regex) {
		for (int i = 0; i < regex.elementCount(); i++) {
			if (regex.getElement(i) instanceof SingularRegularExpression) {
				updateArraySubschemaWithInfoFromComplexType(arraySubschema,
						regex.getElement(i));
			} else if (regex.getElement(i) instanceof SchemaElement) {
				SchemaElement element = (SchemaElement) regex.getElement(i);
				if (element.getNamespace().equals(
						NAMESPACE_ARRAY_ELEMENT.getURI())) {
					JSONObject items = getLocalTypeRestrictionSubschema(
							element.getType());
					arraySubschema.put("items", items);
					break;
				}
			}
		}
	}

	/**
	 * This method takes all the key/value pairs from a source {@link JSONObject} 
	 * and puts them to a destination {@link JSONObject} (if any of then previously 
	 * existed at the destination {@link JSONObject}, the value is replaced).
	 *   
	 * @param source the source {@link JSONObject}
	 * @param destination the destination {@link JSONObject}
	 */
	private void copyAllFromAJSONObjectToAnother(JSONObject source,
			JSONObject destination) {
		for (String sourceKey : source.keySet()) {
			Object value = source.get(sourceKey);
			destination.put(sourceKey, value);
		}
	}

	/**
	 * @see SchemaDocumentGenerator#generateSchemaDocument(Schema,
	 *      XSDInferenceConfiguration)
	 */
	@Override
	public JSONObject generateSchemaDocument(Schema schema,
			XSDInferenceConfiguration configuration) {
		JSONObject schemaDocument = new JSONObject();
		schemaDocument.put("$schema", JSON_SCHEMA_VERSION_URI);

		SchemaElement rootObjectElement = null;
		for (SchemaElement possibleRootElement : schema.getElements()
				.row(NAMESPACE_ROOT_OBJECT.getURI()).values()) {
			if (possibleRootElement.isValidRoot()) {
				rootObjectElement = possibleRootElement;
				break;
			}
		}
		SchemaElement rootArrayElement = null;
		for (SchemaElement possibleRootElement : schema.getElements()
				.row(NAMESPACE_ROOT_ARRAY.getURI()).values()) {
			if (possibleRootElement.isValidRoot()) {
				rootArrayElement = possibleRootElement;
				break;
			}
		}

		if (rootObjectElement != null && rootArrayElement == null) {
			JSONObject rootObjectSubschema = getLocalTypeRestrictionSubschema(
					rootObjectElement.getType());
			copyAllFromAJSONObjectToAnother(rootObjectSubschema, schemaDocument);
		} else if (rootObjectElement == null && rootArrayElement != null) {
			JSONObject rootArraySubschema = getLocalTypeRestrictionSubschema(
					rootArrayElement.getType());
			copyAllFromAJSONObjectToAnother(rootArraySubschema, schemaDocument);
		} else if (rootObjectElement != null && rootArrayElement != null) {
			JSONObject rootObjectSubschema = getLocalTypeRestrictionSubschema(
					rootObjectElement.getType());
			JSONObject rootArraySubschema = getLocalTypeRestrictionSubschema(
					rootArrayElement.getType());
			schemaDocument.put("anyOf",
					ImmutableSet.of(rootArraySubschema, rootObjectSubschema));
		} else {
			// This exception should never be thrown unless a bug is present
			throw new IllegalStateException(
					"Neither an array or an object root has been found for JSON.");
		}

		JSONObject definitions = new JSONObject();
		schemaDocument.put("definitions", definitions);
		for (String complexTypeKey : schema.getComplexTypes().keySet()) {
			ComplexType complexType = schema.getComplexTypes().get(
					complexTypeKey);
			RegularExpression regex = complexType.getRegularExpression();
			if (!(regex instanceof EmptyRegularExpression)) {

				JSONObject objectSubschema = new JSONObject();
				objectSubschema.put("type", "object");
				objectSubschema.put("properties", new JSONObject());
				objectSubschema.put("required", new JSONArray());
				objectSubschema.put("additionalProperties", false);
				updatePropertiesWithRegexRecursive(objectSubschema, true, regex);
				if (objectSubschema.getJSONArray("required").length() == 0) {
					objectSubschema.remove("required");
				}

				JSONObject arraySubschema = new JSONObject();
				arraySubschema.put("type", "array");
				updateArraySubschemaWithInfoFromComplexType(arraySubschema,
						regex);

				JSONObject simpleTypeDescription = null;
				// This handles mixed content
				if (!complexType.getTextSimpleType().isEmpty()
						&& !complexType.getTextSimpleType()
								.consistOnlyOfWhitespaceCharacters()) {
					simpleTypeDescription = getSimpleTypeRepresentation(complexType.getTextSimpleType());
				}

				JSONObject typeDescription;

				if (arraySubschema.isNull("items")) {
					typeDescription = objectSubschema;
				} else if ((objectSubschema.getJSONObject("properties")
						.length() == 0) && (simpleTypeDescription == null)) {
					typeDescription = arraySubschema;
				} else {
					typeDescription = new JSONObject();
					ImmutableSet.Builder<JSONObject> anyOfBuilder = ImmutableSet
							.builder();
					if (!(objectSubschema.getJSONObject("properties").length() == 0)) {
						anyOfBuilder.add(objectSubschema);
					}
					if (!arraySubschema.isNull("items")) {
						anyOfBuilder.add(arraySubschema);
					}
					if (simpleTypeDescription != null) {
						JSONArray alreadyAnyOf = simpleTypeDescription.optJSONArray("anyOf");
						if(alreadyAnyOf!=null){
							for(int i=0;i<alreadyAnyOf.length();i++){
								anyOfBuilder.add(alreadyAnyOf.getJSONObject(i));
							}
						}
						else{
							anyOfBuilder.add(simpleTypeDescription);
						}
					}
					typeDescription.put("anyOf", anyOfBuilder.build());
				}

				definitions.put(complexType.getName(), typeDescription);

			} else if (complexType.getTextSimpleType().isEnum()) {
				JSONObject simpleTypeDescription = getSimpleTypeRepresentation(complexType.getTextSimpleType());
				definitions.put(complexType.getTextSimpleType().getName(),
						simpleTypeDescription);
			}
		}
		return schemaDocument;
	}

}
