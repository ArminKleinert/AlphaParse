package alphaparse.parser;

import alphaparse.Keyword;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonLookahead;
import org.jetbrains.annotations.NotNull;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

public record LookaheadCombinator(
        boolean hide,
        @NotNull ReductionType red,
        @NotNull Combinator parser) implements CombinatorWithParser {

    public LookaheadCombinator(@NotNull Combinator parser) {
        this(defaultHidden, defaultRed, parser);
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
                    new ParseFailureReasonLookahead(Keyword.intern("end-of-string")));
        }
    }

    @Override
    public @NotNull LookaheadCombinator withHideTag(boolean hide) {
        return isHidden() == hide ? this :  new LookaheadCombinator(hide, red, parser);
    }

    @Override
    public @NotNull LookaheadCombinator withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new LookaheadCombinator(hide, red, parser);
    }

    @Override
    public @NotNull LookaheadCombinator withParser(@NotNull Combinator parser) {
        return new LookaheadCombinator(hide, red, parser);
    }
}
