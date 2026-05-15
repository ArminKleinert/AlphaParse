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

    /*
(defn- hiccup-transform
  [transform-map parse-tree]
  (if (and (sequential? parse-tree) (seq parse-tree))
    (if-let [transform (transform-map (first parse-tree))]
      (merge-meta
        (apply transform (map (partial hiccup-transform transform-map)
                              (next parse-tree)))
        (meta parse-tree))
      (with-meta
        (into [(first parse-tree)]
              (map (partial hiccup-transform transform-map)
                   (next parse-tree)))
        (meta parse-tree)))
    parse-tree))
     */

    private static Object transform(
            final @NotNull Node node,
            final @NotNull Map<Sym, Function<List<Object>, Object>> transformMap
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
            final @NotNull Map<Sym, Function<List<Object>, Object>> transformMap,
            final @NotNull Function<Object, T> finalizer
    ) {
        if (!(parseResult instanceof ParseTree parseTree))
            throw new IllegalArgumentException();
        final @NotNull var tagSym = parseTree.getTag().content();
        final @NotNull var transformedNodes = parseTree.getContent().stream().map(node -> transform(node, transformMap)).toList();
        final var transformFn = transformMap.get(tagSym);
        final Object result ;
        if (transformFn != null) {
            result = transformFn.apply(transformedNodes);
        } else {
            result= ParseTree.create(parseTree.getTag(), transformedNodes.stream().map(Node::of).toList());
        }
        return finalizer.apply(result        );
    }

    public static Object transform(
            final @NotNull AlphaParseResult parseResult,
            final @NotNull Map<Sym, Function<List<Object>, Object>> transformMap
    ) {
        if (!(parseResult instanceof ParseTree parseTree))
            throw new IllegalArgumentException();
        final @NotNull var tagSym = parseTree.getTag().content();
        final @NotNull var transformedNodes = parseTree.getContent().stream().map(node -> transform(node, transformMap)).toList();
        final var transformFn = transformMap.get(tagSym);
        final Object result ;
        if (transformFn != null) {
            result = transformFn.apply(transformedNodes);
        } else {
            result= ParseTree.create(parseTree.getTag(), transformedNodes.stream().map(Node::of).toList());
        }
        return result;
    }
}
