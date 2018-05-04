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
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class FilenameValidatorTest {

	private static final String FILENAME = "test.txt";
	private static final String FILENAME_EMPTY = "";
	private static final String FILENAME_TOO_LONG = Strings.repeat("-", FileManagerProperties.MAX_OS_FILENAME_LENGTH) + ".pdf";
	private static final String FILENAME_LONG = Strings.repeat("-", FileManagerProperties.MAX_OS_FILENAME_LENGTH - 4) + ".pdf";

	private FilenameValidator filenameValidator = new FilenameValidator();
	private ImplArgDto<ImplDto> arg;
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
		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(dto));
		messages = filenameValidator.validate(arg);
		assertNull(messages);

		dto.setFilename(FILENAME_LONG);
		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(dto));
		messages = filenameValidator.validate(arg);
		assertNull(messages);

		// sad

		dto.setFilename(null);
		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(dto));
		messages = filenameValidator.validate(arg);
		assertNotNull(messages);

		dto.setFilename(FILENAME_EMPTY);
		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(dto));
		messages = filenameValidator.validate(arg);
		assertNotNull(messages);

		dto.setFilename(FILENAME_TOO_LONG);
		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(dto));
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

		arg = new ImplArgDto<>(null);
		try {
			messages = filenameValidator.validate(null);
			fail("filenameValidator.validate(null) should have thrown exception.");
		} catch (Throwable e) {
			e.printStackTrace();
			assertNotNull(e);
			assertTrue(IllegalArgumentException.class.equals(e.getClass()));
		}

		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(null));
		messages = filenameValidator.validate(arg);
		assertNotNull(messages);
		assertTrue(messages.size() > 0);
		assertTrue(messages.get(0).getKey().equals(MessageKeysEnum.FILE_DTO_NULL.getKey()));

		dto.setFilename("./bad.file");
		arg = new ImplArgDto<>(FileManagerUtils.makeImplDto(dto));
		messages = filenameValidator.validate(arg);
		assertNotNull(messages);
		assertTrue(messages.size() > 0);
		assertTrue(messages.get(0).getKey().equals(MessageKeysEnum.FILE_NAME_MALFORMED.getKey()));
	}

}
