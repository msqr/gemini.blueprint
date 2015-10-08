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

package org.eclipse.gemini.blueprint.iandt.lifecycle;

import java.security.Permission;
import java.util.List;

import org.eclipse.gemini.blueprint.iandt.BaseIntegrationTest;
import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.eclipse.gemini.blueprint.context.ConfigurableOsgiBundleApplicationContext;

import static org.eclipse.gemini.blueprint.iandt.TestUtils.andFilterString;
import static org.eclipse.gemini.blueprint.iandt.TestUtils.filterString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

/**
 * @author Hal Hildebrand Date: Oct 15, 2006 Time: 5:51:36 PM
 */
public class LifecycleTest extends BaseIntegrationTest {

    @Override
    public Option[] getExtraConfig()
    {
        return options(
                mavenBundle("org.eclipse.gemini.blueprint.iandt", "lifecycle").versionAsInProject()
        );
    }

    @Test
    public void testLifecycle() throws Exception {
        assertNotSame("Guinea pig has already been shutdown", "true",
                System.getProperty("org.eclipse.gemini.blueprint.iandt.lifecycle.GuineaPig.close"));

        assertEquals("Guinea pig didn't startup", "true",
                System.getProperty("org.eclipse.gemini.blueprint.iandt.lifecycle.GuineaPig.startUp"));
        Bundle testBundle = null;
        for (Bundle bundle : bundleContext.getBundles()) {
            if ("org.eclipse.gemini.blueprint.iandt.lifecycle".equals(bundle.getSymbolicName())) {
                testBundle = bundle;
                break;
            }
        }

        assertNotNull("Could not find the test bundle", testBundle);

        final String filter = andFilterString(
                filterString(Constants.OBJECTCLASS, AbstractRefreshableApplicationContext.class.getName()),
                filterString(ConfigurableOsgiBundleApplicationContext.APPLICATION_CONTEXT_SERVICE_PROPERTY_NAME, "org.eclipse.gemini.blueprint.iandt.lifecycle")
        );

        logger.info("Created filter = " + filter);

        final ServiceTracker tracker = serviceTrackerWithFilter(filter);
        tracker.open();

        try {
            final AbstractRefreshableApplicationContext appContext = (AbstractRefreshableApplicationContext) tracker.waitForService(30000);
            assertNotNull("test application context", appContext);
            assertTrue("application context is active", appContext.isActive());

            testBundle.stop();
            while (testBundle.getState() == Bundle.STOPPING) {
                Thread.sleep(10);
            }
            assertEquals("Guinea pig didn't shutdown", "true",
                    System.getProperty("org.eclipse.gemini.blueprint.iandt.lifecycle.GuineaPig.close"));

            assertFalse("application context is inactive", appContext.isActive());
        } finally {
            tracker.close();
        }
    }

    protected List<Permission> getTestPermissions() {
        final List<Permission> perms = super.getTestPermissions();
        // export package
        perms.add(new AdminPermission("*", AdminPermission.EXECUTE));
        return perms;
    }
}
