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

package org.eclipse.gemini.blueprint.iandt.namespace;

import java.net.URL;

import org.eclipse.gemini.blueprint.iandt.BaseIntegrationTest;
import org.junit.Test;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.osgi.framework.Bundle;
import org.springframework.beans.factory.xml.NamespaceHandlerResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ObjectUtils;

import static org.junit.Assert.assertNotNull;

/**
 * Integration test for libraries (that contain Spring namespaces) that are
 * embedded inside bundles which use their namespaces. Since the library is not
 * deployed as a bundle, other bundles should not see the namespace but the
 * bundle embedding it, should.
 * 
 * @author Costin Leau
 * 
 */
@ContextConfiguration (locations = {"classpath:org/eclipse/gemini/blueprint/iandt/namespace/context.xml"})
public class EmbeddedNamespaceLibraryTest extends BaseIntegrationTest {

	@ProbeBuilder
	public TestProbeBuilder customizeProbe(TestProbeBuilder builder) {
		builder.setHeader(org.osgi.framework.Constants.BUNDLE_CLASSPATH, ".,namespace/ns.jar");
		return builder;
	}

	protected String[] getBundleContentPattern() {
		return (String[]) ObjectUtils.addObjectToArray(super.getBundleContentPattern(), "namespace/**/*");
	}

	@Test
	public void testApplicationContextWasProperlyStarted() throws Exception {
		assertNotNull(applicationContext);
		assertNotNull(applicationContext.getBean("bean"));
	}

	@Test
	public void testNamespaceFilesOnTheClassPath() throws Exception {
		// simple code to trigger an import for this package
		assertNotNull(NamespaceHandlerResolver.class);

		Bundle bundle = bundleContext.getBundle();
		URL handlers = bundle.getResource("META-INF/spring.handlers");
		URL schemas = bundle.getResource("META-INF/spring.schemas");

		assertNotNull(handlers);
		assertNotNull(schemas);

	}

}
