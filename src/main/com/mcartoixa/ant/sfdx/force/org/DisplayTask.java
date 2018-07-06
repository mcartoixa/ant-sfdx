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

import java.util.List;

import com.mcartoixa.ant.sfdx.SfdxTask;

/**
 *
 * @author mcartoixa
 */
public class DisplayTask extends SfdxTask {

    public DisplayTask() {
        super();
    }

    @Override
    protected List<String> createArguments() {
        final List<String> ret = super.createArguments();

        if (targetUserName != null && !targetUserName.isEmpty()) {
            ret.add("-u");
            ret.add(targetUserName);
        }

        return ret;
    }

    @Override
    protected String getCommand() {
        return "force:org:display";
    }

    public void setTargetUserName(final String userName) {
        targetUserName = userName;
    }

    private transient String targetUserName;
}
