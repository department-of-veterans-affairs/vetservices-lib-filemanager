package gov.va.vetservices.lib.filemanager.util;

import org.apache.commons.lang3.StringUtils;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileParts;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ValidatorDto;

/**
 * Static utilities to help with file processing
 *
 * @author aburkholder
 *
 */
public class FileManagerUtils {

	/** Characters that are not allowed at the beginning of a file names: {@code . / \ :} */
	public static final String[] ILLEGAL_FILE_START_CHARS = { "/", "\\", ":", "." };

	/**
	 * Do not instantiate
	 */
	FileManagerUtils() {
		throw new IllegalAccessError("FileManagerUtils is a static class. Do not instantiate it.");
	}

	/**
	 * Returns {@code true} if the filename is valid.
	 *
	 * @param filename the filename to check
	 * @return boolean
	 */
	public static boolean hasFilename(String filename) {
		return !(StringUtils.isBlank(filename) || StringUtils.startsWithAny(filename, ILLEGAL_FILE_START_CHARS));
	}

	/**
	 * Returns {@code true} if the byte array is not null and has one or more bytes in the array.
	 *
	 * @param bytes the byte array to check
	 * @return boolean
	 */
	public static boolean hasBytes(byte[] bytes) {
		return (bytes != null) && (bytes.length > 0);
	}

	/**
	 * Populates the DTO for use by validators, based on the contents of the fileDto parameter.
	 * If the fileDto paramter is null,
	 *
	 * @param fileDto
	 * @return ValidatorDto
	 */
	public static final ValidatorDto makeValidatorDto(FileDto fileDto) {
		ValidatorDto vdto = new ValidatorDto();
		if (fileDto != null) { // should never happen, but check anyway
			vdto.setFileDto(fileDto);
			vdto.setFileParts(getFileParts(fileDto.getFilename()));
		}
		return vdto;
	}

	/**
	 * If filename is not null or empty, returns a populated {@link FileParts} (though it is possible for the members of FileParts to
	 * be {@code null}, otherwise returns {@code null}
	 *
	 * @param filename
	 * @return FileParts
	 */
	public static final FileParts getFileParts(String filename) {
		FileParts fileParts = null;

		if (StringUtils.isNotBlank(filename)) {
			fileParts = new FileParts();

			String[] filenames = { null, null };

			if (filename.contains(".")) {
				filenames[0] = StringUtils.truncate(filename, filename.lastIndexOf("."));
				filenames[1] = StringUtils.substring(filename, filename.lastIndexOf(".") + 1);
			} else {
				filenames[0] = StringUtils.trim(filename);
			}

			if (StringUtils.isBlank(filenames[0])) {
				filenames[0] = null;
			}
			if (StringUtils.isBlank(filenames[1])) {
				filenames[1] = null;
			}

			fileParts.setName(filenames[0]);
			fileParts.setExtension(filenames[1]);
		}

		return fileParts;
	}

}
