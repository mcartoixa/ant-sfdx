/*
 * Copyright 2019 Mathieu Cartoixa.
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
import com.mcartoixa.ant.sfdx.force.apex.test.TestNameWrapper;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class DeployTask extends SfdxTask {

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
                final JSONArray deployedSource = success.optJSONArray("deployedSource");
                if (deployedSource != null) {
                    for (int i = 0; i < deployedSource.length(); i++) {
                        final Object value = deployedSource.get(i);
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

    public DeployTask() {
        super();
    }

    public void addConfiguredMetadata(final MetadataNameWrapper metadata) {
        this.metadata.add(metadata.getName());
    }

    public void addConfiguredRunTests(final TestNameWrapper runTests) {
        this.runTests.add(runTests.getName());
    }

    public Path createSourcepath() {
        if (this.sourcePath == null) {
            this.sourcePath = new Path(this.getProject());
        }
        return this.sourcePath.createPath();
    }

    public void setCheckOnly(final boolean checkOnly) {
        if (checkOnly) {
            getCommandline().createArgument().setValue("-c");
        }
    }

    public void setIgnoreErrors(final boolean ignoreErrors) {
        if (ignoreErrors) {
            getCommandline().createArgument().setValue("-o");
        }
    }

    public void setIgnoreWarnings(final boolean ignoreWarnings) {
        if (ignoreWarnings) {
            getCommandline().createArgument().setValue("-g");
        }
    }

    public void setManifest(final File manifest) {
        if (manifest != null) {
            getCommandline().createArgument().setValue("-x");
            getCommandline().createArgument().setFile(manifest);
        }
    }

    public void setSourcepath(final Path sourcePath) {
        if (this.sourcePath == null) {
            this.sourcePath = sourcePath;
        } else {
            this.sourcePath.append(sourcePath);
        }
    }

    public void setSourcepathRef(final Reference ref) {
        this.createSourcepath().setRefid(ref);

    }

    public void setTargetUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            getCommandline().createArgument().setValue("-u");
            getCommandline().createArgument().setValue(userName);
        }
    }

    public void setTestLevel(final TestLevel level) {
        final Commandline.Argument arg = getCommandline().createArgument();
        arg.setPrefix("-l");
        arg.setValue(level.name());
    }

    public void setValidateddeployrequestid(final String vdrId) {
        if (vdrId != null && !vdrId.isEmpty()) {
            getCommandline().createArgument().setValue("-q");
            getCommandline().createArgument().setValue(vdrId);
        }
    }

    public void setWait(final int wait) {
        this.wait = wait;
    }

    @Override
    protected void createArguments() {
        if (!this.metadata.isEmpty()) {
            getCommandline().createArgument().setValue("-m");
            getCommandline().createArgument().setValue(String.join(",", this.metadata));
        }

        if (this.sourcePath != null) {
            // Commandline.Argument cannot join path with commas, so we are faking it
            final Commandline.Argument fakeArg = new Commandline.Argument();
            fakeArg.setPath(this.sourcePath);
            final String[] sp = Arrays.stream(fakeArg.getParts())
                    .map(p -> p.replace(File.pathSeparatorChar, ','))
                    .toArray(String[]::new);

            getCommandline().createArgument().setValue("-p");
            getCommandline().createArgument().setValue(String.join(",", sp));
        }

        if (!this.runTests.isEmpty()) {
            getCommandline().createArgument().setValue("-r");
            getCommandline().createArgument().setValue(String.join(",", this.runTests));
        }

        {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-w");
            arg.setValue(Integer.toString(this.wait));
        }

        super.createArguments();
    }

    @Override
    protected String getCommand() {
        return "force:source:deploy";
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new DeployTask.JsonParser();
    }

    private transient int wait = 33;
    private transient Path sourcePath;
    private transient final List<String> runTests = new ArrayList<>();
    private transient final List<String> metadata = new ArrayList<>();
}
