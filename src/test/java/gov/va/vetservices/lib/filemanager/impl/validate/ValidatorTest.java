package gov.va.vetservices.lib.filemanager.impl.validate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import gov.va.ascent.framework.messages.Message;

public class ValidatorTest {

	private static final String ARG_STRING = "hello";
	private static final Integer ARG_INTEGER = Integer.valueOf(42);

	class StringValidator implements Validator<String> {
		@Override
		public List<Message> validate(ValidatorArg<String> toValidate) {
			return null;
		}
	}

	class IntegerValidator implements Validator<Integer> {
		@Override
		public List<Message> validate(ValidatorArg<Integer> toValidate) {
			return null;
		}
	}

	@Test
	public final void testValidate() {
		StringValidator sv = new StringValidator();
		assertNotNull(sv);
		ValidatorArg<String> vas = new ValidatorArg<>(ARG_STRING);
		List<Message> lms = sv.validate(vas);
		assertNull(lms);

		IntegerValidator iv = new IntegerValidator();
		assertNotNull(iv);
		ValidatorArg<Integer> vai = new ValidatorArg<>(ARG_INTEGER);
		List<Message> lmi = iv.validate(vai);
		assertNull(lmi);
	}

}
