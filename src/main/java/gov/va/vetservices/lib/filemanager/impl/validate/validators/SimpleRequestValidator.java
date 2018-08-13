package gov.va.vetservices.lib.filemanager.impl.validate.validators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.va.ascent.framework.messages.Message;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplArgDto;
import gov.va.vetservices.lib.filemanager.impl.validate.Validator;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.util.MessageUtils;

/**
 * Validate the request object to ensure all required objects are present.
 * This class does NOT attempt to determine if the object contents are valid.
 *
 * @author aburkholder
 */
@Component(SimpleRequestValidator.BEAN_NAME)	
public class SimpleRequestValidator implements Validator<FileManagerRequest> {


	public static final String BEAN_NAME = "simpleRequestValidator";
	
	/** Auto wire message utilities */
	@Autowired
	@Qualifier(MessageUtils.BEAN_NAME)
	private MessageUtils messageUtils;
	
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
	public List<Message> validate(final ImplArgDto<FileManagerRequest> toValidate) {
		final List<Message> messages = new ArrayList<>();

		if (toValidate == null || toValidate.getValue() == null) {
			addError(messages, LibFileManagerMessageKeys.REQUEST_NULL);
			return messages;
		}

		if (toValidate.getValue().getFileDto() == null) {
			addError(messages, LibFileManagerMessageKeys.FILE_DTO_NULL);
		} else {
			checkMetadata(messages, toValidate.getValue());
			checkFilename(messages, toValidate.getValue().getFileDto());
			checkFileBytes(messages, toValidate.getValue().getFileDto());
		}
		return messages == null || messages.isEmpty() ? null : messages;
	}

	/**
	 * Check processType, docTypeId, and claimId.
	 *
	 * @param messages to put messages on
	 * @param request the object containing the metadata
	 */
	private void checkMetadata(final List<Message> messages, final FileManagerRequest request) {
		if (StringUtils.isBlank(request.getDocTypeId())) {
			addError(messages, LibFileManagerMessageKeys.DOCTYPEID_NULL_OR_EMPTY);
		}
		if (request.getProcessType() == null) {
			addError(messages, LibFileManagerMessageKeys.PROCESSTYPE_NOT_SPECIFIED);
		} else if (!ProcessType.OTHER.equals(request.getProcessType()) && StringUtils.isBlank(request.getClaimId())) {
			addError(messages, LibFileManagerMessageKeys.CLAIMID_NULL_OR_EMPTY);
		}
	}

	/**
	 * Check the filename has something in it.
	 *
	 * @param messages to put messages on
	 * @param fileDto the object to check
	 */
	private void checkFilename(final List<Message> messages, final FileDto fileDto) {
		if (fileDto == null || StringUtils.isBlank(fileDto.getFilename())) {
			addError(messages, LibFileManagerMessageKeys.FILE_NAME_NULL_OR_EMPTY);
		}
	}

	/**
	 * Check the file bytes has something in it.
	 *
	 * @param messages to put messages on
	 * @param fileDto the object to check
	 */
	private void checkFileBytes(final List<Message> messages, final FileDto fileDto) {
		if (fileDto.getFilebytes() == null || fileDto.getFilebytes().length < 1) {
			addError(messages, LibFileManagerMessageKeys.FILE_BYTES_NULL_OR_EMPTY);
		}
	}

	/**
	 * Adds a {@link MessageSeverity#ERROR} message to the list of messages.
	 *
	 * @param implDto the ImplDto
	 * @param messageKey the  enumeration for key and message
	 */
	private void addError(final List<Message> messages, final String key) {
		messages.add(new Message(MessageSeverity.ERROR, key, messageUtils.returnMessage(key)));
	}

}
