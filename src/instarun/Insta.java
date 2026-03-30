package instarun;

import instarun.parser.Grammar;
import instarun.parser.Parser;
import instarun.reduction.ReductionType;
import instarun.result.InstaFailure;
import instarun.result.InstaParseResult;
import instarun.result.InstaParsesResult;
import instarun.util.KeywordSetup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;

public final class Insta {
    private static @NotNull Parser unhideParser(final @NotNull Parser parser,
                                                final @NotNull Insta.UnhideOptions unhide) {
        final @NotNull CombinatorsSource combinatorsSource = new CombinatorsSource();
        if (unhide == UnhideOptions.none) {
            return parser;
        } else if (unhide == UnhideOptions.content) {
            return parser.withGrammar(combinatorsSource.unhideAllContent(parser.getGrammar()));
        } else if (unhide == UnhideOptions.tags) {
            return parser.withGrammar(combinatorsSource.unhideTags(parser.getGrammar()));
        } else if (unhide == UnhideOptions.all) {
            return parser.withGrammar(combinatorsSource.unhideAll(parser.getGrammar()));
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static @NotNull InstaParseResult parse(final @NotNull Parser parser,
                                                  final @NotNull String text,
                                                  final @NotNull ParsingOptions options) {
        KeywordSetup.initKeywords();
        var startProduction = options.getStartOrDefault(parser.getStartProduction());
        var usePartial = options.usePartial();
        //var useOptimization = options.getOrDefault(Keyword.intern("optimize"), false);
        var doUnhide = options.getUnhide();
        var unhiddenParser = unhideParser(parser, doUnhide);

        final @NotNull InstaParseResult parsingResult;
        if (options.isTotal()) {
            parsingResult = InstaParseResult.make(Gll.parseTotal(unhiddenParser.getGrammar(), startProduction, text, usePartial));
        } else if (options.isOptimizeMemory() && !usePartial) {
            var result = Repeat.tryRepeatingParseStrategy(parser, text, startProduction);
            if (result instanceof InstaFailure)
                result = Gll.parse(parser.getGrammar(), startProduction, text, false);
            parsingResult = InstaParseResult.make(result);
        } else {
            parsingResult = InstaParseResult.make(Gll.parse(unhiddenParser.getGrammar(), startProduction, text, usePartial));
        }

        return parsingResult;
    }

    public static @NotNull InstaParseResult parse(final @NotNull Parser parser,
                                                  final @NotNull String text) {
        return parse(parser, text, ParsingOptions.DEFAULT);
    }

    public static @NotNull InstaParsesResult parses(final @NotNull Parser parser,
                                                    final @NotNull String text,
                                                    final @NotNull ParsingOptions options) {
        KeywordSetup.initKeywords();
        var startProduction = options.getStartOrDefault(parser.getStartProduction());
        var usePartial = options.usePartial();
        var doUnhide = options.getUnhide();
        var unhiddenParser = unhideParser(parser, doUnhide);

        var useParseTotal = options.isTotal();
        if (useParseTotal) {
            return Gll.parsesTotal(unhiddenParser.getGrammar(), startProduction, text, usePartial);
        } else {
            return Gll.parses(unhiddenParser.getGrammar(), startProduction, text, usePartial);
        }
    }

    public static @NotNull InstaParsesResult parses(final @NotNull Parser parser,
                                                    final @NotNull String text) {
        return parses(parser, text, ParsingOptions.DEFAULT);
    }

    public static @NotNull InstaParsesResult parsesOrFailure(final @NotNull Parser parser,
                                                             final @NotNull String text,
                                                             final @NotNull ParsingOptions options) {
        KeywordSetup.initKeywords();
        var startProduction = options.getStartOrDefault(parser.getStartProduction());
        var usePartial = options.usePartial();
        var doUnhide = options.getUnhide();
        var unhiddenParser = unhideParser(parser, doUnhide);

        var useParseTotal = options.isTotal();
        if (useParseTotal) {
            return Gll.parsesTotal(unhiddenParser.getGrammar(), startProduction, text, usePartial);
        } else {
            return Gll.parsesOrFailure(unhiddenParser.getGrammar(), startProduction, text, usePartial);
        }
    }

    public static @NotNull Parser parser(final @NotNull String grammar) {
        return parser(grammar, ParserCreationOptions.getDefault());
    }

    public static @NotNull Parser parser(final @NotNull File grammar) throws IOException {
        return parser(grammar, ParserCreationOptions.getDefault());
    }

    public static @NotNull Parser parser(final @NotNull String grammar,
                                         final @NotNull Insta.ParserCreationOptions options) {
        return Cfg.buildParser(grammar, options);
    }

    public static @NotNull Parser parser(final @NotNull File grammar,
                                         final @NotNull Insta.ParserCreationOptions options) throws IOException {
        final @NotNull String contents = Files.readString(grammar.toPath());
        @NotNull Parser parser = parser(contents, options);
        return parser;
    }

    public static Parser parser(final @NotNull Grammar grammar,
                                final @NotNull Insta.ParserCreationOptions options) throws IOException {
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
        private final @NotNull Insta.UnhideOptions unhide;
        private final boolean total;
        private final boolean optimizeMemory;

        public static final @NotNull ParsingOptions DEFAULT = new ParsingOptions(null, false, UnhideOptions.none, false, false);

        public ParsingOptions() {
            this(null, false, Insta.UnhideOptions.none, false, false);
        }

        public ParsingOptions(final @Nullable Keyword start, final boolean partial, final @NotNull Insta.UnhideOptions unhide, final boolean total, final boolean optimizeMemory) {
            this.start = start;
            this.partial = partial;
            this.unhide = unhide;
            this.total = total;
            this.optimizeMemory = optimizeMemory;
        }

        public static @NotNull ParsingOptions optMemory() {
            return new ParsingOptions(null, false, Insta.UnhideOptions.none, false, true);
        }

        public @Nullable Keyword getStart() {
            return start;
        }

        public @NotNull Keyword getStartOrDefault(final @NotNull Keyword defaultStart) {
            return start == null ? defaultStart : start;
        }

        public boolean usePartial() {
            return partial;
        }

        public @NotNull Insta.UnhideOptions getUnhide() {
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

        public static @NotNull ParserCreationOptions newWithStandardWhitespace() {
            return new ParserCreationOptions(
                    getPredefinedWhitespaceParser(Keyword.intern("standard")),
                    null,
                    Cfg.GlobalCaseInsensitivity.DEFAULT,
                    ReductionType.ReductionTypesAvailable.defaultType);
        }
    }

    private static @Nullable Parser getPredefinedWhitespaceParser(final @Nullable Keyword wsParserName) {
        if (wsParserName == null) {
            return null;
        }
        if (predefWsParsers == null) {
            predefWsParsers = Map.of(
                    Keyword.intern("standard"), parser("whitespace = #'\\s+'", ParserCreationOptions.getDefault()),
                    Keyword.intern("comma"), parser("whitespace = #'[,\\s]+'", ParserCreationOptions.getDefault())
            );
        }
        return predefWsParsers.get(wsParserName);
    }

    private static Map<Keyword, Parser> predefWsParsers;
}
