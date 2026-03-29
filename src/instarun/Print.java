package instarun;

import instarun.parser.Grammar;
import instarun.parser.Parser;
import instarun.parser.combinator.*;
import instarun.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

public final class Print {
    private static @NotNull String parenForTags(
            final @NotNull Predicate<@NotNull Combinator> tags,
            final boolean hidden,
            final @NotNull Combinator parser) {
        if (!hidden && tags.test(parser)) return "(" + combinatorsToString(parser, false) + ")";
        return combinatorsToString(parser, false);
    }

    public static @NotNull String parenForCompound(final boolean hidden, final @NotNull Combinator parser) {
        return parenForTags(
                (c) -> c instanceof AlternationCombinator || c instanceof OrderedCombinator || c instanceof CatCombinator,
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

    public static @NotNull String combinatorsToString(final @NotNull Combinator parser) {
        return combinatorsToString(parser, false);
    }

    public static @NotNull String combinatorsToString(final @NotNull Combinator parser, final boolean hidden) {
        if (!hidden && parser.isHidden())
            return "<" + combinatorsToString(parser, true) + ">";

        switch (parser) {
            case EpsilonCombinator ignored -> {
                return "ε";
            }
            case OptCombinator optCombinator -> {
                return parenForCompound(hidden, optCombinator.getParser()) + "?";
            }
            case PlusCombinator plusCombinator -> {
                return parenForCompound(hidden, plusCombinator.getParser()) + "+";
            }
            case StarCombinator starCombinator -> {
                return parenForCompound(hidden, starCombinator.getParser()) + "*";
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
            case AlternationCombinator alternationCombinator -> {
                final @NotNull List<String> parserStrings =
                        alternationCombinator.getParsers().stream()
                                .map(p -> parenForTags((c) -> c instanceof OrderedCombinator, hidden, p))
                                .toList();
                return String.join(" | ", parserStrings);
            }
            case OrderedCombinator orderedCombinator -> {
                final @NotNull Predicate<Combinator> ks = (c) -> c instanceof AlternationCombinator;
                return parenForTags(ks, hidden, orderedCombinator.getParser1()) +
                        " / " +
                        parenForTags(ks, hidden, orderedCombinator.getParser2());
            }
            case CatCombinator catCombinator -> {
                final @NotNull List<Combinator> parsers = catCombinator.getParsers();
                final @NotNull  Predicate<Combinator> ks = (c) -> c instanceof AlternationCombinator || c instanceof OrderedCombinator;
                final @NotNull Iterable<String> parserStrings =
                        parsers.stream().map(p -> parenForTags(ks, hidden, p)).toList();
                return String.join(" ", parserStrings);
            }
            case StringTerminal stringTerminal -> {
                return escape(stringTerminal.getString());
            }
            case StringCaseInsensitiveTerminal stringCaseInsensitiveTerminal -> {
                return escape(stringCaseInsensitiveTerminal.getString());
            }
            case UnicodeCharTerminal unicodeCharTerminal -> {
                final int lo = unicodeCharTerminal.getLo();
                final int hi = unicodeCharTerminal.getHi();
                return lo == hi ? String.format("%%x%04x", lo) : String.format("%%x%04x-%04x", lo, hi);
            }
            case RegexpTerminal regexpTerminal -> {
                return "#\"" + regexpTerminal.getRegexp().pattern() + '"';
            }
            case NonTerminal nonTerminal -> {
                return nonTerminal.getKeyword().getName();
            }
            case LookaheadCombinator lookaheadCombinator -> {
                return "&" + parenForCompound(hidden, lookaheadCombinator.getParser());
            }
            case NegateCombinator negateCombinator -> {
                return "!" + parenForCompound(hidden, negateCombinator.getParser());
            }
            default -> throw new IllegalArgumentException();
        }
    }

    private static @NotNull String ruleToString(final @NotNull Keyword startProd, final @NotNull Combinator parser) {
        final ReductionType red = parser.getReduction();
        if (red.isHiddenOrRaw())
            return "<" + startProd.getName() + '>' + " = " + combinatorsToString(parser);
        else
            return startProd.getName() + " = " + combinatorsToString(parser);
    }

    public static @NotNull String parserToString(final @NotNull Parser p) {
        final @NotNull Grammar grammar = p.getGrammar();
        final @NotNull Keyword start = p.getStartProduction();

        final @NotNull StringBuilder sb = new StringBuilder(
                Objects.requireNonNull(ruleToString(start, grammar.getProduction(start))));

        sb.append('\n').append(ruleToString(start, grammar.getProduction(start)));

        grammar.forEach((nonTerminal, parser) -> {
                    if (!Objects.equals(nonTerminal, start)) {
                        sb.append('\n').append(ruleToString(nonTerminal, parser));
                    }
                }
        );
        return sb.toString();
    }
}
