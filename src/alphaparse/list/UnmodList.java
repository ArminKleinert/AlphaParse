package alphaparse.list;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * An unmodifiable random-access list type which is optimized for speed and size.
 *
 * @param <T> Generic type.
 */
public final class UnmodList<T> implements List<T>, RandomAccess {
    private final Object[] inner;
    private int hashCode = 0;

    /**
     * Creates a new instance, copying over all elements from a collection.
     *
     * @param coll The other list.
     */
    public UnmodList(final @NotNull List<T> coll) {
        inner = new Object[coll.size()];
        int i = 0;
        for (T t : coll) {
            inner[i] = t;
            i++;
        }
    }

    /**
     * Creates a new instance from a trusted array. Attention: The array is NOT copied. It is assumed that the array will not be modified.
     *
     * @param safeArray The array.
     */
    public UnmodList(final Object[] safeArray) {
        inner = safeArray;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof List<?> c)) {
            return false;
        }
        if (o instanceof UnmodList<?>) {
            return Arrays.equals(inner, ((UnmodList<?>) o).inner);
        }

        final @NotNull var otherIter = c.iterator();
        if (!otherIter.hasNext() && size() > 0) return false;

        for (var thisNext : this) {
            if (!otherIter.hasNext()) return false;
            final @NotNull var otherNext = otherIter.next();
            if (!Objects.equals(thisNext, otherNext)) return false;
        }

        return !otherIter.hasNext();
    }

    @Override
    public int hashCode() {
        if (hashCode != 0)
            hashCode = Arrays.hashCode(inner);
        return hashCode;
    }

    @Override
    public @NotNull String toString() {
        return Arrays.toString(inner);
    }

    @Override
    public int size() {
        return inner.length;
    }

    @Override
    public boolean isEmpty() {
        return size() < 1;
    }

    @Override
    public boolean contains(final Object o) {
        for (T t : this) {
            if (Objects.equals(t, o))
                return true;
        }
        return false;
    }

    @Override
    public Object @NotNull [] toArray() {
        return Arrays.copyOf(inner, inner.length);
    }

    @Override
    public <T1> T1 @NotNull [] toArray(T1[] a) {
        if (a.length < size()) {
            return (T1[]) Arrays.copyOf(inner, size(), a.getClass());
        } else {
            System.arraycopy(inner, 0, a, 0, size());
            if (a.length > size()) {
                a[size()] = null;
            }

            return a;
        }
    }

    @Override
    public boolean containsAll(final @NotNull Collection<?> collection) {
        for (Object o : collection) {
            if (!contains(o))
                return false;
        }
        return true;
    }

    @Override
    public T get(final int i) {
        Objects.checkIndex(i, size());
        return (T) inner[i];
    }

    @Override
    public int indexOf(final Object o) {
        for (int i = 0; i < size(); i++) {
            if (Objects.equals(get(i), o))
                return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(final Object o) {
        for (int i = size() - 1; i >= 0; --i) {
            if (Objects.equals(get(i), o))
                return i;
        }
        return -1;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return listIterator(0);
    }

    @Override
    public @NotNull ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public @NotNull ListIterator<T> listIterator(final int i) {
        if (!isEmpty())
            Objects.checkIndex(i, size());
        return new ListIterator<>() {
            private int cursor = i;

            @Override
            public boolean hasNext() {
                return cursor < size();
            }

            @Override
            public T next() {
                int i = cursor;
                cursor++;
                return get(i);
            }

            @Override
            public boolean hasPrevious() {
                return this.cursor > 0;
            }

            @Override
            public T previous() {
                cursor--;
                return get(cursor);
            }

            @Override
            public int nextIndex() {
                return this.cursor;
            }

            @Override
            public int previousIndex() {
                return this.cursor - 1;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(T t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(T t) {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public @NotNull List<T> subList(final int i, final int i1) {
        if (i < 0 || i1 >= size())
            throw new IllegalArgumentException();
        if (i == 0 && i1 == size() - 1)
            return this;
        return new UnmodList<>(Arrays.copyOfRange(inner, i, i1));
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final @NotNull Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(final int i, final @NotNull Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final @NotNull Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final @NotNull Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T set(final int i, final T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(final int i, final T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(final int i) {
        throw new UnsupportedOperationException();
    }
}
