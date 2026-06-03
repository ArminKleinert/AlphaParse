package alphaparse.parsing;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.collections.FlatSeq;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

/**
 * "Zero or more" repetition. Represents a production which repeatedly ties to match an input. E.g. {@code P*} matches zero or more.
 * <p>
 * Notation: {@code {rule}} or {@code rule*}
 */
public final class ZeroOrMoreRule extends RuleWithChild {
    private ZeroOrMoreRule(final boolean hide,
                           final @NotNull ReductionType red,
                           final @NotNull Rule parser) {
        super(hide, red, parser);
    }

    /**
     * Create a new instance. Depending on the implementation, allows for buffering or create a different type of rule.
     *
     * @param rule The {@link Rule} to match repeatedly.
     * @return A rule.
     */
    public static @NotNull Rule create(final @NotNull Rule rule) {
        return new ZeroOrMoreRule(defaultHidden, defaultReductionType, rule);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Rule rule = getRule();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForStar =
                new TrampolineListenerKey(index, this);
        runner.pushListener(
                new TrampolineListenerKey(index, rule),
                OnceOrMoreRule.plusListener(FlatSeq.make(), rule, index, nodeKeyForStar, runner)
        );
        runner.pushSuccessMessageWithoutValue(nodeKeyForStar, index);
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Rule rule = getRule();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForStar = new TrampolineListenerKey(index, this);
        if (index == runner.tramp().getText().length()) {
            runner.pushSuccessMessageWithoutValue(nodeKeyForStar, index);
        } else {
            runner.pushListener(
                    new TrampolineListenerKey(index, rule),
                    OnceOrMoreRule.plusFullListener(FlatSeq.make(), rule, index, nodeKeyForStar, runner));
        }
    }

    @Override
    public @NotNull ZeroOrMoreRule withParser(final @NotNull Rule parser) {
        return new ZeroOrMoreRule(hide, red, parser);
    }

    @Override
    public @NotNull ZeroOrMoreRule withHideTag(boolean hide) {
        return isHidden() == hide ? this : new ZeroOrMoreRule(hide, red, rule);
    }

    @Override
    public @NotNull ZeroOrMoreRule withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new ZeroOrMoreRule(hide, red, rule);
    }
}
