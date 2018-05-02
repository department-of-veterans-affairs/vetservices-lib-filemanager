package gov.va.vetservices.lib.filemanager.mime.detectors;

import javax.activation.MimeType;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileParts;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;

/**
 * Interface for MIME type detectors.
 *
 * @author aburkholder
 */
public abstract class AbstractDetector {

	/** Separator between file name and file extension */
	public static final String SEPARATOR = ".";

	/**
	 * Do a check to see if both the magic and filename detectors returned the same value.
	 * If not, return the most likely value (weighted toward magic).
	 * If both arguments are {@code null}, then {@code null} is returned.
	 *
	 * @param withMagic the type detected by magic
	 * @param withHint the filename to use a hint to the type
	 * @return String the winning name, or {@code null} if both params are null
	 */
	public static MimeType selfCheck(MimeType withMagic, MimeType withHint) {
		MimeType winner = null;

		if ((withMagic == null) && (withHint == null)) {
			// both are null, do nothing
		} else if ((withMagic == null) || (withHint == null)) {
			winner = withMagic == null ? withHint : withMagic;
		} else {
			// magic does not match hint
			winner = withMagic;
		}

		return winner;
	}

	/**
	 * Perform detection of MimeType given the byte content and filename.
	 *
	 * @param bytes the file content as byte array
	 * @param filename the original name of the file
	 * @return MimeType the MIME type
	 */
	public abstract MimeType detect(final byte[] bytes, final FileParts parts) throws FileManagerException;

}
