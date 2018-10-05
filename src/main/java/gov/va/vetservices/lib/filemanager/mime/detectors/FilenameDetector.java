package gov.va.vetservices.lib.filemanager.mime.detectors;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Properties;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import gov.va.ascent.framework.log.AscentLogger;
import gov.va.ascent.framework.log.AscentLoggerFactory;
import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.util.MessageUtils;

/**
 * Detect MIME type based on the file name (specifically, the extension).
 *
 * @author aburkholder
 */
@Component(FilenameDetector.BEAN_NAME)
public class FilenameDetector extends AbstractDetector {

	private static final AscentLogger LOGGER = AscentLoggerFactory.getLogger(FilenameDetector.class);

	public static final String BEAN_NAME = "filenameDetector";

	private static final String PROPS_CLASSPATH = "mimetypes-by-extension.properties";

	private static Properties props;

	@Autowired
	@Qualifier(MessageUtils.BEAN_NAME)
	private MessageUtils messageUtils;

	/**
	 * Instantiate the detector. This constructor retrieves a full list of MIME types from {@value #PROPS_CLASSPATH}.
	 */
	public FilenameDetector() {
		if (props == null) {
			getProps();
		}
	}

	/**
	 * Load properties from {@value #PROPS_CLASSPATH} from the classpath.
	 *
	 * @throws IllegalArgumentException if the properties file cannot be found
	 */
	private static final synchronized void getProps() {
		if (props != null) {
			return;
		}
		try {
			props = new Properties();

			final Resource resource = new PathMatchingResourcePatternResolver().getResources("classpath*:" + PROPS_CLASSPATH)[0];
			if (resource == null) {
				throw new IOException(PROPS_CLASSPATH + " does not exist on the classpath.");
			}
			InputStream is = null;
			try {
				is = resource.getInputStream();
				if (is == null) {
					throw new IOException(PROPS_CLASSPATH + " cannot be opened.");
				}
				props.load(is);
			} finally {
				IOUtils.closeQuietly(is);
			}

		} catch (final IOException e) {
			final String message =
					"FATAL ERROR: " + e.getClass().getSimpleName() + " - could not load classpath file '" + PROPS_CLASSPATH
							+ "': " + e.getMessage();
			LOGGER.error(message);
			throw new IllegalArgumentException(message, e);
		}
	}

	/**
	 * Derive the mimetype solely from the filename.
	 * The {@code bytes} parameter is ignored by this detector.
	 *
	 * @throws FileManagerException if MIME type cannot be derived
	 */
	@Override
	public MimeType detect(final byte[] bytes, final FilePartsDto parts) throws FileManagerException {
		MimeType mimetype = null;

		mimetype = detectWithExtension(parts);
		if (mimetype == null) {
			throw new FileManagerException(MessageSeverity.ERROR, LibFileManagerMessageKeys.FILE_TYPE_UNVERIFIABLE,
					messageUtils.returnMessage(LibFileManagerMessageKeys.FILE_TYPE_UNVERIFIABLE,
							parts, "UNKNOWN"));
		}

		return mimetype;
	}

	/**
	 * Uses the filename extension to derive the MIME type.
	 * If MIME type cannot be determined, {@code null} is returned.
	 *
	 * @param parts the file extension from which the MIME type is derived
	 * @throws NullPointerException if parts is {@code null} or parts.getExtension() returns {@code null}
	 */
	protected MimeType detectWithExtension(final FilePartsDto parts) {
		MimeType mimetype = null;

		if (parts != null && !StringUtils.isBlank(parts.getExtension())) {
			mimetype = ConvertibleTypesEnum.getMimeTypeForExtension(parts.getExtension());
			if (mimetype == null) {
				mimetype = detectNonConvertibleTypes(parts);
			}
		}

		return mimetype;
	}

	/**
	 * Try to detect any MIME type, whether it is convertible or not. If not, {@code null} is returned.
	 *
	 * @param parts the file extension from which the MIME type is derived
	 * @return MimeType the derived MIME type, or {@code null}
	 */
	private MimeType detectNonConvertibleTypes(final FilePartsDto parts) {
		MimeType mimetype = null;

		final String filename = parts.getName() + SEPARATOR + parts.getExtension();
		String rawtype = URLConnection.guessContentTypeFromName(filename);
		if (StringUtils.isBlank(rawtype)) {
			rawtype = props.getProperty(parts.getExtension().toLowerCase());
		}

		if (rawtype != null) {
			try {
				mimetype = new MimeType(rawtype);
				// CHECKSTYLE:OFF
			} catch (final MimeTypeParseException e) { // NOSONAR - intentionally do not re-throw
				LOGGER.debug("Could not parse raw MIME type '" + rawtype + "'");
				// intentionally ignore this, just let mimetype be null
			}
			// CHECKSTYLE:ON
		}

		return mimetype;
	}

}
