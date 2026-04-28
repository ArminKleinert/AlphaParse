package alphaparse.result;

import alphaparse.IO2;
import alphaparse.result.failure.FailureUtil;
import alphaparse.result.failure.failureReason.ParseFailureReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * TODO
 * @param index TODO
 * @param reason TODO
 * @param line TODO
 * @param column TODO
 * @param text TODO
 */
public record AlphaParseFailure(int index,
                                @NotNull List<ParseFailureReason> reason,
                                int line,
                                int column,
                                @Nullable String text)
        implements  AlphaParseResult {
    /**
     * TODO
     * @param index TODO
     * @param reason TODO
     */
    public AlphaParseFailure(final int index, final @NotNull List<ParseFailureReason> reason) {
        this(index, reason, -1, -1, null);
    }

    /**
     * TODO
     * @return TODO
     */
    public @NotNull List<ParseFailureReason> getReasonList() {
        return reason;
    }

    @Override
    public @NotNull String toString() {
        return FailureUtil.pprintFailure(this);
    }

    /**
     * TODO
     * @return TODO
     */
    public @NotNull String contentsToString() {
        return "[" + index + ", " + reason + ", " + line + ", " + column + ", " + text + "]";
    }

    /**
     * TODO
     * @param failIndex TODO
     * @param failColumn TODO
     * @param failLine TODO
     * @param failText TODO
     * @param failReasonList TODO
     * @return TODO
     */
    public @NotNull String checkCorrectness(
            final int failIndex,
            final int failColumn,
            final int failLine,
            final String failText,
            final @NotNull List<ParseFailureReason> failReasonList) {
        final @NotNull StringBuilder sb;
        sb = new StringBuilder();
        sb.append("Attribute: Index  Column Line   Text   List \n");
        sb.append(String.format(
                "Correct?   %-5s  %-5s  %-5s  %-5s  %-5s",
                index() == failIndex,
                column() == failColumn,
                line() == failLine,
                Objects.equals(text(), failText),
                Objects.equals(getReasonList(), failReasonList)
        ));
        return sb.toString();
    }

    /**
     *  TODO
     * @return TODO
     */
    public @NotNull Object getResult() {
        throw new UnsupportedOperationException();
    }
}
