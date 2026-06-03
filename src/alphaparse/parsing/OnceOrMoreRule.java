package alphaparse.parsing;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.collections.FlatSeq;
import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a "once or more" parse. That is the {@code p+} operator (where p is an instance of {@link Rule}).
 */
public final class OnceOrMoreRule extends RuleWithChild {
    private OnceOrMoreRule(final boolean hide,
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
        if (rule instanceof EpsilonTerm)
            return rule;
        return new OnceOrMoreRule(defaultHidden, defaultReductionType, rule);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Rule parser = getRule();
        runner.pushListener(
                new TrampolineListenerKey(index, parser),
                plusListener(FlatSeq.make(), parser, index, new TrampolineListenerKey(index, this), runner)
        );
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Rule parser = getRule();
        runner.pushListener(
                new TrampolineListenerKey(index, parser),
                plusFullListener(FlatSeq.make(), parser, index, new TrampolineListenerKey(index, this), runner)
        );
    }

    @NotNull
    static Listener plusListener(final @NotNull FlatSeq<Object> resultsSoFar,
                                 final @NotNull Rule rule,
                                 final int prevIndex,
                                 final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                 final @NotNull Gll runner) {
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
            runner.pushListener(
                    new TrampolineListenerKey(continueIndex, rule),
                    plusListener(newResultsSoFar, rule, continueIndex, nodeKey, runner));
            runner.pushSuccessMessage(nodeKey, newResultsSoFar, continueIndex);
        };
    }


    @NotNull
    static Listener plusFullListener(final @NotNull FlatSeq<Object> resultsSoFar,
                                     final @NotNull Rule rule,
                                     final int prevIndex,
                                     final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey,
                                     final @NotNull Gll runner) {
        return result -> {
            final @Nullable var parsedResult = result.getResult();
            final var continueIndex = result.index();
            if (continueIndex == prevIndex) {
                if (resultsSoFar.isEmpty())
                    runner.pushSuccessMessageWithoutValue(nodeKey, continueIndex);
            } else {
                final @NotNull var newResultsSoFar = parsedResult instanceof FlatSeq<?>
                        ? resultsSoFar.concat((FlatSeq<?>) parsedResult)
                        : resultsSoFar.append(parsedResult);
                if (continueIndex == runner.tramp().getText().length()) {
                    runner.pushSuccessMessage(nodeKey, newResultsSoFar, continueIndex);
                } else {
                    runner.pushListener(
                            new TrampolineListenerKey(continueIndex, rule),
                            plusFullListener(newResultsSoFar, rule, continueIndex, nodeKey, runner));
                }
            }
        };
    }

    @Override
    public @NotNull OnceOrMoreRule withInner(final @NotNull Rule rule) {
        return new OnceOrMoreRule(hide, red, rule);
    }

    @Override
    public @NotNull OnceOrMoreRule withHideTag(boolean hide) {
        return isHidden() == hide ? this : new OnceOrMoreRule(hide, red, rule);
    }

    @Override
    public @NotNull OnceOrMoreRule withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new OnceOrMoreRule(hide, red, rule);
    }
}
