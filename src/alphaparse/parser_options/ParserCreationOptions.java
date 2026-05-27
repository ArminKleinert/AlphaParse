package alphaparse.parser_options;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.grammar.RedefinitionOption;
import alphaparse.parser.Parser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class provides options for creating {@link Parser} instances.
 *
 * @param whitespaceParser      A parser which is used to ignore whitespaces between words or characters. This parser is merged into the new parser when the creation options are used.
 * @param startProduction       The starting production name of the parser.
 * @param stringCaseInsensitive Set to make all string terminals case-insensitive or case-sensitive.
 * @param useParserBuffering    Set to true if buffering should be used when creating the parser. This only makes sense if a productions right side is repeated often. For very large grammars, use {@code true}. Otherwise, {@code false} should be generally preferred. Whether buffering is used has only insignificant performance impact.
 * @param redefinitionOption    Sets what to do when a production appears twice in the definition.
 * @param usableRules           A Set of rules that can be used when building the parser. See {@link RulesAvailable}.
 * @param checkCorrectness      Whether to check the correctness of the grammar when creating the parser.
 * @param ruleDefinitionOps     A collection of possible "definition operators" for rules.
 * @param epsilonNames          A collection of possible epsilon names. If null, use {@link #defaultEpsilonNames()}.
 */
public record ParserCreationOptions(@Nullable Parser whitespaceParser,
                                    @Nullable Sym startProduction,
                                    @NotNull GlobalCaseInsensitivity stringCaseInsensitive,
                                    boolean useParserBuffering,
                                    @Nullable RedefinitionOption redefinitionOption,
                                    @NotNull Set<RulesAvailable> usableRules,
                                    boolean checkCorrectness,
                                    @NotNull Collection<@NotNull String> ruleDefinitionOps,
                                    @NotNull Collection<@NotNull String> epsilonNames) {
    private static final boolean defaultUseParserBuffering = true;
    private static final boolean defaultCheckCorrectness = true;

    /**
     * Default for {@link ParserCreationOptions#ruleDefinitionOps()}.
     * Value (might not be up to date): {@code List.of(":=", "::=", "=", ":")}
     *
     * @return List of strings.
     */
    public static @NotNull @Unmodifiable List<String> defaultRuleDefinitionOps() {
        return List.of(":=", "::=", "=", ":");
    }

    /**
     * Default for {@link ParserCreationOptions#epsilonNames()}.
     * Value (might not be up to date): {@code List.of("Epsilon", "epsilon", "EPSILON", "eps", "ε")}
     *
     * @return List of strings.
     */
    public static @NotNull @Unmodifiable List<String> defaultEpsilonNames() {
        return List.of("Epsilon", "epsilon", "EPSILON", "eps", "ε");
    }

    /**
     * Constructor.
     *
     * @param whitespaceParser      A parser which is used to ignore whitespaces between words or characters. This parser is merged into the new parser when the creation options are used.
     * @param startProduction       The starting production name of the parser.
     * @param stringCaseInsensitive Set to make all string terminals case-insensitive or case-sensitive. If null, {@link GlobalCaseInsensitivity#DEFAULT} is used.
     * @param useParserBuffering    Set to true if buffering should be used when creating the parser. This only makes sense if a productions right side is repeated often. For very large grammars, use {@code true}. Otherwise, {@code false} should be generally preferred. Whether buffering is used has only insignificant performance impact.
     * @param redefinitionOption    Sets what to do when a production appears twice in the definition.
     * @param usableRules           A Set of rules that can be used when building the parser. See {@link RulesAvailable}.
     * @param checkCorrectness      Whether to check the correctness of the grammar when creating the parser.
     * @param ruleDefinitionOps     A collection of possible "definition operators" for rules. If null, use {@link #defaultRuleDefinitionOps()}. Example: {@code List.of(":=", "::=", "=", ":")}
     * @param epsilonNames          A collection of possible epsilon names. If null, use {@link #defaultEpsilonNames()}. Example: {@code List.of("epsilon", "ε")}
     */
    public ParserCreationOptions(final @Nullable Parser whitespaceParser,
                                 final @Nullable Sym startProduction,
                                 final @Nullable GlobalCaseInsensitivity stringCaseInsensitive,
                                 final boolean useParserBuffering,
                                 final @Nullable RedefinitionOption redefinitionOption,
                                 final @Nullable Set<RulesAvailable> usableRules,
                                 final boolean checkCorrectness,
                                 final @Nullable Collection<String> ruleDefinitionOps,
                                 final @Nullable Collection<String> epsilonNames) {
        this.whitespaceParser = whitespaceParser;
        this.startProduction = startProduction;
        this.stringCaseInsensitive = stringCaseInsensitive == null
                ? GlobalCaseInsensitivity.DEFAULT
                : stringCaseInsensitive;
        this.useParserBuffering = useParserBuffering;
        this.redefinitionOption = redefinitionOption;
        this.usableRules = usableRules == null
                ? RulesAvailable.defaultRules()
                : usableRules;
        this.checkCorrectness = checkCorrectness;
        this.ruleDefinitionOps = ruleDefinitionOps == null
                ? defaultRuleDefinitionOps() : ruleDefinitionOps;
        this.epsilonNames = epsilonNames == null
                ? defaultEpsilonNames()
                : epsilonNames;
    }

    /**
     * Creates a new instance with the whitespace-ignoring parser set.
     *
     * @param whitespaceParser The parser (or null).
     * @return A new instance.
     */
    public @NotNull ParserCreationOptions withWhitespaceParser(
            final @Nullable Parser whitespaceParser) {
        if (Objects.equals(this.whitespaceParser(), whitespaceParser))
            return this;
        return new ParserCreationOptions(
                whitespaceParser, startProduction, stringCaseInsensitive,
                useParserBuffering, redefinitionOption, usableRules,
                checkCorrectness, ruleDefinitionOps, epsilonNames);
    }

    /**
     * Creates a new instance with the start production set.
     *
     * @param startProduction The start production's name.
     * @return A new instance.
     */
    public @NotNull ParserCreationOptions withStartProduction(
            final @Nullable Sym startProduction) {
        if (Objects.equals(this.startProduction(), startProduction))
            return this;
        return new ParserCreationOptions(
                whitespaceParser, startProduction, stringCaseInsensitive,
                useParserBuffering, redefinitionOption, usableRules,
                checkCorrectness, ruleDefinitionOps, epsilonNames);
    }

    /**
     * Creates a new instance with string case-insensitivity set to the parameter.
     *
     * @param stringCaseInsensitive The setting for the case-insensitivity.
     * @return A new instance.
     */
    public @NotNull ParserCreationOptions withStringCaseInsensitive(
            final @Nullable GlobalCaseInsensitivity stringCaseInsensitive) {
        if (Objects.equals(this.stringCaseInsensitive(), stringCaseInsensitive))
            return this;
        return new ParserCreationOptions(
                whitespaceParser, startProduction, stringCaseInsensitive,
                useParserBuffering, redefinitionOption, usableRules,
                checkCorrectness, ruleDefinitionOps, epsilonNames);
    }

    /**
     * Creates a new instance with string case-insensitivity set to the parameter. The parameter here is a boolean.
     * {@code true} becomes {@link GlobalCaseInsensitivity#TRUE}. {@link GlobalCaseInsensitivity#FALSE}
     *
     * @param stringCaseInsensitive The setting for the case-insensitivity.
     * @return A new instance.
     */
    public @NotNull ParserCreationOptions withStringCaseInsensitive(
            final boolean stringCaseInsensitive) {
        return withStringCaseInsensitive(stringCaseInsensitive
                ? GlobalCaseInsensitivity.TRUE
                : GlobalCaseInsensitivity.FALSE);
    }

    /**
     * Sets what to do when a production appears twice in the definition.
     *
     * @param redefinitionOption Sets what to do when a production appears twice in the definition.
     * @return A new instance.
     */
    public @NotNull ParserCreationOptions withRedefinitionOption(
            final RedefinitionOption redefinitionOption) {
        return new ParserCreationOptions(
                whitespaceParser, startProduction, stringCaseInsensitive,
                useParserBuffering, redefinitionOption, usableRules,
                checkCorrectness, ruleDefinitionOps, epsilonNames);
    }

    /**
     * Creates a new instance with {@link ParserCreationOptions#usableRules()} set to the parameter.
     *
     * @param usableRules The new setting for {@link ParserCreationOptions#usableRules()}.
     * @return A new instance.
     */
    public @NotNull ParserCreationOptions withRulesAvailable(
            final @Nullable Set<RulesAvailable> usableRules) {
        return new ParserCreationOptions(
                whitespaceParser, startProduction, stringCaseInsensitive,
                useParserBuffering, redefinitionOption, usableRules,
                checkCorrectness, ruleDefinitionOps, epsilonNames);
    }

    /**
     * Creates a new instance with the parameter added to {@link ParserCreationOptions#usableRules()}.
     *
     * @param usableRule The new rule type.
     * @return A new instance.
     * @see #withRulesAvailable(Set)
     */
    public @NotNull ParserCreationOptions addAvailableRule(
            final @NotNull RulesAvailable usableRule) {
        return withRulesAvailable(
                Stream.of(Set.of(usableRule), usableRules)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toUnmodifiableSet()));
    }

    /**
     * Creates a new instance with the parameter removed from {@link ParserCreationOptions#usableRules()}.
     *
     * @param usableRule The rule type.
     * @return A new instance.
     * @see #withRulesAvailable(Set)
     */
    public @NotNull ParserCreationOptions removeAvailableRule(
            final @NotNull RulesAvailable usableRule) {
        return withRulesAvailable(
                usableRules.stream()
                        .filter(it -> !it.equals(usableRule))
                        .collect(Collectors.toUnmodifiableSet()));
    }

    /**
     * Creates a new instance with {@link ParserCreationOptions#checkCorrectness()} set to the parameter.
     *
     * @param checkCorrectness The new setting for {@link ParserCreationOptions#checkCorrectness()}.
     * @return A new instance.
     */
    public @NotNull ParserCreationOptions withCorrectnessCheck(
            final boolean checkCorrectness) {
        return new ParserCreationOptions(
                whitespaceParser, startProduction, stringCaseInsensitive,
                useParserBuffering, redefinitionOption, usableRules,
                checkCorrectness, ruleDefinitionOps, epsilonNames);
    }

    /**
     * Creates a new instance with {@link ParserCreationOptions#ruleDefinitionOps()} set to the parameter.
     *
     * @param ruleDefinitionOps The new setting for {@link ParserCreationOptions#ruleDefinitionOps()}.
     * @return A new instance.
     */
    public @NotNull ParserCreationOptions withRuleDefinitionOps(
            final @Nullable Collection<String> ruleDefinitionOps) {
        return new ParserCreationOptions(
                whitespaceParser, startProduction, stringCaseInsensitive,
                useParserBuffering, redefinitionOption, usableRules,
                checkCorrectness, ruleDefinitionOps, epsilonNames);
    }

    /**
     * Creates a new instance with {@link ParserCreationOptions#epsilonNames()} set to the parameter.
     *
     * @param epsilonNames The new setting for {@link ParserCreationOptions#epsilonNames()}.
     * @return A new instance.
     */
    public @NotNull ParserCreationOptions withEpsilonNames(
            final @Nullable Collection<String> epsilonNames) {
        return new ParserCreationOptions(
                whitespaceParser, startProduction, stringCaseInsensitive,
                useParserBuffering, redefinitionOption, usableRules,
                checkCorrectness, ruleDefinitionOps, epsilonNames);
    }

    /**
     * Creates a new instance using the most common whitespace parser.
     * <pre>
     * {@code
     *
     *   // With whitespace parser:
     *   var p = Alpha.parser("S := ('a' | 'b')*");
     *   println(p.parse("a b      a\tb\na")); // Error
     *
     *   // With whitespace parser:
     *   var p = Alpha.parser("S := ('a' | 'b')*", Alpha.ParserCreationOptions.newWithStandardWhitespace());
     *   println(p.parse("a b      a\tb\na")); // [:S, a, b, a, b, a]
     * }
     * </pre>
     *
     * @return A new instance.
     */
    public static @NotNull ParserCreationOptions newWithStandardWhitespace() {
        return ParserCreationOptions
                .getDefault()
                .withWhitespaceParser(Alpha.getPredefinedWhitespaceParser("standard"));
    }

    /**
     * The default settings. Equivalent to {@link ParserCreationOptions#ParserCreationOptions(Parser, Sym, GlobalCaseInsensitivity, boolean, RedefinitionOption, Set, boolean, Collection, Collection)} with using {@code null} or whichever defaults this class provides.
     *
     * @return default settings.
     */
    public static @NotNull ParserCreationOptions getDefault() {
        return new ParserCreationOptions(
                null, null, null,
                defaultUseParserBuffering, null, null,
                defaultCheckCorrectness, null, null);
    }

    /**
     * ABNF settings: Strings are case-insensitive, redefinition of productions with {@code =/} creates a choice rule. For available rules, see {@link RulesAvailable#abnfRules()}.
     * <p>
     * Characteristics:
     * <ul>
     *     <li>Rules available: {@link RulesAvailable#abnfRules()}</li>
     *     <li>Rule definition operators: {@code "=", "=/"}</li>
     *     <li>Case insensitivity: {@code true}</li>
     *     <li>Redefinition option: {@link RedefinitionOption#CHOICE}</li>
     *     <li>Epsilon equivalents: {@code "ε"}</li>
     * </ul>
     *
     * @return Options for ABNF parsers.
     */
    public static @NotNull ParserCreationOptions abnf() {
        return new ParserCreationOptions(
                null, null, GlobalCaseInsensitivity.TRUE,
                defaultUseParserBuffering, RedefinitionOption.CHOICE, RulesAvailable.abnfRules(),
                defaultCheckCorrectness,
                List.of("=/", "=", ":="),
                List.of("ε")
        );
    }

    /**
     * EBNF settings: Strings are case-sensitive. For available rules, see {@link RulesAvailable#ebnfRules()}.
     * <p>
     * Characteristics:
     * <ul>
     *     <li>Rules available: {@link RulesAvailable#ebnfRules()}</li>
     *     <li>Rule definition operators: {@code "=", "::=", "=", ":"}</li>
     *     <li>Case insensitivity: {@code false}</li>
     *     <li>Redefinition option: {@link RedefinitionOption#defaultOption}</li>
     *     <li>Epsilon equivalents: {@code "ε"}</li>
     * </ul>
     *
     * @return Options for EBNF parsers.
     */
    public static @NotNull ParserCreationOptions ebnf() {
        return new ParserCreationOptions(
                null, null, GlobalCaseInsensitivity.FALSE,
                defaultUseParserBuffering, RedefinitionOption.defaultOption, RulesAvailable.ebnfRules(),
                defaultCheckCorrectness,
                List.of("=", "::=", "=", ":"),
                List.of("ε")
        );
    }

    /**
     * EBNF settings without addons: Strings are case-sensitive. For available rules, see {@link RulesAvailable#pureEbnfRules()}.
     * Characteristics:
     * <ul>
     *     <li>Rules available: {@link RulesAvailable#pureEbnfRules()}</li>
     *     <li>Rule definition operators: {@code "="}</li>
     *     <li>Case insensitivity: {@code false}</li>
     *     <li>Redefinition option: {@link RedefinitionOption#defaultOption}</li>
     *     <li>Epsilon equivalents: {@code "ε"}</li>
     * </ul>
     *
     * @return Options for EBNF parsers.
     */
    public static @NotNull ParserCreationOptions pureEbnf() {
        return new ParserCreationOptions(
                null, null, GlobalCaseInsensitivity.FALSE,
                defaultUseParserBuffering, RedefinitionOption.defaultOption, RulesAvailable.pureEbnfRules(),
                defaultCheckCorrectness,
                List.of("="),
                List.of("ε")
        );
    }
}
