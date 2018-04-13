package gov.va.vetservices.lib.filemanager.mime;

import java.util.Arrays;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

/**
 * Uses jMimeMagic to attempt detection of the MIME type of a byte array.
 *
 * @author aburkholder
 */
public class MimeTypeDetector {

	private static final Logger LOGGER = LoggerFactory.getLogger(MimeTypeDetector.class);

	/**
	 * Do not instantiate
	 */
	MimeTypeDetector() {
		throw new IllegalAccessError("MimeTypeDetector is a static class. Do not instantiate it.");
	}

	/**
	 * <p>
	 * Uses jMimeMagic to attempt detection of the MIME type of a byte array.
	 * <p>
	 * Returns {@code null} if an error occurred while parsing the byte array or the detected MIME type,
	 * or a new uninitialized MimeType if the type could not be detected.
	 *
	 * @param bytes the byte array to inspect
	 * @return MimeType null, uninitialized MimeType, or the detected MimeType
	 */
	public static final MimeType detectMimeType(byte[] bytes) {

		MimeType mimetype = null;
		String mimetypeAsString = null;
		try {
			if ((bytes != null) && (bytes.length > 0)) {
				final MagicMatch match = Magic.getMagicMatch(bytes);
				mimetypeAsString = match.getMimeType();
			}
		} catch (final MagicParseException | MagicMatchNotFoundException | MagicException e) {
			LOGGER.error("Internal jmimemagic " + e.getClass().getSimpleName() + " while parsing file bytes: " + e.getMessage(), e);
		}

		// Return detected mime type
		if (null != mimetypeAsString) {
			try {
				mimetype = new MimeType(mimetypeAsString);
			} catch (final MimeTypeParseException e) {
				LOGGER.error("Error parsing jmimemagic mime type: " + e.getMessage(), e);
			}
		} else {
			mimetype = new MimeType();
		}
		return mimetype;
	}

	public static final MimeType deriveMimeType(String fileExtension) {
		return ConvertibleTypesEnum.getMimeTypeForExtension(fileExtension);
	}

	public static boolean isFileExtensionSupported(String fileExtension) {
		return Arrays.asList(FileManagerProperties.CONVERTIBLE_FILE_EXTENSIONS).contains(fileExtension.toLowerCase());
	}

}
