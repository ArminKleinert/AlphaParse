package alphaparse.parser.combinator;

import alphaparse.Gll;
import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.Tramp;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * TODO
 */
public final class OrderedCombinator extends CombinatorWithManyParsers {
    private final @NotNull Combinator parser1;
    private final @NotNull Combinator parser2;

    /**
     * TODO
     *
     * @param parsers TODO
     */
    public OrderedCombinator(final @NotNull List<Combinator> parsers) {
        this(setupParsers(parsers).parser1, setupParsers(parsers).parser2);
    }

    private OrderedCombinator(final @NotNull List<Combinator> parsers,
                              final boolean hide,
                              final @NotNull ReductionType red) {
        this(setupParsers(parsers).parser1, setupParsers(parsers).parser2, hide, red);
    }

    private OrderedCombinator(final @NotNull Combinator parser1,
                              final @NotNull Combinator parser2) {
        super(List.of(parser1, parser2));
        this.parser1 = parser1;
        this.parser2 = parser2;
    }

    private OrderedCombinator(final @NotNull Combinator parser1,
                              final @NotNull Combinator parser2,
                              final boolean hide,
                              final @NotNull ReductionType red) {
        super(List.of(parser1, parser2), hide, red);
        this.parser1 = parser1;
        this.parser2 = parser2;
    }

    private static @NotNull OrderedCombinator setupParsers(
            final @NotNull List<@NotNull Combinator> parsers) {
        if (parsers.size() < 2)
            throw new IllegalArgumentException();

        if (parsers.size() == 2)
            return new OrderedCombinator(parsers.get(0), parsers.get(1));

        var restParsers = parsers.subList(1, parsers.size());
        return new OrderedCombinator(parsers.getFirst(), setupParsers(restParsers));
    }

    @Override
    public void parse(final int index, final @NotNull Tramp tramp) {
        final @NotNull Combinator combinator1 = parser1;
        final @NotNull Combinator combinator2 = parser2;
        final @NotNull TrampolineListenerKey nodeKeyForComb1 =
                new TrampolineListenerKey(index, combinator1);
        final @NotNull TrampolineListenerKey nodeKeyForComb2 =
                new TrampolineListenerKey(index, combinator2);
        final @NotNull Listener listener =
                GllParserListeners.nodeListener(new TrampolineListenerKey(index, this), tramp);
        Gll.pushListener(tramp, nodeKeyForComb1, listener);
        Gll.pushNegativeListener(tramp, nodeKeyForComb1, () -> Gll.pushListener(tramp, nodeKeyForComb2, listener));
    }

    @Override
    public void fullParse(final int index, final @NotNull Tramp tramp) {
        final @NotNull Combinator combinator1 = parser1;
        final @NotNull Combinator combinator2 = parser2;
        final @NotNull TrampolineListenerKey nodeKeyForComb1 =
                new TrampolineListenerKey(index, combinator1);
        final @NotNull TrampolineListenerKey nodeKeyForComb2 =
                new TrampolineListenerKey(index, combinator2);
        final @NotNull Listener listener =
                GllParserListeners.nodeListener(new TrampolineListenerKey(index, this), tramp);
        Gll.pushFullListener(tramp, nodeKeyForComb1, listener);
        Gll.pushNegativeListener(tramp, nodeKeyForComb1, () -> Gll.pushFullListener(tramp, nodeKeyForComb2, listener));
    }

    @Override
    public @NotNull OrderedCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new OrderedCombinator(getParsers(), hide1, this.getReduction());
    }

    @Override
    public @NotNull OrderedCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new OrderedCombinator(getParsers(), isHidden(), red1);
    }

    @Override
    public @NotNull OrderedCombinator withParsers(final @NotNull List<Combinator> parsers) {
        return new OrderedCombinator(parsers, isHidden(), getReduction());
    }
}