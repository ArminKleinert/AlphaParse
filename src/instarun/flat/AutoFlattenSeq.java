package instarun.flat;


import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AutoFlattenSeq<T extends @NotNull Object>
        implements List<T> {

    private final List<T> v;
    private int hashCode = 0;

    public static @NotNull <T> AutoFlattenSeq<T> make() {
        return new AutoFlattenSeq<>(List.of());
    }

    //public static <T> AutoFlattenSeq<T> make(PersistentVector v) {return new AutoFlattenSeq<>(v);}
    public static @NotNull <T> AutoFlattenSeq<T> make(final @NotNull List<T> v) {
        if (v instanceof AutoFlattenSeq<?>)
            return (AutoFlattenSeq<T>) v;
        return new AutoFlattenSeq<>(new ArrayList<>(v));
    }

    //public static <T> AutoFlattenSeq<T> make(PersistentVector v) {return new AutoFlattenSeq<>(v);}
    public static @NotNull <T> AutoFlattenSeq<T> makeUnsafe(final @NotNull List<T> v) {
        if (v instanceof AutoFlattenSeq<?>)
            return (AutoFlattenSeq<T>) v;
        return new AutoFlattenSeq<>(v);
    }

    private AutoFlattenSeq(final @NotNull List<T> v) {
        this.v = v;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return v.iterator();
    }

    @Override
    public int size() {
        return v.size();
    }

    @Override
    public boolean isEmpty() {
        return v.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return v.contains(o);
    }

    @Override
    public Object @NotNull [] toArray() {
        return v.toArray();
    }

    @Override
    public <T1> T1 @NotNull [] toArray(final @NotNull T1 @NotNull [] objects) {
        return v.toArray(objects);
    }

    @Override
    public boolean containsAll(final @NotNull Collection collection) {
        for (Object o : collection)
            if (!contains(o)) return false;
        return true;
    }

    @Override
    public @NotNull String toString() {
        return v.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof List<?> c)){
            return false;
        }
        var otherIter = c.iterator();
        if (!otherIter.hasNext()) return false;

        for (var thisNext : this) {
            if (!otherIter.hasNext()) return false;
            var otherNext = otherIter.next();
            if (!Objects.equals(thisNext, otherNext)) return false;
        }

        return !otherIter.hasNext();
    }

    @Override
    public int hashCode() {
        if (hashCode != 0)
            return hashCode;
        int hc = 1;
        for (final T e : this)
            hc = hc * 31 + Objects.hashCode(e);
        hashCode = hc;
        return hc;
    }

    @Override
    public T get(final int i) {
        return (T) v.get(i);
    }

    @Override
    public T set(int i, T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int i, T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        return v.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return v.lastIndexOf(o);
    }

    @Override
    public @NotNull ListIterator<T> listIterator() {
        return v.listIterator();
    }

    @Override
    public @NotNull ListIterator<T> listIterator(int i) {
        return v.listIterator(i);
    }

    @Override
    public @NotNull List<T> subList(int i, int i1) {
        return v.subList(i, i1);
    }

    @Override
    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int i, @NotNull Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(@NotNull Collection collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection collection) {
        throw new UnsupportedOperationException();
    }

    public @NotNull AutoFlattenSeq<T> conjFlat(Object obj) {
        if (obj == null) return this;
        if (!(obj instanceof AutoFlattenSeq)) {
            List<T> l = new ArrayList<>(v);
            l.add((T) obj);
            return new AutoFlattenSeq<>(l);
        }

        if (isEmpty()) return (AutoFlattenSeq<T>) obj;

        List<T> l = new ArrayList<>(v);
        l.addAll((Collection<T>) obj);
        return new AutoFlattenSeq<>(l);
    }
}
