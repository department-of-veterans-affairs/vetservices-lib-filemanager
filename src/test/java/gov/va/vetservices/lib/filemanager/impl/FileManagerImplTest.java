package gov.va.vetservices.lib.filemanager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;

public class FileManagerImplTest {

	private final static byte[] STRING_BYTES = "This is a test.".getBytes();
	private final static String STRING_FILENAME = "test.txt";

	private FileManagerImpl fileManagerImpl = new FileManagerImpl();
	FileManagerResponse response;

	@Before
	public void setUp() {
		assertNotNull(fileManagerImpl);
	}

	@Test
	public void testValidateFileForPDFConversionTest() {
		FileDto fileDto = null;

		// happy

		fileDto = new FileDto();
		fileDto.setFilebytes(STRING_BYTES);
		fileDto.setFilename(STRING_FILENAME);

		response = fileManagerImpl.validateFileForPDFConversion(fileDto);
		assertNotNull(response);
		assertNotNull(response.getMessages());
		// NOSONAR TODO line below will have to change when ConversionValidator is completed
		assertTrue(!response.getMessages().isEmpty() && response.getMessages().get(0).getKey().equals("UNFINISHED.WORK"));
		assertNull(response.getFileDto());

		// sad

		response = fileManagerImpl.validateFileForPDFConversion(null);
		assertNotNull(response);
		assertNotNull(response.getMessages());
		assertTrue(!response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		fileDto = new FileDto();
		fileDto.setFilebytes(null);
		fileDto.setFilename(STRING_FILENAME);
		response = fileManagerImpl.validateFileForPDFConversion(fileDto);
		assertNotNull(response);
		assertNotNull(response.getMessages());
		assertTrue(!response.getMessages().isEmpty());

		fileDto = new FileDto();
		fileDto.setFilebytes(STRING_BYTES);
		fileDto.setFilename(null);
		response = fileManagerImpl.validateFileForPDFConversion(fileDto);
		assertNotNull(response);
		assertNotNull(response.getMessages());
		assertTrue(!response.getMessages().isEmpty());
	}

	@Test
	public void testConvertToPdfTest() {
		FileDto fileDto = null;

		// Happy

		fileDto = new FileDto();
		fileDto.setFilebytes(STRING_BYTES);
		fileDto.setFilename(null);
		FileManagerResponse response = fileManagerImpl.convertToPdf(fileDto);
		assertNotNull(response);
		assertTrue((response.getMessages() != null) && !response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		// Sad

		response = fileManagerImpl.convertToPdf(null);
		assertNotNull(response);
		assertTrue((response.getMessages() != null) && !response.getMessages().isEmpty());
		assertNull(response.getFileDto());
	}

}
