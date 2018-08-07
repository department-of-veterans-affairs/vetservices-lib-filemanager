package gov.va.vetservices.lib.filemanager.pdf.itext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;

import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;

/**
 * A wrapper for {@link PdfDocument} that provides an intrinsic {@link Document} to declare and manage page layout dimensions
 * like page size, margins, etc. This wrapper eliminates having to pass the Document separately between methods and packages.
 *
 * @author aburkholder
 */
@Component(LayoutAwarePdfDocument.BEAN_NAME)
public class LayoutAwarePdfDocument extends PdfDocument {
	private static final long serialVersionUID = 4845270830809641462L;

	public static final String BEAN_NAME = "layoutAwarePdfDocument";

	private static final Logger LOGGER = LoggerFactory.getLogger(LayoutAwarePdfDocument.class);

	/** The object that defines and manages layout characteristics of the PDF */
	private transient Document document;
	/** Options governing behavior of the document */
	private transient PdfDocumentOptions documentOptions;
	/** the output bytes (only available after {@code close()} has been called) */

	private transient ByteArrayOutputStream destination;

	/**
	 * Create a new PDF.
	 *
	 * @param pdfBytes the PDF to modify
	 * @param properties stamping properties
	 */
	public LayoutAwarePdfDocument() {
		/*
		 * Dev note:
		 * Since class-level variables aren't available until after super(),
		 * the ByteArrayOutputStream needs to be retrieved from PdfWriter afterward.
		 */
		super(ItextUtils.getPdfWriter(new ByteArrayOutputStream()));
		destination = (ByteArrayOutputStream) this.getWriter().getOutputStream();
		initialize(null);
	}

	/**
	 * Modify an exiting PDF that is represented by the pdfBytes parameter. All {@link PdfDocumentOptions} are set to their default
	 * values.
	 *
	 * @param pdfBytes the PDF to modify
	 * @throws IOException some unexpected issue getting a reader from the byte array
	 */
	public LayoutAwarePdfDocument(final byte[] pdfBytes) throws IOException {
		/*
		 * Dev note:
		 * Since class-level variables aren't available until after super(),
		 * the ByteArrayOutputStream needs to be retrieved from PdfWriter afterward.
		 */
		super(ItextUtils.getPdfReader(pdfBytes), ItextUtils.getPdfWriter(new ByteArrayOutputStream()));
		destination = (ByteArrayOutputStream) this.getWriter().getOutputStream();
		initialize(null);
	}

	/**
	 * Modify an exiting PDF that is represented by the pdfBytes parameter, and specify {@link PdfDocumentOptions} with values other
	 * than the defaults.
	 *
	 * @param pdfBytes the PDF to modify
	 * @param properties stamping properties
	 * @throws IOException some unexpected issue getting a reader from the byte array
	 */
	public LayoutAwarePdfDocument(final byte[] pdfBytes, final PdfDocumentOptions options) throws IOException {
		/*
		 * Dev note:
		 * Since class-level variables aren't available until after super(),
		 * the ByteArrayOutputStream needs to be retrieved from PdfWriter afterward.
		 */
		super(ItextUtils.getPdfReader(pdfBytes),
				ItextUtils.getPdfWriter(new ByteArrayOutputStream()),
				ItextUtils.getStampingProperties(options));
		destination = (ByteArrayOutputStream) this.getWriter().getOutputStream();
		initialize(options);
	}

	/**
	 * Constructor helper to apply properties and dimensions to the new PDF.
	 */
	private void initialize(PdfDocumentOptions options) {
		this.setCloseReader(true);
		this.setCloseWriter(true);

		if (options == null) {
			// use default values
			options = new PdfDocumentOptions(); // NOSONAR - intentionally reassigning the same var
		}
		this.documentOptions = options;

		final PageSize usePageSize =
				documentOptions.isRotated() ? documentOptions.getPageSize().rotate() : documentOptions.getPageSize();
		this.setDefaultPageSize(usePageSize);
		this.document = new Document(this);
		this.document.setTextAlignment(documentOptions.getTextAlignment())
				.setHyphenation(documentOptions.getHyphenation())
				.setFont(documentOptions.getFont())
				.setFontSize(documentOptions.getFontSize());
	}

	/**
	 * Get the PDF output as bytes, including any modifications that were made to the {@link PdfDocument}.
	 *
	 * @return byte[] the PdfDocument
	 * @throws PdfException if problem closing the PdfDocument
	 */
	public byte[] closeAndGetOutput() {
		if (!this.isClosed()) {
			this.close();
		}
		return destination == null ? null : destination.toByteArray();
	}

	/**
	 * Get the {@link Document} layout document object.
	 *
	 * @return Document
	 */
	public Document getLayoutDocument() {
		return this.document;
	}

	/**
	 * Get the {@link PdfDocumentOptions} object that contains the default options for the document.
	 *
	 * @return PdfDocumentOptions
	 */
	public PdfDocumentOptions getDocumentOptions() {
		return this.documentOptions;
	}

	/**
	 * Close the PdfDocument.
	 *
	 * @throws PdfException if problem closing the PdfDocument
	 */
	@Override
	public void close() {
		/*
		 * Dev Note:
		 * Do not try to close this.document
		 * The layout document is automatically closed with super.close()
		 * So attempting to close this.document here causes stack overflow
		 */
		// close this PdfDocument
		try {
			super.close();
		} catch (final Exception e) { // NOSONAR catch everything
			final MessageKeysEnum messageKeys = MessageKeysEnum.PDF_ISSUE;

			LOGGER.error(messageKeys.getKey() + ": " + messageKeys.getMessage(), e);
			throw e;
		}

	}
}
