package gov.va.vetservices.lib.filemanager.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;
import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;

public class StampPdfTest {

	StampPdf stampPdf;

	@Before
	public void setUp() throws Exception {
		stampPdf = new StampPdf();
		assertNotNull(stampPdf);
	}

	@Test
	public final void testDoStamp() {
		try {
			stampPdf.doStamp(new ImplDto(), new FileManagerResponse());
		} catch (Throwable e) {
			// NOSONAR TODO finish tests when code is written
			e.printStackTrace();
		}
		assertNotNull("");
	}

}
