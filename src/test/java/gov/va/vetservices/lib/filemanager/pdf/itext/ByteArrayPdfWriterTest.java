package gov.va.vetservices.lib.filemanager.pdf.itext;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

public class ByteArrayPdfWriterTest {

	@Test
	public final void testByteArrayPdfWriter() {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		assertNotNull(new ByteArrayPdfWriter(baos));
	}

	@Test
	public final void testGetDestinationBytes() {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ByteArrayPdfWriter bapw = new ByteArrayPdfWriter(baos);
		assertNotNull(bapw.getDestinationBytes());
	}

}
