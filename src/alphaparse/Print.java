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
        if (!hidden && parser.isHidden()) {
            return "<" + ruleToString(parser, true) + ">";
        }

        if (parser instanceof EpsilonTerm) {
            return "ε";
        }
        if (parser instanceof OptionalRule) {
            return parenForCompound(hidden, ((RuleWithChild) parser).getRule()) + "?";
        }
        if (parser instanceof OnceOrMoreRule) {
            return parenForCompound(hidden, ((RuleWithChild) parser).getRule()) + "+";
        }
        if (parser instanceof ZeroOrMoreRule) {
            return parenForCompound(hidden, ((RuleWithChild) parser).getRule()) + "*";
        }
        if (parser instanceof VariableRepetitionRule) {
            final var repParser = (VariableRepetitionRule) parser;
            final int min = repParser.getMin();
            final int max = repParser.getMax();
            return ""
                    + min
                    + '*'
                    + ((max < Integer.MAX_VALUE) ? max : "")
                    + parenForCompound(hidden, repParser.getRule());
        }
        if (parser instanceof AlternationRule) {
            final @NotNull List<String> parserStrings =
                    ((RuleWithManyChildren) parser).getRules()
                            .stream()
                            .map(p -> parenForTags((rule) -> rule instanceof RuleWithManyChildren, hidden, p))
                            .toList();
            return String.join(" | ", parserStrings);
        }
        if (parser instanceof OrderedChoiceRule) {
            final @NotNull List<String> parserStrings =
                    ((RuleWithManyChildren) parser).getRules()
                            .stream()
                            .map(p -> parenForTags((rule) -> rule instanceof RuleWithManyChildren, hidden, p))
                            .toList();
            return String.join(" / ", parserStrings);
        }
        if (parser instanceof ConcatRule) {
            final @NotNull List<Rule> parsers = ((RuleWithManyChildren) parser).getRules();
            final @NotNull Predicate<Rule> ks = (rule) -> rule instanceof RuleWithManyChildren;
            final @NotNull Iterable<String> parserStrings =
                    parsers.stream().map(p -> parenForTags(ks, hidden, p)).toList();
            return String.join(" ", parserStrings);
        }
        if (parser instanceof StringTerm) {
            return escape(((StringTerm) parser).getString());
        }
        if (parser instanceof ValueRangeTerm) {
            var valueRangeTerm = (ValueRangeTerm) parser;
            final int lo = valueRangeTerm.getLo();
            final int hi = valueRangeTerm.getHi();
            //return lo == hi ? String.format("%%x%04x", lo) : String.format("%%x%04x-%04x", lo, hi);
            return new StringBuilder().appendCodePoint(lo).append('-').appendCodePoint(hi).toString();
        }
        if (parser instanceof RegexTerm) {
            return "#\"" + ((RegexTerm) parser).getRegexp().pattern() + '"';
        }
        if (parser instanceof NonTerminal) {
            return ((NonTerminal) parser).getKeyword().name();
        }
        if (parser instanceof LookaheadRule) {
            return "&" + parenForCompound(hidden, ((RuleWithChild) parser).getRule());
        }
        if (parser instanceof NegativeLookaheadRule) {
            return "!" + parenForCompound(hidden, ((RuleWithChild) parser).getRule());
        }
        if (parser instanceof SpecialSequenceRule) {
            return "?" + ((SpecialSequenceRule) parser).toString() + "?";
        }
        if (parser instanceof ExclusionRule) {
            final @NotNull List<String> parserStrings =
                    ((RuleWithManyChildren) parser).getRules()
                            .stream()
                            .map(rule -> parenForTags((rule1) -> rule1 instanceof RuleWithManyChildren, hidden, rule))
                            .toList();
            return String.join(" - ", parserStrings);
        }
        if (parser instanceof EOFTerm) {
            return "eof";
        }
        throw new IllegalArgumentException("Can not handle value " + parser + " of type " + parser.getClass() + ".");
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
