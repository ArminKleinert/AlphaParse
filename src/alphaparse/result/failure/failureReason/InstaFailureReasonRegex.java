package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public final class InstaFailureReasonRegex extends InstaFailureReason {
    private final @NotNull Pattern expecting;

    public InstaFailureReasonRegex(final @NotNull Pattern expecting, final boolean full) {
        super(full);
        this.expecting = expecting;
    }

    public InstaFailureReasonRegex(final @NotNull Pattern expecting) {
        this(expecting, false);
    }

    @Override
    public @NotNull Pattern getExpecting() {
        return expecting;
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("regex");
    }
}
