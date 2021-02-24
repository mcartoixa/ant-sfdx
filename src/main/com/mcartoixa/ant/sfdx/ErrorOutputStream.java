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

import java.io.IOException;
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
        this.errorMessage = "";
    }

    @Override
    protected void processLine(final String string) {
        if (string != null && !string.isEmpty()) {
            handler.log(string, Project.MSG_VERBOSE);
            this.errorMessage = this.errorMessage.concat(string + "\n");
        }
    }

    @Override
    public void close() throws IOException {
        if (!this.errorMessage.isEmpty()) {
            handler.setErrorMessage(this.errorMessage);
        }
        super.close();
    }

    private final transient ISfdxOutputHandler handler;
    private transient String errorMessage;
}
