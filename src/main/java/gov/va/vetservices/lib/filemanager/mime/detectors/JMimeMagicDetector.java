package gov.va.vetservices.lib.filemanager.mime.detectors;

import java.io.IOException;
import java.text.MessageFormat;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

/**
 * Use JMimeMagic detection capabilities to attempt mime type detection.
 *
 * @author aburkholder
 */
public class JMimeMagicDetector extends AbstractDetector {

	/* Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(JMimeMagicDetector.class);

	@Override
	public MimeType detect(byte[] bytes, FilePartsDto parts) throws FileManagerException {
		MimeType mimetype = null;

		if (bytes == null) {
			MessageKeysEnum msg = MessageKeysEnum.FILE_BYTES_NULL_OR_EMPTY;
			throw new FileManagerException(MessageSeverity.ERROR, msg.getKey(), msg.getMessage());
		}
		if (parts == null) {
			MessageKeysEnum msg = MessageKeysEnum.FILE_NAME_NULL_OR_EMPTY;
			throw new FileManagerException(MessageSeverity.ERROR, msg.getKey(), msg.getMessage());
		}

		try {
			mimetype = detectByMagic(bytes);
			mimetype = fixKnownFlaws(mimetype, parts.getExtension());

		} catch (IOException e) { // NOSONAR - sonar doesn't see the exception being thrown
			MessageKeysEnum msg = MessageKeysEnum.FILE_BYTES_UNREADABLE;
			String filename = parts.getName() + SEPARATOR + parts.getExtension();
			LOGGER.error(msg.getKey() + ": " + MessageFormat.format(msg.getMessage(), filename));
			throw new FileManagerException(MessageSeverity.ERROR, msg.getKey(), msg.getMessage(), filename);

		} catch (MimeTypeParseException e) { // NOSONAR - sonar doesn't see the exception being thrown
			String filename = parts.getName() + SEPARATOR + parts.getExtension();
			MessageKeysEnum msg = MessageKeysEnum.FILE_CONTENT_NOT_CONVERTIBLE;
			LOGGER.error(msg.getKey() + ": " + MessageFormat.format(msg.getMessage(), filename));
			throw new FileManagerException(MessageSeverity.ERROR, msg.getKey(), msg.getMessage(), filename);
		}

		return mimetype;
	}

	/**
	 * Let JMimeMagic try to detect the MIME type with no knowledge of the filename.
	 * <p>
	 * If JMimeMagic cannot decisively determine the MIME type,
	 * it may throw an IOException or return {@code null}.<br/>
	 * JMimeMagic also has a long-standing bug wherein it mistakenly reports
	 * BMP files as {@code text/plain}.
	 *
	 * @param bytes the file content byte array
	 * @return MimeType the detected MimeType or {@code null}
	 * @throws MimeTypeParseException could not convert MediaType to MimeType
	 * @throws IOException the detector could not read the byte array
	 */
	protected MimeType detectByMagic(final byte[] bytes) throws IOException, MimeTypeParseException {
		MimeType mimetype = null;
		try {
			final MagicMatch match = Magic.getMagicMatch(bytes);
			if (match != null) {
				mimetype = new MimeType(match.getMimeType());
			}
		} catch (final MagicParseException | MagicMatchNotFoundException | MagicException e) {
			throw new IOException(
					"Internal jmimemagic " + e.getClass().getSimpleName() + " while parsing file bytes: " + e.getMessage(), e);
		}
		return mimetype;
	}

	/**
	 * Fix known flaws with jMimeMagic detections. The {@code filemanagerDerived} parameter must not be {@code null}.
	 * <p>
	 * When jMimeMagic is not confident about its magic detection, it will default to text/plain.
	 * For some reason, jMimeMagic will not detect BMP images types, so it will always return text/plain for them.
	 * <p>
	 * If jMimeMagic detects text/plain AND the filename extension is bmp, we will assume this
	 * is a BMP image file.
	 *
	 * @param fromBytes the MIME type detected by magic
	 * @param fileExtension the filename
	 * @return String the fixed jMimeMagic MIME type
	 */
	protected MimeType fixKnownFlaws(MimeType fromBytes, String fileExtension) {
		MimeType fixed = fromBytes;

		if ((fromBytes != null) && !StringUtils.isBlank(fileExtension)
				&& StringUtils.equalsIgnoreCase(fileExtension, ConvertibleTypesEnum.BMP.getExtension())
				&& ConvertibleTypesEnum.TXT.getMimeType().match(fromBytes)) {

			fixed = ConvertibleTypesEnum.BMP.getMimeType();
		}

		return fixed;
	}

}
