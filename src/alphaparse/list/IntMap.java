package alphaparse.list;

import org.jetbrains.annotations.Nullable;
import java.util.*;

public class IntMap<V> implements SequencedMap<Integer, V> {

    private int[] keys;
    private Object[] values;
    private int size;

    public IntMap() {
        this(16);
    }

    public IntMap(int capacity) {
        keys = new int[capacity];
        values = new Object[capacity];
    }


    private int findIndex(int key) {
        return Arrays.binarySearch(keys, 0, size, key);
    }

    private void ensureCapacity(int needed) {
        if (needed <= keys.length) return;
        int newCap = Math.max(needed, keys.length * 2 + 1);
        keys = Arrays.copyOf(keys, newCap);
        values = Arrays.copyOf(values, newCap);
    }

    @Override
    public V put(Integer key, V value) {
        return put(key.intValue(), value);
    }

    public V put(int key, V value) {
        int idx = findIndex(key);

        if (idx >= 0) {
            @SuppressWarnings("unchecked")
            V old = (V) values[idx];
            values[idx] = value;
            return old;
        }

        int insertAt = -idx - 1;
        ensureCapacity(size + 1);

        System.arraycopy(keys, insertAt, keys, insertAt + 1, size - insertAt);
        System.arraycopy(values, insertAt, values, insertAt + 1, size - insertAt);

        keys[insertAt] = key;
        values[insertAt] = value;
        size++;
        return null;
    }

    @Override
    public V get(Object key) {
        if (!(key instanceof Integer)) return null;
        return get(((Integer) key).intValue());
    }
    public V get(int key) {
        int idx = findIndex(key);
        if (idx >= 0) {
            @SuppressWarnings("unchecked")
            V val = (V) values[idx];
            return val;
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        if (!(key instanceof Integer)) return null;
        int idx = findIndex((Integer) key);
        if (idx < 0) return null;

        @SuppressWarnings("unchecked")
        V old = (V) values[idx];

        int numMoved = size - idx - 1;
        if (numMoved > 0) {
            System.arraycopy(keys, idx + 1, keys, idx, numMoved);
            System.arraycopy(values, idx + 1, values, idx, numMoved);
        }

        size--;
        values[size] = null; // help GC
        return old;
    }

    @Override
    public boolean containsKey(Object key) {
        return (key instanceof Integer) && findIndex((Integer) key) >= 0;
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(values[i], value)) return true;
        }
        return false;
    }

    @Override
    public void clear() {
        Arrays.fill(values, 0, size, null);
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // ---- Views ----

    @Override
    public Set<Integer> keySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<>() {
                    int cursor = 0;

                    public boolean hasNext() {
                        return cursor < size;
                    }

                    public Integer next() {
                        if (!hasNext()) throw new NoSuchElementException();
                        return keys[cursor++];
                    }
                };
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    @Override
    public Collection<V> values() {
        return new AbstractCollection<>() {
            @Override
            public Iterator<V> iterator() {
                return new Iterator<>() {
                    int cursor = 0;

                    @SuppressWarnings("unchecked")
                    public V next() {
                        if (!hasNext()) throw new NoSuchElementException();
                        return (V) values[cursor++];
                    }

                    public boolean hasNext() {
                        return cursor < size;
                    }
                };
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    @Override
    public Set<Entry<Integer, V>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<Entry<Integer, V>> iterator() {
                return new Iterator<>() {
                    int cursor = 0;

                    public boolean hasNext() {
                        return cursor < size;
                    }

                    public Entry<Integer, V> next() {
                        if (!hasNext()) throw new NoSuchElementException();
                        int i = cursor++;
                        return new AbstractMap.SimpleEntry<>(
                                keys[i],
                                cast(values[i])
                        );
                    }
                };
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    @SuppressWarnings("unchecked")
    private V cast(Object o) {
        return (V) o;
    }

    // ---- Bulk ops (minimal impls) ----

    @Override
    public void putAll(Map<? extends Integer, ? extends V> m) {
        for (Entry<? extends Integer, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public SequencedMap<Integer, V> reversed() {
throw new UnsupportedOperationException();
    }

    public V intMapPoll() {
        if (size == 0)
            return null;
        var last = values[size-1];
        size--;
        return (V) last;
    }
}