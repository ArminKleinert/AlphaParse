package alphaparse.parser;

import alphaparse.flat.FlatSeq;
import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

/**
 * This class represents a concatenation of productions, written as {@code p1 p2 p3 ...} (where p1, p2, etc. are instances of {@link Combinator}).
 * When parsing, it tries to match p1, then p2, then p3 and so on.
 */
public final class ConcatCombinator extends CombinatorWithManyParsers {
    private ConcatCombinator(boolean hide, @NotNull ReductionType red, @NotNull List<Combinator> parsers) {
        super(hide, red, parsers);
    }

    /**
     * Creates a new instance. Instead of using this directly, use methods from {@link CombinatorFactory}.
     *
     * @param parsers The parsers.
     * @see CombinatorFactory#catCombinator(List)
     */
    public ConcatCombinator(@NotNull List<Combinator> parsers) {
        super(parsers);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull List<@NotNull Combinator> parsers = getParsers();
        runner.pushListener(
                new TrampolineListenerKey(index, parsers.getFirst()),
                catListener(FlatSeq.make(), parsers.subList(1, parsers.size()), new TrampolineListenerKey(index, this), runner));
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull List<@NotNull Combinator> parsers = getParsers();
        runner.pushListener(
                new TrampolineListenerKey(index, parsers.getFirst()),
                catFullListener(FlatSeq.make(), parsers.subList(1, parsers.size()), new TrampolineListenerKey(index, this), runner));
    }

    private @NotNull Listener catListener(final @NotNull FlatSeq<Object> resultsSoFar,
                                          final @NotNull List<Combinator> parserSequence,
                                          final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                          final @NotNull Gll runner) {
        return result -> {
            final @Nullable Object parsedResult = result.getResult();
            final int continueIndex = result.index();
            final @NotNull FlatSeq<Object> newResultsSoFar = parsedResult instanceof FlatSeq<?>
                    ? resultsSoFar.concat((FlatSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);

            if (parserSequence.isEmpty()) {
                runner.pushSuccessMessage(nodeKey, newResultsSoFar, continueIndex);
            } else {
                runner.pushListener(
                        new TrampolineListenerKey(continueIndex, parserSequence.getFirst()),
                        catListener(
                                newResultsSoFar,
                                parserSequence.subList(1, parserSequence.size()),
                                nodeKey,
                                runner)
                );
            }
        };
    }

    private @NotNull Listener catFullListener(final @NotNull FlatSeq<Object> resultsSoFar,
                                              final @NotNull List<Combinator> parserSequence,
                                              final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                              final @NotNull Gll runner) {
        return result -> {
            final @Nullable var parsedResult = result.getResult();
            final var continueIndex = result.index();
            final @NotNull var newResultsSoFar = parsedResult instanceof FlatSeq<?>
                    ? resultsSoFar.concat((FlatSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);

            if (parserSequence.size() == 1) {
                runner.pushFullListener(
                        new TrampolineListenerKey(continueIndex, parserSequence.getFirst()),
                        catFullListener(newResultsSoFar, List.of(), nodeKey, runner));
            } else if (!parserSequence.isEmpty()) {
                runner.pushListener(
                        new TrampolineListenerKey(continueIndex, parserSequence.getFirst()),
                        catFullListener(newResultsSoFar, parserSequence.subList(1, parserSequence.size()), nodeKey, runner));
            } else {
                runner.pushSuccessMessage(nodeKey, newResultsSoFar, continueIndex);
            }
        };
    }

    @Override
    public @NotNull Combinator withHideTag(boolean hide) {
        return isHidden() == hide ? this : new ConcatCombinator(hide, getReduction(), getParsers());
    }

    @Override
    public @NotNull Combinator withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new ConcatCombinator(isHidden(), red, getParsers());
    }

    @Override
    public @NotNull ConcatCombinator withParsers(@NotNull List<@NotNull Combinator> parsers) {
        return new ConcatCombinator(isHidden(), getReduction(), parsers);
    }
}
