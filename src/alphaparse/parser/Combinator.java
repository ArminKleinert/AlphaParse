package alphaparse.parser;

import alphaparse.Print;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
public sealed abstract class Combinator permits CombinatorTerminal, CombinatorWithManyParsers, CombinatorWithParser, NonTerminal {
    private final boolean hide;
    private final @NotNull ReductionType red;

    protected Combinator(final boolean hide, final @NotNull ReductionType red) {
        this.hide = hide;
        this.red = red;
    }

    protected Combinator() {
        this(false, ReductionType.nullReduction());
    }

    /**
     * TODO
     *
     * @param index  TODO
     * @param runner TODO
     */
    public abstract void parse(final int index, final @NotNull Gll runner);

    /**
     * TODO
     *
     * @param index  TODO
     * @param runner TODO
     */
    public abstract void fullParse(final int index, final @NotNull Gll runner);

    /**
     * TODO
     *
     * @return TODO
     */
    public final boolean isHidden() {
        return hide;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public final @NotNull ReductionType getReduction() {
        return red;
    }

    /**
     * TODO
     *
     * @param hide TODO
     * @return TODO
     */
    public abstract @NotNull Combinator withHideTag(final boolean hide);

    /**
     * TODO
     *
     * @param red TODO
     * @return TODO
     */
    public abstract @NotNull Combinator withReduction(final @NotNull ReductionType red);

    /**
     * TODO
     *
     * @return TODO
     */
    public final @NotNull Combinator enableHideTag() {
        return withHideTag(true);
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public @NotNull Combinator unhideContent() {
        return withHideTag(false);
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public final @NotNull Combinator hideTag() {
        return withReduction(ReductionType.rawNonTerminalReduction());
    }

    @Override
    public String toString() {
        return Print.combinatorsToString(this);
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
