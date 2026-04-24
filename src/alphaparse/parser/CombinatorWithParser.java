package alphaparse.parser;

import org.jetbrains.annotations.NotNull;

/**
 *  TODO
 */
public sealed interface CombinatorWithParser extends Combinator permits LookaheadCombinator, NegativeLookaheadCombinator, OptionalCombinator, PlusCombinator, RepetitionCombinator, CombinatorStar {
    /**
     *  TODO
     * @return TODO
     */
    Combinator parser();

    /**
     *  TODO
     * @return TODO
     */
    default Combinator getParser() {
        return parser();
    }

    /**
     *  TODO
     * @param parser TODO
     * @return TODO
     */
    @NotNull CombinatorWithParser withParser(final @NotNull Combinator parser);

    @Override
    default @NotNull Combinator unhideContent() {
        return ((CombinatorWithParser) withHideTag(false)).withParser(parser().unhideContent());
    }
}
