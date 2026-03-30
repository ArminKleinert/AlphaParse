package alphaparse.parser.combinator;

import alphaparse.Gll;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonNegative;
import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;
import alphaparse.trampoline.Tramp;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

public final class NegateCombinator extends CombinatorWithParser {
    public NegateCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    public NegateCombinator(final @NotNull Combinator parser, final boolean hide, final @NotNull ReductionType red) {
        super(parser, hide, red);
    }

    @Override
    public void parse(final int index, final @NotNull Tramp tramp) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, combinator);

        if (Gll.resultExists_Q(tramp, nodeKey)) {
            Gll.fail(tramp, new TrampolineListenerKey(index, this), index, new ParseFailureReasonNegative(null));
            return;
        }

        Gll.pushListener(tramp, nodeKey, ignored -> Gll.fail(
                tramp, new TrampolineListenerKey(index, this), index,
                new ParseFailureReasonNegative(combinator)));

        final @NotNull Combinator p = this;
        Gll.pushNegativeListener(tramp, nodeKey, () -> {
            if (!Gll.resultExists_Q(tramp, nodeKey)) {
                Gll.success(tramp, new TrampolineListenerKey(index, p), null, index);
            }
        });
    }

    @Override
    public void fullParse(final int index, final @NotNull Tramp tramp) {
        parse(index, tramp);
    }

    @Override
    public @NotNull NegateCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new NegateCombinator(getParser(), hide1, this.getReduction());
    }

    @Override
    public @NotNull NegateCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new NegateCombinator(getParser(), isHidden(), red1);
    }

    @Override
    public @NotNull NegateCombinator withParser(final @NotNull Combinator parser) {
        return new NegateCombinator(parser, isHidden(), getReduction());
    }
}
