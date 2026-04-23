package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
public final class ParseFailureReasonOptional extends ParseFailureReason {
    private final @NotNull Keyword expecting;

    /**
     * TODO
     *
     * @param expecting TODO
     * @param full      TODO
     */
    public ParseFailureReasonOptional(final @NotNull Keyword expecting, final boolean full) {
        super(full);
        this.expecting = expecting;
    }

    /**
     * TODO
     *
     * @param expecting TODO
     */
    public ParseFailureReasonOptional(final @NotNull Keyword expecting) {
        this(expecting, false);
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("optional");
    }

    @Override
    public @NotNull Keyword getExpecting() {
        return expecting;
    }
}
