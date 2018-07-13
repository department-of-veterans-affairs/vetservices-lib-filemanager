package gov.va.vetservices.lib.filemanager.testutil;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public abstract class ReflectiveFieldManager {

	final Map<Object, HashMap<String, Field>> fetchedFieldsPerObjects = new HashMap<Object, HashMap<String, Field>>();

	protected Field getFieldFromGivenObject(final Object givenObject, final String fieldName) {

		HashMap<String, Field> fetchedFields = fetchedFieldsPerObjects.get(givenObject);

		if ((fetchedFields != null) && (fetchedFields.get(fieldName) != null)) {
			return fetchedFields.get(fieldName);
		} else {
			Field requestedField;
			try {
				requestedField = givenObject.getClass().getDeclaredField(fieldName);
				requestedField.setAccessible(true);
				if (Modifier.isFinal(requestedField.getModifiers())) {
					fail("The field named " + fieldName + " has a final modifier. Hence, cannot be modified as desired for the test.");
				} else {
					if (fetchedFields == null) {
						fetchedFields = new HashMap<String, Field>();
					}
					fetchedFields.put(fieldName, requestedField);
					return requestedField;
				}
			} catch (NoSuchFieldException e) {
				fail("The field named " + fieldName + " does not exit. Hence, cannot be modified as desired for the test.");
			} catch (SecurityException e) {
				fail("The field named " + fieldName
						+ " is prohibited from being accessed. Hence, cannot be modified as desired for the test.");
			}
		}

		return null;
	}

	protected void setFieldFromGivenObject(final Object givenObject, final String fieldName, final Object valueToBeSet) {
		Field fieldToSet = getFieldFromGivenObject(givenObject, fieldName);
		if (fieldToSet != null) {
			try {
				fieldToSet.set(givenObject, valueToBeSet);
			} catch (IllegalArgumentException e) {
				fail("The field named " + fieldName
						+ ", being accessed has a final modifier. Hence, cannot be modified as desired for the test.");
			} catch (IllegalAccessException e) {
				fail("The field named " + fieldName
						+ " is prohibited from being accessed. Hence, cannot be modified as desired for the test.");
			}
		}
	}

}
