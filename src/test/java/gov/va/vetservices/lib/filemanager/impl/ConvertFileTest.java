package gov.va.vetservices.lib.filemanager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
		AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(inheritListeners = false, listeners = { DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class ConvertFileTest {
	private static final byte[] STRING_BYTES = "This is a test.".getBytes();
	private static final String STRING_FILENAME = "test.txt";
	private static final String STRING_FILENAME_NO_NAME = ".txt";
	private static final String STRING_FILENAME_NO_EXT = "test.";
	private static final String STRING_FILENAME_MALFORMED = "test..txt";
	private static final String STRING_FILENAME_UNSUPPORTED = "test.chat";

	private static final byte[] FAKE_BYTES = { 32, 32, 32, 32, 32, 32, 32 };

	private static final String claimId = "11111";
	private static final String docTypeId = "123";

	@Autowired
	ConvertFile convertFile;

	FileDto filedto;
	ImplDto implDto;
	FileManagerResponse response;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		assertNotNull(convertFile);
	}

	@Test
	public final void testDoConversion() throws FileManagerException {

		// happy

		FileManagerRequest request = new FileManagerRequest();
		request.setClaimId(claimId);
		request.setDocTypeId(docTypeId);
		request.setProcessType(ProcessType.CLAIMS_526);
		filedto = new FileDto();
		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME);
		request.setFileDto(filedto);
		implDto = FileManagerUtils.makeImplDto(request);
		assertNotNull(implDto);

		convertFile.convertToPdf(implDto);
		assertNotNull(implDto);
		assertTrue(implDto.getMessages().isEmpty());
		assertNotNull(implDto.getPdfFileDto());

		// sad

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(null);
		implDto = FileManagerUtils.makeImplDto(request);
		convertFile.convertToPdf(implDto);
		assertNotNull(implDto);
		assertTrue(!implDto.getMessages().isEmpty());
		assertNull(implDto.getPdfFileDto());

		filedto.setFilebytes(null);
		filedto.setFilename(STRING_FILENAME);
		implDto = FileManagerUtils.makeImplDto(request);
		convertFile.convertToPdf(implDto);
		assertNotNull(implDto);
		assertTrue(!implDto.getMessages().isEmpty());
		assertNull(implDto.getPdfFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_NO_NAME);
		implDto = FileManagerUtils.makeImplDto(request);
		convertFile.convertToPdf(implDto);
		assertNotNull(implDto);
		assertTrue(implDto.getMessages().isEmpty());
		assertNotNull(implDto.getPdfFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_NO_EXT);
		implDto = FileManagerUtils.makeImplDto(request);
		convertFile.convertToPdf(implDto);
		assertNotNull(implDto);
		assertTrue(!implDto.getMessages().isEmpty());
		assertNull(implDto.getPdfFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_MALFORMED);
		implDto = FileManagerUtils.makeImplDto(request);
		convertFile.convertToPdf(implDto);
		assertNotNull(implDto);
		assertTrue(implDto.getMessages().isEmpty());
		assertNotNull(implDto.getPdfFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_UNSUPPORTED);
		implDto = FileManagerUtils.makeImplDto(request);
		convertFile.convertToPdf(implDto);
		assertNotNull(implDto);
		assertTrue(!implDto.getMessages().isEmpty());
		assertNull(implDto.getPdfFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_UNSUPPORTED);
		implDto = FileManagerUtils.makeImplDto(request);
		implDto.setOriginalFileDto(null);
		convertFile.convertToPdf(implDto);
		assertTrue(!implDto.getMessages().isEmpty());
		assertTrue(LibFileManagerMessageKeys.FILE_DTO_NULL.equals(implDto.getMessages().get(0).getKey()));

		try {
			convertFile.convertToPdf(null);
			fail("convertToPdf should have thrown an exception");
		} catch (Exception e) {
			assertTrue(FileManagerException.class.isAssignableFrom(e.getClass()));
			FileManagerException fme = (FileManagerException) e;
			assertTrue(LibFileManagerMessageKeys.FILEMANAGER_ISSUE.equals(fme.getKey()));
		}
	}

}
