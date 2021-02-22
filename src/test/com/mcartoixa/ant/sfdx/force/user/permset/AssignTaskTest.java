/*
 * Copyright 2021 Mathieu Cartoixa.
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
package com.mcartoixa.ant.sfdx.force.user.permset;

import java.io.File;
import org.apache.tools.ant.BuildFileRule;
import org.apache.tools.ant.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Mathieu Cartoixa
 */
public class AssignTaskTest {

    @Rule
    public final BuildFileRule buildRule = new BuildFileRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public AssignTaskTest() {
    }

    @Before
    public void setUp() {
        buildRule.configureProject("src/test/com/mcartoixa/ant/sfdx/force/user/permset/assign.xml", Project.MSG_DEBUG);
    }

    @Test
    public void executeShouldSetStatusProperty() {
        buildRule.executeTarget("execute");
        Assert.assertEquals("Status property should be set", "0", buildRule.getProject().getProperty("execute.status"));
    }

    @Test
    public void executeShouldAddJsonArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain --json argument", buildRule.getFullLog().contains("'--json'"));
    }

    @Test
    public void executeShouldAddPermsetNameArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain -n argument", buildRule.getFullLog().contains("'-n'" + System.lineSeparator() + "'testpermset'"));
    }

    @Test
    public void executeShouldAddOnBehalfOfArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain -o argument", buildRule.getFullLog().contains("'-o'" + System.lineSeparator() + "'testtarget'"));
    }

    @Test
    public void executeShouldAddTargetUsernameArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain -u argument", buildRule.getFullLog().contains("'-u'" + System.lineSeparator() + "'testuser'"));
    }

    @Test
    public void executeShouldLogAssignedPermissionSet() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Assigned permission set should be logged " + buildRule.getLog(), buildRule.getLog().contains("Permission set testpermset assigned to testuser"));
    }

    @Test
    public void executeQuietShouldHaveNoOutput() {
        buildRule.executeTarget("execute-quiet");
        Assert.assertTrue("Log should be empty", buildRule.getLog().isEmpty());
    }

}
