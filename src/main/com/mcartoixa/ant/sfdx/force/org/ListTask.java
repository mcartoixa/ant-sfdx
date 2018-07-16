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
package com.mcartoixa.ant.sfdx.force.org;

import com.mcartoixa.ant.sfdx.SfdxTask;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class ListTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();

            this.nonScratchOrgs = new ArrayList<>();
            this.scratchOrgs = new ArrayList<>();
        }

        @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
        @Override
        public void parse(final JSONObject json) {
            super.parse(json);

            if (!ListTask.this.getQuiet()) {
                nonScratchOrgs.forEach((nso) -> {
                    this.log(nso.toString(), Project.MSG_INFO);
                    scratchOrgs.forEach((so) -> {
                        if (so.getDevHubOrgId() == null ? nso.getOrgId() == null : so.getDevHubOrgId().equals(nso.getOrgId())) {
                            this.log("\t" + so.toString(), Project.MSG_INFO);
                        }
                    });
                });
            }
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            if (!ListTask.this.getQuiet()) {
                switch (key) {
                    case "nonscratchorgs":
                        currentOrg = new Organization();
                        nonScratchOrgs.add(currentOrg);
                        break;
                    case "scratchorgs":
                        currentOrg = new Organization();
                        scratchOrgs.add(currentOrg);
                        break;
                    case "alias":
                        if (currentOrg != null) {
                            currentOrg.setAlias(value);
                        }
                        break;
                    case "devhuborgid":
                        if (currentOrg != null) {
                            currentOrg.setDevHubOrgId(value);
                        }
                        break;
                    case "expirationdate":
                        if (currentOrg != null) {
                            currentOrg.setExpirationDate(value);
                        }
                        break;
                    case "orgid":
                        if (currentOrg != null) {
                            currentOrg.setOrgId(value);
                        }
                        break;
                    case "orgname":
                        if (currentOrg != null) {
                            currentOrg.setOrgName(value);
                        }
                        break;
                    case "username":
                        if (currentOrg != null) {
                            currentOrg.setUserName(value);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        private transient Organization currentOrg;
        private final transient List<Organization> nonScratchOrgs;
        private final transient List<Organization> scratchOrgs;
    }

    public ListTask() {
        super();
    }

    @Override
    protected String getCommand() {
        return "force:org:list";
    }

    public void setAll(final boolean all) {
        if (all) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setValue("--all");
        }
    }

    public void setClean(final boolean clean) {
        if (clean) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setValue("--clean");
        }
    }

    @Override
    protected void createArguments() {
        this.getCommandline().createArgument()
                .setValue("-p"); // no prompt
    }
}
