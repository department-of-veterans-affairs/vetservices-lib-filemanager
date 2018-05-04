package gov.va.vetservices.lib.filemanager.impl.validate;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MessageKeysEnumTest {

	@Test
	public final void test() {
		assertNotNull(MessageKeysEnum.values());
		assertTrue(MessageKeysEnum.values().length > 0);

		for (MessageKeysEnum mk : MessageKeysEnum.values()) {
			assertNotNull(mk.getKey());
			assertNotNull(mk.getMessage());
		}
	}

}
