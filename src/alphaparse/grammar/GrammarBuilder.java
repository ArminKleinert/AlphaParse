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
 * Using a GrammarBuilder is much faster than constructing grammars from strings.
 * <p>
 * In the following example, two equivalent grammars and parsers are constructed from a string and from a builder. The assertions show that the grammars are equal and that the parsers give equivalent outputs.
 * <pre>
 * {@code
 *         var gFromString = Alpha.parser("""
 *                         S = NUMBER NUMBER*
 *                         NUMBER = '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9'
 *                         """)
 *                 .grammar();
 *         var gFromGB = new GrammarBuilder(ParserCreationOptions.getDefault()) {
 *             @Override
 *             public void make() {
 *                 addProduction("S", concat(Sym.sym("NUMBER"), repeatMin(nt(Sym.sym("NUMBER")), 0)));
 *                 addProduction("NUMBER", alternation("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
 *             }
 *         }.build();
 *
 *         Assertions.assertEquals(gFromString, gFromGB);
 *
 *         var pFromString = Alpha.parser(gFromString, ParserCreationOptions.getDefault().withStartProduction(Sym.sym("S")));
 *         var pFromGB = Alpha.parser(gFromGB, ParserCreationOptions.getDefault().withStartProduction(Sym.sym("S")));
 *         var text = "0123456789";
 *         Assertions.assertEquals(pFromString.parse(text), pFromGB.parse(text));
 * }
 * </pre>
 * <p>
 * The {@link #addProduction(Sym, Rule)} method can be used to add new productions.
 */
public abstract class GrammarBuilder {
    protected @NotNull LinkedHashMap<Sym, Rule> productions;
    protected final @NotNull ParserCreationOptions options;
    protected boolean builtAlready = false;
    private final BufferForRules buffer;

    protected GrammarBuilder(final @NotNull ParserCreationOptions options) {
        productions = new LinkedHashMap<>();
        this.options = options;
        buffer = new BufferForRules();
    }

    /**
     * Override this to create a grammar. Used in {@link #build()}.
     */
    public abstract void make();

    /**
     * Use this to construct the grammar.
     *
     * @return The grammar.
     */
    public final @NotNull Grammar build() {
        return buildWithWhitespace(null);
    }

    /**
     * Use this to construct the grammar.
     *
     * @param wsParser Whitespace parser to include.
     * @return The grammar.
     */
    public final @NotNull Grammar buildWithWhitespace(
            final @Nullable Parser wsParser) {
        return buildWithWhitespace(null, wsParser);
    }

    /**
     * Use this to construct the grammar. Productions can be added before starting the builder.
     *
     * @param initialProductions Productions to add in the beginning.
     * @param wsParser           Whitespace parser to include.
     * @return The grammar.
     */
    public final @NotNull Grammar buildWithWhitespace(
            final @Nullable SequencedMap<Sym, Rule> initialProductions,
            final @Nullable Parser wsParser) {
        if (builtAlready)
            throw new IllegalStateException("Build has been finished already.");

        if (initialProductions != null) {
            for (final @NotNull var entry : initialProductions.sequencedEntrySet()) {
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

    /**
     * Add multiple entries.
     *
     * @param entries The entries.
     * @see #addProduction(Sym, Rule)
     */
    public final void addAllProductions(
            final @NotNull Collection<Map.Entry<Sym, Rule>> entries) {
        for (final @NotNull var entry : entries) {
            addProduction(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Adds a production to the output. The specific behavior depends on the {@link ParserCreationOptions#redefinitionOption()} used.
     *
     * @param lhs The production's key. (left-hand-side)
     * @param rhs The production's right-hand-side.
     */
    public final void addProduction(
            final @NotNull Sym lhs, final @NotNull Rule rhs) {
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

    /**
     * Adds a production to the output.
     *
     * @param lhs The production's key. (left-hand-side)
     * @param rhs The production's right-hand-side.
     * @see #addProduction(Sym, Rule)
     */
    public final void addProduction(
            final @NotNull String lhs, final @NotNull Rule rhs) {
        addProduction(Sym.sym(lhs), rhs);
    }

    /**
     * Creates a rule depending on the input's specific type.
     * <ul>
     * <li>For {@code null}, use {@link EpsilonTerm#getDefault()}.</li>
     * <li>For {@code Rule}, return the input.</li>
     * <li>For {@code String}, use {@link #string(String)}.</li>
     * <li>For {@code Pattern}, use {@link #regex(Pattern)}.</li>
     * <li>For {@code Sym}, use {@link #nt(Sym)}.</li>
     * <li>For {@code List}, use {@link #concat(List)}.</li>
     * <li>For {@code Set}, use {@link #alternation(Object, Object...)}.</li>
     * </ul>
     *
     * @param c Input object.
     * @return A rule depending on the input's type.
     */
    public static @NotNull Rule staticOf(final @Nullable Object c) {
        return switch (c) {
            case null -> EpsilonTerm.getDefault();
            case Rule r -> r;
            case String s -> new StringTerm(s, false);
            case Pattern p -> new RegexTerm(p);
            case Sym s -> NonTerminal.create(s);
            case List<?> l -> new ConcatRule(
                    l.stream().map(GrammarBuilder::staticOf).toList());
            case Set<?> s -> new AlternationRule(
                    s.stream().distinct().map(GrammarBuilder::staticOf).toList());
            default -> throw new IllegalArgumentException(
                    String.valueOf(c.getClass()));
        };
    }

    /**
     * Creates a rule depending on the input's specific type.
     * <ul>
     * <li>For {@code null}, use {@link #epsilon()}.</li>
     * <li>For {@code Rule}, return the input.</li>
     * <li>For {@code String}, use {@link #string(String)}.</li>
     * <li>For {@code Pattern}, use {@link #regex(Pattern)}.</li>
     * <li>For {@code Sym}, use {@link #nt(Sym)}.</li>
     * <li>For {@code List}, use {@link #concat(List)}.</li>
     * <li>For {@code Set}, use {@link #alternation(Object, Object...)}.</li>
     * </ul>
     *
     * @param c Input object.
     * @return A rule depending on the input's type.
     */
    public final @NotNull Rule of(final @Nullable Object c) {
        return switch (c) {
            case null -> EpsilonTerm.getDefault();
            case Rule r -> r;
            case String s -> string(s);
            case Pattern p -> regex(p);
            case Sym s -> nt(s);
            case List<?> l -> concat(l.stream().map(this::of).toList());
            case Set<?> s -> alternationC(s.stream().distinct().map(this::of).toList());
            default -> throw new IllegalArgumentException(
                    String.valueOf(c.getClass()));
        };
    }

    /**
     * Create a {@link RegexTerm}. The rule might be buffered.
     * If the regex can only ever match a single string, a {@link StringTerm} might be returned instead.
     *
     * @param p The input regex.
     * @return A {@link RegexTerm} or something that returns equivalent outputs when parsing.
     */
    public final @NotNull Rule regex(final @NotNull Pattern p) {
        if (allCharsAreAlphaNumeric(p.pattern()))
            return stringCS(p.pattern());
        return buffer.getOrAddRegex(p);
    }

    /**
     * See {@link #regex(Pattern)}.
     *
     * @param s The input regex.
     * @return A {@link RegexTerm} or something that returns equivalent outputs when parsing.
     */
    public final @NotNull Rule regex(final @NotNull String s) {
        if (s.isEmpty())
            return EpsilonTerm.getDefault();
        if (allCharsAreAlphaNumeric(s))
            return stringCS(s);
        return buffer.getOrAddRegex(Pattern.compile(s));
    }

    public static boolean allCharsAreAlphaNumeric(final @NotNull String s) {
        return s.chars().allMatch(Character::isLetterOrDigit);
    }

    /**
     * Creates a {@link LookaheadRule}.
     * The output can be a {@link EpsilonTerm} if the input is epsilon.
     *
     * @param input The rule to look for.
     * @return The new rule.
     */
    public final @NotNull Rule lookahead(final @NotNull Object input) {
        var rule = of(input);
        if (rule instanceof EpsilonTerm)
            return rule;
        return new LookaheadRule(rule);
    }

    /**
     * Creates a {@link NegativeLookaheadRule}.
     * The output can be a {@link EpsilonTerm} if the input is epsilon.
     *
     * @param input The rule to avoid.
     * @return The new rule.
     */
    public final @NotNull Rule negate(final @NotNull Object input) {
        var rule = of(input);
        if (rule instanceof EpsilonTerm)
            return rule;
        return new NegativeLookaheadRule(rule);
    }

    /**
     * Creates a {@link Rule} to match the codepoint range.
     * This can be any kind of {@link Rule} capable of accomplishing that task.
     *
     * @param lo The minimum codepoint.
     * @param hi The maximum codepoint.
     * @return The new parser.
     */
    public @NotNull Rule unicodeChar(final int lo, final int hi) {
        if (lo > hi)
            throw new IllegalArgumentException();

        if (lo == hi)
            return unicodeChar(lo);

        final @NotNull var result = new ValueRangeTerm(lo, hi);
        return result;
    }

    /**
     * Creates a {@link Rule} to match the codepoint.
     * This can be any kind of {@link Rule} capable of accomplishing that task.
     *
     * @param lohi The codepoint.
     * @return The new parser.
     */
    public @NotNull Rule unicodeChar(final int lohi) {
        final @NotNull var result = new ValueRangeTerm(lohi, lohi);
        return result;
    }

    /**
     * Creates a {@link OrderedChoiceRule}.
     * <ul>
     * <li>If the argument List is empty, a {@link EpsilonTerm} is returned instead.</li>
     * <li>If there is only one element in the list, it is returned.</li>
     * <li>Otherwise, returns a {@link OrderedChoiceRule}, as expected.</li>
     * </ul>
     *
     * @param rules The parsers for the output.
     * @return A rule.
     */
    public final @NotNull Rule orderedChoice(final @NotNull List<Object> rules) {
        var parsers = rules.stream().map(this::of).toList();

        if (parsers.isEmpty())
            return epsilon();

        if (parsers.size() == 1) return parsers.getFirst();

        List<@Nullable Rule> newParserList = null;
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
        final @NotNull var result = new OrderedChoiceRule(newParserList);
        return result;
    }

    /**
     * Create a {@link NonTerminal} from the input symbol. The output might be buffered.
     *
     * @param name The name symbol of the output rule.
     * @return A {@link NonTerminal}.
     */
    public @NotNull NonTerminal nt(final @NotNull Sym name) {
        return buffer.getOrAddNt(name);
    }

    /**
     * Equivalent to {@link #nt(Sym)} with the symbol creates from the input string.
     *
     * @param name The name symbol of the output rule.
     * @return A {@link NonTerminal}.
     */
    public @NotNull NonTerminal nt(final @NotNull String name) {
        return nt(Sym.sym(name));
    }

    /**
     * Creates a {@link StringTerm}
     * or {@link EpsilonTerm} if the string is empty.
     *
     * @param string         The string to match.
     * @param caseInsensitive Whether the Terminal will match case insensitive.
     * @return The new parser.
     */
    public @NotNull Rule string(final @NotNull String string, final boolean caseInsensitive) {
        if (string.isEmpty())
            return EpsilonTerm.getDefault();
        return buffer.getOrAddString(string, caseInsensitive);
    }

    /**
     * Creates a case-sensitive {@link StringTerm}
     * or {@link EpsilonTerm} if the string is empty.
     *
     * @param string The string to match.
     * @return The new parser.
     */
    public final @NotNull Rule stringCS(final @NotNull String string) {
        if (string.isEmpty())
            return EpsilonTerm.getDefault();
        return string(string, false);
    }

    /**
     * Creates a case-insensitive {@link StringTerm}
     * or {@link EpsilonTerm} if the string is empty.
     *
     * @param string The string to match.
     * @return The new parser.
     */
    public final @NotNull Rule stringCI(final @NotNull String string) {
        if (string.isEmpty())
            return EpsilonTerm.getDefault();
        return string(string, true);
    }

    /**
     * Creates a {@link StringTerm}
     * or {@link EpsilonTerm} if the string is empty
     * . The case-sensitivity depends on the options used for this {@link GrammarBuilder}.
     *
     * @param string The string to match.
     * @return The new parser.
     */
    public final @NotNull Rule string(final @NotNull String string) {
        if (string.isEmpty())
            return EpsilonTerm.getDefault();
        return switch (options.stringCaseInsensitive()) {
            case TRUE -> buffer.getOrAddString(string, true);
            case FALSE, DEFAULT -> buffer.getOrAddString(string, false);
        };
    }

    /**
     * Create a copy of the input for which {@link Rule#isHidden} returns true.
     *
     * @param o The input.
     * @return A copy of the input for which {@link Rule#isHidden} returns true.
     */
    public final @NotNull Rule hide(final @NotNull Object o) {
        return of(o).enableHideTag();
    }

    /**
     * Get an epsilon rule.
     *
     * @return An {@link EpsilonTerm}.
     */
    public final @NotNull Rule epsilon() {
        return EpsilonTerm.getDefault();
    }

    /**
     * Create a rule which matches end of input.
     *
     * @return A rule.
     */
    public final @NotNull Rule eof() {
        return EOFTerm.getDefault();
    }

    /**
     * Creates a {@link ConcatRule}.
     * <ul>
     * <li>If the argument List is empty, a {@link EpsilonTerm} is returned instead.</li>
     * <li>If there is only one element in the list, it is returned.</li>
     * <li>Otherwise, returns a {@link ConcatRule}, as expected.</li>
     * </ul>
     *
     * @param rule  First rule for the output.
     * @param rules More rules for the output.
     * @param <T>   Type for the rules.
     * @return A rule.
     */
    @SafeVarargs
    public final <T> @NotNull Rule concat(
            final @Nullable T rule, final @Nullable T... rules) {
        return concat(Stream.concat(
                        Stream.of(rule),
                        Arrays.stream(rules))
                .filter(Objects::nonNull)
                .map(this::of));
    }

    /**
     * Creates a {@link ConcatRule}.
     * <ul>
     * <li>If the argument List is empty, a {@link EpsilonTerm} is returned instead.</li>
     * <li>If there is only one element in the list, it is returned.</li>
     * <li>Otherwise, returns a {@link ConcatRule}, as expected.</li>
     * </ul>
     *
     * @param rules The parsers for the output.
     * @return A rule.
     */
    public final @NotNull Rule concat(
            final @NotNull List<@NotNull Rule> rules) {
        return concat(rules.stream());
    }

    private @NotNull Rule concat(
            final @NotNull Stream<Rule> ruleStream) {
        final @NotNull List<@NotNull Rule> result = ruleStream
                .filter(it -> it != EpsilonTerm.getDefault())
                .toList();
        if (result.isEmpty())
            return epsilon();
        if (result.size() == 1)
            return result.getFirst();
        var compressedResult = new ArrayList<Rule>();
        for (final @NotNull Rule rule : result) {
            if (rule instanceof ConcatRule cc) {
                compressedResult.addAll(cc.getParsers());
            } else {
                compressedResult.add(rule);
            }
        }
        return new ConcatRule(compressedResult);
    }

    /**
     * Creates a {@link AlternationRule}.
     * <ul>
     * <li>If the argument List is empty, a {@link EpsilonTerm} is returned instead.</li>
     * <li>If there is only one element in the list, it is returned.</li>
     * <li>Otherwise, returns a {@link AlternationRule}, as expected.</li>
     * </ul>
     *
     * @param rule  The first element for the output.
     * @param rules More elements.
     * @return A rule.
     */
    public final @NotNull Rule alternation(
            final @NotNull Object rule, final @NotNull Object... rules) {
        final @NotNull List<@NotNull Rule> result = Stream
                .concat(Stream.of(rule),
                        Arrays.stream(rules))
                .distinct()
                .map(this::of)
                .distinct()
                .toList();
        return alternationC(result);
    }

    /**
     * Creates a {@link AlternationRule}.
     * <ul>
     * <li>If the argument List is empty, a {@link EpsilonTerm} is returned instead.</li>
     * <li>If there is only one element in the list, it is returned.</li>
     * <li>Otherwise, returns a {@link AlternationRule}, as expected.</li>
     * </ul>
     *
     * @param rules The rules for the output.
     * @return A rule.
     */
    public final @NotNull Rule alternationC(
            final @NotNull List<Rule> rules) {
        if (rules.isEmpty())
            return epsilon();
        if (rules.size() == 1)
            return rules.getFirst();
        var compressedResult = new ArrayList<Rule>();
        for (final @NotNull Rule rule : rules) {
            if (rule instanceof AlternationRule cc)
                compressedResult.addAll(cc.getParsers());
            else
                compressedResult.add(rule);
        }
        return new AlternationRule(compressedResult);
    }

    /**
     * Like {@link #alternationC(List)} except the input
     * list is distinct (each rule in the list occurs exactly once).
     * Use this method only if you are sure that the rules are distinct.
     *
     * @param rules The rules.
     * @return A rule.
     */
    public final @NotNull Rule alternationGuaranteeDistinct(
            final @NotNull List<Rule> rules) {
        if (rules.isEmpty())
            return epsilon();
        if (rules.size() == 1)
            return rules.getFirst();
        var compressedResult = new ArrayList<Rule>();
        for (final @NotNull Rule rule : rules) {
            if (rule instanceof AlternationRule cc)
                compressedResult.addAll(cc.getParsers());
            else
                compressedResult.add(rule);
        }
        return new AlternationRule(compressedResult);
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
     * @return A {@link SpecialSequenceRule}.
     */
    public @NotNull SpecialSequenceRule specialSequence(
            final @NotNull String description,
            final @NotNull Function<@NotNull String, Optional<String>> function) {
        return new SpecialSequenceRule(description, function);
    }

    /**
     * Creates a {@link ZeroOrMoreRule} or an {@link EpsilonTerm} if the input is epsilon.
     * <ul>
     * <li>If minimum and maximum amount of repetitions is set to 0, return an {@link EpsilonTerm}.</li>
     * <li>If minimum and maximum amount of repetitions is set to 1, may return the input parser.</li>
     * <li>If minimum and maximum amount of repetitions are equal, a {@link ConcatRule} might be returned instead.</li>
     * <li>Otherwise, does as normally expected.</li>
     * </ul>
     *
     * @param min  The minimum amount of repetitions.
     * @param max  The maximum amount of repetitions.
     * @param rule The parser.
     * @return A rule, as described.
     */
    public final @NotNull Rule repeat(
            final @NotNull Rule rule, final int min, final int max) {
        if (min < 0 || max < min)
            throw new IllegalArgumentException(
                    "Illegal repetition (min=" + min + ", max=" + max + ")");
        if (min == 0 && max == 0)
            return epsilon();

        var r = of(rule);

        if (r instanceof EpsilonTerm || (min == 1 && max == 1))
            return r;

        if (r instanceof VariableRepetitionRule rc) {
            if (rc.getMin() <= 1 && min <= 1) {
                final int newMin = (int) Long.min(
                        Integer.MAX_VALUE,
                        ((long) rc.getMin()) * min);
                final int newMax = (int) Long.min(
                        Integer.MAX_VALUE,
                        ((long) rc.getMax()) * max);
                return new VariableRepetitionRule(rc.getParser(), newMin, newMax);
            }
        }

        return new VariableRepetitionRule(r, min, max);
    }

    /**
     * Creates a {@link ExclusionRule}.
     *
     * @param parserExpected The rule that must be matched.
     * @param parserExcluded The rule that must not be matched.
     * @return A {@link ExclusionRule}.
     */
    public final @NotNull Rule exclude(
            final @NotNull Rule parserExpected, final @NotNull Rule parserExcluded) {
        if (parserExpected == parserExcluded) return epsilon();
        final @NotNull var r1 = of(parserExpected);
        final @NotNull var r2 = of(parserExcluded);
        if (Objects.equals(r1, r2)) return epsilon();
        return new ExclusionRule(r1, r2);
    }

    /**
     * Equivalent to {@code repeat(rule, exact, exact)}.
     *
     * @param rule  The rule.
     * @param exact Minimum and maximum number of repetitions.
     * @return A repetition rule.
     * @see #repeat(Rule, int, int)
     */
    public final @NotNull Rule repeat(
            final @NotNull Rule rule, final int exact) {
        return repeat(rule, exact, exact);
    }

    /**
     * Equivalent to {@code repeat(rule, min, Integer.MAX_VALUE)}.
     *
     * @param rule The rule.
     * @param min  Minimum number of repetitions.
     * @return A repetition rule.
     * @see #repeat(Rule, int, int)
     */
    public final @NotNull Rule repeatMin(
            final @NotNull Rule rule, final int min) {
        return repeat(rule, min, Integer.MAX_VALUE);
    }

    /**
     * Equivalent to {@code repeat(rule, 0, max)}.
     *
     * @param rule The rule.
     * @param max  Maximum number of repetitions.
     * @return A repetition rule.
     * @see #repeat(Rule, int, int)
     */
    public final @NotNull Rule repeatMax(
            final @NotNull Rule rule, final int max) {
        return repeat(rule, 0, max);
    }

    /**
     * Creates a {@link ZeroOrMoreRule} or an {@link EpsilonTerm} if the input is epsilon.
     *
     * @param rule The rule to match repeatedly.
     * @return A rule.
     */
    public final @NotNull Rule zeroOrMore(final @NotNull Rule rule) {
        if (rule instanceof EpsilonTerm)
            return rule;
        return repeat(rule, 0, Integer.MAX_VALUE);
        //return new ZeroOrMoreRule(rule);
    }

    /**
     * Creates a {@link OnceOrMoreRule} or an {@link EpsilonTerm} if the input is epsilon.
     *
     * @param rule The rule to match repeatedly.
     * @return A rule.
     */
    public final @NotNull Rule onceOrMore(final @NotNull Rule rule) {
        if (rule instanceof EpsilonTerm)
            return rule;
        //return repeat(rule, 1, Integer.MAX_VALUE);
        return new OnceOrMoreRule(rule);
    }

    /**
     * Creates a {@link OptionalRule} or an {@link EpsilonTerm} if the input is epsilon.
     *
     * @param rule The rule to maybe match.
     * @return A rule.
     */
    public final @NotNull Rule optional(final @NotNull Rule rule) {
        if (rule instanceof EpsilonTerm)
            return rule;
        return new OptionalRule(rule);
    }

    private void compress() {
        for (final @NotNull var symRuleEntry : productions.entrySet()) {
            final @NotNull var value = symRuleEntry.getValue();
            final @NotNull var compressedRule = compressRule(value);
            if (compressedRule != value) // Yes, I need a reference equality check here.
                symRuleEntry.setValue(compressedRule);
        }
    }

    private @NotNull Rule compressRule(final @NotNull Rule rule) {
        return switch (rule) {
            case VariableRepetitionRule variableRepetitionRule -> {
                final var min = variableRepetitionRule.getMin();
                final var max = variableRepetitionRule.getMax();
                final @NotNull var parser = compressRule(variableRepetitionRule.getParser());

                final RuleWithChild newRule;
                if (min == 1 && max == Integer.MAX_VALUE) {
                    newRule = new OnceOrMoreRule(parser);
                } else if (min == 0 && max == 1) {
                    newRule = new OptionalRule(parser);
                } else if (min == 0 && max == Integer.MAX_VALUE) {
                    newRule = new ZeroOrMoreRule(parser);
                } else if (min > 1) {
                    newRule = variableRepetitionRule;
                } else {
                    newRule = variableRepetitionRule;
                }
                yield newRule
                        .withHideTag(variableRepetitionRule.isHidden())
                        .withReduction(variableRepetitionRule.getReduction());
            }
            case RuleWithManyChildren ruleWithManyChildren -> ruleWithManyChildren
                    .withParsers(ruleWithManyChildren
                            .getParsers()
                            .stream()
                            .map(this::compressRule)
                            .toList());
            case RuleWithChild ruleWithChild -> ruleWithChild
                    .withParser(compressRule(ruleWithChild.getParser()));
            case NonTerminal nonTerminal -> nonTerminal;
            case Terminal terminal -> terminal;
            case SpecialSequenceRule specialSequenceRule -> specialSequenceRule;
        };
    }

    private void applyStandardReductions() {
        for (final @NotNull var prod : productions.entrySet()) {
            final @NotNull var key = prod.getKey();
            @NotNull var value = prod.getValue();
            if (value.getReduction().getReductionType() == ReductionType.ReductionTypesAvailable.INITIAL) {
                value = value.withReduction(ReductionType.defaultNonRawReduction(key));
            }
            prod.setValue(value);
        }
    }

    private @NotNull Rule autoWhitespaceHelper(
            final @NotNull Rule parser,
            final @NotNull Rule wsParser) {
        return switch (parser) {
            case NonTerminal ignored -> parser;
            case EpsilonTerm ignored2 -> parser;
            case RuleWithChild parser1 -> (parser1.withParser(autoWhitespaceHelper(
                    parser1.getParser(), wsParser)));
            case RuleWithManyChildren combWithParsers -> {
                final @NotNull List<@NotNull Rule> parsers = combWithParsers
                        .getParsers()
                        .stream()
                        .map(p -> autoWhitespaceHelper(p, wsParser))
                        .toList();
                yield (combWithParsers.withParsers(parsers));
            }
            case Terminal ignored -> {
                final @NotNull List<Rule> parsers = new ArrayList<>();
                parsers.add(wsParser);
                final @NotNull Rule result;
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
            case SpecialSequenceRule specialSequenceRule -> specialSequenceRule;
        };
    }

    private void autoWhitespace(final @NotNull Sym start,
                                final @NotNull Grammar grammarWS,
                                final @NotNull Sym startWS) {
        final @NotNull Rule wsParser =
                optional(nt(startWS)).enableHideTag();

        final @NotNull LinkedHashMap<@NotNull Sym, @NotNull Rule> finalGrammar =
                new LinkedHashMap<>(productions);
        for (final @NotNull var symRuleEntry : finalGrammar.sequencedEntrySet()) {
            symRuleEntry.setValue(autoWhitespaceHelper(
                    symRuleEntry.getValue(), wsParser));
        }

        final @NotNull Rule startWithoutReduction = (
                finalGrammar.get(start)
                        .withReduction(ReductionType.standardInitialReduction()));
        final @NotNull Rule newStartComb =
                concat(List.of(startWithoutReduction, wsParser))
                        .withReduction(finalGrammar.get(start).getReduction());

        finalGrammar.put(start, newStartComb);
        finalGrammar.putAll(grammarWS);
        finalGrammar.put(startWS,
                Objects.requireNonNull(grammarWS.getProduction(startWS)).hideTag());

        productions = finalGrammar;
    }

    protected <T extends Rule> T buffer(T rule) {
        //noinspection unchecked
        return (T) switch (rule) {
            case StringTerm stringTerm -> buffer.getOrAdd(stringTerm);
            case RegexTerm regexTerm -> buffer.getOrAdd(regexTerm);
            case NonTerminal nonTerminal -> buffer.getOrAdd(nonTerminal);
            default -> rule;
        };
    }
}
