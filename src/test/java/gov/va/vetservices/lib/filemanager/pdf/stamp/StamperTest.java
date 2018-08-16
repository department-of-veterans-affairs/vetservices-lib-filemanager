package gov.va.vetservices.lib.filemanager.pdf.stamp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.itextpdf.io.font.constants.StandardFonts;

import gov.va.ascent.framework.config.AscentCommonSpringProfiles;
import gov.va.vetservices.lib.filemanager.FileManagerConfig;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.FileDto;
import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;
import gov.va.vetservices.lib.filemanager.exception.PdfStamperException;
import gov.va.vetservices.lib.filemanager.impl.dto.DocMetadataDto;
import gov.va.vetservices.lib.filemanager.pdf.stamp.dto.StampDataDto;
import gov.va.vetservices.lib.filemanager.testutil.AbstractFileHandler;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_IMPLS,
		AscentCommonSpringProfiles.PROFILE_REMOTE_CLIENT_SIMULATORS })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class })
@ContextConfiguration(inheritLocations = false, classes = { FileManagerConfig.class })
public class StamperTest extends AbstractFileHandler {

	private static final String claimId = "1111";
	private static final String pdfPath = "files/application/pdf/IS_text-doc.pdf";

	@Autowired
	Stamper stamper;

	@Before
	public void setUp() {
		assertNotNull(stamper);
	}

	@Test
	public final void testStamp() throws IOException {
		final byte[] bytes = super.readFile(Paths.get(pdfPath));
		final DocMetadataDto metadata = new DocMetadataDto();
		metadata.setClaimId(claimId);
		metadata.setDocTypeId("123");
		metadata.setProcessType(ProcessType.CLAIMS_526);
		metadata.setDocDate(new Date());
		final StampDataDto stampDataDto = new StampDataDto();
		stampDataDto.setFontName(StandardFonts.HELVETICA);
		stampDataDto.setFontSizeInPoints(12);
		stampDataDto.setProcessType(ProcessType.CLAIMS_526);
		final FileDto fileDto = new FileDto();
		fileDto.setFilename(Paths.get(pdfPath).getFileName().toString());
		fileDto.setFilebytes(bytes);

		try {
			final byte[] stamped = stamper.stamp(metadata, stampDataDto, fileDto);
			assertNotNull(stamped);

		} catch (final PdfStamperException e) {
			e.printStackTrace();
			fail(e.getClass().getSimpleName() + ": " + e.getMessage());
		}
	}

	@Test
	public final void testStamp_ProcessTypes() throws IOException {
		final byte[] bytes = super.readFile(Paths.get(pdfPath));
		final DocMetadataDto metadata = new DocMetadataDto();
		metadata.setClaimId(claimId);
		metadata.setDocTypeId("123");
		metadata.setDocDate(new Date());
		final StampDataDto stampDataDto = new StampDataDto();
		stampDataDto.setFontName(StandardFonts.HELVETICA);
		stampDataDto.setFontSizeInPoints(12);
		stampDataDto.setProcessType(ProcessType.CLAIMS_526);
		final FileDto fileDto = new FileDto();
		fileDto.setFilename(Paths.get(pdfPath).getFileName().toString());
		fileDto.setFilebytes(bytes);

		// StampsEnum.CLAIM - stamp has claimId, no docDate
		metadata.setProcessType(ProcessType.CLAIMS_686);
		metadata.setDocDate(new Date());

		try {
			final byte[] stamped = stamper.stamp(metadata, stampDataDto, fileDto);
			assertNotNull(stamped);

		} catch (final PdfStamperException e) {
			e.printStackTrace();
			fail(e.getClass().getSimpleName() + ": " + e.getMessage());
		}

		// StampsEnum.CLAIM_TIMEDATE - stamp has claimId, no docDate
		metadata.setProcessType(ProcessType.CLAIMS_STATUS_SUPPORTING);
		metadata.setDocDate(new Date());

		try {
			final byte[] stamped = stamper.stamp(metadata, stampDataDto, fileDto);
			assertNotNull(stamped);

		} catch (final PdfStamperException e) {
			e.printStackTrace();
			fail(e.getClass().getSimpleName() + ": " + e.getMessage());
		}

	}

	@Test
	public final void testStamp_Bad() {
		final byte[] bytes = new byte[] { 0 };
		final DocMetadataDto metadata = new DocMetadataDto();
		metadata.setClaimId(claimId);
		metadata.setDocTypeId("123");
		metadata.setProcessType(ProcessType.CLAIMS_526);
		metadata.setDocDate(new Date());
		final StampDataDto stampDataDto = new StampDataDto();
		stampDataDto.setFontName(StandardFonts.HELVETICA);
		stampDataDto.setFontSizeInPoints(12);
		stampDataDto.setProcessType(ProcessType.CLAIMS_526);
		final FileDto fileDto = new FileDto();
		fileDto.setFilename("bad-bytes.pdf");
		fileDto.setFilebytes(bytes);

		try {
			final byte[] stamped = stamper.stamp(metadata, stampDataDto, fileDto);
			fail("Exception should have been thrown, stamped is " + stamped);

		} catch (final Throwable e) {
			assertNotNull(e);
			assertTrue(e.getClass().isAssignableFrom(PdfStamperException.class));
			assertNotNull(e.getCause());
			assertTrue(e.getCause().getClass().isAssignableFrom(IOException.class));
			assertNotNull(e.getCause().getCause());
			assertTrue(e.getCause().getCause().getClass().isAssignableFrom(com.itextpdf.io.IOException.class));
		}

		fileDto.setFilename(Paths.get(pdfPath).getFileName().toString());
		fileDto.setFilebytes(bytes);

		try {
			stamper.stamp(null, stampDataDto, fileDto);
			fail("Exception should have been thrown");
		} catch (final Throwable e) {
			assertNotNull(e);
			assertTrue(IllegalArgumentException.class.isAssignableFrom(e.getClass()));
		}

		try {
			stamper.stamp(metadata, null, fileDto);
			fail("Exception should have been thrown");
		} catch (final Throwable e) {
			assertNotNull(e);
			assertTrue(IllegalArgumentException.class.isAssignableFrom(e.getClass()));
		}

		try {
			stamper.stamp(metadata, stampDataDto, null);
			fail("Exception should have been thrown");
		} catch (final Throwable e) {
			assertNotNull(e);
			assertTrue(IllegalArgumentException.class.isAssignableFrom(e.getClass()));
		}
	}
}
