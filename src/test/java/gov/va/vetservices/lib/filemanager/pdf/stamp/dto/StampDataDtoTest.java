package gov.va.vetservices.lib.filemanager.pdf.stamp.dto;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.va.vetservices.lib.filemanager.pdf.font.FontNameEnum;
import gov.va.vetservices.lib.filemanager.pdf.stamp.dto.StampDataDto;

public class StampDataDtoTest {

	private static final String COURIER = "COURIER";
	private static final String STAMP_TEXT = "This is some test stamp text.";

	StampDataDto stampDataDto = new StampDataDto();

	@Test
	public final void testFontName() {
		stampDataDto.setFontName(FontNameEnum.COURIER);
		assertNotNull(stampDataDto.getFontName());
		assertTrue(COURIER.equals(stampDataDto.getFontName().toString()));
	}

	@Test
	public final void testFontSizeInPoints() {
		int points = 12;
		stampDataDto.setFontSizeInPoints(points);
		assertTrue(stampDataDto.getFontSizeInPoints() == points);
	}

	@Test
	public final void testGetStampText() {
		stampDataDto.setStampText(STAMP_TEXT);
		assertNotNull(stampDataDto.getStampText());
		assertTrue(STAMP_TEXT.equals(stampDataDto.getStampText()));
	}

}
