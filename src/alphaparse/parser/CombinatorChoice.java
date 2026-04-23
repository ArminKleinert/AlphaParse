package alphaparse.parser;

import alphaparse.reduction.ReductionType;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CombinatorChoice(
        boolean hide,
        @NotNull ReductionType red,
        @NotNull List<Combinator> parsers) implements CombinatorWithManyParsers {

    public CombinatorChoice(@NotNull List<Combinator> parsers) {
        this(defaultHidden, defaultRed, parsers);
    }

    /**
     * TODO
     *  @param index TODO
     *
     * @param runner TODO
     */
    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        for (final @NotNull Combinator combinator : getParsers()) {
            runner.pushListener(
                    new TrampolineListenerKey(index, combinator),
                    runner.nodeListener(new TrampolineListenerKey(index, this))
            );
        }
    }

    /**
     * TODO
     *  @param index TODO
     *
     * @param runner TODO
     */
    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        for (final @NotNull Combinator parser : getParsers()) {
            runner.pushFullListener(
                    new TrampolineListenerKey(index, parser),
                    runner.nodeListener(new TrampolineListenerKey(index, this))
            );
        }
    }

    @Override
    public @NotNull CombinatorChoice withHideTag(boolean hide) {
        return isHidden() == hide ? this : new CombinatorChoice(hide, red, parsers);
    }

    @Override
    public @NotNull CombinatorChoice withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new CombinatorChoice(hide, red, parsers);
    }

    @Override
    public @NotNull CombinatorChoice withParsers(@NotNull List<@NotNull Combinator> parsers) {
        return new CombinatorChoice(hide, red, parsers);
    }
}
