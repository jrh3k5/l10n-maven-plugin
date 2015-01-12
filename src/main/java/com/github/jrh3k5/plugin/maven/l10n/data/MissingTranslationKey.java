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
 * A class representing a missing translation key.
 * 
 * @author Joshua Hyde
 */
public class MissingTranslationKey implements Comparable<MissingTranslationKey> {
    private final String className;
    private final String keyName;

    /**
     * Create a missing translation key.
     * 
     * @param className
     *            The name of the class to which the missing key belongs.
     * @param keyName
     *            The name of the key.
     */
    public MissingTranslationKey(String className, String keyName) {
        this.className = className;
        this.keyName = keyName;
    }

    @Override
    public int compareTo(MissingTranslationKey o) {
        final int classNameDiff = getClassName().compareTo(o.getClassName());
        if (classNameDiff != 0) {
            return classNameDiff;
        }

        return getKeyName().compareTo(o.getKeyName());
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
     * Get the name of the class to which the missing key belongs.
     * 
     * @return The name of the class to which the missing key belongs.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Get the name of the key.
     * 
     * @return The name of the key.
     */
    public String getKeyName() {
        return keyName;
    }
}