package com.github.jrh3k5.plugin.maven.l10n.data;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

/**
 * Unit tests for {@link AbstractMessagesProperties}.
 * 
 * @author Joshua Hyde
 */

public class AbstractMessagesPropertiesTest {
    private final Set<String> translationKeys = Collections.singleton(UUID.randomUUID().toString());
    private final Locale supportedLocale = Locale.US;
    private final File file = new File(String.format("/tmp/%s.tmp", UUID.randomUUID()));
    private final AbstractMessagesProperties properties = new ConcreteProperties(file, supportedLocale, translationKeys);

    /**
     * Test the retrieval of the file.
     */
    @Test
    public void testGetFile() {
        assertThat(properties.getFile()).isEqualTo(file);
    }

    /**
     * Test the retrieval of the supported locale.
     */
    @Test
    public void testGetSupportedLocale() {
        assertThat(properties.getSupportedLocale()).isEqualTo(supportedLocale);
    }

    /**
     * Test the retrieval of the translation keys.
     */
    @Test
    public void testGetTranslationKeys() {
        assertThat(properties.getTranslationKeys()).isEqualTo(translationKeys);
    }

    /**
     * A concrete implementation of {@link AbstractMessagesProperties} for testing purposes.
     * 
     * @author Joshua Hyde
     */
    private static class ConcreteProperties extends AbstractMessagesProperties {
        /**
         * Create a properties object.
         * 
         * @param file
         *            A {@link File}.
         * @param supportedLocale
         *            A {@link Locale}.
         * @param translationKeys
         *            A {@link Set}.
         */
        private ConcreteProperties(File file, Locale supportedLocale, Set<String> translationKeys) {
            super(file, supportedLocale, translationKeys);
        }
    }
}
