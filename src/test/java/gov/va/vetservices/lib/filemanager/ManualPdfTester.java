package gov.va.vetservices.lib.filemanager;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PRAcroForm;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.impl.dto.DocMetadataDto;
import gov.va.vetservices.lib.filemanager.pdf.stamp.Stamper;
import gov.va.vetservices.lib.filemanager.pdf.stamp.dto.StampDataDto;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

/**
 * DO NOT DELETE
 * This jUnit test class allows for independent, manual testing of PDFs using iText.
 * Use the console output to review the iText interpretation of various PDF file states.
 *
 * @author aburkholder
 */
public class ManualPdfTester extends AbstractFileHandler {

	private static final boolean printStackTrace = false;

	private static final String claimId = "11111";
	private static final String docTypeId = "123";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void test() {
		Stamper stamper = new Stamper();
		StampDataDto stampDataDto = new StampDataDto();
		stampDataDto.setProcessType(ProcessType.CLAIMS_526);
		MimeType mimetype = null;
		try {
			mimetype = new MimeType("application/pdf");
		} catch (MimeTypeParseException e2) {
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

			System.out.println(StringUtils.repeat("-", 80));
			System.out.println("File: " + file.getName());
			byte[] bytes;
			try {
				bytes = Files.readAllBytes(file.toPath());

				try {
					pdfReader = new PdfReader(bytes);
					// built in tests
					int fileLen = pdfReader.getFileLength();
					System.out.println(".. file length:" + fileLen);
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
					HashMap<?, ?> info = pdfReader.getInfo();
					System.out.println(".. info:" + info);
					byte[] metadata = pdfReader.getMetadata();
					System.out.println(".. metadata:" + metadata); // new String(metadata));
					char pdfVersion = pdfReader.getPdfVersion();
					System.out.println(".. pdfVersion:" + pdfVersion);
					AcroFields acroFields = pdfReader.getAcroFields();
					System.out.println(".. signature names: "
							+ ((acroFields != null) && (acroFields.getSignatureNames() != null) ? acroFields.getSignatureNames().size()
									: "null"));
					if ((acroFields != null) && (acroFields.getSignatureNames() != null)) {
						for (Object signame : acroFields.getSignatureNames()) {
							System.out.println("...... " + (signame == null ? "null" : (String) signame));
						}
					}
					PRAcroForm acroForm = pdfReader.getAcroForm();
					System.out
							.println(".. acroForm can be in ObjectStream: " + (acroForm == null ? "null" : acroForm.canBeInObjStm()));
					PdfDictionary dict = pdfReader.getCatalog();
					System.out.println(".. dictionary key/value pairs: " + (dict == null ? "null" : dict.size()));
					if (dict != null) {
						for (Object key : dict.getKeys()) {
							System.out.println("...... " + ((PdfName) key).toString() + " (type " + ((PdfName) key).type() + "): "
									+ StringUtils.replace(dict.get((PdfName) key).toString(), "\n", "_"));
						}
					}
					PdfDictionary trailer = pdfReader.getTrailer();
					System.out.println(".. trailer: key/value pairs: " + (trailer == null ? "null" : trailer.size()));
					if (trailer != null) {
						for (Object key : trailer.getKeys()) {
							System.out.println("...... " + ((PdfName) key).toString() + " (type " + ((PdfName) key).type() + "): "
									+ StringUtils.replace(trailer.get((PdfName) key).toString(), "\n", "_"));
						}
					}
					int xrefs = pdfReader.getXrefSize();
					System.out.println(".. xref number: " + xrefs);
					@SuppressWarnings("unchecked")
					HashMap<PdfName, PdfArray> namedest = pdfReader.getNamedDestination();
					System.out.println(".. named Destinations: " + (namedest == null ? "null" : namedest.size()));
					if (namedest != null) {
						for (PdfName name : namedest.keySet()) {
							System.out.println("...... " + name.toString() + ": " + namedest.get(name));
						}
					}
					int numPages = pdfReader.getNumberOfPages();
					System.out.println(".. number of pages: " + numPages);
					int simpleViewerPrefs = pdfReader.getSimpleViewerPreferences();
					System.out.println(".. Simple Viewer Prefs: " + simpleViewerPrefs);

					// attempt to modify
					// PRIndirectReference addPdfObject = pdfReader.addPdfObject(new PdfObject());
					pdfReader.setAppendable(true);
					System.out.println(".. set appendable succeeded");
					pdfReader.setPageContent(1, "Hello!".getBytes());
					System.out.println(".. set page content succeeded");
					byte[] pageContent = pdfReader.getPageContent(1);
					System.out.println(".. pagecontent: " + new String(pageContent));

				} catch (Throwable e) {
					System.out.println("** pdfReader ERROR " + e.getClass().getSimpleName() + ": "
							+ StringUtils.substringBefore(e.getMessage(), "\n"));
					if (printStackTrace) {
						e.printStackTrace();
					}
				} finally {
					if (pdfReader != null) {
						try {
							pdfReader.close();
						} catch (Throwable e) {
							System.out.println("** Closing pdfReader ERROR " + e.getClass().getSimpleName() + ": "
									+ StringUtils.substringBefore(e.getMessage(), "\n"));
						}
					}
				}
				byte[] pdf = null;
				try {
					DocMetadataDto docMetadata = new DocMetadataDto();
					docMetadata.setClaimId(claimId);
					docMetadata.setDocTypeId(docTypeId);
					docMetadata.setProcessType(ProcessType.CLAIMS_526);
					FileDto fileDto = new FileDto();
					fileDto.setFilename(file.getName());
					fileDto.setFilebytes(bytes);
					pdf = stamper.stamp(docMetadata, stampDataDto, fileDto);
					System.out.println(".. stamped " + file.getName());
				} catch (Throwable e) {
					System.out.println("** Stamper ERROR " + e.getClass().getSimpleName() + ": "
							+ StringUtils.substringBefore(e.getMessage(), "\n"));
					if (printStackTrace) {
						e.printStackTrace();
					}
				}
				try {
					if (pdf != null) {
						super.saveFile(pdf, file.getName());
						System.out.println(".. saved " + file.getName());
					}
				} catch (Throwable e) {
					System.out.println("** save ERROR " + e.getClass().getSimpleName() + ": "
							+ StringUtils.substringBefore(e.getMessage(), "\n"));
					if (printStackTrace) {
						e.printStackTrace();
					}
				}

			} catch (Throwable e) {
				System.out.println("** readAllBytes ERROR " + e.getClass().getSimpleName() + ": "
						+ StringUtils.substringBefore(e.getMessage(), "\n"));
				if (printStackTrace) {
					e.printStackTrace();
				}
			} finally {
				bytes = null;
			}
		} // end for(){
		assertTrue(true);
	}

}
