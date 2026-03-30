package alphaparse.result;

import alphaparse.result.failure.FailureUtil;
import alphaparse.result.failure.failureReason.InstaFailureReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class AlphaFailure implements InstaIntermediateResult, AlphaParseResult {
    private final int index;
    private final @NotNull List<InstaFailureReason> reason;
    private final int line;
    private final int column;
    private final @Nullable String text;
    private final @NotNull Object result;

    public AlphaFailure(final int index, final @NotNull List<InstaFailureReason> reason) {
        this(index, reason, -1, -1, null);
    }

    public AlphaFailure(final int index, final @NotNull List<InstaFailureReason> reason,
                        final int line, final int column, final @Nullable String text) {
        this(index, reason, line, column, text, List.of());
    }

    public AlphaFailure(final int index, final @NotNull List<InstaFailureReason> reason,
                        final int line, final int column, final @Nullable String text,
                        final @NotNull Object result) {
        this.index = index;
        this.reason = reason;
        this.line = line;
        this.column = column;
        this.text = text;
        this.result = result;
    }

    public @NotNull List<InstaFailureReason> getReasonList() {
        return reason;
    }

    @Override
    public String toString() {
        return FailureUtil.pprintFailure(this);
    }

    public String contentsToString() {
        return "[" + index + ", " + reason + ", " + line + ", " + column + ", " + text + ", " + result + "]";
    }

    public String checkCorrectness(
            final int failIndex,
            final int failColumn,
            final int failLine,
            final String failText,
            final @NotNull List<InstaFailureReason> failReasonList) {
        final @NotNull StringBuilder sb;
        sb = new StringBuilder();
        sb.append("Attribute: Index  Column Line   Text   List \n");
        sb.append(String.format(
                "Correct?   %-5s  %-5s  %-5s  %-5s  %-5s",
                getIndex() == failIndex,
                getColumn() == failColumn,
                getLine() == failLine,
                Objects.equals(getText(), failText),
                Objects.equals(getReasonList(), failReasonList)
        ));
        return sb.toString();
    }

    public int getIndex() {
        return index;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public @Nullable String getText() {
        return text;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AlphaFailure that = (AlphaFailure) o;
        return index == that.index
                && line == that.line
                && column == that.column
                && Objects.equals(reason, that.reason)
                && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, reason, line, column, text);
    }

    public @NotNull Object getResult() {
        throw new UnsupportedOperationException();
        //return result;
    }
}
