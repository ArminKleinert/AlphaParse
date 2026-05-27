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
            final @NotNull Predicate<@NotNull Combinator> tags,
            final boolean hidden,
            final @NotNull Combinator parser) {
        if (!hidden && tags.test(parser)) return "(" + combinatorToString(parser, false) + ")";
        return combinatorToString(parser, false);
    }

    private static @NotNull String parenForCompound(final boolean hidden, final @NotNull Combinator parser) {
        return parenForTags(
                (c) -> c instanceof CombinatorWithManyParsers,
                hidden, parser);
    }

    private static @NotNull String escape(final @NotNull String s) {
        return '"' + s
                .replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\b", "\\b")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\f", "\\f")
                .replace("'", "\\'")      // <== not necessary
                .replace("\"", "\\\"")
                + '"';
    }

    /**
     * Returns a string representing the argument.
     *
     * @param parser The argument.
     * @return A string.
     */
    public static @NotNull String combinatorToString(final @NotNull Combinator parser) {
        return combinatorToString(parser, false);
    }

    private static @NotNull String combinatorToString(final @NotNull Combinator parser, final boolean hidden) {
        if (!hidden && parser.isHidden())
            return "<" + combinatorToString(parser, true) + ">";

        switch (parser) {
            case EpsilonCombinator ignored -> {
                return "ε";
            }
            case OptionalCombinator optionalCombinator -> {
                return parenForCompound(hidden, optionalCombinator.getParser()) + "?";
            }
            case PlusCombinator plusCombinator -> {
                return parenForCompound(hidden, plusCombinator.getParser()) + "+";
            }
            case CombinatorStar combinatorStar -> {
                return parenForCompound(hidden, combinatorStar.getParser()) + "*";
            }
            case RepetitionCombinator repParser -> {
                final int min = repParser.getMin();
                final int max = repParser.getMax();
                final @NotNull StringBuilder sb = new StringBuilder(parenForCompound(hidden, repParser.getParser()));
                sb.append('{').append(min);
                if (min != max) sb.append(',').append(max);
                sb.append('}');
                return sb.toString();
            }
            case ChoiceCombinator choiceCombinator -> {
                final @NotNull List<String> parserStrings =
                        choiceCombinator.getParsers().stream()
                                .map(p -> parenForTags((c) -> c instanceof CombinatorWithManyParsers, hidden, p))
                                .toList();
                return String.join(" | ", parserStrings);
            }
            case OrderedChoiceCombinator orderedChoiceCombinator -> {
                final @NotNull List<String> parserStrings =
                        orderedChoiceCombinator.getParsers().stream()
                                .map(p -> parenForTags((c) -> c instanceof CombinatorWithManyParsers, hidden, p))
                                .toList();
                return String.join(" / ", parserStrings);
            }
            case ConcatCombinator concatCombinator -> {
                final @NotNull List<Combinator> parsers = concatCombinator.getParsers();
                final @NotNull Predicate<Combinator> ks = (c) -> c instanceof CombinatorWithManyParsers;
                final @NotNull Iterable<String> parserStrings =
                        parsers.stream().map(p -> parenForTags(ks, hidden, p)).toList();
                return String.join(" ", parserStrings);
            }
            case TerminalStringCombinator terminalStringCombinator -> {
                return escape(terminalStringCombinator.getString());
            }
            case TerminalUnicodeCharCombinator terminalUnicodeCharCombinator -> {
                final int lo = terminalUnicodeCharCombinator.getLo();
                final int hi = terminalUnicodeCharCombinator.getHi();
                //return lo == hi ? String.format("%%x%04x", lo) : String.format("%%x%04x-%04x", lo, hi);
                return new StringBuilder().appendCodePoint(lo).append('-').appendCodePoint(hi).toString();
            }
            case TerminalRegexpCombinator terminalRegexpCombinator -> {
                return "#\"" + terminalRegexpCombinator.getRegexp().pattern() + '"';
            }
            case NonTerminalCombinator nonTerminalCombinator -> {
                return nonTerminalCombinator.getKeyword().name();
            }
            case LookaheadCombinator lookaheadCombinator -> {
                return "&" + parenForCompound(hidden, lookaheadCombinator.getParser());
            }
            case NegativeLookaheadCombinator negativeLookaheadCombinator -> {
                return "!" + parenForCompound(hidden, negativeLookaheadCombinator.getParser());
            }
            case TerminalSpecialSequenceCombinator specialSequenceCombinator ->
            {return "?" + specialSequenceCombinator + "?";}
            case ExclusionCombinator exclusionCombinator-> {
                final @NotNull List<String> parserStrings =
                        exclusionCombinator.getParsers().stream()
                                .map(p -> parenForTags((c) -> c instanceof CombinatorWithManyParsers, hidden, p))
                                .toList();
                return String.join(" - ", parserStrings);
            }
        }
    }

    private static @NotNull String ruleToString(final @NotNull Sym startProd, final @NotNull Combinator parser) {
        final ReductionType red = parser.getReduction();
        if (red.isHiddenOrRaw())
            return "<" + startProd.name() + '>' + " := " + combinatorToString(parser);
        else
            return startProd.name() + " := " + combinatorToString(parser);
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
