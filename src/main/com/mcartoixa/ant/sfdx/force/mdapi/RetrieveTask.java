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
package com.mcartoixa.ant.sfdx.force.mdapi;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;

/**
 *
 * @author Mathieu Cartoixa
 */
public class RetrieveTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            switch (key) {
                case "status":
                    this.log(value, Project.MSG_INFO);
                    break;
                default:
                    break;
            }
        }
    }

    public RetrieveTask() {
        super();
    }

    public void addPackage(final PackageNameWrapper packageName) {
        this.packages.add(packageName.getName());
    }

    public void setApiVersion(final int apiVersion) {
        final Commandline.Argument arg = getCommandline().createArgument();
        arg.setPrefix("-a");
        arg.setValue(Integer.toString(apiVersion));
    }

    public void setRetrieveTargetDir(final File retrieveTargetDir) {
        if (retrieveTargetDir != null) {
            final File dir = !retrieveTargetDir.exists() || retrieveTargetDir.isDirectory() ? retrieveTargetDir : retrieveTargetDir.getParentFile();

            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-r");
            arg.setValue(dir.getAbsolutePath());
        }
    }

    public void setSinglePackage(final boolean singlePackage) {
        if (singlePackage) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setValue("-s");
        }
    }

    public void setSourceDir(final File sourceDir) {
        if (sourceDir != null) {
            final File dir = !sourceDir.exists() || sourceDir.isDirectory() ? sourceDir : sourceDir.getParentFile();

            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-d");
            arg.setValue(dir.getAbsolutePath());
        }
    }

    public void setTargetUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-u");
            arg.setValue(userName);
        }
    }

    public void setUnpackaged(final File unpackaged) {
        if (unpackaged != null) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-k");
            arg.setValue(unpackaged.getAbsolutePath());
        }
    }

    public void setWait(final int wait) {
        final Commandline.Argument arg = getCommandline().createArgument();
        arg.setPrefix("-w");
        arg.setValue(Integer.toString(wait));
    }

    @SuppressWarnings("PMD.ConfusingTernary")
    @Override
    protected void createArguments() {
        if (!packages.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-p");
            arg.setValue(String.join(",", packages));
        }

        super.createArguments();
    }

    @Override
    protected String getCommand() {
        return "force:mdapi:retrieve";
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new RetrieveTask.JsonParser();
    }

    private transient final List<String> packages = new ArrayList<>();
}
