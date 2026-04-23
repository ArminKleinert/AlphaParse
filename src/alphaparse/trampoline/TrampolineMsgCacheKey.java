package alphaparse.trampoline;

import alphaparse.functions.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 *
 * @param index    TODO
 * @param listener TODO
 */
public record TrampolineMsgCacheKey(int index, @NotNull Listener listener) {
}
