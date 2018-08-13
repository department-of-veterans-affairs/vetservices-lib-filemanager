package gov.va.vetservices.lib.filemanager.mime.detectors;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.IOUtils;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.ascent.framework.util.SanitizationUtil;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;
import gov.va.vetservices.lib.filemanager.util.MessageUtils;

/**
 * Use Apache Tika detection capabilities to attempt mime type detection.
 *
 * @author aburkholder
 */
@Component(TikaDetector.BEAN_NAME)
public class TikaDetector extends AbstractDetector {

	/* Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(TikaDetector.class);
	
	public static final String BEAN_NAME = "tikaDetector";

	/** Classpath to the Tika Config file */
	protected static final String TIKA_CONFIG = "/tika-config.xml";
	/** Default (raw) MIME type returned by Tika */
	protected static final String MIME_RAW_OCTECT_STREAM = "application/octet-stream";
	/** Tika Config, contains the detector resources */
	private TikaConfig tikaConfig;
	
	/** Auto wire message utilities */
	@Autowired
	@Qualifier(MessageUtils.BEAN_NAME)
	private MessageUtils messageUtils;

	/**
	 * Instantiate the Tika with {@value #TIKA_CONFIG}.
	 * Can throw runtime exception if classpath file is not found
	 */
	public TikaDetector() {
		getTikaConfig(TIKA_CONFIG);
	}

	/**
	 * Load configuration from the specified tika-config.xml.
	 *
	 * @param path the path (on the classpath) to the config file
	 * @throws IllegalArgumentException could not locate or load the specified file
	 */
	private void getTikaConfig(final String path) {
		if (StringUtils.isBlank(path)) {
			throw new IllegalArgumentException("Configuration file cannot be null or empty.");
		}

		InputStream is = null;
		try {
			final Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:" + path);
			if (resources == null || resources.length < 1) {
				throw new IllegalArgumentException("Configuration file '" + path + "' not found.");
			} else if (resources.length > 1) {
				throw new IllegalArgumentException("Multiple configuration files found at '" + path + "'.");
			}
			final Resource resource = resources[0];
			is = resource.getInputStream();
			tikaConfig = new TikaConfig(is);
		} catch (TikaException | IOException | SAXException e) {
			final String message = "FATAL ERROR: " + e.getClass().getSimpleName() + " - could not load classpath file '" + path + "': "
					+ e.getMessage();
			LOGGER.error(message);
			throw new IllegalArgumentException(message, e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	@Override
	public MimeType detect(final byte[] bytes, final FilePartsDto parts) throws FileManagerException {
		MimeType mimetype = null;

		if (bytes == null) {
			final String key = LibFileManagerMessageKeys.FILE_BYTES_NULL_OR_EMPTY;
			throw new FileManagerException(MessageSeverity.ERROR, key, messageUtils.returnMessage(key));
		}
		if (parts == null) {
			final String key = LibFileManagerMessageKeys.FILE_NAME_NULL_OR_EMPTY;
			throw new FileManagerException(MessageSeverity.ERROR, key, messageUtils.returnMessage(key));
		}

		final String filename = parts.getName() + SEPARATOR + parts.getExtension();
		try {
			final MimeType withMagic = detectByMagic(bytes);
			final MimeType withHint = detectWithFilename(bytes, filename);
			mimetype = selfCheck(withMagic, withHint);

			mimetype = fixKnownFlaws(mimetype, parts);

		} catch (final IOException e) { // NOSONAR - sonar doesn't see the exception being thrown
			LOGGER.debug("File " + filename + " is unreadable.");
			final String key = LibFileManagerMessageKeys.FILE_BYTES_UNREADABLE;
			final String safeName = SanitizationUtil.safeFilename(filename);
			LOGGER.error(key + ": " + MessageFormat.format(messageUtils.returnMessage(key), safeName));
			throw new FileManagerException(MessageSeverity.ERROR, key, messageUtils.returnMessage(key), safeName);

		} catch (final MimeTypeParseException e) { // NOSONAR - sonar doesn't see the exception being thrown
			LOGGER.debug("MIME type '" + mimetype + "' cannot be converted to PDF.");
			final String key = LibFileManagerMessageKeys.FILE_CONTENT_NOT_CONVERTIBLE;
			final String safeName = SanitizationUtil.safeFilename(filename);
			LOGGER.error(key + ": " + MessageFormat.format(messageUtils.returnMessage(key), safeName));
			throw new FileManagerException(MessageSeverity.ERROR, key, messageUtils.returnMessage(key), safeName);
		}

		return mimetype;
	}

	/**
	 * Let Tika try to detect the MIME type with no knowledge of the filename.
	 * <p>
	 * If Tika cannot decisively determine the MIME type,
	 * it will return {@code application/octet-stream} as its default type.<br/>
	 * For example, ASCII text files containing control characters and/or
	 * Extended ASCII characters will likely be considered unidentifiable
	 * because the file content provide no hints if they bytes are binary or not.
	 *
	 * @param bytes the file content byte array
	 * @return MimeType the detected MimeType or {@code null}
	 * @throws MimeTypeParseException could not convert MediaType to MimeType
	 * @throws IOException the detector could not read the byte array
	 */
	protected MimeType detectByMagic(final byte[] bytes) throws IOException, MimeTypeParseException {
		MimeType mimetype = null;

		if (FileManagerUtils.hasBytes(bytes)) {
			MediaType mediatypeMagic = null;

			final MimeTypes mimeRegistry = tikaConfig.getMimeRepository();

			// detect by magic
			mediatypeMagic = mimeRegistry.detect(TikaInputStream.get(bytes), new Metadata());
			if (mediatypeMagic != null) {
				mimetype = new MimeType(mediatypeMagic.getBaseType().toString());
			}
		}

		return mimetype;
	}

	/**
	 * Let Tika try to detect the MIME type with no knowledge of the filename.
	 * <p>
	 * If Tika cannot decisively determine the MIME type,
	 * it will return {@code application/octet-stream} as its default type.<br/>
	 * For example, ASCII text files containing control characters and/or
	 * Extended ASCII characters will likely be considered unidentifiable
	 * because the file content provide no hints if they bytes are binary or not.
	 * Even with the file name hints, Tika has to make a judgment call on which to believe.
	 *
	 * @param bytes the file content byte array
	 * @param filename the filename
	 * @return MimeType the detected MimeType or {@code null}
	 * @throws MimeTypeParseException could not convert MediaType to MimeType
	 * @throws IOException the detector could not read the byte array
	 */
	protected MimeType detectWithFilename(final byte[] bytes, final String filename) throws IOException, MimeTypeParseException {
		MimeType mimetype = null;

		if (FileManagerUtils.hasBytes(bytes)) {
			final MimeTypes mimeRegistry = tikaConfig.getMimeRepository();

			// detect with filename hint
			final Metadata metadata = new Metadata();
			metadata.set(TikaCoreProperties.ORIGINAL_RESOURCE_NAME, filename);

			final MediaType mediatype = mimeRegistry.detect(TikaInputStream.get(bytes), metadata);
			if (mediatype != null) {
				mimetype = new MimeType(mediatype.getBaseType().toString());
			}
		}

		return mimetype;
	}

	/**
	 * Fix known flaws with Tika detections. The {@code filename} parameter must not be {@code null}.
	 * <p>
	 * When Tika is not confident about its magic detection, it will default to octet-stream.
	 * Octet-stream is commonly returned for text files that have extended ASCII characters or
	 * control characters in them.
	 * <p>
	 * If Tika detects octet-stream AND the filename extension is txt, we will assume this
	 * is a text file with unusual ASCII characters in it.
	 *
	 * @param fromBytes the MIME type detected by magic
	 * @param filename the filename
	 * @return MimeType the fixed Tika detected MIME type
	 */
	protected MimeType fixKnownFlaws(final MimeType fromBytes, final FilePartsDto parts) {
		MimeType fixed = fromBytes;

		// sonar cannot be made happy here - it complains about too many arguments in the if statement,
		// and splitting it into 2 if statements, it says to merge them into one statement again
		// ... and it is pointless to create another method just to put the other half of the if statement
		if (fromBytes != null && parts != null && !StringUtils.isBlank(parts.getExtension()) // NOSONAR
				&& MIME_RAW_OCTECT_STREAM.equals(fromBytes.getBaseType()) // NOSONAR
				&& StringUtils.equalsIgnoreCase(parts.getExtension(), ConvertibleTypesEnum.TXT.getExtension())) { // NOSONAR

			fixed = ConvertibleTypesEnum.TXT.getMimeType();
		}

		return fixed;
	}
}
