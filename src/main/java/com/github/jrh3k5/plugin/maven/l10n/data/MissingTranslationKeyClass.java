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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A class representing a class that is referenced by a translation key but does not exist.
 * 
 * @author Joshua Hyde
 */
public class MissingTranslationKeyClass implements Comparable<MissingTranslationKeyClass> {
    private final String className;

    /**
     * Create a missing translation class.
     * 
     * @param className
     *            The name of the translation class that was missing.
     */
    public MissingTranslationKeyClass(String className) {
        this.className = className;
    }

    @Override
    public int compareTo(MissingTranslationKeyClass o) {
        return getClassName().compareTo(o.getClassName());
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Get the name of the translation class that was missing.
     * 
     * @return The name of the translation class that was missing.
     */
    public String getClassName() {
        return className;
    }
}
