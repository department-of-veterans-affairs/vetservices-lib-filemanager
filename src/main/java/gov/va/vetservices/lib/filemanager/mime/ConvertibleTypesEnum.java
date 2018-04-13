package gov.va.vetservices.lib.filemanager.mime;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enumeration of MimeTypes, and their associated file extensions, that can be converted to PDF.
 *
 * @author aburkholder
 */
public enum ConvertibleTypesEnum {

	/** Type for extension "bmp": image/bmp */
	BMP("bmp", "image/bmp"),
	/** Type for extension "gif": image/gif */
	GIF("gif", "image/gif"),
	/** Type for extension "jpeg": image/jpeg */
	JPEG("jpeg", "image/jpeg"),
	/** Type for extension "jpg": image/jpeg */
	JPG("jpg", "image/jpeg"),
	/** Type for extension "pdf": application/pdf */
	PDF("pdf", "application/pdf"),
	/** Type for extension "png": image/png */
	PNG("png", "image/png"),
	/** Type for extension "tif": image/tiff */
	TIF("tif", "image/tiff"),
	/** Type for extension "tiff": image/tiff */
	TIFF("tiff", "image/tiff"),
	/** Type for extension "txt": text/plain */
	TXT("txt", "text/plain");

	private static final Logger LOGGER = LoggerFactory.getLogger(ConvertibleTypesEnum.class);

	protected String extension;
	protected String mimetype;

	/**
	 * Enumeration constructor.
	 *
	 * @param extension the file extension
	 * @param mimetype the raw "type/subtype" mime type
	 */
	ConvertibleTypesEnum(String extension, String mimetype) {
		this.extension = extension;
	}

	/**
	 * Get the file extension associated with the mime type.
	 *
	 * @return String the file extension
	 */
	public String getExtension() {
		return this.extension;
	}

	/**
	 * Get the mime type as a raw "type/subtype".
	 *
	 * @return String the raw mime type
	 */
	public String getMimeString() {
		return this.mimetype;
	}

	/**
	 * Get the mime type as a {@link MimeType} object.
	 *
	 * @return
	 */
	public MimeType getMimeType() {
		MimeType mt = null;
		try {
			mt = new MimeType(this.mimetype);
		} catch (MimeTypeParseException e) {
			// should not happen, log it
			LOGGER.error("Unexpected issue parsing " + this.mimetype + " into a MimeType object.");
		}
		return mt;
	}

	/**
	 * Get the {@link MimeType} for a given file extension.
	 * If the extension is not found in the ENUM, {@code null} is returned.
	 *
	 * @param extension the file extension
	 */
	public static MimeType getMimeTypeForExtension(String extension) {
		MimeType mt = null;

		for (ConvertibleTypesEnum enumeration : ConvertibleTypesEnum.values()) {
			if (enumeration.getExtension().equals(extension)) {
				mt = enumeration.getMimeType();
				break;
			}
		}

		return mt;
	}
}
