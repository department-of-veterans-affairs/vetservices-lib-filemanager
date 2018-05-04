package gov.va.vetservices.lib.filemanager.pdf.font;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.lowagie.text.Font;

public class PdfFontFactoryTest {

	@Test
	public final void testPdfFontFactory() {
		try {
			new PdfFontFactory();
		} catch (Throwable e) {
			assertNotNull(e);
			assertTrue(e.getClass().isAssignableFrom(IllegalAccessError.class));
		}
	}

	@Test
	public final void testGetFont() {
		Font font = PdfFontFactory.getFont(FontNameEnum.COURIER);
		assertNotNull(font);
		assertNotNull(font.getFamily());
	}

}
