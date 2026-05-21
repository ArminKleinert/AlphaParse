package alphaparse;

import alphaparse.util.ClassUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for string-alternatives that are slightly slower to create than strings, but provide interning and O(1) comparisons via the buildin {@code ==} operator.
 * <p>
 * {@code
 * Sym k1 = Sym.sym(str);
 * Sym k2 = Sym.sym(str);
 * k1 == k2 // Guarantied to be true.
 * }
 */
public final class Sym {
    private static final ConcurrentHashMap<String, Reference<Sym>> table =
            new ConcurrentHashMap<>();
    private static final ReferenceQueue<Sym> rq =
            new ReferenceQueue<>();

    private final @NotNull String name;

    /**
     * Creates a new symbol or returns an existing one.
     *
     * @param sym The string for the symbol.
     * @return The new or already existing symbol.
     */
    public static @NotNull Sym sym(final @NotNull String sym) {
        Sym k = null;
        Reference<Sym> existingRef = table.get(sym);
        if (existingRef == null) {
            ClassUtil.clearReferenceCache(rq, table);

            k = new Sym(sym);
            existingRef = table.putIfAbsent(sym, new WeakReference<>(k, rq));
        }

        if (existingRef == null) {
            return k;
        } else {
            Sym existing = existingRef.get();
            if (existing != null) {
                return existing;
            }
            table.remove(sym, existingRef);
            return sym(sym);
        }
    }

    private Sym(final @NotNull String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode() - 1640531527;
    }

    @Override
    public @NotNull String toString() {
        return ":" + this.name;
    }

    /**
     * Returns the backing string.
     *
     * @return The backing string.
     */
    public @NotNull String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
