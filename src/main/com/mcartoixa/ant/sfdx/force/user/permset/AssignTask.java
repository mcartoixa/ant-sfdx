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
package com.mcartoixa.ant.sfdx.force.user.permset;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import org.apache.tools.ant.Project;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class AssignTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
        @Override
        protected void doParse(final JSONObject json) {
            super.doParse(json);

            if (json != null) {
                final JSONObject result = json.getJSONObject("result");
                if (result != null) {
                    if (result.has("successes")) {
                        final JSONArray successes = result.getJSONArray("successes");
                        if (successes != null) {
                            for (int i = 0; i < successes.length(); i++) {
                                final JSONObject s = successes.getJSONObject(i);
                                this.log(
                                        String.format(
                                                "Permission set %s assigned to %s",
                                                s.optString("value"),
                                                s.optString("name")
                                        ),
                                        Project.MSG_INFO
                                );
                            }
                        }
                    }
                    if (result.has("failures")) {
                        final JSONArray failures = result.getJSONArray("failures");
                        if (failures != null) {
                            for (int i = 0; i < failures.length(); i++) {
                                final JSONObject f = failures.getJSONObject(i);
                                final String message = f.optString("message");
                                if (message != null && !message.isEmpty()) {
                                    this.log(message, Project.MSG_ERR);
                                    if (AssignTask.this.getFailOnError() && !AssignTask.this.hasErrorMessage()) {
                                        AssignTask.this.setErrorMessage("Permissions could not be assigned.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public AssignTask() {
        super();
    }

    @Override
    protected String getCommand() {
        return "force:user:permset:assign";
    }

    public void setOnBehalfof(final String onBehalfOf) {
        if (onBehalfOf != null && !onBehalfOf.isEmpty()) {
            getCommandline().createArgument().setValue("-o");
            getCommandline().createArgument().setValue(onBehalfOf);
        }
    }

    public void setPermsetName(final String permsetName) {
        if (permsetName != null && !permsetName.isEmpty()) {
            getCommandline().createArgument().setValue("-n");
            getCommandline().createArgument().setValue(permsetName);
        }
    }

    public void setTargetUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            getCommandline().createArgument().setValue("-u");
            getCommandline().createArgument().setValue(userName);
        }
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new AssignTask.JsonParser();
    }
}
