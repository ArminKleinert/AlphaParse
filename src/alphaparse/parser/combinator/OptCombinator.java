package alphaparse.parser.combinator;

import alphaparse.Keyword;
import alphaparse.Gll;
import alphaparse.trampoline.TrampolineListenerNodeKey;
import alphaparse.trampoline.InstaTramp;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.InstaFailureReasonOptional;
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
        final @NotNull TrampolineListenerNodeKey nodeKeyForOpt = new TrampolineListenerNodeKey(index, this);
        Gll.pushListener(
                tramp, new TrampolineListenerNodeKey(index, combinator),
                GllParserListeners.nodeListener(nodeKeyForOpt, tramp)
        );
        Gll.success(tramp, nodeKeyForOpt, null, index);
    }

    @Override
    public void fullParse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull Combinator parser = getParser();
        final @NotNull TrampolineListenerNodeKey thisNodeKey = new TrampolineListenerNodeKey(index, this);
        Gll.pushFullListener(tramp, new TrampolineListenerNodeKey(index, parser), GllParserListeners.nodeListener(thisNodeKey, tramp));
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
