package gov.va.vetservices.lib.filemanager.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.FileManager;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.SimpleRequestValidator;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

/**
 * Implementation of the {@link FileManager} capabilities
 *
 * @author aburkholder
 */
/*
 * NOSONAR TODO Dev notes:
 * In WSS there is a disconnect between PDFServiceImpl and PdfGenerator.
 * One objective of this class is to combine the best of both for consistent results.
 */
public class FileManagerImpl implements FileManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileManagerImpl.class);

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.vetservices.lib.filemanager.api.FileManager#validateFileForPDFConversion(gov.va.vetservices.lib.filemanager.api.
	 * FileManager.FileDto)
	 */
	@Override
	public FileManagerResponse validateFileForPDFConversion(FileManagerRequest request) throws FileManagerException {
		InterrogateFile interrogateFile = new InterrogateFile();
		FileManagerResponse response = new FileManagerResponse();
		response.setDoNotCacheResponse(true);

		try {

			simpleInputValidation(request, response);

			if (response.getMessages().isEmpty()) {
				ImplDto implDto = FileManagerUtils.makeImplDto(request);

				// determine if the file can be converted to PDF
				response = interrogateFile.canConvertToPdf(implDto);
			}

		} catch (Throwable e) { // NOSONAR - catch everything here
			if (!FileManagerException.class.isAssignableFrom(e.getClass())) {
				MessageKeysEnum key = MessageKeysEnum.UNEXPECTED_ERROR;
				LOGGER.error("Unexpected " + e.getClass().getSimpleName()
						+ " exception in vetservices-lib-filemanager. Please solve thsi issue at its source.", e);
				throw new FileManagerException(MessageSeverity.FATAL, key.getKey(), key.getMessage());
			}
		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.vetservices.lib.filemanager.api.FileManager#convertToPdf(byte[])
	 *
	 * NOSONAR TODO Dev notes:
	 * - Method is sourced from wss PDFServiceImpl.convertPDF(..) & PDFGenerate.generateBody()
	 */
	@Override
	public FileManagerResponse convertToPdf(FileManagerRequest request) throws FileManagerException {
		ConvertFile convertFile = new ConvertFile();
		StampFile stampFile = new StampFile();
		FileManagerResponse response = new FileManagerResponse();
		response.setDoNotCacheResponse(true);

		try {

			simpleInputValidation(request, response);

			if (response.getMessages().isEmpty()) {
				ImplDto implDto = FileManagerUtils.makeImplDto(request);

				// convert the file to PDF
				response = convertFile.convertToPdf(implDto);

				if (!response.hasErrors()) {
					// stamp the PDF, if required
					stampFile.stampPdf(implDto, response);
				}
			}

		} catch (Throwable e) { // NOSONAR - catch everything here
			if (!FileManagerException.class.isAssignableFrom(e.getClass())) {
				MessageKeysEnum key = MessageKeysEnum.UNEXPECTED_ERROR;
				LOGGER.error("Unexpected " + e.getClass().getSimpleName()
						+ " exception in vetservices-lib-filemanager. Please solve thsi issue at its source.", e);
				throw new FileManagerException(MessageSeverity.FATAL, key.getKey(), key.getMessage());
			}
		}

		return response;
	}

	/**
	 * Add error messages onto the response if any required objects are not provided.
	 *
	 * @param request the FileManagerRequest
	 * @param response the File Manager Response
	 */
	private void simpleInputValidation(FileManagerRequest request, FileManagerResponse response) {
		ImplArgDto<FileManagerRequest> arg = new ImplArgDto<FileManagerRequest>(request);
		List<Message> messages = (new SimpleRequestValidator()).validate(arg);

		if ((messages != null) && !messages.isEmpty()) {
			response.addMessages(messages);
		}
	}
}
