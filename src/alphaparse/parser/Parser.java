package alphaparse.parser;

import alphaparse.*;
import alphaparse.reduction.ReductionType;
import alphaparse.result.AlphaParseResult;
import alphaparse.result.AlphaParsesResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * TODO
 *
 * @param grammar         TODO
 * @param startProduction TODO
 * @param outputFormat    TODO
 */
public record Parser(@NotNull Grammar grammar,
                     @NotNull Keyword startProduction,
                     @NotNull ReductionType.ReductionTypesAvailable outputFormat,
                     boolean useBuffering)
        implements BiFunction<String, Alpha.ParsingOptions, AlphaParseResult> {

    /**
     * TODO
     *
     * @param grammar         TODO
     * @param startProduction TODO
     * @param outputFormat    TODO
     */
    public Parser {
        if (!grammar.containsKey(startProduction))
            throw new IllegalArgumentException("Illegal start-production " + startProduction + ": not in grammar.");

    }

    /**
     * TODO
     *
     * @param text    TODO
     * @param options TODO
     * @return TODO
     */
    public @NotNull AlphaParseResult parse(final @NotNull String text,
                                           final @NotNull Alpha.ParsingOptions options) {
        return Alpha.parse(this, text, options);
    }

    /**
     * TODO
     *
     * @param text TODO
     * @return TODO
     */
    public @NotNull AlphaParseResult parse(final @NotNull String text) {
        return Alpha.parse(this, text, Alpha.ParsingOptions.getDefault());
    }

    /**
     * TODO
     *
     * @param text    TODO
     * @param options TODO
     * @return TODO
     */
    public @NotNull AlphaParsesResult parses(final @NotNull String text,
                                             final @NotNull Alpha.ParsingOptions options) {
        return Alpha.parses(this, text, options);
    }

    /**
     * TODO
     *
     * @param text TODO
     * @return TODO
     */
    public @NotNull AlphaParsesResult parses(final @NotNull String text) {
        return Alpha.parses(this, text, Alpha.ParsingOptions.getDefault());
    }

    /**
     * TODO
     *
     * @param grammar TODO
     * @return TODO
     */
    public @NotNull Parser withGrammar(final @NotNull Grammar grammar) {
        if (this.grammar.equals(grammar))
            return this;
        return new Parser(grammar, startProduction, outputFormat, useBuffering);
    }

    /**
     * TODO
     *
     * @param startProduction TODO
     * @return TODO
     */
    public @NotNull Parser withStartProduction(final @NotNull Keyword startProduction) {
        return new Parser(grammar, startProduction, outputFormat, useBuffering);
    }

    /**
     * TODO
     *
     * @param whitespaceParser TODO
     * @return TODO
     */
    public @NotNull Parser withWhitespaceParser(final @NotNull Parser whitespaceParser) {
        return withGrammar((new CombinatorFactory(useBuffering)).autoWhitespace(
                grammar(),
                startProduction(),
                whitespaceParser.grammar(),
                whitespaceParser.startProduction()
        ));
    }

//    @Override
//    public String toString() {
//        return Print.parserToString(this);
//    }

    /**
     * TODO
     *
     * @param s              TODO
     * @param parsingOptions TODO
     * @return TODO
     */
    @Override
    public @NotNull AlphaParseResult apply(
            final @NotNull String s,
            final @Nullable Alpha.ParsingOptions parsingOptions) {
        return parse(s, parsingOptions == null ? Alpha.ParsingOptions.getDefault() : parsingOptions);
    }
}
