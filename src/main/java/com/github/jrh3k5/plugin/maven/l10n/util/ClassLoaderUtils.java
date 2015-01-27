package com.github.jrh3k5.plugin.maven.l10n.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

/**
 * Utility for building and handling classloaders.
 * 
 * @author Joshua Hyde
 * @since 1.3
 */

public abstract class ClassLoaderUtils {
    /**
     * Get a classloader that can be used to load the classes of the translation keys.
     * 
     * @param project
     *            A {@link MavenProject} representing the project whose translation keys are to be verified.
     * @return A {@link ClassLoader} that can be used to load the classes of the translation keys.
     * @throws IOException
     *             If any errors occur in building the classloader.
     */
    public static ClassLoader getClassLoader(MavenProject project) throws IOException {
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
     * Nullary constructor to prevent direct instantiation.
     */
    private ClassLoaderUtils() {
    }
}
