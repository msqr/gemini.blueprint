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

package org.eclipse.gemini.blueprint.iandt.configopt;

import java.awt.Shape;

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for Sync Wait.
 * 
 * Start two bundles, one which requires a dependency and one which provides it
 * (in inverse order).
 * 
 * @author Costin Leau
 * 
 */
public class SyncWaitTest extends BehaviorBaseTest {

	@Test
	public void testBehaviour() throws Exception {

		// start dependency first
		final Bundle tail = installAndStartTestBundle("sync-tail-bundle");
		assertTrue("bundle " + tail + "hasn't been fully started", OsgiBundleUtils.isBundleActive(tail));

		// followed by the bundle
		Bundle syncWaitBundle = installAndStartTestBundle("sync-wait-bundle");

		assertTrue("bundle " + syncWaitBundle + "hasn't been fully started", OsgiBundleUtils.isBundleActive(syncWaitBundle));

		// wait for the listener to get the bundles
		assertContextServiceIs(syncWaitBundle, true, 2000);

		// check that the dependency service is actually started as the
		// dependency bundle has started
		assertNotNull(bundleContext.getServiceReference(Shape.class.getName()));

	}

}
