package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonEpsilon;
import org.jetbrains.annotations.NotNull;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

public record CombinatorEpsilon(
        boolean hide,
        @NotNull ReductionType red) implements CombinatorTerminal {

    private static final @NotNull CombinatorEpsilon epsilon = new CombinatorEpsilon();

    private CombinatorEpsilon() {
        this(defaultHidden, defaultRed);
    }

    public static @NotNull CombinatorEpsilon getDefault() {
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
    public @NotNull CombinatorEpsilon withHideTag(boolean hide) {
        return isHidden() == hide ? this : new CombinatorEpsilon(hide, red);
    }

    @Override
    public @NotNull CombinatorEpsilon withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new CombinatorEpsilon(hide, red);
    }
}
