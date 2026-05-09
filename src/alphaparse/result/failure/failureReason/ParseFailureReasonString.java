package alphaparse.result.failure.failureReason;

import org.jetbrains.annotations.NotNull;

/**
 * This class represents the failure to match a string.
 *
 * @see alphaparse.parser.TerminalStringCombinator
 */
public final class ParseFailureReasonString extends ParseFailureReason {
    private final @NotNull String expecting;

    /**
     * Creates a new instance.
     *
     * @param expecting The string that was expected.
     * @param full      Whether the entire string was supposed to be covered by the combinator.
     */
    public ParseFailureReasonString(final @NotNull String expecting, final boolean full) {
        super(full);
        this.expecting = expecting;
    }

    /**
     * Creates a new instance.
     *
     * @param expecting The string that was expected.
     */
    public ParseFailureReasonString(final @NotNull String expecting) {
        this(expecting, false);
    }

    @Override
    public @NotNull String getTag() {
        return "string";
    }

    @Override
    public @NotNull String getExpecting() {
        return expecting;
    }
}
