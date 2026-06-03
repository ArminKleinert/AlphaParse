package alphaparse;

import alphaparse.error.IllegalGrammarException;
import alphaparse.error.ParserCreationFailure;
import alphaparse.grammar.Grammar;
import alphaparse.grammar.GrammarBuilder;
import alphaparse.parser_options.Unhide;
import alphaparse.parsing.Gll;
import alphaparse.parser.Parser;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.ParsingOptions;
import alphaparse.result.AlphaParseFailure;
import alphaparse.result.AlphaParseResult;
import alphaparse.result.AlphaParsesResult;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;

/**
 * The main interface for interacting with the library.
 */
public final class Alpha {
    private Alpha() {
    }

    private static @NotNull Parser unhideParser(final @NotNull Parser parser,
                                                final @NotNull Unhide.UnhideOptions unhide) {
        if (unhide == Unhide.UnhideOptions.NONE)
            return parser;

        return switch (unhide) {
            case CONTENT -> parser.withGrammar(Unhide.unhideContent(parser.grammar()));
            case TAGS -> parser.withGrammar(Unhide.unhideTags(parser.grammar()));
            case ALL -> parser.withGrammar(Unhide.unhideAll(parser.grammar()));
            default -> throw new IllegalStateException("Unexpected value: " + unhide);
        };
    }

    private static @NotNull Sym getStartProductionFromParserOrOptionsAndCheck(
            final @NotNull ParsingOptions options,
            final @NotNull Parser parser) {
        var startProduction = options.start();
        if (startProduction == null)
            startProduction = parser.startProduction();
        if (!parser.grammar().containsKey(startProduction))
            throw new ParserCreationFailure("Start production not in grammar: " + startProduction);
        return startProduction;
    }

    /**
     * Runs a parser on a text. If the parse is successful, returns a {@link ParseTree}. If the parse fails, returns a {@link AlphaParseFailure}
     * <p>
     * The options apply as follows:
     * <ul>
     *     <li>{@link ParsingOptions#embedFailureInParseTree()}: Return a {@link ParseTree} on failure, with the information included in the tree.</li>
     *     <li>{@link ParsingOptions#unhide()}: Unhide some parts of the parser in the output.</li>
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
        final @NotNull var unhiddenParser = unhideParser(parser, options.unhide());

        final @NotNull AlphaParseResult parsingResult;
        if (options.embedFailureInParseTree()) {
            parsingResult = AlphaParseResult.make(
                    Gll.parseTotal(unhiddenParser.grammar(), startProduction, text, false, options.iterativeDeepening()));
        } else {
            parsingResult = AlphaParseResult.make(
                    Gll.parse(unhiddenParser.grammar(), startProduction, text, false, options.iterativeDeepening()));
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
     *     <li>{@link ParsingOptions#embedFailureInParseTree()}: Include failure information in parse trees.</li>
     *     <li>{@link ParsingOptions#unhide()}: Unhide some parts of the parser in the output.</li>
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
        final @NotNull var unhiddenParser = unhideParser(parser, options.unhide());

        final var useParseTotal = options.embedFailureInParseTree();
        if (useParseTotal) {
            return Gll.parsesTotal(unhiddenParser.grammar(), startProduction, text, usePartial, options.iterativeDeepening());
        } else {
            return Gll.parses(unhiddenParser.grammar(), startProduction, text, usePartial, options.iterativeDeepening());
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
     *     <li>{@link ParsingOptions#embedFailureInParseTree()}: Include failure information in parse trees.</li>
     *     <li>{@link ParsingOptions#unhide()}: Unhide some parts of the parser in the output.</li>
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
        final @NotNull var unhiddenParser = unhideParser(parser, options.unhide());

        final var useParseTotal = options.embedFailureInParseTree();
        if (useParseTotal) {
            return Gll.parsesTotal(unhiddenParser.grammar(), startProduction, text, usePartial, options.iterativeDeepening());
        } else {
            return Gll.parsesOrFailure(unhiddenParser.grammar(), startProduction, text, usePartial, options.iterativeDeepening());
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
     *     <li>{@link ParserCreationOptions#useParserBuffering()}: Whether to use buffering for the productions to ensure that no productions are doubled.</li>
     * </ul>
     *
     * @param grammar The grammar as a string.
     * @param options The options.
     * @return The parser.
     */
    public static @NotNull Parser parser(final @NotNull String grammar,
                                         final @NotNull ParserCreationOptions options) {
        var grammarForParsingGrammar = CfgGrammar.makeCfg(options);
        return Cfg.make(options).buildParser(grammar, grammarForParsingGrammar);
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
                                         final @NotNull ParserCreationOptions options) throws IOException {
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
     * @throws ParserCreationFailure If the start production is invalid.
     */
    public static @NotNull Parser parser(@NotNull Grammar grammar,
                                         final @NotNull ParserCreationOptions options) {
        if (options.startProduction() == null)
            throw new ParserCreationFailure("Start production must be specified when creating a parser from a Grammar object.");

        if (!grammar.containsKey(options.startProduction()))
            throw new ParserCreationFailure("The start production " + options.startProduction() + " is not in the grammar.");

        try {
            var builder = new GrammarBuilder(options) {
                @Override
                public void make() {
                }
            };

            var g = builder.buildWithWhitespace(grammar, options.whitespaceParser());

            return new Parser(g, options.startProduction());
        } catch (IllegalGrammarException exception) {
            throw new ParserCreationFailure(exception);
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
            final @Nullable String wsParserName) {
        if (wsParserName == null) {
            return null;
        }
        if (predefinedWsParsers == null) {
            predefinedWsParsers = Map.of(
                    "standard", parser("whitespace = #'\\s+'", ParserCreationOptions.getDefault()),
                    "comma", parser("whitespace = #'[,\\s]+'", ParserCreationOptions.getDefault())
            );
        }
        return predefinedWsParsers.get(wsParserName);
    }

    private static @Nullable Map<String, Parser> predefinedWsParsers = null;
}
