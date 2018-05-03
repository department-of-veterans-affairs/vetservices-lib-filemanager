
package gov.va.vetservices.lib.filemanager.impl.dto;

import gov.va.ascent.framework.service.ServiceResponse;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.impl.dto.FilePartsDto;

/**
 * A Data Transfer Object used by {@link ImplDto} for transporting data across the layer boundaries between the API and the
 */
public class ImplDto extends ServiceResponse {
	protected FileDto fileDto;
	protected FilePartsDto fileParts;

	/**
	 * Gets the file data transfer object.
	 *
	 * @return FileDto the dto
	 */
	public FileDto getFileDto() {
		return fileDto;
	}

	/**
	 * Sets the file data transfer object.
	 *
	 * @param fileDto the FileDto
	 */
	public void setFileDto(FileDto fileDto) {
		this.fileDto = fileDto;
	}

	/**
	 * Gets the file parts object.
	 *
	 * @return FilePartsDto the file parts
	 */
	public FilePartsDto getFileParts() {
		return fileParts;
	}

	/**
	 * Sets the file parts object.
	 *
	 * @param fileParts the file parts
	 */
	public void setFileParts(FilePartsDto fileParts) {
		this.fileParts = fileParts;
	}

}
