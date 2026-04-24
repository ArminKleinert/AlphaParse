package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * TODO
 */
public abstract sealed class Combinator permits CombinatorTerminal, NonTerminalCombinator, CombinatorWithManyParsers, CombinatorWithParser {
    /**
     * Default value for {@link Combinator#isHidden()}.
     */
    static final boolean defaultHidden = false;

    /**
     * Default value for {@link Combinator#getReduction()}
     */
    static final ReductionType defaultRed = ReductionType.nullReduction();

    protected final boolean hide;
    protected final @NotNull ReductionType red;

    protected Combinator(final boolean hide, final @NotNull ReductionType red) {
        this.hide = hide;
        this.red = red;
    }

    protected Combinator() {
        this(defaultHidden, defaultRed);
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
    public boolean isHidden() {
        return hide;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public @NotNull ReductionType getReduction() {
        return red;
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public @NotNull Combinator enableHideTag() {
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
    public @NotNull Combinator hideTag() {
        return withReduction(ReductionType.rawNonTerminalReduction());
    }

    // Force children to override this.
    @Override
    public abstract boolean equals(Object o);

    // Force children to override this.
    @Override
    public abstract int hashCode();
}
