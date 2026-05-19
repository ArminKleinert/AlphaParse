package alphaparse.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides some utilities for things that come up again and again.
 */
public class Utils {
    private Utils() {
    }

    /**
     * Append a variadic list of {@link Collection} instances to a {@link Set}.
     *
     * @param first The first set.
     * @param more  The other collections.
     * @param <T>   Generic type.
     * @return A new unmodifiable Set.
     */
    @SafeVarargs
    public static <T> @NotNull Set<T> concat(@NotNull Set<T> first, Collection<T>... more) {
        return Stream.concat(first.stream(), Arrays.stream(more).flatMap(Collection::stream)).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Append a variadic list of elements to a {@link Set}.
     *
     * @param first    The first set.
     * @param elements The elements to add.
     * @param <T>      Generic type.
     * @return A new unmodifiable Set.
     */
    @SafeVarargs
    public static <T> @NotNull Set<T> cons(@NotNull Set<T> first, T... elements) {
        return Stream.concat(first.stream(), Arrays.stream(elements)).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Removes elements from a {@link Set}.
     *
     * @param first    The set.
     * @param elements The elements to remove.
     * @param <T>      Generic type.
     * @return A new unmodifiable Set.
     */
    @SafeVarargs
    public static <T> @NotNull Set<T> remove(@NotNull Set<T> first, T... elements) {
        var elemSet = new HashSet<>(Arrays.asList(elements));
        return first.stream().filter(it -> !elemSet.contains(it)).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Append a variadic list of {@link Collection} instances to a {@link List}.
     *
     * @param first The first List.
     * @param more  The other collections.
     * @param <T>   Generic type.
     * @return A new unmodifiable List.
     */
    @SafeVarargs
    public static <T> @NotNull List<T> concat(@NotNull List<T> first, Collection<T>... more) {
        return Stream.concat(first.stream(), Arrays.stream(more).flatMap(Collection::stream)).toList();
    }
}
