package gov.va.vetservices.lib.filemanager.mime;

import java.io.IOException;
import java.text.MessageFormat;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.mime.detectors.FileManagerDetector;
import gov.va.vetservices.lib.filemanager.mime.detectors.JMimeMagicDetector;
import gov.va.vetservices.lib.filemanager.mime.detectors.TikaDetector;

/**
 * Uses jMimeMagic to attempt detection of the MIME type of a byte array.
 *
 * @author aburkholder
 */
public class MimeTypeDetector {

	private static final Logger LOGGER = LoggerFactory.getLogger(MimeTypeDetector.class);

	private static final String MIME_RAW_OCTECT_STREAM = "application/octet-stream";

	private final FileManagerDetector fileManagerDetector = new FileManagerDetector();
	private final TikaDetector tikaDetector = new TikaDetector();
	private final JMimeMagicDetector jmimemagicDetector = new JMimeMagicDetector();

	/**
	 * Determine if a file extension is supported by FileManager.
	 *
	 * @param fileExtension the file extension
	 * @return boolean {@code false} if not supported
	 */
	public static boolean isFileExtensionSupported(String fileExtension) {
		return ConvertibleTypesEnum.getMimeTypeForExtension(fileExtension) != null;
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
	 * @throws FileManagerException mismatch between detected file bytes and the filename extension
	 */
	public MimeType detectMimeType(byte[] bytes, String filename) throws FileManagerException {

		MimeType mimetype = null;

		try {
			String tikaFixed = null;
			String jmimemagicFixed = null;
			String bestGuess = null;

			// derived from filename extension, or null
			String filemanagerDerived = fileManagerDetector.detectWithFilename(bytes, filename);
			if (filemanagerDerived == null) {
				throw new FileManagerException(MessageSeverity.ERROR, MessageKeys.FILE_TYPE_UNVERIFIABLE.getKey(),
						MessageFormat.format(MessageKeys.FILE_TYPE_UNVERIFIABLE.getMessage(), filename, "UNKNOWN"));
			}

			// detected from bytes, and from bytes + filename hint
			String tikaMagic = tikaDetector.detectByMagic(bytes);
			String tikaFilename = tikaDetector.detectWithFilename(bytes, filemanagerDerived);
			tikaFixed = selfCheck(tikaMagic, tikaFilename);
			tikaFixed = fixTikaFlaws(tikaFixed, filemanagerDerived);

			// detected from bytes, and from bytes + filename hint
			String jmimemagicMagic = jmimemagicDetector.detectByMagic(bytes);
			String jmimemagicFilename = jmimemagicDetector.detectWithFilename(bytes, tikaFilename);
			jmimemagicFixed = selfCheck(jmimemagicMagic, jmimemagicFilename);
			jmimemagicFixed = fixJmimemagicFlaws(jmimemagicFixed, filemanagerDerived);

			// make a best guess
			bestGuess = selfCheck(tikaFixed, jmimemagicFixed);
			if (bestGuess == null) {
				String fileExt = filename.contains(".") ? filename.split(".")[1] : "UNKNOWN";
				throw new FileManagerException(MessageSeverity.ERROR, MessageKeys.FILE_TYPE_UNVERIFIABLE.getKey(),
						MessageFormat.format(MessageKeys.FILE_TYPE_UNVERIFIABLE.getMessage(), filename, fileExt));
			}

			// throw exception if filename extension is different than the detected type
			checkContentVsExtension(bestGuess, filemanagerDerived, filename);

			// throw exception if type is not supported
			checkUnsupportedType(bestGuess, filename);

			mimetype = new MimeType(bestGuess);

		} catch (IOException | MimeTypeParseException e) {
			LOGGER.error(("MIME detection occurred while processing '" + filename + "' (" + bytes) == null ? "null"
					: bytes.length + " bytes)", e);
			return null;
		}

		return mimetype;
	}

	/**
	 * Do a check to see if both the magic and filename detectors returned the same value.
	 * If not, return the most likely value (weighted toward magic).
	 * If both arguments are {@code null}, then {@code null} is returned.
	 *
	 * @param withMagic the type detected by magic
	 * @param withFilename the type detected with filename help
	 * @return String the winning name, or {@code null} if both params are null
	 */
	private String selfCheck(String withMagic, String withFilename) {
		String winner = null;

		if ((withMagic == null) && (withFilename == null)) {
			// both are null, do nothing
		} else if ((withMagic == null) || (withFilename == null)) {
			winner = withMagic == null ? (withFilename == null ? null : withFilename) : withMagic;
		} else if (!withMagic.equals(withFilename)) {
			winner = withMagic;
		} else {
			winner = withMagic;
		}

		return winner;
	}

	/**
	 * Fix known flaws with Tika detections. The {@code filemanagerDerived} parameter must not be {@code null}.
	 *
	 * @param tika the mimetype detected by Tika
	 * @param filemanagerDerived the mimetype derived from the filename extension
	 * @return String the fixed Tika mimetype
	 */
	private String fixTikaFlaws(String tika, String filemanagerDerived) {
		String fixed = tika;

		if ((tika != null) && MIME_RAW_OCTECT_STREAM.equals(tika)
				&& StringUtils.equalsIgnoreCase(filemanagerDerived, ConvertibleTypesEnum.TXT.getMimeString())) {
			fixed = ConvertibleTypesEnum.TXT.mimetype;
		}

		return fixed;
	}

	/**
	 * Fix known flaws with jMimeMagic detections. The {@code filemanagerDerived} parameter must not be {@code null}.
	 *
	 * @param jmimemagic the mimetype detected by jMimeMagic
	 * @param filemanagerDerived the mimetype derived from the filename extension
	 * @return String the fixed jMimeMagic mimetype
	 */
	private String fixJmimemagicFlaws(String jmimemagic, String filemanagerDerived) {
		String fixed = jmimemagic;

		if ((filemanagerDerived != null) && ConvertibleTypesEnum.BMP.mimetype.equals(filemanagerDerived)
				&& StringUtils.endsWithIgnoreCase(jmimemagic, ConvertibleTypesEnum.TXT.extension)) {
			fixed = ConvertibleTypesEnum.BMP.mimetype;
		}

		return fixed;
	}

	/**
	 * Throw an exception if the filename extension does not match the detected mimetype.
	 * The {@code derivedtype} parameter must not be {@code null}.
	 *
	 * @param detectedType the raw MIME type detected by tika / jmimemagic
	 * @param extensionType the raw MIME type derived from the filename
	 * @param filename the original filename
	 * @throws FileManagerException mismatch between detected and derived types
	 */
	private void checkContentVsExtension(String detectedType, String derivedtype, String filename) throws FileManagerException {
		if (!derivedtype.equals(detectedType)) {
			String fileExt = derivedtype.contains("/") ? derivedtype.substring(derivedtype.indexOf("/") + 1) : derivedtype;
			String detectedSubtype =
					(detectedType != null) && detectedType.contains("/") ? detectedType.substring(detectedType.indexOf("/") + 1)
							: detectedType;

			LOGGER.error("MIME type detection mismatch. File: " + filename + "; Type by filename extension: " + derivedtype
					+ "; Type by magic byte detection: " + detectedType);
			throw new FileManagerException(MessageSeverity.ERROR, MessageKeys.FILE_EXTENSION_CONTENT_MISMATCH.getKey(),
					MessageKeys.FILE_EXTENSION_CONTENT_MISMATCH.getMessage(), filename, detectedSubtype, fileExt);
		}
	}

	/**
	 * Throw an exception if the detected mimetype is not supported.
	 *
	 * @param mimetype the detected MimeType
	 * @throws FileManagerException unsupported type
	 */
	private void checkUnsupportedType(String mimetype, String filename) throws FileManagerException {
		if (!ConvertibleTypesEnum.hasMimeType(mimetype)) {
			LOGGER.error("Files of type " + mimetype + " are not supported. ");
			throw new FileManagerException(MessageSeverity.ERROR, MessageKeys.FILE_EXTENSION_NOT_CONVERTIBLE.getKey(),
					MessageKeys.FILE_EXTENSION_NOT_CONVERTIBLE.getMessage(), filename);
		}
	}
}
