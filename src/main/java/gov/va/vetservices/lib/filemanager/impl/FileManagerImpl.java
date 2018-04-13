package gov.va.vetservices.lib.filemanager.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.ascent.framework.util.Defense;
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
@Component(FileManagerImpl.BEAN_NAME)
public class FileManagerImpl implements FileManager {

	protected static final String BEAN_NAME = "fileManager";

	@Autowired
	InterrogateFile interrogateFile;

	@PostConstruct
	public final void postConstruct() {
		Defense.notNull(interrogateFile, "interrogateFile cannot be null");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.vetservices.lib.filemanager.api.FileManager#validateFileForPDFConversion(gov.va.vetservices.lib.filemanager.api.
	 * FileManager.FileDto)
	 *
	 * TODO Dev notes:
	 * - Method is sourced from wss ImageServiceImpl.validateFile(..) & gov.va.wss.document.services.service.itext.validate.*Validator
	 * - FileDto is just name change from ImageDto.
	 * - As any good validation should do, boolean is now returned.
	 */
	@Override
	public FileManagerResponse validateFileForPDFConversion(FileDto fileDto) {
		FileManagerResponse response = new FileManagerResponse();

		if (validateInput(response, fileDto)) {
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
	public byte[] convertToPdf(byte[] file) {
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
	public byte[] stampPdf(String stampContent, byte[] file) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * <p>
	 * Do null check. If validation fails, {@code false} is returned, and an ERROR message is returned on the response parameter.
	 * <p>
	 * FileDto members get validated later, so it is enough to just do null check here.
	 *
	 * @param response returned with errors if validation failes
	 * @param fileDto the DTO to check
	 */
	private boolean validateInput(FileManagerResponse response, FileDto fileDto) {
		boolean isValid = true;
		if (fileDto == null) {
			isValid = false;
			response.addMessage(MessageSeverity.ERROR, MessageKeys.FILE_DTO_NULL, MessageKeys.getMessage(MessageKeys.FILE_DTO_NULL));
		}
		return isValid;
	}
}
