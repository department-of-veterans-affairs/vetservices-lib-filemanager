package gov.va.vetservices.lib.filemanager.mime.detectors;

import java.io.IOException;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.xml.sax.SAXException;

import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

/**
 * Use Apache Tika detection capabilities to attempt mime type detection.
 *
 * @author aburkholder
 */
public class TikaDetector implements Detector {

	/* Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(TikaDetector.class);

	/** Classpath to the Tika Config file */
	protected static final String TIKA_CONFIG = "/tika-config.xml";
	/** Tika Config, contains the detector resources */
	private TikaConfig tikaConfig;

	/**
	 * Instantiate the Tika with {@value #TIKA_CONFIG}.
	 */
	public TikaDetector() {
		try {
			Resource resource = (new PathMatchingResourcePatternResolver()).getResources("classpath*:" + TIKA_CONFIG)[0];
			tikaConfig = new TikaConfig(resource.getFile());
		} catch (TikaException | IOException | SAXException e) {
			String message = "FATAL ERROR: " + e.getClass().getSimpleName() + " - could not load classpath file '" + TIKA_CONFIG
					+ "': " + e.getMessage();
			LOGGER.error(message);
			throw new IllegalArgumentException(message, e);
		}
	}

	/**
	 * Let Tika try to detect the MIME type with no knowledge of the filename.
	 * <p>
	 * If Tika cannot decisively determine the MIME type,
	 * it will return {@code application/octet-stream} as its default type.<br/>
	 * For example, ASCII text files containing control characters and/or
	 * Extended ASCII characters will likely be considered unidentifiable
	 * because the file content provide no hints if they bytes are binary or not.
	 */
	@Override
	public String detectByMagic(final byte[] bytes) throws IOException {
		String mimetypeAsString = null;

		if (FileManagerUtils.hasBytes(bytes)) {
			MediaType mediatypeMagic = null;

			MimeTypes mimeRegistry = tikaConfig.getMimeRepository();

			// detect by magic
			mediatypeMagic = mimeRegistry.detect(TikaInputStream.get(bytes), new Metadata());

			mimetypeAsString = mediatypeMagic == null ? null : mediatypeMagic.getBaseType().toString();
		}

		return mimetypeAsString;
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
	 */
	@Override
	public String detectWithFilename(final byte[] bytes, final String filename) throws IOException {
		String mimetypeAsString = null;

		if (FileManagerUtils.hasBytes(bytes)) {
			MimeTypes mimeRegistry = tikaConfig.getMimeRepository();

			// detect by filename
			Metadata metadata = new Metadata();
			metadata.set(TikaCoreProperties.ORIGINAL_RESOURCE_NAME, filename);

			MediaType type = mimeRegistry.detect(TikaInputStream.get(bytes), metadata);

			mimetypeAsString = type.getBaseType().toString();
		}

		return mimetypeAsString;
	}

}
