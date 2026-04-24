package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonString;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents string terminals (both case-sensitive or case-insensitive).
 * <br/>
 * Syntax: can be written with double quotes or single quotes, so {@code "..."} and {@code '...'} are equivalent.
 */
public final class TerminalStringCombinator extends CombinatorTerminal {
    private final @NotNull String string;
    private final boolean caseInsensitive;

    private TerminalStringCombinator(final boolean hide,
                                     final @NotNull ReductionType red,
                                     final @NotNull String string,
                                     final boolean caseInsensitive) {
        super(hide, red);
        this.string = string;
        this.caseInsensitive = caseInsensitive;
    }

    /**
     * Creates a new instance.
     *
     * @param string          The string to match.
     * @param caseInsensitive True if the casing doesn't matter, false if it does matter.
     */
    public TerminalStringCombinator(final @NotNull String string, final boolean caseInsensitive) {
        super();
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
     * Returns the string.
     *
     * @return The string.
     */
    public @NotNull String getString() {
        return string;
    }

    /**
     * True if the casing doesn't matter, false if it does matter.
     *
     * @return True if the casing doesn't matter, false if it does matter.
     */
    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    @Override
    public @NotNull TerminalStringCombinator withHideTag(final boolean hide) {
        return isHidden() == hide ? this : new TerminalStringCombinator(hide, red, string, caseInsensitive);
    }

    @Override
    public @NotNull TerminalStringCombinator withReduction(final @NotNull ReductionType red) {
        return getReduction() == red ? this : new TerminalStringCombinator(hide, red, string, caseInsensitive);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TerminalStringCombinator that)) return false;
        if (this == that) return true;
        return hide == that.hide
                && Objects.equals(red, that.red)
                && Objects.equals(string, that.getString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(hide, red, string);
    }
}
