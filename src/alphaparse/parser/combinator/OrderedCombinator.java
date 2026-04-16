package alphaparse.parser.combinator;

import alphaparse.Gll;
import alphaparse.reduction.ReductionType;
import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;
import alphaparse.trampoline.Tramp;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class OrderedCombinator extends CombinatorWithManyParsers {
    public OrderedCombinator(final @NotNull List<Combinator> parsers) {
        super(parsers);
    }

    private OrderedCombinator(final @NotNull List<Combinator> parsers,
                              final boolean hide,
                              final @NotNull ReductionType red) {
        super(parsers, hide, red);
    }

    @Override
    public void parse(final int index, final @NotNull Tramp tramp) {
//        final @NotNull Combinator combinator1 = getParser1();
//        final @NotNull Combinator combinator2 = getParser2();
//        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForComb1 = new TrampolineListenerKey(index, combinator1);
//        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForComb2 = new TrampolineListenerKey(index, combinator2);
//        final @NotNull Listener listener = GllParserListeners.nodeListener(new TrampolineListenerKey(index, this), tramp);
//        Gll.pushListener(tramp, nodeKeyForComb1, listener);
//        Gll.pushNegativeListener(tramp, nodeKeyForComb1, () -> Gll.pushListener(tramp, nodeKeyForComb2, listener));
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
//        final @NotNull Combinator combinator1 = getParser1();
//        final @NotNull Combinator combinator2 = getParser2();
//        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForComb1 = new TrampolineListenerKey(index, combinator1);
//        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForComb2 = new TrampolineListenerKey(index, combinator2);
//        final @NotNull Listener listener = GllParserListeners.nodeListener(new TrampolineListenerKey(index, this), tramp);
//        Gll.pushFullListener(tramp, nodeKeyForComb1, listener);
//        Gll.pushNegativeListener(tramp, nodeKeyForComb1, () -> Gll.pushFullListener(tramp, nodeKeyForComb2, listener));
        for (final @NotNull Combinator parser : getParsers()) {
            Gll.pushFullListener(
                    tramp,
                    new TrampolineListenerKey(index, parser),
                    GllParserListeners.nodeListener(new TrampolineListenerKey(index, this), tramp)
            );
        }
    }

    @Override
    public @NotNull OrderedCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new OrderedCombinator(getParsers(), hide1, this.getReduction());
    }

    @Override
    public @NotNull OrderedCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new OrderedCombinator(getParsers(), isHidden(), red1);
    }

    public @NotNull OrderedCombinator withParsers(final @NotNull List<Combinator> parsers) {
        return new OrderedCombinator(parsers, isHidden(), getReduction());
    }
}