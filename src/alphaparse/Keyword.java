package alphaparse;

import alphaparse.util.ClassUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 */
public class Keyword {
    private static final ConcurrentHashMap<String, Reference<Keyword>> table =
            new ConcurrentHashMap<>();
    private static final ReferenceQueue<Keyword> rq =
            new ReferenceQueue<>();
    private static boolean cachingDisabled =
            false;

    private final @NotNull String sym;

    /**
     * TODO
     */
    public static void disableCaching() {
        cachingDisabled = true;
        table.clear();
        ClassUtil.clearReferenceCache(rq, table);
    }

    /**
     * TODO
     *
     * @param sym TODO
     * @return TODO
     */
    public static @NotNull Keyword intern(final @NotNull String sym) {
        if (cachingDisabled) {
            return new Keyword(sym);
        }
        Keyword k = null;
        Reference<Keyword> existingRef = table.get(sym);
        if (existingRef == null) {
            ClassUtil.clearReferenceCache(rq, table);

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

    private Keyword(final @NotNull String sym) {
        this.sym = sym;
    }

    @Override
    public final int hashCode() {
        return this.sym.hashCode() - 1640531527;
    }

    @Override
    public @NotNull String toString() {
        return ":" + this.sym;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public @NotNull String getName() {
        return sym;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Keyword)) return false;
        if (cachingDisabled) return getName().equals(((Keyword) o).getName());
        return o == this;
    }
}
