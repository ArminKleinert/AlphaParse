package alphaparse.parser;

import alphaparse.Keyword;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonOptional;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

public record CombinatorOptional(
        boolean hide,
        @NotNull ReductionType red,
        @NotNull Combinator parser) implements CombinatorWithParser {

    public CombinatorOptional(@NotNull Combinator parser) {
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
    public @NotNull CombinatorOptional withParser(final @NotNull Combinator parser) {
        return new CombinatorOptional(hide, red, parser);
    }

    @Override
    public @NotNull CombinatorOptional withHideTag(boolean hide) {
        return isHidden() == hide ? this : new CombinatorOptional(hide, red, parser);
    }

    @Override
    public @NotNull CombinatorOptional withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new CombinatorOptional(hide, red, parser);
    }
}
