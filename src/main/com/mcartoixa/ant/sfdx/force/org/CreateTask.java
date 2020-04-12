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

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Commandline;

/**
 *
 * @author Mathieu Cartoixa
 */
public class CreateTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            if (!CreateTask.this.getQuiet()) {
                switch (key) {
                    case "orgid":
                        this.log("Org " + value + " created", Project.MSG_INFO);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public CreateTask() {
        super();
    }

    @Override
    protected String getCommand() {
        return "force:org:create";
    }

    public Property createParam() {
        final Property ret = new Property();
        this.params.add(ret);
        return ret;
    }

    public void setAlias(final String alias) {
        if (alias != null && !alias.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-a");
            arg.setValue(alias);
        }
    }

    public void setClientId(final String clientId) {
        if (clientId != null && !clientId.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-i");
            arg.setValue(clientId);
        }
    }

    public void setDefaultUserName(final boolean isDefaultUserName) {
        if (isDefaultUserName) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setValue("-s");
        }
    }

    public void setDefinitionFile(final File definitionFile) {
        if (definitionFile != null) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-f");
            arg.setFile(definitionFile);
        }
    }

    public void setDurationDays(final int days) {
        if (days > 0) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-d");
            arg.setValue(Integer.toString(days));
        }
    }

    public void setNoAncestors(final boolean noAncestors) {
        if (noAncestors) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setValue("-c");
        }
    }

    public void setNoNamespace(final boolean noNamespace) {
        if (noNamespace) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setValue("-n");
        }
    }

    public void setTargetDevHubUserName(final String devHubUserName) {
        if (devHubUserName != null && !devHubUserName.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-v");
            arg.setValue(devHubUserName);
        }
    }

    public void setWait(final int timeout) {
        if (timeout > 0) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-w");
            arg.setValue(Integer.toString(timeout));
        }
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @Override
    protected void createArguments() {
        this.params.forEach((p) -> {
            final Commandline.Argument arg = this.getCommandline().createArgument();
            arg.setPrefix(p.getName() + "=");
            arg.setValue(p.getValue());
        });

        super.createArguments();
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new CreateTask.JsonParser();
    }

    private final transient List<Property> params = new ArrayList<>();
}
