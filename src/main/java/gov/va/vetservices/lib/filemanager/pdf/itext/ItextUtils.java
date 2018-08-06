package gov.va.vetservices.lib.filemanager.pdf.itext;

import java.io.ByteArrayOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.itextpdf.io.source.IRandomAccessSource;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;

import gov.va.ascent.framework.messages.MessageSeverity;
import gov.va.vetservices.lib.filemanager.exception.FileManagerException;
import gov.va.vetservices.lib.filemanager.impl.FileManagerImpl;
import gov.va.vetservices.lib.filemanager.impl.validate.MessageKeysEnum;
import gov.va.vetservices.lib.filemanager.modelvalidators.keys.LibFileManagerMessageKeys;
import gov.va.vetservices.lib.filemanager.util.MessageUtils;

@Component(ItextUtils.BEAN_NAME)
public class ItextUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ItextUtils.class);

	/** If ever needed, this variable can be refactored to a property to allow processing pw-protected files */
	private static final boolean PROCESS_PROTECTED_FILES = false;

	public static final String BEAN_NAME = "itextUtils";
	
	/** Auto wire message utilities */
	@Autowired
	@Qualifier("messageUtils")
	private MessageUtils messageUtils;

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
	 * @throws FileManagerException could not read the PDF bytes
	 */
	public PdfReader getPdfReader(final byte[] pdfBytes) throws FileManagerException {
		PdfReader pdfReader = null;

		if (pdfBytes != null && pdfBytes.length > 0) {
			try {
				final RandomAccessSourceFactory factory = new RandomAccessSourceFactory();
				final IRandomAccessSource source = factory.createSource(pdfBytes);
				pdfReader = new PdfReader(source, new ReaderProperties()); // NOSONAR reader is closed by the PdfDocument
				pdfReader.setUnethicalReading(PROCESS_PROTECTED_FILES);
				LOGGER.debug("... pdfReader.getFileLength(): " + pdfReader.getFileLength());
			} catch (final Exception e) {
				String key = LibFileManagerMessageKeys.UNEXPECTED_ERROR;
				String msg = messageUtils.returnMessage(key);
				LOGGER.error(
						key+ ": " + msg + " Cause is " + e.getClass().getSimpleName() + ": " + e.getMessage(),
						e);
				throw new FileManagerException(e, MessageSeverity.ERROR, key, msg);
			}
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
