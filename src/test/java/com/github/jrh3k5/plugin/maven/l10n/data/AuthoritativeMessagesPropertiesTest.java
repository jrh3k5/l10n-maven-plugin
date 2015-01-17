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
package com.github.jrh3k5.plugin.maven.l10n.data;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.junit.Test;

import com.github.jrh3k5.plugin.maven.l10n.data.internal.AbstractUnitTest;

/**
 * Unit tests for {@link AuthoritativeMessagesProperties}.
 * 
 * @author Joshua Hyde
 */

public class AuthoritativeMessagesPropertiesTest extends AbstractUnitTest {
    /**
     * Test the parsing of an authoriative message source from a file.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseFromFile() throws Exception {
        final File messagesFile = getTestFile("messages.properties");

        final String nestedKey = "com.github.jrh3k5.Test.Nested.ERROR";
        final String nonClassKey = "notAClass";
        final String nonNestedKey = "com.github.jrh3k5.Test.INFO";

        final Properties properties = new Properties();
        properties.put(nestedKey, UUID.randomUUID().toString());
        properties.put(nonClassKey, UUID.randomUUID().toString());
        properties.put(nonNestedKey, UUID.randomUUID().toString());
        try (final OutputStream propsOut = new FileOutputStream(messagesFile)) {
            properties.store(propsOut, null);
        }

        AuthoritativeMessagesProperties messages = null;
        try (final InputStream propsIn = new FileInputStream(messagesFile)) {
            messages = new AuthoritativeMessagesProperties.Parser().parse(messagesFile);
        }

        assertThat(messages.getTranslationKeys()).hasSize(3).contains(nestedKey, nonClassKey, nonNestedKey);
        assertThat(messages.getTranslationClasses()).hasSize(2);
        final Map<String, TranslationClass> translationClasses = new HashMap<>(2);
        for (TranslationClass translationClass : messages.getTranslationClasses()) {
            translationClasses.put(translationClass.getTranslationClassName(), translationClass);
        }

        // Verify that the valid class names were populated
        assertThat(translationClasses.get("com.github.jrh3k5.Test$Nested")).isNotNull();
        assertThat(translationClasses.get("com.github.jrh3k5.Test$Nested").getKeyNames()).hasSize(1).contains("ERROR");
        assertThat(translationClasses.get("com.github.jrh3k5.Test")).isNotNull();
        assertThat(translationClasses.get("com.github.jrh3k5.Test").getKeyNames()).hasSize(1).contains("INFO");
    }

}
