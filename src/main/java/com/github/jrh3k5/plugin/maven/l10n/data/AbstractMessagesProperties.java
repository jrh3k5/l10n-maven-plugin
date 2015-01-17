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

import java.io.File;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * An abstract implementation of {@link MessagesProperties} providing common methods.
 * 
 * @author Joshua Hyde
 */

public abstract class AbstractMessagesProperties implements MessagesProperties {
    private final Set<String> translationKeys;
    private final Set<String> duplicateTranslationKeys;
    private final Locale supportedLocale;
    private final File file;

    /**
     * Create a properties file.
     * 
     * @param file
     *            The {@link File} represented by this object.
     * @param supportedLocale
     *            The {@link Locale} supported by this messages properties; {@code null} indicates no supported language.
     * @param translationKeys
     *            A {@link Set} containing the translation keys within the messages properties.
     * @param duplicateTranslationKeys
     *            A {@link Set} containing the duplicate translation keys within the messages properties.
     */
    public AbstractMessagesProperties(File file, Locale supportedLocale, Set<String> translationKeys, Set<String> duplicateTranslationKeys) {
        this.file = file;
        this.supportedLocale = supportedLocale;
        this.translationKeys = Collections.unmodifiableSet(translationKeys);
        this.duplicateTranslationKeys = Collections.unmodifiableSet(duplicateTranslationKeys);
    }

    @Override
    public Set<String> getDuplicateTranslationKeys() {
        return duplicateTranslationKeys;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public Locale getSupportedLocale() {
        return supportedLocale;
    }

    @Override
    public Set<String> getTranslationKeys() {
        return translationKeys;
    }
}
