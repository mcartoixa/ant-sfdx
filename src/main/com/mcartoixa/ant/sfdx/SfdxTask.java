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
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.condition.Os;
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

        @Override
        public void parse(final JSONObject json) {
            if (json != null) {
                this.log("JSON received: " + json.toString(), Project.MSG_DEBUG);

                doParse(json);
            }
        }

        @SuppressWarnings({"PMD.DataflowAnomalyAnalysis", "PMD.NPathComplexity"})
        protected void doParse(final JSONObject json) {
            final int status = json.optInt("status");
            if (SfdxTask.this.getStatusProperty() != null && !SfdxTask.this.getStatusProperty().isEmpty()) {
                SfdxTask.this.getProject().setNewProperty(SfdxTask.this.getStatusProperty(), Integer.toString(status));
            }

            final JSONObject result = json.optJSONObject("result");
            if (result == null) {
                final JSONArray results = json.optJSONArray("result");
                if (results != null) {
                    String property = SfdxTask.this.getResultProperty();
                    if (property == null) {
                        property = "";
                    }
                    parseJsonArray(property, results);
                }
            } else {
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
                this.log(action, Project.MSG_INFO);
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
                parseJsonArray(property + "." + key, array);
            } else {
                handleValue(property, key, value.toString());
            }
        }

        protected void parseJsonArray(final String property, final JSONArray array) {
            for (int i = 0; i < array.length(); i++) {
                parseJsonValue(property, Integer.toString(i), array.get(i));
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
    }

    @SuppressWarnings("PMD.AvoidUncheckedExceptionsInSignatures")
    @Override
    public void execute() throws BuildException {
        this.prepareContext();
        this.checkConfiguration();

        this.getCommandline().createArgument(true).setValue(getCommand());
        this.createArguments();

        try {
            final Project p = getProject();
            final Execute exe = new Execute(new SfdxOutputParser(getParser()));
            exe.setAntRun(p);
            exe.setEnvironment(new String[]{"SFDX_AUTOUPDATE_DISABLE=true"});
            exe.setWorkingDirectory(p.getBaseDir());
            exe.setCommandline(this.getCommandline().getCommandline());
            if (this.getExecutable().endsWith(".cmd")) {
                exe.setVMLauncher(false);
            }
            final int r = exe.execute();

            this.onExecuted(r);

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

    public void setExecutable(final String executable) {
        this.getCommandline().setExecutable(executable);
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

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis") // cf. https://stackoverflow.com/a/21608520/8696
    protected void checkConfiguration() {
        if (this.getExecutable() == null) {
            this.setExecutable("sfdx");
            if (IS_DOS) {
                final String path = System.getenv("PATH");
                final String pathSeparator = System.getProperty("path.separator");

                for (final String pathElement : path.split(pathSeparator)) {
                    final File command = new File(pathElement, "sfdx.cmd");
                    if (command.isFile()) {
                        this.setExecutable("sfdx.cmd");
                        break;
                    }
                    final File executable = new File(pathElement, "sfdx.exe");
                    if (executable.isFile()) {
                        this.setExecutable("sfdx.exe");
                        break;
                    }
                }
            }
        }
    }

    protected void createArguments() {
        this.getCommandline().createArgument().setValue("--json");
    }

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void prepareContext() {
    }

    protected abstract String getCommand();

    protected Commandline getCommandline() {
        return this.cmd;
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

    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    protected void onExecuted(final int result) {
    }

    protected void setErrorMessage(final String message) {
        this.errorMessage = message;
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getExecutable() {
        return this.getCommandline().getExecutable();
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

    private static final boolean IS_DOS = Os.isFamily("dos");
}
