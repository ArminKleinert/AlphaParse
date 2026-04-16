package alphaparse.parser.combinator;

import alphaparse.Gll;
import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;
import alphaparse.trampoline.Tramp;
import alphaparse.flat.AutoFlattenSeq;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

public final class StarCombinator extends CombinatorWithParser {
    public StarCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    private StarCombinator(final @NotNull Combinator parser, final boolean hide, final @NotNull ReductionType red) {
        super(parser, hide, red);
    }

    @Override
    public void parse(final int index, final @NotNull Tramp tramp) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForStar = new TrampolineListenerKey(index, this);
        Gll.pushListener(
                tramp, new TrampolineListenerKey(index, combinator),
                GllParserListeners.plusListener(AutoFlattenSeq.make(), combinator, index, nodeKeyForStar, tramp)
        );
        Gll.success(tramp, nodeKeyForStar, null, index);
    }

    @Override
    public void fullParse(final int index, final @NotNull Tramp tramp) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForStar = new TrampolineListenerKey(index, this);
        if (index == tramp.getText().length()) {
            Gll.success(tramp, nodeKeyForStar, null, index);
        } else {
            Gll.pushListener(
                    tramp, new TrampolineListenerKey(index, combinator),
                    GllParserListeners.plusFullListener(AutoFlattenSeq.make(), combinator, index, nodeKeyForStar, tramp));
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
