package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.impl.validate.Validator;

/**
 * Perform the necessary tests to confirm that the file can be converted to PDF.
 *
 * @author aburkholder
 */
@Component(ConversionValidator.BEAN_NAME)
public class ConversionValidator implements Validator<ImplDto> {

	public static final String BEAN_NAME = "conversionValidator";

	/**
	 * <p>
	 * Perform the necessary tests to confirm that the file can be converted to PDF.
	 * <p>
	 * If validations succeeds, {@code null} is returned, otherwise the returned list of messages is also returned on the ValidataorDto
	 * parameter.
	 * <p>
	 * JavaDoc from {@link Validator}:<br/>
	 * {@inheritDoc Validator#validate(ImplArgDto)}
	 * <p>
	 */
	@Override
	public List<Message> validate(final ImplArgDto<ImplDto> toValidate) {
		if (toValidate == null || toValidate.getValue() == null) {
			final MessageKeysEnum msg = MessageKeysEnum.UNEXPECTED_ERROR;
			final Message message = new Message(MessageSeverity.ERROR, msg.getKey(), msg.getMessage());
			return Arrays.asList(new Message[] { message });
		}

		// NOSONAR TODO would use iText 7 "checker" class(es) as template to validate all objects in the PDF.
		final List<Message> list = null; // NOSONAR
		return list; // NOSONAR
	}

}
