package gov.va.vetservices.lib.filemanager.mime.detectors;

import java.io.IOException;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.ascent.framework.util.SanitizationUtil;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.util.MessageUtils;
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
@Component(JMimeMagicDetector.BEAN_NAME)
public class JMimeMagicDetector extends AbstractDetector {

	/* Logger */
	private static final AscentLogger LOGGER = AscentLoggerFactory.getLogger(JMimeMagicDetector.class);

	public static final String BEAN_NAME = "jMimeMagicDetector";

	/** Auto wire message utilities */
	@Autowired
	@Qualifier(MessageUtils.BEAN_NAME)
	private MessageUtils messageUtils;

	@Override
	public MimeType detect(final byte[] bytes, final FilePartsDto parts)  {
		MimeType mimetype = null;

		if (bytes == null) {
			final String key = LibFileManagerMessageKeys.FILE_BYTES_NULL_OR_EMPTY;
			throw new FileManagerException(MessageSeverity.ERROR, key, messageUtils.returnMessage(key));
		}
		if (parts == null) {
			final String key = LibFileManagerMessageKeys.FILE_NAME_NULL_OR_EMPTY;
			throw new FileManagerException(MessageSeverity.ERROR, key, messageUtils.returnMessage(key));
		}

		try {
			mimetype = detectByMagic(bytes);
			mimetype = fixKnownFlaws(mimetype, parts.getExtension());

		} catch (final IOException e) { // NOSONAR - sonar doesn't see the exception being thrown
			final String key = LibFileManagerMessageKeys.FILE_BYTES_UNREADABLE;
			final String filename = SanitizationUtil.safeFilename(parts.getName() + SEPARATOR + parts.getExtension());
			LOGGER.error(key + ": " + messageUtils.returnMessage(key, filename));
			throw new FileManagerException(MessageSeverity.ERROR, key, messageUtils.returnMessage(key), filename);

		} catch (final MimeTypeParseException e) { // NOSONAR - sonar doesn't see the exception being thrown
			final String filename = SanitizationUtil.safeFilename(parts.getName() + SEPARATOR + parts.getExtension());
			final String key = LibFileManagerMessageKeys.FILE_CONTENT_NOT_CONVERTIBLE;
			LOGGER.error(key + ": " + messageUtils.returnMessage(key, filename));
			throw new FileManagerException(MessageSeverity.ERROR, key, messageUtils.returnMessage(key), filename);
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
	protected MimeType fixKnownFlaws(final MimeType fromBytes, final String fileExtension) {
		MimeType fixed = fromBytes;

		if (fromBytes != null && !StringUtils.isBlank(fileExtension)
				&& StringUtils.equalsIgnoreCase(fileExtension, ConvertibleTypesEnum.BMP.getExtension())
				&& ConvertibleTypesEnum.TXT.getMimeType().match(fromBytes)) {

			fixed = ConvertibleTypesEnum.BMP.getMimeType();
		}

		return fixed;
	}

}
