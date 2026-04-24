package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

/**
 *  TODO
 */
public sealed interface Combinator permits CombinatorTerminal, NonTerminalCombinator, CombinatorWithManyParsers, CombinatorWithParser {
    /**
     *  Default value for {@link Combinator#hide()}.
     */
    boolean defaultHidden = false;

    /**
     *  Default value for {@link Combinator#red()}
     */
    ReductionType defaultRed = ReductionType.nullReduction();

    /**
     *  TODO
     * @param index TODO
     * @param runner TODO
     */
    void parse(final int index, final @NotNull Gll runner);

    /**
     *  TODO
     * @param index TODO
     * @param runner TODO
     */
    void fullParse(final int index, final @NotNull Gll runner);

    /**
     *  TODO
     * @return TODO
     */
    boolean hide();

    /**
     *  TODO
     * @return TODO
     */
    @NotNull ReductionType red();

    /**
     *  TODO
     * @param hide TODO
     * @return TODO
     */
    @NotNull Combinator withHideTag(final boolean hide);

    /**
     *  TODO
     * @param red TODO
     * @return TODO
     */
    @NotNull Combinator withReduction(final @NotNull ReductionType red);

    /**
     *  TODO
     * @return TODO
     */
    default boolean isHidden() {
        return hide();
    }

    /**
     *  TODO
     * @return TODO
     */
    default @NotNull ReductionType getReduction() {
        return red();
    }

    /**
     *  TODO
     * @return TODO
     */
    default @NotNull Combinator enableHideTag() {
        return withHideTag(true);
    }

    /**
     *  TODO
     * @return TODO
     */
    default @NotNull Combinator unhideContent() {
        return withHideTag(false);
    }

    /**
     *  TODO
     * @return TODO
     */
    default @NotNull Combinator hideTag() {
        return withReduction(ReductionType.rawNonTerminalReduction());
    }
}
