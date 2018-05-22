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
	public List<Message> validate(ImplArgDto<ImplDto> toValidate) {
		if ((toValidate == null) || (toValidate.getValue() == null)) {
			MessageKeysEnum msg = MessageKeysEnum.UNEXPECTED_ERROR;
			Message message = new Message(MessageSeverity.ERROR, msg.getKey(), msg.getMessage());
			return Arrays.asList(new Message[] { message });
		}

		// NOSONAR TODO requires conversion and stamping code to be written first
		List<Message> list = null; // new ArrayList<>(); // NOSONAR
// NOSONAR		list.add(new Message(MessageSeverity.ERROR, "UNFINISHED.WORK",
// NOSONAR				"ConversionValidator has not yet been completed. Search for TODO."));
		return list; // NOSONAR
	}

}
