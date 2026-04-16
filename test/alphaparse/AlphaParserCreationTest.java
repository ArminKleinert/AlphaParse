package alphaparse;

import alphaparse.parser.Grammar;
import alphaparse.parser.combinator.EpsilonCombinator;
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
            final @NotNull var grammarFile = new File("grammars/c99.g");
            final @NotNull var p = Alpha.parser(Files.readString(grammarFile.toPath()));
            Assertions.assertEquals(p, Alpha.parser(grammarFile));
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
                    Cfg.GlobalCaseInsensitivity.DEFAULT, ReductionType.ReductionTypesAvailable.defaultType);
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