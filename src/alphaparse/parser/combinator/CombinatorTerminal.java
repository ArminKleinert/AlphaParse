package alphaparse.parser.combinator;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

public abstract class CombinatorTerminal extends Combinator {
    public CombinatorTerminal() {
        super();
    }

    public CombinatorTerminal(final boolean hide, final @NotNull ReductionType red) {
        super(hide, red);
    }
}
