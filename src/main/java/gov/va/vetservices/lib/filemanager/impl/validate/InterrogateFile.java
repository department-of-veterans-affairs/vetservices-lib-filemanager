package gov.va.vetservices.lib.filemanager.impl.validate;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.service.ServiceResponse;
import gov.va.vetservices.lib.filemanager.api.FileDto;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.ByteArrayValidator;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.FilenameValidator;

class InterrogateFile {

	@Autowired
	FilenameValidator filenameValidator;

	@Autowired
	ByteArrayValidator bytearrayValidator;

	/**
	 * <p>
	 * Determines if a file can be converted to a PDF with the technologies currently being used by this library.
	 * </p>
	 * <p>
	 * Any validation errors that occur are passed in the response messages, otherwise the response messages will be empty.
	 * </p>
	 *
	 * @param fileDto the file to interrogate
	 * @return ServiceResponse if the file can be converted, {@code true}, otherwise {@code false}
	 */
	ServiceResponse canConvertToPdf(FileDto fileDto) {
		ServiceResponse response = new ServiceResponse();

		List<Message> messages = new ArrayList<>();
		Message message = null;

		if ((message = filenameValidator.validate(fileDto.getFileName())) != null) {
			messages.add(message);
		}

		if ((message = bytearrayValidator.validate(fileDto.getFileBytes())) != null) {
			messages.add(message);
		}

		return response;
	}

}
