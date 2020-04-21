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
package com.mcartoixa.ant.sfdx;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.LineOrientedOutputStream;

/**
 *
 * @author Mathieu Cartoixa
 */
public class ErrorOutputStream extends LineOrientedOutputStream {

    /**
     * Creates a new instance of this class.
     *
     * @param handler the handler with which to handle the output.
     */
    public ErrorOutputStream(final ISfdxOutputHandler handler) {
        super();
        this.handler = handler;
    }

    @Override
    protected void processLine(final String string) {
        if (string != null && !string.isEmpty()) {
            // Proper messages will be generated from the JSON response (if there is one!)
            handler.log(string, Project.MSG_VERBOSE);
        }
    }

    private final transient ISfdxOutputHandler handler;

}
