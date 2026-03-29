package instarun.parser;

import instarun.Keyword;
import instarun.CombinatorsSource;
import instarun.Insta;
import instarun.Print;
import instarun.parser.combinator.Combinator;
import instarun.reduction.ReductionType;
import instarun.result.InstaParseResult;
import instarun.result.InstaParsesResult;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public final class Parser {
    private final @NotNull Grammar grammar;
    private final @NotNull Keyword startProduction;
    private final @NotNull ReductionType.ReductionTypesAvailable outputFormat;

    public Parser(final @NotNull Map<Keyword, Combinator> grammar,
                  final @NotNull Keyword startProduction,
                  final @NotNull ReductionType.ReductionTypesAvailable outputFormat) {
        this.grammar = new Grammar(grammar);
        this.startProduction = startProduction;
        this.outputFormat = outputFormat;
    }

    public @NotNull InstaParseResult parse(final @NotNull String text, final Insta.ParsingOptions options) {
        return Insta.parse(this, text, options);
    }

    public @NotNull InstaParseResult parse(final @NotNull String text) {
        return Insta.parse(this, text, Insta.ParsingOptions.DEFAULT);
    }

    public @NotNull InstaParsesResult parses(final @NotNull String text, final @NotNull Insta.ParsingOptions options) {
        return Insta.parses(this, text, options);
    }

    public @NotNull InstaParsesResult parses(final @NotNull String text) {
        return Insta.parses(this, text, Insta.ParsingOptions.DEFAULT);
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Parser parser)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(grammar, parser.grammar) && Objects.equals(startProduction, parser.startProduction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), grammar, startProduction);
    }

    public @NotNull Grammar getGrammar() {
        return grammar;
    }

    public @NotNull Keyword getStartProduction() {
        return startProduction;
    }

    public @NotNull Parser withGrammar(final Grammar grammar) {
        return new Parser(grammar, startProduction, outputFormat);
    }

    public @NotNull Parser withGrammar(final Map<@NotNull Keyword, @NotNull Combinator> grammar) {
        return new Parser(new Grammar(grammar), startProduction, outputFormat);
    }

    public @NotNull Parser withStartProduction(final @NotNull Keyword startProduction) {
        return new Parser(grammar, startProduction, outputFormat);
    }

    public @NotNull Parser withWhitespaceParser(final @NotNull Parser whitespaceParser) {
        return withGrammar((new CombinatorsSource()).autoWhitespace(
                getGrammar(),
                getStartProduction(),
                whitespaceParser.getGrammar(),
                whitespaceParser.getStartProduction()
        ));
    }

    @Override
    public String toString() {
        return Print.parserToString(this);
    }
}
