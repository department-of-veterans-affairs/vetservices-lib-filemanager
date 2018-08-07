package gov.va.vetservices.lib.filemanager.pdf.stamp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

import com.itextpdf.io.font.constants.StandardFonts;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.exception.PdfStamperException;
import gov.va.vetservices.lib.filemanager.impl.dto.DocMetadataDto;
import gov.va.vetservices.lib.filemanager.pdf.stamp.dto.StampDataDto;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

public class StamperTest extends AbstractFileHandler {

	private final String claimId = "1111";
	private final String pdfPath = "files/application/pdf/IS_text-doc.pdf";

	@Test
	public final void testStamp() throws IOException {
		final byte[] bytes = super.readFile(Paths.get(pdfPath));
		final Stamper stamper = new Stamper();
		final DocMetadataDto metadata = new DocMetadataDto();
		metadata.setClaimId(claimId);
		metadata.setDocTypeId("123");
		metadata.setProcessType(ProcessType.CLAIMS_526);
		final StampDataDto stampDataDto = new StampDataDto();
		stampDataDto.setFontName(StandardFonts.HELVETICA);
		stampDataDto.setFontSizeInPoints(12);
		stampDataDto.setProcessType(ProcessType.CLAIMS_526);
		final FileDto fileDto = new FileDto();
		fileDto.setFilename(Paths.get(pdfPath).getFileName().toString());
		fileDto.setFilebytes(bytes);

		try {
			final byte[] stamped = stamper.stamp(metadata, stampDataDto, fileDto);
			assertNotNull(stamped);

		} catch (final PdfStamperException e) {
			e.printStackTrace();
			fail(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}

	@Test
	public final void testStamp_Bad() {
		final byte[] bytes = new byte[] { 0 };
		final DocMetadataDto metadata = new DocMetadataDto();
		metadata.setClaimId(claimId);
		metadata.setDocTypeId("123");
		metadata.setProcessType(ProcessType.CLAIMS_526);
		final Stamper stamper = new Stamper();
		final StampDataDto stampDataDto = new StampDataDto();
		stampDataDto.setFontName(StandardFonts.HELVETICA);
		stampDataDto.setFontSizeInPoints(12);
		stampDataDto.setProcessType(ProcessType.CLAIMS_526);
		final FileDto fileDto = new FileDto();
		fileDto.setFilename("bad-bytes.pdf");
		fileDto.setFilebytes(bytes);

		try {
			final byte[] stamped = stamper.stamp(metadata, stampDataDto, fileDto);
			fail("Exception should have been thrown, stamped is " + stamped);

		} catch (final Throwable e) {
			assertNotNull(e);
			assertTrue(e.getClass().isAssignableFrom(PdfStamperException.class));
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getClass().isAssignableFrom(IOException.class));
			assertNotNull(e.getCause().getCause());
			assertTrue(e.getCause().getCause().getClass().isAssignableFrom(com.itextpdf.io.IOException.class));
		}

		fileDto.setFilename(Paths.get(pdfPath).getFileName().toString());
		fileDto.setFilebytes(bytes);

		try {
			stamper.stamp(null, stampDataDto, fileDto);
			fail("Exception should have been thrown");
		} catch (final Throwable e) {
			assertNotNull(e);
			assertTrue(IllegalArgumentException.class.isAssignableFrom(e.getClass()));
		}

		try {
			stamper.stamp(metadata, null, fileDto);
			fail("Exception should have been thrown");
		} catch (final Throwable e) {
			assertNotNull(e);
			assertTrue(IllegalArgumentException.class.isAssignableFrom(e.getClass()));
		}

		try {
			stamper.stamp(metadata, stampDataDto, null);
			fail("Exception should have been thrown");
		} catch (final Throwable e) {
			assertNotNull(e);
			assertTrue(IllegalArgumentException.class.isAssignableFrom(e.getClass()));
		}
	}
}
