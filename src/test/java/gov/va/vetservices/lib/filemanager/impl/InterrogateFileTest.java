package gov.va.vetservices.lib.filemanager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.ascent.framework.util.Defense;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

@RunWith(SpringRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
	AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class InterrogateFileTest {

	private static final byte[] STRING_BYTES = "This is a test.".getBytes();
	private static final String STRING_FILENAME = "test.txt";
	private static final String STRING_FILENAME_NO_NAME = ".txt";
	private static final String STRING_FILENAME_NO_EXT = "test.";
	private static final String STRING_FILENAME_MALFORMED = "test..txt";
	private static final String STRING_FILENAME_UNSUPPORTED = "test.chat";

	private static final String claimId = "11111";
	private static final String docTypeId = "123";

	@Autowired
	private InterrogateFile interrogateFile;

	FileDto filedto;
	ImplDto implDto;
	FileManagerResponse response;

	@Before
	public void setUp() {
		Defense.notNull(interrogateFile);
	}

	@Test
	public final void testCanConvertToPdf() {

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

		response = interrogateFile.canConvertToPdf(implDto);
		assertNotNull(response);
		// NOSONAR TODO line below will have to change when ConversionValidator is completed
		assertTrue(!response.getMessages().isEmpty() && response.getMessages().get(0).getKey().equals("UNFINISHED.WORK"));
		assertNull(response.getFileDto());

		// sad

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(null);
		implDto = FileManagerUtils.makeImplDto(request);
		response = interrogateFile.canConvertToPdf(implDto);
		assertNotNull(response);
		assertTrue(!response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(null);
		filedto.setFilename(STRING_FILENAME);
		implDto = FileManagerUtils.makeImplDto(request);
		response = interrogateFile.canConvertToPdf(implDto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? false : !response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_NO_NAME);
		implDto = FileManagerUtils.makeImplDto(request);
		response = interrogateFile.canConvertToPdf(implDto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? false : !response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_NO_EXT);
		implDto = FileManagerUtils.makeImplDto(request);
		response = interrogateFile.canConvertToPdf(implDto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? false : !response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_MALFORMED);
		implDto = FileManagerUtils.makeImplDto(request);
		response = interrogateFile.canConvertToPdf(implDto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? false : !response.getMessages().isEmpty());
		assertNull(response.getFileDto());

		filedto.setFilebytes(STRING_BYTES);
		filedto.setFilename(STRING_FILENAME_UNSUPPORTED);
		implDto = FileManagerUtils.makeImplDto(request);
		response = interrogateFile.canConvertToPdf(implDto);
		assertNotNull(response);
		assertTrue(response.getMessages() == null ? false : !response.getMessages().isEmpty());
		assertNull(response.getFileDto());
	}

}
