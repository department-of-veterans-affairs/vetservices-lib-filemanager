package gov.va.vetservices.lib.filemanager.testutil;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class AbstractReflectiveFieldManager {

	protected Field getFieldFromGivenObject(final Object givenObject, final String fieldName) {
		try {
			Field requestedField = givenObject.getClass().getDeclaredField(fieldName);
			requestedField.setAccessible(true);
			if (Modifier.isFinal(requestedField.getModifiers())) {
				fail("The field named " + fieldName + " has a final modifier. Hence, cannot be modified as desired for the test.");
			} else {
				return requestedField;
			}
		} catch (NoSuchFieldException e) {
			fail("The field named " + fieldName + " does not exit. Hence, cannot be modified as desired for the test.");
		} catch (SecurityException e) {
			fail("The field named " + fieldName
					+ " is prohibited from being accessed. Hence, cannot be modified as desired for the test.");
		}

		return null;
	}

	protected void setFieldFromGivenObject(final Object givenObject, final String fieldName, final Object valueToBeSet) {
		Field fieldToSet = getFieldFromGivenObject(givenObject, fieldName);

		// else block not needed, function getFieldFromGivenObject above will fail the unit test if fieldToSet == null
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
