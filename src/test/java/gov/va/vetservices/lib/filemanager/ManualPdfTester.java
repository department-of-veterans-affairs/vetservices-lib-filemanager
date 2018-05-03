package gov.va.vetservices.lib.filemanager;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.lowagie.text.pdf.PdfReader;

import gov.va.vetservices.lib.filemanager.api.stamper.StampData;
import gov.va.vetservices.lib.filemanager.pdf.stamp.Stamper;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

/**
 * DO NOT DELETE
 * This jUnit test class allows for independent, manual testing of PDFs using iText.
 * Use the console output to review the iText interpretation of various PDF file states.
 *
 * @author aburkholder
 */
public class ManualPdfTester extends AbstractFileHandler {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Ignore
	@Test
	public final void test() {
		Stamper stamper = new Stamper();
		StampData stampData = new StampData();
		stampData.setStampText("Test header for stamping.");
		MimeType mimetype = null;
		try {
			mimetype = new MimeType("application/pdf");
		} catch (MimeTypeParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		List<File> files = super.listFilesByMimePath(mimetype);
		assertTrue("Files for " + mimetype.getPrimaryType() + " is null or empty.", (files != null) && !files.isEmpty());

		PdfReader pdfReader = null;
		for (File file : files) {
			if (!file.exists()) {
				fail("File enumerated by " + super.getClass().getSimpleName() + ".getFilesByMimePath() returned non-existent file "
						+ file.getPath());
			}

			System.out.println("File: " + file.getName());
			byte[] bytes;
			try {
				bytes = Files.readAllBytes(file.toPath());

				try {
					pdfReader = new PdfReader(bytes);
					// built in tests
					boolean is128Key = pdfReader.is128Key();
					System.out.println(".. is128Key:" + is128Key);
					boolean isAppendable = pdfReader.isAppendable();
					System.out.println(".. isAppendable:" + isAppendable);
					boolean isEncrypted = pdfReader.isEncrypted();
					System.out.println(".. isEncrypted:" + isEncrypted);
					boolean isMetadataEncrypted = pdfReader.isMetadataEncrypted();
					System.out.println(".. isMetadataEncrypted:" + isMetadataEncrypted);
					boolean isNewXrefType = pdfReader.isNewXrefType();
					System.out.println(".. isNewXrefType:" + isNewXrefType);
					boolean isOpenedWithFullPermissions = pdfReader.isOpenedWithFullPermissions();
					System.out.println(".. isOpenedWithFullPermissions:" + isOpenedWithFullPermissions);
					boolean isRebuilt = pdfReader.isRebuilt();
					System.out.println(".. isRebuilt:" + isRebuilt);
					boolean isTampered = pdfReader.isTampered();
					System.out.println(".. isTampered:" + isTampered);
					// get file info
					int certLevel = pdfReader.getCertificationLevel();
					System.out.println(".. certLevel:" + certLevel);
					int permissions = pdfReader.getPermissions();
					System.out.println(".. permissions:" + permissions);
					int cryptoMode = pdfReader.getCryptoMode();
					System.out.println(".. cryptoMode:" + cryptoMode);
					HashMap info = pdfReader.getInfo();
					System.out.println(".. info:" + info);
					byte[] metadata = pdfReader.getMetadata();
					System.out.println(".. metadata:" + metadata); // new String(metadata));
					char pdfVersion = pdfReader.getPdfVersion();
					System.out.println(".. pdfVersion:" + pdfVersion);
					// attempt to modify
					// PRIndirectReference addPdfObject = pdfReader.addPdfObject(new PdfObject());
					pdfReader.setAppendable(true);
					System.out.println(".. set appendable succeeded");
					pdfReader.setPageContent(1, "Hello!".getBytes());
					System.out.println(".. set page content succeeded");
					byte[] pageContent = pdfReader.getPageContent(1);
					System.out.println(".. pagecontent: " + new String(pageContent));
					byte[] pdf = stamper.stamp(stampData, bytes);
					super.saveFile(pdf, file.getName());
				} catch (Throwable e) {
					System.out.println("** ERROR " + e.getClass().getSimpleName() + ": " + e.getMessage());
				} finally {
					if (pdfReader != null) {
						try {
							pdfReader.close();
						} catch (Throwable e) {
							System.out.println("** ERROR " + e.getClass().getSimpleName() + ": " + e.getMessage());
						}
					}
					bytes = null;
				}

			} catch (Throwable e) {
				System.out.println("** ERROR " + e.getClass().getSimpleName() + ": " + e.getMessage());
			}
		}
		assertTrue(true);
	}

}
