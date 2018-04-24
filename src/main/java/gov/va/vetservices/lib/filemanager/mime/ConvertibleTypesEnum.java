package gov.va.vetservices.lib.filemanager.mime;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enumeration of MimeTypes, and their associated file extensions, that can be converted to PDF.
 *
 * @author aburkholder
 */
//Sonar raises a major violation for exceptions that are not logged - even though they are clearly logged.
@java.lang.SuppressWarnings("squid:S1166")
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

	/** The filename extension */
	protected String extension;
	/** The raw mimetype "type/subtype" */
	protected String mimetype;

	/**
	 * Enumeration constructor.
	 *
	 * @param extension the file extension
	 * @param mimetype the raw "type/subtype" mime type
	 */
	ConvertibleTypesEnum(String extension, String mimetype) {
		this.extension = extension;
		this.mimetype = mimetype;
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
	 * @return MimeType
	 */
	public MimeType getMimeType() {
		MimeType mt = null;
		try {
			mt = new MimeType(this.mimetype);
		} catch (MimeTypeParseException e) { // squid:S1166
			// should not happen, log it
			LOGGER.error("Unexpected issue parsing " + this.mimetype + " into a MimeType object.", e);
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

		if (!StringUtils.isBlank(extension)) {
			for (ConvertibleTypesEnum enumeration : ConvertibleTypesEnum.values()) {
				if (StringUtils.equalsIgnoreCase(enumeration.getExtension(), extension)) {
					mt = enumeration.getMimeType();
					break;
				}
			}
		}

		return mt;
	}

	/**
	 * Check to see if a MimeType is supported.
	 *
	 * @param mimetype the MimeType to look for
	 * @return boolean has the MimeType ({@code true}) or not ({@code false})
	 */
	public static boolean hasMimeType(String mimetype) {
		try {
			MimeType mt = new MimeType(mimetype);
			return hasMimeType(mt);
		} catch (MimeTypeParseException e) { // squid:S1166
			LOGGER.debug("Unexpected issue parsing " + mimetype + " into a MimeType object.", e);
			return false;
		}
	}

	/**
	 * Check to see if a MimeType is supported.
	 *
	 * @param mimetype the MimeType to look for
	 * @return boolean has the MimeType ({@code true}) or not ({@code false})
	 */
	public static boolean hasMimeType(MimeType mimetype) {
		boolean matched = false;

		for (ConvertibleTypesEnum enumeration : ConvertibleTypesEnum.values()) {
			if (enumeration.getMimeType().match(mimetype)) {
				matched = true;
				break;
			}
		}

		return matched;
	}
}
