package alphaparse.result.failure.failureReason;

import org.jetbrains.annotations.NotNull;

public record AlphaFailureReasonCharRange(int lo, int hi) {
    @Override
    public @NotNull String toString() {
        if (lo == hi)
            return String.format("%%x%04x", lo);
        return String.format("%%x%04x-%04x", lo, hi);
    }
}
