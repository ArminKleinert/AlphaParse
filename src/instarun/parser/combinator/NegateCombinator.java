package instarun.parser.combinator;

import instarun.Gll;
import instarun.reduction.ReductionType;
import instarun.result.failure.failureReason.InstaFailureReasonNegative;
import instarun.trampoline.InstaNodeKey;
import instarun.trampoline.InstaTramp;
import org.jetbrains.annotations.NotNull;

public final class NegateCombinator extends CombinatorWithParser {
    public NegateCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    public NegateCombinator(final @NotNull Combinator parser, final boolean hide, final @NotNull ReductionType red) {
        super(parser, hide, red);
    }

    @Override
    public void parse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull InstaNodeKey nodeKey = new InstaNodeKey(index, combinator);

        if (Gll.resultExists_Q(tramp, nodeKey)) {
            Gll.fail(tramp, new InstaNodeKey(index, this), index, new InstaFailureReasonNegative(null));
            return;
        }

//        final @NotNull Delay failSend = new Delay(() -> Gll.fail(
//                tramp, new InstaNodeKey(index, this), index,
//                new InstaFailureReasonNegative(combinator)));
//        Gll.pushListener(tramp, nodeKey, ignored -> failSend.execute());
        Gll.pushListener(tramp, nodeKey, ignored -> Gll.fail(
                tramp, new InstaNodeKey(index, this), index,
                new InstaFailureReasonNegative(combinator)));

        final @NotNull Combinator p = this;
        Gll.pushNegativeListener(tramp, nodeKey, () -> {
            if (!Gll.resultExists_Q(tramp, nodeKey)) {
                Gll.success(tramp, new InstaNodeKey(index, p), null, index);
            }
        });
    }

    @Override
    public void fullParse(final int index, final @NotNull InstaTramp tramp) {
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
