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
package com.mcartoixa.ant.sfdx.force.limits.api;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import org.apache.tools.ant.Project;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class DisplayTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void doParse(final JSONObject json) {
            super.doParse(json);

            if (json != null) {
                final JSONArray result = json.optJSONArray("result");
                if (result != null) {
                    for (int i = 0; i < result.length(); i++) {
                        final JSONObject limit = result.optJSONObject(i);
                        if (limit != null) {
                            this.log(
                                    String.format(
                                            "%s: %s/%s",
                                            limit.getString("name"),
                                            limit.getString("remaining"),
                                            limit.getString("max")
                                    ),
                                    Project.MSG_INFO
                            );
                        }
                    }
                }
            }
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            // Empty
        }
    }

    public DisplayTask() {
        super();
    }

    @Override
    protected String getCommand() {
        return "force:limits:api:display";
    }

    public void setTargetUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            getCommandline().createArgument().setValue("-u");
            getCommandline().createArgument().setValue(userName);
        }
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new DisplayTask.JsonParser();
    }
}
