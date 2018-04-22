package gov.va.vetservices.lib.filemanager.mime.detectors;

import java.io.IOException;

/**
 * Interface for MIME type detectors.
 *
 * @author aburkholder
 */
public interface Detector {

	/**
	 * Perform magic MIME type detection.
	 *
	 * @param bytes the file content as byte array
	 * @param filename the original name of the file
	 * @return String the raw MIME type
	 * @throws IOException parsing or processing issue
	 */
	public String detectByMagic(final byte[] bytes) throws IOException;

	/**
	 * Perform magic MIME type detection,
	 * including any means for filename hints that might be available in the detector.
	 *
	 * @param bytes the file content as byte array
	 * @param filename the original name of the file
	 * @return String the raw MIME type
	 * @throws IOException parsing or processing issue
	 */
	public String detectWithFilename(final byte[] bytes, final String filename) throws IOException;
}
