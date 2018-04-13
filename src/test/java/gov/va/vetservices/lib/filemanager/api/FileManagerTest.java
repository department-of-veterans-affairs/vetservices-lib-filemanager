package gov.va.vetservices.lib.filemanager.api;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import gov.va.ascent.framework.service.ServiceResponse;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;

public class FileManagerTest {

	class FileMgrTest implements FileManager {

		@Override
		public ServiceResponse validateFileForPDFConversion(FileDto fileDto) {
			return new ServiceResponse();
		}

		@Override
		public byte[] convertToPdf(byte[] file) {
			return new byte[] {};
		}

		@Override
		public byte[] stampPdf(String stampContent, byte[] file) {
			return new byte[] {};
		}

	}

	@Test
	public void test() {
		FileMgrTest test = new FileMgrTest();
		assertNotNull(test);
		assertNotNull(test.validateFileForPDFConversion(null));
		assertNotNull(test.convertToPdf(null));
		assertNotNull(test.stampPdf(null, null));
	}

}
