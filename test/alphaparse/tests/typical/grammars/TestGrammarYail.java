package alphaparse.tests.typical.grammars;

import alphaparse.Alpha;
import alphaparse.parser.Parser;
import alphaparse.parser_options.ParserCreationOptions;
import alphaparse.parser_options.RulesAvailable;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class TestGrammarYail {
    private @NotNull Parser parser() {
        try {
            return Alpha.parser(
                    Files.readString(Path.of("testres/grammars/yail.g")),
                    ParserCreationOptions.newWithStandardWhitespace().addAvailableRule(RulesAvailable.EXPLICIT_EOF)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void verifySimpleProgram() {
        var text = "print 1 ;";
        Assertions.assertTrue(parser().parse(text).isSuccess());
    }
}
