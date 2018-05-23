package gov.va.vetservices.lib.filemanager.pdf.stamp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.exception.PdfStamperException;
import gov.va.vetservices.lib.filemanager.impl.dto.DocMetadataDto;
import gov.va.vetservices.lib.filemanager.pdf.font.FontNameEnum;
import gov.va.vetservices.lib.filemanager.pdf.stamp.dto.StampDataDto;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

public class StamperTest extends AbstractFileHandler {

	private final String claimId = "1111";
	private final String pdfPath = "files/application/pdf/IS_text-doc.pdf";

	@Test
	public final void testStamp() throws IOException {
		byte[] bytes = super.readFile(Paths.get(pdfPath));
		Stamper stamper = new Stamper();
		DocMetadataDto metadata = new DocMetadataDto();
		metadata.setClaimId(claimId);
		metadata.setDocTypeId("123");
		metadata.setProcessType(ProcessType.CLAIMS_526);
		StampDataDto stampDataDto = new StampDataDto();
		stampDataDto.setFontName(FontNameEnum.HELVETICA);
		stampDataDto.setFontSizeInPoints(12);
		stampDataDto.setProcessType(ProcessType.CLAIMS_526);
		FileDto fileDto = new FileDto();
		fileDto.setFilename(Paths.get(pdfPath).getFileName().toString());
		fileDto.setFilebytes(bytes);

		try {
			byte[] stamped = stamper.stamp(metadata, stampDataDto, fileDto);
			assertNotNull(stamped);

		} catch (PdfStamperException e) {
			e.printStackTrace();
			fail(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}

	@Test
	public final void testStamp_Bad() {
		byte[] bytes = new byte[] { 0 };
		DocMetadataDto metadata = new DocMetadataDto();
		metadata.setClaimId(claimId);
		metadata.setDocTypeId("123");
		metadata.setProcessType(ProcessType.CLAIMS_526);
		Stamper stamper = new Stamper();
		StampDataDto stampDataDto = new StampDataDto();
		stampDataDto.setFontName(FontNameEnum.HELVETICA);
		stampDataDto.setFontSizeInPoints(12);
		stampDataDto.setProcessType(ProcessType.CLAIMS_526);
		FileDto fileDto = new FileDto();
		fileDto.setFilename("bad-bytes.pdf");
		fileDto.setFilebytes(bytes);

		try {
			byte[] stamped = stamper.stamp(metadata, stampDataDto, fileDto);
			fail("Exception should have been thrown, stampped is " + stamped);

		} catch (Throwable e) {
			assertNotNull(e);
			assertTrue(e.getClass().isAssignableFrom(PdfStamperException.class));
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getClass().isAssignableFrom(IOException.class));
		}
	}
}
