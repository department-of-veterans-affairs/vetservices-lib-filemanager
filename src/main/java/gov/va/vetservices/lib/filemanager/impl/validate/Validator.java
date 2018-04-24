package gov.va.vetservices.lib.filemanager.impl.validate;

import java.util.List;

import gov.va.ascent.framework.messages.Message;

/**
 * Interface for FileManager Validators.
 *
 * @author aburkholder
 */
@FunctionalInterface
public interface Validator<T> {

	/**
	 * Validate an object. Paramaeters must implement the {@link ValidatorArg} marker interface.
	 *
	 * @param toValidate the object to validate
	 * @return List&lt;Message&gt; or {@code null} if validation passes
	 */
	public List<Message> validate(ValidatorArg<T> toValidate);

}
