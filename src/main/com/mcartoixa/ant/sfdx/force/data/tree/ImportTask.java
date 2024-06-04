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
package com.mcartoixa.ant.sfdx.force.data.tree;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.ResourceUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
@SuppressWarnings("PMD.LongVariable")
public class ImportTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @SuppressWarnings({"PMD.EmptyCatchBlock", "PMD.LongVariable"})
        @Override
        protected void doParse(final JSONObject json) {
            final JSONArray result = json.optJSONArray("result");
            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    final Object value = result.get(i);
                    if (value instanceof JSONObject) {
                        final JSONObject object = (JSONObject) value;
                        final String refId = object.getString("refId");
                        final String id = object.getString("id");
                        final String referencesProperty = ImportTask.this.getReferencesProperty();
                        if (referencesProperty != null && !referencesProperty.isEmpty()) {
                            ImportTask.this.getProject().setNewProperty(referencesProperty + "." + refId.toLowerCase(Locale.ROOT), id);
                        }

                        final String message = String.format(
                                "%s imported (%s).",
                                refId,
                                id
                        );
                        this.log(message, Project.MSG_INFO);
                    }
                }
            }

            final String errorMessage = json.optString("message");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // JSON embedded inside JSON...
                try {
                    final JSONObject errorJson = new JSONObject(errorMessage);
                    final boolean hasErrors = errorJson.optBoolean("hasErrors", false);
                    if (hasErrors) {
                        final JSONArray results = errorJson.optJSONArray("results");
                        if (results != null) {
                            json.remove("message");
                            ImportTask.this.setErrorMessage(
                                    String.format(
                                            "There were %d error(s) while importing data.",
                                            results.length()
                                    )
                            );
                            for (int i = 0; i < results.length(); i++) {
                                final Object value = results.get(i);
                                if (value instanceof JSONObject) {
                                    final JSONObject object = (JSONObject) value;
                                    final JSONArray errors = object.getJSONArray("errors");
                                    String errorMessages = "";
                                    for (int j = 0; j < errors.length(); j++) {
                                        if (!errorMessages.isEmpty()) {
                                            errorMessages = errorMessages.concat("\n\t");
                                        }
                                        errorMessages = errorMessages.concat(errors.getJSONObject(j).getString("message"));
                                    }
                                    final String message = String.format(
                                            "%s not imported: %s",
                                            object.getString("referenceId"),
                                            errorMessages
                                    );
                                    this.log(message, Project.MSG_ERR);
                                }
                            }
                        }
                    }
                } catch (JSONException jex) {
                    // Error message is not JSON
                }

            }

            super.doParse(json);
        }
    }

    public ImportTask() {
        super();
    }

    @Override
    protected void checkConfiguration() {
        if (this.fileSets.isEmpty() && this.resources == null) {
            throw new BuildException("No resources specified", getLocation());
        }

        super.checkConfiguration();
    }

    @Override
    protected String getCommand() {
        return "force:data:tree:import";
    }

    public void addFileset(final FileSet set) {
        this.fileSets.add(set);
    }

    public void addFilelist(final FileList list) {
        if (this.resources == null) {
            this.resources = new Union();
        }
        this.resources.add(list);
    }

    public void setPlan(final File plan) {
        if (plan != null) {
            getCommandline().createArgument().setValue("-p");
            getCommandline().createArgument().setValue(plan.getPath());
        }
    }

    public void setReferencesProperty(final String referencesProperty) {
        this.referencesProperty = referencesProperty;
    }

    public void setTargetUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            getCommandline().createArgument().setValue("-u");
            getCommandline().createArgument().setValue(userName);
        }
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @Override
    protected void createArguments() {
        final StringBuilder sobjecttreefiles = new StringBuilder();
        for (final FileSet fs : this.fileSets) {
            final DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            for (final String file : ds.getIncludedFiles()) {
                if (sobjecttreefiles.length() > 0) {
                    sobjecttreefiles.append(",");
                }
                try {
                    final Path path = Paths.get(new File(ds.getBasedir(), file).getCanonicalPath());
                    final Path base = Paths.get(this.getProject().getBaseDir().getCanonicalPath());

                    sobjecttreefiles.append(base.relativize(path));
                } catch (IOException ioex) {
                    this.log(ioex.getMessage(), this.getQuiet() ? Project.MSG_WARN : Project.MSG_VERBOSE);
                }
            }
        }
        if (this.resources != null) {
            for (final Resource r : this.resources) {
                final FileProvider fp = r.as(FileProvider.class);
                if (fp != null) {
                    final FileResource fr = ResourceUtils.asFileResource(fp);
                    if (sobjecttreefiles.length() > 0) {
                        sobjecttreefiles.append(",");
                    }
                    try {
                        final Path path = Paths.get(fr.getFile().getCanonicalPath());
                        final Path base = Paths.get(this.getProject().getBaseDir().getCanonicalPath());

                        sobjecttreefiles.append(base.relativize(path));
                    } catch (IOException ioex) {
                        this.log(ioex.getMessage(), this.getQuiet() ? Project.MSG_WARN : Project.MSG_VERBOSE);
                    }
                }
            }
        }
        if (sobjecttreefiles.length() > 0) {
            getCommandline().createArgument().setValue("-f");
            getCommandline().createArgument().setValue(sobjecttreefiles.toString());
        }

        super.createArguments();
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new ImportTask.JsonParser();
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getReferencesProperty() {
        return this.referencesProperty;
    }

    private transient final List<FileSet> fileSets = new ArrayList<>();
    private transient String referencesProperty;
    private transient Union resources = null;
}
