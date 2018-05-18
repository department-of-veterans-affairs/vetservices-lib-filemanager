package gov.va.vetservices.lib.filemanager.impl;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.exception.PdfStamperException;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.pdf.stamp.Stamper;
import gov.va.vetservices.lib.filemanager.pdf.stamp.dto.StampDataDto;

/**
 * Adds a stamp to the file bytes.
 * 
 * @author aburkholder
 */
public class StampFile {
	/*
	 * Design notes:
	 * To accommodate stamping files other than PDF,
	 * add a method in this class to call the appropriate
	 * stamper (presumably in a new package).
	 */

	/**
	 * Stamps the header area of a PDF file with derived text.
	 *
	 * @param implDto the metadata and file
	 * @param response any messages
	 * @throws PdfStamperException caught exceptions during stamping
	 */
	public void stampPdf(ImplDto implDto, FileManagerResponse response) throws PdfStamperException {
		if (implDto == null) {
			throw new IllegalArgumentException("ImplDto is not an optional parameter.");
		}
		if (response == null) {
			throw new IllegalArgumentException("FileManagerResponse is not an optional parameter.");
		}

		Stamper stamper = new Stamper();
		stamper.stamp(implDto.getDocMetadataDto(), new StampDataDto(), implDto.getFileDto().getFilebytes());

	}
}
