package alphaparse;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Keyword implements Comparable<Keyword> {
    private static final ConcurrentHashMap<String, Reference<Keyword>> table = new ConcurrentHashMap<>();
    private static final ReferenceQueue<Keyword> rq = new ReferenceQueue<>();
    public final String sym;

    public static Keyword intern(String sym) {
        Keyword k = null;
        Reference<Keyword> existingRef = table.get(sym);
        if (existingRef == null) {
            clearCache();

            k = new Keyword(sym);
            existingRef = table.putIfAbsent(sym, new WeakReference<>(k, rq));
        }

        if (existingRef == null) {
            return k;
        } else {
            Keyword existingKeyword = existingRef.get();
            if (existingKeyword != null) {
                return existingKeyword;
            }
            table.remove(sym, existingRef);
            return intern(sym);
        }
    }

    private Keyword(String sym) {
        this.sym = sym;
    }

    public final int hashCode() {
        return this.sym.hashCode() - 1640531527;
    }

    public String toString() {
        return ":" + this.sym;
    }

    /**
     * @deprecated
     */
    public Object throwArity() {
        throw new IllegalArgumentException("Wrong number of args passed to keyword: " + this);
    }

    public String getName() {
        return sym;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Keyword)) return false;
        return getName().equals(((Keyword) o).getName());
    }

    @Override
    public int compareTo(@NotNull Keyword keyword) {
        return sym.compareTo(keyword.sym);
    }

    private static void clearCache() {
        if (Keyword.rq.poll() != null) {
            while(Keyword.rq.poll() != null) {
            }

            for(Map.Entry<String, Reference<Keyword>> e : Keyword.table.entrySet()) {
                Reference<Keyword> val = e.getValue();
                if (val != null && val.get() == null) {
                    Keyword.table.remove(e.getKey(), val);
                }
            }
        }
    }
}
