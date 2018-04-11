package gov.va.vetservices.lib.filemanager.impl.validate;

import gov.va.ascent.framework.messages.Message;

/**
 * Abstract class for FileManager Validators.
 *
 * @author aburkholder
 */
public abstract class AbstractValidator {

	/**
	 * Validate an object. Cast the object parameter to the desired type in the validator.
	 * The returned {@link Message} object should be {@code null} if the validation passes.
	 *
	 * @param toValidate the object to validate
	 * @return Message or {@code null} if validation passes
	 */
	public abstract Message validate(Object toValidate);

}
