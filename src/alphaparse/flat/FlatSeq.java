package alphaparse.flat;


import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A list-like type of generic elements. It is used to differentiate from other List types.
 *
 * @param <T> The generic type.
 */
public class FlatSeq<T> implements Iterable<T> {
    private static FlatSeq<Object> EMPTY = null;

    private final Object[] v;
    private int hashCode = 0;

    /**
     * Instances of this type always start empty. This method simply returns an empty sequence.
     *
     * @param <T> The generic type.
     * @return The empty instance.
     */
    public static @NotNull <T> FlatSeq<@NotNull T> make() {
        if (EMPTY == null) EMPTY = new FlatSeq<>(new Object[0]);
        return (FlatSeq<T>) EMPTY;
    }

    private FlatSeq(final @NotNull Object @NotNull [] v) {
        this.v = v;
    }

    @Override
    public @NotNull Iterator<@NotNull T> iterator() {
        return new Iterator<>() {
            private int pos = 0;

            public boolean hasNext() {
                return v.length > pos;
            }

            public T next() {
                return (T) v[pos++];
            }
        };
    }

    /**
     * Return the size of the collection.
     *
     * @return The size as an int.
     */
    public int size() {
        return v.length;
    }

    /**
     * Equivalent to {@code size() == 0}
     *
     * @return true if {@code size() == 0}, false otherwise.
     */
    public boolean isEmpty() {
        return v.length == 0;
    }

    @Override
    public @NotNull String toString() {
        return Arrays.toString(v);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FlatSeq<?> c)) {
            return false;
        }
        return Arrays.equals(v, c.v);
    }

    @Override
    public int hashCode() {
        if (hashCode != 0)
            return hashCode;
        int hc = Arrays.hashCode(v);
        hashCode = hc;
        return hc;
    }

    /**
     * Append a single element. If the element is null, {@code this} is returned.
     *
     * @param obj The new element.
     * @return A new instance with the element appended.
     */
    public @NotNull FlatSeq<@NotNull T> append(final T obj) {
        if (obj == null) {
            return this;
        }

        final @NotNull Object[] newV = Arrays.copyOf(v, v.length + 1);
        newV[newV.length - 1] = obj;

        return new FlatSeq<>(newV);
    }

    /**
     * Appends all elements from another instance.
     *
     * @param obj The other instance.
     * @return A new instance with the objects from {@code this} and the parameter.
     */
    public @NotNull FlatSeq<@NotNull T> concat(final @NotNull FlatSeq<?> obj) {
        if (size() == 0) return (FlatSeq<T>) obj;
        final @NotNull Object[] newV = Arrays.copyOf(v, v.length + obj.v.length);
        System.arraycopy(obj.v, 0, newV, v.length, obj.v.length);

        return new FlatSeq<>(newV);
    }
}
