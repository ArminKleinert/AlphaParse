package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import org.jetbrains.annotations.NotNull;

public final class ParseFailureReasonChar extends ParseFailureReason {
    final int lo;
    final int hi;

    public ParseFailureReasonChar(final int lo, final int hi, final boolean full) {
        super(full);
        this.lo = lo;
        this.hi = hi;
    }

    public ParseFailureReasonChar(final int lo, final int hi) {
        this(lo, hi, false);
    }

    public int getLo() {
        return lo;
    }

    public int getHi() {
        return hi;
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("char");
    }

    @Override
    public @NotNull AlphaFailureReasonCharRange getExpecting() {return new AlphaFailureReasonCharRange(lo, hi);}
}
