package alphaparse.parser;

import alphaparse.*;
import alphaparse.grammar.Grammar;
import alphaparse.parser_options.ParsingOptions;
import alphaparse.result.AlphaParseResult;
import alphaparse.result.AlphaParsesResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * @throws IllegalArgumentException if the parameters are invalid (for example, if the grammar does not contain the start-production symbol).
     */
    public Parser {
        if (!grammar.containsKey(startProduction))
            throw new IllegalArgumentException("Illegal start-production " + startProduction + ": not in grammar.");
    }

    /**
     * Same as {@link Alpha#parse(Parser, String, ParsingOptions)} using {@code this} as the parser.
     *
     * @param text    The text.
     * @param options The options for the parse operation.
     * @return The result of the parse.
     * @see Alpha#parse(Parser, String)
     */
    public @NotNull AlphaParseResult parse(final @NotNull String text,
                                           final @Nullable ParsingOptions options) {
        return Alpha.parse(
                this, text,
                options == null ? ParsingOptions.getDefault() : options);
    }

    /**
     * Same as {@link Alpha#parse(Parser, String)} using {@code this} as the parser.
     *
     * @param text The text.
     * @return The result of the parse.
     * @see Alpha#parse(Parser, String)
     */
    public @NotNull AlphaParseResult parse(final @NotNull String text) {
        return parse(text, null);
    }

    /**
     * Same as {@link Alpha#parses(Parser, String, ParsingOptions)} using {@code this} as the parser.
     *
     * @param text    The text.
     * @param options The options for the parse operation.
     * @return The parse forest or error, as needed.
     * @see Alpha#parses(Parser, String, ParsingOptions)
     */
    public @NotNull AlphaParsesResult parses(final @NotNull String text,
                                             final @Nullable ParsingOptions options) {
        return Alpha.parses(
                this, text,
                options == null ? ParsingOptions.getDefault() : options);
    }

    /**
     * Same as {@link Alpha#parses(Parser, String)} using {@code this} as the parser.
     *
     * @param text The text.
     * @return The parse forest or error, as needed.
     * @see Alpha#parses(Parser, String)
     */
    public @NotNull AlphaParsesResult parses(final @NotNull String text) {
        return parses(text, null);
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
