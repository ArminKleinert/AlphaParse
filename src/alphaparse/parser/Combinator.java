package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

public sealed interface Combinator permits CombinatorTerminal, NonTerminalCombinator, CombinatorWithManyParsers, CombinatorWithParser {
    boolean defaultHidden = false;
    ReductionType defaultRed = ReductionType.nullReduction();

    void parse(final int index, final @NotNull Gll runner);

    void fullParse(final int index, final @NotNull Gll runner);

    boolean hide();

    @NotNull ReductionType red();

    @NotNull Combinator withHideTag(final boolean hide);

    @NotNull Combinator withReduction(final @NotNull ReductionType red);

    default boolean isHidden() {
        return hide();
    }

    default @NotNull ReductionType getReduction() {
        return red();
    }

    default @NotNull Combinator enableHideTag() {
        return withHideTag(true);
    }

    default @NotNull Combinator unhideContent() {
        return withHideTag(false);
    }

    default @NotNull Combinator hideTag() {
        return withReduction(ReductionType.rawNonTerminalReduction());
    }
}
