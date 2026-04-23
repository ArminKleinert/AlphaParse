package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.flat.AutoFlattenSeq;
import alphaparse.functions.Listener;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * TODO
 */
public final class PlusCombinator extends CombinatorWithParser {
    /**
     * TODO
     *
     * @param parser TODO
     */
    public PlusCombinator(final @NotNull Combinator parser) {
        super(parser);
    }

    private PlusCombinator(final @NotNull Combinator parser, final boolean hide, final @NotNull ReductionType red) {
        super(parser, hide, red);
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
    public @NotNull PlusCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new PlusCombinator(getParser(), hide1, this.getReduction());
    }

    @Override
    public @NotNull PlusCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new PlusCombinator(getParser(), isHidden(), red1);
    }

    @Override
    public @NotNull PlusCombinator withParser(final @NotNull Combinator parser) {
        return new PlusCombinator(parser, isHidden(), getReduction());
    }
}
