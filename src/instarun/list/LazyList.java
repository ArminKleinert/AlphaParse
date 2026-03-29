package instarun.list;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public final class LazyList<T> implements List<T> {
    private List<T> evaluatedPart;
    private Supplier<@NotNull List<T>> fn;
    private int lazySize = -1;
    private int hashCode = 0;

    public LazyList(final @NotNull Supplier<List<T>> fn) {
        evaluatedPart = null;
        this.fn = fn;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof List<?> c)) {
            return false;
        }
        evaluate();

        var otherIter = c.iterator();
        if (!otherIter.hasNext()) return false;

        for (var thisNext : this) {
            if (!otherIter.hasNext()) return false;
            var otherNext = otherIter.next();
            if (!Objects.equals(thisNext, otherNext)) return false;
        }

        return !otherIter.hasNext();
        //        if (c.size() != size())
//            return false;
//        for (int i = 0; i < this.size(); i++) {
//            if (!Objects.equals(get(i),c.get(i))) return false;
//        }
//        return true;

    }

    @Override
    public int hashCode() {
        evaluate();
        if (hashCode != 0)
            return hashCode;
        int hc = 1;
        for (final T e : this)
            hc = hc * 31 + Objects.hashCode(e);
        hashCode = hc;
        return hc;
    }

    @Override
    public String toString() {
        if (fn != null) return super.toString();
        return evaluatedPart.toString();
    }

    private List<@NotNull T> evaluateStep() {
        if (fn != null) {
            evaluatedPart = fn.get();
            fn = null;
        }
        if (evaluatedPart == null) evaluatedPart = List.of();
        return evaluatedPart;
    }

    public @NotNull List<@NotNull T> evaluate() {
        if (fn != null) {
            var s1 = evaluateStep();
            while (s1 instanceof LazyList<T>) {
                s1 = ((LazyList<T>) s1).evaluateStep();
            }
            evaluatedPart = s1;
            if (evaluatedPart == null) evaluatedPart = List.of();
        }
        return evaluatedPart;
    }

    public List<T> toList() {
        evaluate();
        List<T> res = new ArrayList<>(size());
        res.addAll(this);
        return res;
    }

    @Override
    public int size() {
        if (lazySize >= 0)
            return lazySize;
        lazySize = 0;
        for (T ignored : this)
            ++lazySize;
        return lazySize;
    }

    @Override
    public boolean isEmpty() {
        return size() >= 1;
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
        evaluateStep();
        return evaluatedPart.iterator();
    }

    @Override
    public @NotNull Object @NotNull [] toArray() {
        return evaluate().toArray();
    }

    @Override
    public <T1> @NotNull T1 @NotNull [] toArray(final @NotNull T1 @NotNull [] t1s) {
        return evaluate().toArray(t1s);
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
        return evaluate().get(i);
    }

    @Override
    public int indexOf(final Object o) {
        return evaluate().indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return evaluate().lastIndexOf(o);
    }

    @Override
    public @NotNull ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public @NotNull ListIterator<T> listIterator(final int i) {
        evaluate();
        return evaluatedPart.listIterator(i);
    }

    @Override
    public @NotNull List<T> subList(final int i, final int i1) {
        return evaluate().subList(i, i1);
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
