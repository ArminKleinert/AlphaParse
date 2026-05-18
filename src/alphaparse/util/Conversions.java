package alphaparse.util;

import alphaparse.Sym;
import alphaparse.result.Node;
import alphaparse.result.ParseFailureNode;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Conversions {

    private static @NotNull List<Object> toParseTreeHelper(final @NotNull List<?> o) {
        return o.stream().map(it -> switch (it) {
            case String ignored -> it;
            case ParseFailureNode ignored -> it;
            case ParseTree ignored -> it;
            case List<?> l -> toParseTree(l);
            case Map<?, ?> m -> toParseTree(m);
            default -> throw new IllegalArgumentException();
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
    public static ParseTree toParseTree(final @NotNull List<?> l) {
        if (l.isEmpty())
            return ParseTree.create(ParseTree.NULL_TAG, List.of());
        if (!(l.getFirst() instanceof Sym))
            return ParseTree.create(ParseTree.NULL_TAG.content(), toParseTreeHelper(l));
        return ParseTree.create((Sym) l.getFirst(), toParseTreeHelper(l.subList(1, l.size())));
    }

    public static ParseTree toParseTree(final @NotNull Map<?, ?> l) {
        if (l.size() != 2
                || !(l.get(Sym.sym("tag")) instanceof Sym tag)
                || !(l.get(Sym.sym("content")) instanceof List<?> content))
            throw new IllegalArgumentException("Cannot handle Map " + l);
        return ParseTree.create(tag, toParseTreeHelper(content));
    }
}
