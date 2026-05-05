package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import alphaparse.Print;
import alphaparse.parser.Combinator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents the failure to NOT match a production in a {@link alphaparse.parser.NegativeLookaheadCombinator}.
 *
 * @see alphaparse.parser.NegativeLookaheadCombinator
 */
public final class ParseFailureReasonNegative extends ParseFailureReason {
    private final @Nullable Combinator expecting;

    /**
     * Creates a new instance.
     *
     * @param expecting The Combinator that was found when it was supposed to not be found.
     * @param full      Whether the entire string was supposed to be covered by the combinator.
     */
    public ParseFailureReasonNegative(final @Nullable Combinator expecting, final boolean full) {
        super(full);
        this.expecting = expecting;
    }

    /**
     * Creates a new instance.
     *
     * @param expecting The Combinator that was found when it was supposed to not be found.
     */
    public ParseFailureReasonNegative(final @Nullable Combinator expecting) {
        this(expecting, false);
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("negative-look");
    }

    @Override
    public @Nullable Combinator getExpecting() {
        return expecting;
    }

    /**
     * A special method to create a string to represent the instance. This makes especially clear that it was supposed to be a negative lookahead.
     *
     * @return A string to represent the instance.
     */
    public String failureReasonString() {
        return "{NOT " + (expecting == null ? "null" : Print.combinatorToString(expecting)) + "}";
    }
}
