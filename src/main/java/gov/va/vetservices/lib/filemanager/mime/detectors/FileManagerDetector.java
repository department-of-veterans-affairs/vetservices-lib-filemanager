package gov.va.vetservices.lib.filemanager.mime.detectors;

import java.io.IOException;

import javax.activation.MimeType;

import org.apache.commons.lang3.StringUtils;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileParts;
import gov.va.vetservices.lib.filemanager.mime.ConvertibleTypesEnum;
import gov.va.vetservices.lib.filemanager.util.FileManagerUtils;

public class FileManagerDetector implements Detector {

	/**
	 * ALWAYS returns {@code null}. This detector does not try to be magic.
	 */
	@Override
	public String detectByMagic(byte[] bytes) throws IOException {
		return null;
	}

	/**
	 * Uses the filename extension to derive the MIME type.
	 * If MIME type cannot be determined, {@code null} is returned.
	 * <p>
	 * The {@code bytes} parameter is ignored.
	 */
	@Override
	public String detectWithFilename(byte[] bytes, String filename) throws IOException {
		String mimetypeAsString = null;

		FileParts parts = FileManagerUtils.getFileParts(filename);
		if (!StringUtils.isBlank(parts.getExtension())) {
			MimeType mimetype = ConvertibleTypesEnum.getMimeTypeForExtension(parts.getExtension());
			if (mimetype != null) {
				mimetypeAsString = mimetype.getBaseType();
			}
		}
		return mimetypeAsString;
	}

}
