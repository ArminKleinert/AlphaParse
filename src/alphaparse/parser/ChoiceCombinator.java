package alphaparse.parser;

import alphaparse.reduction.ReductionType;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * TODO
 */
public final class ChoiceCombinator extends CombinatorWithManyParsers {
    private ChoiceCombinator(boolean hide, @NotNull ReductionType red, @NotNull List<Combinator> parsers) {
        super(hide, red, parsers);
    }

    /**
     * TODO
     *
     * @param parsers TODO
     */
    public ChoiceCombinator(@NotNull List<Combinator> parsers) {
        super(parsers);
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
        return isHidden() == hide ? this : new ChoiceCombinator(hide, getReduction(), getParsers());
    }

    @Override
    public @NotNull ChoiceCombinator withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new ChoiceCombinator(isHidden(), red, getParsers());
    }

    @Override
    public @NotNull ChoiceCombinator withParsers(@NotNull List<@NotNull Combinator> parsers) {
        return new ChoiceCombinator(isHidden(), getReduction(), parsers);
    }

//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof ChoiceCombinator that)) return false;
//        if (this==that ) return true;
//        return hide() == that.hide() && Objects.equals(red(), that.red()) && Objects.equals(parsers(),that.parsers());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(hide(), red(),parsers());
//    }
}
