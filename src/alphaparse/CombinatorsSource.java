package alphaparse;

import alphaparse.parser.*;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

/**
 * TODO
 */
public final class CombinatorsSource {
    private final @NotNull EpsilonCombinator epsilon;
    private final @NotNull CombinatorBuffer buffer;

    /**
     * TODO
     */
    public CombinatorsSource() {
        epsilon = EpsilonCombinator.getDefault();
        buffer = new CombinatorBuffer();
    }

    @NotNull Combinator alternationCombinator(final @NotNull List<@NotNull Combinator> parsers) {
        if (parsers.size() == 1) return parsers.getFirst();
        if (parsers.stream().allMatch(p -> p.equals(epsilon))) return EpsilonCombinator.getDefault();
        return buffer.getOrAdd(new AlternationCombinator(parsers));
    }

    @NotNull Combinator optionalCombinator(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        return buffer.getOrAdd(new OptCombinator(parser));
    }

    @NotNull Combinator plusCombinator(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        return buffer.getOrAdd(new PlusCombinator(parser));
    }

    @NotNull Combinator starCombinator(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        return buffer.getOrAdd(new StarCombinator(parser));
    }

    @NotNull Combinator repetitionCombinator(final int m, final int n, final @NotNull Combinator parser) {
        if (m < 0 || m > n) throw new IllegalArgumentException();
        if ((m == 0 && n == 0) || parser.equals(epsilon)) return epsilon;
        return buffer.getOrAdd(new RepetitionCombinator(parser, m, n));
    }

    @NotNull Combinator orderedChoiceCombinator(final @NotNull List<@NotNull Combinator> parsers) {
        if (parsers.isEmpty())
            return epsilon;

        if (parsers.size() == 1) return parsers.getFirst();

        List<@Nullable Combinator> newParserList = null;
        for (int i = 0; i < parsers.size(); i++) {
            if (parsers.get(i).equals(epsilon)) {
                if (newParserList == null) newParserList = new ArrayList<>(parsers);
                else newParserList.set(i, null); // mark for removal
            }
        }

        if (newParserList == null)
            newParserList = parsers;
        else
            newParserList = newParserList.stream().filter(Objects::nonNull).toList();

        if (newParserList.size() == 1) return Objects.requireNonNull(newParserList.getFirst());
        return buffer.getOrAdd(new OrderedCombinator(newParserList));
    }

    @NotNull Combinator catCombinator(final @NotNull List<@NotNull Combinator> parsers) {
        final var parserStream = parsers.stream().filter(p -> !p.equals(epsilon)).iterator();

        // If no parsers are provided, return the first one only.
        if (!parserStream.hasNext())
            return epsilon;

        final @NotNull var first = parserStream.next();

        // If there is only one parser, then there is no point in making a grouping.
        if (!parserStream.hasNext())
            return first;

        final @NotNull var parserList = new ArrayList<Combinator>();
        parserList.add(first);
        while (parserStream.hasNext()) {
            parserList.add(parserStream.next());
        }

        return buffer.getOrAdd(new CatCombinator(parserList));
    }

    @NotNull Combinator stringOrStringCiTerminal(final @NotNull String string, final boolean caseInsensitive) {
        if (string.isEmpty()) return epsilon;
        return buffer.getOrAdd(new StringTerminal(string, caseInsensitive));
    }

    @NotNull Combinator stringTerminal(final @NotNull String string) {
        if (string.isEmpty()) return epsilon;
        return buffer.getOrAdd(new StringTerminal(string, false));
    }

    @NotNull Combinator unicodeChar(final int lohi) {
        return buffer.getOrAdd(new UnicodeCharTerminal(lohi, lohi));
    }

    @NotNull Combinator unicodeChar(final int lo, final int hi) {
        return buffer.getOrAdd(new UnicodeCharTerminal(lo, hi));
    }

    @NotNull RegexpTerminal createRegexTerminal(final @NotNull Pattern regex) {
        return buffer.getOrAdd(new RegexpTerminal(regex));
    }

    @NotNull NonTerminal makeNonTerminal(final @NotNull Keyword keyword) {
        return buffer.getOrAdd(new NonTerminal(keyword));
    }

    /**
     * TODO
     *
     * @param keyword TODO
     * @return TODO
     */
    public static @NotNull NonTerminal staticMakeNonTerminal(final @NotNull Keyword keyword) {
        return new NonTerminal(keyword);
    }

    @NotNull Combinator makeLookahead(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        return buffer.getOrAdd(new LookaheadCombinator(parser));
    }

    @NotNull Combinator negateRule(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        return buffer.getOrAdd(new NegateCombinator(parser));
    }


    @NotNull Combinator hideTag(final @NotNull Combinator parser) {
        return buffer.getOrAdd(parser.withReduction(ReductionType.rawNonTerminalReduction()));
    }

    @NotNull Grammar unhideAllContent(final @NotNull Grammar grammar) {
        final List<Map.Entry<Keyword, Combinator>> res = new ArrayList<>();
        for (final @NotNull Map.Entry<@NotNull Keyword, @NotNull Combinator> keywordCombinatorEntry : grammar.entrySet()) {
            final @NotNull Keyword key = keywordCombinatorEntry.getKey();
            final @NotNull Combinator value = keywordCombinatorEntry.getValue();
            res.add(Grammar.entry(key, buffer.getOrAdd(value.unhideContent())));
        }
        return Grammar.fromProductions(res);
    }

    @NotNull Grammar unhideTags(final @NotNull ReductionType.ReductionTypesAvailable reductionType,
                                final @NotNull Grammar grammar) {
        final List<Map.Entry<Keyword, Combinator>> res = new ArrayList<>();
        for (final @NotNull Map.Entry<@NotNull Keyword, @NotNull Combinator> keywordCombinatorEntry : grammar.entrySet()) {
            final @NotNull Keyword key = keywordCombinatorEntry.getKey();
            final @NotNull Combinator value = keywordCombinatorEntry.getValue();
            final @NotNull ReductionType reduction = ReductionType.nonTerminalReduction(key, reductionType);
            final @NotNull Combinator comb = buffer.getOrAdd(value.withReduction(reduction));
            res.add(Grammar.entry(key, comb));
        }
        return Grammar.fromProductions(res);
    }

    @NotNull Grammar unhideAll(final @NotNull ReductionType.ReductionTypesAvailable reductionType,
                               final @NotNull Grammar grammar) {
        final List<Map.Entry<Keyword, Combinator>> res = new ArrayList<>();
        for (final @NotNull Map.Entry<@NotNull Keyword, @NotNull Combinator> keywordCombinatorEntry : grammar.entrySet()) {
            final @NotNull Keyword key = keywordCombinatorEntry.getKey();
            final @NotNull Combinator value = keywordCombinatorEntry.getValue();
            final @NotNull ReductionType reduction = ReductionType.nonTerminalReduction(key, reductionType);
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
            case CombinatorWithParser parser1 ->
                    buffer.getOrAdd(parser1.withParser(autoWhitespaceParser(parser1.getParser(), wsParser)));
            case CombinatorWithManyParsers combWithParsers -> {
                final @NotNull List<Combinator> parsers = combWithParsers.getParsers()
                        .stream()
                        .map(p -> autoWhitespaceParser(p, wsParser))
                        .toList();
                yield buffer.getOrAdd(combWithParsers.withParsers(parsers));
            }
            case CombinatorTerminal ignored -> {
                final @NotNull List<Combinator> parsers = new ArrayList<>();
                parsers.add(wsParser);
                final @NotNull Combinator result;
                if (parser.getReduction().getReductionType() != ReductionType.ReductionTypesAvailable.NONE) {
                    parsers.add(parser.withReduction(ReductionType.nullReduction()));
                    result = catCombinator(parsers).withReduction(parser.getReduction());
                } else {
                    parsers.add(parser);
                    result = catCombinator(parsers);
                }
                yield result;
            }
        };
    }

    /**
     * TODO
     *
     * @param grammar   TODO
     * @param start     TODO
     * @param grammarWS TODO
     * @param startWS   TODO
     * @return TODO
     */
    public @NotNull Grammar autoWhitespace(final @NotNull Grammar grammar,
                                           final @NotNull Keyword start,
                                           final @NotNull Grammar grammarWS,
                                           final @NotNull Keyword startWS) {
        final @NotNull Combinator wsParser = optionalCombinator(makeNonTerminal(startWS)).enableHideTag();

        final @NotNull SequencedMap<@NotNull Keyword, @NotNull Combinator> finalGrammar =
                new LinkedHashMap<>(grammar);
        for (var keywordCombinatorEntry : finalGrammar.entrySet()) {
            keywordCombinatorEntry.setValue(autoWhitespaceParser(keywordCombinatorEntry.getValue(), wsParser));
        }

        final @NotNull Combinator startWithoutReduction = buffer
                .getOrAdd(finalGrammar.get(start)
                        .withReduction(ReductionType.nullReduction()));
        final @NotNull Combinator newStartComb =
                catCombinator(List.of(startWithoutReduction, wsParser))
                        .withReduction(finalGrammar.get(start).getReduction());

        finalGrammar.put(start, newStartComb);
        finalGrammar.putAll(grammarWS);
        finalGrammar.put(startWS, hideTag(Objects.requireNonNull(grammarWS.getProduction(startWS))));
        return new Grammar(finalGrammar);
    }
}
