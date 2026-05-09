package alphaparse.result.failure.failureReason;

import org.jetbrains.annotations.NotNull;

/**
 * This class represents the failure to match a production in a {@link alphaparse.parser.LookaheadCombinator}.
 *
 * @see alphaparse.parser.LookaheadCombinator
 */
public final class ParseFailureReasonLookahead extends ParseFailureReason {
    private final @NotNull String expecting;

    /**
     * Creates a new instance.
     *
     * @param expecting The production that was looked for.
     * @param full      Whether the entire string was supposed to be covered by the combinator.
     */
    public ParseFailureReasonLookahead(final @NotNull String expecting, final boolean full) {
        super(full);
        this.expecting = expecting;
    }

    /**
     * Creates a new instance.
     *
     * @param expecting The production that was looked for.
     */
    public ParseFailureReasonLookahead(final @NotNull String expecting) {
        this(expecting, false);
    }

    @Override
    public @NotNull String getTag() {
        return "look";
    }

    @Override
    public @NotNull String getExpecting() {
        return expecting;
    }
}
