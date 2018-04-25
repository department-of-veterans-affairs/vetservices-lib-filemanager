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
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileParts;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeys;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;

public class FilenameDetector extends AbstractDetector {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilenameDetector.class);

	private static final String PROPS_CLASSPATH = "mimetypes-by-extension.properties";

	private static Properties props;

	public FilenameDetector() {
		if (props == null) {
			synchronized (FilenameDetector.class) {
				// double lock test (race conditions)
				if (props == null) {
					try {
						props = new Properties();

						Resource resource =
								(new PathMatchingResourcePatternResolver()).getResources("classpath*:" + PROPS_CLASSPATH)[0];
						if (resource == null) {
							throw new IOException(PROPS_CLASSPATH + " does not exist on the classpath.");
						}
						InputStream is = resource.getInputStream();
						if (is == null) {
							throw new IOException(PROPS_CLASSPATH + " cannot be opened.");
						}
						props.load(is);

					} catch (IOException e) {
						String message = "FATAL ERROR: " + e.getClass().getSimpleName() + " - could not load classpath file '"
								+ PROPS_CLASSPATH + "': " + e.getMessage();
						LOGGER.error(message);
						throw new IllegalArgumentException(message, e);
					}
				}
			}
		}
	}

	/**
	 * Derive the mimetype solely from the filename.
	 * The {@code bytes} parameter is ignored by this detector.
	 *
	 * @throws FileManagerException if MIME type cannot be derived
	 */
	@Override
	public MimeType detect(byte[] bytes, FileParts parts) throws FileManagerException {
		MimeType mimetype = null;

		mimetype = detectWithExtension(parts);
		if (mimetype == null) {
			throw new FileManagerException(MessageSeverity.ERROR, MessageKeys.FILE_TYPE_UNVERIFIABLE.getKey(),
					MessageFormat.format(MessageKeys.FILE_TYPE_UNVERIFIABLE.getMessage(), parts, "UNKNOWN"));
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
	protected MimeType detectWithExtension(FileParts parts) {
		MimeType mimetype = null;

		if ((parts != null) && !StringUtils.isBlank(parts.getExtension())) {
			mimetype = ConvertibleTypesEnum.getMimeTypeForExtension(parts.getExtension());
			if (mimetype == null) {
				String filename = parts.getName() + separator + parts.getExtension();
				String rawtype = URLConnection.guessContentTypeFromName(filename);
				if (StringUtils.isBlank(rawtype)) {
					rawtype = props.getProperty(parts.getExtension().toLowerCase());
				}

				if (rawtype != null) {
					try {
						mimetype = new MimeType(rawtype);
					} catch (MimeTypeParseException e) {
						// intentionally ignore this, just let mimetype be null
					}
				}
			}
		}

		return mimetype;
	}

}
