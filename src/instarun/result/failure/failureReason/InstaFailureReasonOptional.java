package instarun.result.failure.failureReason;

import instarun.Keyword;
import org.jetbrains.annotations.NotNull;

public final class InstaFailureReasonOptional extends InstaFailureReason {
    private final @NotNull Keyword expecting;

    public InstaFailureReasonOptional(final @NotNull Keyword expecting, final boolean full) {
        super(full);this.expecting = expecting;
    }

    public InstaFailureReasonOptional(final @NotNull Keyword expecting) {
        this(expecting, false);
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("optional");
    }

    @Override
    public @NotNull Keyword getExpecting() {return expecting;}
}
