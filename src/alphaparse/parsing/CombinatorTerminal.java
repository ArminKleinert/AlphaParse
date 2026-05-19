package alphaparse.parsing;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

/**
 * A general type of {@link Combinator} for terminals (epsilon, regex, string, char)
 */
public abstract sealed class CombinatorTerminal
        extends Combinator
        permits EpsilonCombinator, TerminalRegexpCombinator, TerminalStringCombinator, TerminalUnicodeCharCombinator {
    protected CombinatorTerminal(final boolean hide, final @NotNull ReductionType red) {
        super(hide, red);
    }

    protected CombinatorTerminal() {
        super();
    }
}
