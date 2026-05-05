package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import alphaparse.parser.Combinator;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the failure to match a production in a {@link alphaparse.parser.RepetitionCombinator}.
 *
 * @see alphaparse.parser.RepetitionCombinator
 */
public final class ParseFailureReasonExpectRepetitionParser extends ParseFailureReason {
    private final @NotNull Combinator p;

    /**
     * Creates a new instance.
     *
     * @param full Whether the entire string was supposed to be covered by the combinator.
     * @param p    The combinator that was expected.
     */
    public ParseFailureReasonExpectRepetitionParser(final boolean full, final @NotNull Combinator p) {
        super(full);
        this.p = p;
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("rep");
    }

    @Override
    public @NotNull Combinator getExpecting() {
        return p;
    }
}