package alphaparse;

import alphaparse.parser.Grammar;
import alphaparse.parser.Parser;
import alphaparse.parser.Gll;
import alphaparse.reduction.ReductionType;
import alphaparse.result.AlphaParseFailure;
import alphaparse.result.AlphaParseResult;
import alphaparse.result.AlphaParsesResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;

/**
 * TODO
 */
public final class Alpha {
    private Alpha() {
    }

    /**
     * TODO
     *
     * @param parser TODO
     * @param unhide TODO
     * @return TODO
     */
    private static @NotNull Parser unhideParser(final @NotNull Parser parser,
                                                final @NotNull Alpha.UnhideOptions unhide) {
        final @NotNull CombinatorsSource combinatorsSource = new CombinatorsSource();

        return switch (unhide) {
            case none -> parser;
            case content -> parser.withGrammar(
                    combinatorsSource.unhideAllContent(parser.grammar()));
            case tags -> parser.withGrammar(
                    combinatorsSource.unhideTags(parser.outputFormat(), parser.grammar()));
            case all -> parser.withGrammar(
                    combinatorsSource.unhideAll(parser.outputFormat(), parser.grammar()));
        };
    }

    /**
     * TODO
     *
     * @param parser  TODO
     * @param text    TODO
     * @param options TODO
     * @return TODO
     */
    public static @NotNull AlphaParseResult parse(final @NotNull Parser parser,
                                                  final @NotNull String text,
                                                  final @NotNull ParsingOptions options) {
        final @NotNull var startProduction = options.getStartOrDefault(parser.startProduction());
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
        final @NotNull var startProduction = options.getStartOrDefault(parser.startProduction());
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
        final @NotNull var startProduction = options.getStartOrDefault(parser.startProduction());
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
         * TODO
         */
        public static final @Nullable Keyword DEFAULT_START = null;
        /**
         * TODO
         */
        public static final boolean DEFAULT_PARTIAL = false;
        /**
         * TODO
         */
        public static final @NotNull Alpha.UnhideOptions DEFAULT_UNHIDE = UnhideOptions.none;
        /**
         * TODO
         */
        public static final boolean DEFAULT_TOTAL = false;
        /**
         * TODO
         */
        public static final boolean DEFAULT_OPTIMIZE_MEMORY = false;

        private final @Nullable Keyword start;
        private final boolean partial;
        private final @NotNull Alpha.UnhideOptions unhide;
        private final boolean total;
        private final boolean optimizeMemory;

        /**
         * TODO
         *
         * @return TODO
         */
        public static @NotNull ParsingOptions getDefault() {
            return new ParsingOptions(DEFAULT_START, DEFAULT_PARTIAL, DEFAULT_UNHIDE, DEFAULT_TOTAL, DEFAULT_OPTIMIZE_MEMORY);
        }

        /**
         * TODO
         *
         * @param start          TODO
         * @param partial        TODO
         * @param unhide         TODO
         * @param total          TODO
         * @param optimizeMemory TODO
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
         * TODO
         *
         * @return TODO
         */
        public static @NotNull ParsingOptions optMemory() {
            return new ParsingOptions(DEFAULT_START, DEFAULT_PARTIAL, DEFAULT_UNHIDE, DEFAULT_TOTAL, true);
        }

        /**
         * TODO
         *
         * @param defaultStart TODO
         * @return TODO
         */
        public @NotNull Keyword getStartOrDefault(final @NotNull Keyword defaultStart) {
            return start == null ? defaultStart : start;
        }

        /**
         * TODO
         *
         * @return TODO
         */
        public boolean usePartial() {
            return partial;
        }

        /**
         * TODO
         *
         * @return TODO
         */
        public @NotNull Alpha.UnhideOptions getUnhide() {
            return unhide;
        }

        /**
         * TODO
         *
         * @return TODO
         */
        public boolean isTotal() {
            return total;
        }

        /**
         * TODO
         *
         * @return TODO
         */
        public boolean isOptimizeMemory() {
            return optimizeMemory;
        }

        /**
         * TODO
         *
         * @param start TODO
         * @return TODO
         */
        public @NotNull ParsingOptions withStartingProdSetTo(final @Nullable Keyword start) {
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }

        /**
         * TODO
         *
         * @param partial TODO
         * @return TODO
         */
        public @NotNull ParsingOptions withPartialSetTo(final boolean partial) {
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }

        /**
         * TODO
         *
         * @param unhide TODO
         * @return TODO
         */
        public @NotNull ParsingOptions withUnhideOptionsSetTo(final @NotNull Alpha.UnhideOptions unhide) {
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }

        /**
         * TODO
         *
         * @param total TODO
         * @return TODO
         */
        public @NotNull ParsingOptions withTotalParseSetTo(final boolean total) {
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }

        /**
         * TODO
         *
         * @param optimizeMemory TODO
         * @return TODO
         */
        public @NotNull ParsingOptions withOptMemorySetTo(final boolean optimizeMemory) {
            return new ParsingOptions(start, partial, unhide, total, optimizeMemory);
        }
    }

    /**
     * TODO
     *
     * @param whitespaceParser      A parser which is used to ignore whitespaces between words or characters. This parser is merged into the new parser when the creation options are used.
     * @param startProduction       The starting production name of the parser.
     * @param stringCaseInsensitive Set to make all string terminals case-insensitive or case-sensitive.
     * @param outputFormat          The output format for successful parses. Currently, the only output for valid parses is {@link alphaparse.result.ParseTree}.
     */
    public record ParserCreationOptions(@Nullable Parser whitespaceParser,
                                        @Nullable Keyword startProduction,
                                        @NotNull GlobalCaseInsensitivity stringCaseInsensitive,
                                        @NotNull ReductionType.ReductionTypesAvailable outputFormat) {
        private static final @NotNull ParserCreationOptions DEFAULT = new ParserCreationOptions(
                null, null,
                GlobalCaseInsensitivity.DEFAULT, ReductionType.ReductionTypesAvailable.defaultType);

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
         * @param whitespaceParser      TODO
         * @param startProduction       TODO
         * @param stringCaseInsensitive TODO
         * @param outputFormat          TODO
         */
        public ParserCreationOptions(final @Nullable Parser whitespaceParser,
                                     final @Nullable Keyword startProduction,
                                     final @Nullable GlobalCaseInsensitivity stringCaseInsensitive,
                                     final @Nullable ReductionType.ReductionTypesAvailable outputFormat) {
            this.whitespaceParser = whitespaceParser;
            this.startProduction = startProduction;
            this.stringCaseInsensitive = stringCaseInsensitive == null
                    ? GlobalCaseInsensitivity.DEFAULT
                    : stringCaseInsensitive;
            this.outputFormat = outputFormat == null
                    ? ReductionType.ReductionTypesAvailable.defaultType
                    : outputFormat;
        }

        /**
         * TODO
         *
         * @param whitespaceParser TODO
         */
        public ParserCreationOptions(final @Nullable Parser whitespaceParser) {
            this(whitespaceParser, null,
                    GlobalCaseInsensitivity.DEFAULT,
                    ReductionType.ReductionTypesAvailable.defaultType);
        }

        /**
         * TODO
         *
         * @param startProduction TODO
         */
        public ParserCreationOptions(final @Nullable Keyword startProduction) {
            this(null, startProduction,
                    GlobalCaseInsensitivity.DEFAULT,
                    ReductionType.ReductionTypesAvailable.defaultType);
        }

        /**
         * TODO
         *
         * @param outputFormat TODO
         */
        public ParserCreationOptions(final @Nullable ReductionType.ReductionTypesAvailable outputFormat) {
            this(null, null,
                    GlobalCaseInsensitivity.DEFAULT, outputFormat);
        }

        /**
         * TODO
         *
         * @param whitespaceParser TODO
         * @return TODO
         */
        public @NotNull ParserCreationOptions withWhitespaceParser(final @Nullable Parser whitespaceParser) {
            return new ParserCreationOptions(whitespaceParser, startProduction, stringCaseInsensitive, outputFormat);
        }

        /**
         * TODO
         *
         * @param startProduction TODO
         * @return TODO
         */
        public @NotNull ParserCreationOptions withStartProduction(final @Nullable Keyword startProduction) {
            return new ParserCreationOptions(whitespaceParser, startProduction, stringCaseInsensitive, outputFormat);
        }

        /**
         * TODO
         *
         * @param stringCaseInsensitive TODO
         * @return TODO
         */
        public @NotNull ParserCreationOptions withCaseInsensitivity(final @Nullable GlobalCaseInsensitivity stringCaseInsensitive) {
            return new ParserCreationOptions(whitespaceParser, startProduction, stringCaseInsensitive, outputFormat);
        }

        /**
         * TODO
         *
         * @param stringCaseInsensitive TODO
         * @return TODO
         */
        public @NotNull ParserCreationOptions withCaseInsensitivity(final boolean stringCaseInsensitive) {
            return new ParserCreationOptions(whitespaceParser, startProduction, stringCaseInsensitive ? GlobalCaseInsensitivity.TRUE : GlobalCaseInsensitivity.FALSE, outputFormat);
        }

        /**
         * TODO
         *
         * @param outputFormat TODO
         * @return TODO
         */
        public @NotNull ParserCreationOptions withOutputFormat(final @Nullable ReductionType.ReductionTypesAvailable outputFormat) {
            return new ParserCreationOptions(whitespaceParser, startProduction, stringCaseInsensitive, outputFormat);
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
                    ReductionType.ReductionTypesAvailable.defaultType);
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
