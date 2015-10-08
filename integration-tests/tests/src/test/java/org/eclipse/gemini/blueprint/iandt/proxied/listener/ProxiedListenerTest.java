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

package org.eclipse.gemini.blueprint.iandt.proxied.listener;

import java.awt.Shape;
import java.awt.geom.Area;

import org.eclipse.gemini.blueprint.iandt.BaseIntegrationTest;
import org.eclipse.gemini.blueprint.iandt.importer.Listener;
import org.junit.Test;
import org.ops4j.pax.exam.Option;
import org.osgi.framework.ServiceRegistration;
import org.springframework.test.context.ContextConfiguration;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

/**
 * @author Costin Leau
 */
@ContextConfiguration (locations = {"classpath:org/eclipse/gemini/blueprint/iandt/proxied/listener/service-import.xml"})
public class ProxiedListenerTest extends BaseIntegrationTest {

	@Override
	public Option[] getExtraConfig()
	{
		return options(
				mavenBundle("org.eclipse.gemini.blueprint.iandt", "proxy.listener").versionAsInProject()
		);
	}

	@Test
	public void testListenerProxy() throws Exception {
		System.out.println(Listener.class.getName());
		Object obj = new Area();
		ServiceRegistration reg = bundleContext.registerService(Shape.class.getName(), obj, null);
		reg.unregister();
	}
}
