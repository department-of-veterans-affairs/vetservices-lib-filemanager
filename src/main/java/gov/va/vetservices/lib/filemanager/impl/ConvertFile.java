package gov.va.vetservices.lib.filemanager.impl;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.pdf.PdfConverter;

/**
 * Attempt to convert a provided file byte array to another format. Current implementation assumes conversion to PDF.
 *
 * @author aburkholder
 */
public class ConvertFile {
	/*
	 * Design notes:
	 * To accommodate conversions to formats other than PDF,
	 * add a method in this class to call an appropriate
	 * converter (presumably in a new package).
	 */

	PdfConverter pdfConverter = new PdfConverter();

	/**
	 * Convert a file from one type to another. Current implementation assumes conversion to PDF.
	 * <p>
	 * It is assumed that the data has already been validated. Unvalidated data may result in runtime exceptions.
	 * Checked exceptions are returned in the response messages.
	 *
	 * @param implDto the transfer object
	 * @return FileManagerResponse the response
	 */
	public FileManagerResponse convertToPdf(ImplDto implDto) {
		FileManagerResponse response = new FileManagerResponse();

		byte[] pdfBytes = null;
		try {
			pdfBytes = pdfConverter.convert(implDto.getFileDto().getFilebytes(), implDto.getFileParts());

			FileDto fdto = new FileDto();
			fdto.setFilebytes(pdfBytes);
			fdto.setFilename(implDto.getFileParts().getName() + ".pdf");

			response.setFileDto(fdto);

		} catch (FileManagerException e) { // NOSONAR - error is reported, shut up sonar
			response.addMessage(e.getMessageSeverity(), e.getKey(), e.getMessage());
		}

		return response;
	}
}
