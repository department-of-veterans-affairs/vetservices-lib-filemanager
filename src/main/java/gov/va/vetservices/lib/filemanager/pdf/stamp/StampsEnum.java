package gov.va.vetservices.lib.filemanager.pdf.stamp;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;

/**
 * PDF header stamp text.
 * To get the formatted text, use the getFormattedText method.
 * <p>
 * Example:
 *
 * <pre>
 * StampsEnum.CLAIM.getFormattedText("12345");
 * </pre>
 *
 * @author aburkholder
 */
public enum StampsEnum {

	/** Stamp with claim number. Args: claim_number */
	CLAIM("#{0} Submitted Electronically",
			new ProcessType[] { ProcessType.CLAIMS_526, ProcessType.CLAIMS_686, ProcessType.CLAIMS_686_SUPPORTING,
					ProcessType.SCHOOL_ATTENDANCE_674 }),
	/** Stamp with time+date and claim number. Args: claim_number, time+date */
	CLAIM_TIMEDATE("{1} #{0} Submitted Electronically",
			new ProcessType[] { ProcessType.CLAIMS_526_SUPPORTING, ProcessType.CLAIMS_STATUS_SUPPORTING });

	private static final String TIMEDATE_FORMAT = "HH:mm z MM/dd/yyyy";

	private String stampText;
	private ProcessType[] processTypes;

	/**
	 * Constructor for enumerations. The stampText can utilize MessageFormat variable substitution.
	 *
	 * @param stampText
	 */
	private StampsEnum(final String stampText, final ProcessType[] processTypes) {
		this.stampText = stampText;
		this.processTypes = processTypes;
	}

	/**
	 * Retrieves the formatted Stamp Text for the given process type.
	 * <p>
	 * The default stamp text is CLAIM_TiMESTAMP (includes the timestamp in the text).
	 * If the processType parameter is not found in the processTypes list that is internally associated with the enumeration,
	 * it invokes the default stamp. The method will return {@code null} for OTHER processType, or if the processType is not
	 * associated with any StampsEnum and claimId is null/empty.
	 * <p>
	 * This method throws a runtime exception if any of the parameters are null or empty.
	 *
	 * @param processType provided by consumer in the FileManager request
	 * @param claimId provided by consumer in the FileManager request
	 * @param docDate date of submission of document from the veteran's point of view
	 * @return String the formatted stamp text, or null if the processType is not found
	 * @throws IllegalArgument exception if the processType or claimId are null or empty
	 */
	public static String getStampText(final ProcessType processType, final String claimId, final Date docDate) {
		if (processType == null) {
			throw new IllegalArgumentException("PDF header stamp requires a ProcessType.");
		}
		for (StampsEnum value : StampsEnum.values()) {
			for (ProcessType process : value.getProcessTypes()) {
				if (process.equals(processType)) {
					return value.getFormattedText(claimId, docDate);
				}
			}
		}
		return null;
	}

	/**
	 * @return the stampText
	 */
	protected String getStampText() {
		return stampText;
	}

	/**
	 * @return the processTypes that use the stamp text format
	 */
	protected ProcessType[] getProcessTypes() {
		return processTypes;
	}

	/**
	 * Get the formatted text to be used for the header stamp. The {@code claimId} is required.
	 *
	 * @param claimId the claim ID for the document
	 * @param docDate date of submission of document from the veteran's point of view
	 * @return String the formatted stamp text
	 * @throws IllegalArgumentException if claimId parameter is null or empty
	 */
	private String getFormattedText(final String claimId, final Date docDate) {
		if (StringUtils.isBlank(claimId)) {
			throw new IllegalArgumentException("PDF header stamp requires a Claim ID.");
		}
		String formattedTimeDate = docDate == null ? "" : new SimpleDateFormat(TIMEDATE_FORMAT).format(docDate);
		return MessageFormat.format(this.stampText, claimId, formattedTimeDate);
	}

}
