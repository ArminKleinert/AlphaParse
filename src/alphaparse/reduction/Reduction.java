package alphaparse.reduction;

import alphaparse.Keyword;
import alphaparse.flat.AutoFlattenSeq;
import alphaparse.parser.Grammar;
import alphaparse.parser.combinator.Combinator;
import alphaparse.parsetree.Node;
import alphaparse.result.ParseTree;
import alphaparse.result.ParseFailureNode;
import alphaparse.result.TotalParsesFailureNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Reduction {
    public static final @NotNull ReductionType rawNonTerminalReduction
            = new ReductionType(ParseTree.NULL_TAG, ReductionType.ReductionTypesAvailable.RAW, true);
    public static final @NotNull ReductionType nullReduction
            = new ReductionType(ParseTree.NULL_TAG, ReductionType.ReductionTypesAvailable.NONE, true);

    private Reduction() {
    }

    // Argument is LazySeq, PersistentVector, Cons, or null
    public static <T> boolean isSingleton(final List<T> list) {
        if (list == null) return false;
        return list.size() == 1;
    }

    public static ReductionType defaultNonRawReduction(final @NotNull Keyword key) {
        return new ReductionType(key, ReductionType.ReductionTypesAvailable.defaultType);
    }

    public static @NotNull ReductionType nonTerminalReduction(final @NotNull Keyword key, final @NotNull ReductionType.ReductionTypesAvailable type) {
        return new ReductionType(key, type);
    }

    public static @NotNull Grammar applyStandardReductions(final @NotNull Grammar grammar) {
        final List<Map.Entry<Keyword, Combinator>> m = new ArrayList<>();
        grammar.forEach((prodKey, pars) -> {
            if (pars.getReduction().getReductionType() == ReductionType.ReductionTypesAvailable.NONE)
                pars = pars.withReduction(defaultNonRawReduction(prodKey));

            m.add(Grammar.entry(prodKey, pars));
        });
        return Grammar.fromProductions(m);
    }

    public static @NotNull ParseTree applyReduction(final @NotNull ReductionType f, final Object result) {
        final @NotNull List<Node> afs = switch (result) {
            case null -> List.of();
            case AutoFlattenSeq<?> objects -> objects.toNodes();
            case String ignored -> List.of(Node.of(result));
            case TotalParsesFailureNode ignored -> List.of(Node.of(result));
            case ParseFailureNode ignored -> List.of(Node.of(result));
            case ParseTree ignored -> List.of(Node.of(result));
            default -> throw new IllegalArgumentException(result.getClass().toString());
        };
        return new ParseTree(new Node.NodeTreeTag(f.getKey()), afs);
    }
}
