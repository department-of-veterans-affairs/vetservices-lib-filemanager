package gov.va.vetservices.lib.filemanager.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.ascent.framework.util.Defense;
import gov.va.vetservices.lib.filemanager.api.FileManager;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.SimpleRequestValidator;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;
import gov.va.vetservices.lib.filemanager.util.MessageUtils;

/**
 * Implementation of the {@link FileManager} capabilities
 *
 * @author aburkholder
 */
@Component(FileManagerImpl.BEAN_NAME)
public class FileManagerImpl implements FileManager {

	private static final AscentLogger LOGGER = AscentLoggerFactory.getLogger(FileManagerImpl.class);

	public static final String BEAN_NAME = "fileManagerImpl";

	@Autowired
	@Qualifier(InterrogateFile.BEAN_NAME)
	InterrogateFile interrogateFile;

	/** Auto wire message utilities */
	@Autowired
	@Qualifier(MessageUtils.BEAN_NAME)
	private MessageUtils messageUtils;

	@Autowired
	@Qualifier(SimpleRequestValidator.BEAN_NAME)
	private SimpleRequestValidator simpleRequestValidator;

	@Autowired
	@Qualifier(StampFile.BEAN_NAME)
	private StampFile stampFile;

	@Autowired
	@Qualifier(ConvertFile.BEAN_NAME)
	private ConvertFile convertFile;

	@PostConstruct
	public void postConstruct() {
		Defense.notNull(interrogateFile, "interrogateFile cannot be null.");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.vetservices.lib.filemanager.api.FileManager#validateFileForPDFConversion(gov.va.vetservices.lib.filemanager.api.
	 * FileManager.FileDto)
	 */
	@Override
	public FileManagerResponse validateFileForPDFConversion(final FileManagerRequest request) {
		final FileManagerResponse response = new FileManagerResponse();
		response.setDoNotCacheResponse(true);

		try {

			simpleInputValidation(request, response);

			if (response.getMessages().isEmpty()) {
				final ImplDto implDto = FileManagerUtils.makeImplDto(request);

				// determine if the file can be converted to PDF
				interrogateFile.canConvertToPdf(implDto);

				if (!implDto.getMessages().isEmpty()) {
					response.addMessages(implDto.getMessages());
				}
			}

		} catch (final Throwable e) { // NOSONAR - catch everything here
			addFileManagerExceptionToResponse(e, response);
		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.vetservices.lib.filemanager.api.FileManager#validateFileForPDFConversion(gov.va.vetservices.lib.filemanager.api.
	 * FileManager.FileDto)
	 */
	@Override
	public FileManagerResponse validateFileForPDFConversion(FileManagerRequest request, ConstraintValidatorContext context) {
		final FileManagerResponse response = validateFileForPDFConversion(request);
		for (final Message msg : response.getMessages()) {
			final ConstraintValidatorContext.ConstraintViolationBuilder builder =
					context.buildConstraintViolationWithTemplate("{" + msg.getKey() + "}");
			builder.addConstraintViolation();
		}
		
		return response;
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.vetservices.lib.filemanager.api.FileManager#convertToPdf(byte[])
	 *
	 * Dev notes:
	 * - Method is sourced from wss PDFServiceImpl.convertPDF(..) & PDFGenerate.generateBody()
	 */
	@Override
	public FileManagerResponse convertToPdf(final FileManagerRequest request) {
		final FileManagerResponse response = new FileManagerResponse();
		response.setDoNotCacheResponse(true);

		try {

			simpleInputValidation(request, response);

			if (response.getMessages().isEmpty()) {
				final ImplDto implDto = FileManagerUtils.makeImplDto(request);

				// convert the file to PDF
				convertFile.convertToPdf(implDto);

				if (implDto.getMessages().isEmpty()) {
					// stamp the PDF, if required
					stampFile.stampPdf(implDto);
				} else {
					response.setMessages(implDto.getMessages());
				}

				response.setFileDto(implDto.getPdfFileDto());

				if (implDto.getPdfFileDto() != null && implDto.getPdfFileDto().getFilename() != null) {
					response.setSafeDatestampedFilename(FileManagerUtils.getSafeDatestampedFilename(implDto));
				}
			}

		} catch (final Throwable e) { // NOSONAR - catch everything here
			addFileManagerExceptionToResponse(e, response);
		}

		return response;
	}

	/**
	 * Add error messages onto the response if any required objects are not provided.
	 *
	 * @param request the FileManagerRequest
	 * @param response the File Manager Response
	 */
	private void simpleInputValidation(final FileManagerRequest request, final FileManagerResponse response) {
		final ImplArgDto<FileManagerRequest> arg = new ImplArgDto<>(request);
		final List<Message> messages = simpleRequestValidator.validate(arg);

		if (messages != null && !messages.isEmpty()) {
			response.addMessages(messages);
		}
	}

	/**
	 * Add an arbitrary exception to the response.
	 *
	 * @param e the Throwable
	 * @param response the response to be returned to consumer
	 */
	protected void addFileManagerExceptionToResponse(final Throwable e, final FileManagerResponse response) {
		if (!FileManagerException.class.isAssignableFrom(e.getClass())) {
			LOGGER.error("Unexpected " + e.getClass().getSimpleName()
					+ " exception in vetservices-lib-filemanager. Please solve thsi issue at its source.", e);
			response.addMessage(MessageSeverity.FATAL, LibFileManagerMessageKeys.UNEXPECTED_ERROR,
					messageUtils.returnMessage(LibFileManagerMessageKeys.UNEXPECTED_ERROR));
		} else {
			final FileManagerException fme = (FileManagerException) e;
			response.addMessage(fme.getMessageSeverity(), fme.getKey(), fme.getMessage());
		}
	}
}