package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;

public class ByteArrayValidatorTest {

	private static final int maxBytes = new Integer(FileManagerProperties.DEFAULT_FILE_MAX_BYTES);
	private static final byte[] bytes = "Test bytes".getBytes();
	private static final byte[] bytesEmpty = "".getBytes();
	private static final byte[] bytesMax = StringUtils.repeat("-", maxBytes).getBytes();
	private static final byte[] bytesOver = StringUtils.repeat("-", maxBytes + 1).getBytes();

	private ByteArrayValidator byteArrayValidator = new ByteArrayValidator();
	private List<Message> messages;

	@Test
	public final void testValidate() {
		// happy

		ImplArgDto<byte[]> arg = new ImplArgDto<>(bytes);
		messages = byteArrayValidator.validate(arg);
		assertNull(messages);

		arg = new ImplArgDto<>(bytesMax);
		messages = byteArrayValidator.validate(arg);
		assertNull(messages);

		// sad

		arg = new ImplArgDto<>(bytesEmpty);
		messages = byteArrayValidator.validate(arg);
		assertNotNull(messages);
		assertTrue(MessageKeys.FILE_BYTES_NULL_OR_EMPTY.getKey().equals(messages.get(0).getKey()));

		arg = new ImplArgDto<>(null);
		messages = byteArrayValidator.validate(arg);
		assertNotNull(messages);
		assertTrue(MessageKeys.FILE_BYTES_NULL_OR_EMPTY.getKey().equals(messages.get(0).getKey()));

		arg = new ImplArgDto<>(bytesOver);
		messages = byteArrayValidator.validate(arg);
		assertNotNull(messages);
		assertTrue(MessageKeys.FILE_BYTES_SIZE.getKey().equals(messages.get(0).getKey()));
	}

}
