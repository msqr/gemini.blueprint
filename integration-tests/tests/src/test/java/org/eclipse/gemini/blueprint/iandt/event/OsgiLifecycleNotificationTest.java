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

package org.eclipse.gemini.blueprint.iandt.event;

import java.io.FilePermission;
import java.security.Permission;
import java.util.List;

import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleApplicationContextListener;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextFailedEvent;
import org.eclipse.gemini.blueprint.context.event.OsgiBundleContextRefreshedEvent;

import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

/**
 * Integration test for the appCtx notification mechanism.
 * 
 * @author Costin Leau
 * 
 */
public class OsgiLifecycleNotificationTest extends AbstractEventTest {

	@Override
	public Option[] getExtraConfig()
	{
		return options(
				mavenBundle("org.eclipse.gemini.blueprint.iandt", "extender.listener.bundle").versionAsInProject()
		);
	}

	protected void onSetUp() throws Exception {
		super.onSetUp();
	}

	@Test
	public void testEventsForCtxThatWork() throws Exception {

		listener = new OsgiBundleApplicationContextListener() {

			public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent event) {
				if (event instanceof OsgiBundleContextRefreshedEvent) {
					eventList.add(event);
					synchronized (lock) {
						lock.notify();
					}
				}
			}
		};

		registerEventListener();

		assertTrue("should start with an empty list", eventList.isEmpty());

		// install a simple osgi bundle and check the list of events
		final Bundle bnd = installTestBundle("simple.service");
		try {

			bnd.start();

			assertTrue("no event received", waitForEvent(TIME_OUT));
			System.out.println("events received " + eventList);
		}
		finally {
			bnd.uninstall();
		}
	}

	@Test
	public void testEventsForCtxThatFail() throws Exception {

		listener = new OsgiBundleApplicationContextListener() {

			public void onOsgiApplicationEvent(OsgiBundleApplicationContextEvent event) {
				if (event instanceof OsgiBundleContextFailedEvent) {
					eventList.add(event);
					synchronized (lock) {
						lock.notify();
					}
				}
			}
		};

		registerEventListener();

		assertTrue("should start with an empty list", eventList.isEmpty());
		// install a simple osgi bundle and check the list of events

		final Bundle errorBundle = installTestBundle("error");

		try {
			errorBundle.start();

			assertTrue("event not received", waitForEvent(TIME_OUT));
		}
		finally {
			errorBundle.uninstall();
		}
	}

	protected List<Permission> getTestPermissions() {
		List<Permission> perms = super.getTestPermissions();
		// export package
		perms.add(new AdminPermission("*", AdminPermission.EXECUTE));
		perms.add(new AdminPermission("*", AdminPermission.LIFECYCLE));
		perms.add(new AdminPermission("*", AdminPermission.RESOLVE));
		perms.add(new FilePermission("<<ALL FILES>>", "read"));
		return perms;
	}
}
