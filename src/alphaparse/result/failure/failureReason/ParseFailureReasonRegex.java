package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * This class represents the failure to match a regex.
 *
 * @see alphaparse.parser.TerminalRegexpCombinator
 */
public final class ParseFailureReasonRegex extends ParseFailureReason {
    private final @NotNull Pattern expecting;

    /**
     * Creates a new instance.
     *
     * @param expecting The regex that was expected.
     * @param full      Whether the entire string was supposed to be covered by the combinator.
     */
    public ParseFailureReasonRegex(final @NotNull Pattern expecting, final boolean full) {
        super(full);
        this.expecting = expecting;
    }

    /**
     * Creates a new instance.
     *
     * @param expecting The regex that was expected.
     */
    public ParseFailureReasonRegex(final @NotNull Pattern expecting) {
        this(expecting, false);
    }

    @Override
    public @NotNull Pattern getExpecting() {
        return expecting;
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("regex");
    }
}
