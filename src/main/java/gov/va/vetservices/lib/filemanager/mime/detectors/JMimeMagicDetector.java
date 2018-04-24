package gov.va.vetservices.lib.filemanager.mime.detectors;

import java.io.IOException;

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
public class JMimeMagicDetector implements Detector {

	/**
	 * Let JMimeMagic try to detect the MIME type with no knowledge of the filename.
	 * <p>
	 * If JMimeMagic cannot decisively determine the MIME type,
	 * it may throw an IOException or return {@code null}.<br/>
	 * JMimeMagic also has a long-standing bug wherein it mistakenly reports
	 * BMP files as {@code text/plain}.
	 */
	@Override
	public String detectByMagic(final byte[] bytes) throws IOException {
		String mimetypeAsString = null;
		try {
			final MagicMatch match = Magic.getMagicMatch(bytes);
			if (match != null) {
				mimetypeAsString = match.getMimeType();
			}
		} catch (final MagicParseException | MagicMatchNotFoundException | MagicException e) {
			throw new IOException(
					"Internal jmimemagic " + e.getClass().getSimpleName() + " while parsing file bytes: " + e.getMessage(), e);
		}
		return mimetypeAsString;
	}

	/**
	 * Let JMimeMagic try to detect the MIME using extension hints.
	 * <p>
	 * If JMimeMagic cannot decisively determine the MIME type,
	 * it may throw an IOException or return {@code null}.<br/>
	 * JMimeMagic also has a long-standing bug wherein it mistakenly reports
	 * BMP files as {@code text/plain}.
	 */
	@Override
	public String detectWithFilename(final byte[] bytes, final String filename) throws IOException {
		String mimetypeAsString = null;
		if (bytes != null) {
			try {
				final MagicMatch match = Magic.getMagicMatch(bytes, true);
				if (match != null) {
					mimetypeAsString = match.getMimeType();
				}
			} catch (final MagicParseException | MagicMatchNotFoundException | MagicException e) {
				throw new IOException(
						"Internal jmimemagic " + e.getClass().getSimpleName() + " while parsing file bytes: " + e.getMessage(), e);
			}
		}
		return mimetypeAsString;
	}

}
