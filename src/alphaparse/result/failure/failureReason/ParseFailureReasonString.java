package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

public final class ParseFailureReasonString extends ParseFailureReason {
    private final @NotNull String expecting;

    public ParseFailureReasonString(final @NotNull String expecting, final boolean full) {
        super(full);this.expecting = expecting;
    }

    public ParseFailureReasonString(final @NotNull String expecting) {
        this(expecting, false);
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("string");
    }

    @Override
    public @NotNull String getExpecting() {return expecting;}
}
