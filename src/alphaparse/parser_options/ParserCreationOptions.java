package alphaparse.parser_options;

import alphaparse.Alpha;
import alphaparse.Sym;
import alphaparse.parser.Parser;
import alphaparse.grammar.ProductionRedefinitionOption;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * This class provides options for creating {@link Parser} instances.
 *
 * @param whitespaceParser      A parser which is used to ignore whitespaces between words or characters. This parser is merged into the new parser when the creation options are used.
 * @param startProduction       The starting production name of the parser.
 * @param stringCaseInsensitive Set to make all string terminals case-insensitive or case-sensitive.
 * @param useParserBuffering    Set to true if buffering should be used when creating the parser. This only makes sense if a productions right side is repeated often. For very large grammars, use {@code true}. Otherwise, {@code false} should be generally preferred. Whether buffering is used has only insignificant performance impact.
 * @param productionRedefinitionOption    Sets what to do when a production appears twice in the definition.
 * @param usableRules           A Set of rules that can be used when building the parser. See {@link RulesAvailable}.
 */
public record ParserCreationOptions(@Nullable Parser whitespaceParser,
                                    @Nullable Sym startProduction,
                                    @NotNull GlobalCaseInsensitivity stringCaseInsensitive,
                                    boolean useParserBuffering,
                                    @Nullable ProductionRedefinitionOption productionRedefinitionOption,
                                    @NotNull Set<RulesAvailable> usableRules) {
    private static final boolean defaultUseParserBuffering = true;
    private static ParserCreationOptions DEFAULT;

    /**
     * The default settings.
     *
     * @return default settings.
     */
    public static @NotNull ParserCreationOptions getDefault() {
        if (DEFAULT == null) {
            DEFAULT = new ParserCreationOptions(
                    null, null, null,
                    defaultUseParserBuffering, null, null);
        }
        return DEFAULT;
    }

    /**
     * Constructor.
     *
     * @param whitespaceParser      A parser which is used to ignore whitespaces between words or characters. This parser is merged into the new parser when the creation options are used.
     * @param startProduction       The starting production name of the parser.
     * @param stringCaseInsensitive Set to make all string terminals case-insensitive or case-sensitive. If null, {@link GlobalCaseInsensitivity#DEFAULT} i.
     * @param useParserBuffering    Set to true if buffering should be used when creating the parser. This only makes sense if a productions right side is repeated often. For very large grammars, use {@code true}. Otherwise, {@code false} should be generally preferred. Whether buffering is used has only insignificant performance impact.
     * @param productionRedefinitionOption    Sets what to do when a production appears twice in the definition.
     * @param usableRules           A Set of rules that can be used when building the parser. See {@link RulesAvailable}.
     */
    public ParserCreationOptions(final @Nullable Parser whitespaceParser,
                                 final @Nullable Sym startProduction,
                                 final @Nullable GlobalCaseInsensitivity stringCaseInsensitive,
                                 final boolean useParserBuffering,
                                 final @Nullable ProductionRedefinitionOption productionRedefinitionOption,
                                 final @Nullable Set<RulesAvailable> usableRules) {
        this.whitespaceParser = whitespaceParser;
        this.startProduction = startProduction;
        this.stringCaseInsensitive = stringCaseInsensitive == null
                ? GlobalCaseInsensitivity.DEFAULT
                : stringCaseInsensitive;
        this.useParserBuffering = useParserBuffering;
        this.productionRedefinitionOption = productionRedefinitionOption;
        this.usableRules = usableRules == null
                ? RulesAvailable.defaultRules()
                : usableRules;
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
                defaultUseParserBuffering, productionRedefinitionOption, usableRules);
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
                defaultUseParserBuffering, productionRedefinitionOption, usableRules);
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
                defaultUseParserBuffering, productionRedefinitionOption, usableRules);
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
     * @param productionRedefinitionOption Sets what to do when a production appears twice in the definition.
     * @return A new instance.
     */
    public @NotNull ParserCreationOptions withRedefinitionOption(
            final ProductionRedefinitionOption productionRedefinitionOption) {
        return new ParserCreationOptions(
                whitespaceParser, startProduction, stringCaseInsensitive,
                defaultUseParserBuffering, productionRedefinitionOption, usableRules);
    }

    /**
     * Creates a new instance using the most common whitespace parser.
     * <pre>
     * {@code
     *
     *   // With whitespace parser:
     *   var p = Alpha.parser("S : ('a' | 'b')*");
     *   println(p.parse("a b      a\tb\na")); // Error
     *
     *   // With whitespace parser:
     *   var p = Alpha.parser("S : ('a' | 'b')*", Alpha.ParserCreationOptions.newWithStandardWhitespace());
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
     * ABNF settings: Strings are case-insensitive, redefinition of productions with {@code =/} creates a choice rule. For available rules, see {@link RulesAvailable#abnf()}.
     * @return Options for ABNF parsers.
     */
    public static @NotNull ParserCreationOptions ABNF() {
        return new ParserCreationOptions(
                null, null, GlobalCaseInsensitivity.TRUE, defaultUseParserBuffering, ProductionRedefinitionOption.CHOICE, RulesAvailable.abnf()
        );
    }


    /**
     * EBNF settings: Strings are case-sensitive. For available rules, see {@link RulesAvailable#ebnf()}.
     * @return Options for EBNF parsers.
     */
    public static @NotNull ParserCreationOptions EBNF() {
        return new ParserCreationOptions(
                null, null, GlobalCaseInsensitivity.FALSE, defaultUseParserBuffering, ProductionRedefinitionOption.defaultOption, RulesAvailable.ebnf()
        );
    }
}
