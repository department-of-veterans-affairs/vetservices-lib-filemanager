package gov.va.vetservices.lib.filemanager.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import gov.va.vetservices.lib.filemanager.FileManagerConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(inheritListeners = false, listeners = { DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class FileManagerImplTest {

	@Autowired
	@Qualifier(FileManagerImpl.BEAN_NAME)
	FileManagerImpl fileManagerImpl;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		assertNotNull("fileManagerImpl cannot be null.", fileManagerImpl);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void validateFileForPDFConversionTest_nullFile() {
		// TODO make this a real test
		assertTrue(true);
//		FileManagerResponse response = fileManagerImpl.validateFileForPDFConversion(null);
//		assertNotNull("validateFileForPDFConversion returned null", response);
//		assertNotNull("validateFileForPDFConversion should have reported error messages", response.getMessages());
//		assertTrue("validateFileForPDFConversion should have reported error messages", !response.getMessages().isEmpty());
	}

	@Test
	public void validateFileForPDFConversionTest() {
//		FileManagerResponse response = fileManagerImpl.validateFileForPDFConversion(fileDto)
		assertTrue(true);
	}

	@Test
	public void convertToPdfTest() {
		// TODO make this a real test
		assertTrue(true);
	}

	@Test
	public void stampPdfTest() {
		// TODO make this a real test
		assertTrue(true);
	}

}
