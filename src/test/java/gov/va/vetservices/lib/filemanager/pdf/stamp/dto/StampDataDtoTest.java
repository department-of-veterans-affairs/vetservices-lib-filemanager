package gov.va.vetservices.lib.filemanager.pdf.stamp.dto;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.itextpdf.io.font.constants.StandardFonts;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;

public class StampDataDtoTest {

	private static final String HELVETICA = StandardFonts.HELVETICA;

	StampDataDto stampDataDto = new StampDataDto();

	@Test
	public final void testFontName() {
		stampDataDto.setFontName(StandardFonts.HELVETICA);
		assertNotNull(stampDataDto.getFontName());
		assertTrue(HELVETICA.equals(stampDataDto.getFontName().toString()));

		stampDataDto.setFontName("Not a font name");
		assertNotNull(stampDataDto.getFontName());
		assertTrue(StandardFonts.COURIER.equals(stampDataDto.getFontName().toString()));
	}

	@Test
	public final void testFontSizeInPoints() {
		final int points = 12;
		stampDataDto.setFontSizeInPoints(points);
		assertTrue(stampDataDto.getFontSizeInPoints() == points);
	}

	@Test
	public final void testGetProcessType() {
		stampDataDto.setProcessType(ProcessType.CLAIMS_526);
		assertNotNull(stampDataDto.getProcessType());
		assertTrue(ProcessType.CLAIMS_526.equals(stampDataDto.getProcessType()));
	}

}
