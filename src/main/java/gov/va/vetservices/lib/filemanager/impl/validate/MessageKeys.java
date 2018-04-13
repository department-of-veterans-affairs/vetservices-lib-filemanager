package gov.va.vetservices.lib.filemanager.impl.validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;

/**
 * FileManager message property keys and values.
 *
 * @author aburkholder
 */
public class MessageKeys {

	private static String validFileExtensions =
			StringUtils.arrayToCommaDelimitedString(FileManagerProperties.CONVERTIBLE_FILE_EXTENSIONS);

	@Autowired
	static FileManagerProperties fileManagerProperties;

	/*
	 * DEV NOTE:
	 * If you add a constant here, you must also add a message value in the MessageValuesEnum below.
	 */

	/** Message for a null file data transfer object */
	public static final String FILE_DTO_NULL = "filemanager.file.dto.null";
	/** Message for a null or empty file name */
	public static final String FILE_NAME_NULL_OR_EMPTY = "filemanager.file.name.null.or.empty";
	/** Message for invalid characters found in file name / extension */
	// public static final String FILE_NAME_INVALID_CHARACTERS = "filemanager.messages.file.name.invalid.characters";
	/** Message for a null or empty byte array */
	public static final String FILE_BYTES_NULL_OR_EMPTY = "filemanager.file.bytes.null.or.empty";
	/** Message for a byte array that is too large */
	public static final String FILE_BYTES_SIZE = "filemanager.file.bytes.size";
	/** Message for a given file extension that is not convertible to PDF */
	public static final String FILE_EXTENSION_NOT_CONVERTIBLE = "filemanager.file.extension.not.convertible";
	/** Message for situation where MimeType derived from file extension and MimeType detected from bytes do not match */
	public static final String FILE_EXTENSION_CONTENT_MISMATCH = "filemanager.file.extension.content.mismatch";

	/**
	 * Do not instantiate
	 */
	MessageKeys() {
		throw new IllegalAccessError("MessageKeys is a static class. Do not instantiate it.");
	}

	/**
	 * <p>
	 * Get the message for a given constant key name.
	 * <p>
	 * The {@code messageConstant} parameter can be the name of the constant, or the constant itself.
	 * For example:
	 *
	 * <pre>
	 * // using the name of the constant
	 * String theMessage = getMessage("FILE_NAME_NULL_OR_EMPTY");
	 * // using the constant itself
	 * String theMessage = getMessage(MessageKeys.FILE_NAME_NULL_OR_EMPTY);
	 * </pre>
	 *
	 * @param messageConstant
	 * @return
	 */
	public static String getMessage(final String messageConstant) {
		String message = null;

		for (MessageValuesEnum enumeration : MessageValuesEnum.values()) {
			// see if they send a key value
			if (enumeration.propKey.equals(messageConstant)) {
				message = enumeration.propValue;
				break;
			}
			// see if they sent the actual name of the MessageKeys constant instead of the property key
			if (enumeration.name().equals(messageConstant)) {
				message = enumeration.propValue;
				break;
			}
		}

		return message;
	}

	/**
	 * Local enumeration to match keys to their message values.
	 *
	 * @author aburkholder
	 */
	protected enum MessageValuesEnum {
		/*
		 * DEV NOTE:
		 * If you add an enumeration here, you should probably add a MessageKeys constant above.
		 */

		FILE_NAME_DTO_NULL(MessageKeys.FILE_DTO_NULL, "File data transfer object cannot be null."),
		FILE_NAME_NULL_OR_EMPTY(MessageKeys.FILE_NAME_NULL_OR_EMPTY, "File name cannot be null, empty or only whitespace."),
		// FILE_NAME_INVALID_CHARACTERS(MessageKeys.FILE_NAME_INVALID_CHARACTERS, "File name must ????????????"),
		FILE_BYTES_NULL_OR_EMPTY(MessageKeys.FILE_BYTES_NULL_OR_EMPTY, "File content cannot be null or empty array."),
		FILE_BYTES_SIZE(MessageKeys.FILE_BYTES_SIZE,
				"File size exceeds maximum allowable size " + fileManagerProperties.getMaxFileMegaBytes() + "."),
		FILE_EXTENSION_NOT_CONVERTIBLE(MessageKeys.FILE_EXTENSION_NOT_CONVERTIBLE,
				"The file extension specified is not supported for conversion to PDF. "
						+ "The file must be one of the follwoing types: " + validFileExtensions),
		FILE_EXTENSION_CONTENT_MISMATCH(MessageKeys.FILE_EXTENSION_CONTENT_MISMATCH,
				"The provided file extension does not match the content of the file.");

		/* ENUM MEMBERS, CONSTRUCTOR AND METHODS */

		/** property key for the given message */
		protected String propKey;
		/** property value for the given message */
		protected String propValue;

		/**
		 * Sets the associated key for the enumeration
		 *
		 * @param key
		 * @param value
		 */
		MessageValuesEnum(String key, String value) {
			this.propKey = key;
			this.propValue = value;
		}

		/**
		 * Get the property value for a given enumeration.
		 *
		 * @param key
		 * @return String
		 */
		protected final String getPropertyValue() {
			return this.propValue;
		}

	}

}
