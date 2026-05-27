package alphaparse.parsing;

import alphaparse.functions.Listener;
import alphaparse.parsing.combinator_factory.CombinatorFactory;
import alphaparse.reduction.ReductionType;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This class is an alternative to the {@link ChoiceCombinator}.
 * <p>
 * It represents the ABNF choice operator {@code (p1 / p2)} (where p1 and p2 are instances of {@link Combinator})
 * and should work like the PEG extension which makes it "ordered".
 * <p>
 * As of now, it does not work right,
 * so it can be considered a worse alternative to the {@link ChoiceCombinator}.
 */
public final class OrderedChoiceCombinator extends CombinatorWithManyParsers {
    private final @NotNull Combinator parser1;
    private final @NotNull Combinator parser2;

    private OrderedChoiceCombinator(boolean hide, @NotNull ReductionType red,
                                    @NotNull Combinator parser1,
                                    @NotNull Combinator parser2) {
        super(hide, red, List.of(parser1, parser2));
        this.parser1 = parser1;
        this.parser2 = parser2;
    }

    private OrderedChoiceCombinator(final @NotNull List<Combinator> parsers,
                                    final boolean hide,
                                    final @NotNull ReductionType red) {
        this(hide, red, setupParsers(parsers).parser1, setupParsers(parsers).parser2);
    }

    private OrderedChoiceCombinator(final @NotNull Combinator parser1,
                                    final @NotNull Combinator parser2) {
        this(defaultHidden, ReductionType.standardInitialReduction(), parser1, parser2);
    }

    /**
     * Creates a new instance from a list of {@link Combinator} objects. Instead of using this directly, use methods from {@link CombinatorFactory}.
     *
     * @param parsers The choices.
     * @see CombinatorFactory#orderedChoiceCombinator(List)
     */
    public OrderedChoiceCombinator(final @NotNull List<Combinator> parsers) {
        this(setupParsers(parsers).parser1, setupParsers(parsers).parser2);
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
    public @NotNull OrderedChoiceCombinator withParsers(final @NotNull List<@NotNull Combinator> parsers) {
        return new OrderedChoiceCombinator(parsers, isHidden(), getReduction());
    }
}
