package alphaparse.trampoline;

import alphaparse.functions.Listener;
import org.jetbrains.annotations.NotNull;

public record TrampolineMsgCacheKey(int index, @NotNull Listener listener) {
}
