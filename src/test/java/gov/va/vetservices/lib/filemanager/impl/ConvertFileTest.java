package gov.va.vetservices.lib.filemanager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
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
	ImplDto implDto;
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
		implDto = FileManagerUtils.makeImplDto(filedto);
		assertNotNull(implDto);

		response = convertFile.doConversion(implDto);
		assertNotNull(response);
		assertTrue(response.getMessages().isEmpty());
		assertNotNull(response.getFileDto());

		// sad

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(null);
		implDto = FileManagerUtils.makeImplDto(filedto);
		response = convertFile.doConversion(implDto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? false : !response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(null);
		filedto.setFilename(STRING_FILENAME);
		implDto = FileManagerUtils.makeImplDto(filedto);
		response = convertFile.doConversion(implDto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? false : !response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_NO_NAME);
		implDto = FileManagerUtils.makeImplDto(filedto);
		response = convertFile.doConversion(implDto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? true : response.getMessages().isEmpty());
		assertNotNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_NO_EXT);
		implDto = FileManagerUtils.makeImplDto(filedto);
		response = convertFile.doConversion(implDto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? false : !response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_MALFORMED);
		implDto = FileManagerUtils.makeImplDto(filedto);
		response = convertFile.doConversion(implDto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? true : response.getMessages().isEmpty());
		assertNotNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_UNSUPPORTED);
		implDto = FileManagerUtils.makeImplDto(filedto);
		response = convertFile.doConversion(implDto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? false : !response.getMessages().isEmpty());
		assertNull(response.getFileDto());
	}

}
