/*
 * Copyright 2020 Mathieu Cartoixa.
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
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class StatusTask extends SfdxTask {

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
                        final String message = String.format(
                                "%s %s: %s",
                                object.getString("type"),
                                object.getString("fullName"),
                                object.getString("state")
                        );
                        this.log(message, Project.MSG_INFO);
                    }
                }
            }

            super.doParse(json);
        }
    }

    public StatusTask() {
        super();
    }

    public void setScope(final StatusScope scope) {
        final Commandline.Argument arg = getCommandline().createArgument();
        switch (scope) {
            case All:
                arg.setValue("-a");
                break;
            case Local:
                arg.setValue("-l");
                break;
            case Remote:
                arg.setValue("-r");
                break;
            default:
                arg.setValue("");
                break;
        }
    }

    public void setTargetUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            getCommandline().createArgument().setValue("-u");
            getCommandline().createArgument().setValue(userName);
        }
    }

    @Override
    protected String getCommand() {
        return "force:source:status";
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new StatusTask.JsonParser();
    }
}
