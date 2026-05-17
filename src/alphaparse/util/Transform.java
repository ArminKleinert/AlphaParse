package alphaparse.util;

import alphaparse.Sym;
import alphaparse.result.AlphaParseResult;
import alphaparse.result.Node;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Transform {

    private static Object transform(
            final @NotNull Node node,
            final @NotNull Map<@NotNull Sym, @NotNull Function<@NotNull List<@NotNull Object>, Object>> transformMap
    ) {
        return switch (node) {
            case Node.NodeTreeTag nodeTreeTag -> nodeTreeTag.content();
            case Node.NodeString nodeString -> nodeString.content();
            case Node.NodeFail nodeFail -> nodeFail.content();
            case Node.NodeParseTree nodeParseTree -> transform(nodeParseTree.content(), transformMap);
        };
    }

    private static Object transform(
            final @NotNull ParseTree parseTree,
            final @NotNull Map<Sym, Function<List<Object>, Object>> transformMap
    ) {
        var tagSym = parseTree.getTag().content();
        var transformFn = transformMap.get(tagSym);
        var transformedNodes = parseTree.getContent().stream().map(node -> transform(node, transformMap)).toList();
        if (transformFn != null) {
            return transformFn.apply(transformedNodes);
        } else {
            return ParseTree.create(parseTree.getTag(), transformedNodes.stream().map(Node::of).toList());
        }
    }

    public static <T> T transform(
            final @NotNull AlphaParseResult parseResult,
            final @NotNull Map<@NotNull Sym, @NotNull Function<@NotNull List<@NotNull Object>, Object>> transformMap,
            final @NotNull Function<Object, T> finalizer
    ) {
        return finalizer.apply(transform(parseResult, transformMap));
    }

    public static Object transform(
            final @NotNull AlphaParseResult parseResult,
            final @NotNull Map<@NotNull Sym, @NotNull Function<@NotNull List<@NotNull Object>, Object>> transformMap
    ) {
        if (!(parseResult instanceof ParseTree parseTree))
            throw new IllegalArgumentException();
        final @NotNull var tagSym = parseTree.getTag().content();
        final @NotNull var transformedNodes = parseTree.getContent().stream().map(node -> transform(node, transformMap)).toList();
        final var transformFn = transformMap.get(tagSym);
        final Object result;
        if (transformFn != null) {
            result = transformFn.apply(transformedNodes);
        } else {
            result = ParseTree.create(parseTree.getTag(), transformedNodes.stream().map(Node::of).toList());
        }
        return result;
    }
}
