package alphaparse.parser.combinator;

import alphaparse.Gll;
import alphaparse.trampoline.TrampolineListenerNodeKey;
import alphaparse.trampoline.InstaTramp;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.InstaFailureReasonString;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class StringTerminal extends CombinatorStringTerminal {
    public StringTerminal(final @NotNull String string) {
        super(string);
    }

    public StringTerminal(final @NotNull String string, final boolean hide, final @NotNull ReductionType red) {
        super(hide, red, string);
    }

    @Override
    public void parse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull String string = getString();
        final @NotNull String text = tramp.getText();
        final int end = Integer.min(text.length(), index + string.length());
        final @NotNull CharSequence head = Gll.subSequence(text, index, end);
        if (string.contentEquals(head)) {
            Gll.success(tramp, new TrampolineListenerNodeKey(index, this), string, end);
        } else {
            Gll.fail(tramp, new TrampolineListenerNodeKey(index, this), index, new InstaFailureReasonString(string));
        }
    }

    @Override
    public void fullParse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull String string = getString();
        final @NotNull String text = tramp.getText();
        final int end = Integer.min(text.length(), string.length() + index);
        final @NotNull CharSequence head = Gll.subSequence(text, index, end);
        final @NotNull TrampolineListenerNodeKey nodeKey = new TrampolineListenerNodeKey(index, this);
        if (text.length() == end && Objects.equals(string, head.toString())) {
            Gll.success(tramp, nodeKey, string, end);
        } else {
            Gll.fail(tramp, nodeKey, index, new InstaFailureReasonString(string, true));
        }
    }

    @Override
    public @NotNull StringTerminal withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new StringTerminal(getString(), hide1, this.getReduction());
    }

    @Override
    public @NotNull StringTerminal withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new StringTerminal(getString(), isHidden(), red1);
    }
}