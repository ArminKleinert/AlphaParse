package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
public final class ParseFailureReasonString extends ParseFailureReason {
    private final @NotNull String expecting;

    /**
     * TODO
     *
     * @param expecting TODO
     * @param full      TODO
     */
    public ParseFailureReasonString(final @NotNull String expecting, final boolean full) {
        super(full);
        this.expecting = expecting;
    }

    /**
     * TODO
     *
     * @param expecting TODO
     */
    public ParseFailureReasonString(final @NotNull String expecting) {
        this(expecting, false);
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("string");
    }

    @Override
    public @NotNull String getExpecting() {
        return expecting;
    }
}
