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
package com.mcartoixa.ant.sfdx.force.mdapi;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import java.io.File;
import org.apache.tools.ant.Project;

/**
 *
 * @author Mathieu Cartoixa
 */
public class DescribeMetadataTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            switch (key) {
                case "message":
                    this.log(value, Project.MSG_INFO);
                    break;
                default:
                    break;
            }
        }
    }

    public DescribeMetadataTask() {
        super();
    }

    public void setResultFile(final File resultFile) {
        if (resultFile != null) {
            getCommandline().createArgument().setValue("-f");
            getCommandline().createArgument().setValue(resultFile.getPath());
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
        return "force:mdapi:describemetadata";
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new DescribeMetadataTask.JsonParser();
    }
}
