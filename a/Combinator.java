package alphaparse.parser;

import alphaparse.Print;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

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

    public abstract void parse(final int index, final @NotNull Gll runner);

    public abstract void fullParse(final int index, final @NotNull Gll runner);

    public final boolean isHidden() {
        return hide;
    }

    public final @NotNull ReductionType getReduction() {
        return red;
    }

    public abstract @NotNull Combinator withHideTag(final boolean hide);

    public abstract @NotNull Combinator withReduction(final @NotNull ReductionType red);

    public final @NotNull Combinator enableHideTag() {
        return withHideTag(true);
    }

    public @NotNull Combinator unhideContent() {
        return withHideTag(false);
    }

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
