package instarun.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class ClassUtil {
    public static Class<?> mostDerived(Collection<?> objects) {
        List<Class<?>> common = null;
        Set<Class<?>> checked = objects.size() > 30 ? new HashSet<>() : null;
        for (Object object : objects) {
            if (object == null) {
                continue;
            }
            Class<?> clz = object.getClass();
            if (checked == null || checked.add(clz)) {
                List<Class<?>> hierarchy = new ArrayList<>();
                for (; clz != Object.class; clz = clz.getSuperclass()) {
                    hierarchy.add(clz);
                }
                if (common == null) {
                    common = hierarchy;
                } else {
                    common.retainAll(hierarchy);
                }
            }
        }
        return common != null ? !common.isEmpty() ? common.getFirst() : Object.class : null;
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
}
