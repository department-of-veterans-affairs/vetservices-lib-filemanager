package gov.va.vetservices.lib.filemanager.impl;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.pdf.Converter;

/**
 * Attempt to convert a provided file byte array to PDF.
 *
 * @author aburkholder
 */
public class ConvertFile {

	Converter converter = new Converter();

	/**
	 * Attempt to convert a file byte array to PDF.
	 * <p>
	 * It is assumed that the data has already been validated. Unvalidated data may result in runtime exceptions.
	 * Checked exceptions are returned in the response messages.
	 *
	 * @param vdto the transfer object
	 * @return FileManagerResponse the response
	 */
	public FileManagerResponse doConversion(ValidatorDto vdto) {
		FileManagerResponse response = new FileManagerResponse();

		byte[] pdfBytes = null;
		try {
			pdfBytes = converter.convert(vdto.getFileDto().getFilebytes(), vdto.getFileParts());

			FileDto fdto = new FileDto();
			fdto.setFilebytes(pdfBytes);
			fdto.setFilename(vdto.getFileParts().getName() + ".pdf");

			response.setFileDto(fdto);

		} catch (FileManagerException e) { // NOSONAR - error is reported, shut up sonar
			response.addMessage(e.getMessageSeverity(), e.getKey(), e.getMessage());
		}

		return response;
	}
}
