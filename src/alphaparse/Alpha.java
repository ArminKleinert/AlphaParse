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
     *
     * The options apply as follows:
     *
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
                getStartProductionFromParserOrOptionsAndCheck(options,parser);
        final var usePartial = options.usePartial();
        //var useOptimization = options.getOrDefault(Keyword.intern("optimize"), false);
        final @NotNull var doUnhide = options.getUnhide();
        final @NotNull var unhiddenParser = unhideParser(parser, doUnhide);

        final @NotNull AlphaParseResult parsingResult;
        if (options.isTotal()) {
            parsingResult = AlphaParseResult.make(
                    Gll.parseTotal(unhiddenParser.grammar(), startProduction, text, usePartial));
        } else if (options.isOptimizeMemory() && !usePartial) {
            @NotNull var result = Repeat.tryRepeatingParseStrategy(parser, text, startProduction);
            if (result instanceof AlphaParseFailure)
                result = Gll.parse(parser.grammar(), startProduction, text, false);
            parsingResult = AlphaParseResult.make(result);
        } else {
            parsingResult = AlphaParseResult.make(
                    Gll.parse(unhiddenParser.grammar(), startProduction, text, usePartial));
        }

        return parsingResult;
    }

    /**
     * TODO
     *
     * @param parser TODO
     * @param text   TODO
     * @return TODO
     */
    public static @NotNull AlphaParseResult parse(final @NotNull Parser parser,
                                                  final @NotNull String text) {
        return parse(parser, text, ParsingOptions.getDefault());
    }

    /**
     * TODO
     *
     * @param parser  TODO
     * @param text    TODO
     * @param options TODO
     * @return TODO
     */
    public static @NotNull AlphaParsesResult parses(final @NotNull Parser parser,
                                                    final @NotNull String text,
                                                    final @NotNull ParsingOptions options) {
        final @NotNull var startProduction =
                getStartProductionFromParserOrOptionsAndCheck(options,parser);
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
     * TODO
     *
     * @param parser TODO
     * @param text   TODO
     * @return TODO
     */
    public static @NotNull AlphaParsesResult parses(final @NotNull Parser parser,
                                                    final @NotNull String text) {
        return parses(parser, text, ParsingOptions.getDefault());
    }

    /**
     * TODO
     *
     * @param parser  TODO
     * @param text    TODO
     * @param options TODO
     * @return TODO
     */
    public static @NotNull AlphaParsesResult parsesOrFailure(final @NotNull Parser parser,
                                                             final @NotNull String text,
                                                             final @NotNull ParsingOptions options) {
        final @NotNull var startProduction =
                getStartProductionFromParserOrOptionsAndCheck(options,parser);
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
     * TODO
     *
     * @param grammar TODO
     * @return TODO
     */
    public static @NotNull Parser parser(final @NotNull String grammar) {
        return parser(grammar, ParserCreationOptions.getDefault());
    }

    /**
     * TODO
     *
     * @param grammar TODO
     * @return TODO
     * @throws IOException TODO
     */
    public static @NotNull Parser parser(final @NotNull File grammar) throws IOException {
        return parser(grammar, ParserCreationOptions.getDefault());
    }

    /**
     * TODO
     *
     * @param grammar TODO
     * @param options TODO
     * @return TODO
     */
    public static @NotNull Parser parser(final @NotNull String grammar,
                                         final @NotNull Alpha.ParserCreationOptions options) {
        return Cfg.buildParser(grammar, options);
    }

    /**
     * TODO
     *
     * @param grammar TODO
     * @param options TODO
     * @return TODO
     * @throws IOException TODO
     */
    public static @NotNull Parser parser(final @NotNull File grammar,
                                         final @NotNull Alpha.ParserCreationOptions options) throws IOException {
        final @NotNull String contents = Files.readString(grammar.toPath());
        return parser(contents, options);
    }

    /**
     * TODO
     *
     * @param grammar TODO
     * @param options TODO
     * @return TODO
     * @throws IOException TODO
     */
    public static @NotNull Parser parser(final @NotNull Grammar grammar,
                                         final @NotNull Alpha.ParserCreationOptions options) throws IOException {
        if (options.startProduction() == null)
            throw new IllegalArgumentException();

        @NotNull Parser parser = Cfg.buildParserFromCombinators(grammar, options);
        if (options.whitespaceParser() != null) {
            parser = parser.withWhitespaceParser(options.whitespaceParser());
        }
        return parser;
    }

    /**
     *  TODO
     */
    public enum UnhideOptions {
        /**
         * TODO
         */
        content,
        /**
         * TODO
         */
        tags,
        /**
         * TODO
         */
        all,
        /**
         * TODO
         */
        none
    }

    /**
     * TODO
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
         *
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
         *
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
         *      println(p.parse("b", Alpha.ParsingOptions.getDefault().withStartingProdSetTo(Keyword.intern("B")))); // [:B, b]
         * }
         * </pre>
         * This can be useful if the grammar has multiple unrelated parts.
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
         * TODO
         *
         * @return TODO
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
         *      var opts = Alpha.ParsingOptions.getDefault().withTotalParseSetTo(true);
         *      println(p.parse("ab", opts));
         *      // => [:S, a, [:failure, could not parse "b" at 1..2]]
         * }
         * </pre>
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
        public @NotNull ParsingOptions withStartingProdSetTo(final @Nullable Keyword start) {
            if (Objects.equals(this.start, start)) return this;
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }

        /**
         * TODO
         *
         * @param partial TODO
         * @return TODO
         */
        public @NotNull ParsingOptions withPartialSetTo(final boolean partial) {
            if (Objects.equals(this.partial, partial)) return this;
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }

        /**
         * TODO
         *
         * @param unhide TODO
         * @return TODO
         */
        public @NotNull ParsingOptions withUnhideOptionsSetTo(final @NotNull Alpha.UnhideOptions unhide) {
            if (Objects.equals(this.unhide, unhide)) return this;
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }

        /**
         * TODO
         *
         * @param total TODO
         * @return TODO
         */
        public @NotNull ParsingOptions withTotalParseSetTo(final boolean total) {
            if (Objects.equals(this.total, total)) return this;
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }

        /**
         * TODO
         *
         * @param optimizeMemory TODO
         * @return TODO
         */
        public @NotNull ParsingOptions withOptMemorySetTo(final boolean optimizeMemory) {
            if (Objects.equals(this.optimizeMemory, optimizeMemory)) return this;
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }
    }

    /**
     * This class provides options for creating {@link Parser} instances.
     * @param whitespaceParser A parser which is used to ignore whitespaces between words or characters. This parser is merged into the new parser when the creation options are used.
     * @param startProduction The starting production name of the parser.
     * @param stringCaseInsensitive Set to make all string terminals case-insensitive or case-sensitive.
     * @param outputFormat The output format for successful parses. Currently, the only output for valid parses is {@link alphaparse.result.ParseTree}.
     * @param useParserBuffering Set to true if buffering should be used when creating the parser. This only makes sense if a productions right side is repeated often. For very large grammars, use {@code true}. Otherwise, {@code false} should be generally preferred. Whether buffering is used has only insignificant performance impact.
     */
    public record ParserCreationOptions(@Nullable Parser whitespaceParser,
                                        @Nullable Keyword startProduction,
                                        @NotNull GlobalCaseInsensitivity stringCaseInsensitive,
                                        @NotNull ReductionType.ReductionTypesAvailable outputFormat,
                                        boolean useParserBuffering) {
        private static final boolean defaultUseParserBuffering = false;

        private static final @NotNull ParserCreationOptions DEFAULT = new ParserCreationOptions(
                null, null,
                GlobalCaseInsensitivity.DEFAULT, ReductionType.ReductionTypesAvailable.defaultType,
                defaultUseParserBuffering);

        /**
         * TODO
         *
         * @return TODO
         */
        public static @NotNull ParserCreationOptions getDefault() {
            return DEFAULT;
        }

        /**
         * TODO
         *
         * @param whitespaceParser      A parser which is used to ignore whitespaces between words or characters. This parser is merged into the new parser when the creation options are used.
         * @param startProduction       The starting production name of the parser.
         * @param stringCaseInsensitive Set to make all string terminals case-insensitive or case-sensitive. If null, {@link GlobalCaseInsensitivity#DEFAULT} i.
         * @param outputFormat          The output format for successful parses. Currently, the only output for valid parses is {@link alphaparse.result.ParseTree}. If null, {@link ReductionType.ReductionTypesAvailable#defaultType} is used.
         * @param useParserBuffering Set to true if buffering should be used when creating the parser. This only makes sense if a productions right side is repeated often. For very large grammars, use {@code true}. Otherwise, {@code false} should be generally preferred. Whether buffering is used has only insignificant performance impact.
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
         * TODO
         *
         * @param whitespaceParser TODO
         */
        public ParserCreationOptions(final @Nullable Parser whitespaceParser) {
            this(whitespaceParser, null,
                    GlobalCaseInsensitivity.DEFAULT,
                    ReductionType.ReductionTypesAvailable.defaultType,
                    defaultUseParserBuffering);
        }

        /**
         * TODO
         *
         * @param startProduction TODO
         */
        public ParserCreationOptions(final @Nullable Keyword startProduction) {
            this(null, startProduction,
                    GlobalCaseInsensitivity.DEFAULT,
                    ReductionType.ReductionTypesAvailable.defaultType,
                    defaultUseParserBuffering);
        }

        /**
         * TODO
         *
         * @param outputFormat TODO
         */
        public ParserCreationOptions(final @Nullable ReductionType.ReductionTypesAvailable outputFormat) {
            this(null, null,
                    GlobalCaseInsensitivity.DEFAULT, outputFormat,
                    defaultUseParserBuffering);
        }

        /**
         * TODO
         *
         * @param whitespaceParser TODO
         * @return TODO
         */
        public @NotNull ParserCreationOptions withWhitespaceParser(final @Nullable Parser whitespaceParser) {
            return new ParserCreationOptions(
                    whitespaceParser, startProduction, stringCaseInsensitive, outputFormat,
                    defaultUseParserBuffering);
        }

        /**
         * TODO
         *
         * @param startProduction TODO
         * @return TODO
         */
        public @NotNull ParserCreationOptions withStartProduction(final @Nullable Keyword startProduction) {
            return new ParserCreationOptions(
                    whitespaceParser, startProduction, stringCaseInsensitive, outputFormat, defaultUseParserBuffering);
        }

        /**
         * TODO
         *
         * @param stringCaseInsensitive TODO
         * @return TODO
         */
        public @NotNull ParserCreationOptions withCaseInsensitivity(final @Nullable GlobalCaseInsensitivity stringCaseInsensitive) {
            return new ParserCreationOptions(
                    whitespaceParser, startProduction, stringCaseInsensitive, outputFormat, defaultUseParserBuffering);
        }

        /**
         * TODO
         *
         * @param stringCaseInsensitive TODO
         * @return TODO
         */
        public @NotNull ParserCreationOptions withCaseInsensitivity(final boolean stringCaseInsensitive) {
            return new ParserCreationOptions(
                    whitespaceParser, startProduction,
                    stringCaseInsensitive ? GlobalCaseInsensitivity.TRUE : GlobalCaseInsensitivity.FALSE,
                    outputFormat, defaultUseParserBuffering);
        }

        /**
         * TODO
         *
         * @param outputFormat TODO
         * @return TODO
         */
        public @NotNull ParserCreationOptions withOutputFormat(final @Nullable ReductionType.ReductionTypesAvailable outputFormat) {
            return new ParserCreationOptions(
                    whitespaceParser, startProduction, stringCaseInsensitive, outputFormat, defaultUseParserBuffering);
        }

        /**
         * TODO
         *
         * @return TODO
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
     * TODO
     *
     * @param wsParserName TODO
     * @return TODO
     */
    public static @Nullable Parser getPredefinedWhitespaceParser(final @Nullable Keyword wsParserName) {
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
