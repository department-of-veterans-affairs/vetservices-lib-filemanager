package gov.va.vetservices.lib.filemanager.pdf.stamp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.util.Date;

import org.junit.Test;

import gov.va.vetservices.lib.filemanager.api.v1.transfer.ProcessType;

public class StampsEnumTest {

	private static final String CLAIM_ID = "11111";

	@Test
	public final void testGetStampTextProcessTypeString() {
		String text = StampsEnum.getStampText(ProcessType.CLAIMS_526, CLAIM_ID, Date.from(Instant.now()));
		assertNotNull(text);
		assertTrue(text.contains(CLAIM_ID));

		text = StampsEnum.getStampText(ProcessType.OTHER, null, Date.from(Instant.now()));
		assertNull(text);

		try {
			text = StampsEnum.getStampText(null, null, Date.from(Instant.now()));
			fail("Should have thrown exception");
		} catch (IllegalArgumentException e) {
			assertNotNull(e);
			assertTrue(e.getMessage().contains("ProcessType"));
		}

	}

	@Test
	public final void testGetStampText() {
		for (StampsEnum value : StampsEnum.values()) {
			assertNotNull(value.getStampText());
		}
	}

	@Test
	public final void testGetProcessTypes() {
		for (StampsEnum value : StampsEnum.values()) {
			assertNotNull(value.getProcessTypes());
		}
	}

}
