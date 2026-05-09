package alphaparse.result.failure.failureReason;

import org.jetbrains.annotations.NotNull;

/**
 * This class represents the failure to match an optional production. This is often not a problem, but is included in a failure's description for completeness.
 *
 * @see alphaparse.parser.OptionalCombinator
 */
public final class ParseFailureReasonOptional extends ParseFailureReason {
    private final @NotNull String expecting;

    /**
     * Creates a new instance.
     *
     * @param expecting The optionally expected production name.
     * @param full      Whether the entire string was supposed to be covered by the combinator.
     */
    public ParseFailureReasonOptional(final @NotNull String expecting, final boolean full) {
        super(full);
        this.expecting = expecting;
    }

    /**
     * Creates a new instance.
     *
     * @param expecting The optionally expected production name.
     */
    public ParseFailureReasonOptional(final @NotNull String expecting) {
        this(expecting, false);
    }

    @Override
    public @NotNull String getTag() {
        return "optional";
    }

    @Override
    public @NotNull String getExpecting() {
        return expecting;
    }
}
