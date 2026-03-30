package alphaparse.parser.combinator;

import alphaparse.Gll;
import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;
import alphaparse.trampoline.InstaTramp;
import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.InstaFailureReasonEpsilon;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class EpsilonCombinator extends CombinatorTerminal {
    private long bufferedHashCode = Long.MIN_VALUE;

    public EpsilonCombinator() {
        super();
    }

    public EpsilonCombinator(final boolean hide, final @NotNull ReductionType red) {
        super(hide, red);
    }

    @Override
    public void parse(final int index, final @NotNull InstaTramp tramp) {
        Gll.success(tramp, new TrampolineListenerKey(index, this), null, index);
    }

    @Override
    public void fullParse(final int index, final @NotNull InstaTramp tramp) {
        if (index == tramp.getText().length())
            Gll.success(tramp, new TrampolineListenerKey(index, this), null, index);
        else
            Gll.fail(tramp, new TrampolineListenerKey(index, this), index,
                    new InstaFailureReasonEpsilon());
    }

    @Override
    public @NotNull EpsilonCombinator withHideTag(final boolean hide1) {
        return isHidden() == hide1 ? this : new EpsilonCombinator(hide1, this.getReduction());
    }

    @Override
    public @NotNull EpsilonCombinator withReduction(final @NotNull ReductionType red1) {
        return getReduction() == red1 ? this : new EpsilonCombinator(isHidden(), red1);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof EpsilonCombinator that)) return false;
        if (hashCode() != o.hashCode()) return false;
        if (!Objects.equals(getReduction(), that.getReduction())) return false;
        return Objects.equals(isHidden(), that.isHidden());
    }

    @Override
    public int hashCode() {
        if (bufferedHashCode == Long.MIN_VALUE)
            bufferedHashCode = Objects.hash(getClass(), getReduction(), isHidden());
        return (int) bufferedHashCode;
    }
}
