package alphaparse.parsing;

import static alphaparse.parsing.OnceOrMoreRule.plusFullListener;
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
            throw new IllegalArgumentException(
                    "Illegal repetition (min=" + min + ", max=" + max + ")");
        if (min == 0 && max == 0)
            return EpsilonTerm.getDefault();
        if (rule instanceof EpsilonTerm || (min == 1 && max == 1))
            return rule;
        if ((rule instanceof VariableRepetitionRule rc) && rc.getMin() <= 1 && min <= 1) {
            final int newMin = (int) Long.min(
                    Integer.MAX_VALUE,
                    ((long) rc.getMin()) * min);
            final int newMax = (int) Long.min(
                    Integer.MAX_VALUE,
                    ((long) rc.getMax()) * max);
            return VariableRepetitionRule.create(rc.getRule(), newMin, newMax);
        }
        return new VariableRepetitionRule(defaultHidden, defaultReductionType, rule, min, max);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Rule rule = getRule();
        final @NotNull TrampolineListenerKey nodeKeyForThis = new TrampolineListenerKey(index, this);
        final @NotNull TrampolineListenerKey nodeKeyForInnerRule = new TrampolineListenerKey(index, rule);
        runner.pushListener(
                nodeKeyForInnerRule,
                repListener(FlatSeq.make(), this, index, nodeKeyForThis, runner, 1));
        if (getMin() == 0) {
            runner.pushSuccessMessageWithoutValue(nodeKeyForInnerRule, index);
        }
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull TrampolineListenerKey nodeKeyForThis = new TrampolineListenerKey(index, this);
        if (getMin() == 0 && index == runner.tramp().getText().length()) {
            runner.pushSuccessMessageWithoutValue(nodeKeyForThis, index);
            return;
        }
        runner.pushListener(
                new TrampolineListenerKey(index, getRule()),
                repFullListener(FlatSeq.make(), this, index, nodeKeyForThis, runner, 0));

    }

    private @NotNull Listener repListener(final @NotNull FlatSeq<Object> resultsSoFar,
                                          final @NotNull VariableRepetitionRule rule,
                                          final int prevIndex,
                                          final @NotNull TrampolineListenerKey nodeKey,
                                          final @NotNull Gll runner,
                                          final int nResultsSoFar) {
        return result -> {
            final @Nullable Object parsedResult = result.getResult();
            final int continueIndex = result.index();
            if (continueIndex == prevIndex) {
                if (resultsSoFar.isEmpty()) {
                    runner.pushSuccessMessageWithoutValue(nodeKey, continueIndex);
                }
                return;
            }
            final FlatSeq<Object> newResultsSoFar = parsedResult instanceof FlatSeq<?>
                    ? resultsSoFar.concat((FlatSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);

            if (nResultsSoFar > rule.getMax())
                return;

            runner.pushListener(
                    new TrampolineListenerKey(continueIndex, rule),
                    repListener(newResultsSoFar, rule, continueIndex, nodeKey, runner, nResultsSoFar + 1));

            if (nResultsSoFar > rule.getMin())
                runner.pushSuccessMessage(nodeKey, newResultsSoFar, continueIndex);
        };
    }

    private @NotNull Listener repFullListener(final @NotNull FlatSeq<Object> resultsSoFar,
                                              final @NotNull VariableRepetitionRule rule,
                                              final int prevIndex,
                                              final @NotNull TrampolineListenerKey nodeKey,
                                              final @NotNull Gll runner,
                                              final int nResultsSoFar) {
        return result -> {
            final @Nullable var parsedResult = result.getResult();
            final var continueIndex = result.index();

            final var newResultsSoFar = parsedResult instanceof FlatSeq<?>
                    ? resultsSoFar.concat((FlatSeq<?>) parsedResult)
                    : resultsSoFar.append(parsedResult);
            var newNResultsSoFar = nResultsSoFar + 1;

            if (continueIndex == runner.tramp().getText().length()) {
                if (rule.getMin() >= newNResultsSoFar && newNResultsSoFar >= rule.getMax()) {
                    runner.pushSuccessMessage(nodeKey, newResultsSoFar, continueIndex);
                }
                return;
            }

            if (nResultsSoFar >= rule.getMax())
                return;

            runner.pushListener(
                    new TrampolineListenerKey(continueIndex, rule.getRule()),
                    repFullListener(newResultsSoFar, rule, continueIndex, nodeKey, runner, newNResultsSoFar));
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
    public @NotNull VariableRepetitionRule withInner(final @NotNull Rule rule) {
        return new VariableRepetitionRule(hide, red, rule, min, max);
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
