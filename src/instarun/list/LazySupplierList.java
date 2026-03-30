package instarun.list;

import instarun.IO2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class LazySupplierList<T> implements List<T> {
    private final @NotNull List<@NotNull T> evaluatedPart;
    private final int maxResults;
    private Supplier<T> nextFn;
    private boolean fullyEvaluated = false;

    public LazySupplierList(final @NotNull Supplier<T> nextFn, final int maxResults) {
        this.evaluatedPart = new ArrayList<>();
        this.nextFn = nextFn;
        this.maxResults = maxResults;
    }

    public LazySupplierList(final @NotNull Supplier<T> nextFn) {
        this(nextFn, Integer.MAX_VALUE);
    }

    private @Nullable T evalutePart() {
        if (evaluatedPart.size() >= maxResults)
            fullyEvaluated = true;

        if (fullyEvaluated)
            return null;

        final var next = nextFn.get();
        if (next == null) {
            fullyEvaluated = true;
            nextFn = null;
            return null;
        }
        evaluatedPart.add(next);
        return next;
    }

    public void evaluate() {
        @Nullable T ep;
        do {
            ep = evalutePart();
        } while (ep != null);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        var cursor = 0;
        var first = getOrNull(cursor);
        cursor++;
        var second = getOrNull(cursor);
        while (first != null) {
            sb.append(first);
            if (second == null) {
                break;
            }
            cursor++;
            first = second;
            second = getOrNull(cursor);
            sb.append(", ");
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof List<?> c)) {
            return false;
        }

        if (o == this)
            return true;

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
        int hc = 1;
        for (final T e : this)
            hc = hc * 31 + Objects.hashCode(e);
        return hc;
    }

    @Override
    public int size() {
        evaluate();
        return evaluatedPart.size();
    }

    @Override
    public boolean isEmpty() {
        return !iterator().hasNext();
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
    public @NotNull Iterator<T> iterator() {
        return new Iterator<>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return getOrNull(cursor) != null;
            }

            @Override
            public T next() {
                int i = cursor;
                ++cursor;
                return get(i);
            }
        };
    }

    @Override
    public @NotNull Object @NotNull [] toArray() {
        return toArray(new Object[0]);
    }

    @Override
    public <T1> T1 @NotNull [] toArray(T1[] t1s) {
        var size = size();
        if (t1s.length < size) {
            t1s = (T1[]) new Object[size];
        }
        int i = 0;
        for (T t : this) {
            t1s[i] = (T1) t;
            i++;
        }
        return t1s;
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
    public T getFirst() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        } else {
            return get(0);
        }
    }

    @Override
    public T get(final int i) {
        var at = getOrNull(i);
        if (at == null)
            throw new IndexOutOfBoundsException("Cannot access index " + i + " because size is " + size());
        return at;
    }

    public T getOrNull(final int i) {
        if (i < evaluatedPart.size())
            return evaluatedPart.get(i);

        int cursor = evaluatedPart.size();
        do {
            if (fullyEvaluated) return null;
            final T next = evalutePart();
            if (cursor == i) return next;
            cursor++;
        } while (true);
    }

    @Override
    public int indexOf(final Object o) {
        int i = 0;
        for (T t : this) {
            if (Objects.equals(o, t))
                return i;
            i++;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(final Object o) {
        int i = 0;
        int last = -1;
        for (T t : this) {
            if (Objects.equals(o, t))
                last = i;
            i++;
        }
        return last;
    }

    @Override
    public @NotNull List<T> subList(final int i, final int i1) {
        List<T> res = new ArrayList<>();
        int cursor = 0;
        for (var t : this)
            if (cursor >= i && cursor < i1) res.add(t);
            else if (cursor >= i1) break;
        return Collections.unmodifiableList(res);
    }

    @Override
    public @NotNull ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public @NotNull ListIterator<T> listIterator(final int i) {
        return List.copyOf(this).listIterator(i);
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
