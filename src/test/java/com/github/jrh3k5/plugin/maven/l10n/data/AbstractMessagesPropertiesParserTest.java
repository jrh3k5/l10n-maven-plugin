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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.github.jrh3k5.plugin.maven.l10n.data.internal.AbstractUnitTest;

/**
 * Unit tests for {@link AbstractMessagesPropertiesParser}.
 * 
 * @author Joshua Hyde
 */

public class AbstractMessagesPropertiesParserTest extends AbstractUnitTest {
    private final AbstractMessagesPropertiesParser parser = new ConcreteParser();

    /**
     * Test the determination of supported locale for a messages properties file with only the language specified.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testDetermineSupportedLocale() throws Exception {
        final File messagesFile = new File("messages_es.properties");
        final Locale supportedLocale = parser.determineSupportedLocale(messagesFile);
        assertThat(supportedLocale.getLanguage()).isEqualTo("es");
        assertThat(supportedLocale.getCountry()).isEmpty();
    }

    /**
     * Test the parsing of a properties file and detection of duplicate translation keys.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetDuplicateTranslationKeys() throws Exception {
        final File testFile = getTestFile("messages.properties");
        FileUtils.writeLines(testFile, Arrays.asList("dup=foo", "not.dup=bar", "dup=fizzbuzz"));
        assertThat(parser.getDuplicateTranslationKeys(testFile)).hasSize(1).contains("dup");
    }

    /**
     * The search for duplicate translation keys should skip comments.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetDuplicateTranslationKeysWithComment() throws Exception {
        final File testFile = getTestFile("messages.properties");
        FileUtils.writeLines(testFile, Arrays.asList("comment.dup=foo", "comment.not.dup=bar", "# This is a comment", "comment.dup=fizzbuzz"));
        assertThat(parser.getDuplicateTranslationKeys(testFile)).hasSize(1).contains("comment.dup");
    }

    /**
     * The parsing of duplicate translation keys should tolerate empty lines in the file.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetDuplicateTranslationKeysWithEmptyLine() throws Exception {
        final File testFile = getTestFile("messages.properties");
        FileUtils.writeLines(testFile, Arrays.asList("emptyline.dup=foo", "emptyline.not.dup=bar", " ", "emptyline.dup=fizzbuzz"));
        assertThat(parser.getDuplicateTranslationKeys(testFile)).hasSize(1).contains("emptyline.dup");
    }

    /**
     * If the locale cannot be resolved for a messages properties file, then {@code null} should be returned for the locale.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testDetermineSupportedLocaleUnresolved() throws Exception {
        assertThat(parser.determineSupportedLocale(new File("messages.properties"))).isNull();
    }

    /**
     * Test the determination of locale support with a specified country.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testDetermineSupportedLocaleWithCountry() throws Exception {
        final File messagesFile = new File("messages_es_MX.properties");
        final Locale supportedLocale = parser.determineSupportedLocale(messagesFile);
        assertThat(supportedLocale.getLanguage()).isEqualTo("es");
        assertThat(supportedLocale.getCountry()).isEqualTo("MX");
    }

    /**
     * Test the loading of translation keys.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetTranslationKeys() throws Exception {
        final File messagesFile = getTestFile("messages.properties");

        final String messageKey = UUID.randomUUID().toString();
        final Properties sourceProperties = new Properties();
        sourceProperties.put(messageKey, UUID.randomUUID().toString());
        try (final OutputStream outputStream = new FileOutputStream(messagesFile)) {
            sourceProperties.store(outputStream, null);
        }
        assertThat(parser.getTranslationKeys(messagesFile)).hasSize(1).contains(messageKey);
    }

    /**
     * A concrete implementation of {@link AbstractMessagesPropertiesParser} for testing purposes.
     * 
     * @author Joshua Hyde
     */
    private static class ConcreteParser extends AbstractMessagesPropertiesParser {

    }
}
