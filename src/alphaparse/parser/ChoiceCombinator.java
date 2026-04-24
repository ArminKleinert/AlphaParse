package alphaparse.parser;

import alphaparse.reduction.ReductionType;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *  TODO
 * @param hide TODO
 * @param red TODO
 * @param parsers TODO
 */
public record ChoiceCombinator(
        boolean hide,
        @NotNull ReductionType red,
        @NotNull List<Combinator> parsers) implements CombinatorWithManyParsers {
    /**
     *  TODO
     * @param parsers TODO
     */
    public ChoiceCombinator(@NotNull List<Combinator> parsers) {
        this(defaultHidden, defaultRed, parsers);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        for (final @NotNull Combinator combinator : getParsers()) {
            runner.pushListener(
                    new TrampolineListenerKey(index, combinator),
                    runner.nodeListener(new TrampolineListenerKey(index, this))
            );
        }
    }

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
    public @NotNull ChoiceCombinator withHideTag(boolean hide) {
        return isHidden() == hide ? this : new ChoiceCombinator(hide, red, parsers);
    }

    @Override
    public @NotNull ChoiceCombinator withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new ChoiceCombinator(hide, red, parsers);
    }

    @Override
    public @NotNull ChoiceCombinator withParsers(@NotNull List<@NotNull Combinator> parsers) {
        return new ChoiceCombinator(hide, red, parsers);
    }
}
