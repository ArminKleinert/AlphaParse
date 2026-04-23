package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonNegative;
import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
public final class NegateCombinator extends CombinatorWithParser {
    /**
     * TODO
     * @param parser TODO
     */
    public NegateCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    private NegateCombinator(final @NotNull Combinator parser, final boolean hide, final @NotNull ReductionType red) {
        super(parser, hide, red);
    }

    /**
     * TODO
     *
     * @param nodeKey TODO
     * @return TODO
     */
    public boolean resultExists_Q(
            final @NotNull Gll runner,
            final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey) {
        final TrampolineListenerNode node = runner.tramp().getNode(nodeKey);

        if (node == null)
            return false;

        return !node.fullResults().isEmpty() || !node.results().isEmpty();
    }

    /**
     * TODO
     *
     * @param index  TODO
     * @param runner TODO
     */
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

    /**
     * TODO
     *
     * @param index  TODO
     * @param runner TODO
     */
    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        parse(index, runner);
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
