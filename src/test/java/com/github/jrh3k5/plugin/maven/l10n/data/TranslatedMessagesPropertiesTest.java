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
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import org.junit.Test;

import com.github.jrh3k5.plugin.maven.l10n.data.internal.AbstractUnitTest;

/**
 * Unit tests for {@link TranslatedMessagesProperties}.
 * 
 * @author Joshua Hyde
 */

public class TranslatedMessagesPropertiesTest extends AbstractUnitTest {

    /**
     * Test the parsing of an message source from a file.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseFromFile() throws Exception {
        final File authoritativeMessagesFile = getTestFile("messages.properties");
        final File spanishPropertiesFile = new File(authoritativeMessagesFile.getParent(), "messages_es.properties");

        final String sharedKey = UUID.randomUUID().toString();
        final String extraKey = UUID.randomUUID().toString();
        final String missingKey = UUID.randomUUID().toString();

        final Properties authoritativeProperties = new Properties();
        authoritativeProperties.put(sharedKey, UUID.randomUUID().toString());
        authoritativeProperties.put(missingKey, UUID.randomUUID().toString());
        try (final OutputStream authoritativeOut = new FileOutputStream(authoritativeMessagesFile)) {
            authoritativeProperties.store(authoritativeOut, null);
        }

        final Properties spanishProperties = new Properties();
        spanishProperties.put(sharedKey, UUID.randomUUID().toString());
        spanishProperties.put(extraKey, UUID.randomUUID().toString());
        try (final OutputStream spanishOut = new FileOutputStream(spanishPropertiesFile)) {
            spanishProperties.store(spanishOut, null);
        }

        final AuthoritativeMessagesProperties authoritativeMessages = new AuthoritativeMessagesProperties.Parser().parse(authoritativeMessagesFile);
        final Collection<TranslatedMessagesProperties> translatedMessages = new TranslatedMessagesProperties.Parser().parse(authoritativeMessages, Collections.singleton(spanishPropertiesFile));
        assertThat(translatedMessages).hasSize(1);
        final TranslatedMessagesProperties translatedMessage = translatedMessages.iterator().next();
        assertThat(translatedMessage.getExtraTranslationKeys()).hasSize(1).contains(extraKey);
        assertThat(translatedMessage.getMissingTranslationKeys()).hasSize(1).contains(missingKey);
        assertThat(translatedMessage.getTranslationKeys()).hasSize(2).contains(sharedKey, extraKey);
    }
}
