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

        @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
        @Override
        public void log(final String message, final int level) {
            if (message != null && !message.isEmpty()) {
                int l = level;
                if (SfdxTask.this.getQuiet() && level < Project.MSG_VERBOSE) {
                    l = Project.MSG_VERBOSE;
                }
                SfdxTask.this.log(message, l);
            }
        }

        @SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.NPathComplexity"})
        @Override
        public void parse(final JSONObject json) {
            if (json != null) {
                this.log("JSON received: " + json.toString(), Project.MSG_DEBUG);

                final int status = json.optInt("status");
                if (SfdxTask.this.getStatusProperty() != null && !SfdxTask.this.getStatusProperty().isEmpty()) {
                    SfdxTask.this.getProject().setNewProperty(SfdxTask.this.getStatusProperty(), Integer.toString(status));
                }

                final JSONObject result = json.optJSONObject("result");
                if (result != null) {
                    String property = SfdxTask.this.getResultProperty();
                    if (property == null) {
                        property = "";
                    }
                    parseJsonObject(property, result);
                }

                final JSONArray warnings = json.optJSONArray("warnings");
                if (warnings != null) {
                    for (int i = 0; i < warnings.length(); i++) {
                        final String w = warnings.getString(i);
                        if (w != null && !w.isEmpty()) {
                            this.log(w, Project.MSG_WARN);
                        }
                    }
                }

                final String message = json.optString("message");
                if (message != null && !message.isEmpty()) {
                    this.log(message, status > 0 ? Project.MSG_ERR : Project.MSG_INFO);
                    if (status > 0 && SfdxTask.this.getFailOnError()) {
                        SfdxTask.this.setErrorMessage(message);
                    }
                }

                final String action = json.optString("action");
                if (action != null && !action.isEmpty()) {
                    final String[] alines = action.split("\n");
                    for (final String a : alines) {
                        this.log(a, Project.MSG_INFO);
                    }
                }
            }
        }

        protected void handleValue(final String property, final String key, final String value) {
            if (SfdxTask.this.getResultProperty() != null && !SfdxTask.this.getResultProperty().isEmpty()) {
                SfdxTask.this.getProject().setNewProperty(property + "." + key, value);
            }
        }

        @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
        protected void parseJsonValue(final String property, final String key, final Object value) {
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
        protected void parseJsonObject(final String property, final JSONObject object) {
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
        this.checkConfiguration();

        cmd.createArgument(true).setValue(getCommand());
        createArguments();

        try {
            final Project p = getProject();
            final Execute exe = new Execute(new SfdxOutputParser(getParser()));
            exe.setAntRun(p);
            exe.setWorkingDirectory(p.getBaseDir());
            exe.setCommandline(cmd.getCommandline());
            final int r = exe.execute();

            if (this.getFailOnError() && Execute.isFailure(r) || this.hasErrorMessage()) {
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

    /**
     * If false, note errors but continue.
     *
     * @param failonerror true or false
     */
    public void setFailOnError(final boolean failonerror) {
        this.failonerror = failonerror;
    }

    /**
     * @param quiet "true" or "on"
     */
    public void setQuiet(final boolean quiet) {
        this.quiet = quiet;
        if (quiet) {
            this.failonerror = false;
        }
    }

    public void setResultProperty(final String resultProperty) {
        this.resultProperty = resultProperty;
    }

    public void setStatusProperty(final String statusProperty) {
        this.statusProperty = statusProperty;
    }

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void checkConfiguration() {
    }

    protected void createArguments() {
        getCommandline().createArgument().setValue("--json");
    }

    protected abstract String getCommand();

    protected Commandline getCommandline() {
        return cmd;
    }

    protected boolean getFailOnError() {
        return this.failonerror;
    }

    protected ISfdxJsonParser getParser() {
        return new JsonParser();
    }

    protected boolean getQuiet() {
        return this.quiet;
    }

    protected boolean hasErrorMessage() {
        return this.errorMessage != null && !this.errorMessage.isEmpty();
    }

    protected void setErrorMessage(final String message) {
        this.errorMessage = message;
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getResultProperty() {
        return this.resultProperty;
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getStatusProperty() {
        return this.statusProperty;
    }

    private final transient Commandline cmd = new Commandline();
    private transient String errorMessage;
    private transient boolean failonerror = true;
    private transient boolean quiet = false;
    private transient String resultProperty;
    private transient String statusProperty;
}
