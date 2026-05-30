package alphaparse.parsing;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

public abstract sealed class SimpleCombinator extends Combinator permits CombinatorTerminal, NonTerminalCombinator, TerminalSpecialSequenceCombinator {
    protected SimpleCombinator(final boolean hide, final @NotNull ReductionType red) {
        super(hide, red);
    }

    protected SimpleCombinator() {
        super();
    }
}
