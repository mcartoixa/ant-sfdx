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
package com.mcartoixa.ant.sfdx;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import org.apache.tools.ant.Project;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author mcartoixa
 */
public class JsonOutputStream extends OutputStream {

    /**
     * Creates a new instance of this class.
     *
     * @param parser the parser with which to parse the JSON
     */
    public JsonOutputStream(final ISfdxJsonParser parser) {
        super();
        this.parser = parser;
    }

    @Override
    public void close() throws IOException {
        this.processBuffer(new String(buffer.toByteArray()));
    }

    @Override
    public final void write(final int b) throws IOException {
        buffer.write(b);
    }

    private void processBuffer(final String string) throws IOException {
        if (string != null && !string.isEmpty()) {
            try {
                final JSONObject json = new JSONObject(string);
                parser.parse(json);
            } catch (JSONException jex) {
                parser.log(jex.getLocalizedMessage(), Project.MSG_DEBUG);
                parser.log(string, Project.MSG_WARN);
            }
        }
    }

    private final transient ISfdxJsonParser parser;
    private final transient ByteArrayOutputStream buffer = new ByteArrayOutputStream(INITIAL_SIZE);

    private static final int INITIAL_SIZE = 132;

}
