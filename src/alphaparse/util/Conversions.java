package alphaparse.util;

import alphaparse.Sym;
import alphaparse.result.Node;
import alphaparse.result.ParseFailureNode;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Methods for converting lists and maps to {@link ParseTree}.
 */
public final class Conversions {
    private Conversions() {
    }

    private static @NotNull List<Object> toParseTreeHelper(final @NotNull List<?> o) {
        return o.stream().map(it -> {
            if (it instanceof String || it instanceof ParseFailureNode || it instanceof ParseTree) return it;
            else if (it instanceof List<?>) return toParseTree((List<?>)it);
            else if (it instanceof Map<?, ?>) return toParseTree((Map<?, ?>)it);
            else throw new IllegalArgumentException();
        }).toList();
    }

    /**
     * Transforms Lists into parse trees.
     * <ul>
     *     <li>Format {@code [Sym, ...]} becomes an equivalent parse tree.</li>
     *     <li>Format {@code [...]} (where the first is not a {@link Sym}) becomes a parse tree with the tag {@link ParseTree#NULL_TAG}.</li>
     * </ul>
     * All elements after the first must be Maps, Lists, or any type which have wrappers in the {@link Node} interface.
     *
     * @param l The list.
     * @return A parse tree.
     */
    public static @NotNull ParseTree toParseTree(final @NotNull List<?> l) {
        if (l.isEmpty())
            return ParseTree.create(ParseTree.NULL_TAG.content(), List.of());
        if (!(l.get(0) instanceof Sym))
            return ParseTree.create(ParseTree.NULL_TAG.content(), toParseTreeHelper(l));
        return ParseTree.create((Sym) l.get(0), toParseTreeHelper(l.subList(1, l.size())));
    }

    /**
     * Converts a Map to a ParseTree. The format is as follows: {@code {:tag Sym, :content List<Object>}}
     *
     * @param m The map.
     * @return A parse tree.
     */
    public static @NotNull ParseTree toParseTree(final @NotNull Map<?, ?> m) {
        if (m.size() != 2
                || !(m.get(Sym.sym("tag")) instanceof Sym tag)
                || !(m.get(Sym.sym("content")) instanceof List<?> content))
            throw new IllegalArgumentException("Cannot handle Map " + m);
        return ParseTree.create(tag, toParseTreeHelper(content));
    }
}
