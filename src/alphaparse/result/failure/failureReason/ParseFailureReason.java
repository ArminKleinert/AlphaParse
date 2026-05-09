package alphaparse.result.failure.failureReason;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A failure lists reasons for the failure. This class represents the possible reasons.
 */
public abstract class ParseFailureReason {
    private final boolean full;

    /**
     * Create a new instance.
     *
     * @param full Whether the production that failed covered the entire input from beginning to end. When showing the object as a string, this adds the note "(followed by end of string)" or something similar.
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
     * Representation of the failure reasonList as a string.
     *
     * @return A representation of the expected production as a string.
     */
    public String failureReasonString() {
        return Objects.toString(getExpecting());
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final @NotNull ParseFailureReason that = (ParseFailureReason) o;
        return full == that.isFull() &&
                Objects.equals(Objects.toString(getExpecting()), Objects.toString(that.getExpecting()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getExpecting(), full);
    }

    /**
     * The tag of the production.
     *
     * @return The tag of the production.
     */
    public abstract @NotNull String getTag();

    /**
     * The expected object. This may be a string, a regex, a combinator or anything else.
     *
     * @return The expected object.
     */
    public abstract @Nullable Object getExpecting();

    /**
     * Whether the production that failed covered the entire input from beginning to end. When showing the object as a string, this adds the note "(followed by end of string)" or something similar.
     *
     * @return Whether the production that failed covered the entire input from beginning to end. When showing the object as a string, this adds the note "(followed by end of string)" or something similar.
     */
    public boolean isFull() {
        return full;
    }
}
