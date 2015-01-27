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
package com.github.jrh3k5.plugin.maven.l10n.mojo.report;

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.github.jrh3k5.plugin.maven.l10n.data.AuthoritativeMessagesProperties;
import com.github.jrh3k5.plugin.maven.l10n.util.ClassLoaderUtils;
import com.github.jrh3k5.plugin.maven.l10n.util.TranslationKeyAnalysisUtils;
import com.github.jrh3k5.plugin.maven.l10n.util.TranslationKeyAnalysisUtils.ClassinessAnalysisResults;

/**
 * A mojo used to verify that a messages properties file meets the following criteria:
 * <ul>
 * <li>It has no duplicate translation keys.</li>
 * <li>Its translation keys are all well-formed, in that they:
 * <ul>
 * <li>Reference existent classes</li>
 * <li>Reference existent fields in those classes</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Joshua Hyde
 * @since 1.2
 */

@Mojo(name = "verify-messages", defaultPhase = LifecyclePhase.VERIFY, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class VerifyMessagesMojo extends AbstractMojo {
    /**
     * The location of the file that is to be read and verified.
     */
    @Parameter(required = true, defaultValue = "${project.basedir}/src/main/resources/messages.properties")
    private File messagesFile;

    /**
     * Configure whether or not the build should fail if there are any verification issues. Defaults to {@code false}, which means the build will <b>not</b> fail if there are any issues with the
     * configured messages properties file.
     */
    @Parameter(required = true, defaultValue = "false")
    private boolean failBuild;

    /**
     * A {@link MavenProject} representing the current project.
     */
    @Parameter(required = true, defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        AuthoritativeMessagesProperties properties;
        try {
            properties = new AuthoritativeMessagesProperties.Parser().parse(messagesFile);
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Failed to parse messages file: %s", messagesFile), e);
        }

        ClassinessAnalysisResults analysisResults;
        try {
            analysisResults = TranslationKeyAnalysisUtils.getInstance(getLog()).analyzeClassiness(ClassLoaderUtils.getClassLoader(project), properties);
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Failed to analyze translation keys for file: %s", messagesFile), e);
        }

        final AbstractIssueEmitter emitter = failBuild ? new LogErrorIssueEmitter(getLog()) : new LogWarnIssueEmitter(getLog());

        boolean shouldFail = false;
        if (!properties.getDuplicateTranslationKeys().isEmpty()) {
            shouldFail = true;
            emitter.emit(String.format("File %s contains %d duplicate keys.", messagesFile.getName(), properties.getDuplicateTranslationKeys().size()));
        }

        if (!analysisResults.getMissingTranslationKeyClasses().isEmpty()) {
            shouldFail = true;
            emitter.emit(String.format("File %s contains %d references to non-existent translation key classes.", messagesFile.getName(), analysisResults.getMissingTranslationKeyClasses().size()));
        }

        if (!analysisResults.getMissingTranslationKeys().isEmpty()) {
            shouldFail = true;
            emitter.emit(String.format("File %s contains %d references to non-existent translation class keys.", messagesFile.getName(), analysisResults.getMissingTranslationKeys().size()));
        }

        if (failBuild && shouldFail) {
            throw new MojoFailureException(String.format("The file %s has one or more verification errors. Refer to messages above for more information.", messagesFile.getName()));
        }
    }

    /**
     * Definition of an "emitter" used to express an issue with a messages file.
     * 
     * @author Joshua Hyde
     * @since 1.2
     */
    private static abstract class AbstractIssueEmitter {
        /**
         * Emit a message.
         * 
         * @param message
         *            The message to be emitted.
         */
        public abstract void emit(String message);
    }

    /**
     * An {@link AbstractIssueEmitter} that emits messages a log ERROR level.
     * 
     * @author Joshua Hyde
     * @since 1.2
     */
    private static class LogErrorIssueEmitter extends AbstractIssueEmitter {
        private final Log log;

        /**
         * Create an emitter.
         * 
         * @param log
         *            The {@link Log} to back this emitter.
         */
        private LogErrorIssueEmitter(Log log) {
            this.log = log;
        }

        @Override
        public void emit(String message) {
            log.error(message);
        }
    }

    /**
     * An {@link AbstractIssueEmitter} that emits messages at a WARN log level.
     * 
     * @author Joshua Hyde
     * @since 1.2
     */
    private static class LogWarnIssueEmitter extends AbstractIssueEmitter {
        private final Log log;

        /**
         * Create an emitter.
         * 
         * @param log
         *            The {@link Log} to back this emitter.
         */
        private LogWarnIssueEmitter(Log log) {
            this.log = log;
        }

        @Override
        public void emit(String message) {
            log.warn(message);
        }

    }
}
