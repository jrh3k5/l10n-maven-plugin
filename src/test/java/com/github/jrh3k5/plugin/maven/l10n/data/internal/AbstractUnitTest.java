/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jrh3k5.plugin.maven.l10n.data.internal;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * A skeleton definition of common-use methods for unit tests.
 * 
 * @author Joshua Hyde
 * @since 1.1
 */

public class AbstractUnitTest {
    /**
     * A {@link Rule} used to retrieve the current test name.
     */
    @Rule
    public TestName testName = new TestName();

    /**
     * Get a reference to a file for testing purposes.
     * 
     * @param filename
     *            The name of the file to be used in the test.
     * @return A {@link File} reference representing a file that can be used for testing.
     * @throws IOException
     *             If any errors occur during the test run.
     */
    protected File getTestFile(String filename) throws IOException {
        final File testFile = new File(String.format("target/test-resources/%s/%s/%s", getClass().getCanonicalName(), testName.getMethodName(), filename));
        FileUtils.forceMkdir(testFile.getParentFile());
        return testFile;
    }
}
