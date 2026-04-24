package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.flat.AutoFlattenSeq;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * TODO
 */
public final class CombinatorStar extends CombinatorWithParser {
    private CombinatorStar(final boolean hide, final @NotNull ReductionType red, final @NotNull Combinator parser) {
        super(hide, red, parser);
    }

    /**
     * TODO
     *
     * @param parser TODO
     */
    public CombinatorStar(final @NotNull Combinator parser) {
        super(parser);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForStar = new TrampolineListenerKey(index, this);
        runner.pushListener(
                new TrampolineListenerKey(index, combinator),
                PlusCombinator.plusListener(AutoFlattenSeq.make(), combinator, index, nodeKeyForStar, runner)
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
                    PlusCombinator.plusFullListener(AutoFlattenSeq.make(), combinator, index, nodeKeyForStar, runner));
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

//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof CombinatorStar that)) return false;
//        if (this==that ) return true;
//        return hide() == that.hide() && Objects.equals(red(), that.red()) && Objects.equals(parser(),that.parser());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(hide(), red(),parser());
//    }
}
