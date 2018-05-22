package gov.va.vetservices.lib.filemanager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
//import gov.va.vetservices.lib.filemanager.api.v1.transfer.DocTypeId;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;

@RunWith(SpringRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
		AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class FileManagerImplTest {

	private final static byte[] STRING_BYTES = "This is a test.".getBytes();
	private final static String STRING_FILENAME = "test.txt";

	private static final String claimId = "11111";
	private static final String docTypeId = "123";

	@Autowired
	@Qualifier(FileManagerImpl.BEAN_NAME)
	private FileManagerImpl fileManagerImpl;

	private FileManagerResponse response;

	@Before
	public void setUp() {
		assertNotNull(fileManagerImpl);
	}

	@Test
	public void testValidateFileForPDFConversion() {
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
		assertTrue(response.getMessages().isEmpty());
		assertNull(response.getFileDto());
		assertNull(response.getSafeDatestampedFilename());
		assertTrue(response.isDoNotCacheResponse());
		// NOSONAR TODO changes when ConversionValidator is completed

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
		assertTrue(response.getMessages().isEmpty());

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
	public void testConvertToPdf() {
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
