package gov.va.vetservices.lib.filemanager.pdf.stamp.dto;

import gov.va.vetservices.lib.filemanager.pdf.font.FontNameEnum;

/**
 * Data that will be used to print onto the header area of the PDF.
 *
 * @author aburkholder
 */
public class StampDataDto {

	private FontNameEnum fontNameEnum = FontNameEnum.COURIER;
	private int fontSizeInPoints = 10;
	private String stampText;

	/**
	 * Returns the {@link FontNameEnum} enumeration.
	 *
	 * @return FontNameEnum the font name
	 */
	public FontNameEnum getFontName() {
		return fontNameEnum;
	}

	/**
	 * Set the {@link FontNameEnum} enumeration. Default value is {@code FontNameEnum.COURIER}.
	 *
	 * @param fontNameEnum the font name to set
	 */
	public void setFontName(FontNameEnum fontNameEnum) {
		this.fontNameEnum = fontNameEnum;
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
