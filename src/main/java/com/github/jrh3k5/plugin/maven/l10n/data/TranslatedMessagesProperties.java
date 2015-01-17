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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * A representation of a properties file that was translated from a designated authoritative messages properties file.
 * 
 * @author Joshua Hyde
 * @see AuthoritativeMessagesProperties
 */

public class TranslatedMessagesProperties extends AbstractMessagesProperties implements Comparable<TranslatedMessagesProperties> {
    private final Set<String> missingTranslationKeys;
    private final Set<String> extraTranslationKeys;

    /**
     * Create a translated messages properties object.
     * 
     * @param file
     *            The {@link File} represented by this object.
     * @param supportedLocale
     *            The {@link Locale} supported by this properties translation (can be {@code null}).
     * @param translationKeys
     *            The translation keys contained in this properties file.
     * @param missingTranslationKeys
     *            A {@link Set} of keys that are found in the authoritative source, but not in this properties file.
     * @param extraTranslationKeys
     *            A {@link Set} of keys that are found in this properties file, but not the authoritative source.
     * @param duplicateTranslationKeys
     *            The duplicate translation keys contained in this file.
     */
    private TranslatedMessagesProperties(File file, Locale supportedLocale, Set<String> translationKeys, Set<String> missingTranslationKeys, Set<String> extraTranslationKeys,
            Set<String> duplicateTranslationKeys) {
        super(file, supportedLocale, translationKeys, duplicateTranslationKeys);
        this.missingTranslationKeys = Collections.unmodifiableSet(missingTranslationKeys);
        this.extraTranslationKeys = Collections.unmodifiableSet(extraTranslationKeys);
    }

    @Override
    public int compareTo(TranslatedMessagesProperties o) {
        return getFile().getName().compareTo(o.getFile().getName());
    }

    public Set<String> getMissingTranslationKeys() {
        return missingTranslationKeys;
    }

    public Set<String> getExtraTranslationKeys() {
        return extraTranslationKeys;
    }

    /**
     * A parser used to produce {@link TranslatedMessagesProperties} objects from external sources.
     * 
     * @author Joshua Hyde
     */
    public static class Parser extends AbstractMessagesPropertiesParser {
        /**
         * Parse files into translation representations.
         * 
         * @param authoritativeMessagesProperties
         *            The {@link AuthoritativeMessagesProperties} to be designated as the authority on localization.
         * @param messagesFiles
         *            A {@link Collection} of {@link File} objects representing the translation files.
         * @return A {@link Collection} of {@link TranslatedMessagesProperties} built out of the given data. This is not guaranteed to be parallel to the given collection of files.
         * @throws IOException
         *             If any errors occur during the parsing.
         */
        public Collection<TranslatedMessagesProperties> parse(AuthoritativeMessagesProperties authoritativeMessagesProperties, Collection<File> messagesFiles) throws IOException {
            final Collection<TranslatedMessagesProperties> translated = new ArrayList<>(messagesFiles.size());
            for (File messagesFile : messagesFiles) {
                final Set<String> translationKeys = getTranslationKeys(messagesFile);
                final Set<String> missingTranslationKeys = new HashSet<>();
                // Find all keys in the authoritative message properties that aren't in this one
                for (String authoritativeTranslationKey : authoritativeMessagesProperties.getTranslationKeys()) {
                    if (!translationKeys.contains(authoritativeTranslationKey)) {
                        missingTranslationKeys.add(authoritativeTranslationKey);
                    }
                }

                // Find all keys in this properties file that isn't in the authoritative bundle
                final Set<String> extraTranslationKeys = new HashSet<>();
                for (String translationKey : translationKeys) {
                    if (!authoritativeMessagesProperties.getTranslationKeys().contains(translationKey)) {
                        extraTranslationKeys.add(translationKey);
                    }
                }
                translated.add(new TranslatedMessagesProperties(messagesFile, determineSupportedLocale(messagesFile), translationKeys, missingTranslationKeys, extraTranslationKeys,
                        getDuplicateTranslationKeys(messagesFile)));
            }
            return Collections.unmodifiableCollection(translated);
        }
    }
}
