package alphaparse.parser.combinator;

import alphaparse.Gll;
import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;
import alphaparse.trampoline.InstaTramp;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.InstaFailureReasonString;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

public final class StringCaseInsensitiveTerminal extends CombinatorStringTerminal {
    public StringCaseInsensitiveTerminal(final @NotNull String string) {
        super(string);
    }

    public StringCaseInsensitiveTerminal(final @NotNull String string,
                                         final boolean hide,
                                         final @NotNull ReductionType red) {
        super(hide, red, string);
    }

    @Override
    public void parse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull String string = getString();
        final @NotNull String text = tramp.getText();
        final int end = Integer.min(text.length(), index + string.length());
        @NotNull CharSequence head = Gll.subSequence(text, index, end);
        head = (head instanceof String) ? (String) head : head.toString();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, this);
        if (string.equalsIgnoreCase((String) head)) {
            Gll.success(tramp, nodeKey, string, end);
        } else {
            Gll.fail(tramp, nodeKey, index, new InstaFailureReasonString(string));
        }
    }

    @Override
    public void fullParse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull var string = getString();
        final @NotNull var text = tramp.getText();
        final var end = Integer.min(text.length(), index + string.length());
        final @NotNull var head = Gll.subSequence(text, index, end);
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, this);
        if (end == text.length() && string.equalsIgnoreCase(head.toString()))
            Gll.success(tramp, nodeKey, string, end);
        else
            Gll.fail(tramp, nodeKey, index, new InstaFailureReasonString(string, true));
    }

    @Override
    public @NotNull StringCaseInsensitiveTerminal withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new StringCaseInsensitiveTerminal(getString(), hide1, this.getReduction());
    }

    @Override
    public @NotNull StringCaseInsensitiveTerminal withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new StringCaseInsensitiveTerminal(getString(), isHidden(), red1);
    }
}