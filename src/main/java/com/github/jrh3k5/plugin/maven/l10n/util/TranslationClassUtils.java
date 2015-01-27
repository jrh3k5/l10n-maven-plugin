package com.github.jrh3k5.plugin.maven.l10n.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

/**
 * Utilities for reading classes used for translation keys.
 * 
 * @author Joshua Hyde
 * @since 1.3
 */

public abstract class TranslationClassUtils {
    /**
     * Get the translation keys for classes that implement, extend, or instances of the given collection of class names.
     * 
     * @param classNames
     *            A {@link Collection} of {@link String} objects representing the names of classes and interfaces that are inherited, implemented, or instantiated as classes containing the translation
     *            keys.
     * @param classLoader
     *            The {@link ClassLoader} to be used to load the classes.
     * @return A {@link Collection} of {@link String} objects representing the read translation keys.
     * @throws ClassNotFoundException
     *             If any of the given class names cannot be loaded as classes.
     */
    public static Collection<String> getTranslationKeys(Collection<String> classNames, ClassLoader classLoader) throws ClassNotFoundException {
        final Set<String> translationKeys = new HashSet<>();
        final Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(new SubTypesScanner(false)).setUrls(ClasspathHelper.forClassLoader(classLoader))
                .addClassLoader(classLoader));
        for (String className : classNames) {
            final Class<?> clazz = classLoader.loadClass(className);
            for(Class<?> subclazz : reflections.getSubTypesOf(clazz)) {
                for (Field field : subclazz.getDeclaredFields()) {
                    if (!Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    // The psuedo field for the values() method should be ignored
                    if ("ENUM$VALUES".equals(field.getName())) {
                        continue;
                    }
                    // Ignore psuedo $VALUES field, too
                    if ("$VALUES".equals(field.getName())) {
                        continue;
                    }
                    translationKeys.add(String.format("%s.%s", subclazz.getCanonicalName(), field.getName()));
                }
            }
        }
        return translationKeys;
    }

    /**
     * Nullary constructor to prevent direct instantiation.
     */
    private TranslationClassUtils() {
    }
}
