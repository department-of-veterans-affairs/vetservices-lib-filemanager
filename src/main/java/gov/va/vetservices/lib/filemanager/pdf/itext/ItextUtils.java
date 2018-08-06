package gov.va.vetservices.lib.filemanager.pdf.itext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;

import gov.va.vetservices.lib.filemanager.exception.FileManagerException;

public class ItextUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItextUtils.class);

	/** If ever needed, this variable can be refactored to a property to allow processing pw-protected files */
	private static final boolean PROCESS_PROTECTED_FILES = false;

	public static final String BEAN_NAME = "itextUtils";

	/**
	 * Do not instantiate.
	 */
	protected ItextUtils() {
		throw new IllegalAccessError("ItextUtils is a static class. Do not instantiate.");
	}

	/**
	 * Constructor helper to get the PdfReader required by the super constructor.
	 *
	 * @param pdfBytes the PDF bytes
	 * @return PdfReader
	 * @throws IOException some unexpected issue getting a reader on the byte array
	 * @throws FileManagerException could not read the PDF bytes
	 */
	public static PdfReader getPdfReader(final byte[] pdfBytes) throws IOException {
		PdfReader pdfReader = null;

		if (pdfBytes != null && pdfBytes.length > 0) {
			final RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
			final IRandomAccessSource source = factory.createSource(pdfBytes);
			try {
				pdfReader = new PdfReader(source, new ReaderProperties()); // NOSONAR reader is closed by the PdfDocument
			} catch (final Exception e) {
				// iText throws *run time* com.itextpdf.io.IOException - convert it to jav.io.IOException
				throw new IOException(e.getMessage(), e);
			}
			pdfReader.setUnethicalReading(PROCESS_PROTECTED_FILES);
			LOGGER.debug("... pdfReader.getFileLength(): " + pdfReader.getFileLength());
		}

		return pdfReader;
	}

	/**
	 * Constructor helper to create a new {@link ByteArrayPdfWriter}.
	 *
	 * @param byteArrayOutputStream the stream after PdfDocument is closed
	 * @return ByteArrayPdfWriter
	 */
	public static PdfWriter getPdfWriter(final ByteArrayOutputStream byteArrayOutputStream) {
		return new PdfWriter(byteArrayOutputStream);
	}

	/**
	 * Constructor helper to get stamping options if they exist.
	 * This method may return {@code null}.
	 *
	 * @param options the {@link PdfDocumentOptions}.
	 * @return the stampingProperties
	 */
	public static StampingProperties getStampingProperties(final PdfDocumentOptions options) {
		if (options != null && options.getStampingProperties() != null) {
			return options.getStampingProperties();
		}
		return new StampingProperties().useAppendMode();
	}

}
