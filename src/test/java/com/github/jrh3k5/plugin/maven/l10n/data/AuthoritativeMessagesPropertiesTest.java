package com.github.jrh3k5.plugin.maven.l10n.data;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * Unit tests for {@link AuthoritativeMessagesProperties}.
 * 
 * @author Joshua Hyde
 */

public class AuthoritativeMessagesPropertiesTest {
    /**
     * Test the parsing of an authoriative message source from a file.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseFromFile() throws Exception {
        final File messagesFile = new File(String.format("target/test-resources/%s/testParseFromFile/messages.properties", getClass().getCanonicalName()));
        FileUtils.forceMkdir(messagesFile.getParentFile());

        final String nestedKey = "com.github.jrh3k5.Test.Nested.ERROR";
        final String nonClassKey = "notAClass";
        final String nonNestedKey = "com.github.jrh3k5.Test.INFO";

        final Properties properties = new Properties();
        properties.put(nestedKey, UUID.randomUUID().toString());
        properties.put(nonClassKey, UUID.randomUUID().toString());
        properties.put(nonNestedKey, UUID.randomUUID().toString());
        try (final OutputStream propsOut = new FileOutputStream(messagesFile)) {
            properties.store(propsOut, null);
        }

        AuthoritativeMessagesProperties messages = null;
        try (final InputStream propsIn = new FileInputStream(messagesFile)) {
            messages = new AuthoritativeMessagesProperties.Parser().parse(messagesFile);
        }

        assertThat(messages.getTranslationKeys()).hasSize(3).contains(nestedKey, nonClassKey, nonNestedKey);
        assertThat(messages.getTranslationClasses()).hasSize(2);
        final Map<String, TranslationClass> translationClasses = new HashMap<>(2);
        for (TranslationClass translationClass : messages.getTranslationClasses()) {
            translationClasses.put(translationClass.getTranslationClassName(), translationClass);
        }

        // Verify that the valid class names were populated
        assertThat(translationClasses.get("com.github.jrh3k5.Test$Nested")).isNotNull();
        assertThat(translationClasses.get("com.github.jrh3k5.Test$Nested").getKeyNames()).hasSize(1).contains("ERROR");
        assertThat(translationClasses.get("com.github.jrh3k5.Test")).isNotNull();
        assertThat(translationClasses.get("com.github.jrh3k5.Test").getKeyNames()).hasSize(1).contains("INFO");
    }

}
