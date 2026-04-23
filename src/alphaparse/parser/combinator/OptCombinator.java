package alphaparse.parser.combinator;

import alphaparse.Keyword;
import alphaparse.Gll;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.trampoline.Tramp;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonOptional;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
public final class OptCombinator extends CombinatorWithParser {
    /**
     * TODO
     *
     * @param parser TODO
     */
    public OptCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    private OptCombinator(final @NotNull Combinator parser, final boolean hide, final @NotNull ReductionType red) {
        super(parser, hide, red);
    }

    @Override
    public void parse(final int index, final @NotNull Tramp tramp) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForOpt = new TrampolineListenerKey(index, this);
        Gll.pushListener(
                tramp, new TrampolineListenerKey(index, combinator),
                GllParserListeners.nodeListener(nodeKeyForOpt, tramp)
        );
        Gll.success(tramp, nodeKeyForOpt, null, index);
    }

    @Override
    public void fullParse(final int index, final @NotNull Tramp tramp) {
        final @NotNull Combinator parser = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey thisNodeKey = new TrampolineListenerKey(index, this);
        Gll.pushFullListener(tramp, new TrampolineListenerKey(index, parser), GllParserListeners.nodeListener(thisNodeKey, tramp));
        if (index == tramp.getText().length()) {
            Gll.success(tramp, thisNodeKey, null, index);
        } else {
            Gll.fail(tramp, thisNodeKey, index, new ParseFailureReasonOptional(Keyword.intern("end-of-string")));
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
