package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.flat.AutoFlattenSeq;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
public final class StarCombinator extends CombinatorWithParser {
    /**
     * TODO
     *
     * @param parser TODO
     */
    public StarCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    private StarCombinator(final @NotNull Combinator parser, final boolean hide, final @NotNull ReductionType red) {
        super(parser, hide, red);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForStar = new TrampolineListenerKey(index, this);
        runner.pushListener(
                new TrampolineListenerKey(index, combinator),
                PlusCombinator.plusListener(AutoFlattenSeq.make(), combinator, index, nodeKeyForStar, runner)
        );
        runner.success(nodeKeyForStar, null, index);
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForStar = new TrampolineListenerKey(index, this);
        if (index == runner.tramp().getText().length()) {
            runner.success(nodeKeyForStar, null, index);
        } else {
            runner.pushListener(
                    new TrampolineListenerKey(index, combinator),
                    PlusCombinator.plusFullListener(AutoFlattenSeq.make(), combinator, index, nodeKeyForStar, runner));
        }
    }

    @Override
    public @NotNull StarCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new StarCombinator(getParser(), hide1, this.getReduction());
    }

    @Override
    public @NotNull StarCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new StarCombinator(getParser(), isHidden(), red1);
    }

    @Override
    public @NotNull StarCombinator withParser(final @NotNull Combinator parser) {
        return new StarCombinator(parser, isHidden(), getReduction());
    }
}
