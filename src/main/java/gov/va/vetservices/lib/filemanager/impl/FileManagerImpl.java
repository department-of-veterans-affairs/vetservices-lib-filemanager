package gov.va.vetservices.lib.filemanager.impl;

import org.apache.commons.lang3.StringUtils;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.FileManager;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

/**
 * Implementation of the {@link FileManager} capabilities
 *
 * @author aburkholder
 */
/*
 * TODO Dev notes:
 * In WSS there is a disconnect between PDFServiceImpl and PdfGenerator.
 * One objective of this class is to combine the best of both for consistent results.
 */
public class FileManagerImpl implements FileManager {

	InterrogateFile interrogateFile = new InterrogateFile();

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.vetservices.lib.filemanager.api.FileManager#validateFileForPDFConversion(gov.va.vetservices.lib.filemanager.api.
	 * FileManager.FileDto)
	 */
	@Override
	public FileManagerResponse validateFileForPDFConversion(FileDto fileDto) {
		FileManagerResponse response = new FileManagerResponse();

		if (fileDto == null) {
			response.addMessage(MessageSeverity.ERROR, MessageKeys.FILE_DTO_NULL.getKey(), MessageKeys.FILE_DTO_NULL.getMessage());
		} else if (StringUtils.isBlank(fileDto.getFilename())) {
			response.addMessage(MessageSeverity.ERROR, MessageKeys.FILE_NAME_NULL_OR_EMPTY.getKey(),
					MessageKeys.FILE_NAME_NULL_OR_EMPTY.getMessage());
		} else if ((fileDto.getFilebytes() == null) || (fileDto.getFilebytes().length < 1)) {
			response.addMessage(MessageSeverity.ERROR, MessageKeys.FILE_BYTES_NULL_OR_EMPTY.getKey(),
					MessageKeys.FILE_BYTES_NULL_OR_EMPTY.getMessage());
		} else {
			ValidatorDto validatorDto = FileManagerUtils.makeValidatorDto(fileDto);
			response = interrogateFile.canConvertToPdf(validatorDto);
		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.vetservices.lib.filemanager.api.FileManager#convertToPdf(byte[])
	 *
	 * TODO Dev notes:
	 * - Method is sourced from wss PDFServiceImpl.convertPDF(..) & PDFGenerate.generateBody()
	 */
	@Override
	public FileManagerResponse convertToPdf(FileDto fileDto) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.vetservices.lib.filemanager.api.FileManager#stampPdf(java.lang.String, byte[])
	 *
	 * TODO Dev notes:
	 * - Method is sourced from wss PDFServiceImpl.stampPDF(..) & PDFGenerate.generateBody()
	 * - It may be necesary to provide more information to this method
	 * - ... could replace String param with a model (content, font size, etc as needed)
	 */
	@Override
	public FileManagerResponse stampPdf(String stampContent, FileDto fileDto) {
		// TODO Auto-generated method stub
		return null;
	}

}
