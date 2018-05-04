package gov.va.vetservices.lib.filemanager.pdf.font;

import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;

/**
 * Get the iText {@link Font} for the Stamper's {@link FontNameEnum}.
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
	 * Gets the iText {@link Font} object from the iText Font Factory for the enumerated {@link FontNameEnum}.
	 *
	 * @param fontNameEnum the FontNameEnum
	 * @return Font the equivalent iText font object
	 */
	public static Font getFont(final FontNameEnum fontNameEnum) {
		return FontFactory.getFont(fontNameEnum.toString());
	}
}
