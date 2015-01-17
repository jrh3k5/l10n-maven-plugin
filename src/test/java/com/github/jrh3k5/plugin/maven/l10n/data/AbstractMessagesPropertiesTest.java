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
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import com.github.jrh3k5.plugin.maven.l10n.data.AbstractMessagesProperties;

/**
 * Unit tests for {@link AbstractMessagesProperties}.
 * 
 * @author Joshua Hyde
 */

public class AbstractMessagesPropertiesTest {
    private final Set<String> translationKeys = Collections.singleton(UUID.randomUUID().toString());
    private final Set<String> duplicateTranslationKeys = Collections.singleton(UUID.randomUUID().toString());
    private final Locale supportedLocale = Locale.US;
    private final File file = new File(String.format("/tmp/%s.tmp", UUID.randomUUID()));
    private final AbstractMessagesProperties properties = new ConcreteProperties(file, supportedLocale, translationKeys, duplicateTranslationKeys);

    /**
     * Test the retrieval of duplicate translation keys.
     * 
     * @since 1.1
     */
    @Test
    public void testGetDuplicateTranslationKeys() {
        assertThat(properties.getDuplicateTranslationKeys()).isEqualTo(duplicateTranslationKeys);
    }

    /**
     * Test the retrieval of the file.
     */
    @Test
    public void testGetFile() {
        assertThat(properties.getFile()).isEqualTo(file);
    }

    /**
     * Test the retrieval of the supported locale.
     */
    @Test
    public void testGetSupportedLocale() {
        assertThat(properties.getSupportedLocale()).isEqualTo(supportedLocale);
    }

    /**
     * Test the retrieval of the translation keys.
     */
    @Test
    public void testGetTranslationKeys() {
        assertThat(properties.getTranslationKeys()).isEqualTo(translationKeys);
    }

    /**
     * A concrete implementation of {@link AbstractMessagesProperties} for testing purposes.
     * 
     * @author Joshua Hyde
     */
    private static class ConcreteProperties extends AbstractMessagesProperties {
        /**
         * Create a properties object.
         * 
         * @param file
         *            A {@link File}.
         * @param supportedLocale
         *            A {@link Locale}.
         * @param translationKeys
         *            A {@link Set}.
         * @param duplicateTranslationKeys
         *            A {@link Set}.
         */
        private ConcreteProperties(File file, Locale supportedLocale, Set<String> translationKeys, Set<String> duplicateTranslationKeys) {
            super(file, supportedLocale, translationKeys, duplicateTranslationKeys);
        }
    }
}
