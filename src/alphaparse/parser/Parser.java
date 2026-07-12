package alphaparse.parser;

import alphaparse.*;
import alphaparse.error.ParserCreationFailure;
import alphaparse.grammar.Grammar;
import alphaparse.parser_options.ParsingOptions;
import alphaparse.parser_options.Unhide;
import alphaparse.parsing.Gll;
import alphaparse.result.AlphaParseFailure;
import alphaparse.result.AlphaParseResult;
import alphaparse.result.AlphaParsesResult;
import alphaparse.result.ParseTree;
import org.jetbrains.annotations.NotNull;

/**
 * A parser contains a grammar and the name of the first production to try.
 *
 * @param grammar         The grammar.
 * @param startProduction The first production to try.
 */
public record Parser(@NotNull Grammar grammar,
                     @NotNull Sym startProduction) {
    /**
     * Creates a new Parser.
     *
     * @param grammar         The grammar.
     * @param startProduction The first production to try.
     * @throws ParserCreationFailure if the parameters are invalid (for example, if the grammar does not contain the start-production symbol).
     */
    public Parser {
        if (!grammar.containsKey(startProduction))
            throw new ParserCreationFailure("Illegal start-production " + startProduction + ": not in grammar.");
    }

    private @NotNull Sym getStartProductionFromParserOrOptionsAndCheck(
            final @NotNull ParsingOptions options) {
        var startProduction = options.start();
        if (startProduction == null)
            startProduction = startProduction();
        if (!grammar().containsKey(startProduction))
            throw new ParserCreationFailure("Start production not in grammar: " + startProduction);
        return startProduction;
    }


    private @NotNull Parser unhideParser(final @NotNull Unhide.UnhideOptions unhide) {
        return switch (unhide) {
            case NONE -> this;
            case CONTENT -> withGrammar(Unhide.unhideContent(grammar()));
            case TAGS -> withGrammar(Unhide.unhideTags(grammar()));
            case ALL -> withGrammar(Unhide.unhideAll(grammar()));
        };
    }

    /**
     * Runs a parser on a text. If the parse is successful, returns a {@link ParseTree}. If the parse fails, returns a {@link AlphaParseFailure}
     * <p>
     * The options apply as follows:
     * <ul>
     *     <li>{@link ParsingOptions#embedFailureInParseTree()}: Return a {@link ParseTree} on failure, with the information included in the tree.</li>
     *     <li>{@link ParsingOptions#unhide()}: Unhide some parts of the parser in the output.</li>
     *     <li>{@link ParsingOptions#start()}: Explicitly changes the start production.</li>
     * </ul>
     *
     * @param text    The text.
     * @param options Options.
     * @return {@link ParseTree} if successful, {@link AlphaParseFailure} if not.
     */
    public @NotNull AlphaParseResult parse(final @NotNull String text,
                                           final @NotNull ParsingOptions options) {
        final @NotNull var startProduction = getStartProductionFromParserOrOptionsAndCheck(options);
        final @NotNull var unhiddenParser = unhideParser(options.unhide());

        final @NotNull AlphaParseResult parsingResult;
        if (options.embedFailureInParseTree()) {
            parsingResult = AlphaParseResult.make(
                    Gll.parseEmbedFailure(unhiddenParser.grammar(), startProduction, text, false, options.iterativeDeepening()));
        } else {
            parsingResult = AlphaParseResult.make(
                    Gll.parse(unhiddenParser.grammar(), startProduction, text, false, options.iterativeDeepening()));
        }

        return parsingResult;
    }

    /**
     * Same as {@link Parser#parse(String, ParsingOptions)} using {@link ParsingOptions#getDefault()} as the options.
     *
     * @param text The text.
     * @return The result of the parse.
     * @see Parser#parse(String, ParsingOptions)
     * @see ParsingOptions#getDefault()
     */
    public @NotNull AlphaParseResult parse(final @NotNull String text) {
        return parse(text, ParsingOptions.getDefault());
    }

    /**
     * Runs a parser on a string and returns a parse forest as a {@link AlphaParsesResult.LazyResultList}.
     * <p>
     * The following options apply:
     * <ul>
     *     <li>{@link ParsingOptions#usePartial()}: Include partial parses.</li>
     *     <li>{@link ParsingOptions#embedFailureInParseTree()}: Include failure information in parse trees.</li>
     *     <li>{@link ParsingOptions#unhide()}: Unhide some parts of the parser in the output.</li>
     *     <li>{@link ParsingOptions#start()}: Explicitly changes the start production.</li>
     * </ul>
     *
     * @param text    The text.
     * @param options The options for the parse operation.
     * @return A (potentially empty) parse forest. ({@link AlphaParsesResult.LazyResultList})
     * @see Alpha#parses(Parser, String, ParsingOptions)
     */
    public @NotNull AlphaParsesResult parses(final @NotNull String text,
                                             final @NotNull ParsingOptions options) {
        final @NotNull var startProduction = getStartProductionFromParserOrOptionsAndCheck(options);
        final @NotNull var unhiddenParser = unhideParser(options.unhide());
        final var usePartial = options.usePartial();

        final var embedFailure = options.embedFailureInParseTree();
        if (embedFailure) {
            return Gll.parsesEmbedFailure(unhiddenParser.grammar(), startProduction, text, usePartial, options.iterativeDeepening());
        } else {
            return Gll.parses(unhiddenParser.grammar(), startProduction, text, usePartial, options.iterativeDeepening(), options.failureIfEmpty());
        }
    }

    /**
     * Same as {@link Parser#parses(String, ParsingOptions)} using {@link ParsingOptions#getDefault()} as the options.
     *
     * @param text The text.
     * @return The parse forest or error, as needed.
     * @see Parser#parses(String, ParsingOptions)
     */
    public @NotNull AlphaParsesResult parses(final @NotNull String text) {
        return parses(text, ParsingOptions.getDefault());
    }

    /**
     * Creates a new Parser with the grammar changed. This method may fail if the input is invalid.
     *
     * @param grammar the new grammar.
     * @return A new Parser.
     * @throws IllegalArgumentException if the new grammar does not include a production for the current start production.
     */
    public @NotNull Parser withGrammar(final @NotNull Grammar grammar) {
        if (this.grammar.equals(grammar))
            return this;
        return new Parser(grammar, startProduction);
    }

    /**
     * Creates a string representing this parser.
     *
     * @return The string.
     */
    public @NotNull String show() {
        return Print.parserToString(this);
    }
}
