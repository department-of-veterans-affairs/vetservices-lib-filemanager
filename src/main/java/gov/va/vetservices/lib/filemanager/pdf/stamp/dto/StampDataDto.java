package gov.va.vetservices.lib.filemanager.pdf.stamp.dto;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.pdf.font.FontNameEnum;

/**
 * Data that will be used to print onto the header area of the PDF.
 *
 * @author aburkholder
 */
public class StampDataDto {

	private FontNameEnum fontNameEnum = FontNameEnum.COURIER;
	private int fontSizeInPoints = 10;
	private ProcessType processType;

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
	public void setProcessType(ProcessType processType) {
		this.processType = processType;
	}

}
