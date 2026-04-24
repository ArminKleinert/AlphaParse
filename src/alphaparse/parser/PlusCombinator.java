package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.flat.AutoFlattenSeq;
import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *  TODO
 * @param hide TODO
 * @param red TODO
 * @param parser TODO
 */
public record PlusCombinator(
        boolean hide,
        @NotNull ReductionType red,
        @NotNull Combinator parser) implements CombinatorWithParser {
    /**
     *  TODO
     * @param parser TODO
     */
    public PlusCombinator(@NotNull Combinator parser) {
        this(defaultHidden, defaultRed, parser);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator parser = getParser();
        runner.pushListener(
                new TrampolineListenerKey(index, parser),
                plusListener(AutoFlattenSeq.make(), parser, index, new TrampolineListenerKey(index, this), runner)
        );
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator parser = getParser();
        runner.pushListener(
                new TrampolineListenerKey(index, parser),
                plusFullListener(AutoFlattenSeq.make(), parser, index, new TrampolineListenerKey(index, this), runner)
        );
    }

    @NotNull
    static Listener plusListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                 final @NotNull Combinator parser,
                                 final int prevIndex,
                                 final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                 final @NotNull Gll runner) {
        return result -> {
            final @Nullable Object parsedResult = result.getResult();
            final int continueIndex = result.index();
            if (continueIndex == prevIndex) {
                if (resultsSoFar.isEmpty()) {
                    runner.success(nodeKey, null, continueIndex);
                }
                return;
            }
            final AutoFlattenSeq<Object> newResultsSoFar = parsedResult instanceof AutoFlattenSeq<?>
                    ? resultsSoFar.concat((AutoFlattenSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);
            runner.pushListener(new TrampolineListenerKey(continueIndex, parser), plusListener(newResultsSoFar, parser, continueIndex, nodeKey, runner));
            runner.success(nodeKey, newResultsSoFar, continueIndex);
        };
    }


    @NotNull
    static Listener plusFullListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                     final @NotNull Combinator parser,
                                     final int prevIndex,
                                     final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                     final @NotNull Gll runner) {
        return result -> {
            final @Nullable var parsedResult = result.getResult();
            final var continueIndex = result.index();
            if (continueIndex == prevIndex) {
                if (resultsSoFar.isEmpty())
                    runner.success(nodeKey, null, continueIndex);
            } else {
                final @NotNull var newResultsSoFar = parsedResult instanceof AutoFlattenSeq<?>
                        ? resultsSoFar.concat((AutoFlattenSeq<?>) parsedResult)
                        : resultsSoFar.append(parsedResult);
                if (continueIndex == runner.tramp().getText().length()) {
                    runner.success(nodeKey, newResultsSoFar, continueIndex);
                } else {
                    runner.pushListener(new TrampolineListenerKey(continueIndex, parser),
                            plusFullListener(newResultsSoFar, parser, continueIndex, nodeKey, runner));
                }
            }
        };
    }

    @Override
    public @NotNull PlusCombinator withParser(final @NotNull Combinator parser) {
        return new PlusCombinator(hide, red, parser);
    }

    @Override
    public @NotNull PlusCombinator withHideTag(boolean hide) {
        return isHidden() == hide ? this : new PlusCombinator(hide, red, parser);
    }

    @Override
    public @NotNull PlusCombinator withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new PlusCombinator(hide, red, parser);
    }
}
