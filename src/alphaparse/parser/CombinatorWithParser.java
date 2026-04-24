package alphaparse.parser;

import org.jetbrains.annotations.NotNull;

public sealed interface CombinatorWithParser extends Combinator permits LookaheadCombinator, NegativeLookaheadCombinator, OptionalCombinator, PlusCombinator, RepetitionCombinator, CombinatorStar {
    Combinator parser();

    default Combinator getParser() {
        return parser();
    }

    @NotNull CombinatorWithParser withParser(final @NotNull Combinator parser);

    default @NotNull Combinator unhideContent() {
        return ((CombinatorWithParser) withHideTag(false)).withParser(parser().unhideContent());
    }
}
