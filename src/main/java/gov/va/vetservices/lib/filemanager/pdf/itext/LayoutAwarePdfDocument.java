package gov.va.vetservices.lib.filemanager.pdf.itext;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;

import gov.va.vetservices.lib.filemanager.exception.FileManagerException;

/**
 * A wrapper for {@link PdfDocument} that provides an intrinsic {@link Document} to declare and manage page layout dimensions
 * like page size, margins, etc. This wrapper eliminates having to pass the Document separately between methods and packages.
 *
 * @author aburkholder
 */
public class LayoutAwarePdfDocument extends PdfDocument {
	private static final long serialVersionUID = 4845270830809641462L;

	/** The object that defines and manages layout characteristics of the PDF */
	private transient Document document;
	private transient PdfDocumentOptions documentOptions;

	/**
	 * Create a new PDF.
	 *
	 * @param pdfBytes the PDF to modify
	 * @param properties stamping properties
	 */
	public LayoutAwarePdfDocument() {
		super(ItextUtils.getPdfWriter());
		initialize(null);
	}

	/**
	 * Modify an exiting PDF that is represented by the pdfBytes parameter. All {@link PdfDocumentOptions} are set to their default
	 * values.
	 *
	 * @param pdfBytes the PDF to modify
	 * @throws FileManagerException could not read pdfBytes
	 */
	public LayoutAwarePdfDocument(final byte[] pdfBytes) throws FileManagerException {
		super(ItextUtils.getPdfReader(pdfBytes), ItextUtils.getPdfWriter());
		initialize(null);
	}

	/**
	 * Modify an exiting PDF that is represented by the pdfBytes parameter, and specify {@link PdfDocumentOptions} with values other
	 * than the defaults.
	 *
	 * @param pdfBytes the PDF to modify
	 * @param properties stamping properties
	 * @throws FileManagerException could not read pdfBytes
	 */
	public LayoutAwarePdfDocument(final byte[] pdfBytes, final PdfDocumentOptions options) throws FileManagerException {
		super(ItextUtils.getPdfReader(pdfBytes), ItextUtils.getPdfWriter(), ItextUtils.getStampingProperties(options));
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
	 */
	public byte[] getOutput() {
		return ((ByteArrayPdfWriter) this.getWriter()).getDestinationBytes();
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
			// NOSONAR nothing to do here
		}

	}
}
