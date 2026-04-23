package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
public final class ParseFailureReasonEpsilon extends ParseFailureReason {
    private final @NotNull Keyword expecting;

    /**
     * TODO
     *
     * @param expecting TODO
     * @param full      TODO
     */
    public ParseFailureReasonEpsilon(final @NotNull Keyword expecting, final boolean full) {
        super(full);
        this.expecting = expecting;
    }

    /**
     * TODO
     */
    public ParseFailureReasonEpsilon() {
        this(Keyword.intern("end-of-string"), false);
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("epsilon");
    }

    @Override
    public @NotNull Keyword getExpecting() {
        return expecting;
    }
}
