package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.flat.AutoFlattenSeq;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

public record CombinatorStar(
        boolean hide,
        @NotNull ReductionType red,
        @NotNull Combinator parser) implements CombinatorWithParser {

    public CombinatorStar(@NotNull Combinator parser) {
        this(defaultHidden, defaultRed, parser);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForStar = new TrampolineListenerKey(index, this);
        runner.pushListener(
                new TrampolineListenerKey(index, combinator),
                CombinatorPlus.plusListener(AutoFlattenSeq.make(), combinator, index, nodeKeyForStar, runner)
        );
        runner.success(nodeKeyForStar, null, index);
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForStar = new TrampolineListenerKey(index, this);
        if (index == runner.tramp().getText().length()) {
            runner.success(nodeKeyForStar, null, index);
        } else {
            runner.pushListener(
                    new TrampolineListenerKey(index, combinator),
                    CombinatorPlus.plusFullListener(AutoFlattenSeq.make(), combinator, index, nodeKeyForStar, runner));
        }
    }

    @Override
    public @NotNull CombinatorStar withParser(final @NotNull Combinator parser) {
        return new CombinatorStar(hide, red, parser);
    }

    @Override
    public @NotNull CombinatorStar withHideTag(boolean hide) {
        return isHidden() == hide ? this : new CombinatorStar(hide, red, parser);
    }

    @Override
    public @NotNull CombinatorStar withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new CombinatorStar(hide, red, parser);
    }
}
