package alphaparse.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides some utilities for things that come up again and again.
 */
public class Utils {
    private Utils(){}

    /**
     * Append a variadic list of {@link Collection} instances to a {@link Set}.
     * @param first The first set.
     * @param more The other collections.
     * @return A new unmodifiable Set.
     * @param <T> Generic type.
     */
    @SafeVarargs
    public static <T> @NotNull Set<T> concat(@NotNull Set<T> first, Collection<T>... more) {
        return Stream.concat(first.stream(), Arrays.stream(more).flatMap(Collection::stream)).collect(Collectors.toSet());
    }

    /**
     * Append a variadic list of elements to a {@link Set}.
     * @param first The first set.
     * @param elements The elements to add.
     * @return A new unmodifiable Set.
     * @param <T> Generic type.
     */
    @SafeVarargs
    public static <T> @NotNull Set<T> cons(@NotNull Set<T> first, T... elements) {
        return Stream.concat(first.stream(), Arrays.stream(elements)).collect(Collectors.toSet());
    }

    /**
     * Append a variadic list of {@link Collection} instances to a {@link List}.
     * @param first The first List.
     * @param more The other collections.
     * @return A new unmodifiable List.
     * @param <T> Generic type.
     */
    @SafeVarargs
    public static <T> @NotNull List<T> concat(@NotNull List<T> first, Collection<T>... more) {
        return Stream.concat(first.stream(), Arrays.stream(more).flatMap(Collection::stream)).toList();
    }
}
