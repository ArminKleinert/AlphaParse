package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class ParseFailureReason {
    private final boolean full;

    protected ParseFailureReason(final boolean full) {
        this.full = full;
    }

    @Override
    public String toString() {
        return "{" +
                "tag=" + getTag() +
                ", expecting=" + failureReasonString() +
                ", full=" + full +
                '}';
    }

    public String failureReasonString() {
        return Objects.toString(getExpecting());
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final @NotNull ParseFailureReason that = (ParseFailureReason) o;
        return full == that.isFull() &&
                Objects.equals(getTag(), that.getTag()) &&
                Objects.equals(Objects.toString(getExpecting()), Objects.toString(that.getExpecting()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTag(), getExpecting(), full);
    }

    public abstract @NotNull Keyword getTag();

    public abstract @Nullable Object getExpecting();

    public boolean isFull() {
        return full;
    }
}
