package gov.va.vetservices.lib.filemanager.impl;

import org.apache.commons.lang3.StringUtils;

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
	 * Stamps the header area of a PDF file with derived text.<br/>
	 * <b>The file to be stamped must be in implDto.getPdfFileDto()</b>.
	 * The stamped PDF will be returned on the implDto parameter in the same field - implDto.getPdfFileDto().
	 * <p>
	 * Error messages are returned on the implDto parameter.
	 *
	 * @param implDto the metadata and file
	 * @param response any messages
	 * @throws PdfStamperException caught exceptions during stamping
	 */
	public void stampPdf(ImplDto implDto) throws PdfStamperException {
		if (implDto == null) {
			throw new IllegalArgumentException("ImplDto is not an optional parameter.");
		}

		Stamper stamper = new Stamper();
		StampDataDto stampData = new StampDataDto();

		if (StringUtils.isNotBlank(implDto.getDocMetadataDto().getClaimId())) {
			stampData.setProcessType(implDto.getProcessType());

			byte[] stampedPdf = stamper.stamp(implDto.getDocMetadataDto(), stampData, implDto.getPdfFileDto());
			implDto.getPdfFileDto().setFilebytes(stampedPdf);
		}

	}
}
