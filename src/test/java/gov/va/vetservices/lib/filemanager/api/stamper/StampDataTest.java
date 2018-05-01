package gov.va.vetservices.lib.filemanager.api.stamper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StampDataTest {

	private static final String COURIER = "COURIER";
	private static final String STAMP_TEXT = "This is some test stamp text.";

	StampData stampData = new StampData();

	@Test
	public final void testFontName() {
		stampData.setFontName(FontName.COURIER);
		assertNotNull(stampData.getFontName());
		assertTrue(COURIER.equals(stampData.getFontName().toString()));
	}

	@Test
	public final void testFontSizeInPoints() {
		int points = 12;
		stampData.setFontSizeInPoints(points);
		assertTrue(stampData.getFontSizeInPoints() == points);
	}

	@Test
	public final void testGetStampText() {
		stampData.setStampText(STAMP_TEXT);
		assertNotNull(stampData.getStampText());
		assertTrue(STAMP_TEXT.equals(stampData.getStampText()));
	}

}
