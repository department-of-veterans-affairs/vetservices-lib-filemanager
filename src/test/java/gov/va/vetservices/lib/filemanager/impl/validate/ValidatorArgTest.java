package gov.va.vetservices.lib.filemanager.impl.validate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ValidatorArgTest {

	private static final String TEST_STRING = "hello";
	private static final Integer TEST_INTEGER = Integer.valueOf(42);

	@Test
	public final void testValidatorArg() {
		ValidatorArg<String> test1 = new ValidatorArg<>(TEST_STRING);
		assertNotNull(test1);

		assertNotNull(test1.getValue());
		assertTrue(String.class.equals(test1.getValue().getClass()));
		assertTrue(TEST_STRING.equals(test1.getValue()));

		ValidatorArg<Integer> test2 = new ValidatorArg<>(TEST_INTEGER);
		assertNotNull(test2);

		assertNotNull(test2.getValue());
		assertTrue(Integer.class.equals(test2.getValue().getClass()));
		assertTrue(TEST_INTEGER.equals(test2.getValue()));
	}

}
