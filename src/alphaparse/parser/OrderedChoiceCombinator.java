package alphaparse.parser;

import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *  TODO
 * @param hide TODO
 * @param red TODO
 * @param parser1 TODO
 * @param parser2 TODO
 */
public record OrderedChoiceCombinator(
        boolean hide,
        @NotNull ReductionType red,
        @NotNull Combinator parser1,
        @NotNull Combinator parser2) implements CombinatorWithManyParsers {
    /**
     *  TODO
     * @param parsers TODO
     */
    public OrderedChoiceCombinator(final @NotNull List<Combinator> parsers) {
        this(setupParsers(parsers).parser1, setupParsers(parsers).parser2);
    }

    private OrderedChoiceCombinator(final @NotNull List<Combinator> parsers,
                                    final boolean hide,
                                    final @NotNull ReductionType red) {
        this(hide, red, setupParsers(parsers).parser1, setupParsers(parsers).parser2);
    }

    private OrderedChoiceCombinator(final @NotNull Combinator parser1,
                                    final @NotNull Combinator parser2) {
        this(defaultHidden, defaultRed, parser1, parser2);
    }

    private static @NotNull OrderedChoiceCombinator setupParsers(
            final @NotNull List<@NotNull Combinator> parsers) {
        if (parsers.size() < 2)
            throw new IllegalArgumentException();

        if (parsers.size() == 2)
            return new OrderedChoiceCombinator(parsers.get(0), parsers.get(1));

        var restParsers = parsers.subList(1, parsers.size());
        return new OrderedChoiceCombinator(parsers.getFirst(), setupParsers(restParsers));
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator1 = parser1;
        final @NotNull Combinator combinator2 = parser2;
        final @NotNull TrampolineListenerKey nodeKeyForComb1 =
                new TrampolineListenerKey(index, combinator1);
        final @NotNull TrampolineListenerKey nodeKeyForComb2 =
                new TrampolineListenerKey(index, combinator2);
        final @NotNull Listener listener =
                runner.nodeListener(new TrampolineListenerKey(index, this));
        runner.pushListener(nodeKeyForComb1, listener);
        runner.pushNegativeListener(nodeKeyForComb1, () -> runner.pushListener(nodeKeyForComb2, listener));
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator1 = parser1;
        final @NotNull Combinator combinator2 = parser2;
        final @NotNull TrampolineListenerKey nodeKeyForComb1 =
                new TrampolineListenerKey(index, combinator1);
        final @NotNull TrampolineListenerKey nodeKeyForComb2 =
                new TrampolineListenerKey(index, combinator2);
        final @NotNull Listener listener =
                runner.nodeListener(new TrampolineListenerKey(index, this));
        runner.pushFullListener(nodeKeyForComb1, listener);
        runner.pushNegativeListener(nodeKeyForComb1, () -> runner.pushFullListener(nodeKeyForComb2, listener));
    }

    @Override
    public @NotNull OrderedChoiceCombinator withHideTag(final boolean hide) {
        return isHidden() == hide ? this : new OrderedChoiceCombinator(getParsers(), hide, this.getReduction());
    }

    @Override
    public @NotNull OrderedChoiceCombinator withReduction(final @NotNull ReductionType red) {
        return getReduction() == red ? this : new OrderedChoiceCombinator(getParsers(), isHidden(), red);
    }

    @Override
    public @NotNull OrderedChoiceCombinator withParsers(final @NotNull List<Combinator> parsers) {
        return new OrderedChoiceCombinator(parsers, isHidden(), getReduction());
    }

    @Override
    public @NotNull List<Combinator> parsers() {
        return List.of(parser1, parser2);
    }
}
