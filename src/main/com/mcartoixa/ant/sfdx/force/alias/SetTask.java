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
package com.mcartoixa.ant.sfdx.force.alias;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.types.Commandline;
import org.json.JSONObject;

/**
 *
 * @author Mathieu Cartoixa
 */
public class SetTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();

            this.aliases = new ArrayList<>();
        }

        @Override
        protected void doParse(final JSONObject json) {
            super.doParse(json);

            if (!SetTask.this.getQuiet()) {
                aliases.forEach((a) -> {
                    this.log(a.toString(), Project.MSG_INFO);
                });
            }
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            if (!SetTask.this.getQuiet()) {
                switch (key) {
                    case "alias":
                        currentAlias = new Alias();
                        currentAlias.setAlias(value);
                        aliases.add(currentAlias);
                        break;
                    case "value":
                        if (currentAlias != null) {
                            currentAlias.setValue(value);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        private transient Alias currentAlias;
        private final transient List<Alias> aliases;
    }

    public SetTask() {
        super();
    }

    public Property createAlias() {
        final Property ret = new Property();
        this.aliases.add(ret);
        return ret;
    }

    @Override
    protected String getCommand() {
        return "force:alias:set";
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    @Override
    protected void createArguments() {
        this.aliases.forEach((p) -> {
            final Commandline.Argument arg = this.getCommandline().createArgument();
            arg.setPrefix(p.getName() + "=");
            arg.setValue(p.getValue());
        });

        super.createArguments();
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new SetTask.JsonParser();
    }

    private final transient List<Property> aliases = new ArrayList<>();
}
