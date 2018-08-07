package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.impl.validate.Validator;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.util.MessageUtils;

/**
 * Perform the necessary tests to confirm that the file can be converted to PDF.
 * This validation would attempt to catch "subtle" corruptions that could be introduced to a PDF file
 * that might cause PDF readers to fail when trying to open it.
 *
 * @author aburkholder
 */
@Component(ConversionValidator.BEAN_NAME)
public class ConversionValidator implements Validator<ImplDto> {

	public static final String BEAN_NAME = "conversionValidator";
	
	/** Auto wire message utilities */
	@Autowired
	@Qualifier("libfilemanagerMessageUtils")
	public MessageUtils messageUtils;

	/**
	 * NOSONAR TODO if it is decided to do this ...
	 * <p>
	 * Perform the necessary tests to confirm that the file can be converted to PDF.
	 * Attempts to catch "subtle" corruptions that could be introduced to a PDF file
	 * that might cause PDF readers to fail when trying to open it.
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
			return Arrays.asList(new Message[] { 
					messageUtils.createMessage(MessageSeverity.ERROR, LibFileManagerMessageKeys.UNEXPECTED_ERROR)  });
		}

		// NOSONAR TODO would use iText 7 "checker" class(es) as template to validate all objects in the PDF.
		final List<Message> list = null; // NOSONAR
		return list; // NOSONAR
	}

}
