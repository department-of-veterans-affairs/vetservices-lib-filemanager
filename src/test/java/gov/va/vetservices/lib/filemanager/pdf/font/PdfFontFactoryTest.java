package gov.va.vetservices.lib.filemanager.pdf.font;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.lowagie.text.Font;

import gov.va.vetservices.lib.filemanager.api.stamper.FontName;

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
		Font font = PdfFontFactory.getFont(FontName.COURIER);
		assertNotNull(font);
		assertNotNull(font.getFamily());
	}

}
