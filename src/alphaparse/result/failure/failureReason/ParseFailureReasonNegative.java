package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import alphaparse.Print;
import alphaparse.parser.combinator.Combinator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * TODO
 */
public final class ParseFailureReasonNegative extends ParseFailureReason {
    private final @Nullable Combinator expecting;

    /**
     * TODO
     *
     * @param expecting TODO
     * @param full      TODO
     */
    public ParseFailureReasonNegative(final @Nullable Combinator expecting, final boolean full) {
        super(full);
        this.expecting = expecting;
    }

    /**
     * TODO
     *
     * @param expecting TODO
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
     * TODO
     *
     * @return TODO
     */
    public String failureReasonString() {
        return "{NOT " + (expecting == null ? "null" : Print.combinatorsToString(expecting)) + "}";
    }
}
