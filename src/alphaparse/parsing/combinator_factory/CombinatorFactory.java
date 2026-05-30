package alphaparse.parsing.combinator_factory;

import alphaparse.grammar.Grammar;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.ParsingOptions;
import alphaparse.Sym;
import alphaparse.parsing.*;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * A factory for creating different types of {@link Combinator} objects.
 */
public final class CombinatorFactory {
    private final @NotNull EpsilonCombinator epsilon;
    private final @NotNull BufferForCombinators buffer;

    /**
     * The constructor.
     */
    public CombinatorFactory() {
        epsilon = EpsilonCombinator.getDefault();
        buffer = new BufferForCombinators();
    }

    /**
     * Ensures that as long as the factory exists, the following is true:
     * {@code if (Objects.equals(x, y)) { combinatorFactory.buffer(x) == combinatorFactory.buffer(y); }}
     *
     * @param c A combinator.
     * @return The input or a buffered equivalent.
     */
    public @NotNull Combinator buffer(final Combinator c) {
        return buffer.getOrAdd(c);
    }

    /**
     * Represents a special sequence. The typical EBNF-notation is {@code ?...?}
     * where {@code ...} stands for some free-form text.
     * For example, {@code S = ?any whitespace except newline?} means what it says.
     * This allows some interaction with the program around the parser.
     * However, special sequences have to be heavily abstracted here.
     *
     * @param description The description of the special sequence.
     * @param function    The function which does what the description says.
     * @return A {@link TerminalSpecialSequenceCombinator}.
     */
    public @NotNull TerminalSpecialSequenceCombinator specialSequence(
            final @NotNull String description,
            final @NotNull Function<@NotNull String, Optional<String>> function) {
        var result = new TerminalSpecialSequenceCombinator(description, function);
        return (TerminalSpecialSequenceCombinator) buffer.getOrAdd(result);
    }

    /**
     * Creates a {@link ExclusionCombinator}.
     *
     * @param parserExpected The rule that must be matched.
     * @param parserExcluded The rule that must not be matched.
     * @return A {@link ExclusionCombinator}.
     */
    public @NotNull ExclusionCombinator exclusionCombinator(
            final @NotNull Combinator parserExpected,
            final @NotNull Combinator parserExcluded) {
        var result = new ExclusionCombinator(parserExpected, parserExcluded);
        return buffer.getOrAdd(result);
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
    public @NotNull Combinator choiceCombinator(
            final @NotNull List<@NotNull Combinator> parsers) {
        if (parsers.isEmpty()) return EpsilonCombinator.getDefault();
        if (parsers.size() == 1) return parsers.getFirst();
        return choiceCombinatorDistinct(parsers.stream().distinct().toList());
    }

    /**
     * Like {@link CombinatorFactory#choiceCombinator(List)} except the input
     * list is distinct (each rule in the list occurs exactly once).
     * Use this method only if you are sure that the rules are distinct.
     *
     * @param parsers The rules.
     * @return A combinator.
     * @see #choiceCombinator(List)
     * @see #alternationCombinator(List)
     */
    public @NotNull Combinator choiceCombinatorDistinct(
            final @NotNull List<@NotNull Combinator> parsers) {
        if (parsers.isEmpty()) return EpsilonCombinator.getDefault();
        if (parsers.size() == 1) return parsers.getFirst();
        final @NotNull var result = new ChoiceCombinator(parsers);
        return buffer.getOrAdd(result);
    }

    /**
     * Alias for {@link CombinatorFactory#choiceCombinator(List)}.
     *
     * @param parsers The parsers for the output.
     * @return A combinator.
     */
    public @NotNull Combinator alternationCombinator(
            final @NotNull List<@NotNull Combinator> parsers) {
        return choiceCombinator(parsers);
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
        return buffer.getOrAdd(result);
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
        return buffer.getOrAdd(result);
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
        return buffer.getOrAdd(result);
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
    public @NotNull Combinator repetitionCombinator(
            final int m, final int n, final @NotNull Combinator parser) {
        if (m < 0 || m > n) {
            var msg = "Minimum number of repetitions must be below maximum number of repetitions.";
            throw new IllegalArgumentException(msg);
        }
        if ((m == 0 && n == 0) || parser.equals(epsilon)) return epsilon;
        if (m == 1 && n == 1) return parser;
        if (m == n) {
            final int repetitionThreshold = 4;
            if (m <= repetitionThreshold)
                return catCombinator(Collections.nCopies(m, parser));
            // Otherwise fallthrough.
        }
        final @NotNull var result = new RepetitionCombinator(parser, m, n);
        return buffer.getOrAdd(result);
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
    public @NotNull Combinator orderedChoiceCombinator(
            final @NotNull List<@NotNull Combinator> parsers) {
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
            newParserList = newParserList
                    .stream()
                    .filter(Objects::nonNull).toList();

        if (newParserList.size() == 1)
            return Objects.requireNonNull(newParserList.getFirst());
        final @NotNull var result = new OrderedChoiceCombinator(newParserList);
        return buffer.getOrAdd(result);
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
    public @NotNull Combinator catCombinator(
            final @NotNull List<@NotNull Combinator> parsers) {
        final var parserStream = parsers
                .stream()
                .filter(p -> !p.equals(epsilon)).iterator();

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
        return buffer.getOrAdd(result);
    }

    /**
     * Creates a {@link TerminalStringCombinator}
     * or {@link EpsilonCombinator} if the string is empty.
     *
     * @param string          The string to match.
     * @param caseInsensitive Whether the Terminal will match without caring about casing.
     * @return The new parser.
     */
    public @NotNull Combinator stringTerminal(final @NotNull String string,
                                              final boolean caseInsensitive) {
        if (string.isEmpty()) return epsilon;
        final @NotNull var result = new TerminalStringCombinator(string, caseInsensitive);
        return buffer.getOrAdd(result);
    }

    /**
     * Creates a case-sensitive {@link TerminalStringCombinator}
     * or {@link EpsilonCombinator} if the string is empty.
     *
     * @param string The string to match.
     * @return The new parser.
     * @see CombinatorFactory#stringTerminal(String, boolean)
     */
    public @NotNull Combinator stringTerminal(final @NotNull String string) {
        return stringTerminal(string, false);
    }

    /**
     * Creates a {@link Combinator} to match the codepoint.
     * This can be any kind of {@link Combinator} capable of accomplishing that task.
     *
     * @param lohi The codepoint.
     * @return The new parser.
     */
    public @NotNull Combinator unicodeChar(final int lohi) {
        final @NotNull var result = new TerminalUnicodeCharCombinator(lohi, lohi);
        return buffer.getOrAdd(result);
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

        final @NotNull var result = new TerminalUnicodeCharCombinator(lo, hi);
        return buffer.getOrAdd(result);
    }

    /**
     * Creates a new regex terminal.
     *
     * @param regex The pattern.
     * @return The new parser.
     */
    public @NotNull TerminalRegexpCombinator createRegexTerminal(
            final @NotNull Pattern regex) {
        final @NotNull var result = new TerminalRegexpCombinator(regex);
        return buffer.getOrAdd(result);
    }

    /**
     * Creates a new non-terminal.
     *
     * @param keyword The name of the non-terminal.
     * @return The new parser.
     */
    public @NotNull NonTerminalCombinator makeNonTerminal(
            final @NotNull Sym keyword) {
        var temp = buffer.nt(keyword);
        if (temp == null) {
            temp = new NonTerminalCombinator(keyword);
            buffer.putNt(temp);
        }
        return temp;
    }

    /**
     * Creates a new non-terminal. This is a static method which can not use buffering-
     *
     * @param keyword The name of the non-terminal.
     * @return The new parser.
     */
    public static @NotNull NonTerminalCombinator staticMakeNonTerminal(
            final @NotNull Sym keyword) {
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
        return buffer.getOrAdd(result);
    }

    /**
     * Creates a negative lookahead combinator.
     * The output can be a {@link EpsilonCombinator} if the input is epsilon.
     *
     * @param parser The combinator to avoid.
     * @return The new combinator.
     */
    public @NotNull Combinator negateRule(final @NotNull Combinator parser) {
        if (parser.equals(epsilon)) return epsilon;
        final @NotNull var result = new NegativeLookaheadCombinator(parser);
        return buffer.getOrAdd(result);
    }

    /**
     * Hides the tag of a combinator in the result of a parse.
     *
     * @param parser The parser.
     * @return The new parser.
     */
    public @NotNull Combinator hideTag(final @NotNull Combinator parser) {
        final @NotNull var result = parser.hideTag();
        return buffer.getOrAdd(result);
    }

    /**
     * Applies {@link Combinator#unhideContent} to all entries in the grammar.
     *
     * @param grammar The grammar.
     * @return The new grammar.
     * @see ParsingOptions#unhide()
     */
    public @NotNull Grammar unhideAllContent(final @NotNull Grammar grammar) {
        final @NotNull LinkedHashMap<Sym, Combinator> res = new LinkedHashMap<>();
        for (final @NotNull var keywordCombinatorEntry : grammar.sequencedEntrySet()) {
            final @NotNull var key = keywordCombinatorEntry.getKey();
            final @NotNull var value = keywordCombinatorEntry.getValue();
            final @NotNull var pUnhide = buffer(value.unhideContent());
            res.put(key, pUnhide);
        }
        return new Grammar(res);
    }

    /**
     * Applies the reduction-type to all entries in the grammar.
     *
     * @param grammar The grammar.
     * @return The new grammar.
     */
    public @NotNull Grammar unhideTags(final @NotNull Grammar grammar) {
        final @NotNull LinkedHashMap<Sym, Combinator> res = new LinkedHashMap<>();
        for (final @NotNull var keywordCombinatorEntry : grammar.sequencedEntrySet()) {
            final @NotNull var key = keywordCombinatorEntry.getKey();
            final @NotNull var value = keywordCombinatorEntry.getValue();
            final @NotNull var reduction = ReductionType.nonTerminalReduction(key);
            final @NotNull var pUnhide = buffer(value.withReduction(reduction));
            res.put(key, pUnhide);
        }
        return new Grammar(res);
    }

    /**
     * Applies the reduction-type to all entries in the grammar
     * and applies {@link Combinator#unhideContent()}.
     *
     * @param grammar The grammar.
     * @return The new grammar.
     */
    public @NotNull Grammar unhideAll(final @NotNull Grammar grammar) {
        final @NotNull LinkedHashMap<Sym, Combinator> res = new LinkedHashMap<>();
        for (final @NotNull var keywordCombinatorEntry : grammar.sequencedEntrySet()) {
            final @NotNull var key = keywordCombinatorEntry.getKey();
            final @NotNull var value = keywordCombinatorEntry.getValue();
            final @NotNull var reduction = ReductionType.nonTerminalReduction(key);
            final @NotNull var p = buffer(value.unhideContent().withReduction(reduction));
            res.put(key, p);
        }
        return new Grammar(res);
    }

    private @NotNull Combinator autoWhitespaceHelper(
            final @NotNull Combinator parser,
            final @NotNull Combinator wsParser) {
        return switch (parser) {
            case NonTerminalCombinator ignored -> parser;
            case EpsilonCombinator ignored2 -> parser;
            case CombinatorWithParser parser1 ->
                    buffer(parser1.withParser(autoWhitespaceHelper(
                            parser1.getParser(), wsParser)));
            case CombinatorWithManyParsers combWithParsers -> {
                final @NotNull List<@NotNull Combinator> parsers = combWithParsers
                        .getParsers()
                        .stream()
                        .map(p -> autoWhitespaceHelper(p, wsParser))
                        .toList();
                yield buffer(combWithParsers.withParsers(parsers));
            }
            case CombinatorTerminal ignored -> {
                final @NotNull List<Combinator> parsers = new ArrayList<>();
                parsers.add(wsParser);
                final @NotNull Combinator result;
                if (!parser.getReduction().isHiddenOrRaw()) {
                    // Hide the terminal in the output.
                    // It still appears in the tree, but is flattened into the concatenation.
                    parsers.add(parser.withReduction(ReductionType.
                            standardIntermediateReduction()));
                    result = catCombinator(parsers).withReduction(
                            parser.getReduction());
                } else {
                    parsers.add(parser);
                    result = catCombinator(parsers);
                }
                yield result;
            }
        };
    }

    /**
     * Merges another grammar into this grammar which eats whitespaces (at
     * least that is the intended purpose).
     * This method should not be used directly.
     * Use {@link ParserCreationOptions#withWhitespaceParser} or set the
     * option in the {@link ParserCreationOptions} instead when creating a parser.
     *
     * @param grammar   The main grammar.
     * @param start     The starting symbol of the main grammar.
     * @param grammarWS The whitespace grammar.
     * @param startWS   The starting symbol of the whitespace grammar.
     * @return The new grammar.
     */
    public @NotNull Grammar autoWhitespace(final @NotNull Grammar grammar,
                                           final @NotNull Sym start,
                                           final @NotNull Grammar grammarWS,
                                           final @NotNull Sym startWS) {
        final @NotNull Combinator wsParser =
                optionalCombinator(makeNonTerminal(startWS)).enableHideTag();

        final @NotNull SequencedMap<@NotNull Sym, @NotNull Combinator> finalGrammar =
                new LinkedHashMap<>(grammar);
        for (var keywordCombinatorEntry : finalGrammar.sequencedEntrySet()) {
            keywordCombinatorEntry.setValue(autoWhitespaceHelper(
                    keywordCombinatorEntry.getValue(), wsParser));
        }

        final @NotNull Combinator startWithoutReduction = buffer(
                finalGrammar.get(start)
                        .withReduction(ReductionType.standardInitialReduction()));
        final @NotNull Combinator newStartComb =
                catCombinator(List.of(startWithoutReduction, wsParser))
                        .withReduction(finalGrammar.get(start).getReduction());

        finalGrammar.put(start, newStartComb);
        finalGrammar.putAll(grammarWS);
        finalGrammar.put(startWS, hideTag(
                Objects.requireNonNull(grammarWS.getProduction(startWS))));
        return new Grammar(finalGrammar);
    }
}
