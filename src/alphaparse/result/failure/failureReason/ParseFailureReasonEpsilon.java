package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

/**
 * Failure reason for when end of string was expected, but there was more text remaining.
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
    public @NotNull Keyword getTag() {
        return Keyword.intern("epsilon");
    }

    @Override
    public @NotNull Keyword getExpecting() {
        return Keyword.intern("end-of-string");
    }
}
