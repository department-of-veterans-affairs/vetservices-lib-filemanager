/**
 *
 */
package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.impl.validate.AbstractValidator;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;

/**
 * Validate file names that are consistent with common operating system constraints.
 *
 * @author aburkholder
 */
public class FilenameValidator extends AbstractValidator {

	@Value("${" + MessageKeys.FILE_NAME_NULL_OR_EMPTY + "}")
	private String messageValue;

	/**
	 * <p>
	 * Pass only {@code String} to this validator.
	 * </p>
	 * <p>
	 * JavaDoc from {@link AbstractValidator}:<br/>
	 * {@inheritDoc AbstractValidator#validate(java.lang.Object)}
	 * <p/>
	 */
	@Override
	public Message validate(Object toValidate) {
		String filename = (String) toValidate;

		Message message = null;

		if (StringUtils.isBlank(filename)) {
			message = new Message(MessageSeverity.ERROR, MessageKeys.FILE_NAME_NULL_OR_EMPTY, messageValue);

		} else {
			String[] filenames = { null, null };
			if (filename.contains(".")) {
				filenames[0] = StringUtils.truncate(filename, filename.lastIndexOf("."));
				filenames[1] = StringUtils.substring(filename, filename.lastIndexOf(".") + 1);
			} else {
				filenames[0] = filename;
			}

			if (StringUtils.isBlank(filenames[0])) {
				message = new Message(MessageSeverity.ERROR, MessageKeys.FILE_NAME_NULL_OR_EMPTY, messageValue);
			}
		}

		return message;
	}

}
