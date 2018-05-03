package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.util.ArrayList;
import java.util.List;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.validate.Validator;

/**
 * Perform the necessary tests to confirm that the file can be converted to PDF.
 *
 * @author aburkholder
 */
public class ConversionValidator implements Validator<ImplDto> {

	/**
	 * Perform the necessary tests to confirm that the file can be converted to PDF.
	 */
	@Override
	public List<Message> validate(ImplArgDto<ImplDto> toValidate) {
		// NOSONAR TODO requires conversion and stamping code to be written first
		List<Message> list = new ArrayList<>();
		list.add(new Message(MessageSeverity.ERROR, "UNFINISHED.WORK",
				"ConversionValidator has not yet been completed. Search for TODO."));
		return list;
	}

}
