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
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.AbstractMavenReportRenderer;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.FileUtils;

import com.github.jrh3k5.plugin.maven.l10n.data.AuthoritativeMessagesProperties;
import com.github.jrh3k5.plugin.maven.l10n.data.MissingTranslationKey;
import com.github.jrh3k5.plugin.maven.l10n.data.MissingTranslationKeyClass;
import com.github.jrh3k5.plugin.maven.l10n.data.TranslatedMessagesProperties;
import com.github.jrh3k5.plugin.maven.l10n.util.TranslationKeyAnalysisUtils;
import com.github.jrh3k5.plugin.maven.l10n.util.TranslationKeyAnalysisUtils.ClassinessAnalysisResults;

/**
 * A goal used to verify that translation keys in a configured messages properties file are all valid. This mojo assumes that you follow the practice of using class and fields for your keys; for
 * example, if you had the following:
 * 
 * <pre>
 * package my.example;
 * 
 * static enum TranslationKeys {
 *     ERROR;
 * }
 * </pre>
 * 
 * ...your properties file would contain:
 * 
 * <pre>
 * my.example.TranslationKeys.ERROR = Oopsies!
 * </pre>
 * 
 * @author Joshua Hyde
 */

@Mojo(name = "translation-key-verifification", requiresDependencyResolution = ResolutionScope.RUNTIME)
public class TranslationKeyVerifier extends AbstractMavenReport {
    private static final String OUTPUT_NAME = "translation-key-verification";

    /**
     * The location of the file that is to be read and verified. This is considered the "authoritative" messages file, of which all other messages are to be considered translations.
     */
    @Parameter(required = true, defaultValue = "${project.basedir}/src/main/resources/messages.properties")
    private File messagesFile;

    /**
     * The pattern to be used to locate translations of the defined authoritative messages properties (applied to the project's base directory).
     */
    @Parameter(required = true, defaultValue = "src/main/resources/messages*.properties")
    private String translatedMessagesPattern;

    /**
     * The plugin descriptor.
     */
    @Parameter(required = true, readonly = true, defaultValue = "${descriptor}")
    private PluginDescriptor pluginDescriptor;

    @Override
    public String getOutputName() {
        return OUTPUT_NAME;
    }

    @Override
    public String getName(Locale locale) {
        return "Translation Key Verification";
    }

    @Override
    public String getDescription(Locale locale) {
        return "A report that alerts to missing or invalid translation keys";
    }

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        AuthoritativeMessagesProperties authoritativeProperties;
        Collection<TranslatedMessagesProperties> translatedProperties;
        try {
            authoritativeProperties = new AuthoritativeMessagesProperties.Parser().parse(messagesFile);
        } catch (IOException e) {
            throw new MavenReportException(String.format("Failed to parse authoritative messages file: %s", messagesFile), e);
        }

        try {
            final List<File> translationFiles = FileUtils.getFiles(getProject().getBasedir(), translatedMessagesPattern, null);
            // Don't consider the authoritative resource, if found, to be a "translation"
            translationFiles.remove(messagesFile);
            translatedProperties = new TranslatedMessagesProperties.Parser().parse(authoritativeProperties, translationFiles);
        } catch (IOException e) {
            throw new MavenReportException(String.format("Failed to parse translated messages files for pattern: %s", translatedMessagesPattern), e);
        }

        ClassinessAnalysisResults analysisResults;
        try {
            analysisResults = TranslationKeyAnalysisUtils.getInstance(getLog()).analyzeClassiness(getProject(), authoritativeProperties);
        } catch (IOException e) {
            throw new MavenReportException(String.format("Failed to verify %s", messagesFile), e);
        }

        new ReportRenderer(this, locale, getSink(), authoritativeProperties, analysisResults, translatedProperties).render();
    }

    /**
     * A class used for rendering a report containing the issues with translation keys and classes.
     * 
     * @author Joshua Hyde
     */
    static class ReportRenderer extends AbstractMavenReportRenderer {
        private final TranslationKeyVerifier mojo;
        private final Locale locale;
        private final AuthoritativeMessagesProperties authoritativeProperties;
        private final SortedSet<MissingTranslationKey> missingTranslationKeys;
        private final SortedSet<MissingTranslationKeyClass> missingTranslationKeyClasses;
        private final SortedSet<TranslatedMessagesProperties> translatedProperties;

        /**
         * Create a renderer.
         * 
         * @param mojo
         *            The mojo using the renderer.
         * @param locale
         *            The {@link Locale} to be used for localization of the rendered report.
         * @param sink
         *            The {@link Sink} to be used for generation of the report.
         * @param authoritativeProperties
         *            The {@link AuthoritativeMessagesProperties} to drive the primary statistics of the report.
         * @param analysisResults
         *            A {@link com.github.jrh3k5.plugin.maven.l10n.util.TranslationKeyAnalysisUtils.ClassinessAnalysisResults AuthoritativeMessagesProperties} object representing analysis results for
         *            the authoritative message properties file.
         * @param translatedProperties
         *            A {@link Collection} of {@link TranslatedMessagesProperties} objects representing the analysis of translations of the authoritative messages properties file.
         */
        ReportRenderer(TranslationKeyVerifier mojo, Locale locale, Sink sink, AuthoritativeMessagesProperties authoritativeProperties, ClassinessAnalysisResults analysisResults,
                Collection<TranslatedMessagesProperties> translatedProperties) {
            super(sink);
            this.mojo = mojo;
            this.locale = locale;
            this.authoritativeProperties = authoritativeProperties;
            this.missingTranslationKeyClasses = new TreeSet<>(analysisResults.getMissingTranslationKeyClasses());
            this.missingTranslationKeys = new TreeSet<>(analysisResults.getMissingTranslationKeys());
            this.translatedProperties = new TreeSet<>(translatedProperties);
        }

        @Override
        public String getTitle() {
            return mojo.getName(locale);
        }

        @Override
        protected void renderBody() {
            // Title of report
            sink.sectionTitle1();
            sink.text(mojo.getName(locale));
            sink.sectionTitle1_();

            sink.paragraph();
            sink.text("This report describes translation keys listed in your messages properties file that are in an invalid state.");
            sink.paragraph_();

            sink.sectionTitle2();
            sink.text("Duplicate Translation Keys");
            sink.sectionTitle2_();
            
            sink.paragraph();
            if(authoritativeProperties.getDuplicateTranslationKeys().isEmpty()) {
                sink.text("No duplicate translation keys were found.");
            } else {
                sink.text("The following duplicate translation keys were found in your messages properties file.");

                sink.table();
                super.tableHeader(new String[] { "Translation Key" });
                for (String duplicate : authoritativeProperties.getDuplicateTranslationKeys()) {
                    super.tableRow(new String[] { duplicate });
                }
                sink.table_();
            }
            sink.paragraph_();

            // Render missing translation classes
            sink.sectionTitle2();
            sink.text("Missing Translation Classes");
            sink.sectionTitle2_();

            sink.paragraph();

            if (missingTranslationKeyClasses.isEmpty()) {
                sink.text("No missing translation key classes were found.");
            } else {
                sink.text("The following is a list of classes that are listed in your messages properties file, but are not found to actually exist.");

                sink.table();
                super.tableHeader(new String[] { "Class Name" });
                for (MissingTranslationKeyClass keyClass : missingTranslationKeyClasses) {
                    super.tableRow(new String[] { keyClass.getClassName() });
                }
                sink.table_();
            }
            sink.paragraph_();

            // Render out list of missing translation keys
            sink.sectionTitle2();
            sink.text("Missing Translation Keys");
            sink.sectionTitle2_();

            sink.paragraph();

            if (missingTranslationKeys.isEmpty()) {
                sink.text("No missing translation keys were found.");
            } else {
                sink.text("The following is a list of translation keys that are found in the messages properties file, but were not found to actually exist.");

                sink.table();
                super.tableHeader(new String[] { "Class Name", "Key Name" });
                for (MissingTranslationKey key : missingTranslationKeys) {
                    super.tableRow(new String[] { key.getClassName(), key.getKeyName() });
                }
                sink.table_();
            }
            sink.paragraph_();

            // Render the statistics for the authoritative messages properties

            sink.sectionTitle2();
            sink.text("Authoritative Messages Statistics");
            sink.sectionTitle2_();

            sink.paragraph();
            sink.text("The following describes some statistics and information about the configured 'authoritative' messages properties file.");
            sink.paragraph();

            sink.table();
            super.tableRow(new String[] { "Filename", authoritativeProperties.getFile().getName() });
            if(authoritativeProperties.getSupportedLocale() != null) {
                super.tableRow(new String[] { "Supported Language", authoritativeProperties.getSupportedLocale().getDisplayLanguage() });
                if (StringUtils.isNotEmpty(authoritativeProperties.getSupportedLocale().getCountry())) {
                    super.tableRow(new String[] { "Supported Country", authoritativeProperties.getSupportedLocale().getDisplayCountry() });
                }
            }
            super.tableRow(new String[] { "Translation Key Count", Integer.toString(authoritativeProperties.getTranslationKeys().size()) });
            sink.table_();

            sink.sectionTitle2();
            sink.text("Translated Messages Statistics");
            sink.sectionTitle2_();

            if (translatedProperties.isEmpty()) {
                sink.paragraph();
                sink.text("No translations of the configured authoritative messages file were found.");
                sink.paragraph_();
            } else {
                sink.paragraph();
                sink.text("The following describes statistics and information about each of the discovered translations of the authoritative messages properties.");
                sink.paragraph_();
                sink.paragraph();
                sink.text("'Extra translation keys' are keys that are discovered in the translated messages properties, but do not exist in the authoritative message properties.");
                sink.paragraph_();
                sink.paragraph();
                sink.text("'Missing translation keys' are keys that are found in the authoritative messages properties, but are not found in the translation.");
                sink.paragraph_();
            }

            for (TranslatedMessagesProperties translatedProperty : translatedProperties) {
                sink.sectionTitle3();
                sink.text(translatedProperty.getFile().getName());
                sink.sectionTitle3_();

                sink.table();
                if (translatedProperty.getSupportedLocale() != null) {
                    super.tableRow(new String[] { "Supported Language", translatedProperty.getSupportedLocale().getDisplayLanguage() });
                    if (StringUtils.isNotEmpty(translatedProperty.getSupportedLocale().getCountry())) {
                        super.tableRow(new String[] { "Supported Country", translatedProperty.getSupportedLocale().getDisplayCountry() });
                    }
                }
                super.tableRow(new String[] { "Translation Key Count", Integer.toString(translatedProperty.getTranslationKeys().size()) });
                super.tableRow(new String[] { "Missing Translation Keys", Integer.toString(translatedProperty.getMissingTranslationKeys().size()) });
                super.tableRow(new String[] { "Extra Translation Keys", Integer.toString(translatedProperty.getExtraTranslationKeys().size()) });

                final int comparativeCount = translatedProperty.getTranslationKeys().size() - translatedProperty.getExtraTranslationKeys().size();
                final double percentComplete = (((double) comparativeCount) / authoritativeProperties.getTranslationKeys().size()) * 100;
                super.tableRow(new String[] { "Translation Completion Percentage", String.format("%.2f", percentComplete) + "%" });
                sink.table_();

                sink.sectionTitle4();
                sink.text("Duplicate Translation Keys");
                sink.sectionTitle4_();

                sink.paragraph();
                if (translatedProperty.getDuplicateTranslationKeys().isEmpty()) {
                    sink.text("No duplicate translation keys were found.");
                } else {
                    sink.text("The following duplicate translation keys were found in this messages properties file.");

                    sink.table();
                    super.tableHeader(new String[] { "Translation Key" });
                    for (String duplicate : translatedProperty.getDuplicateTranslationKeys()) {
                        super.tableRow(new String[] { duplicate });
                    }
                    sink.table_();
                }
                sink.paragraph_();
            }
        }
    }
}
