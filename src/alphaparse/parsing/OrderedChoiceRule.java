package alphaparse.parsing;

import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This class is an alternative to the {@link AlternationRule}.
 * <p>
 * It represents the ABNF choice operator {@code (p1 / p2)} (where p1 and p2 are instances of {@link Rule})
 * and should work like the PEG extension which makes it "ordered".
 * <p>
 * As of now, it does not work right,
 * so it can be considered a worse alternative to the {@link AlternationRule}.
 */
public final class OrderedChoiceRule extends RuleWithManyChildren {
    private final @NotNull Rule parser1;
    private final @NotNull Rule parser2;

    private OrderedChoiceRule(final boolean hide, final @NotNull ReductionType red,
                              final @NotNull Rule parser1,
                              final @NotNull Rule parser2) {
        super(hide, red, List.of(parser1, parser2));
        this.parser1 = parser1;
        this.parser2 = parser2;
    }

    private OrderedChoiceRule(final @NotNull List<Rule> parsers,
                              final boolean hide,
                              final @NotNull ReductionType red) {
        this(hide, red, setupParsers(parsers).parser1, setupParsers(parsers).parser2);
    }

    private OrderedChoiceRule(final @NotNull Rule parser1,
                              final @NotNull Rule parser2) {
        this(defaultHidden, ReductionType.standardInitialReduction(), parser1, parser2);
    }

    /**
     * Creates a new instance from a list of {@link Rule} objects.
     *
     * @param parsers The choices.
     */
    public OrderedChoiceRule(final @NotNull List<Rule> parsers) {
        this(setupParsers(parsers).parser1, setupParsers(parsers).parser2);
    }

    private static @NotNull OrderedChoiceRule setupParsers(
            final @NotNull List<@NotNull Rule> parsers) {
        if (parsers.size() < 2)
            throw new IllegalArgumentException();

        if (parsers.size() == 2)
            return new OrderedChoiceRule(parsers.get(0), parsers.get(1));

        var restParsers = parsers.subList(1, parsers.size());
        return new OrderedChoiceRule(parsers.getFirst(), setupParsers(restParsers));
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Rule rule1 = parser1;
        final @NotNull Rule rule2 = parser2;
        final @NotNull TrampolineListenerKey nodeKeyForComb1 =
                new TrampolineListenerKey(index, rule1);
        final @NotNull TrampolineListenerKey nodeKeyForComb2 =
                new TrampolineListenerKey(index, rule2);
        final @NotNull Listener listener =
                runner.nodeListener(new TrampolineListenerKey(index, this));
        runner.pushListener(nodeKeyForComb1, listener);
        runner.pushNegativeListener(nodeKeyForComb1, () -> runner.pushListener(nodeKeyForComb2, listener));
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Rule rule1 = parser1;
        final @NotNull Rule rule2 = parser2;
        final @NotNull TrampolineListenerKey nodeKeyForComb1 =
                new TrampolineListenerKey(index, rule1);
        final @NotNull TrampolineListenerKey nodeKeyForComb2 =
                new TrampolineListenerKey(index, rule2);
        final @NotNull Listener listener =
                runner.nodeListener(new TrampolineListenerKey(index, this));
        runner.pushFullListener(nodeKeyForComb1, listener);
        runner.pushNegativeListener(nodeKeyForComb1, () -> runner.pushFullListener(nodeKeyForComb2, listener));
    }

    @Override
    public @NotNull OrderedChoiceRule withHideTag(final boolean hide) {
        return isHidden() == hide ? this : new OrderedChoiceRule(getParsers(), hide, this.getReduction());
    }

    @Override
    public @NotNull OrderedChoiceRule withReduction(final @NotNull ReductionType red) {
        return getReduction() == red ? this : new OrderedChoiceRule(getParsers(), isHidden(), red);
    }

    @Override
    public @NotNull OrderedChoiceRule withParsers(final @NotNull List<@NotNull Rule> parsers) {
        return new OrderedChoiceRule(parsers, isHidden(), getReduction());
    }
}
