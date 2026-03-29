package instarun.result.failure.failureReason;

import instarun.Keyword;
import org.jetbrains.annotations.NotNull;

public final class InstaFailureReasonChar extends InstaFailureReason {
    final int lo;
    final int hi;

    public InstaFailureReasonChar(final int lo, final int hi, final boolean full) {
        super(full);
        this.lo = lo;
        this.hi = hi;
    }

    public InstaFailureReasonChar(final int lo, final int hi) {
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
    public @NotNull InstaFailureReasonCharRange getExpecting() {return new InstaFailureReasonCharRange(lo, hi);}
}
