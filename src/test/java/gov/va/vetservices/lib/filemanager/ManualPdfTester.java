package gov.va.vetservices.lib.filemanager;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.FingerPrint;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
//import com.lowagie.text.pdf.AcroFields;
//import com.lowagie.text.pdf.PRAcroForm;
//import com.lowagie.text.pdf.PdfArray;
//import com.lowagie.text.pdf.PdfDictionary;
//import com.lowagie.text.pdf.PdfName;
//import com.lowagie.text.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.collection.PdfCollection;
import com.itextpdf.pdfa.PdfADocument;
import com.itextpdf.pdfa.checker.PdfA1Checker;

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

	private static final boolean readProtectedAnyway = true;
	private static final boolean printStackTrace = false;

	private static final String claimId = "11111";
	private static final String docTypeId = "123";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Ignore
	@Test
	public final void testTikaItext217() {
//		Stamper stamper = new Stamper();
//		StampDataDto stampDataDto = new StampDataDto();
//		stampDataDto.setProcessType(ProcessType.CLAIMS_526);
//		MimeType mimetype = null;
//		try {
//			mimetype = new MimeType("application/pdf");
//		} catch (MimeTypeParseException e2) {
//			e2.printStackTrace();
//		}
//		List<File> files = super.listFilesByMimePath(mimetype);
//		assertTrue("Files for " + mimetype.getPrimaryType() + " is null or empty.", (files != null) && !files.isEmpty());
//
//		PdfReader pdfReader = null;
//		for (File file : files) {
//			if (!file.exists()) {
//				fail("File enumerated by " + super.getClass().getSimpleName() + ".getFilesByMimePath() returned non-existent file "
//						+ file.getPath());
//			}
//
//			System.out.println(StringUtils.repeat("-", 80));
//			System.out.println("File: " + file.getName());
//			byte[] bytes;
//			try {
//				bytes = Files.readAllBytes(file.toPath());
//
//				try {
//					pdfReader = new PdfReader(bytes);
//					// built in tests
//					int fileLen = pdfReader.getFileLength();
//					System.out.println(".. file length:" + fileLen);
//					boolean is128Key = pdfReader.is128Key();
//					System.out.println(".. is128Key:" + is128Key);
//					boolean isAppendable = pdfReader.isAppendable();
//					System.out.println(".. isAppendable:" + isAppendable);
//					boolean isEncrypted = pdfReader.isEncrypted();
//					System.out.println(".. isEncrypted:" + isEncrypted);
//					boolean isMetadataEncrypted = pdfReader.isMetadataEncrypted();
//					System.out.println(".. isMetadataEncrypted:" + isMetadataEncrypted);
//					boolean isNewXrefType = pdfReader.isNewXrefType();
//					System.out.println(".. isNewXrefType:" + isNewXrefType);
//					boolean isOpenedWithFullPermissions = pdfReader.isOpenedWithFullPermissions();
//					System.out.println(".. isOpenedWithFullPermissions:" + isOpenedWithFullPermissions);
//					boolean isRebuilt = pdfReader.isRebuilt();
//					System.out.println(".. isRebuilt:" + isRebuilt);
//					boolean isTampered = pdfReader.isTampered();
//					System.out.println(".. isTampered:" + isTampered);
//					// get file info
//					int certLevel = pdfReader.getCertificationLevel();
//					System.out.println(".. certLevel:" + certLevel);
//					int permissions = pdfReader.getPermissions();
//					System.out.println(".. permissions:" + permissions);
//					int cryptoMode = pdfReader.getCryptoMode();
//					System.out.println(".. cryptoMode:" + cryptoMode);
//					HashMap<?, ?> info = pdfReader.getInfo();
//					System.out.println(".. info:" + info);
//					byte[] metadata = pdfReader.getMetadata();
//					System.out.println(".. metadata:" + metadata); // new String(metadata));
//					char pdfVersion = pdfReader.getPdfVersion();
//					System.out.println(".. pdfVersion:" + pdfVersion);
//					AcroFields acroFields = pdfReader.getAcroFields();
//					System.out.println(".. signature names: "
//							+ ((acroFields != null) && (acroFields.getSignatureNames() != null) ? acroFields.getSignatureNames().size()
//									: "null"));
//					if ((acroFields != null) && (acroFields.getSignatureNames() != null)) {
//						for (Object signame : acroFields.getSignatureNames()) {
//							System.out.println("...... " + (signame == null ? "null" : (String) signame));
//						}
//					}
//					PRAcroForm acroForm = pdfReader.getAcroForm();
//					System.out
//							.println(".. acroForm can be in ObjectStream: " + (acroForm == null ? "null" : acroForm.canBeInObjStm()));
//					PdfDictionary dict = pdfReader.getCatalog();
//					System.out.println(".. dictionary key/value pairs: " + (dict == null ? "null" : dict.size()));
//					if (dict != null) {
//						for (Object key : dict.getKeys()) {
//							System.out.println("...... " + ((PdfName) key).toString() + " (type " + ((PdfName) key).type() + "): "
//									+ StringUtils.replace(dict.get((PdfName) key).toString(), "\n", "_"));
//						}
//					}
//					PdfDictionary trailer = pdfReader.getTrailer();
//					System.out.println(".. trailer: key/value pairs: " + (trailer == null ? "null" : trailer.size()));
//					if (trailer != null) {
//						for (Object key : trailer.getKeys()) {
//							System.out.println("...... " + ((PdfName) key).toString() + " (type " + ((PdfName) key).type() + "): "
//									+ StringUtils.replace(trailer.get((PdfName) key).toString(), "\n", "_"));
//						}
//					}
//					int xrefs = pdfReader.getXrefSize();
//					System.out.println(".. xref number: " + xrefs);
//					@SuppressWarnings("unchecked")
//					HashMap<PdfName, PdfArray> namedest = pdfReader.getNamedDestination();
//					System.out.println(".. named Destinations: " + (namedest == null ? "null" : namedest.size()));
//					if (namedest != null) {
//						for (PdfName name : namedest.keySet()) {
//							System.out.println("...... " + name.toString() + ": " + namedest.get(name));
//						}
//					}
//					int numPages = pdfReader.getNumberOfPages();
//					System.out.println(".. number of pages: " + numPages);
//					int simpleViewerPrefs = pdfReader.getSimpleViewerPreferences();
//					System.out.println(".. Simple Viewer Prefs: " + simpleViewerPrefs);
//
//					// attempt to modify
//					// PRIndirectReference addPdfObject = pdfReader.addPdfObject(new PdfObject());
//					pdfReader.setAppendable(true);
//					System.out.println(".. set appendable succeeded");
//					pdfReader.setPageContent(1, "Hello!".getBytes());
//					System.out.println(".. set page content succeeded");
//					byte[] pageContent = pdfReader.getPageContent(1);
//					System.out.println(".. pagecontent: " + new String(pageContent));
//
//				} catch (Throwable e) {
//					System.out.println("** pdfReader ERROR " + e.getClass().getSimpleName() + ": "
//							+ StringUtils.substringBefore(e.getMessage(), "\n"));
//					if (printStackTrace) {
//						e.printStackTrace();
//					}
//				} finally {
//					if (pdfReader != null) {
//						try {
//							pdfReader.close();
//						} catch (Throwable e) {
//							System.out.println("** Closing pdfReader ERROR " + e.getClass().getSimpleName() + ": "
//									+ StringUtils.substringBefore(e.getMessage(), "\n"));
//						}
//					}
//				}
//				byte[] pdf = null;
//				try {
//					DocMetadataDto docMetadata = new DocMetadataDto();
//					docMetadata.setClaimId(claimId);
//					docMetadata.setDocTypeId(docTypeId);
//					docMetadata.setProcessType(ProcessType.CLAIMS_526);
//					FileDto fileDto = new FileDto();
//					fileDto.setFilename(file.getName());
//					fileDto.setFilebytes(bytes);
//					pdf = stamper.stamp(docMetadata, stampDataDto, fileDto);
//					System.out.println(".. stamped " + file.getName());
//				} catch (Throwable e) {
//					System.out.println("** Stamper ERROR " + e.getClass().getSimpleName() + ": "
//							+ StringUtils.substringBefore(e.getMessage(), "\n"));
//					if (printStackTrace) {
//						e.printStackTrace();
//					}
//				}
//				try {
//					if (pdf != null) {
//						super.saveFile(pdf, file.getName());
//						System.out.println(".. saved " + file.getName());
//					}
//				} catch (Throwable e) {
//					System.out.println("** save ERROR " + e.getClass().getSimpleName() + ": "
//							+ StringUtils.substringBefore(e.getMessage(), "\n"));
//					if (printStackTrace) {
//						e.printStackTrace();
//					}
//				}
//
//			} catch (Throwable e) {
//				System.out.println("** readAllBytes ERROR " + e.getClass().getSimpleName() + ": "
//						+ StringUtils.substringBefore(e.getMessage(), "\n"));
//				if (printStackTrace) {
//					e.printStackTrace();
//				}
//			} finally {
//				bytes = null;
//			}
//		} // end for(){
		assertTrue(true);
	}

	@Test
	public final void testTikaItext712() {
		MimeType mimetype = null;
		try {
			mimetype = new MimeType("application/pdf");
		} catch (final MimeTypeParseException e2) {
			e2.printStackTrace();
		}
		final List<File> files = super.listFilesByMimePath(mimetype);
		assertTrue("Files for " + mimetype.getPrimaryType() + " is null or empty.", files != null && !files.isEmpty());

		PdfReader pdfReader = null;
		PdfWriter pdfWriter = null;
		PdfDocument pdfDoc = null;
		for (final File file : files) {
			if (!file.exists()) {
				fail("File enumerated by " + super.getClass().getSimpleName() + ".getFilesByMimePath() returned non-existent file "
						+ file.getPath());
			}

			System.out.println(StringUtils.repeat("-", 80));
			System.out.println("File: " + file.getName());
			InputStream inputstream = null;
			ByteArrayOutputStream outputstream = null;
			try {
				inputstream = Files.newInputStream(file.toPath());
				pdfReader = new PdfReader(inputstream);
				pdfReader.setUnethicalReading(readProtectedAnyway);
				System.out.println("readProtectedAnyway:" + readProtectedAnyway);

				// PdfReader
				try {
					// built in tests
					final long fileLen = pdfReader.getFileLength();
					System.out.println(".. file length:" + fileLen);
					final boolean isCloseStream = pdfReader.isCloseStream();
					System.out.println(".. isCloseStream:" + isCloseStream);
					final boolean isEncrypted = pdfReader.isEncrypted();
					System.out.println(".. isEncrypted:" + isEncrypted);
					final boolean isOpenedWithFullPermission = pdfReader.isOpenedWithFullPermission();
					System.out.println(".. isOpenedWithFullPermission:" + isOpenedWithFullPermission);
					final boolean hasHybridXref = pdfReader.hasHybridXref();
					System.out.println(".. hasHybridXref:" + hasHybridXref);
					final boolean hasFixedXref = pdfReader.hasFixedXref();
					System.out.println(".. hasFixedXref:" + hasFixedXref);
					final boolean hasRebuiltXref = pdfReader.hasRebuiltXref();
					System.out.println(".. hasRebuiltXref:" + hasRebuiltXref);
					final boolean hasXrefStm = pdfReader.hasXrefStm();
					System.out.println(".. hasXrefStm:" + hasXrefStm);
					final int cryptoMode = pdfReader.getCryptoMode();
					System.out.println(".. cryptoMode:" + cryptoMode);
					final long getPermissions = pdfReader.getPermissions();
					System.out.println(".. getPermissions:" + getPermissions);
					final PdfAConformanceLevel get_STATED_PdfAConformanceLevel = pdfReader.getPdfAConformanceLevel();
					System.out.println(".. get_STATED_PdfAConformanceLevel:"
							+ (get_STATED_PdfAConformanceLevel == null ? "null" : get_STATED_PdfAConformanceLevel.getConformance()));
				} catch (final Throwable e) {
					System.out.println("** pdfReader ERROR " + e.getClass().getSimpleName() + ": "
							+ StringUtils.substringBefore(e.getMessage(), "\n"));
					if (printStackTrace) {
						e.printStackTrace();
					}
				}

				outputstream = new ByteArrayOutputStream();
				pdfWriter = new PdfWriter(outputstream);
				// PdfDocument
				try {
					pdfDoc = new PdfDocument(pdfReader, pdfWriter);
					// built in tests
					final boolean isAppendMode = pdfDoc.isAppendMode();
					System.out.println(".. isAppendMode:" + isAppendMode);
					final boolean isCloseReader = pdfDoc.isCloseReader();
					System.out.println(".. isCloseReader:" + isCloseReader);
					final boolean isCloseWriter = pdfDoc.isCloseWriter();
					System.out.println(".. isCloseWriter:" + isCloseWriter);
					final boolean isFlushUnusedObjects = pdfDoc.isFlushUnusedObjects();
					System.out.println(".. isFlushUnusedObjects:" + isFlushUnusedObjects);
					final boolean isTagged = pdfDoc.isTagged();
					System.out.println(".. isTagged:" + isTagged);
					final boolean hasOutlines = pdfDoc.hasOutlines();
					System.out.println(".. hasOutlines:" + hasOutlines);
					final PdfArray getAssociatedFiles = pdfDoc.getAssociatedFiles();
					System.out
							.println(".. getAssociatedFiles:" + (getAssociatedFiles == null ? "null" : getAssociatedFiles.toString()));
					final PdfCatalog getCatalog = pdfDoc.getCatalog();
					System.out.println(".. getCatalog:" + (getCatalog == null ? "null" : getCatalog.toString()));
					if (getCatalog != null) {
						final PdfCollection getCollection = getCatalog.getCollection();
						System.out.println("...... getCollection:" + (getCollection == null ? "null"
								: "isViewDetails:" + getCollection.isViewDetails() + ";isViewHidden:" + getCollection.isViewHidden()
										+ ";isViewTile:" + getCollection.isViewTile()));
						final PdfString getLang = getCatalog.getLang();
						System.out.println("...... getLang:" + (getLang == null ? "null" : getLang.getValue()));
						final PdfName getPageLayout = getCatalog.getPageLayout();
						System.out.println("...... getPageLayout:" + (getPageLayout == null ? "null" : getPageLayout.getValue()));
						final PdfName getPageMode = getCatalog.getPageMode();
						System.out.println("...... getPageMode:" + (getPageMode == null ? "null" : getPageMode.getValue()));
						final PdfDictionary getPdfObject = getCatalog.getPdfObject();
						System.out.println("...... getPdfObject:" + (getPdfObject == null ? "null"
								: "isArray:" + getPdfObject.isArray() + ";isBoolean:" + getPdfObject.isBoolean() + ";isDictionary:"
										+ getPdfObject.isDictionary() + ";isEmpty:" + getPdfObject.isEmpty() + ";isLiteral:"
										+ getPdfObject.isLiteral() + ";isName:" + getPdfObject.isName() + ";isNull:"
										+ getPdfObject.isNull() + ";isNumber:" + getPdfObject.isNumber() + ";isStream:"
										+ getPdfObject.isStream() + ";isString:" + getPdfObject.isString() + "\n........ toString:"
										+ getPdfObject.toString()));
						final PdfViewerPreferences getViewerPreferences = getCatalog.getViewerPreferences();
						System.out.println("...... getViewerPreferences:"
								+ (getViewerPreferences == null ? "null" : getViewerPreferences.getEnforce().toString()));
					}
					final PdfFont getDefaultFont = pdfDoc.getDefaultFont();
					System.out.println(".. getDefaultFont:" + (getDefaultFont == null ? "null" : getDefaultFont.toString()));
					if (getDefaultFont != null) {
						System.out.println("...... isEmbedded:" + getDefaultFont.isEmbedded());
						System.out.println("...... getRegistry:"
								+ (getDefaultFont.getFontProgram() == null ? "null" : getDefaultFont.getFontProgram().getRegistry()));
						System.out.println("...... getFontName:" + (getDefaultFont.getFontProgram() == null ? "null"
								: getDefaultFont.getFontProgram().getFontNames().getFontName()));
					}
					final PageSize getDefaultPageSize = pdfDoc.getDefaultPageSize();
					System.out.println(".. getDefaultPageSize(L,B,R,T):" + (getDefaultPageSize == null ? "null"
							: getDefaultPageSize.getLeft() + ", " + getDefaultPageSize.getBottom() + ", "
									+ getDefaultPageSize.getRight() + ", " + getDefaultPageSize.getTop()));
					final PdfDocumentInfo getDocumentInfo = pdfDoc.getDocumentInfo();
					System.out.println(".. getDocumentInfo:" + (getDocumentInfo == null ? "null" : getDocumentInfo.toString()));
					System.out.println("...... getAuthor:" + (getDocumentInfo == null ? "null" : getDocumentInfo.getAuthor()));
					System.out.println("...... getCreator:" + (getDocumentInfo == null ? "null" : getDocumentInfo.getCreator()));
					System.out.println("...... getKeywords:" + (getDocumentInfo == null ? "null" : getDocumentInfo.getKeywords()));
					System.out.println("...... getProducer:" + (getDocumentInfo == null ? "null" : getDocumentInfo.getProducer()));
					System.out.println("...... getSubject:" + (getDocumentInfo == null ? "null" : getDocumentInfo.getSubject()));
					System.out.println("...... getTitle:" + (getDocumentInfo == null ? "null" : getDocumentInfo.getTitle()));
					final FingerPrint getFingerPrint = pdfDoc.getFingerPrint();
					System.out.println(".. getFingerPrint:" + (getFingerPrint == null ? "null" : getFingerPrint.toString()));
					System.out.println("...... getProducts.size:"
							+ (getFingerPrint.getProducts() == null ? "null" : getFingerPrint.getProducts().size()));
					final int getNumberOfPages = pdfDoc.getNumberOfPages();
					System.out.println(".. getNumberOfPages:" + getNumberOfPages);
					final int getNumberOfPdfObjects = pdfDoc.getNumberOfPdfObjects();
					System.out.println(".. getNumberOfPdfObjects:" + getNumberOfPdfObjects);
					final String[] getPageLabels = pdfDoc.getPageLabels();
					System.out.println(".. getPageLabels:" + (getPageLabels == null ? "null" : Arrays.toString(getPageLabels)));
					final PdfDictionary getTrailer = pdfDoc.getTrailer();
					System.out.println("...... getTrailer:" + (getTrailer == null ? "null"
							: "isArray:" + getTrailer.isArray() + ";isBoolean:" + getTrailer.isBoolean() + ";isDictionary:"
									+ getTrailer.isDictionary() + ";isEmpty:" + getTrailer.isEmpty() + ";isLiteral:"
									+ getTrailer.isLiteral() + ";isName:" + getTrailer.isName() + ";isNull:" + getTrailer.isNull()
									+ ";isNumber:" + getTrailer.isNumber() + ";isStream:" + getTrailer.isStream() + ";isString:"
									+ getTrailer.isString() + "\n........ toString:" + getTrailer.toString()));
					final PdfVersion getPdfVersion = pdfDoc.getPdfVersion();
					System.out.println(".. getPdfVersion:" + getPdfVersion.toString());
				} catch (final Throwable e) {
					System.out.println("** pdfDocument ERROR " + e.getClass().getSimpleName() + ": "
							+ StringUtils.substringBefore(e.getMessage(), "\n"));
					if (printStackTrace) {
						e.printStackTrace();
					}
				}

				// PdfStamper
				byte[] pdf = null;
				try {
					final Stamper stamper = new Stamper();
					final StampDataDto stampDataDto = new StampDataDto();
					stampDataDto.setProcessType(ProcessType.CLAIMS_526);
					final DocMetadataDto docMetadata = new DocMetadataDto();
					docMetadata.setClaimId(claimId);
					docMetadata.setDocTypeId(docTypeId);
					docMetadata.setProcessType(ProcessType.CLAIMS_526);
					final FileDto fileDto = new FileDto();
					fileDto.setFilename(file.getName());
					inputstream = Files.newInputStream(file.toPath());
					fileDto.setFilebytes(IOUtils.toByteArray(inputstream));
					pdf = stamper.stamp(docMetadata, stampDataDto, fileDto);
					System.out.println(".. stamped " + file.getName());
				} catch (final Throwable e) {
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
				} catch (final Throwable e) {
					System.out.println("** save ERROR " + e.getClass().getSimpleName() + ": "
							+ StringUtils.substringBefore(e.getMessage(), "\n"));
					if (printStackTrace) {
						e.printStackTrace();
					}
				} finally {
					if (pdfDoc != null) {
						try {
							pdfDoc.setCloseReader(true);
							pdfDoc.setCloseWriter(true);
							pdfDoc.close();
						} catch (final Throwable e) {
							System.out.println("** Closing pdfDocument ERROR " + e.getClass().getSimpleName() + ": "
									+ StringUtils.substringBefore(e.getMessage(), "\n"));
						}
					}
				}

			} catch (final Throwable e) {
				System.out.println("** readAllBytes ERROR " + e.getClass().getSimpleName() + ": "
						+ StringUtils.substringBefore(e.getMessage(), "\n"));
				if (printStackTrace) {
					e.printStackTrace();
				}
			}
		} // end for(){
		assertTrue(true);
	}

//	@Ignore
	@Test
	public void testItext217_PdfA_Checker() {
		MimeType mimetype = null;
		try {
			mimetype = new MimeType("application/pdf");
		} catch (final MimeTypeParseException e2) {
			e2.printStackTrace();
		}
		final List<File> files = super.listFilesByMimePath(mimetype);
		assertTrue("Files for " + mimetype.getPrimaryType() + " is null or empty.", files != null && !files.isEmpty());

		PdfReader pdfReader = null;
		PdfWriter pdfWriter = null;

		PdfA1Checker pdfA1Checker = null;
		PdfADocument pdfADoc = null;

		for (final File file : files) {
			if (!file.exists()) {
				fail("File enumerated by " + super.getClass().getSimpleName() + ".getFilesByMimePath() returned non-existent file "
						+ file.getPath());
			}

			System.out.println(StringUtils.repeat("-", 80));
			System.out.println("File: " + file.getName());
			InputStream inputstream = null;
			ByteArrayOutputStream outputstream = null;
			try {
				outputstream = new ByteArrayOutputStream();
				inputstream = Files.newInputStream(file.toPath());
				pdfReader = new PdfReader(inputstream);
				pdfWriter = new PdfWriter(outputstream);

				pdfADoc = new PdfADocument(pdfReader, pdfWriter);
				pdfA1Checker = new PdfA1Checker(PdfAConformanceLevel.PDF_A_1A);
				// PdfA1Checker
				try {
//					pdfADoc.rea
					for (int pageNum = 1; pageNum <= pdfADoc.getNumberOfPages(); pageNum++) {
						final PdfPage page = pdfADoc.getPage(pageNum);
						pdfA1Checker.checkSinglePage(page);
					}

					try {
						final byte[] bytes = outputstream.toByteArray();
						if (bytes != null) {
							super.saveFile(bytes, file.getName());
							System.out.println(".. saved " + file.getName());
						}
					} catch (final Throwable e) {
						System.out.println("** save ERROR " + e.getClass().getSimpleName() + ": "
								+ StringUtils.substringBefore(e.getMessage(), "\n"));
						if (printStackTrace) {
							e.printStackTrace();
						}
					}
				} catch (final Throwable e) {
					System.out.println("** pdfDocument ERROR " + e.getClass().getSimpleName() + ": "
							+ StringUtils.substringBefore(e.getMessage(), "\n"));
					if (printStackTrace) {
						e.printStackTrace();
					}
				} finally {
					if (pdfADoc != null) {
						try {
							pdfADoc.setCloseReader(true);
							pdfADoc.setCloseWriter(true);
							pdfADoc.close();
						} catch (final Throwable e) {
							System.out.println("** Closing pdfDocument ERROR " + e.getClass().getSimpleName() + ": "
									+ StringUtils.substringBefore(e.getMessage(), "\n"));
						}
					}
				}
			} catch (final Throwable e) {
				System.out.println("** readAllBytes ERROR " + e.getClass().getSimpleName() + ": "
						+ StringUtils.substringBefore(e.getMessage(), "\n"));
				if (printStackTrace) {
					e.printStackTrace();
				}
			}
		} // end for()
	}

	/**
	 * The POM must include pdfbox dependency
	 */
	@Test
	public void testPdfBox() {

	}

}
