package gov.va.vetservices.lib.filemanager.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.util.Defense;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.ByteArrayValidator;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.ConversionValidator;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.FileTypeValidator;
import gov.va.vetservices.lib.filemanager.impl.validate.validators.FilenameValidator;

/**
 * Interrogate a provided file byte array to determine if it can be converted.
 *
 * @author aburkholder
 */
@Component(InterrogateFile.BEAN_NAME)
public class InterrogateFile {
	/*
	 * Design notes:
	 * To accommodate validations other than "convertible to PDF",
	 * add a method in this class to call the appropriate
	 * validators (presumably in a new package).
	 */

	public static final String BEAN_NAME = "interrogateFile";

	@Autowired
	@Qualifier(FilenameValidator.BEAN_NAME)
	FilenameValidator filenameValidator;

	@Autowired
	@Qualifier(ByteArrayValidator.BEAN_NAME)
	ByteArrayValidator bytearrayValidator;

	@Autowired
	@Qualifier(FileTypeValidator.BEAN_NAME)
	FileTypeValidator filetypeValidator;

	@Autowired
	@Qualifier(ConversionValidator.BEAN_NAME)
	ConversionValidator conversionValidator;

	@PostConstruct
	public void postconstruct() {
		Defense.notNull(filenameValidator, "filenameValidator cannot be null");
		Defense.notNull(bytearrayValidator, "bytearrayValidator cannot be null");
		Defense.notNull(filetypeValidator, "filetypeValidator cannot be null");
		Defense.notNull(conversionValidator, "conversionValidator cannot be null");
	}

	/**
	 * Determines if a file can be converted to a PDF with the technologies currently being used by this library.
	 * <p>
	 * Any validation errors that occur are passed in the response messages, otherwise the response messages will be {@code null}.
	 *
	 * @param fileDto the file to interrogate
	 * @return ServiceResponse with {@code null} messages if the file can be converted
	 */
	public FileManagerResponse canConvertToPdf(ImplDto implDto) {
		FileManagerResponse response = new FileManagerResponse();

		List<Message> messages = null;

		// validate file name
		if ((messages = filenameValidator.validate((new ImplArgDto<ImplDto>(implDto)))) != null) {
			response.addMessages(messages);
		}

		// validate byte array
		if ((messages = bytearrayValidator.validate((new ImplArgDto<byte[]>(implDto.getFileDto().getFilebytes())))) != null) {
			response.addMessages(messages);
		} else {
			// validate file type
			if ((messages = filetypeValidator.validate((new ImplArgDto<ImplDto>(implDto)))) != null) {
				response.addMessages(messages);
			} else {
				// validate can be converted
				if ((messages = conversionValidator.validate((new ImplArgDto<ImplDto>(implDto)))) != null) {
					response.addMessages(messages);
				}
			}
		}

		return response;
	}

}
