package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
public abstract sealed class CombinatorTerminal extends Combinator permits EpsilonCombinator, TerminalRegexpCombinator, TerminalStringCombinator, TerminalUnicodeCharCombinator {
    protected CombinatorTerminal(final boolean hide, final @NotNull ReductionType red) {
        super(hide, red);
    }

    protected CombinatorTerminal() {
        super();
    }
}
