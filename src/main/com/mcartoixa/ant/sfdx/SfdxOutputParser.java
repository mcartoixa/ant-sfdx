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

import org.apache.tools.ant.Project;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author mcartoixa
 */
public class SfdxOutputParser extends PumpStreamHandler {

    /* default */ static class ErrorParser implements ISfdxJsonParser {

        /* default */ ErrorParser(final SfdxTask task) {
            this.task = task;
        }

        @Override
        public void log(final String message, final int level) {
            this.task.log(message, level);
        }

        @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
        @Override
        public void parse(final JSONObject json) {
            if (json != null) {
                final String message = json.getString("message");
                if (message != null && !message.isEmpty()) {
                    this.log(message, Project.MSG_ERR);
                    this.task.setErrorMessage(message);

                }

                final JSONArray warnings = json.getJSONArray("warnings");
                if (warnings != null) {
                    for (int i = 0; i < warnings.length(); i++) {
                        final String w = warnings.getString(i);
                        if (w != null && !w.isEmpty()) {
                            this.log(w, Project.MSG_WARN);
                        }
                    }
                }

                final String action = json.getString("action");
                if (action != null && !action.isEmpty()) {
                    final String[] alines = action.split("\n");
                    for (final String a : alines) {
                        if (a != null && !a.isEmpty()) {
                            this.log(a, Project.MSG_INFO);
                        }
                    }
                }
            }
        }

        private final transient SfdxTask task;
    }

    /**
     * Creates log stream handler
     *
     * @param task the task for whom to log
     * @param parser the parser to use
     */
    public SfdxOutputParser(final SfdxTask task, final ISfdxJsonParser parser) {
        super(new JsonOutputStream(parser), new JsonOutputStream(new ErrorParser(task)));
    }

    /**
     * Stop the log stream handler.
     */
    @Override
    public void stop() {
        super.stop();
        FileUtils.close(getErr());
        FileUtils.close(getOut());
    }

}
