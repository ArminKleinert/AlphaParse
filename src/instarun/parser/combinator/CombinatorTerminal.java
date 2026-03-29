package instarun.parser.combinator;

import instarun.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CombinatorTerminal extends Combinator {
    public CombinatorTerminal() {
        super();
    }

    public CombinatorTerminal(final boolean hide, final @NotNull ReductionType red) {
        super(hide, red);
    }
}
