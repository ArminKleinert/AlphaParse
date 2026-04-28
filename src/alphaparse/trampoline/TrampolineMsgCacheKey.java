package alphaparse.trampoline;

import alphaparse.functions.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * A key for the trampoline's message cache.
 *
 * @param index    The index.
 * @param listener The listener.
 */
public record TrampolineMsgCacheKey(int index, @NotNull Listener listener) {
}
