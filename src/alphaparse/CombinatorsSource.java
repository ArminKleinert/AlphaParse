package alphaparse;

import alphaparse.parser.Grammar;
import alphaparse.reduction.Reduction;
import alphaparse.parser.combinator.*;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

public final class CombinatorsSource {
    public static final @NotNull EpsilonCombinator epsilon = EpsilonCombinator.getDefault();
    private final @NotNull CombinatorBuffer buffer = new CombinatorBuffer();

    public CombinatorsSource() {
    }

    public @NotNull Combinator alternationCombinator(final @NotNull List<@NotNull Combinator> parsers) {
        if (parsers.size() == 1) return parsers.getFirst();
        if (parsers.stream().allMatch(p -> p.equals(epsilon))) return EpsilonCombinator.getDefault();
        return buffer.getOrAdd(new AlternationCombinator(parsers));
    }

    public @NotNull Combinator optionalCombinator(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        return buffer.getOrAdd(new OptCombinator(parser));
    }

    public @NotNull Combinator plusCombinator(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        return buffer.getOrAdd(new PlusCombinator(parser));
    }

    public @NotNull Combinator starCombinator(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        return buffer.getOrAdd(new StarCombinator(parser));
    }

    public @NotNull Combinator repetitionCombinator(final int m, final int n, final @NotNull Combinator parser) {
        if (m < 0 || m > n) throw new IllegalArgumentException();
        if ((m == 0 && n == 0) || parser.equals(epsilon)) return epsilon;
        return buffer.getOrAdd(new RepetitionCombinator(parser, m, n));
    }

    public @NotNull Combinator orderedChoiceCombinator(final @NotNull List<@NotNull Combinator> parsers) {
//        if (parsers.isEmpty())
//            return epsilon;
//
//        final var firstComb = parsers.getFirst();
//        int sublistStartIndex = 1;
//
//        if (firstComb.equals(epsilon)) {
//            while (sublistStartIndex < parsers.size() && parsers.get(sublistStartIndex).equals(epsilon)) {
//                sublistStartIndex++;
//            }
//        }
//
//        if (sublistStartIndex == parsers.size())
//            return firstComb;
//
//        final @NotNull var restParsers = parsers.subList(sublistStartIndex, parsers.size());
//
//        return buffer.getOrAdd(new OrderedCombinator(parsers));
        if (parsers.size() == 1) return parsers.getFirst();
        if (parsers.stream().allMatch(p -> p.equals(epsilon))) return EpsilonCombinator.getDefault();
        return buffer.getOrAdd(new OrderedCombinator(parsers));
    }

    public @NotNull Combinator catCombinator(final @NotNull List<@NotNull Combinator> parsers) {
        final var parserStream = parsers.stream().filter(p -> !p.equals(epsilon)).iterator();

        if (!parserStream.hasNext())
            return epsilon;

        final @NotNull var first = parserStream.next();

        if (!parserStream.hasNext())
            return first;

        final @NotNull var parserList = new ArrayList<Combinator>();
        parserList.add(first);
        do {
            parserList.add(parserStream.next());
        } while (parserStream.hasNext());

        return buffer.getOrAdd(new CatCombinator(parserList));
    }

    public @NotNull Combinator stringOrStringCiTerminal(final @NotNull String string, final boolean caseInsensitive) {
        if (string.isEmpty()) return epsilon;
        return buffer.getOrAdd(new StringTerminal(string, caseInsensitive));
    }

    public @NotNull Combinator stringTerminal(final @NotNull String string) {
        if (string.isEmpty()) return epsilon;
        return buffer.getOrAdd(new StringTerminal(string, false));
    }

    public @NotNull Combinator unicodeChar(final int lohi) {
        return buffer.getOrAdd(new UnicodeCharTerminal(lohi, lohi));
    }

    public @NotNull Combinator unicodeChar(final int lo, final int hi) {
        return buffer.getOrAdd(new UnicodeCharTerminal(lo, hi));
    }

    public @NotNull RegexpTerminal createRegexTerminal(final @NotNull Pattern regex) {
        return buffer.getOrAdd(new RegexpTerminal(regex));
    }

    public @NotNull NonTerminal makeNonTerminal(final @NotNull Keyword keyword) {
        return buffer.getOrAdd(new NonTerminal(keyword));
    }

    public static @NotNull NonTerminal staticMakeNonTerminal(final @NotNull Keyword keyword) {
        return new NonTerminal(keyword);
    }

    public @NotNull Combinator makeLookahead(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        return buffer.getOrAdd(new LookaheadCombinator(parser));
    }

    public @NotNull Combinator negateRule(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        return buffer.getOrAdd(new NegateCombinator(parser));
    }


    public @NotNull Combinator hideTag(final @NotNull Combinator parser) {
        return buffer.getOrAdd(parser.withReduction(Reduction.rawNonTerminalReduction));
    }

    public @NotNull Grammar unhideAllContent(final @NotNull Grammar grammar) {
        final List<Map.Entry<Keyword, Combinator>> res = new ArrayList<>();
        for (@NotNull Map.Entry<@NotNull Keyword, @NotNull Combinator> keywordCombinatorEntry : grammar.entrySet()) {
            final @NotNull Keyword key = keywordCombinatorEntry.getKey();
            final @NotNull Combinator value = keywordCombinatorEntry.getValue();
            res.add(Grammar.entry(key, buffer.getOrAdd(value.unhideContent())));
        }
        return Grammar.fromProductions(res);
    }

    public @NotNull Grammar unhideTags(final @NotNull ReductionType.ReductionTypesAvailable reductionType,
                                       final @NotNull Grammar grammar) {
        final List<Map.Entry<Keyword, Combinator>> res = new ArrayList<>();
        for (@NotNull Map.Entry<@NotNull Keyword, @NotNull Combinator> keywordCombinatorEntry : grammar.entrySet()) {
            final @NotNull Keyword key = keywordCombinatorEntry.getKey();
            final @NotNull Combinator value = keywordCombinatorEntry.getValue();
            final @NotNull ReductionType reduction = new ReductionType(key, reductionType);
            final @NotNull Combinator comb = buffer.getOrAdd(value.withReduction(reduction));
            res.add(Grammar.entry(key, comb));
        }
        return Grammar.fromProductions(res);
    }

    public @NotNull Grammar unhideAll(final @NotNull ReductionType.ReductionTypesAvailable reductionType,
                                      final @NotNull Grammar grammar) {
        final List<Map.Entry<Keyword, Combinator>> res = new ArrayList<>();
        for (@NotNull Map.Entry<@NotNull Keyword, @NotNull Combinator> keywordCombinatorEntry : grammar.entrySet()) {
            final @NotNull Keyword key = keywordCombinatorEntry.getKey();
            final @NotNull Combinator value = keywordCombinatorEntry.getValue();
            final @NotNull ReductionType reduction = new ReductionType(key, reductionType);
            final @NotNull Combinator comb = buffer.getOrAdd(value.unhideContent().withReduction(reduction));
            res.add(Grammar.entry(key, comb));
        }
        return Grammar.fromProductions(res);
    }

    private @NotNull Combinator autoWhitespaceParser(final @NotNull Combinator parser,
                                                     final @NotNull Combinator wsParser) {
        return switch (parser) {
            case NonTerminal ignored -> parser;
            case EpsilonCombinator ignored2 -> parser;
            case CombinatorWithParser parser1 -> buffer.getOrAdd(parser1.withParser(autoWhitespaceParser(parser1.getParser(), wsParser)));
            case CombinatorWithManyParsers combWithParsers -> {
                @NotNull List<Combinator> parsers = combWithParsers.getParsers().stream()
                        .map(p -> autoWhitespaceParser(p, wsParser))
                        .toList();
                yield buffer.getOrAdd(combWithParsers.withParsers(parsers));
            }
            case CombinatorTerminal ignored -> {
                final @NotNull List<Combinator> parsers = new ArrayList<>();
                parsers.add(wsParser);
                final @NotNull Combinator result;
                if (parser.getReduction().getReductionType() != ReductionType.ReductionTypesAvailable.NONE) {
                    parsers.add(parser.withReduction(Reduction.nullReduction));
                    result = catCombinator(parsers).withReduction(parser.getReduction());
                } else {
                    parsers.add(parser);
                    result = catCombinator(parsers);
                }
                yield result;
            }
        };
    }

    public @NotNull Grammar autoWhitespace(final @NotNull Grammar grammar,
                                           final @NotNull Keyword start,
                                           final @NotNull Grammar grammarWS,
                                           final @NotNull Keyword startWS) {
        final @NotNull Combinator wsParser = optionalCombinator(makeNonTerminal(startWS)).enableHideTag();

        final @NotNull var finalGrammar =
                new LinkedHashMap<>(grammar);
        for (var keywordCombinatorEntry : finalGrammar.entrySet()) {
            keywordCombinatorEntry.setValue(autoWhitespaceParser(keywordCombinatorEntry.getValue(), wsParser));
        }

        final @NotNull Combinator startWithoutReduction = buffer
                        .getOrAdd(finalGrammar.get(start)
                        .withReduction(Reduction.nullReduction));
        final @NotNull Combinator newStartComb =
                catCombinator(List.of(startWithoutReduction, wsParser))
                .withReduction(finalGrammar.get(start).getReduction());

        finalGrammar.put(start, newStartComb);
        finalGrammar.putAll(grammarWS);
        finalGrammar.put(startWS, hideTag(Objects.requireNonNull(grammarWS.getProduction(startWS))));
        return new Grammar(finalGrammar);
    }
}
