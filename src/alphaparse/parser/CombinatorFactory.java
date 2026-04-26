package alphaparse.parser;

import alphaparse.Alpha;
import alphaparse.Keyword;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

/**
 * A factory for creating different types of {@link Combinator} objects.
 */
public final class CombinatorFactory {
    private final @NotNull EpsilonCombinator epsilon;
    private final @Nullable BufferForCombinators buffer;
    private final boolean useBuffer;

    /**
     * The constructor. The parameters allow buffering of created {@link Combinator} objects.
     * Buffering can reduce memory consumption if the grammar is very big, but might momentarily increase memory consumption for very small grammars, too. In general, using the buffer is a bit faster.
     *
     * @param useBuffer Set to true if buffering should be used, false otherwise.
     */
    public CombinatorFactory(final boolean useBuffer) {
        this.useBuffer = useBuffer;
        epsilon = EpsilonCombinator.getDefault();
        buffer = useBuffer ? new BufferForCombinators() : null;
    }

    private Combinator bufferIfRequested(Combinator c) {
        return useBuffer ? buffer.getOrAdd(c) : c;
    }

    /**
     * Creates a {@link ChoiceCombinator}.
     * <ul>
     * <li>If the argument List is empty, a {@link EpsilonCombinator} is returned instead.</li>
     * <li>If there is only one element in the list, it is returned.</li>
     * <li>Otherwise, returns a {@link ChoiceCombinator}, as expected.</li>
     * </ul>
     *
     * @param parsers The parsers for the output.
     * @return A combinator.
     */
    public @NotNull Combinator choiceCombinator(final @NotNull List<@NotNull Combinator> parsers) {
        if (parsers.size() == 1) return parsers.getFirst();
        if (parsers.stream().allMatch(p -> p.equals(epsilon))) return EpsilonCombinator.getDefault();
        final @NotNull var result = new ChoiceCombinator(parsers);
        return useBuffer ? buffer.getOrAdd(result) : result;
    }

    /**
     * Creates a {@link OptionalCombinator} or an {@link EpsilonCombinator} if the input is epsilon.
     *
     * @param parser The combinator to maybe match.
     * @return A combinator.
     */
    public @NotNull Combinator optionalCombinator(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        final @NotNull var result = new OptionalCombinator(parser);
        return useBuffer ? buffer.getOrAdd(result) : result;
    }

    /**
     * Creates a {@link PlusCombinator} or an {@link EpsilonCombinator} if the input is epsilon.
     *
     * @param parser The combinator to match repeatedly.
     * @return A combinator.
     */
    public @NotNull Combinator plusCombinator(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        final @NotNull var result = new PlusCombinator(parser);
        return useBuffer ? buffer.getOrAdd(result) : result;
    }

    /**
     * Creates a {@link CombinatorStar} or an {@link EpsilonCombinator} if the input is epsilon.
     *
     * @param parser The combinator to match repeatedly.
     * @return A combinator.
     */
    public @NotNull Combinator starCombinator(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        final @NotNull var result = new CombinatorStar(parser);
        return useBuffer ? buffer.getOrAdd(result) : result;
    }

    /**
     * Creates a {@link CombinatorStar} or an {@link EpsilonCombinator} if the input is epsilon.
     * <ul>
     * <li>If minimum and maximum amount of repetitions is set to 0, return an {@link EpsilonCombinator}.</li>
     * <li>If minimum and maximum amount of repetitions is set to 1, may return the input parser.</li>
     * <li>If minimum and maximum amount of repetitions are equal, a {@link ConcatCombinator} might be returned instead.</li>
     * <li>Otherwise, does as normally expected.</li>
     * </ul>
     *
     * @param m      The minimum amount of repetitions.
     * @param n      The maximum amount of repetitions.
     * @param parser The parser.
     * @return A combinator, as described.
     */
    public @NotNull Combinator repetitionCombinator(final int m, final int n, final @NotNull Combinator parser) {
        if (m < 0 || m > n) throw new IllegalArgumentException();
        if ((m == 0 && n == 0) || parser.equals(epsilon)) return epsilon;
        if (m == 1 && n == 1) return parser;
        if (m == n) {
            final int repetitionThreshold = 4;
            if (m <= repetitionThreshold)
                return catCombinator(Collections.nCopies(m, parser));
            // Otherwise fallthrough.
        }
        final @NotNull var result = new RepetitionCombinator(parser, m, n);
        return useBuffer ? buffer.getOrAdd(result) : result;
    }

    /**
     * Creates a {@link OrderedChoiceCombinator}.
     * <ul>
     * <li>If the argument List is empty, a {@link EpsilonCombinator} is returned instead.</li>
     * <li>If there is only one element in the list, it is returned.</li>
     * <li>Otherwise, returns a {@link OrderedChoiceCombinator}, as expected.</li>
     * </ul>
     *
     * @param parsers The parsers for the output.
     * @return A combinator.
     */
    public @NotNull Combinator orderedChoiceCombinator(final @NotNull List<@NotNull Combinator> parsers) {
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
        final @NotNull var result = new OrderedChoiceCombinator(newParserList);
        return useBuffer ? buffer.getOrAdd(result) : result;
    }

    /**
     * Creates a {@link ConcatCombinator}.
     * <ul>
     * <li>If the argument List is empty, a {@link EpsilonCombinator} is returned instead.</li>
     * <li>If there is only one element in the list, it is returned.</li>
     * <li>Otherwise, returns a {@link ConcatCombinator}, as expected.</li>
     * </ul>
     *
     * @param parsers The parsers for the output.
     * @return A combinator.
     */
    public @NotNull Combinator catCombinator(final @NotNull List<@NotNull Combinator> parsers) {
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

        final @NotNull var result = new ConcatCombinator(parserList);
        return useBuffer ? buffer.getOrAdd(result) : result;
    }

    /**
     * Creates a {@link TerminalStringCombinator} or {@link EpsilonCombinator} if the string is empty.
     *
     * @param string          The string to match.
     * @param caseInsensitive Whether the Terminal will match without caring about casing.
     * @return The new parser.
     */
    public @NotNull Combinator stringOrStringCiTerminal(final @NotNull String string,
                                                        final boolean caseInsensitive) {
        if (string.isEmpty()) return epsilon;
        final @NotNull var result = new TerminalStringCombinator(string, caseInsensitive);
        return useBuffer ? buffer.getOrAdd(result) : result;
    }

    /**
     * Creates a case-sensitive {@link TerminalStringCombinator} or {@link EpsilonCombinator} if the string is empty.
     *
     * @param string The string to match.
     * @return The new parser.
     * @see CombinatorFactory#stringOrStringCiTerminal(String, boolean)
     */
    public @NotNull Combinator stringTerminal(final @NotNull String string) {
        return stringOrStringCiTerminal(string, false);
    }

    /**
     * Creates a {@link Combinator} to match the codepoint.
     * This can be any kind of {@link Combinator} capable of accomplishing that task.
     *
     * @param lohi The codepoint.
     * @return The new parser.
     */
    public @NotNull Combinator unicodeChar(final int lohi) {
        final @NotNull var str = new StringBuilder(4).appendCodePoint(lohi).toString();
        //return buffer.getOrAdd(new TerminalUnicodeCharCombinator(lohi, lohi));
        return stringOrStringCiTerminal(str, false);
    }

    /**
     * Creates a {@link Combinator} to match the codepoint range.
     * This can be any kind of {@link Combinator} capable of accomplishing that task.
     *
     * @param lo The minimum codepoint.
     * @param hi The maximum codepoint.
     * @return The new parser.
     */
    public @NotNull Combinator unicodeChar(final int lo, final int hi) {
        if (lo > hi)
            throw new IllegalArgumentException();

        if (lo == hi)
            return unicodeChar(lo);

        final @NotNull String regex = new StringBuilder()
                .append("[\\x{")
                .append(Integer.toString(lo, 16))
                .append("}-\\x{")
                .append(Integer.toString(hi, 16))
                .append("}]")
                .toString();
        return createRegexTerminal(Pattern.compile(regex));
        //"[\\x{1F601}-\\x{1F64F}]"
        //return buffer.getOrAdd(new TerminalUnicodeCharCombinator(lo, hi));
    }

    /**
     * Creates a new regex terminal.
     *
     * @param regex The pattern.
     * @return The new parser.
     */
    public @NotNull TerminalRegexpCombinator createRegexTerminal(final @NotNull Pattern regex) {
        final @NotNull var result = new TerminalRegexpCombinator(regex);
        return useBuffer ? buffer.getOrAdd(result) : result;
    }

    /**
     * Creates a new non-terminal.
     *
     * @param keyword The name of the non-terminal.
     * @return The new parser.
     */
    public @NotNull NonTerminalCombinator makeNonTerminal(final @NotNull Keyword keyword) {
        final @NotNull var result = new NonTerminalCombinator(keyword);
        return useBuffer ? buffer.getOrAdd(result) : result;
    }

    /**
     * Creates a new non-terminal. This is a static method which can not use buffering-
     *
     * @param keyword The name of the non-terminal.
     * @return The new parser.
     */
    public static @NotNull NonTerminalCombinator staticMakeNonTerminal(final @NotNull Keyword keyword) {
        return new NonTerminalCombinator(keyword);
    }

    /**
     * Creates a lookahead combinator.
     * The output can be a {@link EpsilonCombinator} if the input is epsilon.
     *
     * @param parser The combinator to look for.
     * @return The new combinator.
     */
    public @NotNull Combinator makeLookahead(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        final @NotNull var result = new LookaheadCombinator(parser);
        return useBuffer ? buffer.getOrAdd(result) : result;
    }

    /**
     * Creates a lookahead combinator.
     * The output can be a {@link EpsilonCombinator} if the input is epsilon.
     *
     * @param parser The combinator to avoid.
     * @return The new combinator.
     */
    public @NotNull Combinator negateRule(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        final @NotNull var result = new NegativeLookaheadCombinator(parser);
        return useBuffer ? buffer.getOrAdd(result) : result;
    }

    /**
     * Hides the tag of a combinator in the result of a parse.
     *
     * @param parser The parser.
     * @return The new parser.
     */
    public @NotNull Combinator hideTag(final @NotNull Combinator parser) {
        final @NotNull var result = parser.hideTag();
        return useBuffer ? buffer.getOrAdd(result) : result;
    }

    /**
     * Applies {@link Combinator#unhideContent} to all entries in the grammar.
     *
     * @param grammar The grammar.
     * @return The new grammar.
     * @see Alpha.ParsingOptions#getUnhide()
     */
    public @NotNull Grammar unhideAllContent(final @NotNull Grammar grammar) {
        final List<Map.Entry<Keyword, Combinator>> res = new ArrayList<>();
        for (final @NotNull Map.Entry<@NotNull Keyword, @NotNull Combinator> keywordCombinatorEntry : grammar.entrySet()) {
            final @NotNull Keyword key = keywordCombinatorEntry.getKey();
            final @NotNull Combinator value = keywordCombinatorEntry.getValue();
            final @NotNull var pUnhide = value.unhideContent();
            final @NotNull var pB = bufferIfRequested(pUnhide);
            res.add(Grammar.entry(key, pB));
        }
        return Grammar.fromProductions(res);
    }

    /**
     * Applies the reduction-type to all entries in the grammar.
     *
     * @param reductionType The reduction-type to apply.
     * @param grammar       The grammar.
     * @return The new grammar.
     */
    public @NotNull Grammar unhideTags(final @NotNull ReductionType.ReductionTypesAvailable reductionType,
                                       final @NotNull Grammar grammar) {
        final List<Map.Entry<Keyword, Combinator>> res = new ArrayList<>();
        for (final @NotNull Map.Entry<@NotNull Keyword, @NotNull Combinator> keywordCombinatorEntry : grammar.entrySet()) {
            final @NotNull Keyword key = keywordCombinatorEntry.getKey();
            final @NotNull Combinator value = keywordCombinatorEntry.getValue();
            final @NotNull ReductionType reduction = ReductionType.nonTerminalReduction(key, reductionType);

            final @NotNull var pUnhide = value.withReduction(reduction);
            final @NotNull var comb = bufferIfRequested(pUnhide);
            res.add(Grammar.entry(key, comb));
        }
        return Grammar.fromProductions(res);
    }

    /**
     * Applies the reduction-type to all entries in the grammar and applies {@link Combinator#unhideContent()}.
     *
     * @param reductionType The reduction-type to apply.
     * @param grammar       The grammar.
     * @return The new grammar.
     */
    public @NotNull Grammar unhideAll(final @NotNull ReductionType.ReductionTypesAvailable reductionType,
                                      final @NotNull Grammar grammar) {
        final List<Map.Entry<Keyword, Combinator>> res = new ArrayList<>();
        for (final @NotNull Map.Entry<@NotNull Keyword, @NotNull Combinator> keywordCombinatorEntry : grammar.entrySet()) {
            final @NotNull Keyword key = keywordCombinatorEntry.getKey();
            final @NotNull Combinator value = keywordCombinatorEntry.getValue();
            final @NotNull ReductionType reduction = ReductionType.nonTerminalReduction(key, reductionType);
            final @NotNull var p = value.unhideContent().withReduction(reduction);
            final @NotNull Combinator comb = bufferIfRequested(p);
            res.add(Grammar.entry(key, comb));
        }
        return Grammar.fromProductions(res);
    }

    private @NotNull Combinator autoWhitespaceParser(final @NotNull Combinator parser,
                                                     final @NotNull Combinator wsParser) {
        return switch (parser) {
            case NonTerminalCombinator ignored -> parser;
            case EpsilonCombinator ignored2 -> parser;
            case CombinatorWithParser parser1 ->
                    bufferIfRequested(parser1.withParser(autoWhitespaceParser(parser1.getParser(), wsParser)));
            case CombinatorWithManyParsers combWithParsers -> {
                final @NotNull List<Combinator> parsers = combWithParsers.getParsers()
                        .stream()
                        .map(p -> autoWhitespaceParser(p, wsParser))
                        .toList();
                yield bufferIfRequested(combWithParsers.withParsers(parsers));
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
     * Merges another grammar into this grammar which eats whitespaces (at least that is the intended purpose).
     * This method should not be used directly. Use {@link alphaparse.Alpha.ParserCreationOptions#withWhitespaceParser} or set the option in the {@link alphaparse.Alpha.ParserCreationOptions} instead when creating a parser.
     *
     * @param grammar   The main grammar.
     * @param start     The starting symbol of the main grammar.
     * @param grammarWS The whitespace grammar.
     * @param startWS   The starting symbol of the whitespace grammar.
     * @return The new grammar.
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

        final @NotNull Combinator startWithoutReduction = bufferIfRequested(
                finalGrammar.get(start)
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
