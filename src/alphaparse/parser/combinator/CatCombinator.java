package alphaparse.parser.combinator;

import alphaparse.Gll;
import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;
import alphaparse.trampoline.Tramp;
import alphaparse.flat.AutoFlattenSeq;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CatCombinator extends CombinatorWithManyParsers {
    public CatCombinator(final @NotNull List<@NotNull Combinator> parsers) {
        super(parsers);
    }

    public CatCombinator(final @NotNull List<@NotNull Combinator> parsers, final boolean hide, final @NotNull ReductionType red) {
        super(parsers, hide, red);
    }

    @Override
    public void parse(final int index, final @NotNull Tramp tramp) {
        final @NotNull List<@NotNull Combinator> parsers = getParsers();
        Gll.pushListener(
                tramp, new TrampolineListenerKey(index, parsers.getFirst()),
                GllParserListeners.catListener(AutoFlattenSeq.make(), parsers.subList(1, parsers.size()), new TrampolineListenerKey(index, this), tramp));
    }

    @Override
    public void fullParse(final int index, final @NotNull Tramp tramp) {
        final @NotNull List<@NotNull Combinator> parsers = getParsers();
        Gll.pushListener(
                tramp, new TrampolineListenerKey(index, parsers.getFirst()),
                GllParserListeners.catFullListener(AutoFlattenSeq.make(), parsers.subList(1, parsers.size()), new TrampolineListenerKey(index, this), tramp));
    }

    @Override
    public @NotNull CatCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new CatCombinator(getParsers(), hide1, this.getReduction());
    }

    @Override
    public @NotNull CatCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new CatCombinator(getParsers(), isHidden(), red1);
    }

    @Override
    public @NotNull CatCombinator withParsers(final @NotNull List<Combinator> parsers) {
        return new CatCombinator(parsers, isHidden(), getReduction());
    }
}
