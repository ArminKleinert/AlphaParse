package instarun.result.failure.failureReason;

import instarun.Keyword;
import org.jetbrains.annotations.NotNull;

public final class InstaFailureReasonString extends InstaFailureReason {
    private final @NotNull String expecting;

    public InstaFailureReasonString(final @NotNull String expecting, final boolean full) {
        super(full);this.expecting = expecting;
    }

    public InstaFailureReasonString(final @NotNull String expecting) {
        this(expecting, false);
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("string");
    }

    @Override
    public @NotNull String getExpecting() {return expecting;}
}
