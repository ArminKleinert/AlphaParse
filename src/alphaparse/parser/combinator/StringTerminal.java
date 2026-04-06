package alphaparse.parser.combinator;

import alphaparse.Gll;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.trampoline.Tramp;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonString;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class StringTerminal extends CombinatorStringTerminal {
    public StringTerminal(final @NotNull String string) {
        super(string, false);
    }

    public StringTerminal(final @NotNull String string,
                          final boolean hide,
                          final @NotNull ReductionType red) {
        super(hide, red, string, false);
    }

//    @Override
//    public void parse(final int index, final @NotNull Tramp tramp) {
//        final @NotNull String string = getString();
//        final @NotNull String text = tramp.getText();
//        final int end = Integer.min(text.length(), index + string.length());
//        final @NotNull CharSequence head = Gll.subSequence(text, index, end);
//
//        final @NotNull TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, this);
//        if (string.contentEquals(head)) {
//            Gll.success(tramp, nodeKey, string, end);
//        } else {
//            Gll.fail(tramp, nodeKey, index, new ParseFailureReasonString(string));
//        }
//    }
//
//    @Override
//    public void fullParse(final int index, final @NotNull Tramp tramp) {
//        final @NotNull var string = getString();
//        final @NotNull var text = tramp.getText();
//        final var end = Integer.min(text.length(), string.length() + index);
//        final @NotNull var head = Gll.subSequence(text, index, end);
//        final @NotNull TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, this);
//        if (text.length() == end && Objects.equals(string, head.toString())) {
//            Gll.success(tramp, nodeKey, string, end);
//        } else {
//            Gll.fail(tramp, nodeKey, index, new ParseFailureReasonString(string, true));
//        }
//    }

    @Override
    public @NotNull StringTerminal withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new StringTerminal(getString(), hide1, this.getReduction());
    }

    @Override
    public @NotNull StringTerminal withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new StringTerminal(getString(), isHidden(), red1);
    }
}