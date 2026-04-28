package alphaparse.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utilities for tests.
 */
public class ClassUtil {
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

    /**
     * Clears a reference queue and removes references to deallocated objects from the concurrent hashmap.
     *
     * @param rq    The reference queue.
     * @param table The table.
     * @param <K>   Key type.
     * @param <T>   Value type.
     */
    public static <K, T> void clearReferenceCache(
            final @NotNull ReferenceQueue<T> rq,
            final @NotNull ConcurrentHashMap<K, Reference<T>> table) {
        if (rq.poll() != null) {
            Object o = rq.poll();
            while (o != null) {
                o = rq.poll();
            }

            for (final @NotNull Map.Entry<K, Reference<T>> e : table.entrySet()) {
                final @NotNull Reference<T> val = e.getValue();
                if (val != null && val.get() == null) {
                    table.remove(e.getKey(), val);
                }
            }
        }
    }
}
