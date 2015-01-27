package com.github.jrh3k5.plugin.maven.l10n.util;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.github.jrh3k5.plugin.maven.l10n.util.internal.TestInterface;
import com.github.jrh3k5.plugin.maven.l10n.util.internal.TestReflection;

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
        final Collection<String> translationKeys = TranslationClassUtils.getTranslationKeys(Collections.singleton(TestInterface.class.getCanonicalName()), buildClassLoader());
        assertThat(translationKeys).hasSize(2).contains(String.format("%s.A", TestReflection.class.getCanonicalName()), String.format("%s.B", TestReflection.class.getCanonicalName()));
    }

    /**
     * Build a classloader that includes the test classes.
     * 
     * @return A {@link ClassLoader} that optionally includes the test classes.
     * @throws IOException
     *             If any errors occur while building the class loader.
     */
    private ClassLoader buildClassLoader() throws IOException {
        final Collection<URL> urls = new ArrayList<>();
        final File testSources = new File("target/test-classes");
        if (testSources.exists()) {
            urls.add(FileUtils.toURLs(new File[] { testSources })[0]);
        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), TranslationClassUtils.class.getClassLoader());
    }
}
