package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * TODO
 */
public sealed abstract class CombinatorWithManyParsers extends Combinator permits ChoiceCombinator, ConcatCombinator, OrderedChoiceCombinator {
    private long bufferedHashCode = Long.MIN_VALUE;
    protected final @NotNull List<Combinator> parsers;

    protected CombinatorWithManyParsers(final boolean hide, final @NotNull ReductionType red, final @NotNull List<Combinator> parsers) {
        super(hide, red);
        this.parsers = parsers;
    }

    protected CombinatorWithManyParsers(final @NotNull List<Combinator> parsers) {
        super();
        this.parsers = parsers;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public List<Combinator> parsers() {
        return parsers;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public @NotNull List<Combinator> getParsers() {
        return parsers();
    }

    @NotNull
    public CombinatorWithManyParsers unhideContent() {
        return ((CombinatorWithManyParsers) withHideTag(false)).withParsers(parsers().stream().map(Combinator::unhideContent).toList());
    }

    /**
     * TODO
     *
     * @param parsers TODO
     * @return TODO
     */
    public abstract @NotNull CombinatorWithManyParsers withParsers(final @NotNull List<@NotNull Combinator> parsers);

    @Override
    public boolean equals(Object o) {
        if (!getClass().equals(o.getClass())) return false;
        if (hashCode() != o.hashCode()) return false;
        final @NotNull var that = (CombinatorWithManyParsers) o;
        if (!Objects.equals(getReduction(), that.getReduction())) return false;
        if (!Objects.equals(isHidden(), that.isHidden())) return false;
        return Objects.equals(getParsers(), that.getParsers());
    }

    @Override
    public int hashCode() {
        if (bufferedHashCode == Long.MIN_VALUE)
            bufferedHashCode = Objects.hash(getClass(), getReduction(), isHidden(), getParsers());
        return (int) bufferedHashCode;
    }
}