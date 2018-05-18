package gov.va.vetservices.lib.filemanager.impl.dto;

/**
 * Enumeration of key forms.
 *
 * @author aburkholder
 */
@Deprecated
public enum FormsEnum {
	/** 526 standard claim */
	VA_21_526("131", "VA 21-526", "Veterans Application for Compensation or Pension"),
	/** 526 fully developed claim */
	VA_21_526EZ("533", "VA 21-526EZ", "Fully Developed Claim (Compensation)"),
	/** 526 supplemental claim */
	VA_21_526B("532", "VA 21-526b", "Veteran Supplemental Claim"),
	/** 526 predischarge claim */
	VA_21_526C("530", "VA 21-526c", "Pre-Discharge Compensation Claim"),
	/** 4502 conveyance adaptive equipment */
	VA_21_4502("126", "VA 21-4502", "Application for Automobile or Other Conveyance and Adaptive Equipment Under 38 U.S.C. 3901-3904"),
	/** 8940 unemployability increase */
	VA_21_8940("158", "VA 21-8940", "Veterans Application for Increased Compensation Based on Unemployability"),
	/** 2680 need permanent aid and attendance */
	VA_21_2680("111", "VA 21-2680", "Examination for Housebound Status or Permanent Need for Regular Aid and Attendance"),
	/** 0779 nursing home info */
	VA_21_0779("375", "VA 21-0779", "Request for Nursing Home Info In Connection with Claim for Aid and Attendance"),
	/** 0781 ptsd supporting statement */
	VA_21_0781("381", "VA 21-0781", "Statement in Support of Claim for PTSD"),
	/** 0781a ptsd supporting statement secondary to assault */
	VA_21_0781A("382", "VA 21-0781a", "Statement in Support of Claim for PTSD Secondary to Personal Assault"),
	/** 4192 employment information */
	VA_21_4192("124", "VA 21-4192", "Request for Employment Information in Connection with Claim for Disability"),
	/** 674 school attendance */
	VA_21_674("142", "VA 21-674", "Report of School Attendance"),
	/** 674B school attendance report */
	VA_21_674B("143", "VA 21-674b", "School Attendance Report"),
	/** 674c school attendance approval request */
	VA_21_674C("144", "VA 21-674c", "Request for Approval of School Attendance"),
	/** 686c dependents status */
	VA_21_686C("148", "VA 21-686c", "Declaration of Status of Dependents"),
	/** Supplemental documents to 686 */
	SUPPLEMENTAL_686("002", "686 Supplemental", "Supplemental 686 documents"),
	/** Supplemental documents to claims */
	SUPPLEMENTAL_CLAIMS("001", "Claims Supplemental", "Supplemental Claims documents"),
	/** Other supporting documents */
	OTHER("000", "Other", "Other supporting documents");

	String docTypeId;
	String formName;
	String formDescription;

	@Deprecated
	FormsEnum(String docTypeId, String formName, String formDescription) {
		this.docTypeId = docTypeId;
		this.formName = formName;
		this.formDescription = formDescription;
	}

	@Deprecated
	public static FormsEnum getByDoctype(String doctypeId) {
		for (FormsEnum form : FormsEnum.values()) {
			if (form.docTypeId.equals(doctypeId)) {
				return form;
			}
		}
		return null;
	}
}
