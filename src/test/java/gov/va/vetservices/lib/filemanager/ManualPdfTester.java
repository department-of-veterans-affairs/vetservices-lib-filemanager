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

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.FingerPrint;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.collection.PdfCollection;
import com.itextpdf.pdfa.PdfADocument;
import com.itextpdf.pdfa.checker.PdfA1Checker;
import com.itextpdf.signatures.SignatureUtil;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.impl.dto.DocMetadataDto;
import gov.va.vetservices.lib.filemanager.pdf.itext.ItextUtils;
import gov.va.vetservices.lib.filemanager.pdf.itext.LayoutAwarePdfDocument;
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

	/** Value of true engages iText7 "unethicalReading" to decrypt password protections when possible */
	private static final boolean READ_PROTECTED_FILES = false;
	/** Controls whether console output includes exception stack traces or not */
	private static final boolean PRINT_STACKTRACES = true;

	private static final String CLAIM_ID = "11111";
	private static final String DOC_TYPE_ID = "123";

	private static final String FILES_CLASSPATH_PDF = "application/pdf";

//	@Ignore
	@Test
	public final void testTikaItext712() {
		final List<File> files = getFilesForMimeType(FILES_CLASSPATH_PDF);

		for (final File file : files) {
			if (!file.exists()) {
				fail("File enumerated by " + super.getClass().getSimpleName() + ".getFilesByMimePath() returned non-existent file "
						+ file.getPath());
			}

			System.out.println(StringUtils.repeat("-", 80));
			System.out.println("File: " + file.getName());

			PdfReader pdfReader = null;
			try {
				// use common method of getting reader ...
				pdfReader = ItextUtils.getPdfReader(Files.readAllBytes(file.toPath()));
				// read protected files? does not work for cert encrypted files ...
				pdfReader.setUnethicalReading(READ_PROTECTED_FILES);
				System.out.println("readProtectedAnyway:" + READ_PROTECTED_FILES);

				// PdfReader
				try {
					System.out.println(">> Start PdfReader operations");
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
					final String computeUserPassword =
							pdfReader.computeUserPassword() == null ? "null" : new String(pdfReader.computeUserPassword());
					System.out.println(".. computeUserPassword:" + computeUserPassword);
					final PdfAConformanceLevel get_STATED_PdfAConformanceLevel = pdfReader.getPdfAConformanceLevel();
					System.out.println(".. get_STATED_PdfAConformanceLevel:"
							+ (get_STATED_PdfAConformanceLevel == null ? "null" : get_STATED_PdfAConformanceLevel.getConformance()));
				} catch (final Throwable e) {
					System.out.println("** pdfReader ERROR " + e.getClass().getSimpleName() + ": "
							+ StringUtils.substringBefore(e.getMessage(), "\n"));
					if (PRINT_STACKTRACES) {
						e.printStackTrace();
					}
				}

				// PdfDocument
				LayoutAwarePdfDocument pdfDoc = null;
				try {
					System.out.println(">> Start PdfDocument operations");
					// re-read the file again, just to be safe
					pdfDoc = new LayoutAwarePdfDocument(Files.readAllBytes(file.toPath()));
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

					// PdfDocument -> PdfSignatures
					final SignatureUtil sigutil = new SignatureUtil(pdfDoc);
					final List<String> signames = sigutil.getSignatureNames();
					System.out.println(".. getSignatureNames:" + (signames == null ? "null" : signames.size()));
					if (signames != null && !signames.isEmpty()) {
						for (final String signame : signames) {
							System.out.println("...... name:" + signame);
							System.out.println("........ doesSignatureFieldExist:" + sigutil.doesSignatureFieldExist(signame));
							System.out.println("........ getTranslatedFieldName:" + sigutil.getTranslatedFieldName(signame));
							System.out
									.println("........ signatureCoversWholeDocument:" + sigutil.signatureCoversWholeDocument(signame));
							System.out.println("........ getSignature.getName:" + sigutil.getSignature(signame).getName());
							System.out.println("........ getSignature.getType:" + sigutil.getSignature(signame).getType().toString());
							System.out.println(
									"........ getSignature.getCert:" + (sigutil.getSignature(signame).getCert() == null ? "null"
											: sigutil.getSignature(signame).getCert().getType()));
							System.out.println("........ getSignature.getCert.getType:"
									+ (sigutil.getSignature(signame).getCert() == null ? "null"
											: sigutil.getSignature(signame).getCert().getType()));
							System.out.println("........ getSignature.getCert.getValue:"
									+ (sigutil.getSignature(signame).getCert() == null ? "null"
											: sigutil.getSignature(signame).getCert().getValue()));
						}
					}

					// PdfAcroForm
					final PdfAcroForm acroform = PdfAcroForm.getAcroForm(pdfDoc, false);
					System.out
							.println(".. hasAcroForm:" + (acroform != null && acroform.getFormFields().size() > 0 ? "true" : "false"));
					if (acroform != null) {
						System.out.println(".. hasXfaForm:" + acroform.hasXfaForm());
						System.out.println(".. getFormFields.size:"
								+ (acroform.getFormFields() == null ? "null" : acroform.getFormFields().size()));
					}

				} catch (final Throwable e) {
					System.out.println("** pdfDocument ERROR " + e.getClass().getSimpleName() + ": "
							+ StringUtils.substringBefore(e.getMessage(), "\n"));
					if (PRINT_STACKTRACES) {
						e.printStackTrace();
					}
				}

				// PdfStamper
				byte[] pdf = null;
				try {
					System.out.println(">> Start Stamper operations");
					final Stamper stamper = new Stamper();
					final StampDataDto stampDataDto = new StampDataDto();
					stampDataDto.setProcessType(ProcessType.CLAIMS_526);
					final DocMetadataDto docMetadata = new DocMetadataDto();
					docMetadata.setClaimId(CLAIM_ID);
					docMetadata.setDocTypeId(DOC_TYPE_ID);
					docMetadata.setProcessType(ProcessType.CLAIMS_526);
					// re-read the input file again, just to be safe...
					final FileDto fileDto = new FileDto();
					fileDto.setFilename(file.getName());
					fileDto.setFilebytes(Files.readAllBytes(file.toPath()));

					pdf = stamper.stamp(docMetadata, stampDataDto, fileDto);
					System.out.println(".. stamped " + file.getName());

				} catch (final Throwable e) {
					System.out.println("** Stamper ERROR " + e.getClass().getSimpleName() + ": "
							+ StringUtils.substringBefore(e.getMessage(), "\n"));
					if (PRINT_STACKTRACES) {
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
					if (PRINT_STACKTRACES) {
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
							if (PRINT_STACKTRACES) {
								e.printStackTrace();
							}
						}
					}
				}

			} catch (final Throwable e) {
				System.out.println("** readAllBytes ERROR " + e.getClass().getSimpleName() + ": "
						+ StringUtils.substringBefore(e.getMessage(), "\n"));
				if (PRINT_STACKTRACES) {
					e.printStackTrace();
				}
			}
		} // end for(){
		assertTrue(true);
	}

	@Ignore
	@Test
	public void testItext712_PdfA_Checker() {
		final List<File> files = getFilesForMimeType(FILES_CLASSPATH_PDF);

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
						if (PRINT_STACKTRACES) {
							e.printStackTrace();
						}
					}
				} catch (final Throwable e) {
					System.out.println("** pdfDocument ERROR " + e.getClass().getSimpleName() + ": "
							+ StringUtils.substringBefore(e.getMessage(), "\n"));
					if (PRINT_STACKTRACES) {
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
							if (PRINT_STACKTRACES) {
								e.printStackTrace();
							}
						}
					}
				}
			} catch (final Throwable e) {
				System.out.println("** readAllBytes ERROR " + e.getClass().getSimpleName() + ": "
						+ StringUtils.substringBefore(e.getMessage(), "\n"));
				if (PRINT_STACKTRACES) {
					e.printStackTrace();
				}
			}
		} // end for()
	}

	private List<File> getFilesForMimeType(final String classpathDir) {
		MimeType mimetype = null;
		try {
			mimetype = new MimeType(classpathDir);
		} catch (final MimeTypeParseException e) {
			e.printStackTrace();
		}
		final List<File> files = super.listFilesByMimePath(mimetype);
		assertTrue("Files for " + mimetype.getPrimaryType() + " is null or empty.", files != null && !files.isEmpty());
		return files;
	}
}
