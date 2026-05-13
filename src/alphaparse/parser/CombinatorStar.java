package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.flat.FlatSeq;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

/**
 * "Zero or more" repetition. Represents a production which repeatedly ties to match an input. E.g. {@code P*} matches zero or more.
 * <p>
 * Notation: {@code {rule}} or {@code rule*}
 */
public final class CombinatorStar extends CombinatorWithParser {
    private CombinatorStar(final boolean hide, final @NotNull ReductionType red, final @NotNull Combinator parser) {
        super(hide, red, parser);
    }

    /**
     * Creates a new instance. Instead of using this directly, use methods from {@link CombinatorFactory}.
     *
     * @param parser The {@link Combinator} to match repeatedly.
     * @see CombinatorFactory#starCombinator(Combinator)
     */
    public CombinatorStar(final @NotNull Combinator parser) {
        super(parser);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForStar = new TrampolineListenerKey(index, this);
        runner.pushListener(
                new TrampolineListenerKey(index, combinator),
                PlusCombinator.plusListener(FlatSeq.make(), combinator, index, nodeKeyForStar, runner)
        );
        runner.pushSuccessMessageWithoutValue(nodeKeyForStar, index);
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForStar = new TrampolineListenerKey(index, this);
        if (index == runner.tramp().getText().length()) {
            runner.pushSuccessMessageWithoutValue(nodeKeyForStar, index);
        } else {
            runner.pushListener(
                    new TrampolineListenerKey(index, combinator),
                    PlusCombinator.plusFullListener(FlatSeq.make(), combinator, index, nodeKeyForStar, runner));
        }
    }

    @Override
    public @NotNull CombinatorStar withParser(final @NotNull Combinator parser) {
        return new CombinatorStar(hide, red, parser);
    }

    @Override
    public @NotNull CombinatorStar withHideTag(boolean hide) {
        return isHidden() == hide ? this : new CombinatorStar(hide, red, parser);
    }

    @Override
    public @NotNull CombinatorStar withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new CombinatorStar(hide, red, parser);
    }
}
