package com.github.jrh3k5.plugin.maven.l10n.util;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

/**
 * Unit tests for {@link TranslationClassUtils}.
 * 
 * @author Joshua Hyde
 * @since 1.3
 */

public class TranslationClassUtilsTest {
    /**
     * Test the retrieval of the translation keys.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetTranslationKeys() throws Exception {
        final Collection<String> translationKeys = TranslationClassUtils.getTranslationKeys(Collections.singleton("com.github.jrh3k5.plugin.maven.l10n.util.TranslationClassUtilsTest$TestInterface"),
                Thread.currentThread().getContextClassLoader());
        assertThat(translationKeys).hasSize(2).contains(String.format("%s.A", TestReflection.class.getCanonicalName()), String.format("%s.B", TestReflection.class.getCanonicalName()));
    }

    /**
     * A simple interface for testing purposes.
     * 
     * @author Joshua Hyde
     * @since 1.3
     */
    public static interface TestInterface {
        /**
         * Get the name.
         * 
         * @return The name.
         */
        String name();
    }

    /**
     * Test enumerations implementing {@link TestInterface}.
     * 
     * @author Joshua Hyde
     * @since 1.3
     */
    public static enum TestReflection implements TestInterface {
        A, B;
    }
}
