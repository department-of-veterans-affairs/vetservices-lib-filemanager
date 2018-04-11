package gov.va.vetservices.lib.filemanager.impl.validate;

public class MessageKeys {

	/** Message for a null or empty file name */
	public static final String FILE_NAME_NULL_OR_EMPTY = "filemanager.messages.file.name.null.or.empty";
	/** Message for invalid characters found in file name / extension */
	public static final String FILE_NAME_INVALID_CHARACTERS = "filemanager.messages.file.name.invalid.characters";
	/** Message for a null or empty byte array */
	public static final String FILE_BYTES_NULL_OR_EMPTY = "filemanager.config.file.bytes.null.or.empty";
	/** Message for a byte array that is too large */
	public static final String FILE_BYTES_SIZE = "filemanager.config.file.bytes.size";

	/**
	 * Do not instantiate
	 */
	MessageKeys() {
		throw new IllegalAccessError("MessageKeys is a static class. Do not instantiate it.");
	}

}
