package alphaparse.parser;

import alphaparse.flat.AutoFlattenSeq;
import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

/**
 *  TODO
 * @param hide TODO
 * @param red TODO
 * @param parsers TODO
 */
public record ConcatCombinator(
        boolean hide,
        @NotNull ReductionType red,
        @NotNull List<Combinator> parsers) implements CombinatorWithManyParsers {
    /**
     *  TODO
     * @param parsers TODO
     */
    public ConcatCombinator(@NotNull List<Combinator> parsers) {
        this(defaultHidden, defaultRed, parsers);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull List<@NotNull Combinator> parsers = getParsers();
        runner.pushListener(
                new TrampolineListenerKey(index, parsers.getFirst()),
                catListener(AutoFlattenSeq.make(), parsers.subList(1, parsers.size()), new TrampolineListenerKey(index, this), runner));
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull List<@NotNull Combinator> parsers = getParsers();
        runner.pushListener(
                new TrampolineListenerKey(index, parsers.getFirst()),
                catFullListener(AutoFlattenSeq.make(), parsers.subList(1, parsers.size()), new TrampolineListenerKey(index, this), runner));
    }

    private @NotNull Listener catListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                          final @NotNull List<Combinator> parserSequence,
                                          final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                          final @NotNull Gll runner) {
        return result -> {
            final @Nullable Object parsedResult = result.getResult();
            final int continueIndex = result.index();
            final @NotNull AutoFlattenSeq<Object> newResultsSoFar = parsedResult instanceof AutoFlattenSeq<?>
                    ? resultsSoFar.concat((AutoFlattenSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);

            if (parserSequence.isEmpty()) {
                runner.success(nodeKey, newResultsSoFar, continueIndex);
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

    private @NotNull Listener catFullListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                              final @NotNull List<Combinator> parserSequence,
                                              final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                              final @NotNull Gll runner) {
        return result -> {
            final @Nullable var parsedResult = result.getResult();
            final var continueIndex = result.index();
            final @NotNull var newResultsSoFar = parsedResult instanceof AutoFlattenSeq<?>
                    ? resultsSoFar.concat((AutoFlattenSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);

            if (parserSequence.size() == 1) {
                runner.pushFullListener(new TrampolineListenerKey(continueIndex, parserSequence.getFirst()),
                        catFullListener(newResultsSoFar, List.of(), nodeKey, runner));
            } else if (!parserSequence.isEmpty()) {
                runner.pushListener(new TrampolineListenerKey(continueIndex, parserSequence.getFirst()),
                        catFullListener(newResultsSoFar, parserSequence.subList(1, parserSequence.size()), nodeKey, runner));
            } else {
                runner.success(nodeKey, newResultsSoFar, continueIndex);
            }
        };
    }

    @Override
    public @NotNull Combinator withHideTag(boolean hide) {
        return isHidden() == hide ? this : new ConcatCombinator(hide, red, parsers);
    }

    @Override
    public @NotNull Combinator withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new ConcatCombinator(hide, red, parsers);
    }

    @Override
    public @NotNull ConcatCombinator withParsers(@NotNull List<@NotNull Combinator> parsers) {
        return new ConcatCombinator(hide, red, parsers);
    }
}
