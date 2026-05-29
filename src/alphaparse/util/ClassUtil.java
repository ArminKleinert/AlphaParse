package alphaparse.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Utilities for tests.
 */
public final class ClassUtil {
    private ClassUtil() {
    }

    /**
     * Takes a collection of objects and returns the superclass used by all objects. Note that it can't notice interfaces.
     * <pre>
     * {@code
     *   println(ClassUtil.mostDerived(List.of("abc", "abc"))); // class java.lang.String
     *   println(ClassUtil.mostDerived(List.of(new StringBuilder("abc"), new StringBuffer("abc")))); // class java.lang.AbstractStringBuilder
     *   println(ClassUtil.mostDerived(List.of(new StringBuilder("abc"), "abc"))); // class java.lang.Object (not CharSequence)
     * }
     * </pre>
     *
     * @param objects The objects.
     * @return The most common superclass.
     */
    public static @Nullable Class<?> mostDerived(final @NotNull Collection<?> objects) {
        List<Class<?>> common = null;
        SequencedSet<Class<?>> checked = objects.size() > 30 ? new LinkedHashSet<>() : null;
        for (final Object object : objects) {
            if (object == null) {
                continue;
            }

            @NotNull Class<?> clz = object.getClass();

            if (checked != null) {
                var wasNewlyAdded = checked.add(clz);
                if (!wasNewlyAdded) continue;
            }

            final @NotNull List<Class<?>> hierarchy = new ArrayList<>();
            for (; clz != Object.class; clz = clz.getSuperclass()) {
                hierarchy.add(clz);
            }
            if (common == null) {
                common = hierarchy;
            } else {
                common.retainAll(hierarchy);
            }
        }
        return common != null ? (!common.isEmpty() ? common.getFirst() : Object.class) : null;
    }

    private static @Nullable List<String> uniqueStrings = null;

    /**
     * Logs a string into a file unless it was already logged.
     *
     * @param s The string.
     */
    public static void fileLog(String s) {
        if (uniqueStrings == null)
            uniqueStrings = new ArrayList<>();
        if (uniqueStrings.contains(s))
            return;
        uniqueStrings.add(s);
        try {
            new File("logfile").createNewFile();
            Files.write(Path.of("logfile"), List.of(s), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
