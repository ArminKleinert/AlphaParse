package alphaparse.trampoline;

import alphaparse.functions.Listener;
import alphaparse.parser.combinator.Combinator;
import alphaparse.result.success.AlphaParseSuccess;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * TODO
 *
 * @param listeners     TODO
 * @param fullListeners TODO
 * @param results       TODO
 * @param fullResults   TODO
 */
public record TrampolineListenerNode(@NotNull List<Listener> listeners,
                                     @NotNull List<Listener> fullListeners,
                                     @NotNull SequencedSet<AlphaParseSuccess> results,
                                     @NotNull SequencedSet<AlphaParseSuccess> fullResults) {
    /**
     * TODO
     */
    public TrampolineListenerNode() {
        this(new ArrayList<>(), new ArrayList<>(), new LinkedHashSet<>(), new LinkedHashSet<>());
    }

    /**
     * TODO
     *
     * @param index  TODO
     * @param parser TODO
     */
    public record TrampolineListenerKey(int index, @NotNull Combinator parser) {
    }
}
