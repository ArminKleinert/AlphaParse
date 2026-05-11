package alphaparse.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    private Utils(){}

    public static <T> Set<T> concat(Set<T> first, Collection<T>... sets) {
        return Stream.concat(first.stream(), Arrays.stream(sets).flatMap(Collection::stream)).collect(Collectors.toSet());
    }

    public static <T> Set<T> cons(Set<T> first, T... elements) {
        return Stream.concat(first.stream(), Arrays.stream(elements)).collect(Collectors.toSet());
    }

    public static <T> List<T> concat(List<T> first, Collection<T>... sets) {
        return Stream.concat(first.stream(), Arrays.stream(sets).flatMap(Collection::stream)).toList();
    }
}
