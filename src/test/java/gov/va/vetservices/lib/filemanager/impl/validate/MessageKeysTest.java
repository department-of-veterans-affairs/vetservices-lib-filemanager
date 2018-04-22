package gov.va.vetservices.lib.filemanager.impl.validate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MessageKeysTest {

	@Test
	public final void test() {
		assertNotNull(MessageKeys.values());
		assertTrue(MessageKeys.values().length > 0);

		for (MessageKeys mk : MessageKeys.values()) {
			assertNotNull(mk.getKey());
			assertNotNull(mk.getMessage());
		}
	}

}
