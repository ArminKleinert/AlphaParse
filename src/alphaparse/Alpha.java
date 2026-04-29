package alphaparse;

import alphaparse.parser.CombinatorFactory;
import alphaparse.parser.Grammar;
import alphaparse.parser.Parser;
import alphaparse.parser.Gll;
import alphaparse.reduction.ReductionType;
import alphaparse.result.AlphaParseFailure;
import alphaparse.result.AlphaParseResult;
import alphaparse.result.AlphaParsesResult;
import alphaparse.result.ParseTree;
import alphaparse.result.ParseFailureNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

/**
 * Helpers for creating and using parsers.
 */
public final class Alpha {
    private Alpha() {
    }

    private static @NotNull Parser unhideParser(final @NotNull Parser parser,
                                                final @NotNull Alpha.UnhideOptions unhide) {
        final @NotNull CombinatorFactory combinatorFactory = new CombinatorFactory(true);

        return switch (unhide) {
            case none -> parser;
            case content -> parser.withGrammar(
                    combinatorFactory.unhideAllContent(parser.grammar()));
            case tags -> parser.withGrammar(
                    combinatorFactory.unhideTags(parser.outputFormat(), parser.grammar()));
            case all -> parser.withGrammar(
                    combinatorFactory.unhideAll(parser.outputFormat(), parser.grammar()));
        };
    }

    private static @NotNull Keyword getStartProductionFromParserOrOptionsAndCheck(
            final @NotNull ParsingOptions options,
            final @NotNull Parser parser) {
        final var startProduction = options.getStart();
        if (startProduction == null)
            return parser.startProduction();
        if (!parser.grammar().containsKey(startProduction))
            throw new IllegalArgumentException("Start production not in grammar: " + startProduction);
        return startProduction;
    }

    /**
     * Runs a parser on a text. If the parse is successful, returns a {@link ParseTree}. If the parse fails, returns a {@link AlphaParseFailure}
     * <p>
     * The options apply as follows:
     * <ul>
     *     <li>{@link ParsingOptions#isOptimizeMemory()}: Try to use a different algorithm for parsing.</li>
     *     <li>{@link ParsingOptions#isTotal()}: Return a {@link ParseTree} on failure, with the information included in the tree.</li>
     *     <li>{@link ParsingOptions#getUnhide()}: Unhide some parts of the parser in the output.</li>
     *     <li>{@link ParsingOptions#getStart()}: Explicitly changes the start production.</li>
     * </ul>
     *
     * @param parser  The parser.
     * @param text    The text.
     * @param options Options.
     * @return {@link ParseTree} if successful, {@link AlphaParseFailure} if not.
     */
    public static @NotNull AlphaParseResult parse(final @NotNull Parser parser,
                                                  final @NotNull String text,
                                                  final @NotNull ParsingOptions options) {
        final @NotNull var startProduction =
                getStartProductionFromParserOrOptionsAndCheck(options, parser);
        //var useOptimization = options.getOrDefault(Keyword.intern("optimize"), false);
        final @NotNull var doUnhide = options.getUnhide();
        final @NotNull var unhiddenParser = unhideParser(parser, doUnhide);

        final @NotNull AlphaParseResult parsingResult;
        if (options.isTotal()) {
            parsingResult = AlphaParseResult.make(
                    Gll.parseTotal(unhiddenParser.grammar(), startProduction, text, false));
        } else if (options.isOptimizeMemory()) {
            @NotNull var result = Repeat.tryRepeatingParseStrategy(parser, text, startProduction);
            if (result instanceof AlphaParseFailure)
                result = Gll.parse(parser.grammar(), startProduction, text, false);
            parsingResult = AlphaParseResult.make(result);
        } else {
            parsingResult = AlphaParseResult.make(
                    Gll.parse(unhiddenParser.grammar(), startProduction, text, false));
        }

        return parsingResult;
    }

    /**
     * Runs the parse algorithm with default options.
     *
     * @param parser The parser.
     * @param text   The text.
     * @return The resulting tree or failure.
     * @see #parse(Parser, String, ParsingOptions)
     * @see ParsingOptions#getDefault()
     */
    public static @NotNull AlphaParseResult parse(final @NotNull Parser parser,
                                                  final @NotNull String text) {
        return parse(parser, text, ParsingOptions.getDefault());
    }

    /**
     * Runs a parser on a string and returns a parse forest as a {@link AlphaParsesResult.LazyResultList}.
     * <p>
     * The following options apply:
     * <ul>
     *     <li>{@link ParsingOptions#usePartial()}: Include partial parses.</li>
     *     <li>{@link ParsingOptions#isTotal()}: Include failure information in parse trees.</li>
     *     <li>{@link ParsingOptions#getUnhide()}: Unhide some parts of the parser in the output.</li>
     *     <li>{@link ParsingOptions#getStart()}: Explicitly changes the start production.</li>
     * </ul>
     *
     * @param parser  The parser.
     * @param text    The text.
     * @param options The options.
     * @return A (potentially empty) parse forest. ({@link AlphaParsesResult.LazyResultList})
     */
    public static @NotNull AlphaParsesResult parses(final @NotNull Parser parser,
                                                    final @NotNull String text,
                                                    final @NotNull ParsingOptions options) {
        final @NotNull var startProduction =
                getStartProductionFromParserOrOptionsAndCheck(options, parser);
        final var usePartial = options.usePartial();
        final @NotNull var doUnhide = options.getUnhide();
        final @NotNull var unhiddenParser = unhideParser(parser, doUnhide);

        final var useParseTotal = options.isTotal();
        if (useParseTotal) {
            return Gll.parsesTotal(unhiddenParser.grammar(), startProduction, text, usePartial);
        } else {
            return Gll.parses(unhiddenParser.grammar(), startProduction, text, usePartial);
        }
    }

    /**
     * Runs the parses algorithm with default options.
     *
     * @param parser The parser.
     * @param text   The text.
     * @return The resulting trees.
     * @see #parses(Parser, String, ParsingOptions)
     * @see ParsingOptions#getDefault()
     */
    public static @NotNull AlphaParsesResult parses(final @NotNull Parser parser,
                                                    final @NotNull String text) {
        return parses(parser, text, ParsingOptions.getDefault());
    }

    /**
     * Runs a parser on a string and returns a parse forest as an {@link AlphaParsesResult.LazyResultList}.
     * If no parse is successful, returns a {@link AlphaParsesResult.ParsesFailureResult} instead.
     * <p>
     * The following options apply:
     * <ul>
     *     <li>{@link ParsingOptions#usePartial()}: Include partial parses.</li>
     *     <li>{@link ParsingOptions#isTotal()}: Include failure information in parse trees.</li>
     *     <li>{@link ParsingOptions#getUnhide()}: Unhide some parts of the parser in the output.</li>
     *     <li>{@link ParsingOptions#getStart()}: Explicitly changes the start production.</li>
     * </ul>
     *
     * @param parser  The parser.
     * @param text    The text.
     * @param options The options.
     * @return A {@link AlphaParsesResult.LazyResultList} (parse forest) if successful, {@link AlphaParsesResult.ParsesFailureResult} if not.
     */
    public static @NotNull AlphaParsesResult parsesOrFailure(final @NotNull Parser parser,
                                                             final @NotNull String text,
                                                             final @NotNull ParsingOptions options) {
        final @NotNull var startProduction =
                getStartProductionFromParserOrOptionsAndCheck(options, parser);
        final var usePartial = options.usePartial();
        final @NotNull var doUnhide = options.getUnhide();
        final @NotNull var unhiddenParser = unhideParser(parser, doUnhide);

        final var useParseTotal = options.isTotal();
        if (useParseTotal) {
            return Gll.parsesTotal(unhiddenParser.grammar(), startProduction, text, usePartial);
        } else {
            return Gll.parsesOrFailure(unhiddenParser.grammar(), startProduction, text, usePartial);
        }
    }

    /**
     * Creates a parser from a grammar specification, using the default creation options.
     *
     * @param grammar The grammar as a string.
     * @return The parser.
     * @see #parser(String, ParserCreationOptions)
     * @see ParserCreationOptions#getDefault()
     */
    public static @NotNull Parser parser(final @NotNull String grammar) {
        return parser(grammar, ParserCreationOptions.getDefault());
    }

    /**
     * Creates a parser from a grammar specification, using the default creation options.
     *
     * @param grammar The grammar as a file.
     * @return The parser.
     * @throws IOException If the file doesn't exist or can't be accessed.
     * @see #parser(File, ParserCreationOptions)
     * @see ParserCreationOptions#getDefault()
     */
    public static @NotNull Parser parser(final @NotNull File grammar) throws IOException {
        return parser(grammar, ParserCreationOptions.getDefault());
    }

    /**
     * Creates a parser from a grammar specification.
     * <p>
     * The options are as follows:
     * <ul>
     *     <li>{@link ParserCreationOptions#whitespaceParser()}: Include another parser which is intended to filter out whitespace.</li>
     *     <li>{@link ParserCreationOptions#startProduction()}: Explicitly set the starting production.</li>
     *     <li>{@link ParserCreationOptions#stringCaseInsensitive()}: Make ll string terminals ignore casing.</li>
     *     <li>{@link ParserCreationOptions#outputFormat()}: Set the output format this parser will give.</li>
     *     <li>{@link ParserCreationOptions#useParserBuffering()}: Whether to use buffering for the productions to ensure that no productions are doubled.</li>
     * </ul>
     *
     * @param grammar The grammar as a string.
     * @param options The options.
     * @return The parser.
     */
    public static @NotNull Parser parser(final @NotNull String grammar,
                                         final @NotNull Alpha.ParserCreationOptions options) {
        return Cfg.buildParser(grammar, options);
    }

    /**
     * Creates a parser from a grammar specification. To see how options apply, see {@link #parser(String, ParserCreationOptions)}.
     *
     * @param grammar The grammar as a file.
     * @param options The options
     * @return The parser.
     * @throws IOException If the file doesn't exist or can't be accessed.
     */
    public static @NotNull Parser parser(final @NotNull File grammar,
                                         final @NotNull Alpha.ParserCreationOptions options) throws IOException {
        final @NotNull String contents = Files.readString(grammar.toPath());
        return parser(contents, options);
    }

    /**
     * Creates a parser from a grammar. See {@link #parser(String, ParserCreationOptions)} for what the options do.
     * Unlike the other creation methods, the options are mandatory, specifically {@link ParserCreationOptions#startProduction()}, which must not be null.
     *
     * @param grammar The grammar.
     * @param options The options, most importantly the start production.
     * @return The parser.
     * @throws IllegalArgumentException If the start production is invalid.
     */
    public static @NotNull Parser parser(final @NotNull Grammar grammar,
                                         final @NotNull Alpha.ParserCreationOptions options) {
        if (options.startProduction() == null)
            throw new IllegalArgumentException("Start production must be specified when creating a parser from a Grammar object.");

        if (!grammar.containsKey(options.startProduction()))
            throw new IllegalArgumentException("The start production " + options.startProduction() + " is not in the grammar.");

        @NotNull var parser = Cfg.buildParserFromCombinators(grammar, options);
        if (options.whitespaceParser() != null) {
            parser = parser.withWhitespaceParser(options.whitespaceParser());
        }
        return parser;
    }

    /**
     * Options for unhiding parts of the output from a parse. A thorough description can be found in the description of the {@link ParsingOptions} class.
     *
     * @see ParsingOptions#getUnhide()
     */
    public enum UnhideOptions {
        /**
         * Do nothing.
         *
         * @see ParsingOptions#getUnhide()
         */
        none,
        /**
         * Unhide tags, do not show hidden contents.
         *
         * @see ParsingOptions#getUnhide()
         */
        tags,
        /**
         * Unhide contents, but keep tags hidden.
         *
         * @see ParsingOptions#getUnhide()
         */
        content,
        /**
         * Show both contents and tags.
         *
         * @see ParsingOptions#getUnhide()
         */
        all
    }

    /**
     * Options for parsing with an already created parser.
     */
    public static class ParsingOptions {
        /**
         * Default for the start production name of a parse operation. ({@code null})
         */
        public static final @Nullable Keyword DEFAULT_START = null;
        /**
         * Default for the start production name of a parse operation. ({@code false})
         */
        public static final boolean DEFAULT_PARTIAL = false;
        /**
         * By default, leave the parse trees as intended (hidden parts stay hidden). The value is {@link UnhideOptions#none}
         */
        public static final @NotNull Alpha.UnhideOptions DEFAULT_UNHIDE = UnhideOptions.none;
        /**
         * By default, do not include failure nodes in parse trees. ({@code false})
         */
        public static final boolean DEFAULT_TOTAL = false;
        /**
         * By default, use the normal parse algorithm, not the more memory-efficient one. ({@code false})
         */
        public static final boolean DEFAULT_OPTIMIZE_MEMORY = false;

        private final @Nullable Keyword start;
        private final boolean partial;
        private final @NotNull Alpha.UnhideOptions unhide;
        private final boolean total;
        private final boolean optimizeMemory;

        /**
         * Calls {@link ParsingOptions#ParsingOptions(Keyword, boolean, UnhideOptions, boolean, boolean)} with the static defaults.
         *
         * @return An instance of this class, using all the static DEFAULT_* values.
         * @see ParsingOptions#DEFAULT_START
         * @see ParsingOptions#DEFAULT_PARTIAL
         * @see ParsingOptions#DEFAULT_UNHIDE
         * @see ParsingOptions#DEFAULT_TOTAL
         * @see ParsingOptions#DEFAULT_OPTIMIZE_MEMORY
         */
        public static @NotNull ParsingOptions getDefault() {
            return new ParsingOptions(DEFAULT_START, DEFAULT_PARTIAL, DEFAULT_UNHIDE, DEFAULT_TOTAL, DEFAULT_OPTIMIZE_MEMORY);
        }

        /**
         * Creates a new instance.
         *
         * @param start          Start production for the parse. Use {@code null} to use the Parser's start production.
         * @param partial        Whether to return partial (incomplete) parses.
         * @param unhide         What (if anything) to "unhide" in the results.
         * @param total          Whether to return parse trees containing failure nodes or just return the failure itself.
         * @param optimizeMemory Whether to attempt using more memory-efficient algorithms for parsing.
         * @see ParsingOptions#DEFAULT_START
         * @see ParsingOptions#DEFAULT_PARTIAL
         * @see ParsingOptions#DEFAULT_UNHIDE
         * @see ParsingOptions#DEFAULT_TOTAL
         * @see ParsingOptions#DEFAULT_OPTIMIZE_MEMORY
         */
        public ParsingOptions(final @Nullable Keyword start,
                              final boolean partial,
                              final @NotNull Alpha.UnhideOptions unhide,
                              final boolean total,
                              final boolean optimizeMemory) {
            this.start = start;
            this.partial = partial;
            this.unhide = unhide;
            this.total = total;
            this.optimizeMemory = optimizeMemory;
        }

        /**
         * An instance of this class with the {@link ParsingOptions#optimizeMemory} set to true.
         *
         * @return An instance of this class with the {@link ParsingOptions#optimizeMemory} set to true.
         */
        public static @NotNull ParsingOptions optMemory() {
            return new ParsingOptions(DEFAULT_START, DEFAULT_PARTIAL, DEFAULT_UNHIDE, DEFAULT_TOTAL, true);
        }

        /**
         * When a parser is created, it carries its own start production. With this option, the start production can be forcefully changed for a parse. If the parser's start should be used, this method returns {@code null}.
         * <p>
         * Example:
         * <pre>
         * {@code
         *      // The grammar has these two, unrelated productions. The first production is A. So that is the production the parser uses.
         *      //    A = 'a'
         *      //    B = 'b'
         *      var p = Alpha.parser("A = 'a'\nB = 'b'");
         *
         *      println(p.parse("a")); // Success: [:A, a]
         *      println(p.parse("b")); // Failure: 'b' could not be parsed from production A.
         *
         *      // With the start production explicitly changed, it works:
         *      println(p.parse("b", Alpha.ParsingOptions.getDefault().withStart(Keyword.intern("B")))); // [:B, b]
         * }
         * </pre>
         * This can be useful if the grammar has multiple unrelated parts.
         *
         * @return A keyword.
         */
        public @Nullable Keyword getStart() {
            return start;
        }

        /**
         * When generating a parse tree, the parser tries to parse the full string first. With this option, partial parses can be made available.
         * <br>
         * The option only applies when requesting the full parse forest (e.g. {@link Alpha#parses(Parser, String, ParsingOptions)} and {@link Alpha#parsesOrFailure(Parser, String, ParsingOptions)}), but not when only a single parse is requested (e.g. {@link Alpha#parse(Parser, String, ParsingOptions)}).
         * <pre>
         * {@code
         *      var p = Alpha.parser("S = 'a'+");
         *      println(p.parses("aa")); // [[:S, a, a]]
         *
         *      var opts = Alpha.ParsingOptions.getDefault().withPartialSetTo(true);
         *      println(p.parses("aa", opts)); // [[:S, a], [:S, a, a]]
         *
         *      // The option has no effect on single parse:
         *      println(p.parse("aa", opts)); // [:S, a, a]
         * }
         * </pre>
         *
         * @return true or false
         * @see ParsingOptions#DEFAULT_PARTIAL
         * @see ParsingOptions#getDefault()
         */
        public boolean usePartial() {
            return partial;
        }

        /**
         * Determine which parts of a parse result to show when they would normally be hidden by the parser.
         * <p>
         * Consider the following grammar:
         * <pre>
         * {@code
         *   S   = 'a' <B> C <D> 'e' (* Expects the string "abcde", but will hide the substrings parsed by productions B and D *)
         *   B   = 'b'
         *   <C> = 'c' (* Expects the string "c", but will "flatten" itself into the output. *)
         *   <D> = 'd' (* Same as C *)
         * }
         * </pre>
         * Explanation: Production S expects the string "abcde", but the outputs of productions B and D will be hidden. The result of production C will be shown in the output without the associated tag "C".
         * <br>
         * Now the code:
         * <pre>
         * {@code
         *   var p = Alpha.parser("S : 'a' <B> C <D> 'a'\nB : 'b'+\n<C> : 'c'\n<D> : 'd'");
         *
         *   // No options.
         *   println("Default => " + p.parse("abcda"));       // [:S, a, c, a]
         *
         *   var opts = Alpha.ParsingOptions.getDefault();
         *   println("Default => " + p.parse("abcda"));       // [:S, a, c, a]
         *
         *   opts = opts.withUnhide(Alpha.UnhideOptions.none);
         *   println("none    => " + p.parse("abcda", opts)); // [:S, a, c, a]
         *
         *   opts = opts.withUnhide(Alpha.UnhideOptions.tags);
         *   println("tags    => " + p.parse("abcda", opts)); // [:S, a, [:C, c], a]
         *
         *   opts = opts.withUnhide(Alpha.UnhideOptions.content);
         *   println("content => " + p.parse("abcda", opts)); // [:S, a, [:B, b], c, d, a]
         *
         *   opts = opts.withUnhide(Alpha.UnhideOptions.all);
         *   println("all     => " + p.parse("abcda", opts)); // [:S, a, [:B, b], [:C, c], [:D, d], a]
         * }
         * </pre>
         *
         * @return The unhide option.
         * @see ParsingOptions#DEFAULT_UNHIDE
         * @see ParsingOptions#getDefault()
         */
        public @NotNull Alpha.UnhideOptions getUnhide() {
            return unhide;
        }

        /**
         * If true, a failed parse results in a {@link ParseTree}, as a success would, but a {@link ParseFailureNode} is embedded in the tree.
         * <pre>
         * {@code
         *      var p = Alpha.parser("S = #'a'+");
         *      var text = "ab":
         *
         *      // A normal parse results in a failure.
         *      println(p.parse("ab").castToParseFailure().contentsToString());
         *      //   => [1, [{tag=:regex, expecting=a, full=false}], 1, 2, ab]
         *
         *      // With the total option, a parsetree is returned, potentially providing more information about the failure.
         *      var opts = Alpha.ParsingOptions.getDefault().withTotal(true);
         *      println(p.parse("ab", opts));
         *      // => [:S, a, [:failure, could not parse "b" at 1..2]]
         * }
         * </pre>
         *
         * @return true or false
         * @see ParsingOptions#DEFAULT_TOTAL
         * @see ParsingOptions#getDefault()
         */
        public boolean isTotal() {
            return total;
        }

        /**
         * Whether to use a more memory efficient algorithm. Attention: This option has no influence on methods that return a parse forest.
         *
         * @return true or false.
         * @see ParsingOptions#DEFAULT_OPTIMIZE_MEMORY
         * @see ParsingOptions#getDefault()
         * @see ParsingOptions#optMemory()
         */
        public boolean isOptimizeMemory() {
            return optimizeMemory;
        }

        /**
         * Sets the start production explicitly.
         *
         * @param start The new start production.
         * @return A new instance.
         * @see ParsingOptions#getStart()
         * @see ParsingOptions#DEFAULT_START
         * @see ParsingOptions#getDefault()
         */
        public @NotNull ParsingOptions withStart(final @Nullable Keyword start) {
            if (Objects.equals(this.start, start)) return this;
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }

        /**
         * Makes a new instance with the "partial" parameter set to the argument.
         *
         * @param partial The argument as a boolean.
         * @return A new instance.
         * @see #usePartial()
         */
        public @NotNull ParsingOptions withPartial(final boolean partial) {
            if (Objects.equals(this.partial, partial)) return this;
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }

        /**
         * Creates a new instance with the unhide option set to the parameter.
         *
         * @param unhide The new option.
         * @return A new instance.
         * @see #getUnhide()
         */
        public @NotNull ParsingOptions withUnhide(final @NotNull Alpha.UnhideOptions unhide) {
            if (Objects.equals(this.unhide, unhide)) return this;
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }

        /**
         * Creates a new instance with the total option set to the parameter.
         *
         * @param total The new (or old) setting.
         * @return A new instance.
         * @see #isTotal()
         */
        public @NotNull ParsingOptions withTotal(final boolean total) {
            if (Objects.equals(this.total, total)) return this;
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }

        /**
         *  Creates a new instance with the optimizeMemory option set to the parameter.
         *
         * @param optimizeMemory The new (or old) setting.
         * @return A new instance.
         * @see #isOptimizeMemory()
         */
        public @NotNull ParsingOptions withOptimizeMemory(final boolean optimizeMemory) {
            if (Objects.equals(this.optimizeMemory, optimizeMemory)) return this;
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }
    }

    /**
     * This class provides options for creating {@link Parser} instances.
     *
     * @param whitespaceParser      A parser which is used to ignore whitespaces between words or characters. This parser is merged into the new parser when the creation options are used.
     * @param startProduction       The starting production name of the parser.
     * @param stringCaseInsensitive Set to make all string terminals case-insensitive or case-sensitive.
     * @param outputFormat          The output format for successful parses. Currently, the only output for valid parses is {@link alphaparse.result.ParseTree}.
     * @param useParserBuffering    Set to true if buffering should be used when creating the parser. This only makes sense if a productions right side is repeated often. For very large grammars, use {@code true}. Otherwise, {@code false} should be generally preferred. Whether buffering is used has only insignificant performance impact.
     */
    public record ParserCreationOptions(@Nullable Parser whitespaceParser,
                                        @Nullable Keyword startProduction,
                                        @NotNull GlobalCaseInsensitivity stringCaseInsensitive,
                                        @NotNull ReductionType.ReductionTypesAvailable outputFormat,
                                        boolean useParserBuffering) {
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
                        null, null,
                        GlobalCaseInsensitivity.DEFAULT, ReductionType.ReductionTypesAvailable.defaultType,
                        defaultUseParserBuffering);
            }
            return DEFAULT;
        }

        /**
         * Constructor.
         *
         * @param whitespaceParser      A parser which is used to ignore whitespaces between words or characters. This parser is merged into the new parser when the creation options are used.
         * @param startProduction       The starting production name of the parser.
         * @param stringCaseInsensitive Set to make all string terminals case-insensitive or case-sensitive. If null, {@link GlobalCaseInsensitivity#DEFAULT} i.
         * @param outputFormat          The output format for successful parses. Currently, the only output for valid parses is {@link alphaparse.result.ParseTree}. If null, {@link ReductionType.ReductionTypesAvailable#defaultType} is used.
         * @param useParserBuffering    Set to true if buffering should be used when creating the parser. This only makes sense if a productions right side is repeated often. For very large grammars, use {@code true}. Otherwise, {@code false} should be generally preferred. Whether buffering is used has only insignificant performance impact.
         */
        public ParserCreationOptions(final @Nullable Parser whitespaceParser,
                                     final @Nullable Keyword startProduction,
                                     final @Nullable GlobalCaseInsensitivity stringCaseInsensitive,
                                     final @Nullable ReductionType.ReductionTypesAvailable outputFormat,
                                     final boolean useParserBuffering) {
            this.whitespaceParser = whitespaceParser;
            this.startProduction = startProduction;
            this.stringCaseInsensitive = stringCaseInsensitive == null
                    ? GlobalCaseInsensitivity.DEFAULT
                    : stringCaseInsensitive;
            this.outputFormat = outputFormat == null
                    ? ReductionType.ReductionTypesAvailable.defaultType
                    : outputFormat;
            this.useParserBuffering = useParserBuffering;
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
                    whitespaceParser, startProduction, stringCaseInsensitive, outputFormat,
                    defaultUseParserBuffering);
        }

        /**
         * Creates a new instance with the start production set.
         *
         * @param startProduction The start production's name.
         * @return A new instance.
         */
        public @NotNull ParserCreationOptions withStartProduction(
                final @Nullable Keyword startProduction) {
            if (Objects.equals(this.startProduction(), startProduction))
                return this;
            return new ParserCreationOptions(
                    whitespaceParser, startProduction, stringCaseInsensitive, outputFormat, defaultUseParserBuffering);
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
                    whitespaceParser, startProduction, stringCaseInsensitive, outputFormat, defaultUseParserBuffering);
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
         * Creates a new instance with output format set to the parameter.
         *
         * @param outputFormat The setting for the output format.
         * @return A new instance.
         */
        public @NotNull ParserCreationOptions withOutputFormat(
                final @Nullable ReductionType.ReductionTypesAvailable outputFormat) {
            return new ParserCreationOptions(
                    whitespaceParser, startProduction, stringCaseInsensitive, outputFormat, defaultUseParserBuffering);
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
            return new ParserCreationOptions(
                    getPredefinedWhitespaceParser(Keyword.intern("standard")),
                    null,
                    GlobalCaseInsensitivity.DEFAULT,
                    ReductionType.ReductionTypesAvailable.defaultType,
                    defaultUseParserBuffering);
        }
    }

    /**
     * Get a whitespace parser (for use in {@link ParserCreationOptions#withWhitespaceParser(Parser)}). The parser is accessed by a keyword.
     * <p>
     * The defined names are the keywords {@code :standard} (ignores spaces, tabs and newlines) and {@code :comma} (which also ignores commas).
     *
     * @param wsParserName The key.
     * @return A parser or null.
     */
    public static @Nullable Parser getPredefinedWhitespaceParser(
            final @Nullable Keyword wsParserName) {
        if (wsParserName == null) {
            return null;
        }
        if (predefinedWsParsers == null) {
            predefinedWsParsers = Map.of(
                    Keyword.intern("standard"), parser("whitespace = #'\\s+'", ParserCreationOptions.getDefault()),
                    Keyword.intern("comma"), parser("whitespace = #'[,\\s]+'", ParserCreationOptions.getDefault())
            );
        }
        return predefinedWsParsers.get(wsParserName);
    }

    private static @Nullable Map<Keyword, Parser> predefinedWsParsers = null;
}
