package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.testutil.TestingConstants;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class ByteArrayValidatorTest {

	private static final byte[] bytes = "Test bytes".getBytes();
	private static final byte[] bytesEmpty = "".getBytes();

	private int maxBytes;
	private byte[] bytesMax;
	private byte[] bytesOver;

	private List<Message> messages;

	@Autowired
	@Qualifier(ByteArrayValidator.BEAN_NAME)
	private ByteArrayValidator byteArrayValidator;

	@Before
	public final void setup() {
		assertNotNull(byteArrayValidator);

		maxBytes = byteArrayValidator.maxFileBytes();
		bytesMax = StringUtils.repeat("-", maxBytes).getBytes();
		bytesOver = StringUtils.repeat("-", maxBytes + 1).getBytes();

		if (TestingConstants.PRINT) {
			System.out.println("maxBytes:\t" + maxBytes);
			System.out.println("bytesMax:\t" + bytesMax.length);
			System.out.println("bytesOver:\t" + bytesOver.length);
		}

		assertTrue(maxBytes > 25000000);
		assertTrue(bytesMax.length == maxBytes);
		assertTrue(bytesOver.length > maxBytes);
	}

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

		arg = null;
		messages = byteArrayValidator.validate(arg);
		assertNotNull(messages);
		assertTrue(MessageKeysEnum.UNEXPECTED_ERROR.getKey().equals(messages.get(0).getKey()));

		arg = new ImplArgDto<>(bytesEmpty);
		messages = byteArrayValidator.validate(arg);
		assertNotNull(messages);
		assertTrue(MessageKeysEnum.FILE_BYTES_NULL_OR_EMPTY.getKey().equals(messages.get(0).getKey()));

		arg = new ImplArgDto<>(null);
		messages = byteArrayValidator.validate(arg);
		assertNotNull(messages);
		assertTrue(MessageKeysEnum.FILE_BYTES_NULL_OR_EMPTY.getKey().equals(messages.get(0).getKey()));

		arg = new ImplArgDto<>(bytesOver);
		messages = byteArrayValidator.validate(arg);
		assertNotNull(messages);
		assertTrue(MessageKeysEnum.FILE_BYTES_SIZE.getKey().equals(messages.get(0).getKey()));
	}

}
