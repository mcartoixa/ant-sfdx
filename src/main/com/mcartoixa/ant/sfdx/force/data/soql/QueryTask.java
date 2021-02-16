/*
 * Copyright 2021 Mathieu Cartoixa.
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
package com.mcartoixa.ant.sfdx.force.data.soql;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import org.apache.tools.ant.Project;

/**
 *
 * @author Mathieu Cartoixa
 */
public class QueryTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            if (!QueryTask.this.getQuiet()) {
                switch (key) {
                    case "totalsize":
                        this.log(value + " record(s) returned.", Project.MSG_INFO);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public QueryTask() {
        super();
    }

    public void setQuery(final String query) {
        if (query != null && !query.isEmpty()) {
            getCommandline().createArgument().setValue("-q");
            getCommandline().createArgument().setValue(query);
        }
    }

    public void setTargetUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            getCommandline().createArgument().setValue("-u");
            getCommandline().createArgument().setValue(userName);
        }
    }

    public void setUseToolingApi(final boolean useToolingApi) {
        if (useToolingApi) {
            getCommandline().createArgument().setValue("-t");
        }
    }

    @Override
    protected String getCommand() {
        return "force:data:soql:query";
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new QueryTask.JsonParser();
    }

}
