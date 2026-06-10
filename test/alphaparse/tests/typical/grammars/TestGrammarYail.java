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

    @Test
    void verifyVar() {
        var text = "var v = 0;";
        Assertions.assertTrue(parser().parse(text).isSuccess());
    }

    @Test
    void verifyAssign() {
        var text = "v = 0;";
        Assertions.assertTrue(parser().parse(text).isSuccess());
    }

    @Test
    void verifyFunctionDef() {
        var text = "/** */\nfun f() {}";
        Assertions.assertTrue(parser().parse(text).isSuccess());
        System.out.println(parser().parse(text));
    }

    @Test
    void verifySimpleStuff() {
        var text = """
                var v = 4;
                fun f(v) { if (v > 0) return -v; else return v; }
                print f(v) <= 0;""";
        Assertions.assertTrue(parser().parse(text).isSuccess());
    }
}
