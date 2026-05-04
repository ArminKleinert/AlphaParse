package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

/**
 *  TODO
 */
public final class ParseFailureReasonChar extends ParseFailureReason {
    final int lo;
    final int hi;

    /**
     *  TODO
     * @param lo TODO
     * @param hi TODO
     * @param full TODO
     */
    public ParseFailureReasonChar(final int lo, final int hi, final boolean full) {
        super(full);
        this.lo = lo;
        this.hi = hi;
    }

    /**
     *  TODO
     * @param lo TODO
     * @param hi TODO
     */
    public ParseFailureReasonChar(final int lo, final int hi) {
        this(lo, hi, false);
    }

    /**
     *  TODO
     * @return TODO
     */
    public int getLo() {
        return lo;
    }

    /**
     *  TODO
     * @return TODO
     */
    public int getHi() {
        return hi;
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("char");
    }

    /**
     * This class just represents the int-pair required by the {@link ParseFailureReasonChar} class when printing the reason. It holds the high and low values of a char range, as needed for a {@link alphaparse.parser.TerminalUnicodeCharCombinator}.
     * @param lo Lowest possible codepoint.
     * @param hi Highest possible codepoint.
     */
    public record AlphaFailureReasonCharRange(int lo, int hi) {
        @Override
        public @NotNull String toString() {
            if (lo == hi)
                return String.format("%%x%04x", lo);
            return String.format("%%x%04x-%04x", lo, hi);
        }
    }
    @Override
    public @NotNull AlphaFailureReasonCharRange getExpecting() {return new AlphaFailureReasonCharRange(lo, hi);}
}
