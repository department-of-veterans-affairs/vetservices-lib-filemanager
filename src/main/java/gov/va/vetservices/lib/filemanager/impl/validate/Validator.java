package gov.va.vetservices.lib.filemanager.impl.validate;

import java.util.List;

import gov.va.ascent.framework.messages.Message;

/**
 * Interface for FileManager Validators.
 *
 * @author aburkholder
 */
public interface Validator<T> {

	/**
	 * Validate an object. Paramaeters must implement the {@link ValidatorParameter} marker interface.
	 *
	 * @param toValidate the object to validate
	 * @return Message or {@code null} if validation passes
	 */
	public List<Message> validate(ValidatorArg<T> toValidate);

}
