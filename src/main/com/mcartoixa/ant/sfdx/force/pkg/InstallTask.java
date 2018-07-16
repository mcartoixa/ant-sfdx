/*
 * Copyright 2018 mcartoixa.
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
package com.mcartoixa.ant.sfdx.force.pkg;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline.Argument;

/**
 *
 * @author mcartoixa
 */
public class InstallTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            if (!InstallTask.this.getQuiet()) {
                switch (key) {
                    case "status":
                        switch (value) {
                            case "IN_PROGRESS":
                                this.log("Package " + InstallTask.this.getPackage() + " installation in progress...", Project.MSG_INFO);
                                break;
                            case "SUCCESS":
                                this.log("Package " + InstallTask.this.getPackage() + " installation succeeded", Project.MSG_INFO);
                                break;
                            default:
                                this.log("Package " + InstallTask.this.getPackage() + " installation status: " + value, Project.MSG_INFO);
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public InstallTask() {
        super();
    }

    public void setInstallationKey(final String key) {
        if (key != null && !key.isEmpty()) {
            final Argument arg = getCommandline().createArgument();
            arg.setPrefix("-k");
            arg.setValue(key);
        }
    }

    public void setPackage(final String id) {
        this.packageId = id;

        if (id != null && !id.isEmpty()) {
            final Argument arg = getCommandline().createArgument();
            arg.setPrefix("--package");
            arg.setValue(id);
        }
    }

    public void setPublishWait(final int timeout) {
        if (timeout > 0) {
            final Argument arg = getCommandline().createArgument();
            arg.setPrefix("--publishwait");
            arg.setValue(Integer.toString(timeout));
        }
    }

    public void setTargetUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            final Argument arg = getCommandline().createArgument();
            arg.setPrefix("-u");
            arg.setValue(userName);
        }
    }

    public void setWait(final int timeout) {
        if (timeout > 0) {
            final Argument arg = getCommandline().createArgument();
            arg.setPrefix("-w");
            arg.setValue(Integer.toString(timeout));
        }
    }

    @Override
    protected void createArguments() {
        this.getCommandline().createArgument()
                .setValue("-r"); // no prompt
    }

    @Override
    protected String getCommand() {
        return "force:package:install";
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new JsonParser();
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getPackage() {
        return this.packageId;
    }

    private transient String packageId;
}
