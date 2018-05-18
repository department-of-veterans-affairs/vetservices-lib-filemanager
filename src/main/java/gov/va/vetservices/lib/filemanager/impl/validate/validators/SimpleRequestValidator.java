package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.impl.validate.Validator;

/**
 * Validate the request object to ensure all required objects are present.
 * This class does NOT attempt to determine if the object contents are valid.
 *
 * @author aburkholder
 */
public class SimpleRequestValidator implements Validator<FileManagerRequest> {

	/**
	 * <p>
	 * Validate the request object to ensure all required objects are present.
	 * This method does NOT attempt to determine if the object contents are valid.
	 * <p>
	 * If validations succeeds, an empty list is returned.
	 * <p>
	 * JavaDoc from {@link Validator}
	 * {@inheritDoc Validator#validate(ImplArgDto)}
	 * <p>
	 */
	@Override
	public List<Message> validate(ImplArgDto<FileManagerRequest> toValidate) {
		List<Message> messages = new ArrayList<>();

		if ((toValidate == null) || (toValidate.getValue() == null)) {
			addError(messages, MessageKeysEnum.REQUEST_NULL);
			return messages;
		}

		if (toValidate.getValue().getFileDto() == null) {
			addError(messages, MessageKeysEnum.FILE_DTO_NULL);
		} else {
			// check processType, docTypeId, and claimId
			if (StringUtils.isBlank(toValidate.getValue().getDocTypeId())) {
				addError(messages, MessageKeysEnum.DOCTYPEID_NULL_OR_EMPTY);
			}
			if (toValidate.getValue().getProcessType() == null) {
				addError(messages, MessageKeysEnum.PROCESSTYPE_NOT_SPECIFIED);
			} else if (!ProcessType.OTHER.equals(toValidate.getValue().getProcessType())
					&& StringUtils.isBlank(toValidate.getValue().getClaimId())) {
				addError(messages, MessageKeysEnum.CLAIMID_NULL_OR_EMPTY);
			}
			// check filename
			if (StringUtils.isBlank(toValidate.getValue().getFileDto().getFilename())) {
				addError(messages, MessageKeysEnum.FILE_NAME_NULL_OR_EMPTY);
			}
			// check file bytes
			if ((toValidate.getValue().getFileDto().getFilebytes() == null)
					|| (toValidate.getValue().getFileDto().getFilebytes().length < 1)) {
				addError(messages, MessageKeysEnum.FILE_BYTES_NULL_OR_EMPTY);
			}
		}
		return (messages == null) || messages.isEmpty() ? null : messages;
	}

	/**
	 * Adds a {@link MessageSeverity#ERROR} message to the list of messages.
	 *
	 * @param implDto the ImplDto
	 * @param messageKey the {@link MessageKeysEnum} enumeration for key and message
	 */
	private void addError(List<Message> messages, MessageKeysEnum messageKey) {
		messages.add(new Message(MessageSeverity.ERROR, messageKey.getKey(), messageKey.getMessage()));
	}

}
