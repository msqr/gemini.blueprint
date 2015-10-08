/******************************************************************************
 * Copyright (c) 2006, 2010 VMware Inc., Oracle Inc.
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
 *   Oracle Inc.
 *****************************************************************************/

package org.eclipse.gemini.blueprint.iandt.extender;

import java.io.FilePermission;
import java.security.Permission;
import java.util.List;

import org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext;
import org.eclipse.gemini.blueprint.iandt.BaseIntegrationTest;
import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.context.ApplicationContext;

import static org.eclipse.gemini.blueprint.iandt.TestUtils.andFilterString;
import static org.eclipse.gemini.blueprint.iandt.TestUtils.filterString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

/**
 * @author Hal Hildebrand Date: May 21, 2007 Time: 4:43:52 PM
 */
public class ExtenderTest extends BaseIntegrationTest {

	protected String getManifestLocation() {
		return null;
	}

//	// Overridden to remove the spring extender bundle!
//	protected String[] getTestFrameworkBundlesNames() {
//		String[] bundles = super.getTestFrameworkBundlesNames();
//		List list = new ArrayList(bundles.length);
//
//		// remove extender
//		CollectionUtils.mergeArrayIntoCollection(bundles, list);
//		// additionally remove the annotation bundle as well (if included)
//
//		int bundlesFound = 0;
//		for (Iterator iter = list.iterator(); (iter.hasNext() && (bundlesFound < 2));) {
//			String element = (String) iter.next();
//			if (element.indexOf("extender") >= 0 || element.indexOf("osgi-annotation") >= 0) {
//				iter.remove();
//				bundlesFound++;
//			}
//		}
//
//		return (String[]) list.toArray(new String[list.size()]);
//	}

	// Specifically cannot wait - test scenario has bundles which are spring
	// powered, but will not be started.
	protected boolean shouldWaitForSpringBundlesContextCreation() {
		return false;
	}

	@Override
	public Option[] getExtraConfig()
	{
		return options(
				mavenBundle("org.eclipse.gemini.blueprint.iandt", "lifecycle").versionAsInProject()
		);
	}

	@Test
	public void testLifecycle() throws Exception {
		assertNull("Guinea pig has already been started", System
				.getProperty("org.eclipse.gemini.blueprint.iandt.lifecycle.GuineaPig.close"));

		final String filter = andFilterString(
				filterString(Constants.OBJECTCLASS, ApplicationContext.class.getName()),
				filterString(ConfigurableOsgiBundleApplicationContext.SPRING_DM_APPLICATION_CONTEXT_SERVICE_PROPERTY_NAME, "org.eclipse.gemini.blueprint.iandt.lifecycle")
		);

		final ServiceTracker tracker = serviceTrackerWithFilter(filter);

		ApplicationContext appContext = (ApplicationContext) tracker.waitForService(1);

		assertNull("lifecycle application context does not exist", appContext);

		final String extenderBundleUrl = mavenBundle("org.eclipse.gemini.blueprint", "gemini-blueprint-extender")
				.versionAsInProject().getURL();
		final Bundle extenderBundle = bundleContext.installBundle(extenderBundleUrl);
		assertNotNull("Extender bundle", extenderBundle);

		extenderBundle.start();

		tracker.open();

		appContext = (ApplicationContext) tracker.waitForService(60000);

		assertNotNull("lifecycle application context exists", appContext);

		assertNotSame("Guinea pig hasn't already been shutdown", "true", System
				.getProperty("org.eclipse.gemini.blueprint.iandt.lifecycle.GuineaPig.close"));

		assertEquals("Guinea pig started up", "true", System
				.getProperty("org.eclipse.gemini.blueprint.iandt.lifecycle.GuineaPig.startUp"));

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
