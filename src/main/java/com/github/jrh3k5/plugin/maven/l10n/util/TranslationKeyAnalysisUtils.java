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
package com.github.jrh3k5.plugin.maven.l10n.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import com.github.jrh3k5.plugin.maven.l10n.data.AuthoritativeMessagesProperties;
import com.github.jrh3k5.plugin.maven.l10n.data.MissingTranslationKey;
import com.github.jrh3k5.plugin.maven.l10n.data.MissingTranslationKeyClass;
import com.github.jrh3k5.plugin.maven.l10n.data.TranslationClass;

/**
 * Utilities for verifying translation keys.
 * 
 * @author Joshua Hyde
 * @since 1.2
 */

public class TranslationKeyAnalysisUtils {
    private final Log log;

    /**
     * Get an instance of this utility class.
     * @param log The {@link Log} to be used by this class to write out any information.
     * @return A {@link TranslationKeyAnalysisUtils} object that can be used to verify translation keys.
     */
    public static TranslationKeyAnalysisUtils getInstance(Log log) {
        return new TranslationKeyAnalysisUtils(log);
    }

    /**
     * A private constructor to prevent direct instantiation.
     * 
     * @param log
     *            The {@link Log} to be used by this class to print out information.
     */
    private TranslationKeyAnalysisUtils(Log log) {
        this.log = log;
    }

    /**
     * Analyze the "classiness" of an authoritative messages properties file - that is, how many classes it references are invalid or class fields are invalid.
     * 
     * @param project
     *            A {@link MavenProject} object representing the project whose classes are to be used to evaluate the "classiness" of the given messages properties file.
     * @param messagesProperties
     *            The {@link AuthoritativeMessagesProperties} whose translation keys are to be analysed.
     * @return A {@link ClassinessAnalysisResults} object representing the results of the analysis.
     * @throws IOException
     *             If any errors occur during the analysis.
     */
    public ClassinessAnalysisResults analyzeClassiness(MavenProject project, AuthoritativeMessagesProperties messagesProperties) throws IOException {
        final ClassLoader classLoader = getClassLoader(project);
        final List<MissingTranslationKey> missingTranslationKeys = new ArrayList<>();
        final List<MissingTranslationKeyClass> missingTranslationKeyClasses = new ArrayList<>();

        // First, build the report for missing translation keys and classes
        for (TranslationClass translationClass : messagesProperties.getTranslationClasses()) {
            final String className = translationClass.getTranslationClassName();

            Class<?> translationKeyClass = null;
            try {
                translationKeyClass = classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                log.debug(String.format("The translation key class %s was not found.", className), e);
                missingTranslationKeyClasses.add(new MissingTranslationKeyClass(className));
                continue;
            }

            for (String keyName : translationClass.getKeyNames()) {
                try {
                    translationKeyClass.getDeclaredField(keyName);
                } catch (NoSuchFieldException | SecurityException e) {
                    log.debug(String.format("The translation key %s for class %s was not found.", keyName, className), e);
                    missingTranslationKeys.add(new MissingTranslationKey(className, keyName));
                    continue;
                }
            }
        }

        return new ClassinessAnalysisResults(missingTranslationKeyClasses, missingTranslationKeys);
    }

    /**
     * Get a classloader that can be used to load the classes of the translation keys.
     * 
     * @param project
     *            A {@link MavenProject} representing the project whose translation keys are to be verified.
     * @return A {@link ClassLoader} that can be used to load the classes of the translation keys.
     * @throws IOException
     *             If any errors occur in building the classloader.
     */
    private ClassLoader getClassLoader(MavenProject project) throws IOException {
        final List<URL> classpathUrls = new ArrayList<>();
        try {
            for (Object dependency : project.getRuntimeClasspathElements()) {
                classpathUrls.add(new File(dependency.toString()).toURI().toURL());
            }
        } catch (MalformedURLException | DependencyResolutionRequiredException e) {
            throw new IOException("Failed to build project classloader.", e);
        }
        return new URLClassLoader(classpathUrls.toArray(new URL[classpathUrls.size()]));
    }

    /**
     * An object representing the results of a {@link TranslationKeyAnalysisUtils#analyzeClassiness(MavenProject, AuthoritativeMessagesProperties) classiness analysis}.
     * 
     * @author Joshua Hyde
     * @since 1.2
     */
    public static class ClassinessAnalysisResults {
        private final List<MissingTranslationKeyClass> missingTranslationKeyClasses;
        private final List<MissingTranslationKey> missingTranslationKeys;

        /**
         * Create a results object.
         * 
         * @param missingTranslationKeyClasses
         *            A {@link List} of {@link MissingTranslationKeyClass} objects representing the missing translation key classes referenced in the properties file.
         * @param missingTranslationKeys
         *            A {@link List} of {@link MissingTranslationKey} objects representing the missing translation keys referenced in the properties file.
         */
        private ClassinessAnalysisResults(List<MissingTranslationKeyClass> missingTranslationKeyClasses, List<MissingTranslationKey> missingTranslationKeys) {
            this.missingTranslationKeyClasses = Collections.unmodifiableList(missingTranslationKeyClasses);
            this.missingTranslationKeys = Collections.unmodifiableList(missingTranslationKeys);
        }

        /**
         * Get the missing translation keys.
         * 
         * @return A {@link List} of {@link MissingTranslationKey} objects representing the missing translation keys referenced in the properties file.
         */
        public List<MissingTranslationKey> getMissingTranslationKeys() {
            return missingTranslationKeys;
        }

        /**
         * Get the missing translation key classes.
         * 
         * @return A {@link List} of {@link MissingTranslationKeyClass} objects representing the missing translation key classes referenced in the properties file.
         */
        public List<MissingTranslationKeyClass> getMissingTranslationKeyClasses() {
            return missingTranslationKeyClasses;
        }
    }
}
