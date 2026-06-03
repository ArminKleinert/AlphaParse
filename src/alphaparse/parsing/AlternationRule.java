package alphaparse.parsing;

import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.reduction.ReductionType;

import static alphaparse.trampoline.TrampolineListenerNode.TrampolineListenerKey;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A class representing a choice or alternation. That is the {@code (p1 | p2)} operator in EBNF (where p1 and p2 are instances of {@link Rule}).
 * <p>
 * Notation: {@code rule1 | rule2}
 * <p>
 * Example
 * <pre>
 * {@code
 *         // Accepts the language {"a", "b", "ab"}
 *         var p = Alpha.parser("S := 'a' | 'b' | 'ab'");
 *         println(p.parse("a"));  // [:S, a]
 *         println(p.parse("b"));  // [:S, b]
 *         println(p.parse("ab")); // [:S, ab]
 * }
 * </pre>
 * <p>
 * Alternatively, the {@link ParserCreationOptions} class allows an alternative notation for defining alternations:
 * <pre>
 * {@code
 *         var opts = Alpha.ParserCreationOptions
 *                 .getDefault()
 *                 .withRedefinitionOption(Grammar.RedefinitionOption.CHOICE);
 *         var p = Alpha.parser("""
 *                 S : 'a'
 *                 S : 'b'
 *                 S : 'ab'
 *                 """, opts);
 *         println(p.parse("a"));  // [:S, a]
 *         println(p.parse("b"));  // [:S, b]
 *         println(p.parse("ab")); // [:S, ab]
 * }
 * </pre>
 */
public final class AlternationRule extends RuleWithManyChildren {
    private AlternationRule(final boolean hide,
                            final @NotNull ReductionType red,
                            final @NotNull List<Rule> rules) {
        super(hide, red, rules);
    }

    /**
     * Create a new instance. Depending on the implementation, allows for buffering or create a different type of rule.
     *
     * @param rules The wrapped rules.
     * @return A rule.
     */
    public static @NotNull Rule create(final @NotNull List<Rule> rules) {
        return new AlternationRule(defaultHidden, defaultReductionType, rules);
    }

    @Override
    public void parse(final int index, final @NotNull Gll runner) {
        for (final @NotNull Rule rule : getRules()) {
            runner.pushListener(
                    new TrampolineListenerKey(index, rule),
                    runner.nodeListener(new TrampolineListenerKey(index, this))
            );
        }
    }

    @Override
    public void fullParse(final int index, final @NotNull Gll runner) {
        for (final @NotNull Rule parser : getRules()) {
            runner.pushFullListener(
                    new TrampolineListenerKey(index, parser),
                    runner.nodeListener(new TrampolineListenerKey(index, this))
            );
        }
    }

    @Override
    public @NotNull AlternationRule withHideTag(boolean hide) {
        return isHidden() == hide ? this : new AlternationRule(hide, getReduction(), getRules());
    }

    @Override
    public @NotNull AlternationRule withReduction(@NotNull ReductionType red) {
        return getReduction() == red ? this : new AlternationRule(isHidden(), red, getRules());
    }

    @Override
    public @NotNull AlternationRule withParsers(@NotNull List<@NotNull Rule> parsers) {
        return new AlternationRule(isHidden(), getReduction(), parsers);
    }
}
