package alphaparse.parser;

import alphaparse.Keyword;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

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
     *  @param index TODO
     *
     * @param runner TODO
     */
    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull var nodeKey = new TrampolineListenerKey(index, this);
        runner.pushListener(new TrampolineListenerKey(index, combinator),
                ignored -> runner.success(nodeKey, null, index));
    }

    /**
     * TODO
     *  @param index TODO
     *
     * @param runner TODO
     */
    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        if (index == runner.tramp().getText().length()) {
            parse(index, runner);
        } else {
            runner.fail(
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
