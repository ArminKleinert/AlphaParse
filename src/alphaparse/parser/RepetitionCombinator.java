package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.IO2;
import alphaparse.flat.AutoFlattenSeq;
import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonExpectParser;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * A class representing the ABNF counted repetition operator.
 * There are multiple variants, depending on the syntax (where n1 and n2 are integers, and p is an instance of {@link Combinator}):
 * <ul>
 *     <li>Repeat a minimum of {@code n1} times and a maximum of {@code n2} times: {@code n1*n2 p}</li>
 *     <li>Repeat a minimum of {@code n1} times: {@code n1* p}</li>
 *     <li>Repeat a maximum of {@code n1} times: {@code *n1 p}</li>
 *     <li>Repeat exactly of {@code n1} times: {@code n1 p}</li>
 * </ul>
 */
public final class RepetitionCombinator extends CombinatorWithParser {
    private final int min;
    private final int max;

    private RepetitionCombinator(final boolean hide, final @NotNull ReductionType red,
                                 final @NotNull Combinator parser, final int min, final int max) {
        super(hide, red, parser);
        this.min = min;
        this.max = max;
    }

    /**
     * Creates a new instance.
     *
     * @param parser The inner element.
     * @param min    Minimum repetitions.
     * @param max    Maximum repetitions.
     * @throws IllegalArgumentException if minimum or maximum is invalid.
     */
    public RepetitionCombinator(final @NotNull Combinator parser, final int min, final int max) {
        super(parser);
        if (min < 0 || min > max)
            throw new IllegalArgumentException();
        this.min = min;
        this.max = max;
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
        } else {
            runner.pushListener(
                    combinatorNodeKey,
                    repListener(AutoFlattenSeq.make(), 0, this, parserNodeKey, runner));
        }
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator parser = getParser();
        final int minimum = getMin();
        final int maximum = getMax();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForParser = new TrampolineListenerKey(index, parser);
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForThis = new TrampolineListenerKey(index, this);
        final @NotNull var emptyResults = AutoFlattenSeq.make();
        if (minimum == 0) {
            runner.success(new TrampolineListenerKey(index, this), null, index);
            if (maximum >= 1) {
                runner.pushListener(
                        nodeKeyForParser,
                        repFullListener(emptyResults, 0, parser, 1, maximum, index, nodeKeyForThis, runner));
            }
        } else {
            runner.pushListener(
                    nodeKeyForParser,
                    repFullListener(emptyResults, 0, parser, minimum, maximum, index, nodeKeyForThis, runner));
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
                                              final int minimum,
                                              final int maximum,
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
                if (minimum <= newNResultsSoFar && newNResultsSoFar <= maximum)
                    runner.success(nodeKey, newResultsSoFar, continueIndex);
                else runner.fail(nodeKey, continueIndex, new ParseFailureReasonExpectParser(false, parser));
            } else {
                if (newNResultsSoFar < maximum) {
                    final @NotNull var listener = repFullListener(
                            newResultsSoFar, newNResultsSoFar,
                            parser, minimum, maximum, continueIndex, nodeKey, runner);
                    runner.pushListener(new TrampolineListenerKey(continueIndex, parser), listener);
                } else runner.fail(nodeKey, continueIndex, new ParseFailureReasonExpectParser(false, parser));
            }
        };
    }

    /**
     * Return the minimum number of repetitions.
     *
     * @return minimum
     */
    public int getMin() {
        return min;
    }

    /**
     * Return the maximum number of repetitions.
     *
     * @return maximum
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RepetitionCombinator that)) return false;
        if (this == that) return true;
        return hide == that.hide
                && Objects.equals(red, that.red)
                && Objects.equals(parser, that.parser)
                && Objects.equals(min, that.min)
                && Objects.equals(max, that.max);
    }

    @Override
    public int hashCode() {
        return min * 31 + max * 31 + Objects.hash(hide, red, parser);
    }
}
