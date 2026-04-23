package alphaparse.parser;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

/**
 * TODO
 */
public sealed abstract class CombinatorTerminal extends Combinator permits EpsilonCombinator, RegexpTerminal, StringTerminal, UnicodeCharTerminal {
    protected CombinatorTerminal() {
        super();
    }

    protected CombinatorTerminal(final boolean hide, final @NotNull ReductionType red) {
        super(hide, red);
    }
}
