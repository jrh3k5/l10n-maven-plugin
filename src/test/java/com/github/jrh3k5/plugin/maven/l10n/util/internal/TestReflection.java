package com.github.jrh3k5.plugin.maven.l10n.util.internal;

import java.util.ArrayList;
import java.util.List;


/**
 * Test enumerations implementing {@link TestInterface}.
 * 
 * @author Joshua Hyde
 * @since 1.3
 */

public enum TestReflection implements TestInterface {
    A, B;

    public static List<String> NAMES = new ArrayList<>();
}
