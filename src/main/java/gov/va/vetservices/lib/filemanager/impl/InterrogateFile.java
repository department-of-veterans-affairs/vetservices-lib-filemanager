package gov.va.vetservices.lib.filemanager.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.util.Defense;
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
	 * Any validation errors that occur are passed in the implDto messages, otherwise the implDto messages will be {@code null}.
	 *
	 * @param fileDto the file to interrogate
	 */
	public void canConvertToPdf(ImplDto implDto) {

		validateFilename(implDto);

		if (implDto.getMessages().isEmpty()) {
			validateByteArray(implDto);
		}

		if (implDto.getMessages().isEmpty()) {
			validateFileType(implDto);
		}

		if (implDto.getMessages().isEmpty()) {
			validateConversion(implDto);
		}
	}

	/**
	 * Validate the filename for common operating system constraints.
	 * Any error messages are returned on the implDto parameter.
	 *
	 * @param implDto the DTO with filename to validate
	 * @see FilenameValidator#validate(ImplArgDto)
	 */
	private void validateFilename(ImplDto implDto) {
		ImplArgDto<ImplDto> arg = new ImplArgDto<>(implDto);
		filenameValidator.validate(arg);
	}

	/**
	 * Validate the original file bytes for length. Any error messages will be returned on the implDto parameter.
	 *
	 * @param bytes the bytes to validate
	 * @see ByteArrayValidator#validate(ImplArgDto)
	 */
	private void validateByteArray(ImplDto implDto) {
		ImplArgDto<byte[]> argDtoBytes = new ImplArgDto<>(implDto.getOriginalFileDto().getFilebytes());
		List<Message> messages = bytearrayValidator.validate(argDtoBytes);
		if (messages != null) {
			implDto.addMessages(messages);
		}
	}

	/**
	 * Validate the MIME type to ensure it can be converted to PDF.
	 *
	 * @param implDto the DTO with filename to validate
	 * @param response the response to return to the consumer
	 * @see FileTypeValidator#validate(ImplArgDto)
	 */
	private void validateFileType(ImplDto implDto) {
		ImplArgDto<ImplDto> arg = new ImplArgDto<>(implDto);
		List<Message> messages = filetypeValidator.validate(arg);
		if (messages != null) {
			implDto.addMessages(messages);
		}
	}

	/**
	 * Validate that the file bytes can be converted to a PDF.
	 *
	 * @param implDto the DTO with filename to validate
	 * @param response the response to return to the consumer
	 * @see FileTypeValidator#validate(ImplArgDto)
	 */
	private void validateConversion(ImplDto implDto) {
		ImplArgDto<ImplDto> arg = new ImplArgDto<>(implDto);
		List<Message> messages = conversionValidator.validate(arg);
		if (messages != null) {
			implDto.addMessages(messages);
		}
	}
}
