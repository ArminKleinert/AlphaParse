package instarun.result.failure.failureReason;

import instarun.Keyword;
import org.jetbrains.annotations.NotNull;

public final class InstaFailureReasonEpsilon extends InstaFailureReason {
    private final @NotNull Keyword expecting;

    public InstaFailureReasonEpsilon(final @NotNull Keyword expecting, final boolean full) {
        super(full);this.expecting = expecting;
    }

    public InstaFailureReasonEpsilon() {
        this(Keyword.intern("end-of-string"), false);
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("epsilon");
    }

    @Override
    public @NotNull Keyword getExpecting() {return expecting;}
}
