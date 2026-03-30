package alphaparse.parser.combinator;

import alphaparse.Gll;
import alphaparse.parser.Reduction;
import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;
import alphaparse.trampoline.InstaTramp;
import alphaparse.flat.AutoFlattenSeq;
import alphaparse.reduction.ReductionType;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class RepetitionCombinator extends CombinatorWithParser {
    private final int min;
    private final int max;

    public RepetitionCombinator(final @NotNull Combinator parser, final int min, final int max) {
        this(parser, min, max, false, Reduction.nullReduction);
    }

    public RepetitionCombinator(final @NotNull Combinator parser, final int min, final int max, final boolean hide, final @NotNull ReductionType reduction) {
        super(parser, hide, reduction);
        if (min < 0 || min > max) throw new IllegalArgumentException();
        this.min = min;
        this.max = max;
    }

    @Override
    public void parse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull Combinator combinator = getParser();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey parserNodeKey = new TrampolineListenerKey(index, this);
        final @NotNull TrampolineListenerNode.TrampolineListenerKey combinatorNodeKey = new TrampolineListenerKey(index, combinator);
        if (getMin() == 0) {
            Gll.success(tramp, combinatorNodeKey, null, index);
            if (getMax() >= 1) {
                Gll.pushListener(tramp, combinatorNodeKey,
                        GllParserListeners.repListener(AutoFlattenSeq.make(), 0, this, parserNodeKey, tramp));
            }
        }
        Gll.pushListener(tramp,
                combinatorNodeKey,
                GllParserListeners.repListener(AutoFlattenSeq.make(), 0, this, parserNodeKey, tramp));
    }

    @Override
    public void fullParse(final int index, final @NotNull InstaTramp tramp) {
        final @NotNull Combinator parser = getParser();
        final int m = getMin();
        final int n = getMax();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForParser = new TrampolineListenerKey(index, parser);
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForThis = new TrampolineListenerKey(index, this);
        final @NotNull var emptyResults = AutoFlattenSeq.make();
        if (m == 0) {
            Gll.success(tramp, new TrampolineListenerKey(index, this), null, index);
            if (n >= 1) {
                Gll.pushListener(
                        tramp, nodeKeyForParser,
                        GllParserListeners.repFullListener(emptyResults, 0, parser, 1, n, index, nodeKeyForThis, tramp));
            }
        } else {
            Gll.pushListener(
                    tramp, nodeKeyForParser,
                    GllParserListeners.repFullListener(emptyResults, 0, parser, m, n, index, nodeKeyForThis, tramp));
        }
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public @NotNull RepetitionCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new RepetitionCombinator(getParser(), min, max, hide1, this.getReduction());
    }

    @Override
    public @NotNull RepetitionCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new RepetitionCombinator(getParser(), min, max, isHidden(), red1);
    }

    @Override
    public @NotNull CombinatorWithParser withParser(final @NotNull Combinator parser) {
        return new RepetitionCombinator(parser, min, max, isHidden(), getReduction());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), getReduction(), isHidden(), getParser(), min, max);
    }
}