package instarun.parser.combinator;

import instarun.Gll;
import instarun.trampoline.InstaNodeKey;
import instarun.trampoline.InstaTramp;
import instarun.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class AlternationCombinator extends CombinatorWithManyParsers {
    public AlternationCombinator(final @NotNull List<Combinator> parsers) {
        super(parsers);
    }

    public AlternationCombinator(final @NotNull List<Combinator> parsers,
                                 final boolean hide,
                                 final @NotNull ReductionType red) {
        super(parsers, hide, red);
    }

    @Override
    public void parse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull List<Combinator> parsers = getParsers();
        for (Combinator combinator : parsers) {
            Gll.pushListener(
                    tramp,
                    new InstaNodeKey(index, combinator),
                    GllParserListeners.nodeListener(new InstaNodeKey(index, this), tramp)
            );
        }
    }

    @Override
    public void fullParse(final int index, final @NotNull InstaTramp tramp) {
        for (final @NotNull Combinator parser : getParsers()) {
            Gll.pushFullListener(
                    tramp,
                    new InstaNodeKey(index, parser),
                    GllParserListeners.nodeListener(new InstaNodeKey(index, this), tramp)
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
