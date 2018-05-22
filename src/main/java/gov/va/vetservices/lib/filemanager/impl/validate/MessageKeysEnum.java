package gov.va.vetservices.lib.filemanager.impl.validate;

import java.text.MessageFormat;
import java.util.EnumSet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

/**
 * An enumeration of messages that could be sent back to a calling program if an exception occurs.
 * Each enumeration contains a key (for reference purposes) and an explanatory message.
 * <p>
 * The messages may contain replaceable parameters, according to the rules of the {@link java.text.MessageFormat} class.
 *
 * @author aburkholder
 */
public enum MessageKeysEnum {

	/** Some unanticipated runtime exception that is not properly caught and dealt with */
	UNEXPECTED_ERROR("filemanager.unexpected.error", "FileManager library encountered an unexpected error. Please investigate."),
	/** FileManagerRequest was null */
	REQUEST_NULL("filemanager.request.null", "File Manager Request cannot be null."),
	/** FileDto was null */
	FILE_DTO_NULL("filemanager.file.dto.null", "File data transfer object cannot be null."),
	/** The process type associated with the file was not specified */
	PROCESSTYPE_NOT_SPECIFIED("filemanager.process.type.not.specified",
			"The process type associated with the file was not specified."),
	/** The docTypeId associated with the file was not specified */
	DOCTYPEID_NULL_OR_EMPTY("filemanager.doc.type.id.null.or.empty",
			"The document type ID associated with the file was not specified."),
	/** The form associated with the file was not specified */
	CLAIMID_NULL_OR_EMPTY("filemanager.claim.id.null.or.empty", "The Claim ID cannnot be null or empty for claims-related requests."),
	/** Filename was null or empty */
	FILE_NAME_NULL_OR_EMPTY("filemanager.file.name.null.or.empty", "File name cannot be null or empty."),
	/** Filename starts or ends with one of {@link FileManagerUtils.ILLEGAL_FILE_START_CHARS} */
	FILE_NAME_MALFORMED("filemanager.file.name.nmalformed",
			"File name is malformed. Filenames cannot begin or end with any of " + FileManagerProperties.FILE_NAME_ILLEGAL_CHARS),
	/** Filename was too long - dev note: arg is automatically replaced in getMessage() */
	FILE_NAME_TOO_LONG("filemanager.file.name.length", "File name must be less than {0} characters"),
	/** Byte array was null or empty */
	FILE_BYTES_NULL_OR_EMPTY("filemanager.file.bytes.null.or.empty", "File content cannot be null or empty."),
	/** Byte array cannot be read. <b>Args:</b> {@code filename} */
	FILE_BYTES_UNREADABLE("filemanager.file.bytes.unreadable",
			"Content of {0} cannot be read due to corrupted file or an unexpected issue."),
	/** Byte array is too large. - dev note: arg is automatically replaced in getMessage() */
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
	/** PDF is unreadable. <b>Args:</b> {@code filename, reason (corrupt, tampered with, etc)} */
	PDF_UNREADABLE("filemanager.pdf.unreadable", "The file {0} cannot be read. The file is {1}."),
	/** The image is not convertible for PDF. <b>Args:</b> {@code filename, itextErrorMessage} */
	IMAGE_ITEXT_NOT_CONVERTIBLE("filemanager.pdf.image.not.consumable", "The file {0} cannot be used in a PDF. Error: {1}"),
	/** Problem with internal (iText) processing during conversion. <b>Args:</b> {@code filename, itextErrorMessage} */
	PDF_CONVERSION_PROCESSING("filemanager.pdf.conversion.processing",
			"The file {0} cannot be converted to PDF due to internal processing issue. Error: {1}"),
	/** Problem with internal (iText) processing during stamping. <b>Args:</b> {@code filename, itextErrorMessage} */
	PDF_STAMPING("filemanager.pdf.stamping", "Could not stamp file {0}. Error: {1}"),
	/** Internal FileManager issues that cannot be resolved at runtime */
	FILEMANAGER_ISSUE("filemanager.internal.issue", "Internal issue occurred. Please check the application logs.");

	/**
	 * This is a hack to inject the spring-managed FileManagerProperties bean
	 * into the enumerations on this enum. Lovin' spring, oh yeah.
	 *
	 * @author aburkholder
	 */
	@Component
	public static class FileManagerPropertiesInjector {
		/** The spring-managed component to inject into the enumerations */
		@Autowired
		private FileManagerProperties fileManagerProperties;

		/**
		 * Put a reference to the FileManagerProperties component
		 * into the enumerations.
		 */
		@PostConstruct
		public void postConstruct() {
			for (MessageKeysEnum mke : EnumSet.allOf(MessageKeysEnum.class)) {
				mke.setFileManagerProperties(fileManagerProperties);
			}
		}
	}

	/** the local reference into which the injector puts the FileManagerProperties component reference */
	private FileManagerProperties fileManagerProperties;

	private String key;
	private String message;

	/**
	 * Add a new enumeration with key and message.
	 *
	 * @param key the message key
	 * @param message the explanatory message
	 */
	MessageKeysEnum(String key, String message) {
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

	/**
	 * Set the fileManagerProperties value from the FileManagerPropertiesInjector inner class.
	 *
	 * @param fileManagerProperties
	 */
	private void setFileManagerProperties(FileManagerProperties fileManagerProperties) { // NOSONAR must be outside the injector
		this.fileManagerProperties = fileManagerProperties;
	}

	/**
	 * The key for the enumeration.
	 *
	 * @return String the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * The message for the enumeration.
	 *
	 * @return String the message
	 */
	public String getMessage() {
		if (this.equals(MessageKeysEnum.FILE_NAME_TOO_LONG)) {
			return MessageFormat.format(this.message, fileManagerProperties.getMaxFilenameLen());
		} else if (this.equals(MessageKeysEnum.FILE_BYTES_SIZE)) {
			return MessageFormat.format(this.message, fileManagerProperties.getMaxFileMegaBytes());
		}
		return message;
	}

}
