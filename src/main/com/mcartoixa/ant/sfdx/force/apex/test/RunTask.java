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
package com.mcartoixa.ant.sfdx.force.apex.test;

import com.mcartoixa.ant.sfdx.ISfdxJsonParser;
import com.mcartoixa.ant.sfdx.SfdxTask;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.junit.AggregateTransformer;
import org.apache.tools.ant.taskdefs.optional.junit.XMLResultAggregator;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Mathieu Cartoixa
 */
public class RunTask extends SfdxTask {

    /* default */ class JsonParser extends SfdxTask.JsonParser {

        /* default */ JsonParser() {
            super();
        }

        @Override
        protected void doParse(final JSONObject json) {
            super.doParse(json);

            final JSONObject result = json.optJSONObject("result");
            if (result != null) {
                final JSONObject summary = result.optJSONObject("summary");
                if (summary != null) {
                    this.log(
                            String.format(
                                    "\nTests run: %d, Failures: %d, Skipped: %d, Time elapsed: %s\nCode coverage: %s\n\n",
                                    summary.optInt("testsRan"),
                                    summary.optInt("failing"),
                                    summary.optInt("skipped"),
                                    summary.optString("testExecutionTime"),
                                    summary.optString("testRunCoverage")
                            ),
                            Project.MSG_INFO
                    );
                }

                final JSONArray tests = result.optJSONArray("tests");
                if (tests != null) {
                    for (int i = 0; i < tests.length(); i++) {
                        final JSONObject test = tests.getJSONObject(i);
                        switch (test.getString("Outcome")) {
                            case "Fail":
                                this.log(
                                        String.format(
                                                "Testcase: %s:\tFAILED\n%s\n\tat %s\n\n",
                                                test.optString("FullName"),
                                                test.optString("Message"),
                                                test.optString("StackTrace")
                                        ),
                                        Project.MSG_ERR
                                );
                                if (RunTask.this.getFailOnError()) {
                                    RunTask.this.setErrorMessage("Some tests failed; see above for details.");
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }

        @Override
        protected void handleValue(final String property, final String key, final String value) {
            super.handleValue(property, key, value);

            switch (key) {
                case "testrunid":
                    RunTask.this.setRunId(value);
                    this.log(
                            String.format(
                                    "Test run %s was launched",
                                    value
                            ),
                            Project.MSG_INFO
                    );
                    break;
                default:
                    break;
            }
        }
    }

    private static class TempDirectoryHelper {

        private TempDirectoryHelper() {
        }

        // temporary directory location
        private static final File TMPDIR = new File(System.getProperty("java.io.tmpdir"));

        @SuppressWarnings("PMD.DefaultPackage")
        /* default */ static File location() {
            return TMPDIR;
        }

        // file name generation
        private static final SecureRandom RANDOM = new SecureRandom();

        @SuppressWarnings("PMD.DefaultPackage")
        /* default */ static File generateDirectory(final String prefix, final String suffix, final File dir)
                throws IOException {
            long n = RANDOM.nextLong();
            if (n == Long.MIN_VALUE) {
                n = 0;      // corner case
            } else {
                n = Math.abs(n);
            }

            // Use only the file name from the supplied prefix
            final String p = new File(prefix).getName();

            final String name = p + Long.toString(n) + suffix;
            return new File(dir, name);
        }
    }

    public RunTask() {
        super();
    }

    @Override
    protected String getCommand() {
        return "force:apex:test:run";
    }

    public void addConfiguredClass(final TestNameWrapper className) {
        this.classes.add(className.getName());
    }

    public void addConfiguredSuite(final TestNameWrapper suiteName) {
        this.suites.add(suiteName.getName());
    }

    public void addConfiguredTest(final TestNameWrapper test) {
        this.tests.add(test.getName());
    }

    public AggregateTransformer createReport() {
        final AggregateTransformer transformer = new AggregateTransformer(this.getXMLResultAggregator());
        transformers.add(transformer);
        return transformer;
    }

    public void setSynchronous(final boolean synchronous) {
        if (synchronous) {
            getCommandline().createArgument().setValue("-y");
        }
    }

    public void setTargetUserName(final String userName) {
        if (userName != null && !userName.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-u");
            arg.setValue(userName);
        }
    }

    public void setTestLevel(final TestLevel level) {
        final Commandline.Argument arg = getCommandline().createArgument();
        arg.setPrefix("-l");
        arg.setValue(level.name());
    }

    public void setToDir(final File toDir) {
        if (toDir != null) {
            this.toDir = toDir.isDirectory() ? toDir : toDir.getParentFile();

            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-d");
            arg.setValue(toDir.getAbsolutePath());
        }
    }

    public void setToFile(final String toFile) {
        if (toFile != null) {
            this.toFile = toFile;
        }
    }

    public void setWait(final int wait) {
        this.wait = wait;
    }

    @SuppressWarnings("PMD.ConfusingTernary")
    @Override
    protected void checkConfiguration() {
        if (this.toDir == null) {
            try {
                final File td = TempDirectoryHelper.generateDirectory("test-result-", "", TempDirectoryHelper.location());
                td.mkdirs();
                this.setToDir(td);
            } catch (IOException ex) {
                throw new BuildException(ex, getLocation());
            }
        }

        if (!this.classes.isEmpty() ? !this.suites.isEmpty() || !this.tests.isEmpty() : !this.suites.isEmpty() && !this.tests.isEmpty()) {
            throw new BuildException("The class, suite and test nested elements are mutually exclusive.");
        }
    }

    @SuppressWarnings("PMD.ConfusingTernary")
    @Override
    protected void createArguments() {
        if (!classes.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-n");
            arg.setValue(String.join(",", classes));
        } else if (!suites.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-s");
            arg.setValue(String.join(",", suites));
        } else if (!tests.isEmpty()) {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-t");
            arg.setValue(String.join(",", tests));
        }

        getCommandline().createArgument().setValue("-c");
        {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-r");
            arg.setValue("junit");
        }
        {
            final Commandline.Argument arg = getCommandline().createArgument();
            arg.setPrefix("-w");
            arg.setValue(Integer.toString(this.wait));
        }

        super.createArguments();
    }

    @SuppressWarnings({"PMD.ConfusingTernary", "PMD.DataflowAnomalyAnalysis"})
    @Override
    protected void onExecuted(final int result) {
        final File[] resultFiles = this.toDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File directory, final String fileName) {
                return fileName.equals("test-result-" + getRunId() + "-junit.xml");
            }
        });
        if (resultFiles.length > 0) {
            try {
                if (this.toFile != null) {
                    final FileUtils fu = FileUtils.getFileUtils();
                    fu.copyFile(resultFiles[0].getAbsolutePath(), this.toFile);
                } else {
                    // Required by transformers, via the virtual XMLResultAggregator
                    this.toFile = resultFiles[0].getAbsolutePath();
                }

                if (!transformers.isEmpty()) {
                    final Document document = getDocumentBuilder().parse(resultFiles[0].getAbsolutePath());
                    for (final AggregateTransformer transformer : transformers) {
                        transformer.setXmlDocument(document);
                        transformer.transform();
                    }
                }
            } catch (SAXException | IOException ex) {
                throw new BuildException(ex, getLocation());
            }
        }
    }

    @Override
    protected ISfdxJsonParser getParser() {
        return new RunTask.JsonParser();
    }

    protected void setRunId(final String runId) {
        this.runId = runId;
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    // AggregateTransformer requires a valid XMLResultAggregator instance
    private XMLResultAggregator getXMLResultAggregator() {
        final XMLResultAggregator ret = new XMLResultAggregator() {
            @Override
            public File getDestinationFile() {
                return new File(getToFile());
            }
        };
        ret.bindToOwner(this);
        return ret;
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getRunId() {
        return this.runId;
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ String getToFile() {
        return this.toFile;
    }

    @SuppressWarnings("PMD.DefaultPackage")
    /* default */ File getToDir() {
        return this.toDir;
    }

    /**
     * Create a new document builder. Will issue an
     * <tt>ExceptionInitializerError</tt>
     * if something is going wrong. It is fatal anyway.
     *
     * @return a new document builder to create a DOM
     */
    private static DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException pcex) {
            throw new ExceptionInInitializerError(pcex);
        }
    }

    private transient int wait = 6;
    private transient String runId;
    private transient String toFile;
    private transient File toDir;
    private transient final List<AggregateTransformer> transformers = new ArrayList<>();
    private transient final List<String> classes = new ArrayList<>();
    private transient final List<String> suites = new ArrayList<>();
    private transient final List<String> tests = new ArrayList<>();
}
