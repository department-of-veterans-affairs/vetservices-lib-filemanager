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

	/**
	 * Do not instantiate
	 */
	FileManagerUtils() {
		throw new IllegalAccessError("FileManagerUtils is a static class. Do not instantiate it.");
	}

	/**
	 * Populates the DTO for use by validators, based on the contents of the fileDto parameter.
	 *
	 * @param fileDto
	 * @return ValidatorDto
	 */
	public static final ValidatorDto makeValidatorDto(FileDto fileDto) {
		ValidatorDto vdto = new ValidatorDto();
		vdto.setFileDto(fileDto);
		vdto.setFileParts(getFileParts(fileDto.getFilename()));

		return vdto;
	}

	private static final FileParts getFileParts(String filename) {
		String[] filenames = { null, null };

		if (filename.contains(".")) {
			filenames[0] = StringUtils.truncate(filename, filename.lastIndexOf("."));
			filenames[1] = StringUtils.substring(filename, filename.lastIndexOf(".") + 1);
		} else {
			filenames[0] = filename;
		}

		FileParts fileParts = new FileParts();
		fileParts.setName(filenames[0]);
		fileParts.setExtension(filenames[1]);

		return fileParts;
	}
}
