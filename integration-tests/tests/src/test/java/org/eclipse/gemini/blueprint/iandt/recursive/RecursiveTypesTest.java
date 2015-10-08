package org.eclipse.gemini.blueprint.iandt.recursive;

import org.eclipse.gemini.blueprint.iandt.BaseIntegrationTest;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;

public class RecursiveTypesTest extends BaseIntegrationTest {

	@Test
	public void testBeanReference() throws Exception {

		final Bundle bundle = installAndStartTestBundle("recursive");
		waitOnContextCreation(bundle.getSymbolicName());
		System.out.println("started bundle [" + OsgiStringUtils.nullSafeSymbolicName(bundle) + "]");
	}
}
