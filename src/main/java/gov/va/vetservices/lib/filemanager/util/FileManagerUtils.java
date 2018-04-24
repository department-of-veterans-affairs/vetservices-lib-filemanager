package gov.va.vetservices.lib.filemanager.util;

import org.apache.commons.lang3.StringUtils;

import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
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
		return !(StringUtils.isBlank(filename) || StringUtils.startsWithAny(filename, FileManagerProperties.FILE_NAME_ILLEGAL_CHARS));
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
	 * be {@code null} or empty). Otherwise returns {@code null}
	 *
	 * @param filename
	 * @return FileParts
	 */
	public static final FileParts getFileParts(String filename) {
		FileParts fileParts = null;

		if (StringUtils.isNotBlank(filename)) {
			fileParts = new FileParts();

			String[] filenames = splitOnLastOf(filename, '.');

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

	/**
	 * Split a string into two parts, splitting on the last occurrence of the separator argument.
	 * <p>
	 * If the separator does not appear in the string, the {@code string} will be returned on the first member of the array.
	 * It is possible for either array element to be {@code null} or empty.
	 *
	 * <pre>
	 * FileManagerutils.splitOnLastOf(null,    'x') = { null,   null  }
	 * FileManagerutils.splitOnLastOf("",      'x') = { null,   null  }
	 * FileManagerutils.splitOnLastOf("   ",   'x') = { null,   null  }
	 * FileManagerutils.splitOnLastOf(" bc ",  'x') = { " bc ", null  }
	 * FileManagerutils.splitOnLastOf("abcd",  'x') = { "abcd", null  }
	 * FileManagerutils.splitOnLastOf("abcd",  'a') = { "",     "bcd" }
	 * FileManagerutils.splitOnLastOf("abcd,   'd') = { "abc",  ""    }
	 * FileManagerutils.splitOnLastOf("abcd",  'b') = { "a",    "cd"  }
	 * FileManagerutils.splitOnLastOf("abcba", 'b') = { "abc",   "a"  }
	 * </pre>
	 *
	 * @param string the string to split
	 * @param separator the character on which to split the string
	 * @return String[] an array of two values
	 */
	public static String[] splitOnLastOf(String string, char separator) {
		String[] separated = { null, null };

		if (!StringUtils.isBlank(string)) {
			if (string.contains(String.valueOf(separator))) {
				separated[0] = StringUtils.truncate(string, string.lastIndexOf(separator));
				separated[1] = StringUtils.substring(string, string.lastIndexOf(separator) + 1);
			} else {
				separated[0] = string;
			}
		}

		return separated;
	}
}
