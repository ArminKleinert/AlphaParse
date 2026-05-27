package alphaparse.parsing;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.ParseFailureReason;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

///**
// * This class should almost never be used, but can have its uses occasionally.
// * <p>
// * Format: {@code "eοf"} (Careful: That is not the same as {@code "eof"})
// */
//public final class EOFCombinator extends CombinatorTerminal {
//    private static final @NotNull EOFCombinator eof = new EOFCombinator();
//
//    private EOFCombinator(final boolean hide, final @NotNull ReductionType red) {
//        super(hide, red);
//    }
//
//    private EOFCombinator() {
//        super();
//    }
//
//    /**
//     * The default is always buffered. {@link #getReduction()} and {@link #isHidden()} have their default values.
//     *
//     * @return The canonical instance of Epsilon.
//     */
//    public static @NotNull EOFCombinator getDefault() {
//        return eof;
//    }
//
//    @Override
//    public void parse(final int index, final @NotNull Gll runner) {
//        fullParse(index, runner);
//    }
//
//    @Override
//    public void fullParse(final int index, final @NotNull Gll runner) {
//        if (index == runner.tramp().getText().length())
//            runner.pushSuccessMessageWithoutValue(new TrampolineListenerNode.TrampolineListenerKey(index, this), index);
//        else
//            runner.fail(new TrampolineListenerNode.TrampolineListenerKey(index, this), index,
//                    ParseFailureReason.ofEpsilon(EpsilonCombinator.getDefault(), true));
//    }
//
//    @Override
//    public @NotNull EOFCombinator withHideTag(final boolean hide) {
//        return isHidden() == hide ? this : new EOFCombinator(hide, red);
//    }
//
//    @Override
//    public @NotNull EOFCombinator withReduction(final @NotNull ReductionType red) {
//        return getReduction() == red ? this : new EOFCombinator(hide, red);
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (!(o instanceof EOFCombinator that)) return false;
//        if (this == that) return true;
//        return hide == that.hide && Objects.equals(red, that.red);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(hide, red);
//    }
//}
