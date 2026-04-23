package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * TODO
 */
public abstract class ParseFailureReason {
    private final boolean full;

    /**
     * TODO
     *
     * @param full TODO
     */
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

    /**
     * TODO
     *
     * @return TODO
     */
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

    /**
     * TODO
     *
     * @return TODO
     */
    public abstract @NotNull Keyword getTag();

    /**
     * TODO
     *
     * @return TODO
     */
    public abstract @Nullable Object getExpecting();

    /**
     * TODO
     *
     * @return TODO
     */
    public boolean isFull() {
        return full;
    }
}
