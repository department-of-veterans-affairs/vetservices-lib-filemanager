package gov.va.vetservices.lib.filemanager.pdf.itext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.layout.hyphenation.HyphenationConfig;
import com.itextpdf.layout.property.TextAlignment;

public class PdfDocumentOptionsTest {

	@Test
	public final void testPdfDocumentOptions() {
		final PdfDocumentOptions options = null;

		assertNotNull(new PdfDocumentOptions());

		assertNotNull(new PdfDocumentOptions(
				PageSize.LETTER,
				false,
				TextAlignment.LEFT,
				new HyphenationConfig("en", "us", 3, 3),
				StandardFonts.TIMES_ROMAN,
				10,
				new StampingProperties().useAppendMode()));

	}

	@Test
	public final void testSettersGetters() {
		final PdfDocumentOptions options = new PdfDocumentOptions();

		assertTrue(PdfDocumentOptions.DEFAULT_FONT.equals(options.getFont()));
		assertTrue(PdfDocumentOptions.DEFAULT_FONT_SIZE == options.getFontSize());
		assertTrue(PdfDocumentOptions.DEFAULT_HYPHENATION.equals(options.getHyphenation()));
		assertTrue(PdfDocumentOptions.DEFAULT_PAGE_SIZE.equals(options.getPageSize()));
		assertTrue(PdfDocumentOptions.DEFAULT_STAMPING_PROPERTIES.equals(options.getStampingProperties()));
		assertTrue(PdfDocumentOptions.DEFAULT_TEXT_ALIGNMENT.equals(options.getTextAlignment()));
		assertTrue(PdfDocumentOptions.DEFAULT_ROTATED == options.isRotated());

		final String font = StandardFonts.COURIER;
		options.setFont(font);
		assertTrue(font.equals(options.getFont()));

		final int fontSize = 14;
		options.setFontSize(fontSize);
		assertTrue(fontSize == options.getFontSize());

		final HyphenationConfig hc = new HyphenationConfig("en", "uk", 4, 4);
		options.setHyphenation(hc);
		assertTrue(hc.equals(options.getHyphenation()));

		final PageSize ps = PageSize.A10;
		options.setPageSize(ps);
		assertTrue(ps.equals(options.getPageSize()));

		final StampingProperties sp = new StampingProperties().useAppendMode();
		options.setStampingProperties(sp);
		assertTrue(sp.equals(options.getStampingProperties()));

		final TextAlignment ta = TextAlignment.CENTER;
		options.setTextAlignment(ta);
		assertTrue(ta.equals(options.getTextAlignment()));

		options.setRotated(true);
		assertTrue(options.isRotated());
	}

}
