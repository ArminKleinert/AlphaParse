package alphaparse.result;

import alphaparse.result.failure.FailureUtil;
import alphaparse.result.failure.failureReason.ParseFailureReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 *
 * @param index
 * @param reason
 * @param line
 * @param column
 * @param text
 */
public record AlphaParseFailure(int index,
                                @NotNull List<ParseFailureReason> reason,
                                int line,
                                int column,
                                @Nullable String text)
        implements AlphaIntermediateResult, AlphaParseResult {

    public AlphaParseFailure(final int index, final @NotNull List<ParseFailureReason> reason) {
        this(index, reason, -1, -1, null);
    }

    public @NotNull List<ParseFailureReason> getReasonList() {
        return reason;
    }

    @Override
    public @NotNull String toString() {
        return FailureUtil.pprintFailure(this);
    }

    public @NotNull String contentsToString() {
        return "[" + index + ", " + reason + ", " + line + ", " + column + ", " + text + "]";
    }

    /**
     *
     * @param failIndex
     * @param failColumn
     * @param failLine
     * @param failText
     * @param failReasonList
     * @return
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

    public @NotNull Object getResult() {
        throw new UnsupportedOperationException();
    }
}
