package gov.va.vetservices.lib.filemanager.pdf.font;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;

import gov.va.vetservices.lib.filemanager.api.stamper.FontName;

/**
 * Get the iText {@link Font} for the Stamper's {@link FontName}.
 *
 * @author aburkholder
 */
public class PdfFontFactory {

	/**
	 * DO NOT INSTANTIATE
	 */
	protected PdfFontFactory() {
		throw new IllegalAccessError("PdfFontFactory is a static class. Do not instantiate it.");
	}

	/**
	 * Gets the iText {@link Font} object from the iText Font Factory for the enumerated {@link FontName}.
	 *
	 * @param fontName the FontName
	 * @return Font the equivalent iText font object
	 */
	public static Font getFont(final FontName fontName) {
		return FontFactory.getFont(fontName.toString());
	}
}
