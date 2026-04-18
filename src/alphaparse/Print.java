package alphaparse;

import alphaparse.parser.Grammar;
import alphaparse.parser.Parser;
import alphaparse.parser.combinator.*;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class Print {
    private static @NotNull String parenForTags(
            final @NotNull Predicate<@NotNull Combinator> tags,
            final boolean hidden,
            final @NotNull Combinator parser) {
        if (!hidden && tags.test(parser)) return "(" + combinatorsToString(parser, false) + ")";
        return combinatorsToString(parser, false);
    }

    private static @NotNull String parenForCompound(final boolean hidden, final @NotNull Combinator parser) {
        return parenForTags(
                (c) -> c instanceof CombinatorWithManyParsers,
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

    private static @NotNull String combinatorsToString(final @NotNull Combinator parser, final boolean hidden) {
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
                                .map(p -> parenForTags((c) -> c instanceof CombinatorWithManyParsers, hidden, p))
                                .toList();
                return String.join(" | ", parserStrings);
            }
            case OrderedCombinator orderedCombinator -> {
                final @NotNull List<String> parserStrings =
                        orderedCombinator.getParsers().stream()
                                .map(p -> parenForTags((c) -> c instanceof CombinatorWithManyParsers, hidden, p))
                                .toList();
                return String.join(" / ", parserStrings);
            }
            case CatCombinator catCombinator -> {
                final @NotNull List<Combinator> parsers = catCombinator.getParsers();
                final @NotNull Predicate<Combinator> ks = (c) -> c instanceof CombinatorWithManyParsers;
                final @NotNull Iterable<String> parserStrings =
                        parsers.stream().map(p -> parenForTags(ks, hidden, p)).toList();
                return String.join(" ", parserStrings);
            }
            case StringTerminal stringTerminal -> {
                return escape(stringTerminal.getString());
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
        final @NotNull Grammar grammar = p.grammar();
        final @NotNull Keyword start = p.startProduction();

        final @NotNull StringBuilder sb = new StringBuilder(
                ruleToString(start, Objects.requireNonNull(grammar.getProduction(start))));

        sb.append('\n').append(ruleToString(start, Objects.requireNonNull(grammar.getProduction(start))));

        grammar.forEach((nonTerminal, parser) -> {
                    if (!Objects.equals(nonTerminal, start)) {
                        sb.append('\n').append(ruleToString(nonTerminal, parser));
                    }
                }
        );
        return sb.toString();
    }
}
