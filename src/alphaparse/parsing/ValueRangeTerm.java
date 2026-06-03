package alphaparse.parsing;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import alphaparse.reduction.ReductionType;
import alphaparse.result.failure.ParseFailureReason;
import alphaparse.trampoline.TrampolineListenerNode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents an ABNF value range.
 */
public final class ValueRangeTerm extends Terminal {
    private final int lo;
    private final int hi;

    private ValueRangeTerm(final boolean hide, final @NotNull ReductionType red, final int lo, final int hi) {
        super(hide, red);
        if (lo > hi) throw new IllegalArgumentException();
        this.lo = lo;
        this.hi = hi;
    }

    /**
     * Creates a new instance..
     *
     * @param lo The lowest codepoint.
     * @param hi The highest codepoint.
     * @throws IllegalArgumentException if the minimum codepoint value is greater than the maximum.
     */
    public ValueRangeTerm(final int lo, final int hi) {
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
            runner.fail(nodeKey, index, ParseFailureReason.ofUnicodeChar(this, false));
            return;
        }

        if (hi <= 0xFFFF) {
            final int code = text.charAt(index); // (int (.charAt text index))
            if (lo <= code && code <= hi) {
                runner.pushSuccessMessage(nodeKey, String.valueOf((char)code), index + 1);
            } else {
                runner.fail(nodeKey, index, ParseFailureReason.ofUnicodeChar(this, false));
            }
            return;
        }

        final int codePoint = Character.codePointAt(text, index);
        final @NotNull String charString = new String(Character.toChars(codePoint));
        if (lo <= codePoint && codePoint <= hi) {
            runner.pushSuccessMessage(nodeKey, charString, index + charString.length());
        } else {
            runner.fail(nodeKey, index, ParseFailureReason.ofUnicodeChar(this, false));
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
            runner.fail(nodeKeyForThis, index, ParseFailureReason.ofUnicodeChar(this, true));
            return;
        }

        if (hi <= 0xFFFF) {
            final var code = (int) text.charAt(index);
            if (index + 1 == end && lo <= code && code <= hi) {
                runner.pushSuccessMessage(nodeKeyForThis, Character.toString(code), end);
            } else {
                runner.fail(nodeKeyForThis, index, ParseFailureReason.ofUnicodeChar(this, true));
            }
            return;
        }

        final int codePoint = Character.codePointAt(text, index);
        final @NotNull String charString = new String(Character.toChars(codePoint));

        if ((index + charString.length()) == end && lo <= codePoint && codePoint <= hi) {
            runner.pushSuccessMessage(nodeKeyForThis, charString, end);
        } else {
            runner.fail(nodeKeyForThis, index, ParseFailureReason.ofUnicodeChar(this, true));
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
    public @NotNull ValueRangeTerm withHideTag(final boolean hide) {
        return isHidden() == hide ? this : new ValueRangeTerm(hide, red, lo, hi);
    }

    @Override
    public @NotNull ValueRangeTerm withReduction(final @NotNull ReductionType red) {
        return getReduction() == red ? this : new ValueRangeTerm(hide, red, lo, hi);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ValueRangeTerm that)) return false;
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
