package gov.va.vetservices.lib.filemanager.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;
import gov.va.vetservices.lib.filemanager.impl.validate.ValidatorArg;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.ByteArrayValidator;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.FileConvertibleMimeTypeValidator;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.FilenameValidator;

@Component
public class InterrogateFile {

	FilenameValidator filenameValidator = new FilenameValidator();

	ByteArrayValidator bytearrayValidator = new ByteArrayValidator();

	FileConvertibleMimeTypeValidator filetypeValidator = new FileConvertibleMimeTypeValidator();

	/**
	 * <p>
	 * Determines if a file can be converted to a PDF with the technologies currently being used by this library.
	 * </p>
	 * <p>
	 * Any validation errors that occur are passed in the response messages, otherwise the response messages will be {@code null}.
	 * </p>
	 *
	 * @param fileDto the file to interrogate
	 * @return ServiceResponse with {@code null} messages if the file can be converted
	 */
	public FileManagerResponse canConvertToPdf(ValidatorDto validatorDto) {
		FileManagerResponse response = new FileManagerResponse();

		List<Message> messages = null;

		if ((messages = filenameValidator.validate((new ValidatorArg<ValidatorDto>(validatorDto)))) != null) {
			response.addMessages(messages);
		}

		if ((messages = bytearrayValidator.validate((new ValidatorArg<byte[]>(validatorDto.getFileDto().getFilebytes())))) != null) {
			response.addMessages(messages);
		} else {
			if ((messages = filetypeValidator.validate((new ValidatorArg<ValidatorDto>(validatorDto)))) != null) {
				response.addMessages(messages);
			}
		}

		return response;
	}

}
