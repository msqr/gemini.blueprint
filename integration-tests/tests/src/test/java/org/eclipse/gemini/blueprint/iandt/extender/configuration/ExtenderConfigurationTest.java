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

package org.eclipse.gemini.blueprint.iandt.extender.configuration;

import java.security.Permission;
import java.util.List;
import java.util.Properties;

import org.eclipse.gemini.blueprint.iandt.BaseIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

/**
 * Extender configuration fragment.
 * 
 * @author Costin Leau
 * 
 */
@ContextConfiguration (locations = {"classpath:org/eclipse/gemini/blueprint/iandt/extender/configuration/config.xml"})
public class ExtenderConfigurationTest extends BaseIntegrationTest {

	private ApplicationContext context;

	@Before
	public void onSetUp() throws Exception {
		context = (ApplicationContext) applicationContext.getBean("appCtx");
	}

	@Override
	public Option[] getExtraConfig()
	{
		return options(
				mavenBundle("org.eclipse.gemini.blueprint.iandt", "extender-fragment-bundle").versionAsInProject()
		);
	}

	@Test
	public void testExtenderConfigAppCtxPublished() throws Exception {
		ServiceReference[] refs =
				bundleContext.getAllServiceReferences("org.springframework.context.ApplicationContext", null);
		for (int i = 0; i < refs.length; i++) {
			System.out.println(OsgiStringUtils.nullSafeToString(refs[i]));
		}
		assertNotNull(context);
	}

	@Test
	public void testPackageAdminReferenceBean() throws Exception {
		if (PackageAdmin.class.hashCode() != 0)
			;
		logger.info("Calling package admin bean");
		assertNotNull(context.getBean("packageAdmin"));
	}

	@Test
	public void testShutdownTaskExecutor() throws Exception {
		assertTrue(context.containsBean("shutdownTaskExecutor"));
		Object bean = context.getBean("shutdownTaskExecutor");
		// TODO - Test bundle needs to use a different class, as this has been removed
		//assertTrue("unexpected type", bean instanceof TimerTaskExecutor);
	}

	@Test
	public void testTaskExecutor() throws Exception {
		assertTrue(context.containsBean("taskExecutor"));
		Object bean = context.getBean("shutdownTaskExecutor");
		assertTrue("unexpected type", bean instanceof TaskExecutor);
	}

	@Test
	public void testCustomProperties() throws Exception {
		assertTrue(context.containsBean("extenderProperties"));
		Object bean = context.getBean("extenderProperties");
		assertTrue("unexpected type", bean instanceof Properties);
	}

	protected List<Permission> getTestPermissions() {
		final List<Permission> list = super.getTestPermissions();
		list.add(new AdminPermission("*", AdminPermission.METADATA));
		return list;
	}
}