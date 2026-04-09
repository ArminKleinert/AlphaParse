package alphaparse;

import alphaparse.parser.Grammar;
import alphaparse.parser.Parser;
import alphaparse.reduction.ReductionType;
import alphaparse.result.AlphaParseFailure;
import alphaparse.result.AlphaParseResult;
import alphaparse.result.AlphaParsesResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;

public final class Alpha {
    private static @NotNull Parser unhideParser(final @NotNull Parser parser,
                                                final @NotNull Alpha.UnhideOptions unhide) {
        final @NotNull CombinatorsSource combinatorsSource = new CombinatorsSource();

        return switch (unhide) {
            case none -> parser;
            case content -> parser.withGrammar(combinatorsSource.unhideAllContent(parser.grammar()));
            case tags -> parser.withGrammar(combinatorsSource.unhideTags(parser.outputFormat(), parser.grammar()));
            case all -> parser.withGrammar(combinatorsSource.unhideAll(parser.outputFormat(), parser.grammar()));
        };
    }

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
            parsingResult = AlphaParseResult.make(Gll.parseTotal(unhiddenParser.grammar(), startProduction, text, usePartial));
        } else if (options.isOptimizeMemory() && !usePartial) {
            @NotNull var result = Repeat.tryRepeatingParseStrategy(parser, text, startProduction);
            if (result instanceof AlphaParseFailure)
                result = Gll.parse(parser.grammar(), startProduction, text, false);
            parsingResult = AlphaParseResult.make(result);
        } else {
            parsingResult = AlphaParseResult.make(Gll.parse(unhiddenParser.grammar(), startProduction, text, usePartial));
        }

        return parsingResult;
    }

    public static @NotNull AlphaParseResult parse(final @NotNull Parser parser,
                                                  final @NotNull String text) {
        return parse(parser, text, ParsingOptions.DEFAULT);
    }

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

    public static @NotNull AlphaParsesResult parses(final @NotNull Parser parser,
                                                    final @NotNull String text) {
        return parses(parser, text, ParsingOptions.DEFAULT);
    }

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

    public static @NotNull Parser parser(final @NotNull String grammar) {
        return parser(grammar, ParserCreationOptions.getDefault());
    }

    public static @NotNull Parser parser(final @NotNull File grammar) throws IOException {
        return parser(grammar, ParserCreationOptions.getDefault());
    }

    public static @NotNull Parser parser(final @NotNull String grammar,
                                         final @NotNull Alpha.ParserCreationOptions options) {
        return Cfg.buildParser(grammar, options);
    }

    public static @NotNull Parser parser(final @NotNull File grammar,
                                         final @NotNull Alpha.ParserCreationOptions options) throws IOException {
        final @NotNull String contents = Files.readString(grammar.toPath());
        return parser(contents, options);
    }

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

    public enum UnhideOptions {
        content, tags, all, none
    }

    public static class ParsingOptions {
        private final @Nullable Keyword start;
        private final boolean partial;
        private final @NotNull Alpha.UnhideOptions unhide;
        private final boolean total;
        private final boolean optimizeMemory;

        public static final @NotNull ParsingOptions DEFAULT = new ParsingOptions(null, false, UnhideOptions.none, false, false);

        public ParsingOptions(final @Nullable Keyword start, final boolean partial, final @NotNull Alpha.UnhideOptions unhide, final boolean total, final boolean optimizeMemory) {
            this.start = start;
            this.partial = partial;
            this.unhide = unhide;
            this.total = total;
            this.optimizeMemory = optimizeMemory;
        }

        public static @NotNull ParsingOptions optMemory() {
            return new ParsingOptions(null, false, UnhideOptions.none, false, true);
        }

        public @NotNull Keyword getStartOrDefault(final @NotNull Keyword defaultStart) {
            return start == null ? defaultStart : start;
        }

        public boolean usePartial() {
            return partial;
        }

        public @NotNull Alpha.UnhideOptions getUnhide() {
            return unhide;
        }

        public boolean isTotal() {
            return total;
        }

        public boolean isOptimizeMemory() {
            return optimizeMemory;
        }
    }

    public record ParserCreationOptions(@Nullable Parser whitespaceParser,
                                        @Nullable Keyword startProduction,
                                        @NotNull Cfg.GlobalCaseInsensitivity stringCaseInsensitive,
                                        @NotNull ReductionType.ReductionTypesAvailable outputFormat) {
        private static final @NotNull ParserCreationOptions DEFAULT =
                new ParserCreationOptions(null, null, Cfg.GlobalCaseInsensitivity.DEFAULT, ReductionType.ReductionTypesAvailable.defaultType);

        public static @NotNull ParserCreationOptions getDefault() {
            return DEFAULT;
        }

        public ParserCreationOptions(final @Nullable Parser whitespaceParser,
                                     final @Nullable Keyword startProduction,
                                     final @Nullable Cfg.GlobalCaseInsensitivity stringCaseInsensitive,
                                     final @Nullable ReductionType.ReductionTypesAvailable outputFormat) {
            this.whitespaceParser = whitespaceParser;
            this.startProduction = startProduction;
            this.stringCaseInsensitive = stringCaseInsensitive == null
                    ? Cfg.GlobalCaseInsensitivity.DEFAULT
                    : stringCaseInsensitive;
            this.outputFormat = outputFormat == null
                    ? ReductionType.ReductionTypesAvailable.defaultType
                    : outputFormat;
        }

        public ParserCreationOptions(final @Nullable Parser whitespaceParser) {
            this(whitespaceParser, null, Cfg.GlobalCaseInsensitivity.DEFAULT, ReductionType.ReductionTypesAvailable.defaultType);
        }

        public ParserCreationOptions(final @Nullable Keyword startProduction) {
            this(null, startProduction, Cfg.GlobalCaseInsensitivity.DEFAULT, ReductionType.ReductionTypesAvailable.defaultType);
        }

        public ParserCreationOptions(final @Nullable ReductionType.ReductionTypesAvailable outputFormat) {
            this(null, null, Cfg.GlobalCaseInsensitivity.DEFAULT, outputFormat);
        }

        public static @NotNull ParserCreationOptions newWithStandardWhitespace() {
            return new ParserCreationOptions(
                    getPredefinedWhitespaceParser(Keyword.intern("standard")),
                    null,
                    Cfg.GlobalCaseInsensitivity.DEFAULT,
                    ReductionType.ReductionTypesAvailable.defaultType);
        }
    }

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
