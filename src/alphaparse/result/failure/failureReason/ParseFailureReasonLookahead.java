package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

public final class ParseFailureReasonLookahead extends ParseFailureReason {
    private final @NotNull Keyword expecting;

    public ParseFailureReasonLookahead(final @NotNull Keyword expecting, final boolean full) {
        super(full);this.expecting = expecting;
    }

    public ParseFailureReasonLookahead(final @NotNull Keyword expecting) {
        this(expecting, false);
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("look");
    }

    @Override
    public @NotNull Keyword getExpecting() {return expecting;}
}
