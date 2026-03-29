package instarun.trampoline;

import instarun.parser.combinator.Combinator;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class InstaNodeKey {
    private final int index;
    private final @NotNull Combinator parser;

    public InstaNodeKey(final int index, final @NotNull Combinator parser) {
        this.index = index;
        this.parser = parser;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        InstaNodeKey that = (InstaNodeKey) o;
        return index == that.index && Objects.equals(parser, that.parser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, parser);
    }

    @Override
    public String toString() {
        return "{index=" + index +
                ", parser=" + parser +
                '}';
    }

    public int getIndex() {
        return index;
    }

    public @NotNull Combinator getParser() {
        return parser;
    }
}
