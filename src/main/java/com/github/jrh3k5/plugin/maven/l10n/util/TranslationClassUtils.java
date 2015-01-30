package com.github.jrh3k5.plugin.maven.l10n.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
     * @throws IllegalArgumentException
     *             If a class' field cannot be read.
     */
    public static Collection<String> getTranslationKeys(Collection<String> classNames, ClassLoader classLoader) throws ClassNotFoundException {
        final Set<String> translationKeys = new HashSet<>();
        final Reflections reflections = new Reflections(new ConfigurationBuilder().setScanners(new SubTypesScanner(false)).setUrls(ClasspathHelper.forClassLoader(classLoader))
                .addClassLoader(classLoader));
        // Load up all of the classes first
        final Collection<Class<?>> clazzes = new ArrayList<>(classNames.size());
        for (String className : classNames) {
            clazzes.add(classLoader.loadClass(className));
        }

        for (Class<?> clazz : clazzes) {
            for(Class<?> subclazz : reflections.getSubTypesOf(clazz)) {
                for (Field field : subclazz.getDeclaredFields()) {
                    if (!Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }

                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }

                    // Reject any fields that are not instances of the designated classes
                    boolean isInstanceOf = false;
                    for (Class<?> instanceOfClazz : clazzes) {
                        try {
                            isInstanceOf |= instanceOfClazz.isAssignableFrom(field.get(null).getClass());
                        } catch (IllegalArgumentException | IllegalAccessException e) {
                            throw new IllegalArgumentException(String.format("Unable to read class type of field %s.%s", clazz.getCanonicalName(), field.getName()), e);
                        }
                    }

                    if (!isInstanceOf) {
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
