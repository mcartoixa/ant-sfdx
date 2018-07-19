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
package com.mcartoixa.ant.sfdx.force.data.record;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Commandline;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class CreateTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
        @Override
        protected void doParse(final JSONObject json) {
            super.parse(json);

            if (json != null) {
                final JSONObject result = json.getJSONObject("result");
                if (result != null) {
                    if (result.getBoolean("success")) {
                        this.log("Record " + result.getString("id") + " created", Project.MSG_INFO);
                    } else {
                        if (json.has("errors")) {
                            final JSONArray errors = json.getJSONArray("errors");
                            if (errors != null) {
                                for (int i = 0; i < errors.length(); i++) {
                                    final String e = errors.getString(i);
                                    if (e != null && !e.isEmpty()) {
                                        this.log(e, Project.MSG_ERR);
                                        if (CreateTask.this.getFailOnError() && !CreateTask.this.hasErrorMessage()) {
                                            CreateTask.this.setErrorMessage("The record could not be created.");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public CreateTask() {
        super();
    }

    @Override
    protected String getCommand() {
        return "force:data:record:create";
    }

    public Property createField() {
        final Property ret = new Property();
        this.fields.add(ret);
        return ret;
    }

    public void setSObjectType(final String sObjectType) {
        if (sObjectType != null && !sObjectType.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-s");
            arg.setValue(sObjectType);
        }
    }

    public void setTargetUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-u");
            arg.setValue(userName);
        }
    }

    public void setUseToolingApi(final boolean useToolingApi) {
        if (useToolingApi) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setValue("-t");
        }
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @Override
    protected void createArguments() {
        if (!this.fields.isEmpty()) {
            final StringBuilder record = new StringBuilder();
            this.fields.forEach((f) -> {
                if (record.length() > 0) {
                    record.append(" ");
                }
                record.append(f.getName());
                record.append("='");
                record.append(f.getValue());
                record.append("'");
            });
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setLine("-v \"" + record + "\"");
        }

        super.createArguments();
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new CreateTask.JsonParser();
    }

    private final transient List<Property> fields = new ArrayList<>();
}
