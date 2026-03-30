package alphaparse.flat;


import alphaparse.parsetree.Node;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AutoFlattenSeq<T> {
    private static final AutoFlattenSeq<Object> EMPTY = new AutoFlattenSeq<>(new Object[0]);

    private final Object[] v;
    private int hashCode = 0;

    public static long instancesEver = 0;
    public static long highestSize = 0;
    public static long emptyInstances = 0;
    public static long toNodesCalls = 0;
    public static long iteratorCalls = 0;
    public static long totalEqualsCalls = 0;
    public static long afsEqualsCalls = 0;
    public static long singleAdditions = 0;
    public static long multiAdditions = 0;
    public static long nullAdditions = 0;
    public static long hashCodeCalls = 0;
    public static long hashCodeCalcs = 0;

    public static @NotNull <T> AutoFlattenSeq<@NotNull T> make() {
        return (AutoFlattenSeq<T>) EMPTY;
    }

    private AutoFlattenSeq(final @NotNull Object @NotNull [] v) {
//        instancesEver++;
//        if (v.length > highestSize) highestSize = v.length;
//        if (v.length==0) emptyInstances++;
        this.v = v;
    }

    public @NotNull List<@NotNull Node> toNodes() {
//        toNodesCalls++;
        var result = new ArrayList<Node>();
        var iter = iterator();
        while (iter.hasNext())
            result.add(Node.of(iter.next()));
        return result;
    }

    public @NotNull Iterator<@NotNull T> iterator() {
//        iteratorCalls++;
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
        return v.length==0;
    }

    @Override
    public @NotNull String toString() {
        return Arrays.toString(v);
    }

    @Override
    public boolean equals(Object o) {
//        totalEqualsCalls++;

        if (!(o instanceof AutoFlattenSeq<?> c)) {
            return false;
        }
//        afsEqualsCalls++;

        return Arrays.equals(v, c.v);
    }

    @Override
    public int hashCode() {
//        hashCodeCalls++;
        if (hashCode != 0)
            return hashCode;
//        hashCodeCalcs++;
        int hc = Arrays.hashCode(v);
        hashCode = hc;
        return hc;
    }

    public @NotNull AutoFlattenSeq<@NotNull T> append(final T obj) {
        if (obj == null) {
//            nullAdditions++;
            return this;
        }
//        singleAdditions++;

        final @NotNull Object[] newV = Arrays.copyOf(v, v.length+1);
        newV[newV.length-1] = obj;

        return new AutoFlattenSeq<>(newV);
    }

    public @NotNull AutoFlattenSeq<@NotNull T> concat(final @NotNull AutoFlattenSeq<?> obj) {
        if (size() == 0) return (AutoFlattenSeq<T>) obj;

//        multiAdditions++;

        final @NotNull Object[] newV = Arrays.copyOf(v, v.length+obj.v.length);
        System.arraycopy(obj.v, 0, newV, v.length, obj.v.length);

        return new AutoFlattenSeq<>(newV);
    }
}
