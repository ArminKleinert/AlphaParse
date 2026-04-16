package alphaparse.parser.combinator;

import alphaparse.Gll;
import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;
import alphaparse.trampoline.Tramp;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class AlternationCombinator extends CombinatorWithManyParsers {
    public AlternationCombinator(final @NotNull List<Combinator> parsers) {
        super(parsers);
    }

    private AlternationCombinator(final @NotNull List<Combinator> parsers,
                                 final boolean hide,
                                 final @NotNull ReductionType red) {
        super(parsers, hide, red);
    }

    @Override
    public void parse(final int index, final @NotNull Tramp tramp) {
        for (final @NotNull Combinator combinator : getParsers()) {
            Gll.pushListener(
                    tramp,
                    new TrampolineListenerKey(index, combinator),
                    GllParserListeners.nodeListener(new TrampolineListenerKey(index, this), tramp)
            );
        }
    }

    @Override
    public void fullParse(final int index, final @NotNull Tramp tramp) {
        for (final @NotNull Combinator parser : getParsers()) {
            Gll.pushFullListener(
                    tramp,
                    new TrampolineListenerKey(index, parser),
                    GllParserListeners.nodeListener(new TrampolineListenerKey(index, this), tramp)
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
