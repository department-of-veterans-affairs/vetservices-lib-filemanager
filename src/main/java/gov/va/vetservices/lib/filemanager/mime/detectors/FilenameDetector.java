package gov.va.vetservices.lib.filemanager.mime.detectors;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Properties;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;

public class FilenameDetector extends AbstractDetector {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilenameDetector.class);

	private static final String PROPS_CLASSPATH = "mimetypes-by-extension.properties";

	private static Properties props;

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

			Resource resource = (new PathMatchingResourcePatternResolver()).getResources("classpath*:" + PROPS_CLASSPATH)[0];
			if (resource == null) {
				throw new IOException(PROPS_CLASSPATH + " does not exist on the classpath.");
			}
			InputStream is = resource.getInputStream();
			if (is == null) {
				throw new IOException(PROPS_CLASSPATH + " cannot be opened.");
			}
			props.load(is);

		} catch (IOException e) {
			String message = "FATAL ERROR: " + e.getClass().getSimpleName() + " - could not load classpath file '" + PROPS_CLASSPATH
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
	public MimeType detect(byte[] bytes, FilePartsDto parts) throws FileManagerException {
		MimeType mimetype = null;

		mimetype = detectWithExtension(parts);
		if (mimetype == null) {
			throw new FileManagerException(MessageSeverity.ERROR, MessageKeysEnum.FILE_TYPE_UNVERIFIABLE.getKey(),
					MessageFormat.format(MessageKeysEnum.FILE_TYPE_UNVERIFIABLE.getMessage(), parts, "UNKNOWN"));
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
	protected MimeType detectWithExtension(FilePartsDto parts) {
		MimeType mimetype = null;

		if ((parts != null) && !StringUtils.isBlank(parts.getExtension())) {
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
	private MimeType detectNonConvertibleTypes(FilePartsDto parts) {
		MimeType mimetype = null;

		String filename = parts.getName() + SEPARATOR + parts.getExtension();
		String rawtype = URLConnection.guessContentTypeFromName(filename);
		if (StringUtils.isBlank(rawtype)) {
			rawtype = props.getProperty(parts.getExtension().toLowerCase());
		}

		if (rawtype != null) {
			try {
				mimetype = new MimeType(rawtype);
				// CHECKSTYLE:OFF
			} catch (MimeTypeParseException e) { // NOSONAR - intentionally do not re-throw
				LOGGER.debug("Could not parse raw MIME type '" + rawtype + "'");
				// intentionally ignore this, just let mimetype be null
			}
			// CHECKSTYLE:ON
		}

		return mimetype;
	}

}
