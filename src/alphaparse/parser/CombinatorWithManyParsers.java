package alphaparse.parser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public sealed interface CombinatorWithManyParsers extends Combinator permits CombinatorChoice, CombinatorConcatenation, CombinatorOrderedChoice {
    List<Combinator> parsers();

    default List<Combinator> getParsers() {
        return parsers();
    }

    @NotNull
    default CombinatorWithManyParsers unhideContent() {
        return ((CombinatorWithManyParsers) withHideTag(false)).withParsers(parsers().stream().map(Combinator::unhideContent).toList());
    }

    @NotNull CombinatorWithManyParsers withParsers(final @NotNull List<@NotNull Combinator> parsers);
}
