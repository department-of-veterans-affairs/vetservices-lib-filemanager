
package gov.va.vetservices.lib.filemanager.impl.dto;

import java.util.ArrayList;
import java.util.List;

import gov.va.ascent.framework.messages.Message;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;

/**
 * A Data Transfer Object used by {@link ImplDto} for transporting data across the layer boundaries between the API and business impls.
 */
public class ImplDto {
	protected DocMetadataDto docMetadataDto;
	protected FileDto originalFileDto;
	protected FileDto pdfFileDto;
	protected FilePartsDto fileParts;
	protected ProcessType processType;
	protected List<Message> messages = new ArrayList<>();

	/**
	 * @return the docMetadataDto
	 */
	public DocMetadataDto getDocMetadataDto() {
		return docMetadataDto;
	}

	/**
	 * @param docMetadataDto the docMetadataDto to set
	 */
	public void setDocMetadataDto(DocMetadataDto docMetadataDto) {
		this.docMetadataDto = docMetadataDto;
	}

	/**
	 * Gets the file data transfer object.
	 *
	 * @return FileDto the dto
	 */
	public FileDto getOriginalFileDto() {
		return originalFileDto;
	}

	/**
	 * Sets the file data transfer object.
	 *
	 * @param fileDto the FileDto
	 */
	public void setOriginalFileDto(FileDto fileDto) {
		this.originalFileDto = fileDto;
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

	/**
	 * @return the messages
	 */
	public List<Message> getMessages() {
		return messages;
	}

	/**
	 * @param message the message to add
	 */
	public void addMessage(Message message) {
		this.messages.add(message);
	}

	/**
	 * @param messages the messages to add
	 */
	public void addMessages(List<Message> messages) {
		this.messages.addAll(messages);
	}

	/**
	 * @return the pdfFileDto
	 */
	public FileDto getPdfFileDto() {
		return pdfFileDto;
	}

	/**
	 * @param pdfFileDto the pdfFileDto to set
	 */
	public void setPdfFileDto(FileDto pdfFileDto) {
		this.pdfFileDto = pdfFileDto;
	}

	/**
	 * @return the processType
	 */
	public ProcessType getProcessType() {
		return processType;
	}

	/**
	 * @param processType the processType to set
	 */
	public void setProcessType(ProcessType processType) {
		this.processType = processType;
	}

}
