package alphaparse.collections;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * An interface which pretends to be a list.
 *
 * @param <T> The generic type.
 */
public interface PretenderList<T> extends List<T> {
    @Override
    default int size() {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean isEmpty() {
        return size() == 0;
    }

    @Override
    default @NotNull Iterator<T> iterator() {
        return Arrays.asList(toArray()).iterator();
    }

    @Override
    default boolean contains(Object o) {
        for (var e : this) if (Objects.equals(o, e)) return true;
        return false;
    }

    @Override
    default @NotNull T @NotNull [] toArray() {
        return toArray((T[])new Object[0]);
    }

    @Override
    default @NotNull <T1> T1 @NotNull [] toArray(@NotNull T1 @NotNull [] ts) {
        if (ts.length < size())
            ts = (T1[])new Object[size()];
        for (int i = 0; i < size(); i++)
            ts[i] = (T1) get(i);
        return ts;
    }

    @Override
    default boolean add(T o) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean containsAll(@NotNull Collection<?> collection) {
        for (Object o : collection) {
            if (!contains(o)) return false;
        }return true;
    }

    @Override
    default boolean addAll(@NotNull Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean addAll(int i, @NotNull Collection<? extends T> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean removeAll(@NotNull Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    default boolean retainAll(@NotNull Collection<?> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    default T get(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    default T set(int i, T o) {
        throw new UnsupportedOperationException();
    }

    @Override
    default void add(int i, T o) {
        throw new UnsupportedOperationException();
    }

    @Override
    default T remove(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    default int indexOf(Object o) {
        for (int i = 0; i < size(); i++) {
            if (Objects.equals(get(i),o))return i;
        }return -1;
    }

    @Override
    default int lastIndexOf(Object o) {
        for (int i = size()-1; i >= 0; i--) {
                if (Objects.equals(get(i),o))return i;
        }
        return -1;
    }

    @Override
    default @NotNull ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    default @NotNull ListIterator<T> listIterator(int i) {
        return Arrays.asList(toArray()).listIterator(i);
    }

    @Override
    default @NotNull List<T> subList(int i, int i1) {
        return Arrays.asList(toArray()).subList(i, i1);
    }
}
