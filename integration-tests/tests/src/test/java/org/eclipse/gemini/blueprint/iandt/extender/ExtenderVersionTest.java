/******************************************************************************
 * Copyright (c) 2006, 2010 VMware Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution. 
 * The Eclipse Public License is available at 
 * http://www.eclipse.org/legal/epl-v10.html and the Apache License v2.0
 * is available at http://www.opensource.org/licenses/apache2.0.php.
 * You may elect to redistribute this code under either of these licenses. 
 * 
 * Contributors:
 *   VMware Inc.
 *****************************************************************************/

package org.eclipse.gemini.blueprint.iandt.extender;

import java.awt.Point;
import java.io.FilePermission;
import java.security.Permission;
import java.util.List;

import org.eclipse.gemini.blueprint.iandt.BaseIntegrationTest;
import org.junit.Test;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

/**
 * @author Costin Leau
 * 
 */
public class ExtenderVersionTest extends BaseIntegrationTest {

	protected String getManifestLocation() {
		return null;
	}

	// given bundle should not be picked up by the extender since it expects a
	// certain version
	@Test
	public void testBundleIgnoredBasedOnSpringExtenderVersion() throws Exception {

		final String extenderVersionBundleUrl = mavenBundle("org.eclipse.gemini.blueprint.iandt", "extender-version-bundle")
				.versionAsInProject().getURL();

		final Bundle bundle = bundleContext.installBundle(extenderVersionBundleUrl);
		assertNotNull(bundle);
		bundle.start();

		assertTrue(OsgiBundleUtils.isBundleActive(bundle));
		assertNull("no point should be published ", bundleContext.getServiceReference(Point.class.getName()));
	}

	protected List<Permission> getTestPermissions() {
		final List<Permission> perms = super.getTestPermissions();
		// export package
		perms.add(new AdminPermission("*", AdminPermission.EXECUTE));
		perms.add(new AdminPermission("*", AdminPermission.LIFECYCLE));
		perms.add(new AdminPermission("*", AdminPermission.RESOLVE));
		perms.add(new FilePermission("<<ALL FILES>>", "read"));
		return perms;
	}
}
