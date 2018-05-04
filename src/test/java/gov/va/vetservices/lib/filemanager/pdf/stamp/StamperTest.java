package gov.va.vetservices.lib.filemanager.pdf.stamp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Test;

import gov.va.vetservices.lib.filemanager.exception.PdfStamperException;
import gov.va.vetservices.lib.filemanager.pdf.font.FontNameEnum;
import gov.va.vetservices.lib.filemanager.pdf.stamp.dto.StampDataDto;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

public class StamperTest extends AbstractFileHandler {

	private static final String STAMP_TEXT = "This is some test stamp text.";

	private final String pdfPath = "files/application/pdf/IS_text-doc.pdf";

	@Test
	public final void testStamp() throws IOException {
		byte[] bytes = super.readFile(Paths.get(pdfPath));
		Stamper stamper = new Stamper();
		StampDataDto stampDataDto = new StampDataDto();
		stampDataDto.setFontName(FontNameEnum.HELVETICA);
		stampDataDto.setFontSizeInPoints(12);
		stampDataDto.setStampText(STAMP_TEXT);

		try {
			byte[] stamped = stamper.stamp(stampDataDto, bytes);
			assertNotNull(stamped);

		} catch (PdfStamperException e) {
			e.printStackTrace();
			fail(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}

	@Test
	public final void testStamp_Bad() {
		byte[] bytes = new byte[] { 0 };
		Stamper stamper = new Stamper();
		StampDataDto stampDataDto = new StampDataDto();
		stampDataDto.setFontName(FontNameEnum.HELVETICA);
		stampDataDto.setFontSizeInPoints(12);
		stampDataDto.setStampText(STAMP_TEXT);

		try {
			byte[] stamped = stamper.stamp(stampDataDto, bytes);
			fail("Exception should have been thrown, stampped is " + stamped);

		} catch (Throwable e) {
			assertNotNull(e);
			assertTrue(e.getClass().isAssignableFrom(PdfStamperException.class));
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getClass().isAssignableFrom(IOException.class));
		}
	}
}
