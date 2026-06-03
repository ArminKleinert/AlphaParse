package alphaparse.parsing;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.collections.FlatSeq;
import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.ParseFailureReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A class representing the ABNF counted repetition operator.
 * There are multiple variants, depending on the syntax (where n1 and n2 are integers, and p is an instance of {@link Rule}):
 * <ul>
 *     <li>Repeat a minimum of {@code n1} times and a maximum of {@code n2} times: {@code n1*n2 p}</li>
 *     <li>Repeat a minimum of {@code n1} times: {@code n1* p}</li>
 *     <li>Repeat a maximum of {@code n1} times: {@code *n1 p}</li>
 *     <li>Repeat exactly of {@code n1} times: {@code n1 p}</li>
 * </ul>
 */
public final class VariableRepetitionRule extends RuleWithChild {
    private final int min;
    private final int max;

    private VariableRepetitionRule(final boolean hide,
                                   final @NotNull ReductionType red,
                                   final @NotNull Rule parser,
                                   final int min, final int max) {
        super(hide, red, parser);
        this.min = min;
        this.max = max;
    }

    /**
     * Create a new instance. Depending on the implementation, allows for buffering or create a different type of rule.
     *
     * @param rule The inner element.
     * @param min  Minimum repetitions.
     * @param max  Maximum repetitions.
     * @return A rule.
     */
    public static @NotNull Rule create(final @NotNull Rule rule, final int min, final int max) {
        if (min < 0 || min > max)
            throw new IllegalArgumentException();
        return new VariableRepetitionRule(defaultHidden, defaultReductionType, rule, min, max);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Rule rule = getRule();
        final @NotNull TrampolineListenerKey nodeKeyForThis = new TrampolineListenerKey(index, this);
        final @NotNull TrampolineListenerKey nodeKeyForInnerRule = new TrampolineListenerKey(index, rule);
        if (getMin() == 0) {
            runner.pushSuccessMessageWithoutValue(nodeKeyForInnerRule, index);
            if (getMax() >= 1) {
                runner.pushListener(nodeKeyForInnerRule,
                        repListener(FlatSeq.make(), 0, this, nodeKeyForThis, runner));
            }
        } else {
            runner.pushListener(
                    nodeKeyForInnerRule,
                    repListener(FlatSeq.make(), 0, this, nodeKeyForThis, runner));
        }
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Rule parser = getRule();
        final int minimum = getMin();
        final int maximum = getMax();
        final @NotNull TrampolineListenerKey nodeKeyForThis = new TrampolineListenerKey(index, this);
        final @NotNull TrampolineListenerKey nodeKeyForInnerRule = new TrampolineListenerKey(index, parser);
        final @NotNull var emptyResults = FlatSeq.make();
        if (minimum == 0) {
            runner.pushSuccessMessageWithoutValue(new TrampolineListenerKey(index, this), index);
            if (maximum >= 1) {
                runner.pushListener(
                        nodeKeyForInnerRule,
                        repFullListener(emptyResults, 0, parser, 1, maximum, nodeKeyForThis, runner));
            }
        } else {
            runner.pushListener(
                    nodeKeyForInnerRule,
                    repFullListener(emptyResults, 0, parser, minimum, maximum, nodeKeyForThis, runner));
        }
    }

    private @NotNull Listener repListener(final @NotNull FlatSeq<Object> resultsSoFar,
                                          final int nResultsSoFar,
                                          final @NotNull VariableRepetitionRule parser,
                                          final @NotNull TrampolineListenerKey nodeKey,
                                          final @NotNull Gll runner) {
        return result -> {
            final @Nullable Object parsedResult = result.getResult();
            final int continueIndex = result.index();

            final @NotNull FlatSeq<Object> newResultsSoFar = parsedResult instanceof FlatSeq<?>
                    ? resultsSoFar.concat((FlatSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);

            final int newNResultsSoFar = nResultsSoFar + 1;

            if (Integer.max(parser.getMin(), 0) <= newNResultsSoFar && newNResultsSoFar <= parser.getMax())
                runner.pushSuccessMessage(nodeKey, newResultsSoFar, continueIndex);

            if (newNResultsSoFar < parser.getMax())
                runner.pushListener(
                        new TrampolineListenerKey(continueIndex, parser.getRule()),
                        repListener(newResultsSoFar, newNResultsSoFar, parser, nodeKey, runner)
                );
        };
    }

    private @NotNull Listener repFullListener(final @NotNull FlatSeq<Object> resultsSoFar,
                                              final int nResultsSoFar,
                                              final @NotNull Rule parser,
                                              final int minimum,
                                              final int maximum,
                                              final @NotNull TrampolineListenerKey nodeKey,
                                              final @NotNull Gll runner) {
        return result -> {
            final @Nullable var parsedResult = result.getResult();
            final int continueIndex = result.index();
            final @NotNull var newResultsSoFar = parsedResult instanceof FlatSeq<?>
                    ? resultsSoFar.concat((FlatSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);
            final int newNResultsSoFar = nResultsSoFar + 1;
            if (continueIndex == runner.tramp().getText().length()) {
                if (minimum <= newNResultsSoFar && newNResultsSoFar <= maximum) {
                    runner.pushSuccessMessage(nodeKey, newResultsSoFar, continueIndex);
                } else {
                    runner.fail(nodeKey, continueIndex,
                            ParseFailureReason.ofRepetition(this, false));
                }
            } else {
                if (newNResultsSoFar < maximum) {
                    final @NotNull var listener = repFullListener(
                            newResultsSoFar, newNResultsSoFar,
                            parser, minimum, maximum, nodeKey, runner);
                    runner.pushListener(new TrampolineListenerKey(continueIndex, parser), listener);
                } else {
                    runner.fail(nodeKey, continueIndex,
                            ParseFailureReason.ofRepetition(this, false));
                }
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
    public @NotNull VariableRepetitionRule withParser(final @NotNull Rule parser) {
        return new VariableRepetitionRule(hide, red, parser, min, max);
    }

    @Override
    public @NotNull VariableRepetitionRule withHideTag(boolean hide) {
        return isHidden() == hide ? this : new VariableRepetitionRule(hide, red, rule, min, max);
    }

    @Override
    public @NotNull VariableRepetitionRule withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new VariableRepetitionRule(hide, red, rule, min, max);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VariableRepetitionRule that)) return false;
        if (this == that) return true;
        return hide == that.hide
                && Objects.equals(red, that.red)
                && Objects.equals(rule, that.rule)
                && Objects.equals(min, that.min)
                && Objects.equals(max, that.max);
    }

    @Override
    public int hashCode() {
        return min * 31 + max * 31 + Objects.hash(hide, red, rule);
    }
}
