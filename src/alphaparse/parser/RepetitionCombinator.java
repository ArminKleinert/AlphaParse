package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.flat.AutoFlattenSeq;
import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record RepetitionCombinator(
        boolean hide,
        @NotNull ReductionType red,
        @NotNull Combinator parser,
        int min,
        int max) implements CombinatorWithParser {

    public RepetitionCombinator(final @NotNull Combinator parser, final int min, final int max) {
        this(defaultHidden, defaultRed, parser, min, max);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey parserNodeKey = new TrampolineListenerKey(index, this);
        final @NotNull TrampolineListenerNode.TrampolineListenerKey combinatorNodeKey = new TrampolineListenerKey(index, combinator);
        if (getMin() == 0) {
            runner.success(combinatorNodeKey, null, index);
            if (getMax() >= 1) {
                runner.pushListener(combinatorNodeKey,
                        repListener(AutoFlattenSeq.make(), 0, this, parserNodeKey, runner));
            }
        }
        runner.pushListener(
                combinatorNodeKey,
                repListener(AutoFlattenSeq.make(), 0, this, parserNodeKey, runner));
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator parser = getParser();
        final int m = getMin();
        final int n = getMax();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForParser = new TrampolineListenerKey(index, parser);
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForThis = new TrampolineListenerKey(index, this);
        final @NotNull var emptyResults = AutoFlattenSeq.make();
        if (m == 0) {
            runner.success(new TrampolineListenerKey(index, this), null, index);
            if (n >= 1) {
                runner.pushListener(
                        nodeKeyForParser,
                        repFullListener(emptyResults, 0, parser, 1, n, index, nodeKeyForThis, runner));
            }
        } else {
            runner.pushListener(
                    nodeKeyForParser,
                    repFullListener(emptyResults, 0, parser, m, n, index, nodeKeyForThis, runner));
        }
    }

    private @NotNull Listener repListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                          final int nResultsSoFar,
                                          final @NotNull RepetitionCombinator parser,
                                          final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey, final @NotNull Gll runner) {
        return result -> {
            final @Nullable Object parsedResult = result.getResult();
            final int continueIndex = result.index();

            final @NotNull AutoFlattenSeq<Object> newResultsSoFar = parsedResult instanceof AutoFlattenSeq<?>
                    ? resultsSoFar.concat((AutoFlattenSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);

            final int newNResultsSoFar = nResultsSoFar + 1;

            if (Integer.max(parser.getMin(), 0) <= newNResultsSoFar && newNResultsSoFar <= parser.getMax())
                runner.success(nodeKey, newResultsSoFar, continueIndex);

            if (newNResultsSoFar < parser.getMax())
                runner.pushListener(
                        new TrampolineListenerKey(continueIndex, parser.getParser()),
                        repListener(newResultsSoFar, newNResultsSoFar, parser, nodeKey, runner)
                );
        };
    }


    private @NotNull Listener repFullListener(final @NotNull AutoFlattenSeq<Object> resultsSoFar,
                                              final int nResultsSoFar,
                                              final @NotNull Combinator parser,
                                              final int m,
                                              final int n,
                                              final int prevIndex,
                                              final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                              final @NotNull Gll runner) {
        return result -> {
            final @Nullable var parsedResult = result.getResult();
            final int continueIndex = result.index();
            final @NotNull var newResultsSoFar = parsedResult instanceof AutoFlattenSeq<?>
                    ? resultsSoFar.concat((AutoFlattenSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);
            final int newNResultsSoFar = nResultsSoFar + 1;
            if (continueIndex == runner.tramp().getText().length()) {
                if (m <= newNResultsSoFar && newNResultsSoFar <= n)
                    runner.success(nodeKey, newResultsSoFar, continueIndex);
            } else {
                if (newNResultsSoFar < n) {
                    final @NotNull var listener = repFullListener(
                            newResultsSoFar, newNResultsSoFar,
                            parser, m, n, continueIndex, nodeKey, runner);
                    runner.pushListener(new TrampolineListenerKey(continueIndex, parser), listener);
                }
            }
        };
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public int getMin() {
        return min;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public int getMax() {
        return max;
    }

    @Override
    public @NotNull RepetitionCombinator withParser(final @NotNull Combinator parser) {
        return new RepetitionCombinator(hide, red, parser, min, max);
    }

    @Override
    public @NotNull RepetitionCombinator withHideTag(boolean hide) {
        return isHidden() == hide ? this : new RepetitionCombinator(hide, red, parser, min, max);
    }

    @Override
    public @NotNull RepetitionCombinator withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new RepetitionCombinator(hide, red, parser, min, max);
    }
}
