package gov.va.vetservices.lib.filemanager.api;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerRequest;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileManagerResponse;

public class FileManagerTest {

	class TestFileManager implements FileManager {

		@Override
		public FileManagerResponse validateFileForPDFConversion(FileManagerRequest request) {
			return new FileManagerResponse();
		}

		@Override
		public FileManagerResponse convertToPdf(FileManagerRequest request) {
			return new FileManagerResponse();
		}

	}

	@Test
	public void test() {
		TestFileManager test = new TestFileManager();
		assertNotNull(test);
		assertNotNull(test.validateFileForPDFConversion(null));
		assertNotNull(test.convertToPdf(null));
	}

}
