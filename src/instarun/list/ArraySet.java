package instarun.list;

import java.util.*;

public class ArraySet<E> extends AbstractSet<E> {

    private final List<E> elements;

    public ArraySet() {
        this.elements = new ArrayList<>();
    }

    public ArraySet(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    @Override
    public boolean add(E e) {
        if (!elements.contains(e)) {
            elements.add(e);
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return elements.contains(o);
    }

    @Override
    public boolean remove(Object o) {
        return elements.remove(o);
    }

    @Override
    public Iterator<E> iterator() {
        return elements.iterator();
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public void clear() {
        elements.clear();
    }
}
