package alphaparse;

import alphaparse.util.ClassUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for string-alternatives that are slightly slower to create than strings, but provide interning and O(1) comparisons via the buildin {@code ==} operator. Optionally, this interning can be disabled, which may save memory if the keywords are often discarded. If interning is disabled, the fast comparison is disabled as well.
 * <p>
 * {@code Keyword k1 = Keyword.intern(str);
 * Keyword k2 = Keyword.intern(str);
 * k1 == k2 // Guarantied to be true.}
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
     * Disable caching.
     */
    public static void disableCaching() {
        cachingDisabled = true;
        table.clear();
        ClassUtil.clearReferenceCache(rq, table);
    }

    /**
     * Creates a new keyword or returns an existing one.
     *
     * @param sym The string for the keyword.
     * @return The new or already existing keyword.
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
     * Returns the backing string.
     *
     * @return The backing string.
     */
    public @NotNull String getName() {
        return sym;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Keyword)) return false;
        if (!cachingDisabled) return false;
        return getName().equals(((Keyword) o).getName());
    }
}
