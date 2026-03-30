package alphaparse.trampoline;

import alphaparse.functions.Listener;
import alphaparse.parser.combinator.Combinator;
import alphaparse.result.success.InstaSuccess;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record TrampolineListenerNode(@NotNull List<Listener> listeners,
                                     @NotNull List<Listener> fullListeners,
                                     @NotNull Set<InstaSuccess> results,
                                     @NotNull Set<InstaSuccess> fullResults) {
    public TrampolineListenerNode() {
        this(new ArrayList<>(), new ArrayList<>(), new HashSet<>(), new HashSet<>());
    }

    public record TrampolineListenerKey(int index, @NotNull Combinator parser) {
    }
}
