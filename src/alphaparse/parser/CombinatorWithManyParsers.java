package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * TODO
 */
public sealed abstract class CombinatorWithManyParsers
        extends Combinator
        permits AlternationCombinator, CatCombinator, OrderedCombinator {
    private long bufferedHashCode = Long.MIN_VALUE;
    private final @NotNull List<@NotNull Combinator> parsers;

    protected CombinatorWithManyParsers(final @NotNull List<@NotNull Combinator> parsers) {
        super();
        this.parsers = new ArrayList<>(parsers);
    }

    protected CombinatorWithManyParsers(final @NotNull List<@NotNull Combinator> parsers,
                                        final boolean hide,
                                        final @NotNull ReductionType red) {
        super(hide, red);
        this.parsers = new ArrayList<>(parsers);
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public final @NotNull CombinatorWithManyParsers unhideContent() {
        return withHideTag(false).withParsers(parsers.stream().map(Combinator::unhideContent).toList());
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public @NotNull List<@NotNull Combinator> getParsers() {
        return parsers;
    }

    /**
     * TODO
     *
     * @param hide TODO
     * @return TODO
     */
    @Override
    public abstract @NotNull CombinatorWithManyParsers withHideTag(final boolean hide);

    /**
     * TODO
     *
     * @param red TODO
     * @return TODO
     */
    @Override
    public abstract @NotNull CombinatorWithManyParsers withReduction(final @NotNull ReductionType red);

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
