package gov.va.vetservices.lib.filemanager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.impl.dto.DocMetadataDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

public class StampFileTest extends AbstractFileHandler {

	private static final String PATH_PDF = "files/application/pdf/IS_text-doc.pdf";
	private static final String claimId = "11111";
	private static final String docTypeId = "123";

	StampFile stampFile;

	@Before
	public void setUp() throws Exception {
		stampFile = new StampFile();
		assertNotNull(stampFile);
	}

	@Test
	public final void testDoStamp() {
		FileDto pdfFileDto = new FileDto();
		try {
			pdfFileDto.setFilebytes(super.readFile(Paths.get(PATH_PDF)));
		} catch (IOException e1) {
			e1.printStackTrace();
			fail("Could not read file: " + PATH_PDF);
		}
		pdfFileDto.setFilename(Paths.get(PATH_PDF).getFileName().toString());
		DocMetadataDto docMetadata = new DocMetadataDto();
		docMetadata.setClaimId(claimId);
		docMetadata.setDocTypeId(docTypeId);
		docMetadata.setProcessType(ProcessType.CLAIMS_526);
		ImplDto implDto = new ImplDto();
		implDto.setDocMetadataDto(docMetadata);
		implDto.setPdfFileDto(pdfFileDto);
		implDto.setProcessType(ProcessType.CLAIMS_526);

		try {
			stampFile.stampPdf(implDto);
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
		assertNotNull(implDto);
		assertNotNull(implDto.getPdfFileDto());
		assertNotNull(implDto.getPdfFileDto().getFilebytes());
	}

	@Test
	public final void testDoStamp_Sad() {

		try {
			stampFile.stampPdf(null);
		} catch (Throwable e) {
			assertNotNull(e);
			assertTrue(IllegalArgumentException.class.equals(e.getClass()));
		}

		FileDto pdfFileDto = new FileDto();
		try {
			pdfFileDto.setFilebytes(super.readFile(Paths.get(PATH_PDF)));
		} catch (IOException e1) {
			e1.printStackTrace();
			fail("Could not read file: " + PATH_PDF);
		}
		pdfFileDto.setFilename(Paths.get(PATH_PDF).getFileName().toString());
		DocMetadataDto docMetadata = new DocMetadataDto();
		docMetadata.setClaimId(null);
		docMetadata.setDocTypeId(docTypeId);
		docMetadata.setProcessType(ProcessType.CLAIMS_526);
		ImplDto implDto = new ImplDto();
		implDto.setDocMetadataDto(docMetadata);
		implDto.setPdfFileDto(pdfFileDto);
		implDto.setProcessType(ProcessType.CLAIMS_526);

		try {
			stampFile.stampPdf(implDto);
		} catch (Throwable e) {
			e.printStackTrace();
			fail("Unexpected exception");
		}
		assertNotNull(implDto);
		assertNotNull(implDto.getPdfFileDto());
		assertNotNull(implDto.getPdfFileDto().getFilebytes());
	}

}
