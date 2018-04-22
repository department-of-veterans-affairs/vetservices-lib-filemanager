package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.Test;

import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;
import gov.va.vetservices.lib.filemanager.impl.validate.ValidatorArg;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;
import gov.va.vetservices.lib.filemanager.testutil.TestingConstants;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class FileConvertibleValidatorTest extends AbstractFileHandler {

	private static final byte[] bytes = "This is a text/plain byte array".getBytes();
	private static final String filename = "plaintext.txt";

	private FileConvertibleValidator fileConvertibleValidator = new FileConvertibleValidator();
	private FileDto dto;
	private ValidatorArg<ValidatorDto> arg;
	private List<Message> messages;

	@Test
	public final void testValidate() {
		FileDto dto = new FileDto();
		dto.setFilebytes(bytes);
		dto.setFilename(filename);

		arg = new ValidatorArg<>(FileManagerUtils.makeValidatorDto(dto));
		messages = fileConvertibleValidator.validate(arg);
		assertNull(messages);

		for (ConvertibleTypesEnum enumeration : ConvertibleTypesEnum.values()) {
			List<File> files = super.listFilesByMimePath(enumeration.getMimeType());
			assertTrue("Files for " + enumeration.getMimeString() + " is null or empty.", (files != null) && !files.isEmpty());

			// we have to detect every file in the directory
			// to confirm that we can detect the variations of that file type
			for (File file : files) {
				if (!file.exists()) {
					fail("File enumerated by AbstractFileManager.getFilesByMimePath() returned non-existent file " + file.getPath());
				}

				try {
					byte[] readBytes = Files.readAllBytes(file.toPath());
					dto = new FileDto();
					dto.setFilebytes(readBytes);
					dto.setFilename(file.getName());
					arg = new ValidatorArg<>(FileManagerUtils.makeValidatorDto(dto));
					messages = fileConvertibleValidator.validate(arg);

					if ((messages != null) && (file.getName().contains(TestingConstants.TEST_FILE_PREFIX_CORRUPT)
							|| file.getName().contains(TestingConstants.TEST_FILE_PREFIX_ILLEGITIMATE))) {
						assertNotNull(file.getName() + " messages are null.", messages);
					} else {
						if (file.getName().startsWith(TestingConstants.TEST_FILE_PREFIX_LEGITIMATE)) {
							assertNull(file.getName() + " messages not null.", messages);
						} else if (file.getName().startsWith(TestingConstants.TEST_FILE_PREFIX_CORRUPT)) {
							assertNotNull(file.getName() + " messages are null.", messages);
						} else if (file.getName().startsWith(TestingConstants.TEST_FILE_PREFIX_ILLEGITIMATE)) {
							assertNotNull(file.getName() + " messages are null.", messages);
						}
					}

				} catch (IOException e) {
					fail("Could not read file: " + file.toPath());
				}
			}
		}
	}

}
