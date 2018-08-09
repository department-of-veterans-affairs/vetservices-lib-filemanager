package gov.va.vetservices.lib.filemanager.impl.dto;

import java.util.Date;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;

/**
 * A Data Transfer Object used by {@link ImplDto} for transporting data across the layer boundaries between the API and business impls.
 */
public class DocMetadataDto {
	private ProcessType processType;
	private String claimId;
	private String docTypeId;
	private Date docDate;

	/**
	 * @return the processType
	 */
	public ProcessType getProcessType() {
		return processType;
	}

	/**
	 * @param processType the processType to set
	 */
	public void setProcessType(final ProcessType processType) {
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
	public void setClaimId(final String claimId) {
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
	public void setDocTypeId(final String docTypeId) {
		this.docTypeId = docTypeId;
	}

	/**
	 * @return the date of submission of the document from the veterans point of view
	 */
	public Date getDocDate() {
		return docDate;
	}

	/**
	 * @param docDate the date of submission of the document from the veterans point of view
	 */
	public void setDocDate(final Date docDate) {
		this.docDate = docDate;
	}
}
