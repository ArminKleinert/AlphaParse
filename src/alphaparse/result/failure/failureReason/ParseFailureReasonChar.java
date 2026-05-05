package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the failure to match an ABNF Unicode character.
 *
 * @see alphaparse.parser.TerminalUnicodeCharCombinator
 */
public final class ParseFailureReasonChar extends ParseFailureReason {
    final int lo;
    final int hi;

    /**
     * Creates a new instance.
     *
     * @param lo   The lowest codepoint.
     * @param hi   The highest codepoint.
     * @param full Whether the entire string was supposed to be covered by the combinator.
     */
    public ParseFailureReasonChar(final int lo, final int hi, final boolean full) {
        super(full);
        this.lo = lo;
        this.hi = hi;
    }

    /**
     * Creates a new instance, assuming that the combinator was supposed to cover the whole string.
     *
     * @param lo The lowest codepoint.
     * @param hi The highest codepoint.
     */
    public ParseFailureReasonChar(final int lo, final int hi) {
        this(lo, hi, false);
    }

    /**
     * The lowest codepoint.
     *
     * @return The lowest codepoint.
     */
    public int getLo() {
        return lo;
    }

    /**
     * The highest codepoint.
     *
     * @return The highest codepoint.
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
     *
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
    public @NotNull AlphaFailureReasonCharRange getExpecting() {
        return new AlphaFailureReasonCharRange(lo, hi);
    }
}
