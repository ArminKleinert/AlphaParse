package alphaparse.trampoline;

import alphaparse.functions.Listener;
import alphaparse.parser.combinator.Combinator;
import alphaparse.result.success.AlphaParseSuccess;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record TrampolineListenerNode(@NotNull List<Listener> listeners,
                                     @NotNull List<Listener> fullListeners,
                                     @NotNull Set<AlphaParseSuccess> results,
                                     @NotNull Set<AlphaParseSuccess> fullResults) {
    public TrampolineListenerNode() {
        this(new ArrayList<>(), new ArrayList<>(), new HashSet<>(), new HashSet<>());
    }

    public record TrampolineListenerKey(int index, @NotNull Combinator parser) {
    }
}
