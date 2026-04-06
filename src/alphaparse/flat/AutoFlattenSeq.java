package alphaparse.flat;


import alphaparse.parsetree.Node;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AutoFlattenSeq<T> {
    private static final AutoFlattenSeq<Object> EMPTY = new AutoFlattenSeq<>(new Object[0]);

    private final Object[] v;
    private int hashCode = 0;

    public static @NotNull <T> AutoFlattenSeq<@NotNull T> make() {
        return (AutoFlattenSeq<T>) EMPTY;
    }

    private AutoFlattenSeq(final @NotNull Object @NotNull [] v) {
        this.v = v;
    }

    public @NotNull List<@NotNull Node> toNodes() {
        var result = new ArrayList<Node>();
        var iter = iterator();
        while (iter.hasNext())
            result.add(Node.of(iter.next()));
        return result;
    }

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

    public int size() {
        return v.length;
    }

    public boolean isEmpty() {
        return v.length == 0;
    }

    @Override
    public @NotNull String toString() {
        return Arrays.toString(v);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AutoFlattenSeq<?> c)) {
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

    public @NotNull AutoFlattenSeq<@NotNull T> append(final T obj) {
        if (obj == null) {
            return this;
        }

        final @NotNull Object[] newV = Arrays.copyOf(v, v.length + 1);
        newV[newV.length - 1] = obj;

        return new AutoFlattenSeq<>(newV);
    }

    public @NotNull AutoFlattenSeq<@NotNull T> concat(final @NotNull AutoFlattenSeq<?> obj) {
        if (size() == 0) return (AutoFlattenSeq<T>) obj;
        final @NotNull Object[] newV = Arrays.copyOf(v, v.length + obj.v.length);
        System.arraycopy(obj.v, 0, newV, v.length, obj.v.length);

        return new AutoFlattenSeq<>(newV);
    }
}
