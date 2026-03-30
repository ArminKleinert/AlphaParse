package instarun.parser.combinator;

import instarun.Keyword;
import instarun.Gll;
import instarun.trampoline.InstaNodeKey;
import instarun.trampoline.InstaTramp;
import instarun.reduction.ReductionType;
import instarun.result.failure.failureReason.InstaFailureReasonOptional;
import org.jetbrains.annotations.NotNull;

public final class OptCombinator extends CombinatorWithParser {
    public OptCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    public OptCombinator(final @NotNull Combinator parser, final boolean hide, final @NotNull ReductionType red) {
        super(parser, hide, red);
    }

    @Override
    public void parse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull InstaNodeKey nodeKeyForOpt = new InstaNodeKey(index, this);
        Gll.pushListener(
                tramp, new InstaNodeKey(index, combinator),
                GllParserListeners.nodeListener(nodeKeyForOpt, tramp)
        );
        Gll.success(tramp, nodeKeyForOpt, null, index);
    }

    @Override
    public void fullParse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull Combinator parser = getParser();
        final @NotNull InstaNodeKey thisNodeKey = new InstaNodeKey(index, this);
        Gll.pushFullListener(tramp, new InstaNodeKey(index, parser), GllParserListeners.nodeListener(thisNodeKey, tramp));
        if (index == tramp.getText().length()) {
            Gll.success(tramp, thisNodeKey, null, index);
        } else {
            Gll.fail(tramp, thisNodeKey, index, new InstaFailureReasonOptional(Keyword.intern("end-of-string")));
        }
    }

    @Override
    public @NotNull OptCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new OptCombinator(getParser(), hide1, this.getReduction());
    }

    @Override
    public @NotNull OptCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new OptCombinator(getParser(), isHidden(), red1);
    }

    @Override
    public @NotNull OptCombinator withParser(final @NotNull Combinator parser) {
        return new OptCombinator(parser, isHidden(), getReduction());
    }
}
