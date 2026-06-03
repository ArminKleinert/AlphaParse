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
     * Creates a new instance.
     *
     * @param parser The inner element.
     */
    public OnceOrMoreRule(final @NotNull Rule parser) {
        super(defaultHidden, ReductionType.standardInitialReduction(), parser);
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
                                 final @NotNull Rule parser,
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
                    new TrampolineListenerKey(continueIndex, parser),
                    plusListener(newResultsSoFar, parser, continueIndex, nodeKey, runner));
            runner.pushSuccessMessage(nodeKey, newResultsSoFar, continueIndex);
        };
    }


    @NotNull
    static Listener plusFullListener(final @NotNull FlatSeq<Object> resultsSoFar,
                                     final @NotNull Rule parser,
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
                            new TrampolineListenerKey(continueIndex, parser),
                            plusFullListener(newResultsSoFar, parser, continueIndex, nodeKey, runner));
                }
            }
        };
    }

    @Override
    public @NotNull OnceOrMoreRule withParser(final @NotNull Rule parser) {
        return new OnceOrMoreRule(hide, red, parser);
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
