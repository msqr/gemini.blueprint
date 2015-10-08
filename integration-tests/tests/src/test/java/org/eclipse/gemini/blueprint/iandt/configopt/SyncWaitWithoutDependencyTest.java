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

import org.junit.Test;
import org.osgi.framework.Bundle;
import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;

import static junit.framework.TestCase.assertTrue;

/**
 * Integration test for Sync Wait but this time, by checking the waiting by
 * satisfying the dependency through this test.
 * 
 * @author Costin Leau
 * 
 */
public class SyncWaitWithoutDependencyTest extends BehaviorBaseTest {

	@Test
	public void testBehaviour() throws Exception {

		// start bundle first (no dependency)
		final Bundle bundle = installAndStartTestBundle("sync-wait-bundle");

		assertTrue("bundle " + bundle + "should have started", OsgiBundleUtils.isBundleActive(bundle));

		final Bundle tailBundle = installAndStartTestBundle("sync-tail-bundle");
		assertTrue("bundle " + tailBundle + "hasn't been fully started", OsgiBundleUtils.isBundleActive(tailBundle));

		// check appCtx hasn't been published
		assertContextServiceIs(bundle, false, 500);
		// check the dependency ctx
		assertContextServiceIs(tailBundle, true, 500);

		// restart the bundle (to catch the tail)
		bundle.stop();
		bundle.start();

		// check appCtx has been published
		assertContextServiceIs(bundle, true, 500);
	}
}
