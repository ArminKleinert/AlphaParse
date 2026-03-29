package instarun.list;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class Cons<T> implements List<T> {
    private final T head;
    private @NotNull List<T> tail;
    private int hashCode = 0;
    private int size = -1;

    public Cons(final T head, final @NotNull List<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    public @NotNull List<T> getTail() {
        if (tail instanceof LazyList<T>) {
            tail = ((LazyList<T>) tail).evaluate();
        }
        return tail;
    }

    public T getHead() {
        return head;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; ; i++) {
            sb.append(get(i));
            if (i == size() - 1) {
                break;
            }
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
        if ((o instanceof Cons<?> cons)) {
            return Objects.equals(head, cons.head) && Objects.equals(getTail(), cons.getTail());
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
    public int size() {
        if (size == -1) {
            //size = getTail().size() + 1;
            int cnt = 0;
            for (T ignored : this) {
                cnt++;
            }
            size = cnt;
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(final Object o) {
        if (Objects.equals(head, o))
            return true;
        for (T t : getTail()) {
            if (Objects.equals(t, o))
                return true;
        }
        return false;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new Iterator<>() {
            private List<T> rest = Cons.this;
            private Iterator<T> delegate = null;
            private T nextHead;
            private boolean hasBufferedHead = false;

            @Override
            public boolean hasNext() {
                bufferNext();
                return hasBufferedHead || (delegate != null && delegate.hasNext());
            }

            @Override
            public T next() {
                bufferNext();
                if (hasBufferedHead) {
                    hasBufferedHead = false;
                    return nextHead;
                }
                if (delegate != null) {
                    return delegate.next();
                }
                throw new NoSuchElementException();
            }

            private void bufferNext() {
                if (hasBufferedHead || delegate != null) {
                    return;
                }

                if (rest instanceof Cons<?>) {
                    Cons<T> c = (Cons<T>) rest;
                    nextHead = c.head;
                    rest = c.tail;
                    hasBufferedHead = true;
                    return;
                }

                delegate = rest.iterator();
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
        for (int i = 0; i < size; i++) {
            t1s[i] = (T1) get(i);
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
            return head;
        }
    }

    @Override
    public T get(final int i) {
        if (i == 0) return head;
        return getTail().get(i - 1);
    }

    @Override
    public int indexOf(final Object o) {
        if (Objects.equals(head, o)) return 0;
        int i = getTail().indexOf(o);
        if (i >= 0) return i + 1;
        return -1;
    }

    @Override
    public int lastIndexOf(final Object o) {
        int i = getTail().lastIndexOf(o);
        if (i >= 0) return i + 1;
        if (Objects.equals(head, o)) return 0;
        return -1;
    }

    @Override
    public @NotNull List<T> subList(final int i, final int i1) {
        if (i < 0 || i1 <= i)
            throw new IllegalArgumentException();
        if (i1 == size()) {
            if (i == 0) return this;
            if (i == 1) return getTail();
        }
        if (i > 1) {
            return getTail().subList(i - 1, i1 - 1);
        }

        System.out.println("subList");
        final @NotNull List<T> temp = new ArrayList<>();
        temp.add(head);
        temp.addAll(getTail().subList(0, i1 - 1));
        return temp;
    }

    @Override
    public @NotNull ListIterator<T> listIterator() {
        return listIterator(0);
    }

    @Override
    public @NotNull ListIterator<T> listIterator(final int i) {
        return new ArrayList<>(this).listIterator(i);
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
