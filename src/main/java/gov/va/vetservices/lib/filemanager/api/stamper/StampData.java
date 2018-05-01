package gov.va.vetservices.lib.filemanager.api.stamper;

/**
 * Data that will be used to print onto the header area of the PDF.
 *
 * @author aburkholder
 */
public class StampData {

	private FontName fontName = FontName.COURIER;
	private int fontSizeInPoints = 10;
	private String stampText;

	/**
	 * Returns the {@link FontName} enumeration.
	 *
	 * @return FontName the font name
	 */
	public FontName getFontName() {
		return fontName;
	}

	/**
	 * Set the {@link FontName} enumeration. Default value is {@code FontName.COURIER}.
	 *
	 * @param fontName the font name to set
	 */
	public void setFontName(FontName fontName) {
		this.fontName = fontName;
	}

	/**
	 * Returns the font size in points.
	 *
	 * @return int the font size in points
	 */
	public int getFontSizeInPoints() {
		return fontSizeInPoints;
	}

	/**
	 * Sets the font size. All font sizes are expressed in points. The default value is {@code 10}.
	 *
	 * @param fontSizeInPoints the font size in points
	 */
	public void setFontSizeInPoints(int fontSizeInPoints) {
		this.fontSizeInPoints = fontSizeInPoints;
	}

	/**
	 * Returns the text that will be printed onto the PDF header area.
	 *
	 * @return the stampText
	 */
	public String getStampText() {
		return stampText;
	}

	/**
	 * Sets the text that will be printed onto the PDF header area.
	 *
	 * @param stampText the stamp text to set
	 */
	public void setStampText(String stampText) {
		this.stampText = stampText;
	}

}
