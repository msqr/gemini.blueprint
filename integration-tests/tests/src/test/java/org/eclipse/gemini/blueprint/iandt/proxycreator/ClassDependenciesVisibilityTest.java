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

package org.eclipse.gemini.blueprint.iandt.proxycreator;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.Permission;
import java.util.List;

import javax.swing.event.DocumentEvent;

import org.eclipse.gemini.blueprint.iandt.BaseIntegrationTest;
import org.eclipse.gemini.blueprint.service.importer.support.*;
import org.junit.Test;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.util.PathUtils;
import org.osgi.framework.Constants;

import static org.eclipse.gemini.blueprint.test.BlueprintOptions.coreBundles;
import static org.eclipse.gemini.blueprint.test.BlueprintOptions.withLogging;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.bootDelegationPackages;
import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.junitBundles;
import static org.ops4j.pax.exam.CoreOptions.options;

/**
 * Integration test for bug OSGI-597.
 * 
 * This test tries to create a proxy for DocumentEvent w/o importing its
 * dependency, namely javax.swing.text.Element.
 * 
 * @author Costin Leau
 */
public class ClassDependenciesVisibilityTest extends BaseIntegrationTest {

	private static String DEPENDENCY_CLASS = "javax.swing.text.Element";

	// TODO This shouldn't be necessary - completely overrides boot delegation!
	@ProbeBuilder
	public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
		probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "org.*,javax.swing.event");
		return probe;
	}

	@Configuration
	public static Option[] defaultConfig() {
		return options(
				bootDelegationPackages("sun.*",
						"com.sun.*",
						"org.w3c.*",
						"org.xml.*",
						"javax.sql.*",
						"javax.transaction.*",
						"javax.activation.*",
						"org.apache.xerces.jaxp.*"),
				cleanCaches(false),
				junitBundles(),
				coreBundles(),
				withLogging(new File(PathUtils.getBaseDir() + "/target/test-classes/logback.xml").toURI())
		);
	}

	@Test
	public void testPackageDependency() throws Exception {
		ClassLoader cl = applicationContext.getClassLoader();
		System.out.println(cl);
		OsgiServiceProxyFactoryBean fb = new OsgiServiceProxyFactoryBean();
		fb.setBundleContext(bundleContext);
        fb.setAvailability(Availability.OPTIONAL);
		fb.setImportContextClassLoader(ImportContextClassLoaderEnum.UNMANAGED);
		fb.setInterfaces(new Class<?>[] { DocumentEvent.class });
		fb.setBeanClassLoader(cl);
		fb.setApplicationEventPublisher(applicationContext);
		fb.afterPropertiesSet();

		checkPackageVisibility(cl);

		Object proxy = fb.getObject();
		assertNotNull(proxy);
		assertTrue(proxy instanceof DocumentEvent);
		System.out.println(proxy.getClass());

	}

	@Test
	public void testJdkProxy() throws Exception {
		InvocationHandler ih = new InvocationHandler() {

			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return null;
			}
		};
		ClassLoader cl = applicationContext.getClassLoader();
		checkPackageVisibility(cl);

		try {
			Object proxy = Proxy.newProxyInstance(cl, new Class<?>[] { DocumentEvent.class }, ih);
			assertNotNull(proxy);
			System.out.println(proxy.getClass());

			fail("should have failed");
		}
		catch (Throwable cnfe) {
			// expected
		}
	}

	// TODO Been hit by this: https://ops4j1.jira.com/browse/PAXEXAM-22
	// The generated test bundle adds DynamicImport-Package: *
	private void checkPackageVisibility(ClassLoader cl) throws Exception {

		try {
			cl.loadClass(DEPENDENCY_CLASS);
			fail("should not be able to load " + DEPENDENCY_CLASS);
		}
		catch (ClassNotFoundException cnfe) {
			// expected
		}
	}

	protected List<Permission> getTestPermissions() {
		List<Permission> perms = super.getTestPermissions();
		// export package
		perms.add(new RuntimePermission("*", "getClassLoader"));
		return perms;
	}
}
