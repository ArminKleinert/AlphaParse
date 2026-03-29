package instarun.parser.combinator;

import instarun.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class CombinatorWithManyParsers extends Combinator {
    private long bufferedHashCode = Long.MIN_VALUE;
    private final @NotNull List<@NotNull Combinator> parsers;

    public CombinatorWithManyParsers(final @NotNull List<@NotNull Combinator> parsers) {
        super();
        this.parsers = new ArrayList<>(parsers);
    }

    public CombinatorWithManyParsers(final @NotNull List<@NotNull Combinator> parsers,
                                     final boolean hide,
                                     final @NotNull ReductionType red) {
        super(hide, red);
        this.parsers = new ArrayList<>(parsers);
    }

    public final @NotNull CombinatorWithManyParsers unhideContent() {
        return withHideTag(false).withParsers(parsers.stream().map(Combinator::unhideContent).toList());
    }

    public @NotNull List<@NotNull Combinator> getParsers() {
        return parsers;
    }

    @Override
    public abstract @NotNull CombinatorWithManyParsers withHideTag(final boolean hide);

    @Override
    public abstract @NotNull CombinatorWithManyParsers withReduction(final @NotNull ReductionType red);

    public abstract @NotNull CombinatorWithManyParsers withParsers(final @NotNull List<@NotNull Combinator> parsers);

    @Override
    public boolean equals(Object o) {
        if (!getClass().equals(o.getClass())) return false;
        if (hashCode() != o.hashCode()) return false;
        var that = (CombinatorWithManyParsers) o;
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
