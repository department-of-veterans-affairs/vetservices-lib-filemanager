package gov.va.vetservices.lib.filemanager.api.stamper;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FontNameTest {

	private static final String COURIER = "COURIER";
	private static final String HELVETICA = "HELVETICA";
	private static final String TIMES = "TIMES";
	private static final String UNKNOWN = "UNKNOWN";

	@Test
	public final void test() {
		assertTrue(FontName.COURIER.toString().equals(COURIER));
		assertTrue(FontName.HELVETICA.toString().equals(HELVETICA));
		assertTrue(FontName.TIMES.toString().equals(TIMES));
		assertTrue(FontName.UNKNOWN.toString().equals(UNKNOWN));
	}

}
