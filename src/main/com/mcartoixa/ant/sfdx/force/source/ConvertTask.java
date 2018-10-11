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
package com.mcartoixa.ant.sfdx.force.source;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import java.io.File;
import org.apache.tools.ant.types.Commandline;

/**
 *
 * @author Mathieu Cartoixa
 */
public class ConvertTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }
    }

    public ConvertTask() {
        super();
    }

    @Override
    protected String getCommand() {
        return "force:source:convert";
    }

    public void setPackageName(final String name) {
        if (name != null && !name.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setLine("--packagename " + name);
        }
    }

    public void setOutputDir(final File outputDir) {
        if (outputDir != null) {
            final File dir = outputDir.isDirectory() ? outputDir : outputDir.getParentFile();

            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-r");
            arg.setValue(dir.getAbsolutePath());
        }
    }

    public void setRootDir(final File rootDir) {
        if (rootDir != null) {
            final File dir = rootDir.isDirectory() ? rootDir : rootDir.getParentFile();

            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-r");
            arg.setValue(dir.getAbsolutePath());
        }
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new ConvertTask.JsonParser();
    }
}
