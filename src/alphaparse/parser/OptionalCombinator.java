package alphaparse.parser;

import alphaparse.Keyword;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonOptional;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

/**
 *  TODO
 * @param hide TODO
 * @param red TODO
 * @param parser TODO
 */
public record OptionalCombinator(
        boolean hide,
        @NotNull ReductionType red,
        @NotNull Combinator parser) implements CombinatorWithParser {
    /**
     *  TODO
     * @param parser TODO
     */
    public OptionalCombinator(@NotNull Combinator parser) {
        this(defaultHidden, defaultRed, parser);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForOpt = new TrampolineListenerKey(index, this);
        runner.pushListener(
                new TrampolineListenerKey(index, combinator),
                runner.nodeListener(nodeKeyForOpt)
        );
        runner.success(nodeKeyForOpt, null, index);
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator parser = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey thisNodeKey = new TrampolineListenerKey(index, this);
        runner.pushFullListener(new TrampolineListenerKey(index, parser), runner.nodeListener(thisNodeKey));
        if (index == runner.tramp().getText().length()) {
            runner.success(thisNodeKey, null, index);
        } else {
            runner.fail(thisNodeKey, index, new ParseFailureReasonOptional(Keyword.intern("end-of-string")));
        }
    }

    @Override
    public @NotNull OptionalCombinator withParser(final @NotNull Combinator parser) {
        return new OptionalCombinator(hide, red, parser);
    }

    @Override
    public @NotNull OptionalCombinator withHideTag(boolean hide) {
        return isHidden() == hide ? this : new OptionalCombinator(hide, red, parser);
    }

    @Override
    public @NotNull OptionalCombinator withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new OptionalCombinator(hide, red, parser);
    }
}
