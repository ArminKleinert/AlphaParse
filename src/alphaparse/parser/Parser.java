package alphaparse.parser;

import alphaparse.*;
import alphaparse.reduction.ReductionType;
import alphaparse.result.AlphaParseResult;
import alphaparse.result.AlphaParsesResult;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * A parser contains a grammar and the name of the first production to try.
 *
 * @param grammar         The grammar.
 * @param startProduction The first production to try.
 * @param outputFormat    The output format for the parses.
 */
public record Parser(@NotNull Grammar grammar,
                     @NotNull Keyword startProduction,
                     @NotNull ReductionType.ReductionTypesAvailable outputFormat)
        implements Function<String, AlphaParseResult> {

    /**
     * Creates a new Parser.
     *
     * @param grammar         The grammar.
     * @param startProduction The first production to try.
     * @param outputFormat    The output format for the parses.
     * @throws IllegalArgumentException if the parameters are invalid (for example, if the grammar does not contain the start-production symbol).
     */
    public Parser {
        if (!grammar.containsKey(startProduction))
            throw new IllegalArgumentException("Illegal start-production " + startProduction + ": not in grammar.");

    }

    /**
     * Same as {@link Alpha#parse(Parser, String, Alpha.ParsingOptions)} using {@code this} as the parser.
     *
     * @param text    The text.
     * @param options The options for the parse operation.
     * @return The result of the parse.
     * @see Alpha#parse(Parser, String)
     */
    public @NotNull AlphaParseResult parse(final @NotNull String text,
                                           final @NotNull Alpha.ParsingOptions options) {
        return Alpha.parse(this, text, options);
    }

    /**
     * Same as {@link Alpha#parse(Parser, String)} using {@code this} as the parser.
     *
     * @param text The text.
     * @return The result of the parse.
     * @see Alpha#parse(Parser, String)
     */
    public @NotNull AlphaParseResult parse(final @NotNull String text) {
        return Alpha.parse(this, text, Alpha.ParsingOptions.getDefault());
    }

    /**
     * Same as {@link Alpha#parses(Parser, String, Alpha.ParsingOptions)} using {@code this} as the parser.
     *
     * @param text    The text.
     * @param options The options for the parse operation.
     * @return The parse forest or error, as needed.
     * @see Alpha#parses(Parser, String, alphaparse.Alpha.ParsingOptions)
     */
    public @NotNull AlphaParsesResult parses(final @NotNull String text,
                                             final @NotNull Alpha.ParsingOptions options) {
        return Alpha.parses(this, text, options);
    }

    /**
     * Same as {@link Alpha#parses(Parser, String)} using {@code this} as the parser.
     *
     * @param text The text.
     * @return The parse forest or error, as needed.
     * @see Alpha#parses(Parser, String)
     */
    public @NotNull AlphaParsesResult parses(final @NotNull String text) {
        return Alpha.parses(this, text, Alpha.ParsingOptions.getDefault());
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
        if (!grammar.containsKey(startProduction))
            throw new IllegalArgumentException();
        return new Parser(grammar, startProduction, outputFormat);
    }

    /**
     * Creates a new Parser with the start production changed. This method may fail if the input is invalid.
     *
     * @param startProduction The new start production key.
     * @return A new Parser.
     * @throws IllegalArgumentException if the grammar does not include a production for the new start production.
     */
    public @NotNull Parser withStartProduction(final @NotNull Keyword startProduction) {
        if (!grammar.containsKey(startProduction))
            throw new IllegalArgumentException();
        return new Parser(grammar, startProduction, outputFormat);
    }

    /**
     * Creates a new parser with a whitespace-parser added.
     *
     * @param whitespaceParser The parser for whitespace.
     * @return The new Parser.
     * @see CombinatorFactory#autoWhitespace(Grammar, Keyword, Grammar, Keyword)
     */
    public @NotNull Parser withWhitespaceParser(final @NotNull Parser whitespaceParser) {
        return withGrammar((new CombinatorFactory(true)).autoWhitespace(
                grammar(),
                startProduction(),
                whitespaceParser.grammar(),
                whitespaceParser.startProduction()
        ));
    }

    /**
     * Same as {@link #parse(String)}.
     *
     * @param s The string to parse.
     * @return The result of the parse.
     */
    @Override
    public @NotNull AlphaParseResult apply(final @NotNull String s) {
        return parse(s);
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
