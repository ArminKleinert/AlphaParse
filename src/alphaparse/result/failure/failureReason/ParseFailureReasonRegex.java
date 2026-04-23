package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * TODO
 */
public final class ParseFailureReasonRegex extends ParseFailureReason {
    private final @NotNull Pattern expecting;

    /**
     * TODO
     *
     * @param expecting TODO
     * @param full      TODO
     */
    public ParseFailureReasonRegex(final @NotNull Pattern expecting, final boolean full) {
        super(full);
        this.expecting = expecting;
    }

    /**
     * TODO
     *
     * @param expecting TODO
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
