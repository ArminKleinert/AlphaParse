package alphaparse.grammar;

import alphaparse.Sym;
import alphaparse.error.IllegalGrammarException;
import alphaparse.parser.Parser;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parsing.*;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * This class provides an easy way to construct grammars. To use it, it needs to be inherited and the {@link #make()} method overridden.
 * <p>
 * <pre>
 * {@code
 * var pGrammarList = new LinkedHashMap<Sym, Combinator>();
 *         pGrammarList.put(Sym.sym("S"),
 *                 new ConcatCombinator(List.of(new NonTerminalCombinator(Sym.sym("NUMBER")), new PlusCombinator(new NonTerminalCombinator(Sym.sym("NUMBER"))))));
 *         pGrammarList.put(Sym.sym("NUMBER"), new ChoiceCombinator(Stream.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9").map (it -> new TerminalStringCombinator(it, false)).toList()));
 *         var pFromGrammar = new Grammar(pGrammarList);
 * }
 * </pre>
 * <p>
 * The {@link #addProduction(Sym, Combinator)} method can be used to add new productions.
 */
public abstract class GrammarBuilder {
    protected @NotNull LinkedHashMap<Sym, Combinator> productions;
    protected final @NotNull ParserCreationOptions options;
    protected boolean builtAlready = false;

    protected GrammarBuilder(final @NotNull ParserCreationOptions options) {
        productions = new LinkedHashMap<>();
        this.options = options;
    }

    public abstract void make();

    public final @NotNull Grammar build() {
        return buildWithWhitespace(null);
    }

    public final @NotNull Grammar buildWithWhitespace(
            final @Nullable Parser wsParser) {
        return buildWithWhitespace(null, wsParser);
    }

    public final @NotNull Grammar buildWithWhitespace(
            final @Nullable SequencedMap<Sym, Combinator> initialProductions,
            final @Nullable Parser wsParser) {
        if (builtAlready)
            throw new IllegalStateException("Build has been finished already.");

        if (initialProductions != null) {
            for (Map.Entry<Sym, Combinator> entry : initialProductions.sequencedEntrySet()) {
                addProduction(entry.getKey(), entry.getValue());
            }
        }

        make();

        var start = options.startProduction() != null
                ? options.startProduction()
                : productions.sequencedKeySet().getFirst();

        compress();

        applyStandardReductions();
        if (wsParser != null) {
            autoWhitespace(start, wsParser.grammar(), wsParser.startProduction());
        }

        var g = new Grammar(start, productions);
        builtAlready = true;

        if (options.checkCorrectness()) {
            final @NotNull var analysisResult = g.analyze();
            if (!analysisResult.isValid())
                throw new IllegalGrammarException(
                        "The keys "
                                + analysisResult.getUndefinedUsedNTs()
                                + " appear on the right-hand side of the"
                                + " grammar, but not on the left.");
        }

        return g;
    }

    public final void addAllProductions(
            final @NotNull Collection<Map.Entry<Sym, Combinator>> entries) {
        for (Map.Entry<Sym, Combinator> entry : entries) {
            addProduction(entry.getKey(), entry.getValue());
        }
    }

    public final void addProduction(
            final @NotNull Sym lhs, final @NotNull Combinator rhs) {
        var existing = productions.putIfAbsent(lhs, rhs);
        if (existing == null)
            return;

        switch (options.redefinitionOption()) {
            case OVERRIDE -> productions.put(lhs, rhs);
            case ERROR -> throw new IllegalArgumentException(
                    "Production already in grammar: " + lhs);
            case CHOICE -> productions.put(lhs, alternation(existing, rhs));
            case KEEP -> {
            }
        }
    }

    public final void addProduction(
            final @NotNull String lhs, final @NotNull Combinator rhs) {
        addProduction(Sym.sym(lhs), rhs);
    }

    public static @NotNull Combinator staticOf(final @Nullable Object c) {
        return switch (c) {
            case null -> EpsilonCombinator.getDefault();
            case Combinator r -> r;
            case String s -> string(s, false);
            case Pattern p -> new TerminalRegexpCombinator(p);
            case Sym s -> new NonTerminalCombinator(s);
            case List<?> l -> new ConcatCombinator(
                    l.stream().map(GrammarBuilder::staticOf).toList());
            case Set<?> s -> new ChoiceCombinator(
                    s.stream().distinct().map(GrammarBuilder::staticOf).toList());
            default -> throw new IllegalArgumentException(
                    String.valueOf(c.getClass()));
        };
    }

    public final @NotNull Combinator of(final @Nullable Object c) {
        return switch (c) {
            case null -> EpsilonCombinator.getDefault();
            case Combinator r -> r;
            case String s -> string(s);
            case Pattern p -> new TerminalRegexpCombinator(p);
            case Sym s -> new NonTerminalCombinator(s);
            case List<?> l -> new ConcatCombinator(
                    l.stream().map(this::of).toList());
            case Set<?> s -> new ChoiceCombinator(
                    s.stream().distinct().map(this::of).toList());
            default -> throw new IllegalArgumentException(
                    String.valueOf(c.getClass()));
        };
    }

    public final @NotNull Combinator regex(final @NotNull Pattern c) {
        return new TerminalRegexpCombinator(c);
    }

    /**
     * Creates a lookahead combinator.
     * The output can be a {@link EpsilonCombinator} if the input is epsilon.
     *
     * @param c The rule to look for.
     * @return The new combinator.
     */
    public final @NotNull Combinator lookahead(final @NotNull Object c) {
        var combinator = of(c);
        if (combinator instanceof EpsilonCombinator)
            return combinator;
        return new LookaheadCombinator(combinator);
    }

    /**
     * Creates a negative lookahead combinator.
     * The output can be a {@link EpsilonCombinator} if the input is epsilon.
     *
     * @param c The combinator to avoid.
     * @return The new combinator.
     */
    public final @NotNull Combinator negate(final @NotNull Object c) {
        var combinator = of(c);
        if (combinator instanceof EpsilonCombinator)
            return combinator;
        return new NegativeLookaheadCombinator(combinator);
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
        return result;
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
        return result;
    }

    /**
     * Creates a {@link OrderedChoiceCombinator}.
     * <ul>
     * <li>If the argument List is empty, a {@link EpsilonCombinator} is returned instead.</li>
     * <li>If there is only one element in the list, it is returned.</li>
     * <li>Otherwise, returns a {@link OrderedChoiceCombinator}, as expected.</li>
     * </ul>
     *
     * @param rules The parsers for the output.
     * @return A combinator.
     */
    public final @NotNull Combinator orderedChoice(final @NotNull List<Object> rules) {
        var parsers = rules.stream().map(this::of).toList();

        if (parsers.isEmpty())
            return epsilon();

        if (parsers.size() == 1) return parsers.getFirst();

        List<@Nullable Combinator> newParserList = null;
        for (int i = 0; i < parsers.size(); i++) {
            if (parsers.get(i).equals(epsilon())) {
                if (newParserList == null) newParserList = new ArrayList<>(parsers);
                else newParserList.set(i, null); // mark for removal
            }
        }

        if (newParserList == null) {
            newParserList = parsers;
        } else {
            newParserList = newParserList
                    .stream()
                    .filter(Objects::nonNull).toList();
        }

        if (newParserList.size() == 1)
            return Objects.requireNonNull(newParserList.getFirst());
        final @NotNull var result = new OrderedChoiceCombinator(newParserList);
        return result;
    }

    public static @NotNull NonTerminalCombinator nt(final @NotNull Sym name) {
        return new NonTerminalCombinator(name);
    }

    public static @NotNull NonTerminalCombinator nt(final @NotNull String name) {
        return nt(Sym.sym(name));
    }

    /**
     * Creates a {@link TerminalStringCombinator}
     * or {@link EpsilonCombinator} if the string is empty.
     *
     * @param string         The string to match.
     * @param explicitCasing Whether the Terminal will match without caring about casing.
     * @return The new parser.
     */
    public static @NotNull Combinator string(
            final @NotNull String string, final boolean explicitCasing) {
        if (string.isEmpty())
            return EpsilonCombinator.getDefault();
        return new TerminalStringCombinator(string, explicitCasing);
    }

    /**
     * Creates a case-sensitive {@link TerminalStringCombinator}
     * or {@link EpsilonCombinator} if the string is empty.
     *
     * @param string The string to match.
     * @return The new parser.
     */
    public final @NotNull Combinator stringCS(final @NotNull String string) {
        if (string.isEmpty())
            return EpsilonCombinator.getDefault();
        return string(string, false);
    }

    /**
     * Creates a case-insensitive {@link TerminalStringCombinator}
     * or {@link EpsilonCombinator} if the string is empty.
     *
     * @param string The string to match.
     * @return The new parser.
     */
    public final @NotNull Combinator stringCI(final @NotNull String string) {
        if (string.isEmpty())
            return EpsilonCombinator.getDefault();
        return string(string, true);
    }

    /**
     * Creates a {@link TerminalStringCombinator}
     * or {@link EpsilonCombinator} if the string is empty
     * . The case-sensitivity depends on the options used for this {@link GrammarBuilder}.
     *
     * @param string The string to match.
     * @return The new parser.
     */
    public final @NotNull Combinator string(final @NotNull String string) {
        if (string.isEmpty())
            return EpsilonCombinator.getDefault();

        return switch (options.stringCaseInsensitive()) {
            case TRUE -> new TerminalStringCombinator(string, true);
            case FALSE, DEFAULT -> new TerminalStringCombinator(string, false);
        };
    }

    public final @NotNull Combinator hide(final @NotNull Combinator o) {
        return of(o).enableHideTag();
    }

    public final @NotNull Combinator epsilon() {
        return EpsilonCombinator.getDefault();
    }

    public final @NotNull Combinator eof() {
        return EOFCombinator.getDefault();
    }

    @SafeVarargs
    public final <T> @NotNull Combinator concat(
            final @Nullable T rule, final @Nullable T... rules) {
        return concat(Stream.concat(
                        Stream.of(rule),
                        Arrays.stream(rules))
                .filter(Objects::nonNull)
                .map(this::of));
    }

    public final @NotNull Combinator concat(
            final @NotNull Combinator rule, final @NotNull Combinator... rules) {
        return concat(Stream.concat(Stream.of(rule), Arrays.stream(rules)));
    }

    /**
     * Creates a {@link ConcatCombinator}.
     * <ul>
     * <li>If the argument List is empty, a {@link EpsilonCombinator} is returned instead.</li>
     * <li>If there is only one element in the list, it is returned.</li>
     * <li>Otherwise, returns a {@link ConcatCombinator}, as expected.</li>
     * </ul>
     *
     * @param rules The parsers for the output.
     * @return A combinator.
     */
    public final @NotNull Combinator concat(
            final @NotNull List<@NotNull Combinator> rules) {
        return concat(rules.stream());
    }

    private @NotNull Combinator concat(
            final @NotNull Stream<Combinator> ruleStream) {
        final @NotNull List<@NotNull Combinator> result = ruleStream
                .filter(it -> it != EpsilonCombinator.getDefault())
                .toList();
        if (result.isEmpty())
            return epsilon();
        if (result.size() == 1)
            return result.getFirst();
        var compressedResult = new ArrayList<Combinator>();
        for (Combinator combinator : result) {
            if (combinator instanceof ConcatCombinator cc)
                compressedResult.addAll(cc.getParsers());
            else
                compressedResult.add(combinator);
        }
        return new ConcatCombinator(compressedResult);
    }

    /**
     * Creates a {@link ChoiceCombinator}.
     * <ul>
     * <li>If the argument List is empty, a {@link EpsilonCombinator} is returned instead.</li>
     * <li>If there is only one element in the list, it is returned.</li>
     * <li>Otherwise, returns a {@link ChoiceCombinator}, as expected.</li>
     * </ul>
     *
     * @param rule  The first element for the output.
     * @param rules More elements.
     * @return A combinator.
     */
    public final @NotNull Combinator alternation(
            final @NotNull Object rule, final @NotNull Object... rules) {
        List<@NotNull Combinator> result = Stream.concat(
                        Stream.of(rule),
                        Arrays.stream(rules))
                .distinct()
                .map(this::of)
                .distinct()
                .toList();
        return alternationC(result);
    }

    /**
     * Creates a {@link ChoiceCombinator}.
     * <ul>
     * <li>If the argument List is empty, a {@link EpsilonCombinator} is returned instead.</li>
     * <li>If there is only one element in the list, it is returned.</li>
     * <li>Otherwise, returns a {@link ChoiceCombinator}, as expected.</li>
     * </ul>
     *
     * @param rules The rules for the output.
     * @return A combinator.
     */
    public final @NotNull Combinator alternationC(
            final @NotNull List<Combinator> rules) {
        if (rules.isEmpty())
            return epsilon();
        if (rules.size() == 1)
            return rules.getFirst();
        var compressedResult = new ArrayList<Combinator>();
        for (Combinator combinator : rules) {
            if (combinator instanceof ChoiceCombinator cc)
                compressedResult.addAll(cc.getParsers());
            else
                compressedResult.add(combinator);
        }
        return new ChoiceCombinator(compressedResult);
    }

    /**
     * Like {@link #alternationC(List)} except the input
     * list is distinct (each rule in the list occurs exactly once).
     * Use this method only if you are sure that the rules are distinct.
     *
     * @param rules The rules.
     * @return A combinator.
     */
    public final @NotNull Combinator alternationGuaranteeDistinct(
            final @NotNull List<Combinator> rules) {
        if (rules.isEmpty())
            return epsilon();
        if (rules.size() == 1)
            return rules.getFirst();
        var compressedResult = new ArrayList<Combinator>();
        for (Combinator combinator : rules) {
            if (combinator instanceof ChoiceCombinator cc)
                compressedResult.addAll(cc.getParsers());
            else
                compressedResult.add(combinator);
        }
        return new ChoiceCombinator(compressedResult);
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
        return result;
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
     * @param min  The minimum amount of repetitions.
     * @param max  The maximum amount of repetitions.
     * @param rule The parser.
     * @return A combinator, as described.
     */
    public final @NotNull Combinator repeat(
            final @NotNull Combinator rule, final int min, final int max) {
        if (min < 0 || max < min)
            throw new IllegalArgumentException(
                    "Illegal repetition (min=" + min + ", max=" + max + ")");
        if (min == 0 && max == 0)
            return epsilon();

        var r = of(rule);

        if (r instanceof EpsilonCombinator || (min == 1 && max == 1))
            return r;

        if (r instanceof RepetitionCombinator rc) {
            if (rc.getMin() <= 1 && min <= 1) {
                final int newMin = (int) Long.min(
                        Integer.MAX_VALUE,
                        ((long) rc.getMin()) * min);
                final int newMax = (int) Long.min(
                        Integer.MAX_VALUE,
                        ((long) rc.getMax()) * max);
                return new RepetitionCombinator(rc.getParser(), newMin, newMax);
            }
        }

        return new RepetitionCombinator(r, min, max);
    }

    /**
     * Creates a {@link ExclusionCombinator}.
     *
     * @param parserExpected The rule that must be matched.
     * @param parserExcluded The rule that must not be matched.
     * @return A {@link ExclusionCombinator}.
     */
    public final @NotNull Combinator exclude(
            final @NotNull Combinator parserExpected, final @NotNull Combinator parserExcluded) {
        if (parserExpected == parserExcluded) return epsilon();
        final @NotNull var r1 = of(parserExpected);
        final @NotNull var r2 = of(parserExcluded);
        if (Objects.equals(r1, r2)) return epsilon();
        return new ExclusionCombinator(r1, r2);
    }

    public final @NotNull Combinator repeat(
            final @NotNull Combinator rule, final int exact) {
        return repeat(rule, exact, exact);
    }

    public final @NotNull Combinator repeatMin(
            final @NotNull Combinator rule, final int min) {
        return repeat(rule, min, Integer.MAX_VALUE);
    }

    public final @NotNull Combinator repeatMax(
            final @NotNull Combinator rule, final int max) {
        return repeat(rule, 0, max);
    }

    /**
     * Creates a {@link CombinatorStar} or an {@link EpsilonCombinator} if the input is epsilon.
     *
     * @param rule The combinator to match repeatedly.
     * @return A combinator.
     */
    public final @NotNull Combinator zeroOrMore(final @NotNull Combinator rule) {
        if (rule instanceof EpsilonCombinator)
            return rule;
        //return repeat(rule, 0, Integer.MAX_VALUE);
        return new CombinatorStar(rule);
    }

    /**
     * Creates a {@link PlusCombinator} or an {@link EpsilonCombinator} if the input is epsilon.
     *
     * @param rule The combinator to match repeatedly.
     * @return A combinator.
     */
    public final @NotNull Combinator onceOrMore(final @NotNull Combinator rule) {
        if (rule instanceof EpsilonCombinator)
            return rule;
        //return repeat(rule, 1, Integer.MAX_VALUE);
        return new PlusCombinator(rule);
    }

    /**
     * Creates a {@link OptionalCombinator} or an {@link EpsilonCombinator} if the input is epsilon.
     *
     * @param rule The combinator to maybe match.
     * @return A combinator.
     */
    public final @NotNull Combinator optional(final @NotNull Combinator rule) {
        if (rule instanceof EpsilonCombinator)
            return rule;
        return new OptionalCombinator(rule);
    }

    private void compress() {
        var buffer = new HashMap<Combinator, Combinator>();

        for (var symCombinatorEntry : productions.entrySet()) {
            final @NotNull var value = symCombinatorEntry.getValue();
            final @NotNull var compressedCombinator = compressCombinator(value);
            if (compressedCombinator != value) // Yes, I need a reference equality check here.
                symCombinatorEntry.setValue(compressedCombinator);
        }
    }

    private @NotNull Combinator compressCombinator(
            final @NotNull Combinator combinator) {
        return switch (combinator) {
            case RepetitionCombinator repetitionCombinator -> {
                final var min = repetitionCombinator.getMin();
                final var max = repetitionCombinator.getMax();
                final @NotNull var parser = compressCombinator(repetitionCombinator.getParser());

                if (min == 1 && max == Integer.MAX_VALUE) {
                    yield new PlusCombinator(parser);
                } else if (min == 0 && max == 1) {
                    yield new OptionalCombinator(parser);
                } else if (min == 0 && max == Integer.MAX_VALUE) {
                    yield new CombinatorStar(parser);
                } else if (min > 1) {
                    yield repetitionCombinator;
                } else {
                    yield repetitionCombinator;
                }
            }
            case CombinatorWithManyParsers combinatorWithManyParsers ->
                    combinatorWithManyParsers.withParsers(combinatorWithManyParsers
                            .getParsers()
                            .stream()
                            .map(this::compressCombinator)
                            .toList());
            case CombinatorWithParser combinatorWithParser -> combinatorWithParser.withParser(
                    compressCombinator(combinatorWithParser.getParser()));
            case NonTerminalCombinator nonTerminalCombinator -> nonTerminalCombinator;
            case CombinatorTerminal combinatorTerminal -> combinatorTerminal;
            case SimpleCombinator simpleCombinator -> simpleCombinator;
        };
    }

    private void applyStandardReductions() {
        for (var prod : productions.entrySet()) {
            final @NotNull var key = prod.getKey();
            @NotNull var value = prod.getValue();
            if (value.getReduction().getReductionType() == ReductionType.ReductionTypesAvailable.INITIAL) {
                value = value.withReduction(ReductionType.defaultNonRawReduction(key));
            }
            prod.setValue(value);
        }
    }

    private @NotNull Combinator autoWhitespaceHelper(
            final @NotNull Combinator parser,
            final @NotNull Combinator wsParser) {
        return switch (parser) {
            case NonTerminalCombinator ignored -> parser;
            case EpsilonCombinator ignored2 -> parser;
            case CombinatorWithParser parser1 -> (parser1.withParser(autoWhitespaceHelper(
                    parser1.getParser(), wsParser)));
            case CombinatorWithManyParsers combWithParsers -> {
                final @NotNull List<@NotNull Combinator> parsers = combWithParsers
                        .getParsers()
                        .stream()
                        .map(p -> autoWhitespaceHelper(p, wsParser))
                        .toList();
                yield (combWithParsers.withParsers(parsers));
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
                    result = concat(parsers).withReduction(
                            parser.getReduction());
                } else {
                    parsers.add(parser);
                    result = concat(parsers);
                }
                yield result;
            }
            case SimpleCombinator ignored -> parser;
        };
    }

    private void autoWhitespace(final @NotNull Sym start,
                                final @NotNull Grammar grammarWS,
                                final @NotNull Sym startWS) {
        final @NotNull Combinator wsParser =
                optional(nt(startWS)).enableHideTag();

        final @NotNull LinkedHashMap<@NotNull Sym, @NotNull Combinator> finalGrammar =
                new LinkedHashMap<>(productions);
        for (var keywordCombinatorEntry : finalGrammar.sequencedEntrySet()) {
            keywordCombinatorEntry.setValue(autoWhitespaceHelper(
                    keywordCombinatorEntry.getValue(), wsParser));
        }

        final @NotNull Combinator startWithoutReduction = (
                finalGrammar.get(start)
                        .withReduction(ReductionType.standardInitialReduction()));
        final @NotNull Combinator newStartComb =
                concat(List.of(startWithoutReduction, wsParser))
                        .withReduction(finalGrammar.get(start).getReduction());

        finalGrammar.put(start, newStartComb);
        finalGrammar.putAll(grammarWS);
        finalGrammar.put(startWS,
                Objects.requireNonNull(grammarWS.getProduction(startWS)).hideTag());

        productions = finalGrammar;
    }
}
