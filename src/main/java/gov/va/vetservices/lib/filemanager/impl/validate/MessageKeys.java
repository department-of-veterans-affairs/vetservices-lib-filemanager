package gov.va.vetservices.lib.filemanager.impl.validate;

import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public enum MessageKeys {

	/** FileDto was null */
	FILE_DTO_NULL("filemanager.file.dto.null", "File data transfer object cannot be null."),
	/** Filename was null or empty */
	FILE_NAME_NULL_OR_EMPTY("filemanager.file.name.null.or.empty", "File name cannot be null or empty."),
	/** Filename starts or ends with one of {@link FileManagerUtils.ILLEGAL_FILE_START_CHARS} */
	FILE_NAME_MALFORMED("filemanager.file.name.nmalformed",
			"File name is malformed. Filenames cannot begin or end with any of " + FileManagerProperties.FILE_NAME_ILLEGAL_CHARS),
	/** Filename was too long */
	FILE_NAME_TOO_LONG("filemanager.file.name.length", "File name must be less than " + maxFilenameLength() + " characters."),
	/** Byte array was null or empty */
	FILE_BYTES_NULL_OR_EMPTY("filemanager.file.bytes.null.or.empty", "File content cannot be null or empty array."),
	/** Byte array cannot be read. <b>Args:</b> {@code filename} */
	FILE_BYTES_UNREADABLE("filemanager.file.bytes.unreadable",
			"Content of {0} cannot be read due to corrupted file or an unexpected issue."),
	/** Byte array is too large. <b>Args:</b> {@code maxSizeMB} */
	FILE_BYTES_SIZE("filemanager.file.bytes.size", "File size exceeds maximum allowable size of {0}."),
	/** Filename extension is not convertible to PDF. <b>Args:</b> {@code filename} */
	FILE_EXTENSION_NOT_CONVERTIBLE("filemanager.file.extension.not.convertible",
			"File {0} connot be converted to PDF. Supported types are: " + convertibleTypes()),
	/** File byte content is not convertible to PDF. <b>Args:</b> {@code filename} */
	FILE_CONTENT_NOT_CONVERTIBLE("filemanager.file.content.not.convertible",
			"Content of file {0} connot be converted to PDF. Supported types are: " + convertibleTypes()),
	/** File extension doesn't match detected type. <b>Args:</b> {@code filename, detectedSubtype, fileExtension} */
	FILE_EXTENSION_CONTENT_MISMATCH("filemanager.file.extension.content.mismatch",
			"Filename {0} type does not match file content, or the file is corrupt. The file content is {1}, not {2}."),
	/** There is uncertainty about the MIME type based on content and file extension. <b>Args:</b> {@code filename, fileExtension} */
	FILE_TYPE_UNVERIFIABLE("filemanager.file.type.unverifiable",
			"Filename {0} cannot be verified to have {1} content. Ensure your file has a valid file extension, or try a different format. Supported types are: "
					+ convertibleTypes()),
	/** PDF content is invalid. <b>Args:</b> {@code filename} */
	PDF_CONTENT_INVALID("filemanager.pdf.content.invalid", "The content of {0} is locked, corrput or otherwise invalid."),
	/** The PDF is locked with Adobe encryption. <b>Args:</b> {@code filename} */
	PDF_LOCKED("filemanager.pdf.locked", "The file {0} is locked with Adobe encryption. Unlock the PDF file."),
	/** The PDF is locked with Adobe encryption. <b>Args:</b> {@code filename} */
	PDF_TAMPERED("filemanager.pdf.tampered", "The file {0} is signed and has been tampered with."),
	/** PDF is unreadable. <b>Args:</b> {@code filename} */
	PDF_UNREADABLE("filemanager.pdf.unreadable", "The file {0} cannot be read. It may be corrupt or tampered with."),
	/** The image is not convertible for PDF. <b>Args:</b> {@code filename, itextErrorMessage} */
	IMAGE_ITEXT_NOT_CONVERTIBLE("filemanager.pdf.image.not.consumable", "The file {0} cannot be used in a PDF. Error: {1}"),
	/** Internal FileManager issues that cannot be resolved at runtime */
	FILEMANAGER_ISSUE("filemanager.internal.issue", "Internal issue occurred. Please check the application logs.");

	private String key;
	private String message;

	MessageKeys(String key, String message) {
		this.key = key;
		this.message = message;
	}

	/**
	 * Make supported (convertible) types available to the enumerations.
	 *
	 * @return String[] the convertible types
	 */
	private static String convertibleTypes() {
		return FileManagerProperties.CONVERTIBLE_FILE_EXTENSIONS_STRING;
	}

	private static String maxFilenameLength() {
		return FileManagerProperties.DEFAULT_FILENAME_MAX_LENGTH;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}
