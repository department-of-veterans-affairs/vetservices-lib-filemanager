package gov.va.vetservices.lib.filemanager.pdf.stamp.dto;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.va.vetservices.lib.filemanager.pdf.font.FontNameEnum;
import gov.va.vetservices.lib.filemanager.pdf.stamp.StampsEnum;

public class StampDataDtoTest {

	private static final String COURIER = "COURIER";

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
	public final void testGetStampsEnum() {
		stampDataDto.setStampsEnum(StampsEnum.CLAIM);
		assertNotNull(stampDataDto.getStampsEnum());
		assertTrue(StampsEnum.CLAIM.equals(stampDataDto.getStampsEnum()));
	}

}
