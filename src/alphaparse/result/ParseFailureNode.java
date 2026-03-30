package alphaparse.result;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ParseFailureNode implements AlphaParseResult {
    private final @NotNull String text;
    private final int start;
    private final int end;
    private final @NotNull Keyword key;

    public ParseFailureNode(final @NotNull String text, final @NotNull Keyword key, final int start, final int end) {
        this.text = text;
        this.key = key;
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ParseFailureNode that)) return false;
        return getStart() == that.getStart()
                && getEnd() == that.getEnd()
                && Objects.equals(getText(), that.getText())
                && Objects.equals(getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getText(), getStart(), getEnd(), getKey());
    }

    public @NotNull String getText() {
        return text;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public @NotNull Keyword getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "[" + key + ", could not parse \"" + text + "\"]";
    }
}
