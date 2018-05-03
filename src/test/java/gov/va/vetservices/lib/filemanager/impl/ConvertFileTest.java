package gov.va.vetservices.lib.filemanager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class ConvertFileTest {
	private static final byte[] STRING_BYTES = "This is a test.".getBytes();
	private static final String STRING_FILENAME = "test.txt";
	private static final String STRING_FILENAME_NO_NAME = ".txt";
	private static final String STRING_FILENAME_NO_EXT = "test.";
	private static final String STRING_FILENAME_MALFORMED = "test..txt";
	private static final String STRING_FILENAME_UNSUPPORTED = "test.chat";

	private ConvertFile convertFile = new ConvertFile();
	FileDto filedto;
	ValidatorDto vdto;
	FileManagerResponse response;

	@Before
	public void setUp() throws Exception {
		assertNotNull(convertFile);
	}

	@Test
	public final void testDoConversion() {

		// happy

		filedto = new FileDto();
		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME);
		vdto = FileManagerUtils.makeValidatorDto(filedto);
		assertNotNull(vdto);

		response = convertFile.doConversion(vdto);
		assertNotNull(response);
		assertTrue(response.getMessages().isEmpty());
		assertNotNull(response.getFileDto());

		// sad

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(null);
		vdto = FileManagerUtils.makeValidatorDto(filedto);
		response = convertFile.doConversion(vdto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? false : !response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(null);
		filedto.setFilename(STRING_FILENAME);
		vdto = FileManagerUtils.makeValidatorDto(filedto);
		response = convertFile.doConversion(vdto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? false : !response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_NO_NAME);
		vdto = FileManagerUtils.makeValidatorDto(filedto);
		response = convertFile.doConversion(vdto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? true : response.getMessages().isEmpty());
		assertNotNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_NO_EXT);
		vdto = FileManagerUtils.makeValidatorDto(filedto);
		response = convertFile.doConversion(vdto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? false : !response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_MALFORMED);
		vdto = FileManagerUtils.makeValidatorDto(filedto);
		response = convertFile.doConversion(vdto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? true : response.getMessages().isEmpty());
		assertNotNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_UNSUPPORTED);
		vdto = FileManagerUtils.makeValidatorDto(filedto);
		response = convertFile.doConversion(vdto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? false : !response.getMessages().isEmpty());
		assertNull(response.getFileDto());
	}

}
