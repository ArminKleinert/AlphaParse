package alphaparse.parser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 *  TODO
 */
public sealed interface CombinatorWithManyParsers extends Combinator permits ChoiceCombinator, ConcatCombinator, OrderedChoiceCombinator {
    /**
     *  TODO
     * @return TODO
     */
 List<Combinator> parsers();

    /**
     *  TODO
     * @return TODO
     */
 default List<Combinator> getParsers() {
 return parsers();
 }

 @NotNull
 default CombinatorWithManyParsers unhideContent() {
 return ((CombinatorWithManyParsers) withHideTag(false)).withParsers(parsers().stream().map(Combinator::unhideContent).toList());
 }

    /**
     *  TODO
     * @param parsers TODO
     * @return TODO
     */
 @NotNull CombinatorWithManyParsers withParsers(final @NotNull List<@NotNull Combinator> parsers);
 }