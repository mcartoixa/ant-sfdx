/*
 * Copyright 2020 Mathieu Cartoixa.
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
package com.mcartoixa.ant.sfdx.force.user;

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
public class CreateTaskTest {

    @Rule
    public final BuildFileRule buildRule = new BuildFileRule();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public CreateTaskTest() {
    }

    @Before
    public void setUp() {
        buildRule.configureProject("src/test/com/mcartoixa/ant/sfdx/force/user/create.xml", Project.MSG_DEBUG);
    }

    @Test
    public void executeShouldSetStatusProperty() {
        buildRule.executeTarget("execute");
        Assert.assertEquals("Status property should be set", "0", buildRule.getProject().getProperty("execute.status"));
    }

    @Test
    public void executeShouldSetResultOrgIdProperty() {
        buildRule.executeTarget("execute");
        Assert.assertEquals("OrgId result property should set", "000000000000000000", buildRule.getProject().getProperty("execute.result.orgid"));
    }

    @Test
    public void executeShouldAddJsonArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain --json argument", buildRule.getFullLog().contains("'--json'"));
    }

    @Test
    public void executeShouldAddAliasArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain -a argument", buildRule.getFullLog().contains("'-atestalias'"));
    }

    @Test
    public void executeShouldAddDefinitionFileArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain -f argument", buildRule.getFullLog().contains("'-f" + new File(buildRule.getProject().getBaseDir(), "testfile").getAbsolutePath() + "'"));
    }

    @Test
    public void executeShouldAddParamArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain param argument", buildRule.getFullLog().contains("'generatepassword=true'"));
    }

    @Test
    public void executeShouldAddTargetDevHubUsernameArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain -v argument", buildRule.getFullLog().contains("'-vtestdevhub'"));
    }

    @Test
    public void executeShouldAddTargetUsernameArgument() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Full log should contain -u argument", buildRule.getFullLog().contains("'-utestuser'"));
    }

    @Test
    public void executeShouldLogCreatedUser() {
        buildRule.executeTarget("execute");
        Assert.assertTrue("Created user should be logged " + buildRule.getLog(), buildRule.getLog().contains("User test@ant-sfdx.org created"));
    }

    @Test
    public void executeQuietShouldHaveNoOutput() {
        buildRule.executeTarget("execute-quiet");
        Assert.assertTrue("Log should be empty", buildRule.getLog().isEmpty());
    }
}
