package alphaparse.parser.combinator;

import alphaparse.Keyword;
import alphaparse.Gll;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.trampoline.Tramp;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonLookahead;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
public final class LookaheadCombinator extends CombinatorWithParser {
    /**
     * TODO
     *
     * @param parser TODO
     */
    public LookaheadCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    private LookaheadCombinator(final @NotNull Combinator parser, final boolean hide, final @NotNull ReductionType red) {
        super(parser, hide, red);
    }

    /**
     * TODO
     *
     * @param index TODO
     * @param tramp TODO
     */
    @Override
    public void parse(final int index, final @NotNull Tramp tramp) {
        final @NotNull Combinator combinator = getParser();
        Gll.pushListener(tramp, new TrampolineListenerKey(index, combinator),
                GllParserListeners.lookListener(new TrampolineListenerKey(index, this), tramp));
    }

    /**
     * TODO
     *
     * @param index TODO
     * @param tramp TODO
     */
    @Override
    public void fullParse(final int index, final @NotNull Tramp tramp) {
        if (index == tramp.getText().length()) {
            parse(index, tramp);
        } else {
            Gll.fail(
                    tramp,
                    new TrampolineListenerKey(index, this),
                    index,
                    new ParseFailureReasonLookahead(Keyword.intern("end-of-string")));
        }
    }

    @Override
    public @NotNull LookaheadCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new LookaheadCombinator(getParser(), hide1, getReduction());
    }

    @Override
    public @NotNull LookaheadCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new LookaheadCombinator(getParser(), isHidden(), red1);
    }

    @Override
    public @NotNull LookaheadCombinator withParser(final @NotNull Combinator parser) {
        return new LookaheadCombinator(parser, isHidden(), getReduction());
    }
}
