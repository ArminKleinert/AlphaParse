package alphaparse;

import alphaparse.parser.Grammar;
import alphaparse.parser.EpsilonCombinator;
import alphaparse.reduction.ReductionType;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

class AlphaParserCreationTest {

    @Test
    void parserFrom() {
    }

    @Test
    void parserFromString() {
        {
            final @NotNull var p = Alpha.parser("S : '1'");
            final @NotNull var p2 = Alpha.parser("S : '1'");
            Assertions.assertEquals(p, p2);
        }
    }

    @Test
    void parserFromFile() {
        try {

            final @NotNull String text = "aaaaabbbaaaabb";
            final @NotNull var grammarFile = new File("grammars/as_and_bs.g");
            final @NotNull var p = Alpha.parser(Files.readString(grammarFile.toPath()));
            final @NotNull var grammarText = Files.readString(grammarFile.toPath());

            Assertions.assertEquals(
                    Alpha.parser(grammarText).parse(text),
                    p.parse(text)
            );
            Assertions.assertEquals(
                    Alpha.parser(grammarText),
                    p
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            final @NotNull var grammarFile = new File("grammars/as_and_bs.g");
            final @NotNull var p = Alpha.parser(Files.readString(grammarFile.toPath()));
            Assertions.assertEquals(p, Alpha.parser(grammarFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            final @NotNull var grammarFile = new File("grammars/c99.g");
            final @NotNull var pFromString = Alpha.parser(
                    Files.readString(grammarFile.toPath()),
                    Alpha.ParserCreationOptions.newWithStandardWhitespace());
            final @NotNull var pFromFile = Alpha.parser(
                    Files.readString(grammarFile.toPath()),
                    Alpha.ParserCreationOptions.newWithStandardWhitespace());
            final @NotNull var text = "void a(){}";
            Assertions.assertEquals(pFromString.parses(text), pFromFile.parses(text));
            Assertions.assertEquals(pFromString, pFromFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void parserFromStringWithOptions() {
    }
/*
@Nullable Parser whitespaceParser,
@Nullable Keyword startProduction,
@NotNull Cfg.GlobalCaseInsensitivity stringCaseInsensitive,
@NotNull ReductionType.ReductionTypesAvailable outputFormat
*/

    @Test
    void parserFromFileWithOptions() {
    }

    @Test
    void parserFromGrammarWithOptions() {
    }

    @Test
    void parserCreationFail() {
        {
            // Error: Illegal grammar
            final @NotNull var grammar = "S : A";
            Assertions.assertThrows(IllegalStateException.class, () -> Alpha.parser(grammar));
        }
        {
            // Error: Starting symbol not in grammar
            final @NotNull var grammar = "S : 'abc'";
            final @NotNull var options = new Alpha.ParserCreationOptions(
                    null, Keyword.intern("C"),
                    GlobalCaseInsensitivity.DEFAULT, ReductionType.ReductionTypesAvailable.OUTPUT,
                    true);
            Assertions.assertThrows(IllegalArgumentException.class, () -> Alpha.parser(grammar, options));
        }
        {
            // Error: No start symbol provided
            final @NotNull var grammar = new Grammar(Map.of(
                    Keyword.intern("S"), EpsilonCombinator.getDefault(),
                    Keyword.intern("A"), EpsilonCombinator.getDefault()
            ));
            Assertions.assertThrows(IllegalArgumentException.class, () ->
                    Alpha.parser(grammar, Alpha.ParserCreationOptions.getDefault()));
        }
    }

}