package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Strings;

import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.impl.validate.ValidatorArg;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class FilenameValidatorTest {

	private static final String FILENAME = "test.txt";
	private static final String FILENAME_EMPTY = "";
	private static final String FILENAME_TOO_LONG = Strings.repeat("-", FileManagerProperties.MAX_OS_FILENAME_LENGTH) + ".pdf";
	private static final String FILENAME_LONG = Strings.repeat("-", FileManagerProperties.MAX_OS_FILENAME_LENGTH - 4) + ".pdf";

	private FilenameValidator filenameValidator = new FilenameValidator();
	private FileDto dto;
	private ValidatorArg<ValidatorDto> arg;
	private List<Message> messages;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testValidate() {
		FileDto dto = new FileDto();

		// happy

		dto.setFilename(FILENAME);
		arg = new ValidatorArg<>(FileManagerUtils.makeValidatorDto(dto));
		messages = filenameValidator.validate(arg);
		assertNull(messages);

		dto.setFilename(FILENAME_LONG);
		arg = new ValidatorArg<>(FileManagerUtils.makeValidatorDto(dto));
		messages = filenameValidator.validate(arg);
		assertNull(messages);

		// sad

		dto.setFilename(null);
		arg = new ValidatorArg<>(FileManagerUtils.makeValidatorDto(dto));
		messages = filenameValidator.validate(arg);
		assertNotNull(messages);

		dto.setFilename(FILENAME_EMPTY);
		arg = new ValidatorArg<>(FileManagerUtils.makeValidatorDto(dto));
		messages = filenameValidator.validate(arg);
		assertNotNull(messages);

		dto.setFilename(FILENAME_TOO_LONG);
		arg = new ValidatorArg<>(FileManagerUtils.makeValidatorDto(dto));
		messages = filenameValidator.validate(arg);
		assertNotNull(messages);

		try {
			messages = filenameValidator.validate(null);
			fail("filenameValidator.validate(null) should have thrown exception.");
		} catch (Throwable e) {
			e.printStackTrace();
			assertNotNull(e);
			assertTrue(IllegalArgumentException.class.equals(e.getClass()));
		}

		arg = new ValidatorArg<>(null);
		try {
			messages = filenameValidator.validate(null);
			fail("filenameValidator.validate(null) should have thrown exception.");
		} catch (Throwable e) {
			e.printStackTrace();
			assertNotNull(e);
			assertTrue(IllegalArgumentException.class.equals(e.getClass()));
		}

		arg = new ValidatorArg<>(FileManagerUtils.makeValidatorDto(null));
		messages = filenameValidator.validate(arg);
		assertNotNull(messages);
		assertTrue(messages.size() > 0);
		assertTrue(messages.get(0).getKey().equals(MessageKeys.FILE_DTO_NULL.getKey()));

		dto.setFilename("./bad.file");
		arg = new ValidatorArg<>(FileManagerUtils.makeValidatorDto(dto));
		messages = filenameValidator.validate(arg);
		assertNotNull(messages);
		assertTrue(messages.size() > 0);
		assertTrue(messages.get(0).getKey().equals(MessageKeys.FILE_NAME_MALFORMED.getKey()));
	}

}
