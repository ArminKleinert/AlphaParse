package alphaparse.parser;

import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CombinatorOrderedChoice(
        boolean hide,
        @NotNull ReductionType red,
        @NotNull Combinator parser1,
        @NotNull Combinator parser2) implements CombinatorWithManyParsers {

    public CombinatorOrderedChoice(final @NotNull List<Combinator> parsers) {
        this(setupParsers(parsers).parser1, setupParsers(parsers).parser2);
    }

    private CombinatorOrderedChoice(final @NotNull List<Combinator> parsers,
                                    final boolean hide,
                                    final @NotNull ReductionType red) {
        this(hide, red, setupParsers(parsers).parser1, setupParsers(parsers).parser2);
    }

    private CombinatorOrderedChoice(final @NotNull Combinator parser1,
                                    final @NotNull Combinator parser2) {
        this(defaultHidden, defaultRed, parser1, parser2);
    }

    private static @NotNull CombinatorOrderedChoice setupParsers(
            final @NotNull List<@NotNull Combinator> parsers) {
        if (parsers.size() < 2)
            throw new IllegalArgumentException();

        if (parsers.size() == 2)
            return new CombinatorOrderedChoice(parsers.get(0), parsers.get(1));

        var restParsers = parsers.subList(1, parsers.size());
        return new CombinatorOrderedChoice(parsers.getFirst(), setupParsers(restParsers));
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
    public @NotNull CombinatorOrderedChoice withHideTag(final boolean hide) {
        return isHidden() == hide ? this : new CombinatorOrderedChoice(getParsers(), hide, this.getReduction());
    }

    @Override
    public @NotNull CombinatorOrderedChoice withReduction(final @NotNull ReductionType red) {
        return getReduction() == red ? this : new CombinatorOrderedChoice(getParsers(), isHidden(), red);
    }

    @Override
    public @NotNull CombinatorOrderedChoice withParsers(final @NotNull List<Combinator> parsers) {
        return new CombinatorOrderedChoice(parsers, isHidden(), getReduction());
    }

    @Override
    public @NotNull List<Combinator> parsers() {
        return List.of(parser1, parser2);
    }
}
