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
package com.mcartoixa.ant.sfdx.force.org;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import java.util.List;

import com.mcartoixa.ant.sfdx.SfdxTask;
import org.json.JSONObject;

/**
 *
 * @author mcartoixa
 */
public class DisplayTask extends SfdxTask {

    /* default */ class JsonParser implements ISfdxJsonParser {

        /* default */ JsonParser() {
        }

        @Override
        public void log(final String message, final int level) {
            DisplayTask.this.log(message, level);
        }

        @Override
        public void parse(final JSONObject json) {
        }
    }

    public DisplayTask() {
        super();

        parser = new JsonParser();
    }

    @Override
    protected List<String> createArguments() {
        final List<String> ret = super.createArguments();

        if (targetUserName != null && !targetUserName.isEmpty()) {
            ret.add("-u ".concat(targetUserName));
        }

        return ret;
    }

    @Override
    protected String getCommand() {
        return "force:org:display";
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return parser;
    }

    public void setTargetUserName(final String userName) {
        targetUserName = userName;
    }

    private final JsonParser parser;
    private transient String targetUserName;
}
