package gov.va.vetservices.lib.filemanager.impl;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.exception.PdfStamperException;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.pdf.stamp.StampDataResolver;
import gov.va.vetservices.lib.filemanager.pdf.stamp.Stamper;
import gov.va.vetservices.lib.filemanager.pdf.stamp.dto.StampDataDto;

public class StampPdf {
	public void doStamp(ImplDto implDto, FileManagerResponse response) {
		if (implDto == null) {
			throw new IllegalArgumentException("ImplDto is not an optional parameter.");
		}
		if (response == null) {
			throw new IllegalArgumentException("FileManagerResponse is not an optional parameter.");
		}

		StampDataResolver resolver = new StampDataResolver();
		Stamper stamper = new Stamper();

		StampDataDto stampData = resolver.resolve(implDto);
		try { // NOSONAR
			byte[] bytes = stamper.stamp(stampData, implDto.getFileDto().getFilebytes());
			if ((bytes != null) && (bytes.length > 0)) {
				response.getFileDto().setFilebytes(bytes);
			}
		} catch (PdfStamperException e) { // NOSONAR
			// NOSONAR TODO decide to let it throw, or handle it
		}

	}
}
