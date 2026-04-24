package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonString;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record TerminalStringCombinator(
        boolean hide,
        @NotNull ReductionType red,
        @NotNull String string,
        boolean caseInsensitive) implements CombinatorTerminal {

    public TerminalStringCombinator(final @NotNull String string, final boolean caseInsensitive) {
        this(defaultHidden, defaultRed, string, caseInsensitive);
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

    public @NotNull String getString() {
        return string;
    }

    public boolean isCaseInsensitive() {
        return caseInsensitive;
    }

    @Override
    public @NotNull TerminalStringCombinator withHideTag(boolean hide) {
        return isHidden() == hide ? this : new TerminalStringCombinator(hide, red, string, caseInsensitive);
    }

    @Override
    public @NotNull TerminalStringCombinator withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new TerminalStringCombinator(hide, red, string, caseInsensitive);
    }
}
