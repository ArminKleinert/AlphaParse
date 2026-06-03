package alphaparse;

import alphaparse.grammar.Grammar;
import alphaparse.parser.*;
import alphaparse.parsing.*;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Helpers for things converting things to strings.
 */
public final class Print {
    private Print() {
    }

    private static @NotNull String parenForTags(
            final @NotNull Predicate<@NotNull Rule> tags,
            final boolean hidden,
            final @NotNull Rule parser) {
        if (!hidden && tags.test(parser)) return "(" + ruleToString(parser, false) + ")";
        return ruleToString(parser, false);
    }

    private static @NotNull String parenForCompound(final boolean hidden, final @NotNull Rule parser) {
        return parenForTags(
                (rule) -> rule instanceof RuleWithManyChildren,
                hidden, parser);
    }

    private static @NotNull String escape(final @NotNull String s) {
        return s.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                .replace("'", "\\'")      // <== not necessary
                .replace("\"", "\\\"");
    }

    /**
     * Returns a string representing the argument.
     *
     * @param parser The argument.
     * @return A string.
     */
    public static @NotNull String ruleToString(final @NotNull Rule parser) {
        return ruleToString(parser, false);
    }

    private static @NotNull String ruleToString(final @NotNull Rule parser, final boolean hidden) {
        if (!hidden && parser.isHidden())
            return "<" + ruleToString(parser, true) + ">";

        switch (parser) {
            case EpsilonTerm ignored -> {
                return "ε";
            }
            case OptionalRule optionalRule -> {
                return parenForCompound(hidden, optionalRule.getRule()) + "?";
            }
            case OnceOrMoreRule onceOrMoreRule -> {
                return parenForCompound(hidden, onceOrMoreRule.getRule()) + "+";
            }
            case ZeroOrMoreRule zeroOrMoreRule -> {
                return parenForCompound(hidden, zeroOrMoreRule.getRule()) + "*";
            }
            case VariableRepetitionRule repParser -> {
                final int min = repParser.getMin();
                final int max = repParser.getMax();
                return ""
                        + min
                        + '*'
                        + max
                        + parenForCompound(hidden, repParser.getRule());
            }
            case AlternationRule alternationRule -> {
                final @NotNull List<String> parserStrings =
                        alternationRule.getRules().stream()
                                .map(p -> parenForTags((rule) -> rule instanceof RuleWithManyChildren, hidden, p))
                                .toList();
                return String.join(" | ", parserStrings);
            }
            case OrderedChoiceRule orderedChoiceRule -> {
                final @NotNull List<String> parserStrings =
                        orderedChoiceRule.getRules().stream()
                                .map(p -> parenForTags((rule) -> rule instanceof RuleWithManyChildren, hidden, p))
                                .toList();
                return String.join(" / ", parserStrings);
            }
            case ConcatRule concatRule -> {
                final @NotNull List<Rule> parsers = concatRule.getRules();
                final @NotNull Predicate<Rule> ks = (rule) -> rule instanceof RuleWithManyChildren;
                final @NotNull Iterable<String> parserStrings =
                        parsers.stream().map(p -> parenForTags(ks, hidden, p)).toList();
                return String.join(" ", parserStrings);
            }
            case StringTerm stringTerm -> {
                return escape(stringTerm.getString());
            }
            case ValueRangeTerm valueRangeTerm -> {
                final int lo = valueRangeTerm.getLo();
                final int hi = valueRangeTerm.getHi();
                //return lo == hi ? String.format("%%x%04x", lo) : String.format("%%x%04x-%04x", lo, hi);
                return new StringBuilder().appendCodePoint(lo).append('-').appendCodePoint(hi).toString();
            }
            case RegexTerm regexTerm -> {
                return "#\"" + regexTerm.getRegexp().pattern() + '"';
            }
            case NonTerminal nonTerminal -> {
                return nonTerminal.getKeyword().name();
            }
            case LookaheadRule lookaheadRule -> {
                return "&" + parenForCompound(hidden, lookaheadRule.getRule());
            }
            case NegativeLookaheadRule negativeLookaheadRule -> {
                return "!" + parenForCompound(hidden, negativeLookaheadRule.getRule());
            }
            case SpecialSequenceRule specialSequenceRule -> {
                return "?" + specialSequenceRule + "?";
            }
            case ExclusionRule exclusionRule -> {
                final @NotNull List<String> parserStrings =
                        exclusionRule.getRules().stream()
                                .map(rule -> parenForTags((rule1) -> rule1 instanceof RuleWithManyChildren, hidden, rule))
                                .toList();
                return String.join(" - ", parserStrings);
            }
            case EOFTerm ignored -> {
                return "eof";
            }
        }
    }

    private static @NotNull String ruleToString(final @NotNull Sym startProd, final @NotNull Rule parser) {
        final ReductionType red = parser.getReduction();
        if (red.isHiddenOrRaw())
            return "<" + startProd.name() + '>' + " = " + ruleToString(parser);
        else
            return startProd.name() + " = " + ruleToString(parser);
    }

    /**
     * Returns a (likely multiline) string representing a {@link Parser}.
     *
     * @param p The parser.
     * @return A string.
     */
    public static @NotNull String parserToString(final @NotNull Parser p) {
        final @NotNull Grammar grammar = p.grammar();
        final @NotNull Sym start = p.startProduction();

        final @NotNull StringBuilder sb = new StringBuilder(
                ruleToString(start, Objects.requireNonNull(grammar.getProduction(start))));

        grammar.forEach((nonTerminal, parser) -> {
                    if (!Objects.equals(nonTerminal, start)) {
                        sb.append('\n').append(ruleToString(nonTerminal, parser));
                    }
                }
        );
        return sb.toString();
    }
}
