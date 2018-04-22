package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Strings;

import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;
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

	}

}
