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
package com.mcartoixa.ant.sfdx.force.apex;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import java.io.File;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class ExecuteTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
        @Override
        public void parse(final JSONObject json) {
            super.parse(json);

            if (json != null) {
                final JSONObject result = json.getJSONObject("result");
                if (result != null) {
                    if (!result.getBoolean("success")) {
                        final String message = String.format(
                                "%s:%d: %s: %s",
                                getApexCodeFile().getAbsolutePath(),
                                result.optInt("line"),
                                result.getBoolean("compiled") ? "error" : "exception",
                                result.getBoolean("compiled") ? result.optString("exceptionMessage") : result.optString("compileProblem")
                        );
                        this.log(message, Project.MSG_ERR);
                        if (ExecuteTask.this.getFailOnError()) {
                            ExecuteTask.this.setErrorMessage(
                                    result.getBoolean("compiled")
                                    ? "An error ocurred during APEX compilation."
                                    : "An exception occurred dunring APEX execution."
                            );
                        }
                    }

                    final String logs = result.optString("logs");
                    if (logs != null && !logs.isEmpty()) {
                        final String[] llines = logs.split("\n");
                        for (final String l : llines) {
                            this.log(l, Project.MSG_VERBOSE);
                        }
                    }
                }
            }
        }
    }

    public ExecuteTask() {
        super();
    }

    @Override
    protected String getCommand() {
        return "force:apex:execute";
    }

    public void setApexCodeFile(final File apexCodeFile) {
        this.apexCodefile = apexCodeFile;

        if (apexCodeFile != null) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-f");
            arg.setFile(apexCodeFile);
        }
    }

    public void setTargetUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-u");
            arg.setValue(userName);
        }
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new ExecuteTask.JsonParser();
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ File getApexCodeFile() {
        return this.apexCodefile;
    }

    private transient File apexCodefile;
}
