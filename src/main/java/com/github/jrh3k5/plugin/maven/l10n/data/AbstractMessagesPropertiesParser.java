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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * Abstract definition of a class used to parse a messages properties file.
 * 
 * @author Joshua Hyde
 */

public abstract class AbstractMessagesPropertiesParser {
    /**
     * Determine the supported locale of a messages properties file.
     * 
     * @param messagesFile
     *            The location of the file whose supported locale is to be determined.
     * @return {@code null} if the locale could not be determined; otherwise, a {@link Locale} representing the supported locale indicated by the given file's name.
     */
    protected Locale determineSupportedLocale(File messagesFile) {
        final String filename = messagesFile.getName();
        final int underscorePos = filename.indexOf('_');
        if (underscorePos < 0) {
            return null;
        }
    
        final int periodPos = filename.indexOf('.');
        final int secondaryUnderscorePos = filename.indexOf('_', underscorePos + 1);
        if (secondaryUnderscorePos < 0) {
            return new Locale(filename.substring(underscorePos + 1, periodPos));
        } else {
            final String language = filename.substring(underscorePos + 1, secondaryUnderscorePos);
            final String country = filename.substring(secondaryUnderscorePos + 1, periodPos);
            return new Locale(language, country);
        }
    }

    /**
     * Parse and search a translation file for duplicate translation keys.
     * 
     * @param messagesFile
     *            A {@link File} representing the file to be searched for duplicate translation keys.
     * @return A {@link Set} of {@link String} objects representing translation keys that appear more than once in the given file.
     * @throws IOException
     *             If any errors occur during the parsing.
     */
    protected Set<String> getDuplicateTranslationKeys(File messagesFile) throws IOException {
        try (final InputStream fileIn = new FileInputStream(messagesFile); final InputStreamReader streamReader = new InputStreamReader(fileIn); final BufferedReader bufferedReader = new BufferedReader(streamReader)) {
            String currentLine = null;
            final Set<String> duplicateKeys = new HashSet<>();
            final Set<String> seenKeys = new HashSet<>();
            while ((currentLine = bufferedReader.readLine()) != null) {
                // Skip blank lines and comments
                if (StringUtils.isBlank(currentLine) || currentLine.trim().startsWith("#")) {
                    continue;
                }

                final int equalsPos = currentLine.indexOf('=');
                // Don't let invalid properties stop parsing
                if (equalsPos < 0) {
                    continue;
                }
                final String key = currentLine.substring(0, equalsPos);
                if (seenKeys.contains(key)) {
                    duplicateKeys.add(key);
                } else {
                    seenKeys.add(key);
                }
            }
            return duplicateKeys;
        }
    }

    /**
     * Load the message keys of the translation file.
     * 
     * @return A {@link Set} of {@link String} objects representing the keys of the messages file.
     * @throws IOException
     *             If any errors occur while reading the messages file.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Set<String> getTranslationKeys(File messagesFile) throws IOException {
        final Properties properties = new Properties();
        try (final InputStream inputStream = new FileInputStream(messagesFile)) {
            properties.load(inputStream);
            return (Set) properties.keySet();
        }
    }
}
