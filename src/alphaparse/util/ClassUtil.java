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

public class ClassUtil {
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

    private static final List<String> uniqueStrings = new ArrayList<>();

    public static void fileLog(String s) {
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
