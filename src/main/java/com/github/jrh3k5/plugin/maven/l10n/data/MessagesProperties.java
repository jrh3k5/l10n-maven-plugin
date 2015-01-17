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
import java.util.Locale;
import java.util.Set;

/**
 * Definition of an object that represents a messages properties file containing localized copies of internationalized strings.
 * 
 * @author Joshua Hyde
 */

public interface MessagesProperties {
    /**
     * Get any translation keys that appear more than once in the file.
     * 
     * @return A {@link Set} of any translation keys that appear more than once in the properties file.
     * @since 1.1
     */
    Set<String> getDuplicateTranslationKeys();

    /**
     * Get the file represented by this properties object.
     * 
     * @return A {@link File} object representing the file that is represented by this properties object.
     */
    File getFile();

    /**
     * Get the locale supported by this messages properties file.
     * 
     * @return {@code null} if no locale support is indicated by this messages properties file; otherwise, a {@link Locale} representing the locale supported by this messages properties file.
     */
    Locale getSupportedLocale();

    /**
     * Get the translation keys contained in the messages properties.
     * 
     * @return A {@link Set} containing the translation keys in the properties file.
     */
    Set<String> getTranslationKeys();
}
