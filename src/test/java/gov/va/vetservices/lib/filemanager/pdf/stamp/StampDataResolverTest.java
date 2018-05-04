package gov.va.vetservices.lib.filemanager.pdf.stamp;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import gov.va.vetservices.lib.filemanager.impl.dto.ImplDto;
import gov.va.vetservices.lib.filemanager.pdf.stamp.dto.StampDataDto;

public class StampDataResolverTest {

	StampDataResolver resolver;

	@Before
	public void setUp() throws Exception {
		resolver = new StampDataResolver();
		assertNotNull(resolver);
	}

	@Test
	public final void testResolve() {
		// NOSONAR TODO make this a real test when coding is done
		StampDataDto stampData = resolver.resolve(new ImplDto());
		assertNotNull(stampData);
	}

}
