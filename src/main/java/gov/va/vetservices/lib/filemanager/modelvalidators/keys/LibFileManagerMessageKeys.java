package gov.va.vetservices.lib.filemanager.modelvalidators.keys;

/**
 * Project message keys for jsr303 custom validators
 *
 * @author Vanapalliv
 */
// Using interface so it works with the error key generator for display
@java.lang.SuppressWarnings("squid:S1214")
// CHECKSTYLE:OFF
public interface LibFileManagerMessageKeys { // NOSONAR
	// CHECKSTYLE:OFF

	/** Some unanticipated runtime exception that is not properly caught and dealt with. No args. */
	public static final String UNEXPECTED_ERROR = "filemanager.unexpected.error";
	/** FileManagerRequest was null. No args. */
	public static final String REQUEST_NULL = "filemanager.request.null";
	/** FileDto was null. No args. */
	public static final String FILE_DTO_NULL = "filemanager.file.dto.null";
	/** The process type associated with the file was not specified. No args. */
	public static final String PROCESSTYPE_NOT_SPECIFIED = "filemanager.process.type.not.specified";
	/** The docTypeId associated with the file was not specified. No args. */
	public static final String DOCTYPEID_NULL_OR_EMPTY = "filemanager.doc.type.id.null.or.empty";

	/** The form associated with the file was not specified. No args. */
	public static final String CLAIMID_NULL_OR_EMPTY = "filemanager.claim.id.null.or.empty";
	/** Filename was null or empty. No args. */
	public static final String FILE_NAME_NULL_OR_EMPTY = "filemanager.file.name.null.or.empty";
	/** Filename starts or ends with one of the valid types. No args. */
	public static final String FILE_NAME_MALFORMED = "filemanager.file.name.malformed";
	/** Filename contains illegal characters, per the restrictions of the Windows operating system. No args. */
	public static final String FILE_NAME_ILLEGAL = "filemanager.file.name.illegal";
	/** Filename was too long - dev note: arg is automatically replaced in getMessage() */
	public static final String FILE_NAME_TOO_LONG = "filemanager.file.name.length";

	/** Byte array was null or empty. No args. */
	public static final String FILE_BYTES_NULL_OR_EMPTY = "filemanager.file.bytes.null.or.empty";
	/** Byte array cannot be read. <b>Args:</b> {@code filename} */
	public static final String FILE_BYTES_UNREADABLE = "filemanager.file.bytes.unreadable";
	/** Byte array is too large. - dev note: arg is automatically replaced in getMessage() */
	public static final String FILE_BYTES_SIZE = "filemanager.file.bytes.size";
	/** Filename extension is not convertible to PDF. <b>Args:</b> {@code filename} */
	public static final String FILE_EXTENSION_NOT_CONVERTIBLE = "filemanager.file.extension.not.convertible";
	/** File byte content is not convertible to PDF. <b>Args:</b> {@code filename} */
	public static final String FILE_CONTENT_NOT_CONVERTIBLE = "filemanager.file.content.not.convertible";

	/** File extension doesn't match detected type. <b>Args:</b> {@code filename, detectedSubtype, fileExtension} */
	public static final String FILE_EXTENSION_CONTENT_MISMATCH = "filemanager.file.extension.content.mismatch";
	/** There is uncertainty about the MIME type based on content and file extension. <b>Args:</b> {@code filename, fileExtension} */
	public static final String FILE_TYPE_UNVERIFIABLE = "filemanager.file.type.unverifiable";

	/** PDF content is invalid. <b>Args:</b> {@code filename} */
	public static final String PDF_CONTENT_INVALID = "filemanager.pdf.content.invalid";
	/** The PDF is locked with Adobe encryption. <b>Args:</b> {@code filename} */
	public static final String PDF_LOCKED = "filemanager.pdf.locked";
	/** The PDF has been tampered with. <b>Args:</b> {@code filename} */
	public static final String PDF_TAMPERED = "filemanager.pdf.tampered";
	/** PDF is unreadable. <b>Args:</b> {@code filename, reason (corrupt, tampered with, etc)} */
	public static final String PDF_UNREADABLE = "The file {0} cannot be read. The file is {1}.";

	/** The image is not convertible for PDF. <b>Args:</b> {@code filename, itextErrorMessage} */
	public static final String IMAGE_ITEXT_NOT_CONVERTIBLE = "filemanager.pdf.image.not.consumable";
	/** Problem with internal (iText) processing during conversion. <b>Args:</b> {@code filename, itextErrorMessage} */
	public static final String PDF_CONVERSION_PROCESSING = "filemanager.pdf.conversion.processing";
	/** Problem with internal (iText) processing during stamping. <b>Args:</b> {@code filename, itextErrorMessage} */
	public static final String PDF_STAMPING = "filemanager.pdf.stamping";
	/** Internal PDF processing issue. <b>Args:</b> {@code itextErrorMessage} */
	public static final String PDF_ISSUE = "filemanager.pdf.internal.issue";
	/** Internal FileManager issues that cannot be resolved at runtime. No args. */
	public static final String FILEMANAGER_ISSUE = "filemanager.internal.issue";

}
