package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonNegative;
import org.jetbrains.annotations.NotNull;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.trampoline.TrampolineListenerNode;

import java.util.Objects;

/**
 * TODO
 */
public final class NegativeLookaheadCombinator extends CombinatorWithParser {
    private NegativeLookaheadCombinator(final boolean hide, final @NotNull ReductionType red, final @NotNull Combinator parser) {
        super(hide, red, parser);
    }

    /**
     * TODO
     *
     * @param parser TODO
     */
    public NegativeLookaheadCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    private boolean resultExists_Q(
            final @NotNull Gll runner,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        final TrampolineListenerNode node = runner.tramp().getNode(nodeKey);

        if (node == null)
            return false;

        return !node.fullResults().isEmpty() || !node.results().isEmpty();
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, combinator);

        if (resultExists_Q(runner, nodeKey)) {
            runner.fail(new TrampolineListenerKey(index, this), index, new ParseFailureReasonNegative(null));
            return;
        }

        runner.pushListener(nodeKey, ignored -> runner.fail(
                new TrampolineListenerKey(index, this), index,
                new ParseFailureReasonNegative(combinator)));

        final @NotNull Combinator p = this;
        runner.pushNegativeListener(nodeKey, () -> {
            if (!resultExists_Q(runner, nodeKey)) {
                runner.success(new TrampolineListenerKey(index, p), null, index);
            }
        });
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        parse(index, runner);
    }

    @Override
    public @NotNull NegativeLookaheadCombinator withParser(final @NotNull Combinator parser) {
        return new NegativeLookaheadCombinator(hide, red, parser);
    }

    @Override
    public @NotNull NegativeLookaheadCombinator withHideTag(boolean hide) {
        return isHidden() == hide ? this : new NegativeLookaheadCombinator(hide, red, parser);
    }

    @Override
    public @NotNull NegativeLookaheadCombinator withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new NegativeLookaheadCombinator(hide, red, parser);
    }

//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof NegativeLookaheadCombinator that)) return false;
//        if (this==that ) return true;
//        return hide() == that.hide() && Objects.equals(red(), that.red()) && Objects.equals(parser(),that.parser());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(hide(), red(),parser());
//    }
}
