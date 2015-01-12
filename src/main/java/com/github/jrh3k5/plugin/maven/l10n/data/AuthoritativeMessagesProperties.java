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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * A description of the authoritative message properties file of which all other files are to be considered translations.
 * 
 * @author Joshua Hyde
 */

public class AuthoritativeMessagesProperties extends AbstractMessagesProperties {
    private final Collection<TranslationClass> translationClasses;

    /**
     * Parse the translation keys into their respective class representations.
     * 
     * @param translationKeys
     *            The keys to be parsed.
     * @return A {@link Collection} of {@link TranslationClass} objects representing the classes represented by the given translation keys.
     */
    private static Collection<TranslationClass> parseTranslationClasses(Collection<String> translationKeys) {
        final Map<String, Set<String>> classStaging = new HashMap<>();

        for (String translationKey : translationKeys) {
            final int lastPeriodPos = translationKey.lastIndexOf('.');
            if (lastPeriodPos < 0) {
                continue;
            }

            final String className = sanitizeClassName(translationKey.substring(0, lastPeriodPos));
            final String keyName = translationKey.substring(lastPeriodPos + 1);
            if (classStaging.containsKey(className)) {
                classStaging.get(className).add(keyName);
            } else {
                final Set<String> keys = new HashSet<>();
                keys.add(keyName);
                classStaging.put(className, keys);
            }
        }

        final Collection<TranslationClass> translationClasses = new ArrayList<>();
        for (Entry<String, Set<String>> stagedClass : classStaging.entrySet()) {
            translationClasses.add(new TranslationClass(stagedClass.getKey(), stagedClass.getValue()));
        }

        return translationClasses;
    }

    /**
     * Turn a dot-delimited class name into a class name that can be resolved using a classloader.
     * 
     * @param className
     *            The name of the class to be sanitized.
     * @return The given class name, transformed in a way that can be resolved using a classloader.
     */
    private static String sanitizeClassName(String className) {
        final String[] parts = className.split("\\.");
        if (parts.length < 2) {
            return className;
        }

        final List<String> packageComponents = new ArrayList<>();
        String rootClassName = null;
        final List<String> nestedClassNames = new ArrayList<>();
        for (String part : parts) {
            if (part.equals(part.toLowerCase(Locale.US))) {
                packageComponents.add(part);
            } else if (rootClassName == null) {
                rootClassName = part;
            } else {
                nestedClassNames.add(part);
            }
        }

        final String packageName = StringUtils.join(packageComponents, ".");
        return nestedClassNames.isEmpty() ? String.format("%s.%s", packageName, rootClassName) : String.format("%s.%s$%s", packageName, rootClassName, StringUtils.join(nestedClassNames, "$"));
    }

    /**
     * Create an authoritative messages properties object.
     * 
     * @param file
     *            The {@link File} represented by this object.
     * @param supportedLocale
     *            The {@link Locale} supported by these properties; can be {@code null}.
     * @param translationKeys
     *            The translation keys contained in this properties file.
     */
    private AuthoritativeMessagesProperties(File file, Locale supportedLocale, Set<String> translationKeys) {
        super(file, supportedLocale, translationKeys);
        this.translationClasses = Collections.unmodifiableCollection(parseTranslationClasses(translationKeys));
    }

    /**
     * Get the translation class information for this properties object.
     * 
     * @return A {@link Collection} of {@link TranslationClass} objects representing the classes represented by this messages properties file's translation keys.
     */
    public Collection<TranslationClass> getTranslationClasses() {
        return translationClasses;
    }

    /**
     * A parser to create {@link AuthoritativeMessagesProperties} from external sources.
     * 
     * @author Joshua Hyde
     */
    public static class Parser extends AbstractMessagesPropertiesParser {
        /**
         * Parse a file into an authoritative messages properties.
         * 
         * @param file
         *            The {@link File} to be parsed.
         * @return An {@link AuthoritativeMessagesProperties} parsed out of the given file.
         * @throws IOException
         *             If any errors occur during the test run.
         */
        public AuthoritativeMessagesProperties parse(File file) throws IOException {
            return new AuthoritativeMessagesProperties(file, determineSupportedLocale(file), getTranslationKeys(file));
        }
    }
}
