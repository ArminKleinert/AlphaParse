package alphaparse.result.failure.failureReason;

import alphaparse.Keyword;
import alphaparse.parser.Combinator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * TODO
 */
public final class ParseFailureReasonExpectParser extends ParseFailureReason {
    private final @NotNull Combinator p;

    /**
     * TODO
     *
     * @param full      TODO
     * @param p TODO
     */
    public ParseFailureReasonExpectParser(final boolean full, final @NotNull Combinator p) {
        super(full);
        this.p = p;
    }

    @Override
    public @NotNull Keyword getTag() {
        return Keyword.intern("rep");
    }

    @Override
    public @NotNull Combinator getExpecting() {
        return p;
    }
}