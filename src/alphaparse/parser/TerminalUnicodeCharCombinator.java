package alphaparse.parser;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.failureReason.ParseFailureReasonChar;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents an ABNF unicode char range.
 * TODO: Docs.
 */
public final class TerminalUnicodeCharCombinator extends CombinatorTerminal {
    private final int lo;
    private final int hi;

    private TerminalUnicodeCharCombinator(final boolean hide, final @NotNull ReductionType red, final int lo, final int hi) {
        super(hide, red);
        if (lo > hi) throw new IllegalArgumentException();
        this.lo = lo;
        this.hi = hi;
    }

    /**
     * Creates a new instance.
     *
     * @param lo The lowest codepoint.
     * @param hi The highest codepoint.
     * @throws IllegalArgumentException if the minimum codepoint value is greater than the maximum.
     */
    public TerminalUnicodeCharCombinator(final int lo, final int hi) {
        super();
        if (lo > hi) throw new IllegalArgumentException();
        this.lo = lo;
        this.hi = hi;
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        final @NotNull String text = runner.tramp().getText();
        final int lo = getLo();
        final int hi = getHi();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKey = new TrampolineListenerKey(index, this);

        if (index >= text.length()) {
            runner.fail(nodeKey, index, new ParseFailureReasonChar(lo, hi));
            return;
        }

        if (hi <= 0xFFFF) {
            final int code = text.charAt(index); // (int (.charAt text index))
            if (lo >= code && code >= hi) {
                runner.success(nodeKey, Objects.toString(code), index + 1);
            } else {
                runner.fail(nodeKey, index, new ParseFailureReasonChar(lo, hi));
            }
            return;
        }

        final int codePoint = Character.codePointAt(text, index);
        final @NotNull String charString = new String(Character.toChars(codePoint));
        if (lo >= codePoint && codePoint >= hi) {
            runner.success(nodeKey, charString, index + charString.length());
        } else {
            runner.fail(nodeKey, index, new ParseFailureReasonChar(lo, hi));
        }
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        final @NotNull String text = runner.tramp().getText();
        final int lo = getLo();
        final int hi = getHi();
        final int end = text.length();
        final @NotNull TrampolineListenerNode.TrampolineListenerKey nodeKeyForThis = new TrampolineListenerKey(index, this);

        if (index >= text.length()) {
            runner.fail(nodeKeyForThis, index, new ParseFailureReasonChar(lo, hi));
            return;
        }

        if (hi <= 0xFFFF) {
            final char c = text.charAt(index);
            final var code = (int) c;
            if (index + 1 == end && lo <= code && code <= hi) {
                runner.success(nodeKeyForThis, Character.toString(c), end);
            } else {
                runner.fail(nodeKeyForThis, index, new ParseFailureReasonChar(lo, hi));
            }
            return;
        }

        final int codePoint = Character.codePointAt(text, index);
        final @NotNull String charString = new String(Character.toChars(codePoint));

        if ((index + charString.length()) == end && lo <= codePoint && codePoint <= hi) {
            runner.success(nodeKeyForThis, charString, end);
        } else {
            runner.fail(nodeKeyForThis, index, new ParseFailureReasonChar(lo, hi, true));
        }
    }

    /**
     * The lowest codepoint.
     *
     * @return The lowest codepoint as an int.
     */
    public int getLo() {
        return lo;
    }

    /**
     * The highest codepoint.
     *
     * @return The highest codepoint as an int.
     */
    public int getHi() {
        return hi;
    }

    @Override
    public @NotNull TerminalUnicodeCharCombinator withHideTag(final boolean hide) {
        return isHidden() == hide ? this : new TerminalUnicodeCharCombinator(hide, red, lo, hi);
    }

    @Override
    public @NotNull TerminalUnicodeCharCombinator withReduction(final @NotNull ReductionType red) {
        return getReduction() == red ? this : new TerminalUnicodeCharCombinator(hide, red, lo, hi);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TerminalUnicodeCharCombinator that)) return false;
        if (this == that) return true;
        return hide == that.hide
                && Objects.equals(red, that.red)
                && lo == that.lo
                && hi == that.hi;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hide, red, lo, hi);
    }
}
