package gov.va.vetservices.lib.filemanager.impl.validate;

import java.util.List;

import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;

/**
 * Interface for FileManager Validators.
 *
 * @author aburkholder
 */
@FunctionalInterface
public interface Validator<T> {

	/**
	 * Validate an object. Paramaeters must implement the {@link ImplArgDto} marker interface.
	 *
	 * @param toValidate the object to validate
	 * @return List&lt;Message&gt; or {@code null} if validation passes
	 */
	public List<Message> validate(ImplArgDto<T> toValidate);

}
