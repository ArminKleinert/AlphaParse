package alphaparse.parser.combinator;

import alphaparse.Gll;
import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;
import alphaparse.trampoline.Tramp;
import alphaparse.flat.AutoFlattenSeq;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

public final class PlusCombinator extends CombinatorWithParser {
    public PlusCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    public PlusCombinator(final @NotNull Combinator parser, final boolean hide, final @NotNull ReductionType red) {
        super(parser, hide, red);
    }

    @Override
    public void parse(final int index, final @NotNull Tramp tramp) {
        final @NotNull Combinator parser = getParser();
        Gll.pushListener(
                tramp, new TrampolineListenerKey(index, parser),
                GllParserListeners.plusListener(AutoFlattenSeq.make(), parser, index, new TrampolineListenerKey(index, this), tramp)
        );
    }

    @Override
    public void fullParse(final int index, final @NotNull Tramp tramp) {
        final @NotNull Combinator parser = getParser();
        Gll.pushListener(
                tramp, new TrampolineListenerKey(index, parser),
                GllParserListeners.plusFullListener(AutoFlattenSeq.make(), parser, index, new TrampolineListenerKey(index, this), tramp)
        );
    }

    @Override
    public @NotNull PlusCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new PlusCombinator(getParser(), hide1, this.getReduction());
    }

    @Override
    public @NotNull PlusCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new PlusCombinator(getParser(), isHidden(), red1);
    }

    @Override
    public @NotNull PlusCombinator withParser(final @NotNull Combinator parser) {
        return new PlusCombinator(parser, isHidden(), getReduction());
    }
}
