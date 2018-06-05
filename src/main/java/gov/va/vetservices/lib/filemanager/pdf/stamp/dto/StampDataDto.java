package gov.va.vetservices.lib.filemanager.pdf.stamp.dto;

import com.itextpdf.io.font.constants.StandardFonts;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;

/**
 * Data that will be used to print onto the header area of the PDF.
 *
 * @author aburkholder
 */
public class StampDataDto {

	private String fontName = StandardFonts.COURIER;
	private int fontSizeInPoints = 10;
	private ProcessType processType;

	/**
	 * Returns the {@link StandardFonts} font name.
	 *
	 * @return String the font name
	 */
	public String getFontName() {
		return fontName;
	}

	/**
	 * Set the {@link StandardFonts} font name. Default value is {@code StandardFonts.COURIER}.
	 * If the font name does not appear in the StandardFonts enumeration, the default font is applied.
	 *
	 * @param fontName the font name to set
	 */
	public void setFontName(final String fontName) {
		if (StandardFonts.isStandardFont(fontName)) {
			this.fontName = fontName;
		} else {
			this.fontName = StandardFonts.COURIER;
		}
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
	public void setFontSizeInPoints(final int fontSizeInPoints) {
		this.fontSizeInPoints = fontSizeInPoints;
	}

	/**
	 * Gets the ProcessType from which the stamp text is derived. There is no default for this field.
	 *
	 * @return the processType
	 */
	public ProcessType getProcessType() {
		return processType;
	}

	/**
	 * Sets the ProcessType from which the stamp text is derived. There is no default for this field.
	 *
	 * @param processType the processType to set
	 */
	public void setProcessType(final ProcessType processType) {
		this.processType = processType;
	}

}
