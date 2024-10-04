/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.felix.ipojo.runtime.core.test.dependencies.di;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.architecture.InstanceDescription;
import org.apache.felix.ipojo.runtime.core.test.dependencies.Common;
import org.apache.felix.ipojo.runtime.core.test.services.CheckService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.ServiceReference;

import java.util.Properties;

import static org.junit.Assert.*;

public class TestMethodOptionalDependencies extends Common {

    ComponentInstance instance3, instance4, instance5;

    ComponentInstance fooProvider;

    @Before
    public void setUp() {
        try {
            Properties prov = new Properties();
            prov.put("instance.name", "FooProvider");
            fooProvider = ipojoHelper.getFactory("FooProviderType-1").createComponentInstance(prov);
            fooProvider.stop();

            Properties i3 = new Properties();
            i3.put("instance.name", "Object");
            instance3 = ipojoHelper.getFactory("DIMObjectOptionalCheckServiceProvider").createComponentInstance(i3);

            Properties i4 = new Properties();
            i4.put("instance.name", "Ref");
            instance4 = ipojoHelper.getFactory("DIMRefOptionalCheckServiceProvider").createComponentInstance(i4);

            Properties i5 = new Properties();
            i5.put("instance.name", "Both");
            instance5 = ipojoHelper.getFactory("DIMBothOptionalCheckServiceProvider").createComponentInstance(i5);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @After
    public void tearDown() {
        instance3.dispose();
        instance4.dispose();
        instance5.dispose();
        fooProvider.dispose();
        instance3 = null;
        instance4 = null;
        instance5 = null;
        fooProvider = null;
    }

    @Test
    public void testObject() {

        ServiceReference arch_ref = ipojoHelper.getServiceReferenceByName(Architecture.class.getName(), instance3.getInstanceName());
        assertNotNull("Check architecture availability", arch_ref);
        InstanceDescription id = ((Architecture) getContext().getService(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity - 1", id.getState() == ComponentInstance.VALID);

        ServiceReference cs_ref = ipojoHelper.getServiceReferenceByName(CheckService.class.getName(), instance3.getInstanceName());
        assertNotNull("Check CheckService availability", cs_ref);
        CheckService cs = (CheckService) getContext().getService(cs_ref);

        Properties props = cs.getProps();

        // Check properties
        assertFalse("check CheckService invocation -1", ((Boolean) props.get("result")).booleanValue()); // False is returned (nullable)
        assertEquals("check void bind invocation -1", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -1", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -1", ((Integer) props.get("objectB")).intValue(), 0);
        assertEquals("check object unbind callback invocation -1", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -1", ((Integer) props.get("refB")).intValue(), 0);
        assertEquals("check ref unbind callback invocation -1", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -1", ((Integer) props.get("bothB")).intValue(), 0);
        assertEquals("check both unbind callback invocation -1", ((Integer) props.get("bothU")).intValue(), 0);

        fooProvider.start();

        id = ((Architecture) getContext().getService(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity - 2", id.getState() == ComponentInstance.VALID);

        assertNotNull("Check CheckService availability", cs_ref);
        cs = (CheckService) getContext().getService(cs_ref);
        props = cs.getProps();
        // Check properties
        assertTrue("check CheckService invocation -2", ((Boolean) props.get("result")).booleanValue());
        assertEquals("check void bind invocation -2", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -2", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -2", ((Integer) props.get("objectB")).intValue(), 1);
        assertEquals("check object unbind callback invocation -2", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -2", ((Integer) props.get("refB")).intValue(), 0);
        assertEquals("check ref unbind callback invocation -2", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -2", ((Integer) props.get("bothB")).intValue(), 0);
        assertEquals("check both unbind callback invocation -2", ((Integer) props.get("bothU")).intValue(), 0);

        fooProvider.stop();

        id = ((Architecture) getContext().getService(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity - 3", id.getState() == ComponentInstance.VALID);

        cs = (CheckService) getContext().getService(cs_ref);
        props = cs.getProps();
        // Check properties
        assertFalse("check CheckService invocation -3", ((Boolean) props.get("result")).booleanValue());
        assertEquals("check void bind invocation -3", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -3", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -3", ((Integer) props.get("objectB")).intValue(), 1);
        assertEquals("check object unbind callback invocation -3", ((Integer) props.get("objectU")).intValue(), 1);
        assertEquals("check ref bind callback invocation -3", ((Integer) props.get("refB")).intValue(), 0);
        assertEquals("check ref unbind callback invocation -3", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -3", ((Integer) props.get("bothB")).intValue(), 0);
        assertEquals("check both unbind callback invocation -3", ((Integer) props.get("bothU")).intValue(), 0);

        id = null;
        cs = null;
        getContext().ungetService(arch_ref);
        getContext().ungetService(cs_ref);
    }

    @Test
    public void testRef() {
        ServiceReference arch_ref = ipojoHelper.getServiceReferenceByName(Architecture.class.getName(), instance4.getInstanceName());
        assertNotNull("Check architecture availability", arch_ref);
        InstanceDescription id = ((Architecture) getContext().getService(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity - 1", id.getState() == ComponentInstance.VALID);

        ServiceReference cs_ref = ipojoHelper.getServiceReferenceByName(CheckService.class.getName(), instance4.getInstanceName());
        assertNotNull("Check CheckService availability", cs_ref);
        CheckService cs = (CheckService) getContext().getService(cs_ref);
        Properties props = cs.getProps();
        // Check properties
        assertFalse("check CheckService invocation -1", ((Boolean) props.get("result")).booleanValue()); // False is returned (nullable)
        assertEquals("check void bind invocation -1", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -1", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -1", ((Integer) props.get("objectB")).intValue(), 0);
        assertEquals("check object unbind callback invocation -1", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -1", ((Integer) props.get("refB")).intValue(), 0);
        assertEquals("check ref unbind callback invocation -1", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -1", ((Integer) props.get("bothB")).intValue(), 0);
        assertEquals("check both unbind callback invocation -1", ((Integer) props.get("bothU")).intValue(), 0);

        fooProvider.start();

        id = ((Architecture) getContext().getService(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity - 2", id.getState() == ComponentInstance.VALID);

        assertNotNull("Check CheckService availability", cs_ref);
        cs = (CheckService) getContext().getService(cs_ref);
        props = cs.getProps();
        // Check properties
        assertTrue("check CheckService invocation -2", ((Boolean) props.get("result")).booleanValue());
        assertEquals("check void bind invocation -2", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -2", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -2", ((Integer) props.get("objectB")).intValue(), 0);
        assertEquals("check object unbind callback invocation -2", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -2", ((Integer) props.get("refB")).intValue(), 1);
        assertEquals("check ref unbind callback invocation -2", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -2", ((Integer) props.get("bothB")).intValue(), 0);
        assertEquals("check both unbind callback invocation -2", ((Integer) props.get("bothU")).intValue(), 0);

        fooProvider.stop();

        id = ((Architecture) getContext().getService(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity - 3", id.getState() == ComponentInstance.VALID);

        cs = (CheckService) getContext().getService(cs_ref);
        props = cs.getProps();
        // Check properties
        assertFalse("check CheckService invocation -3", ((Boolean) props.get("result")).booleanValue());
        assertEquals("check void bind invocation -3", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -3", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -3", ((Integer) props.get("objectB")).intValue(), 0);
        assertEquals("check object unbind callback invocation -3", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -3", ((Integer) props.get("refB")).intValue(), 1);
        assertEquals("check ref unbind callback invocation -3", ((Integer) props.get("refU")).intValue(), 1);
        assertEquals("check both bind callback invocation -3", ((Integer) props.get("bothB")).intValue(), 0);
        assertEquals("check both unbind callback invocation -3", ((Integer) props.get("bothU")).intValue(), 0);

        id = null;
        cs = null;
        getContext().ungetService(arch_ref);
        getContext().ungetService(cs_ref);
    }

    @Test
    public void testBoth() {
        ServiceReference arch_ref = ipojoHelper.getServiceReferenceByName(Architecture.class.getName(), instance5.getInstanceName());
        assertNotNull("Check architecture availability", arch_ref);
        InstanceDescription id = ((Architecture) getContext().getService(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity - 1", id.getState() == ComponentInstance.VALID);

        ServiceReference cs_ref = ipojoHelper.getServiceReferenceByName(CheckService.class.getName(), instance5.getInstanceName());
        assertNotNull("Check CheckService availability", cs_ref);
        CheckService cs = (CheckService) getContext().getService(cs_ref);
        Properties props = cs.getProps();
        // Check properties
        assertFalse("check CheckService invocation -1", ((Boolean) props.get("result")).booleanValue()); // False is returned (nullable)
        assertEquals("check void bind invocation -1", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -1", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -1", ((Integer) props.get("objectB")).intValue(), 0);
        assertEquals("check object unbind callback invocation -1", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -1", ((Integer) props.get("refB")).intValue(), 0);
        assertEquals("check ref unbind callback invocation -1", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -1", ((Integer) props.get("bothB")).intValue(), 0);
        assertEquals("check both unbind callback invocation -1", ((Integer) props.get("bothU")).intValue(), 0);

        fooProvider.start();

        id = ((Architecture) getContext().getService(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity - 2", id.getState() == ComponentInstance.VALID);

        assertNotNull("Check CheckService availability", cs_ref);
        cs = (CheckService) getContext().getService(cs_ref);
        props = cs.getProps();
        // Check properties
        assertTrue("check CheckService invocation -2", ((Boolean) props.get("result")).booleanValue());
        assertEquals("check void bind invocation -2", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -2", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -2", ((Integer) props.get("objectB")).intValue(), 0);
        assertEquals("check object unbind callback invocation -2", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -2", ((Integer) props.get("refB")).intValue(), 0);
        assertEquals("check ref unbind callback invocation -2", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -2", ((Integer) props.get("bothB")).intValue(), 1);
        assertEquals("check both unbind callback invocation -2", ((Integer) props.get("bothU")).intValue(), 0);

        fooProvider.stop();

        id = ((Architecture) getContext().getService(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity - 3", id.getState() == ComponentInstance.VALID);

        cs = (CheckService) getContext().getService(cs_ref);
        props = cs.getProps();
        // Check properties
        assertFalse("check CheckService invocation -3", ((Boolean) props.get("result")).booleanValue());
        assertEquals("check void bind invocation -3", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -3", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -3", ((Integer) props.get("objectB")).intValue(), 0);
        assertEquals("check object unbind callback invocation -3", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -3", ((Integer) props.get("refB")).intValue(), 0);
        assertEquals("check ref unbind callback invocation -3", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -3", ((Integer) props.get("bothB")).intValue(), 1);
        assertEquals("check both unbind callback invocation -3", ((Integer) props.get("bothU")).intValue(), 1);

        id = null;
        cs = null;
        getContext().ungetService(arch_ref);
        getContext().ungetService(cs_ref);
    }

}
