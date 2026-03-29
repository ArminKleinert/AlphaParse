package instarun.result.failure.failureReason;

import java.util.Objects;

public final class InstaFailureReasonCharRange {
    final int lo;
    final int hi;

    public InstaFailureReasonCharRange(final int lo, final int hi) {
        this.lo = lo;
        this.hi = hi;
    }

    public int getLo() {
        return lo;
    }

    public int getHi() {
        return hi;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InstaFailureReasonCharRange that = (InstaFailureReasonCharRange) o;
        return lo == that.lo && hi == that.hi;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lo, hi);
    }

    @Override
    public String toString() {
        if (lo == hi)
            return String.format("%%x%04x", lo);
        return String.format("%%x%04x-%04x", lo, hi);
    }
}
