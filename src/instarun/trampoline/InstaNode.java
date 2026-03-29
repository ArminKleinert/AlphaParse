package instarun.trampoline;

import instarun.functions.Listener;
import instarun.result.success.InstaSuccess;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class InstaNode {
    private final @NotNull List<Listener> listeners;
    private final @NotNull List<Listener> fullListeners;
    private final @NotNull Set<InstaSuccess> results;
    private final @NotNull Set<InstaSuccess> fullResults;

    public InstaNode() {
        this.listeners = new ArrayList<>();
        this.fullListeners = new ArrayList<>();
        this.results = new HashSet<>();
        this.fullResults = new HashSet<>();
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof @NotNull InstaNode instaNode)) return false;
        return Objects.equals(listeners, instaNode.listeners)
                && Objects.equals(fullListeners, instaNode.fullListeners)
                && Objects.equals(results, instaNode.results)
                && Objects.equals(fullResults, instaNode.fullResults);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listeners, fullListeners, results, fullResults);
    }

    public @NotNull List<@NotNull Listener> getListeners() {
        return listeners;
    }

    public @NotNull List<@NotNull Listener> getFullListeners() {
        return fullListeners;
    }

    public @NotNull Set<@NotNull InstaSuccess> getResults() {
        return results;
    }

    public @NotNull Set<@NotNull InstaSuccess> getFullResults() {
        return fullResults;
    }
}
