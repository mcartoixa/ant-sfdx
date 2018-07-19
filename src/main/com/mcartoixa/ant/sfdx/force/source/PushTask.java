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
package com.mcartoixa.ant.sfdx.force.source;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class PushTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void doParse(final JSONObject json) {
            final JSONArray result = json.optJSONArray("result");
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    final Object value = result.get(i);
                    if (value instanceof JSONObject) {
                        final JSONObject object = (JSONObject) value;
                        final String filePath = object.optString("filePath");
                        if (filePath == null || filePath.isEmpty() || filePath.equals("N/A")) {
                            this.log(object.getString("error"), Project.MSG_ERR);
                        } else {
                            final String lineNumber = object.optString("lineNumber");
                            final String message = String.format(
                                    "%s:%s: %s error: %s",
                                    new File(filePath).getAbsolutePath(),
                                    lineNumber == null || lineNumber.isEmpty() ? "0" : lineNumber,
                                    object.getString("type"),
                                    object.getString("error")
                            );
                            this.log(message, Project.MSG_ERR);
                        }
                    }
                }
            }

            final JSONObject success = json.optJSONObject("result");
            if (success != null) {
                final JSONArray pushedSource = success.optJSONArray("pushedSource");
                if (pushedSource != null) {
                    for (int i = 0; i < pushedSource.length(); i++) {
                        final Object value = pushedSource.get(i);
                        if (value instanceof JSONObject) {
                            final JSONObject object = (JSONObject) value;
                            final String message = String.format(
                                    "%s %s %s",
                                    object.getString("state"),
                                    object.getString("type"),
                                    object.getString("fullName")
                            );
                            this.log(message, Project.MSG_INFO);
                        }
                    }
                }
            }

            super.doParse(json);
        }
    }

    public PushTask() {
        super();
    }

    @Override
    public void execute() throws BuildException {
        if (!this.getQuiet()) {
            this.log("Push in progress...", Project.MSG_INFO);
        }

        super.execute();

        if (!this.getQuiet()) {
            this.log("Push completed.", Project.MSG_INFO);
        }
    }

    @Override
    protected String getCommand() {
        return "force:source:push";
    }

    public void setForceOverwrite(final boolean forceOverwrite) {
        if (forceOverwrite) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setValue("-f");
        }
    }

    public void setIgnoreWarnings(final boolean ignoreWarnings) {
        if (ignoreWarnings) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setValue("-g");
        }
    }

    public void setTargetUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-u");
            arg.setValue(userName);
        }
    }

    public void setWait(final int wait) {
        final Commandline.Argument arg = getCommandline().createArgument();
        arg.setPrefix("-w");
        arg.setValue(Integer.toString(wait));
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new PushTask.JsonParser();
    }
}
