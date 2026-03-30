package alphaparse.parser.combinator;

import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class CombinatorStringTerminal extends CombinatorTerminal {
    private long bufferedHashCode = Long.MIN_VALUE;
    private final @NotNull String string;

    public CombinatorStringTerminal(final @NotNull String string) {
        super();
        this.string = string;
    }

    public CombinatorStringTerminal(final boolean hide, final @NotNull ReductionType red, @NotNull String string) {
        super(hide, red);
        this.string = string;
    }

    public final @NotNull String getString() {
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (!getClass().equals(o.getClass())) return false;
        if (hashCode() != o.hashCode()) return false;
        var that = (CombinatorStringTerminal) o;
        if (!Objects.equals(getReduction(), that.getReduction())) return false;
        if (!Objects.equals(isHidden(), that.isHidden())) return false;
        return Objects.equals(getString(), that.getString());
    }

    @Override
    public int hashCode() {
        if (bufferedHashCode == Long.MIN_VALUE)
            bufferedHashCode = Objects.hash(getClass(), getReduction(), isHidden(), getString());
        return (int) bufferedHashCode;
    }
}
