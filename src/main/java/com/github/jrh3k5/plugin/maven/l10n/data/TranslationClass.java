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

import java.util.Collections;
import java.util.Set;

/**
 * A representation of a class used as a translation key.
 * 
 * @author Joshua Hyde
 */

public class TranslationClass {
    private final String translationClassName;
    private final Set<String> keyNames;

    /**
     * Create a translation class.
     * 
     * @param translationClassName
     *            The name of the class containing the unqualified translation keys. This must be formatted such that it can be used with a {@link ClassLoader} to load the class.
     * @param keyNames
     *            A {@link Set} containing the unqualified translation key names belonging to the given translation class.
     */
    public TranslationClass(String translationClassName, Set<String> keyNames) {
        this.translationClassName = translationClassName;
        this.keyNames = Collections.unmodifiableSet(keyNames);
    }

    /**
     * Get the key names ascribed to the translation class.
     * 
     * @return A {@link Set} containing the unqualified translation key names belonging to the translation class.
     */
    public Set<String> getKeyNames() {
        return keyNames;
    }

    /**
     * Get the name of the translation class.
     * 
     * @return The name of the translation class. This is formatted such that it can be used with a {@link ClassLoader} to load the class.
     */
    public String getTranslationClassName() {
        return translationClassName;
    }
}
