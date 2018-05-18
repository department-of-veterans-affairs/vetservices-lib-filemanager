package gov.va.vetservices.lib.filemanager.impl.dto;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;

/**
 * A Data Transfer Object used by {@link ImplDto} for transporting data across the layer boundaries between the API and business impls.
 */
public class DocMetadataDto {
	private ProcessType processType;
	private String claimId;
	private String docTypeId;

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

	/**
	 * @return the claimId
	 */
	public String getClaimId() {
		return claimId;
	}

	/**
	 * @param claimId the claimId to set
	 */
	public void setClaimId(String claimId) {
		this.claimId = claimId;
	}

	/**
	 * @return the docTypeId
	 */
	public String getDocTypeId() {
		return docTypeId;
	}

	/**
	 * @param docTypeId the docTypeId to set
	 */
	public void setDocTypeId(String docTypeId) {
		this.docTypeId = docTypeId;
	}
}
