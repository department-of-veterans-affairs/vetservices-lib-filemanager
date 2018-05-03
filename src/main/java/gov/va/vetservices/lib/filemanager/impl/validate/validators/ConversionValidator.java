package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.util.ArrayList;
import java.util.List;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;
import gov.va.vetservices.lib.filemanager.impl.validate.Validator;
import gov.va.vetservices.lib.filemanager.impl.validate.ValidatorArg;

/**
 * Perform the necessary tests to confirm that the file can be converted to PDF.
 *
 * @author aburkholder
 */
public class ConversionValidator implements Validator<ValidatorDto> {

	/**
	 * Perform the necessary tests to confirm that the file can be converted to PDF.
	 */
	@Override
	public List<Message> validate(ValidatorArg<ValidatorDto> toValidate) {
		// NOSONAR TODO requires conversion and stamping code to be written first
		List<Message> list = new ArrayList<>();
		list.add(new Message(MessageSeverity.ERROR, "UNFINISHED.WORK",
				"ConversionValidator has not yet been completed. Search for TODO."));
		return list;
	}

}
