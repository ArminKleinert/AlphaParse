package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * TODO
 */
public final class AlternationCombinator extends CombinatorWithManyParsers {
    /**
     * TODO
     *
     * @param parsers TODO
     */
    public AlternationCombinator(final @NotNull List<Combinator> parsers) {
        super(parsers);
    }

    private AlternationCombinator(final @NotNull List<Combinator> parsers,
                                  final boolean hide,
                                  final @NotNull ReductionType red) {
        super(parsers, hide, red);
    }

    /**
     * TODO
     *  @param index TODO
     *
     * @param runner TODO
     */
    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        for (final @NotNull Combinator combinator : getParsers()) {
            runner.pushListener(
                    new TrampolineListenerKey(index, combinator),
                    runner.nodeListener(new TrampolineListenerKey(index, this))
            );
        }
    }

    /**
     * TODO
     *  @param index TODO
     *
     * @param runner TODO
     */
    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        for (final @NotNull Combinator parser : getParsers()) {
            runner.pushFullListener(
                    new TrampolineListenerKey(index, parser),
                    runner.nodeListener(new TrampolineListenerKey(index, this))
            );
        }
    }

    @Override
    public @NotNull AlternationCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new AlternationCombinator(getParsers(), hide1, this.getReduction());
    }

    @Override
    public @NotNull AlternationCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new AlternationCombinator(getParsers(), isHidden(), red1);
    }

    @Override
    public @NotNull AlternationCombinator withParsers(final @NotNull List<Combinator> parsers) {
        return new AlternationCombinator(parsers, isHidden(), getReduction());
    }
}
