package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * TODO
 */
public sealed abstract class CombinatorWithParser extends Combinator permits LookaheadCombinator, NegativeLookaheadCombinator, OptionalCombinator, PlusCombinator, RepetitionCombinator, CombinatorStar {
    private long bufferedHashCode = Long.MIN_VALUE;
    protected final @NotNull Combinator parser;

    CombinatorWithParser(final boolean hide, final @NotNull ReductionType red, final @NotNull Combinator parser) {
        super(hide, red);
        this.parser = parser;
    }

    CombinatorWithParser(final @NotNull Combinator parser) {
        super();
        this.parser = parser;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public @NotNull Combinator getParser() {
        return parser;
    }

    /**
     * TODO
     *
     * @param parser TODO
     * @return TODO
     */
    public abstract @NotNull CombinatorWithParser withParser(final @NotNull Combinator parser);

    @Override
    public @NotNull Combinator unhideContent() {
        return ((CombinatorWithParser) withHideTag(false)).withParser(parser.unhideContent());
    }

    @Override
    public boolean equals(Object o) {
        if (!getClass().equals(o.getClass())) return false;
        if (hashCode() != o.hashCode()) return false;
        final @NotNull var that = (CombinatorWithParser) o;
        if (!Objects.equals(getReduction(), that.getReduction())) return false;
        if (!Objects.equals(isHidden(), that.isHidden())) return false;
        return Objects.equals(getParser(), that.getParser());
    }

    @Override
    public int hashCode() {
        if (bufferedHashCode == Long.MIN_VALUE)
            bufferedHashCode = Objects.hash(getClass(), getReduction(), isHidden(), getParser());
        return (int) bufferedHashCode;
    }
}
