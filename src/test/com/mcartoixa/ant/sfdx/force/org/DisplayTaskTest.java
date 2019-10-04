/*
 * Copyright 2018 Mathieu Cartoixa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mcartoixa.ant.sfdx.force.org;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildFileRule;
import org.apache.tools.ant.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Mathieu Cartoixa
 */
public class DisplayTaskTest {

    @Rule
    public final BuildFileRule buildRule = new BuildFileRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public DisplayTaskTest() {
     }

    @Before
    public void setUp() {
        buildRule.configureProject("src/test/com/mcartoixa/ant/sfdx/force/org/display.xml", Project.MSG_DEBUG);
    }

    @Ignore("Not ready yet: environment specific")
    @Test
    public void testDefault() {
        thrown.expect(BuildException.class);
        thrown.expectMessage("No defaultusername org found");

        buildRule.executeTarget("execute-default");
        Assert.assertEquals("1", buildRule.getProject().getProperty("execute-default.status"));
    }

    @Ignore("Not ready yet: environment specific")
    @Test
    public void testDefaultNoError() {
        buildRule.executeTarget("execute-default-noerror");
        Assert.assertEquals("1", buildRule.getProject().getProperty("execute-default-noerror.status"));
    }

    @Ignore("Not ready yet: environment specific")
    @Test
    public void testDevw() {
        buildRule.executeTarget("execute-devw");
        Assert.assertEquals("", buildRule.getFullLog());
    }
}
