package alphaparse.parser;

import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.reduction.ReductionType;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A class representing a choice or alternation. That is the {@code (p1 | p2)} operator in EBNF (where p1 and p2 are instances of {@link Combinator}).
 * <p>
 * Notation: {@code rule1 | rule2}
 *
 * Example
 * <pre>
 *{@code
 *         // Accepts the language {"a", "b", "ab"}
 *         var p = Alpha.parser("S : 'a' | 'b' | 'ab'");
 *         IO2.println(p.parse("a"));  // [:S, a]
 *         IO2.println(p.parse("b"));  // [:S, b]
 *         IO2.println(p.parse("ab")); // [:S, ab]
 *}
 * </pre>
 *
 * Alternatively, the {@link ParserCreationOptions} class allows an alternative notation for defining alternations:
 * <pre>
 *{@code
 *         var opts = Alpha.ParserCreationOptions
 *                 .getDefault()
 *                 .withRedefinitionOption(Grammar.RedefinitionOption.CHOICE);
 *         var p = Alpha.parser("""
 *                 S : 'a'
 *                 S : 'b'
 *                 S : 'ab'
 *                 """, opts);
 *         IO2.println(p.parse("a"));  // [:S, a]
 *         IO2.println(p.parse("b"));  // [:S, b]
 *         IO2.println(p.parse("ab")); // [:S, ab]
 * }
 * </pre>
 */
public final class ChoiceCombinator extends CombinatorWithManyParsers {
    private ChoiceCombinator(boolean hide, @NotNull ReductionType red, @NotNull List<Combinator> parsers) {
        super(hide, red, parsers);
    }

    /**
     * Creates a new instance. Instead of using this directly, use methods from {@link CombinatorFactory}.
     *
     * @param parsers The different parsers in the choice.
     * @see CombinatorFactory#choiceCombinator(List)
     */
    public ChoiceCombinator(@NotNull List<Combinator> parsers) {
        super(parsers);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        for (final @NotNull Combinator combinator : getParsers()) {
            runner.pushListener(
                    new TrampolineListenerKey(index, combinator),
                    runner.nodeListener(new TrampolineListenerKey(index, this))
            );
        }
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        for (final @NotNull Combinator parser : getParsers()) {
            runner.pushFullListener(
                    new TrampolineListenerKey(index, parser),
                    runner.nodeListener(new TrampolineListenerKey(index, this))
            );
        }
    }

    @Override
    public @NotNull ChoiceCombinator withHideTag(boolean hide) {
        return isHidden() == hide ? this : new ChoiceCombinator(hide, getReduction(), getParsers());
    }

    @Override
    public @NotNull ChoiceCombinator withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new ChoiceCombinator(isHidden(), red, getParsers());
    }

    @Override
    public @NotNull ChoiceCombinator withParsers(@NotNull List<@NotNull Combinator> parsers) {
        return new ChoiceCombinator(isHidden(), getReduction(), parsers);
    }
}
