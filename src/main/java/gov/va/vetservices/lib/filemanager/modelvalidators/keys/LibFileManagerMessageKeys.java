package gov.va.vetservices.lib.filemanager.modelvalidators.keys;

/**
 * Project message keys for jsr303 custom validators
 *
 * @author Vanapalliv
 */
// Using interface so it works with the error key generator for display
@java.lang.SuppressWarnings("squid:S1214")
// CHECKSTYLE:OFF
public interface LibFileManagerMessageKeys {
	// CHECKSTYLE:OFF
	// NOSONAR

	public static final String UNEXPECTED_ERROR = "filemanager.unexpected.error";
	public static final String REQUEST_NULL = "filemanager.request.null";
	public static final String FILE_DTO_NULL = "filemanager.file.dto.null";
	public static final String PROCESSTYPE_NOT_SPECIFIED = "filemanager.process.type.not.specified";
	public static final String DOCTYPEID_NULL_OR_EMPTY = "filemanager.doc.type.id.null.or.empty";
	
	public static final String CLAIMID_NULL_OR_EMPTY = "filemanager.claim.id.null.or.empty";
	public static final String FILE_NAME_NULL_OR_EMPTY = "filemanager.file.name.null.or.empty";
	public static final String FILE_NAME_MALFORMED = "filemanager.file.name.malformed";
	public static final String FILE_NAME_ILLEGAL = "filemanager.file.name.illegal";
	public static final String FILE_NAME_TOO_LONG = "filemanager.file.name.length";
	
	public static final String FILE_BYTES_NULL_OR_EMPTY = "filemanager.file.bytes.null.or.empty";
	public static final String FILE_BYTES_UNREADABLE = "filemanager.file.bytes.unreadable";
	public static final String FILE_BYTES_SIZE = "filemanager.file.bytes.size";
	public static final String FILE_EXTENSION_NOT_CONVERTIBLE = "filemanager.file.extension.not.convertible";
	public static final String FILE_CONTENT_NOT_CONVERTIBLE = "filemanager.file.content.not.convertible";
	
	public static final String FILE_EXTENSION_CONTENT_MISMATCH = "filemanager.file.extension.content.mismatch";
	public static final String FILE_TYPE_UNVERIFIABLE = "filemanager.file.type.unverifiable";
	public static final String PDF_CONTENT_INVALID = "filemanager.pdf.content.invalid";
	public static final String PDF_LOCKED = "filemanager.pdf.locked";
	public static final String PDF_TAMPERED = "filemanager.pdf.tampered";
	
	public static final String PDF_UNREADABLE = "filemanager.pdf.unreadable";
	public static final String IMAGE_ITEXT_NOT_CONVERTIBLE = "filemanager.pdf.image.not.consumable";
	public static final String PDF_CONVERSION_PROCESSING = "filemanager.pdf.conversion.processing";
	public static final String PDF_STAMPING = "filemanager.pdf.stamping";
	public static final String FILEMANAGER_ISSUE = "filemanager.internal.issue";
	public static final String PDF_ISSUE = "filemanager.pdf.internal.issue";
	
}
