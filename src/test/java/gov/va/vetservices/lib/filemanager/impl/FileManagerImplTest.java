package gov.va.vetservices.lib.filemanager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
//import gov.va.vetservices.lib.filemanager.api.v1.transfer.DocTypeId;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;

public class FileManagerImplTest {

	private final static byte[] STRING_BYTES = "This is a test.".getBytes();
	private final static String STRING_FILENAME = "test.txt";

	private static final String claimId = "11111";
	private static final String docTypeId = "123";

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

		FileManagerRequest request = new FileManagerRequest();
		request.setClaimId(claimId);
		request.setDocTypeId(docTypeId);
		request.setProcessType(ProcessType.CLAIMS_526);
		fileDto = new FileDto();
		fileDto.setFilebytes(STRING_BYTES);
		fileDto.setFilename(STRING_FILENAME);
		request.setFileDto(fileDto);

		try {
			response = fileManagerImpl.validateFileForPDFConversion(request);
		} catch (FileManagerException e) {
			e.printStackTrace();
			if (e.getCause() != null) {
				e.getCause().printStackTrace();
			}
			fail("Unexpected " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		assertNotNull(response);
		assertNotNull(response.getMessages());
		// NOSONAR TODO line below will have to change when ConversionValidator is completed
		assertTrue(!response.getMessages().isEmpty());// && response.getMessages().get(0).getKey().equals("UNFINISHED.WORK"));
		assertNull(response.getFileDto());

		// sad

		try {
			response = fileManagerImpl.validateFileForPDFConversion(null);
		} catch (FileManagerException e) {
			e.printStackTrace();
			fail("Unexpected " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		assertNotNull(response);
		assertNotNull(response.getMessages());
		assertTrue(!response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		fileDto.setFilebytes(STRING_BYTES);
		fileDto.setFilename(STRING_FILENAME);
		try {
			response = fileManagerImpl.validateFileForPDFConversion(request);
		} catch (FileManagerException e) {
			e.printStackTrace();
			fail("Unexpected " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		assertNotNull(response);
		assertNotNull(response.getMessages());
		assertTrue(!response.getMessages().isEmpty());

		fileDto.setFilebytes(null);
		fileDto.setFilename(STRING_FILENAME);
		try {
			response = fileManagerImpl.validateFileForPDFConversion(request);
		} catch (FileManagerException e) {
			e.printStackTrace();
			fail("Unexpected " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		assertNotNull(response);
		assertNotNull(response.getMessages());
		assertTrue(!response.getMessages().isEmpty());

		fileDto.setFilebytes(STRING_BYTES);
		fileDto.setFilename(null);
		try {
			response = fileManagerImpl.validateFileForPDFConversion(request);
		} catch (FileManagerException e) {
			e.printStackTrace();
			fail("Unexpected " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		assertNotNull(response);
		assertNotNull(response.getMessages());
		assertTrue(!response.getMessages().isEmpty());
	}

	@Test
	public void testConvertToPdfTest() {
		FileDto fileDto = null;

		// Happy

		FileManagerRequest request = new FileManagerRequest();
		request.setClaimId(claimId);
		request.setDocTypeId(docTypeId);
		request.setProcessType(ProcessType.CLAIMS_526);
		fileDto = new FileDto();
		fileDto.setFilebytes(STRING_BYTES);
		fileDto.setFilename(null);
		request.setFileDto(fileDto);
		FileManagerResponse response = null;
		try {
			response = fileManagerImpl.convertToPdf(request);
		} catch (FileManagerException e) {
			e.printStackTrace();
			fail("Unexpected " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		assertNotNull(response);
		assertTrue((response.getMessages() != null) && !response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		// Sad

		try {
			response = fileManagerImpl.convertToPdf(null);
		} catch (FileManagerException e) {
			e.printStackTrace();
			fail("Unexpected " + e.getClass().getSimpleName() + ": " + e.getMessage());
		}
		assertNotNull(response);
		assertTrue((response.getMessages() != null) && !response.getMessages().isEmpty());
		assertNull(response.getFileDto());
	}

}
