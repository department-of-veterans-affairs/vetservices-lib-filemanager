package gov.va.vetservices.lib.filemanager.impl;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.FileManager;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FormsEnum;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
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

	/*
	 * (non-Javadoc)
	 *
	 * @see gov.va.vetservices.lib.filemanager.api.FileManager#validateFileForPDFConversion(gov.va.vetservices.lib.filemanager.api.
	 * FileManager.FileDto)
	 */
	@Override
	public FileManagerResponse validateFileForPDFConversion(FileDto fileDto) {
		InterrogateFile interrogateFile = new InterrogateFile();
		FileManagerResponse response = new FileManagerResponse();

		validateInputs(fileDto, response);
		if (response.getMessages().isEmpty()) {
			ImplDto implDto = FileManagerUtils.makeImplDto(fileDto);

			// determine if the file can be converted to PDF
			response = interrogateFile.canConvertToPdf(implDto);
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
	public FileManagerResponse convertToPdf(FileDto fileDto) {
		ConvertFile convertFile = new ConvertFile();
		StampPdf stampPdf = new StampPdf();
		FileManagerResponse response = new FileManagerResponse();

		validateInputs(fileDto, response);
		if (response.getMessages().isEmpty()) {
			ImplDto implDto = FileManagerUtils.makeImplDto(fileDto);

			// convert the file to PDF
			response = convertFile.doConversion(implDto);

			// stamp the PDF, if required
			stampPdf.doStamp(implDto, response);
		}

		return response;
	}

	/**
	 * Add error messages onto the response if there are any issues with the FileDto object / contents.
	 *
	 * @param fileDto the FileDto
	 * @param response the File Manager Response
	 */
	private void validateInputs(FileDto fileDto, FileManagerResponse response) {
		if (fileDto == null) {
			response.addMessage(MessageSeverity.ERROR, MessageKeysEnum.FILE_DTO_NULL.getKey(),
					MessageKeysEnum.FILE_DTO_NULL.getMessage());
		} else {
			if ((fileDto.getFormName() == null) || !Arrays.asList(FormsEnum.values()).contains(fileDto.getFormName())) {
				response.addMessage(MessageSeverity.ERROR, MessageKeysEnum.FORM_NOT_SPECIFIED.getKey(),
						MessageKeysEnum.FORM_NOT_SPECIFIED.getMessage());
			}
			if (StringUtils.isBlank(fileDto.getFilename())) {
				response.addMessage(MessageSeverity.ERROR, MessageKeysEnum.FILE_NAME_NULL_OR_EMPTY.getKey(),
						MessageKeysEnum.FILE_NAME_NULL_OR_EMPTY.getMessage());
			}
			if ((fileDto.getFilebytes() == null) || (fileDto.getFilebytes().length < 1)) {
				response.addMessage(MessageSeverity.ERROR, MessageKeysEnum.FILE_BYTES_NULL_OR_EMPTY.getKey(),
						MessageKeysEnum.FILE_BYTES_NULL_OR_EMPTY.getMessage());
			}
		}
	}
}
