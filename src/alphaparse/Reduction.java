package alphaparse;

import alphaparse.flat.AutoFlattenSeq;
import alphaparse.parser.Grammar;
import alphaparse.parser.combinator.Combinator;
import alphaparse.reduction.ReductionType;
import alphaparse.result.Node;
import alphaparse.result.ParseTree;
import alphaparse.result.ParseFailureNode;
import alphaparse.result.TotalParsesFailureNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class Reduction {

    private Reduction() {
    }

    static @NotNull Grammar applyStandardReductions(final @NotNull Grammar grammar) {
        final List<Map.Entry<Keyword, Combinator>> m = new ArrayList<>();
        grammar.forEach((prodKey, pars) -> {
            if (pars.getReduction().getReductionType() == ReductionType.ReductionTypesAvailable.NONE)
                pars = pars.withReduction(ReductionType.defaultNonRawReduction(prodKey));

            m.add(Grammar.entry(prodKey, pars));
        });
        return Grammar.fromProductions(m);
    }

    static @NotNull ParseTree applyReduction(final @NotNull ReductionType f, final Object result) {
        final @NotNull var afs = switch (result) {
            case null -> List.<Node>of();
            case AutoFlattenSeq<?> objects -> objects.toNodes();
            case String ignored -> List.of(Node.of(result));
            case TotalParsesFailureNode ignored -> List.of(Node.of(result));
            case ParseFailureNode ignored -> List.of(Node.of(result));
            case ParseTree ignored -> List.of(Node.of(result));
            default -> throw new IllegalArgumentException(result.getClass().toString());
        };
        return ParseTree.create(new Node.NodeTreeTag(f.getKey()), afs);
    }
}
