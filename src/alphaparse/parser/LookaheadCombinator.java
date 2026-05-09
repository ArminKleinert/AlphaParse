package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonLookahead;
import org.jetbrains.annotations.NotNull;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

/**
 * This class represents the ABNF "lookahead" operator {@code &p} (where p is an instance of {@link Combinator}).
 */
public final class LookaheadCombinator extends CombinatorWithParser {
    private LookaheadCombinator(final boolean hide, final @NotNull ReductionType red, final @NotNull Combinator parser) {
        super(hide, red, parser);
    }

    /**
     * Creates a new instance.  Instead of using this directly, use methods from {@link CombinatorFactory}.
     *
     * @param parser The inner parser.
     * @see CombinatorFactory#makeLookahead(Combinator)
     */
    public LookaheadCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull var nodeKey = new TrampolineListenerKey(index, this);
        runner.pushListener(new TrampolineListenerKey(index, combinator),
                ignored -> runner.success(nodeKey, null, index));
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        if (index == runner.tramp().getText().length()) {
            parse(index, runner);
        } else {
            runner.fail(
                    new TrampolineListenerKey(index, this),
                    index,
                    new ParseFailureReasonLookahead("end-of-string"));
        }
    }

    @Override
    public @NotNull LookaheadCombinator withHideTag(final boolean hide) {
        return isHidden() == hide ? this : new LookaheadCombinator(hide, red, parser);
    }

    @Override
    public @NotNull LookaheadCombinator withReduction(final @NotNull ReductionType red) {
        return getReduction() == red ? this : new LookaheadCombinator(hide, red, parser);
    }

    @Override
    public @NotNull LookaheadCombinator withParser(final @NotNull Combinator parser) {
        return new LookaheadCombinator(hide, red, parser);
    }
}
