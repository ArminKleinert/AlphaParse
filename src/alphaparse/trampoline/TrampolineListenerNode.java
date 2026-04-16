package alphaparse.trampoline;

import alphaparse.functions.Listener;
import alphaparse.parser.combinator.Combinator;
import alphaparse.result.success.AlphaParseSuccess;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public record TrampolineListenerNode(@NotNull List<Listener> listeners,
                                     @NotNull List<Listener> fullListeners,
                                     @NotNull SequencedSet<AlphaParseSuccess> results,
                                     @NotNull SequencedSet<AlphaParseSuccess> fullResults) {
    public TrampolineListenerNode() {
        this(new ArrayList<>(), new ArrayList<>(), new LinkedHashSet<>(), new LinkedHashSet<>());
    }

    public record TrampolineListenerKey(int index, @NotNull Combinator parser) {
    }
}
