package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonString;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * TODO
 */
public final class StringTerminal extends CombinatorTerminal {
    private long bufferedHashCode = Long.MIN_VALUE;
    private final @NotNull String string;
    private final boolean caseInsensitive;

    /**
     * TODO
     *
     * @param string          TODO
     * @param caseInsensitive TODO
     */
    public StringTerminal(final @NotNull String string, final boolean caseInsensitive) {
        super();
        this.string = string;
        this.caseInsensitive = caseInsensitive;
    }

    private StringTerminal(final boolean hide, final @NotNull ReductionType red, final @NotNull String string, final boolean caseInsensitive) {
        super(hide, red);
        this.string = string;
        this.caseInsensitive = caseInsensitive;
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull String string = getString();
        final @NotNull String text = runner.tramp().getText();
        final int end = Integer.min(text.length(), index + string.length());
        final @NotNull String head = text.substring(index, end);

        final @NotNull TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, this);
        if (caseInsensitive) {
            if (string.equalsIgnoreCase(head))
                runner.success(nodeKey, string, end);
            else
                runner.fail(nodeKey, index, new ParseFailureReasonString(string));
        } else {
            if (string.contentEquals(head))
                runner.success(nodeKey, string, end);
            else
                runner.fail(nodeKey, index, new ParseFailureReasonString(string));
        }
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull var string = getString();
        final @NotNull var text = runner.tramp().getText();
        final var end = Integer.min(text.length(), string.length() + index);
        final @NotNull var head = text.substring(index, end);
        final @NotNull TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, this);
        if (caseInsensitive) {
            if (end == text.length() && string.equalsIgnoreCase(head))
                runner.success(nodeKey, string, end);
            else
                runner.fail(nodeKey, index, new ParseFailureReasonString(string, true));
        } else {
            if (text.length() == end && Objects.equals(string, head))
                runner.success(nodeKey, string, end);
            else
                runner.fail(nodeKey, index, new ParseFailureReasonString(string, true));
        }
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public @NotNull String getString() {
        return string;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    @Override
    public boolean equals(Object o) {
        if (!getClass().equals(o.getClass())) return false;
        if (hashCode() != o.hashCode()) return false;
        final @NotNull var that = (StringTerminal) o;
        if (caseInsensitive != that.caseInsensitive) return false;
        if (!Objects.equals(getReduction(), that.getReduction())) return false;
        if (!Objects.equals(isHidden(), that.isHidden())) return false;
        return Objects.equals(getString(), that.getString());
    }

    @Override
    public int hashCode() {
        if (bufferedHashCode == Long.MIN_VALUE)
            bufferedHashCode = Objects.hash(getClass(), getReduction(), isHidden(), getString());
        return (int) bufferedHashCode;
    }

    @Override
    public @NotNull StringTerminal withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new StringTerminal(hide1, this.getReduction(), getString(), caseInsensitive);
    }

    @Override
    public @NotNull StringTerminal withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new StringTerminal(isHidden(), red1, getString(), caseInsensitive);
    }
}
