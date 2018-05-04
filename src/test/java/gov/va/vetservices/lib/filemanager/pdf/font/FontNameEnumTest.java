package gov.va.vetservices.lib.filemanager.pdf.font;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.va.vetservices.lib.filemanager.pdf.font.FontNameEnum;

public class FontNameEnumTest {

	private static final String COURIER = "COURIER";
	private static final String HELVETICA = "HELVETICA";
	private static final String TIMES = "TIMES";
	private static final String UNKNOWN = "UNKNOWN";

	@Test
	public final void test() {
		assertTrue(FontNameEnum.COURIER.toString().equals(COURIER));
		assertTrue(FontNameEnum.HELVETICA.toString().equals(HELVETICA));
		assertTrue(FontNameEnum.TIMES.toString().equals(TIMES));
		assertTrue(FontNameEnum.UNKNOWN.toString().equals(UNKNOWN));
	}

}
