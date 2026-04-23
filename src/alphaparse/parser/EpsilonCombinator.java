package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonEpsilon;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * TODO
 */
public final class EpsilonCombinator extends CombinatorTerminal {
    private static final @NotNull EpsilonCombinator epsilon = new EpsilonCombinator();

    /**
     * TODO
     *
     * @return TODO
     */
    public static @NotNull EpsilonCombinator getDefault() {
        return epsilon;
    }

    private EpsilonCombinator() {
        super();
    }

    private EpsilonCombinator(final boolean hide, final @NotNull ReductionType red) {
        super(hide, red);
    }

    /**
     * TODO
     *  @param index TODO
     *
     * @param runner TODO
     */
    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        runner.success(new TrampolineListenerKey(index, this), null, index);
    }

    /**
     * TODO
     *  @param index TODO
     *
     * @param runner TODO
     */
    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        if (index == runner.tramp().getText().length())
            runner.success(new TrampolineListenerKey(index, this), null, index);
        else
            runner.fail(new TrampolineListenerKey(index, this), index,
                    new ParseFailureReasonEpsilon());
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
        return Objects.hash(isHidden(), getReduction());
    }
}
