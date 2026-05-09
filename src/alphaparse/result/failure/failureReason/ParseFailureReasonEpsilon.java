package alphaparse.result.failure.failureReason;

import org.jetbrains.annotations.NotNull;

/**
 * Failure reasonList for when end of string was expected, but there was more text remaining.
 *
 * @see alphaparse.parser.EpsilonCombinator
 */
public final class ParseFailureReasonEpsilon extends ParseFailureReason {

    /**
     * The constructor.
     *
     * @param full Whether the entire string was supposed to be covered by the epsilon production.
     */
    public ParseFailureReasonEpsilon(final boolean full) {
        super(full);
    }

    /**
     * Creates a new default instance.
     */
    public ParseFailureReasonEpsilon() {
        this(false);
    }

    @Override
    public @NotNull String getTag() {
        return "epsilon";
    }

    @Override
    public @NotNull String getExpecting() {
        return "end-of-string";
    }
}
