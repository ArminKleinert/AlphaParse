package instarun.trampoline;

import instarun.functions.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class InstaMsgCacheKey {
    private final int index;
    private final @NotNull Listener listener;

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final InstaMsgCacheKey that = (InstaMsgCacheKey) o;
        return index == that.index && Objects.equals(listener, that.listener);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, listener);
    }

    public int getIndex() {
        return index;
    }

    public InstaMsgCacheKey(final int index, final @NotNull Listener f) {
        this.index = index;
        this.listener = f;
    }
}
