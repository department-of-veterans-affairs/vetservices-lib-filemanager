package gov.va.vetservices.lib.filemanager.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.api.FileManagerProperties;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.dto.DocMetadataDto;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;

/**
 * Static utilities to help with file processing
 *
 * @author aburkholder
 */
public class FileManagerUtils {

	private static final String EMPTY_STRING = "";
	private static final String SAFE_FILENAME_REGEX = "[^a-z0-9-_]";
	private static final String SAFE_FILENAME_DATE_FORMAT = "yyyyMMdd";
	private static final int SAFE_FILENAME_MAX_LEN = 245;

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
		return !(StringUtils.isBlank(filename) || StringUtils.startsWithAny(filename, FileManagerProperties.FILE_NAME_ILLEGAL_CHARS.stream().toArray(String[]::new)));
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
	 * @return ImplDto
	 */
	public static final ImplDto makeImplDto(FileManagerRequest request) {
		ImplDto implDto = new ImplDto();
		if (request != null) {
			// metadata
			DocMetadataDto metadata = new DocMetadataDto();
			metadata.setProcessType(request.getProcessType());
			metadata.setDocTypeId(request.getDocTypeId());
			metadata.setClaimId(request.getClaimId());
			implDto.setDocMetadataDto(metadata);
			// file Dto
			if (request.getFileDto() != null) { // should never happen, but avoid null pointers
				implDto.setOriginalFileDto(request.getFileDto());
				implDto.setFileParts(getFileParts(request.getFileDto().getFilename()));
			}
			implDto.setProcessType(request.getProcessType());
		}
		return implDto;
	}

	/**
	 * If filename is not null or empty, returns a populated {@link FilePartsDto} (though it is possible for the members of
	 * FilePartsDto to
	 * be {@code null} or empty). Otherwise returns {@code null}
	 *
	 * @param filename
	 * @return FilePartsDto
	 */
	public static final FilePartsDto getFileParts(String filename) {
		FilePartsDto fileParts = new FilePartsDto();

		if (StringUtils.isNotBlank(filename)) {

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

	/**
	 * <b>Based on the implDto.getPdfFileDto()</b> filename provided in the implDto parameter,
	 * gets a filename that is limited to a total of {@value #SAFE_FILENAME_MAX_LEN} characters,
	 * lowercase, containing only characters {@value #SAFE_FILENAME_REGEX}, with the file name
	 * terminated by the current date.
	 *
	 * @param implDto as constructed from the initial request
	 * @return String the safe filename
	 * @throws FileManagerException if implDto or fileDto within it are not provided
	 */
	public static String getSafeDatestampedFilename(ImplDto implDto) throws FileManagerException {
		if ((implDto == null) || (implDto.getPdfFileDto() == null) || StringUtils.isBlank(implDto.getPdfFileDto().getFilename())) {
			MessageKeysEnum key = MessageKeysEnum.FILE_NAME_NULL_OR_EMPTY;
			throw new FileManagerException(MessageSeverity.ERROR, key.getKey(), key.getMessage());
		}

		String filename = implDto.getPdfFileDto().getFilename();

		// lowercase
		filename = filename.toLowerCase(Locale.US);
		// simple ascii
		final String today = new SimpleDateFormat(SAFE_FILENAME_DATE_FORMAT, Locale.US).format(new Date());
		FilePartsDto parts = getFileParts(filename);
		parts.setName(parts.getName().replaceAll(SAFE_FILENAME_REGEX, EMPTY_STRING));
		parts.setExtension(parts.getExtension().replaceAll(SAFE_FILENAME_REGEX, EMPTY_STRING));
		// truncate
		int extPlusDotLen = parts.getExtension().length() + 1; // +1 for the period
		int datePlusDashLen = today.length() + 1; // +1 for a dash
		int maxNameLen = SAFE_FILENAME_MAX_LEN - extPlusDotLen - datePlusDashLen;
		parts.setName(StringUtils.substring(parts.getName(), 0, maxNameLen - 1) + "-" + today); // -1 because substring is 0-based

		return parts.getName() + "." + parts.getExtension();
	}
}
