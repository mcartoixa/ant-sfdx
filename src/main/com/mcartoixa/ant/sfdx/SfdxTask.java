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
package com.mcartoixa.ant.sfdx;

/**
 *
 * @author mcartoixa
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class SfdxTask extends Task {

    public class JsonParser implements ISfdxJsonParser {

        protected JsonParser() {
        }

        @Override
        public void log(final String message, final int level) {
            SfdxTask.this.log(message, level);
        }

        @Override
        public void parse(final JSONObject json) {
            if (json != null) {
                this.log(json.toString(), Project.MSG_DEBUG);

                final JSONObject result = json.getJSONObject("result");
                if (result != null && SfdxTask.this.getResultProperty() != null && !SfdxTask.this.getResultProperty().isEmpty()) {
                    parseJsonObject(SfdxTask.this.getResultProperty(), result);
                }
            }
        }

        protected void handleValue(final String property, final String key, final String value) {
            SfdxTask.this.getProject().setNewProperty(property + "." + key, value);
        }

        @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
        private void parseJsonValue(final String property, final String key, final Object value) {
            if (value instanceof JSONObject) {
                parseJsonObject(property + "." + key, (JSONObject) value);
            } else if (value instanceof JSONArray) {
                final JSONArray array = (JSONArray) value;
                for (int i = 0; i < array.length(); i++) {
                    parseJsonValue(property + "." + key, Integer.toString(i), array.get(i));
                }
            } else {
                handleValue(property, key, value.toString());
            }
        }

        @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
        private void parseJsonObject(final String property, final JSONObject object) {
            object.keySet().forEach((key) -> {
                parseJsonValue(property, key.toLowerCase(Locale.ROOT), object.get(key));
            });
        }
    }

    protected SfdxTask() {
        super();
        cmd.setExecutable("sfdx");
    }

    @Override
    public void execute() throws BuildException {
        cmd.addArguments(new String[]{getCommand()});

        final List<String> arguments = createArguments();
        cmd.addArguments(arguments.toArray(new String[arguments.size()]));

        try {
            final Project p = getProject();
            final Execute exe = new Execute(new SfdxOutputParser(this, getParser()));
            exe.setAntRun(p);
            exe.setWorkingDirectory(p.getBaseDir());
            exe.setCommandline(cmd.getCommandline());
            final int r = exe.execute();

            if (Execute.isFailure(r)) {
                String message = this.errorMessage;
                if (message == null || message.isEmpty()) {
                    message = cmd.getExecutable() + " returned " + r;
                }
                throw new BuildException(message, getLocation());
            }
        } catch (IOException e) {
            throw new BuildException(e, getLocation());
        }
    }

    public void setResultProperty(final String resultProperty) {
        this.resultProperty = resultProperty;
    }

    protected abstract String getCommand();

    protected ISfdxJsonParser getParser() {
        return new JsonParser();
    }

    protected List<String> createArguments() {
        final List<String> ret = new ArrayList<>();
        ret.add("--json");
        return ret;
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getResultProperty() {
        return this.resultProperty;
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ void setErrorMessage(final String message) {
        this.errorMessage = message;
    }

    private final transient Commandline cmd = new Commandline();
    private transient String errorMessage;
    private transient String resultProperty;
}
