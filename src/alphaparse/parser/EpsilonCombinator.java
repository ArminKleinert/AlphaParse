package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonEpsilon;
import org.jetbrains.annotations.NotNull;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

/**
 *  TODO
 * @param hide TODO
 * @param red TODO
 */
public record EpsilonCombinator(
        boolean hide,
        @NotNull ReductionType red) implements CombinatorTerminal {

    private static final @NotNull EpsilonCombinator epsilon = new EpsilonCombinator();

    private EpsilonCombinator() {
        this(defaultHidden, defaultRed);
    }

    /**
     *  TODO
     * @return TODO
     */
    public static @NotNull EpsilonCombinator getDefault() {
        return epsilon;
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        runner.success(new TrampolineListenerKey(index, this), null, index);
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        if (index == runner.tramp().getText().length())
            runner.success(new TrampolineListenerKey(index, this), null, index);
        else
            runner.fail(new TrampolineListenerKey(index, this), index,
                    new ParseFailureReasonEpsilon());
    }

    @Override
    public @NotNull EpsilonCombinator withHideTag(boolean hide) {
        return isHidden() == hide ? this : new EpsilonCombinator(hide, red);
    }

    @Override
    public @NotNull EpsilonCombinator withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new EpsilonCombinator(hide, red);
    }
}
