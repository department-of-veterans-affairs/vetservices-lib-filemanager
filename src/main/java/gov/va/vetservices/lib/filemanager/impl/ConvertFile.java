package gov.va.vetservices.lib.filemanager.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.pdf.PdfConverter;
import gov.va.vetservices.lib.filemanager.util.MessageUtils;

/**
 * Attempt to convert a provided file byte array to another format. Current implementation assumes conversion to PDF.
 *
 * @author aburkholder
 */
@Component(ConvertFile.BEAN_NAME)
public class ConvertFile {
	/*
	 * Design notes:
	 * To accommodate conversions to formats other than PDF,
	 * add a method in this class to call an appropriate
	 * converter (presumably in a new package).
	 */

	public static final String BEAN_NAME = "convertFile";

	@Autowired
	@Qualifier(MessageUtils.BEAN_NAME)
	MessageUtils messageUtils;

	@Autowired
	PdfConverter pdfConverter;

	/**
	 * Convert a file from one type to another. Current implementation assumes conversion to PDF.<br/>
	 * <b>The file to be converted must be on implDto.getOriginalFileDto()</b>.
	 * The converted file is returned on implDto.getPdfFileDto().
	 * <p>
	 * It is assumed that the data has already been validated. Unvalidated data may result in runtime exceptions.
	 * Checked exceptions are returned in the messages on the implDto parameter.
	 *
	 * @param implDto the transfer object
	 * @return FileManagerResponse the response
	 * @throws FileManagerException
	 */
	public void convertToPdf(ImplDto implDto) throws FileManagerException {

		byte[] pdfBytes = null;

		if (implDto == null) {
			throw new FileManagerException(MessageSeverity.ERROR, LibFileManagerMessageKeys.FILEMANAGER_ISSUE,
					messageUtils.returnMessage(LibFileManagerMessageKeys.FILEMANAGER_ISSUE));
		}

		try {
			if (implDto.getOriginalFileDto() == null) {
				throw new FileManagerException(MessageSeverity.ERROR, LibFileManagerMessageKeys.FILE_DTO_NULL,
						messageUtils.returnMessage(LibFileManagerMessageKeys.FILE_DTO_NULL));
			}

			pdfBytes = pdfConverter.convert(implDto.getOriginalFileDto().getFilebytes(), implDto.getFileParts());

			FileDto fdto = new FileDto();
			fdto.setFilebytes(pdfBytes);
			fdto.setFilename(implDto.getFileParts().getName() + ".pdf");

			implDto.setPdfFileDto(fdto);

		} catch (FileManagerException e) { // NOSONAR - error is reported, shut up sonar
			implDto.addMessage(new Message(e.getMessageSeverity(), e.getKey(), e.getMessage()));
		}
	}
}
