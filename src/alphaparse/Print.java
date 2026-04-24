package alphaparse;

import alphaparse.parser.*;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * TODO
 */
public final class Print {
    private Print() {
    }

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

    /**
     * TODO
     *
     * @param parser TODO
     * @return TODO
     */
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
                return lo == hi ? String.format("%%x%04x", lo) : String.format("%%x%04x-%04x", lo, hi);
            }
            case TerminalRegexpCombinator terminalRegexpCombinator -> {
                return "#\"" + terminalRegexpCombinator.getRegexp().pattern() + '"';
            }
            case NonTerminalCombinator nonTerminalCombinator -> {
                return nonTerminalCombinator.getKeyword().getName();
            }
            case LookaheadCombinator lookaheadCombinator -> {
                return "&" + parenForCompound(hidden, lookaheadCombinator.getParser());
            }
            case NegativeLookaheadCombinator negativeLookaheadCombinator -> {
                return "!" + parenForCompound(hidden, negativeLookaheadCombinator.getParser());
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

    /**
     * TODO
     *
     * @param p TODO
     * @return TODO
     */
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
