package gov.va.vetservices.lib.filemanager.testutil;

import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class AbstractReflectiveAccessManager {

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

	/**
	 * Invokes a method (even if it is private) in a class and returns the object that the method returns,
	 * returns null if the method being invoked has a void return type.
	 *
	 * @param givenObject the object that the method needs to be invoked upon
	 * @param methodName the name of the method being invoked
	 * @param parameterTypes the array of data types of parameters being passed to invoke the method, can be {@code null} or empty
	 *            array for methods with no parameters
	 * @param parametersForMethod the actual parameters being passed to invoke the method, passed in as variable arguments, can be
	 *            {@code null} or empty array for methods with no parameters. Individual parameters are automatically unwrapped to
	 *            match primitive formal parameters, and both primitive and reference parameters are subject to method invocation
	 *            conversions as necessary.
	 * @return Object the object being returned by the method being invoked, {@code null} if the method has void return type. If the
	 *         value has a primitive type, it is first appropriately wrapped in an object. However, if the value has the type of an
	 *         array of a primitive type, the elements of the array are not wrapped in objects
	 * @throws InvocationTargetException
	 */
	protected Object invokeMethodForGivenObject(final Object givenObject, final String methodName, final Class<?>[] parameterTypes,
			final Object... parametersForMethod) throws InvocationTargetException {
		Object returnObject = null;

		try {
			Method methodToBeInvoked = givenObject.getClass().getDeclaredMethod(methodName, parameterTypes);
			methodToBeInvoked.setAccessible(true);
			returnObject = methodToBeInvoked.invoke(givenObject, parametersForMethod);
		} catch (NoSuchMethodException e) {
			fail("The method named " + methodName
					+ ", being accessed, does not exist. Hence, cannot be invoked as desired for the test.");
		} catch (SecurityException e) {
			fail("The method named " + methodName
					+ ", being accessed, has a security violation. Hence, cannot be invoked as desired for the test.");
		} catch (InvocationTargetException e) {
			throw e;
		} catch (IllegalAccessException e) {
			fail("The method named " + methodName
					+ ", being accessed, is not accessible. Hence, cannot be invoked as desired for the test.");
		} catch (IllegalArgumentException e) {
			fail("The method named " + methodName
					+ ", being accessed, is given inappropriate arguments. Hence, cannot be invoked as desired for the test.");
		}

		return returnObject;

	}

}
