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
	private FileDto fileDto;
	FileManagerResponse response;

	@Before
	public void setUp() {
		assertNotNull(fileManagerImpl);
	}

	@Test
	public void testValidateFileForPDFConversionTest() {

		// happy
		fileDto = new FileDto();
		fileDto.setFilebytes(STRING_BYTES);
		fileDto.setFilename(STRING_FILENAME);

		response = fileManagerImpl.validateFileForPDFConversion(fileDto);
		assertNotNull(response);
		assertNotNull(response.getMessages());
		assertTrue(response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		// sad

		response = fileManagerImpl.validateFileForPDFConversion(null);
		assertNotNull(response);
		assertNotNull(response.getMessages());
		assertTrue(!response.getMessages().isEmpty());
		assertNull(response.getFileDto());
	}

	@Test
	public void testConvertToPdfTest() {
		// TODO make this a real test
		FileManagerResponse response = fileManagerImpl.convertToPdf(null);
		assertNull(response);
	}

	@Test
	public void testStampPdfTest() {
		// TODO make this a real test
		FileManagerResponse response = fileManagerImpl.stampPdf(null, null);
		assertNull(response);
	}

}
