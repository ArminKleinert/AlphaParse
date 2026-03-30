package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import alphaparse.Print;
import alphaparse.parser.combinator.Combinator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class InstaFailureReasonNegative extends InstaFailureReason {
    private final @Nullable Combinator expecting;

    public InstaFailureReasonNegative(final @Nullable Combinator expecting, final boolean full) {
        super(full);
        this.expecting = expecting;
    }

    public InstaFailureReasonNegative(final @Nullable Combinator expecting) {
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

    public String failureReasonString() {
        return "{NOT " + (expecting == null ? "null" : Print.combinatorsToString(expecting)) + "}";
    }
}
