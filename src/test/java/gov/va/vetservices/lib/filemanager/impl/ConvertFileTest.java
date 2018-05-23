package gov.va.vetservices.lib.filemanager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class ConvertFileTest {
	private static final byte[] STRING_BYTES = "This is a test.".getBytes();
	private static final String STRING_FILENAME = "test.txt";
	private static final String STRING_FILENAME_NO_NAME = ".txt";
	private static final String STRING_FILENAME_NO_EXT = "test.";
	private static final String STRING_FILENAME_MALFORMED = "test..txt";
	private static final String STRING_FILENAME_UNSUPPORTED = "test.chat";

	private static final String claimId = "11111";
	private static final String docTypeId = "123";

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
	}

}
