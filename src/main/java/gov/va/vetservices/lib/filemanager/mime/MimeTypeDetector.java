package gov.va.vetservices.lib.filemanager.mime;

import java.text.MessageFormat;

import javax.activation.MimeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.mime.detectors.AbstractDetector;
import gov.va.vetservices.lib.filemanager.mime.detectors.FilenameDetector;
import gov.va.vetservices.lib.filemanager.mime.detectors.JMimeMagicDetector;
import gov.va.vetservices.lib.filemanager.mime.detectors.TikaDetector;

/**
 * Uses jMimeMagic to attempt detection of the MIME type of a byte array.
 *
 * @author aburkholder
 */
public class MimeTypeDetector {

	private static final Logger LOGGER = LoggerFactory.getLogger(MimeTypeDetector.class);

	private final FilenameDetector filenameDetector = new FilenameDetector();
	private final TikaDetector tikaDetector = new TikaDetector();
	private final JMimeMagicDetector jmimemagicDetector = new JMimeMagicDetector();

	// dev note: these two variables are candidates to externally expose in FileMangerProperties
	/** Determines if the TikaDetector should be operational (true) or not (false) */
	private boolean enableTika = true;
	/** Determines if the JMimeMagicDetector should be operational (true) or not (false) */
	private boolean enableJMimeMagic = false;

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
	 * Uses Tika and jMimeMagic to attempt detection of the MIME type of a byte array.
	 * The detected MIME type must match the MIME type that is derived from the file extension.
	 * <p>
	 * Returns {@code null} if an error occurred while parsing the byte array or the detected MIME type,
	 * or a new uninitialized MimeType if the type could not be detected.
	 *
	 * @param bytes the byte array to inspect
	 * @param parts the file definition info
	 * @return MimeType null, uninitialized MimeType, or the detected MimeType
	 * @throws FileManagerException mismatch between detected file bytes and the filename extension
	 */
	public MimeType detectMimeType(byte[] bytes, FilePartsDto parts) throws FileManagerException {

		MimeType bestGuess = null;

		// derived from filename extension, or null
		MimeType filenameDerived = filenameDetector.detect(bytes, parts);

		MimeType tikaDetected = null;
		MimeType jmimeDetected = null;

		if (enableTika) {
			// detected from bytes, and from bytes + filename hint
			tikaDetected = tikaDetector.detect(bytes, parts);
		}

		if (enableJMimeMagic) {
			// detected from bytes
			jmimeDetected = jmimemagicDetector.detect(bytes, parts);
		}

		// make a best guess
		bestGuess = AbstractDetector.selfCheck(tikaDetected, jmimeDetected);
		if (bestGuess == null) {
			// both tika and jmimemagic are turned off
			throw new FileManagerException(MessageSeverity.ERROR, MessageKeysEnum.FILE_TYPE_UNVERIFIABLE.getKey(),
					MessageFormat.format(MessageKeysEnum.FILE_TYPE_UNVERIFIABLE.getMessage(), parts, parts.getExtension()));
		}

		// throw exception if filename extension is different than the detected type
		checkContentVsExtension(bestGuess, filenameDerived, parts);

		// throw exception if type is not supported
		checkUnsupportedType(bestGuess, parts);

		return bestGuess;
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
	private void checkContentVsExtension(MimeType detectedType, MimeType derivedtype, FilePartsDto parts) throws FileManagerException {
		if (!derivedtype.match(detectedType)) {
			String filename = parts.getName() + AbstractDetector.SEPARATOR + parts.getExtension();
			LOGGER.error("MIME type detection mismatch. File: " + filename + "; Type by filename extension: " + derivedtype
					+ "; Type by magic byte detection: " + detectedType);
			throw new FileManagerException(MessageSeverity.ERROR, MessageKeysEnum.FILE_EXTENSION_CONTENT_MISMATCH.getKey(),
					MessageKeysEnum.FILE_EXTENSION_CONTENT_MISMATCH.getMessage(), filename, detectedType.getBaseType(),
					derivedtype.getBaseType());
		}
	}

	/**
	 * Throw an exception if the detected mimetype is not supported.
	 *
	 * @param mimetype the detected MimeType
	 * @throws FileManagerException unsupported type
	 */
	private void checkUnsupportedType(MimeType mimetype, FilePartsDto parts) throws FileManagerException {
		if (!ConvertibleTypesEnum.hasMimeType(mimetype)) {
			String filename = parts.getName() + AbstractDetector.SEPARATOR + parts.getExtension();
			LOGGER.error("Files of type " + mimetype + " are not supported. ");
			throw new FileManagerException(MessageSeverity.ERROR, MessageKeysEnum.FILE_EXTENSION_NOT_CONVERTIBLE.getKey(),
					MessageKeysEnum.FILE_EXTENSION_NOT_CONVERTIBLE.getMessage(), filename);
		}
	}

	/**
	 * Determines if the TikaDetector should be operational (default: true)
	 * 
	 * @return the enableTika
	 */
	public boolean isEnableTika() {
		return enableTika;
	}

	/**
	 * Determines if the TikaDetector should be operational (default: true)
	 * 
	 * @param enableTika the enableTika to set
	 */
	public void setEnableTika(boolean enableTika) {
		this.enableTika = enableTika;
	}

	/**
	 * Determines if the JMimeMagicDetector should be operational (default: false)
	 * 
	 * @return the enableJMimeMagic
	 */
	public boolean isEnableJMimeMagic() {
		return enableJMimeMagic;
	}

	/**
	 * Determines if the JMimeMagicDetector should be operational (default: false)
	 * 
	 * @param enableJMimeMagic the enableJMimeMagic to set
	 */
	public void setEnableJMimeMagic(boolean enableJMimeMagic) {
		this.enableJMimeMagic = enableJMimeMagic;
	}
}
