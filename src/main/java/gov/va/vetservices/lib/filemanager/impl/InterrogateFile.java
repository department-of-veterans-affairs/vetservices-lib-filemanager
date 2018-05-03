package gov.va.vetservices.lib.filemanager.impl;

import java.util.List;

import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.ByteArrayValidator;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.ConversionValidator;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.FileTypeValidator;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.FilenameValidator;

/**
 * Interrogate a provided file byte array to determine if it can be converted to PDF.
 *
 * @author aburkholder
 */
public class InterrogateFile {

	FilenameValidator filenameValidator = new FilenameValidator();

	ByteArrayValidator bytearrayValidator = new ByteArrayValidator();

	FileTypeValidator filetypeValidator = new FileTypeValidator();

	ConversionValidator conversionValidator = new ConversionValidator();

	/**
	 * Determines if a file can be converted to a PDF with the technologies currently being used by this library.
	 * <p>
	 * Any validation errors that occur are passed in the response messages, otherwise the response messages will be {@code null}.
	 *
	 * @param fileDto the file to interrogate
	 * @return ServiceResponse with {@code null} messages if the file can be converted
	 */
	public FileManagerResponse canConvertToPdf(ImplDto validatorDto) {
		FileManagerResponse response = new FileManagerResponse();

		List<Message> messages = null;

		// validate file name
		if ((messages = filenameValidator.validate((new ImplArgDto<ImplDto>(validatorDto)))) != null) {
			response.addMessages(messages);
		}

		// validate byte array
		if ((messages = bytearrayValidator.validate((new ImplArgDto<byte[]>(validatorDto.getFileDto().getFilebytes())))) != null) {
			response.addMessages(messages);
		} else {
			// validate file type
			if ((messages = filetypeValidator.validate((new ImplArgDto<ImplDto>(validatorDto)))) != null) {
				response.addMessages(messages);
			} else {
				// validate can be converted
				if ((messages = conversionValidator.validate((new ImplArgDto<ImplDto>(validatorDto)))) != null) {
					response.addMessages(messages);
				}
			}
		}

		return response;
	}

}
